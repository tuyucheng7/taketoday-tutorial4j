package cn.tuyucheng.taketoday.gatling.http;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.bodyString;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class FetchSinglePostSimulation extends Simulation {

   public FetchSinglePostSimulation() {
      HttpProtocolBuilder httpProtocolBuilder = http.baseUrl("https://jsonplaceholder.typicode.com");

      ScenarioBuilder scn = scenario("Display Full HTTP Response Body").exec(http("GET Request").get("/posts/1")
                  .check(status().is(200))
                  .check(bodyString().saveAs("responseBody")))
            .exec(session -> {
               System.out.println("Response Body:");
               System.out.println(session.getString("responseBody"));
               return session;
            });
      setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocolBuilder);
   }
}