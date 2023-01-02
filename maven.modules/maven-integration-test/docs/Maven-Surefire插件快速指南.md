## 1. 概述

本教程演示了surefire插件，它是Maven构建工具的核心插件之一。有关其他核心插件的概述，请参阅[本文](https://www.baeldung.com/core-maven-plugins)。

## 2. 插件目标

**我们可以使用surefire插件运行项目的测试**，默认情况下，此插件在目录target/surefire-reports中生成XML报告。

这个插件只有一个目标test，这个目标绑定到默认构建生命周期的测试阶段，命令mvn test将执行它。

## 3. 配置

surefire插件可以与测试框架JUnit和TestNG一起使用，无论我们使用哪个框架，surefire的行为都是一样的。

默认情况下，surefire会自动包含名称以Test开头或以Test、Tests或TestCase结尾的所有测试类。

但是，我们可以使用<excludes\>和<includes\>参数更改此配置：

```xml
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>
    <configuration>
        <excludes>
            <exclude>DataTest.java</exclude>
        </excludes>
        <includes>
            <include>DataCheck.java</include>
        </includes>
    </configuration>
</plugin>
```

使用此配置，DataCheck类中的测试用例会被执行，而DataTest类中的测试用例则不会。

我们可以在[这里](https://search.maven.org/search?q=a:maven-surefire-plugin)找到该插件的最新版本。

## 4. 总结

在这篇简短的文章中，我们介绍了surefire插件，描述了它的唯一目标以及如何配置它。