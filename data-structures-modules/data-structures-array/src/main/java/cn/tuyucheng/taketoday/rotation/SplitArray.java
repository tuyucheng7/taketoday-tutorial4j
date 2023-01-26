package cn.tuyucheng.taketoday.rotation;

public class SplitArray {

	public static void splitArray(int[] arr, int n, int k) {
		for (int i = 0; i < k; i++) {
			int temp = arr[0];
			for (int j = 0; j < n - 1; j++)
				arr[j] = arr[j + 1];
			arr[n - 1] = temp;
		}
	}

	public static void splitArrayUsingTempArray(int[] arr, int n, int k) {
		int[] temp = new int[2 * n];
		for (int i = 0; i < n; i++)
			temp[i] = temp[i + n] = arr[i];
		for (int i = 0; i < n; i++)
			arr[i] = temp[(k + i) % n];
	}
}