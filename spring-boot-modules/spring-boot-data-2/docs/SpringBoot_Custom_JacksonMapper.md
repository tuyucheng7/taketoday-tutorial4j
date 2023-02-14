## 1. 概述

当使用JSON格式的数据时，Spring Boot将使用ObjectMapper实例来序列化响应和反序列化请求。

在本教程中，我们将了解配置序列化和反序列化选项的最常用方法。

## 2. 默认配置

默认情况下，Spring Boot配置会禁用以下功能：

+ MapperFeature.DEFAULT_VIEW_INCLUSION
+ DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
+ SerializationFeature.WRITE_DATES_AS_TIMESTAMPS

让我们从一个简单的例子开始：

+ 客户端向我们的/coffee?name=Lavazza端点发送一个GET请求。
+ 控制器将返回一个新的Coffee对象。
+ Spring将使用ObjectMapper将我们的POJO序列化为JSON。

我们将通过使用String和LocalDateTime对象来举例说明自定义配置：

```java

@Data
public class Coffee {
    private String name;
    private String brand;
    private LocalDateTime date;
}
```

并且定义一个简单的REST控制器来演示序列化：

```java

@RestController
public class CoffeeController {

    @GetMapping("/coffee")
    public Coffee getCoffee(@RequestParam(required = false) String brand,
                            @RequestParam(required = false) String name) {
        return new Coffee().setBrand(brand).setDate(FIXED_DATE).setName(name);
    }
}
```

默认情况下，这将是调用GET http://lolcahost:8080/coffee?brand=Lavazza时的响应：

```json
{
    "name": null,
    "brand": "Lavazza",
    "date": "2022-10-16T10:21:35.974"
}
```

假设我们希望排除空值，并使用自定义日期格式(yyyy-MM-dd HH:mm)。下面是我们想要的响应内容：

```json
{
    "brand": "Lavazza",
    "date": "2022-10-16 10:21"
}
```

使用Spring Boot时，我们可以选择自定义默认ObjectMapper或覆盖它。

## 3. 自定义默认的ObjectMapper

在本节中，我们将了解如何自定义Spring Boot使用的默认ObjectMapper。

### 3.1 配置文件和自定义Jackson Module

**配置映射器的最简单方法是通过配置文件属性**。

下面是在Spring Boot中配置Jackson的一般结构：

```text
spring.jackson.<category_name>.<feature_name>=true,false
```

例如，我们可以添加以下内容以禁用SerializationFeature.WRITE_DATES_AS_TIMESTAMPS：

```properties
spring.jackson.serialization.write-dates-as-timestamps=false
```

除了上述属性类别外，我们还可以配置property-inclusion：

```properties
spring.jackson.default-property-inclusion=always, non_null, non_absent, non_default, non_empty
```

配置属性是最简单的方法。**这种方法的缺点是我们无法自定义高级选项，例如为LocalDateTime自定义日期格式**。

此时，我们会得到这样的结果：

```json
{
    "brand": "Lavazza",
    "date": "2022-10-16T13:39:56.1758063"
}
```

为了实现我们的目标，我们将使用自定义日期格式注册一个新的JavaTimeModule：

```java

@Configuration
@PropertySource("classpath:coffee.properties")
public class CoffeeRegisterModuleConfig {

    @Bean
    public Module javaTimeModule() {
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LOCAL_DATETIME_SERIALIZER);
        return module;
    }
}
```

此外，coffee.properties文件包含以下属性配置：

```properties
spring.jackson.default-property-inclusion=non_null
```

Spring Boot会自动注册任何类型为com.fasterxml.jackson.databind.Module的bean。下面是我们的最终结果：

```json
{
    "brand": "Lavazza",
    "date": "10-16-2022 13:43"
}
```

### 3.2 Jackson2ObjectMapperBuilderCustomizer

这个函数式接口的目的是允许我们创建配置bean。

它们将应用于通过Jackson2ObjectMapperBuilder创建的默认ObjectMapper：

```java

@Configuration
public class CoffeeCustomizerConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.serializationInclusion(JsonInclude.Include.NON_NULL).serializers(CoffeeConstants.LOCAL_DATETIME_SERIALIZER);
    }
}
```

**配置bean以特定的顺序应用，我们可以使用@Order注解进行控制**。如果我们想从不同的配置或模块配置ObjectMapper，这种优雅的方法是合适的。

## 4. 覆盖默认配置

如果我们想完全控制配置，**有几个选项可以禁用自动配置并只允许应用我们的自定义配置**。

### 4.1 ObjectMapper

覆盖默认配置的最简单方法是定义一个ObjectMapper bean并将其标记为@Primary：

```java

@Configuration
public class CoffeeObjectMapperConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LOCAL_DATETIME_SERIALIZER);
        return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).registerModule(module);
    }
}
```

**当我们想要完全控制序列化过程并且不希望允许外部配置时，我们应该使用这种方法**。

### 4.2 Jackson2ObjectMapperBuilder

另一种简单的方法是定义一个Jackson2ObjectMapperBuilder bean。

Spring Boot实际上在构建ObjectMapper时默认使用这个构建器，并将自动获取定义的对象：

```java

@Configuration
public class CoffeeJacksonBuilderConfig {

    @Bean
    @Primary
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder()
                .serializers(LOCAL_DATETIME_SERIALIZER)
                .serializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
```

默认情况下，它将配置两个选项：

+ 禁用MapperFeature.DEFAULT_VIEW_INCLUSION
+ 禁用DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES

根据Jackson2ObjectMapperBuilder文档，如果某些模块出现在类路径上，它也会注册这些模块：

+ jackson-datatype-jdk8：支持其他Java 8类型，如Optional。
+ jackson-datatype-jsr310：支持Java 8日期和时间API类型。
+ jackson-datatype-joda：支持Joda-Time类型。

**这种方法的优点是，Jackson2ObjectMapperBuilder提供了一种简单直观的方式来构建ObjectMapper**。

### 4.3 MappingJackson2HttpMessageConverter

我们可以定义一个MappingJackson2HttpMessageConverter类型的 bean，Spring Boot会自动使用它：:

```java

@Configuration
public class CoffeeHttpConverterConfiguration {

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                .serializers(LOCAL_DATETIME_SERIALIZER)
                .serializationInclusion(JsonInclude.Include.NON_NULL);
        return new MappingJackson2HttpMessageConverter(builder.build());
    }
}
```

## 5. 测试配置

为了测试我们的配置，我们将使用TestRestTemplate并将对象序列化为String。

通过这种方式，我们可以验证我们的Coffee对象是否序列化为不包含空值并且使用自定义日期格式：

```java

@SpringBootTest(classes = JacksonApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractCoffeeIntegrationTest {

    @Autowired
    protected TestRestTemplate restTemplate;

    @Test
    void whenGetCoffee_thenSerializedWithDateAndNonNull() {
        String formattedDate = DateTimeFormatter.ofPattern(CoffeeConstants.DATETIME_FORMAT).format(FIXED_DATE);

        String brand = "Lavazza";
        String url = "/coffee?brand=" + brand;

        String response = restTemplate.getForObject(url, String.class);

        assertThat(response).isEqualTo("{\"brand\":\"" + brand + "\",\"date\":\"" + formattedDate + "\"}");
    }
}
```

## 6. 总结

在本文中，我们了解了在使用Spring Boot时配置JSON序列化选项的几种方法。

我们介绍了两种不同的方法：配置默认选项或覆盖默认配置。