/*
 * Copyright (C) 2012 Zhenya Leonov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.palamida.util.io;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedOutputStream;
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
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.common.io.Files;

/**
 * Static utility methods for working with {@link File}s.
 * 
 * @see Files
 * @author Zhenya Leonov
 */
final public class MoreFiles {

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
	 * @param append
	 *            if {@code true}, then bytes will be written to the end of the
	 *            file rather than the beginning
	 * @param autoFlush
	 *            if {@code true}, the {@code println}, {@code printf}, and
	 *            {@code format} methods will flush the output buffer
	 * @throws IOException
	 *             if an I/O error occurs
	 * @return a print writer to the given file
	 * @see Charsets
	 */
	public static PrintWriter newPrintWriter(final File path, final Charset charset, final boolean append,
			final boolean autoFlush) throws IOException {
		checkNotNull(path);
		checkNotNull(charset);
		final OutputStream fos = new BufferedOutputStream(new FileOutputStream(path, append));
		return new PrintWriter(fos, autoFlush);
	}

	/**
	 * Returns a new {@code PrintStream} which writes to the given file using
	 * the specified charset.
	 * 
	 * @param path
	 *            the specified file
	 * @param append
	 *            if {@code true}, then bytes will be written to the end of the
	 *            file rather than the beginning
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
	public static PrintStream newPrintStream(final File path, final Charset charset, final boolean append,
			final boolean autoFlush) throws FileNotFoundException {
		checkNotNull(path);
		checkNotNull(charset);
		try {
			return new PrintStream(new BufferedOutputStream(new FileOutputStream(path, append)), autoFlush,
					charset.toString());
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError(); // cannot happen
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
	public static File writeLines(final Iterable<String> lines, final File path, final Charset charset)
			throws IOException {
		checkNotNull(path);
		checkNotNull(charset);
		checkNotNull(lines);
		final PrintWriter writer = MoreFiles.newPrintWriter(path, charset, false, false);
		for (final String line : lines)
			writer.println(line);
		writer.close();
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

	/**
	 * Returns all the files and sub-directories in the specified path.
	 * 
	 * @param path
	 *            the specified path
	 * @return all the files and sub-directories in the specified path
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static Iterable<File> walkFileTree(final File path) throws IOException {
		checkNotNull(path);
		checkArgument(path.isDirectory());
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
	public static Iterable<File> walkFileTree(final File path, final FileFilter filter) throws IOException {
		checkNotNull(path);
		checkNotNull(filter);
		checkArgument(path.isDirectory());
		final ImmutableSet.Builder<File> builder = ImmutableSet.builder();
		walkFileTree(builder, path, filter, FileProcessors.alwaysTrue());
		return builder.build();
	}

	/**
	 * Processes all the files and sub-directories in the specified path which
	 * satisfy the given filter.
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
	public static void walkFileTree(final File path, final FileFilter filter, final FileProcessor<?> processor)
			throws IOException {
		checkNotNull(path);
		checkNotNull(filter);
		checkNotNull(processor);
		checkArgument(path.exists());
		walkFileTree(null, path, filter, processor);
	}

	/**
	 * Processes all the files and sub-directories in the specified path.
	 * 
	 * @param path
	 *            the specified path
	 * @param processor
	 *            the {@code FileProcessor} instance used to process the files
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static void walkFileTree(final File path, final FileProcessor<?> processor) throws IOException {
		checkNotNull(path);
		checkNotNull(processor);
		checkArgument(path.isDirectory());
		walkFileTree(null, path, FileFilters.TRUE, processor);
	}

	private static void walkFileTree(final ImmutableSet.Builder<File> builder, final File path,
			final FileFilter filter, final FileProcessor<?> processor) throws IOException {
		final File[] files = path.listFiles(FileFilters.or(FileFilters.DIRECTORY, filter));
		if (files != null)
			for (final File file : files) {
				if (filter.accept(file)) {
					if (builder != null)
						builder.add(file);
					if (!processor.processFile(file))
						return;
				}
				if (file.isDirectory())
					walkFileTree(builder, file, filter, processor);
			}
		else if (filter.accept(path)) {
			if (builder != null)
				builder.add(path);
			if (!processor.processFile(path))
				return;
		}
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
	public static File appendLines(final List<String> lines, final File to, final Charset charset) throws IOException {
		checkNotNull(lines);
		checkNotNull(to);
		checkNotNull(charset);
		final PrintWriter writer = newPrintWriter(to, charset, true, false);
		for (final String line : lines)
			writer.println(line);
		Closeables.close(writer, false);
		return to;
	}

}