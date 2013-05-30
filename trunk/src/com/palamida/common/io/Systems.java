/*
 * Copyright (C) 2013 Zhenya Leonov
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

package com.palamida.common.io;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Static utility methods useful for working with system-dependent tasks.
 * 
 * @author Zhenya Leonov
 */
final public class Systems {

	private Systems() {
	}

	/**
	 * Starts a new system-dependent process.
	 * 
	 * @param command
	 *            the list containing the program and its arguments
	 * @return the {@code Process} object representing the started process
	 * @throws IOException
	 *             if an error occurs while starting the process
	 */
	public static Process start(final List<String> command) throws IOException {
		checkNotNull(command);
		checkArgument(!command.isEmpty());
		final ProcessBuilder builder = new ProcessBuilder(command);
		return builder.start();
	}

	/**
	 * Starts a new system-dependent process and waits for it to finish before
	 * returning.
	 * 
	 * @param command
	 *            the list containing the program and its arguments
	 * @return the {@code Process} object representing the started process
	 * @throws InterruptedException
	 *             if the current thread is {@link Thread#interrupt()
	 *             interrupted} by another thread while it is waiting, then the
	 *             wait is ended and an {@link InterruptedException} is thrown
	 * @throws IOException
	 *             if an error occurs while starting the process
	 */
	public static Process startAndWait(final List<String> command) throws InterruptedException, IOException {
		checkNotNull(command);
		checkArgument(!command.isEmpty());
		final Process process = start(command);
		process.waitFor();
		return process;
	}

	/**
	 * Launches the system associated editor application and opens the specified
	 * file for editing.
	 * 
	 * @param file
	 *            the file to edit
	 * @throws IOException
	 *             if an I/O error occurs
	 * @return the specified file
	 */
	public static File edit(final File file) throws IOException {
		checkNotNull(file);
		checkArgument(file.exists());
		checkArgument(file.isFile());
		Desktop.getDesktop().edit(file);
		return file;
	}

	/**
	 * Opens the specified file with the system associated application.
	 * 
	 * @param file
	 *            the file or directory to open
	 * @throws IOException
	 *             if an I/O error occurs
	 * @return the specified file
	 */
	public static File open(final File file) throws IOException {
		checkNotNull(file);
		checkArgument(file.exists());
		Desktop.getDesktop().open(file);
		return file;
	}

	/**
	 * Reassigns the "standard" output stream to the given file, using the
	 * specified charset.
	 * 
	 * @param path
	 *            the specified file
	 * @param charset
	 *            the character set to use when writing the lines
	 * @param autoFlush
	 *            if {@code true}, the output buffer will be flushed whenever a
	 *            byte array is written, one of the println methods is invoked,
	 *            or a newline character or byte ('\n') is written
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static void setOut(final File path, final Charset charset, final boolean autoFlush) throws IOException {
		checkNotNull(path);
		checkNotNull(charset);
		final PrintStream ps = MoreFiles.newPrintStream(path, charset, false, true);
		System.setOut(ps);
	}

	/**
	 * Reassigns the "standard" error stream to the given file, using the
	 * specified charset.
	 * 
	 * @param path
	 *            the specified file
	 * @param charset
	 *            the character set to use when writing the lines
	 * @param autoFlush
	 *            if {@code true}, the output buffer will be flushed whenever a
	 *            byte array is written, one of the println methods is invoked,
	 *            or a newline character or byte ('\n') is written
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static void setErr(final File path, final Charset charset, final boolean autoFlush) throws IOException {
		checkNotNull(path);
		checkNotNull(charset);
		final PrintStream ps = MoreFiles.newPrintStream(path, charset, false, true);
		System.setErr(ps);
	}

}
