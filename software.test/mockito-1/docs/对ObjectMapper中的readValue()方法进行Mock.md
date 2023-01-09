## 1. 概述

在对涉及使用Jackson反序列化JSON的代码进行单元测试时，我们可能会发现mock ObjectMapper#readValue方法更容易。
通过这样做，我们不需要在测试中指定很长的JSON输入。

在本教程中，我们将了解如何使用Mockito实现这一点。

## 2. maven依赖

```text

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>4.0.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.0</version>
    <type>bundle</type>
</dependency>
```

## 3. 一个ObjectMapper例子

我们考虑一个简单的Flower类：

```java

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flower {

    private String name;
    private Integer petals;
}
```

假设我们有一个类用于验证Flower对象的JSON字符串表示。它将ObjectMapper作为构造函数参数 - 这使得我们稍后可以轻松地mock它：

```java
public class FlowerJsonStringValidator {
    private final ObjectMapper objectMapper;

    public FlowerJsonStringValidator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public boolean flowerHasPetals(String jsonFlowerAsString) throws JsonProcessingException {
        Flower flower = objectMapper.readValue(jsonFlowerAsString, Flower.class);
        return flower.getPetals() > 0;
    }
}
```

接下来，我们将使用Mockito为验证器逻辑编写单元测试。

## 4. 测试

首先创建我们的测试类。我们可以简单地mock ObjectMapper并将其作为构造函数参数传递给我们的FlowerStringValidator类：

```java

@ExtendWith(MockitoExtension.class)
class FlowerJsonStringValidatorUnitTest {

    @Mock
    private ObjectMapper objectMapper;

    private FlowerJsonStringValidator flowerJsonStringValidator;

    @BeforeEach
    void setUp() {
        flowerJsonStringValidator = new FlowerJsonStringValidator(objectMapper);
    }
}
```

注意，我们在测试中使用的是JUnit 5，因此我们使用@ExtendWith(MockitoExtension.class)标注了我们的测试类。

现在我们让我们编写一个简单的测试方法：

```java
class FlowerJsonStringValidatorUnitTest {

    @Test
    void whenCallingHasPetalsWithPetals_thenReturnsTrue() throws JsonProcessingException {
        Flower rose = new Flower("testFlower", 100);

        when(objectMapper.readValue(anyString(), eq(Flower.class))).thenReturn(rose);

        assertTrue(flowerJsonStringValidator.flowerHasPetals("this can be a very long json flower"));

        verify(objectMapper, times(1)).readValue(anyString(), eq(Flower.class));
    }
}
```

由于我们在这里mock ObjectMapper，所以我们可以忽略它的输入并专注于它的输出，然后将其传递给实际的验证器逻辑。
正如我们所看到的，我们不需要指定有效的JSON输入，这在实际场景中可能非常长而且难以编写。

## 5. 总结

在本文中，我们看到了如何mock ObjectMapper以围绕它提供高效的测试用例。