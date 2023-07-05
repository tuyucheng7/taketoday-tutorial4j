package cn.tuyucheng.taketoday.concurrent.parallel;

import java.io.IOException;

class Benchmark {

   public static void main(String[] args) {
      try {
         org.openjdk.jmh.Main.main(new String[]{"cn.tuyucheng.taketoday.concurrent.parallel.Processor"});
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
}