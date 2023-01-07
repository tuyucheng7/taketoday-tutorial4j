package cn.tuyucheng.taketoday.collections.convertarrayprimitives;

import com.google.common.primitives.Ints;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConvertPrimitivesArrayToList {

    public static void failConvert() {
        int[] input = new int[]{1, 2, 3, 4};
        // List<Integer> inputAsList = Arrays.asList(input);
    }

    public static List<Integer> iterateConvert(int[] input) {
        List<Integer> output = new ArrayList<>();
        for (int value : input) {
            output.add(value);
        }
        return output;
    }

    public static List<Integer> streamConvert(int[] input) {
        return Arrays.stream(input).boxed().collect(Collectors.toList());
    }

    public static List<Integer> streamConvertIntStream(int[] input) {
        return IntStream.of(input).boxed().collect(Collectors.toList());
    }

    public static List<Integer> guavaConvert(int[] input) {
        return Ints.asList(input);
    }

    public static List<Integer> apacheCommonConvert(int[] input) {
        Integer[] outputBoxed = ArrayUtils.toObject(input);
        return Arrays.asList(outputBoxed);
    }
}