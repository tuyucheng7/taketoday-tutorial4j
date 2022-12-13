package cn.tuyucheng.taketoday.stream;

import com.codepoetics.protonpack.Indexed;
import com.codepoetics.protonpack.StreamUtils;
import io.vavr.collection.Stream;
import one.util.streamex.EntryStream;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StreamIndices {

	public static List<String> getEvenIndexedStrings(String[] names) {
		return IntStream.range(0, names.length)
			.filter(i -> i % 2 == 0)
			.mapToObj(i -> names[i])
			.collect(Collectors.toList());
	}

	public List<String> getEvenIndexedStringsVersionTwo(List<String> names) {
		return EntryStream.of(names)
			.filterKeyValue((index, name) -> index % 2 == 0)
			.values()
			.toList();
	}

	public static List<Indexed<String>> getEvenIndexedStrings(List<String> names) {
		return StreamUtils.zipWithIndex(names.stream())
			.filter(i -> i.getIndex() % 2 == 0)
			.collect(Collectors.toList());
	}

	public static List<Indexed<String>> getOddIndexedStrings(List<String> names) {
		return StreamUtils.zipWithIndex(names.stream())
			.filter(i -> i.getIndex() % 2 == 1)
			.collect(Collectors.toList());
	}

	public static List<String> getOddIndexedStrings(String[] names) {
		return IntStream.range(0, names.length)
			.filter(i -> i % 2 == 1)
			.mapToObj(i -> names[i])
			.collect(Collectors.toList());
	}

	public static List<String> getOddIndexedStringsVersionTwo(String[] names) {
		return Stream.of(names)
			.zipWithIndex()
			.filter(tuple -> tuple._2 % 2 == 1)
			.map(tuple -> tuple._1)
			.toJavaList();
	}
}