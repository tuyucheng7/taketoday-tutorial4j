package cn.tuyucheng.taketoday.web.dto;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

@XmlRootElement
public class Foo implements Serializable {

   public Foo() {
      super();
   }
}