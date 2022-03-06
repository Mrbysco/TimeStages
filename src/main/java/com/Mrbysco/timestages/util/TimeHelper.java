package com.mrbysco.timestages.util;

public class TimeHelper {

	public static int getProperTime(int time, String amount) {
		int actualTime = 0;
		if (amount.isEmpty()) {
			return 0;
		} else {
			if (amount.contains("seconds")) {
				actualTime = time;
				return actualTime;
			} else if (amount.contains("min")) {
				actualTime = time * 60;
				return actualTime;
			} else if (amount.contains("hour")) {
				actualTime = time * 3600;
				return actualTime;
			} else if (amount.contains("day")) {
				return time;
			}
		}

		return actualTime;
	}
}
