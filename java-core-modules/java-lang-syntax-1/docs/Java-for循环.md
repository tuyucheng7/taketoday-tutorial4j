## 1. 概述

在本文中，我们将了解Java语言的一个核心方面-使用for循环重复执行一条语句或一组语句。

## 2. 简单的for循环

**for循环是一种控制结构，它允许我们通过递增和评估循环计数器来重复某些操作**。

在第一次迭代之前，循环计数器被初始化，然后执行条件评估，然后是步骤定义(通常是简单的递增)。

for循环的语法是：

```java
for (initialization; Boolean-expression; step) 
    statement;
```

让我们看一个简单的例子：

```java
for (int i = 0; i < 5; i++) {
    System.out.println("Simple for loop: i = " + i);
}
```

**for语句中使用的初始化、布尔表达式和步骤是可选的**。下面是一个无限循环的例子：

```java
for ( ; ; ) {
    // Infinite for loop
}
```

### 2.1 标记for循环

我们也可以标记for循环。如果我们有嵌套的for循环，这很有用，这样我们就可以从特定的for循环中中断/继续：

```java
aa: for (int i = 1; i <= 3; i++) {
    if (i == 1)
      continue;
    bb: for (int j = 1; j <= 3; j++) {
        if (i == 2 && j == 2) {
            break aa;
        }
        System.out.println(i + " " + j);
    }
}
```

## 3. 增强for循环

从Java 5开始，我们有了第二种for循环，称为增强型for，它可以更轻松地遍历数组或集合中的所有元素。

增强的for循环的语法是：

```java
for(Type item : items)
  statement;
```

由于这个循环与标准的for循环相比被简化了，所以我们在初始化循环时只需要声明两件事：

1.  我们当前正在迭代的元素的句柄
2.  我们正在迭代的源数组/集合

因此，我们可以说：**对于items中的每个元素，将元素分配给item变量并运行循环体**。

让我们看一个简单的例子：

```java
int[] intArr = { 0,1,2,3,4 }; 
for (int num : intArr) {
    System.out.println("Enhanced for-each loop: i = " + num);
}
```

我们可以使用它来迭代各种Java数据结构：

给定一个List<String\>列表对象——我们可以迭代它：

```java
for (String item : list) {
    System.out.println(item);
}
```

我们可以类似地迭代Set<String\> set：

```java
for (String item : set) {
    System.out.println(item);
}
```

并且，给定一个Map<String,Integer> map，我们也可以对其进行迭代：

```java
for (Entry<String, Integer> entry : map.entrySet()) {
    System.out.println(
        "Key: " + entry.getKey() + 
        " - " + 
        "Value: " + entry.getValue());
}
```

### 3.1 Iterable.forEach()

从Java 8开始，我们可以以稍微不同的方式利用for-each循环。**我们现在在Iterable接口中有一个专用的forEach()方法，它接收代表我们要执行的操作的lambda表达式**。

在内部，它只是将工作委托给标准循环：

```java
default void forEach(Consumer<? super T> action) {
    Objects.requireNonNull(action);
    for (T t : this) {
        action.accept(t);
    }
}
```

让我们看一下这个例子：

```java
List<String> names = new ArrayList<>();
names.add("Larry");
names.add("Steve");
names.add("James");
names.add("Conan");
names.add("Ellen");

names.forEach(name -> System.out.println(name));
```

## 4. 总结

在本快速教程中，我们探讨了Java的for循环。