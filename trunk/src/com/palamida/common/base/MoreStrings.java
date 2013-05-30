package com.palamida.common.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.StringReader;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;

/**
 * Static utility methods that operate on or return {@link String}s.
 * 
 * @see Strings
 * @see CharMatcher
 * @see Joiner
 * @see Splitter
 * @author Zhenya Leonov
 */
final public class MoreStrings {

	private MoreStrings() {
	}

	/**
	 * Returns {@code true} if {@code string} ends with the specified suffix,
	 * ignoring case differences; else {@code false}.
	 * 
	 * @param string
	 *            the specified string
	 * @param suffix
	 *            the suffix to look for
	 * @return {@code true} if {@code string} ends with the wise specified
	 *         suffix, ignoring case differences; else {@code false}
	 */
	public static boolean endsWithIgnoreCase(final String string, final String suffix) {
		checkNotNull(string);
		checkNotNull(suffix);
		return string.regionMatches(string.length() - suffix.length(), suffix, 0, suffix.length());
	}

	/**
	 * Returns {@code true} if {@code string} starts with the specified prefix,
	 * ignoring case differences; else {@code false}.
	 * 
	 * @param string
	 *            the specified string
	 * @param prefix
	 *            the prefix to look for
	 * @return {@code true} if {@code string} starts with the specified prefix,
	 *         ignoring case differences; else {@code false}
	 */
	public static boolean startsWithIgnoreCase(final String string, final String prefix) {
		checkNotNull(string);
		checkNotNull(prefix);
		return string.regionMatches(true, 0, prefix, 0, prefix.length());
	}

	/**
	 * Returns {@code true} if the substring of {@code string} beginning at the
	 * specified index starts with the specified prefix, ignoring case
	 * differences; else {@code false}.
	 * 
	 * @param string
	 *            the specified string
	 * @param prefix
	 *            the prefix to look for
	 * @param toffset
	 *            where to begin looking for the prefix
	 * @return {@code true} if the substring of {@code string} beginning at the
	 *         specified index starts with the specified prefix, ignoring case
	 *         differences; else {@code false}
	 */
	public static boolean startsWithIgnoreCase(final String string, final String prefix, final int toffset) {
		checkNotNull(string);
		checkNotNull(prefix);
		return string.regionMatches(true, toffset, prefix, 0, prefix.length());
	}

	/**
	 * Returns all of the lines from a string. The lines do not include
	 * line-termination characters, but do include other leading and trailing
	 * whitespace.
	 * 
	 * @param string
	 *            the specified string
	 * @return return all of the lines from a string
	 * @throws IOException
	 *             if an I/O exception occurs
	 */
	public static Iterable<String> readLines(final String string) throws IOException {
		return CharStreams.readLines(new StringReader(string));
	}

}