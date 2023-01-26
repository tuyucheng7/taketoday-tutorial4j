package cn.tuyucheng.taketoday.arrangement;

public class PositiveNegativeSplit {

	public static void rearrange(int[] arr, int n) {
		int i = -1, temp;
		for (int j = 0; j < n; j++) {
			if (arr[j] < 0) {
				i++;
				temp = arr[i];
				arr[i] = arr[j];
				arr[j] = temp;
			}
		}

		int pos = i + 1, neg = 0;

		while (pos < n && neg < pos && arr[neg] < 0) {
			temp = arr[neg];
			arr[neg] = arr[pos];
			arr[pos] = temp;
			pos++;
			neg += 2;
		}
	}

	private static void rightrotate(int[] arr, int n, int outofplace, int cur) {
		int tmp = arr[cur];
		for (int i = cur; i > outofplace; i--)
			arr[i] = arr[i - 1];
		arr[outofplace] = tmp;
	}

	public static void rearrangeOptimization(int[] arr, int n) {
		int outofplace = -1;
		for (int index = 0; index < n; index++) {
			if (outofplace >= 0) {
				// find the item which must be moved into
				// the out-of-place entry if out-of-place
				// entry is positive and current entry is
				// negative OR if out-of-place entry is
				// negative and current entry is negative
				// then right rotate
				//
				// [...-3, -4, -5, 6...] -->   [...6, -3,
				// -4, -5...]
				//      ^                          ^
				//      |                          |
				//     outofplace      -->      outofplace
				//
				if (((arr[index] >= 0)
					&& (arr[outofplace] < 0))
					|| ((arr[index] < 0)
					&& (arr[outofplace] >= 0))) {
					rightrotate(arr, n, outofplace, index);

					// the new out-of-place entry is now 2
					// steps ahead
					if (index - outofplace >= 2)
						outofplace = outofplace + 2;
					else
						outofplace = -1;
				}
			}

			// if no entry has been flagged out-of-place
			if (outofplace == -1) {
				// check if current entry is out-of-place
				if (((arr[index] >= 0) && ((index & 0x01) == 0)) || ((arr[index] < 0) && (index & 0x01) == 1))
					outofplace = index;
			}
		}
	}
}