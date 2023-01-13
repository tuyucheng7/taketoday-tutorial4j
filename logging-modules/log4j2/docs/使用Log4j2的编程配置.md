## 1. 简介

在本教程中，我们将了解以编程方式配置 Apache Log4j 2 的不同方法。

## 2.初始设置

要开始使用 Log4j 2，我们只需要在我们的pom.xml中包含[log4j-core](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.logging.log4j" AND a%3A"log4j-core")和[log4j-slf4j-impl](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.logging.log4j" AND a%3A"log4j-slf4j-impl")依赖项：

```xml
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.11.0</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-slf4j-impl</artifactId>
    <version>2.11.0</version>
</dependency>
```

## 3.配置生成器

一旦我们配置了 Maven，然后我们需要创建一个ConfigurationBuilder，这是一个让我们配置 附加程序、过滤器、布局 和记录器的类。

Log4j 2 提供了几种获取 ConfigurationBuilder的方法。

让我们从最直接的方式开始：

```java
ConfigurationBuilder<BuiltConfiguration> builder
 = ConfigurationBuilderFactory.newConfigurationBuilder();
```

为了开始配置组件， ConfigurationBuilder为每个组件配备了相应的 新方法，如 newAppender 或 newLayout 。

一些组件有不同的子类型，比如FileAppender或ConsoleAppender，这些在 API 中被称为 插件。

### 3.1. 配置附加程序

让我们通过配置appender告诉构建器将每个日志行发送到哪里 ：

```java
AppenderComponentBuilder console 
  = builder.newAppender("stdout", "Console"); 

builder.add(console);

AppenderComponentBuilder file 
  = builder.newAppender("log", "File"); 
file.addAttribute("fileName", "target/logging.log");

builder.add(file);
```

虽然大多数新方法不支持这一点， 但 newAppender(name, plugin)允许我们给 appender 一个名字，这在以后会很重要。这些 appender，我们称为 stdout和 log， 尽管我们可以给它们起任何名字。

我们还告诉 构建器要使用哪个 appender 插件 (或者更简单地说，是哪种 appender)。控制台和 文件 分别指的是 Log4j 2 的附加程序，用于写入标准输出和文件系统。

尽管[Log4j 2 支持多个 appender](https://logging.apache.org/log4j/2.x/manual/appenders.html)，但使用Java配置它们可能有点棘手，因为AppenderComponentBuilder 是所有 appender 类型的通用类。

这使得它具有 addAttribute和 addComponent 之类的方法，而不是 setFileName和 addTriggeringPolicy：

```java
AppenderComponentBuilder rollingFile 
  = builder.newAppender("rolling", "RollingFile");
rollingFile.addAttribute("fileName", "rolling.log");
rollingFile.addAttribute("filePattern", "rolling-%d{MM-dd-yy}.log.gz");

builder.add(rollingFile);

```

最后，不要忘记调用builder.add将其附加到主配置中！

### 3.2. 配置过滤器

我们可以向每个附加程序添加过滤器，这些附加程序决定每个日志行是否应该附加。

让我们在控制台附加程序上使用 MarkerFilter 插件：

```java
FilterComponentBuilder flow = builder.newFilter(
  "MarkerFilter", 
  Filter.Result.ACCEPT,
  Filter.Result.DENY);  
flow.addAttribute("marker", "FLOW");

console.add(flow);
```

请注意，此新方法不允许我们为过滤器命名，但它会要求我们指示过滤器通过或失败时要做什么。

在这种情况下，我们保持简单，说明如果 MarkerFilter通过，则 接受日志行。否则， 拒绝 它。

请注意，在这种情况下，我们不会将其附加到构建器，而是附加到我们要使用此过滤器的附加程序。

### 3.3. 配置布局

接下来，让我们定义每个日志行的布局。在这种情况下，我们将使用 PatternLayout插件：

```java
LayoutComponentBuilder standard 
  = builder.newLayout("PatternLayout");
standard.addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable");

console.add(standard);
file.add(standard);
rolling.add(standard);
```

同样，我们将这些直接添加到适当的附加程序中，而不是直接添加到 构建器中。

### 3.4. 配置根记录器

现在我们知道日志将被发送到哪里，我们想要配置哪些日志将发送到每个目的地。

root logger是最高级别的logger，有点像Java中的 Object。除非被覆盖，否则默认情况下将使用此记录器。

因此，让我们使用根记录器将默认日志记录级别设置为 ERROR，并将默认附加程序设置为上面的 标准输出附加程序：

```java
RootLoggerComponentBuilder rootLogger 
  = builder.newRootLogger(Level.ERROR);
rootLogger.add(builder.newAppenderRef("stdout"));

builder.add(rootLogger);
```

为了将我们的记录器指向特定的附加程序，我们不给它一个构建器的实例。相反，我们用我们之前给它的 名字来指代它。

### 3.5. 配置额外的记录器

子记录器可用于定位特定包或记录器名称。

让我们在我们的应用程序中为com包添加一个记录器，将日志记录级别设置为 DEBUG并将它们转到我们的 日志 附加程序：

```java
LoggerComponentBuilder logger = builder.newLogger("com", Level.DEBUG);
logger.add(builder.newAppenderRef("log"));
logger.addAttribute("additivity", false);

builder.add(logger);
```

请注意，我们可以使用我们的记录器设置可加性 ，这表明该记录器是否应该从其祖先那里继承诸如日志记录级别和附加程序类型之类的属性。

### 3.6. 配置其他组件

并非所有组件在ConfigurationBuilder上都有专用的新方法 。

所以，在这种情况下，我们调用newComponent。

例如，因为没有TriggeringPolicyComponentBuilder，我们需要使用newComponent 来指定滚动文件附加程序的触发策略：

```java
ComponentBuilder triggeringPolicies = builder.newComponent("Policies")
  .addComponent(builder.newComponent("CronTriggeringPolicy")
    .addAttribute("schedule", "0 0 0   ?"))
  .addComponent(builder.newComponent("SizeBasedTriggeringPolicy")
    .addAttribute("size", "100M"));
 
rolling.addComponent(triggeringPolicies);
```

### 3.7. 等效的 XML

ConfigurationBuilder 配备了一个方便的方法来打印出等效的 XML：

```java
builder.writeXmlConfiguration(System.out);
```

运行上面的行打印出：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration>
   <Appenders>
      <Console name="stdout">
         <PatternLayout pattern="%d [%t] %-5level: %msg%n%throwable" />
         <MarkerFilter onMatch="ACCEPT" onMisMatch="DENY" marker="FLOW" />
      </Console>
      <RollingFile name="rolling" 
        fileName="target/rolling.log" 
        filePattern="target/archive/rolling-%d{MM-dd-yy}.log.gz">
         <PatternLayout pattern="%d [%t] %-5level: %msg%n%throwable" />
         <Policies>
            <CronTriggeringPolicy schedule="0 0 0   ?" />
            <SizeBasedTriggeringPolicy size="100M" />
         </Policies>
      </RollingFile>
      <File name="FileSystem" fileName="target/logging.log">
         <PatternLayout pattern="%d [%t] %-5level: %msg%n%throwable" />
      </File>
   </Appenders>
   <Loggers>
      <Logger name="com" level="DEBUG" additivity="false">
         <AppenderRef ref="log" />
      </Logger>
      <Root level="ERROR" additivity="true">
         <AppenderRef ref="stdout" />
      </Root>
   </Loggers>
</Configuration>
```

当我们想要仔细检查我们的配置或者如果我们想要将我们的配置持久化到文件系统时，这会派上用场。

### 3.8. 把它们放在一起

现在我们已经完全配置好了，让我们告诉 Log4j 2 使用我们的配置：

```java
Configurator.initialize(builder.build());
```

调用后，以后对 Log4j 2 的调用将使用我们的配置。

请注意，这意味着我们需要在调用 LogManager.getLogger 之前 调用Configurator.initialize。

## 4.配置工厂

现在我们已经看到了一种获取和应用 ConfigurationBuilder的方法，让我们再看一个：

```java
public class CustomConfigFactory
  extends ConfigurationFactory {
 
    public Configuration createConfiguration(
      LoggerContext context, 
      ConfigurationSource src) {
 
        ConfigurationBuilder<BuiltConfiguration> builder = super
          .newConfigurationBuilder();

        // ... configure appenders, filters, etc.

        return builder.build();
    }

    public String[] getSupportedTypes() { 
        return new String[] { "" };
    }
}
```

在这种情况下，我们没有使用 ConfigurationBuilderFactory ，而是将ConfigurationFactory子类 化，这是一个抽象类，旨在创建 Configuration的实例。

然后，不像我们第一次那样调用 Configurator.initialize，我们只需要让 Log4j 2 知道我们的新配置工厂。

可以通过三种方式做到这一点：

-   静态初始化
-   运行时属性，或
-   @Plugin 注解_ 

### 4.1. 使用静态初始化

Log4j 2 支持 在静态初始化期间调用setConfigurationFactory ：

```java
static {
    ConfigurationFactory custom = new CustomConfigFactory();
    ConfigurationFactory.setConfigurationFactory(custom);
}
```

这种方法与我们看到的最后一种方法有相同的限制，即我们需要在调用 LogManager.getLogger之前调用它。

### 4.2. 使用运行时属性

如果我们可以访问Java启动命令，那么 Log4j 2 还支持 通过-D参数指定要使用 的ConfigurationFactory ：

```bash
-Dlog4j2.configurationFactory=com.baeldung.log4j2.CustomConfigFactory
```

这种方法的主要好处是我们不必像使用前两种方法那样担心初始化顺序。

### 4.3. 使用 @Plugin 注解

最后，在我们不想通过添加 -D来修改Java启动命令的情况下，我们可以简单地使用 Log4j 2 @Plugin 注解来注解我们的CustomConfigurationFactory：

```java
@Plugin(
  name = "CustomConfigurationFactory", 
  category = ConfigurationFactory.CATEGORY)
@Order(50)
public class CustomConfigFactory
  extends ConfigurationFactory {

  // ... rest of implementation
}
```

Log4j 2 将扫描具有 @Plugin 注解的类的类路径，并在ConfigurationFactory 类别中找到此类 ，将使用它。

### 4.4. 结合静态配置

使用 ConfigurationFactory扩展的另一个好处是我们可以轻松地将我们的自定义配置与其他配置源(如 XML)结合起来：

```java
public Configuration createConfiguration(
  LoggerContext context, 
  ConfigurationSource src) {
    return new WithXmlConfiguration(context, src);
}

```

source参数表示 Log4j 2 找到的静态 XML 或 JSON 配置文件(如果有)。 

我们可以获取该配置文件并将其发送到我们自定义的 XmlConfiguration实现，我们可以在其中放置我们需要的任何覆盖配置：

```java
public class WithXmlConfiguration extends XmlConfiguration {
 
    @Override
    protected void doConfigure() {
        super.doConfigure(); // parse xml document

        // ... add our custom configuration
    }
}
```

## 5.总结

在本文中，我们研究了如何使用Log4j 2 中可用的新ConfigurationBuilder API。

我们还研究了 结合ConfigurationBuilder自定义ConfigurationFactory 以用于更高级的用例。