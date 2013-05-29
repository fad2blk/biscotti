import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

public class EncodingTest {

	public EncodingTest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * Truncates and returns the specified string to the given length using the
	 * the specified charset.
	 * 
	 * @param string
	 *            the string to truncate
	 * @param cs
	 *            the specified charset
	 * @param length
	 *            the given length in bytes
	 * @return a string truncated to the given length
	 */
	public static String truncate(final String string, final Charset cs,
			final int length) {
		final byte[] byteArray = string.getBytes(cs);

		if (byteArray.length < length)
			return string;

		final CharsetDecoder charsetDecoder = cs.newDecoder();
		final ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray, 0, length);
		final CharBuffer charBuffer = CharBuffer.allocate(length);
		charsetDecoder.onMalformedInput(CodingErrorAction.IGNORE);
		charsetDecoder.decode(byteBuffer, charBuffer, true);
		charsetDecoder.flush(charBuffer);
		return new String(charBuffer.array(), 0, charBuffer.position());
	}

}
