## 1. 概述

JSON 对象中的字段名称可以采用多种格式。当我们想要将它们加载到 POJO 中时，我们可能会遇到Java代码中的属性名称与 JSON 中的命名约定不匹配的问题。

在这个简短的教程中，我们将了解如何使用[Jackson](https://www.baeldung.com/jackson)将 snake case JSON 反序列化为 camel case 字段。

## 2.安装杰克逊

让我们首先将[Jackson 依赖](https://search.maven.org/classic/#search|gav|1|g%3A"com.fasterxml.jackson.core" AND a%3A"jackson-databind")项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13</version>
</dependency>
```

## 3.使用默认反序列化

让我们考虑一个示例用户类：

```java
public class User {
    private String firstName;
    private String lastName;

    // standard getters and setters
}
```

让我们尝试加载此 JSON，它使用 Snake Case 命名标准(小写名称由_分隔)：

```json
{
    "first_name": "Jackie",
    "last_name": "Chan"
}
```

首先，我们需要使用ObjectMapper反序列化这个 JSON：

```java
ObjectMapper objectMapper = new ObjectMapper();
User user = objectMapper.readValue(JSON, User.class);

```

然而，当我们尝试这样做时，我们得到一个错误：

```plaintext
com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException: Unrecognized field "first_name" (class snakecase.cn.tuyucheng.taketoday.jackson.User), not marked as ignorable (2 known properties: "lastName", "firstName"])
```

不幸的是，Jackson 无法将 JSON 中的名称与User 中的字段名称完全匹配。

接下来，我们将学习三种方法来解决这个问题。

## 4.使用@JsonProperty注解

我们可以在类的字段上使用@JsonProperty注解将字段映射到 JSON 中的确切名称：

```java
public class UserWithPropertyNames {
    @JsonProperty("first_name")
    private String firstName;
    
    @JsonProperty("last_name")
    private String lastName;

    // standard getters and setters
}
```

现在我们可以将 JSON 反序列化为 UserWithPropertyNames：

```java
ObjectMapper objectMapper = new ObjectMapper();
UserWithPropertyNames user = objectMapper.readValue(JSON, UserWithPropertyNames.class);
assertEquals("Jackie", user.getFirstName());
assertEquals("Chan", user.getLastName());
```

## 5.使用@JsonNaming注解

接下来，我们可以在类上使用@JsonNaming注解，所有字段将使用 snake case 反序列化：

```java
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserWithSnakeStrategy {
    private String firstName;
    private String lastName;

    // standard getters and setters
}
```

然后再次反序列化我们的 JSON：

```java
ObjectMapper objectMapper = new ObjectMapper();
UserWithSnakeStrategy user = objectMapper.readValue(JSON, UserWithSnakeStrategy.class);
assertEquals("Jackie", user.getFirstName());
assertEquals("Chan", user.getLastName());
```

## 6.配置ObjectMapper

最后，我们可以使用ObjectMapper上的setPropertyNamingStrategy方法为所有序列化配置它：

```java
ObjectMapper objectMapper = new ObjectMapper()
  .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
User user = objectMapper.readValue(JSON, User.class);
assertEquals("Jackie", user.getFirstName());
assertEquals("Chan", user.getLastName());
```

如我们所见，我们现在可以将 JSON 反序列化为原始User对象，即使User类没有任何注解。

我们应该注意到还有其他命名约定(例如 kebab 大小写)，并且上述解决方案也适用于它们。

## 七. 总结

在本文中，我们看到了使用 Jackson 将 snake case JSON 反序列化为 camel case 字段的不同方法。

首先，我们明确命名了这些字段。然后我们在 POJO 本身上设置命名策略。

最后，我们向ObjectMapper添加了一个全局配置。