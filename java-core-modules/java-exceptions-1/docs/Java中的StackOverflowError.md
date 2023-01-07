## 1. 概述

StackOverflowError对于Java开发人员来说可能很烦人，因为它是我们可能遇到的最常见的运行时错误之一。

在本文中，我们将通过查看各种代码示例以及如何处理它来了解此错误是如何发生的。

## 2. Stack Frames 以及StackOverflowError是如何发生的

让我们从基础开始。调用方法时，会在[调用堆栈](https://www.baeldung.com/cs/call-stack)上创建一个新的堆栈帧。这个栈帧保存被调用方法的参数、它的局部变量和方法的返回地址，即被调用方法返回后方法应该继续执行的点。

堆栈帧的创建将继续，直到它到达在嵌套方法中找到的方法调用的末尾。

在这个过程中，如果 JVM 遇到没有空间创建新栈帧的情况，就会抛出StackOverflowError。

JVM 遇到这种情况的最常见原因是未终止/无限递归——StackOverflowError 的 Javadoc 描述提到错误是由于特定代码片段中递归太深而引发的。

但是，递归并不是导致此错误的唯一原因。它也可能发生在应用程序不断从方法内部调用方法直到堆栈耗尽的情况下。这是一种罕见的情况，因为没有开发人员会故意遵循错误的编码做法。另一个罕见的原因是在一个方法中有大量的局部变量。

当应用程序设计为在类之间具有循环关系时，也可能抛出StackOverflowError 。在这种情况下，彼此的构造函数被重复调用，导致抛出此错误。这也可以被认为是递归的一种形式。

另一个导致此错误的有趣场景是，如果某个类在与该类的实例变量相同的类中被实例化。这将导致同一类的构造函数被一次又一次(递归地)调用，最终导致 StackOverflowError 。

在下一节中，我们将查看一些演示这些场景的代码示例。

## 3. StackOverflowError实战

在下面显示的示例中，由于意外的递归，将抛出StackOverflowError ，开发人员忘记为递归行为指定终止条件：

```java
public class UnintendedInfiniteRecursion {
    public int calculateFactorial(int number) {
        return number  calculateFactorial(number - 1);
    }
}
```

在这里，对于传递给方法的任何值，都会在所有情况下抛出错误：

```java
public class UnintendedInfiniteRecursionManualTest {
    @Test(expected = StackOverflowError.class)
    public void givenPositiveIntNoOne_whenCalFact_thenThrowsException() {
        int numToCalcFactorial= 1;
        UnintendedInfiniteRecursion uir 
          = new UnintendedInfiniteRecursion();
        
        uir.calculateFactorial(numToCalcFactorial);
    }
    
    @Test(expected = StackOverflowError.class)
    public void givenPositiveIntGtOne_whenCalcFact_thenThrowsException() {
        int numToCalcFactorial= 2;
        UnintendedInfiniteRecursion uir 
          = new UnintendedInfiniteRecursion();
        
        uir.calculateFactorial(numToCalcFactorial);
    }
    
    @Test(expected = StackOverflowError.class)
    public void givenNegativeInt_whenCalcFact_thenThrowsException() {
        int numToCalcFactorial= -1;
        UnintendedInfiniteRecursion uir 
          = new UnintendedInfiniteRecursion();
        
        uir.calculateFactorial(numToCalcFactorial);
    }
}
```

但是，在下一个示例中指定了终止条件，但如果将值-1传递给calculateFactorial()方法，则永远不会满足终止条件，这会导致未终止/无限递归：

```java
public class InfiniteRecursionWithTerminationCondition {
    public int calculateFactorial(int number) {
       return number == 1 ? 1 : number  calculateFactorial(number - 1);
    }
}
```

这组测试演示了这种情况：

```java
public class InfiniteRecursionWithTerminationConditionManualTest {
    @Test
    public void givenPositiveIntNoOne_whenCalcFact_thenCorrectlyCalc() {
        int numToCalcFactorial = 1;
        InfiniteRecursionWithTerminationCondition irtc 
          = new InfiniteRecursionWithTerminationCondition();

        assertEquals(1, irtc.calculateFactorial(numToCalcFactorial));
    }

    @Test
    public void givenPositiveIntGtOne_whenCalcFact_thenCorrectlyCalc() {
        int numToCalcFactorial = 5;
        InfiniteRecursionWithTerminationCondition irtc 
          = new InfiniteRecursionWithTerminationCondition();

        assertEquals(120, irtc.calculateFactorial(numToCalcFactorial));
    }

    @Test(expected = StackOverflowError.class)
    public void givenNegativeInt_whenCalcFact_thenThrowsException() {
        int numToCalcFactorial = -1;
        InfiniteRecursionWithTerminationCondition irtc 
          = new InfiniteRecursionWithTerminationCondition();

        irtc.calculateFactorial(numToCalcFactorial);
    }
}
```

在这种特殊情况下，如果将终止条件简单地表示为：

```java
public class RecursionWithCorrectTerminationCondition {
    public int calculateFactorial(int number) {
        return number <= 1 ? 1 : number  calculateFactorial(number - 1);
    }
}
```

这是在实践中显示这种情况的测试：

```java
public class RecursionWithCorrectTerminationConditionManualTest {
    @Test
    public void givenNegativeInt_whenCalcFact_thenCorrectlyCalc() {
        int numToCalcFactorial = -1;
        RecursionWithCorrectTerminationCondition rctc 
          = new RecursionWithCorrectTerminationCondition();

        assertEquals(1, rctc.calculateFactorial(numToCalcFactorial));
    }
}
```

现在让我们看一下由于类之间的循环关系而发生StackOverflowError的场景。让我们考虑ClassOne和ClassTwo，它们在它们的构造函数中相互实例化，从而导致循环关系：

```java
public class ClassOne {
    private int oneValue;
    private ClassTwo clsTwoInstance = null;
    
    public ClassOne() {
        oneValue = 0;
        clsTwoInstance = new ClassTwo();
    }
    
    public ClassOne(int oneValue, ClassTwo clsTwoInstance) {
        this.oneValue = oneValue;
        this.clsTwoInstance = clsTwoInstance;
    }
}
public class ClassTwo {
    private int twoValue;
    private ClassOne clsOneInstance = null;
    
    public ClassTwo() {
        twoValue = 10;
        clsOneInstance = new ClassOne();
    }
    
    public ClassTwo(int twoValue, ClassOne clsOneInstance) {
        this.twoValue = twoValue;
        this.clsOneInstance = clsOneInstance;
    }
}
```

现在假设我们尝试实例化ClassOne，如本测试所示：

```java
public class CyclicDependancyManualTest {
    @Test(expected = StackOverflowError.class)
    public void whenInstanciatingClassOne_thenThrowsException() {
        ClassOne obj = new ClassOne();
    }
}
```

这以StackOverflowError结束，因为ClassOne的构造函数正在实例化ClassTwo，而ClassTwo的构造函数再次实例化ClassOne。并且这种情况反复发生，直到它溢出堆栈。

接下来，我们将看看当一个类在与该类的实例变量相同的类中被实例化时会发生什么。

如下例所示，AccountHolder将自身实例化为实例变量jointAccountHolder：

```java
public class AccountHolder {
    private String firstName;
    private String lastName;
    
    AccountHolder jointAccountHolder = new AccountHolder();
}
```

实例化AccountHolder类时，由于构造函数的递归调用而引发 StackOverflowError，如本测试所示：

```java
public class AccountHolderManualTest {
    @Test(expected = StackOverflowError.class)
    public void whenInstanciatingAccountHolder_thenThrowsException() {
        AccountHolder holder = new AccountHolder();
    }
}
```

## 4. 处理StackOverflowError

遇到StackOverflowError时最好的做法是仔细检查堆栈跟踪以识别行号的重复模式。这将使我们能够找到递归有问题的代码。

让我们检查一些由我们之前看到的代码示例引起的堆栈跟踪。

如果我们省略预期的异常声明，则此堆栈跟踪由InfiniteRecursionWithTerminationConditionManualTest生成：

```java
java.lang.StackOverflowError

 at c.b.s.InfiniteRecursionWithTerminationCondition
  .calculateFactorial(InfiniteRecursionWithTerminationCondition.java:5)
 at c.b.s.InfiniteRecursionWithTerminationCondition
  .calculateFactorial(InfiniteRecursionWithTerminationCondition.java:5)
 at c.b.s.InfiniteRecursionWithTerminationCondition
  .calculateFactorial(InfiniteRecursionWithTerminationCondition.java:5)
 at c.b.s.InfiniteRecursionWithTerminationCondition
  .calculateFactorial(InfiniteRecursionWithTerminationCondition.java:5)
```

在这里，可以看到第 5 行在重复。这是进行递归调用的地方。现在只需检查代码以查看递归是否以正确的方式完成。

这是我们通过执行CyclicDependancyManualTest获得的堆栈跟踪(同样，没有预期的异常)：

```java
java.lang.StackOverflowError
  at c.b.s.ClassTwo.<init>(ClassTwo.java:9)
  at c.b.s.ClassOne.<init>(ClassOne.java:9)
  at c.b.s.ClassTwo.<init>(ClassTwo.java:9)
  at c.b.s.ClassOne.<init>(ClassOne.java:9)
```

此堆栈跟踪显示在循环关系中的两个类中导致问题的行号。ClassTwo的第 9 行和ClassOne的第 9 行指向构造函数中尝试实例化另一个类的位置。

彻底检查代码后，如果以下任何一项(或任何其他代码逻辑错误)都不是错误的原因：

-   递归实现不正确(即没有终止条件)
-   类之间的循环依赖
-   在同一个类中实例化一个类作为该类的实例变量

尝试增加堆栈大小是个好主意。根据安装的 JVM，默认堆栈大小可能会有所不同。

-Xss标志可用于增加堆栈的大小，无论是从项目的配置还是命令行。

## 5.总结

在本文中，我们仔细研究了StackOverflowError，包括Java代码如何导致它以及我们如何诊断和修复它。