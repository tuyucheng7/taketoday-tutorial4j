package cn.tuyucheng.taketoday.arrangement;

public class MoveZero {

	public static void pushZeroToEnd(int[] arr, int n) {
		int count = 0;
		for (int i = 0; i < n; i++) {
			if (arr[i] != 0)
				arr[count++] = arr[i];
		}
		for (int i = count; i < n; i++)
			arr[i] = 0;
	}

	public static void pushZeroToEndUsingPartition(int[] arr) {
		int n = arr.length;
		int j = 0;
		for (int i = 0; i < n; i++) {
			if (arr[i] != 0) {
				int temp = arr[i];
				arr[i] = arr[j];
				arr[j] = temp;
				j++;
			}
		}
	}
}