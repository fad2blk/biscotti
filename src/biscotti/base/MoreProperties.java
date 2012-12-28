package biscotti.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import com.google.common.io.Closeables;

/**
 * Static utility methods for working with {@link Properties}.
 * 
 * @author Zhenya Leonov
 */
public final class MoreProperties {

	private MoreProperties() {
	}

	/**
	 * Returns a new Java {@code Properties} object loaded from the XML document
	 * in the specified input stream. The input stream is closed after this
	 * method returns.
	 * <p>
	 * The XML document must have the following DOCTYPE declaration:
	 * 
	 * <pre>
	 * &lt;!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd"&gt;
	 * </pre>
	 * 
	 * Furthermore, the document must satisfy the properties DTD described
	 * above.
	 * 
	 * @param in
	 *            the specified input stream
	 * @return a new Java {@code Properties} object loaded from the specified
	 *         input stream
	 * @throws IOException
	 *             if an I/O error occurs
	 * @see Properties#loadFromXML(InputStream)
	 */
	public static Properties loadFromXML(final InputStream in)
			throws IOException {
		checkNotNull(in);
		final Properties props = new Properties();
		props.loadFromXML(in);
		Closeables.close(in, false);
		return props;
	}

	/**
	 * Returns a new Java {@code Properties} object loaded from the specified
	 * XML document.
	 * <p>
	 * The XML document must have the following DOCTYPE declaration:
	 * 
	 * <pre>
	 * &lt;!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd"&gt;
	 * </pre>
	 * 
	 * Furthermore, the document must satisfy the properties DTD described
	 * above.
	 * 
	 * @param path
	 *            the specified XML document
	 * @return a new Java {@code Properties} object loaded from the specified
	 *         input stream
	 * @throws IOException
	 *             if an I/O error occurs
	 * @see Properties#loadFromXML(InputStream)
	 */
	public static Properties loadFromXML(final File path) throws IOException {
		checkNotNull(path);
		return loadFromXML(new BufferedInputStream(new FileInputStream(path)));
	}

	/**
	 * Returns a new Java {@code Properties} object loaded from the specified
	 * input stream. The input stream is closed after this method returns.
	 * 
	 * @param in
	 *            the specified input stream
	 * @return a new Java {@code Properties} object loaded from the specified
	 *         input stream
	 * @throws IOException
	 *             if an I/O error occurs
	 * @see Properties#load(InputStream)
	 */
	public static Properties load(final InputStream in) throws IOException {
		checkNotNull(in);
		final Properties props = new Properties();
		props.load(in);
		Closeables.close(in, false);
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
	 * @see Properties#loadFromXML(InputStream)
	 */
	public static Properties load(final File path) throws IOException {
		checkNotNull(path);
		return load(new BufferedInputStream(new FileInputStream(path)));
	}

	/**
	 * Writes the specified {@code Properties} to the given file.
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
		Closeables.close(out, false);
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
		Closeables.close(out, false);
		return path;
	}

}
