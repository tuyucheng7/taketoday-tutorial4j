package cn.tuyucheng.taketoday.inmemory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class InMemoryAuthController {

    @GetMapping("/public/hello")
    public List<String> publicHello() {
        return Arrays.asList("Hello", "World", "from", "Public");
    }

    @GetMapping("/private/hello")
    public List<String> privateHello() {
        return Arrays.asList("Hello", "World", "from", "Private");
    }

}
