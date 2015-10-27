## About ##
<font size='2'>
The Biscotti Project extends the <a href='http://code.google.com/p/google-collections/'>Google Collections Library</a> (now part of <a href='http://code.google.com/p/guava-libraries/'>Guava</a>) by offering new collection types, implementations, and related components for Java 6 or higher.<br>
<br>
This project fully adheres to all the contracts and conventions specified in the JDK and Gauva.<br>
<h2>What's in here?</h2>
<table cellpadding='0' border='0' cellspacing='0'>
<tr>
<td width='5%'></td>
<td><b>Interfaces</b></td>
</tr>
<tr>
<td></td>
<td>
<ul><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/BoundedMap.html'>BoundedMap</a></b> - A capacity restricted <code>Map</code>. The size of this map can vary, but never exceed the maximum number of entries (the bound) specified at creation.<br>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/BoundedQueue.html'>BoundedQueue</a></b> - A capacity restricted <code>Queue</code>. The size of this queue can vary, but never exceed the maximum number of elements (the bound) specified at creation.<br>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/SortedCollection.html'>SortedCollection</a></b> - A collection whose elements are automatically sorted, either according to their <i>natural ordering</i>, or by a <code>Comparator</code> object. This interface is the root of all <i>sorted</i> collection interfaces and implementations.<br>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/SortedList.html'>SortedList</a></b> - A <code>List</code> that further provides a <i>total ordering</i> on its elements. This interface is the <code>List</code> analog of <code>SortedSet</code>.<br>
</td></tr>
<tr>
<td width='5%'></td>
<td><b>General-Purpose Implementations</b></td>
</tr>
<tr>
<td></td>
<td>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/TreeList.html'>TreeList</a></b> - An implementation of the <code>SortedList</code> interface based on a modified <a href='http://en.wikipedia.org/wiki/Red-black_tree'>Red-Black Tree</a>.<br>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/TreeQueue.html'>TreeQueue</a></b> - A <a href='http://en.wikipedia.org/wiki/Red-black_tree'>Red-Black Tree</a> implementation of an unbounded priority <code>Queue</code>.<br>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/TreeDeque.html'>TreeDeque</a></b> - A <a href='http://en.wikipedia.org/wiki/Red-black_tree'>Red-Black Tree</a> implementation of an unbounded double-ended priority queue (also known as a <code>Deque</code>). Extends <code>TreeQueue</code>.<br>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/SkipList.html'>SkipList</a></b> - A List which maintains its elements in sorted order while providing logarithmic running time for insertion, removal, and <a href='http://en.wikipedia.org/wiki/Random_access'>random access</a> lookup operations, based on a modified <a href='http://en.wikipedia.org/wiki/Skip_list'>Skip List</a>.<br>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/RankList.html'>RankList</a></b> - A List implementation optimized for efficient <a href='http://en.wikipedia.org/wiki/Random_access'>random access</a> insertion and removal operations, based on a modified <a href='http://en.wikipedia.org/wiki/Skip_list'>Skip List</a>.<br>
</td>
</tr>
<tr>
<td width='5%'></td>
<td><b>Special-Purpose Implementations</b> - Capacity-restricted implementations.</td>
</tr>
<tr>
<td></td>
<td>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/TreeBoundedDeque.html'>TreeBoundedDeque</a></b> - An implementation of <code>BoundedQueue</code> backed by a <code>TreeDeque</code>. When this deque is full new elements are rejected or accepted based on their priority.<br>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/LinkedBoundedQueue.html'>LinkedBoundedQueue</a></b> - A linked list implementation of <code>BoundedQueue</code>. This queue orders elements in <i>first-in-first-out</i> (FIFO) manner; it considers the eldest element to be the <i>least-recently-inserted</i> element.<br>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/FIFOBoundedMap.html'>FIFOBoundedMap</a></b> - A <code>BoundedMap</code> implementation which removes stale mappings in <i>first-in-first-out</i> order. Extends <code>LinkedHashMap</code>.<br>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/LRUBoundedMap.html'>LRUBoundedMap</a></b> - A <code>BoundedMap</code> implementation which removes stale mappings in <i>least-recently-accessed</i> order. Extends <code>LinkedHashMap</code>.<br>
</td>
</tr>
<tr>
<td width='5%'></td>
<td><b>Infrastructure</b></td>
</tr>
<tr>
<td></td>
<td>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/Collections3.html'>Collections3</a></b> - Convenient factory methods and <i>synchronize</i> and <i>unmodifiable</i> wrappes.<br>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/Iterators2.html'>Iterators2</a></b> - Offering count, concat, reverse, transform with function, filter with predicate, wrappers, and convenience implementations.<br>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/Iterables2.html'>Iterables2</a></b> - A sister class of <code>Iterators2</code> containing methods which operate exclusively on <code>Iterable</code>s.<br>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/base/Preconditions2.html'>Preconditions2</a></b> - Static utility methods used to verify correctness of arguments passed to your own methods.<br>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/base/CloneNotSupportedException.html'>CloneNotSupportedException</a></b> - A runtime exception analogous to the checked <code>java.util.CloneNotSupportedException</code>.<br>
</td>
</tr>
<tr>
<td width='5%'></td>
<td><b>Forwarding Implementations</b> - Collections allowing you to customize their behavior per the <a href='http://en.wikipedia.org/wiki/Decorator_pattern'>decorator pattern</a> without subclassing.<br>
</td>
</tr>
<tr>
<td></td>
<td>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/ForwardingDeque.html'>ForwardingDeque</a></b> - A <code>Deque</code> which forwards all its method calls to a backing deque.<br>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/ForwardingNavigableMap.html'>ForwardingNavigableMap</a></b> - A <code>NavigableMap</code> which forwards all its method calls to a backing navigable map.<br>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/ForwardingNavigableSet.html'>ForwardingNavigableSet</a></b> - A <code>NavigableSet</code> which forwards all its method calls to a backing navigable set.<br>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/ForwardingPeekingIterator.html'>ForwardingPeekingIterator</a></b> - A <code>PeekingIterator</code> which forwards all its method calls to a backing peeking iterator.<br>
</li><li><b><a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/ForwardingSortedList.html'>ForwardingSortedList</a></b> - A <code>SortedList</code> which forwards all its method calls to a backing sorted list.<br>
</td>
</tr>
</table></li></ul>

<h2>Getting Started</h2>

It is assumed that you are familiar with the Java Collection Framework and the Google Guava Libraries. Please examine the Links section if you are not.<br>
<br><br>
You can begin by reading the <a href='http://code.google.com/p/biscotti/wiki/FAQ'>FAQ</a>, looking at the <a href='http://biscotti.googlecode.com/svn/trunk/javadoc/index.html'>API Documentation</a>, and browsing the <a href='http://code.google.com/p/biscotti/source/browse/#svn/trunk/src/com/googlecode/biscotti'>source code</a>.<br>
<br><br>
A very early pre-alpha release is available for <a href='http://code.google.com/p/biscotti/downloads/list'>download</a>. You will need to have the guava-r##.jar available in your project's classpath.<br>
<br>
<h2>Coming Soon</h2>

Why should you be interested in this project? What are the advantages and disadvantages of using a <code>TreeDeque</code> versus <code>java.util.PriorityQueue</code>? A <b>Tutorial</b> is coming.<br>
<br>
The next big thing I am working on is implementing a concurrent <a href='http://en.wikipedia.org/wiki/Skip_list'>Skip List</a> based algorithm.