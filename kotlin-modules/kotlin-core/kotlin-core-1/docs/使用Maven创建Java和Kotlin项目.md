## 1. 简介

在本快速教程中，我们将了解如何设置一个同时处理Java和Kotlin源代码的Maven项目。

我们首先创建一个仅用于Java源代码的项目，然后我们将添加kotlin-maven-plugin插件来处理Kotlin。

最后，我们添加一些虚拟类，打包我们的应用程序，并测试一切是否按预期进行。

## 2. 使用Maven创建一个Java项目

首先，让我们**使用Maven创建一个简单的Java项目**：

```xml
<artifactId>maven-java-kotlin</artifactId>
<packaging>jar</packaging>

<properties>
    <java.version>1.8</java.version>
</properties>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.1</version>
            <configuration>
                <source>${java.version}</source>
                <target>${java.version}</target>
            </configuration>
        </plugin>
    </plugins>
</build>
```

这个pom文件包含了我们编译Java源代码并将它们打包成jar文件所需的所有内容。

## 3. 添加Kotlin Maven插件

现在我们需要调整这个pom文件，以便它也可以处理Kotlin源代码。

首先让我们将kotlin.version添加到我们的属性中，**将**[kotlin-stdlib-jdk8](https://search.maven.org/search?q=a:kotlin-stdlib-jdk8)**添加到我们的依赖中**，这样我们就可以访问Kotlin的功能： 

```xml
<properties>
    <java.version>1.8</java.version>
    <kotlin.version>1.2.51</kotlin.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.jetbrains.kotlin</groupId>
        <artifactId>kotlin-stdlib-jdk8</artifactId>
        <version>${kotlin.version}</version>
    </dependency>
</dependencies>
```

然后，**我们需要将**[kotlin-maven-plugin](https://search.maven.org/search?q=a:kotlin-maven-plugin)**添加到我们的Maven插件中**。

我们将配置它来处理compile和test-compile目标，告诉它在哪里可以找到我们的源代码。

按照惯例，我们将Java和Kotlin源代码保存在不同的目录中，尽管我们也可以将它们全部放在同一个目录中：

```xml
<plugin>
    <artifactId>kotlin-maven-plugin</artifactId>
    <groupId>org.jetbrains.kotlin</groupId>
    <version>${kotlin.version}</version>
    <executions>
        <execution>
            <id>compile</id>
            <goals>
                <goal>compile</goal>
            </goals>
            <configuration>
                <sourceDirs>
                    <sourceDir>${project.basedir}/src/main/kotlin</sourceDir>
                    <sourceDir>${project.basedir}/src/main/java</sourceDir>
                </sourceDirs>
            </configuration>
        </execution>
        <execution>
            <id>test-compile</id>
            <goals>
                <goal>test-compile</goal>
            </goals>
            <configuration>
                <sourceDirs>
                    <sourceDir>${project.basedir}/src/test/kotlin</sourceDir>
                    <sourceDir>${project.basedir}/src/test/java</sourceDir>
                </sourceDirs>
            </configuration>
        </execution>
    </executions>
</plugin>
```

配置到这几乎就完成了，我们需要**调整maven-compiler-plugin配置，因为我们需要在Java源代码之前编译Kotlin源代码**。

通常，Maven插件的<execution/\>是根据声明顺序发生的，所以我们应该把maven-compiler-plugin放在kotlin-maven-plugin之后。但是前者有两个特定的<execution/\>，在各个阶段中先于其他所有内容执行：default-compile和default-testCompile。

我们需要禁用它们并改为启用java-compile和java-test-compile以确保kotlin-maven-plugin的<execution/\>将在maven-compiler-plugin之前发生：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.1</version>
    <configuration>
        <source>${java.version}</source>
        <target>${java.version}</target>
    </configuration>
    <executions>
        <execution>
            <id>default-compile</id>
            <phase>none</phase>
        </execution>
        <execution>
            <id>default-testCompile</id>
            <phase>none</phase>
        </execution>
        <execution>
            <id>java-compile</id>
            <phase>compile</phase>
            <goals>
                <goal>compile</goal>
            </goals>
        </execution>
        <execution>
            <id>java-test-compile</id>
            <phase>test-compile</phase>
            <goals>
                <goal>testCompile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

我们可以看到，默认的<execution/\>是使用阶段none禁用的，并且特定于Java的<execution/\>被绑定到compile和test-compile阶段。

## 4. Java和Kotlin的”Hello World“

现在我们已经正确地设置了一切，让我们测试来自Java和Kotlin的”Hello World“。

为此，让我们创建一个带有main()方法的Application类，此方法将根据其第一个参数调用Java或Kotlin类：

```java
public class Application {

    static String JAVA = "java";
    static String KOTLIN = "kotlin";

    public static void main(String[] args) {
        String language = args[0];
        switch (language) {
            case JAVA:
                new JavaService().sayHello();
                break;
            case KOTLIN:
                new KotlinService().sayHello();
                break;
            default:
                // Do nothing
                break;
        }
    }
}
```

JavaService和KotlinService类只是简单的输出“Hello World！”：

```java
public class JavaService {

    public void sayHello() {
        System.out.println("Java says 'Hello World!'");
    }
}
```

```kotlin
class KotlinService {

    fun sayHello() {
        System.out.println("Kotlin says 'Hello World!'")
    }
}
```

我们现在可以通过调用mvn package命令来编译和打包我们的应用程序。

让我们通过在终端中运行以下命令来测试生成的jar：

```bash
java -cp maven-java-kotlin-1.0.0.jar path.to.your.Class "java"
```

正如我们所看到的，这调用了JavaService类，它将“Java says 'Hello World!'”打印到控制台。

```bash
java -cp maven-java-kotlin-1.0.0.jar path.to.your.Class "kotlin"
```

这个命令调用KotlinService类，它打印“Kotlin says 'Hello World!'”。

## 5. 总结

在本文中，我们重点介绍了如何创建一个同时处理Java和Kotlin源代码的Maven项目，将它们编译并打包到一个jar中。