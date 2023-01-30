## 1. 概述

在本教程中，我们将了解java.lang.VerifyError错误的原因以及避免它的多种方法。

## 2.原因

Java 虚拟机[(JVM)](https://www.baeldung.com/jvm-vs-jre-vs-jdk)不信任所有加载的字节码作为 Java 安全模型的核心原则。在运行时，JVM 将加载 .class文件并尝试将它们链接在一起以形成可执行文件——但这些加载的.class文件的有效性是未知的。

为了确保加载的 .class文件不会对最终的可执行文件构成威胁，JVM会对[.class](https://docs.oracle.com/javase/specs/jvms/se13/html/jvms-4.html#jvms-4.10)[文件进行](https://docs.oracle.com/javase/specs/jvms/se13/html/jvms-4.html#jvms-4.10)[验证](https://docs.oracle.com/javase/specs/jvms/se13/html/jvms-4.html#jvms-4.10)。此外，JVM 确保二进制文件格式正确。例如，JVM 将验证类不对最终类进行子类型化。

在许多情况下，对有效的、非恶意字节码的验证会失败，因为较新版本的 Java 具有比旧版本更严格的验证过程。例如，JDK 13 可能添加了 JDK 7 中未强制执行的验证步骤。因此，如果我们使用 JVM 13 运行应用程序并包含使用旧版本的[Java 编译器 (javac) 编译](https://www.baeldung.com/javac)的依赖项，JVM 可能会考虑过时的依赖无效。

因此，当将较旧的.class文件与较新的 JVM 链接时，JVM 可能会抛出类似于以下内容的java.lang.VerifyError ：

```plaintext
java.lang.VerifyError: Expecting a stackmap frame at branch target X
Exception Details:
  Location:
    
com/example/baeldung.Foo(Lcom/example/baeldung/Bar:Baz;)Lcom/example/baeldung/Foo; @1: infonull
  Reason:
    Expected stackmap frame at this location.
  Bytecode:
    0000000: 0001 0002 0003 0004 0005 0006 0007 0008
    0000010: 0001 0002 0003 0004 0005 0006 0007 0008
    ...
```

有两种方法可以解决这个问题：

-   将依赖项更新为使用更新的javac编译的版本
-   禁用 Java 验证

## 三、生产方案

验证错误的最常见原因是使用较旧版本的javac编译的较新 JVM 版本链接二进制文件。当依赖项具有由[Javassist](https://www.baeldung.com/javassist)等工具生成的字节码时，这种情况更为常见，如果工具已过时，这些字节码可能会生成过时的字节码。

要解决此问题，请将依赖项更新为 使用与用于构建应用程序的 JDK 版本匹配的 JDK 版本构建的版本。例如，如果我们使用 JDK 13 构建应用程序，则应使用 JDK 13 构建依赖项。

要查找兼容版本，请检查依赖项的[JAR 清单文件中的](https://www.baeldung.com/java-jar-manifest)Build-Jdk ，以确保它与用于构建应用程序的 JDK 版本相匹配。

## 四、调试开发方案

在调试或开发应用程序时，我们可以禁用验证作为快速修复。

不要将此解决方案用于生产代码。

通过禁用验证，JVM 可以将恶意代码或错误代码链接到我们的应用程序，从而导致安全威胁或执行时崩溃。

另请注意，从 JDK 13 开始，[此解决方案已被弃用](https://bugs.openjdk.java.net/browse/JDK-8218003)，我们不应期望此解决方案在未来的 Java 版本中起作用。禁用验证将导致以下警告：

```plaintext
Java HotSpot(TM) 64-Bit Server VM warning: Options -Xverify:none and -noverify were deprecated
  in JDK 13 and will likely be removed in a future release.
```

禁用字节码验证的机制因我们运行代码的方式而异。

### 4.1. 命令行

要在命令行上禁用验证，请将noverify标志传递给 java命令：

```plaintext
java -noverify Foo.class
```

请注意，-noverify是-Xverify:none的快捷方式，两者可以互换使用。

### 4.2. 行家

[要在Maven](https://www.baeldung.com/maven)构建 中禁用验证，请将noverify标志传递给任何所需的插件：

```xml
<plugin>
    <groupId>com.example.baeldung</groupId>
    <artifactId>example-plugin</artifactId>
    <!-- ... -->
    <configuration>
        <argLine>-noverify</argLine>
        <!-- ... -->
    </configuration>
</plugin>
```

### 4.3. 摇篮

[要在Gradle](https://www.baeldung.com/gradle)构建 中禁用验证，请将noverify标志传递给任何所需的任务：

```groovy
someTask {
    // ...
    jvmArgs = jvmArgs << "-noverify"
}
```

## 5.总结

在本快速教程中，我们了解了 JVM 执行字节码验证的原因以及导致java.lang.VerifyError错误的原因。我们还探索了两种解决方案：生产解决方案和非生产解决方案。

如果可能，请使用最新版本的依赖项 而不是禁用验证。