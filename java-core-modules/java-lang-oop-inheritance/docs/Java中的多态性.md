## 1. 概述

所有面向对象编程(OOP)语言都需要表现出四个基本特征：抽象、封装、继承和[多态](https://www.baeldung.com/cs/polymorphism)。

在本文中，我们介绍了两种核心类型的多态性：**静态或编译时多态性以及动态或运行时多态性**。静态多态性是在[编译时](https://www.baeldung.com/cs/compile-load-execution-time)强制执行的，而动态多态性是在[运行时](https://www.baeldung.com/cs/runtime-vs-compile-time)实现的。

## 2. 静态多态性

根据[维基百科](https://en.wikipedia.org/wiki/Template_metaprogramming#Static_polymorphism)，**静态多态性是对多态性的模仿，它在编译时解析，因此消除了运行时虚拟表查找**。

例如，我们在文件管理器应用程序中的TextFile类可以有三个具有与read()方法相同签名的方法：

```java
public class TextFile extends GenericFile {
    //...

    public String read() {
        return this.getContent()
              .toString();
    }

    public String read(int limit) {
        return this.getContent()
              .toString()
              .substring(0, limit);
    }

    public String read(int start, int stop) {
        return this.getContent()
              .toString()
              .substring(start, stop);
    }
}
```

在代码编译期间，编译器会验证对read方法的所有调用是否对应于上面定义的三种方法中的至少一种。

## 3. 动态多态性

通过动态多态性，**Java虚拟机(JVM)会检测在将子类分配给其父形式时要执行的适当方法**。这是必要的，因为子类可能会覆盖父类中定义的部分或全部方法。

在一个假设的文件管理器应用程序中，让我们为所有名为GenericFile的文件定义父类：

```java
public class GenericFile {
    private String name;

    // ...

    public String getFileInfo() {
        return "Generic File Impl";
    }
}
```

我们还可以实现一个ImageFile类，它扩展了GenericFile但覆盖了getFileInfo()方法并附加了更多信息：

```java
public class ImageFile extends GenericFile {
    private int height;
    private int width;

    // ... getters and setters

    public String getFileInfo() {
        return "Image File Impl";
    }
}
```

当我们创建ImageFile的实例并将其分配给GenericFile类时，隐式强制转换就完成了。但是，JVM保留对ImageFile实际形式的引用。

**上面的构造类似于方法覆盖**。我们可以通过以下方式调用getFileInfo()方法来确认这一点：

```java
public static void main(String[] args) {
    GenericFile genericFile = new ImageFile("SampleImageFile", 200, 100, new BufferedImage(100, 200, BufferedImage.TYPE_INT_RGB)
        .toString()
        .getBytes(), "v1.0.0");
    logger.info("File Info: \n" + genericFile.getFileInfo());
}
```

正如预期的那样，genericFile.getFileInfo()触发了ImageFile类的getFileInfo()方法，如下面的输出所示：

```bash
File Info: 
Image File Impl
```

## 4. Java中的其他多态特性

除了Java中的这两种主要类型的多态性之外，Java编程语言中还有其他表现出多态性的特征。让我们讨论其中的一些特征。

### 4.1 胁迫

多态强制处理编译器完成的隐式类型转换以防止类型错误。一个典型的例子是整数和字符串的拼接：

```java
String str = “string” + 2;
```

### 4.2 运算符重载

运算符或方法重载是指同一符号或运算符根据上下文具有不同含义(形式)的多态特性。

例如，加号(+)可用于数学加法和字符串拼接。在任何一种情况下，只有上下文(即参数类型)决定符号的解释：

```java
String str = "2" + 2;
int sum = 2 + 2;
System.out.printf(" str = %s\n sum = %d\n", str, sum);
```

输出：

```shell
str = 22
sum = 4
```

### 4.3 多态参数

参数多态性允许类中的参数或方法的名称与不同的类型相关联。我们在下面有一个典型的例子，我们将content定义为String，后来又定义为Integer：

```java
public class TextFile extends GenericFile {
    private String content;

    public String setContentDelimiter() {
        int content = 100;
        this.content = this.content + content;
    }
}
```

同样重要的是要注意**多态参数的声明可能会导致称为变量隐藏的问题**，其中参数的局部声明总是覆盖另一个具有相同名称的参数的全局声明。

为了解决这个问题，通常建议使用全局引用(例如this关键字)来指向局部上下文中的全局变量。

### 4.4 多态亚型

多态子类型方便地使我们可以将多个子类型分配给一个类型，并期望对该类型的所有调用都触发子类型中的可用定义。

例如，如果我们有一个GenericFile的集合，并且我们对它们中的每一个调用getInfo()方法，我们可以预期输出会有所不同，具体取决于集合中每个元素的派生子类型：

```java
GenericFile [] files = {new ImageFile("SampleImageFile", 200, 100, new BufferedImage(100, 200, BufferedImage.TYPE_INT_RGB).toString() 
    .getBytes(), "v1.0.0"), new TextFile("SampleTextFile", "This is a sample text content", "v1.0.0")};
 
for (int i = 0; i < files.length; i++) {
    files[i].getInfo();
}
```

**向上转型和后期绑定的结合使子类型多态性成为可能**。向上转型涉及将继承层次结构从超类型转换为子类型：

```java
ImageFile imageFile = new ImageFile();
GenericFile file = imageFile;
```

上述结果的结果是无法在新的超类型GenericFile上调用特定于ImageFile的方法。但是，子类型中的方法会覆盖超类型中定义的类似方法。

为了解决在向上转换为超类型时无法调用特定于子类型的方法的问题，我们可以对从超类型到子类型的继承进行向下转换。这是通过以下方式完成的：

```java
ImageFile imageFile = (ImageFile) file;
```

**后期绑定策略帮助编译器决定向上转型后触发谁的方法**。在上面示例中的imageFile#getInfo与file#getInfo的情况下，编译器保留对ImageFile的getInfo方法的引用。

## 5. 多态性问题

让我们看一下多态性中的一些歧义，如果检查不当，这些歧义可能会导致运行时错误。

### 5.1 向下转型期间的类型识别

回想一下，我们之前在执行向上转换后失去了对某些特定于子类型的方法的访问。虽然我们能够通过向下转型解决这个问题，但这并不能保证实际的类型检查。

例如，如果我们执行向上转换和随后的向下转换：

```java
GenericFile file = new GenericFile();
ImageFile imageFile = (ImageFile) file;
System.out.println(imageFile.getHeight());
```

我们注意到编译器允许将GenericFile向下转型为ImageFile，即使该类实际上是GenericFile而不是ImageFile。

因此，如果我们尝试在imageFile类上调用getHeight()方法，我们会得到一个ClassCastException，因为GenericFile没有定义getHeight()方法：

```shell
Exception in thread "main" java.lang.ClassCastException:
GenericFile cannot be cast to ImageFile
```

为了解决这个问题，JVM执行运行时类型信息(RTTI)检查。我们还可以通过使用instanceof关键字来尝试显式类型标识，如下所示：

```java
ImageFile imageFile;
if (file instanceof ImageFile) {
    imageFile = file;
}
```

以上有助于避免在运行时出现ClassCastException异常。可以使用的另一个选项是将强制转换包装在try和catch块中并捕获ClassCastException。

应该注意的是，由于有效验证类型正确所需的时间和资源，**RTTI检查非常昂贵**。此外，频繁使用instanceof关键字几乎总是意味着糟糕的设计。

### 5.2 脆弱的基类问题

根据[维基百科](https://en.wikipedia.org/wiki/Fragile_base_class)，如果对基类看似安全的修改可能导致派生类出现故障，则基类或超类被认为是脆弱的。

让我们考虑一个名为GenericFile的超类及其子类TextFile的声明：

```java
public class GenericFile {
    private String content;

    void writeContent(String content) {
        this.content = content;
    }

    void toString(String str) {
        str.toString();
    }
}
```

```java
public class TextFile extends GenericFile {
    @Override
    void writeContent(String content) {
        toString(content);
    }
}
```

当我们修改GenericFile类时：

```java
public class GenericFile {
    // ...

    void toString(String str) {
        writeContent(str);
    }
}
```

我们观察到，上面的修改使TextFile在writeContent()方法中无限递归，最终导致堆栈溢出。

为了解决脆弱的基类问题，我们可以使用final关键字来防止子类覆盖writeContent()方法。适当的文档也可以提供帮助。最后但并非最不重要的一点是，组合通常应该优先于继承。

## 6. 总结

在本文中，我们讨论了多态性的基本概念，同时关注其优点和缺点。