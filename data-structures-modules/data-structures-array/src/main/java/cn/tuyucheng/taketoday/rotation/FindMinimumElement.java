package cn.tuyucheng.taketoday.rotation;

public class FindMinimumElement {

	public static int findMinUsingBinarySearch(int[] arr, int low, int high) {
		if (low > high)
			return arr[0];
		if (high == low)
			return arr[low];
		int mid = low + (high - low) / 2;
		if (low < mid && arr[mid] < arr[mid - 1])
			return arr[mid];
		if (mid < high && arr[mid] > arr[mid + 1])
			return arr[mid + 1];
		if (arr[mid] < arr[high])
			return findMinUsingBinarySearch(arr, low, mid - 1);
		return findMinUsingBinarySearch(arr, mid + 1, high);
	}

	public static int findMinWithDuplicateElement(int[] arr, int low, int high) {
		while (low < high) {
			int mid = low + (high - low) / 2;
			if (arr[mid] == arr[high])
				high--;
			else if (arr[mid] > arr[high])
				low = mid + 1;
			else
				high = mid;
		}
		return arr[high];
	}
}