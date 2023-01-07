## 1. 概述

[URL](https://www.baeldung.com/java-url)代表统一资源定位器，它是 Web 上唯一资源的地址。

在本教程中，我们将讨论使用Java验证 URL。在现代 Web 开发中，通过应用程序读取、写入或访问 URL 是很常见的。因此，成功的验证可确保 URL 有效且合规。

有不同的库用于验证 URL。我们将讨论两个类——来自 JDK 的[java.net.Url](https://developer.classpath.org/doc/java/net/URL.html) 和来自 Apache Commons 库的[org.apache.commons.validator.routines.UrlValidator 。](https://commons.apache.org/proper/commons-validator/apidocs/org/apache/commons/validator/routines/UrlValidator.html)

## 2. 使用 JDK 验证 URL

让我们看看如何使用java.net.URL类来验证 URL ：

```java
boolean isValidURL(String url) throws MalformedURLException, URISyntaxException {
    try {
        new URL(url).toURI();
        return true;
    } catch (MalformedURLException e) {
        return false;
    } catch (URISyntaxException e) {
        return false;
    }
}
```

在上面的方法中，new URL(url).toURI(); 尝试使用String参数创建URI。如果传递的String不符合 URL 语法，库将抛出Exception。

当内置URL类在输入String对象中发现格式错误的语法时，它会抛出MalformedURLException。当String的格式不兼容时，内置类会抛出URISyntaxException。

现在，让我们通过一个小测试来验证我们的方法是否有效：

```java
assertTrue(isValidURL("http://baeldung.com/"));
assertFalse(isValidURL("https://www.baeldung.com/ java-%%$^&& iuyi"));
```

[我们必须了解URL 和 URI](https://www.baeldung.com/java-url-vs-uri)之间的区别。toURI ()方法在这里很重要，因为它确保任何符合[RC 2396 的](https://datatracker.ietf.org/doc/html/rfc2396)URL 字符串都转换为URL。但是，如果我们使用new URL(String value)，则不能确保创建的 URL 完全合规。 

让我们看一个例子，如果我们只使用new URL(String url)，许多不兼容的 URL 将通过验证：

```java
boolean isValidUrl(String url) throws MalformedURLException {
    try {
        // it will check only for scheme and not null input 
        new URL(url);
        return true;
    } catch (MalformedURLException e) {
        return false;
    }
}

```

让我们看看上述方法如何通过一些测试来验证不同的 URL：

```java
assertTrue(isValidUrl("http://baeldung.com/"));
assertTrue(isValidUrl("https://www.baeldung.com/ java-%%$^&& iuyi")); 
assertFalse(isValidUrl(""));
```

在上述方法中，[new URL(url)](https://docs.oracle.com/javase/8/docs/api/java/net/URL.html#URL-java.lang.String-) 仅检查有效协议和空字符串作为输入。 因此，如果协议正确，它会返回一个URL对象，即使它不符合 RC 2396。

因此，我们必须使用new [URL(url).toURI()](https://docs.oracle.com/javase/8/docs/api/java/net/URL.html#toURI--)来确保 URL 有效且合规。

## 3. 使用 Apache Commons 验证 URL

我们需要将[commons-validator](https://mvnrepository.com/artifact/commons-validator/commons-validator)依赖导入到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>commons-validator</groupId>
    <artifactId>commons-validator</artifactId>
    <version>1.7</version>
</dependency>
```

让我们使用这个库中的UrlValidator类来验证：

```java
boolean isValidURL(String url) throws MalformedURLException {
    UrlValidator validator = new UrlValidator();
    return validator.isValid(url);
}
```

在上面的方法中，我们创建了一个URLValidator，然后使用isValid()方法来检查String参数的 URL 有效性。

让我们检查一下上述方法对不同输入的行为：

```java
assertFalse(isValidURL("https://www.baeldung.com/ java-%%$^&& iuyi"));
assertTrue(isValidURL("http://baeldung.com/"));
```

URLValidator允许我们微调条件以验证 URL 字符串。例如，如果我们使用重载构造函数UrlValidator(String[] schemes)，它只会针对提供的方案列表(http、https、ftp等)验证 URL。

同样，还有一些其他标志 - ALLOW_2_SLASHES、NO_FRAGMENT和ALLOW_ALL_SCHEMES可以根据要求进行设置。[我们可以在官方文档](https://commons.apache.org/proper/commons-validator/apidocs/org/apache/commons/validator/routines/UrlValidator.html)中找到库提供的所有选项的详细信息。

## 4。总结

在本文中，我们学习了两种不同的方法来验证 URL。我们还讨论了URL(String url)和URL.toURI()之间的区别。

如果我们只验证协议和非空字符串，那么我们可以使用URL(String url) constructor 。但是，当我们必须验证并通过合规性检查时，我们需要使用URL(url).to URI()。

此外，如果我们添加 Apache Commons 依赖项，我们可以使用URLValidator类来执行验证，并且该类中还有其他可用选项来微调验证规则。