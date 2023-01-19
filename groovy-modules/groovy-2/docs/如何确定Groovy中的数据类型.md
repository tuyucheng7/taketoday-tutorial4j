## 一、简介

在本快速教程中，我们将探索在 Groovy 中查找数据类型的不同方法。

实际上，这取决于我们在做什么：

-   首先，我们将看看如何处理基元
-   然后，我们看看集合将如何带来一些独特的挑战
-   最后，我们来看看对象和类变量

## 2. 原始类型

[Groovy](https://groovy-lang.org/objectorientation.html)支持与 Java 相同数量的原始类型。我们可以通过三种方式找到原语的数据类型。

首先，假设我们有一个人年龄的多种表示形式。

首先，让我们从instanceof运算符开始：

```groovy
@Test
public void givenWhenParameterTypeIsInteger_thenReturnTrue() {
    Person personObj = new Person(10)
    Assert.assertTrue(personObj.ageAsInt instanceof Integer);
}
```

[instanceof](https://www.baeldung.com/java-instanceof) 是一个二元运算符，我们可以使用它来检查对象是否是给定类型的实例。如果对象是该特定类型的实例，则返回true ，否则返回false。

此外，Groovy 3 添加了新的!instanceof运算符。如果对象不是某个类型的实例，则返回true ，否则返回false。

然后，我们还可以使用 Object 类中的getClass()方法。它返回实例的运行时类：

```groovy
@Test
public void givenWhenParameterTypeIsDouble_thenReturnTrue() {
    Person personObj = new Person(10.0)
    Assert.assertTrue((personObj.ageAsDouble).getClass() == Double)
}
```

最后，让我们应用 . 查找数据类型的类运算符：

```groovy
@Test
public void givenWhenParameterTypeIsString_thenReturnTrue() {
    Person personObj = new Person("10 years")
    Assert.assertTrue(personObj.ageAsString.class == String)
}
```

同样，我们可以找到任何原始类型的数据类型。

## 3.收藏品

Groovy 提供对各种集合类型的支持。

让我们在 Groovy 中定义一个简单的列表：

```groovy
@Test
public void givenGroovyList_WhenFindClassName_thenReturnTrue() {
    def ageList = ['ageAsString','ageAsDouble', 10]
    Assert.assertTrue(ageList.class == ArrayList)
    Assert.assertTrue(ageList.getClass() == ArrayList)
}
```

但在地图上，无法应用.class运算符：

```groovy
@Test
public void givenGrooyMap_WhenFindClassName_thenReturnTrue() {
    def ageMap = [ageAsString: '10 years', ageAsDouble: 10.0]
    Assert.assertFalse(ageMap.class == LinkedHashMap)
}
```

在上面的代码片段中，ageMap.class将尝试从给定的 map 中检索键类的值。对于地图，建议应用getClass() 而不是.class。

## 4. 对象和类变量

在上面的部分中，我们使用了各种策略来查找基元和集合的数据类型。

要查看类变量如何工作，假设我们有一个类Person：

```groovy
@Test
public void givenClassName_WhenParameterIsInteger_thenReturnTrue() {
    Assert.assertTrue(Person.class.getDeclaredField('ageAsInt').type == int.class)
}
```

请记住，[getDeclaredField()](https://www.baeldung.com/java-reflection-class-fields)返回特定类的所有字段。

我们可以使用instanceof、getClass()和.class运算符找到任何对象的类型：

```groovy
@Test
public void givenWhenObjectIsInstanceOfType_thenReturnTrue() {
    Person personObj = new Person()
    Assert.assertTrue(personObj instanceof Person)
}
```

此外，我们还可以在 中使用 Groovy 成员资格运算符：

```groovy
@Test
public void givenWhenInstanceIsOfSubtype_thenReturnTrue() {
    Student studentObj = new Student()
    Assert.assertTrue(studentObj in Person)
}
```

## 5.总结

在这篇简短的文章中，我们了解了如何在 Groovy 中查找数据类型。相比之下，getClass()方法比.class运算符更安全。我们还讨论了in运算符和instanceof运算符的工作原理。此外，我们学习了如何获取类的所有字段并应用.type运算符。