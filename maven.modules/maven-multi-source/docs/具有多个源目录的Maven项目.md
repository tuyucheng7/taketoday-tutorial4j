## 1. 概述

有时我们需要在Java项目中使用多个源目录，一个常见的例子是，有自动生成并放置在不同目录中的类。

在这篇简短的文章中，我们将演示如何**设置Maven以使用其他源目录**。

## 2. 添加另一个源目录

假设我们已经创建了一个Maven项目，让我们在src/main文件夹中添加一个名为another-src的新源目录。

之后，让我们在此文件夹中创建一个简单的Java类：

```java
public class Foo {
    public static String foo() {
        return "foo";
    }
}
```

现在，让我们在src/main/java目录中创建另一个类，它调用我们刚刚创建的Foo类：

```java
public class MultipleSrcFolders {
    public static void callFoo() {
        Foo.foo();
    }
}
```

我们的项目结构现在看起来像这样：

<img src="../assets/img.png">

**如果我们尝试用Maven编译这个项目，我们会得到一个编译错误**，因为Foo类不包含在项目中：

```bash
[ERROR] .../MultipleSrcFolders.java:[6,9] cannot find symbol
[ERROR]   symbol:   variable Foo
[ERROR]   location: class cn.tuyucheng.taketoday.maven.plugins.MultipleSrcFolders
```

## 3. 使用Builder Helper插件

通过Maven，我们可以使用Builder Helper插件来添加更多的源目录，这个插件允许我们以不同的方式自定义构建生命周期。

**它的目标之一是add-sources，目的是在generate-sources阶段向项目中添加更多src目录**。

我们可以通过将它添加到我们的pom.xml来在我们的项目中使用它：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>build-helper-maven-plugin</artifactId>
    <version>3.0.0</version>
    <executions>
        <execution>
            <phase>generate-sources</phase>
            <goals>
                <goal>add-source</goal>
            </goals>
            <configuration>
                <sources>
                    <source>src/main/another-src</source>
                </sources>
            </configuration>
        </execution>
    </executions>
</plugin>
```

最新版本的插件可以在[Maven Central](https://search.maven.org/search?q=a:build-helper-maven-plugin)中找到。

如果我们现在编译我们的项目，构建就会成功。

## 4. 总结

我们在本文中了解了如何设置Builder Helper Maven插件以处理Maven项目中的多个src目录。