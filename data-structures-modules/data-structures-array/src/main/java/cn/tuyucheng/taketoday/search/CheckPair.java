package cn.tuyucheng.taketoday.search;

import java.util.Arrays;
import java.util.HashSet;

public class CheckPair {

	public static boolean checkPair(int[] A, int size, int x) {
		for (int i = 0; i < size - 1; i++) {
			for (int j = i + 1; j < size; j++) {
				if (A[i] + A[j] == x)
					return true;
			}
		}
		return false;
	}

	public static boolean checkPairUsingTwoPointer(int[] A, int arr_size, int sum) {
		int l = 0;
		int r = arr_size - 1;
		Arrays.sort(A);
		while (l < r) {
			if (A[l] + A[r] == sum)
				return true;
			if (A[l] + A[r] < sum)
				l++;
			else
				r--;
		}
		return false;
	}

	public static boolean checkPairUsingHashing(int[] A, int sum) {
		HashSet<Integer> s = new HashSet();
		for (int i = 0; i < A.length; i++) {
			if (s.contains(sum - A[i]))
				return true;
			s.add(A[i]);
		}
		return false;
	}

	public static boolean checkPairUsingRemainders(int[] A, int n, int sum) {
		int[] rem = new int[n];
		for (int i = 0; i < n; i++)
			rem[i] = 0;
		for (int i = 0; i < n; i++) {
			if (A[i] < sum) {

			}
		}
		return false;
	}
}