package cn.tuyucheng.taketoday.sparkjava;

import static spark.Spark.get;

public class HelloWorldService {
   public static void main(String[] args) {
      get("/hello", (req, res) -> "Hello, Tuyucheng");

      get("/hello/:name", (req, res) -> {
         return "Hello: " + req.params(":name");
      });
   }
}
