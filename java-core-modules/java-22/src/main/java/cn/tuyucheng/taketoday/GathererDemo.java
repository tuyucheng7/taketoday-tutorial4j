package cn.tuyucheng.taketoday;

import java.time.Instant;
import java.util.List;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public class GathererDemo {

   record Reading(Instant obtainedAt, int kelvins) {

      Reading(String time, int kelvins) {
         this(Instant.parse(time), kelvins);
      }

      static Stream<Reading> loadRecentReadings() {
         // In reality these could be read from a file, a database,
         // a service, or otherwise
         return Stream.of(
               new Reading("2023-09-21T10:15:30.00Z", 310),
               new Reading("2023-09-21T10:15:31.00Z", 312),
               new Reading("2023-09-21T10:15:32.00Z", 350),
               new Reading("2023-09-21T10:15:33.00Z", 310)
         );
      }

   }

   public static void main(String[] args) {
      findSuspicious(Reading.loadRecentReadings())
            .forEach(System.out::println);
   }

   static List<List<Reading>> findSuspicious(Stream<Reading> source) {
      return source
            .gather(Gatherers.windowSliding(2))
            .filter(window -> (window.size() == 2
                  && isSuspicious(window.get(0),
                  window.get(1))))
            .toList();
   }

   static boolean isSuspicious(Reading previous, Reading next) {
      return next.obtainedAt().isBefore(previous.obtainedAt().plusSeconds(5))
            && (next.kelvins() > previous.kelvins() + 30
            || next.kelvins() < previous.kelvins() - 30);
   }
}