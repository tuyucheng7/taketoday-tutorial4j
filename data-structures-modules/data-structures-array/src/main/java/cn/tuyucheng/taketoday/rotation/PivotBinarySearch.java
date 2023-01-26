package cn.tuyucheng.taketoday.rotation;

public class PivotBinarySearch {

	public static int pivotBinarySearch(int[] arr, int n, int key) {
		int pivot = findPivot(arr, 0, n - 1);
		if (pivot == -1)
			return binarySearch(arr, 0, n - 1, key);
		if (arr[pivot] == key)
			return pivot;
		if (key >= arr[0])
			return binarySearch(arr, 0, pivot - 1, key);
		return binarySearch(arr, pivot + 1, n - 1, key);
	}

	private static int binarySearch(int[] arr, int low, int high, int key) {
		if (low > high)
			return -1;
		int mid = (low + high) / 2;
		if (key == arr[mid])
			return mid;
		if (key < arr[mid])
			return binarySearch(arr, low, mid - 1, key);
		return binarySearch(arr, mid + 1, high, key);
	}

	private static int findPivot(int[] arr, int low, int high) {
		if (high < low)
			return -1;
		if (high == low)
			return low;
		int mid = (low + high) / 2;
		if (mid < high && arr[mid] > arr[mid + 1])
			return mid;
		if (mid > low && arr[mid] < arr[mid - 1])
			return mid - 1;
		if (arr[low] >= arr[mid])
			return findPivot(arr, low, mid - 1);
		return findPivot(arr, mid + 1, high);
	}

	public static int optimizationPivotBinarySearch(int[] arr, int l, int h, int key) {
		if (l > h)
			return -1;
		int mid = (l + h) / 2;
		if (arr[mid] == key)
			return mid;
		if (arr[l] <= arr[mid]) {
			if (arr[l] <= key && arr[mid] >= key)
				return optimizationPivotBinarySearch(arr, l, mid - 1, key);
			return optimizationPivotBinarySearch(arr, mid + 1, h, key);
		}
		if (key >= arr[mid] && key <= arr[h])
			return optimizationPivotBinarySearch(arr, mid + 1, h, key);
		return optimizationPivotBinarySearch(arr, l, mid - 1, key);
	}

	public static boolean pairInSortedRotated(int[] arr, int n, int x) {
		int i;
		for (i = 0; i < n - 1; i++)
			if (arr[i] > arr[i + 1])
				break;
		int r = i;
		int l = (i + 1) % n;
		while (l != r) {
			if (arr[l] + arr[r] == x)
				return true;
			if (arr[l] + arr[r] < x)
				l = (l + 1) % n;
			else
				r = (n + r - 1) % n;
		}
		return false;
	}

	public static int pairsCountInSortedRotated(int[] arr, int n, int x) {
		int count = 0;
		int i;
		for (i = 0; i < n - 1; i++)
			if (arr[i] > arr[i + 1])
				break;
		int l = (i + 1) % n;
		int r = i;
		while (r != l) {
			int sum = arr[r] + arr[l];
			if (sum == x) {
				count++;
				if (l == (n + r - 1) % n)
					return count;
				l = (l + 1) % n;
				r = (n + r - 1) % n;
			} else if (sum < x)
				l = (l + 1) % n;
			else
				r = (n + r - 1) % n;
		}
		return count;
	}
}