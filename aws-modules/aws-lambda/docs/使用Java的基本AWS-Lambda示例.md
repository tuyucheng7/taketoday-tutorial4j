## 1. 简介

[AWS Lambda](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html)是亚马逊提供的一种无服务器计算服务，用于减少服务器配置、操作系统、可扩展性等。AWS Lambda 能够在 AWS Cloud 上执行代码。

它运行以响应不同 AWS 资源上的事件，从而触发 AWS Lambda 函数。定价是按需付费，这意味着我们不会在闲置的 lambda 函数上花钱。

本教程需要一个有效的 AWS 账户；[你可以在这里](https://aws.amazon.com/)创建一个。

## 2.Maven依赖

要启用 AWS lambda，我们的项目中需要以下依赖项：

```html
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-lambda-java-core</artifactId>
    <version>1.1.0</version>
</dependency>
```

这种依赖关系可以在[Maven 存储库](https://search.maven.org/classic/#search|ga|1|aws-lambda-java-core)中找到。

我们还需要[Maven Shade 插件](https://search.maven.org/classic/#search|ga|1|a%3A"maven-shade-plugin")来构建 lambda 应用程序：

```html
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>2.4.3</version>
    <configuration>
        <createDependencyReducedPom>false</createDependencyReducedPom>
    </configuration>
    <executions>
        <execution>
            <phase>package</phase>
	    <goals>
                <goal>shade</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## 3.创建处理程序

简单地说，要调用 lambda 函数，我们需要指定一个处理程序；有 3 种创建处理程序的方法：

1.  创建自定义MethodHandler
2.  实现RequestHandler接口
3.  实现RequestStreamHandler接口

让我们看看如何使用代码示例来做到这一点。

### 3.1. 自定义方法处理器

我们将创建一个处理程序方法，它将作为传入请求的入口点。我们可以使用 JSON 格式或原始数据类型作为输入值。

此外，可选的Context对象将允许我们访问 Lambda 执行环境中可用的有用信息：

```java
public class LambdaMethodHandler {
    public String handleRequest(String input, Context context) {
        context.getLogger().log("Input: " + input);
        return "Hello World - " + input;
    }
}
```

### 3.2. 请求处理器接口

我们还可以在我们的类中实现RequestHandler并覆盖handleRequest方法，这将是我们请求的入口点：

```java
public class LambdaRequestHandler
  implements RequestHandler<String, String> {
    public String handleRequest(String input, Context context) {
        context.getLogger().log("Input: " + input);
        return "Hello World - " + input;
    }
}
```

在这种情况下，输入将与第一个示例中的相同。

### 3.3. RequestStreamHandler接口

我们也可以在我们的类中实现RequestStreamHandler并简单地覆盖handleRequest方法。

区别在于InputStream、ObjectStream和Context对象作为参数传递：

```java
public class LambdaRequestStreamHandler
  implements RequestStreamHandler {
    public void handleRequest(InputStream inputStream, 
      OutputStream outputStream, Context context) {
        String input = IOUtils.toString(inputStream, "UTF-8");
        outputStream.write(("Hello World - " + input).getBytes());
    }
}
```

## 4.构建部署文件

配置好所有内容后，我们可以通过简单地运行来创建部署文件：

```java
mvn clean package shade:shade
```

jar文件将在目标文件夹下创建。

## 5. 通过管理控制台创建 Lambda 函数

登录[AWS Amazon](https://aws.amazon.com/)，然后单击服务下的 Lambda。此页面将显示已创建的 lambda 函数列表。

以下是创建 lambda 所需的步骤：

1.  “选择蓝图”，然后选择“空白功能”

2.  “配置触发器”(在我们的例子中，我们没有任何触发器或事件)

3.  “配置功能”：

    -   名称：提供MethodHandlerLambda，

    -   描述：任何描述我们的 lambda 函数的东西

    -   运行时：选择java8

    -   代码条目类型和功能包：选择“上传.ZIP 和Jar 文件”并单击“上传”按钮。选择包含 lambda 代码的文件。

    -   在

        Lambda 函数处理程序和角色

        下：

        -   处理程序名称：提供 lambda 函数处理程序名称com.baeldung.MethodHandlerLambda::handleRequest
        -   角色名称：如果在 lambda 函数中使用任何其他 AWS 资源，则通过创建/使用现有角色提供访问权限，并定义策略模板。

    -   在

        高级设置下：

        -   内存：提供我们的 lambda 函数将使用的内存。
        -   超时：为每个请求选择执行 lambda 函数的时间。

4.  完成所有输入后，单击“下一步”，这将显示你查看配置。

5.  审核完成后，点击“创建功能”。

## 6.调用函数

创建 AWS lambda 函数后，我们将通过传递一些数据来测试它：

-   从列表中单击你的 lambda 函数，然后单击“测试”按钮
-   将出现一个弹出窗口，其中包含用于发送数据的虚拟值。用“Baeldung”覆盖数据
-   单击“保存并测试”按钮

在屏幕上，你可以看到成功返回输出的执行结果部分为：

```java
"Hello World - Baeldung"
```

## 七. 总结

在这篇快速介绍文章中，我们使用Java8 创建了一个简单的 AWS Lambda 应用程序，将其部署到 AWS 并对其进行了测试。