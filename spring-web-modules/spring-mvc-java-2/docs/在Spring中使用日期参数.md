## 1. 简介

在这个简短的教程中，我们将学习如何在请求和应用程序级别接受Spring REST请求中的Date、LocalDate和LocalDateTime参数。

## 2. 问题

让我们考虑一个控制器，它具有接受Date、LocalDate和LocalDateTime参数的三种方法：

```java
@RestController
public class DateTimeController {

    @PostMapping("/date")
    public void date(@RequestParam("date") Date date) {
        // ...
    }

    @PostMapping("/localdate")
    public void localDate(@RequestParam("localDate") LocalDate localDate) {
        // ...
    }

    @PostMapping("/localdatetime")
    public void dateTime(@RequestParam("localDateTime") LocalDateTime localDateTime) {
        // ...
    }
}
```

当使用符合ISO 8601格式的参数向这些方法中的任何一个发送POST请求时，我们将得到一个异常。

例如，当发送“2018-10-22”到/date端点时，我们会收到一个错误的请求错误，消息类似于这样：

```bash
Failed to convert value of type 'java.lang.String' to required type 'java.time.LocalDate'; 
  nested exception is org.springframework.core.convert.ConversionFailedException.
```

这是因为默认情况下，Spring无法将String参数转换为任何日期或时间对象。

## 3. 在请求级别转换日期参数

处理此问题的方法之一是使用@DateTimeFormat注解对参数进行标注，并提供格式化模式参数：

```java
@RestController
public class DateTimeController {

    @PostMapping("/date")
    public void date(@RequestParam("date")
                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date) {
        // ...
    }

    @PostMapping("/local-date")
    public void localDate(@RequestParam("localDate")
                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate localDate) {
        // ...
    }

    @PostMapping("/local-date-time")
    public void dateTime(@RequestParam("localDateTime")
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime localDateTime) {
        // ...
    }
}
```

这样，如果字符串使用ISO 8601格式进行格式化，则字符串将正确转换为日期对象。

我们还可以通过在@DateTimeFormat注解中提供模式参数来使用我们自己的转换模式：

```java
@PostMapping("/date")
public void date(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") Date date) {
    // ...
}
```

## 4. 在应用层转换日期参数

在Spring中处理日期和时间对象转换的另一种方法是提供全局配置，按照[官方文档](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#format-configuring-formatting-globaldatetimeformat)，我们应该扩展WebMvcConfigurationSupport配置及其mvcConversionService方法：

```java
@Configuration
public class DateTimeConfig extends WebMvcConfigurationSupport {

    @Bean
    @Override
    public FormattingConversionService mvcConversionService() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService(false);

        DateTimeFormatterRegistrar dateTimeRegistrar = new DateTimeFormatterRegistrar();
        dateTimeRegistrar.setDateFormatter(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        dateTimeRegistrar.setDateTimeFormatter(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        dateTimeRegistrar.registerFormatters(conversionService);

        DateFormatterRegistrar dateRegistrar = new DateFormatterRegistrar();
        dateRegistrar.setFormatter(new DateFormatter("dd.MM.yyyy"));
        dateRegistrar.registerFormatters(conversionService);

        return conversionService;
    }
}
```

首先，我们使用false参数创建DefaultFormattingConversionService，这意味着Spring默认不会注册任何格式化程序。

然后我们需要为日期和日期时间参数注册我们的自定义格式。我们通过注册两个自定义格式注册商来做到这一点。第一个，DateTimeFormatterRegistrar，将负责解析LocalDate和LocalDateTime对象。第二个DateFormattingRegistrar将处理Date对象。

## 5. 在属性文件中配置日期时间

Spring还为我们提供了通过应用程序属性文件设置全局日期时间格式的选项。日期、日期时间和时间格式有三个单独的参数：

```properties
spring.mvc.format.date=yyyy-MM-dd
spring.mvc.format.date-time=yyyy-MM-dd HH:mm:ss
spring.mvc.format.time=HH:mm:ss
```

所有这些参数都可以用iso值替换。例如，将日期时间参数设置为：

```properties
spring.mvc.format.date-time=iso
```

将等于ISO-8601 格式：

```properties
spring.mvc.format.date-time=yyyy-MM-dd HH:mm:ss
```

## 6. 总结

在本文中，我们学习了如何在Spring MVC请求中接受日期参数。我们讨论了如何根据请求在全局范围内执行此操作。

我们还学习了如何创建我们自己的日期格式化模式。