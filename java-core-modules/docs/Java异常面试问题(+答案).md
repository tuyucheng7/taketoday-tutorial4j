## **一、概述**

异常是每个 Java 开发人员都应该熟悉的基本主题。本文提供了一些在面试中可能会出现的问题的答案。

## **2.问题**

### **Q1。什么是异常？**

异常是程序执行过程中发生的异常事件，它扰乱了程序指令的正常流程。

### **Q2。throw 和 throws 关键字的目的是什么？**

throws关键字用于指定方法在执行期间可能引发异常*。*它在调用方法时强制执行显式异常处理：

```java
public void simpleMethod() throws Exception {
    // ...
}复制
```

throw关键字允许我们抛出一个异常对象来中断程序的正常流程*。*当程序无法满足给定条件时，最常使用这种方法：

```java
if (task.isTooComplicated()) {
    throw new TooComplicatedException("The task is too complicated");
}复制
```

### **Q3. 如何处理异常？**

通过使用*try-catch-finally*语句：

```java
try {
    // ...
} catch (ExceptionType1 ex) {
    // ...
} catch (ExceptionType2 ex) {
    // ...
} finally {
    // ...
}复制
```

可能发生异常的代码块包含在*try*块中。此块也称为“受保护”或“受保护”代码。

如果发生异常，则执行与抛出的异常相匹配的*catch*块，否则，将忽略所有*catch块。*

*finally*块总是在*try*块退出后执行，无论其内部是否抛出异常。

### **Q4. 如何捕获多个异常？**

可以通过三种方式处理代码块中的多个异常。

第一种是使用可以处理所有抛出的异常类型的*catch块：*

```java
try {
    // ...
} catch (Exception ex) {
    // ...
}复制
```

您应该记住，推荐的做法是使用尽可能准确的异常处理程序。

过于宽泛的异常处理程序会使您的代码更容易出错，捕获未预料到的异常，并导致程序出现意外行为。

第二种方法是实现多个 catch 块：

```java
try {
    // ...
} catch (FileNotFoundException ex) {
    // ...
} catch (EOFException ex) {
    // ...
}复制
```

注意，如果异常有继承关系；子类型必须在前，父类型在后。如果我们不这样做，就会导致编译错误。

第三种是使用多捕获块：

```java
try {
    // ...
} catch (FileNotFoundException | EOFException ex) {
    // ...
}复制
```

这个特性，在 Java 7 中首次引入；减少代码重复，更易于维护。

### **Q5. 已检查异常和未检查异常有什么区别？**

*检查异常必须在try-catch*块中处理或在*throws*子句中声明；而未经检查的异常不需要处理或声明。

检查异常和未检查异常也分别称为编译时异常和运行时异常。

*除了Error*、*RuntimeException*及其子类指示的异常之外，所有异常都是已检查的异常。

### **Q6. 异常和错误有什么区别？**

异常是表示可以从中恢复的条件的事件，而错误表示通常无法从中恢复的外部情况。

JVM 抛出的所有错误都是*Error*或其子类之一的实例，比较常见的包括但不限于：

-   *OutOfMemoryError——*当 JVM 由于内存不足而无法分配更多对象时抛出，并且垃圾收集器无法提供更多可用对象
-   *StackOverflowError* – 当线程的堆栈空间用完时发生，通常是因为应用程序递归太深
-   *ExceptionInInitializerError* - 表示在评估静态初始化程序期间发生了意外异常
-   *NoClassDefFoundError——*当类加载器试图加载一个类的定义但找不到它时抛出，通常是因为在类路径中找不到所需的*类文件*
-   *UnsupportedClassVersionError* – 当 JVM 尝试读取*类*文件并确定文件中的版本不受支持时发生，通常是因为该文件是使用较新版本的 Java 生成的

*尽管可以使用try*语句处理错误，但这不是推荐的做法，因为无法保证程序在抛出错误后能够可靠地执行任何操作。

### **Q7. 执行下面的代码块会抛出什么异常？**

```java
Integer[][] ints = { { 1, 2, 3 }, { null }, { 7, 8, 9 } };
System.out.println("value = " + ints[1][1].intValue());复制
```

它抛出*ArrayIndexOutOfBoundsException*，因为我们试图访问大于数组长度的位置。

### **Q8. 什么是异常链接？**

在为响应另一个异常而引发异常时发生。这使我们能够发现我们提出的问题的完整历史：

```java
try {
    task.readConfigFile();
} catch (FileNotFoundException ex) {
    throw new TaskException("Could not perform task", ex);
}复制
```

### **Q9. 什么是堆栈跟踪以及它与异常有何关系？**

堆栈跟踪提供了从应用程序开始到发生异常点所调用的类和方法的名称。

这是一个非常有用的调试工具，因为它使我们能够准确地确定异常在应用程序中被抛出的位置以及导致它的原始原因。

### **Q10。为什么要对异常进行子类化？**

如果异常类型不是由 Java 平台中已经存在的异常类型表示的，或者如果您需要向客户端代码提供更多信息以更精确地处理它，那么您应该创建一个自定义异常。

决定是否应检查或取消检查自定义异常完全取决于业务案例。但是，根据经验；如果可以预期使用您的异常的代码可以从中恢复，则创建一个已检查的异常，否则将其取消检查。

此外，您应该继承与您要抛出的异常密切相关的最具体的*异常子类。*如果没有这样的类，则选择*Exception*作为父类。

### **Q11. 异常有哪些优点？**

传统的错误检测和处理技术通常会导致意大利面条式代码难以维护和阅读。但是，异常使我们能够将应用程序的核心逻辑与发生意外情况时的操作细节分开。

此外，由于 JVM 向后搜索调用堆栈以找到任何对处理特定异常感兴趣的方法；我们获得了在调用堆栈中向上传播错误的能力，而无需编写额外的代码。

此外，由于程序中抛出的所有异常都是对象，因此可以根据其类层次结构对它们进行分组或分类。*这允许我们通过在catch*块中指定异常的超类来在单个异常处理程序中捕获一组异常。

### **Q12. 你能在 Lambda 表达式的主体中抛出任何异常吗？**

当使用 Java 已经提供的标准功能接口时，您只能抛出未经检查的异常，因为标准功能接口在方法签名中没有“throws”子句：

```java
List<Integer> integers = Arrays.asList(3, 9, 7, 0, 10, 20);
integers.forEach(i -> {
    if (i == 0) {
        throw new IllegalArgumentException("Zero not allowed");
    }
    System.out.println(Math.PI / i);
});复制
```

但是，如果您使用的是自定义功能接口，则可能会抛出已检查的异常：

```java
@FunctionalInterface
public static interface CheckedFunction<T> {
    void apply(T t) throws Exception;
}复制
public void processTasks(
  List<Task> taks, CheckedFunction<Task> checkedFunction) {
    for (Task task : taks) {
        try {
            checkedFunction.apply(task);
        } catch (Exception e) {
            // ...
        }
    }
}

processTasks(taskList, t -> {
    // ...
    throw new Exception("Something happened");
});复制
```

### **Q13. 重写抛出异常的方法需要遵循哪些规则？**

有几条规则规定了必须如何在继承上下文中声明异常。

当父类方法不抛出任何异常时，子类方法不能抛出任何已检查的异常，但它可能会抛出任何未检查的异常。

这是一个示例代码来演示这一点：

```java
class Parent {
    void doSomething() {
        // ...
    }
}

class Child extends Parent {
    void doSomething() throws IllegalArgumentException {
        // ...
    }
}复制
```

下一个示例将无法编译，因为覆盖方法抛出未在覆盖方法中声明的已检查异常：

```java
class Parent {
    void doSomething() {
        // ...
    }
}

class Child extends Parent {
    void doSomething() throws IOException {
        // Compilation error
    }
}复制
```

当父类方法抛出一个或多个已检查异常时，子类方法可以抛出任何未检查异常；所有、没有或声明的已检查异常的子集，甚至更多，只要它们具有相同或更窄的范围。

这是成功遵循先前规则的示例代码：

```java
class Parent {
    void doSomething() throws IOException, ParseException {
        // ...
    }

    void doSomethingElse() throws IOException {
        // ...
    }
}

class Child extends Parent {
    void doSomething() throws IOException {
        // ...
    }

    void doSomethingElse() throws FileNotFoundException, EOFException {
        // ...
    }
}复制
```

请注意，这两种方法都遵守规则。第一个抛出的异常比覆盖的方法少，而第二个抛出的异常更多；它们的范围更窄。

但是，如果我们尝试抛出父类方法未声明的已检查异常，或者抛出范围更广的异常；我们会得到一个编译错误：

```java
class Parent {
    void doSomething() throws FileNotFoundException {
        // ...
    }
}

class Child extends Parent {
    void doSomething() throws IOException {
        // Compilation error
    }
}复制
```

当父类方法有一个带有未检查异常的 throws 子句时，子类方法可以不抛出任何或任意数量的未检查异常，即使它们不相关。

这是一个遵守规则的例子：

```java
class Parent {
    void doSomething() throws IllegalArgumentException {
        // ...
    }
}

class Child extends Parent {
    void doSomething()
      throws ArithmeticException, BufferOverflowException {
        // ...
    }
}复制
```

### **Q14. 以下代码可以编译吗？**

```java
void doSomething() {
    // ...
    throw new RuntimeException(new Exception("Chained Exception"));
}复制
```

是的。当链接异常时，编译器只关心链中的第一个，因为它检测到一个未经检查的异常，我们不需要添加 throws 子句。

### **Q15. 有什么方法可以从没有 throws 子句的方法中抛出检查异常？**

是的。我们可以利用编译器执行的类型擦除，让它认为我们正在抛出一个未经检查的异常，而实际上；我们抛出一个检查异常：

```java
public <T extends Throwable> T sneakyThrow(Throwable ex) throws T {
    throw (T) ex;
}

public void methodWithoutThrows() {
    this.<RuntimeException>sneakyThrow(new Exception("Checked Exception"));
}复制
```

## **3.结论**

在本文中，我们探讨了一些可能出现在 Java 开发人员技术面试中的有关异常的问题。这不是一个详尽的列表，它应该只是作为进一步研究的开始。

在 Baeldung，我们祝愿您在接下来的面试中取得成功。