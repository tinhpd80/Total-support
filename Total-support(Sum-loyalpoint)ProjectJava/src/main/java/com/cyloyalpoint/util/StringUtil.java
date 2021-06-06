package com.cyloyalpoint.util;

import java.util.concurrent.TimeUnit;

public class StringUtil {

	private StringUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static String getDurationBreakdown(long millis) {
		if (millis < 0) {
			return "";
		}

		long days = TimeUnit.MILLISECONDS.toDays(millis);
		long hours = TimeUnit.MILLISECONDS.toHours(millis) % 24;
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
		// long milliseconds = millis % 1000;

		String result;

		if (days > 0) {
			result = String.format("%02d Days %02d Hours %02d Minutes %02d Seconds", days, hours, minutes, seconds);
		} else if (hours > 0) {
			result = String.format("%02d Hours %02d Minutes %02d Seconds", hours, minutes, seconds);
		} else if (minutes > 0) {
			result = String.format("%02d Minutes %02d Seconds", minutes, seconds);
		} else {
			result = String.format("%02d Seconds", seconds);
		}

		return result;
	}
}
