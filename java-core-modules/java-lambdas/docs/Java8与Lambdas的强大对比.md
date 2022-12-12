## 1. 概述

在本教程中，我们将首先了解Java 8中的Lambda支持，特别是如何利用它来编写Comparator并对Collection进行排序。

首先，让我们定义一个简单的实体类：

```java
public class Human {
    private String name;
    private int age;

    // standard constructors, getters/setters, equals and hashcode
}
```

## 2. 没有Lambda的基本排序

在Java 8之前，对集合进行排序将涉及为排序中使用的Comparator创建一个匿名内部类：

```java
new Comparator<Human>() {
    @Override
    public int compare(Human h1, Human h2) {
        return h1.getName().compareTo(h2.getName());
    }
}
```

这将仅用于对人类实体集合进行排序：

```java
@Test
final void givenPreLambda_whenSortingEntitiesByName_thenCorrectlySorted() {
	final ArrayList<Human> humans = Lists.newArrayList(new Human("Search", 10), new Human("Jack", 12));
    
	Collections.sort(humans, new Comparator<Human>() {
		@Override
		public int compare(final Human h1, final Human h2) {
			return h1.getName().compareTo(h2.getName());
		}
	});
    
	assertThat(humans.get(0), equalTo(new Human("Jack", 12)));
}
```

## 3. Lambda支持的基本排序

随着Lambdas的引入，我们现在可以绕过匿名内部类并使用**简单的函数式语义**实现相同的结果：

```java
(final Human h1, final Human h2) -> h1.getName().compareTo(h2.getName());
```

同样，我们现在可以像以前一样测试排序行为：

```java
@Test
final void whenSortingEntitiesByName_thenCorrectlySorted() {
	final ArrayList<Human> humans = Lists.newArrayList(new Human("Search", 10), new Human("Jack", 12));
    
	humans.sort((final Human h1, final Human h2) -> h1.getName().compareTo(h2.getName()));
    
	assertThat(humans.get(0), equalTo(new Human("Jack", 12)));
}
```

请注意，我们还使用了**添加到Java 8中的java.util.List的新排序API**，而不是旧的Collections.sort API。

## 4. 没有类型定义的基本排序

我们可以通过不指定类型定义来进一步简化表达式；编译器能够自行推断这些：

```java
(h1, h2) -> h1.getName().compareTo(h2.getName())
```

同样，测试仍然非常相似：

```java
@Test
final void givenLambdaShortForm_whenSortingEntitiesByName_thenCorrectlySorted() {
	final ArrayList<Human> humans = Lists.newArrayList(new Human("Search", 10), new Human("Jack", 12));
    
	humans.sort((h1, h2) -> h1.getName().compareTo(h2.getName()));
    
	assertThat(humans.get(0), equalTo(new Human("Jack", 12)));
}
```

## 5. 使用静态方法引用进行排序

接下来，我们使用带有对静态方法的引用的Lambda表达式来执行排序。

首先，我们将使用与Comparator<Human\>对象中的compare方法完全相同的签名来定义方法compareByNameThenAge：

```java
public static int compareByNameThenAge(Human lhs, Human rhs) {
    if (lhs.name.equals(rhs.name)) {
        return Integer.compare(lhs.age, rhs.age);
    } else {
        return lhs.name.compareTo(rhs.name);
    }
}
```

然后我们将使用此引用调用humans.sort方法：

```java
humans.sort(Human::compareByNameThenAge);
```

最终结果是使用静态方法作为Comparator对集合进行排序：

```java
@Test
final void givenMethodDefinition_whenSortingEntitiesByNameThenAge_thenCorrectlySorted() {
	final List<Human> humans = Lists.newArrayList(new Human("Sarah", 10), new Human("Jack", 12));
    
	humans.sort(Human::compareByNameThenAge);
    
	assertThat(humans.get(0), equalTo(new Human("Jack", 12)));
}
```

## 6. 对提取的比较器进行排序

我们还可以通过使用实例方法引用和Comparator.comparing方法来避免定义比较逻辑本身，该方法基于该函数提取并创建Comparable。

我们将使用getter getName()来构建Lambda表达式并按名称对集合进行排序：

```java
@Test
final void givenInstanceMethod_whenSortingEntitiesByName_thenCorrectlySorted() {
	final List<Human> humans = Lists.newArrayList(new Human("Sarah", 10), new Human("Jack", 12));
    
	Collections.sort(humans, Comparator.comparing(Human::getName));
    
	assertThat(humans.get(0), equalTo(new Human("Jack", 12)));
}
```

## 7. 反向排序

JDK 8还引入了一个用于反转比较器的工具方法，我们可以快速利用它来反转我们的排序：

```java
@Test
final void whenSortingEntitiesByNameReversed_thenCorrectlySorted() {
	final List<Human> humans = Lists.newArrayList(new Human("Sarah", 10), new Human("Jack", 12));
	final Comparator<Human> comparator = (h1, h2) -> h1.getName().compareTo(h2.getName());
    
	humans.sort(comparator.reversed());
    
	assertThat(humans.get(0), equalTo(new Human("Sarah", 10)));
}
```

## 8. 多条件排序

比较lambda表达式不需要这么简单。我们也可以编写更复杂的表达式，例如首先按名称对实体进行排序，然后按年龄排序：

```java
@Test
public void whenSortingEntitiesByNameThenAge_thenCorrectlySorted() {
    List<Human> humans = Lists.newArrayList(
      new Human("Sarah", 12), 
      new Human("Sarah", 10), 
      new Human("Zack", 12)
    );
    
    humans.sort((lhs, rhs) -> {
        if (lhs.getName().equals(rhs.getName())) {
            return Integer.compare(lhs.getAge(), rhs.getAge());
        } else {
            return lhs.getName().compareTo(rhs.getName());
        }
    });
    Assert.assertThat(humans.get(0), equalTo(new Human("Sarah", 10)));
}
```

## 9. 多条件排序-组合

相同的比较逻辑，首先按姓名排序，然后按年龄排序，也可以通过对Comparator的新组合支持来实现。

从JDK 8开始，我们现在可以将多个比较器链接在一起以构建更复杂的比较逻辑：

```java
@Test
final void givenComposition_whenSortingEntitiesByNameThenAge_thenCorrectlySorted() {
	final List<Human> humans = Lists.newArrayList(new Human("Sarah", 12), new Human("Sarah", 10), new Human("Zack", 12));
    
	humans.sort(Comparator.comparing(Human::getName).thenComparing(Human::getAge));
    
	assertThat(humans.get(0), equalTo(new Human("Sarah", 10)));
}
```

## 10. 使用Stream.sorted()对集合进行排序

**我们还可以使用Java 8的Stream sorted() API对集合进行排序**。 

我们可以使用自然顺序以及比较器提供的顺序对流进行排序。为此，我们可以使用两个重载的sorted() API变体：

-   sorted()：使用自然顺序对Stream的元素进行排序；元素类必须实现Comparable接口。
-   sorted(Comparator<? super T> comparator)：根据Comparator实例对元素进行排序

让我们看一个如何使用具有自然排序的sorted()方法的示例：

```java
@Test
final void givenStreamNaturalOrdering_whenSortingEntitiesByName_thenCorrectlySorted() {
	final List<String> letters = Lists.newArrayList("B", "A", "C");
    
	List<String> sortedLetters = letters.stream().sorted().collect(Collectors.toList());
    
	assertThat(sortedLetters.get(0), equalTo("A"));
}
```

现在让我们看看如何使用带有自定义比较器的sorted() API：

```java
@Test
final void givenStreamCustomOrdering_whenSortingEntitiesByName_thenCorrectlySorted() {
	final List<Human> humans = Lists.newArrayList(new Human("Sarah", 10), new Human("Jack", 12));
	final Comparator<Human> nameComparator = (h1, h2) -> h1.getName().compareTo(h2.getName());
    
	final List<Human> sortedHumans = humans.stream().sorted(nameComparator).collect(Collectors.toList());
    
	assertThat(sortedHumans.get(0), equalTo(new Human("Jack", 12)));
}
```

如果我们使用Comparator.comparing()方法，我们可以进一步简化上面的例子：

```java
@Test
final void givenStreamComparatorOrdering_whenSortingEntitiesByName_thenCorrectlySorted() {
	final List<Human> humans = Lists.newArrayList(new Human("Sarah", 10), new Human("Jack", 12));
	final List<Human> sortedHumans = humans.stream().sorted(Comparator.comparing(Human::getName)).collect(Collectors.toList());
    
	assertThat(sortedHumans.get(0), equalTo(new Human("Jack", 12)));
}
```

## 11. 使用Stream.sorted()对集合进行反向排序

我们还可以使用Stream.sorted()对集合进行反向排序。

首先，让我们看一个示例，说明如何将sorted()方法与Comparator.reverseOrder()结合起来以反向自然顺序对列表进行排序：

```java
@Test
final void givenStreamNaturalOrdering_whenSortingEntitiesByNameReversed_thenCorrectlySorted() {
	final List<String> letters = Lists.newArrayList("B", "A", "C");
    
	final List<String> reverseSortedLetters = letters.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    
	assertThat(reverseSortedLetters.get(0), equalTo("C"));
}
```

现在让我们看看如何使用sorted()方法和自定义Comparator：

```java
@Test
final void givenStreamCustomOrdering_whenSortingEntitiesByNameReversed_thenCorrectlySorted() {
	final List<Human> humans = Lists.newArrayList(new Human("Sarah", 10), new Human("Jack", 12));
	final Comparator<Human> reverseNameComparator = (h1, h2) -> h2.getName().compareTo(h1.getName());
    
	final List<Human> reverseSortedHumans = humans.stream().sorted(reverseNameComparator).collect(Collectors.toList());
    
	assertThat(reverseSortedHumans.get(0), equalTo(new Human("Sarah", 10)));
}
```

请注意，compareTo的调用是翻转的，它负责反转。

最后，让我们使用Comparator.comparing()方法简化上面的示例：

```java
@Test
final void givenStreamComparatorOrdering_whenSortingEntitiesByNameReversed_thenCorrectlySorted() {
	final List<Human> humans = Lists.newArrayList(new Human("Sarah", 10), new Human("Jack", 12));
    
	final List<Human> reverseSortedHumans = humans.stream()
        .sorted(Comparator.comparing(Human::getName, Comparator.reverseOrder()))
        .collect(Collectors.toList());
    
	assertThat(reverseSortedHumans.get(0), equalTo(new Human("Sarah", 10)));
}
```

## 12. 空值

到目前为止，我们以无法对包含空值的集合进行排序的方式实现我们的Comparator。也就是说，如果集合包含至少一个null元素，则sort方法会抛出NullPointerException：

```java
@Test
final void givenANullElement_whenSortingEntitiesByName_thenThrowsNPE() {
	final List<Human> humans = Lists.newArrayList(null, new Human("Jack", 12));
    
	assertThrows(NullPointerException.class, () -> humans.sort((h1, h2) -> h1.getName().compareTo(h2.getName())));
}
```

最简单的解决方案是在我们的Comparator实现中手动处理空值：

```java
@Test
final void givenANullElement_whenSortingEntitiesByNameManually_thenMovesTheNullToLast() {
	final List<Human> humans = Lists.newArrayList(null, new Human("Jack", 12), null);
    
	humans.sort((h1, h2) -> {
		if (h1 == null) return h2 == null ? 0 : 1;
		else if (h2 == null) return -1;
		return h1.getName().compareTo(h2.getName());
	});
    
	assertNotNull(humans.get(0));
	assertNull(humans.get(1));
	assertNull(humans.get(2));
}
```

在这里，我们将所有null元素推向集合的末尾。为此，比较器认为空值大于非空值。当两者都为null时，它们被认为是相等的。

此外，我们可以将任何非空安全的Comparator传递给[Comparator.nullsLast()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Comparator.html#nullsLast(java.util.Comparator))方法并获得相同的结果：

```java
@Test
final void givenANullElement_whenSortingEntitiesByName_thenMovesTheNullToLast() {
	final List<Human> humans = Lists.newArrayList(null, new Human("Jack", 12), null);
    
	humans.sort(Comparator.nullsLast(Comparator.comparing(Human::getName)));
    
	assertNotNull(humans.get(0));
	assertNull(humans.get(1));
	assertNull(humans.get(2));
}
```

同样，我们可以使用[Comparator.nullsFirst()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Comparator.html#nullsFirst(java.util.Comparator))将空元素移向集合的开头：

```java
@Test
final void givenANullElement_whenSortingEntitiesByName_thenMovesTheNullToStart() {
	final List<Human> humans = Lists.newArrayList(null, new Human("Jack", 12), null);
    
	humans.sort(Comparator.nullsFirst(Comparator.comparing(Human::getName)));
    
	assertNull(humans.get(0));
	assertNull(humans.get(1));
	assertNotNull(humans.get(2));
}
```

强烈建议使用nullsFirst()或nullsLast()装饰器，因为它们更加灵活和可读。

## 13. 总结

本文阐述了使用Java 8 Lambda表达式对列表进行排序的各种令人兴奋的方式，直接超越了语法糖，进入了真实而强大的函数语义。