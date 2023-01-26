package cn.tuyucheng.taketoday.rotation;

public class FindRotateCount {

	public static int countRotationsUsingLinearSearch(int[] arr, int n) {
		int min = arr[0];
		int minIndex = 0;
		for (int i = 1; i < n; i++) {
			if (arr[i] < min) {
				min = arr[i];
				minIndex = i;
			}
		}
		return minIndex;
	}

	public static int countRotationUsingBinarySearch(int[] arr, int low, int high) {
		if (low > high)
			return 0;
		if (high == low)
			return low;
		int mid = low + (high - low) / 2;
		if (mid > low && arr[mid] < arr[mid - 1])
			return mid;
		if (mid < high && arr[mid] > arr[mid + 1])
			return mid + 1;
		if (arr[high] > arr[mid])
			return countRotationUsingBinarySearch(arr, low, mid - 1);
		return countRotationUsingBinarySearch(arr, mid + 1, high);
	}

	public static int countRotationUsingBinarySearchIterative(int[] arr, int n) {
		int low = 0;
		int high = n - 1;
		while (low <= high) {
			int mid = low + (high - low) / 2;
			int previous = (mid - 1 + n) % n;
			int next = (mid + 1) % n;
			if (arr[mid] <= arr[previous] && arr[mid] <= arr[next])
				return mid;
			else if (arr[mid] <= arr[high])
				high = mid - 1;
			else if (arr[mid] >= arr[low])
				low = mid + 1;
		}
		return 0;
	}
}