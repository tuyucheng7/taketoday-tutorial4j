package cn.tuyucheng.taketoday.rotation;

public class MaximumValueWithRotation {

	public static int maxSumNativeMethod(int[] arr, int n) {
		int res = Integer.MIN_VALUE;
		for (int i = 0; i < n; i++) {
			int current_sum = 0;
			for (int j = 0; j < n; j++) {
				int index = (j + i) % n;
				current_sum += j * arr[index];
			}
			res = Math.max(res, current_sum);
		}
		return res;
	}

	public static int maxSum(int[] arr) {
		int arrSum = 0;
		int n = arr.length;
		int currentVal = 0;
		for (int i = 0; i < n; i++) {
			arrSum += arr[i];
			currentVal += i * arr[i];
		}
		int maxVal = currentVal;
		for (int j = 1; j < n; j++) {
			currentVal = currentVal + arrSum - n * arr[n - j];
			if (currentVal > maxVal)
				maxVal = currentVal;
		}
		return maxVal;
	}

	public static int maxSumUsingFormula(int[] arr) {
		int n = arr.length;
		int cum_sum = 0;
		for (int i = 0; i < n; i++) // 计算所有数组元素之和
			cum_sum += arr[i];
		int curr_val = 0;
		for (int i = 0; i < n; i++) // 计算数组未旋转时i * arr[i]的和
			curr_val += i * arr[i];
		int res = curr_val; // 初始化返回值
		for (int i = 1; i < n; i++) { // 计算旋转i次时的和
			int next_val = curr_val - (cum_sum - arr[i - 1]) + arr[i - 1] * (n - 1); // 使用curr_val计算next_val
			curr_val = next_val; // 更新curr_val
			res = Math.max(res, next_val); // res取current_val和next_val的最大值。
		}
		return res;
	}
}