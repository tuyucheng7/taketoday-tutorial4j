## 1. 概述

在这个快速教程中，我们将了解如何从[Maven](https://www.baeldung.com/maven)的pom.xml中读取环境变量以自定义构建过程。

## 2. 环境变量

**要从pom.xml引用环境变量，我们可以使用${env.VARIABLE_NAME}语法**。

例如，让我们在构建过程中使用它来[外部化Java版本](https://www.baeldung.com/maven-java-version)：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.1</version>
            <configuration>
                <source>${env.JAVA_VERSION}</source>
                <target>${env.JAVA_VERSION}</target>
            </configuration>
        </plugin>
    </plugins>
</build>
```

我们应该记住通过环境变量传递Java版本信息，如果我们不这样做，那么我们将无法构建项目。

要针对这样的构建文件运行Maven目标或阶段，我们应该首先导出环境变量，例如：

```bash
$ export JAVA_VERSION=9
$ mvn clean package
```

在Windows上，我们应该使用“set VAR=value”语法来导出环境变量。

**为了在缺少JAVA_VERSION环境变量时提供默认值，我们可以使用Maven的Profile**：

```xml
<profiles>
    <profile>
        <id>default-java</id>
        <activation>
            <property>
                <name>!env.JAVA_VERSION</name>
            </property>
        </activation>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.8.1</version>
                    <configuration>
                        <source>1.8</source>
                        <target>1.8</target>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

如上所示，我们正在创建一个Profile并仅在缺少JAVA_VERSION环境变量时才使其处于激活状态(!env.JAVA_VERSION部分)，如果发生这种情况，那么这个新的插件定义将覆盖现有的插件定义。

## 3. 总结

在这个简短的教程中，我们了解了如何通过将环境变量传递给pom.xml来自定义构建过程。