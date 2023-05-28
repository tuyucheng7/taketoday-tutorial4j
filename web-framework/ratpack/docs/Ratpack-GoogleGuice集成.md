## 1. 概述

在我们之前的[文章中](https://www.baeldung.com/ratpack)，我们展示了如何使用 Ratpack 构建可扩展的应用程序。

在本教程中，我们将进一步讨论如何使用带有[Ratpack的](https://ratpack.io/)Google Guice作为依赖管理引擎。

## 2. 为什么选择 Google Guice？

[Google Guice](https://github.com/google/guice)是Google在Apache 许可下发布的Java平台的开源软件框架。

它是非常轻量级的依赖管理模块，易于配置。此外，为了可用性，它只允许构造函数级别的依赖注入。

[可以在此处](https://www.baeldung.com/guice)找到有关Guice的更多详细信息。

## 3. 将 Guice 与 Ratpack 结合使用

### 3.1. Maven 依赖

Ratpack 对Guice依赖有一流的支持。因此，我们不必手动为Guice 添加任何外部依赖；它已经预装了Ratpack。[可以在此处](https://ratpack.io/manual/current/guice.html)找到有关Ratpack的Guice支持的更多详细信息。

因此，我们只需要在pom.xml中添加以下核心Ratpack依赖项：

```xml
<dependency>
    <groupId>io.ratpack</groupId>
    <artifactId>ratpack-core</artifactId>
    <version>1.4.5</version>
</dependency>
```

[你可以在Maven ](https://search.maven.org/classic/#search|gav|1|g%3A"io.ratpack" AND a%3A"ratpack-core")[Central](https://search.maven.org/classic/#search|gav|1|g%3A"io.ratpack" AND a%3A"ratpack-core")查看最新版本。

### 3.2. 构建服务模块

完成Maven配置后，我们将构建服务并在此处的示例中充分利用一些简单的依赖项注入。

让我们创建一个服务接口和一个服务类：

```java
public interface DataPumpService {
    String generate();
}
```

这是将充当注入器的服务接口。现在，我们必须构建将实现它的服务类，并将定义服务方法generate()：

```java
public class DataPumpServiceImpl implements DataPumpService {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }

}
```

这里需要注意的重要一点是，由于我们使用的是Ratpack 的 Guice模块，因此我们不需要使用Guice的@ImplementedBy或@Inject注解来手动注入服务类。

### 3.3. 依赖管理

使用Google Guice执行依赖项管理有两种方法。

第一个是使用Guice的[AbstractModule](https://google.github.io/guice/api-docs/latest/javadoc/index.html?com/google/inject/AbstractModule.html)，另一个是使用 Guice 的[实例绑定](https://github.com/google/guice/wiki/InstanceBindings)机制方法：

```java
public class DependencyModule extends AbstractModule {

    @Override
    public void configure() {
        bind(DataPumpService.class).to(DataPumpServiceImpl.class)
          .in(Scopes.SINGLETON);
    }

}
```

这里需要注意几点：

-   通过扩展AbstractModule，我们覆盖了默认的configure()方法
-   我们将DataPumpServiceImpl类映射到DataPumpService接口，这是之前构建的服务层
-   我们还以单例方式注入了依赖项。

### 3.4. 与现有应用程序集成

由于依赖管理配置已准备就绪，现在让我们集成它：

```java
public class Application {

    public static void main(String[] args) throws Exception {

      RatpackServer
          .start(server -> server.registry(Guice
            .registry(bindings -> bindings.module(DependencyModule.class)))
            .handlers(chain -> chain.get("randomString", ctx -> {
                DataPumpService dataPumpService = ctx.get(DataPumpService.class);
                ctx.render(dataPumpService.generate().length());
            })));
    }
}
```

在这里，通过registry() – 我们绑定了扩展AbstractModule的DependencyModule类。Ratpack 的 Guice模块将在内部完成剩余的必要工作，并将服务注入到应用程序Context中。

由于它在应用程序上下文中可用，我们现在可以从应用程序的任何地方获取服务实例。在这里，我们从当前上下文中获取了DataPumpService实例，并使用服务的generate()方法映射了/randomString URL 。

因此，无论何时访问/randomString URL，它都会返回随机字符串片段。

### 3.5. 运行时实例绑定

如前所述，我们现在将使用 Guice 的实例绑定机制来进行运行时的依赖管理。除了使用 Guice 的bindInstance()方法而不是AbstractModule来注入依赖项之外，它与之前的技术几乎相同：

```java
public class Application {

    public static void main(String[] args) throws Exception {

      RatpackServer.start(server -> server
        .registry(Guice.registry(bindings -> bindings
        .bindInstance(DataPumpService.class, new DataPumpServiceImpl())))
        .handlers(chain -> chain.get("randomString", ctx -> {
            DataPumpService dataPumpService = ctx.get(DataPumpService.class);
            ctx.render(dataPumpService.generate());
        })));
    }
}
```

在这里，通过使用bindInstance()，我们正在执行实例绑定，即将DataPumpService接口注入到DataPumpServiceImpl类。

通过这种方式，我们可以像在前面的示例中那样将服务实例注入到应用程序上下文中。

虽然我们可以使用这两种技术中的任何一种来进行依赖管理，但使用AbstractModule总是更好，因为它将依赖管理模块与应用程序代码完全分开。这样以后代码会干净很多，也更容易维护。

### 3.6. 工厂装订

还有一种称为工厂绑定的依赖管理方式。它与Guice 的实现没有直接关系，但这也可以与Guice并行工作。

工厂类将客户端与实现分离。一个简单的工厂使用静态方法来获取和设置接口的模拟实现。

我们可以使用已经创建的服务类来启用工厂绑定。我们只需要创建一个像DependencyModule一样的工厂类(它扩展了Guice 的 AbstractModule类)并通过静态方法绑定实例：

```java
public class ServiceFactory {

    private static DataPumpService instance;

    public static void setInstance(DataPumpService dataPumpService) {
        instance = dataPumpService;
    }

    public static DataPumpService getInstance() {
        if (instance == null) {
            return new DataPumpServiceImpl();
        }
        return instance;
    }
}
```

在这里，我们在工厂类中静态注入服务接口。因此，该工厂类一次只能使用该接口的一个实例。然后，我们创建了普通的getter/setter方法来设置和获取服务实例。

这里要注意的是，在getter方法中，我们进行了一次显式检查，以确保仅存在一个服务实例；如果它为空，则只有我们创建了实现服务类的实例并返回了相同的实例。

此后，我们可以在应用链中使用这个工厂实例：

```java
.get("factory", ctx -> ctx.render(ServiceFactory.getInstance().generate()))
```

## 4.测试

在Ratpack的内部 JUnit 测试框架的帮助下，我们将使用Ratpack的[MainClassApplicationUnderTest来测试我们的应用程序。](https://ratpack.io/manual/current/api/ratpack/test/MainClassApplicationUnderTest.html)我们必须为它添加必要的依赖项 ( [ratpack-test](https://search.maven.org/classic/#search|gav|1|g%3A"io.ratpack" AND a%3A"ratpack-test") )。

这里需要注意的是，由于 URL 内容是动态的，我们在编写测试用例时无法预测它。因此，我们将匹配测试用例中/randomString URL 端点的内容长度：

```java
@RunWith(JUnit4.class)
public class ApplicationTest {

    MainClassApplicationUnderTest appUnderTest
      = new MainClassApplicationUnderTest(Application.class);

    @Test
    public void givenStaticUrl_getDynamicText() {
        assertEquals(21, appUnderTest.getHttpClient()
          .getText("/randomString").length());
    }
	
    @After
    public void shutdown() {
        appUnderTest.close();
    }
}
```

## 5.总结

在这篇简短的文章中，我们展示了如何将Google Guice与Ratpack结合使用。