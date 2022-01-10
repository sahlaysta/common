package com.github.sahlaysta.common.collection;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

//Documentation borrowed from HashMap + ArrayList
/**
 * {@link ArrayMap} class implements the {@link Map} interface and
 * uses the {@link Object#equals(Object) equals} method of objects
 * when comparing keys instead of
 * {@link Object#hashCode() hashCode} like
 * {@link java.util.HashMap HashMap} does. Provides all optional
 * map operations. {@link ArrayMap} is backed by {@link ArrayList}
 * of {@link ArrayMapEntry} objects, gettable and settable by
 * {@link #getArrayList()} and {@link #setArrayList(ArrayList)}.
 * 
 * <p>This map permits <code>null</code> keys and values, and
 * guarantees that elements will stay in the same order over time.
 * The <tt>size</tt>, <tt>isEmpty</tt>,
 * <tt>entrySet().iterator()</tt>, <tt>keySet().iterator()</tt>,
 * and <tt>values().iterator()</tt> operations run in constant
 * time. The basic operations (<code>get</code> and
 * <code>put</code>) run in linear time, along with all the
 * other operations.
 * 
 * <p>An application can increase the capacity of an
 * {@link ArrayMap} instance before adding a large number of
 * elements using the <tt>ensureCapacity</tt> operation. This may
 * reduce the amount of incremental reallocation of the backing
 * {@link ArrayList}.
 * 
 * <p><strong>Note that this implementation is not
 * synchronized.</strong>
 * If multiple threads access an
 * {@link ArrayMap} concurrently, and at least one of the threads
 * modifies the map structurally, it <i>must</i> be synchronized
 * externally.  (A structural modification is any operation that
 * adds or deletes one or more mappings; merely changing the value
 * associated with a key that an instance already contains is not a
 * structural modification.) This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the map.
 * 
 * If no such object exists, the map should be "wrapped" using the
 * {@link Collections#synchronizedMap Collections.synchronizedMap}
 * method. This is best done at creation time, to prevent accidental
 * unsynchronized access to the map.
 * 
 * <p>The iterators returned by all of this class's "collection view
 * methods" are <i>fail-fast</i>: if the map is structurally modified
 * at any time after the iterator is created, in any way except
 * through the iterator's own <tt>remove</tt> method, the iterator
 * will throw a {@link ConcurrentModificationException}. Thus, in
 * the face of concurrent modification, the iterator fails quickly
 * and cleanly, rather than risking arbitrary, non-deterministic
 * behavior at an undetermined time in the future.
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
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * 
 * @author porog
 * @see Object#equals(Object)
 * @see ArrayMapEntry
 * @see ArrayList
 * @see ArraySet
 * */
public class ArrayMap<K, V>
	extends
		AbstractMap<K, V>
	implements
		Cloneable,
		java.io.Serializable {
	
	//serialVersionUID generated as the last edit
	private static final long serialVersionUID = -2466205718851435649L;
	
	/** The backing {@link ArrayList} of this {@link ArrayMap}. */
	protected transient ArrayList<ArrayMapEntry<K, V>> entries;
	
	/** Constructs an empty {@link ArrayMap} with the specified
	 * initial capacity in the backing {@link ArrayList}.
	 * @param initialCapacity the initial capacity
	 * of the {@link ArrayMap}
	 * @see ArrayList#ArrayList(int) */
	public ArrayMap(int initialCapacity) {
		this(new ArrayList<>(initialCapacity));
	}
	
	/** Constructs an empty {@link ArrayMap} with the
	 * default initial capacity of {@link ArrayList#ArrayList()}.
	 * @see ArrayList#ArrayList() */
	public ArrayMap() {
		this(new ArrayList<>());
	}
	
	/** Constructs an {@link ArrayMap} with the specified
	 * {@link ArrayList} as the backing {@link ArrayList} of
	 * this {@link ArrayMap}. Changes to the {@link ArrayList}
	 * are reflected to the {@link ArrayMap} and vice-versa.
	 * @param arrayList the {@link ArrayList} to back
	 * the {@link ArrayMap}
	 * @see #setArrayList(ArrayList) */
	public ArrayMap(ArrayList<? extends
			ArrayMapEntry<? extends K, ? extends V>> arrayList) {
		setArrayList(arrayList);
	}
	
	/** Sets the backing {@link ArrayList} of this
	 * {@link ArrayMap} to the specified {@link ArrayList}.
	 * Changes to the {@link ArrayList} are reflected to
	 * this {@link ArrayMap} and vice-versa.
	 * @param arrayList the new {@link ArrayList} to back
	 * this {@link ArrayMap}
	 * @see #getArrayList() */
	public void setArrayList(ArrayList<? extends
			ArrayMapEntry<? extends K, ? extends V>> arrayList) {
		@SuppressWarnings("unchecked")
		ArrayList<ArrayMapEntry<K, V>> casted
			= (ArrayList<ArrayMapEntry<K, V>>)arrayList;
		this.entries = casted;
	}
	
	/** Returns the backing {@link ArrayList} of this {@link ArrayMap}.
	 * Changes to the {@link ArrayList} are reflected to this
	 * {@link ArrayMap} and vice-versa.
	 * @return the backing {@link ArrayList} of this {@link ArrayMap}
	 * @see #setArrayList(ArrayList) */
	public ArrayList<ArrayMapEntry<K, V>> getArrayList() {
		return entries;
	}
	
	/** Ensures the capacity of the backing {@link ArrayList}
	 * of this {@link ArrayMap}.
	 * @param minCapacity the desired minimum capacity
	 * @see ArrayList#ensureCapacity(int) */
	public void ensureCapacity(int minCapacity) {
		entries.ensureCapacity(minCapacity);
	}
	
	/** Constructs a new {@link ArrayMap} copying all of
	 * the mappings of the specified map, with an initial
	 * capacity of its size.
	 * @param m the map whose mappings will be copied
	 * @see ArrayList#ArrayList(int)
	 * @see ArrayMap#putAll(Map) */
	public ArrayMap(Map<? extends K, ? extends V> m) {
		this(m.size());
		putAll(m);
	}
	
	/** {@link ArrayMapEntry} is the node class of {@link ArrayMap}.
	 * It represents a map entry (key-value pair), maintaining the
	 * two variables, {@link #key} and {@link #value}. Implements
	 * the {@link Map.Entry} interface.
	 * 
	 * @param <K2> the type of key of entry
	 * @param <V2> the type of value
	 * 
	 * @author porog
	 * @see ArrayMap */
	public static class ArrayMapEntry<K2, V2> implements Map.Entry<K2, V2> {
		
		/** The key corresponding to this {@link ArrayMapEntry}. */
		protected final K2 key;
		
		/** The value corresponding to this {@link ArrayMapEntry}. */
		protected V2 value;
		
		/** Constructs an {@link ArrayMapEntry} with the
		 * specified key and value.
		 * @param key the key corresponding to the {@link ArrayMapEntry}
		 * @param value the value corresponding to
		 * the {@link ArrayMapEntry} */
		public ArrayMapEntry(K2 key, V2 value) {
			this.key = key;
			this.value = value;
		}
		
		/** Returns the key corresponding to this {@link ArrayMapEntry}.
		 * @return the key corresponding to this {@link ArrayMapEntry} */
		@Override
		public K2 getKey() {
			return key;
		}
		
		/** Returns the value corresponding to this {@link ArrayMapEntry}.
		 * @return the value corresponding to this {@link ArrayMapEntry} */
		@Override
		public V2 getValue() {
			return value;
		}
		
		/** Replaces the value of this {@link ArrayMapEntry} to the
		 * specified value (changes reflect to the {@link ArrayMap}).
		 * @param value new value to be stored in
		 * this {@link ArrayMapEntry}
		 * @return the old value corresponding to
		 * this {@link ArrayMapEntry} */
		@Override
		public V2 setValue(V2 value) {
			V2 oldVal = this.value;
			this.value = value;
			return oldVal;
		}
		
		/** Returns a string representation of
		 * this {@link ArrayMapEntry}. The resulted string is computed as:
		 *<blockquote><pre>{@link
		 *Objects#toString(Object)
		 *Objects.toString}({@link
		 *#key}) + "=" + {@link
		 *Objects#toString(Object)
		 *Objects.toString}({@link
		 *#value})</pre></blockquote>
		 * @return a string representation of this {@link ArrayMapEntry}
		 * */
		@Override
		public String toString() {
			//same as HashMap's entry toString()
			return Objects.toString(key) + "=" + Objects.toString(value);
		}
		
		/** Returns a hash code for this {@link ArrayMapEntry}. The
		 * resulted hash code is computed as:
		 *<blockquote><pre>{@link
		 *Objects#hashCode(Object)
		 *Objects.hashCode}({@link
		 *#key}) ^ {@link
		 *Objects#hashCode(Object)
		 *Objects.hashCode}({@link
		 *#value})</pre></blockquote>
		 * @return a hash code for this {@link ArrayMapEntry}
		 * */
		@Override
		public int hashCode() {
			//same as HashMap's entry hashCode()
			return Objects.hashCode(key) ^ Objects.hashCode(value);
		}
		
		/** Compares this {@link ArrayMapEntry} to the specified object.
		 * The result is {@code true} if and only if the argument is
		 * not {@code null} and is a {@link Map.Entry} object that
		 * has the same key and value as this {@link ArrayMapEntry}.
		 * @param obj the object to compare
		 * this {@link ArrayMapEntry} against
		 * @return {@code true} if the given object represents a
		 * {@link Map.Entry} equivalent to this
		 * {@link ArrayMapEntry}, {@code false} otherwise */
		@Override
		public boolean equals(Object obj) {
			//same as HashMap's entry equals
			if (obj == this)
				return true;
			if (!(obj instanceof Map.Entry))
				return false;
			Map.Entry<?, ?> e = (Map.Entry<?, ?>)obj;
			return Objects.equals(key, e.getKey())
				&& Objects.equals(value, e.getValue());
		}
	}
	
	// iterator for keySet() and values()
	private abstract class ArrayMapIterator<T> implements Iterator<T> {
		final Iterator<ArrayMapEntry<K, V>> it = entries.iterator();
		abstract T derive(ArrayMapEntry<K, V> ame);
		@Override
		public boolean hasNext() {
			return it.hasNext();
		}
		@Override
		public T next() {
			return derive(it.next());
		}
		@Override
		public void remove() {
			it.remove();
		}
		@Override
		public void forEachRemaining(Consumer<? super T> action) {
			Objects.requireNonNull(action);
			it.forEachRemaining((e) -> action.accept(derive(e)));
		}
	}
	
	//spliterator for keySet() and values()
	private abstract class ArrayMapSpliterator<T> implements Spliterator<T> {
		final Spliterator<ArrayMapEntry<K, V>> sp;
		abstract T derive(ArrayMapEntry<K, V> ame);
		ArrayMapSpliterator(Spliterator<ArrayMapEntry<K, V>> sp) {
			this.sp = sp;
		}
		@Override
		public int characteristics() {
			return sp.characteristics();
		}
		@Override
		public long estimateSize() {
			return sp.estimateSize();
		}
		@Override
		public boolean tryAdvance(Consumer<? super T> action) {
			return sp.tryAdvance((e) -> action.accept(derive(e)));
		}
		@Override
		public void forEachRemaining(Consumer<? super T> action) {
			Objects.requireNonNull(action);
			sp.forEachRemaining((e) -> action.accept(derive(e)));
		}
	}

	/** Removes all of the mappings from this {@link ArrayMap}.
	 * The {@link ArrayMap} will be empty after this call returns.
	 * @see ArrayList#clear() */
	@Override
	public void clear() {
		entries.clear();
	}

	/** Finds the mapping in this {@link ArrayMap} with an equivalent
	 * key to the specified key with the
	 * {@link Object#equals(Object) equals} method and returns
	 * {@code true}. Returns {@code false} if this {@link ArrayMap} has no
	 * mapping with the specified key.
	 * @param key the key whose presence in this {@link ArrayMap} is to
	 * be tested
	 * @return {@code true} if this {@link ArrayMap} contains a mapping
	 * with the specified key */
	@Override
	public boolean containsKey(Object key) {
		for (ArrayMapEntry<K, V> ame: entries)
			if (Objects.equals(key, ame.key))
				return true;
		return false;
	}

	/** Finds the mapping in this {@link ArrayMap} with an equivalent
	 * value to the specified value with the
	 * {@link Object#equals(Object) equals} method and returns
	 * {@code true}. Returns {@code false} if this {@link ArrayMap} has no
	 * mapping with the specified value.
	 * @param value the value whose presence in this {@link ArrayMap} is to
	 * be tested
	 * @return {@code true} if this {@link ArrayMap} contains a mapping
	 * with the specified value */
	@Override
	public boolean containsValue(Object value) {
		for (ArrayMapEntry<K, V> ame: entries)
			if (Objects.equals(value, ame.value))
				return true;
		return false;
	}

	//holds cached entrySet()
	private transient EntrySet entrySet;
	
	/** Returns a {@code Set} view of the mappings contained in
	 * this {@link ArrayMap}. The set is backed by the
	 * {@link ArrayMap}, so changes to the {@link ArrayMap} are
	 * reflected in the set, and vice-versa. The iteration of
	 * the set is fail-fast and proper sequence. The set supports
	 * element removal, which removes the corresponding mapping
	 * from the map, via the {@code Iterator.remove},
	 * {@code Set.remove}, {@code removeAll}, {@code retainAll}
	 * and {@code clear} operations. It does not support the
	 * {@code add} or {@code addAll} operations.
	 * @return a {@code Set} view of the mappings
	 * contained in this {@link ArrayMap}.
	 * @see ArrayList#iterator() */
	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		if (entrySet == null)
			entrySet = new EntrySet();
		return entrySet;
	}
	
	//the entrySet() class
	private final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
		@Override
		public int size() {
			return ArrayMap.this.size();
		}
		@Override
		public boolean contains(Object o) {
			return entries.contains(o);
		}
		@Override
		public void clear() {
			ArrayMap.this.clear();
		}
		@Override
		public boolean remove(Object o) {
			return entries.remove(o);
		}
		@SuppressWarnings("unchecked")
		@Override
		public Iterator<Map.Entry<K, V>> iterator() {
			return
				(Iterator<Map.Entry<K, V>>)
				(Iterator<?>)
				entries.iterator();
		}
		@SuppressWarnings("unchecked")
		@Override
		public Spliterator<Map.Entry<K, V>> spliterator() {
			return
				(Spliterator<Map.Entry<K, V>>)
				(Spliterator<?>)
				entries.spliterator();
		}
		@Override
		public void forEach(Consumer<? super Map.Entry<K, V>> action) {
			entries.forEach(action);
		}
	}

	
	/** Finds the mapping in this {@link ArrayMap} whose key is
	 * equal to the specified key with the
	 * {@link Object#equals(Object) equals} method, and returns its
	 * value. Returns {@code null} if this {@link ArrayMap} has no
	 * mapping with the specified key.
	 * @param key the key whose associated value in this
	 * {@link ArrayMap} is to be returned
	 * @return the value to which the specified key is mapped in
	 * this {@link ArrayMap}, or {@code null} if this {@link ArrayMap}
	 * contains no mapping for the specified key (A {@code null}
	 * return can also indicate that {@code null} is associated with
	 * the key) */
	@Override
	public V get(Object key) {
		for (ArrayMapEntry<K, V> ame: entries)
			if (Objects.equals(key, ame.key))
				return ame.value;
		return null;
	}
	
	/** Finds the mapping in this {@link ArrayMap} whose key is
	 * equal to the specified key with the
	 * {@link Object#equals(Object) equals} method, and returns its
	 * value. Returns defaultValue if this {@link ArrayMap} has no
	 * mapping with the specified key.
	 * @param key the key whose associated value in this
	 * {@link ArrayMap} is to be returned
	 * @param defaultValue the default value returned if this
	 * {@link ArrayMap} does not contain a mapping with the
	 * specified key
	 * @return the value to which the specified key is mapped in
	 * this {@link ArrayMap}, or defaultValue if this
	 * {@link ArrayMap} contains no mapping for the specified key */
	@Override
	public V getOrDefault(Object key, V defaultValue) {
		for (ArrayMapEntry<K, V> ame: entries)
			if (Objects.equals(key, ame.key))
				return ame.value;
		return defaultValue;
	}

	/** Returns {@code true} if this {@link ArrayMap} contains no
	 * key-value mappings.
	 * @return {@code true} if this {@link ArrayMap} contains no
	 * key-value mappings
	 * @see ArrayList#isEmpty() */
	@Override
	public boolean isEmpty() {
		return entries.isEmpty();
	}

	//holds cached keySet()
	private transient KeySet keySet;
	
	/** Returns a {@code Set} view of the keys contained in this
	 * {@link ArrayMap}. The set is backed by the {@link ArrayMap},
	 * so changes to the map are reflected in the set, and
	 * vice-versa. The iteration of the set is fail-fast and proper
	 * sequence. The set supports element removal, which removes the
	 * corresponding mapping from the map, via the
	 * {@code Iterator.remove}, {@code Set.remove},
	 * {@code removeAll}, {@code retainAll} and {@code clear}
	 * operations. It does not support the {@code add} or
	 * {@code addAll} operations.
	 * @return a set view of the keys contained in
	 * this {@link ArrayMap} */
	@Override
	public Set<K> keySet() {
		return keySet == null ? keySet = new KeySet() : keySet;
	}
	
	//the keySet() class
	private final class KeySet extends AbstractSet<K> {
		@Override
		public int size() {
			return ArrayMap.this.size();
		}
		@Override
		public void clear() {
			ArrayMap.this.clear();
		}
		@Override
		public boolean contains(Object o) {
			return containsKey(o);
		}
		@Override
		public boolean remove(Object o) {
			Iterator<K> it = iterator();
			while (it.hasNext()) {
				K key = it.next();
				if (Objects.equals(o, key)) {
					it.remove();
					return true;
				}
			}
			return false;
		}
		@Override
		public Iterator<K> iterator() {
			return new KeyIterator();
		}
		@Override
		public Spliterator<K> spliterator() {
			return new KeySpliterator(entries.spliterator());
		}
		@Override
		public void forEach(Consumer<? super K> action) {
			Objects.requireNonNull(action);
			entries.forEach(ame -> action.accept(ame.key));
		}
	}
	
	private final class KeyIterator extends ArrayMapIterator<K> {
		@Override
		K derive(ArrayMapEntry<K, V> ame) {
			return ame.key;
		}
	}
	private final class KeySpliterator extends ArrayMapSpliterator<K> {
		KeySpliterator(Spliterator<ArrayMapEntry<K, V>> sp) {
			super(sp);
		}
		@Override
		public Spliterator<K> trySplit() {
			return new KeySpliterator(sp.trySplit());
		}
		@Override
		K derive(ArrayMapEntry<K, V> ame) {
			return ame.key;
		}
	}
	

	/** Associates the specified value with the specified key in
	 * this {@link ArrayMap}. If this {@link ArrayMap} previously
	 * contained a mapping for the key, its value is replaced by
	 * the specified value.
	 * @param key the key with which the specified value is to
	 * be associated
	 * @param value the value to be associated with the
	 * specified key
	 * @return the previous value associated with the key, or
	 * {@code null} if there was no mapping for the key (A
	 * {@code null} return can also indicate that {@code null}
	 * was associated with the key) */
	@Override
	public V put(K key, V value) {
		for (ArrayMapEntry<K, V> ame: entries) {
			if (Objects.equals(key, ame.key)) {
				V oldVal = ame.value;
				ame.value = value;
				return oldVal;
			}
		}
		entries.add(new ArrayMapEntry<K, V>(key, value));
		return null;
	}

	/** Copies all of the mappings from the specified map to
	 * this {@link ArrayMap}, using its
	 * {@link Map#forEach(BiConsumer) forEach} method.
	 * The effect of this call is equivalent to that of calling
	 * {@link #put(Object, Object) put(k, v)}
	 * on this {@link ArrayMap} once for each mapping from
	 * key {@code k} to value {@code v} in the specified map.
	 * @param m the map whose mappings are to be copied to
	 * this {@link ArrayMap} */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		m.forEach((k, v) -> put(k, v));
	}

	/** Finds the mapping in this {@link ArrayMap} whose key is
	 * equal to the specified key with the
	 * {@link Object#equals(Object) equals} method and removes
	 * it.
	 * @param key the key whose mapping is to be removed from
	 * this {@link ArrayMap}
	 * @return the value of the mapping that was removed, or
	 * {@code null} if there was no mapping for the key
	 * (A {@code null} return can also indicate that
	 * {@code null} was associated with the key) */
	@Override
	public V remove(Object key) {
		Iterator<ArrayMapEntry<K, V>> it = entries.iterator();
		while (it.hasNext()) {
			ArrayMapEntry<K, V> ame = it.next();
			if (Objects.equals(key, ame.key)) {
				it.remove();
				return ame.value;
			}
		}
		return null;
	}

	/** Returns the number of key-value mappings
	 * in this {@link ArrayMap}
	 * @return the number of key-value mappings
	 * in this {@link ArrayMap}
	 * @see ArrayList#size() */
	@Override
	public int size() {
		return entries.size();
	}

	
	//holds cached values()
	private transient Values values;
	
	/** Returns a {@code Collection} view of the values contained
	 * in this {@link ArrayMap}. The collection is backed by the map, so
	 * changes to the map are reflected in the collection, and
	 * vice-versa. The iteration of the collection is fail-fast and
	 * proper sequence. The collection does not support the {@code add}
	 * or {@code addAll} operations. The collection supports element
	 * removal, which removes the corresponding mapping from the map,
	 * but exclusively via the {@code clear} and the
	 * {@code Iterator.remove} methods.
	 * @return a collection view of the values contained in this
	 * {@link ArrayMap} */
	@Override
	public Collection<V> values() {
		return values == null ? values = new Values() : values;
	}
	
	private final class Values extends AbstractCollection<V> {
		@Override
		public int size() {
			return ArrayMap.this.size();
		}
		@Override
		public void clear() {
			ArrayMap.this.clear();
		}
		@Override
		public boolean contains(Object o) {
			return containsValue(o);
		}
		@Override
		public void forEach(Consumer<? super V> action) {
			Objects.requireNonNull(action);
			entries.forEach(ame -> action.accept(ame.value));
		}
		@Override
		public Iterator<V> iterator() {
			return new ValueIterator();
		}
		@Override
		public Spliterator<V> spliterator() {
			return new ValueSpliterator(entries.spliterator());
		}
	}
	
	private final class ValueIterator extends ArrayMapIterator<V> {
		@Override
		V derive(ArrayMapEntry<K, V> ame) {
			return ame.value;
		}
	}
	
	private final class ValueSpliterator extends ArrayMapSpliterator<V> {
		ValueSpliterator(Spliterator<ArrayMapEntry<K, V>> sp) {
			super(sp);
		}
		@Override
		public Spliterator<V> trySplit() {
			return new ValueSpliterator(sp.trySplit());
		}
		@Override
		V derive(ArrayMapEntry<K, V> ame) {
			return ame.value;
		}
	}
	
	
	/** Computes a mapping for the specified key and its current mapped value
	 * (or {@code null} if there is no current mapping). If the function
	 * returns {@code null}, the mapping is removed (or remains absent if
	 * initially absent). If the function itself throws an (unchecked)
	 * exception, the exception is rethrown, and the current mapping is
	 * left unchanged.
	 * @param key key with which the specified value is to be associated
	 * @param remappingFunction the function to compute a value
	 * @return the new value associated with the specified key, or
	 * {@code null} if none
	 * @throws NullPointerException the remappingFunction is {@code null}
	 * */
	@Override
	public V compute(
			K key,
			BiFunction<? super K, ? super V, ? extends V>
				remappingFunction) {
		Objects.requireNonNull(remappingFunction);
		
		Iterator<ArrayMapEntry<K, V>> it = entries.iterator();
		while (it.hasNext()) {
			ArrayMapEntry<K, V> ame = it.next();
			if (Objects.equals(key, ame.key)) {
				V newValue = remappingFunction.apply(key, ame.value);
				if (newValue == null) {
					it.remove();
					return null;
				} else {
					return ame.value = newValue;
				}
			}
		}
		
		//key not contained
		V newValue = remappingFunction.apply(key, null);
		if (newValue == null) {
			return null;
		} else {
			entries.add(new ArrayMapEntry<K, V>(key, newValue));
			return newValue;
		}
	}
	
	/** If the specified key is not already associated with a value (or is mapped
	 * to {@code null}), attempts to compute its value using the given mapping
	 * function and enters it into this {@link ArrayMap} unless {@code null}.
	 * If the function returns {@code null} no mapping is recorded. If the function
	 * itself throws an (unchecked) exception, the exception is rethrown, and no
	 * mapping is recorded.
	 * @param key key with which the specified value is to be associated
	 * @param mappingFunction the function to compute a value
	 * @return the current (existing or computed) value associated with the
	 * specified key, or {@code null} if the computed value is {@code null}
	 * @throws NullPointerException the mappingFunction is {@code null}
	 * */
	@Override
	public V computeIfAbsent(
			K key,
			Function<? super K, ? extends V>
				mappingFunction) {
		Objects.requireNonNull(mappingFunction);
		
		for (ArrayMapEntry<K, V> ame: entries)
			if (Objects.equals(key, ame.key))
				return ame.value;
		
		V newValue = mappingFunction.apply(key);
		if (newValue == null) {
			return null;
		} else {
			entries.add(new ArrayMapEntry<K, V>(key, newValue));
			return newValue;
		}
	}
	
	/** If the value for the specified key is present and non-null,
	 * attempts to compute a new mapping given the key and its
	 * current mapped value. If the function returns {@code null},
	 * the mapping is removed. If the function itself throws an
	 * (unchecked) exception, the exception is rethrown, and the
	 * current mapping is left unchanged.
	 * @param key key with which the specified value is to be
	 * associated
	 * @param remappingFunction the function to compute a value
	 * @return the new value associated with the specified key,
	 * or {@code null} if none
	 * @throws NullPointerException the remappingFunction
	 * is {@code null}
	 * */
	@Override
	public V computeIfPresent(
			K key,
			BiFunction<? super K, ? super V, ? extends V>
				remappingFunction) {
		Objects.requireNonNull(remappingFunction);
		
		Iterator<ArrayMapEntry<K, V>> it = entries.iterator();
		while (it.hasNext()) {
			ArrayMapEntry<K, V> ame = it.next();
			if (Objects.equals(key, ame.key)) {
				V newValue = remappingFunction.apply(key, ame.value);
				if (newValue == null) {
					it.remove();
					return null;
				} else {
					return ame.value = newValue;
				}
			}
		}
		
		return null;
	}
	
	/** If the specified key is not already associated with a value or
	 * is associated with {@code null}, associates it with the given
	 * non-null value. Otherwise, replaces the associated value with
	 * the results of the given remapping function, or removes if the
	 * result is {@code null}. If the function returns {@code null}
	 * the mapping is removed. If the function itself throws an
	 * (unchecked) exception, the exception is rethrown, and the
	 * current mapping is left unchanged.
	 * @param key key with which the resulting value is
	 * to be associated
	 * @param value the non-null value to be merged with the existing
	 * value associated with the key or, if no existing value or
	 * a {@code null} value is associated with the key, to be
	 * associated with the key
	 * @param remappingFunction the function to recompute a value
	 * if present
	 * @return the new value associated with the specified key, or
	 * {@code null} if no value is associated with the key
	 * @throws NullPointerException the value or
	 * remappingFunction is {@code null} */
	@Override
	public V merge(
			K key,
			V value,
			BiFunction<? super V, ? super V, ? extends V>
				remappingFunction) {
		Objects.requireNonNull(remappingFunction);
		Objects.requireNonNull(value);
		
		Iterator<ArrayMapEntry<K, V>> it = entries.iterator();
		while (it.hasNext()) {
			ArrayMapEntry<K, V> ame = it.next();
			if (Objects.equals(key, ame.key)) {
				V newValue = remappingFunction.apply(ame.value, value);
				if (newValue == null) {
					it.remove();
					return null;
				} else {
					return ame.value = newValue;
				}
			}
		}
		
		entries.add(new ArrayMapEntry<K, V>(key, value));
		return value;
	}
	
	/** Performs the given action for each entry in this {@link ArrayMap}
	 * in proper sequence until all entries have been processed or the
	 * action throws an exception. Exceptions thrown by the action are
	 * relayed to the caller.
	 * @param action the action to be performed for each entry
	 * @throws NullPointerException the action is {@code null}
	 * @throws ConcurrentModificationException if an entry is found to
	 * be removed during iteration */
	@Override
	public void forEach(
			BiConsumer<? super K, ? super V>
				action) {
		Objects.requireNonNull(action);
		entries.forEach(ame -> action.accept(ame.key, ame.value));
	}
	
	/** If the specified key is not already associated with a value
	 * (or is mapped to {@code null}) associates it with the given
	 * value and returns {@code null}, else returns the current value.
	 * @param key key with which the specified value is to be associated
	 * @param value value to be associated with the specified key
	 * @return {@code null} if previously absent, the mapped
	 * value otherwise */
	@Override
	public V putIfAbsent(K key, V value) {
		for (ArrayMapEntry<K, V> ame: entries) {
			if (Objects.equals(key, ame.key)) {
				if (ame.value == null) {
					ame.value = value;
					return null;
				} else {
					return ame.value;
				}
			}
		}
		
		entries.add(new ArrayMapEntry<K, V>(key, value));
		return null;
	}
	
	/** Removes the entry for the specified key 
	 * only if it is currently mapped to the
	 * specified value.
	 * @param key key with which the specified
	 * value is associated
	 * @param value value expected to be
	 * associated with the specified key
	 * @return {@code true} if the value was
	 * removed */
	@Override
	public boolean remove(Object key, Object value) {
		Iterator<ArrayMapEntry<K, V>> it = entries.iterator();
		while (it.hasNext()) {
			ArrayMapEntry<K, V> ame = it.next();
			if (Objects.equals(key, ame.key)
					&& Objects.equals(value, ame.value)) {
				it.remove();
				return true;
			}
		}
		return false;
	}
	
	/** Replaces the entry for the specified key only if it
	 * is mapped in this {@link ArrayMap}.
	 * @param key key with which the specified value is associated
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with the
	 * specified key, or {@code null} if there was no mapping for
	 * the key. (A {@code null} return can also indicate that a
	 * {@code null} value was associated with the key). */
	@Override
	public V replace(K key, V value) {
		for (ArrayMapEntry<K, V> ame: entries) {
			if (Objects.equals(key, ame.key)) {
				V oldVal = ame.value;
				ame.value = value;
				return oldVal;
			}
		}
		return null;
	}
	
	/** Replaces the entry for the specified key only if mapped
	 * to the specified value.
	 * @param key key with which the specified value is associated
	 * @param oldValue value expected to be associated with
	 * the specified key
	 * @param newValue value to be associated with the specified key
	 * @return {@code true} if the value was replaced */
	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		for (ArrayMapEntry<K, V> ame: entries) {
			if (Objects.equals(key, ame.key)
					&& Objects.equals(oldValue, ame.value)) {
				ame.value = newValue;
				return true;
			}
		}
		return false;
	}
	
	/** Replaces each entry's value with the result of invoking
	 * the given function on that entry until all entries have
	 * been processed or the function throws an exception.
	 * Exceptions thrown by the function are relayed to the
	 * caller.
	 * @param function the function to apply
	 * to each entry
	 * @throws NullPointerException the
	 * function is {@code null}
	 * @throws ConcurrentModificationException
	 * if an entry is found to be removed during
	 * iteration */
	@Override
	public void replaceAll(
			BiFunction<? super K, ? super V, ? extends V>
				function) {
		Objects.requireNonNull(function);
		entries.forEach(ame ->
			ame.value = function.apply(ame.key, ame.value));
	}
	
	
	
	//Cloning and serialization operations
	
	/** Returns a shallow copy of this {@link ArrayMap}
	 * instance, with new copies of the {@link ArrayMapEntry}
	 * objects. The elements themselves are not copied.
	 * @return a shallow copy of this {@link ArrayMap}
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public Object clone() {
		try {
			ArrayMap<K, V> newMap = (ArrayMap<K, V>)super.clone();
			
			ArrayList<ArrayMapEntry<K, V>> newList
				= new ArrayList<>(entries.size());
			entries.forEach(entry -> newList.add(
				new ArrayMapEntry<>(entry.key, entry.value)));
			
			newMap.entries = newList;
			return newMap;
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e);
		}
	}
	
	/** Save the state of the {@link ArrayMap} instance to a
	 * stream (that is, serialize it).
	 * 
	 * @serialData the size of this {@link ArrayMap}, followed
	 * by the key (Object) and value (Object) for each
	 * key-value mapping. The key-value mappings are emitted
	 * in the proper order */
	private void writeObject(java.io.ObjectOutputStream s)
			throws java.io.IOException {
		s.defaultWriteObject();
		s.writeInt(size());
		for (ArrayMapEntry<K, V> ame: entries) {
			s.writeObject(ame.key);
			s.writeObject(ame.value);
		}
	}
	
	/** Reconstitute the {@link ArrayMap} from a
	 * stream (that is, deserialize it). */
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		int size = s.readInt();
		this.entries = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
			this.entries.add(new ArrayMapEntry<K, V>(
				(K) s.readObject(),
				(V) s.readObject()));
	}
}