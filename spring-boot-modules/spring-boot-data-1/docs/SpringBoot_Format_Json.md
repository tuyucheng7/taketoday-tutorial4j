## 1. 概述

在本教程中，我们将展示如何在Spring Boot应用程序中格式化JSON日期字段。

我们将介绍使用Jackson格式化日期的各种方法，Spring Boot将Jackson用作其默认的JSON处理器。

## 2. 在Date字段上使用@JsonFormat

### 2.1 设置格式

我们可以使用@JsonFormat注解来格式化一个特定的字段：

```java

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contact {
    private String name;
    private String address;
    private String phone;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdate;
}
```

在birthday字段上，我们使用仅呈现年月日的模式，而在lastUpdate字段上，我们还包含时分秒。

我们使用了Java 8日期类型，这对于处理时间类型非常方便。

当然，如果我们需要使用java.util.Date等遗留类型，同样可以使用此注解：

```java

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactWithJavaUtilDate {
    private String name;
    private String address;
    private String phone;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdate;
}
```

最后，让我们看看使用给定日期格式的@JsonFormat呈现的输出：

```json
{
    "birthday": "2022-09-05",
    "lastUpdate": "2022-09-05 10:08:02"
}
```

**正如我们所见，使用@JsonFormat注解是格式化特定日期字段的绝佳方式**。

**但是，我们应该只在需要格式化特定的字段时使用它**。如果我们想为我们的应用程序中的所有日期提供一个通用格式，有更好的方法来实现这一点。

### 2.2 设置时区

如果我们需要使用特定的时区，我们可以设置@JsonFormat的timezone属性：

```
@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="Asia/Shanghai")
private LocalDateTime lastUpdate;
```

如果一个日期类型已经包含时区，我们就不需要使用它，例如java.time.ZonedDatetime。

## 3. 配置默认格式

虽然@JsonFormat本身就很强大，但对格式和时区进行硬编码可能不是最佳的选择。

**如果我们想为应用程序中的所有日期配置默认格式，更灵活的方法是在application.properties中进行配置**：

```properties
spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
```

如果我们想在JSON日期中使用特定的时区，可以使用另外一个属性：

```properties
spring.jackson.time-zone=Asia/Shanghai
```

尽管像这样设置默认格式非常方便和直接，但这种方法也有一个缺点，它不适用于类似LocalDate和LocalDateTime这样的Java 8日期类型。
我们只能使用它来格式化java.util.Date或java.util.Calendar类型的字段。不过，我们还是有相应的解决方案。

## 4. 自定义Jackson的ObjectMapper

因此，如果我们想使用Java 8日期类型并设置默认日期格式，我们需要创建Jackson2ObjectMapperBuilderCustomizer bean：

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

虽然这种方法看起来有点麻烦，但好处是它同时适用于Java 8和遗留的日期类型。

## 5. 总结

在本文中，我们介绍了在Spring Boot应用程序中格式化JSON日期的多种方法。