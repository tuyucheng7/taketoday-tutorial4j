package cn.tuyucheng.taketoday.springcloudgateway.customfilters.service.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Locale;

@RestController
public class ServiceRestController {

    @GetMapping("/resource")
    public Mono<ResponseEntity<String>> getResource() {
        return Mono.just(ResponseEntity.ok()
              .header(HttpHeaders.CONTENT_LANGUAGE, Locale.ENGLISH.getLanguage())
              .body("Service Resource"));

    }
}
