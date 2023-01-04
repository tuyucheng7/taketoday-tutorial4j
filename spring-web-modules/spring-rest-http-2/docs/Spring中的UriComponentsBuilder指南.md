## 1. 简介

在本教程中，我们将重点关注Spring UriComponentsBuilder。更具体地说，我们将描述各种实际的实施示例。

该构建器与UriComponents类一起工作，URI组件的不可变容器。

一个新的UriComponentsBuilder类通过提供对准备URI的所有方面的细粒度控制(包括构建、从模板变量扩展和编码)来帮助创建UriComponents实例。

## 2. Maven依赖

为了使用构建器，我们需要在pom.xml的依赖项中包含以下部分：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>5.2.2.RELEASE</version>
</dependency>
```

最新版本可以在[这里](https://search.maven.org/search?q=a:spring-web)找到。

此依赖项仅涵盖Spring Web，因此不要忘记为完整的Web应用程序添加spring-context。

当然，我们还需要为项目设置日志记录，更多内容请见[此处](https://www.baeldung.com/java-logging-intro)。

## 3. 用例

UriComponentsBuilder有许多实际用例，从相应URI组件中不允许的字符的上下文编码开始，到动态替换URL的部分结束。

UriComponentsBuilder的最大优点之一是我们可以将它直接注入到控制器方法中：

```java
@RequestMapping(method = RequestMethod.POST)
public ResponseEntity createCustomer(UriComponentsBuilder builder) {
    // implementation
}
```

让我们开始一个一个地描述有用的例子。我们将使用JUnit框架立即测试我们的实现。

### 3.1 构建URI

让我们从最简单的开始。我们只想使用UriComponentsBuilder来创建简单的链接：

```java
@Test
public void constructUri() {
    UriComponents uriComponents = UriComponentsBuilder.newInstance()
        .scheme("http").host("www.baeldung.com").path("/junit-5").build();

    assertEquals("/junit-5", uriComponents.toUriString());
}
```

我们可能会观察到，我们创建了UriComponentsBuilder的新实例，然后我们提供了方案类型、主机和到请求目的地的路径。

当我们想要执行重定向到我们网站的其他部分/链接时，这个简单的例子可能会有用。

### 3.2 构造一个编码的URI

除了创建一个简单的链接，我们可能还想对最终结果进行编码。让我们在实践中看看：

```java
@Test
public void constructUriEncoded() {
    UriComponents uriComponents = UriComponentsBuilder.newInstance()
        .scheme("http").host("www.baeldung.com").path("/junit 5").build().encode();

    assertEquals("/junit%205", uriComponents.toUriString());
}
```

此示例的不同之处在于我们要在单词junit和数字5之间添加空格。根据[RFC 3986](https://www.ietf.org/rfc/rfc3986.txt)，这是不可能的。我们需要使用encode()方法对链接进行编码以获得有效结果。

### 3.3 从模板构建URI

URI的大部分组件都允许使用URI模板，但它们的值仅限于特定元素，我们将其表示为模板。让我们看这个例子来澄清：

```java
@Test
public void constructUriFromTemplate() {
    UriComponents uriComponents = UriComponentsBuilder.newInstance()
      .scheme("http").host("www.baeldung.com").path("/{article-name}")
      .buildAndExpand("junit-5");

    assertEquals("/junit-5", uriComponents.toUriString());
}
```

此示例中的不同之处在于我们声明路径的方式以及我们构建最终URI的方式。将被关键字替换的模板在path()方法中用括号-{...}表示。用于生成最终链接的关键字在名为buildAndExpand(...)的方法中使用。

请注意，要替换的关键字可能不止一个。此外，URI的路径可以是相对的。

当我们想将模型对象传递给我们将基于其构建URI的Spring控制器时，此示例将非常有用。

### 3.4 使用查询参数构建URI

另一个非常有用的案例是使用查询参数创建URI。

我们需要使用UriComponentsBuilder中的query()来指定URI查询参数。让我们看下面的例子：

```java
@Test
public void constructUriWithQueryParameter() {
    UriComponents uriComponents = UriComponentsBuilder.newInstance()
        .scheme("http").host("www.google.com")
        .path("/").query("q={keyword}").buildAndExpand("baeldung");

     assertEquals("http://www.google.com/?q=baeldung", uriComponents.toUriString());
}
```

查询将添加到链接的主要部分。我们可以使用方括号{...}提供多个查询参数。它们将被名为buildAndExpand(...)的方法中的关键字替换。

UriComponentsBuilder的此实现可能用于构建(例如)REST API的查询语言。

### 3.5 使用正则表达式扩展URI

最后一个示例展示了使用正则表达式验证构建URI。仅当正则表达式验证成功时，我们才能扩展uriComponents ：

```java
@Test
public void expandWithRegexVar() {
    String template = "/myurl/{name:[a-z]{1,5}}/show";
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(template)
        .build();
    uriComponents = uriComponents.expand(Collections.singletonMap("name", "test"));
 
    assertEquals("/myurl/test/show", uriComponents.getPath());
}
```

在上面提到的例子中，我们可以看到链接的中间部分只需要a-z的字母和1-5之间的长度。

此外，我们使用singletonMap将关键字名称替换为值test。

当我们允许用户动态指定链接时，此示例特别有用，但我们希望提供一种安全性，只有有效链接才能在我们的Web应用程序中工作。

## 4. 总结

本教程介绍了UriComponentsBuilder的有用示例。

UriComponentsBuilder的主要优点是使用URI模板变量的灵活性，以及将其直接注入Spring Controller方法的可能性。