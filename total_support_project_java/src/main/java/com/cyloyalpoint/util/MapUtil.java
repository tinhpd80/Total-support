package com.cyloyalpoint.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MapUtil {

	private MapUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static Map<Integer, Float> sortIntegerFloatMapByValue(Map<Integer, Float> unsortMap, final boolean order) {
		List<Entry<Integer, Float>> list = new LinkedList<>(unsortMap.entrySet());

		Collections.sort(list, (Entry<Integer, Float> o1, Entry<Integer, Float> o2) -> {
			if (order) {
				return o1.getValue().compareTo(o2.getValue());
			} else {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		Map<Integer, Float> sortedMap = new LinkedHashMap<>();
		for (Entry<Integer, Float> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	public static Map<String, Float> sortStringFloatMapByValue(Map<String, Float> unsortMap, final boolean order) {
		List<Entry<String, Float>> list = new LinkedList<>(unsortMap.entrySet());

		Collections.sort(list, (Entry<String, Float> o1, Entry<String, Float> o2) -> {
			if (order) {
				return o1.getValue().compareTo(o2.getValue());
			} else {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		Map<String, Float> sortedMap = new LinkedHashMap<>();
		for (Entry<String, Float> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}
}
