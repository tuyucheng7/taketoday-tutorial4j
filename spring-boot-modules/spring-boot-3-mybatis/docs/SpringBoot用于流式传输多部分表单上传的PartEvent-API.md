## 1. 简介

从[Spring 6和Spring boot 3](https://www.baeldung.com/spring-boot-3-spring-6-new)开始，我们可以使用新的[PartEvent](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/codec/multipart/PartEvent.html) API将**多部分事件流式传输**到[Spring WebFlux控制器](https://howtodoinjava.com/spring-webflux/spring-webflux-tutorial/)。PartEvent API有助于以流方式按顺序处理多部分数据。

使用部分事件时，多部分HTTP消息中的每个部分将生成至少一个PartEvent实例，其中包含标头和一个包含该部分内容的缓冲区。

多部分内容可以通过PartEvent对象发送：

-   **FormPartEvent**：为每个表单字段生成一个对象，包含该字段的值。
-   **FilePartEvent**：生成一个或多个包含文件名和内容的对象。如果文件大到可以拆分到多个缓冲区，则第一个FilePartEvent之后将是后续事件。

我们可以将@RequestBody与Flux(或Kotlin中的Flow)一起使用，以在服务器端接受多部分。

## 2. 多部分文件上传控制器

以下是文件上传控制器及其接受多部分事件的处理程序方法的示例。

-   它使用PartEvent::isLast，如果有属于后续部分的附加事件则为true。
-   Flux::switchOnFirst运算符允许查看我们是否正在处理表单字段或文件上传。
-   我们可以使用相应的方法，例如FormPartEvent.value()、FilePartEvent.filename()和PartEvent::content从多部分上传部分检索信息。

请注意，正文内容必须完全消耗、中继或释放，以避免内存泄漏。

```java
@RestController
@Slf4j
public class FileUploadController {
    @PostMapping("upload-with-part-events")
    public ResponseEntity<Flux<String>> handlePartsEvents(@RequestBody Flux<PartEvent> allPartsEvents) {
        var result = allPartsEvents.windowUntil(PartEvent::isLast)
                .concatMap(p -> p.switchOnFirst((signal, partEvents) -> {
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
                        )
                );
        return ok().body(result);
    }
}
```

## 3. 使用WebTestClient发送PartEvent对象

我们创建了一个[@WebFluxTest](https://howtodoinjava.com/spring-boot2/testing/webfluxtest-with-webtestclient/)注解测试类，它自动配置一个WebTestClient。我们将使用WebTestClient将多部分事件发送到上述文件上传API并验证结果。

不要忘记将“spring.png”放在“/resources”文件夹中，否则你可能会得到FileNotFoundException。

```java
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
                .body(Flux.concat(
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
c.t.t.upload.web.FileUploadController : form value: test
c.t.t.upload.web.FileUploadController : upload file name:spring.png
```

## 4. 总结

在这个简短的教程中，我们学习了如何使用Spring 6中新引入的PartEvent API将多部分请求发送到Webflux控制器并处理上传的表单参数和文件内容。