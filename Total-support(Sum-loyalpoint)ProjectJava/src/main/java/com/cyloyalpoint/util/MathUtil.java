package com.cyloyalpoint.util;

import java.util.Random;

public class MathUtil {

	private MathUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static int randomInRange(int min, int max) {
		Random r = new Random();
		return r.nextInt((max + 1) - min) + min;
	}

	public static boolean nearlyEqual(float a, float b, float eps) {
		return Math.abs(a - b) < eps;
	}

	public static float zero(float num, float eps) {
		if (Math.abs(num) < eps) {
			return 0.0f;
		}
		return num;
	}

	public static int gcd(int a, int b) {
		if (a == 0) {
			return b;
		}
		return gcd(b % a, a);
	}

	public static int findGCD(int[] array, int n) {
		int result = array[0];
		
		for (int i = 1; i < n; i++) {
			result = gcd(array[i], result);
		}

		return result;
	}
}
