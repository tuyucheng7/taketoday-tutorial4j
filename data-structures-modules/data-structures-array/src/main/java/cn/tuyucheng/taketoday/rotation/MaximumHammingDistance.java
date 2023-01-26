package cn.tuyucheng.taketoday.rotation;

public class MaximumHammingDistance {

	public static int maxHamming(int[] arr, int n) {
		int[] temp = new int[2 * n];
		for (int i = 0; i < n; i++)
			temp[i] = temp[n + i] = arr[i];
		int maxHamming = 0;
		for (int i = 1; i < n; i++) {
			int currentHamming = 0;
			for (int j = i, k = 0; j < (i + n); j++, k++)
				if (temp[j] != arr[k])
					currentHamming++;
			if (currentHamming == n)
				return n;
			maxHamming = Math.max(maxHamming, currentHamming);
		}
		return maxHamming;
	}

	public static int maxHammingWithConstantSpace(int[] arr, int n) {
		int maxHamming = 0;
		for (int j = 1; j < n; j++) {
			maxHamming = 0;
			for (int i = 0; i < n; i++) {
				if (arr[i] != arr[(i + j) % n])
					maxHamming++;
			}
			if (maxHamming == n)
				return maxHamming;
		}
		return maxHamming;
	}
}