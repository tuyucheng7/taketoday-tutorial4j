## 什么是 REST 保证？

Rest-Assured 是一个基于 Java 的开源库，可用于测试和验证[***REST API***](https://www.toolsqa.com/rest-assured/rest-api-end-to-end-test/)或 RESTFul Web 服务。它就像一个访问和测试 REST Web 服务的无头（无 GUI）客户端。它由[***Johan Haleby***](https://code.haleby.se/sample-page/)在其他几位贡献者的支持下开发和维护。

放心简化了基于 REST 的服务的测试。它带来了像 Groovy 和 Ruby 这样在 Java 中执行 API 测试的动态语言的简单性。该库支持任何 HTTP 方法，并且还明确支持 GET、POST、PUT、DELETE、OPTIONS 和 HEAD。它还包括规范和验证，如参数、标头、cookie 等。我们还可以使用它来验证和验证 HTTP 请求的响应。

![放心](https://toolsqa.com/gallery/Rest%20Assured/1.Rest-Assured.png)

[***资源***](https://devqa.io/)

除了测试基于 JSON 的 Web 服务外，Rest Assured 还可用于测试基于 XML 的 Web 服务。我们还可以将此库与[***JUnit***](https://www.toolsqa.com/java/junit-framework/junit-introduction/)和[***TestNG***](https://www.toolsqa.com/testng/testng-tutorial/)框架集成，并为应用程序编写测试用例。此外，它可以与[***Maven***](https://toolsqa.com/maven/maven-introduction/)很好地集成，并且其高效的匹配技术会产生直接的结果。

REST 保证的另一个强大功能是它支持 XML 路径和 JSON 路径语法来检查响应数据的特定元素，类似于使用 XPath API。对于这些概念的新手，请参考以下语法示例。

XPath 语法：

```java
<?xml version="1.0" encoding="UTF-8"?>
<bookstore>
<book>
  <title>Freedom In Exile</title>
  <price>14.29</price>
</book>
</bookstore>
```

JSON路径语法：

```java
$['store']['book'][3]['title']
```

除了上述所有功能之外，这个奇妙的库还提供了各种其他功能，例如类似***DSL 的语法、规范重用、XPath-Validation、简单的文件上传***，以及所有有利于自动化测试的功能。

## 为什么我们需要放心？

上面关于这个 java 库的讨论让我们相信它是一个可靠的库。但是我们为什么需要它或者在我们的应用程序测试中使用它的原因是什么？

以下是我们需要放心的主要原因：

1.  *它是一个开源库，拥有活跃的开发社区，使其成为 API 自动化的绝佳选择。*
2.  *早些时候，我们不得不使用 Ruby、Groovy 等动态语言进行 API 测试，这非常具有挑战性。*
3.  *REST 服务的验证和测试在 Java 中更难。使用 REST Assured，它变得更加简单和容易。*
4.  *该库使用 Java，因此使用基本 Java 代码发送带有自定义的 HTTPS 请求变得简单。一旦我们了解了 API 和[***集成测试***](https://www.toolsqa.com/software-testing/integration-testing)的基础知识，使用 Rest Assured 的自动化就会对后端充满信心。因此，我们可以更多地关注前端测试。*

## 最新版本

以下是我们通常在应用程序中使用的一些 Rest Assured 库版本。

1.  ***io.rest-assured*** -*这是一个用于轻松测试 REST 服务的 Java DSL。最后一次发布：2020 年 12 月 11 日（**4.3.3**）。*
2.  ***com.jayway.restassured*** -*这是一个遗留库，用于使用 Java DSL 轻松测试 REST 服务。最后发布：2016 年 3 月 4 日。*
3.  ***ddf.thirdparty*** -*这是一个第三方库。最后发布：2021 年 4 月 9 日*

## 放心的优势

下表列出了该库的一些优点。

| 编号 | 优点                                                         |
| ---- | ------------------------------------------------------------ |
| 1    | 它是开源的，因此可以免费使用。                               |
| 2    | 它的语法和现成的断言非常丰富。与 Apache HTTP 客户端相比，Rest Assured 需要更少的编码。 |
| 3    | Rest Assured 的设置简单明了。                                |
| 4    | 响应以 JSON 或 XML 格式给出，易于解析和验证。                |
| 5    | 它使用内置的[***Hemcrest 匹配器***](https://en.wikipedia.org/wiki/Hamcrest)来轻松提取值。 |
| 6    | 响应时间和状态代码的断言一样快。                             |
| 7    | 该库具有强大的日志记录机制。此外，我们可以即时验证标头、cookie、内容类型等。 |
| 8    | 它可以很容易地与其他 Java 库如 TestNG、JUnit 等集成。我们也可以将它与 Selenium-Java 集成，实现端到端的自动化。 |
| 9    | 它对各种API认证机制有很好的支持。                            |
| 10   | 它支持有助于解析 JSON 和 XML 响应的 JsonPath 和 XmlPath。它还支持 JSON Schema Validation 库来验证 JSON Schema。 |
| 11   | Rest Assured 也可以与 Maven 和 CICD 集成。                   |
| 12   | 支持多部分表单数据                                           |
| 13   | 支持 Spring Mock MVC、Spring Web Test Client、Scala 和 Kotlin。 |
| 14   | 它遵循[***BDD（行为数据驱动）***](https://www.toolsqa.com/cucumber/behavior-driven-development/)方法和 given() when()、then() 等关键字，使代码可读并支持干净的编码。此功能从 2.0 版开始提供。 |
| 15   | REST Assured 4.1.2 添加了对 Java 13 的支持。                 |

## 放心的缺点

该库有以下缺点。

1.  *它不支持显式测试[***SOAP（简单对象访问协议）***](https://www.toolsqa.com/soapui/soapui-tutorial/) API。*
2.  *使用该库需要用户具备良好的 Java 编程知识*
3.  *Rest Assured 中没有内置报告。*

## 关键要点

我们从这篇文章中得到以下结论：

1.  *它是一个开源 Java 库，可以免费使用。*
2.  *它用于验证和测试 Java API。*
3.  *除了 Java DSL 语法等其他功能外，还支持所有 HTTP 方法和 REST 方法，如 GET、POST、PUT、DELETE 等。*
4.  *我们可以将 Rest Assured 与所有主要的自动化框架（如 TestNG、JUnit）集成，并将其与 maven 和 CI/CD 集成。*
5.  *该库具有丰富的语法，并且由于它是开源的，因此不断添加越来越多的功能，这使其成为 API 自动化的非常高效和简单的库。*

有了这些知识，我们将在后续文章中从实际的 API 测试教程开始。在此之前，我们将讨论使用 Eclipse IDE 配置 Rest Assured。