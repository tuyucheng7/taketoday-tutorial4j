## 1. 概述

这个简单实用的教程描述了failsafe插件，它是Maven构建工具的核心插件之一。

有关其他核心插件的概述，请参阅[本文](https://www.baeldung.com/core-maven-plugins)。

## 2. 插件目标

**failsafe插件用于项目的集成测试**，它有两个目标：

-   integration-test：运行集成测试；默认情况下，此目标绑定到集成测试阶段
-   verify：验证集成测试是否通过；默认情况下，此目标绑定到验证(verify)阶段

## 3. 目标执行

这个插件就像surefire插件一样在测试类中运行方法，我们可以以类似的方式配置这两个插件。但是，它们之间存在一些重要的区别。

首先，与包含在super pom.xml中的surefire(参见[这篇文章](https://www.baeldung.com/maven-surefire-plugin))不同，failsafe插件及其目标必须在pom.xml中明确指定为构建生命周期的一部分：

```xml
<plugin>
    <artifactId>maven-failsafe-plugin</artifactId>
    <version>2.21.0</version>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
            <configuration>
                ...
            </configuration>
        </execution>
    </executions>
</plugin>
```

这个插件的最新版本在[这里](https://search.maven.org/artifact/org.apache.maven.plugins/maven-failsafe-plugin)可以找到。

其次，failsafe插件使用不同的目标运行和验证测试，**集成测试阶段(integration-test)的测试失败不会立即使构建失败，从而允许执行后集成测试阶段(post-integration-test)**，在该阶段执行清理操作。

失败的测试(如果有的话)只会在验证阶段报告，即正确拆除集成测试环境后才报告。

## 4. 总结

在本文中，我们介绍了failsafe插件，并将其与另一种用于测试的流行插件surefire插件进行了比较。