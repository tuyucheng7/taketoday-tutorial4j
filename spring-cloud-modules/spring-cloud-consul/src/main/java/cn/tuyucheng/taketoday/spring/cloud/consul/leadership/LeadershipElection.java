package cn.tuyucheng.taketoday.spring.cloud.consul.leadership;

import net.kinguin.leadership.consul.factory.SimpleConsulClusterFactory;

public class LeadershipElection {
   public static void main(String[] args) {
      new SimpleConsulClusterFactory()
            .mode(SimpleConsulClusterFactory.MODE_MULTI)
            .debug(true)
            .build()
            .asObservable()
            .subscribe(System.out::println);
   }
}