## 1. 简介

当我们开发Web服务时，我们可能需要处理复杂的或意想不到的可以包含斜线的URL路径。因此，我们可能会遇到正在使用的Web服务器或框架的问题。

由于Spring提供的默认配置，在这方面特别棘手。

在本教程中，我们将展示一些在Spring中处理带有斜线的URL的常见解决方案和建议。我们还将了解为什么我们不应该使用一些常见的技巧来解决这些问题。继续阅读以了解更多信息！

## 2. 手动解析请求

在我们的Web服务中，有时我们需要将某个路径下的所有请求映射到同一个端点。更糟糕的是，我们可能不知道路径的其余部分会是什么样子。我们可能还需要以某种方式接收此路径作为参数，以便以后使用它。

假设我们可以在/mypaths下接收任何路径的请求：

```bash
http://localhost:8080/mypaths/any/custom/path
```

假设我们想将所有这些不同的路径存储在数据库中，以了解我们收到的请求。

我们可能想到的第一个解决方案是将路径的动态部分捕获到@PathVariable中：

```java
@GetMapping("mypaths/{anything}")
public String pathVariable(@PathVariable("anything") String anything) {
    return anything;
}
```

不幸的是，我们很快发现如果PathVariable包含斜杠，这将返回404。斜线字符是[URI标准](https://tools.ietf.org/html/rfc3986)路径定界符，它之后的所有内容都算作路径层次结构中的新级别。正如预期的那样，Spring 遵循此标准。

我们可以通过使用通配符为特定路径下的所有请求创建回退来轻松解决此问题：

```java
@GetMapping("all/**")
public String allDirectories(HttpServletRequest request) {
    return request.getRequestURI()
        .split(request.getContextPath() + "/all/")[1];
}
```

然后我们必须自己解析URI，以便获得我们感兴趣的路径部分。

当使用类似URL的参数时，此解决方案非常方便，但正如我们将在下一节中看到的那样，它对于其他一些情况还不够。

## 3. 使用查询参数

与我们之前的示例相反，在其他情况下，我们不仅映射不同的路径，而且还接收任何字符串作为URL中的参数。

让我们想象一下，在我们之前的示例中，我们使用包含连续斜杠的路径参数发出请求：

```bash
http://localhost:8080/all/http://myurl.com
```

起初，我们可能认为这应该可行，但我们很快意识到我们的控制器返回http:/myurl.com。发生这种情况是因为Spring Security标准化了URL并将任何双斜杠替换为单个斜杠。

Spring还规范化了URL中的其他序列，例如路径遍历。它采取这些预防措施来防止恶意URL绕过定义的安全约束，如官方[Spring Security文档](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#request-matching)中所述。

在这些情况下，强烈建议改用查询参数：

```java
@GetMapping("all")
public String queryParameter(@RequestParam("param") String param) {
    return param;
}
```

这样，我们可以不受这些安全限制地接收任何String参数，我们的Web服务将更加健壮和安全。

## 4. 避免变通

我们提出的解决方案可能意味着我们的映射设计发生了一些变化。这可能会诱使我们使用一些常见的解决方法来使我们的原始端点在接收URL中的斜线时正常工作。

最常见的解决方法可能是对路径参数中的斜杠进行编码。然而，过去曾报告过一些[安全漏洞](https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2007-0450)，大多数Web和应用程序服务器[对此作出反应](https://tomcat.apache.org/security-6.html#Fixed_in_Apache_Tomcat_6.0.10)，默认情况下不允许编码斜杠。仍然可以通过更改相应的设置来更改此行为，就像在[Tomcat](https://tomcat.apache.org/tomcat-9.0-doc/config/systemprops.html#Security)中一样。

[Apache Server](https://httpd.apache.org/docs/current/mod/core.html#allowencodedslashes)等其他人走得更远，引入了一个选项，允许编码斜杠而不对其进行解码，这样它们就不会被解释为路径定界符。在任何情况下，都不建议这样做，因为它可能会带来潜在的安全风险。

另一方面，Web框架也采取了一些预防措施。正如我们之前看到的，Spring添加了一些机制来防止不太严格的Servlet容器。所以，如果我们在我们的服务器中允许编码的斜杠，我们仍然必须在Spring中允许它们。

最后，还有其他类型的解决方法，例如更改Spring默认提供的URI规范化。和以前一样，如果我们更改这些默认值，我们应该非常谨慎。

## 5. 总结

在这篇简短的文章中，我们展示了一些在Spring中处理URL中的斜杠的解决方案。我们还介绍了一些安全问题，如果我们更改服务器或框架(如Spring)的默认配置，可能会出现这些问题。

根据经验，查询参数通常是处理URL中斜杠的最佳解决方案。