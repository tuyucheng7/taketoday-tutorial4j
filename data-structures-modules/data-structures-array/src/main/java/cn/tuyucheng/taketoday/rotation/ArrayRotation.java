package cn.tuyucheng.taketoday.rotation;

public class ArrayRotation {

	public static int[] rotateUsingTempArray(int[] arr, int n, int d) {
		int[] temp = new int[d];
		for (int i = 0; i < d; i++)
			temp[i] = arr[i];
		for (int i = 0; i < n - d; i++)
			arr[i] = arr[i + d];
		for (int i = 0; i < d; i++)
			arr[n - d + i] = temp[i];
		return arr;
	}

	public static int[] rotateOneByOne(int[] arr, int n, int d) {
		for (int i = 0; i < d; i++) {
			int temp = arr[0];
			for (int j = 1; j < n; j++)
				arr[j - 1] = arr[j];
			arr[n - 1] = temp;
		}
		return arr;
	}

	public static int[] rotatiteUsingJuggling(int[] arr, int n, int d) {
		d = d % n;
		int i, j, k, temp;
		int gcd = gcd(d, n);
		for (i = 0; i < gcd; i++) {
			temp = arr[i];
			j = i;
			while (true) {
				k = j + d;
				if (k >= n)
					k = k - n;
				if (k == i)
					break;
				arr[j] = arr[k];
				j = k;
			}
			arr[j] = temp;
		}
		return arr;
	}

	private static int gcd(int a, int b) {
		if (b == 0)
			return a;
		else return gcd(b, a % b);
	}

	public static void rotateUsingReversal(int[] arr, int d) {
		if (d == 0)
			return;
		int n = arr.length;
		d = d % n;
		reverseArray(arr, 0, d - 1);
		reverseArray(arr, d, n - 1);
		reverseArray(arr, 0, n - 1);
	}

	private static void reverseArray(int[] arr, int start, int end) {
		int temp;
		while (start < end) {
			temp = arr[start];
			arr[start] = arr[end];
			arr[end] = temp;
			start++;
			end--;
		}
	}

	public static void rotateUsingBlockSwap(int[] arr, int d, int n) {
		rotateUsingBlockSwapRecursive(arr, 0, d, n);
	}

	public static void rotateUsingBlockSwapRecursive(int[] arr, int i, int d, int n) {
		if (d == 0 || d == n) // 如果要旋转的次数为0或等于数组大小，直接return
			return;
		if (n - d == d) {
			swap(arr, i - 1, n + i - d, d);
			return;
		}
		if (d < n - d) { // 如果A数组更短
			swap(arr, i, n - d + i, d);
			rotateUsingBlockSwapRecursive(arr, i, d, n - d);
		} else { // 如果B数组更短
			swap(arr, i, d, n - d);
			rotateUsingBlockSwapRecursive(arr, n - d + i, 2 * d - n, d);
		}
	}

	private static void swap(int[] arr, int fi, int si, int d) {
		int i, temp;
		for (i = 0; i < d; i++) {
			temp = arr[fi + i];
			arr[fi + i] = arr[si + i];
			arr[si + i] = temp;
		}
	}

	public static void rotateUsingBlockSwapIterative(int[] arr, int d, int n) {
		int i, j;
		if (d == 0 || d == n)
			return;
		if (d > n)
			d %= n;
		i = d;
		j = n - d;
		while (i != j) {
			if (i < j) {
				swap(arr, d - i, d + j - i, i);
				j -= i;
			} else {
				swap(arr, d - i, d, j);
				i -= j;
			}
		}
		swap(arr, d - i, d, i);
	}

	public static void rotateArrayByOne(int[] arr) {
		int n = arr.length;
		int x = arr[n - 1];
		for (int i = n - 1; i > 0; i--)
			arr[i] = arr[i - 1];
		arr[0] = x;
	}

	public static void rotateArrayByOneUsingTwoPointer(int[] arr) {
		int n = arr.length;
		int i = 0;
		int j = n - 1;
		while (i != j) {
			int temp = arr[i];
			arr[i] = arr[j];
			arr[j] = temp;
			i++;
		}
	}
}