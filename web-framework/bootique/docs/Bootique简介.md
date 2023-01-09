## 1. 概述

[Bootique](http://bootique.io/)是一个非常轻量级的开源无容器JVM 框架，旨在构建下一代可扩展的微服务。它建立在嵌入式 Jetty 服务器之上，并完全支持[带有jax-rs 的](https://github.com/jax-rs)REST处理程序。

在本文中，我们将展示如何使用Bootique构建一个简单的 Web 应用程序。

## 2.Maven依赖

让我们通过将以下依赖项添加到pom.xml来开始使用Bootique ：

```xml
<dependency>
    <groupId>io.bootique.jersey</groupId>
    <artifactId>bootique-jersey</artifactId>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>io.bootique</groupId>
    <artifactId>bootique-test</artifactId>
    <scope>test</scope>
</dependency>

```

但是，Bootique还需要申报一些BOM(“物料清单”)进口。这就是为什么需要在 pom.xml 中添加以下<dependencyManagement>部分：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.bootique.bom</groupId>
            <artifactId>bootique-bom</artifactId>
            <version>0.23</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Bootique的最新版本在[Central Maven Repository](https://search.maven.org/classic/#search|ga|1|g%3A"io.bootique")中可用。

为了构建可运行的 jar，Bootique依赖于[maven-shade-plugin](https://maven.apache.org/plugins/maven-shade-plugin/)。这就是为什么我们还需要添加以下配置的原因：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

## 3. 开始申请

启动Bootique应用程序的最简单方法是从 main 方法调用[Bootique](https://github.com/bootique/bootique/blob/master/bootique/src/main/java/io/bootique/Bootique.java)的exec()方法：

```java
public class App {
    public static void main(String[] args) {
        Bootique.app(args)
          .autoLoadModules()
          .exec();
    }
}
```

但是，这不会启动嵌入式服务器。运行上述代码后，应显示以下日志：

```plaintext
NAME
      com.baeldung.bootique.App

OPTIONS
      -c yaml_location, --config=yaml_location
           Specifies YAML config location, which can be a file path 
           or a URL.

      -h, --help
           Prints this message.

      -H, --help-config
           Prints information about application modules and their 
           configuration options.

      -s, --server
           Starts Jetty server.
```

这些只不过是与Bootique预捆绑的可用程序参数。

名称是不言自明的；因此，要启动服务器，我们需要传递–s或–server参数，服务器将在默认端口 8080上启动并运行。

## 4.模块

Bootique应用程序由“模块”集合组成。在Bootique的术语“模块是包含一些代码的Java库”中，这意味着它将每个服务都视为一个模块。它使用[Google Guice](https://github.com/google/guice) 进行依赖注入。

为了看看它是如何工作的，让我们创建一个界面：

```java
public interface HelloService {
    boolean save();
}
```

现在，我们需要创建一个实现：

```java
public class HelloServiceImpl implements HelloService {
 
    @Override
    public boolean save() {
        return true;
    }
}
```

我们可以通过两种方式加载模块。第一种是使用Guice的[Module](https://google.github.io/guice/api-docs/latest/javadoc/index.html?com/google/inject/Module.html)接口，另一种是使用Bootique的[BQModuleProvider](https://github.com/bootique/bootique/blob/master/bootique/src/main/java/io/bootique/BQModuleProvider.java)，也称为自动加载。

### 4.1. 指导模块

在这里，我们可以使用Guice的[Module](https://google.github.io/guice/api-docs/latest/javadoc/index.html?com/google/inject/Module.html)接口来绑定实例：

```java
public class ModuleBinder implements Module {
 
    @Override
    public void configure(Binder binder) {
        binder
          .bind(HelloService.class)
          .to(HelloServiceImpl.class);
    }
}
```

定义模块后，我们需要将此自定义模块映射到Bootique实例：

```java
Bootique
  .app(args)
    .module(module)
    .module(ModuleBinder.class)
  .autoLoadModules()
  .exec();
```

### 4.2. BQModuleProvider(自动加载)

在这里，我们需要做的就是使用BQModuleProvider定义之前创建的模块绑定器：

```java
public class ModuleProvider implements BQModuleProvider {
 
    @Override
    public Module module() {
        return new ModuleBinder();
    }
}
```

这种技术的优点是我们不需要将任何模块信息映射到Bootique实例。

我们只需要在/resources/META-INF/services/io.bootique.BQModuleProvider中创建一个文件，并写入ModuleProvider的全名，包括包名，Bootique将负责其余的工作：

```java
com.baeldung.bootique.module.ModuleProvider
```

现在，我们可以使用[@Inject](https://google.github.io/guice/api-docs/latest/javadoc/index.html?com/google/inject/Inject.html)注解在运行时使用服务实例：

```java
@Inject
HelloService helloService;
```

这里需要注意的一件重要事情是，由于我们使用Bootique自己的 DI 机制，我们不需要使用Guice [@ImplementedBy](https://google.github.io/guice/api-docs/latest/javadoc/index.html?com/google/inject/ImplementedBy.html)注解来绑定服务实例。

## 5.REST端点

使用 JAX-RS API 创建 REST 端点非常简单：

```java
@Path("/")
public class IndexController {
 
    @GET
    public String index() {
        return "Hello, baeldung!";
    }
 
    @POST
    public String save() {
        return "Data Saved!";
    }
}
```

要将端点映射到Bootique自己的Jersey实例，我们需要定义一个[JerseyModule](https://github.com/bootique/bootique-jersey/blob/master/bootique-jersey/src/main/java/io/bootique/jersey/JerseyModule.java)：

```java
Module module = binder -> JerseyModule
  .extend(binder)
  .addResource(IndexController.class);
```

## 六、配置

我们可以在基于 YAML 的属性文件中提供内置或自定义配置信息。

例如，如果我们想在自定义端口上启动应用程序并添加默认 URI 上下文“hello”，我们可以使用以下 YAML 配置：

```plaintext
jetty:
    context: /hello
    connector:
        port: 10001
```

现在，在启动应用程序时，我们需要在配置参数中提供此文件的位置：

```plaintext
--config=/home/baeldung/bootique/config.yml
```

## 7. 日志记录

开箱即用的Bootique带有一个[bootique-logback](http://bootique.io/docs/0/bootique-logback-docs/)模块。要使用此模块，我们需要在pom.xml中添加以下依赖项：

```xml
<dependency>
    <groupId>io.bootique.logback</groupId>
    <artifactId>bootique-logback</artifactId>
</dependency>
```

这个模块带有一个[BootLogger](https://github.com/bootique/bootique/blob/master/bootique/src/main/java/io/bootique/log/BootLogger.java)接口，我们可以覆盖它来实现自定义日志记录：

```java
Bootique.app(args)
  .module(module)
  .module(ModuleBinder.class)
    .bootLogger( new BootLogger() {
      @Override
      public void trace( Supplier<String> args ) {
          // ...
      }
      @Override
      public void stdout( String args ) {
          // ...
      }
      @Override
      public void stderr( String args, Throwable thw ) {
          // ...
      }
      @Override
      public void stderr( String args ) {
          // ...
      }
}).autoLoadModules().exec();
```

另外，我们可以在config.yaml文件中定义日志记录配置信息：

```plaintext
log:
    level: warn
    appenders:
    - type: file
      logFormat: '%c{20}: %m%n'
      file: /path/to/logging/dir/logger.log
```

## 8. 测试

为了进行测试，Bootique附带了[bootique-test](https://github.com/bootique/bootique/tree/master/bootique-test)模块。我们可以通过两种方式测试Bootique应用程序。

第一种方法是“前台”方法，它使所有测试用例都在主测试线程上运行。

另一种是“后台”方法，它使测试用例在隔离的线程池上运行。

[可以使用BQTestFactory](https://github.com/bootique/bootique/blob/master/bootique-test/src/main/java/io/bootique/test/junit/BQTestFactory.java)初始化“前景”环境：

```java
@Rule
public BQTestFactory bqTestFactory = new BQTestFactory();
```

可以使用[BQDaemonTestFactory](https://github.com/bootique/bootique/blob/master/bootique-test/src/main/java/io/bootique/test/junit/BQDaemonTestFactory.java)初始化“后台”环境：

```java
@Rule
public BQDaemonTestFactory bqDaemonTestFactory = new BQDaemonTestFactory();
```

一旦环境工厂准备就绪，我们就可以编写简单的测试用例来测试服务：

```java
@Test
public void givenService_expectBoolen() {
    BQRuntime runtime = bqTestFactory
      .app("--server").autoLoadModules()
      .createRuntime();
    HelloService service = runtime.getInstance( HelloService.class );
 
    assertEquals( true, service.save() );
}
```

## 9.总结

在本文中，我们展示了如何使用Bootique的核心模块构建应用程序。还有其他几个可用的Bootique模块，如[bootique-jooq](https://github.com/bootique/bootique-jooq)、[bootique-kotlin](https://github.com/bootique/bootique-kotlin)、[bootique-job](https://github.com/bootique/bootique-job)等。可用模块的完整列表可[在此处](https://github.com/bootique)获得。