## 1. 简介

在本教程中，我们将学习如何使用Spring Cloud Function。

**我们将在本地构建并运行一个简单的Spring Cloud Function，然后将其部署到AWS**。

## 2. Spring Cloud Function设置

首先，让我们从头开始实现并使用不同的方法测试一个包含两个函数的简单项目：

-   字符串反向器，使用普通方法
-   和一个使用专门类的greeting

### 2.1 Maven依赖项

我们需要做的第一件事是包含[spring-cloud-starter-function-web](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-function-web/4.0.1)依赖项。这将充当我们的本地适配器并引入必要的依赖项以在本地运行我们的函数：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-function-web</artifactId>
    <version>1.0.1.RELEASE</version>
</dependency>
```

请继续关注，因为我们将在部署到AWS时对其进行一些修改。

### 2.2 编写Spring Cloud Function

**使用Spring Cloud Function，我们可以将Function、Consumer或Supplier类型的@Bean公开为单独的方法**：

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

就像在这段代码中一样，我们可以将reverseString函数公开为Function，我们的目标函数平台可以调用该函数。

### 2.3 在本地测试反向字符串函数

spring-cloud-starter-function-web将函数公开为HTTP端点。在我们运行CloudFunctionApplication之后，我们可以使用curl调用我们的目标以在本地测试它：

```bash
curl localhost:8080/reverseString -H "Content-Type: text/plain" -d "Baeldung User"
```

**请注意，端点是bean的名称**。 

正如预期的那样，我们得到了反转的字符串作为输出：

```bash
resU gnehcuyuT
```

### 2.4 扫描包中的Spring Cloud Function

除了将我们的方法公开为@Bean之外，我们还可以将我们的软件编写为实现函数接口Function<T, R\>的类：

```java
public class Greeter implements Function<String, String> {

    @Override
    public String apply(String s) {
        return "Hello " + s + ", and welcome to Spring Cloud Function!!!";
    }
}
```

然后我们可以在application.properties中指定要扫描相关bean的包：

```properties
spring.cloud.function.scan.packages=cn.tuyucheng.taketoday.spring.cloudfunction.functions
```

### 2.5 在本地测试Greeter函数

同样，我们可以启动应用程序并使用curl来测试Greeter函数：

```bash
curl localhost:8080/greeter -H "Content-Type: text/plain" -d "World"
```

**请注意，端点是实现Function接口的类的名称**。 

而且，毫不奇怪，我们得到了预期的问候语：

```html
Hello World, and welcome to Spring Cloud function!!!
```

## 3. AWS上的Spring Cloud Function

Spring Cloud Function如此强大的原因在于我们可以构建与云无关的支持Spring的函数。函数本身不需要知道它是如何被调用的或者它被部署到的环境。例如，**我们可以轻松地将此greeter程序部署到AWS、Azure或Google Cloud平台，而无需更改任何业务逻辑**。

由于AWS Lambda是流行的Serverless解决方案之一，因此让我们专注于如何将我们的应用程序部署到其中。

### 3.1 Maven依赖项

记住我们最初添加的spring-cloud-starter-function-web依赖项。现在是时候改变它了。

根据我们要运行Spring Cloud Function的位置，我们需要添加适当的依赖项。

对于AWS，我们将使用[spring-cloud-function-adapter-aws](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-function-adapter-aws/4.0.1)：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-function-adapter-aws</artifactId>
</dependency>
```

接下来，让我们添加处理Lambda事件所需的AWS依赖项：

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

最后，由于我们要将Maven构建生成的工件上传到AWS Lambda，因此我们需要构建一个shaded的工件，这意味着，它将所有依赖项分解为单独的class文件而不是jar。

[spring-boot-thin-layout](https://central.sonatype.com/artifact/org.springframework.boot.experimental/spring-boot-thin-layout/1.0.29.RELEASE)依赖项通过排除一些不需要的依赖项来帮助我们减小工件的大小：

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

### 3.2 AWS处理程序

如果我们想通过HTTP请求再次公开我们的字符串反转器，那么Spring Cloud Function AWS附带了SpringBootRequestHandler。它实现了AWS的RequestHandler并负责将AWS请求分派给我们的函数。

```java
public class MyStringHandlers extends SpringBootRequestHandler<String, String> {
}
```

Spring Cloud Function AWS还附带了SpringBootStreamHandler和FunctionInvokingS3EventHandler作为其他示例。

**现在，MyStringHandlers只是一个空类似乎有点奇怪，但它在充当Lambda函数的入口点以及定义其输入和输出类型方面都发挥着重要作用**。

正如我们将在下面的屏幕截图中看到的，我们将在AWS Lambda配置页面的处理程序输入字段中提供此类的完全限定名称。

### 3.3 AWS如何知道要调用哪个云函数？

事实证明，即使我们的应用程序中有多个Spring Cloud Function，**AWS也只能调用其中一个**。

在下一节中，我们将在AWS控制台上名为FUNCTION_NAME的环境变量中指定云函数名称。

## 4. 将函数上传到AWS并进行测试

最后，让我们使用Maven构建我们的jar，然后通过AWS控制台UI上传它。

### 4.1 在AWS控制台上创建Lambda函数并进行配置

在AWS Lambda控制台页面的Function code部分，我们可以选择**Java 8**运行时，然后只需单击**Upload**。

之后，我们需要在**Handler**字段中指示实现SpringBootRequestHandler的类的完全限定名称或者在本例中为cn.tuyucheng.taketoday.spring.cloudfunction.MyStringHandlers：

<img src="../assets/img.png">

然后在Environment variables中，我们通过FUNCTION_NAME环境变量指示要调用哪个Spring函数bean：

<img src="../assets/img_1.png">

完成此操作后，我们就可以通过创建测试事件并提供示例字符串来测试Lambda函数：

<img src="../assets/img_2.png">

### 4.2 在AWS上测试函数

现在，我们保存我们的测试，然后单击Test按钮。

而且，正如预期的那样，我们得到的输出与我们在本地测试函数时得到的输出相同：

<img src="../assets/img_3.png">

### 4.3 测试另一个函数

请记住，我们的应用程序中还有一个函数：greeter。让我们确保它也有效。

我们将FUNCTION_NAME环境变量更改为greeter：

<img src="../assets/img_4.png">

单击Save按钮，最后再次单击Test按钮：

<img src="../assets/img_5.png">

## 5. 总结

总之，尽管处于早期阶段，**Spring Cloud Function是一个强大的工具，可以将业务逻辑与任何特定的运行时目标解耦**。

有了它，相同的代码可以作为Web端点、在云平台上或作为流的一部分运行。它抽象出所有传输细节和基础设施，允许开发人员保留所有熟悉的工具和流程，并坚定地专注于业务逻辑。