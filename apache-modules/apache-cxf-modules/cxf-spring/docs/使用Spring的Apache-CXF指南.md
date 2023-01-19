## 1. 概述

本教程的重点是配置和使用[Apache CXF](https://cxf.apache.org/)框架以及Spring——使用Java或 XML 配置。

这是 Apache CXF 系列中的第二篇；[第一个](https://www.baeldung.com/introduction-to-apache-cxf)侧重于 CXF 的基础知识，作为 JAX-WS 标准 API 的实现。

## 2.Maven依赖

与上一个教程类似，需要包含以下两个依赖项：

```xml
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-rt-frontend-jaxws</artifactId>
    <version>3.1.6</version>
</dependency>
<dependency>
    <groupId>org.apache.cxf</groupId>
    <artifactId>cxf-rt-transports-http</artifactId>
    <version>3.1.6</version>
</dependency>
```

有关 Apache CXF 工件的最新版本，请查看[apache-cxf](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.cxf")。

另外，为了支持Spring，还需要以下依赖：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>4.3.1.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>4.3.1.RELEASE</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework")找到最新版本的 Spring 工件。

最后，因为我们将使用JavaServlet 3.0+ API 而不是传统的web.xml部署描述符以编程方式配置应用程序，所以我们需要以下工件：

```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>3.1.0</version>
</dependency>
```

[这](https://search.maven.org/classic/#search|ga|1|g%3A"javax.servlet" AND a%3A"javax.servlet-api")是我们可以找到最新版本的 Servlet API 的地方。

## 3. 服务器端组件

现在让我们看一下需要出现在服务器端以发布 Web 服务端点的组件。

### 3.1. WebApplicationInitializer接口

实现WebApplicationInitializer接口以编程方式为应用程序配置ServletContext接口。当存在于类路径中时，其onStartup方法由 servlet 容器自动调用，此后ServletContext被实例化和初始化。

下面是如何定义一个类来实现WebApplicationInitializer接口：

```java
public class AppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext container) {
        // Method implementation
    }
}
```

onStartup ()方法是使用下面显示的代码片段实现的。

首先，创建并配置 Spring 应用程序上下文以注册包含配置元数据的类：

```java
AnnotationConfigWebApplicationContext context 
  = new AnnotationConfigWebApplicationContext();
context.register(ServiceConfiguration.class);
```

ServiceConfiguration类使用@Configuration注解进行注解以提供bean 定义。此类将在下一小节中讨论。

以下代码片段显示了如何将 Spring 应用程序上下文添加到 servlet 上下文：

```java
container.addListener(new ContextLoaderListener(context));
```

生成并注册由 Apache CXF 定义的CXFServlet类来处理传入请求：

```java
ServletRegistration.Dynamic dispatcher 
  = container.addServlet("dispatcher", new CXFServlet());
```

应用程序上下文加载在配置文件中定义的 Spring 元素。在这种情况下，servlet 的名称是cxf ，因此默认情况下，上下文会在名为cxf-servlet.xml的文件中查找这些元素。

最后，CXF servlet 被映射到一个相对 URL：

```java
dispatcher.addMapping("/services");
```

### 3.2. 好旧的web.xml

或者，如果我们想使用(有点过时的)部署描述符而不是WebApplicationInitializer接口，则相应的web.xml文件应包含以下 servlet 定义：

```xml
<servlet>
    <servlet-name>cxf</servlet-name>
    <servlet-class>org.apache.cxf.transport.servlet.CXFServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
</servlet>
    <servlet-mapping>
    <servlet-name>cxf</servlet-name>
    <url-pattern>/services/</url-pattern>
</servlet-mapping>
```

### 3.3. 服务配置类

现在让我们看一下服务配置——首先是一个包含 Web 服务端点的 bean 定义的基本框架：

```java
@Configuration
public class ServiceConfiguration {
    // Bean definitions
}
```

第一个必需的 bean 是SpringBus——它为 Apache CXF 提供扩展以与 Spring Framework 一起工作：

```java
@Bean
public SpringBus springBus() {
    return new SpringBus();
}
```

还需要使用SpringBus bean和 Web 服务实现器创建EnpointImpl bean 。这个 bean 用于在给定的 HTTP 地址发布端点：

```java
@Bean
public Endpoint endpoint() {
    EndpointImpl endpoint = new EndpointImpl(springBus(), new BaeldungImpl());
    endpoint.publish("http://localhost:8080/services/baeldung");
    return endpoint;
}
```

BaeldungImpl类用于实现 Web 服务接口。它的定义在下一小节中给出。

或者，我们也可以在 XML 配置文件中声明服务器端点。具体来说，下面的cxf-servlet.xml文件与 3.1 小节中定义的web.xml部署描述符一起工作，并描述了完全相同的端点：

```xml

<jaxws:endpoint
        id="baeldung"
        implementor="cn.tuyucheng.taketoday.cxf.spring.BaeldungImpl"
        address="http://localhost:8080/services/baeldung"/>
```

请注意，XML 配置文件以部署描述符中定义的 servlet 名称命名，即cxf。

### 3.4. 类型定义

接下来——这是上一节中已经提到的实现者的定义：

```java
@WebService(endpointInterface = "spring.cxf.cn.tuyucheng.taketoday.Baeldung")
public class BaeldungImpl implements Baeldung {
    private int counter;

    public String hello(String name) {
        return "Hello " + name + "!";
    }

    public String register(Student student) {
        counter++;
        return student.getName() + " is registered student number " + counter;
    }
}
```

此类为Apache CXF 将包含在已发布的 WSDL 元数据中的Baeldung端点接口提供实现：

```java
@WebService
public interface Baeldung {
    String hello(String name);
    String register(Student student);
}
```

端点接口和实现者都使用Student类，其定义如下：

```java
public class Student {
    private String name;

    // constructors, getters and setters
}
```

## 4.客户端Bean

为了利用 Spring Framework，我们在@Configuration注解类中声明一个 bean：

```java
@Configuration
public class ClientConfiguration {
    // Bean definitions
}
```

定义了一个名为client的 bean ：

```java
@Bean(name = "client")
public Object generateProxy() {
    return proxyFactoryBean().create();
}
```

客户端bean 表示Baeldung Web 服务的代理。它是通过调用 JaxWsProxyFactoryBean bean 上的create方法创建的， JaxWsProxyFactoryBean bean 是用于创建 JAX-WS 代理的工厂。

JaxWsProxyFactoryBean对象是通过以下方法创建和配置的：

```java
@Bean
public JaxWsProxyFactoryBean proxyFactoryBean() {
    JaxWsProxyFactoryBean proxyFactory = new JaxWsProxyFactoryBean();
    proxyFactory.setServiceClass(Baeldung.class);
    proxyFactory.setAddress("http://localhost:8080/services/baeldung");
    return proxyFactory;
}
```

工厂的serviceClass属性表示 Web 服务接口，而address属性表示代理进行远程调用的 URL 地址。

同样对于客户端的 Spring bean，可以恢复为 XML 配置文件。以下元素声明与我们刚刚在上面以编程方式配置的那些相同的 beans：

```xml

<bean id="client" factory-bean="clientFactory" factory-method="create"/>
<bean id="clientFactory" class="org.apache.cxf.jaxws.JaxWsProxyFactoryBean">
<property name="serviceClass" value="cn.tuyucheng.taketoday.cxf.spring.Baeldung"/>
<property name="address" value="http://localhost:8080/services/baeldung"/>
</bean>
```

## 5. 测试用例

本节描述用于说明 Apache CXF 对 Spring 的支持的测试用例。测试用例在名为StudentTest的类中定义。

首先，我们需要从前面提到的ServiceConfiguration配置类中加载一个 Spring 应用程序上下文，并将其缓存在上下文字段中：

```java
private ApplicationContext context 
  = new AnnotationConfigApplicationContext(ClientConfiguration.class);
```

接下来，从应用程序上下文中声明并加载服务端点接口的代理：

```java
private Baeldung baeldungProxy = (Baeldung) context.getBean("client");
```

这个Baeldung代理将在下面描述的测试用例中使用。

在第一个测试用例中，我们证明当在代理上本地调用hello方法时，响应与端点实现者从远程 Web 服务返回的响应完全相同：

```java
@Test
public void whenUsingHelloMethod_thenCorrect() {
    String response = baeldungProxy.hello("John Doe");
    assertEquals("Hello John Doe!", response);
}
```

在第二个测试用例中，学生通过本地调用代理上的注册方法来注册 Baeldung 课程，代理又会调用 Web 服务。该远程服务随后将计算学生人数并将其返回给调用者。以下代码片段证实了我们的预期：

```java
@Test
public void whenUsingRegisterMethod_thenCorrect() {
    Student student1 = new Student("Adam");
    Student student2 = new Student("Eve");
    String student1Response = baeldungProxy.register(student1);
    String student2Response = baeldungProxy.register(student2);

    assertEquals("Adam is registered student number 1", student1Response);
    assertEquals("Eve is registered student number 2", student2Response);
}
```

## 6. 集成测试

为了在服务器上部署为 Web 应用程序，需要先将本教程中的代码片段打包到 WAR 文件中。这可以通过在 POM 文件中声明包装属性来实现：

```xml
<packaging>war</packaging>
```

打包作业由 Maven WAR 插件实现：

```xml
<plugin>
    <artifactId>maven-war-plugin</artifactId>
    <version>2.6</version>
    <configuration>
        <failOnMissingWebXml>false</failOnMissingWebXml>
    </configuration>
</plugin>
```

该插件将编译后的源代码打包成一个 WAR 文件。由于我们使用Java代码配置 servlet 上下文，传统的web.xml部署描述符不需要存在。因此，必须将failOnMissingWebXml属性设置为false，以避免在执行插件时失败。

我们可以点击[此链接](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.maven.plugins" AND a%3A"maven-war-plugin")获取最新版本的 Maven WAR 插件。

为了说明 Web 服务的操作，我们创建了一个集成测试。该测试首先生成一个 WAR 文件并启动一个嵌入式服务器，然后让客户端调用 Web 服务，验证后续响应并最终停止服务器。

Maven POM 文件中需要包含以下插件。有关详细信息，请查看[此集成测试教程](https://www.baeldung.com/apache-cxf-with-spring)。

这是 Maven Surefire 插件：

```xml
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>
    <configuration>
        <excludes>
            <exclude>StudentTest.java</exclude>
        </excludes>
    </configuration>
</plugin>
```

可以在[此处](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.maven.plugins" AND a%3A"maven-surefire-plugin")找到此插件的最新版本。

声明一个id为integration的配置文件部分，以方便集成测试：

```xml
<profiles>
   <profile>
      <id>integration</id>
      <build>
         <plugins>
            ...
         </plugins>
      </build>
   </profile>
</profiles>
```

Maven Cargo 插件包含在集成配置文件中：

```xml
<plugin>
    <groupId>org.codehaus.cargo</groupId>
    <artifactId>cargo-maven2-plugin</artifactId>
    <version>1.5.0</version>
    <configuration>
        <container>
            <containerId>jetty9x</containerId>
            <type>embedded</type>
        </container>
        <configuration>
            <properties>
                <cargo.hostname>localhost</cargo.hostname>
                <cargo.servlet.port>8080</cargo.servlet.port>
            </properties>
        </configuration>
    </configuration>
    <executions>
        <execution>
            <id>start-server</id>
            <phase>pre-integration-test</phase>
            <goals>
                <goal>start</goal>
            </goals>
        </execution>
        <execution>
            <id>stop-server</id>
            <phase>post-integration-test</phase>
            <goals>
                <goal>stop</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

请注意，包含cargo.hostname和cargo.servlet.port配置属性只是为了清楚起见。这些配置属性可以被忽略而不会对应用程序产生任何影响，因为它们的值与默认值相同。这个插件启动服务器，等待连接，最后停止服务器以释放系统资源。

[这个链接](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.maven.plugins" AND a%3A"maven-war-plugin")允许我们检查最新版本的 Maven Cargo 插件。

Maven Surefire 插件在集成配置文件中再次声明，以覆盖其在主构建部分中的配置并执行上一节中描述的测试用例：

```xml
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
            </configuration>
        </execution>
    </executions>
</plugin>
```

现在整个过程可以通过命令运行：mvn -Pintegration clean install。

## 七. 总结

本教程说明了 Apache CXF 对 Spring 的支持。特别是，它展示了如何使用 Spring 配置文件发布 Web 服务，以及客户端如何通过 Apache CXF 代理工厂创建的代理与该服务交互，该代理在另一个配置文件中声明。