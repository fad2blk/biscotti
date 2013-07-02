/*
 * Copyright (C) 2010 Zhenya Leonov
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

package base;

/**
 * A runtime exception thrown by the {@code clone()} method to indicate that an
 * object could not or should not be cloned.
 * <p>
 * Unlike the checked {@link java.lang.CloneNotSupportedException
 * java.lang.CloneNotSupportedException}, this exception can be thrown by
 * sub-classes whose parent classes do not throw a
 * {@code java.lang.CloneNotSupportedException}.
 */
public class CloneNotSupportedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a {@code CloneNotSupportedException} with no detail message.
	 */
	public CloneNotSupportedException() {
		super();
	}

	/**
	 * Constructs a {@code CloneNotSupportedException} with the specified detail
	 * message.
	 * 
	 * @param s
	 *            the detail message
	 */
	public CloneNotSupportedException(String s) {
		super(s);
	}
}
