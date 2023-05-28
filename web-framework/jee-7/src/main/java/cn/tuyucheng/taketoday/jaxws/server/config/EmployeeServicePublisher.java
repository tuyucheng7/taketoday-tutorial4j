package cn.tuyucheng.taketoday.jaxws.server.config;

import cn.tuyucheng.taketoday.jaxws.server.bottomup.EmployeeServiceImpl;
import cn.tuyucheng.taketoday.jaxws.server.topdown.EmployeeServiceTopDownImpl;

import javax.xml.ws.Endpoint;

public class EmployeeServicePublisher {

   public static void main(String[] args) {
      Endpoint.publish("http://localhost:8080/employeeservicetopdown", new EmployeeServiceTopDownImpl());
      Endpoint.publish("http://localhost:8080/employeeservice", new EmployeeServiceImpl());
   }
}
