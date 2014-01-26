package utils;

import java.util.Collection;

public class Utils {
	/**
	 * Constructs a new java.util.Collection that will contain the given array elements.
	 * 
	 * @param <E>
	 * @param <C>
	 * @param array
	 * @param collection
	 * @return
	 */
	public static <E,C extends Collection<E>> C arrayToCollection(E[] array,C collection)
	{
		collection.clear();
		for (E e : array)
		{
			collection.add(e);
		}
		return collection;
	}
}
