package sahlaysta.common.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Utility class that provides static methods for removing duplicate
 * elements from a collection.
 * 
 * <p>All methods in this class take a collection, and throw a
 * {@link NullPointerException} if that collection is {@code null},
 * and may also throw an {@link UnsupportedOperationException} if
 * that collection does not support element removal (modification
 * of elements).
 * 
 * @author porog
 * */
public final class CollectionDuplicateRemover {

	private CollectionDuplicateRemover() {}
	
	/** Represents a predicate (boolean-valued function) of two arguments
	 * to be compared and tested if they are duplicates or not.
	 * 
	 * @param <T> the element type of the two elements being compared
	 * @see #areDuplicates(Object, Object) */
	public static interface DuplicatePredicate<T> {
		
		/** Compares the two specified elements and
		 * returns {@code true} if they are duplicates.
		 * It is possible for both arguments to be the
		 * same object, and it is also possible that one
		 * or both are {@code null}.
		 * @return {@code true} if the two specified
		 * elements are duplicates
		 * @param t1 the first element to be compared
		 * @param t2 the second element to be compared */
		public boolean areDuplicates(T t1, T t2);
	}
	
	/** Removes all the duplicate elements in the specified collection.
	 * A duplicate is defined as a {@code true} return from two elements
	 * being compared on the {@link Object#equals(Object) equals} method.
	 * @param <T> the element type of the collection
	 * @param c the collection to remove duplicate elements from
	 * @return {@code true} if at least one element was removed,
	 * {@code false} otherwise
	 * @throws NullPointerException the collection is {@code null}
	 * @throws UnsupportedOperationException the collection does not
	 * support element removal
	 * @see #removeDuplicates(Collection, DuplicatePredicate) */
	public static <T> boolean removeDuplicates(Collection<T> c) {
		return removeDuplicates(c, (t1, t2) -> Objects.equals(t1, t2));
	}
	
	/** Removes all the duplicate elements in the specified collection
	 * with the specified {@link DuplicatePredicate}.
	 * 
	 * <p>It is possible to remove duplicates by
	 * field:<pre><codeblock><!--
	 * -->CollectionDuplicateRemover.<!--
	 * -->removeDuplicates(listOfEmployees, <!--
	 * -->(t1, t2) -> Objects.equals(<!--
	 * -->t1.getEmployeeName(), <!--
	 * -->t2.getEmployeeName()));<!--
	 * --></codeblock></pre>
	 * 
	 * @param <T> the element type of the collection
	 * @param c the collection to remove duplicate elements from
	 * @param dp the {@link DuplicatePredicate} to test duplicates
	 * between elements
	 * @return {@code true} if at least one element was removed,
	 * {@code false} otherwise
	 * @throws NullPointerException the collection or
	 * {@link DuplicatePredicate} is {@code null}
	 * @throws UnsupportedOperationException the collection does not
	 * support element removal */
	public static <T> boolean removeDuplicates(
			Collection<T> c, DuplicatePredicate<T> dp) {
		Objects.requireNonNull(dp);
		if (c.size() <= 1)
			return false;
		
		ArrayList<T> elmnts = new ArrayList<>(c.size());
		return c.removeIf(
			t1 -> {
				for (T t2: elmnts)
					if (dp.areDuplicates(t1, t2))
						return true;
				elmnts.add(t1);
				return false;
			}
		);
	}
	
	/** Returns {@code true} if the specified collection contains
	 * duplicate elements. A duplicate is defined as a {@code true}
	 * return from two elements being compared on the
	 * {@link Object#equals(Object) equals} method.
	 * @param <T> the element type of the collection
	 * @param c the collection to test for duplicate elements
	 * @return {@code true} if the specified collection contains
	 * duplicate elements
	 * @throws NullPointerException the collection is {@code null}
	 * @see #containsDuplicates(Collection, DuplicatePredicate) */
	public static <T> boolean containsDuplicates(Collection<T> c) {
		return containsDuplicates(c, (t1, t2) -> Objects.equals(t1, t2));
	}
	
	/** Returns {@code true} if the specified collection contains
	 * duplicate elements with the specified
	 * {@link DuplicatePredicate}.
	 * @param <T> the element type of the collection
	 * @param c the collection to test for duplicate elements
	 * @param dp the {@link DuplicatePredicate} to test duplicates
	 * between elements
	 * @return {@code true} if the specified collection contains
	 * duplicate elements
	 * @throws NullPointerException the collection or
	 * {@link DuplicatePredicate} is {@code null} */
	public static <T> boolean containsDuplicates(
			Collection<T> c, DuplicatePredicate<T> dp) {
		Objects.requireNonNull(dp);
		if (c.size() <= 1)
			return false;
		
		ArrayList<T> elmnts = new ArrayList<>(c.size());
		for (T t1: c) {
			for (T t2: elmnts)
				if (dp.areDuplicates(t1, t2))
					return true;
			elmnts.add(t1);
		}
		return false;
	}
}