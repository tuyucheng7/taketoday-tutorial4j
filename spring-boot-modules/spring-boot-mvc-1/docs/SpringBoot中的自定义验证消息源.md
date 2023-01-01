## 1. 概述

MessageSource是Spring应用程序中的一个强大功能，这有助于应用程序开发人员通过编写一些额外代码来处理各种复杂场景，例如特定于环境的配置、国际化或可配置值。

另一种情况可能是将默认验证消息修改为更用户友好/自定义的消息。

在本教程中，我们将了解如何**使用Spring Boot在应用程序中配置和管理自定义验证MessageSource**。

## 2. Maven依赖

首先我们从添加必要的Maven依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

## 3. 自定义验证消息示例

让我们考虑一个场景，我们必须开发一个支持多种语言的应用程序，如果用户没有提供正确的详细信息作为输入，我们希望根据用户的语言环境显示错误消息。

下面以LoginForm bean为例：

```java
@Setter
@Getter
public class LoginForm {

    @NotEmpty(message = "{email.notempty}")
    @Email
    private String email;

    @NotNull
    private String password;
}
```

在这里，我们添加了校验注解来验证电子邮件是否没有提供，或者提供了但没有遵循标准的电子邮件地址格式。

为了显示自定义和特定于语言环境的消息，我们可以为@NotEmpty注解提供一个占位符。

email.notempty属性将通过MessageSource配置从属性文件中解析。

## 4. 定义MessageSource bean

应用程序上下文将消息解析委托给名称为messageSource的确切bean，ReloadableResourceBundleMessageSource是最常见的MessageSource实现，它用于解析来自不同区域环境的资源包的消息：

```java
@Bean
public MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    
    messageSource.setBasename("classpath:messages");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
}
```

在这里，我们需要提供basename，因为特定于语言环境的文件名将根据提供的名称进行解析。

## 5. 定义LocalValidatorFactory Bean

**要在属性文件中使用自定义名称消息，我们需要定义一个LocalValidatorFactory bean并注册messageSource**：

```java
@Bean
public LocalValidatorFactoryBean getValidator() {
    LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
    bean.setValidationMessageSource(messageSource());
    return bean;
}
```

但是，请注意，如果我们已经扩展了WebMvcConfigurerAdapter，为了避免忽略自定义验证器，我们必须通过重写父类的getValidator()方法来设置验证器。

现在我们可以定义一个属性消息，如：

```properties
email.notempty=<Custom_Message>
```

而不是

```properties
javax.validation.constraints.NotEmpty.message=<Custom_message>
```

## 6. 定义属性文件

**最后一步是在src/main/resources目录中创建一个属性(properties)文件，其文件名与第4步中的设置的basename一致**：

```properties
# messages.properties
email.notempty=请提供有效的邮箱id
```

在这里，我们可以同时利用国际化。假设我们想为英语用户的语言显示消息，在这种情况下，我们必须在同一位置再添加一个名为messages_en.properties的属性文件(不需要修改代码)：

```properties
# messages_en.properties
email.notempty=Please provide valid email id.
```

## 7. 总结

在本文中，我们介绍了如何在不修改代码的情况下通过配置更改默认验证消息，我们还可以利用国际化的支持来使应用程序更加友好。