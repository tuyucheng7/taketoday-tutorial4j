## 1. 概述

当我们编译一个.java文件时，我们得到一个单独的类文件，扩展名为.class。.class文件由几个部分组成，常量池就是其中之一。

在本快速教程中，我们将探索常量池的细节。此外，我们还将了解它支持哪些类型以及它如何格式化信息。

## 2. Java中的常量池

简单的说，常量池包含运行特定类的代码所需要的常量。基本上，它是一个类似于符号表的运行时数据结构。它是Java类文件中每个类或每个接口的运行时表示。

常量池的内容由编译器生成的符号引用组成。这些引用是从代码中引用的变量、方法、接口和类的名称。JVM[使用](https://www.baeldung.com/jvm-parameters)它们将代码与它所依赖的其他类链接起来。

让我们使用一个简单的Java类来理解常量池的结构：

```java
public class ConstantPool {
    
    public void sayHello() {
        System.out.println("Hello World");
    }
}
```

要查看常量池的内容，我们需要先编译文件，然后运行命令：

```bash
javap -v name.class
```

上面的命令将产生：

```bash
   #1 = Methodref          #6.#14         // java/lang/Object."<init>":()V
   #2 = Fieldref           #15.#16        // java/lang/System.out:Ljava/io/PrintStream;
   #3 = String             #17            // Hello World
   #4 = Methodref          #18.#19        // java/io/PrintStream.println:(Ljava/lang/String;)V
   #5 = Class              #20            // com/baeldung/jvm/ConstantPool
   #6 = Class              #21            // java/lang/Object
   #7 = Utf8               <init>
   #8 = Utf8               ()V
   #9 = Utf8               Code
  #10 = Utf8               LineNumberTable
  #11 = Utf8               sayHello
  #12 = Utf8               SourceFile
  #13 = Utf8               ConstantPool.java
  #14 = NameAndType        #7:#8          // "<init>":()V
  #15 = Class              #22            // java/lang/System
  #16 = NameAndType        #23:#24        // out:Ljava/io/PrintStream;
  #17 = Utf8               Hello World
  #18 = Class              #25            // java/io/PrintStream
  #19 = NameAndType        #26:#27        // println:(Ljava/lang/String;)V
  #20 = Utf8               com/baeldung/jvm/ConstantPool
  #21 = Utf8               java/lang/Object
  #22 = Utf8               java/lang/System
  #23 = Utf8               out
  #24 = Utf8               Ljava/io/PrintStream;
  #25 = Utf8               java/io/PrintStream
  #26 = Utf8               println
  #27 = Utf8               (Ljava/lang/String;)V

```

#n表示对常量池的引用。#17 是对“Hello World”字符串的符号引用，#18 是System.out，#19 是println。同样， #8 突出显示方法的返回类型为void，#20 是完全限定的类名。

需要注意的是，常量池表是从索引1开始的，索引值为0被认为是无效索引。

### 2.1. 类型

常量池支持几种类型：

-   Integer , Float : 32 位常量
-   Double、Long：具有 64 位常量
-   String：一个 16 位字符串常量，指向池中包含实际字节的另一个条目
-   Class：包含完全限定的类名
-   Utf8：字节流
-   NameAndType：一对以冒号分隔的值，第一个条目表示名称，第二个条目表示类型
-   Fieldref、Methodref、InterfaceMethodref：一对点分隔的值，第一个值指向Class条目，而第二个值指向NameAndType条目

其他类型如boolean、short和byte呢？这些类型在池中表示为整数常量。

### 2.2. 格式

表中的每个条目都遵循通用格式：

```java
cp_info {
    u1 tag;
    u1 info[];
}
```

最初的 1 字节标记表示常量的种类。一旦 JVM 获取并拦截了标签，它就知道标签后面是什么。通常，标记后跟两个或多个字节来携带有关该常量的信息。

让我们看看一些类型和它们的标签索引：

-   UTF8：1
-   整数：3
-   浮动：4
-   长：5
-   双: 6
-   课堂参考：7
-   字符串参考：8

任何类或接口的常量池只有在 JVM 加载完成后才会创建。

## 3.总结

在这篇快速文章中，我们了解了 JVM 中的常量池。我们已经看到它包含用于定位实际对象的符号引用。此外，我们还将了解池如何格式化有关常量及其类型的信息。