package biscotti.io;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

/**
 * Static utility methods useful for working with {@link ZipFile}s.
 * 
 * @author Zhenya Leonov
 */
final public class Archives {

	private Archives() {
	}

	/**
	 * A predicate which tests if a {@code ZipEntry} is a directory.
	 */
	public static Predicate<ZipEntry> DIRECTORY = new Predicate<ZipEntry>() {

		@Override
		public boolean apply(ZipEntry e) {
			return e.isDirectory();
		}
	};

	/**
	 * A predicate which tests if a {@code ZipEntry} is a file.
	 */
	public static Predicate<ZipEntry> FILE = new Predicate<ZipEntry>() {

		@Override
		public boolean apply(ZipEntry e) {
			return !e.isDirectory();
		}
	};

	/**
	 * Returns a {@code File} representing the canonical pathname of the
	 * specified zip file.
	 * 
	 * @param zip
	 *            the specified zip file
	 * @return a file representing the canonical pathname of the specified zip
	 *         file
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static File getCanonicalFile(final ZipFile zip) throws IOException {
		checkNotNull(zip);
		return new File(zip.getName()).getCanonicalFile();
	}

	/**
	 * Opens the specified zip file for reading.
	 * 
	 * @param path
	 *            the specified zip file
	 * @return a new {@code ZipFile} object given the specified zip file
	 * @throws ZipException
	 *             if a zip format error has occurred
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static ZipFile open(final File path) throws ZipException,
			IOException {
		checkNotNull(path);
		return new ZipFile(path);
	}

	/**
	 * Returns all the entries in the specified zip file.
	 * 
	 * @param zip
	 *            the specified zip file
	 * @return an {@code Iterable} containing all the entries (files and
	 *         directories) from the specified zip file
	 */
	public static Iterable<? extends ZipEntry> getEntries(final ZipFile zip) {
		checkNotNull(zip);
		return Collections.list(zip.entries());
	}

	/**
	 * Returns a byte array containing all bytes from the specified zip entry.
	 * 
	 * @param zip
	 *            the zip file in which to find the zip entry
	 * @param entry
	 *            the specified zip entry
	 * @return a byte array containing all bytes from the specified zip entry
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static byte[] toByteArray(final ZipFile zip, final ZipEntry entry)
			throws IOException {
		checkNotNull(zip);
		checkNotNull(entry);
		return ByteStreams.toByteArray(new BufferedInputStream(zip
				.getInputStream(entry)));
	}

	/**
	 * Returns a string containing all the characters from the given zip entry,
	 * using the specified charset. Does not close the zip file. The entry must
	 * not be a directory to avoid an {@code IllegalArgumentException}.
	 * 
	 * @param zip
	 *            the zip file in which to find the zip entry
	 * @param entry
	 *            the given zip entry
	 * @param charset
	 *            the character set to use when reading the zip entry
	 * @return a string containing all the characters from the specified zip
	 *         entry
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws IllegalArgumentException
	 *             if the specified entry is a directory
	 * @see Charsets
	 */
	public static String toString(final ZipFile zip, final ZipEntry entry,
			final Charset charset) throws IOException {
		checkNotNull(zip);
		checkNotNull(entry);
		checkNotNull(charset);
		checkArgument(!entry.isDirectory());
		return CharStreams.toString(new InputStreamReader(zip
				.getInputStream(entry), charset));
	}

	/**
	 * Returns all of the lines from the specified zip entry using the specified
	 * charset. The lines do not include line-termination characters, but do
	 * include other leading and trailing whitespace.
	 * 
	 * @param zip
	 *            the zip file in which to find the zip entry
	 * @param entry
	 *            the specified zip entry
	 * @param charset
	 *            the charset used to decode the zip entry
	 * @return a mutable {@link List} containing all the lines from a zip entry
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws IllegalArgumentException
	 *             if the specified entry is a directory
	 */
	public static List<String> readLines(final ZipFile zip,
			final ZipEntry entry, final Charset charset) throws IOException {
		checkNotNull(zip);
		checkNotNull(entry);
		checkNotNull(charset);
		checkArgument(!entry.isDirectory());
		return CharStreams.readLines(new InputStreamReader(zip
				.getInputStream(entry), charset));
	}

	/**
	 * Computes and returns the hash code for the specified zip entry using the
	 * given hash function.
	 * 
	 * @param zip
	 *            the zip file in which to find the zip entry
	 * @param entry
	 *            the specified zip entry
	 * @param hashFunction
	 *            the given hash function
	 * @return hash code for the specified zip entry using the given hash
	 *         function
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static HashCode hash(final ZipFile zip, final ZipEntry entry,
			final HashFunction hashFunction) throws IOException {
		checkNotNull(zip);
		checkNotNull(entry);
		checkNotNull(hashFunction);
		final Hasher hasher = hashFunction.newHasher();
		return hasher.hash();
	}

	/**
	 * Returns a zip output stream to the specified file.
	 * 
	 * @param path
	 *            the specified file
	 * @return a zip output stream to the specified file.
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static ZipOutputStream create(final File path) throws IOException {
		checkNotNull(path);
		return new ZipOutputStream(Files.newOutputStreamSupplier(path)
				.getOutput());
	}

	/**
	 * Begins writing a new {@code ZipEntry} and positions the stream to the
	 * start of the entry data. Closes the current entry if still active. The
	 * default compression method and the current time will be used.
	 * 
	 * @param out
	 *            the specified zip output stream
	 * @param name
	 *            the name of the new entry
	 * @throws IOException
	 *             if an I/O error occurs
	 * @return the newly created zip entry
	 */
	public static ZipEntry startZipEntry(final ZipOutputStream out,
			final String name) throws IOException {
		checkNotNull(out);
		checkNotNull(name);
		checkArgument(!name.isEmpty());
		final ZipEntry ze = new ZipEntry(name);
		out.putNextEntry(ze);
		return ze;
	}

}