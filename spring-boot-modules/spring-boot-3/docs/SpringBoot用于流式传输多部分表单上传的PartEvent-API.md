## 1. 简介

使用部分事件时，多部分HTTP消息中的每个部分将生成至少一个PartEvent实例，其中包含两个标头和一个包含该部分内容的缓冲区。

多部分内容可以通过PartEvent对象发送：

-   FormPartEvent：为每个表单字段生成一个对象，包含该字段的值。
-   FilePartEvent：生成一个或多个包含文件名和内容的对象。如果文件大到可以拆分到多个缓冲区，则第一个FilePartEvent之后将是后续事件。

我们可以将@RequestBody与Flux(或Kotlin 中的Flow)一起使用，以在服务器端接受多部分。

## 2. 多部分文件上传控制器

以下是文件上传控制器及其接受多部分事件的处理程序方法的示例。

-   它使用PartEvent::isLast如果有属于后续部分的附加事件则为真。
-   Flux ::switchOnFirst运算符允许查看我们是在处理表单字段还是文件上传。
-   我们可以使用相应的方法，例如FormPartEvent.value()、 FilePartEvent.filename() 和PartEvent::content从分段上传部分检索信息。

请注意，正文内容必须完全消耗、中继或释放，以避免内存泄漏。

```java
import static org.springframework.http.ResponseEntity.ok;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePartEvent;
import org.springframework.http.codec.multipart.FormPartEvent;
import org.springframework.http.codec.multipart.PartEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@RestController
@Slf4j
public class FileUploadController {
    @PostMapping("upload-with-part-events")
    public ResponseEntity<Flux<String>> handlePartsEvents(@RequestBody Flux<PartEvent> allPartsEvents) {
        var result = allPartsEvents.windowUntil(PartEvent::isLast)
              .concatMap(p -> {
                        return p.switchOnFirst((signal, partEvents) -> {
                                  if (signal.hasValue()) {
                                      PartEvent event = signal.get();
                                      if (event instanceof FormPartEvent formEvent) {
                                          String value = formEvent.value();
                                          log.info("form value: {}", value);
                                          // handle form field
                                          return Mono.just(value + "n");
                                      } else if (event instanceof FilePartEvent fileEvent) {
                                          String filename = fileEvent.filename();
                                          log.info("upload file name:{}", filename);
                                          // handle file upload
                                          Flux<DataBuffer> contents = partEvents.map(PartEvent::content);
                                          var fileBytes = DataBufferUtils.join(contents)
                                                .map(dataBuffer -> {
                                                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                                    dataBuffer.read(bytes);
                                                    DataBufferUtils.release(dataBuffer);
                                                    return bytes;
                                                });
                                          return Mono.just(filename);
                                      } else
                                          // no value
                                          return Mono.error(new RuntimeException("Unexpected event: " + event));
                                  }
                                  log.info("return default flux");
                                  // return result;
                                  return Flux.empty(); // either complete or error signal
                              }
                        );
                    }
              );
        return ok().body(result);
    }
}
```

## 3.使用WebTestClient发送PartEvent对象

我们已经创建了一个[@WebFluxTest](https://howtodoinjava.com/spring-boot2/testing/webfluxtest-with-webtestclient/)注释测试类，它自动配置一个WebTestClient。我们将使用WebTestClient将多部分事件发送到上述文件上传 API 并验证结果。

不要忘记将'spring.png'放在'/resources'文件夹中，否则你可能会得到FileNotFoundException。

```java
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import com.howtodoinjava.app.web.FileUploadController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.codec.multipart.FilePartEvent;
import org.springframework.http.codec.multipart.FormPartEvent;
import org.springframework.http.codec.multipart.PartEvent;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
@WebFluxTest
public class FileUploadControllerTest {
    @Autowired
    FileUploadController fileUploadController;
    @Autowired
    WebTestClient client;
    @Test
    public void testUploadUsingPartEvents() {
        this.client
              .post().uri("/upload-with-part-events")
              .contentType(MULTIPART_FORM_DATA)
              .body(
                    Flux.concat(
                          FormPartEvent.create("name", "test"),
                          FilePartEvent.create("file", new ClassPathResource("spring.png"))
                    ),
                    PartEvent.class
              )
              .exchange()
              .expectStatus().isOk()
              .expectBodyList(String.class).hasSize(2);
    }
}
```

测试成功通过，我们可以在控制台日志中验证结果。

```shell
...c.h.app.web.FileUploadController : form value: test
...c.h.app.web.FileUploadController : upload file name:spring.png
```

## 4。结论

在这个简短的教程中，我们学习了如何使用 Spring 6 中新引入的 PartEvent API 将多部分请求发送到 webflux 控制器并处理上传的表单参数和文件内容。