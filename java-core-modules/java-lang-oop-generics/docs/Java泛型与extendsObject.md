## 一、概述

在本快速教程中，我们将了解***<?>\*****和*****<?\*****之间的相同点和不同点。在[Java Generics中](https://www.baeldung.com/java-generics)\*扩展 Object>\***。

然而，这是一个高级话题，在我们深入问题的症结之前，必须对该主题[有一个基本的了解。](https://www.baeldung.com/java-generics)

## 2. 泛型的背景

JDK 5 中引入了泛型，以消除运行时错误并加强类型安全。这种额外的类型安全消除了某些用例中的强制转换，并使程序员能够编写通用算法，这两者都可以产生更具可读性的代码。

例如，在 JDK 5 之前，我们必须使用转换来处理列表的元素。反过来，这会产生特定类别的运行时错误：

```java
List aList = new ArrayList();
aList.add(new Integer(1));
aList.add("a_string");
        
for (int i = 0; i < aList.size(); i++) {
    Integer x = (Integer) aList.get(i);
}复制
```

现在，这段代码有两个我们要解决的问题：

-   *我们需要一个显式转换来从aList*中提取值——类型取决于左侧的变量类型——在本例中为*整数*
-   *当我们尝试将a_string*转换为*Integer*时，我们会在第二次迭代时遇到运行时错误

泛型为我们填补了这个角色：

```java
List<Integer> iList = new ArrayList<>();
iList.add(1);
iList.add("a_string"); // compile time error

for (int i = 0; i < iList.size(); i++) {
    int x = iList.get(i);
}
复制
```

编译器会告诉我们不可能将*a_string*添加到*Integer*类型的*列表*中，这比在运行时发现要好。

此外，不需要显式转换，因为编译器已经知道*iList*包含*Integer* s。此外，由于拆箱的魔力，我们甚至不需要*Integer*类型，它的原始形式就足够了。

## 3.泛型中的通配符

问号或通配符在泛型中用于表示未知类型。它可以有三种形式：

-   *Unbounded Wildcards* : *List<?>*表示未知类型的列表
-   *上限通配符*：*List<? extends Number>表示**Number*或其子类型的列表，例如*Integer*和*Double*
-   *下界通配符*：*List<? super Integer>表示**Integer*或其超类型*Number*和*Object*的列表

现在，由于*Object*是 Java 中所有类型的固有超类型，我们很容易认为它也可以表示未知类型。换句话说，*List<?>*和*List<Object>*可以达到相同的目的。但他们没有。

让我们考虑这两种方法：

```java
public static void printListObject(List<Object> list) {    
    for (Object element : list) {        
        System.out.print(element + " ");    
    }        
}    

public static void printListWildCard(List<?> list) {    
    for (Object element: list) {        
        System.out.print(element + " ");    
    }     
}
复制
```

给定一个*Integer*列表，说：

```java
List<Integer> li = Arrays.asList(1, 2, 3);复制
```

*printListObject(li)*不会编译，我们会得到这个错误：

```java
The method printListObject(List<Object>) is not applicable for the arguments (List<Integer>)复制
```

而*printListWildCard(li)将编译并将**1 2 3*输出到控制台。

## 4. *<?>*和*<? extends Object>* ——相似之处

在上面的示例中，如果我们将*printListWildCard*的方法签名更改为：

```java
public static void printListWildCard(List<? extends Object> list)复制
```

*它的功能与printListWildCard(List<?> list)*相同。这是因为*Object*是所有 Java 对象的超类型，基本上一切都扩展了*Object*。因此，也会处理*Integer*的*列表。*

简而言之，**这意味着 \*？\*和\*？extends Object\*在此示例中是同义词**。

虽然在大多数情况下都是如此，**但也存在一些差异**。让我们在下一节中看看它们。

## 5. *<?>*和*<? extends Object>* ——区别

[可具体化的](https://www.baeldung.com/java-super-type-tokens#1reification)类型是那些在编译时不被擦除的类型。换句话说，非具体化类型的运行时表示将比其编译时对应的信息更少，因为其中一些会被擦除。

作为一般规则，参数化类型是不可具体化的。这意味着*List<String>*和*Map<Integer, String>*不可具体化。编译器擦除它们的类型并将它们分别视为*List*和*Map*。

此规则的唯一例外是无限通配符类型。**这意味着\*List<?>\*和\*Map<?,?>\*是可具体化的**。

另一方面，***List<? extends Object>\*不可具体化**。虽然微妙，但这是一个显着的区别。

[不可具体化的类型不能在某些情况](https://docs.oracle.com/javase/tutorial/java/generics/nonReifiableVarargsType.html)下使用，例如在[*instanceof*](https://www.baeldung.com/java-instanceof)运算符中或作为数组的元素。

所以，如果我们写：

```java
List someList = new ArrayList<>();
boolean instanceTest = someList instanceof List<?>复制
```

此代码编译并且*instanceTest*为*true*。

但是，如果我们在*List* *<? 扩展对象>*：

```java
List anotherList = new ArrayList<>();
boolean instanceTest = anotherList instanceof List<? extends Object>;复制
```

那么第 2 行不编译。

同样，在下面的代码片段中，第 1 行编译，但第 2 行不编译：

```java
List<?>[] arrayOfList = new List<?>[1];
List<? extends Object>[] arrayOfAnotherList = new List<? extends Object>[1]复制
```

## 六，结论

*在这个简短的教程中，我们看到了<?>*和*<?*的相同点和不同点。*扩展对象>*。

虽然大部分相似，但两者在具体化与否方面存在细微差别。