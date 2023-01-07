package cn.tuyucheng.taketoday.collections.combiningcollections;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.exec.util.MapUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CombiningMaps {

    public static Map<String, String> usingPlainJava(Map<String, String> first, Map<String, String> second) {
        Map<String, String> combined = new HashMap<>();
        combined.putAll(first);
        combined.putAll(second);
        return combined;
    }

    public static Map<String, String> usingJava8ForEach(Map<String, String> first, Map<String, String> second) {
        second.forEach((key, value) -> first.merge(key, value, String::concat));
        return first;
    }

    public static Map<String, String> usingJava8FlatMaps(Map<String, String> first, Map<String, String> second) {
        return Stream.of(first, second).map(Map::entrySet).flatMap(Collection::stream)
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, String::concat));

    }

    public static Map<String, String> usingApacheCommons(Map<String, String> first, Map<String, String> second) {
        return MapUtils.merge(first, second);
    }

    public static Map<String, String> usingGuava(Map<String, String> first, Map<String, String> second) {
        return ImmutableMap.<String, String>builder()
              .putAll(first)
              .putAll(second)
              .build();
    }
}