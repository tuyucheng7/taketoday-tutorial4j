## 1. 概述

在本文中，我们将探讨在Java中查找对象类的不同方法。

## 2. 使用getClass()方法

我们要检查的第一个方法是getClass()方法。

首先，让我们看一下我们的代码。我们将编写一个用户类：

```java
public class User {
    
    // implementation details
}
```

现在，让我们创建一个扩展User的Lender类：

```java
public class Lender extends User {
    
    // implementation details
}
```

同样，我们将创建一个扩展User的Borrower类：

```java
public class Borrower extends User {
    
    // implementation details
}
```

getClass()方法只是返回我们正在评估的对象的运行时类，因此，我们不考虑继承。

正如我们所见，getClass()表明我们的lender对象的类是Lender类型而不是User类型：

```java
@Test
public void givenLender_whenGetClass_thenEqualsLenderType() {
    User lender = new Lender();
    assertEquals(Lender.class, lender.getClass());
    assertNotEquals(User.class, lender.getClass());
}
```

## 3. 使用isInstance()方法

当使用isInstance()方法时，我们正在检查一个对象是否属于特定类型，并且根据类型，我们要么在谈论类，要么在谈论接口。

如果作为方法参数发送的对象通过类或接口类型的IS-A测试，则此方法将返回true。

我们可以使用isInstance()方法在运行时检查对象的类。此外，isInstance()还处理[自动装箱](https://www.baeldung.com/java-wrapper-classes#autoboxing-and-unboxing)。

如果我们检查以下代码，我们会发现代码无法编译：

```java
@Ignore
@Test
public void givenBorrower_whenDoubleOrNotString_thenRequestLoan() {
    Borrower borrower = new Borrower();
    double amount = 100.0;
        
    if(amount instanceof Double) { // Compilation error, no autoboxing
        borrower.requestLoan(amount);
    }
        
    if(!(amount instanceof String)) { // Compilation error, incompatible operands
        borrower.requestLoan(amount);
    }
}
```

让我们使用isInstance()方法检查自动装箱操作：

```java
@Test
public void givenBorrower_whenLoanAmountIsDouble_thenRequestLoan() {
    Borrower borrower = new Borrower();
    double amount = 100.0;
        
    if(Double.class.isInstance(amount)) { // No compilation error
        borrower.requestLoan(amount);
    }
    assertEquals(100, borrower.getTotalLoanAmount());
}
```

现在，让我们尝试在运行时评估我们的对象：

```java
@Test
public void givenBorrower_whenLoanAmountIsNotString_thenRequestLoan() {
    Borrower borrower = new Borrower();
    Double amount = 100.0;
        
    if(!String.class.isInstance(amount)) { // No compilation error
        borrower.requestLoan(amount);
    }
    assertEquals(100, borrower.getTotalLoanAmount());
}
```

我们还可以使用isInstance()来验证对象是否可以在转换之前转换为另一个类：

```java
@Test
public void givenUser_whenIsInstanceOfLender_thenDowncast() {
    User user = new Lender();
    Lender lender = null;
        
    if(Lender.class.isInstance(user)) {
        lender = (Lender) user;
    }
        
    assertNotNull(lender);
}
```

当我们使用isInstance()方法时，我们保护我们的程序免于尝试非法向下转换，尽管在这种情况下使用instanceof运算符会更顺利。我们接下来检查一下。

## 4. 使用instanceof操作符

与isInstance()方法类似，如果被评估的对象属于给定类型，则instanceof运算符返回true-换句话说，如果我们在运算符左侧引用的对象通过了类的IS-A测试或接口类型在右侧。

我们可以评估Lender对象是否为Lender类型和User类型：

```java
@Test
public void givenLender_whenInstanceOf_thenReturnTrue() {
    User lender = new Lender();
    assertTrue(lender instanceof Lender);
    assertTrue(lender instanceof User);
}
```

要深入了解instanceof运算符的工作原理，我们可以在我们的[Java instanceOf运算符](https://www.baeldung.com/java-instanceof)文章中找到更多信息。

## 5. 总结

在本文中，我们回顾了在Java中查找对象类的三种不同方法：getClass()方法、isInstance()方法和instanceof运算符。