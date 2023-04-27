## 1. 概述

项目生命周期中一个非常普遍的需求是设置集成测试。在本教程中，我们将了解如何使用Maven Cargo插件设置此场景。

## 2. Maven集成测试构建阶段

幸运的是，Maven内置了对这种情况的支持，默认构建生命周期的以下阶段(来自Maven[文档](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#Lifecycle_Reference))：

-   pre-integration-test：在执行集成测试之前执行所需的操作。这可能涉及诸如设置所需环境之类的事情。
-   integration-test：如有必要，将包处理并部署到可以运行集成测试的环境中。
-   post-integration-test：执行集成测试后所需的操作。这可能包括清理环境。

## 3. 设置Cargo插件

让我们逐步了解所需的设置。

### 3.1 从Surefire插件中排除集成测试

首先，配置[maven-surefire-plugin](https://maven.apache.org/plugins/maven-surefire-plugin/)以便将集成测试排除在标准构建生命周期之外：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>
    <configuration>
        <excludes>
            <exclude>**/*IntegrationTest.java</exclude>
        </excludes>
    </configuration>
</plugin>
```

[排除](https://maven.apache.org/plugins/maven-surefire-plugin/examples/inclusion-exclusion.html)是通过ant风格的路径表达式完成的，因此所有集成测试都必须遵循此模式并以“IntegrationTest.java”结尾。

### 3.2 配置Cargo插件

接下来，使用[cargo-maven3-plugin](https://codehaus-cargo.github.io/cargo/Maven+3+Plugin.html)，因为[Cargo](https://codehaus-cargo.github.io/)为嵌入式Web服务器提供了一流的开箱即用支持。当然，如果服务器环境需要特定的配置，cargo也知道如何从归档包中构建服务器以及部署到外部服务器。

```xml
<plugin>
    <groupId>org.codehaus.cargo</groupId>
    <artifactId>cargo-maven3-plugin</artifactId>
    <version>1.9.9</version>
    <configuration>
        <configuration>
            <properties>
                <cargo.servlet.port>8080</cargo.servlet.port>
            </properties>
        </configuration>
    </configuration>
</plugin>
```

定义了默认的嵌入式Jetty 9 Web服务器，监听端口8080。

在较新版本的cargo(1.1.0 以上)中，等待标志的默认值已更改为false，对于cargo:start。这个目标应该只用于运行集成测试并且绑定到Maven生命周期；对于开发，应该执行cargo:run目标—它有wait=true。

为了让maven package阶段生成可部署的war文件，项目的打包必须是<packaging\>war</packaging\>。

### 3.3 添加新的Maven Profile

接下来，创建一个新的集成Maven Profile，以便仅在该Profile处于活动状态时运行集成测试，而不是作为标准构建生命周期的一部分。

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

此Profile将包含所有剩余的配置详细信息。

现在，Jetty服务器配置为在预集成测试阶段启动并在后集成测试阶段停止。

```xml
<plugin>
    <groupId>org.codehaus.cargo</groupId>
    <artifactId>cargo-maven3-plugin</artifactId>
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

这确保了cargo:start目标和cargo:stop目标将在集成测试阶段之前和之后执行。请注意，因为有两个单独的执行定义，所以id元素必须在两者中都存在(并且不同)，这样Maven才能接受配置。

### 3.4 在新Profile中配置集成测试

接下来，需要在integration Profile中覆盖maven-surefire-plugin配置，以便现在包含并运行默认生命周期中排除的集成测试：

```xml
<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
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
                        <include>**/*IntegrationTest.java</include>
                    </includes>
                </configuration>
            </execution>
        </executions>
    </plugin>
</plugins>
```

有几点值得注意：

1. maven-surefire-plugin的测试目标在integration-test阶段执行；此时，Jetty已经启动并部署了项目，因此集成测试应该可以正常运行。

2. 集成测试现在包含在执行中。为了实现这一点，排除项也被覆盖，这是因为Maven处理Profile中覆盖插件配置的方式。

基本配置并没有被完全覆盖，而是在Profile中增加了新的配置元素。

因此，最初排除集成测试的原始<excludes\>配置仍然存在于配置文件中，需要覆盖，否则它会与<includes\>配置冲突，测试仍然无法运行.

3. 请注意，由于只有一个<execution\>元素，因此不需要定义id。

现在，整个过程可以运行了：

```
mvn clean install -Pintegration
```

## 4. 总结

Maven的分步配置涵盖了将集成过程设置为项目生命周期一部分的整个过程。

通常，这被设置为在持续集成环境中运行，最好在每次提交之后运行。如果CI服务器已经有一个服务器在运行并使用端口，那么cargo配置将不得不处理这种情况，我将在以后的帖子中介绍。

如需此机制的完整运行配置，请查看[REST GitHub项目](https://github.com/tu-yucheng/taketoday-tutorial4j/tutorials/tree/master/spring-web-modules/spring-rest-testing)。