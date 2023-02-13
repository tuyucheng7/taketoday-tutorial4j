## 1. 简介

在本教程中，我们将介绍有助于减少Spring Boot启动时间的不同配置和设置。首先，我们将回顾一下Spring特定的配置。其次，我们将介绍Java虚拟机选项。最后，我们将介绍如何利用GraalVM和本机镜像编译来进一步缩短启动时间。

## 2. Spring调整

在开始之前，让我们设置一个测试应用程序。我们将使用Spring Boot版本2.5.4，并将Spring Web、Spring Actuator和Spring Security作为依赖项。在pom.xml中，我们将添加带有配置的spring-boot-maven-plugin以将我们的应用程序打包到一个jar文件中：

```xml
<plugin> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-maven-plugin</artifactId> 
    <version>${spring-boot.version}</version> 
    <configuration> 
        <finalName>springStartupApp</finalName> 
        <mainClass>cn.tuyucheng.taketoday.springStart.SpringStartApplication</mainClass> 
    </configuration> 
    <executions> 
        <execution> 
            <goals> 
                <goal>repackage</goal> 
            </goals> 
        </execution> 
    </executions> 
</plugin>
```

我们使用标准的java -jar命令运行我们的jar文件并监控我们应用程序的启动时间：

```shell
c.t.t.springstart.SpringStartApplication: Started SpringStartApplication in 3.403 seconds (JVM running for 3.961)
```

如我们所见，我们的应用程序在大约3.4秒后启动。我们将使用这段时间作为未来调整的参考。

### 2.1 惰性初始化

Spring框架支持惰性初始化。**[惰性初始化](https://www.baeldung.com/spring-boot-lazy-initialization)意味着Spring不会在启动时创建所有bean。此外，在需要该bean之前，Spring不会注入任何依赖项**。从Spring Boot版本2.2开始。可以使用application.properties启用延迟初始化：

```properties
spring.main.lazy-initialization=true
```

在构建一个新的jar文件并像前面的例子一样启动它之后，新的启动时间稍微好一些：

```shell
c.t.t.springstart.SpringStartApplication: Started SpringStartApplication in 2.95 seconds (JVM running for 3.497)
```

**根据我们代码库的大小，惰性初始化可以显著减少启动时间**。减少取决于我们应用程序的依赖图。

此外，延迟初始化在使用DevTools热重启功能的开发过程中也有好处。增加延迟初始化的重启次数将使JVM更好地优化代码。

但是，惰性初始化有一些缺点。最显著的缺点是应用程序处理第一个请求的速度较慢。因为Spring需要时间来初始化所需的bean，另一个缺点是我们可能会在启动时错过一些错误。这可能会导致运行时出现ClassNotFoundException。

### 2.2 排除不必要的自动配置

Spring Boot总是倾向于约定优于配置。**Spring可能会初始化我们的应用程序不需要的beans**。我们可以使用启动日志检查所有自动配置的beans。在application.properties中的org.springframework.boot.autoconfigure上将日志记录级别设置为DEBUG：

```properties
logging.level.org.springframework.boot.autoconfigure=DEBUG
```

在日志中，我们将看到专用于自动配置的新行，以：

```shell
============================
CONDITIONS EVALUATION REPORT
============================
```

使用此报告，我们可以排除部分应用程序配置。为了排除部分配置，我们使用@EnableAutoConfiguration注解：

```java
@EnableAutoConfiguration(exclude = {JacksonAutoConfiguration.class, JvmMetricsAutoConfiguration.class, 
    LogbackMetricsAutoConfiguration.class, MetricsAutoConfiguration.class})
```

如果我们排除Jackson JSON库和一些我们不使用的指标配置，我们可以在启动时节省一些时间：

```shell
c.t.t.springstart.SpringStartApplication: Started SpringStartApplication in 3.183 seconds (JVM running for 3.732)
```

### 2.3 其他小调整

Spring Boot带有一个嵌入式Servlet容器。默认情况下，我们获取Tomcat。**虽然Tomcat在大多数情况下已经足够好了，但其他Servlet容器的性能可能更高**。[在测试中](https://www.baeldung.com/spring-boot-servlet-containers)，来自JBoss的Undertow表现优于Tomcat或Jetty。它需要更少的内存并具有更好的平均响应时间。要切换到Undertow，我们需要更改pom.xml：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-undertow</artifactId>
</dependency>
```

以下小改进可以在类路径扫描中进行。Spring类路径扫描是快速操作。当我们有一个大型代码库时，我们可以通过创建静态索引来缩短启动时间。**我们需要在spring-context-indexer中添加一个依赖来生成索引**。Spring不需要任何额外的配置。在编译期间，Spring将在META-INF\spring.components中创建一个附加文件。Spring会在启动时自动使用它：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context-indexer</artifactId>
    <version>${spring.version}</version>
    <optional>true</optional>
</dependency>
```

由于我们只有一个Spring组件，因此这种调整在我们的测试中没有产生显著的结果。

**接下来，application.properties(或.yml)文件有几个有效位置**。最常见的是在类路径根目录或与jar文件相同的文件夹中。我们可以通过使用spring.config.location参数设置显式路径来避免搜索多个位置，并节省几毫秒的搜索时间：

```shell
java -jar .\target\springStartupApp.jar --spring.config.location=classpath:/application.properties
```

最后，Spring Boot提供了一些MBean来使用JMX监控我们的应用程序。**完全关闭JMX并避免创建这些bean的成本**：

```properties
spring.jmx.enabled=false
```

## 3. JVM调整

### 3.1 Verify标志

该标志设置字节码验证器模式。字节码验证提供类的格式是否正确以及是否在JVM规范约束内。我们在启动期间在JVM设置此标志。

这个标志有几个选项：

-   **-Xverify是默认值并启用对所有非引导加载程序类的验证。** 
-   **-Xverify:all启用所有类的验证**。此设置将对初创公司产生重大的负面性能影响。
-   **-Xverify:none(或-Xnoverify)**。此选项会完全禁用验证程序，并将显著减少启动时间。

我们可以在启动时传递这个标志：


```shell
java -jar -noverify .\target\springStartupApp.jar 
```

我们将收到来自JVM的警告，指出此选项已弃用。此外，启动时间将减少：

```shell
c.t.t.springstart.SpringStartApplication: Started SpringStartApplication in 3.193 seconds (JVM running for 3.686)
```

这个标志带来了重要的权衡。我们的应用程序可能会在运行时中断，并出现我们可以更早捕获的错误。这是此选项在Java 13中被标记为已弃用的原因之一，因此它将在未来的版本中删除。

### 3.2 分层编译标志

Java 7引入了[分层编译](https://www.baeldung.com/jvm-tiered-compilation)。HotSpot编译器将对代码使用不同级别的编译。

众所周知，Java代码首先被解释为字节码。接下来，字节码被编译成机器码。这种转换发生在方法级别。C1编译器在一定数量的调用后编译一个方法。在运行更多次之后，C2编译器编译它进一步提高性能。

**使用-XX:-TieredCompilation标志，我们可以禁用中间编译层**。这意味着我们的方法将使用C2编译器进行解释或编译，以实现最大优化。这不会导致启动速度下降。我们需要的是禁用C2编译。我们可以使用-XX:TieredStopAtLevel=1选项来做到这一点。结合-noverify标志，这可以减少启动时间。不幸的是，这会在后期减慢JIT编译器的速度。

TieredCompilation标志单独带来了坚实的改进：

```shell
c.t.t.springstart.SpringStartApplication: Started SpringStartApplication in 2.754 seconds (JVM running for 3.172)
```

更有趣的是，结合运行本节中的两个标志可以进一步减少启动时间：

```shell
java -jar -XX:TieredStopAtLevel=1 -noverify .\target\springStartupApp.jar
c.t.t.springstart.SpringStartApplication: Started SpringStartApplication in 2.537 seconds (JVM running for 2.912)
```

## 4. Spring Native

本机映像是使用提前编译器编译并打包到可执行文件中的Java代码。它不需要运行Java。由于没有JVM开销，生成的程序速度更快且对内存的依赖更少。[GraalVM](https://www.baeldung.com/graal-java-jit-compiler)项目引入了本机镜像和所需的构建工具。

**[Spring Native](https://www.baeldung.com/spring-native-intro)是一个实验模块，支持使用GraalVM本机镜像编译器对Spring应用程序进行本机编译**。提前编译器在构建期间执行多项任务以减少启动时间(静态分析、删除未使用的代码、创建固定的类路径等)。原生镜像仍然存在一些限制：

-   它不支持所有Java功能
-   反射需要特殊配置
-   延迟类加载不可用
-   Windows兼容性是一个问题

要将应用程序编译为原生镜像，我们需要在pom.xml中添加spring-aot和spring-aot-maven-plugin依赖。Maven将在target文件夹中的package命令上创建本机镜像。

## 5. 总结

在本文中，我们探讨了改进Spring Boot应用程序启动时间的不同方法。首先，我们介绍了各种有助于减少启动时间的Spring相关功能。接下来，我们展示了特定于JVM的选项。最后，我们介绍了Spring Native和原生镜像的创建。