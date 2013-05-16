package com.rayer.util.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class CollectionUtil {

	/**
	 * Sort any collection and return a new list with it. Extremely useful while you need sorted map key/value pairs
	 * @param c
	 * @param comparator
	 * @return
	 */
	public static <T> List<T> asSortedList(Collection<T> c, Comparator<T> comparator) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list, comparator);
	  return list;
	}

}
