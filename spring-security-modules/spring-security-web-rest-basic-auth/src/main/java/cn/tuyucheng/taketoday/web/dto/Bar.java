package cn.tuyucheng.taketoday.web.dto;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

@XmlRootElement
public class Bar implements Serializable {

   public Bar() {
      super();
   }
}