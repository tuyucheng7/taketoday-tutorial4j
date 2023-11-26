package cn.tuyucheng.taketoday.cors;

import org.springframework.web.bind.annotation.*;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/account")
public class AccountController {

   @CrossOrigin("http://example.com")
   @RequestMapping(method = RequestMethod.GET, path = "/{id}")
   public Account retrieve(@PathVariable Long id) {
      return new Account(id);
   }

   @RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
   public void remove(@PathVariable Long id) {
      // ...
   }
}