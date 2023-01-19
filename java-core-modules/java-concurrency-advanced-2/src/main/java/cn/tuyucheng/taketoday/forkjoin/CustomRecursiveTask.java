package cn.tuyucheng.taketoday.forkjoin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class CustomRecursiveTask extends RecursiveTask<Integer> {
	private final int[] array;
	private static final int THRESHOLD = 20;

	public CustomRecursiveTask(int[] array) {
		this.array = array;
	}

	@Override
	protected Integer compute() {
		if (array.length > THRESHOLD)
			return ForkJoinTask.invokeAll(createSubTasks()).stream().mapToInt(ForkJoinTask::join).sum();
		else
			return processing(array);
	}

	private Collection<CustomRecursiveTask> createSubTasks() {
		List<CustomRecursiveTask> dividedTasks = new ArrayList<>();
		dividedTasks.add(new CustomRecursiveTask(Arrays.copyOfRange(array, 0, array.length / 2)));
		dividedTasks.add(new CustomRecursiveTask(Arrays.copyOfRange(array, array.length / 2, array.length)));
		return dividedTasks;
	}

	private Integer processing(int[] array) {
		return Arrays.stream(array).filter(a -> a > 10 && a < 27).map(a -> a * 10).sum();
	}
}