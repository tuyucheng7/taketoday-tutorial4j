## 1.生成一个Unbounded Long

让我们从生成一个 Long 开始：

```java
@Test
public void givenUsingPlainJava_whenGeneratingRandomLongUnbounded_thenCorrect() {
    long generatedLong = new Random().nextLong();
}
```

## 2. 生成一个范围内的多头

### 2.1. 带有纯Java的随机长

接下来——让我们看看创建一个随机的有界 Long——即给定范围或间隔内的 Long 值：

```java
@Test
public void givenUsingPlainJava_whenGeneratingRandomLongBounded_thenCorrect() {
    long leftLimit = 1L;
    long rightLimit = 10L;
    long generatedLong = leftLimit + (long) (Math.random()  (rightLimit - leftLimit));
}
```

### 2.2. 随机长与 Apache Commons 数学

让我们看一下使用更简洁的 API 和 Commons Math 生成随机 Long：

```java
@Test
public void givenUsingApacheCommons_whenGeneratingRandomLongBounded_thenCorrect() {
    long leftLimit = 10L;
    long rightLimit = 100L;
    long generatedLong = new RandomDataGenerator().nextLong(leftLimit, rightLimit);
}
```

## 3.生成无界整数

让我们继续生成一个没有边界的随机整数：

```java
@Test
public void givenUsingPlainJava_whenGeneratingRandomIntegerUnbounded_thenCorrect() {
    int generatedInteger = new Random().nextInt();
}
```

如你所见，它非常接近于生成一个长的。

## 4.生成一个范围内的整数

### 4.1. 普通Java的随机整数

Next – 给定范围内的随机整数：

```java
@Test
public void givenUsingPlainJava_whenGeneratingRandomIntegerBounded_thenCorrect() {
    int leftLimit = 1;
    int rightLimit = 10;
    int generatedInteger = leftLimit + (int) (new Random().nextFloat()  (rightLimit - leftLimit));
}
```

### 4.2. 具有公共数学的随机整数

Common Math 也一样：

```java
@Test
public void givenUsingApache_whenGeneratingRandomIntegerBounded_thenCorrect() {
    int leftLimit = 1;
    int rightLimit = 10;
    int generatedInteger = new RandomDataGenerator().nextInt(leftLimit, rightLimit);
}
```

## 5. 生成无界浮点数

现在，让我们来看看生成随机浮点数——首先是无界的：

```java
@Test
public void givenUsingPlainJava_whenGeneratingRandomFloatUnbouned_thenCorrect() {
    float generatedFloat = new Random().nextFloat();
}
```

## 6. 生成一个范围内的浮点数

### 6.1. 普通Java的随机浮点数

还有一个有界的随机浮点数：

```java
@Test
public void givenUsingPlainJava_whenGeneratingRandomFloatBouned_thenCorrect() {
    float leftLimit = 1F;
    float rightLimit = 10F;
    float generatedFloat = leftLimit + new Random().nextFloat()  (rightLimit - leftLimit);
}
```

### 6.2. Commons Math 的随机浮点数

现在——使用 Commons Math 的有界随机浮点数：

```java
@Test
public void givenUsingApache_whenGeneratingRandomFloatBounded_thenCorrect() {
    float leftLimit = 1F;
    float rightLimit = 10F;
    float randomFloat = new RandomDataGenerator().getRandomGenerator().nextFloat();
    float generatedFloat = leftLimit + randomFloat  (rightLimit - leftLimit);
}
```

## 7. 生成无界双精度

### 7.1. 带有普通Java的随机无界双精度

最后——我们将生成随机双精度值——首先，使用JavaMath API：

```java
@Test
public void givenUsingPlainJava_whenGeneratingRandomDoubleUnbounded_thenCorrect() {
    double generatedDouble = Math.random();
}
```

### 7.2. 带有公共数学的随机无界双精度数

以及 Apache Commons 数学库的随机双精度值：

```java
@Test
public void givenUsingApache_whenGeneratingRandomDoubleUnbounded_thenCorrect() {
    double generatedDouble = new RandomDataGenerator().getRandomGenerator().nextDouble();
}
```

## 8. 在一定范围内生成双精度数

### 8.1. 带有纯Java的随机有界双精度数

在这个例子中，让我们看一下在一个区间内生成的随机双精度值——使用 Java：

```java
@Test
public void givenUsingPlainJava_whenGeneratingRandomDoubleBounded_thenCorrect() {
    double leftLimit = 1D;
    double rightLimit = 10D;
    double generatedDouble = leftLimit + new Random().nextDouble()  (rightLimit - leftLimit);
}
```

### 8.2. 带有 Commons 数学的随机有界双精度数

最后——使用 Apache Commons 数学库在一个区间内随机取一个双精度值：

```java
@Test
public void givenUsingApache_whenGeneratingRandomDoubleBounded_thenCorrect() {
    double leftLimit = 1D;
    double rightLimit = 100D;
    double generatedDouble = new RandomDataGenerator().nextUniform(leftLimit, rightLimit);
}
```

你已经掌握了如何为Java中最常见的数字基元生成无限值和有界值的快速而切题的示例。

## 9.总结

本教程说明了我们如何使用不同的技术和库生成绑定或非绑定的随机数。