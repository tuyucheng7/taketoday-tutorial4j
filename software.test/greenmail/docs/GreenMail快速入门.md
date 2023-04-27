## 1. 概述

发送电子邮件是企业应用程序的共同职责。使用Spring Boot Starter Mail依赖项，这个过程变得微不足道。但是我们如何在不向用户发送实际电子邮件的情况下编写集成测试来验证我们的功能呢？

在这篇简短的文章中，**我们将介绍[GreenMail](https://greenmail-mail-test.github.io/greenmail/)，以对使用Spring Boot发送电子邮件的应用程序进行集成测试**。

## 2. Spring Boot项目设置

演示如何编写集成测试以使用JavaMailSender发送电子邮件的项目非常简单，我们只需要在POM中包含以下Spring Boot Starters依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

对于我们的应用程序，每当我们使用有效负载对/notifications执行HTTP POST时，我们都会通过向用户发送电子邮件来通知他/她：

```java
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public void createNotification(@Valid @RequestBody NotificationRequest request) {
        this.notificationService.notifyUser(request.getEmail(), request.getContent());
    }
}
```

我们使用Bean Validation来确保我们的客户端传递有效的电子邮件地址和非空的电子邮件消息：

```java
public class NotificationRequest {

    @Email
    private String email;

    @NotBlank
    private String content;

    // getters and setters
}
```

实际的电子邮件传输发生在NotificationService中，该服务使用为我们自动配置的来自Spring的JavaMailSender。

## 3. 使用JavaMailSender从Spring发送电子邮件

根据Spring Boot的自动配置机制，**每当我们指定spring.mail.*属性时，我们都会得到一个现成的[JavaMailSender](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/mail/javamail/JavaMailSender.html) bean**。

如果你想了解此自动配置发生的方式和时间，可以检查Spring Boot类[MailSenderAutoConfiguration](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/mail/MailSenderAutoConfiguration.html)和[MailSenderPropertiesConfiguration](https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/mail/MailSenderPropertiesConfiguration.java)。

使用Google的Gmail SMTP服务器的最低配置如下所示：

```yaml
spring:
    mail:
        password: t0pS3cReT
        username: yourmail@gmail.com
        host: smtp.gmail.com
        port: 587
        protocol: smtp
        properties:
            mail:
                smtp:
                    auth: true
                    starttls:
                        enable: true
```

然后我们可以注入JavaMailSender并开始从我们的Spring Boot应用程序发送电子邮件：

```java
@Service
public class NotificationService {

    private final JavaMailSender javaMailSender;

    public NotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void notifyUser(String email, String content) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("admin@spring.io");
        mail.setSubject("A new message for you");
        mail.setText(content);
        mail.setTo(email);

        this.javaMailSender.send(mail);
    }
}
```

为了发送更高级的电子邮件(例如带有附件或HTML负载)，我们可以**使用JavaMailSender创建一个MimeMessage**。但是，对于我们的测试Demo来说，SimpleMailMessage完全没问题。

现在，在为我们应用程序的这个组件编写集成测试时，我们**不想连接到真正的SMTP服务器并发送电子邮件**。否则，每当我们运行测试套件时，我们的用户都会收到无用的测试消息。

更好的方法是**使用本地沙箱电子邮件服务器来捕获和验证所有电子邮件流量**。

GreenMail就是这样一个电子邮件服务器，它允许测试发送和接收邮件。它是开源的，使用Java编写，我们可以轻松地将它集成到我们的项目中。

## 4. 使用GreenMail和JUnit 5编写集成测试

有多种方法可以将GreenMail集成到我们的测试中。让我们从最直观的方法开始，注册GreenMail的JUnit 5扩展。

为此，我们需要以下[GreenMail依赖项](https://central.sonatype.com/artifact/com.icegreen/greenmail-junit5/2.1.0-alpha-1)：

```xml
<dependency>
    <groupId>com.icegreen</groupId>
    <artifactId>greenmail-junit5</artifactId>
    <version>1.6.14</version>
    <scope>test</scope>
</dependency>
```

在注册GreenMail扩展时，我们可以配置电子邮件服务器并决定测试所需的协议。**由于我们的Demo应用程序仅发送电子邮件，因此激活SMTP就足够了**：

```java
@RegisterExtension
static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
    .withConfiguration(GreenMailConfiguration.aConfig().withUser("duke", "springboot"))
    .withPerMethodLifecycle(false);
```

作为设置GreenMail服务器的一部分，我们创建了一个服务用户。我们可以使用此信息来覆盖应用程序的配置，方法是将包含以下内容的application.yml放在src/test/resources中：

```yaml
spring:
    mail:
        password: springboot
        username: duke
        host: 127.0.0.1
        port: 3025 # default protocol port + 3000 as offset
        protocol: smtp
```

**此扩展的默认行为是为每个测试方法启动和停止GreenMail服务器，这将为每个测试执行提供一个空的电子邮件沙箱服务器**。这种方法带来了额外的测试执行时间(几乎可以忽略不计)，因为我们在每次测试之前/之后启动/停止GreenMail。

我们可以**使用withPerMethodLifecycle(false)为测试类的所有测试方法共享一个GreenMail服务器**来覆盖此默认行为。但是，在使用此方法时，我们必须**确保我们的测试是独立的**，并且如果先前的测试将电子邮件发送到同一收件箱，则不会失败。这可以通过使用随机电子邮件地址来缓解。

我们使用@SpringBootTest进行集成测试以启动整个Spring上下文。为了调用我们的端点，我们使用自动配置的TestRestTemplate，因为我们还启动了嵌入式Tomcat(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)。

综上所述，验证电子邮件传输的基本集成测试如下所示：

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NotificationControllerIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("duke", "springboot"))
            .withPerMethodLifecycle(false);

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void shouldSendEmailWithCorrectPayloadToUser() throws Exception {
        String payload = """
                {
                	"email": "duke@spring.io",
                	"content": "Hello World!"
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        ResponseEntity<Void> response = this.testRestTemplate.postForEntity("/notifications", request, Void.class);

        assertEquals(200, response.getStatusCodeValue());

        await().atMost(2, SECONDS).untilAsserted(() -> {
            MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
            assertEquals(1, receivedMessages.length);

            MimeMessage receivedMessage = receivedMessages[0];
            assertEquals("Hello World!", GreenMailUtil.getBody(receivedMessage));
            assertEquals(1, receivedMessage.getAllRecipients().length);
            assertEquals("duke@spring.io", receivedMessage.getAllRecipients()[0].toString());
        });
    }
}
```

调用我们的端点后，我们可以从GreenMail扩展请求所有捕获的电子邮件。

上面的测试断言非常简单，因为我们希望在调用我们的端点后立即发送电子邮件。

我们还**可以使用[Awaitility](https://github.com/awaitility/awaitility)包装我们的期望，以更好地验证应用程序的这种异步操作**：

```java
await().atMost(2, SECONDS).untilAsserted(() -> {
    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
    assertEquals(1, receivedMessages.length);
    
    MimeMessage receivedMessage = receivedMessages[0];
    assertEquals("Hello World!", GreenMailUtil.getBody(receivedMessage));
    assertEquals(1, receivedMessage.getAllRecipients().length);
    assertEquals("duke@spring.io", receivedMessage.getAllRecipients()[0].toString());
});
```

## 5. 使用Testcontainer启动GreenMail服务器

GreenMail项目还提供了[官方Docker镜像](https://hub.docker.com/r/greenmail/standalone/)。**结合[Testcontainers](https://rieckpil.de/howto-write-spring-boot-integration-tests-with-a-real-database/)，我们可以使用此Docker镜像为我们的集成测试启动本地GreenMail Docker容器**。

我们将使用Testcontainers的[JUnit Jupiter扩展](https://central.sonatype.com/artifact/org.testcontainers/junit-jupiter/1.18.0)来管理容器生命周期：

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.16.3</version>
    <scope>test</scope>
</dependency>
```

接下来，我们使用官方GreenMail Docker镜像定义一个GenericContainer。我们可以使用环境变量GREENMAIL_OPTS来调整电子邮件服务器配置并添加我们的服务用户。

剩下的就是告诉Testcontainers要公开哪个端口以及哪个日志消息表示容器已准备好接收流量：

```java
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NotificationControllerLiveTest {

    @Container
    static GenericContainer greenMailContainer = new GenericContainer<>(DockerImageName.parse("greenmail/standalone:1.6.1"))
            .waitingFor(Wait.forLogMessage(".*Starting GreenMail standalone.*", 1))
            .withEnv("GREENMAIL_OPTS", "-Dgreenmail.setup.test.smtp -Dgreenmail.hostname=0.0.0.0 -Dgreenmail.users=duke:springboot")
            .withExposedPorts(3025);

    @DynamicPropertySource
    static void configureMailHost(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", greenMailContainer::getHost);
        registry.add("spring.mail.port", greenMailContainer::getFirstMappedPort);
    }

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void shouldSendEmailWithCorrectPayloadToUser() throws Exception {
        String payload = """
                {
                	"email": "duke@spring.io",
                	"content": "Hello World!"
                }
                """;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        ResponseEntity<Void> response = this.testRestTemplate.postForEntity("/notifications", request, Void.class);

        assertEquals(200, response.getStatusCodeValue());
    }
}
```

由于Testcontainers会将GreenMail的3025端口映射到我们机器上的随机临时端口，因此地址是动态的。

使用@DynamicPropertySource，我们可以在启动Spring TestContext之前覆盖电子邮件服务器配置的动态部分。

使用这种方法验证电子邮件变得有点棘手。我们可以在测试执行后创建一个JavaMail Session并连接到容器化的GreenMail实例。

但是，**如果你想为多个测试类共享一个GreenMail实例，并且只需要一个正在运行的电子邮件服务器来启动你的上下文，则此设置可能很有用**。你还可以在本地开发期间使用这个独立的GreenMail Docker镜像。

## 6. 总结

GreenMail是一个非常有用的工具，可以避免在测试发送电子邮件的情况下使用真实的SMTP服务器。

在本文中，我们了解了如何使用GreenMail扩展和Testcontainers来测试Spring Boot应用程序中的电子邮件发送。