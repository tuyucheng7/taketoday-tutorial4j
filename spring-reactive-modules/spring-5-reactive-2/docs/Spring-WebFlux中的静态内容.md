## 1. 概述

有时，我们必须在Web应用程序中提供静态资源。它可能是图像、HTML、CSS或JavaScript文件。

在本教程中，我们将展示如何使用[Spring WebFlux](https://www.baeldung.com/spring-webflux)提供静态内容。我们还假设我们的Web应用程序将使用[Spring Boot](https://www.baeldung.com/spring-boot-start)进行配置。

## 2. 覆盖默认配置

默认情况下，Spring Boot从以下位置提供静态资源：

+ /public
+ /static
+ /resources
+ /META-INF/resources

来自这些路径的所有文件都在/[resource-file-name]路径下提供。

如果我们想更改Spring WebFlux的默认路径，我们需要将此属性添加到我们的application.properties文件中：

```properties
# Use in Static content Example
spring.webflux.static-path-pattern=/assets/**
```

现在，静态资源将位于/assets/[resource-file-name]下。

**请注意，当存在@EnableWebFlux注解时，这将不起作用**。

## 3. 路由示例

也可以使用WebFlux路由机制来提供静态资源。

让我们看一个为index.html文件提供服务的路由定义示例：

```java
@Configuration
public class StaticContentConfig {

    @Bean
    public RouterFunction<ServerResponse> htmlRouter(@Value("classpath:/public/index.html") Resource html) {
        return route(GET("/"), 
              req -> ok().contentType(MediaType.TEXT_HTML).bodyValue(html));
    }
}
```

我们还可以在RouterFunction的帮助下从自定义位置提供静态内容。

让我们看看如何使用/img/**路径从src/main/resources/img目录提供图像：

```java
@Bean
public RouterFunction<ServerResponse> imgRouter() {
    return resources("/img/**", new ClassPathResource("img/"));
}
```

## 4. 自定义Web资源路径示例

另一种提供存储在自定义位置的静态资源的方法是使用[maven-resources-plugin](https://central.sonatype.com/artifact/org.apache.maven.plugins/maven-resources-plugin/3.3.0)和一个额外的Spring WebFlux属性，而不是默认的src/main/resources路径。

首先，让我们将插件添加到我们的pom.xml中：

```xml
<plugin>
    <artifactId>maven-resources-plugin</artifactId>
    <version>3.1.0</version>
    <executions>
        <execution>
            <id>copy-resources</id>
            <phase>validate</phase>
            <goals>
                <goal>copy-resources</goal>
            </goals>
            <configuration>
                <resources>
                    <resource>
                        <directory>src/main/assets</directory>
                        <filtering>true</filtering>
                    </resource>
                </resources>
                <outputDirectory>${basedir}/target/classes/assets</outputDirectory>
            </configuration>
        </execution>
    </executions>
</plugin>
```

然后，我们只需要设置static-locations属性：

```properties
spring.resources.static-locations=classpath:/assets/
```

在这些操作之后，index.html将在[http://localhost:8080/index.html](http://localhost:8080/index.html) URL下可用。

## 5. 总结

在本文中，我们学习了如何在Spring WebFlux中提供静态资源。