## 1. 概述

本快速教程介绍了clean插件，它是Maven构建工具的核心插件之一。

有关其他核心插件的概述，请参阅[本文](https://www.baeldung.com/core-maven-plugins)。

## 2. 插件目标

clean生命周期只有一个名为clean的阶段，它会自动绑定到具有相同名称的插件的唯一目标。因此，可以使用命令`mvn clean`执行此目标。

clean插件已经包含在super POM中，因此我们可以使用它而无需在项目的POM中指定任何内容。

顾名思义，**该插件的作用就是清理上次构建时生成的文件和目录**，默认情况下，插件会删除target目录。

## 3. 配置

我们可以使用<filesets\>参数添加要清理的目录：

```xml
<plugin>
    <artifactId>maven-clean-plugin</artifactId>
    <version>3.0.0</version>
    <configuration>
        <filesets>
            <fileset>
                <directory>output-resources</directory>
            </fileset>
        </filesets>
    </configuration>
</plugin>
```

[此处](https://search.maven.org/search?q=a:maven-clean-plugin)列出了此插件的最新版本 。

如果output-resources目录包含一些生成的资源，则无法使用默认设置将其删除，我们刚刚所做的更改指示clean插件删除该目录以及默认目录。

## 4. 总结 

在本文中，我们介绍了clean插件并指导了如何自定义它。