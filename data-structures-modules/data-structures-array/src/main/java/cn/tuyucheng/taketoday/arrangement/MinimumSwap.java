package cn.tuyucheng.taketoday.arrangement;

public class MinimumSwap {

	public static int minSwap(int[] arr, int k) {
		int n = arr.length;
		int count = 0;
		for (int i = 0; i < n; i++)
			if (arr[i] <= k)
				++count;
		int bad = 0;
		for (int i = 0; i < count; i++)
			if (arr[i] > k)
				++bad;
		int res = bad;
		for (int i = 0, j = count; j < n; ++i, ++j) {
			if (arr[i] > k)
				--bad;
			if (arr[j] > k)
				++bad;
			res = Math.min(res, bad);
		}
		return res;
	}
}