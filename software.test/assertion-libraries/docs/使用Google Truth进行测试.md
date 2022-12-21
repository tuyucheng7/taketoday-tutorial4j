## 1. 概述

[Truth](https://google.github.io/truth/)是一个流畅而灵活的开源测试框架，旨在使测试断言和失败消息更具可读性。

在本文中，我们介绍Truth框架的关键特性并通过案例来演示它的功能。

## 2. Maven依赖

首先，我们需要将truth和truth-java8-extension添加到我们的pom.xml 中：

```xml
<dependency>
    <groupId>com.google.truth</groupId>
    <artifactId>truth</artifactId>
    <version>0.32</version>
</dependency>
<dependency>
    <groupId>com.google.truth.extensions</groupId>
    <artifactId>truth-java8-extension</artifactId>
    <version>0.32</version>
    <scope>test</scope>
</dependency>
```

## 3. 简介

Truth允许我们为各种类编写可读的断言和失败消息：

-   **标准Java**：原始类型、数组、字符串、对象、集合、Throwable对象、Class等
-   **Java 8**：Optional和Stream实例
-   **Guava**：Optional、Multimap、Multiset和Table对象
-   **自定义类型**：通过扩展Subject类，我们稍后会介绍

通过Truth和Truth8类，该库提供了用于编写适用于主题(即被测值或对象)的断言的工具方法。

**一旦知道了主题，Truth就可以在编译时推断该主题已知的命题**，这允许它返回围绕我们的值的包装器，这些包装器声明特定于该特定主题的命题方法。

例如，在对集合进行断言时，Truth返回一个IterableSubject实例，该实例定义了contains()和containsAnyOf()等方法。当在Map上断言时，它返回一个MapSubject，它声明了containsEntry()和containsKey()等方法。

## 4. 入门

要开始编写断言，我们首先可以静态导入Truth的以下类：

```java
import static com.google.common.truth.Truth.;
import static com.google.common.truth.Truth8.;
```

现在，我们编写一个简单的类，将在下面的几个示例中使用它：

```java
public class User {
    private String name = "John Doe";
    private List<String> emails = Arrays.asList("contact@baeldung.com", "staff@baeldung.com");

    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        User other = (User) obj;
        return Objects.equals(this.name, other.name);
    }

    // standard constructors, getters and setters
}
```

请注意自定义的equals()方法，其中我们声明两个User对象如果它们的名称相等，则它们是相等的。

## 5. 标准Java断言

### 5.1 对象断言

Truth提供了Subject包装器，用于对对象执行断言。Subject也是库中所有其他包装器的父类，并声明了用于确定Object(在我们的例子中为User)是否等于另一个对象的方法：

```java
@Test
void whenComparingUsers_thenEqual() {
    User aUser = new User("John Doe");
    User anotherUser = new User("John Doe");

    assertThat(aUser).isEqualTo(anotherUser);
}
```

或者是否它等于集合中的给定对象：

```java
@Test
void whenComparingUser_thenInList() {
    User aUser = new User();

    assertThat(aUser).isIn(Arrays.asList(1, 3, aUser, null));
}
```

或者是否不是集合中的给定对象：

```java
@Test
void whenComparingUser_thenNotInList() {
    // ...
    assertThat(aUser).isNotIn(Arrays.asList(1, 3, "Three"));
}
```

是否为空：

```java
@Test
void whenComparingUser_thenIsNull() {
    User aUser = null;

    assertThat(aUser).isNull();
}

@Test
void whenComparingUser_thenNotNull() {
    User aUser = new User();

    assertThat(aUser).isNotNull();
}
```

或者是否是特定类的实例：

```java
@Test
void whenComparingUser_thenInstanceOf() {
    // ...

    assertThat(aUser).isInstanceOf(User.class);
}
```

Subject类中还有其他断言方法，要了解全部内容，请参阅[Subject文档](https://google.github.io/truth/api/latest/com/google/common/truth/Subject.html)。

在接下来的部分中，**我们重点关注Truth支持的每种特定类型最相关的方法**。但是，请记住，Subject类中的所有方法也可以应用。

### 5.2 整数、浮点数和双精度值断言

我们可以比较Integer、Float和Double实例是否相等：

```java
@Test
void whenComparingInteger_thenEqual() {
    int anInt = 10;

    assertThat(anInt).isEqualTo(10);
}
```

或者给定的数是否大于另一个数：

```java
@Test
void whenComparingFloat_thenIsBigger() {
    float aFloat = 10.0f;

    assertThat(aFloat).isGreaterThan(1.0f);
}
```

或者是否小于：

```java
@Test
void whenComparingDouble_thenIsSmaller() {
    double aDouble = 10.0f;

    assertThat(aDouble).isLessThan(20.0);
}
```

此外，还可以检查Float和Double实例以查看它们是否在预期的精度范围内：

```java
@Test
void whenComparingDouble_thenWithinPrecision() {
    double aDouble = 22.18;

    assertThat(aDouble).isWithin(2).of(23d);
}

@Test
void whenComparingFloat_thenNotWithinPrecision() {
    float aFloat = 23.04f;

    assertThat(aFloat).isNotWithin(1.3f).of(100f);
}
```

### 5.3 BigDecimal断言

除了常见的断言之外，这种类型可以忽略其规模进行比较：

```java
@Test
void whenComparingBigDecimal_thenEqualIgnoringScale() {
    BigDecimal aBigDecimal = BigDecimal.valueOf(1000, 3);

    assertThat(aBigDecimal).isEqualToIgnoringScale(new BigDecimal(1.0));
}
```

### 5.4 布尔断言

只提供了两个相关的方法，isTrue()和isFalse()：

```java
@Test
void whenCheckingBoolean_thenTrue() {
    boolean aBoolean = true;

    assertThat(aBoolean).isTrue();
}
```

### 5.5 字符串断言

我们可以测试一个字符串是否以特定文本开头：

```java
@Test
void whenCheckingString_thenStartsWith() {
    String aString = "This is a string";

    assertThat(aString).startsWith("This");
}
```

此外，我们可以检查字符串是否包含给定的字符串，是否以预期值结尾或是否为空。源代码中提供了这些方法和其他方法的测试用例。

### 5.6 数组断言

我们可以检查数组以确定它们是否等于其他数组：

```java
@Test
void whenComparingArrays_thenEqual() {
    String[] firstArrayOfStrings = { "one", "two", "three" };
    String[] secondArrayOfStrings = { "one", "two", "three" };

    assertThat(firstArrayOfStrings).isEqualTo(secondArrayOfStrings);
}
```

或者是否不包含任何元素：

```java
@Test
void whenCheckingArray_thenEmpty() {
    Object[] anArray = {};

    assertThat(anArray).isEmpty();
}
```

### 5.7 Comparable断言

除了测试Comparable是否大于或小于另一个实例之外，我们还可以检查它们是否至少是给定值：

```java
@Test
void whenCheckingComparable_thenAtLeast() {
    Comparable<Integer> aComparable = 5;

    assertThat(aComparable).isAtLeast(1);
}
```

此外，我们可以测试它们是否在特定范围内：

```java
@Test
void whenCheckingComparable_thenInRange() {
    // ...

    assertThat(aComparable).isIn(Range.closed(1, 10));
}
```

或是否在特定集合中：

```java
@Test
void whenCheckingComparable_thenInList() {
    // ...

    assertThat(aComparable).isIn(Arrays.asList(4, 5, 6));
}
```

我们还可以根据类的compareTo()方法测试两个Comparable实例是否等价。

首先，我们修改User类，并实现Comparable接口：

```java
public class User implements Comparable<User> {
    // ...
    
    public int compareTo(User o) {
        return this.getName().compareToIgnoreCase(o.getName());
    }
}
```

现在，我们可以断言两个同名用户是等价的：

```java
@Test
void whenComparingUsers_thenEquivalent() {
    User aUser = new User();
    aUser.setName("John Doe");

    User anotherUser = new User();
    anotherUser.setName("john doe");

    assertThat(aUser).isEquivalentAccordingToCompareTo(anotherUser);
}
```

### 5.8 Iterable断言

除了断言Iterable实例的大小，是否它是空的或者是没有重复的，对Iterable的最典型的断言是它包含一些元素：

```java
@Test
void whenCheckingIterable_thenContains() {
    List<Integer> aList = Arrays.asList(4, 5, 6);

    assertThat(aList).contains(5);
}
```

或者包含另一个Iterable的任何元素：

```java
@Test
void whenCheckingIterable_thenContainsAnyInList() {
    List<Integer> aList = Arrays.asList(1, 2, 3);

    assertThat(aList).containsAnyIn(Arrays.asList(1, 5, 10));
}
```

并且具有相同的元素，以相同的顺序：

```java
@Test
void whenCheckingIterable_thenContainsExactElements() {
    List<String> aList = Arrays.asList("10", "20", "30");
    List<String> anotherList = Arrays.asList("10", "20", "30");

    assertThat(aList)
        .containsExactlyElementsIn(anotherList)
        .inOrder();
}
```

或者在使用自定义比较器时是排序的：

```java
@Test
void givenComparator_whenCheckingIterable_thenOrdered() {
    Comparator<String> aComparator 
        = (a, b) -> new Float(a).compareTo(new Float(b));

    List<String> aList = Arrays.asList("1", "012", "0020", "100");

    assertThat(aList).isOrdered(aComparator);
}
```

### 5.9 Map断言

除了断言Map实例是否为空，或具有特定大小；我们还可以检查它是否有特定的Entry：

```java
@Test
void whenCheckingMap_thenContainsEntry() {
    Map<String, Object> aMap = new HashMap<>();
    aMap.put("one", 1L);

    assertThat(aMap).containsEntry("one", 1L);
}
```

是否包含一个特定的key：

```java
@Test
void whenCheckingMap_thenContainsKey() {
    // ...

    assertThat(map).containsKey("one");
}
```

或者是否它与另一个Map具有相同的Entry：

```java
@Test
void whenCheckingMap_thenContainsEntries() {
    Map<String, Object> aMap = new HashMap<>();
    aMap.put("first", 1L);
    aMap.put("second", 2.0);
    aMap.put("third", 3f);

    Map<String, Object> anotherMap = new HashMap<>(aMap);

    assertThat(aMap).containsExactlyEntriesIn(anotherMap);
}
```

### 5.10 异常断言

Exception对象只提供了两种重要的方法，我们可以编写针对异常原因的断言：

```java
@Test
public void whenCheckingException_thenInstanceOf() {
    Exception anException 
        = new IllegalArgumentException(new NumberFormatException());

    assertThat(anException)
        .hasCauseThat()
        .isInstanceOf(NumberFormatException.class);
}
```

或根据异常消息断言：

```java
@Test
void whenCheckingException_thenCauseMessageIsKnown() {
    Exception anException 
        = new IllegalArgumentException("Bad value");

    assertThat(anException)
       .hasMessageThat()
       .startsWith("Bad");
}
```

### 5.11 Classes断言

Class断言只有一个重要的方法，我们可以用它来测试一个类是否可以分配给另一个类：

```java
@Test
void whenCheckingClass_thenIsAssignable() {
    Class<Double> aClass = Double.class;

    assertThat(aClass).isAssignableTo(Number.class);
}
```

## 6. Java 8断言

Optional和Stream是Truth支持的仅有的两种Java 8类型。

### 6.1 Optional断言

有三种重要的方法来验证Optional，我们可以测试它是否具有特定值：

```java
@Test
void whenCheckingJavaOptional_thenHasValue() {
    Optional<Integer> anOptional = Optional.of(1);

    assertThat(anOptional).hasValue(1);
}
```

是否值存在：

```java
@Test
void whenCheckingJavaOptional_thenPresent() {
    Optional<String> anOptional = Optional.of("Tuyucheng");

    assertThat(anOptional).isPresent();
}
```

或者是否该值不存在：

```java
@Test
void whenCheckingJavaOptional_thenEmpty() {
    Optional anOptional = Optional.empty();

    assertThat(anOptional).isEmpty();
}
```

### 6.2 Stream断言

Stream的断言与Iterable的断言非常相似，例如，我们可以测试一个特定的Stream是否以相同的顺序包含一个Iterable的所有对象：

```java
@Test
void whenCheckingStream_thenContainsInOrder() {
    Stream<Integer> anStream = Stream.of(1, 2, 3);

    assertThat(anStream)
        .containsAllOf(1, 2, 3)
        .inOrder();
}
```

有关更多示例，请参阅Iterable断言部分。

## 7. Guava断言

在本节中，我们介绍Truth中支持的Guava类型的断言案例。

### 7.1 Optional断言

Guava Optional有三个重要的断言方法，hasValue()和isPresent()方法的行为与Java 8 Optional完全相同，但是我们可以使用isAbsent()代替isEmpty()，来断言Optional不存在：

```java
@Test
void whenCheckingGuavaOptional_thenIsAbsent() {
    Optional anOptional = Optional.absent();

    assertThat(anOptional).isAbsent();
}
```

### 7.2 Multimap断言

Multimap和标准Map断言非常相似，一个显著的区别是我们可以在Multimap中获取键的多个值并对这些值进行断言。

以下是一个测试键为“one”的值是否为两个的示例：

```java
@Test
void whenCheckingGuavaMultimap_thenExpectedSize() {
    Multimap<String, Object> aMultimap = ArrayListMultimap.create();
    aMultimap.put("one", 1L);
    aMultimap.put("one", 2.0);

    assertThat(aMultimap)
        .valuesForKey("one")
        .hasSize(2);
}
```

有关更多示例，请参阅Map断言部分。

### 7.3 Multiset断言

Multiset对象的断言包括Iterable的断言和一个额外的方法，用于验证键是否具有特定的出现次数：

```java
@Test
void whenCheckingGuavaMultiset_thenExpectedCount() {
    TreeMultiset<String> aMultiset = TreeMultiset.create();
    aMultiset.add("tuyucheng", 10);

    assertThat(aMultiset).hasCount("tuyucheng", 10);
}
```

### 7.4 Table断言

除了检查Table的大小或其为空的位置之外，我们还可以检查一个Table来验证它是否包含给定行和列的特定Map：

```java
@Test
void whenCheckingGuavaTable_thenContains() {
    Table<String, String, String> aTable = TreeBasedTable.create();
    aTable.put("firstRow", "firstColumn", "tuyucheng");

    assertThat(aTable).contains("firstRow", "firstColumn");
}
```

或者是否它包含特定的单元格：

```java
@Test
void whenCheckingGuavaTable_thenContainsCell() {
    Table<String, String, String> aTable = getDummyGuavaTable();

    assertThat(aTable).containsCell("firstRow", "firstColumn", "tuyucheng");
}
```

此外，我们可以检查它是否包含给定的行、列或值；更多例子请参阅相关测试用例的源代码。

## 8. 自定义失败消息和标签

当一个断言失败时，Truth会显示非常可读的消息，确切地指出哪里出了什么问题。但是，有时需要向这些消息添加更多信息以提供有关所发生情况的更多详细信息。

Truth允许我们自定义这些失败消息：

```java
@Test
void whenFailingAssertion_thenCustomMessage() {
    assertWithMessage("TEST-985: Secret user subject was NOT null!")
        .that(new User())
        .isNull();
}
```

运行测试后，我们得到以下输出：

```shell
TEST-985: Secret user subject was NOT null!:
  Not true that <cn.tuyucheng.taketoday.testing.truth.User@ae805d5e> is null
```

此外，我们还可以添加一个自定义标签，该标签在错误消息中显示在主题之前。当对象没有有用的字符串表示时，这可能会派上用场：

```java
@Test
void whenFailingAssertion_thenMessagePrefix() {
    User aUser = new User();

    assertThat(aUser)
        .named("User [%s]", aUser.getName())
        .isNull();
}
```

如果我们运行测试，我们可以看到以下输出：

```shell
Not true that User [John Doe]
  (<cn.tuyucheng.taketoday.testing.truth.User@ae805d5e>) is null
```

## 9. 扩展

扩展Truth意味着我们可以添加对自定义类型的支持；为此，我们需要创建一个类：

-   扩展Subject类或其子类之一
-   定义一个接收两个参数的构造函数：一个FailureStrategy和一个我们自定义类型的实例
-   声明一个SubjectFactory类型的字段，Truth将使用该字段来创建我们自定义主题的实例
-   实现一个接收我们自定义类型的静态assertThat()方法
-   公开我们的测试断言API

下面我们创建一个类来添加对User类型对象的支持：

```java
public class UserSubject extends ComparableSubject<UserSubject, User> {

	private UserSubject(FailureStrategy failureStrategy, User target) {
		super(failureStrategy, target);
	}

	private static final SubjectFactory<UserSubject, User> USER_SUBJECT_FACTORY = new SubjectFactory<UserSubject, User>() {
		@Override
		public UserSubject getSubject(FailureStrategy failureStrategy, User target) {
			return new UserSubject(failureStrategy, target);
		}
	};

	public static UserSubject assertThat(User user) {
		return Truth.assertAbout(USER_SUBJECT_FACTORY)
				.that(user);
	}

	// Our API begins here
	public void hasName(String name) {
		if (!actual().getName().equals(name)) {
			fail("has name", name);
		}
	}

	public void hasNameIgnoringCase(String name) {
		if (!actual().getName().equalsIgnoreCase(name)) {
			fail("has name ignoring case", name);
		}
	}

	public IterableSubject emails() {
		return Truth.assertThat(actual().getEmails());
	}
}
```

现在，我们可以静态导入自定义主题的assertThat()方法并编写一些测试：

```java
@Test
void whenCheckingUser_thenHasName() {
    User aUser = new User();

    assertThat(aUser).hasName("John Doe");
}

@Test
void whenCheckingUser_thenHasNameIgnoringCase() {
    // ...

    assertThat(aUser).hasNameIgnoringCase("john doe");
}

@Test
void givenUser_whenCheckingEmails_thenExpectedSize() {
    // ...

    assertThat(aUser)
        .emails()
        .hasSize(2);
}
```

## 10. 总结

在本教程中，我们演示了支持Java和Guava类型的最常用的断言方法、自定义的失败消息以及带有自定义主题的扩展Truth。
