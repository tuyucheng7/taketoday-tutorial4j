## 1. 概述

在本快速教程中，我们将了解模运算符是什么，以及如何在一些常见用例中将其与Java结合使用。

## 2. 模运算符

先从Java简单除法的缺点说起。

如果除法运算符两边的操作数都是int类型，则运算结果是另一个int：

```java
@Test
public void whenIntegerDivision_thenLosesRemainder() {
    assertThat(11 / 4).isEqualTo(2);
}
```

当至少一个操作数的类型为float或double时，相同的除法会给出不同的结果：

```java
@Test
public void whenDoubleDivision_thenKeepsRemainder() {
    assertThat(11 / 4.0).isEqualTo(2.75);
}
```

我们可以观察到在除以整数时我们丢失了除法运算的余数。

模运算符正好给了我们这个余数：

```java
@Test
public void whenModulo_thenReturnsRemainder() {
    assertThat(11 % 4).isEqualTo(3);
}
```

余数是11(被除数)除以4(除数)后的余数，在本例中为3。

出于同样的原因，除以零是不可能的，当右侧参数为零时，也不可能使用模运算符。

当我们尝试使用零作为右侧操作数时，除法和模运算都会抛出ArithmeticException：

```java
@Test(expected = ArithmeticException.class)
public void whenDivisionByZero_thenArithmeticException() {
    double result = 1 / 0;
}

@Test(expected = ArithmeticException.class)
public void whenModuloByZero_thenArithmeticException() {
    double result = 1 % 0;
}
```

## 3. 常见用例

模运算符最常见的用例是确定给定数字是奇数还是偶数。

如果任何数与二之间的模运算结果等于一，则它是一个奇数：

```java
@Test
public void whenDivisorIsOddAndModulusIs2_thenResultIs1() {
    assertThat(3 % 2).isEqualTo(1);
}
```

相反，如果结果为零(即没有余数)，则为偶数：

```java
@Test
public void whenDivisorIsEvenAndModulusIs2_thenResultIs0() {
    assertThat(4 % 2).isEqualTo(0);
}
```

模运算的另一个好处是跟踪圆形阵列中下一个空闲点的索引。

在int值循环队列的简单实现中，元素保存在固定大小的数组中。

任何时候我们想要将一个元素推送到我们的循环队列中，我们只需通过计算我们已经插入的项目数的模数加上1和队列容量来计算下一个空闲位置：

```java
@Test
public void whenItemsIsAddedToCircularQueue_thenNoArrayIndexOutOfBounds() {
    int QUEUE_CAPACITY= 10;
    int[] circularQueue = new int[QUEUE_CAPACITY];
    int itemsInserted = 0;
    for (int value = 0; value < 1000; value++) {
        int writeIndex = ++itemsInserted % QUEUE_CAPACITY;
        circularQueue[writeIndex] = value;
    }
}
```

使用模运算符，我们可以防止writeIndex超出数组的边界；因此，我们永远不会得到ArrayIndexOutOfBoundsException。

但是，一旦我们插入超过QUEUE_CAPACITY的项目，下一个项目将覆盖第一个。

## 4. 总结

模运算符用于计算否则会丢失的整数除法的余数。

它对于执行简单的事情很有用，例如确定给定数字是偶数还是奇数，以及更复杂的任务，例如跟踪圆形数组中的下一个书写位置。