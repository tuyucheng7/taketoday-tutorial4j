## 1. 概述

Java中的数组是由一组类似类型的变量组成的，这些变量由一个公共名称引用。Java中的数组与C/C++中的数组工作方式不同。下面是关于Java数组的一些要点。

+ 在Java中，所有数组都是动态分配的。(下文讨论)
+ 因为数组在Java中是对象，所以我们可以使用对象属性length来找到它们的长度。这与C/C++不同，在C/C++中，我们使用sizeof获取长度。
+ Java数组变量也可以像其他变量一样声明，在数据类型之后加上[]。
+ 数组中的变量是有序的，每个变量都有一个从0开始的索引。
+ Java数组也可以用作静态字段、局部变量或方法参数。
+ 数组的大小必须由int或short值指定，而不是long。
+ 数组类型的直接父类是Object。
+ 每种数组类型都实现了Cloneable和java.io.Serializable接口。

根据数组的定义，数组可以包含原始数据类型(int、char等)和对象引用。在原始数据类型的情况下，实际值存储在连续的内存位置。在对象的情况下，实际对象存储在堆中。

<img src="../assets/Array_In_Java-1.png">

## 2. 创建、初始化和访问数组

一维数组：

一维数组声明的一般形式是：

```
type[] var-name;
或者
type var-name[];
```

数组声明由两个组成部分：类型和名称。type声明数组的元素类型。元素类型确定组成数组的每个元素的数据类型。
与整数数组一样，我们也可以创建其他原始数据类型的数组，如char、float、double等，或用户定义的数据类型(类)。
因此，数组的元素类型决定了数组将保存的数据类型。

示例：

```
// both are valid declarations
int intArray[]; 
or int[] intArray; 

byte byteArray[];
short shortsArray[];
boolean booleanArray[];
long longArray[];
float floatArray[];
double doubleArray[];
char charArray[];

// an array of references to objects of the class MyClass (a class created by user)
MyClass myClassArray[]; 

Object[]  ao,        // array of Object
Collection[] ca;  // array of Collection of unknown type
```

