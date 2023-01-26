package cn.tuyucheng.taketoday.arrangement;

public class ReverseArray {

	public static void reverseArray(int[] arr, int start, int end) {
		while (start < end) {
			int temp = arr[start];
			arr[start] = arr[end];
			arr[end] = temp;
			start++;
			end--;
		}
	}

	public static void reverseArrayRecursive(int[] arr, int start, int end) {
		if (start >= end)
			return;
		int temp = arr[start];
		arr[start] = arr[end];
		arr[end] = temp;
		reverseArrayRecursive(arr, start + 1, end - 1);
	}
}