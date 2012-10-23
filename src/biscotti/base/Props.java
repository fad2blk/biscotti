package biscotti.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

/**
 * Static utility methods for working with {@link Properties}.
 * 
 * @author Zhenya Leonov
 */
public final class Props {

	private Props() {
	}

	/**
	 * Returns a new Java {@code Properties} object loaded from the specified
	 * input stream. The input stream may contain an XML Document or a simple
	 * line-oriented format as specified in {@link Properties#load(Reader)}. The
	 * input stream is closed after this method returns.
	 * <p>
	 * If the input stream contain an XML Document it must have the following
	 * DOCTYPE declaration:
	 * 
	 * <pre>
	 * &lt;!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd"&gt;
	 * </pre>
	 * 
	 * Furthermore, the document must satisfy the properties DTD described
	 * above.
	 * <p>
	 * Otherwise the input stream is assumed to use the ISO 8859-1 character
	 * encoding; that is each byte is one Latin1 character. Characters not in
	 * Latin1, and certain special characters, are represented in keys and
	 * elements using Unicode escapes as defined in section 3.3 of <i>The Java™
	 * Language Specification.</i>
	 * 
	 * @param in
	 *            the specified input stream
	 * @return a new Java {@code Properties} object loaded from the specified
	 *         input stream
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static Properties load(final InputStream in) throws IOException {
		checkNotNull(in);
		Properties props = new Properties();
		final InputStream bin = new ByteArrayInputStream(
				ByteStreams.toByteArray(in));
		Closeables.closeQuietly(in);

		try {
			props.loadFromXML(bin);
		} catch (InvalidPropertiesFormatException e) {
			props = new Properties();
			bin.reset();
		}

		props = new Properties();
		props.load(in);
		return props;
	}

	/**
	 * Returns a new Java {@code Properties} object loaded from the specified
	 * file.
	 * 
	 * @param path
	 *            the specified file
	 * @return a new Java {@code Properties} object loaded from the specified
	 *         file
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static Properties load(final File path) throws IOException {
		checkNotNull(path);
		return load(new BufferedInputStream(new FileInputStream(path)));
	}

	/**
	 * Writes the specified {@code Properties} to the given file. Internally
	 * this method delegates to {@link Properties#store(Writer, String)}.
	 * <p>
	 * Properties from the defaults table of this Properties table (if any) are
	 * not written out by this method.
	 * 
	 * @param properties
	 *            the specified {@code Properties}
	 * @param comments
	 *            a description of the property list, or {@code null} if no
	 *            comments are desired
	 * @param path
	 *            the given file
	 * @return the given file
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static File save(final Properties properties, final String comments,
			final File path) throws IOException {
		checkNotNull(properties);
		checkNotNull(path);
		final OutputStream out = new BufferedOutputStream(new FileOutputStream(
				path));
		properties.store(out, comments);
		Closeables.closeQuietly(out);
		return path;
	}

	/**
	 * Writes an XML document representing the specified {@code Properties} to
	 * the given file using the specified charset. Internally this methods
	 * delegates to {@link Properties#storeToXML(OutputStream, String, String)}.
	 * <p>
	 * The XML document will have the following DOCTYPE declaration:
	 * 
	 * <pre>
	 * &lt;!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd"&gt;
	 * </pre>
	 * 
	 * @param properties
	 *            the specified {@code Properties}
	 * @param comment
	 *            a description of the property list, or {@code null} if no
	 *            comment is desired
	 * @param path
	 *            the given file
	 * @param charset
	 *            the specified charset
	 * @return the given file
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static File saveAsXML(final Properties properties,
			final String comment, final File path, final Charset charset)
			throws IOException {
		checkNotNull(properties);
		checkNotNull(path);
		checkNotNull(charset);
		final OutputStream out = new BufferedOutputStream(new FileOutputStream(
				path));
		properties.storeToXML(out, comment, charset.toString());
		Closeables.closeQuietly(out);
		return path;
	}

}
