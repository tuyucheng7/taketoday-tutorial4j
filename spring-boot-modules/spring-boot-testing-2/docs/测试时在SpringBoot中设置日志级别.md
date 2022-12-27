## 1. 概述

在本教程中，我们将学习如何**在为Spring Boot应用程序运行测试时设置日志级别**。

尽管在测试通过时我们大多可以忽略日志，但如果我们需要**诊断失败的测试**，选择正确的日志级别是至关重要的。

## 2. 日志级别的重要性

正确配置日志级别可以为我们节省大量时间。

例如，如果测试在CI服务器上失败，但在我们的本地机器上通过，**除非我们有足够的日志输出，否则我们很难定位到出现问题的点**。相反，如果我们输出太多过于细节的日志，可能会更难以让我们找到有用的信息。

为了输出适量的日志信息，我们可以微调应用程序包的日志记录级别。如果我们发现某个Java包对我们的测试更重要，我们可以给它一个更低的级别，比如DEBUG。同样，为了避免输出太多不必要的日志，我们可以为不太重要的包配置更高的级别，比如INFO或ERROR。

因此，接下来我们将介绍设置日志输出级别的各种方法。

## 3. 在application.properties设置日志级别

如果我们想在测试中**修改日志级别**，我们可以在src/test/resources/application.properties中设置一个属性：

```properties
logging.level.cn.tuyucheng.taketoday.testloglevel=debug
```

此属性将专门为cn.tuyucheng.taketoday.testloglevel包**设置日志级别**。

同样，我们可以通过**设置根日志级别**来更改所有包的日志级别：

```properties
logging.level.root=info
```

在这里，我们使用原始的xml配置，首先我们在测试目录的resources下新建一个application-logback-test.properties文件用于特定的测试profile：

```properties
#application-logback-test.properties
logging.config=classpath:logback-test.xml
```

下面是引用的logback-test.xml文件，该文件同样位于测试目录的resources下：

```xml
<configuration>
    <include resource="/org/springframework/boot/logging/logback/base.xml"/>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
            </pattern>
        </encoder>
    </appender>
    <root level="error">
        <appender-ref ref="STDOUT"/>
    </root>
    <logger name="cn.tuyucheng.taketoday.testloglevel" level="debug"/>
</configuration>
```

如我们所见，我们为root配置了info级别，而我们自己的包cn.tuyucheng.taketoday.testloglevel配置的是debug级别。

现在让我们通过添加一个输出一些日志的Rest API来测试我们的日志设置：

```java
package cn.tuyucheng.taketoday.testloglevel;

@RestController
public class TestLogLevelController {
    private static final Logger LOG = LoggerFactory.getLogger(TestLogLevelController.class);

    @Autowired
    private OtherComponent otherComponent;

    @GetMapping("/testLogLevel")
    public String testLogLevel() {
        LOG.trace("This is a TRACE log");
        LOG.debug("This is a DEBUG log");
        LOG.info("This is an INFO log");
        LOG.error("This is an ERROR log");

        otherComponent.processData();
        return "Added some log output to console...";
    }
}
```

```java
package cn.tuyucheng.taketoday.component;

public class OtherComponent {
    private static final Logger LOG = LoggerFactory.getLogger(OtherComponent.class);

    public void processData() {
        LOG.trace("This is a TRACE log from another package");
        LOG.debug("This is a DEBUG log from another package");
        LOG.info("This is an INFO log from another package");
        LOG.error("This is an ERROR log from another package");
    }
}
```

正如预期的那样，如果我们在测试中调用此API，我们将能够看到TestLogLevelController打印了DEBUG日志。但是，由于OtherComponent类不是位于cn.tuyucheng.taketoday.testloglevel的包中，因此我们只能看到OtherComponent中打印INFO及以上级别的日志。

```java
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = TestLogLevelApplication.class)
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@ActiveProfiles("logback-test")
public class LogbackTestLogLevelIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Rule
    public OutputCaptureRule outputCapture = new OutputCaptureRule();

    private String baseUrl = "/testLogLevel";

    @Test
    public void givenErrorRootLevelAndDebugLevelForOurPackage_whenCall_thenPrintDebugLogsForOurPackage() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThatOutputContainsLogForOurPackage("DEBUG");
    }

    @Test
    public void givenErrorRootLevelAndDebugLevelForOurPackage_whenCall_thenNoDebugLogsForOtherPackages() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThatOutputDoesntContainLogForOtherPackages("DEBUG");
    }

    @Test
    public void givenErrorRootLevelAndDebugLevelForOurPackage_whenCall_thenPrintErrorLogs() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl, String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThatOutputContainsLogForOurPackage("ERROR");
        assertThatOutputContainsLogForOtherPackages("ERROR");
    }

    private void assertThatOutputContainsLogForOurPackage(String level) {
        assertThat(outputCapture.toString()).containsPattern("TestLogLevelController." + level + ".");
    }

    private void assertThatOutputDoesntContainLogForOtherPackages(String level) {
        assertThat(outputCapture.toString().replaceAll("(?m)^.TestLogLevelController.$", "")).doesNotContain(level);
    }

    private void assertThatOutputContainsLogForOtherPackages(String level) {
        assertThat(outputCapture.toString().replaceAll("(?m)^.TestLogLevelController.$", "")).contains(level);
    }
}
```

```shell
00:11:53.478 [http-nio-auto-1-exec-1] DEBUG [c.t.t.t.TestLogLevelController] >>> This is a DEBUG log 
00:11:53.478 [http-nio-auto-1-exec-1] INFO  [c.t.t.t.TestLogLevelController] >>> This is an INFO log 
00:11:53.478 [http-nio-auto-1-exec-1] ERROR [c.t.t.t.TestLogLevelController] >>> This is an ERROR log 
00:11:53.478 [http-nio-auto-1-exec-1] INFO  [c.t.t.component.OtherComponent] >>> This is an INFO log from another package 
00:11:53.478 [http-nio-auto-1-exec-1] ERROR [c.t.t.component.OtherComponent] >>> This is an ERROR log from another package 
```

像这样设置日志级别非常容易，如果我们的测试是使用@SpringBootTest注解，我们绝对应该这样做。但是，如果我们不使用该注解，我们将不得不以不同的方式配置日志级别。

### 3.1 基于Profiles的日志设置

尽管将属性配置放在src/test/application.properties文件中在大多数情况下都有效，但在某些情况下，我们可能希望**为一个测试或一组测试设置不同的配置**。

在这种情况下，我**们可以使用@ActiveProfiles注解将Spring Profile添加到我们的测试中**：

```java
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = TestLogLevelApplication.class)
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@ActiveProfiles("logging-test")
public class TestLogLevelWithProfileIntegrationTest {
    // ...
}
```

然后，我们的日志配置将放置在src/test/resources/application-logging-test.properties文件中:

```properties
#src/test/resources/application-logging-test.properties
logging.level.cn.tuyucheng.taketoday.testloglevel=TRACE
logging.level.root=ERROR
```

如果我们使用以上配置从我们的测试中调用TestLogLevelController，我们可以看到TestLogLevelController打印了TRACE日志，而位于cn.tuyucheng.taketoday.testloglevel包之外的类，例如OtherComponent只会打印ERROR级别的日志：

```shell
00:30:28.118 [http-nio-auto-3-exec-1] TRACE [c.t.t.t.TestLogLevelController] >>> This is a TRACE log 
00:30:28.118 [http-nio-auto-3-exec-1] DEBUG [c.t.t.t.TestLogLevelController] >>> This is a DEBUG log 
00:30:28.118 [http-nio-auto-3-exec-1] INFO  [c.t.t.t.TestLogLevelController] >>> This is an INFO log 
00:30:28.118 [http-nio-auto-3-exec-1] ERROR [c.t.t.t.TestLogLevelController] >>> This is an ERROR log 
00:30:28.118 [http-nio-auto-3-exec-1] ERROR [c.t.t.component.OtherComponent] >>> This is an ERROR log from another package 
```

## 4. 配置Logback

如果我们使用在Spring Boot中默认使用的Logback作为日志实现，我们可以**在src/test/resources中的logback-test.xml文件中设置日志级别**：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
            </pattern>
        </encoder>
    </appender>

    <logger name="cn.tuyucheng.taketoday.testloglevel" level="debug"/>

    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

上面的示例显示了如何在我们的Logback配置中为测试设置日志级别。根日志级别设置为INFO，我们的cn.tuyucheng.taketoday.testloglevel包的日志级别设置为DEBUG。

同样，让我们在应用上面的设置后检查日志输出：

```shell
14:56:58.457 [http-nio-auto-3-exec-1] DEBUG [c.t.t.t.TestLogLevelController] >>> This is a DEBUG log 
14:56:58.457 [http-nio-auto-3-exec-1] INFO  [c.t.t.t.TestLogLevelController] >>> This is an INFO log 
14:56:58.457 [http-nio-auto-3-exec-1] ERROR [c.t.t.t.TestLogLevelController] >>> This is an ERROR log 
14:56:58.457 [http-nio-auto-3-exec-1] INFO  [c.t.t.component.OtherComponent] >>> This is an INFO log from another package 
14:56:58.457 [http-nio-auto-3-exec-1] ERROR [c.t.t.component.OtherComponent] >>> This is an ERROR log from another package
```

### 4.1 基于Profile的Logback配置

为我们的测试**设置特定于Profile的配置**的另一种方法是在application.properties中为我们的Profile设置logging.config属性：

```properties
logging.config=classpath:logback-test.xml
```

或者，**如果我们希望类路径上只有一个单一的Logback配置文件**，我们可以使用logback.xml中的<springProfile\>标签：

```xml
<configuration>
    <include resource="/org/springframework/boot/logging/logback/base.xml"/>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
            </pattern>
        </encoder>
    </appender>

    <root level="error">
        <appender-ref ref="STDOUT"/>
    </root>

    <springProfile name="logback-test1">
        <logger name="cn.tuyucheng.taketoday.testloglevel" level="info"/>
    </springProfile>

    <springProfile name="logback-test2">
        <logger name="cn.tuyucheng.taketoday.testloglevel" level="trace"/>
    </springProfile>
</configuration>
```

现在，如果我们使用Profile logback-test1在测试中调用TestLogLevelController，我们将得到以下输出：

```shell
15:14:57.422 [http-nio-auto-1-exec-1] INFO  [c.t.t.t.TestLogLevelController] >>> This is an INFO log 
15:14:57.422 [http-nio-auto-1-exec-1] ERROR [c.t.t.t.TestLogLevelController] >>> This is an ERROR log 
15:14:57.422 [http-nio-auto-1-exec-1] INFO  [c.t.t.component.OtherComponent] >>> This is an INFO log from another package 
15:14:57.422 [http-nio-auto-1-exec-1] ERROR [c.t.t.component.OtherComponent] >>> This is an ERROR log from another package 
```

相反，如果我们将Profile更改为logback-test2，则输出将为：

```shell
15:14:57.422 [http-nio-auto-1-exec-1] DEBUG [c.t.t.t.TestLogLevelController] >>> This is a DEBUG log 
15:14:57.422 [http-nio-auto-1-exec-1] INFO  [c.t.t.t.TestLogLevelController] >>> This is an INFO log 
15:14:57.422 [http-nio-auto-1-exec-1] ERROR [c.t.t.t.TestLogLevelController] >>> This is an ERROR log 
15:14:57.422 [http-nio-auto-1-exec-1] INFO  [c.t.t.component.OtherComponent] >>> This is an INFO log from another package 
15:14:57.422 [http-nio-auto-1-exec-1] ERROR [c.t.t.component.OtherComponent] >>> This is an ERROR log from another package 
```

## 5. Log4J替代方案

或者，如果我们使用Log4J2，我们可以**在src/test/resources中的log4j2-spring.xml文件中设置日志级别**：

```xml
<Configuration>
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"/>
        </Console>
    </Appenders>

    <Loggers>
        <Logger name="cn.tuyucheng.taketoday.testloglevel" level="debug"/>

        <Root level="info">
            <AppenderRef ref="Console"/>
        </Root>
    </Loggers>
</Configuration>
```

我们可以通过在application.properties中设置logging.config属性来设置Log4J配置文件的路径：

```properties
logging.config=classpath:log4j-testloglevel.xml
```

最后，让我们检查应用上述设置后的输出：

```shell
2022-12-27 14:08:27.545 DEBUG 56585 --- [nio-8080-exec-1] c.t.t.testloglevel.TestLogLevelController  : This is a DEBUG log
2022-12-27 14:08:27.545  INFO 56585 --- [nio-8080-exec-1] c.t.t.testloglevel.TestLogLevelController  : This is an INFO log
2022-12-27 14:08:27.546 ERROR 56585 --- [nio-8080-exec-1] c.t.t.testloglevel.TestLogLevelController  : This is an ERROR log
2022-12-27 14:08:27.546  INFO 56585 --- [nio-8080-exec-1] c.t.t.component.OtherComponent  : This is an INFO log from another package
2022-12-27 14:08:27.546 ERROR 56585 --- [nio-8080-exec-1] c.t.t.component.OtherComponent  : This is an ERROR log from another package
```

## 6. 总结

在本文中，我们学习了**如何在测试Spring Boot应用程序时设置日志级别**，并演示了许多不同的配置方式。

当我们使用@SpringBootTest注解时，在Spring Boot的application.properties文件中设置日志级别是最简单的选择。