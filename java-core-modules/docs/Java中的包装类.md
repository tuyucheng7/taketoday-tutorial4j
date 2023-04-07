## **一、概述**

顾名思义，**包装器类是封装原始 Java 类型的对象。**

每个 Java 原语都有一个相应的包装器：

-   *布尔型、字节型、短型、字符型、整数型、长型、浮点型、双精度型* 
-   *布尔、字节、短整型、字符、整数、长整型、浮点型、* *双精度*

这些都是在*java.lang*包中定义的，因此我们不需要手动导入它们。

## **2.包装类**

“包装类的目的是什么？”。[这是最常见的 Java 面试问题](https://javarevisited.blogspot.com/2015/10/133-java-interview-questions-answers-from-last-5-years.html)之一 。

基本上，**泛型类只适用于对象，不支持原语**。因此，如果我们想使用它们，我们必须将原始值转换为包装器对象。

例如，Java Collection Framework 专门处理对象。很久以前（在 Java 5 之前，将近 15 年前）没有自动装箱，例如，我们不能简单地对整数集合调用*add(5)* *。*

那时，需要将那些***原始\* 值手动转换为 \*相应的\* 包装类**并存储在集合中。

今天，通过自动装箱，我们可以轻松地执行*ArrayList.add(101)* 但在内部 Java 将原始值转换为整数 *，*然后使用*valueOf()*方法 将其存储在 *ArrayList中**。*

## **3. Primitive 到 Wrapper 类的转换**

现在最大的问题是：我们如何将原始值转换为相应的包装类，例如将*int*转换为*Integer*或将*char*转换为*Character？*

好吧，我们可以使用构造函数或静态工厂方法将原始值转换为包装类的对象。

然而，从 Java 9 开始，许多装箱原语（例如*[Integer](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Integer.html#(int))* 或 [*Long）*](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Long.html#(long))的构造函数 已被弃用。

所以**强烈建议只在新代码上使用工厂方法**。

让我们看一个在 Java 中将*int*值转换为*Integer*对象的示例：

```java
Integer object = new Integer(1);

Integer anotherObject = Integer.valueOf(1);复制
```

valueOf *()*方法返回一个表示指定*int*值的实例。

它返回缓存的值，使其高效。它总是缓存 -128 到 127 之间的值，但也可以缓存此范围之外的其他值。

同样，我们也可以将*boolean*转*Boolean，byte*转*Byte，char*转*Character，long*转*Long，float*转*Float，* double*转*Double *。*尽管如果我们必须[将 String 转换为 Integer](https://javarevisited.blogspot.com/2011/08/convert-string-to-integer-to-string.html)，那么我们需要使用*parseInt()*方法，因为*String*不是包装类。

另一方面，**要将包装对象转换为原始值，我们可以使用相应的方法，例如\*intValue()、doubleValue()\*** 等：

```java
int val = object.intValue();
复制
```

[可在此处](https://www.baeldung.com/java-primitive-conversions)找到全面的参考资料。

## **4. 自动装箱和拆箱**

在上一节中，我们展示了如何手动将原始值转换为对象。

在 Java 5 之后，**这种转换可以通过使用称为自动装箱和拆箱的功能自动完成。**

**“装箱”是指将原始值转换为相应的包装对象。**因为这可以自动发生，所以称为自动装箱。

类似地，**当包装器对象被解包为原始值时，这称为拆箱。**

这在实践中意味着我们可以将原始值传递给需要包装对象的方法 ，或者将原始值分配给需要对象的变量：

```java
List<Integer> list = new ArrayList<>();
list.add(1); // autoboxing

Integer val = 2; // autoboxing复制
```

在此示例中，Java 会自动将原始*int*值转换为包装器。

在内部，它使用*valueOf()*方法来促进转换。例如，以下几行是等效的：

```java
Integer value = 3;

Integer value = Integer.valueOf(3);复制
```

虽然这使得转换更容易并且代码更易读，但在某些情况下**我们不应该使用自动装箱，例如在循环中**。

与自动装箱类似，当将对象传递给需要基元的方法或将其分配给基元变量时，拆箱会自动完成：

```java
Integer object = new Integer(1); 
int val1 = getSquareValue(object); //unboxing
int val2 = object; //unboxing

public static int getSquareValue(int i) {
    return i*i;
}复制
```

基本上，**如果我们编写一个接受原始值或包装器对象的方法，我们仍然可以将这两个值传递给它们。**Java 将负责根据上下文传递正确的类型，例如原始类型或包装器。

## **5.结论**

在这个快速教程中，我们讨论了 Java 中的包装类，以及自动装箱和拆箱的机制。