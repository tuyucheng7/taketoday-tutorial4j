## 1. 概述

在本教程中，我们介绍如何将Spring Boot应用程序用作另一个项目的依赖项。

## 2. Spring Boot打包

Spring Boot Maven和Gradle插件都将我们的应用程序打包为可执行JAR-**这样的文件不能在另一个项目中使用，因为类文件被放入BOOT-INF/classes中**。这不是错误，而是一个功能。

为了与另一个项目共享类，最好的方法是**创建一个包含共享类的单独jar**，然后使其成为依赖于它们的所有模块的依赖项。

但如果这是不可能的，我们可以配置插件来生成一个单独的jar，可以用作依赖项。

### 2.1 Maven配置

让我们用分类器配置插件：

```xml
...
<build>
    ...
    <plugins>
        ...
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
	    <configuration>
	        <classifier>exec</classifier>
            </configuration>
        </plugin>
    </plugins>
</build>
```

不过，Spring Boot 1.x的配置会略有不同：

```xml
...
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>repackage</goal>
            </goals>
            <configuration>
                <classifier>exec</classifier>
            </configuration>
        </execution>
    </executions>
</plugin>
```

这将创建两个jar，**一个后缀为exec的是可执行jar，另一个是我们可以包含在其他项目中的更典型的jar**。

## 3. 使用Maven Assembly Plugin打包

我们也可以使用[maven-assembly-plugin](https://search.maven.org/search?q=a:maven-assembly-plugin)来创建依赖的jar：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-assembly-plugin</artifactId>
    <configuration>
        <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
        </descriptorRefs>
    </configuration>
    <executions>
        <execution>
            <id>make-assembly</id>
            <phase>package</phase>
            <goals>
                <goal>single</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

如果我们将此插件与spring-boot-maven-plugin中的exec分类器一起使用，它将生成三个jar。前两个与我们之前看到的相同，第三个将具有我们在<descriptorRef\>标签中指定的任何后缀，并将包含项目的所有传递依赖项。如果我们将它包含在另一个项目中，我们将不需要单独包含Spring依赖项。

## 4. 总结

在本文中，我们演示了几种打包Spring Boot应用程序以用作其他Maven项目的依赖项的方法。