package com.example.videohits.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

	private ListUtils() {
		    throw new IllegalStateException("Utility class");
	}

	public static <T> List<List<T>> chopped(List<T> list, int sizeSubList) {
		List<List<T>> parts = new ArrayList<>();
		int listSize = list.size();
		for (int i = 0; i < listSize; i += sizeSubList) {
			parts.add(new ArrayList<>(list.subList(i, Math.min(listSize, i + sizeSubList))));
		}
		return parts;
	}
}
