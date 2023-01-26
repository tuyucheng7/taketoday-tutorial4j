package cn.tuyucheng.taketoday.arrangement;

import java.util.Arrays;

public class EvenOddSplit {

	public static void rearrangeArr(int[] arr, int n) {
		int evenPosition = n / 2;
		int oddPotision = n - evenPosition;
		int[] temp = new int[n];
		for (int i = 0; i < n; i++)
			temp[i] = arr[i];
		Arrays.sort(temp);
		int j = oddPotision - 1;
		for (int i = 0; i < n; i += 2) {
			arr[i] = temp[j];
			j--;
		}
		j = oddPotision;
		for (int i = 1; i < n; i += 2) {
			arr[i] = temp[j];
			j++;
		}
	}

	public static void rearrangeArrOptimization(int[] arr, int n) {
		int p = 0;
		int q = n - 1;
		int[] temp = new int[n];
		System.arraycopy(arr, 0, temp, 0, n);
		Arrays.sort(temp);
		for (int i = n - 1; i >= 0; i--) {
			if (i % 2 != 0) {
				arr[i] = temp[q];
				q--;
			} else {
				arr[i] = temp[p];
				p++;
			}
		}
	}
}