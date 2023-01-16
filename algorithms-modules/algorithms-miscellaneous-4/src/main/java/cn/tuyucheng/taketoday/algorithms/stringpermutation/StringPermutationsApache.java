package cn.tuyucheng.taketoday.algorithms.stringpermutation;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.iterators.PermutationIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StringPermutationsApache {

	public List<String> eagerPermutationWithRepetitions(final String string) {
		final List<Character> characters = Helper.toCharacterList(string);
		return CollectionUtils.permutations(characters)
			.stream()
			.map(Helper::toString)
			.collect(Collectors.toList());
	}

	public List<String> lazyPermutationWithoutRepetitions(final String string) {
		final List<Character> characters = Helper.toCharacterList(string);
		final PermutationIterator<Character> permutationIterator = new PermutationIterator<>(characters);
		final List<String> result = new ArrayList<>();
		while (permutationIterator.hasNext()) {
			result.add(Helper.toString(permutationIterator.next()));
		}
		return result;
	}

}
