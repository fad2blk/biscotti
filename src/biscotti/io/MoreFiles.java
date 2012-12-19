package biscotti.io;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;

/**
 * Static utility methods for working with {@link File}s.
 * 
 * @see Files
 * @see Files#copy(File, File)
 * @see Files#copy(File, OutputStream)
 * @see Files#copy(File, OutputSupplier)
 * @see Files#copy(InputSupplier, File)
 * @see Files#toByteArray(File)
 * @see Files#touch(File)
 * @author Zhenya Leonov
 */
final public class MoreFiles {

	private static Joiner LINE_SEPARATOR = Joiner.on(System
			.getProperty("line.separator"));

	private MoreFiles() {
	}

	/**
	 * Creates any necessary but nonexistent parent directories of the specified
	 * file. Note that if this operation fails it may have succeeded in creating
	 * some (but not all) of the necessary parent directories.
	 * <p>
	 * Unlike {@link Files#createParentDirs(File)} this methods returns the
	 * specified file.
	 * 
	 * @param path
	 *            the specified file
	 * @return the specified file
	 * @throws IOException
	 */
	public static File createParentDirs(final File path) throws IOException {
		checkNotNull(path);
		Files.createParentDirs(path);
		return path;
	}

	/**
	 * Returns a {@code File} object representing the canonical path of the
	 * current working directory.
	 * <p>
	 * For example the following code will simulate the Linux/Unix <i>pwd</i>
	 * command: {@code System.out.println(IO.getCurrentDirectory());}
	 * 
	 * 
	 * @return a {@code File} object representing the current working directory
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static File getWorkingDirectory() throws IOException {
		return new File(".").getCanonicalFile();
	}

	/**
	 * Returns a print writer to the given file using the specified charset.
	 * 
	 * @param path
	 *            the given file
	 * @param charset
	 *            the character set to use when writing the file
	 * @return a print writer to the given file
	 * @param autoFlush
	 *            if {@code true}, the {@code println}, {@code printf}, and
	 *            {@code format} methods will flush the output buffer
	 * @throws IOException
	 *             if an I/O error occurs
	 * @see Charsets
	 */
	public static PrintWriter newPrintWriter(final File path,
			final Charset charset, final boolean autoFlush) throws IOException {
		checkNotNull(path);
		checkNotNull(charset);
		return new PrintWriter(Files.newWriter(path, charset), autoFlush);
	}

	/**
	 * Returns a new {@code PrintStream} which writes to the given file using
	 * the specified charset.
	 * 
	 * @param path
	 *            the specified file
	 * @param charset
	 *            the character set to use when writing to the file
	 * @param autoFlush
	 *            if {@code true}, the output buffer will be flushed whenever a
	 *            byte array is written, one of the println methods is invoked,
	 *            or a newline character or byte ('\n') is written
	 * @return new {@code PrintStream} which writes to the given file
	 * @throws FileNotFoundException
	 *             if an attempt to open the specified file fails
	 */
	public static PrintStream newPrintStream(final File path,
			final Charset charset, final boolean autoFlush)
			throws FileNotFoundException {
		checkNotNull(path);
		checkNotNull(charset);
		try {
			return new PrintStream(new FileOutputStream(path), autoFlush,
					charset.toString());
		} catch (UnsupportedEncodingException e) {
			throw new Error(); // cannot happen
		}
	}

	/**
	 * Writes a list of strings (separating them using line-termination
	 * characters) to the given file using the specified charset.
	 * 
	 * @param path
	 *            the given file
	 * @param charset
	 *            the character set to use when writing the lines
	 * @param lines
	 *            an iterable of strings to be written to the file
	 * @return the given {@code File} object
	 * @throws IOException
	 *             if an I/O error occurs
	 * @see Charsets
	 */
	public static File writeLines(final Iterable<String> lines,
			final File path, final Charset charset) throws IOException {
		checkNotNull(path);
		checkNotNull(charset);
		checkNotNull(lines);
		final PrintWriter writer = MoreFiles.newPrintWriter(path, charset, false);
		for (String line : lines)
			writer.println(line);
		Closeables.closeQuietly(writer);
		return path;
	}

	/**
	 * Returns the specified path string normalized to use the system separator
	 * as specified by {@link File#separator}.
	 * 
	 * @param path
	 *            the specified path
	 * @return the specified path string normalized to use the system separator
	 *         as specified by {@link File#separator}
	 */
	public static String separatorsToSystem(final String path) {
		checkNotNull(path);
		if (File.separator.equals("/"))
			return path.replace("\\", "/");
		else
			return path.replace("/", "\\");
	}

	// /**
	// * Creates an empty file including any necessary but nonexistent parent
	// * directories or updates the last updated timestamp if the file exists.
	// * Similar Unix command with the same name.
	// * <p>
	// * Note: If this operation fails it may have succeeded in creating some
	// (but
	// * not all) of the necessary parent directories.
	// *
	// * @param path
	// * the specified file
	// * @throws IOException
	// * if an I/O error occurs
	// * @return the newly <i>touched</i> file
	// */
	// public static File touch(final File path) throws IOException {
	// checkNotNull(path);
	// Files.createParentDirs(path);
	// Files.touch(path);
	// return path;
	// }

	/**
	 * Returns all the files and sub-directories in the specified path.
	 * 
	 * @param path
	 *            the specified path
	 * @return all the files and sub-directories in the specified path
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static Iterable<File> walkFileTree(final File path)
			throws IOException {
		checkNotNull(path);
		return walkFileTree(path, FileFilters.TRUE);
	}

	/**
	 * Returns all the files and sub-directories in the specified path which
	 * satisfy the given filter.
	 * 
	 * @param path
	 *            the specified path
	 * @param filter
	 *            a file filter
	 * @return all the files and sub-directories in the specified path which
	 *         satisfy the given filter
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static Iterable<File> walkFileTree(final File path,
			final FileFilter filter) throws IOException {
		checkNotNull(path);
		checkNotNull(filter);
		final ImmutableSet.Builder<File> builder = ImmutableSet.builder();
		walkFileTree(builder, path, filter);
		return builder.build();
	}

	/**
	 * Processes and returns all the files and sub-directories in the specified
	 * path which satisfy the given filter.
	 * 
	 * @param path
	 *            the specified directory
	 * @param filter
	 *            a file filter
	 * @param processor
	 *            the {@code FileProcessor} instance used to process the files
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static void walkFileTree(final File path,
			final FileFilter filter, final FileProcessor<?> processor)
			throws IOException {
		checkNotNull(path);
		checkNotNull(filter);
		checkNotNull(processor);
		processFileTree(path, filter, processor);
	}

	/**
	 * Processes and returns all the files and sub-directories in the specified
	 * path.
	 * 
	 * @param path
	 *            the specified path
	 * @param processor
	 *            the {@code FileProcessor} instance used to process the files
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static void walkFileTree(final File path,
			final FileProcessor<?> processor) throws IOException {
		checkNotNull(path);
		checkNotNull(processor);
		processFileTree(path, FileFilters.TRUE, processor);
	}

	private static void walkFileTree(final ImmutableSet.Builder<File> builder,
			final File path, final FileFilter filter) throws IOException {
		File[] files = path.listFiles(FileFilters.or(FileFilters.DIRECTORY,
				filter));
		if (files != null)
			for (File file : files) {
				if (filter.accept(file))
					builder.add(file);
				if (file.isDirectory())
					walkFileTree(builder, file, filter);
			}
		else if (filter.accept(path))
			builder.add(path);
	}

	private static void processFileTree(final File path,
			final FileFilter filter, final FileProcessor<?> processor)
			throws IOException {
		File[] files = path.listFiles(FileFilters.or(FileFilters.DIRECTORY,
				filter));
		if (files != null)
			for (File file : files) {
				if (filter.accept(file))
					if (!processor.processFile(file))
						return;
				if (file.isDirectory())
					processFileTree(file, filter, processor);
			}
		else if (filter.accept(path))
			if (!processor.processFile(path))
				return;
	}

	/**
	 * Appends a list of strings (separating them using line-termination
	 * characters) to the given file using the specified charset.
	 * 
	 * @param lines
	 *            the character sequence to append
	 * @param to
	 *            the given file
	 * @param charset
	 *            the character set to use when wOriting the lines
	 * @return the given file
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static File appendLines(final List<String> lines, final File to,
			final Charset charset) throws IOException {
		checkNotNull(lines);
		checkNotNull(to);
		checkNotNull(charset);
		Files.append(LINE_SEPARATOR.join(lines), to, charset);
		return to;
	}

}