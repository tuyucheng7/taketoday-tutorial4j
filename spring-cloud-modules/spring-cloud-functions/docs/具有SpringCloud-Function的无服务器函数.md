## 1. 简介

在本教程中，我们将学习如何使用 Spring Cloud Function。

我们将在本地构建并运行一个简单的 Spring Cloud Function，然后将其部署到 AWS。

## 2. Spring Cloud功能设置

首先，让我们从头开始实施并使用不同的方法测试一个包含两个函数的简单项目：

-   字符串反向器，使用普通方法
-   和一个使用专门课程的迎宾员

### 2.1. Maven 依赖项

我们需要做的第一件事是包含[spring-cloud-starter-function-web](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.cloud" AND a%3A"spring-cloud-starter-function-web") 依赖项。这将充当我们的本地适配器并引入必要的依赖项以在本地运行我们的功能：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-function-web</artifactId>
    <version>1.0.1.RELEASE</version>
</dependency>
```

敬请期待，因为我们将在部署到 AWS 时对其进行一些修改。

### 2.2. 编写 Spring Cloud 函数

使用 Spring Cloud Function，我们可以将 Function、 Consumer 或 Supplier类型的 @Bean公开为单独的方法：

```java
@SpringBootApplication
public class CloudFunctionApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudFunctionApplication.class, args);
    }

    @Bean
    public Function<String, String> reverseString() {
        return value -> new StringBuilder(value).reverse().toString();
    }
}
```

就像在这段代码中一样，我们可以将反向字符串功能公开为Function，我们的目标功能平台可以调用它。

### 2.3. 在本地测试反向字符串函数

spring-cloud-starter-function-web 将 函数公开为 HTTP 端点。在我们运行 CloudFunctionApplication之后，我们可以卷曲我们的目标以在本地测试它：

```bash
curl localhost:8080/reverseString -H "Content-Type: text/plain" -d "Baeldung User"
```

请注意，端点是 bean 的名称。 

正如预期的那样，我们得到了反转的字符串作为输出：

```bash
resU gnudleaB
```

### 2.4. 扫描包中的 Spring Cloud Function

除了将我们的方法公开为 @Bean 之外， 我们还可以将我们的软件编写为实现功能接口Function<T, R>的类：

```java
public class Greeter implements Function<String, String> {

    @Override
    public String apply(String s) {
        return "Hello " + s + ", and welcome to Spring Cloud Function!!!";
    }
}
```

然后我们可以在application.properties中指定要扫描相关 bean 的包：

```plaintext
spring.cloud.function.scan.packages=com.baeldung.spring.cloudfunction.functions
```

### 2.5. 在本地测试 Greeter 函数

同样，我们可以启动应用程序并使用 curl 来测试Greeter功能：

```bash
curl localhost:8080/greeter -H "Content-Type: text/plain" -d "World"
```

请注意，端点是实现 Functional 接口的类的名称。 

而且，毫不奇怪，我们得到了预期的问候语：

```plaintext
Hello World, and welcome to Spring Cloud function!!!
```

## 3. AWS 上的 Spring Cloud Function

Spring Cloud Function 如此强大的原因在于我们可以构建与云无关的支持 Spring 的函数。函数本身不需要知道它是如何被调用的或者它被部署到的环境。例如，我们可以轻松地将此欢迎程序部署到 AWS、Azure 或 Google Cloud 平台，而无需更改任何业务逻辑。

由于 AWS Lambda 是流行的无服务器解决方案之一，让我们关注如何将我们的应用程序部署到其中。

所以，让我们不要再等了，把我们的功能部署到云端吧！

### 3.1. Maven 依赖项

记住我们最初添加的spring-cloud-starter-function-web依赖项。现在是时候改变它了。

看，根据我们要运行 Spring Cloud Function 的位置，我们需要添加适当的依赖项。

对于 AWS，我们将使用[spring-cloud-function-adapter-aws](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.cloud" AND a%3A"spring-cloud-function-adapter-aws")：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-function-adapter-aws</artifactId>
</dependency>
```

接下来，让我们添加所需的 AWS 依赖项来处理 Lambda 事件：

```xml
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-lambda-java-events</artifactId>
    <version>2.0.2</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>com.amazonaws</groupId>
    <artifactId>aws-lambda-java-core</artifactId>
    <version>1.1.0</version>
    <scope>provided</scope>
</dependency>
```

最后，因为我们要将 maven 构建生成的工件上传到 AWS Lambda，所以我们需要构建一个带阴影的工件，这意味着，它将所有依赖项分解为单独的类文件而不是 jar。

[spring-boot-thin-layout ](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.boot.experimental" AND a%3A"spring-boot-thin-layout")依赖项通过排除一些不需要的依赖项来帮助我们减小工件的大小：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-deploy-plugin</artifactId>
            <configuration>
                <skip>true</skip>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <dependencies>
                <dependency>
                    <groupId>org.springframework.boot.experimental</groupId>
                    <artifactId>spring-boot-thin-layout</artifactId>
                    <version>1.0.10.RELEASE</version>
                </dependency>
            </dependencies>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <configuration>
                <createDependencyReducedPom>false</createDependencyReducedPom>
                <shadedArtifactAttached>true</shadedArtifactAttached>
                <shadedClassifierName>aws</shadedClassifierName>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 3.2. AWS 处理程序

如果我们想通过 HTTP 请求再次公开我们的字符串反向器，那么 Spring Cloud Function AWS 附带了 SpringBootRequestHandler。它实现了 AWS 的 RequestHandler 并负责将 AWS 请求分派给我们的函数。

```java
public class MyStringHandlers extends SpringBootRequestHandler<String, String> {

}
```

Spring Cloud Function AWS 还附带了 SpringBootStreamHandler和 FunctionInvokingS3EventHandler作为其他示例

现在， MyStringHandlers 只是一个空类似乎有点奇怪，但它在充当 Lambda 函数的入口点以及定义其输入和输出类型方面都发挥着重要作用。

正如我们将在下面的屏幕截图中看到的，我们将在 AWS Lambda 配置页面的处理程序输入字段中提供此类的完全限定名称。

### 3.3. AWS 如何知道要调用哪个云函数？

事实证明，即使我们的应用程序中有多个 Spring Cloud Function，AWS 也只能调用其中一个。

在下一节中，我们将 在 AWS 控制台上名为FUNCTION_NAME的环境变量中指定云函数名称。

## 4. 上传函数到AWS并测试

最后，让我们用 maven 构建我们的 jar，然后通过 AWS 控制台 UI 上传它。

### 4.1. 在 AWS 控制台上创建 Lambda 函数并进行配置

在 AWS Lambda 控制台页面的 Function code 部分，我们可以选择一个Java 8运行时，然后只需单击 Upload。

之后，我们需要在Handler 字段中指示实现 SpringBootRequestHandler或 com.baeldung.spring.cloudfunction 的类的完全限定名称。在我们的例子中是MyStringHandlers ：

[![云1](https://www.baeldung.com/wp-content/uploads/2018/09/cloud1.png)](https://www.baeldung.com/wp-content/uploads/2018/09/cloud1.png)

然后在环境变量中，我们指示通过FUNCTION_NAME 环境变量调用哪个 Spring 函数 bean ：

[![云2](https://www.baeldung.com/wp-content/uploads/2018/09/cloud2.png)](https://www.baeldung.com/wp-content/uploads/2018/09/cloud2.png)

完成后，我们就可以通过创建测试事件并提供示例字符串来测试 Lambda 函数：

[![云3](https://www.baeldung.com/wp-content/uploads/2018/09/cloud3.png)](https://www.baeldung.com/wp-content/uploads/2018/09/cloud3.png)

### 4.2. 在 AWS 上测试函数

现在，我们 保存 我们的测试，然后单击测试按钮。

而且，正如预期的那样，我们得到的输出与我们在本地测试函数时得到的输出相同：

[![云4](https://www.baeldung.com/wp-content/uploads/2018/09/cloud4.png)](https://www.baeldung.com/wp-content/uploads/2018/09/cloud4.png)

### 4.3. 测试另一个功能

请记住，我们的应用程序中还有一项功能： greeter。让我们确保它也有效。

我们将 FUNCTION_NAME 环境变量更改为greeter：

[![云5](https://www.baeldung.com/wp-content/uploads/2018/09/cloud5.png)](https://www.baeldung.com/wp-content/uploads/2018/09/cloud5.png)

单击保存按钮，最后 再次单击测试按钮：

[![云6](https://www.baeldung.com/wp-content/uploads/2018/09/cloud6.png)](https://www.baeldung.com/wp-content/uploads/2018/09/cloud6.png)

## 5.总结

总之，尽管在早期阶段，Spring Cloud Function 是一个强大的工具，可以将业务逻辑与任何特定的运行时目标解耦。

有了它，相同的代码可以作为 Web 端点、在云平台上或作为流的一部分运行。它抽象出所有传输细节和基础设施，允许开发人员保留所有熟悉的工具和流程，并坚定地专注于业务逻辑。