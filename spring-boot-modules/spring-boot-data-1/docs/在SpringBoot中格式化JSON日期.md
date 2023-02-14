## 1. 概述

在本教程中，我们将展示如何在Spring Boot应用程序中格式化JSON日期字段。

我们将探索使用[Jackson](https://www.baeldung.com/jackson)格式化日期的各种方法，Spring Boot将Jackson用作其默认的JSON处理器。

## 2. 在Date字段上使用@JsonFormat

### 2.1 设置格式

**我们可以使用[@JsonFormat](https://www.baeldung.com/jackson-jsonformat)注解来格式化特定字段**：

```java
public class Contact {

    // other fields

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate birthday;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdate;

    // standard getters and setters
}
```

在birthday字段上，我们使用仅呈现年月日的模式，而在lastUpdate字段上，我们还包含时分秒。

我们使用了[Java 8日期类型](https://www.baeldung.com/java-8-date-time-intro)，这对于处理时态类型非常方便。

当然，如果我们需要使用像java.util.Date这样的遗留类型，我们可以以同样的方式使用注解：

```java
public class ContactWithJavaUtilDate {

    // other fields

    @JsonFormat(pattern="yyyy-MM-dd")
    private Date birthday;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date lastUpdate;

    // standard getters and setters
}
```

最后，让我们看一下使用具有给定日期格式的@JsonFormat呈现的输出：

```json
{
    "birthday": "2022-09-05",
    "lastUpdate": "2022-09-05 10:08:02"
}
```

**如我们所见，使用@JsonFormat注解是格式化特定日期字段的绝佳方式**。

**但是，我们应该只在需要对字段进行特定格式设置时才使用它**。如果我们想为我们的应用程序中的所有日期使用通用格式，我们将在后面看到更好的方法来实现这一点。

### 2.2 设置时区

如果我们需要使用特定的时区，我们可以设置@JsonFormat的timezone属性：

```java
@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Shanghai")
private LocalDateTime lastUpdate;
```

如果类型已经包含时区，则我们不需要使用它，例如，使用java.time.ZonedDatetime。

## 3. 配置默认格式

虽然@JsonFormat本身很强大，但对格式和时区进行硬编码可能会让我们陷入困境。

**如果我们想为应用程序中的所有日期配置默认格式，更灵活的方法是在application.properties中进行配置**：

```properties
spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
```

如果我们想在JSON日期中使用特定时区，那么还有一个属性：

```properties
spring.jackson.time-zone=Asia/Shanghai
```

尽管像这样设置默认格式非常方便和直接，**但这种方法也有一个缺点。不幸的是，它不适用于LocalDate和LocalDateTime这样的Java 8日期类型**。我们只能使用它来格式化java.util.Date或java.util.Calendar类型的字段。不过，希望还是有的，我们很快就会看到。

## 4. 自定义Jackson的ObjectMapper

因此，如果我们想使用Java 8日期类型并设置默认日期格式，我们需要考虑**创建一个[Jackson2ObjectMapperBuilderCustomizer](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/jackson/Jackson2ObjectMapperBuilderCustomizer.html) bean**：

```java
@Configuration
public class ContactAppConfig {
    private static final String dateFormat = "yyyy-MM-dd";
    private static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    @Bean
    @ConditionalOnProperty(value = "spring.jackson.date-format", matchIfMissing = true, havingValue = "none")
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.simpleDateFormat(dateTimeFormat);
            builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)));
            builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimeFormat)));
        };
    }
}
```

上面的示例显示了如何在我们的应用程序中配置默认格式。我们必须定义一个bean并覆盖它的customize()方法来设置所需的格式。

虽然这种方法可能看起来有点麻烦，但好处是它同时适用于Java 8和遗留的日期类型。

## 5. 总结

在本文中，我们探讨了在Spring Boot应用程序中格式化JSON日期的多种方法。