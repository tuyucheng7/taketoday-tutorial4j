## 1. 概述

在这篇快速文章中，我们将介绍continue和breakJava关键字，并重点介绍如何在实践中使用它们。

简单地说，这些语句的执行会导致当前控制流的分支，并终止当前迭代中代码的执行。

## 2. break语句

break语句有两种形式：未标记和标记。

[![图1](https://www.baeldung.com/wp-content/uploads/2017/11/Illustration-1-1024x1024.jpg)](https://www.baeldung.com/wp-content/uploads/2017/11/Illustration-1.jpg)

### 2.1 未标记的中断

我们可以使用无标签语句来终止for、while或do-while循环以及switch-case块：

```java
for (int i = 0; i < 5; i++) {
    if (i == 3) {
        break;
    }
}
```

此代码段定义了一个应该迭代五次的for循环。但是当计数器等于3时，if条件变为真，break语句终止循环。这导致控制流转移到for循环结束后的语句。

在嵌套循环的情况下，未标记的break语句仅终止它所在的内部循环。外循环继续执行：

```java
for (int rowNum = 0; rowNum < 3; rowNum++) {
    for (int colNum = 0; colNum < 4; colNum++) {
        if (colNum == 3) {
            break;
        }
    }
}
```

此代码段嵌套了for循环。当colNum等于3时，if条件的计算结果为真，break语句导致内部for循环终止。但是，外部for循环继续迭代。

### 2.2 标记中断

我们还可以使用带标签的break语句来终止for、while或do-while循环。带标签的break终止外层循环。

终止后，控制流立即转移到外循环结束后的语句：

```java
compare: 
for (int rowNum = 0; rowNum < 3; rowNum++) {
    for (int colNum = 0; colNum < 4; colNum++) {
        if (rowNum == 1 && colNum == 3) {
            break compare;
        }
    }
}
```

在这个例子中，我们在外循环之前引入了一个标签。当rowNum等于1且colNum等于3时，if条件的计算结果为true，break语句终止外层循环。

然后将控制流转移到外部for循环结束后的语句。

## 3. continue声明

continue语句也有两种形式：未标记和标记。

[![图2](https://www.baeldung.com/wp-content/uploads/2017/11/Illustration-2-1024x1024.jpg)](https://www.baeldung.com/wp-content/uploads/2017/11/Illustration-2.jpg)

### 3.1 未标记继续

我们可以使用未标记的语句来绕过for、while或do-while循环的当前迭代中其余语句的执行。它跳到内部循环的末尾并继续循环：

```java
int counter = 0;
for (int rowNum = 0; rowNum < 3; rowNum++) {
    for (int colNum = 0; colNum < 4; colNum++) {
        if (colNum != 3) {
            continue;
        }
        counter++;
    }
}
```

在此代码段中，只要colNum不等于3，未标记的continue语句就会跳过当前迭代，从而绕过该迭代中变量计数器的增量。但是，外部for循环继续迭代。因此，只有在外部for循环的每次迭代中colNum等于3时，counter才会增加。

### 3.2 标记为继续

我们还可以使用带标签的continue语句来跳过外层循环。跳过后，控制流转移到外循环的末尾，有效地继续外循环的迭代：

```java
int counter = 0;
compare: 
for (int rowNum = 0; rowNum < 3; rowNum++) {
    for (int colNum = 0; colNum < 4; colNum++) {
        if (colNum == 3) {
            counter++;
            continue compare;
        }
    }
}
```

我们在外循环之前引入了一个标签。每当colNum等于3时，变量counter就会递增。带标签的continue语句导致外部for循环的迭代跳过。

控制流被转移到外部for循环的末尾，继续进行下一次迭代。

## 4. 总结

在本教程中，我们看到了在Java中使用关键字break和continue作为分支语句的不同方式。