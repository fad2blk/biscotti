package biscotti.io;

import java.io.File;
import java.io.IOException;

/**
 * A callback interface to be used when processing files.
 * <p>
 * {@link #processFile(File)} will be called for each file that is encountered, and
 * should return {@code false} when you want to stop processing.
 * 
 * @param <R>
 *            the type of result this processor returns
 * @author Zhenya Leonov
 */
public interface FileProcessor<R> {

	/**
	 * Return the result of processing all the files.
	 */
	public R getResult();

	/**
	 * This method will be called for each file that is encountered.
	 * 
	 * @param path
	 *            the specified file
	 * @return {@code true} to continue processing, {@code false} to stop
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public boolean processFile(final File path) throws IOException;

}
