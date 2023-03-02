## 1. 概述

本文展示了如何将Javascript和CSS资源压缩为构建步骤，并使用Spring MVC提供生成的文件。

我们将使用[YUI Compressor](https://yui.github.io/yuicompressor/)作为底层以上所库，并使用[YUI Compressor Maven插件](https://davidb.github.io/yuicompressor-maven-plugin/)将其集成到我们的构建过程中。

## 2. Maven插件配置

首先，我们需要在pom.xml文件中声明我们将使用压缩器插件并执行compress目标。这将压缩src/main/webapp下的所有.js和.css文件，以便foo.js将被压缩为foo-min.js并且myCss.css将被压缩为myCss-min.css：

```xml
<plugin>
    <groupId>net.alchim31.maven</groupId>
    <artifactId>yuicompressor-maven-plugin</artifactId>
    <version>1.5.1</version>
    <executions>
        <execution>
            <goals>
                <goal>compress</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

我们的src/main/webapp目录包含以下文件：

```powershell
js/
├── foo.js
├── jquery-1.11.1.min.js
resources/
└── myCss.css
```

执行mvn clean package后，生成的WAR文件将包含以下文件：

```powershell
js/
├── foo.js
├── foo-min.js
├── jquery-1.11.1.min.js
├── jquery-1.11.1.min-min.js
resources/
├── myCss.css
└── myCss-min.css
```

## 3. 保持文件名相同

在这个阶段，当我们执行mvn clean package时，插件会创建foo-min.js和myCss-min.css。由于我们最初在引用文件时使用的是foo.js和myCss.css，因此我们的页面仍将使用原始的非压缩文件，因为压缩文件的名称与原始文件不同。

为了防止同时拥有foo.js/foo-min.js和myCss.css/myCss-min.css并在不更改名称的情况下压缩文件，我们需要使用nosuffix选项配置插件，如下所示：

```xml
<plugin>
    <groupId>net.alchim31.maven</groupId>
    <artifactId>yuicompressor-maven-plugin</artifactId>
    <version>1.5.1</version>
    <executions>
        <execution>
            <goals>
                <goal>compress</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <nosuffix>true</nosuffix>
    </configuration>
</plugin>
```

现在当我们执行mvn clean package时，我们将在生成的WAR文件中包含以下文件：

```powershell
js/
├── foo.js
├── jquery-1.11.1.min.js
resources/
└── myCss.css
```

## 4. WAR插件配置

保持文件名相同会产生副作用。它会导致WAR插件用原始文件覆盖压缩的foo.js和myCss.css文件，因此我们在最终输出中没有文件的压缩版本。foo.js文件在压缩之前包含以下行：

```javascript
function testing() {
    alert("Testing");
}
```

当我们检查生成的WAR文件中的foo.js文件的内容时，我们看到它具有原始内容而不是压缩后的内容。为了解决这个问题，我们需要为压缩器插件指定一个webappDirectory并在WAR插件配置中引用它。

```xml
<plugin>
    <groupId>net.alchim31.maven</groupId>
    <artifactId>yuicompressor-maven-plugin</artifactId>
    <version>1.5.1</version>
    <executions>
        <execution>
            <goals>
                <goal>compress</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <nosuffix>true</nosuffix>
        <webappDirectory>${project.build.directory}/min</webappDirectory>
    </configuration>
</plugin>
<plugin>
<artifactId>maven-war-plugin</artifactId>
<configuration>
    <webResources>
        <resource>
            <directory>${project.build.directory}/min</directory>
        </resource>
    </webResources>
</configuration>
</plugin>
```

在这里，我们将min目录指定为压缩文件的输出目录，并将WAR插件配置为将其包含在最终输出中。

现在我们在生成的WAR文件中有了压缩的文件，它们的原始文件名是foo.js和myCss.css。我们可以检查foo.js以查看它现在具有以下压缩的内容：

```javascript
function testing(){alert("Testing")};
```

## 5. 排除已经压缩的文件

第三方Javascript和CSS库可能有压缩版本可供下载。如果你恰好在你的项目中使用其中一个，则不需要再次处理它们。

在构建项目时，包括已经压缩的文件会产生警告消息。

例如，jquery-1.11.1.min.js是一个已经压缩的Javascript文件，它会在构建期间产生类似于以下内容的警告消息：

```shell
[WARNING] .../src/main/webapp/js/jquery-1.11.1.min.js [-1:-1]: 
Using 'eval' is not recommended. Moreover, using 'eval' reduces the level of compression!
execScript||function(b){a. ---> eval <--- .call(a,b);})
[WARNING] ...jquery-1.11.1.min.js:line -1:column -1: 
Using 'eval' is not recommended. Moreover, using 'eval' reduces the level of compression!
execScript||function(b){a. ---> eval <--- .call(a,b);})
```

要从构建过程中排除已经压缩的文件，请使用executions选项配置压缩器插件，如下所示：

```xml
<plugin>
    <groupId>net.alchim31.maven</groupId>
    <artifactId>yuicompressor-maven-plugin</artifactId>
    <version>1.5.1</version>
    <executions>
        <execution>
            <goals>
                <goal>compress</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <nosuffix>true</nosuffix>
        <webappDirectory>${project.build.directory}/min</webappDirectory>
        <excludes>
            <exclude>**/*.min.js</exclude>
        </excludes>
    </configuration>
</plugin>
```

这将排除所有目录下文件名以min.js结尾的所有文件。执行mvn clean package现在不会产生警告消息，并且构建不会尝试压缩已经压缩的文件。

## 6. 总结

在本文中，我们描述了一种将Javascript和CSS文件的压缩集成到你的Maven工作流中的好方法。要使用Spring MVC应用程序提供这些静态资源，请参阅我们的[使用Spring提供静态资源](https://www.baeldung.com/spring-mvc-static-resources)一文。