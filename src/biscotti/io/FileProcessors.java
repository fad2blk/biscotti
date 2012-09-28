package biscotti.io;

import java.io.File;
import java.io.IOException;

import javax.annotation.processing.Processor;

/**
 * Static utility methods that operate on or return {@link Processor}s.
 * 
 * @author Zhenya Leonov
 */
final public class FileProcessors {

	final private static FileProcessor<Object> ALWAYS_TRUE = new FileProcessor<Object>() {

		@Override
		public Object getResult() {
			return null;
		}

		@Override
		public boolean processFile(File path) throws IOException {
			return true;
		}

	};

	final private static FileProcessor<Object> ALWAYS_FALSE = new FileProcessor<Object>() {

		@Override
		public Object getResult() {
			return null;
		}

		@Override
		public boolean processFile(File path) throws IOException {
			return false;
		}

	};

	private FileProcessors() {
	}

	/**
	 * Returns a file processor whose {@link FileProcessor#processFile(File)}
	 * method always returns {@code true}. The {@link FileProcessor#getResult()}
	 * method always returns {@code null}.
	 * 
	 * @return a file processor whose {@link FileProcessor#processFile(File)}
	 *         method always returns {@code true}
	 */
	@SuppressWarnings("unchecked")
	public static <R> FileProcessor<R> alwaysTrue() {
		return (FileProcessor<R>) ALWAYS_TRUE;
	}

	/**
	 * Returns a file processor whose {@link FileProcessor#processFile(File)}
	 * method always returns {@code false}. The
	 * {@link FileProcessor#getResult()} method always returns {@code null}.
	 * 
	 * @return a file processor whose {@link FileProcessor#processFile(File)}
	 *         method always returns {@code false}
	 */
	@SuppressWarnings("unchecked")
	public static <R> FileProcessor<R> alwaysFalse() {
		return (FileProcessor<R>) ALWAYS_FALSE;
	}

}
