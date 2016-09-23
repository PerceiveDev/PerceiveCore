
package com.perceivedev.perceivecore.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

	public static String concatList(List<String> list, String filler) {

		if (list.size() == 0) {

		return "";

		}

		String output = "";
		for (int i = 0; i < list.size() - 1; i++) {

			output += list.get(i).toString();
			output += filler;

		}

		output += list.get(list.size() - 1).toString();

		return output;

	}

	public static List<String> colorList(List<String> list) {
		if (list == null || isEmpty(list)) { return list; }
		List<String> coloredList = new ArrayList<String>();
		for (String string : list) {
			coloredList.add(TextUtils.colorize(string));
		}
		return coloredList;
	}

	public static List<String> reverseColorList(List<String> list) {
		if (list == null || isEmpty(list)) { return list; }
		List<String> coloredList = new ArrayList<String>();
		for (String string : list) {
			coloredList.add(TextUtils.uncolorize(string));
		}
		return coloredList;
	}

	public static boolean isEmpty(List<?> list) {

		return list.size() < 1;

	}

	public static <T> List<T> empty() {
		return new ArrayList<T>();
	}

}
