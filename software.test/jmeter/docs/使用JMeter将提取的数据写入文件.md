## 1. 概述

在本教程中，我们将探索两种从[Apache JMeter](https://www.baeldung.com/jmeter)提取数据 并将其写入外部文件的方法。

## 2. 设置基本的 JMeter 脚本

现在让我们开始创建一个基本的 JMeter 脚本。让我们创建一个具有单个线程的线程组(这是创建线程组时的默认设置)：

[![JMeter 创建线程组](https://www.baeldung.com/wp-content/uploads/2021/02/JMeter-create-thread-group.png)](https://www.baeldung.com/wp-content/uploads/2021/02/JMeter-create-thread-group.png)

在此线程组中，让我们现在创建一个HTTP 采样器：

[![JMeter 创建 Http 采样器](https://baeldung.com/wp-content/uploads/2021/02/JMeter-create-http-sampler.png)](https://baeldung.com/wp-content/uploads/2021/02/JMeter-create-http-sampler.png)

让我们设置我们的HTTP 采样器来调用在本地主机上运行的 API。我们可以从使用简单的[REST 控制器](https://www.baeldung.com/spring-controller-vs-restcontroller)定义 API 开始：

```java
@RestController
public class RetrieveUuidController {

    @GetMapping("/api/uuid")
    public Response uuid() {
        return new Response(format("Test message... %s.", UUID.randomUUID()));
    }
}
```

此外，我们还要定义由我们的控制器返回的Response实例，如上所述：

```java
public class Response {
    private Instant timestamp;
    private UUID uuid;
    private String message;

    // getters, setters, and constructor omitted
}
```

现在让我们使用它来测试我们的 JMeter 脚本。默认情况下，这将在端口 8080 上运行。如果我们无法使用端口 8080，则需要相应地更新HTTP 采样器中的端口号字段。

HTTP Sampler请求应如下所示：

[![JMeter http 采样器详细信息](https://baeldung.com/wp-content/uploads/2021/02/JMeter-http-sampler-details.png)](https://baeldung.com/wp-content/uploads/2021/02/JMeter-http-sampler-details.png)

## 3. 使用监听器编写提取的输出

接下来，让我们使用一个Save Responses to a file类型的监听器来提取我们想要的数据到一个文件中：

[![JMeter写监听器](https://baeldung.com/wp-content/uploads/2021/02/JMeter-WriteListener.png)](https://baeldung.com/wp-content/uploads/2021/02/JMeter-WriteListener.png)

使用此侦听器很方便，但在我们可以提取到文件的内容方面没有太大的灵活性。对于我们的案例，这将生成一个 JSON 文件，该文件保存到 JMeter 当前运行的位置(尽管可以在文件名前缀字段中配置路径)。

## 4. 使用后处理器写入提取的输出

我们可以将数据提取到文件的另一种方法是创建[BeanShell](https://jmeter.apache.org/usermanual/component_reference.html#BeanShell_Sampler) PostProcessor。BeanShell是一个非常灵活的脚本处理器，它允许我们使用Java代码编写脚本以及使用 JMeter 提供的一些内置变量。

BeanShell可用于各种不同的用例。在这种情况下，让我们创建一个BeanShell后处理器并添加一个脚本来帮助我们将一些数据提取到文件中：

[![JMeter BeanShell 后处理器](https://baeldung.com/wp-content/uploads/2021/02/JMeter-BeanShell-PostProcessor.png)](https://baeldung.com/wp-content/uploads/2021/02/JMeter-BeanShell-PostProcessor.png)

现在让我们将以下脚本添加到脚本部分：

```java
FileWriter fWriter = new FileWriter("/<path>/result.txt", true);
BufferedWriter buff = new BufferedWriter(fWriter);

buff.write("data");

buff.close();
fWriter.close();
```

我们现在有一个简单的脚本，它将字符串数据输出到一个名为 result 的文件中。这里要注意的重要一点是FileWriter构造函数的第二个参数。这必须设置为true 以便我们的BeanShell将附加到文件而不是覆盖它。 这在 JMeter 中使用多线程时非常重要。

接下来，我们要提取对我们的用例更有意义的内容。让我们使用 JMeter 提供的[ctx](https://jmeter.apache.org/api/org/apache/jmeter/threads/JMeterContext.html) 变量。这将允许我们访问运行 HTTP 请求的单个线程所持有的上下文。

从ctx中，让我们获取响应代码、响应标头和响应主体，并将它们提取到我们的文件中：

```java
buff.write("Response Code : " + ctx.getPreviousResult().getResponseCode());
buff.write(System.getProperty("line.separator"));
buff.write("Response Headers : " + ctx.getPreviousResult().getResponseHeaders());
buff.write(System.getProperty("line.separator"));
buff.write("Response Body : " + new String(ctx.getPreviousResult().getResponseData()));
```

如果我们想收集特定的字段数据并将其写入我们的文件，我们可以使用[vars](https://jmeter.apache.org/api/org/apache/jmeter/threads/JMeterVariables.html)变量。我们可以在后处理器中使用它来存储和检索字符串数据。

对于这个更复杂的示例，让我们在文件提取器之前创建另一个后处理器。这将通过 HTTP 请求的 JSON 响应进行搜索：

[![JMeter JSON 提取器](https://baeldung.com/wp-content/uploads/2021/02/JMeter-JSON-Exctractor.png)](https://baeldung.com/wp-content/uploads/2021/02/JMeter-JSON-Exctractor.png)

这个提取器将创建一个名为message的变量。剩下要做的就是在我们的文件提取器中引用此变量以将其输出到我们的文件：

```java
buff.write("More complex extraction : " + vars.get("message"));
```

注意：我们可以将此方法与其他后处理器(例如“正则表达式提取器”)结合使用，以更加定制的方式收集信息。

## 5.总结

在本教程中，我们介绍了如何使用 BeanShell 后处理器和写入侦听器将数据从 JMeter 提取到外部文件。我们使用的 JMeter Script 和 Spring REST 应用程序可以[在 GitHub 上找到。](https://github.com/eugenp/tutorials/tree/master/jmeter)