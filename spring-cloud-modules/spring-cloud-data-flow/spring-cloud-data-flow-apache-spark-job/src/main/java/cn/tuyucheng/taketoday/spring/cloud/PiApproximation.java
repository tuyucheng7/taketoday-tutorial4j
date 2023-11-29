package cn.tuyucheng.taketoday.spring.cloud;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.List;
import java.util.stream.IntStream;

public class PiApproximation {
   public static void main(String[] args) {
      SparkConf conf = new SparkConf().setAppName("TuyuchengPIApproximation").setMaster("local[2]");
      JavaSparkContext context = new JavaSparkContext(conf);
      int slices = args.length >= 1 ? Integer.parseInt(args[0]) : 2;
      int n = (100000L * slices) > Integer.MAX_VALUE ? Integer.MAX_VALUE : 100000 * slices;

      List<Integer> xs = IntStream.rangeClosed(0, n)
            .boxed()
            .toList();

      JavaRDD<Integer> dataSet = context.parallelize(xs, slices);

      JavaRDD<Integer> pointsInsideTheCircle = dataSet.map(integer -> {
         double x = Math.random() * 2 - 1;
         double y = Math.random() * 2 - 1;
         return (x * x + y * y) < 1 ? 1 : 0;
      });

      int count = pointsInsideTheCircle.reduce(Integer::sum);

      System.out.println("The pi was estimated as:" + count / n);

      context.stop();
   }
}