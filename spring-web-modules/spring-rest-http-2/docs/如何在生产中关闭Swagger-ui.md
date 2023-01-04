## 1. 概述

Swagger用户界面允许我们查看有关REST服务的信息。这样可以很方便的进行开发。但是，出于安全考虑，我们可能不想在我们的公共环境中允许这种行为。

在这个简短的教程中，我们将了解如何在生产中关闭Swagger。

## 2. Swagger配置

要[使用Spring设置Swagger](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)，我们在配置bean中定义它。

让我们创建一个SwaggerConfig类：

```java
@Configuration
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
              .apis(RequestHandlerSelectors.basePackage("cn.tuyucheng.taketoday"))
              .paths(PathSelectors.regex("/."))
              .build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
              .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
              .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
```

默认情况下，这个配置bean总是被注入到我们的Spring上下文中。因此，Swagger可用于所有环境。

要在生产中禁用Swagger，让我们切换是否注入此配置bean。

## 3. 使用Spring Profile

在Spring中，我们可以使用[@Profile注解](https://www.baeldung.com/spring-profiles)来启用或禁用bean的注入。

让我们尝试使用[SpEL表达式](https://www.baeldung.com/spring-expression-language)来匹配“swagger” Profile，而不是“prod” Profile：

```java
@Profile({"!prod && swagger"})
```

这迫使我们明确说明要激活Swagger的环境，它还有助于防止在生产中意外打开它。

我们可以将注解添加到我们的配置中：

```java
@Configuration 
@Profile({"!prod && swagger"})
@EnableSwagger2 
public class SwaggerConfig implements WebMvcConfigurer {
    // ...
}
```

现在，让我们通过对spring.profiles.active属性的不同设置启动我们的应用程序来测试它是否有效：

```bash
  -Dspring.profiles.active=prod // Swagger is disabled

  -Dspring.profiles.active=prod,anyOther // Swagger is disabled

  -Dspring.profiles.active=swagger // Swagger is enabled

  -Dspring.profiles.active=swagger,anyOtherNotProd // Swagger is enabled

  none // Swagger is disabled
```

## 4. 使用条件

Spring Profiles可能过于粗粒度地作为功能切换的解决方案。这种方法可能导致配置错误和冗长、难以管理的Profiles列表。

作为替代方案，我们可以使用@ConditionalOnExpression，它允许指定自定义属性以启用bean：

```java
@Configuration
@ConditionalOnExpression(value = "${useSwagger:false}")
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {
    // ...
}
```

如果缺少“useSwagger”属性，则此处的默认值为false。

要对此进行测试，我们可以在application.properties(或application.yaml)文件中设置属性，或将其设置为VM选项：

```properties
-DuseSwagger=true
```

我们应该注意，此示例不包括任何方式来保证我们的生产实例不会意外地将useSwagger设置为true。

## 5. 避免陷阱

如果启用Swagger是一个安全问题，那么我们需要选择一种防错但易于使用的策略。

当我们使用@Profile时，一些SpEL表达式可能会违背这些目标：

```java
@Profile({"!prod"}) // Leaves Swagger enabled by default with no way to disable it in other profiles
```

```java
@Profile({"swagger"}) // Allows activating Swagger in prod as well
```

```java
@Profile({"!prod", "swagger"}) // Equivalent to {"!prod || swagger"} so it's worse than {"!prod"} as it provides a way to activate Swagger in prod too
```

这就是我们的 @Profile示例使用的原因：

```java
@Profile({"!prod && swagger"})
```

这个解决方案可能是最严格的，因为它默认禁用Swagger并保证它不能在“prod”中启用。 

## 6. 总结

在本文中，我们研究了在生产环境中禁用Swagger的解决方案。

我们研究了如何通过@Profile和@ConditionalOnExpression注解来切换打开Swagger的bean，我们还考虑了如何防止错误配置和不良默认设置。