## 1. 概述

[Apache Camel](https://www.baeldung.com/apache-camel-intro)是一个强大的开源集成框架，实现了许多已知的[企业集成模式](https://www.baeldung.com/camel-integration-patterns)。

通常，在使用 Camel 处理消息路由时，我们会希望使用众多受支持的可插入[数据格式](https://camel.apache.org/manual/latest/data-format.html)之一。鉴于 JSON 在大多数现代 API 和数据服务中都很流行，它成为一个显而易见的选择。

在本教程中， 我们将了解使用camel-jackson组件将[JSON 数组](https://www.baeldung.com/jackson-collection-array)解组为Java对象列表的几种方法。

## 2.依赖关系

首先，让我们将[camel-jackson依赖](https://search.maven.org/classic/#search|ga|1|g%3Aorg.apache.camel a%3Acamel-jackson)项添加 到我们的 pom.xml中：

```xml
<dependency>
    <groupId>org.apache.camel</groupId>
    <artifactId>camel-jackson</artifactId>
    <version>3.6.0</version>
</dependency>
```

然后，我们还将专门为我们的单元测试添加camel-test依赖项，它也可以从[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3Aorg.apache.camel a%3Acamel-test)获得：

```xml
<dependency>
    <groupId>org.apache.camel</groupId>
    <artifactId>camel-test</artifactId>
    <version>3.6.0</version>
</dependency>
```

## 3.水果领域类

在本教程中，我们将使用几个轻型[POJO](https://www.baeldung.com/java-pojo-class)对象来为我们的水果域建模。

让我们继续定义一个带有 id 和 name 的类来表示水果：

```java
public class Fruit {

    private String name;
    private int id;

    // standard getter and setters
}
```

接下来，我们将定义一个容器来保存Fruit对象列表：

```java
public class FruitList {

    private List<Fruit> fruits;

    public List<Fruit> getFruits() {
        return fruits;
    }

    public void setFruits(List<Fruit> fruits) {
        this.fruits = fruits;
    }
}
```

在接下来的几节中，我们将了解如何将表示水果列表的 JSON 字符串解组到这些域类中。最终我们要寻找的是一个 我们可以使用的List<Fruit>类型的变量。

## 4. 解组一个 JSON FruitList

在第一个示例中，我们将使用 JSON 格式表示一个简单的水果列表：

```json
{
    "fruits": [
        {
            "id": 100,
            "name": "Banana"
        },
        {
            "id": 101,
            "name": "Apple"
        }
    ]
}
```

最重要的是，我们应该强调这个 JSON 表示一个包含名为fruits的属性的对象，该属性包含我们的数组。

现在让我们设置 Apache Camel[路由](https://www.baeldung.com/apache-camel-intro#domain-specific-language)来执行反序列化：

```java
@Override
protected RouteBuilder createRouteBuilder() throws Exception {
    return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
            from("direct:jsonInput")
              .unmarshal(new JacksonDataFormat(FruitList.class))
              .to("mock:marshalledObject");
        }
    };
}
```

在此示例中，我们使用名称为jsonInput的直接端点。接下来，我们调用unmarshal方法，它使用指定的数据格式对 Camel 交换器上的消息正文进行解组。

我们使用具有自定义解组类型FruitList的JacksonDataFormat类。这本质上是[Jackon ObjectMapper](https://www.baeldung.com/jackson-object-mapper-tutorial)的一个简单包装器，让我们可以编组到 JSON 或从 JSON 编组。

最后，我们将unmarshal方法的结果发送到一个名为marshalledObject的模拟端点。正如我们将要看到的，这就是我们测试路由以查看其是否正常工作的方式。

考虑到这一点，让我们继续编写我们的第一个单元测试：

```java
public class FruitListJacksonUnmarshalUnitTest extends CamelTestSupport {

    @Test
    public void givenJsonFruitList_whenUnmarshalled_thenSuccess() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:marshalledObject");
        mock.expectedMessageCount(1);
        mock.message(0).body().isInstanceOf(FruitList.class);

        String json = readJsonFromFile("/json/fruit-list.json");
        template.sendBody("direct:jsonInput", json);
        assertMockEndpointsSatisfied();

        FruitList fruitList = mock.getReceivedExchanges().get(0).getIn().getBody(FruitList.class);
        assertNotNull("Fruit lists should not be null", fruitList);

        List<Fruit> fruits = fruitList.getFruits();
        assertEquals("There should be two fruits", 2, fruits.size());

        Fruit fruit = fruits.get(0);
        assertEquals("Fruit name", "Banana", fruit.getName());
        assertEquals("Fruit id", 100, fruit.getId());

        fruit = fruits.get(1);
        assertEquals("Fruit name", "Apple", fruit.getName());
        assertEquals("Fruit id", 101, fruit.getId());
    }
}
```

让我们通过测试的关键部分来了解发生了什么：

-   首先，我们首先扩展CamelTestSupport类——一个有用的测试实用程序基类
-   然后我们设置我们的测试期望。我们的模拟变量应该有一条消息，消息类型应该是FruitList
-   现在我们准备好将 JSON 输入文件作为字符串发送到我们之前定义的直接端点
-   在我们检查我们的模拟期望已经满足之后，我们可以自由地检索FruitList并检查内容是否符合预期

此测试确认我们的路由正常工作并且我们的 JSON 正在按预期解组。惊人的！

## 5. 解组一个 JSON水果数组

另一方面，我们希望避免使用容器对象来保存我们的Fruit对象。我们可以修改我们的 JSON 来直接保存一个水果数组：

```json
[
    {
        "id": 100,
        "name": "Banana"
    },
    {
        "id": 101,
        "name": "Apple"
    }
]
```

这一次，我们的路线几乎完全相同，但我们将其设置为专门使用 JSON 数组：

```java
@Override
protected RouteBuilder createRouteBuilder() throws Exception {
    return new RouteBuilder() {
        @Override
        public void configure() throws Exception {
            from("direct:jsonInput")
              .unmarshal(new ListJacksonDataFormat(Fruit.class))
              .to("mock:marshalledObject");
        }
    };
}
```

如我们所见，与之前示例的唯一区别是我们使用的是带有自定义解组类型Fruit的ListJacksonDataFormat类。这是一种直接用于列表的 Jackson 数据格式类型。

同样，我们的单元测试非常相似：

```java
@Test
public void givenJsonFruitArray_whenUnmarshalled_thenSuccess() throws Exception {
    MockEndpoint mock = getMockEndpoint("mock:marshalledObject");
    mock.expectedMessageCount(1);
    mock.message(0).body().isInstanceOf(List.class);

    String json = readJsonFromFile("/json/fruit-array.json");
    template.sendBody("direct:jsonInput", json);
    assertMockEndpointsSatisfied();

    @SuppressWarnings("unchecked")
    List<Fruit> fruitList = mock.getReceivedExchanges().get(0).getIn().getBody(List.class);
    assertNotNull("Fruit lists should not be null", fruitList);

    // more standard assertions
}
```

但是，与我们在上一节中看到的测试有两个细微差别：

-   我们首先设置我们的模拟期望直接包含一个带有List.class的主体
-   当我们将消息正文检索为List.class时，我们将收到有关类型安全的标准警告——因此使用@SuppressWarnings(“unchecked”)

## 六. 总结

在这篇简短的文章中，我们看到了使用 camel 消息路由和camel-jackson组件解组 JSON 数组的两种简单方法。