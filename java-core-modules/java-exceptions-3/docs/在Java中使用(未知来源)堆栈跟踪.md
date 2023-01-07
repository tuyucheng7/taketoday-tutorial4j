## 1. 概述

在这篇简短的文章中，我们将研究为什么我们可能会在Java异常堆栈跟踪中看到未知来源，以及我们如何修复它。

## 2.类调试信息

Java 类文件包含可选的调试信息以方便调试。我们可以在编译期间选择是否以及将所有调试信息添加到类文件中。这将确定哪些调试信息在运行时可用。

让我们研究Java编译器的帮助文档以查看可用的各种选项：

```bash
javac -help

Usage: javac <options> <source files>
where possible options include:
  -g                         Generate all debugging info
  -g:none                    Generate no debugging info
  -g:{lines,vars,source}     Generate only some debugging info
```

Java 编译器的默认行为是将行和源信息添加到类文件中，这相当于-g:lines,source。

### 2.1. 使用调试选项编译

让我们看看使用上述选项编译Java类时会发生什么。我们有一个Main类，它有意生成StringIndexOutOfBoundsException。

根据使用的编译机制，我们必须相应地指定编译选项。在这里，我们将使用 Maven 及其编译器插件来自定义编译器选项：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.1</version>
    <configuration>
        <compilerArgs>
            <arg>-g:none</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

我们已将-g设置为none，这意味着不会为我们编译的类生成调试信息。运行我们有问题的Main类会生成堆栈跟踪，我们会在其中看到未知来源，而不是发生异常的行号。

```bash
Exception in thread "main" java.lang.StringIndexOutOfBoundsException: begin 0, end 10, length 5
  at java.base/java.lang.String.checkBoundsBeginEnd(String.java:3751)
  at java.base/java.lang.String.substring(String.java:1907)
  at com.baeldung.unknownsourcestacktrace.Main.getShortenedName(Unknown Source)
  at com.baeldung.unknownsourcestacktrace.Main.getGreetingMessage(Unknown Source)
  at com.baeldung.unknownsourcestacktrace.Main.main(Unknown Source)

```

让我们看看生成的类文件包含什么。我们将使用javap ，它是[Java 类文件反汇编程序](https://www.baeldung.com/java-class-view-bytecode) 来执行此操作：

```bash
javap -l -p Main.class

public class com.baeldung.unknownsourcestacktrace.Main {
    private static final org.slf4j.Logger logger;
    private static final int SHORT_NAME_LIMIT;
    public com.baeldung.unknownsourcestacktrace.Main();
    public static void main(java.lang.String[]);
    private static java.lang.String getGreetingMessage(java.lang.String);
    private static java.lang.String getShortenedName(java.lang.String);
    static {};
}
```

可能很难知道我们在这里应该期待什么样的调试信息，所以让我们更改编译选项，看看会发生什么。

### 2.3. 修复

现在让我们将编译选项更改为-g:lines,vars,source，这会将LineNumberTable、 LocalVariableTable和Source信息放入我们的类文件中。它也等同于只使用-g 来放置所有调试信息：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.1</version>
    <configuration>
        <compilerArgs>
            <arg>-g</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

再次运行我们的错误主类现在会产生：

```bash
Exception in thread "main" java.lang.StringIndexOutOfBoundsException: begin 0, end 10, length 5
  at java.base/java.lang.String.checkBoundsBeginEnd(String.java:3751)
  at java.base/java.lang.String.substring(String.java:1907)
  at com.baeldung.unknownsourcestacktrace.Main.getShortenedName(Main.java:23)
  at com.baeldung.unknownsourcestacktrace.Main.getGreetingMessage(Main.java:19)
  at com.baeldung.unknownsourcestacktrace.Main.main(Main.java:15)
```

瞧，我们在堆栈跟踪中看到了行号信息。让我们看看类文件中发生了什么变化：

```arduino
javap -l -p Main

Compiled from "Main.java"
public class com.baeldung.unknownsourcestacktrace.Main {
  private static final org.slf4j.Logger logger;

  private static final int SHORT_NAME_LIMIT;

  public com.baeldung.unknownsourcestacktrace.Main();
    LineNumberTable:
      line 7: 0
    LocalVariableTable:
      Start  Length  Slot  Name   Signature
          0       5     0  this   Lcom/baeldung/unknownsourcestacktrace/Main;

  public static void main(java.lang.String[]);
    LineNumberTable:
      line 12: 0
      line 13: 8
      line 15: 14
      line 16: 29
    LocalVariableTable:
      Start  Length  Slot  Name   Signature
          0      30     0  args   [Ljava/lang/String;
          8      22     1  user   Lcom/baeldung/unknownsourcestacktrace/dto/User;

  private static java.lang.String getGreetingMessage(java.lang.String);
    LineNumberTable:
      line 19: 0
    LocalVariableTable:
      Start  Length  Slot  Name   Signature
          0      28     0  name   Ljava/lang/String;

  private static java.lang.String getShortenedName(java.lang.String);
    LineNumberTable:
      line 23: 0
    LocalVariableTable:
      Start  Length  Slot  Name   Signature
          0       8     0  name   Ljava/lang/String;

  static {};
    LineNumberTable:
      line 8: 0
}
```

我们的类文件现在包含三个关键信息：

1.  Source，顶部标头指示生成.class文件的.java文件。在堆栈跟踪的上下文中，它提供发生异常的类名。
2.  LineNumberTable将JVM实际运行的代码中的行号映射到我们源代码文件中的行号。在堆栈跟踪上下文中，它提供发生异常的行号。我们还需要它能够在我们的调试器中使用断点。
3.  LocalVariableTable包含获取局部变量值的详细信息。调试器可以使用它来读取局部变量的值。在堆栈跟踪的上下文中，这无关紧要。

## 3.总结

我们现在已经熟悉了Java编译器生成的调试信息。操作它们的方式，-g编译器选项。我们看到了如何使用 Maven 编译器插件来做到这一点。

因此，如果我们在堆栈跟踪中发现未知来源，我们可以调查我们的类文件以检查调试信息是否可用。接下来我们可以根据我们的构建工具选择正确的编译选项来解决这个问题。