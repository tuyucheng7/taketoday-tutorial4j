## 1. 概述

在[Kotlin](https://discuss.kotlinlang.org/t/classes-final-by-default/166/2)中，默认情况下所有类都是最终的，除了其明显的优势之外，在 Spring 应用程序中可能会出现问题。简而言之，Spring 中的某些区域仅适用于非最终类。

自然的解决方案是使用open关键字手动打开 Kotlin 类或使用kotlin-allopen插件——它会自动打开 Spring 工作所需的所有类。

## 2.Maven依赖

让我们从添加[Kotlin-Allopen](https://search.maven.org/classic/#search|ga|1|kotlin allopen)依赖项开始：

```java
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-maven-allopen</artifactId>
    <version>1.1.4-3</version>
</dependency>
```

要启用该插件，我们需要在构建部分配置kotlin-allopen ：

```xml
<build>
   ...
  <plugins>
        ...
        <plugin>
            <artifactId>kotlin-maven-plugin</artifactId>
            <groupId>org.jetbrains.kotlin</groupId>
            <version>1.1.4-3</version>
            <configuration>
                <compilerPlugins>
                    <plugin>spring</plugin>
                </compilerPlugins>
                <jvmTarget>1.8</jvmTarget>
            </configuration>
            <executions>
                <execution>
                    <id>compile</id>
                    <phase>compile</phase>
                    <goals>
                        <goal>compile</goal>
                    </goals>
                </execution>
                <execution>
                    <id>test-compile</id>
                    <phase>test-compile</phase>
                    <goals>
                        <goal>test-compile</goal>
                    </goals>
                </execution>
            </executions>
            <dependencies>
                <dependency>
                    <groupId>org.jetbrains.kotlin</groupId>
                    <artifactId>kotlin-maven-allopen</artifactId>
                    <version>1.1.4-3</version>
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</build>
```

## 3.设置

现在让我们考虑SimpleConfiguration.kt，一个简单的配置类：

```java
@Configuration
class SimpleConfiguration {
}
```

## 4. 没有Kotlin-Allopen

如果我们在没有插件的情况下构建项目，我们将看到以下错误消息：

```plaintext
org.springframework.beans.factory.parsing.BeanDefinitionParsingException: 
  Configuration problem: @Configuration class 'SimpleConfiguration' may not be final. 
  Remove the final modifier to continue.
```

解决它的唯一方法是手动打开它：

```scala
@Configuration
open class SimpleConfiguration {
}
```

## 5.包括Kotlin-Allopen

为 Spring 打开所有类不是很方便。如果我们使用插件，所有必要的类都将打开。

我们可以清楚地看到，如果我们查看编译后的类：

```java
@Configuration
public open class SimpleConfiguration public constructor() {
}
```

## 六，总结

在这篇简短的文章中，我们了解了如何解决 Spring 和 Kotlin 中的“类可能不是最终的”问题。