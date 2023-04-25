package cn.tuyucheng.taketoday.interpolation;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidationController {

    @PostMapping("/test-not-null")
    public void testNotNull(@Valid @RequestBody NotNullRequest request) {

    }
}
