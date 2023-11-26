package cn.tuyucheng.taketoday.mime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/foos")
public class FooController {

   @Autowired
   private FooDao fooDao;

   @GetMapping(value = "/{id}")
   public Foo findById(@PathVariable("id") final Long id, final HttpServletResponse response) {
      return fooDao.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
   }

   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   public Foo create(@RequestBody final Foo resource, final HttpServletResponse response) {
      return fooDao.save(resource);
   }
}