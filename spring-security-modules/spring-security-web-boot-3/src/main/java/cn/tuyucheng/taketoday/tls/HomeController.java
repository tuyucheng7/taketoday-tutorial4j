package cn.tuyucheng.taketoday.tls;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/tuyucheng")
    public ResponseEntity<String> welcome() {
        return new ResponseEntity<>("tls/tuyucheng", HttpStatus.OK);
    }
}