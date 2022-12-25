## 1. 概述

在本快速教程中，我们将学习如何从Spring属性文件中将值注入数组或集合。

## 2. 默认行为

我们将从一个简单的application.properties文件开始：

```properties
arrayOfStrings=Tuyucheng,dot,com
```

让我们看看当我们将变量类型设置为String[]时Spring的默认行为：

```java
@Value("${arrayOfStrings}")
private String[] arrayOfStrings;
```

```java
@Test
void whenContextIsInitialized_thenInjectedArrayContainsExpectedValues() {
    assertArrayEquals(new String[] {"Tuyucheng", "dot", "com"}, arrayOfStrings);
}
```

**我们可以看到Spring正确地假设我们的分隔符是逗号并相应地初始化了数组**。

我们还应该注意，默认情况下，只有当我们有逗号分隔的值时，注入数组才能正常工作。

## 3. 注入集合

如果我们尝试以相同的方式注入一个List，我们会得到一个令人惊讶的结果：

```java
@Value("${arrayOfStrings}")
private List<String> unexpectedListOfStrings;
```

```java
@Test
void whenContextIsInitialized_thenInjectedListContainsUnexpectedValues() {
    assertEquals(Collections.singletonList("Tuyucheng,dot,com"), unexpectedListOfStrings);
}
```

**我们的集合只包含一个元素，它等于我们在属性文件中设置的值**。

为了正确地注入一个List，我们需要使用一种称为[Spring Expression Language]()(SpEL)的特殊语法：

```java
@Value("#{'${arrayOfStrings}'.split(',')}")
private List<String> listOfStrings;
```

```java
@Test
void whenContextIsInitialized_thenInjectedListContainsExpectedValues() {
    assertEquals(Arrays.asList("Tuyucheng", "dot", "com"), listOfStrings);
}
```

我们可以看到我们的表达式以#开头，而不是我们习惯于@Value的$。

**我们还应该注意，我们正在调用一个split方法，这使得表达式比通常的注入更复杂一些**。

如果我们想让我们的表达式更简单一些，我们可以用一种特殊的格式声明我们的属性：

```properties
listOfStrings={'Tuyucheng','dot','com'}
```

Spring会识别这种格式，我们将能够使用更简单的表达式来注入我们的List：

```java
@Value("#{${listOfStrings}}")
private List<String> listOfStringsV2;
```

```java
@Test
void whenContextIsInitialized_thenInjectedListV2ContainsExpectedValues() {
    assertEquals(Arrays.asList("Tuyucheng", "dot", "com"), listOfStringsV2);
}
```

## 4. 使用自定义分隔符

让我们创建一个类似的属性，但这一次，我们将使用不同的分隔符：

```properties
listOfStringsWithCustomDelimiter=Tuyucheng;dot;com
```

**正如我们在注入List时所看到的那样，我们可以使用一个特殊的表达式来指定我们想要的分隔符**：

```java
@Value("#{'${listOfStringsWithCustomDelimiter}'.split(';')}")
private List<String> listOfStringsWithCustomDelimiter;
```

```java
@Test
void whenContextIsInitialized_thenInjectedListWithCustomDelimiterContainsExpectedValues() {
    assertEquals(Arrays.asList("Tuyucheng", "dot", "com"), listOfStringsWithCustomDelimiter);
}
```

## 5. 注入其他类型

让我们看一下以下属性：

```properties
listOfBooleans=false,false,true
listOfIntegers=1,2,3,4
listOfCharacters=a,b,c
```

**我们可以看到Spring支持开箱即用的基本类型，所以我们不需要做任何特殊的解析**：

```java
@Value("#{'${listOfBooleans}'.split(',')}")
private List<Boolean> listOfBooleans;

@Value("#{'${listOfIntegers}'.split(',')}")
private List<Integer> listOfIntegers;

@Value("#{'${listOfCharacters}'.split(',')}")
private List<Character> listOfCharacters;
```

```java
@Test
void whenContextIsInitialized_thenInjectedListOfBasicTypesContainsExpectedValues() {
    assertEquals(Arrays.asList(false, false, true), listOfBooleans);
    assertEquals(Arrays.asList(1, 2, 3, 4), listOfIntegers);
    assertEquals(Arrays.asList('a', 'b', 'c'), listOfCharacters);
}
```

这仅通过SpEL支持，因此我们不能以相同的方式注入数组。

## 6. 以编程方式读取属性

为了以编程方式读取属性，我们首先需要获取Environment对象的实例：

```java
@Autowired
private Environment environment;
```

**然后我们可以简单地使用getProperty方法通过指定其键和预期类型来读取任何属性**：

```java
@Test
void whenReadingFromSpringEnvironment_thenPropertiesHaveExpectedValues() {
    String[] arrayOfStrings = environment.getProperty("arrayOfStrings", String[].class);
    List<String> listOfStrings = (List<String>)environment.getProperty("arrayOfStrings", List.class);

    assertArrayEquals(new String[] {"Tuyucheng", "dot", "com"}, arrayOfStrings);
    assertEquals(Arrays.asList("Tuyucheng", "dot", "com"), listOfStrings);
}
```

## 7. 总结

在本文中，我们通过一些简单实用的示例学习了如何轻松地将配置属性注入数组和List。