## 1. 简介

监控对于查找错误和优化性能非常有帮助。我们可以手动检测我们的代码以添加计时器和日志记录，但这会导致大量分散注意力的样板文件。

另一方面，我们可以使用由注解驱动的监控框架，例如[Dropwizard Metrics](https://www.baeldung.com/dropwizard-metrics)。

在本教程中，我们将使用[Metrics AspectJ](https://github.com/astefanutti/metrics-aspectj)和 Dropwizard Metrics @Timed注解检测一个简单的类。

## 2.Maven 设置

首先，让我们将 Metrics AspectJ Maven 依赖项添加到我们的项目中：

```xml
<dependency>
    <groupId>io.astefanutti.metrics.aspectj</groupId>
    <artifactId>metrics-aspectj</artifactId>
    <version>1.2.0</version>
    <exclusions>
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>io.astefanutti.metrics.aspectj</groupId>
    <artifactId>metrics-aspectj-deps</artifactId>
    <version>1.2.0</version>
</dependency>
```

我们使用[metrics-aspectj](https://search.maven.org/search?q=a:metrics-aspectj AND g:io.astefanutti.metrics.aspectj)通过面向方面的编程提供指标，并使用[metrics-aspectj-deps](https://search.maven.org/search?q=a:metrics-aspectj-deps AND g:io.astefanutti.metrics.aspectj)提供其依赖项。

我们还需要[aspectj-maven-plugin](https://search.maven.org/search?q=g:org.codehaus.mojo AND a:aspectj-maven-plugin)来设置指标注解的编译时处理：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>aspectj-maven-plugin</artifactId>
    <version>1.8</version>
    <configuration>
        <complianceLevel>1.8</complianceLevel>
        <source>1.8</source>
        <target>1.8</target>
        <aspectLibraries>
            <aspectLibrary>
                <groupId>io.astefanutti.metrics.aspectj</groupId>
                <artifactId>metrics-aspectj</artifactId>
            </aspectLibrary>
        </aspectLibraries>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

现在我们的项目已准备好检测一些Java代码。

## 3.注解检测

首先，让我们创建一个方法并用@Timed注解对其进行注解。我们还将用我们的计时器的名称填充name属性：

```java
import com.codahale.metrics.annotation.Timed;
import io.astefanutti.metrics.aspectj.Metrics;

@Metrics(registry = "objectRunnerRegistryName")
public class ObjectRunner {

    @Timed(name = "timerName")
    public void run() throws InterruptedException {
        Thread.sleep(1000L);
    }
}
```

我们在类级别使用@Metrics注解让 Metrics AspectJ 框架知道这个类有要监视的方法。我们将@Timed放在创建计时器的方法上。

此外， @Metrics使用提供的注册表名称(在本例中为 objectRunnerRegistryName)创建一个注册表 来存储指标。

我们的示例代码只休眠一秒钟来模拟操作。

现在，让我们定义一个类来启动应用程序并配置我们的MetricsRegistry：

```java
public class ApplicationMain {
    static final MetricRegistry registry = new MetricRegistry();

    public static void main(String args[]) throws InterruptedException {
        startReport();

        ObjectRunner runner = new ObjectRunner();

        for (int i = 0; i < 5; i++) {
            runner.run();
        }

        Thread.sleep(3000L);
    }

    static void startReport() {
        SharedMetricRegistries.add("objectRunnerRegistryName", registry);

        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .outputTo(new PrintStream(System.out))
                .build();
        reporter.start(3, TimeUnit.SECONDS);
    }
}
```

在ApplicationMain的startReport方法中，我们使用与@Metrics中使用的相同注册表名称将注册表实例设置为SharedMetricRegistries。

之后，我们创建一个简单的ConsoleReporter来报告来自@Timed注解方法的指标。我们应该注意到还有[其他类型的记者可用](https://metrics.dropwizard.io/3.1.0/apidocs/com/codahale/metrics/Reporter.html)。

我们的应用程序将调用 timed 方法五次。让我们用 Maven 编译它然后执行它：

```shell
-- Timers ----------------------------------------------------------------------
ObjectRunner.timerName
             count = 5
         mean rate = 0.86 calls/second
     1-minute rate = 0.80 calls/second
     5-minute rate = 0.80 calls/second
    15-minute rate = 0.80 calls/second
               min = 1000.49 milliseconds
               max = 1003.00 milliseconds
              mean = 1001.03 milliseconds
            stddev = 1.10 milliseconds
            median = 1000.54 milliseconds
              75% <= 1001.81 milliseconds
              95% <= 1003.00 milliseconds
              98% <= 1003.00 milliseconds
              99% <= 1003.00 milliseconds
            99.9% <= 1003.00 milliseconds
```

正如我们所见，度量框架为我们提供了详细的统计信息，只需对我们想要检测的方法进行很少的代码更改。

我们应该注意，在没有 Maven 构建的情况下运行应用程序——例如，通过 IDE——可能无法获得上述输出。我们需要确保 AspectJ 编译插件包含在构建中才能正常工作。

## 4. 总结

在本教程中，我们研究了如何使用 Metrics AspectJ 检测简单的Java应用程序。

我们发现 Metrics AspectJ 注解是检测代码的好方法，无需像 Spring、JEE 或 Dropwizard 这样的大型应用程序框架。相反，通过使用方面，我们能够在编译时添加拦截器。