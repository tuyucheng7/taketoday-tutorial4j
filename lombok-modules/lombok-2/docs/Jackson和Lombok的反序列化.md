## 一、概述

通常情况下，在使用[Project Lombok](https://projectlombok.org/)时，我们会希望将我们的数据相关类与 JSON 框架（如[Jackson](https://www.baeldung.com/jackson) ）相结合。鉴于 JSON 在大多数现代 API 和数据服务中广泛使用，这一点尤其正确。

在本快速教程中，我们将了解如何配置 Lombok[构建器类](https://www.baeldung.com/lombok-builder)以与 Jackson 无缝协作。

## 2.依赖关系

我们需要开始的是将[org.projectlombok](https://search.maven.org/classic/#search|ga|1|g%3A"org.projectlombok")添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.24</version>
</dependency>
```

当然，我们还需要[jackson-databind](https://search.maven.org/search?q=g:com.fasterxml.jackson.core)依赖项：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.14.1</version>
</dependency>
```

## 3. 一个简单的水果域

让我们继续定义一个启用 Lombok 的类，它有一个 id 和一个代表水果的名称：

```java
@Data
@Builder
@Jacksonized
public class Fruit {
    private String name;
    private int id;
}
```

让我们来看看 POJO 的关键注释：

-   首先，我们首先将@Data注释添加到我们的类中——这会生成通常与简单 POJO 关联的所有样板，例如 getter 和 setter
-   然后，我们添加[@Builder](http://baeldung.com/lombok-builder)[注释——一种使用Builder 模式](https://www.baeldung.com/creational-design-patterns#builder)创建对象的有用机制 
-   最后也是最重要的，我们添加@Jacksonized注解

简单地扩展一下， @Jacksonized注解是@Builder的附加注解。使用此注释可以让我们自动配置生成的构建器类以使用 Jackson 的反序列化。

重要的是要注意，此注释仅在同时存在@Builder或[@SuperBuilder](https://www.baeldung.com/lombok-builder-inheritance)注释时才有效。

最后，我们应该提一下，虽然@Jacksonized是在 Lombok v1.18.14 中引入的。它仍然被认为是一个[实验性的功能](https://projectlombok.org/features/experimental/)。

## 4.反序列化和序列化

现在定义了我们的域模型，让我们继续编写单元测试以使用 Jackson 反序列化水果：

```java
@Test
public void withFruitJSON_thenDeserializeSucessfully() throws IOException {
    String json = "{\"name\":\"Apple\",\"id\":101}";
        
    Fruit fruit = newObjectMapper().readValue(json, Fruit.class);
    assertEquals(new Fruit("Apple", 101), fruit);
}
```

ObjectMapper的简单readValue() API就足够了。我们可以使用它将 JSON 水果字符串反序列化为Fruit Java 对象。

同样，我们可以使用writeValue() API 将Fruit对象序列化为 JSON 输出：

```java
@Test
void withFruitObject_thenSerializeSucessfully() throws IOException {
    Fruit fruit = Fruit.builder()
      .id(101)
      .name("Apple")
      .build();

    String json = newObjectMapper().writeValueAsString(fruit);
    assertEquals("{\"name\":\"Apple\",\"id\":101}", json);
}
```

该测试展示了我们如何使用 Lombok 构建器 API 构建Fruit，以及序列化的 Java 对象与预期的 JSON 字符串匹配。

## 5. 使用定制的构建器

有时我们可能需要使用定制的构建器实现，而不是 Lombok 为我们生成的构建器实现。例如，当我们的 bean 的属性名称与 JSON 字符串中字段的名称不同时。

假设我们想要反序列化以下 JSON 字符串：

```json
{
    "id": 5,
    "name": "Bob"
}
```

但是我们的 POJO 上的属性不匹配：

```java
@Data
@Builder(builderClassName = "EmployeeBuilder")
@JsonDeserialize(builder = Employee.EmployeeBuilder.class)
@AllArgsConstructor
public class Employee {

    private int identity;
    private String firstName;

}
```

在这种情况下，我们可以将@JsonDeserialize注释与@JsonPOJOBuilder注释一起使用，我们可以将其插入到生成的构建器类中以覆盖 Jackson 的默认值：

```java
@JsonPOJOBuilder(buildMethodName = "createEmployee", withPrefix = "construct")
public static class EmployeeBuilder {

    private int idValue;
    private String nameValue;

    public EmployeeBuilder constructId(int id) {
        idValue = id;
        return this;
    }
            
    public EmployeeBuilder constructName(String name) {
        nameValue = name;
        return this;
    }

    public Employee createEmployee() {
        return new Employee(idValue, nameValue);
    }
}
```

然后我们可以像以前一样继续编写测试：

```java
@Test
public void withEmployeeJSON_thenDeserializeSucessfully() throws IOException {
    String json = "{\"id\":5,\"name\":\"Bob\"}";
    Employee employee = newObjectMapper().readValue(json, Employee.class);

    assertEquals(5, employee.getIdentity());
    assertEquals("Bob", employee.getFirstName());
}
```

结果表明，尽管属性名称不匹配，但已成功地从 JSON 源重新创建了一个新的Employee数据对象。

## 六，结论

在这篇简短的文章中，我们看到了两种简单的方法来配置我们的 Lombok[构建器类](https://www.baeldung.com/lombok-builder)以与 Jackson 无缝协作。

如果没有@Jacksonized注释，我们将不得不专门定制我们的构建器类。但是，使用@Jacksonized可以让我们使用 Lombok 生成的构建器类。