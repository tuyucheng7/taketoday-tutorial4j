## 1. 概述

在本文中，我们将讨论Kotlin中的init块、属性初始化器和构造函数之间的区别。

## 2. 示例类

为了理解Kotlin中构造函数和init块之间的区别，我们将在整篇文章中使用以下示例：

```kotlin
class Person(val firstName: String, val lastName: String) {

    private val fullName: String = "$firstName $lastName".trim()
        .also { println("Initializing full name") }

    init {
        println("You're $fullName")
    }

    private val initials = "${firstName.firstOrEmpty()}${lastName.firstOrEmpty()}".trim()
        .also { println("Initializing initials") }

    init {
        println("You're initials are $initials")
    }

    constructor(lastName: String) : this("", lastName) {
        println("I'm secondary")
    }

    private fun String.firstOrEmpty(): Char = firstOrNull()?.toUpperCase() ?: ' '
}
```

在上面的示例中，我们有一个主构造函数，它接收两个String作为构造的输入。此外，我们还有一个仅接收此人姓氏的lastName构造函数。除了这两个构造函数之外，我们还根据构造函数输入声明了两个具有属性初始值设定项的变量。最后，我们有两个初始化块(以关键字init为前缀的块)。

## 3. 构造函数和初始化块

与辅助构造函数相反，**主构造函数不能包含任何代码**。为了克服这个限制，我们可以将初始化逻辑放在init块和属性初始化器中，就像我们在上面的例子中所做的那样。

在实例初始化期间，**Kotlin按照它们在类主体中出现的相同顺序执行初始化程序块和属性初始化程序**。因此，如果我们创建Person类的实例，我们将看到一些日志，其顺序与它们在类中出现的顺序相同：

```kotlin
val p = Person("ali", "dehghani")
```

上述对象实例化的日志如下：

```shell
Initializing full name
You're ali dehghani
Initializing initials
You're initials are AD
```

如上所示，第一个日志用于fullName属性初始化器，第二个日志用于第一个init块，第三个日志用于initials属性初始化器，最后一个用于最后一个init块。

### 3.1 字节码表示

尽管我们不能在Kotlin的主构造函数中放置一些代码，但为构造函数生成的字节码将包含所有初始化逻辑。基本上，**Kotlin编译器将生成一个大型构造函数，其中包含来自所有属性初始化器和init块初始化器的逻辑**。

简而言之，**init块和属性初始值设定项最终将作为主构造函数的一部分**。显然，我们可以通过查看生成的字节码来验证这一点：

```bash
>> kotlinc Person.kt
>> javap -c -p cn.tuyucheng.taketoday.initblock.Person
// primary constructor
public cn.tuyucheng.taketoday.initblock.Person(java.lang.String, java.lang.String);
    Code:
       // primary constructor properties
      13: invokespecial #20                 // Method Object."<init>":()V
      16: aload_0
      17: aload_1
      18: putfield      #23                 // Field firstName:LString;
      21: aload_0
      22: aload_2
      23: putfield      #25                 // Field lastName:LString;
      26: aload_0

      // full name property initializer
      27: new           #27                 // class StringBuilder
      30: dup
      31: invokespecial #28                 // Method StringBuilder."<init>":()V
      34: aload_0
      35: getfield      #23                 // Field firstName:LString;
      38: invokevirtual #32                 // Method StringBuilder.append:(LString;)LStringBuilder;
      41: bipush        32
      43: invokevirtual #35                 // Method StringBuilder.append:(C)LStringBuilder;
      46: aload_0
      47: getfield      #25                 // Field lastName:LString;
      50: invokevirtual #32                 // Method StringBuilder.append:(LString;)LStringBuilder;
      53: invokevirtual #39                 // Method StringBuilder.toString:()LString;

      // printing
      99: ldc           #57                 // String Initializing full name
     101: astore        8
     103: iconst_0
     104: istore        9
     106: getstatic     #63                 // Field System.out:LPrintStream;
     109: aload         8
     111: invokevirtual #69                 // Method PrintStream.println:(LObject;)V
     
     // first init block
     123: putfield      #78                 // Field fullName:LString;
     126: nop
     127: ldc           #80                 // String You\'re
     129: aload_0
     130: getfield      #78                 // Field fullName:LString;
     133: invokestatic  #84                 // Method ntrinsics.stringPlus:(LString;LObject;)LString;
     136: astore_3
     137: iconst_0
     138: istore        4
     140: getstatic     #63                 // Field System.out:LPrintStream;
     143: aload_3
     144: invokevirtual #69                 // Method PrintStream.println:(LObject;)V
     
     // other property initializers and init blocks
```

这是字节码的高度截断和简化版本！如上所示，字节码的第一部分是初始化firstName和lastName构造函数属性。之后，字节码专门用于fullName属性初始化器和打印静态日志。最后，我们有一组操作码负责第一个init块。

为了简洁起见，字节码的剩余部分被截断了。无论如何，**很明显Kotlin编译主构造函数来保存所有逻辑**。

### 3.2 辅助构造函数

与主构造函数相反，辅助构造函数可以包含初始化逻辑。**对主构造函数的委派作为辅助构造函数的第一条语句发生，无论是显式还是隐式**。因此，Kotlin在辅助构造函数体之前执行所有初始化块和属性初始化器中的代码。

因此，如果我们使用辅助构造函数创建一个实例：

```kotlin
val p = Person("dehghani")
```

我们将在主构造函数的所有其他日志之后看到辅助构造函数日志：

```shell
Initializing full name
You're dehghani
Initializing initials
You're initials are D
I'm secondary
```

同样，字节码可以验证辅助构造函数的调用顺序：

```bash
public com.baeldung.initblock.Person(java.lang.String);
    Code:
      // calling the primary constructor
      10: invokespecial #109                // Method "<init>":(LString;LString;)V

      // secondary log
      13: ldc           #111                // String I\'m secondary
      18: getstatic     #63                 // Field System.out:LPrintStream;
      21: aload_2
      22: invokevirtual #69                 // Method PrintStream.println:(LObject;)V
```

如上所示，它首先调用主构造函数，然后在标准输出上打印预期的日志。

## 4. 总结

在本文中，我们了解了Kotlin中的init块和构造函数之间的区别。此外，为了更好地理解这种差异，我们查看了每种情况下生成的字节码。