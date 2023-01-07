package cn.tuyucheng.taketoday.list.primitive;

import com.google.common.primitives.ImmutableIntArray;
import gnu.trove.list.array.TIntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.util.OptionalDouble;
import java.util.stream.IntStream;

public class PrimitiveCollections {

    public static void main(String[] args) {

        int[] primitives = new int[]{5, 10, 0, 2, -8};

        guavaPrimitives(primitives);

        intStream(primitives);

        TIntArrayList tList = new TIntArrayList(primitives);

        cern.colt.list.IntArrayList coltList = new cern.colt.list.IntArrayList(primitives);

        IntArrayList fastUtilList = new IntArrayList(primitives);

        System.out.println(tList);

        System.out.println(coltList);

        System.out.println(fastUtilList);
    }

    private static void intStream(int[] primitives) {

        IntStream stream = IntStream.of(5, 10, 0, 2, -8);

        IntStream newStream = IntStream.of(primitives);

        OptionalDouble average = stream.filter(i -> i > 0).average();
    }


    private static void guavaPrimitives(int[] primitives) {

        ImmutableIntArray immutableIntArray = ImmutableIntArray.builder().addAll(primitives).build();
        System.out.println(immutableIntArray);
    }
}
