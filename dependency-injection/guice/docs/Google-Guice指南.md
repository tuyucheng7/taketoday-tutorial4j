## 1. 简介

在本教程中，我们将研究Google Guice 的基础知识。然后，我们将了解在 Guice 中完成基本依赖注入 (DI) 任务的一些方法。

我们还将比较和对比 Guice 方法与更成熟的 DI 框架的方法，例如 Spring 和上下文以及依赖注入 (CDI)。

本教程假定读者了解[依赖注入模式](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)的基础知识。

## 2.设置

为了在我们的 Maven 项目中使用 Google Guice，我们需要将以下依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.google.inject</groupId>
    <artifactId>guice</artifactId>
    <version>4.1.0</version>
</dependency>

```

[这里](https://search.maven.org/classic/#search|ga|1|g%3A"com.google.inject.extensions" AND v%3A"4.1.0")还有一组 Guice 扩展(我们稍后会介绍)，以及用于扩展 Guice 功能的[第三方模块(主要是通过提供与更成熟的Java框架的集成)。](https://github.com/google/guice/wiki/3rdPartyModules)

## 3. Guice 的基本依赖注入

### 3.1. 我们的示例应用程序

我们将处理一个场景，在该场景中，我们设计的类支持服务台业务中的三种通信方式：电子邮件、SMS 和 IM。

首先，让我们考虑一下类：

```java
public class Communication {
 
    @Inject 
    private Logger logger;
    
    @Inject
    private Communicator communicator;

    public Communication(Boolean keepRecords) {
        if (keepRecords) {
            System.out.println("Message logging enabled");
        }
    }
 
    public boolean sendMessage(String message) {
        return communicator.sendMessage(message);
    }

}
```

这个通信类是通信的基本单元。此类的实例用于通过可用的通信通道发送消息。如上所示，Communication有一个Communicator，我们将使用它来进行实际的消息传输。

Guice 的基本入口点是Injector：

```java
public static void main(String[] args){
    Injector injector = Guice.createInjector(new BasicModule());
    Communication comms = injector.getInstance(Communication.class);
}

```

此主要方法检索我们的Communication类的实例。它还介绍了 Guice 的基本概念：Module(在此示例中使用BasicModule)。模块是定义绑定(或接线，在 Spring 中众所周知)的基本单元。

Guice 采用代码优先的方法进行依赖项注入和管理，因此我们不会处理大量开箱即用的 XML。

在上面的示例中，Communication的依赖树将使用称为即时绑定的功能隐式注入，前提是类具有默认的无参数构造函数。这从一开始就是 Guice 中的一个特性，并且只在 Spring v4.3 之后可用。

### 3.2. Guice 基本绑定

绑定之于 Guice 就像接线之于 Spring。通过绑定，我们定义了 Guice 如何将依赖项注入到类中。

在com.google.inject.AbstractModule的实现中定义了一个绑定：

```java
public class BasicModule extends AbstractModule {
 
    @Override
    protected void configure() {
        bind(Communicator.class).to(DefaultCommunicatorImpl.class);
    }
}
```

此模块实现指定在找到Communicator变量的任何地方都将注入Default CommunicatorImpl的实例。

### 3.3. 命名绑定

这种机制的另一个体现是命名绑定。考虑以下变量声明：

```java
@Inject @Named("DefaultCommunicator")
Communicator communicator;

```

为此，我们将有以下绑定定义：

```java
@Override
protected void configure() {
    bind(Communicator.class)
      .annotatedWith(Names.named("DefaultCommunicator"))
      .to(DefaultCommunicatorImpl.class);
}

```

此绑定将为使用@Named(“DefaultCommunicator”)注解注解的变量提供Communicator实例。

我们还可以看到@Inject和@Named注解似乎是来自 Jakarta EE 的 CDI 的贷款注解，它们确实是。它们在com.google.inject.包中，我们在使用 IDE 时应该小心地从正确的包中导入。

提示：虽然我们刚刚说过使用 Guice 提供的@Inject和@Named，但值得注意的是，Guice 确实提供了对javax.inject.Inject和javax.inject.Named以及其他 Jakarta EE 注解的支持。

### 3.4. 构造函数绑定

我们还可以使用构造函数绑定注入一个没有默认无参数构造函数的依赖项：

```java
public class BasicModule extends AbstractModule {
 
    @Override
    protected void configure() {
        bind(Boolean.class).toInstance(true);
        bind(Communication.class).toConstructor(
          Communication.class.getConstructor(Boolean.TYPE));
}

```

上面的代码片段将使用带有布尔参数的构造函数注入一个Communication实例。我们通过定义Boolean类的非目标绑定来为构造函数提供true参数。

此外，此非目标绑定将急切地提供给绑定中接受布尔参数的任何构造函数。通过这种方法，我们可以注入Communication的所有依赖项。

构造函数特定绑定的另一种方法是实例绑定，我们直接在绑定中提供一个实例：

```java
public class BasicModule extends AbstractModule {
 
    @Override
    protected void configure() {
        bind(Communication.class)
          .toInstance(new Communication(true));
    }    
}
```

无论我们在哪里声明Communication变量，此绑定都将提供Communication类的实例。

然而，在这种情况下，不会自动连接类的依赖树。此外，我们应该限制在不需要任何繁重的初始化或依赖注入的情况下使用这种模式。

## 4.依赖注入的类型

Guice 还支持我们期望使用 DI 模式的标准注入类型。在Communicator类中，我们需要注入不同类型的CommunicationMode。

### 4.1. 字段注入

```java
@Inject @Named("SMSComms")
CommunicationMode smsComms;
```

我们可以使用可选的@Named注解作为限定符，实现基于名字的定向注入。

### 4.2. 方法注入

这里我们将使用一个setter方法来实现注入：

```java
@Inject
public void setEmailCommunicator(@Named("EmailComms") CommunicationMode emailComms) {
    this.emailComms = emailComms;
}

```

### 4.3. 构造函数注入

我们还可以使用构造函数注入依赖项：

```java
@Inject
public Communication(@Named("IMComms") CommunicationMode imComms) {
    this.imComms= imComms;
}

```

### 4.4. 隐式注入

Guice 还将隐式注入一些通用组件，例如Injector和java.util.Logger实例等。请注意，我们在所有示例中都使用记录器，但我们不会为它们找到实际的绑定。

## 5. Guice 中的范围界定

Guice 支持我们在其他 DI 框架中已经习惯的作用域和作用域机制。Guice 默认提供已定义依赖项的新实例。

### 5.1. 单例

让我们在我们的应用程序中注入一个单例：

```java
bind(Communicator.class).annotatedWith(Names.named("AnotherCommunicator"))
  .to(Communicator.class).in(Scopes.SINGLETON);

```

in (Scopes.SINGLETON)指定任何带有@Named(“AnotherCommunicator”)注解的Communicator字段都将注入单例。默认情况下，此单例是延迟启动的。

### 5.2. 渴望单例

然后我们将注入一个渴望的单例：

```java
bind(Communicator.class).annotatedWith(Names.named("AnotherCommunicator"))
  .to(Communicator.class)
  .asEagerSingleton();

```

asEagerSingleton ()调用将单例定义为急切实例化。

除了这两个范围之外，Guice 还支持自定义范围，以及 Jakarta EE 提供的仅限 Web 的@RequestScoped和@SessionScoped注解(这些注解没有 Guice 提供的版本)。

## 6. Guice 中的面向方面编程

Guice 符合 AOPAlliance 的面向方面编程规范。我们可以实现典型的日志记录拦截器，我们将使用它来跟踪示例中的消息发送，只需四个步骤。

第 1 步 – 实施 AOPAlliance 的[MethodInterceptor](http://aopalliance.sourceforge.net/doc/org/aopalliance/intercept/MethodInterceptor.html)：

```java
public class MessageLogger implements MethodInterceptor {

    @Inject
    Logger logger;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] objectArray = invocation.getArguments();
        for (Object object : objectArray) {
            logger.info("Sending message: " + object.toString());
        }
        return invocation.proceed();
    }
}

```

第 2 步 – 定义纯Java注解：

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MessageSentLoggable {
}

```

第 3 步 – 为匹配器定义绑定：

Matcher是一个 Guice 类，我们将使用它来指定我们的 AOP 注解将应用于的组件。在这种情况下，我们希望注解应用于CommunicationMode 的实现：

```java
public class AOPModule extends AbstractModule {

    @Override
    protected void configure() {
        bindInterceptor(
            Matchers.any(),
            Matchers.annotatedWith(MessageSentLoggable.class),
            new MessageLogger()
        );
    }
}

```

在这里，我们指定了一个Matcher，它将把我们的MessageLogger拦截器应用于任何类，该类的方法应用了MessageSentLoggable注解。

第 4 步 – 将我们的注解应用到我们的通信模式并加载我们的模块

```java
@Override
@MessageSentLoggable
public boolean sendMessage(String message) {
    logger.info("SMS message sent");
    return true;
}

public static void main(String[] args) {
    Injector injector = Guice.createInjector(new BasicModule(), new AOPModule());
    Communication comms = injector.getInstance(Communication.class);
}
```

## 七. 总结

了解了基本的 Guice 功能后，我们可以了解 Guice 的灵感来自 Spring。

除了对 [JSR-330](https://github.com/google/guice/wiki/JSR330)的支持外，Guice 还旨在成为一个以注入为中心的 DI 框架(而 Spring 为编程方便提供了一个完整的生态系统，而不仅仅是 DI)，目标是需要 DI 灵活性的开发人员。

[Guice 也是高度可扩展](https://github.com/google/guice/wiki/ExtendingGuice)的，允许程序员编写可移植插件，从而灵活和创造性地使用框架。这是对 Guice 已经为最流行的框架和平台(例如 Servlet、JSF、JPA 和 OSGi 等)提供的广泛集成的补充。