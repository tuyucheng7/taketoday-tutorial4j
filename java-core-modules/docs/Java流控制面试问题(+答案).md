## **一、简介**

控制流语句允许开发人员使用决策、循环和分支来有条件地改变特定代码块的执行流。

在这篇文章中，我们将讨论一些在面试中可能会出现的流程控制面试问题，并在适当的时候；我们实施示例以更好地理解他们的答案。

## **2.问题**

### **Q1。描述\*if-then\*和\*if-then-else\*语句。什么类型的表达式可以用作条件？**

*这两个语句都告诉我们的程序仅当特定条件的计算结果为true*时才执行其中的代码。但是，*if-then-else*语句提供了辅助执行路径，以防 if 子句的计算结果为*false*：

```java
if (age >= 21) {
    // ...
} else {
    // ...
}复制
```

与其他编程语言不同，Java 只支持*布尔*表达式作为条件。如果我们尝试使用不同类型的表达式，则会出现编译错误。

### **Q2。描述\*switch\*语句。\*switch\*子句中可以使用哪些对象类型？**

Switch 允许根据变量的值选择多个执行路径。

每个路径都标有*case*或*default*，*switch*语句评估每个*case*表达式是否匹配，并执行匹配标签后面的所有语句，直到找到*break语句。*如果找不到匹配项，将改为执行*默认块：*

```java
switch (yearsOfJavaExperience) {
    case 0:
        System.out.println("Student");
        break;
    case 1:
        System.out.println("Junior");
        break;
    case 2:
        System.out.println("Middle");
        break;
    default:
        System.out.println("Senior");
}复制
```

我们可以使用*byte*、*short*、*char*、*int*，它们的包装版本，*enum* s 和*String* s 作为*开关*值。

### **Q3. 当我们忘记在\*switch\*的\*case\*子句中加入\*break\*语句时会发生什么？**

switch*语句*失败了。这意味着它将继续执行所有*case*标签，直到 if 找到*break*语句，即使这些标签与表达式的值不匹配。

下面是一个例子来证明这一点：

```java
int operation = 2;
int number = 10;

switch (operation) {
    case 1:
        number = number + 10;
        break;
    case 2:
        number = number - 4;
    case 3:
        number = number / 3;
    case 4:
        number = number * 10;
        break;
}复制
```

运行代码后，*number*的值为 20，而不是 6。这在我们想要将同一操作与多个案例相关联的情况下很有用。

### **Q4. 什么时候使用 S \*witch\*优于 I \*f-Then-Else\*语句，反之亦然？**

switch语句更适合针对许多单个值测试单个变量或多个值将执行相同代码的情况*：*

```java
switch (month) {
    case 1:
    case 3:
    case 5:
    case 7:
    case 8:
    case 10:
    case 12:
        days = 31;
        break;
case 2:
    days = 28;
    break;
default:
    days = 30;
}复制
```

当我们需要检查值的范围或多个条件时，*if-then-else*语句更可取：

```java
if (aPassword == null || aPassword.isEmpty()) {
    // empty password
} else if (aPassword.length() < 8 || aPassword.equals("12345678")) {
    // weak password
} else {
    // good password
}复制
```

### **Q5. Java 支持哪些类型的循环？**

Java 提供三种不同类型的循环：*for*、*while*和*do-while*。

for*循环*提供了一种迭代一系列值的方法。当我们事先知道任务将重复多少次时，它最有用：

```java
for (int i = 0; i < 10; i++) {
     // ...
}复制
```

while*循环可以在特定条件为**真*时执行语句块：

```java
while (iterator.hasNext()) {
    // ...
}复制
```

do *-while是**while*语句的变体，其中*布尔*表达式的计算位于循环的底部。这保证代码将至少执行一次：

```java
do {
    // ...
} while (choice != -1);复制
```

### **Q6. 什么是\*增强型 for\*循环？**

*是for*语句的另一种语法，旨在遍历集合、数组、枚举或任何实现*Iterable*接口的对象的所有元素：

```java
for (String aString : arrayOfStrings) {
    // ...
}复制
```

### **Q7. 如何提前退出循环？**

使用*break*语句，我们可以立即终止循环的执行：

```java
for (int i = 0; ; i++) {
    if (i > 10) {
        break;
    }
}复制
```

### **Q8. \*未标记和标记的break\*语句有什么区别？**

未标记的*break*语句终止最里面的*switch*、*for*、*while*或*do-while*语句，而标记的*break*结束外部语句的执行。

让我们创建一个例子来证明这一点：

```java
int[][] table = { { 1, 2, 3 }, { 25, 37, 49 }, { 55, 68, 93 } };
boolean found = false;
int loopCycles = 0;

outer: for (int[] rows : table) {
    for (int row : rows) {
        loopCycles++;
        if (row == 37) {
            found = true;
            break outer;
        }
    }
}复制
```

当找到数字 37 时，标记的*break*语句终止最外层的*for*循环，不再执行循环。因此，*loopCycles*以值 5 结束。

然而，未标记的*break*仅结束最内层的语句，将控制流返回到最外层的*for*继续循环到*表*变量中的*下一行*，使*loopCycles*以值 8 结束。

### **Q9. \*无标签和有标签的continue\*语句有什么区别？**

未标记的*continue语句跳到最内层的**for*、*while*或*do-while*循环中当前迭代的末尾，而标记的*continue*跳到标有给定标签的外部循环。

这是一个演示这一点的示例：

```java
int[][] table = { { 1, 15, 3 }, { 25, 15, 49 }, { 15, 68, 93 } };
int loopCycles = 0;

outer: for (int[] rows : table) {
    for (int row : rows) {
        loopCycles++;
        if (row == 15) {
            continue outer;
        }
    }
}复制
```

推理与上一个问题相同。带标签的*continue*语句终止最外层的*for*循环。

因此，*loopCycles*以值 5 结束，而未标记的版本仅终止最内层的语句，使 loopCycles*以*值 9 结束。

### **Q10。\*描述try-catch-finally\*结构中的执行流程。**

当程序进入*try块，并在其中抛出异常时，* *try*块的执行被中断，控制流继续到catch 块，该*catch*块可以处理抛出的异常。

如果不存在这样的块，则当前方法执行停止，并将异常抛给调用堆栈上的前一个方法。或者，如果没有发生异常，则忽略所有*catch块，程序继续正常执行。*

*无论在try*块的主体内是否抛出异常，*finally*块总是被执行。

### **Q11. \*finally\*块在哪些情况下可能不会被执行？**

当 JVM 在执行*try*或*catch*块时终止，例如，通过调用*System.exit()，*或者当执行线程被中断或杀死时，finally 块不会被执行。

### **Q12. 执行以下代码的结果是什么？**

```java
public static int assignment() {
    int number = 1;
    try {
        number = 3;
        if (true) {
            throw new Exception("Test Exception");
        }
        number = 2;
    } catch (Exception ex) {
        return number;
    } finally {
        number = 4;
    }
    return number;
}

System.out.println(assignment());复制
```

代码输出数字 3。即使始终执行*finally块，这也只会在**try*块退出后发生。

在示例中，*return语句在**try-catch*块结束之前执行。*因此， finally*块中对*number*的赋值不起作用，因为变量已经返回给赋值方法的调用*代码*。

### **Q13. 在哪些情况下可以使用\*try-finally\*块，即使可能不会抛出异常？**

*当我们想要确保不会因遇到break*、*continue*或*return*语句而意外绕过代码中使用的资源清理时，此块很有用：

```java
HeavyProcess heavyProcess = new HeavyProcess();
try {
    // ...
    return heavyProcess.heavyTask();
} finally {
    heavyProcess.doCleanUp();
}复制
```

此外，我们可能会面临无法在本地处理抛出的异常的情况，或者我们希望当前方法仍然抛出异常，同时允许我们释放资源：

```java
public void doDangerousTask(Task task) throws ComplicatedException {
    try {
        // ...
        task.gatherResources();
        if (task.isComplicated()) {
            throw new ComplicatedException("Too difficult");
        }
        // ...
    } finally {
        task.freeResources();
    }
}复制
```

### **Q14. \*try-with-resources\*如何工作？**

try *-with-resources语句在执行**try*块之前声明并初始化一个或多个资源，并在语句结束时自动关闭它们，而不管该块是正常完成还是突然完成。任何实现*AutoCloseable*或*Closeable*接口的对象都可以用作资源：

```java
try (StringWriter writer = new StringWriter()) {
    writer.write("Hello world!");
}复制
```

## **3.结论**

在本文中，我们介绍了 Java 开发人员技术面试中出现的一些与控制流语句有关的最常见问题。这应该只被视为进一步研究的开始，而不是一个详尽的清单。

祝你面试顺利。