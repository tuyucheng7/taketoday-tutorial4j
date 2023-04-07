## 一、概述

在本教程中，我们将了解 Java 9 的新命令行选项*–release。**使用–release N*选项运行的 Java 编译器会自动生成与 Java 版本 N 兼容的类文件**。**我们将讨论此选项与现有编译器命令行选项*-source*和*-target 的关系。*

## 2.需要*--release*选项

*为了理解对--release*选项的需求，让我们考虑一个场景，在这个场景中，我们需要使用 Java 8 编译我们的代码，并希望编译后的类与 Java 7 兼容。

在 Java 9 之前，可以通过使用 —source 和 —target 选项来实现*这*一点*，*其中

-   *-source：*指定编译器接受的Java版本
-   *-target：*指定要生成的类文件的Java版本

假设编译后的程序使用当前平台版本独有的 API，在我们的例子中是 Java 8。在这种情况下，编译后的程序无法在早期版本（如 Java 7）上运行，无论传递给 –source 和 – 的*值*如何*目标*选项。

此外，我们需要添加 –bootclasspath*选项*以及 –source*和*–target*以*在 Java 8 及以下版本中工作。

**为了简化这个交叉编译问题，Java 9 引入了新的选项——release\*来\*简化这个过程。**

## 3.与-s *ource*和-t *target*选项的关系

根据[JDK定义](http://openjdk.java.net/jeps/247)，*-release* N可以展开为：

-   对于 N < 9，*-source* N *-target* N *-bootclasspath* <documented-APIs-from-N>
-   对于 N >= 9，*-source* N *-target* N *–system* <documented-APIs-from-N>

以下是有关这些内部选项的一些详细信息：

-   *-bootclasspath：*用于搜索引导类文件的以分号分隔的目录、JAR 存档和 ZIP 存档列表
-   — *system* ：覆盖 Java 9 及更高版本的[系统模块](https://www.baeldung.com/java-9-modularity)的位置

此外，记录的 API 位于*$JDK_ROOT/lib/ct.sym*中，这是一个 ZIP 文件，其中包含根据 Java 版本精简的类文件。

对于 Java 版本 N< 9，这些 API 包括从位于*jre/lib/rt.jar*和其他相关 jar 中的 jar 检索的引导程序类。

*对于 Java 版本 N >= 9，这些 API 包括从位于jdkpath/jmods/*目录中的 Java 模块检索的引导程序类。

## 4. 使用命令行

首先，让我们创建一个示例类并使用*ByteBuffer* 的重写*翻转*方法，该方法是在 Java 9 中添加的：

```java
import java.nio.ByteBuffer;

public class TestForRelease {

    public static void main(String[] args) {
        ByteBuffer bb = ByteBuffer.allocate(16);
        bb.flip();
        System.out.println("Baeldung: --release option test is successful");
    }
}复制
```

### 4.1. 使用现有的 -source 和 -target 选项

让我们使用值为 8 的*-source*和*-target*选项在 Java 9 中编译代码：

```bash
/jdk9path/bin/javac TestForRelease.java -source 8 -target 8 复制
```

结果是成功的，但有一个警告：

```bash
warning: [options] bootstrap class path not set in conjunction with -source 8

1 warning复制
```

现在，让我们在 Java 8 上运行我们的代码：

```bash
/jdk8path/bin/java TestForRelease复制
```

我们看到这失败了：

```bash
Exception in thread "main" java.lang.NoSuchMethodError: java.nio.ByteBuffer.flip()Ljava/nio/ByteBuffer;
at com.corejava.TestForRelease.main(TestForRelease.java:9)复制
```

*正如我们所看到的，这不是我们在-release*和*-target*选项中给定值 8 所期望看到的。所以虽然编译器应该考虑它，但事实并非如此。

让我们更详细地了解这一点。

在 Java 9 之前的版本中，*Buffer*类包含*flip*方法：

```java
public Buffer flip() {
    ...
 }复制
```

在 Java 9 中， 继承*Buffer 的**ByteBuffer*重写了*flip*方法：

```java
@Override
public ByteBuffer flip() {
    ...
}复制
```

当这个新方法在 Java 9 上编译并在 Java 8 上运行时，我们得到错误，因为这两个方法有不同的返回类型，并且使用描述符的方法查找在运行时失败：

```bash
Exception in thread "main" java.lang.NoSuchMethodError: java.nio.ByteBuffer.flip()Ljava/nio/ByteBuffer;
at com.corejava.TestForRelease.main(TestForRelease.java:9)复制
```

 

在编译期间，我们收到了之前忽略的警告。这是因为**Java 编译器默认使用最新的 API 进行编译**。换句话说，即使我们指定*–source*和–target为8，编译器仍然使用Java 9类，所以我们的程序无法在Java 8上运行。

*因此，我们必须将另一个名为–bootclasspath*的命令行选项传递给Java 编译器以选择正确的版本。

*现在，让我们使用–bootclasspath*选项重新编译相同的代码*：*

```bash
/jdk9path/bin/javac TestForRelease.java -source 8 -target 8 -Xbootclasspath ${jdk8path}/jre/lib/rt.jar复制
```

同样，这个结果是成功的，这次我们没有任何警告。

现在，让我们在 Java 8 上运行我们的代码，我们看到这是成功的：

```bash
/jdk8path/bin/java TestForRelease 
Baeldung: --release option test is successful
复制
```

**虽然交叉编译现在可以工作，但我们必须提供三个命令行选项。**

### 4.2. 带有 –release 选项

*现在，让我们使用–release*选项编译相同的代码：

```bash
/jdk9path/bin/javac TestForRelease.java —-release 8复制
```

同样，这次编译成功，没有任何警告。

最后，当我们在 Java 8 上运行代码时，我们看到它是成功的：

```bash
/jdk8path/bin/java TestForRelease
Baeldung: --release option test is successful复制
```

我们看到使用***-release\*****选项很简单，因为\*javac在内部为\**-source、-target\*和-bootclasspath设置了正确的值\*。\***

## 5. Maven 编译器插件的使用

通常，我们使用 Maven 或 Gradle 等构建工具，而不是命令行*javac*工具。因此，在本节中，我们将了解如何在 Maven 编译器插件中应用*–release选项。*

让我们首先看看我们如何使用现有的*-source*和*-target*选项：

```xml
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
 </plugins>复制
```

以下是我们如何使用*–release*选项：

```xml
<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.8.1</version>
        <configuration>
            <release>1.8</release>
        </configuration>
    </plugin>
 </plugins>复制
```

虽然行为与我们之前描述的相同，但是我们将这些值传递给 Java 编译器的方式不同。

## 六，结论

在本文中，我们了解了*–release*选项及其与现有的*-source*和*-target*选项的关系。然后，我们了解了如何在命令行和 Maven 编译器插件中使用该选项。

最后，我们看到新的 --release*选项*需要更少的交叉编译输入选项。因此，建议尽可能使用它而不是*-target、-source*和*-bootclasspath*选项*。*