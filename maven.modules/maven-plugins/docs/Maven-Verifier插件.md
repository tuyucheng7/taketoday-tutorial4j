## 1. 概述

本教程主要介绍Maven构建工具的核心插件之一，verifier插件。

有关其他核心插件的概述，请参阅[这篇概述文章](https://www.baeldung.com/core-maven-plugins)。

## 2. 插件目标

**verifier插件只有一个目标verify，此目标验证文件和目录是否存在**，可以选择根据正则表达式检查文件内容。

尽管名称如此，但默认情况下verify目标绑定到集成测试阶段(integration-test)而不是验证阶段(verify)。

## 3. 配置

verifier插件只有在明确添加到pom.xml时才会被触发：

```xml
<plugin>
    <artifactId>maven-verifier-plugin</artifactId>
    <version>1.1</version>
    <configuration>
        <verificationFile>input-resources/verifications.xml</verificationFile>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

[此链接](https://search.maven.org/artifact/org.apache.maven.plugins/maven-verifier-plugin)显示插件的最新版本。

**验证文件的默认位置是src/test/verifier/verifications.xml**，如果我们想使用另一个文件，我们必须为verificationFile参数设置一个值。

以下是给定配置中显示的验证文件的内容：

```xml
<verifications
        xmlns="http://maven.apache.org/verifications/1.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/verifications/1.0.0 
        http://maven.apache.org/xsd/verifications-1.0.0.xsd">
    <files>
        <file>
            <location>input-resources/tuyucheng.txt</location>
            <contains>Welcome</contains>
        </file>
    </files>
</verifications>
```

此验证文件确认名为input-resources/tuyucheng.txt的文件存在并且包含单词Welcome，我们之前已经添加了这样一个文件，因此目标执行成功。

## 4. 总结

在本文中，我们介绍了verifier插件并描述了如何自定义它。