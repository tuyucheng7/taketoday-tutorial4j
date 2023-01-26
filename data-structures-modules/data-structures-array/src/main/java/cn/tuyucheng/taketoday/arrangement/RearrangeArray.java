package cn.tuyucheng.taketoday.arrangement;

import java.util.HashSet;
import java.util.Set;

public class RearrangeArray {

	public static void fixArray(int[] arr, int n) {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i == arr[j]) {
					int temp = arr[i];
					arr[i] = arr[j];
					arr[j] = temp;
					break;
				}
			}
		}
		for (int i = 0; i < n; i++)
			if (i != arr[i])
				arr[i] = -1;
	}

	public static void fixArrayOptimization(int[] arr, int n) {
		for (int i = 0; i < n; i++) {
			if (arr[i] != -1 && arr[i] != i) {
				int x = arr[i];
				while (arr[x] != x && arr[x] != -1) {
					int y = arr[x];
					arr[x] = x;
					x = y;
				}
				arr[x] = x;
				if (arr[i] != i)
					arr[i] = -1;
			}
		}
	}

	public static void fixArrayUsingSet(int[] arr, int n) {
		Set<Integer> set = new HashSet<>();
		for (int i = 0; i < n; i++)
			set.add(arr[i]);
		for (int i = 0; i < n; i++) {
			if (set.contains(i))
				arr[i] = i;
			else
				arr[i] = -1;
		}
	}

	public static void fixArrayUsingSwapElement(int[] arr, int n) {
		for (int i = 0; i < n; ) {
			if (i != arr[i] && arr[i] > 0) {
				int temp = arr[arr[i]];
				arr[arr[i]] = arr[i];
				arr[i] = temp;
			} else
				i++;
		}
	}
}