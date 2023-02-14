## 1. 概述

Java是一种类型化语言，这意味着它利用了类型的概念。有两个不同的类型组：

1.  原始数据类型
2.  抽象数据类型

在本文中，我们将重点关注原始类型的转换。

## 2. 原始类型概述

我们必须知道的第一件事是原始类型可以使用什么样的值。有八种原始类型，它们是：

-   **byte**：8位并有符号
-   **short**：16位并有符号
-   **char**：16位且无符号，因此它可以表示Unicode字符
-   **int**：32位并有符号
-   **long**：64位并有符号
-   **float**：32位并有符号
-   **double**：64位并有符号
-   **boolean**：它不是数字，可能只有true或false值

这不是关于基本类型的广泛讨论，我们将根据需要在转换过程中更多地讨论它们的细节。

## 3. 扩大原始转换

当我们需要从比目标类型更简单或更小的原始类型进行转换时，我们不必为此使用任何特殊的表示法：

```java
int myInt = 127;
long myLong = myInt;
```

在扩大转换期间，较小的原始值被放置在较大的容器上，这意味着该值左侧的所有额外空间都用零填充。这也可用于从整数组到浮点数：

```java
float myFloat = myLong;
double myDouble = myLong;
```

这是可能的，因为移动到更广泛的原始类型不会丢失任何信息。

## 4. 缩小原始转换

有时我们需要适应一个比变量声明中使用的类型更大的值。这可能会导致信息丢失，因为必须丢弃一些字节。

在这种情况下，我们必须通过使用强制转换明确表示我们知道这种情况并且我们同意这一点：

```java
int myInt = (int) myDouble;
byte myByte = (byte) myInt;
```

## 5. 扩大和缩小原始转换

**当我们想要从byte转换为char时，这种情况会发生在一个非常特殊的情况下**。第一个转换是将byte扩大到int，然后从int缩小到char。

一个例子将阐明这一点：

```java
byte myLargeValueByte = (byte) 130;   //0b10000010 -126
```

130的二进制表示对于-126是一样的，区别在于信号位的解释。现在让我们从byte转换为char：

```java
char myLargeValueChar = (char) myLargeValueByte;
  //0b11111111 10000010 unsigned value
int myLargeValueInt = myLargeValueChar; //0b11111111 10000010 65410
```

char表示是一个Unicode值，但转换为int向我们展示了一个非常大的值，它的低8位与-126完全相同。

如果我们再次将它转换为字节，我们会得到：

```java
byte myOtherByte = (byte) myLargeValueInt; //0b10000010 -126
```

我们使用的原始值。如果整个代码以char开头，则值将不同：

```java
char myLargeValueChar2 = 130; //This is an int not a byte! 
  //0b 00000000 10000010 unsigned value
        
int myLargeValueInt2 = myLargeValueChar2; //0b00000000 10000010  130
        
byte myOtherByte2 = (byte) myLargeValueInt2; //0b10000010 -126
```

虽然字节表示是相同的，即-126，但字符表示为我们提供了两个不同的字符。

## 6. 装箱/拆箱转换

在Java中，我们为每个原始类型都有一个包装类，这是一种为程序员提供有用的处理方法的巧妙方式，而无需将所有内容都作为重量级对象引用的开销。自Java 1.5以来，自动将原始类型转换为对象和返回对象的能力被包括在内，它通过简单的属性实现：

```java
Integer myIntegerReference = myInt;
int myOtherInt = myIntegerReference;
```

## 7. 字符串转换

所有原始类型都可以通过它们的包装类转换为String，包装类覆盖toString()方法：

```java
String myString = myIntegerReference.toString();
```

如果我们需要回到原始类型，我们需要使用相应的包装类定义的parse方法：

```java
byte  myNewByte   = Byte.parseByte(myString);
short myNewShort  = Short.parseShort(myString);
int   myNewInt    = Integer.parseInt(myString);
long  myNewLong   = Long.parseLong(myString);

float  myNewFloat  = Float.parseFloat(myString);
double myNewDouble = Double.parseDouble(myString);

boolean myNewBoolean = Boolean.parseBoolean(myString);
```

这里唯一的例外是Character类，因为String无论如何都是由char组成的，这样，考虑到String可能由单个char组成，我们可以使用String类的charAt()方法：

```java
char myNewChar = myString.charAt(0);
```

## 8. 数字操作

要执行二元运算，两个操作数的大小必须兼容。

有一组简单的规则适用：

1.  如果其中一个操作数是double，则另一个被提升为double
2.  否则，如果其中一个操作数是float，则另一个被提升为float
3.  否则，如果其中一个操作数是long，则另一个被提升为long
4.  否则，两者都被认为是int

让我们看一个例子：

```java
byte op1 = 4;
byte op2 = 5;
byte myResultingByte = (byte) (op1 + op2);
```

两个操作数都被提升为int，结果必须再次向下转换为byte。

## 9. 总结

类型之间的转换是日常编程活动中非常常见的任务。有一组规则管理静态类型语言操作这些转换的方式。了解此规则可能会在试图找出某个代码编译或未编译的原因时节省大量时间。