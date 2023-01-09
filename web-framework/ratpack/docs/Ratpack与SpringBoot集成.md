## 1. 概述

之前，我们介绍了[Ratpack](https://www.baeldung.com/ratpack)及其[与 Google Guice 的集成](https://www.baeldung.com/ratpack-google-guice)。

在这篇简短的文章中，我们将展示如何将 Ratpack 与Spring Boot集成。

## 2.Maven依赖

在我们继续之前，让我们将以下依赖项添加到我们的pom.xml 中：

```xml
<dependency>
    <groupId>io.ratpack</groupId>
    <artifactId>ratpack-spring-boot-starter</artifactId>
    <version>1.4.6</version>
    <type>pom</type>
</dependency>
```

ratpack [-spring-boot-starter](https://search.maven.org/classic/#search|ga|1|a%3A"ratpack-spring-boot-starter") pom 依赖项会自动将[ratpack-spring-boot](https://search.maven.org/classic/#search|ga|1|a%3A"ratpack-spring-boot")和[spring-boot-starter](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter")添加到我们的依赖项中。

## 3. 将 Ratpack 与Spring Boot集成

我们可以将 Ratpack 嵌入到Spring Boot中，作为 Tomcat 或 Undertow 提供的 servlet 容器的替代方案。我们只需要用[@EnableRatpack](https://ratpack.io/manual/current/api/ratpack/spring/config/EnableRatpack.html)[注解](https://ratpack.io/manual/current/api/ratpack/handling/Chain.html)一个Spring配置类，声明[Action](https://ratpack.io/manual/current/api/ratpack/func/Action.html) <Chain>类型的bean ：

```java
@SpringBootApplication
@EnableRatpack
public class EmbedRatpackApp {

    @Autowired 
    private Content content;
 
    @Autowired 
    private ArticleList list;

    @Bean
    public Action<Chain> home() {
        return chain -> chain.get(ctx -> ctx.render(content.body()));
    }

    public static void main(String[] args) {
        SpringApplication.run(EmbedRatpackApp.class, args);
    }
}
```

对于那些更熟悉Spring Boot的人来说，Action<Chain>可以充当 Web 过滤器和/或控制器。

当涉及到提供静态文件时，Ratpack 会在 @Autowired [ChainConfigurers中为](https://github.com/ratpack/ratpack/blob/master/ratpack-spring-boot/src/main/java/ratpack/spring/config/internal/ChainConfigurers.java#L63)/public和/static下的静态资源自动注册处理程序。

然而，[目前](https://github.com/ratpack/ratpack/blob/master/ratpack-spring-boot/src/main/java/ratpack/spring/config/RatpackProperties.java#L202)这个“魔法”的实现取决于我们的项目设置和开发环境。所以如果我们要实现静态资源在不同环境下的稳定服务，我们应该显式指定baseDir：

```java
@Bean
public ServerConfig ratpackServerConfig() {
    return ServerConfig
      .builder()
      .findBaseDir("static")
      .build();
}
```

上面的代码假设我们在类路径下有一个静态文件夹。此外，我们将[ServerConfig](https://ratpack.io/manual/current/api/ratpack/server/ServerConfig.html) bean 命名为ratpackServerConfig以覆盖在[RatpackConfiguration](https://github.com/ratpack/ratpack/blob/master/ratpack-spring-boot/src/main/java/ratpack/spring/config/RatpackConfiguration.java#L70)中注册的默认 bean 。

然后我们可以使用[ratpack-test](https://search.maven.org/classic/#search|gav|1|g%3A"io.ratpack" AND a%3A"ratpack-test")测试我们的应用程序：

```java
MainClassApplicationUnderTest appUnderTest
  = new MainClassApplicationUnderTest(EmbedRatpackApp.class);

@Test
public void whenSayHello_thenGotWelcomeMessage() {
    assertEquals("hello baeldung!", appUnderTest
      .getHttpClient()
      .getText("/hello"));
}

@Test
public void whenRequestList_thenGotArticles()  {
    assertEquals(3, appUnderTest
      .getHttpClient()
      .getText("/list")
      .split(",").length);
}

@Test
public void whenRequestStaticResource_thenGotStaticContent() {
    assertThat(appUnderTest
      .getHttpClient()
      .getText("/"), containsString("page is static"));
}
```

## 4. 将Spring Boot与 Ratpack 集成

首先，我们将在 Spring 配置类中注册所需的 bean：

```java
@Configuration
public class Config {

    @Bean
    public Content content() {
        return () -> "hello baeldung!";
    }
}
```

然后我们可以使用ratpack-spring-boot提供的便捷方法[spring(...)](https://ratpack.io/manual/current/api/ratpack/spring/Spring.html#spring-java.lang.Class-java.lang.String...-)轻松创建注册表：

```java
public class EmbedSpringBootApp {

    public static void main(String[] args) throws Exception {
        RatpackServer.start(server -> server
          .registry(spring(Config.class))
          .handlers(chain -> chain.get(ctx -> ctx.render(ctx
            .get(Content.class)
            .body()))));
    }
}
```

在 Ratpack 中，注册表用于请求处理中的处理程序间通信。我们在处理程序中使用的[Context](https://ratpack.io/manual/current/api/ratpack/handling/Context.html)对象实现了[Registry](https://ratpack.io/manual/current/api/ratpack/registry/Registry.html)接口。

在这里，Spring Boot提供的[ListableBeanFactory](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/ListableBeanFactory.html)实例适用于Registry ，以支持Handler的Context中的注册表。因此，当我们想从Context中获取特定类型的对象时，将使用ListableBeanFactory来查找匹配的 bean。

## 5.总结

在本教程中，我们探讨了如何集成Spring Boot和 Ratpack。