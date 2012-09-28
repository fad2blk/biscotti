package biscotti.base;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
final public class Strings2 {

	private Strings2() {
	}

	/**
	 * Returns {@code true} if {@code string} contains {@code substring},
	 * ignoring case differences; else {@code false}.
	 * <p>
	 * Note: This method uses the rules of the default locale.
	 * 
	 * @param string
	 *            the specified string
	 * @param substring
	 *            the substring to look for
	 * @return {@code true} if {@code string} contains the specified
	 *         {@code substring}, ignoring case differences; {@code false}
	 *         otherwise
	 */
	public static boolean containsIgnoreCase(final String string,
			final String substring) {
		checkNotNull(string);
		checkNotNull(substring);
		return indexOfIgnoreCase(string, substring) != -1;
	}

	/**
	 * Returns {@code true} if {@code string} ends with the specified suffix,
	 * ignoring case differences; else {@code false}.
	 * <p>
	 * Note: This method uses the rules of the default locale.
	 * 
	 * @param string
	 *            the specified string
	 * @param suffix
	 *            the suffix to look for
	 * @return {@code true} if {@code string} ends with the wise specified
	 *         suffix, ignoring case differences; else {@code false}
	 */
	public static boolean endsWithIgnoreCase(final String string,
			final String suffix) {
		checkNotNull(string);
		checkNotNull(suffix);
		return string.toLowerCase().endsWith(suffix.toLowerCase());
	}

	/**
	 * Returns the index within {@code string} of the first occurrence of
	 * {@code substring}, ignoring case differences.
	 * <p>
	 * Note: This method uses the rules of the default locale.
	 * 
	 * @param string
	 *            the specified string
	 * @param substring
	 *            the substring to look for
	 * @return the index within {@code string} of the first occurrence of
	 *         {@code substring}, ignoring case differences
	 */
	public static int indexOfIgnoreCase(final String string,
			final String substring) {
		checkNotNull(string);
		checkNotNull(substring);
		return string.toLowerCase().indexOf(substring.toLowerCase());
	}

	/**
	 * Returns the index within {@code string} of the first occurrence of
	 * {@code substring}, starting at the specified index and ignoring case
	 * differences.
	 * <p>
	 * Note: This method uses the rules of the default locale.
	 * 
	 * @param string
	 *            the specified string
	 * @param substring
	 *            the substring to look for
	 * @param fromIndex
	 *            the index from which to start the search
	 * @return the index within {@code string} of the first occurrence of
	 *         {@code substring}, ignoring case differences
	 */
	public static int indexOfIgnoreCase(final String string,
			final String substring, final int fromIndex) {
		checkNotNull(string);
		checkNotNull(substring);
		checkStringIndex(fromIndex, string);
		return string.toLowerCase().indexOf(substring.toLowerCase(), fromIndex);
	}

	/**
	 * Returns the index within {@code string} of the last occurrence of
	 * {@code substring}, ignoring case differences.
	 * <p>
	 * Note: This method uses the rules of the default locale.
	 * 
	 * @param string
	 *            the specified string
	 * @param substring
	 *            the substring to look for
	 * @return the index within {@code string} of the last occurrence of
	 *         {@code substring}, ignoring case differences
	 */
	public static int lastIndexOfIgnoreCase(final String string,
			final String substring) {
		checkNotNull(string);
		checkNotNull(substring);
		return string.toLowerCase().lastIndexOf(substring.toLowerCase());
	}

	/**
	 * Returns the index within {@code string} of the last occurrence of
	 * {@code substring}, starting at the specified index and ignoring case
	 * differences.
	 * <p>
	 * Note: This method uses the rules of the default locale.
	 * 
	 * @param string
	 *            the specified string
	 * @param substring
	 *            the substring to look for
	 * @param fromIndex
	 *            the index from which to start the search
	 * @return the index within {@code string} of the last occurrence of
	 *         {@code substring}, ignoring case differences
	 */
	public static int lastIndexOfIgnoreCase(final String string,
			final String substring, final int fromIndex) {
		checkNotNull(string);
		checkNotNull(substring);
		checkStringIndex(fromIndex, string);
		return string.toLowerCase().lastIndexOf(substring.toLowerCase(),
				fromIndex);
	}

	/**
	 * Returns {@code true} if {@code string} starts with the specified prefix,
	 * ignoring case differences; else {@code false}.
	 * <p>
	 * Note: This method uses the rules of the default locale.
	 * 
	 * @param string
	 *            the specified string
	 * @param prefix
	 *            the prefix to look for
	 * @return {@code true} if {@code string} starts with the specified prefix,
	 *         ignoring case differences; else {@code false}
	 */
	public static boolean startsWithIgnoreCase(final String string,
			final String prefix) {
		checkNotNull(string);
		checkNotNull(prefix);
		return string.toLowerCase().startsWith(prefix.toLowerCase());
	}

	/**
	 * Returns {@code true} if the substring of {@code string} beginning at the
	 * specified index starts with the specified prefix, ignoring case
	 * differences; else {@code false}.
	 * <p>
	 * Note: This method uses the rules of the default locale.
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
	public static boolean startsWithIgnoreCase(final String string,
			final String prefix, final int toffset) {
		checkNotNull(string);
		checkNotNull(prefix);
		checkStringIndex(toffset, string);
		return string.toLowerCase().startsWith(prefix.toLowerCase(), toffset);
	}

	/**
	 * Removes all occurrences of {@code character} from {@code string}.
	 * 
	 * @param character
	 *            the character to remove
	 * @return the original string with all occurrences of {@code character}
	 *         removed
	 */
	public static String remove(final String string, final char character) {
		checkNotNull(string);
		checkNotNull(character);
		final StringBuilder sb = new StringBuilder(string.length());
		for (int j = 0; j < string.length(); j++) {
			char c = string.charAt(j);
			if (c != character)
				sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * Removes all occurrences of {@code substring} from {@code string}.
	 * 
	 * @param substring
	 *            the specified substring
	 * @return the original string with all occurrences of {@code substring}
	 *         removed
	 */
	public static String remove(final String string, final String substring) {
		checkNotNull(string);
		checkNotNull(substring);
		return string.replace(substring, "");
	}

	/**
	 * Removes all substrings which match the given regular expression from
	 * {@code string}.
	 * 
	 * @param regex
	 *            the specified regular expression
	 * @return the original string with all substrings which match the given
	 *         regular expression removed
	 */
	public static String removeAll(final String string, final String regex) {
		checkNotNull(string);
		checkNotNull(regex);
		return string.replaceAll(regex, "");
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
	public static Iterable<String> readLines(final String string)
			throws IOException {
		return CharStreams.readLines(new StringReader(string));
	}

	/**
	 * Ensures that {@code index} specifies a valid position in {@code string}.
	 * 
	 * @param index
	 *            the specified index
	 * @param string
	 *            the specified string
	 * @return the value of {@code index}
	 * @throws IllegalArgumentException
	 *             if {@code index} < 0
	 * @throws StringIndexOutOfBoundsException
	 *             if {@code index} is more than the length of {@code string}
	 */
	private static int checkStringIndex(final int index, final String string) {
		checkArgument(index < 0);
		if (index < 0)
			throw new IllegalArgumentException();
		if (index > string.length())
			throw new StringIndexOutOfBoundsException(index);
		return index;
	}

	/**
	 * Returns the number of occurrences of {@code substring} in {@code string}.
	 * 
	 * @param string
	 *            the specified string
	 * @param substring
	 *            the substring to find
	 * @return the number of occurrences of {@code substring} in {@code string}
	 */
	public static int count(final String string, final String substring) {
		checkNotNull(string);
		checkNotNull(substring);
		int index = 0;
		int count = 0;
		while (index != -1) {
			index = string.indexOf(substring, index);
			if (index != -1) {
				count++;
				index += substring.length();
			}
		}
		return count;
	}

	/**
	 * Returns the number of matches made against the specified string with the
	 * pattern compiled from the given {@code regex} and {@code flags}.
	 * 
	 * @param string
	 *            the specified string
	 * @param regex
	 *            the given regular expression
	 * @param flags
	 *            Match flags, a bit mask that may include
	 *            {@link Pattern#CASE_INSENSITIVE}, {@link Pattern#MULTILINE},
	 *            {@link Pattern#DOTALL}, {@link Pattern#UNICODE_CASE},
	 *            {@link Pattern#CANON_EQ}, {@link Pattern#UNIX_LINES},
	 *            {@link Pattern#LITERAL},
	 *            {@link Pattern#UNICODE_CHARACTER_CLASS} and
	 *            {@link Pattern#COMMENTS}
	 * @return the number of matches made against the specified string with the
	 *         pattern compiled from the given {@code regex} and {@code flags}
	 */
	public static int countAll(final String string, final String regex,
			int flags) {
		checkNotNull(string);
		checkNotNull(regex);
		final Pattern pattern = Pattern.compile(regex, flags);
		final Matcher matcher = pattern.matcher(string);
		int count = 0;
		while (matcher.find())
			count++;
		return count;
	}

}