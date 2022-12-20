## 1. 概述

依赖管理是任何复杂项目的关键方面，手动执行此操作并不理想；你花在它上面的时间越多，你在项目其他重要方面的时间就越少。

Spring Boot Starter正是为了解决这个问题而构建的。Starter POM是一组方便的依赖描述符，通过将它们包含在你的应用程序中，你可以获得所需的所有Spring和相关技术的一站式服务，而无需搜索示例代码和复制粘贴大量依赖项描述符。

Spring Boot提供了大量的官方Starter，接下里我们简要介绍一些。

## 2. Web启动器

首先，让我们看看如何开发REST服务；我们可以使用像Spring MVC、Tomcat和Jackson这样的库-单个应用程序有很多依赖项。

Spring Boot启动器可以帮助减少手动添加依赖项的数量，只需添加一个依赖项即可。因此，无需手动指定依赖项，只需添加一个starter，如下例所示：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

现在我们可以创建一个REST控制器，为了简单起见，我们不会使用数据库，而是专注于REST控制器：

```java
@RestController
public class GenericEntityController {
    private List<GenericEntity> entityList = new ArrayList<>();

    @RequestMapping("/entity/all")
    public List<GenericEntity> findAll() {
        return entityList;
    }

    @RequestMapping(value = "/entity", method = RequestMethod.POST)
    public GenericEntity addEntity(GenericEntity entity) {
        entityList.add(entity);
        return entity;
    }

    @RequestMapping("/entity/findby/{id}")
    public GenericEntity findById(@PathVariable Long id) {
        return entityList.stream()
              .filter(entity -> entity.getId().equals(id))
              .findFirst()
              .get();
    }
}
```

GenericEntity是一个简单的bean，其id类型为Long且值类型为String。

就是这样，在应用程序运行的情况下，你可以访问[http://localhost:8080/entity/all](http://localhost:8080/springbootapp/entity/all)并检查控制器是否正常工作。至此，我们创建了一个配置非常小的REST应用程序。

## 3. 测试启动器

对于测试，我们通常使用以下一组库：Spring Test、JUnit、Hamcrest和Mockito。我们可以手动包含所有这些库，但是Spring Boot启动器可以通过以下方式自动包含这些库：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

请注意，你不需要指定工件的版本号，Spring Boot会确定要使用的版本-你只需指定spring-boot-starter-parent工件的版本。如果以后需要升级Boot库和依赖项，只需在一个地方升级Boot版本，其余的将由它来完成。

让我们实际测试一下我们在上一个示例中创建的控制器。

有两种方法可以测试控制器：

-   使用Mock环境
-   使用嵌入式Servlet容器(如Tomcat或Jetty)

在此示例中，我们将使用Mock环境：

```java
@ExtendWith(SpringExtension.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
class SpringBootApplicationIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @BeforeEach
    void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void givenRequestHasBeenMade_whenMeetsAllOfGivenConditions_thenCorrect() throws Exception {
        MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
        
        mockMvc.perform(MockMvcRequestBuilders.get("/entity/all")).
              andExpect(MockMvcResultMatchers.status().isOk()).
              andExpect(MockMvcResultMatchers.content().contentType(contentType)).
              andExpect(jsonPath("$", hasSize(4)));
    }
}
```

上面的测试调用/entity/all端点并验证JSON响应是否包含4个元素，为了通过这个测试，我们还必须在控制器类中初始化我们的集合：

```java
public class GenericEntityController {
    private List<GenericEntity> entityList = new ArrayList<>();

    {
        entityList.add(new GenericEntity(1L, "entity_1"));
        entityList.add(new GenericEntity(2L, "entity_2"));
        entityList.add(new GenericEntity(3L, "entity_3"));
        entityList.add(new GenericEntity(4L, "entity_4"));
    }
    //...
}
```

这里重要的是@WebAppConfiguration注解和MockMVC是spring-test模块的一部分，hasSize是一个Hamcrest匹配器，@BeforeEach是一个JUnit注解。这些都可以通过导入一个这样的启动器依赖项来获得。

## 4. Data JPA启动器

大多数Web应用程序都需要与持久层框架通信-通常就是JPA。

与其手动定义所有相关的依赖项，不如让我们使用启动器：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

请注意，我们开箱即用地自动支持至少以下数据库：H2、Derby和Hsqldb。在我们的示例中，我们将使用H2。

现在为我们的实体创建Repository：

```java
public interface GenericEntityRepository extends JpaRepository<GenericEntity, Long> {
}
```

以下是JUnit测试：

```java
@ExtendWith(SpringExtension.class)
@SpringApplicationConfiguration(classes = Application.class)
class SpringBootJPATest {

    @Autowired
    private GenericEntityRepository genericEntityRepository;

    @Test
    void givenGenericEntityRepository_whenSaveAndRetreiveEntity_thenOK() {
        GenericEntity genericEntity = genericEntityRepository.save(new GenericEntity("test"));
        GenericEntity foundedEntity = genericEntityRepository.findOne(genericEntity.getId());

        assertNotNull(foundedEntity);
        assertEquals(genericEntity.getValue(), foundedEntity.getValue());
    }
}
```

我们没有明确指定数据库供应商、URL连接和密码，不需要额外的配置，因为我们受益于可靠的Boot默认设置；但当然，如果需要，仍然可以手动配置所有这些细节。

## 5. Mail启动器

企业开发中一个非常常见的任务是发送电子邮件，而直接处理Java Mail API通常会很困难。

Spring Boot starter隐藏了这种复杂性-可以通过以下方式指定邮件依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

现在我们可以直接使用JavaMailSender，出于测试目的，我们需要一个简单的SMTP服务器。在此示例中，我们将使用Wiser，下面我们将它包含在我们的POM中：

```xml
<dependency>
    <groupId>org.subethamail</groupId>
    <artifactId>subethasmtp</artifactId>
    <version>3.1.7</version>
    <scope>test</scope>
</dependency>

```

最新版本的Wiser可以在[Maven中央仓库](https://search.maven.org/classic/#search|ga|1|subethasmtp)中找到。

这是测试的源代码：

```java
@ExtendWith(SpringExtension.class)
@SpringApplicationConfiguration(classes = Application.class)
class SpringBootMailTest {
    @Autowired
    private JavaMailSender javaMailSender;

    private Wiser wiser;

    private String userTo = "user2@localhost";
    private String userFrom = "user1@localhost";
    private String subject = "Test subject";
    private String textMail = "Text subject mail";

    @BeforeEach
    void setUp() throws Exception {
        final int TEST_PORT = 25;
        wiser = new Wiser(TEST_PORT);
        wiser.start();
    }

    @AfterEach
    void tearDown() throws Exception {
        wiser.stop();
    }

    @Test
    void givenMail_whenSendAndReceived_thenCorrect() throws Exception {
        SimpleMailMessage message = composeEmailMessage();
        javaMailSender.send(message);
        List<WiserMessage> messages = wiser.getMessages();

        assertThat(messages, hasSize(1));
        WiserMessage wiserMessage = messages.get(0);
        assertEquals(userFrom, wiserMessage.getEnvelopeSender());
        assertEquals(userTo, wiserMessage.getEnvelopeReceiver());
        assertEquals(subject, getSubject(wiserMessage));
        assertEquals(textMail, getMessage(wiserMessage));
    }

    private String getMessage(WiserMessage wiserMessage) throws MessagingException, IOException {
        return wiserMessage.getMimeMessage().getContent().toString().trim();
    }

    private String getSubject(WiserMessage wiserMessage) throws MessagingException {
        return wiserMessage.getMimeMessage().getSubject();
    }

    private SimpleMailMessage composeEmailMessage() {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(userTo);
        mailMessage.setReplyTo(userFrom);
        mailMessage.setFrom(userFrom);
        mailMessage.setSubject(subject);
        mailMessage.setText(textMail);
        return mailMessage;
    }
}
```

在测试中，@BeforeEach和@AfterEach方法负责启动和停止邮件服务器。

请注意，我们注入了JavaMailSender bean-**该bean是由Spring Boot自动创建的**。

就像Boot中的任何其他默认设置一样，JavaMailSender的电子邮件设置可以在application.properties中自定义：

```properties
spring.mail.host=localhost
spring.mail.port=25
spring.mail.properties.mail.smtp.auth=false
```

通过上面的配置，我们在localhost:25上配置了邮件服务器，并且不需要身份验证。

## 6. 总结

在本文中，我们概述了Starters，解释了我们为什么需要它们，并提供了有关如何在你的项目中使用它们的示例。

让我们回顾一下使用Spring Boot启动器的好处：

-   提高pom可管理性
-   生产就绪、测试和支持的依赖配置
-   减少项目的整体配置时间