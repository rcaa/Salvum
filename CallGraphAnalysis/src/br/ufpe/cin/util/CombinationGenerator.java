package br.ufpe.cin.util;

import java.util.ArrayList;
import java.util.List;

public class CombinationGenerator {

	private static void combinationUtil(String arr[], String data[], int start,
			int end, int index, int r, List<String> entryMethods) {
		if (index == r) {
			String combination = "";
			for (int j = 0; j < r; j++) {
				combination = combination + data[j] + ":";
			}
			entryMethods
					.add(combination.substring(0, combination.length() - 1));
			return;
		}

		for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
			data[index] = arr[i];
			combinationUtil(arr, data, i + 1, end, index + 1, r, entryMethods);
		}
	}

	private static void combine(String arr[], int n, int r,
			List<String> entryMethods) {
		String data[] = new String[r];
		combinationUtil(arr, data, 0, n - 1, 0, r, entryMethods);
	}

	public static List<String> run(List<String> entryPoints) {
		String[] epArray = new String[entryPoints.size()];
		epArray = entryPoints.toArray(epArray);
		int n = epArray.length;
		List<String> entryMethods = new ArrayList<>();
		for (int i = 1; i <= n; i++) {
			combine(epArray, n, i, entryMethods);
		}
		return entryMethods;
	}
}
