package sahlaysta.common.collection;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The <tt>CollectionBackedMap</tt> class provides static methods
 * that return a new map view that is backed by a given collection.
 * The backing collection of a collection backed map must be a
 * {@link CBMEntry} collection. This may be done as illustrated by
 * the following code segment:<pre><codeblock><!--
 *-->Map&lt;Object, Object&gt; map = CollectionBackedMap.newMap(new ArrayList&lt;&gt;());<!--
 *--></pre></codeblock>
 * Collection backed maps specify all map operations, including
 * all optional map operations.
 * Collection backed maps follow a {@link CBMModel}, and
 * compare keys and values using the boolean-valued functions of
 * the model, such as
 * {@link CBMModel#compareKeyToObj(Object, Object)}.
 * 
 * <p>Collection backed maps permit <code>null</code> keys and values.
 * The basic operations (<code>get</code> and <code>put</code>)
 * run in linear time, performing an iteration over the backing
 * collection when comparing keys or values. Collection backed
 * maps display the same ordering, concurrency, and performance
 * characteristics as the backing collection in this regard.
 * 
 * <p>Collection backed maps are serializable if the
 * backing collection is serializable.
 * 
 * @author porog
 * 
 * @see CollectionBackedMap#newMap(Collection)
 * @see CollectionBackedMap#newMap(Collection, CBMModel)
 * @see CBMEntry
 * @see CBMModel
 * */
public final class CollectionBackedMap {
	
	private CollectionBackedMap() {}
	
	
	//cbmmap
	/**
	 * Returns a new map view that is backed by the specified
	 * collection and uses the specified {@link CBMModel} to
	 * compare elements.
	 * 
	 * <p>The returned map specifies all map operations, including
	 * all optional map operations. The map uses the specified
	 * {@link CBMModel} to compare keys and values, using its
	 * boolean-valued functions such as
	 * {@link CBMModel#compareKeyToObj(Object, Object)}.
	 * 
	 * <p>The returned map permits <code>null</code> keys
	 * and values. The basic operations (<code>get</code>
	 * and <code>put</code>) run in linear time, performing an
	 * iteration over the specified collection when comparing
	 * keys or values. The returned map displays the same
	 * ordering, concurrency, and performance characteristics
	 * as the specified collection in this regard.
	 * 
	 * <p>The returned map will be serializable if the
	 * specified collection is also serializable.
	 * 
	 * @param c the backing collection
	 * @param cbmModel the model for element comparison
	 * @param <K> the type of key of the map
	 * @param <V> the type of value of the map
	 * @return a new map view that is backed by the specified
	 * collection and uses the specified {@link CBMModel} to
	 * compare elements
	 * @see CollectionBackedMap
	 * @see CBMEntry
	 * @see CBMModel
	 * */
	public static <K, V> Map<K, V> newMap(
			Collection<? extends CBMEntry<? extends K, ? extends V>> c,
			CBMModel<? super K, ? super V> cbmModel) {
		@SuppressWarnings("unchecked")
		Collection<CBMEntry<K, V>> collection = (Collection<CBMEntry<K, V>>)c;
		@SuppressWarnings("unchecked")
		CBMModel<K, V> model = (CBMModel<K, V>)cbmModel;
		return new CBM<>(collection, model);
	}
	
	/**
	 * Returns a new map view that is backed by the specified
	 * collection.
	 * 
	 * <p>The returned map specifies all map operations, including
	 * all optional map operations. The map uses the
	 * {@link Objects#equals(Object, Object)} method when
	 * comparing keys and values.
	 * 
	 * <p>The returned map permits <code>null</code> keys
	 * and values. The basic operations (<code>get</code>
	 * and <code>put</code>) run in linear time, performing an
	 * iteration over the specified collection when comparing
	 * keys or values. The returned map displays the same
	 * ordering, concurrency, and performance characteristics
	 * as the specified collection in this regard.
	 * 
	 * <p>The returned map will be serializable if the
	 * specified collection is also serializable.
	 * 
	 * @param c the backing collection
	 * @param <K> the type of key of the map
	 * @param <V> the type of value of the map
	 * @return a new map view that is backed
	 * by the specified collection
	 * @see CollectionBackedMap
	 * @see CBMEntry
	 * @see CBMModel
	 * */
	public static <K, V> Map<K, V> newMap(
			Collection<? extends CBMEntry<? extends K, ? extends V>> c) {
		@SuppressWarnings("unchecked")
		Collection<CBMEntry<K, V>> collection = (Collection<CBMEntry<K, V>>)c;
		@SuppressWarnings("unchecked")
		CBMModel<K, V> model = (CBMModel<K, V>)DEFAULT_CBM_MODEL;
		return newMap(collection, model);
	}
	private static final Object DEFAULT_CBM_MODEL = new CBMModel<Object, Object>() {
		private static final long serialVersionUID = 2236371537458861330L;
		@Override
		public boolean compareKeys(Object k1, Object k2) {
			return Objects.equals(k1, k2);
		}
		@Override
		public boolean compareKeyToObj(Object key, Object obj) {
			return Objects.equals(key, obj);
		}
		@Override
		public boolean compareValues(Object v1, Object v2) {
			return Objects.equals(v1, v2);
		}
		@Override
		public boolean compareValueToObj(Object value, Object obj) {
			return Objects.equals(value, obj);
		}
	};
	
	
	
	//cbmmodel
	/** <tt>CBMModel</tt> (collection backed map model) is an
	 * interface that defines boolean-valued functions used
	 * in an underlying collection backed map, including the
	 * comparison of keys or values.
	 * @param <K> the key type of the map
	 * @param <V> the value type of the map
	 * @see CollectionBackedMap
	 * */
	public static interface CBMModel<K, V> extends java.io.Serializable {
		
		/** Compares the two specified keys.
		 * It is possible for both arguments to be the
		 * same object, and it is also possible that one
		 * or both arguments are {@code null}.
		 * @param k1 the first key to compare
		 * @param k2 the second key to compare
		 * @return {@code true} if the two keys display
		 * a positive comparison, {@code false} otherwise
		 * */
		public boolean compareKeys(K k1, K k2);
		
		/** Compares the specified key to the specified
		 * object.
		 * It is possible for both arguments
		 * to be the same object, and it is also possible
		 * that one or both arguments are {@code null}.
		 * @param key the key to compare to the object
		 * @param obj the object to compare the key to
		 * @return {@code true} if the two arguments
		 * display a positive comparison, {@code false}
		 * otherwise
		 * */
		public boolean compareKeyToObj(K key, Object obj);
		
		/** Compares the two specified values.
		 * It is possible for both arguments to be the
		 * same object, and it is also possible that one
		 * or both arguments are {@code null}.
		 * @param v1 the first value to compare
		 * @param v2 the second value to compare
		 * @return {@code true} if the two values display
		 * a positive comparison, {@code false} otherwise
		 * */
		public boolean compareValues(V v1, V v2);
		
		/** Compares the specified value to the specified
		 * object.
		 * It is possible for both arguments
		 * to be the same object, and it is also possible
		 * that one or both arguments are {@code null}.
		 * @param value the value to compare to the object
		 * @param obj the object to compare the value to
		 * @return {@code true} if the two arguments
		 * display a positive comparison, {@code false}
		 * otherwise
		 * */
		public boolean compareValueToObj(V value, Object obj);
	}
	
	
	//cbmentry
	/** <tt>CBMEntry</tt> (collection backed map
	 * entry) is the node entry class used by
	 * collection backed maps.
	 * A {@link CBMEntry} instance
	 * maintains a key-value-pair.
	 * @param <K> the type of key maintained
	 * by the map
	 * @param <V> the type of value maintained
	 * by the map
	 * @see CollectionBackedMap
	 * */
	public static class CBMEntry<K, V> implements Cloneable, java.io.Serializable {
		
		private static final long serialVersionUID = 7133150580319061578L;
		
		//constructor
		private final K key;
		private V value;
		/** Creates a new {@link CBMEntry} with
		 * the specified key-value-pair.
		 * @param key the key associated with
		 * the key-value-pair
		 * @param value the value associated with
		 * the key-value-pair */
		public CBMEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}
		
		/** Returns the key corresponding to
		 * this key-value-pair.
		 * @return the key corresponding to
		 * this key-value-pair */
		public K getKey() {
			return key;
		}
		
		/** Returns the value corresponding to
		 * this key-value-pair.
		 * @return the value corresponding to
		 * this key-value-pair */
		public V getValue() {
			return value;
		}
		
		/** Replaces the value of this key-value-pair
		 * to the specified value (change will reflect
		 * to the underlying collection backed map).
		 * @param value the new value to store in
		 * this key-value-pair
		 * @return the old value corresponding to
		 * this key-value-pair */
		public V setValue(V value) {
			V oldVal = this.value;
			this.value = value;
			return oldVal;
		}
		
		/** Returns a string representation of
		 * this key-value-pair. The resulted string is computed
		 * as:<blockquote><pre><!--
		 * -->Objects.toString(key) + "=" + Objects.toString(value)<!--
		 * --></pre></blockquote>
		 * @return a string representation of this key-value-pair
		 * @see Objects#toString(Object)
		 * */
		@Override
		public String toString() {
			return Objects.toString(key) + "=" + Objects.toString(value);
		}
		
		/** Returns a hash code for this key-value-pair. The
		 * resulted hash code is computed
		 * as:<blockquote><pre><!--
		 * -->Objects.hashCode(key) ^ Objects.hashCode(value)<!--
		 * --></pre></blockquote>
		 * @return a hash code for this key-value-pair
		 * @see Objects#hashCode(Object)
		 * */
		@Override
		public int hashCode() {
			return Objects.hashCode(key) ^ Objects.hashCode(value);
		}
		
		/** Compares this {@link CBMEntry} to the specified object.
		 * The result is {@code true} if and only if the
		 * argument is not {@code null} and is a {@link CBMEntry}
		 * object that has the same key-value-pair
		 * as this {@link CBMEntry}.
		 * @param obj the object to compare this {@link CBMEntry} against
		 * @return {@code true} if the given object represents a
		 * {@link CBMEntry} equivalent to this {@link CBMEntry}, {@code false}
		 * otherwise
		 * */
		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (!(obj instanceof CBMEntry))
				return false;
			CBMEntry<?, ?> e = (CBMEntry<?, ?>)obj;
			return Objects.equals(key, e.key)
				&& Objects.equals(value, e.value);
		}
		
		
		//serialization and cloning
		/** Returns a shallow copy of this {@link CBMEntry}
		 * instance. Neither the key nor value themselves
		 * are copied.
		 * @return a shallow copy of this {@link CBMEntry}
		 * */
		@Override
		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				throw new InternalError(e);
			}
		}
	}
	
	
	//collection backed map class
	private static final class CBM<K, V>
			extends AbstractMap<K, V>
			implements java.io.Serializable {
		
		private static final long serialVersionUID = 2253057114744262697L;
		
		//Constructor
		final Collection<CBMEntry<K, V>> bc;//the backing collection
		final CBMModel<K, V> m;//the cbm model
		CBM(Collection<CBMEntry<K, V>> c, CBMModel<K, V> cbmModel) {
			this.bc = c;
			this.m = cbmModel;
		}
		
		//Abstract derivable iterator
		abstract class Itr<E> implements Iterator<E> {
			abstract E derive(CBMEntry<K, V> e);
			
			final Iterator<CBMEntry<K, V>> it = bc.iterator();
			@Override
			public boolean hasNext() {
				return it.hasNext();
			}
			@Override
			public E next() {
				return derive(it.next());
			}
			@Override
			public void forEachRemaining(Consumer<? super E> action) {
				Objects.requireNonNull(action);
				it.forEachRemaining(e -> action.accept(derive(e)));
			}
			@Override
			public void remove() {
				it.remove();
			}
		}
		
		//Abstract derivable spliterator
		abstract class Splitr<E> implements Spliterator<E> {
			abstract E derive(CBMEntry<K, V> e);
			
			final Spliterator<CBMEntry<K, V>> s;
			Splitr(Spliterator<CBMEntry<K, V>> s) {
				this.s = s;
			}
			Splitr() {
				this(bc.spliterator());
			}
			@Override
			public int characteristics() {
				return s.characteristics();
			}
			@Override
			public long estimateSize() {
				return s.estimateSize();
			}
			@Override
			public void forEachRemaining(Consumer<? super E> action) {
				Objects.requireNonNull(action);
				s.forEachRemaining(e -> action.accept(derive(e)));
			}
			@Override
			public long getExactSizeIfKnown() {
				return s.getExactSizeIfKnown();
			}
			@Override
			public boolean hasCharacteristics(int characteristics) {
				return s.hasCharacteristics(characteristics);
			}
			@Override
			public boolean tryAdvance(Consumer<? super E> action) {
				Objects.requireNonNull(action);
				return s.tryAdvance(e -> action.accept(derive(e)));
			}
			@Override
			public Spliterator<E> trySplit() {
				return new Splitr<E>(s.trySplit()) {
					@Override
					E derive(CBMEntry<K, V> e) {
						return Splitr.this.derive(e);
					}
				};
			}
		}
		
		
		
		//Map operations
		
		@Override
		public void clear() {
			bc.clear();
		}

		@Override
		public boolean containsKey(Object key) {
			for (CBMEntry<K, V> e: bc)
				if (m.compareKeyToObj(e.key, key))
					return true;
			return false;
		}

		@Override
		public boolean containsValue(Object value) {
			for (CBMEntry<K, V> e: bc)
				if (m.compareValueToObj(e.value, value))
					return true;
			return false;
		}

		transient EntrySet entrySet; //cached entryset
		@Override
		public Set<Map.Entry<K, V>> entrySet() {
			return entrySet == null ? entrySet = new EntrySet() : entrySet;
		}
		final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
			Map.Entry<K, V> toMapEntry(CBMEntry<K, V> e) {
				//return Map.Entry view of a CBMEntry
				return new Map.Entry<K, V>() {
					@Override
					public K getKey() {
						return e.key;
					}
					@Override
					public V getValue() {
						return e.value;
					}
					@Override
					public V setValue(V value) {
						V oldVal = e.value;
						e.value = value;
						return oldVal;
					}
					@Override
					public String toString() {
						return
							Objects.toString(e.key)
							+ "=" +
							Objects.toString(e.value); 
					}
					@Override
					public int hashCode() {
						return
							Objects.hashCode(e.key)
							^
							Objects.hashCode(e.value);
					}
					@Override
					public boolean equals(Object obj) {
						if (obj == this)
							return true;
						if (!(obj instanceof Map.Entry))
							return false;
						Map.Entry<?, ?> me = (Map.Entry<?, ?>)obj;
						return
							m.compareKeyToObj(
								e.key, me.getKey())
							&& m.compareValueToObj(
								e.value, me.getValue());
					}
				};
			}
			@Override
			public int size() {
				return CBM.this.size();
			}
			@Override
			public void clear() {
				CBM.this.clear();
			}
			@Override
			public Iterator<Map.Entry<K, V>> iterator() {
				return new Itr<Map.Entry<K, V>>() {
					@Override
					Map.Entry<K, V> derive(CBMEntry<K, V> e) {
						return toMapEntry(e);
					}
				};
			}
			@Override
			public boolean contains(Object o) {
				if (!(o instanceof Map.Entry))
					return false;
				Map.Entry<?, ?> me = (Map.Entry<?, ?>)o;
				for (CBMEntry<K, V> e: bc) {
					if (m.compareKeyToObj(
						e.key, me.getKey())
					&& m.compareValueToObj(
						e.value, me.getValue())) {
						return true;
					}
				}
				return false;
			}
			@Override
			public boolean remove(Object o) {
				if (!(o instanceof Map.Entry))
					return false;
				Map.Entry<?, ?> me = (Map.Entry<?, ?>)o;
				Iterator<CBMEntry<K, V>> it = bc.iterator();
				while (it.hasNext()) {
					CBMEntry<K, V> e = it.next();
					if (m.compareKeyToObj(
						e.key, me.getKey())
					&& m.compareValueToObj(
						e.value, me.getValue())) {
						it.remove();
						return true;
					}
				}
				return false;
			}
			@Override
			public Spliterator<Map.Entry<K, V>> spliterator() {
				return new Splitr<Map.Entry<K, V>>() {
					@Override
					Map.Entry<K, V> derive(CBMEntry<K, V> e) {
						return toMapEntry(e);
					}
				};
			}
			@Override
			public void forEach(Consumer<? super Map.Entry<K, V>> action) {
				Objects.requireNonNull(action);
				bc.forEach(e -> action.accept(toMapEntry(e)));
			}
		}

		@Override
		public V get(Object key) {
			for (CBMEntry<K, V> e: bc)
				if (m.compareKeyToObj(e.key, key))
					return e.value;
			return null;
		}

		@Override
		public boolean isEmpty() {
			return bc.isEmpty();
		}

		transient KeySet keySet; //cached keyset
		@Override
		public Set<K> keySet() {
			return keySet == null ? keySet = new KeySet() : keySet;
		}
		final class KeySet extends AbstractSet<K> {
			@Override
			public int size() {
				return CBM.this.size();
			}
			@Override
			public void clear() {
				CBM.this.clear();
			}
			@Override
			public Iterator<K> iterator() {
				return new Itr<K>() {
					@Override
					K derive(CBMEntry<K, V> e) {
						return e.key;
					}
				};
			}
			@Override
			public boolean contains(Object o) {
				return CBM.this.containsKey(o);
			}
			@Override
			public boolean remove(Object o) {
				Iterator<CBMEntry<K, V>> it = bc.iterator();
				while (it.hasNext()) {
					CBMEntry<K, V> e = it.next();
					if (m.compareKeyToObj(e.key, o)) {
						it.remove();
						return true;
					}
				}
				return false;
			}
			@Override
			public Spliterator<K> spliterator() {
				return new Splitr<K>() {
					@Override
					K derive(CBMEntry<K, V> e) {
						return e.key;
					}
				};
			}
			@Override
			public void forEach(Consumer<? super K> action) {
				Objects.requireNonNull(action);
				bc.forEach(e -> action.accept(e.key));
			}
		}

		@Override
		public V put(K key, V value) {
			for (CBMEntry<K, V> e: bc) {
				if (m.compareKeys(e.key, key)) {
					V oldVal = e.value;
					e.value = value;
					return oldVal;
				}
			}
			
			bc.add(new CBMEntry<>(key, value));
			return null;
		}

		@Override
		public void putAll(Map<? extends K, ? extends V> m) {
			m.forEach((k, v) -> put(k, v));
		}

		@Override
		public V remove(Object key) {
			Iterator<CBMEntry<K, V>> it = bc.iterator();
			while (it.hasNext()) {
				CBMEntry<K, V> e = it.next();
				if (m.compareKeyToObj(e.key, key)) {
					it.remove();
					return e.value;
				}
			}
			return null;
		}

		@Override
		public int size() {
			return bc.size();
		}

		transient Values values; //cached values
		@Override
		public Collection<V> values() {
			return values == null ? values = new Values() : values;
		}
		final class Values extends AbstractSet<V> {
			@Override
			public int size() {
				return CBM.this.size();
			}
			@Override
			public void clear() {
				CBM.this.clear();
			}
			@Override
			public Iterator<V> iterator() {
				return new Itr<V>() {
					@Override
					V derive(CBMEntry<K, V> e) {
						return e.value;
					}
				};
			}
			@Override
			public boolean contains(Object o) {
				return CBM.this.containsValue(o);
			}
			@Override
			public Spliterator<V> spliterator() {
				return new Splitr<V>() {
					@Override
					V derive(CBMEntry<K, V> e) {
						return e.value;
					}
				};
			}
			@Override
			public void forEach(Consumer<? super V> action) {
				Objects.requireNonNull(action);
				bc.forEach(e -> action.accept(e.value));
			}
		}
		
		
		

		
		@Override
		public V compute(
				K key,
				BiFunction<? super K, ? super V, ? extends V>
					remappingFunction) {
			Objects.requireNonNull(remappingFunction);
			
			Iterator<CBMEntry<K, V>> it = bc.iterator();
			while (it.hasNext()) {
				CBMEntry<K, V> e = it.next();
				if (m.compareKeys(e.key, key)) {
					V newValue = remappingFunction.apply(key, e.value);
					if (newValue == null) {
						it.remove();
						return null;
					} else {
						return e.value = newValue;
					}
				}
			}
			
			//key not contained
			V newValue = remappingFunction.apply(key, null);
			if (newValue == null) {
				return null;
			} else {
				bc.add(new CBMEntry<>(key, newValue));
				return newValue;
			}
		}
		
		@Override
		public V computeIfAbsent(
				K key,
				Function<? super K, ? extends V>
					mappingFunction) {
			Objects.requireNonNull(mappingFunction);
			
			for (CBMEntry<K, V> e: bc)
				if (m.compareKeys(e.key, key))
					return e.value;
			
			V newValue = mappingFunction.apply(key);
			if (newValue == null) {
				return null;
			} else {
				bc.add(new CBMEntry<>(key, newValue));
				return newValue;
			}
		}
		
		@Override
		public V computeIfPresent(
				K key,
				BiFunction<? super K, ? super V, ? extends V>
					remappingFunction) {
			Objects.requireNonNull(remappingFunction);
			
			Iterator<CBMEntry<K, V>> it = bc.iterator();
			while (it.hasNext()) {
				CBMEntry<K, V> e = it.next();
				if (m.compareKeys(e.key, key)) {
					V newValue = remappingFunction.apply(key, e.value);
					if (newValue == null) {
						it.remove();
						return null;
					} else {
						return e.value = newValue;
					}
				}
			}
			
			return null;
		}
		
		@Override
		public V merge(
				K key,
				V value,
				BiFunction<? super V, ? super V, ? extends V>
					remappingFunction) {
			Objects.requireNonNull(remappingFunction);
			Objects.requireNonNull(value);
			
			Iterator<CBMEntry<K, V>> it = bc.iterator();
			while (it.hasNext()) {
				CBMEntry<K, V> e = it.next();
				if (m.compareKeys(e.key, key)) {
					V newValue = remappingFunction.apply(e.value, value);
					if (newValue == null) {
						it.remove();
						return null;
					} else {
						return e.value = newValue;
					}
				}
			}
			
			bc.add(new CBMEntry<>(key, value));
			return value;
		}
		
		@Override
		public void forEach(BiConsumer<? super K, ? super V> action) {
			Objects.requireNonNull(action);
			bc.forEach(e -> action.accept(e.key, e.value));
		}
		
		@Override
		public V putIfAbsent(K key, V value) {
			for (CBMEntry<K, V> e: bc) {
				if (m.compareKeys(e.key, key)) {
					if (e.value == null) {
						e.value = value;
						return null;
					} else {
						return e.value;
					}
				}
			}
			
			bc.add(new CBMEntry<>(key, value));
			return null;
		}
		
		@Override
		public boolean remove(Object key, Object value) {
			Iterator<CBMEntry<K, V>> it = bc.iterator();
			while (it.hasNext()) {
				CBMEntry<K, V> e = it.next();
				if (m.compareKeyToObj(e.key, key)
					&& m.compareValueToObj(e.value, value)) {
					it.remove();
					return true;
				}
			}
			return false;
		}
		
		@Override
		public V replace(K key, V value) {
			for (CBMEntry<K, V> e: bc) {
				if (m.compareKeys(e.key, key)) {
					V oldVal = e.value;
					e.value = value;
					return oldVal;
				}
			}
			return null;
		}
		
		@Override
		public boolean replace(K key, V oldValue, V newValue) {
			for (CBMEntry<K, V> e: bc) {
				if (m.compareKeys(e.key, key)
					&& m.compareValues(e.value, oldValue)) {
					e.value = newValue;
					return true;
				}
			}
			return false;
		}
		
		@Override
		public void replaceAll(
				BiFunction<? super K, ? super V, ? extends V>
					function) {
			Objects.requireNonNull(function);
			bc.forEach(e -> e.value = function.apply(e.key, e.value));
		}
	}
}