## 1. 概述

命令模式是一种行为型设计模式，是[GoF](https://en.wikipedia.org/wiki/Design_Patterns)正式设计模式集合的一部分。简单地说，**该模式旨在将执行给定动作(命令)所需的所有数据封装在一个对象中**，包括要调用的方法、方法的参数以及方法所属的对象。

该模型允许我们**将生成命令的对象与其使用者分离**，因此这就是该模式通常被称为生产者-消费者模式的原因。

在本教程中，我们学习如何通过使用面向对象和对象功能方法在Java中实现命令模式，并且我们会介绍它在哪些用例中很有用。

## 2. 面向对象的实现

**在经典实现中，命令模式需要实现四个组件：命令、接收器、调用程序和客户端**。

为了了解该模式的工作原理以及每个组件所扮演的角色，让我们创建一个基本示例。

假设我们要开发一个文本文件应用程序。在这种情况下，我们应该实现执行某些与文本文件相关操作所需的所有功能，例如打开、写入、保存文本文件等。因此，我们应该将应用程序分解为上述四个组件。

### 2.1 命令类

**命令是一个对象，其作用是存储执行动作所需的所有信息**，包括要调用的方法、方法参数和实现该方法的对象(称为接收者)。

为了更准确地了解命令对象的工作原理，首先我们开发一个简单的命令层，它只包含一个接口和两个实现：

```java
@FunctionalInterface
public interface TextFileOperation {
    String execute();
}
```

```java
public class OpenTextFileOperation implements TextFileOperation {

    private TextFile textFile;

    // constructors

    @Override
    public String execute() {
        return textFile.open();
    }
}
```

```java
public class SaveTextFileOperation implements TextFileOperation {

    // same field and constructor as above

    @Override
    public String execute() {
        return textFile.save();
    }
}
```

在这种情况下，TextFileOperation接口定义了命令对象的API，OpenTextFileOperation和SaveTextFileOperation两个实现执行具体操作。前者打开一个文本文件，而后者保存一个文本文件。

命令对象的功能一目了然：TextFileOperation命令封装了打开和保存文本文件所需的所有信息，包括接收者对象、要调用的方法和参数(在本例中，不需要参数，但他们可以接收参数)。

**值得强调的是，执行文件操作的组件是接收器(TextFile实例)**。

### 2.2 接收器类

接收器是**执行一组内聚操作**的对象，它是调用命令的execute()方法时执行实际操作的组件。

在这种情况下，我们需要定义一个接收器类，其作用是对TextFile对象进行建模：

```java
public class TextFile {

    private String name;

    // constructor

    public String open() {
        return "Opening file " + name;
    }

    public String save() {
        return "Saving file " + name;
    }

    // additional text file methods (editing, writing, copying, pasting)
}
```

### 2.3 调用者类

**调用者是一个知道如何执行给定命令但不知道该命令是如何实现的对象，它只知道命令的接口**。

在某些情况下，除了执行命令之外，调用者还会存储命令并对其进行排队。这对于实现一些附加功能非常有用，例如宏录制或撤消和重做功能。

在我们的示例中，很明显，必须有一个额外的组件负责调用命令对象并通过命令的execute()方法执行它们，**这正是调用者类发挥作用的地方**。

让我们看一下调用者的基本实现：

```java
public class TextFileOperationExecutor {

    private final List<TextFileOperation> textFileOperations = new ArrayList<>();

    public String executeOperation(TextFileOperation textFileOperation) {
        textFileOperations.add(textFileOperation);
        return textFileOperation.execute();
    }
}
```

**TextFileOperationExecutor类只是一个薄的抽象层，它将命令对象与其使用者分离**，并调用封装在TextFileOperation命令对象中的方法。

在这种情况下，该类还将命令对象存储在List中。当然，这在模式实现中不是强制性的，除非我们需要对操作的执行过程添加一些进一步的控制。

### 2.4 客户端类

客户端是一个对象，它通过指定要执行的命令以及在过程的哪个阶段执行命令来控制命令执行过程。

因此，如果我们想要正统地使用模式的正式定义，我们必须使用典型的main方法创建一个客户端类：

```java
public static void main(String[] args) {
    TextFileOperationExecutor textFileOperationExecutor = new TextFileOperationExecutor();
    textFileOperationExecutor.executeOperation(new OpenTextFileOperation(new TextFile("file1.txt"))));
    textFileOperationExecutor.executeOperation(new SaveTextFileOperation(new TextFile("file2.txt"))));
}
```

## 3. 对象函数实现

到目前为止，我们已经使用面向对象的方法来实现命令模式，这一切都很好。

从Java 8开始，我们可以使用基于lambda表达式和方法引用的对象函数式方法，使代码更紧凑、更简洁。

### 3.1 使用Lambda表达式

由于TextFileOperation接口是一个[函数式接口](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/function/package-summary.html)，我们可以**将lambda表达式形式的命令对象传递给调用程序**，而无需显式创建TextFileOperation实例：

```java
TextFileOperationExecutor textFileOperationExecutor = new TextFileOperationExecutor();
textFileOperationExecutor.executeOperation(() -> "Opening file file1.txt");
textFileOperationExecutor.executeOperation(() -> "Saving file file1.txt");
```

我们减少了样板代码的数量，因此现在的实施看起来更加精简和简洁。

即便如此，问题仍然存在：与面向对象的方法相比，这种方法是否更好？

嗯，这很棘手。如果我们假设在大多数情况下更紧凑的代码意味着更好的代码，那么确实如此。

**根据经验，我们应该根据每个用例评估何时使用lambda表达式**。

### 3.2 使用方法引用

同样，我们可以使用方法引用**将命令对象传递给调用者**：

```java
TextFileOperationExecutor textFileOperationExecutor = new TextFileOperationExecutor();
TextFile textFile = new TextFile("file1.txt");
textFileOperationExecutor.executeOperation(textFile::open);
textFileOperationExecutor.executeOperation(textFile::save);
```

在这种情况下，实现比使用lambdas的实现稍微冗长一点，因为我们仍然必须创建TextFile实例。

## 4. 总结

在本文中，我们了解了命令模式的关键概念以及如何使用面向对象的方法以及lambda表达式和方法引用的组合在Java中实现命令模式。