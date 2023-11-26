package cn.tuyucheng.taketoday.swaggerjwt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController(value = "/clients")
@Tag(name = "Clients")
public class ClientsRestController {

   @Operation(summary = "This method is used to get the clients.")
   @GetMapping
   public List<String> getClients() {
      return Arrays.asList("First Client", "Second Client");
   }
}