## 1. 概述

**Hamcrest提供静态匹配器使单元测试断言更简单、更清晰**，在本文中，我们介绍与数字相关的匹配器。

## 2. 设置

只需将以下Maven依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>java-hamcrest</artifactId>
    <version>2.0.0.0</version>
</dependency>
```

## 3. 接近匹配器

我们介绍的第一组匹配器是检查某个元素是否接近某个值+/-一个错误的匹配器。

更准确地说：

```java
value - error <= element <= value + error
```

如果上面的比较为真，则断言将通过。

### 3.1 isClose与Double值

假设我们有一个double类型的actual值，并且我们要测试actual值是否接近 1 +/- 0.5：

```plaintext
1 - 0.5 <= actual <= 1 + 0.5
    0.5 <= actual <= 1.5
```

现在让我们使用isClose匹配器编写一个单元测试：

```java
@Test
void givenADouble_whenCloseTo_thenCorrect() {
    double actual = 1.3;
    double operand = 1;
    double error = 0.5;
 
    assertThat(actual, closeTo(operand, error));
}
```

由于1.3介于0.5和1.5之间，因此测试将通过；同样，我们可以测试反面情况：

```java
@Test
public void givenADouble_whenNotCloseTo_thenCorrect() {
    double actual = 1.6;
    double operand = 1;
    double error = 0.5;
 
    assertThat(actual, not(closeTo(operand, error)));
}
```

### 3.2 isClose与BigDecimal值

**isClose是重载的，可以与double值一起使用，也可以与BigDecimal对象一起使用**：

```java
@Test
void givenABigDecimal_whenCloseTo_thenCorrect() {
    BigDecimal actual = new BigDecimal("1.0003");
    BigDecimal operand = new BigDecimal("1");
    BigDecimal error = new BigDecimal("0.0005");
    
    assertThat(actual, is(closeTo(operand, error)));
}

@Test
void givenABigDecimal_whenNotCloseTo_thenCorrect() {
    BigDecimal actual = new BigDecimal("1.0006");
    BigDecimal operand = new BigDecimal("1");
    BigDecimal error = new BigDecimal("0.0005");
    
    assertThat(actual, is(not(closeTo(operand, error))));
}
```

请注意，**is匹配器只包装其他匹配器，不添加额外的逻辑**；它只是使整个断言更具可读性。

## 4. 顺序匹配器

顾名思义，这些匹配器有助于对顺序进行断言。

其中包含：

-   comparesEqualTo
-   greaterThan
-   greaterThanOrEqualTo
-   lessThan
-   lessThanOrEqualTo

### 4.1 Integer值与顺序匹配器

最常见的情况是将这些匹配器与整数一起使用：

```java
@Test
void given5_whenComparesEqualTo5_thenCorrect() {
    Integer five = 5;
    
    assertThat(five, comparesEqualTo(five));
}

@Test
void given5_whenNotComparesEqualTo7_thenCorrect() {
    Integer seven = 7;
    Integer five = 5;

    assertThat(five, not(comparesEqualTo(seven)));
}

@Test
void given7_whenGreaterThan5_thenCorrect() {
    Integer seven = 7;
    Integer five = 5;
 
    assertThat(seven, is(greaterThan(five)));
}

@Test
void given7_whenGreaterThanOrEqualTo5_thenCorrect() {
    Integer seven = 7;
    Integer five = 5;
 
    assertThat(seven, is(greaterThanOrEqualTo(five)));
}

@Test
void given5_whenGreaterThanOrEqualTo5_thenCorrect() {
    Integer five = 5;
 
    assertThat(five, is(greaterThanOrEqualTo(five)));
}

@Test
void given3_whenLessThan5_thenCorrect() {
   Integer three = 3;
   Integer five = 5;
 
   assertThat(three, is(lessThan(five)));
}

@Test
void given3_whenLessThanOrEqualTo5_thenCorrect() {
   Integer three = 3;
   Integer five = 5;
 
   assertThat(three, is(lessThanOrEqualTo(five)));
}

@Test
void given5_whenLessThanOrEqualTo5_thenCorrect() {
   Integer five = 5;
 
   assertThat(five, is(lessThanOrEqualTo(five)));
}
```

### 4.2 String值与顺序匹配器

尽管比较数字更有意义，但很多时候比较其他类型的元素也很有用，**这就是为什么顺序匹配器可以应用于任何实现Comparable接口的类的原因**。

下面是一些字符串的例子：

```java
@Test
void givenBenjamin_whenGreaterThanAmanda_thenCorrect() {
    String amanda = "Amanda";
    String benjamin = "Benjamin";
 
    assertThat(benjamin, is(greaterThan(amanda)));
}

@Test
void givenAmanda_whenLessThanBenajmin_thenCorrect() {
    String amanda = "Amanda";
    String benjamin = "Benjamin";
 
    assertThat(amanda, is(lessThan(benjamin)));
}
```

String实现Comparable接口的compareTo方法的方式是根据字母顺序进行排序。

### 4.3 LocalDate值与顺序匹配器

与Strings一样，我们也可以比较日期，下面是一些LocalDate对象的例子：

```java
@Test
void givenToday_whenGreaterThanYesterday_thenCorrect() {
    LocalDate today = LocalDate.now();
    LocalDate yesterday = today.minusDays(1);
 
    assertThat(today, is(greaterThan(yesterday)));
}

@Test
void givenToday_whenLessThanTomorrow_thenCorrect() {
    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(1);
    
    assertThat(today, is(lessThan(tomorrow)));
}
```

可以看到，assertThat(today, is(lessThan(tomorrow)))几乎接近于我们人类可读的方式(即今天是小于明天)。

### 4.4 自定义类与顺序匹配器

首先我们创建一个Person bean：

```java
static class Person {
    String name;
    int age;

    // standard constructor, getters and setters
}
```

并实现Comparable接口：

```java
static class Person implements Comparable<Person> {
        
    // ...

	@Override
	public int compareTo(Person o) {
		if (this.age == o.getAge()) return 0;
		if (this.age > o.age) return 1;
		else return -1;
	}
}
```

上面的compareTo方法实现按年龄比较两个Person对象，下面我们创建几个测试方法：

```java
@Test
void givenAmanda_whenOlderThanBenjamin_thenCorrect() {
    Person amanda = new Person("Amanda", 20);
    Person benjamin = new Person("Benjamin", 18);
 
    assertThat(amanda, is(greaterThan(benjamin)));
}

@Test
void givenBenjamin_whenYoungerThanAmanda_thenCorrect() {
    Person amanda = new Person("Amanda", 20);
    Person benjamin = new Person("Benjamin", 18);
 
    assertThat(benjamin, is(lessThan(amanda)));
}
```

匹配器现在基于我们的compareTo逻辑工作。

## 5. NaN匹配

Hamcrest提供了一个额外的数字匹配器来**定义一个数字是否实际上是一个数字**：

```java
@Test
void givenNaN_whenIsNotANumber_thenCorrect() {
    double zero = 0d;
    
    assertThat(zero / zero, is(notANumber()));
}
```

## 6. 总结

如你所见，**数字匹配器对于简化常见断言非常有用**。更重要的是，一般来说，Hamcrest匹配器的方法是**见名知意且易于阅读的**，加上将匹配器与自定义比较逻辑相结合的能力，使它们成为大多数项目的强大工具。
