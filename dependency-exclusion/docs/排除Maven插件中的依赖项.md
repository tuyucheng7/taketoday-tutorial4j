## 一、概述

在 Maven 中排除依赖是一个常见的操作。但是，在处理 Maven 插件时会变得更加困难。

## 2.什么是依赖排除

Maven 管理依赖关系的传递性。这意味着**Maven 可以通过我们添加的依赖自动添加所有需要的依赖**。在某些情况下，这种**传递性会迅速增加依赖项的数量，因为它增加了级联依赖项**。

例如，如果我们有 A → B → C → D 这样的依赖关系，那么 A 将依赖于 B、C 和 D。如果 A 只使用 B 的一小部分，不需要 C，那么可以告诉 Maven 忽略 A 中的 B → C 依赖。

因此，A 将只依赖于 B 而不再依赖于 C 和 D。这称为依赖排除。

## 3.排除传递依赖

*我们可以使用<exclusions>*元素排除子依赖项，该元素包含一组对特定依赖项的排除项。**简而言之，我们只需要在POM 文件的\*<dependency>元素中添加一个\**<exclusions>\*元素。**

例如，让我们考虑*commons-text*依赖的例子，并假设我们的项目只使用来自*commons-text 的代码，*不需要*commons-lang*子依赖。

我们将能够从我们项目的*commons-text传递链中排除**commons-lang*依赖，只需在我们项目的*POM*文件中的*commons-text*依赖声明中添加一个*<exclusions>*部分，如下所示：

```xml
<project>
    ...
    <dependencies>
        ...
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-text</artifactId>
            <version>1.1</version>
            <exclusions>
                <exclusion>
                    <groupId>org.apache.commons</groupId>
                    <artifactId>commons-lang3</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>
    ...
</project>复制
```

因此**，**如果我们用上面的*POM*重建一个项目，我们会看到*commons-text*库被集成到我们的项目中，而不是*commons-lang*库。

## 4. 从插件中排除传递依赖

到目前为止，Maven 不支持从插件中排除直接依赖项，并且已经公开了包含此新功能的[问题。](https://issues.apache.org/jira/browse/MNG-6222)在本章中，**我们将讨论一种解决方法，通过用虚拟对象覆盖它来排除 Maven 插件的直接依赖。**

假设我们必须排除 Maven Surefire 插件的 JUnit 4.7 依赖项。

首先，我们必须创建一个虚拟模块，它必须是我们项目根*POM*的一部分。该模块将仅包含一个如下所示的*POM文件：*

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.apache.maven.surefire</groupId>
    <artifactId>surefire-junit47</artifactId>
    <version>dummy</version>
</project>复制
```

接下来，我们需要在我们希望停用依赖项的地方调整我们的子*POM 。*为此，我们必须将虚拟版本的依赖项添加到 Maven Surefire 插件声明中：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>${surefire-version}</version>
            <configuration>
                <runOrder>alphabetical</runOrder>
                <threadCount>1</threadCount>
                <properties>
                    <property>
                        <name>junit</name>
                        <value>false</value>
                    </property>
                </properties>
            </configuration>
            <dependencies>
                <dependency>
                    <!-- Deactivate JUnit 4.7 engine by overriding it with an empty dummy -->
                    <groupId>org.apache.maven.surefire</groupId>
                    <artifactId>surefire-junit47</artifactId>
                    <version>dummy</version>
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</build>复制
```

最后，一旦我们构建了我们的项目，我们将看到 Maven Surefire 插件的 JUnit 4.7 依赖项没有包含在项目中并且排除效果很好。

## 5.结论

在本快速教程中，我们解释了依赖排除以及如何使用*<exclusions>*元素排除传递依赖。此外，我们公开了一种解决方法，通过用虚拟对象覆盖插件来排除插件中的直接依赖项。 