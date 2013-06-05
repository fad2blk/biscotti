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
 * Static utility methods that operate on or return {@link FileProcessor}s.
 * 
 * @author Zhenya Leonov
 */
final public class FileProcessors {

	final private static FileProcessor<Void> ALWAYS_TRUE = new FileProcessor<Void>() {

		@Override
		public Void getResult() {
			return null;
		}

		@Override
		public boolean processFile(File path) throws IOException {
			return true;
		}

	};

	final private static FileProcessor<Void> ALWAYS_FALSE = new FileProcessor<Void>() {

		@Override
		public Void getResult() {
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
