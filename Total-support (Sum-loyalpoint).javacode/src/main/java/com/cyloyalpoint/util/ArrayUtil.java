package com.cyloyalpoint.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ArrayUtil {

	private ArrayUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static void swapArrays(float[] array1, float[] array2, int size) {
		float[] buffer = new float[size];
		for (int i = 0; i < size; i++) {
			buffer[i] = array1[i];
			array1[i] = array2[i];
			array2[i] = buffer[i];
		}
	}

	public static int[] convertIntegers(List<Integer> integers) {
		int[] ret = new int[integers.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = integers.get(i).intValue();
		}
		return ret;
	}

	public static float maxValue(float[] array) {
		float max = Float.MIN_VALUE;
		for (int i = 0; i < array.length; i++) {
			if (array[i] > max) {
				max = array[i];
			}
		}
		return max;
	}

	public static int findMinIndex(int[] arrays) {
		if (arrays == null || arrays.length == 0) {
			return -1;
		}

		int minVal = arrays[0];
		int minIdx = 0;

		for (int idx = 1; idx < arrays.length; idx++) {
			if (arrays[idx] < minVal) {
				minVal = arrays[idx];
				minIdx = idx;
			}
		}
		return minIdx;
	}

	public static int findMaxIndex(int[] arrays) {
		if (arrays == null || arrays.length == 0) {
			return -1;
		}

		int maxVal = arrays[0];
		int maxIdx = 0;

		for (int idx = 1; idx < arrays.length; idx++) {
			if (arrays[idx] > maxVal) {
				maxVal = arrays[idx];
				maxIdx = idx;
			}
		}
		return maxIdx;
	}

	public static int[][] splitArrayIntoChunks(int[] arrayToSplit, int chunkSize) {
		if (chunkSize <= 0) {
			return new int[][] {};
		}
		int rest = arrayToSplit.length % chunkSize;

		int chunks = arrayToSplit.length / chunkSize + (rest > 0 ? 1 : 0);

		int[][] arrays = new int[chunks][];

		for (int i = 0; i < (rest > 0 ? chunks - 1 : chunks); i++) {
			arrays[i] = Arrays.copyOfRange(arrayToSplit, i * chunkSize, i * chunkSize + chunkSize);
		}
		if (rest > 0) {
			arrays[chunks - 1] = Arrays.copyOfRange(arrayToSplit, (chunks - 1) * chunkSize,
					(chunks - 1) * chunkSize + rest);
		}
		return arrays;
	}

	public static int[][] splitArrayBaseOnRatio(int[] arrayToSplit, int[] ratio) {
		if (ratio.length <= 0 || arrayToSplit.length < ratio.length) {
			return new int[][] {};
		}

		int numberOfChunk = ratio.length;

		List<ArrayList<Integer>> list = new ArrayList<>();
		for (int i = 0; i < numberOfChunk; i++) {
			list.add(new ArrayList<Integer>());
		}
		
		int[] threshold = new int[numberOfChunk];
		Arrays.fill(threshold, 0);

		ArrayIndexComparator comparator = new ArrayIndexComparator(ratio, false);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);

		int preferIndex = findMaxIndex(ratio);

		int elementIndex = 0;
		while (elementIndex < arrayToSplit.length) {

			boolean flag = false;

			for (int index : indexes) {
				if (list.get(index).size() <= threshold[index]) {
					list.get(index).add(arrayToSplit[elementIndex]);
					flag = true;
					break;
				}
			}

			if (!flag) {
				list.get(preferIndex).add(arrayToSplit[elementIndex]);

				for (int index : indexes) {
					threshold[index] = list.get(index).size() + ratio[index];
				}
			}

			elementIndex++;
		}

		int[][] arrays = new int[numberOfChunk][];

		for (int i = 0; i < numberOfChunk; i++) {
			arrays[i] = list.get(i).stream().mapToInt(Integer::intValue).toArray();
		}

		return arrays;
	}

	public static class ArrayIndexComparator implements Comparator<Integer> {
		private final int[] array;
		private boolean asc;

		public ArrayIndexComparator(int[] array, boolean asc) {
			this.array = array;
			this.asc = asc;
		}

		public Integer[] createIndexArray() {
			Integer[] indexes = new Integer[array.length];
			for (int i = 0; i < array.length; i++) {
				indexes[i] = i;
			}
			return indexes;
		}

		@Override
		public int compare(Integer index1, Integer index2) {
			if (asc) {
				return Integer.compare(array[index1], array[index2]);
			}
			return Integer.compare(array[index2], array[index1]);
		}
	}
}
