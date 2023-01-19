## 一、简介

在本文中，我们将描述并查看有关如何在[Groovy](https://www.baeldung.com/groovy-language)应用程序中使用 JSON 的示例。

首先，要启动并运行本文的示例，我们需要设置pom.xml：

```xml
<build>
    <plugins>
        // ...
        <plugin>
            <groupId>org.codehaus.gmavenplus</groupId>
            <artifactId>gmavenplus-plugin</artifactId>
            <version>1.6</version>
        </plugin>
    </plugins>
</build>
<dependencies>
    // ...
    <dependency>
        <groupId>org.codehaus.groovy</groupId>
        <artifactId>groovy-all</artifactId>
        <version>2.4.13</version>
    </dependency>
</dependencies>
```

最新的 Maven 插件可以在[这里](https://mvnrepository.com/artifact/org.codehaus.gmavenplus/gmavenplus-plugin)找到，最新版本的groovy-all可以 [在这里找到](https://mvnrepository.com/artifact/org.codehaus.groovy/groovy-all)。

## 2. 将Groovy对象解析为 JSON

在Groovy中将对象转换为 JSON 非常简单，假设我们有一个Account类：

```groovy
class Account {
    String id
    BigDecimal value
    Date createdAt
}
```

要将该类的实例转换为 JSON字符串，我们需要使用JsonOutput类并调用静态方法toJson()：

```groovy
Account account = new Account(
    id: '123', 
    value: 15.6,
    createdAt: new SimpleDateFormat('MM/dd/yyyy').parse('01/01/2018')
) 
println JsonOutput.toJson(account)
```

结果，我们将得到解析后的 JSON字符串：

```javascript
{"value":15.6,"createdAt":"2018-01-01T02:00:00+0000","id":"123"}
```

### 2.1. 自定义 JSON 输出

如我们所见，日期输出不是我们想要的。为此，从 2.5 版开始，包groovy.json附带了一组专用工具。

使用JsonGenerator类，我们可以为 JSON 输出定义选项：

```groovy
JsonGenerator generator = new JsonGenerator.Options()
  .dateFormat('MM/dd/yyyy')
  .excludeFieldsByName('value')
  .build()

println generator.toJson(account)
```

结果，我们将获得格式化的 JSON，不包含我们排除的值字段和格式化的日期：

```javascript
{"createdAt":"01/01/2018","id":"123"}
```

### 2.2. 格式化 JSON 输出

通过上面的方法，我们看到 JSON 输出总是在一行中，如果必须处理更复杂的对象，它会变得混乱。

但是，我们可以使用prettyPrint方法格式化我们的输出：

```groovy
String json = generator.toJson(account)
println JsonOutput.prettyPrint(json)
```

我们得到格式化的 JSON 波纹管：

```javascript
{
    "value": 15.6,
    "createdAt": "01/01/2018",
    "id": "123"
}
```

## 3. 将 JSON 解析为Groovy对象

我们将使用Groovy类JsonSlurper将 JSON 转换为对象。

此外，对于JsonSlurper ，我们有一堆重载的解析方法和一些特定的方法，如parseText、parseFile等。

我们将使用parseText将String解析为Account类：

```groovy
def jsonSlurper = new JsonSlurper()

def account = jsonSlurper.parseText('{"id":"123", "value":15.6 }') as Account
```

在上面的代码中，我们有一个方法接收一个 JSON字符串并返回一个Account对象，它可以是任何Groovy对象。

此外，我们可以将 JSON String解析为Map，调用它而不进行任何转换，并且使用Groovy动态类型，我们可以拥有与对象相同的内容。

### 3.1. 解析 JSON 输入

JsonSlurper的默认解析器实现是JsonParserType.CHAR_BUFFER，但在某些情况下，我们需要处理解析问题。

让我们看一个例子：给定一个带有日期属性的 JSON字符串， JsonSlurper将无法正确创建对象，因为它会尝试将日期解析为字符串：

```groovy
def jsonSlurper = new JsonSlurper()
def account 
  = jsonSlurper.parseText('{"id":"123","createdAt":"2018-01-01T02:00:00+0000"}') as Account
```

因此，上面的代码将返回一个Account对象，其所有属性都包含空值。

要解决该问题，我们可以使用JsonParserType.INDEX_OVERLAY。

因此，它将尽可能避免创建String或 char 数组：

```groovy
def jsonSlurper = new JsonSlurper(type: JsonParserType.INDEX_OVERLAY)
def account 
  = jsonSlurper.parseText('{"id":"123","createdAt":"2018-01-01T02:00:00+0000"}') as Account
```

现在，上面的代码将返回一个适当创建的Account实例。

### 3.2. 解析器变体

此外，在JsonParserType 内部，我们还有一些其他实现：

-   JsonParserType.LAX将允许更轻松的 JSON 解析，包括注解、无引号字符串等。
-   JsonParserType.CHARACTER_SOURCE用于大文件解析。

## 4。总结

我们已经通过几个简单示例介绍了Groovy应用程序中的大量 JSON 处理。

有关groovy.json包类的更多信息，我们可以查看[Groovy 文档](http://groovy-lang.org/json.html)。