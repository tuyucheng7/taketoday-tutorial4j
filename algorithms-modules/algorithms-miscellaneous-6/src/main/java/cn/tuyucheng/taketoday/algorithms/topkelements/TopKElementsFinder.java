package cn.tuyucheng.taketoday.algorithms.topkelements;

import java.util.List;

public interface TopKElementsFinder<T extends Comparable<T>> {
	List<T> findTopK(List<T> input, int k);
}
