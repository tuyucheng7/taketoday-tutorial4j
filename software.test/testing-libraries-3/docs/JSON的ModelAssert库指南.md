## 1. 概述

在为使用 JSON 的软件编写自动化测试时，我们经常需要将 JSON 数据与一些预期值进行比较。

在某些情况下，我们可以将实际和预期的 JSON 视为字符串并进行字符串比较，但这有很多限制。

在本教程中，我们将了解如何使用[ModelAssert](https://github.com/webcompere/model-assert)编写 JSON 值之间的断言和比较。我们将看到如何对 JSON 文档中的单个值构造断言以及如何比较文档。我们还将介绍如何处理无法预测其确切值的字段，例如日期或 GUID。

## 2. 入门

ModelAssert 是一个数据断言库，其语法类似于[AssertJ](https://www.baeldung.com/introduction-to-assertj) ，功能与[JSONAssert](https://www.baeldung.com/jsonassert)相当。它基于[Jackson](https://www.baeldung.com/jackson)进行 JSON 解析，并使用[JSON 指针](https://www.baeldung.com/json-pointer)表达式来描述文档中字段的路径。

让我们首先为这个 JSON 编写一些简单的断言：

```xml
{
   "name": "Baeldung",
   "isOnline": true,
   "topics": [ "Java", "Spring", "Kotlin", "Scala", "Linux" ]
}
```

### 2.1。依赖

首先，让我们将[ModelAssert](https://search.maven.org/classic/#search|gav|1|g%3A"uk.org.webcompere" AND a%3A"model-assert")添加到我们的 pom.xml中：

```java
<dependency>
    <groupId>uk.org.webcompere</groupId>
    <artifactId>model-assert</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

### 2.2. 在 JSON 对象中声明字段

假设示例 JSON 作为 字符串返回给我们，我们要检查 name字段是否等于Baeldung：

```java
assertJson(jsonString)
  .at("/name").isText("Baeldung");
```

assertJson 方法将从各种来源读取 JSON，包括String、 File、 Path和 Jackson 的 JsonNode。返回的对象是一个断言，我们可以在其上使用流利的 DSL(领域特定语言)来添加条件。

at方法描述了文档中我们希望进行字段断言的位置。 然后，isText指定我们需要一个值为Baeldung的文本节点。

我们可以 使用稍长的 JSON 指针表达式来断言主题数组中的路径：

```java
assertJson(jsonString)
  .at("/topics/1").isText("Spring");
```

虽然我们可以一个一个地编写字段断言，但我们也可以将它们组合成一个断言：

```java
assertJson(jsonString)
  .at("/name").isText("Baeldung")
  .at("/topics/1").isText("Spring");
```

### 2.3. 为什么字符串比较不起作用

通常我们希望将整个 JSON 文档与另一个文档进行比较。字符串比较虽然在某些情况下是可能的，但经常被不相关的 JSON 格式问题所吸引：

```java
String expected = loadFile(EXPECTED_JSON_PATH);
assertThat(jsonString)
  .isEqualTo(expected);
```

像这样的失败消息很常见：

```makefile
org.opentest4j.AssertionFailedError: 
expected: "{
    "name": "Baeldung",
    "isOnline": true,
    "topics": [ "Java", "Spring", "Kotlin", "Scala", "Linux" ]
}"
but was : "{"name": "Baeldung","isOnline": true,"topics": [ "Java", "Spring", "Kotlin", "Scala", "Linux" ]}"
```

### 2.4. 语义比较树

要进行整个文档比较，我们可以使用 isEqualTo：

```java
assertJson(jsonString)
  .isEqualTo(EXPECTED_JSON_PATH);
```

在这种情况下，实际 JSON 的字符串由assertJson加载，而预期的 JSON 文档——由Path描述的文件 ——被加载到 isEqualTo中。比较是根据数据进行的。

### 2.5. 不同的格式

ModelAssert 还支持可以被 Jackson 转换为JsonNode的Java 对象， 以及yaml格式。

```java
Map<String, String> map = new HashMap<>();
map.put("name", "baeldung");

assertJson(map)
  .isEqualToYaml("name: baeldung");
```

对于 yaml处理，isEqualToYaml方法用于指示字符串或文件的格式。如果源是yaml ，这需要assertYaml：

```java
assertYaml("name: baeldung")
  .isEqualTo(map);
```

## 3. 字段断言

到目前为止，我们已经看到了一些基本的断言。让我们看看更多的 DSL。

### 3.1。在任何节点上断言

ModelAssert 的 DSL 允许针对树中的任何节点添加几乎所有可能的条件。这是因为 JSON 树可能包含任何级别的任何类型的节点。

让我们看看我们可能添加到示例 JSON 的根节点的一些断言：

```java
assertJson(jsonString)
  .isNotNull()
  .isNotNumber()
  .isObject()
  .containsKey("name");
```

由于断言对象在其接口上提供了这些方法，我们的 IDE 将建议我们在按下“.”时可以添加的各种断言。钥匙。

在这个例子中，我们添加了很多不必要的条件，因为最后一个条件已经暗示了一个非空对象。

大多数情况下，我们使用来自根节点的 JSON 指针表达式来对树下层的节点执行断言：

```java
assertJson(jsonString)
  .at("/topics").hasSize(5);
```

此断言使用 hasSize来检查主题字段中的数组是否有五个元素。hasSize 方法对对象、数组和字符串进行操作。对象的大小是它的键数，字符串的大小是它的字符数，数组的大小是它的元素数。

我们需要对字段做出的大多数断言取决于字段的确切类型。当我们尝试在特定类型上编写断言时，我们可以使用方法number、 array、 text、 booleanNode和 object移动到更具体的断言子集。这是可选的，但可以更具表现力：

```java
assertJson(jsonString)
  .at("/isOnline").booleanNode().isTrue();
```

当我们按下“.” 在booleanNode之后键入我们的 IDE ，我们只能看到布尔节点的自动完成选项。

### 3.2. 文本节点

当我们断言文本节点时，我们可以使用 isText来使用精确值进行比较。或者，我们可以使用textContains来断言子字符串：

```java
assertJson(jsonString)
  .at("/name").textContains("ael");
```

我们还可以通过 匹配使用[正则表达式](https://www.baeldung.com/regular-expressions-java)：

```java
assertJson(jsonString)
  .at("/name").matches("[A-Z].+");
```

此示例断言 名称以大写字母开头。

### 3.3. 编号节点

对于数字节点，DSL 提供了一些有用的数字比较：

```java
assertJson("{count: 12}")
  .at("/count").isBetween(1, 25);
```

我们还可以指定我们期望的 Java 数字类型：

```java
assertJson("{height: 6.3}")
  .at("/height").isGreaterThanDouble(6.0);
```

isEqualTo方法是为整个树匹配保留的，所以为了比较数值相等，我们使用isNumberEqualTo：

```java
assertJson("{height: 6.3}")
  .at("/height").isNumberEqualTo(6.3);
```

### 3.4. 数组节点

我们可以使用isArrayContaining测试数组的内容 ：

```java
assertJson(jsonString)
  .at("/topics").isArrayContaining("Scala", "Spring");
```

这将测试给定值的存在并允许实际数组包含其他项目。如果我们希望断言更精确的匹配，我们可以使用isArrayContainingExactlyInAnyOrder：

```java
assertJson(jsonString)
   .at("/topics")
   .isArrayContainingExactlyInAnyOrder("Scala", "Spring", "Java", "Linux", "Kotlin");
```

我们也可以使这需要确切的顺序：

```java
assertJson(ACTUAL_JSON)
  .at("/topics")
  .isArrayContainingExactly("Java", "Spring", "Kotlin", "Scala", "Linux");
```

这是断言包含原始值的数组内容的好方法。如果数组包含对象，我们可能希望使用isEqualTo 来代替。

## 4.整棵树匹配

虽然我们可以构建具有多个特定于字段的条件的断言来检查 JSON 文档中的内容，但我们经常需要将整个文档与另一个文档进行比较。

isEqualTo方法(或 isNotEqualTo)用于比较整个树。 这可以与 at结合在进行比较之前移动到实际的子树：

```java
assertJson(jsonString)
  .at("/topics")
  .isEqualTo("[ "Java", "Spring", "Kotlin", "Scala", "Linux" ]");
```

当 JSON 包含以下数据时，整个树比较可能会遇到问题：

-   相同，但顺序不同
-   由一些无法预测的值组成

where方法用于自定义下一个isEqualTo 操作来绕过这些。

### 4.1。添加键顺序约束

让我们看两个看起来一样的 JSON 文档：

```java
String actualJson = "{a:{d:3, c:2, b:1}}";
String expectedJson = "{a:{b:1, c:2, d:3}}";
```

我们应该注意，这不是严格的 JSON 格式。ModelAssert 允许我们对 JSON 使用 JavaScript 表示法，以及通常引用字段名称的有线格式。

这两个文档在“a”下具有完全相同的键 ，但它们的顺序不同。这些断言将失败，因为ModelAssert 默认为 strict key order。

我们可以通过添加where配置来放宽 key order 规则 ：

```java
assertJson(actualJson)
  .where().keysInAnyOrder()
  .isEqualTo(expectedJson);
```

这允许树中的任何对象具有与预期文档不同的键顺序并且仍然匹配。

我们可以将此规则本地化到特定路径：

```java
assertJson(actualJson)
  .where()
    .at("/a").keysInAnyOrder()
  .isEqualTo(expectedJson);
```

这将 keysInAnyOrder限制为仅根对象中的“a”字段。

自定义比较规则的能力使我们能够处理无法完全控制或预测生成的确切文档的许多场景。

### 4.2. 放宽数组约束

如果我们有值顺序可以变化的数组，那么我们可以放宽整个比较的数组排序约束：

```java
String actualJson = "{a:[1, 2, 3, 4, 5]}";
String expectedJson = "{a:[5, 4, 3, 2, 1]}";

assertJson(actualJson)
  .where().arrayInAnyOrder()
  .isEqualTo(expectedJson);
```

或者我们可以将该约束限制为一条路径，就像我们对 keysInAnyOrder所做的那样。

### 4.3. 忽略路径

也许我们的实际文档包含一些无趣或不可预测的字段。我们可以添加一条规则来忽略该路径：

```java
String actualJson = "{user:{name: "Baeldung", url:"http://www.baeldung.com"}}";
String expectedJson = "{user:{name: "Baeldung"}}";

assertJson(actualJson)
  .where()
    .at("/user/url").isIgnored()
  .isEqualTo(expectedJson);
```

我们应该注意，我们所表达的路径总是根据实际.

实际中的额外字段“url”现在被忽略。

### 4.4. 忽略任何 GUID

到目前为止，我们只添加了使用at的规则，以便在文档中的特定位置自定义比较。

路径语法允许我们使用通配符来描述我们的规则适用于何处。当我们在比较的位置添加 at 或路径条件时，我们还 可以提供 上面的 任何字段断言来代替与预期文档的并排比较。

假设我们有一个 id字段出现在文档中的多个位置，并且是一个我们无法预测的 GUID。

我们可以使用路径规则忽略此字段：

```java
String actualJson = "{user:{credentials:[" +
  "{id:"a7dc2567-3340-4a3b-b1ab-9ce1778f265d",role:"Admin"}," +
  "{id:"09da84ba-19c2-4674-974f-fd5afff3a0e5",role:"Sales"}]}}";
String expectedJson = "{user:{credentials:" +
  "[{id:"???",role:"Admin"}," +
  "{id:"???",role:"Sales"}]}}";

assertJson(actualJson)
  .where()
    .path("user","credentials", ANY, "id").isIgnored()
  .isEqualTo(expectedJson);
```

在这里，我们的期望值可以是 id字段的任何值，因为我们只是忽略了 JSON 指针以“/user/credentials”开头然后有一个节点(数组索引)并以 “/id”结尾的任何字段。

### 4.5. 匹配任何 GUID

忽略我们无法预测的字段是一种选择。最好按类型匹配这些节点，也许还可以通过它们必须满足的其他条件来匹配。让我们切换到强制这些 GUID 匹配 GUID 的模式，让我们允许id节点出现在树的任何叶节点上：

```java
assertJson(actualJson)
  .where()
    .path(ANY_SUBTREE, "id").matches(GUID_PATTERN)
  .isEqualTo(expectedJson);
```

ANY_SUBTREE通配符匹配路径表达式部分之间的 任意数量的节点。GUID_PATTERN 来自 ModelAssert Patterns类，其中包含一些常用的正则表达式来匹配数字和日期戳等内容。

### 4.6. 自定义isEqualTo

where与 path或 at表达式的组合 允许我们覆盖树中任何位置的比较。我们要么为对象或数组匹配添加内置规则，要么指定特定的替代断言以用于比较中的单个或类路径。

如果我们有一个共同的配置，在各种比较中重复使用，我们可以将它提取到一个方法中：

```java
private static <T> WhereDsl<T> idsAreGuids(WhereDsl<T> where) {
    return where.path(ANY_SUBTREE, "id").matches(GUID_PATTERN);
}
```

然后，我们可以使用configureBy将该配置添加到特定断言中 ：

```java
assertJson(actualJson)
  .where()
    .configuredBy(where -> idsAreGuids(where))
  .isEqualTo(expectedJson);
```

## 5. 与其他库的兼容性

ModelAssert 是为互操作性而构建的。到目前为止，我们已经看到了 AssertJ 风格的断言。这些可以有多个条件，它们将在第一个不满足的条件下失败。

但是，有时我们需要生成一个匹配器对象以用于其他类型的测试。

### 5.1。Hamcrest 比赛

[Hamcrest](https://www.baeldung.com/java-junit-hamcrest-guide)是许多工具支持的主要断言助手库。我们可以使用 ModelAssert 的 DSL 来生成一个 Hamcrest 匹配器：

```java
Matcher<String> matcher = json()
  .at("/name").hasValue("Baeldung");
```

json方法用于描述一个匹配器，该匹配器将接受其中 包含 JSON 数据的字符串。我们还可以使用 jsonFile来生成一个 期望断言 File内容的Matcher。ModelAssert 中的 JsonAssertions类包含多个像这样的构建器方法来开始构建 Hamcrest 匹配器。

表示比较的 DSL 与 assertJson相同，但在使用匹配器之前不会执行比较。

因此，我们可以将 ModelAssert 与 Hamcrest 的MatcherAssert一起使用：

```java
MatcherAssert.assertThat(jsonString, json()
  .at("/name").hasValue("Baeldung")
  .at("/topics/1").isText("Spring"));
```

### 5.2. 使用 Spring Mock MVC

[在 Spring Mock MVC 中](https://www.baeldung.com/integration-testing-in-spring#2-verify-response-body)使用响应体验证时，我们可以使用 Spring 内置的jsonPath断言。然而，Spring 也允许我们使用[Hamcrest 匹配器来断言](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/web/servlet/result/ContentResultMatchers.html#string-org.hamcrest.Matcher-)作为响应内容返回的字符串。这意味着我们可以使用 ModelAssert 执行复杂的内容断言。

### 5.3. 与 Mockito 一起使用

Mockito 已经可以与 Hamcrest 互操作。然而，ModelAssert 也提供了一个原生的[ArgumentMatcher](https://www.baeldung.com/mockito-argument-matchers)。这既可以用来设置存根的行为，也可以用来验证对它们的调用：

```scss
public interface DataService {
    boolean isUserLoggedIn(String userDetails);
}

@Mock
private DataService mockDataService;

@Test
void givenUserIsOnline_thenIsLoggedIn() {
    given(mockDataService.isUserLoggedIn(argThat(json()
      .at("/isOnline").isTrue()
      .toArgumentMatcher())))
      .willReturn(true);

    assertThat(mockDataService.isUserLoggedIn(jsonString))
      .isTrue();

    verify(mockDataService)
      .isUserLoggedIn(argThat(json()
        .at("/name").isText("Baeldung")
        .toArgumentMatcher()));
}
```

在此示例中，Mockito argThat用于模拟和验证的设置。在其中，我们使用 Hamcrest 样式构建器作为匹配器 - json。然后我们为其添加条件，最后使用toArgumentMatcher转换为 Mockito 的ArgumentMatcher。

## 六，总结

在本文中，我们研究了在测试中比较 JSON 语义的必要性。

我们看到了如何使用 ModelAssert 在 JSON 文档中的单个节点以及整个树上构建断言。然后我们看到了如何自定义树比较以允许不可预测或不相关的差异。

最后，我们看到了如何将 ModelAssert 与 Hamcrest 和其他库一起使用。