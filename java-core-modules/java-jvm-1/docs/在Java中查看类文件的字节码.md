## 1. 概述

出于多种原因，字节码分析是Java开发人员的常见做法，例如查找代码问题、代码分析以及搜索具有特定注解的类。

在本文中，我们将探讨在Java中查看类文件的字节码的方法。

## 2.什么是字节码？

字节码是Java程序的中间表示，允许 JVM 将程序翻译成[机器级汇编指令](https://www.baeldung.com/cs/instructions-programs)。

编译Java程序时，会以.class文件的形式生成字节码。此.class文件包含不可运行的指令并依赖于 JVM 进行解释。

## 3.使用javap

Java 命令行附带了javap工具，它显示有关类文件的字段、构造函数和方法的信息。

根据使用的选项，它可以反汇编一个类并显示构成Java字节码的指令。

### 3.1. javap

让我们使用javap命令查看最常见的Object类的字节码：

```shell
$ javap java.lang.Object
```

该命令的输出将显示Object类的最低限度构造：

```shell
public class java.lang.Object {
  public java.lang.Object();
  public final native java.lang.Class<?> getClass();
  public native int hashCode();
  public boolean equals(java.lang.Object);
  protected native java.lang.Object clone() throws java.lang.CloneNotSupportedException;
  public java.lang.String toString();
  public final native void notify();
  public final native void notifyAll();
  public final native void wait(long) throws java.lang.InterruptedException;
  public final void wait(long, int) throws java.lang.InterruptedException;
  public final void wait() throws java.lang.InterruptedException;
  protected void finalize() throws java.lang.Throwable;
  static {};
}
```

默认情况下，字节码输出将不包含带有私有 [访问修饰符](https://www.baeldung.com/java-access-modifiers)的字段/方法。

### 3.2. javap -p

要查看所有类和成员，我们可以使用-p参数：

```shell
public class java.lang.Object {
  public java.lang.Object();
  private static native void registerNatives();
  public final native java.lang.Class<?> getClass();
  public native int hashCode();
  public boolean equals(java.lang.Object);
  protected native java.lang.Object clone() throws java.lang.CloneNotSupportedException;
  // ...
}
```

在这里，我们可以观察到私有方法registerNatives也显示在Object类的字节码中。

### 3.3. javap -v

同样，我们可以使用-v参数来查看详细信息，例如堆栈大小和Object类方法的参数：

```bash
Classfile jar:file:/Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/jre/lib/rt.jar!/java/lang/Object.class
  Last modified Mar 15, 2017; size 1497 bytes
  MD5 checksum 5916745820b5eb3e5647da3b6cc6ef65
  Compiled from "Object.java"
public class java.lang.Object
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Class              #49            // java/lang/StringBuilder
   // ...
{
  public java.lang.Object();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=0, locals=1, args_size=1
         0: return
      LineNumberTable:
        line 37: 0

  public final native java.lang.Class<?> getClass();
    descriptor: ()Ljava/lang/Class;
    flags: ACC_PUBLIC, ACC_FINAL, ACC_NATIVE
    Signature: #26                          // ()Ljava/lang/Class<>;

  // ...
}
SourceFile: "Object.java"
```

### 3.4. javap -c

此外，javap命令允许使用-c参数反汇编整个Java类：

```shell
Compiled from "Object.java"
public class java.lang.Object {
  public java.lang.Object();
    Code:
       0: return
  public boolean equals(java.lang.Object);
    Code:
       0: aload_0
       1: aload_1
       2: if_acmpne     9
       5: iconst_1
       6: goto          10
       9: iconst_0
      10: ireturn
  protected native java.lang.Object clone() throws java.lang.CloneNotSupportedException;
  // ...
}
```

此外，javap命令允许我们使用各种参数检查系统信息、常量和内部类型签名。

我们可以使用-help参数列出javap命令支持的所有参数。

现在我们已经了解了用于查看类文件字节码的Java命令行解决方案，让我们来研究一些字节码操作库。

## 4.使用ASM

[ASM](https://www.baeldung.com/java-asm)是一种流行的面向性能的低级Java字节码操作和分析框架。

### 4.1. 设置

首先，让我们将最新的[asm](https://search.maven.org/search?q=g:org.ow2.asm a:asm)和[asm-util](https://search.maven.org/search?q=g:org.ow2.asm a:asm-util) Maven 依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.ow2.asm</groupId>
    <artifactId>asm</artifactId>
    <version>8.0.1</version>
</dependency>
<dependency>
    <groupId>org.ow2.asm</groupId>
    <artifactId>asm-util</artifactId>
    <version>8.0.1</version>
</dependency>
```

### 4.2. 查看字节码

然后，我们将使用[ClassReader](https://asm.ow2.io/javadoc/org/objectweb/asm/ClassReader.html)和[TraceClassVisitor](https://asm.ow2.io/javadoc/org/objectweb/asm/util/TraceClassVisitor.html)查看Object类的字节码：

```java
try {
    ClassReader reader = new ClassReader("java.lang.Object");
    StringWriter sw = new StringWriter();
    TraceClassVisitor tcv = new TraceClassVisitor(new PrintWriter(System.out));
    reader.accept(tcv, 0);
} catch (IOException e) {
    e.printStackTrace();
}
```

在这里，我们会注意到[TraceClassVisitor对象需要PrintWriter对象](https://www.baeldung.com/java-asm#1-using-the-traceclassvisitor)来提取和生成字节码：

```shell
// class version 52.0 (52)
// access flags 0x21
public class java/lang/Object {

  // compiled from: Object.java

  // access flags 0x1
  public <init>()V
   L0
    LINENUMBER 37 L0
    RETURN
    MAXSTACK = 0
    MAXLOCALS = 1

  // access flags 0x101
  public native hashCode()I

  // access flags 0x1
  public equals(Ljava/lang/Object;)Z
   L0
    LINENUMBER 149 L0
    ALOAD 0
    ALOAD 1
    IF_ACMPNE L1
    ICONST_1
    GOTO L2
   L1

    // ...
}
```

## 5. 使用 BCEL

字节代码工程库(通常称为[Apache Commons BCEL)](https://commons.apache.org/proper/commons-bcel/)提供了一种创建/操作Java类文件的便捷方法。

### 5.1. Maven 依赖

像往常一样，让我们将最新的[bcel](https://search.maven.org/search?q=g:org.apache.bcel  a:bcel) Maven 依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.apache.bcel</groupId>
    <artifactId>bcel</artifactId>
    <version>6.5.0</version>
</dependency>
```

### 5.2. 反汇编类并查看字节码

然后，我们可以使用[Repository](https://commons.apache.org/proper/commons-bcel/apidocs/org/apache/bcel/Repository.html)类来生成[JavaClass](https://commons.apache.org/proper/commons-bcel/apidocs/org/apache/bcel/classfile/JavaClass.html)对象：

```java
try { 
    JavaClass objectClazz = Repository.lookupClass("java.lang.Object");
    System.out.println(objectClazz.toString());
} catch (ClassNotFoundException e) { 
    e.printStackTrace(); 
}
```

在这里，我们在objectClazz对象上使用了toString方法 来以简洁的格式查看字节码：


```shell
public class java.lang.Object
file name		java.lang.Object
compiled from		Object.java
compiler version	52.0
access flags		33
constant pool		78 entries
ACC_SUPER flag		true

Attribute(s):
    SourceFile: Object.java

14 methods:
    public void <init>()
    private static native void registerNatives()
    public final native Class getClass() [Signature: ()Ljava/lang/Class<>;]
    public native int hashCode()
    public boolean equals(Object arg1)
    protected native Object clone()
      throws Exceptions: java.lang.CloneNotSupportedException
    public String toString()
    public final native void notify()
	
    // ...
```

此外，JavaClass类提供了getConstantPool、getFields和getMethods等方法来查看反汇编类的详细信息。

```java
assertEquals(objectClazz.getFileName(), "java.lang.Object");
assertEquals(objectClazz.getMethods().length, 14);
assertTrue(objectClazz.toString().contains("public class java.lang.Object"));

```

同样，set方法可用于字节码操作。

## 6. 使用Javassist

此外，我们可以使用提供高级 API 的[Javassist](https://www.baeldung.com/javassist)(Java 编程助手) 库来查看/操作Java字节码。

### 6.1. Maven 依赖

首先，我们将最新的[javassist](https://search.maven.org/search?q=g:org.javassist a:javassist) Maven 依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.javassist</groupId>
    <artifactId>javassist</artifactId>
    <version>3.27.0-GA</version>
</dependency>
```

### 6.2. 生成类文件

然后，我们可以使用[ClassPool](http://www.javassist.org/html/javassist/ClassPool.html)和[ClassFile](https://www.javassist.org/html/javassist/bytecode/ClassFile.html)类[来生成一个Java类](https://www.baeldung.com/javassist#generating-a-java-class)：

```java
try {
    ClassPool cp = ClassPool.getDefault();
    ClassFile cf = cp.get("java.lang.Object").getClassFile();
    cf.write(new DataOutputStream(new FileOutputStream("Object.class")));
} catch (NotFoundException e) {
    e.printStackTrace();
}
```

在这里，我们使用了write方法，它允许我们使用DataOutputStream对象编写类文件：

```plaintext
// Compiled from Object.java (version 1.8 : 52.0, super bit)
public class java.lang.Object {
  
  // Method descriptor #19 ()V
  // Stack: 0, Locals: 1
  public Object();
    0  return
      Line numbers:
        [pc: 0, line: 37]
  
  // Method descriptor #19 ()V
  private static native void registerNatives();
  
  // Method descriptor #24 ()Ljava/lang/Class;
  // Signature: ()Ljava/lang/Class<>;
  public final native java.lang.Class getClass();
  
  // Method descriptor #28 ()I
  public native int hashCode();
  
  // ...
```

此外， ClassFile类的对象提供对常量池、字段和方法的访问：

```shell
assertEquals(cf.getName(), "java.lang.Object"); 
assertEquals(cf.getMethods().size(), 14);
```

## 7.J类库

此外，我们可以使用基于 IDE 的插件来查看类文件的字节码。例如，让我们探索可用于 IntelliJ IDEA的jclasslib 字节码查看器插件。

### 7.1. 安装

首先，我们将使用“设置/首选项”对话框安装插件：

[![截图-2020-06-01](https://www.baeldung.com/wp-content/uploads/2020/06/Screen-Shot-2020-06-01-at-3.23.42-PM-1024x752.png)](https://www.baeldung.com/wp-content/uploads/2020/06/Screen-Shot-2020-06-01-at-3.23.42-PM.png)

### 7.2. 查看对象类的字节码

然后，我们可以选择“View”菜单下的“Show Bytecode With Jclasslib”选项来查看所选Object类的字节码：

[![截图-2020-06-01](https://www.baeldung.com/wp-content/uploads/2020/06/Screen-Shot-2020-06-01-at-3.24.59-PM.png)](https://www.baeldung.com/wp-content/uploads/2020/06/Screen-Shot-2020-06-01-at-3.24.59-PM.png)

接下来，将打开一个对话框以显示Object类的字节码：

[![截图-2020-06-01](https://www.baeldung.com/wp-content/uploads/2020/06/Screen-Shot-2020-06-01-at-3.25.41-PM-1024x476.png)](https://www.baeldung.com/wp-content/uploads/2020/06/Screen-Shot-2020-06-01-at-3.25.41-PM.png)

### 7.3. 查看详情

此外，我们可以使用 Jclasslib 插件对话框查看字节码的各种细节，如常量池、字段和方法：

[![截图-2020-06-01](https://www.baeldung.com/wp-content/uploads/2020/06/Screen-Shot-2020-06-01-at-3.26.23-PM-1024x569.png)](https://www.baeldung.com/wp-content/uploads/2020/06/Screen-Shot-2020-06-01-at-3.26.23-PM.png)

同样，我们有Bytecode Visualizer Plugin可以使用 Eclipse IDE 查看类文件的字节码。

## 八、总结

在本教程中，我们探讨了在Java中查看类文件字节码的方法。

首先，我们检查了javap命令及其各种参数。然后，我们浏览了一些字节码操作库，它们提供了查看和操作字节码的功能。

最后，我们研究了一个基于 IDE 的插件Jclasslib，它允许我们在 IntelliJ IDEA 中查看字节码。