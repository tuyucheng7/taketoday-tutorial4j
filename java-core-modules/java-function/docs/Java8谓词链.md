## 1. 概述

在本快速教程中，我们将讨论在Java8 中链接[谓词](https://www.baeldung.com/cs/predicates)的不同方法。

## 2. 基本示例

首先，让我们看看如何使用一个简单的Predicate来过滤名称列表：

```java
@Test
public void whenFilterList_thenSuccess(){
   List<String> names = Arrays.asList("Adam", "Alexander", "John", "Tom");
   List<String> result = names.stream()
     .filter(name -> name.startsWith("A"))
     .collect(Collectors.toList());
   
   assertEquals(2, result.size());
   assertThat(result, contains("Adam","Alexander"));
}
```

在这个例子中，我们使用Predicate过滤我们的名字 列表，只留下以“A”开头的名字：

```java
name -> name.startsWith("A")
```

但是如果我们想应用多个Predicates怎么办？

## 3.多重过滤器

如果我们想应用多个Predicates，一种选择是简单地链接多个过滤器：

```java
@Test
public void whenFilterListWithMultipleFilters_thenSuccess(){
    List<String> result = names.stream()
      .filter(name -> name.startsWith("A"))
      .filter(name -> name.length() < 5)
      .collect(Collectors.toList());

    assertEquals(1, result.size());
    assertThat(result, contains("Adam"));
}
```

我们现在更新了我们的示例，通过提取以“A”开头且长度小于 5 的名称来过滤我们的列表。

我们使用了两个过滤器——每个Predicate一个。

## 4.复杂谓词

现在，我们可以使用一个带有复杂Predicate的过滤器，而不是使用多个过滤器：

```java
@Test
public void whenFilterListWithComplexPredicate_thenSuccess(){
    List<String> result = names.stream()
      .filter(name -> name.startsWith("A") && name.length() < 5)
      .collect(Collectors.toList());

    assertEquals(1, result.size());
    assertThat(result, contains("Adam"));
}
```

这个选项比第一个更灵活，因为我们可以使用按位运算来构建 我们想要的复杂谓词。

## 5.组合 谓词

接下来，如果我们不想使用按位运算构建复杂的Predicate ，Java 8 Predicate有一些有用的方法可以用来组合Predicates。

我们将使用Predicate.and ()、Predicate.or()和Predicate.negate() 方法组合谓词。

### 5.1. 谓词.and()

在这个例子中，我们将显式定义我们的谓词，然后我们将使用 Predicate.and() 组合它们：

```java
@Test
public void whenFilterListWithCombinedPredicatesUsingAnd_thenSuccess(){
    Predicate<String> predicate1 =  str -> str.startsWith("A");
    Predicate<String> predicate2 =  str -> str.length() < 5;
  
    List<String> result = names.stream()
      .filter(predicate1.and(predicate2))
      .collect(Collectors.toList());
        
    assertEquals(1, result.size());
    assertThat(result, contains("Adam"));
}
```

如我们所见，语法相当直观，方法名称暗示了操作的类型。使用and()，我们通过仅提取满足两个条件的名称来过滤我们的列表。

### 5.2. 谓词.or()

我们还可以使用 Predicate.or()来组合谓词。

让我们提取以“J”开头的名称，以及长度小于 4 的名称：

```java
@Test
public void whenFilterListWithCombinedPredicatesUsingOr_thenSuccess(){
    Predicate<String> predicate1 =  str -> str.startsWith("J");
    Predicate<String> predicate2 =  str -> str.length() < 4;
    
    List<String> result = names.stream()
      .filter(predicate1.or(predicate2))
      .collect(Collectors.toList());
    
    assertEquals(2, result.size());
    assertThat(result, contains("John","Tom"));
}
```

### 5.3. 谓词.negate()

我们也可以在组合谓词时使用Predicate.negate() ：

```java
@Test
public void whenFilterListWithCombinedPredicatesUsingOrAndNegate_thenSuccess(){
    Predicate<String> predicate1 =  str -> str.startsWith("J");
    Predicate<String> predicate2 =  str -> str.length() < 4;
    
    List<String> result = names.stream()
      .filter(predicate1.or(predicate2.negate()))
      .collect(Collectors.toList());
    
    assertEquals(3, result.size());
    assertThat(result, contains("Adam","Alexander","John"));
}
```

在这里，我们使用了or()和negate()的组合来过滤以“J”开头或长度不小于 4 的名称的列表。

### 5.4. 组合谓词内联

我们不需要显式定义谓词来使用and()、 or()和negate()。

我们还可以通过转换 Predicate来内联使用它们：

```java
@Test
public void whenFilterListWithCombinedPredicatesInline_thenSuccess(){
    List<String> result = names.stream()
      .filter(((Predicate<String>)name -> name.startsWith("A"))
      .and(name -> name.length()<5))
      .collect(Collectors.toList());

    assertEquals(1, result.size());
    assertThat(result, contains("Adam"));
}
```

## 6. 组合谓词集合

最后，让我们看看如何通过归约来链接一组谓词。

在以下示例中，我们有一个使用Predicate.and()组合的谓词列表：

```java
@Test
public void whenFilterListWithCollectionOfPredicatesUsingAnd_thenSuccess(){
    List<Predicate<String>> allPredicates = new ArrayList<Predicate<String>>();
    allPredicates.add(str -> str.startsWith("A"));
    allPredicates.add(str -> str.contains("d"));        
    allPredicates.add(str -> str.length() > 4);
    
    List<String> result = names.stream()
      .filter(allPredicates.stream().reduce(x->true, Predicate::and))
      .collect(Collectors.toList());
    
    assertEquals(1, result.size());
    assertThat(result, contains("Alexander"));
}
```

请注意，我们将基本身份用作：

```java
x->true
```

但如果我们想使用Predicate.or()组合它们，那将是不同的：

```java
@Test
public void whenFilterListWithCollectionOfPredicatesUsingOr_thenSuccess(){
    List<String> result = names.stream()
      .filter(allPredicates.stream().reduce(x->false, Predicate::or))
      .collect(Collectors.toList());
    
    assertEquals(2, result.size());
    assertThat(result, contains("Adam","Alexander"));
}
```

## 七、总结

在本文中，我们通过使用filter()、构建复杂的Predicates和组合 Predicates 探索了在Java8 中链接 Predicates 的不同方法。