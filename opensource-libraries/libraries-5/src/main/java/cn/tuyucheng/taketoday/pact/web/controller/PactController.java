package cn.tuyucheng.taketoday.pact.web.controller;

import cn.tuyucheng.taketoday.pact.web.dto.PactDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PactController {

   List<PactDto> pacts = new ArrayList<>();

   @GetMapping(value = "/pact", produces = MediaType.APPLICATION_JSON_VALUE)
   @ResponseBody
   public PactDto getPact() {
      return new PactDto(true, "tom");
   }

   @PostMapping("/pact")
   @ResponseStatus(HttpStatus.CREATED)
   public void createPact(PactDto pact) {
      pacts.add(pact);
   }
}