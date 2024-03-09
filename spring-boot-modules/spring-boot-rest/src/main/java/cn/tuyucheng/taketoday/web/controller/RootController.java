package cn.tuyucheng.taketoday.web.controller;

import cn.tuyucheng.taketoday.web.util.LinkUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriTemplate;

import java.net.URI;

@Controller
public class RootController {

   @GetMapping("/")
   @ResponseStatus(value = HttpStatus.NO_CONTENT)
   public void adminRoot(final HttpServletRequest request, final HttpServletResponse response) {
      final String rootUri = request.getRequestURL()
            .toString();

      final URI fooUri = new UriTemplate("{rootUri}{resource}").expand(rootUri, "foos");
      final String linkToFoos = LinkUtil.createLinkHeader(fooUri.toASCIIString(), "collection");
      response.addHeader("Link", linkToFoos);
   }
}