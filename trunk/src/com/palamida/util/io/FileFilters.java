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

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 * Static utility methods pertaining to {@link FileFilter}s.
 * 
 * @author Zhenya Leonov
 */
final public class FileFilters {

	private FileFilters() {
	}

	/**
	 * A file filter that always evaluates to {@code true}.
	 */
	public static final FileFilter TRUE = new FileFilter() {

		@Override
		public boolean accept(final File path) {
			return true;
		}
	};

	/**
	 * A file filter that always evaluates to {@code false}.
	 */
	public static final FileFilter FALSE = new FileFilter() {

		@Override
		public boolean accept(final File path) {
			return false;
		}
	};

	/**
	 * A {@code FileFilter} which determines if the specified {@code File} is a
	 * normal file. A file is <i>normal</i> if it is not a directory and, in
	 * addition, satisfies other system-dependent criteria.
	 */
	public static final FileFilter FILE = new FileFilter() {

		@Override
		public boolean accept(final File path) {
			checkNotNull(path);
			checkArgument(path.exists());
			return path.isFile();
		}
	};

	/**
	 * A {@code FileFilter} which tests if the specified {@code File} is a
	 * directory.
	 */
	public static final FileFilter DIRECTORY = new FileFilter() {

		@Override
		public boolean accept(final File path) {
			checkNotNull(path);
			checkArgument(path.exists());
			return path.isDirectory();
		}
	};

	/**
	 * Returns a file filter that evaluates to {@code true} if the given file
	 * filter evaluates to {@code false}.
	 * 
	 * @param filter
	 *            the given file filter
	 * @return a file filter that evaluates to {@code true} if the given file
	 *         filter evaluates to {@code false}
	 */
	public static FileFilter not(final FileFilter filter) {
		checkNotNull(filter);
		return new FileFilter() {
			@Override
			public boolean accept(final File path) {
				checkNotNull(path);
				return !filter.accept(path);
			}
		};
	}

	/**
	 * Returns a file filter that evaluates to {@code true} if any of the
	 * specified file filters evaluate to {@code true}.
	 * 
	 * @param first
	 *            the first filter
	 * @param second
	 *            the second filter
	 * @param rest
	 *            the rest specified file filters
	 * @return a file filter that evaluates to {@code true} if all of the
	 *         specified file filters evaluate to {@code true}
	 */
	public static FileFilter or(final FileFilter first, final FileFilter second, final FileFilter... rest) {
		checkNotNull(first);
		checkNotNull(second);
		checkNotNull(rest);
		return new FileFilter() {
			@Override
			public boolean accept(final File path) {
				checkNotNull(path);
				if (first.accept(path) || second.accept(path))
					return true;
				for (final FileFilter filter : rest) {
					if (!filter.accept(path))
						return true;
				}
				return false;
			}
		};
	}

	/**
	 * Returns a file filter that evaluates to {@code true} if all of the
	 * specified file filters evaluate to {@code true}.
	 * 
	 * @param first
	 *            the first filter
	 * @param second
	 *            the second filter
	 * @param rest
	 *            the rest specified file filters
	 * @return a file filter that evaluates to {@code true} if all of the
	 *         specified file filters evaluate to {@code true}
	 */
	public static FileFilter and(final FileFilter first, final FileFilter second, final FileFilter... rest) {
		checkNotNull(first);
		checkNotNull(second);
		checkNotNull(rest);
		return new FileFilter() {
			@Override
			public boolean accept(final File path) {
				checkNotNull(path);
				if (!first.accept(path) || !second.accept(path))
					return false;
				for (final FileFilter filter : rest) {
					if (!filter.accept(path))
						return false;
				}
				return true;
			}
		};
	}

	/**
	 * Adapts a {@code FilenameFilter} to the {@code FileFilter} interface.
	 * 
	 * @param filter
	 *            the specified {@code FilenameFilter}
	 * @return a new {@code FileFilter} which mimics the behavior of the
	 *         specified {@code FilenameFilter}
	 */
	public static FileFilter forFilenameFilter(final FilenameFilter filter) {
		return new FileFilter() {
			@Override
			public boolean accept(final File file) {
				checkNotNull(file);
				return filter.accept(file.getParentFile(), file.getName());
			}
		};
	}
}
