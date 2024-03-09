package cn.tuyucheng.taketoday;

import java.util.stream.Gatherers;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class StreamGatherersBuiltin {
   public static void main(String[] args) {
      System.out.println("=== Peek ===");
      peek();
      System.out.println("=== Fold ===");
      fold();
      System.out.println("=== Scan ===");
      scan();
      System.out.println("=== Window Fixed ===");
      windowFixed();
      System.out.println("=== Window Sliding ===");
      windowSliding();
   }

   static void peek() {
      System.out.print("Elements: ");
      var elements = generate(10).collect(toList());
      System.out.println(elements);
		/*
		* Shows in [Javadoc](https://cr.openjdk.org/~vklang/gatherers/api/java.base/java/util/stream/Gatherers.html#peek(java.util.function.Consumer))
		* but seems not implemented yet.
		generate(10)
			.gather(
				Gatherers.peek(elem -> System.out.printf("%s "))
			)
			.collect(toList());
		System.out.println();
		*/
   }

   static void fold() {
      var sum = generate(10)
            .gather(
                  Gatherers.fold(() -> 0, (acc, elem) -> acc + elem)
            )
            .findFirst()
            .get();
      System.out.println(STR."The sum of all elements: \{sum}");
   }

   static void scan() {
      var sums = generate(10)
            .gather(
                  Gatherers.scan(() -> 0, (acc, elem) -> acc + elem)
            )
            .toList();
      System.out.println(STR."The sum of each element with its previous: \{sums}");
   }

   static void windowFixed() {
      var pairs = generate(10)
            .gather(Gatherers.windowFixed(2))
            .toList();
      System.out.println(STR."Pair of elements: \{pairs}");
   }

   static void windowSliding() {
      var neighbors = generate(10)
            .gather(Gatherers.windowSliding(2))
            .toList();
      System.out.println(STR."Elements that are neighbor: \{neighbors}");
   }

   static Stream<Integer> generate(int size) {
      return Stream.iterate(1, acc -> acc + 1).limit(size);
   }
}