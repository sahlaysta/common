package com.github.sahlaysta.common.collection;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

//Documentation borrowed from HashSet + ArrayList
/**
 * {@link ArraySet} class implements the {@link Set} interface and
 * uses the {@link Object#equals(Object) equals} method of objects
 * when comparing elements instead of
 * {@link Object#hashCode() hashCode}
 * like {@link java.util.HashSet HashSet} does. Provides all optional
 * set operations. {@link ArraySet} is backed by {@link ArrayList},
 * gettable and settable by {@link #getArrayList()} and
 * {@link #setArrayList(ArrayList)}.
 * 
 * <p>This set permits all elements including {@code null}, and
 * guarantees that elements will stay in the same order over time.
 * The <tt>size</tt>, <tt>isEmpty</tt>, and <tt>iterator</tt>
 * operations run in constant time. The basic operations
 * <tt>add</tt> and <tt>addAll</tt> run in linear time, along with
 * all the other operations.
 * 
 * <p><strong>Note that this implementation is not
 * synchronized.</strong>
 * If multiple threads access an {@link ArraySet} instance
 * concurrently, and at least one of the threads modifies the
 * set, it <i>must</i> be synchronized externally. This is typically
 * accomplished by synchronizing on some object that naturally
 * encapsulates the set.
 * 
 * If no such object exists, the set should be "wrapped" using the
 * {@link Collections#synchronizedSet Collections.synchronizedSet}
 * method. This is best done at creation time, to prevent accidental
 * unsynchronized access to the set.
 * 
 * <p>The iterators returned by this class's <tt>iterator</tt>
 * method are <i>fail-fast</i>: if the set is modified at any time
 * after the iterator is created, in any way except through the
 * iterator's own <tt>remove</tt> method, the Iterator throws a
 * {@link ConcurrentModificationException}. Thus, in the face of
 * concurrent modification, the iterator fails quickly and cleanly,
 * rather than risking arbitrary, non-deterministic behavior at an
 * undetermined time in the future.
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be
 * guaranteed as it is, generally speaking, impossible to make any
 * hard guarantees in the presence of unsynchronized concurrent
 * modification. Fail-fast iterators throw
 * <tt>ConcurrentModificationException</tt> on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on
 * this exception for its correctness: <i>the fail-fast behavior of
 * iterators should be used only to detect bugs.</i>
 * 
 * @param <E> the type of elements maintained by this set
 * 
 * @author porog
 * @see Object#equals(Object)
 * @see ArrayList
 * @see ArrayMap
 * */
public class ArraySet<E>
	extends
		AbstractSet<E>
	implements
		Cloneable,
		java.io.Serializable {

	//serialVersionUID generated as the last edit
	private static final long serialVersionUID = 5102934845380500898L;
	
	/** The backing {@link ArrayList} of this {@link ArraySet}. */
	protected transient ArrayList<E> list;
	
	/** Constructs an empty {@link ArraySet} with the specified
	 * initial capacity in the backing {@link ArrayList}.
	 * @param initialCapacity the initial
	 * capacity of the {@link ArraySet}
	 * @see ArrayList#ArrayList(int) */
	public ArraySet(int initialCapacity) {
		this(new ArrayList<>(initialCapacity));
	}
	
	/** Constructs an empty {@link ArraySet} with the default
	 * initial capacity of {@link ArrayList#ArrayList()}.
	 * @see ArrayList#ArrayList() */
	public ArraySet() {
		this(new ArrayList<>());
	}
	
	/** Constructs a new {@link ArraySet} with the
	 * specified {@link ArrayList} as the backing
	 * {@link ArrayList} of the {@link ArraySet}. Changes
	 * to the {@link ArrayList} are reflected to the
	 * {@link ArraySet} and vice-versa.
	 * @param arrayList the {@link ArrayList}
	 * to back the {@link ArraySet}
	 * @see ArraySet#setArrayList(ArrayList)
	 * */
	public ArraySet(ArrayList<? extends E> arrayList) {
		setArrayList(arrayList);
	}
	
	/** Sets the backing {@link ArrayList} of this
	 * {@link ArraySet} to the specified {@link ArrayList}.
	 * Changes to the {@link ArrayList} are reflected to this
	 * {@link ArraySet} and vice-versa.
	 * @param arrayList the {@link ArrayList} to
	 * back this {@link ArraySet} */
	public void setArrayList(ArrayList<? extends E> arrayList) {
		@SuppressWarnings("unchecked")
		ArrayList<E> casted = (ArrayList<E>)arrayList;
		list = casted;
	}
	
	/** Returns the backing {@link ArrayList} of this
	 * {@link ArraySet}. Changes to the {@link ArrayList} are
	 * reflected to this {@link ArraySet} and vice-versa.
	 * @return the backing {@link ArrayList}
	 * of this {@link ArraySet} */
	public ArrayList<E> getArrayList() {
		return list;
	}
	
	/** Ensures the capacity of the backing
	 * {@link ArrayList} of this {@link ArraySet}.
	 * @param minCapacity the desired
	 * minimum capacity
	 * @see ArrayList#ensureCapacity(int)*/
	public void ensureCapacity(int minCapacity) {
		list.ensureCapacity(minCapacity);
	}
	
	/** Adds the specified element to this {@link ArraySet} if it is
	 * not already present.
	 * @param e element to be added to this {@link ArraySet}
	 * @return {@code true} if this {@link ArraySet} did not already
	 * contain the specified element
	 * @see ArrayList#contains(Object)
	 * @see ArrayList#add(Object) */
	@Override
	public boolean add(E e) {
		return list.contains(e) ? false : list.add(e);
	}

	/** Adds all of the elements in the specified collection to
	 * this {@link ArraySet}. The effect of this call is equivalent
	 * to that of calling {@link #add(E)} on this {@link ArraySet}
	 * once for each object in the specified collection.
	 * @param c collection containing elements to be added to
	 * this {@link ArraySet}
	 * @return {@code true} if this {@link ArraySet} changed as a
	 * result of the call
	 * @see ArraySet#add(Object)
	 * @see ArrayList#iterator() */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean modified = false;
		for (E e: c)
			if (add(e) && !modified)
				modified = true;
		return modified;
	}

	/** Removes all of the elements from this {@link ArraySet}.
	 * The set will be empty after this call returns.
	 * @see ArrayList#clear() */
	@Override
	public void clear() {
		list.clear();
	}

	/** Returns {@code true} if this {@link ArraySet} has an
	 * element that is equal to the specified object with the
	 * {@link Object#equals(Object) equals} method.
	 * @param o element whose presence in this {@link ArraySet}
	 * is to be tested
	 * @return {@code true} if this {@link ArraySet} contains
	 * the specified element
	 * @see ArrayList#contains(Object) */
	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	/** Returns {@code true} if this {@link ArraySet} contains
	 * all of the elements of the specified collection.
	 * @param c collection to be checked for containment in
	 * this set
	 * @return {@code true} if this set contains all of the
	 * elements of the specified collection
	 * @see ArrayList#containsAll(Collection) */
	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	/** Returns {@code true} if this {@link ArraySet} contains
	 * no elements.
	 * @return {@code true} if this {@link ArraySet} contains
	 * no elements
	 * @see ArrayList#isEmpty() */
	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	/** Returns a fail-fast iterator over the elements of this
	 * {@link ArraySet} in proper sequence.
	 * @return an iterator over the elements in this
	 * {@link ArraySet} in proper sequence
	 * @see ArrayList#iterator() */
	@Override
	public Iterator<E> iterator() {
		return list.iterator();
	}

	/** Removes the specified element from this {@link ArraySet}
	 * if it is present.
	 * @param o the object to be removed from this
	 * {@link ArraySet}, if present
	 * @return {@code true} if this {@link ArraySet} contained the
	 * specified element
	 * @see ArrayList#remove(Object) */
	@Override
	public boolean remove(Object o) {
		return list.remove(o);
	}

	/** Removes from this {@link ArraySet} all of its elements
	 * that are contained in the specified collection.
	 * @param c collection containing elements to be removed from
	 * this {@link ArraySet}.
	 * @return {@code true} if this {@link ArraySet} changed as
	 * a result of the call
	 * @see ArrayList#removeAll(Collection) */
	@Override
	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	/** Retains only the elements in this {@link ArraySet} that
	 * are contained in the specified collection. In other
	 * words, removes from this {@link ArraySet} all of its
	 * elements that are not contained in the specified
	 * collection.
	 * @param c collection containing elements to be retained
	 * in this {@link ArraySet}
	 * @return {@code true} if this {@link ArraySet} changed
	 * as a result of the call */
	@Override
	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	/** Returns the number of elements in this {@link ArraySet}
	 * @return the number of elements in this {@link ArraySet}
	 * @see ArrayList#size() */
	@Override
	public int size() {
		return list.size();
	}

	/** Returns an array containing all of the elements in
	 * this {@link ArraySet} in proper sequence (from first
	 * to last element). The returned array will be "safe" in
	 * that no references to it are maintained by this
	 * {@link ArraySet}. (In other words, this method must
	 * allocate a new array). The caller is thus free to modify
	 * the returned array.
	 * @return an array containing all the elements in this
	 * {@link ArraySet}
	 * @see ArrayList#toArray() */
	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	/** Returns an array containing all of the elements in this
	 * {@link ArraySet} in proper sequence (from first to last
	 * element); the runtime type of the returned array is that of
	 * the specified array. If the {@link ArraySet} fits in the
	 * specified array, it is returned therein. Otherwise, a new
	 * array is allocated with the runtime type of the specified
	 * array and the size of this {@link ArraySet}.<br>
	 * If the {@link ArraySet} fits in the specified array with
	 * room to spare (i.e., the array has more elements than the
	 * {@link ArraySet}), the element in the array immediately
	 * following the end of the collection is set to {@code null}.
	 * (This is useful in determining the length of the
	 * {@link ArraySet} only if the caller knows that the
	 * {@link ArraySet} does not contain any null elements.)
	 * @param a the array into which the elements of this
	 * {@link ArraySet} are to be stored, if it is big enough;
	 * otherwise, a new array of the same runtime type is
	 * allocated for this purpose.
	 * @return an array containing the elements of this
	 * {@link ArraySet}
	 * @see ArrayList#toArray(Object[]) */
	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}
	
	/** Performs the given action for each element of this
	 * {@link ArraySet} in proper sequence until all elements
	 * have been processed or the action throws an exception.
	 * @param action the action to be performed for each
	 * element
	 * @see ArrayList#forEach(Consumer) */
	@Override
	public void forEach(Consumer<? super E> action) {
		list.forEach(action);
	}
	
	/** Removes all of the elements of this {@link ArraySet}
	 * that satisfy the given predicate. Errors or runtime
	 * exceptions thrown during iteration or by the predicate
	 * are relayed to the caller.
	 * @param filter a predicate which returns {@code true}
	 * for elements to be removed
	 * @return {@code true} if any elements were removed
	 * @see ArrayList#removeIf(Predicate) */
	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		return list.removeIf(filter);
	}
	
	/** Creates a late-binding and fail-fast {@code Spliterator}
	 * over the elements in this {@link ArraySet}. The
	 * {@code Spliterator} reports
	 * {@link Spliterator#SIZED},
	 * {@link Spliterator#SUBSIZED}, and
	 * {@link Spliterator#ORDERED}.
	 * Overriding implementations should document the reporting of
	 * additional characteristic values.
	 * @return a {@code Spliterator} over the elements in this
	 * {@link ArraySet}.
	 * @see ArrayList#spliterator() */
	@Override
	public Spliterator<E> spliterator() {
		return list.spliterator();
	}
	
	
	//Cloning and serialization operations
	
	/** Returns a shallow copy of this {@link ArraySet} instance.
	 * The backing {@link ArrayList} is cloned. The elements
	 * themselves are not copied.
	 * @return a shallow copy of this {@link ArraySet} */
	@Override
	public Object clone() {
		try {
			@SuppressWarnings("unchecked")
			ArraySet<E> newSet = (ArraySet<E>) super.clone();
			
			@SuppressWarnings("unchecked")
			ArrayList<E> casted = (ArrayList<E>)list.clone();
			newSet.list = casted;
			return newSet;
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e);
		}
	}

	/** Serializes the backing {@link ArrayList} of
	 * this {@link ArraySet} to a stream.
	 * @serialData the backing {@link ArrayList}
	 * of this {@link ArraySet} */
	private void writeObject(java.io.ObjectOutputStream s)
			throws java.io.IOException {
		s.writeObject(list);
	}
	
	/** De-serializes the backing {@link ArrayList}
	 * from a stream. */
	private void readObject(java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		@SuppressWarnings("unchecked")
		ArrayList<E> deserialized = (ArrayList<E>)s.readObject();
		
		list = deserialized;
	}
}