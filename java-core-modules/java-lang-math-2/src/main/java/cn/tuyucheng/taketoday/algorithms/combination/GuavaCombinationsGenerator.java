package cn.tuyucheng.taketoday.algorithms.combination;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Set;

public class GuavaCombinationsGenerator {

	public static void main(String[] args) {

		Set<Set<Integer>> combinations = Sets.combinations(ImmutableSet.of(0, 1, 2, 3, 4, 5), 3);
		System.out.println(combinations.size());
		System.out.println(Arrays.toString(combinations.toArray()));
	}
}
