package cn.tuyucheng.taketoday.jooby.mvc;

import io.jooby.annotations.POST;
import io.jooby.annotations.Path;

@Path("/submit")
public class PostController {

   @POST
   public String hello() {
      return "Submit Tuyucheng";
   }
}