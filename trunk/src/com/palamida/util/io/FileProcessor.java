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
