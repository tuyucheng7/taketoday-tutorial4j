## **一、简介**

本文是关于*jshell*的，这是一个交互式*REPL*（读取-评估-打印-循环）控制台，它与即将发布的 Java 9 版本的 JDK 捆绑在一起。对于那些不熟悉这个概念的人，REPL 允许交互式地运行任意代码片段并评估它们的结果。

REPL 可用于诸如快速检查想法的可行性或找出*String*或*SimpleDateFormat*的格式化字符串等事情。

## **2.跑步**

要开始，我们需要运行 REPL，这是通过调用来完成的：

```bash
$JAVA_HOME/bin/jshell复制
```

如果需要来自 shell 的更详细消息，可以使用*-v标志：*

```bash
$JAVA_HOME/bin/jshell -v复制
```

一旦准备就绪，我们将在底部收到一条友好的消息和熟悉的 Unix 风格的提示。

## **3.定义和调用方法**

可以通过键入方法的签名和正文来添加方法：

```bash
jshell> void helloWorld() { System.out.println("Hello world");}
|  created method helloWorld()复制
```

在这里，我们定义了无处不在的“hello world”方法。可以使用普通的 Java 语法调用它：

```bash
jshell> helloWorld()
Hello world复制
```

## **4.变量**

可以使用普通的 Java 声明语法定义变量：

```bash
jshell> int i = 0;
i ==> 0
|  created variable i : int

jshell> String company = "Baeldung"
company ==> "Baeldung"
|  created variable company : String

jshell> Date date = new Date()
date ==> Sun Feb 26 06:30:16 EST 2017
|  created variable date : Date复制
```

请注意，分号是可选的。变量也可以在没有初始化的情况下声明：

```java
jshell> File file
file ==> null
|  created variable file : File复制
```

## **5. 表情**

接受任何有效的 Java 表达式并显示计算结果。如果没有提供明确的结果接收者，将创建“scratch”变量：

```bash
jshell> String.format("%d of bottles of beer", 100)
$6 ==> "100 of bottles of beer"
|  created scratch variable $6 : String复制
```

REPL 在这里非常有用，它告诉我们它创建了一个名为*$6*的临时变量，其值为“墙上的 100 瓶啤酒”，其类型为*String*。

多行表达式也是可能的。***Jshell\*足够聪明，可以知道表达式何时不完整，并会提示用户换行：**

```bash
jshell> int i =
   ...> 5;
i ==> 5
|  modified variable i : int
|    update overwrote variable i : int复制
```

请注意提示如何变为缩进的*…>*以表示表达式的继续。

## **6. 命令**

Jshell 提供了很多与评估 Java 语句无关的元命令。它们都以正斜杠 (/) 开头，以区别于正常操作。例如，我们可以通过发出*/help*或 /?来请求所有可用命令的列表。

让我们来看看其中的一些。

### **6.1. 进口**

要列出当前会话中所有活动的导入，我们可以使用*/import*命令：

```bash
jshell> /import
|    import java.io.*
|    import java.math.*
|    import java.net.*
|    import java.nio.file.*
|    import java.util.*
|    import java.util.concurrent.*
|    import java.util.function.*
|    import java.util.prefs.*
|    import java.util.regex.*
|    import java.util.stream.*复制
```

正如我们所见，shell 开始时已经添加了很多有用的导入。

### **6.2. 列表**

在 REPL 中工作并不像拥有一个触手可及的全功能 IDE 那样容易：很容易忘记哪些变量具有哪些值，哪些方法已经定义等等。要检查 shell 的状态，我们可以使用*/var*、*/methods*、*/list*或*/history ：*

```bash
jshell> /var
| int i = 0
| String company = "Baeldung"
| Date date = Sun Feb 26 06:30:16 EST 2017
| File file = null
| String $6 = "100 of bottles of beer on the wall"

jshell> /methods
| void helloWorld()

jshell> /list

 1 : void helloWorld() { System.out.println("Hello world");}
 2 : int i = 0;
 3 : String company = "Baeldung";
 4 : Date date = new Date();
 5 : File file;
 6 : String.format("%d of bottles of beer on the wall", 100)

jshell> /history

void helloWorld() { System.out.println("Hello world");}
int i = 0;
String company = "Baeldung"
Date date = new Date()
File file
String.format("%d of bottles of beer on the wall", 100)
/var
/methods
/list
/history
复制
```

*/list*和*/history*的区别在于后者除了表达式之外还显示命令。

### **6.3. 保存**

要保存表达式历史，可以使用*/save命令：*

```bash
jshell> /save repl.java
复制
```

这会将我们的表达式历史记录保存到*repl.java*中，该目录与我们运行*jshell*命令的目录相同。

### **6.4. 加载中**

要加载以前保存的文件，我们可以使用*/open*命令：

```bash
jshell> /open repl.java
复制
```

*然后可以通过发出/var*、*/method*或*/list*来验证加载的会话。

### **6.5. 退出**

完成工作后，*/exit*命令可以终止 shell：

```bash
jshell> /exit
|  Goodbye复制
```

再见*jshell*。

## **七、结论**

在本文中，我们了解了 Java 9 REPL。由于 Java 已经存在了 20 多年，也许它来得有点晚。然而，它应该被证明是我们 Java 工具箱中的另一个有价值的工具。