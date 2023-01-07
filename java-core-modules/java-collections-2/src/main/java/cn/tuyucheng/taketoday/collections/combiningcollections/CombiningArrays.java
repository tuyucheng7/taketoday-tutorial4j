package cn.tuyucheng.taketoday.collections.combiningcollections;

import com.google.common.collect.ObjectArrays;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.stream.Stream;

public class CombiningArrays {

    public static Object[] usingNativeJava(Object[] first, Object[] second) {
        Object[] combined = new Object[first.length + second.length];
        System.arraycopy(first, 0, combined, 0, first.length);
        System.arraycopy(second, 0, combined, first.length, second.length);
        return combined;
    }

    public static Object[] usingJava8ObjectStream(Object[] first, Object[] second) {
        return Stream.concat(Arrays.stream(first), Arrays.stream(second)).toArray();
    }

    public static Object[] usingJava8FlatMaps(Object[] first, Object[] second) {
        return Stream.of(first, second).flatMap(Stream::of).toArray(String[]::new);
    }

    public static Object[] usingApacheCommons(Object[] first, Object[] second) {
        return ArrayUtils.addAll(first, second);
    }

    public static Object[] usingGuava(Object[] first, Object[] second) {
        return ObjectArrays.concat(first, second, Object.class);
    }
}