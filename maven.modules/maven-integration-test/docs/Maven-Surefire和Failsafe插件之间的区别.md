## 1. 概述

在典型的测试驱动开发中，我们的目标是编写大量可快速运行并独立设置的低级单元测试。此外，也有一些依赖于外部系统的高级集成测试，例如，设置服务器或数据库。不出所料，这些通常既耗费资源又耗时。

**因此，这些测试大多需要一些集成前设置和集成后清理才能正常终止。因此，最好区分这两种类型的测试，并能够在构建过程中分别运行它们**。

在本教程中，我们将比较最常用于在典型[Apache Maven](https://www.baeldung.com/maven-guide)构建中运行各种类型测试的Surefire和Failsafe插件。

## 2. Surefire插件

[Surefire插件](https://www.baeldung.com/maven-surefire-plugin)属于一组[Maven核心插件](https://www.baeldung.com/core-maven-plugins)，运行应用程序的单元测试。

项目POM默认包含此插件，但我们也可以显式配置它：

```xml
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.0.0-M5</version>
                ....
            </plugin>
         </plugins>
    </pluginManagement>
</build>
```

该插件绑定到[默认生命周期](https://www.baeldung.com/maven#introduction-8)的测试阶段，因此，我们可以使用以下命令执行它：

```bash
mvn clean test
```

这将运行我们项目中的所有单元测试。**由于Surefire插件与测试阶段绑定，因此在任何测试失败的情况下，构建都会失败，并且在构建过程中不会执行进一步的阶段**。

或者，我们可以修改插件配置以运行集成测试以及单元测试。但是，对于集成测试来说，这可能不是理想的行为，因为集成测试可能需要在测试之前进行一些环境设置，以及在测试执行之后进行一些清理。

Maven正是为此提供了另一个插件。

## 3. Failsafe插件

[Failsafe插件](https://www.baeldung.com/maven-failsafe-plugin)旨在运行项目中的集成测试。

### 3.1 配置

首先，让我们在项目POM中配置它：

```xml
<plugin>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>3.0.0-M5</version>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
            ....
        </execution>
    </executions>
</plugin>
```

在这里，插件的目标绑定到构建周期的集成测试和验证阶段，以便执行集成测试。

现在，让我们从命令行执行验证阶段：

```bash
mvn clean verify
```

**这将运行所有集成测试，但如果在集成测试阶段有任何测试失败，插件不会立即使构建失败**。

相反，Maven仍然执行后集成测试阶段(post-integration-test)。因此，作为后集成测试阶段的一部分，我们仍然可以执行任何清理和环境拆卸。构建过程的后续验证阶段报告任何测试失败。

### 3.2 例子

在我们的示例中，我们将Jetty服务器配置为在运行集成测试之前启动并在测试执行之后停止。

首先，让我们将Jetty插件添加到我们的POM中：

```xml
<plugin>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-maven-plugin</artifactId>
    <version>9.4.11.v20180605</version>
    ....
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

在这里，我们添加了配置以分别在预集成测试(pre-integration-test)和后集成测试(post-integration-test)阶段启动和停止Jetty服务器。

现在，让我们再次执行集成测试并查看控制台输出：

```bash
....
[INFO] <<< jetty-maven-plugin:9.4.11.v20180605:start (start-jetty) 
  < validate @ maven-integration-test <<<
[INFO] --- jetty-maven-plugin:9.4.11.v20180605:start (start-jetty)
  @ maven-integration-test ---
[INFO] Started ServerConnector@4b9dc62f{HTTP/1.1,[http/1.1]}{0.0.0.0:8999}
[INFO] Started @6794ms
[INFO] Started Jetty Server
[INFO]
[INFO] --- maven-failsafe-plugin:3.0.0-M5:integration-test (default)
  @ maven-integration-test ---
[INFO]
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running cn.tuyucheng.taketoday.maven.it.FailsafeBuildPhaseIntegrationTest
[ERROR] Tests run: 1, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 0.024 s
  <<< FAILURE! - in cn.tuyucheng.taketoday.maven.it.FailsafeBuildPhaseIntegrationTest
[ERROR] cn.tuyucheng.taketoday.maven.it.FailsafeBuildPhaseIntegrationTest.whenTestExecutes_thenPreAndPostIntegrationBuildPhasesAreExecuted
  Time elapsed: 0.012 s  <<< FAILURE!
org.opentest4j.AssertionFailedError: expected: <true> but was: <false>
	at cn.tuyucheng.taketoday.maven.it.FailsafeBuildPhaseIntegrationTest
          .whenTestExecutes_thenPreAndPostIntegrationBuildPhasesAreExecuted(FailsafeBuildPhaseIntegrationTest.java:11)
[INFO]
[INFO] Results:
[INFO]
[ERROR] Failures:
[ERROR]   FailsafeBuildPhaseIntegrationTest.whenTestExecutes_thenPreAndPostIntegrationBuildPhasesAreExecuted:11
  expected: <true> but was: <false>
[INFO]
[ERROR] Tests run: 1, Failures: 1, Errors: 0, Skipped: 0
[INFO]
[INFO] --- jetty-maven-plugin:9.4.11.v20180605:stop (stop-jetty)
  @ maven-integration-test ---
[INFO]
[INFO] --- maven-failsafe-plugin:3.0.0-M5:verify (default)
  @ maven-integration-test ---
[INFO] Stopped ServerConnector@4b9dc62f{HTTP/1.1,[http/1.1]}{0.0.0.0:8999}
[INFO] node0 Stopped scavenging
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
....
```

在这里，根据我们的配置，Jetty服务器在集成测试执行之前启动。为了演示，我们有一个失败的集成测试，但这不会立即使构建失败。后集成测试阶段(post-integration-test)在测试执行之后执行，服务器在构建失败之前停止。

**相反，如果我们使用Surefire Plugin来运行这些集成测试，构建将在集成测试阶段停止而不执行任何必需的清理**。

对不同类型的测试使用不同插件的另一个好处是各种配置之间的分离，这提高了项目构建的可维护性。

## 4. 总结

在本文中，我们比较了用于分离和运行不同类型测试的Surefire和Failsafe插件，我们还演示了一个示例，了解了Failsafe插件如何为运行需要进一步设置和清理的测试提供额外的功能。