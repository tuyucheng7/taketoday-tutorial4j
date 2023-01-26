package cn.tuyucheng.taketoday.rotation;

import java.util.Arrays;
import java.util.Collections;

public class FindMultipleLeftRotation {

	public static void preProcess(int[] arr, int n, int[] temp) {
		for (int i = 0; i < n; i++)
			temp[i] = temp[i + n] = arr[i];
	}

	public static void leftRotate(int[] arr, int n, int k, int[] temp) {
		int start = k % n;
		for (int i = start; i < n + start; i++)
			System.out.print(temp[i] + " ");
		System.out.println("\n");
	}

	public static void spaceOptimizationLefeRotate(int[] arr, int n, int k) {
		for (int i = k; i < k + n; i++)
			System.out.print(arr[i % n] + " ");
		System.out.println("\n");
	}

	public static void leftRotateUsingApi(int[] arr, int n, int k) {
		Collections.rotate(Arrays.asList(arr), n - k);
		for (int i = 0; i < n; i++)
			System.out.print(arr[i] + " ");
	}
}