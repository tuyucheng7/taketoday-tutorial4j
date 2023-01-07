package cn.tuyucheng.taketoday.collections.combiningcollections;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CombiningLists {

    public static List<Object> usingNativeJava(List<Object> first, List<Object> second) {
        List<Object> combined = new ArrayList<>();
        combined.addAll(first);
        combined.addAll(second);
        return combined;
    }

    public static List<Object> usingJava8ObjectStream(List<Object> first, List<Object> second) {
        return Stream.concat(first.stream(), second.stream()).collect(Collectors.toList());
    }

    public static List<Object> usingJava8FlatMaps(List<Object> first, List<Object> second) {
        return Stream.of(first, second).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static List<Object> usingApacheCommons(List<Object> first, List<Object> second) {
        return ListUtils.union(first, second);
    }

    public static List<Object> usingGuava(List<Object> first, List<Object> second) {
        Iterable<Object> combinedIterables = Iterables.unmodifiableIterable(
              Iterables.concat(first, second));

        return Lists.newArrayList(combinedIterables);
    }
}