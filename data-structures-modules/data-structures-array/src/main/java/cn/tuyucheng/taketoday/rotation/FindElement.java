package cn.tuyucheng.taketoday.rotation;

public class FindElement {

	public static int findElement(int[] arr, int[][] ranges, int rotations, int index) {
		for (int i = rotations - 1; i >= 0; i--) {
			int left = ranges[i][0];
			int right = ranges[i][1];
			if (left <= index && right >= index)
				if (index == left)
					index = right;
				else
					index--;
		}
		return arr[index];
	}
}