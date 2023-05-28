package cn.tuyucheng.taketoday.jooby.mvc;

import io.jooby.ModelAndView;
import io.jooby.annotations.GET;
import io.jooby.annotations.Path;

import java.util.HashMap;

@Path("/hello")
public class GetController {

   @GET
   public String hello() {
      return "Hello Tuyucheng";
   }

   @GET
   @Path("/home")
   public ModelAndView home() {
      return new ModelAndView("welcome.html", new HashMap<>());
   }
}