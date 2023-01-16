package cn.tuyucheng.taketoday.algorithms.quicksort;

public class DutchNationalFlagPartioning {

	public static Partition partition(int[] input, int begin, int end) {
		int lt = begin, current = begin, gt = end;
		int partitioningValue = input[begin];

		while (current <= gt) {
			int compareCurrent = SortingUtils.compare(input[current], partitioningValue);
			switch (compareCurrent) {
				case -1:
					SortingUtils.swap(input, current++, lt++);
					break;
				case 0:
					current++;
					break;
				case 1:
					SortingUtils.swap(input, current, gt--);
					break;
			}
		}
		return new Partition(lt, gt);
	}

	public static void quicksort(int[] input, int begin, int end) {
		if (end <= begin)
			return;

		Partition middlePartition = partition(input, begin, end);

		quicksort(input, begin, middlePartition.getLeft() - 1);
		quicksort(input, middlePartition.getRight() + 1, end);
	}
}
