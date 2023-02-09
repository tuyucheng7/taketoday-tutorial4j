## 一、概述

在这个简短的教程中，我们将了解如何 **在 Java 中验证 IPv4 地址**。

## 2. IPv4 验证规则

我们的有效 IPv4 地址采用*“xxxx”*形式，其中每个*x*是 0 <= *x* <= 255 范围内的数字，没有前导零，并由点分隔。

以下是一些有效的 IPv4 地址：

-   192.168.0.1
-   10.0.0.255
-   255.255.255.255

还有一些无效的：

-   192.168.0.256（值高于 255）
-   192.168.0（只有 3 个八位字节）
-   .192.168.0.1（以“.”开头）
-   192.168.0.01（有前导零）

## 3. 使用 Apache Commons Validator

我们可以使用[Apache Commons Validator库中的](https://search.maven.org/search?q=g:commons-validator a:commons-validator)*InetAddressValidator*类来验证我们的 IPv4 或 IPv6 地址。

让我们向我们的*pom.xml*文件添加一个依赖项：

```xml
<dependency>
    <groupId>commons-validator</groupId>
    <artifactId>commons-validator</artifactId>
    <version>1.7</version>
</dependency>复制
```

然后，我们只需要使用*InetAddressValidator*对象的*isValid()*方法：

```java
InetAddressValidator validator = InetAddressValidator.getInstance();
validator.isValid("127.0.0.1");复制
```

## 4.使用番石榴

或者，我们可以使用[Guava库中的](https://www.baeldung.com/guava-guide)*InetAddresses*类来实现相同的目标：

```java
InetAddresses.isInetAddress("127.0.0.1");复制
```

## 5.使用正则表达式

最后，我们还可以使用[Regex](https://www.baeldung.com/regular-expressions-java)：

```java
String regex = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";
Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(ip);
matcher.matches();复制
```

在这个正则表达式中，*((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b)*是一个组重复四次以匹配 IPv4 地址中的四个八位字节。以下匹配每个八位字节：

-   *25[0-5]* – 这匹配 250 到 255 之间的数字。
-   *(2[0-4]|1\\d|[1-9])* – 这匹配 200 – 249、100 – 199 和 1 – 9 之间的数字。
-   *\\d* – 这匹配任何数字 (0-9)。
-   *\\.?* – 这匹配可选的点 (.) 字符。
-   *\\b* – 这是一个单词边界。

因此，此正则表达式匹配 IPv4 地址。

## 六，结论

总之，我们学习**了在 Java 中验证 IPv4 地址的**不同方法。