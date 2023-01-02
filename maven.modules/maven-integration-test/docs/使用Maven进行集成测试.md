## 1. 概述

Maven是Java领域中最流行的构建工具，而集成测试是开发过程的重要组成部分。因此，**使用Maven配置和执行集成测试是自然而然的选择**。

在本教程中，我们将介绍使用Maven进行集成测试以及将集成测试与单元测试分开的多种不同方法。

## 2. 准备

为了使演示代码接近真实世界的项目，我们将设置一个JAX-RS应用程序，此应用程序在执行集成测试之前部署到服务器并在之后拆除。

### 2.1 Maven配置

我们将围绕Jersey(JAX-RS的参考实现)构建我们的REST应用程序，此实现需要几个依赖项：

```xml
<dependency>
    <groupId>org.glassfish.jersey.containers</groupId>
    <artifactId>jersey-container-servlet-core</artifactId>
    <version>2.27</version>
</dependency>
<dependency>
    <groupId>org.glassfish.jersey.inject</groupId>
    <artifactId>jersey-hk2</artifactId>
    <version>2.27</version>
</dependency>
```

我们可以在[此处](https://search.maven.org/search?q=a:jersey-container-servlet-core)和[此处](https://search.maven.org/search?q=a:jersey-hk2)找到这些依赖项的最新版本。

我们将使用Jetty Maven插件来设置测试环境，**此插件在Maven构建生命周期的pre-integration-test阶段启动Jetty服务器，然后在post-integration-test阶段停止它**。

以下是我们如何在pom.xml中配置Jetty Maven插件：

```xml
<plugin>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-maven-plugin</artifactId>
    <version>9.4.11.v20180605</version>
    <configuration>
        <httpConnector>
            <port>8999</port>
        </httpConnector>
        <stopKey>quit</stopKey>
        <stopPort>9000</stopPort>
    </configuration>
    <executions>
        <execution>
            <id>start-jetty</id>
            <phase>pre-integration-test</phase>
            <goals>
                <goal>start</goal>
            </goals>
        </execution>
        <execution>
            <id>stop-jetty</id>
            <phase>post-integration-test</phase>
            <goals>
                <goal>stop</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

当Jetty服务器启动时，它将监听端口8999。<stopKey\>和<stopPort\>配置元素仅由插件的stop目标使用，从我们的角度来看，它们的值并不重要。

在[这里](https://search.maven.org/search?q=a:jetty-maven-plugin)可以找到最新版本的Jetty Maven插件。

另外需要注意的是，我们必须将pom.xml文件中的<packaging\>元素设置为war，否则Jetty插件无法启动服务器：

```xml
<packaging>war</packaging>
```

### 2.2 创建REST应用程序

应用程序端点非常简单，当GET请求到达上下文根时返回欢迎消息：

```java
@Path("/")
public class RestEndpoint {
    @GET
    public String hello() {
        return "Welcome to Tuyucheng!";
    }
}
```

这就是我们向Jersey注册端点类的方式：

```java
package cn.tuyucheng.taketoday.maven.it;

import org.glassfish.jersey.server.ResourceConfig;

public class EndpointConfig extends ResourceConfig {
    public EndpointConfig() {
        register(RestEndpoint.class);
    }
}
```

为了让Jetty服务器知道我们的REST应用程序，我们可以使用经典的web.xml部署描述符：

```xml
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
         http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd" version="3.1">
    <servlet>
        <servlet-name>rest-servlet</servlet-name>
        <servlet-class>org.glassfish.jersey.servlet.ServletContainer</servlet-class>
        <init-param>
            <param-name>javax.ws.rs.Application</param-name>
            <param-value>cn.tuyucheng.taketoday.maven.it.EndpointConfig</param-value>
        </init-param>
    </servlet>
    <servlet-mapping>
        <servlet-name>rest-servlet</servlet-name>
        <url-pattern>/*</url-pattern>
    </servlet-mapping>
</web-app>
```

**这个描述符必须放在目录/src/main/webapp/WEB-INF中才能被服务器识别**。

### 2.3 客户端测试代码

以下各节中的所有测试类都包含一个方法：

```java
@Test
public void whenSendingGet_thenMessageIsReturned() throws IOException {
    String url = "http://localhost:8999";
    URLConnection connection = new URL(url).openConnection();
    try (InputStream response = connection.getInputStream(); 
         Scanner scanner = new Scanner(response)) {
        String responseBody = scanner.nextLine();
        assertEquals("Welcome to Tuyucheng!", responseBody);
    }
}
```

如我们所见，此方法只是向我们之前设置的Web应用程序发送GET请求并验证响应。

## 3. 集成测试实践

关于集成测试需要注意的一件重要事情是**测试方法通常需要很长时间才能运行**。

因此，我们应该从默认的构建生命周期中排除集成测试，以防止它们在我们每次构建项目时减慢整个过程。

**分离集成测试的一种便捷方法是使用构建Profile**，这种配置使我们能够仅在必要时执行集成测试(通过指定合适的Profile)。

在接下来的部分中，我们将使用构建Profile配置所有集成测试。

## 4. 使用Failsafe插件进行测试

运行集成测试的最简单方法是使用[Maven failsafe插件](https://www.baeldung.com/maven-failsafe-plugin)。

默认情况下，**Maven surefire插件在test阶段执行单元测试，而failsafe插件在integration-test阶段运行集成测试**。

我们可以为这些插件命名具有不同模式的测试类，以分别获取包含的测试。

**由surefire和failsafe强制执行的默认命名约定是不同的，因此我们只需要遵循这些约定来分离单元测试和集成测试**。

surefire插件的执行包括名称以Test开头或以Test、Tests或TestCase结尾的所有类。相比之下，failsafe插件在名称以IT开头或以IT或ITCase结尾的类中执行测试方法。

[在这里](https://maven.apache.org/surefire/maven-surefire-plugin/examples/inclusion-exclusion.html)我们可以找到关于surefire的测试包含的文档，[这里](https://maven.apache.org/surefire/maven-failsafe-plugin/examples/inclusion-exclusion.html)是failsafe的文档。

让我们使用默认配置将failsafe插件添加到POM中：

```xml
<profile>
    <id>failsafe</id>
    <build>
        <plugins>
            <plugin>
                <artifactId>maven-failsafe-plugin</artifactId>
                <version>2.22.0</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>integration-test</goal>
                            <goal>verify</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</profile>
```

[此链接](https://search.maven.org/search?q=a:maven-failsafe-plugin)是查找failsafe插件最新版本的位置。

通过以上配置，将在集成测试阶段执行以下测试方法：

```java
public class RestIT {
    // test method shown in subsection 2.3
}
```

由于Jetty服务器在pre-integration-test阶段启动并在post-integration-test关闭，因此我们刚刚看到的测试通过以下命令：

```bash
mvn verify -Pfailsafe
```

我们还可以自定义命名模式以包含具有不同名称的类：

```xml
<plugin>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>2.22.0</version>
    <configuration>
        <includes>
            <include>**/*RestIT</include>
            <include>**/RestITCase</include>
        </includes>
    </configuration>
    ...
</plugin>
```

## 5. 使用Surefire插件进行测试

除了failsafe插件，**我们还可以使用**[surefire插件](https://www.baeldung.com/maven-surefire-plugin)**在不同阶段执行单元和集成测试**。

假设我们想用后缀IntegrationTest命名所有集成测试，由于surefire插件默认在测试阶段运行具有这样名称的测试，我们需要将它们从默认执行中排除：

```xml
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>
    <configuration>
        <excludes>
            <exclude>**/*IntegrationTest</exclude>
        </excludes>
    </configuration>
</plugin>
```

这个插件的最新版本在[这里](https://search.maven.org/artifact/org.apache.maven.plugins/maven-surefire-plugin/3.0.0-M7/maven-plugin)可以找到。

我们已将所有名称以IntegrationTest结尾的测试类从构建生命周期中移除，是时候用Profile把它们放回去了：

```xml
<profile>
    <id>surefire</id>
    <build>
        <plugins>
            <plugin>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>2.22.2</version>
                <executions>
                    <execution>
                        <phase>integration-test</phase>
                        <goals>
                            <goal>test</goal>
                        </goals>
                        <configuration>
                            <excludes>
                                <exclude>none</exclude>
                            </excludes>
                            <includes>
                                <include>**/*IntegrationTest</include>
                            </includes>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</profile>
```

**我们没有像往常一样将surefire插件的test目标绑定到测试构建阶段，而是将其绑定到集成测试阶段**。然后，该插件将在集成测试过程中启动。

**请注意，我们必须将<exclude\>元素设置为none以覆盖基本配置中指定的排除**。

现在，让我们用我们的命名模式定义一个集成测试类：

```java
public class RestIntegrationTest {
    // test method shown in subsection 2.3
}
```

此测试将使用以下命令运行：

```bash
mvn verify -Psurefire
```

## 6. 使用Cargo插件进行测试

我们可以将surefire插件与Maven cargo插件一起使用，该插件内置了对嵌入式服务器的支持，这对于集成测试非常有用。

有关此组合的更多详细信息，请参阅[此处](https://www.baeldung.com/integration-testing-with-the-maven-cargo-plugin)。

## 7. 使用JUnit的@Category进行测试

有选择地执行测试的一种便捷方法是利用JUnit4框架中的[@Category注解](https://junit.org/junit4/javadoc/latest/org/junit/experimental/categories/Category.html)，**这个注解允许我们从单元测试中排除特定的测试，并将它们包含在集成测试中**。

首先，我们需要一个接口或类作为类别标识符：

```java
package cn.tuyucheng.taketoday.maven.it;

public interface Integration { }
```

然后我们可以用@Category注解和Integration标识符来标注一个测试类：

```java
@Category(Integration.class)
public class RestJUnitTest {
    // test method shown in subsection 2.3
}
```

除了在测试类上声明@Category注解之外，我们还可以在方法级别使用它来对各个测试方法进行分类。

从测试构建阶段排除一个类别很简单：

```xml
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>
    <configuration>
        <excludedGroups>cn.tuyucheng.taketoday.maven.it.Integration</excludedGroups>
    </configuration>
</plugin>
```

在集成测试阶段包括Integration类别也很简单：

```xml
<profile>
    <id>category</id>
        <build>
        <plugins>
            <plugin>
                <artifactId>maven-failsafe-plugin</artifactId>
                <version>2.22.0</version>
                <configuration>
                    <includes>
                        <include>**/*</include>
                    </includes>
                    <groups>cn.tuyucheng.taketoday.maven.it.Integration</groups>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>integration-test</goal>
                            <goal>verify</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</profile>
```

现在，我们可以使用Maven命令运行集成测试：

```bash
mvn verify -Pcategory
```

## 8. 为集成测试添加一个单独的目录

有时最好有一个单独的目录用于集成测试，**以这种方式组织测试使我们能够将集成测试与单元测试完全隔离开来**。

为此，我们可以使用Maven build helper插件：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>build-helper-maven-plugin</artifactId>
    <version>3.0.0</version>
    <executions>
        <execution>
            <id>add-integration-test-source</id>
            <phase>generate-test-sources</phase>
            <goals>
                <goal>add-test-source</goal>
            </goals>
            <configuration>
                <sources>
                    <source>src/integration-test/java</source>
                </sources>
            </configuration>
        </execution>
    </executions>
</plugin>
```

在[这里](https://search.maven.org/search?q=a:build-helper-maven-plugin)我们可以找到这个插件的最新版本。

我们刚刚看到的配置在构建中添加了一个测试源目录，让我们向该新目录添加一个类定义：

```java
public class RestITCase {
    // test method shown in subsection 2.3
}
```

接下类我们也可以运行该测试源目录中的集成测试了：

```bash
mvn verify -Pfailsafe
```

由于我们在3.1小节中设置的配置，Maven failsafe插件将执行此测试类中的方法。

测试源目录通常与资源目录一起使用，我们可以在另一个<execution\>元素中将这样的目录添加到插件配置中：

```xml
<executions>
    ...
    <execution>
        <id>add-integration-test-resource</id>
        <phase>generate-test-resources</phase>
        <goals>
            <goal>add-test-resource</goal>
        </goals>
        <configuration>
            <resources>
                <resource>
                    <directory>src/integration-test/resources</directory>
                </resource>
            </resources>
        </configuration>
    </execution>
</executions>
```

## 9. 总结

本文介绍了使用Maven与Jetty服务器运行集成测试，重点介绍了Maven surefire和failsafe插件的配置。