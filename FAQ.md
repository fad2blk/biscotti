# Introduction #

### Is this library safe to use? ###

Not really. This project is in the pre-alpha stage of development. Although there is a download available, I would encourage you to thoroughly test any part of this library that you are planning to use in a production environment.

### What was your motivation for building all this? ###

Having somewhat of a background in data mining, I have always been fascinated by _cute_ and _efficient_ data structures.  I have been playing around with various third party collections libraries for Java since the days of Objectspace JGL. This library has been influenced by [JGL](http://recursionsw.com/Products/jgl.html), [Apache Commons Collections](http://commons.apache.org/collections/), [GNU Trove](http://trove.starlight-systems.com/), and of course the [Google Collections](http://code.google.com/p/google-collections/), and the [Java Collections Framework](http://download.oracle.com/javase/6/docs/technotes/guides/collections/index.html).

I designed this library from the ground up to have a well formed interface hierarchy matching (and in some cases improving on) that of the JDK. Furthermore this project faithfully adheres to the conventions set forth by the JDK and to a lesser extent by the Google Collections Library.

# Design #

### Why so much emphasis on sorted collections? ###

This library isn't just a rehash of what is already out there. For the most part sorted data structures have generally been missing from most collections libraries, and not only for Java. For example I would be very interested to see any implementation of the [List](http://download.oracle.com/javase/6/docs/api/java/util/List.html) interface which maintains the elements in sorted order, let alone provides fast random insertion and removal operations.

### Why do you need a sorted list? A [TreeMultiset](http://google-collections.googlecode.com/svn/trunk/javadoc/index.html?com/google/common/collect/TreeMultiset.html) is already provided in the Google Collections. ###

I've been asked this question several times. The idea that a `Multiset` (or `Bag`) can be substituted for a `List` is a misnomer. Without getting into mathematical abstractions, it is suffice to say that a `List` is the only data structure that provides explicit control over the order of the elements. For example you can explicitly query for the element located in the `i`_th_ position.

Incidentally a similar question was asked under one of the issues in the Google Collections regarding the implementation of a `UniqueList` (a list the rejects duplicate elements) versus a `SortedSet`.

### If you build on Java 6 and try to match the interface hierarchy of the JDK, why do you have a [SortedList](http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/SortedList.html)<br>and not a <code>NavigableList</code> with methods analogous to <code>NavigableSet</code>?</h3>

Good catch. Simply put, the `SortedList` interface was more than enough to demonstrate the concrete [TreeList](http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/TreeList.html) implementation. In the future I plan to create a `NavigableList` interface and have `TreeList` to implement it.

### Besides synchronized wrappers in the [Collections3](http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/collect/Collections3.html) class, you don't have any thread-safe, let alone<br>concurrent implementations of your collections, what gives?</h3>

Good question. The _tree_-based concurrent implementations which I am aware of choke under concurrent access. Their performance degrades linearly with each new thread, and they don't perform significantly better than the provided synchronized wrappers which act like a global lock.

On the other hand, there are several [Skip List](http://en.wikipedia.org/wiki/Skip_list) locking and lock-free concurrent algorithms which are remarkably fast, and are fairly strait forward to implement. I've been working on implementing [A Simple Optimistic Skip-List Algorithm](http://www.cs.brown.edu/~levyossi/Pubs/LazySkipList.pdf). The first implementation will probably come in the form of a `ConcurrentSkipQueue`.

### Didn't [Doug Lea](http://en.wikipedia.org/wiki/Doug_Lea) already implement a [ConcurrentSkipListSet](http://download.oracle.com/javase/6/docs/api/java/util/concurrent/ConcurrentSkipListSet.html) for Java 6? ###

Yes. In fact the algorithm implemented by Doug Lea is lock-free and is the fastest concurrent skip-list based algorithm that I am aware of. Having said that, besides being complicated, OpenJDK was released under the GNU General Public License (GPL). Even if it was possible to modify his algorithm for the purposes of this project, the GPL License is completely incompatible with the Apache License 2.0 under which this project was released.

### Why do you provide a [CloneNotSupportedException](http://biscotti.googlecode.com/svn/trunk/javadoc/index.html?com/googlecode/biscotti/base/CloneNotSupportedException.html)? One is already provided in Java, and you don't<br>even use it in your Collections.</h3>

Another good catch. My collections are cloneable for the sake of consistency, but this wasn't always so. Internally child classes which need _not_ be cloneable throw this exception. For the most part the following explanation is part of a much bigger rant that doesn't belong in this FAQ. Having said that:

The [CloneNotSupportedException](http://download.oracle.com/javase/6/docs/api/java/lang/CloneNotSupportedException.html) in the JDK is _not_ a runtime exception. If you extend a class which implements the `Cloneable` interface, the child class can only throw the `CloneNotSupportedException` if the `clone()` method of the parent class also throws it. This is a Catch-22 situation, since the parent class is designed to be cloneable and would not throw it in the first place.

For many other reasons, cloning is horribly broken in Java. Don't take my word for it. Here is what [Joshua Bloch](http://en.wikipedia.org/wiki/Joshua_Bloch) had to say [about it](http://www.artima.com/intv/bloch13.html).

# Implementation #

### Why do the `TreeList.addAll(Collection)`, `TreeQueue.addAll(Collection)`, and <br> <code>TreeDeque.addAll(Collection)</code> operations run in logarithmic time? Shouldn't they run in linear time?</h3>

That's correct. It's possible to merge two balanced trees together in _O(n)_ time. I haven't gotten around to it. I will update all the tree collections at some point. Usage of the `addAll` operation is generally expected to be low. This isn't a very high priority at the moment.

The same goes for Skip List based collections as well.



















