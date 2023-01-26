package cn.tuyucheng.taketoday.rotation;

public class ArrayRightRotation {

	public static void rightRotate(int[] arr, int n, int d) {
		d = d % n;
		reverse(arr, 0, n - 1);
		reverse(arr, 0, d - 1);
		reverse(arr, d, n - 1);
	}

	public static void reverse(int[] arr, int start, int end) {
		int temp;
		while (start < end) {
			temp = arr[start];
			arr[start] = arr[end];
			arr[end] = temp;
			start++;
			end--;
		}
	}
}