## 1. 概述

XML的优势之一是处理的可用性，包括XPath-它被定义为[W3C标准](https://www.w3.org/TR/xpath/)。对于JSON，出现了一个名为JSONPath的类似工具。

本教程将**介绍Jayway JsonPath**，它是[JSONPath规范](http://goessner.net/articles/JsonPath/)的Java实现。它描述了设置、语法、通用API和用例演示。

## 延伸阅读

### [Spring中的集成测试](https://www.baeldung.com/integration-testing-in-spring)

为Spring Web应用程序编写集成测试的快速指南。

[阅读更多](https://www.baeldung.com/integration-testing-in-spring)→

### [Spring MVC中的HttpMediaTypeNotAcceptableException](https://www.baeldung.com/spring-httpmediatypenotacceptable)

了解如何在Spring中处理HttpMediaTypeNotAcceptableException。

[阅读更多](https://www.baeldung.com/spring-httpmediatypenotacceptable)→

### [Spring REST API中的二进制数据格式](https://www.baeldung.com/spring-rest-api-with-binary-data-formats)

在本文中，我们探讨了如何配置Spring REST机制以利用我们用Kryo说明的二进制数据格式。此外，我们还展示了如何使用Google Protocol buffers支持多种数据格式。

[阅读更多](https://www.baeldung.com/spring-rest-api-with-binary-data-formats)→

## 2. 设置

要使用JsonPath，我们只需要在Maven pom中包含一个依赖项：

```xml
<dependency>
    <groupId>com.jayway.jsonpath</groupId>
    <artifactId>json-path</artifactId>
    <version>2.4.0</version>
</dependency>
```

## 3. 语法

我们将使用以下JSON结构来演示JsonPath的语法和API：

```json
{
    "tool":
    {
        "jsonpath":
        {
            "creator":
            {
                "name": "Jayway Inc.",
                "location":
                [
                    "Malmo",
                    "San Francisco",
                    "Helsingborg"
                ]
            }
        }
    },

    "book":
    [
        {
            "title": "Beginning JSON",
            "price": 49.99
        },

        {
            "title": "JSON at Work",
            "price": 29.99
        }
    ]
}
```

### 3.1 符号

JsonPath使用特殊的符号来表示JsonPath路径中的节点及其与相邻节点的连接。有两种风格的符号：点和括号。

以下两个路径都引用上述JSON文档中的同一个节点，它是creator节点location字段中的第三个元素，它是属于根节点下tool的jsonpath对象的子对象。

首先，我们将看到带有点符号的路径：

```jsonpath
$.tool.jsonpath.creator.location[2]
```

现在让我们看一下括号表示法：

```jsonpath
$['tool']['jsonpath']['creator']['location'][2]
```

美元符号($)代表根成员对象。

### 3.2 运算符

我们在JsonPath中有几个有用的运算符：

-   **根节点($)**表示JSON结构的根成员，无论它是对象还是数组。我们在上一小节中包含了使用示例。
-   **当前节点(@)**表示正在处理的节点。我们主要将它用作谓词输入表达式的一部分。假设我们正在处理上述JSON文档中的book数组；表达式book\[?(@.price==49.99)\]指的是该数组中的第一本书。
-   **通配符(*)**表示指定范围内的所有元素。例如，book[\*\]表示book数组中的所有节点。

### 3.3 函数和过滤器

JsonPath还有我们可以在路径末尾使用的函数来合成该路径的输出表达式：min()、max()、avg()、stddev()和length()。

最后，我们有过滤器。这些是布尔表达式，用于将返回的节点列表限制为仅调用方法需要的节点。

一些示例是相等性(==)、正则表达式匹配(=~)、包含(in)和检查是否为空(empty)。我们主要对谓词使用过滤器。

有关不同运算符、函数和过滤器的完整列表和详细说明，请参阅[JsonPath GitHub](https://github.com/jayway/JsonPath)项目。

## 4. 运算符

在我们开始介绍运算符之前，作为快速的旁注：本节使用我们之前定义的JSON示例结构。

### 4.1 访问文档

JsonPath有一种方便的方式来访问JSON文档。我们通过静态read API来执行此操作：

```java
<T> T JsonPath.read(String jsonString, String jsonPath, Predicate... filters);
```

read API可以与静态流式的API一起使用以提供更大的灵活性：

```java
<T> T JsonPath.parse(String jsonString).read(String jsonPath, Predicate... filters);
```

我们可以对不同类型的JSON源使用read的其他重载变体，包括Object、InputStream、URL和File。

为简单起见，这部分的测试不包括参数列表中的谓词(空可变参数)。但我们将在后面的小节中讨论谓词。

让我们首先定义两个要处理的示例路径：

```java
String jsonpathCreatorNamePath = "$['tool']['jsonpath']['creator']['name']";
String jsonpathCreatorLocationPath = "$['tool']['jsonpath']['creator']['location'][*]";
```

接下来，我们将通过解析给定的JSON源jsonDataSourceString来创建一个DocumentContext对象。然后，新创建的对象将用于使用上面定义的路径读取内容：

```java
DocumentContext jsonContext = JsonPath.parse(jsonDataSourceString);
String jsonpathCreatorName = jsonContext.read(jsonpathCreatorNamePath);
List<String> jsonpathCreatorLocation = jsonContext.read(jsonpathCreatorLocationPath);
```

第一个read API返回一个包含JsonPath创建者名称的字符串，而第二个read API返回其地址列表。

我们将使用JUnit Assert API来确认这些方法按预期工作：

```java
assertEquals("Jayway Inc.", jsonpathCreatorName);
assertThat(jsonpathCreatorLocation.toString(), containsString("Malmo"));
assertThat(jsonpathCreatorLocation.toString(), containsString("San Francisco"));
assertThat(jsonpathCreatorLocation.toString(), containsString("Helsingborg"));
```

### 4.2 谓词

现在我们已经掌握了基础知识，让我们定义一个新的JSON示例来处理并说明如何创建和使用谓词：

```json
{
    "book":
    [
        {
            "title": "Beginning JSON",
            "author": "Ben Smith",
            "price": 49.99
        },

        {
            "title": "JSON at Work",
            "author": "Tom Marrs",
            "price": 29.99
        },

        {
            "title": "Learn JSON in a DAY",
            "author": "Acodemy",
            "price": 8.99
        },

        {
            "title": "JSON: Questions and Answers",
            "author": "George Duckett",
            "price": 6.00
        }
    ],

    "price range":
    {
        "cheap": 10.00,
        "medium": 20.00
    }
}
```

谓词确定过滤器的true或false输入值，以将返回的列表缩小到仅匹配的对象或数组。通过将Predicate用作其静态工厂方法的参数，我们可以轻松地将Predicate集成到Filter中。然后可以使用该Filter从JSON字符串中读取请求的内容：

```java
Filter expensiveFilter = Filter.filter(Criteria.where("price").gt(20.00));
List<Map<String, Object>> expensive = JsonPath.parse(jsonDataSourceString)
    .read("$['book'][?]", expensiveFilter);
predicateUsageAssertionHelper(expensive);
```

我们还可以定义自定义的Predicate并将其用作read API的参数：

```java
Predicate expensivePredicate = new Predicate() {
    public boolean apply(PredicateContext context) {
        String value = context.item(Map.class).get("price").toString();
        return Float.valueOf(value) > 20.00;
    }
};

List<Map<String, Object>> expensive = JsonPath.parse(jsonDataSourceString)
    .read("$['book'][?]", expensivePredicate);
predicateUsageAssertionHelper(expensive);
```

最后，一个谓词可以直接应用于read API而不需要创建任何对象，这被称为内联谓词e：

```java
List<Map<String, Object>> expensive = JsonPath.parse(jsonDataSourceString)
    .read("$['book'][?(@['price'] > $['price range']['medium'])]");
predicateUsageAssertionHelper(expensive);
```

上面的所有三个Predicate示例都在以下断言辅助方法的帮助下进行了验证：

```java
private void predicateUsageAssertionHelper(List<?> predicate) {
    assertThat(predicate.toString(), containsString("Beginning JSON"));
    assertThat(predicate.toString(), containsString("JSON at Work"));
    assertThat(predicate.toString(), not(containsString("Learn JSON in a DAY")));
    assertThat(predicate.toString(), not(containsString("JSON: Questions and Answers")));
}
```

## 5. 配置

### 5.1 Option

Jayway JsonPath提供了几个选项来调整默认配置：

-   Option.AS_PATH_LIST返回评估命中的路径而不是它们的值
-   Option.DEFAULT_PATH_LEAF_TO_NULL为缺失的叶节点返回null
-   Option.ALWAYS_RETURN_LIST返回一个列表，即使路径是确定的
-   Option.SUPPRESS_EXCEPTIONS确保路径评估不会传播任何异常
-   Option.REQUIRE_PROPERTIES需要在评估不确定路径时在路径中定义的属性

以下是如何从头开始应用Option：

```java
Configuration configuration = Configuration.builder().options(Option.<OPTION>).build();
```

以及如何将其添加到现有配置中：

```java
Configuration newConfiguration = configuration.addOptions(Option.<OPTION>);
```

### 5.2 SPI

在Option的帮助下，JsonPath的默认配置应该足以满足大多数任务的需求。但是，具有更复杂用例的用户可以根据他们的特定要求修改JsonPath的行为-使用三种不同的SPI：

-   JsonProvider SPI允许我们改变JsonPath解析和处理JSON文档的方式
-   MappingProvider SPI允许自定义节点值和返回对象类型之间的绑定
-   CacheProvider SPI调整缓存路径的方式，这有助于提高性能

## 6. 示例用例

我们现在对JsonPath功能有了很好的理解。那么，让我们看一个例子。

本节说明如何处理从Web服务返回的JSON数据。

假设我们有一个返回以下结构的电影信息服务：

```json
[
    {
        "id": 1,
        "title": "Casino Royale",
        "director": "Martin Campbell",
        "starring":
        [
            "Daniel Craig",
            "Eva Green"
        ],
        "desc": "Twenty-first James Bond movie",
        "release date": 1163466000000,
        "box office": 594275385
    },

    {
        "id": 2,
        "title": "Quantum of Solace",
        "director": "Marc Forster",
        "starring":
        [
            "Daniel Craig",
            "Olga Kurylenko"
        ],
        "desc": "Twenty-second James Bond movie",
        "release date": 1225242000000,
        "box office": 591692078
    },

    {
        "id": 3,
        "title": "Skyfall",
        "director": "Sam Mendes",
        "starring":
        [
            "Daniel Craig",
            "Naomie Harris"
        ],
        "desc": "Twenty-third James Bond movie",
        "release date": 1350954000000,
        "box office": 1110526981
    },

    {
        "id": 4,
        "title": "Spectre",
        "director": "Sam Mendes",
        "starring":
        [
            "Daniel Craig",
            "Lea Seydoux"
        ],
        "desc": "Twenty-fourth James Bond movie",
        "release date": 1445821200000,
        "box office": 879376275
    }
]
```

其中release date字段的值是自纪元以来的毫秒数，而box office是电影在电影院的美元收入。

我们将处理与GET请求相关的五个不同工作场景，假设上述JSON层次结构已被提取并存储在名为jsonString的字符串变量中。

### 6.1 获取给定ID的对象数据

在此用例中，客户端通过向服务器提供电影的确切id来请求有关特定电影的详细信息。此示例演示服务器如何在返回给客户端之前查找请求的数据。

假设我们需要找到一个id等于2的记录。

第一步是选择正确的数据对象：

```java
Object dataObject = JsonPath.parse(jsonString).read("$[?(@.id == 2)]");
String dataString = dataObject.toString();
```

JUnit Assert API用于验证几个字段的存在：

```java
assertThat(dataString, containsString("2"));
assertThat(dataString, containsString("Quantum of Solace"));
assertThat(dataString, containsString("Twenty-second James Bond movie"));
```

### 6.2 获取主演为给定名称的电影

假设我们要查找由一位名叫Eva Green的女演员主演的电影。服务器需要返回starring数组中包含Eva Green的电影名称。

后续测试将说明如何执行此操作并验证返回的结果：

```java
@Test
public void givenStarring_whenRequestingMovieTitle_thenSucceed() {
    List<Map<String, Object>> dataList = JsonPath.parse(jsonString)
      .read("$[?('Eva Green' in @['starring'])]");
    String title = (String) dataList.get(0).get("title");

    assertEquals("Casino Royale", title);
}
```

### 6.3 总收入的计算

此场景使用名为length()的JsonPath函数来计算电影记录的数量，以便计算所有电影的总收入。

让我们看看实现和测试：

```java
@Test
public void givenCompleteStructure_whenCalculatingTotalRevenue_thenSucceed() {
    DocumentContext context = JsonPath.parse(jsonString);
    int length = context.read("$.length()");
    long revenue = 0;
    for (int i = 0; i < length; i++) {
        revenue += context.read("$[" + i + "]['box office']", Long.class);
    }

    assertEquals(594275385L + 591692078L + 1110526981L + 879376275L, revenue);
}
```

### 6.4 最高收入电影

此用例举例说明了使用非默认JsonPath配置选项(即Option.AS_PATH_LIST)来找出收入最高的电影。

首先，我们需要提取所有电影票房收入的列表。然后我们将其转换为数组进行排序：

```java
DocumentContext context = JsonPath.parse(jsonString);
List<Object> revenueList = context.read("$[*]['box office']");
Integer[] revenueArray = revenueList.toArray(new Integer[0]);
Arrays.sort(revenueArray);
```

我们可以很容易地从revenueArray排序数组中获取highestRevenue变量，然后用它来计算出收入最高的电影记录的路径：

```java
int highestRevenue = revenueArray[revenueArray.length - 1];
Configuration pathConfiguration = Configuration.builder().options(Option.AS_PATH_LIST).build();
List<String> pathList = JsonPath.using(pathConfiguration).parse(jsonString)
    .read("$[?(@['box office'] == " + highestRevenue + ")]");
```

根据计算出的路径，我们将确定并返回相应电影的title：

```java
Map<String, String> dataRecord = context.read(pathList.get(0));
String title = dataRecord.get("title");
```

整个过程通过Assert API验证：

```java
assertEquals("Skyfall", title);
```

### 6.5 导演的最新电影

此示例将说明如何计算出由名为Sam Mendes的导演执导的最后一部电影。

首先，我们创建一个由Sam Mendes执导的所有电影的列表：

```java
DocumentContext context = JsonPath.parse(jsonString);
List<Map<String, Object>> dataList = context.read("$[?(@.director == 'Sam Mendes')]");
```

然后我们使用该列表提取发布日期。这些日期将存储在一个数组中，然后进行排序：

```java
List<Object> dateList = new ArrayList<>();
for (Map<String, Object> item : dataList) {
    Object date = item.get("release date");
    dateList.add(date);
}

Long[] dateArray = dateList.toArray(new Long[0]);
Arrays.sort(dateArray);
```

我们使用latestTime变量(排序数组的最后一个元素)结合director字段的值来确定所请求电影的title：

```java
long latestTime = dateArray[dateArray.length - 1];
List<Map<String, Object>> finalDataList = context.read("$[?(@['director'] == 'Sam Mendes' && @['release date'] == " + latestTime + ")]");
String title = (String) finalDataList.get(0).get("title");
```

以下断言证明一切都按预期工作：

```java
assertEquals("Spectre", title);
```

## 7. 总结

本文介绍了Jayway JsonPath的基本功能，这是一个用于遍历和解析JSON文档的强大工具。

尽管JsonPath有一些缺点，例如缺少用于访问父节点或兄弟节点的运算符，但它在很多场景中都非常有用。