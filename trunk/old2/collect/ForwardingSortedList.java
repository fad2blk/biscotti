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

package collect;

import java.util.Comparator;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.ForwardingObject;

/**
 * A {@link SortedList} which forwards all its method calls to another {@code
 * SortedList}. Subclasses should override one or more methods to modify the
 * behavior of the backing list as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 * 
 * @see ForwardingObject
 * @author Zhenya Leonov
 */
public abstract class ForwardingSortedList<E> extends ForwardingList<E>
		implements SortedList<E> {

	@Override
	protected abstract SortedList<E> delegate();

	@Override
	public Comparator<? super E> comparator() {
		return delegate().comparator();
	}

	@Override
	public SortedList<E> headList(E toElement) {
		return delegate().headList(toElement);
	}

	@Override
	public SortedList<E> subList(E fromElement, E toElement) {
		return delegate().subList(fromElement, toElement);
	}

	@Override
	public SortedList<E> subList(int fromIndex, int toIndex) {
		return delegate().subList(fromIndex, toIndex);
	}

	@Override
	public SortedList<E> tailList(E fromElement) {
		return delegate().tailList(fromElement);
	}

}
