## 1. 概述

**Hamcrest是一个提供称为匹配器的方法的库，可帮助开发人员编写更简单的单元测试**。在本文中，我们介绍bean 匹配器。

## 2. Maven

只需将以下Maven依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>java-hamcrest</artifactId>
    <version>2.0.0.0</version>
    <scope>test</scope>
</dependency>
```

## 3. Bean匹配器

Bean匹配器**对于检查POJO上的条件非常有用**，这是编写大多数单元测试时经常需要的。

首先，我们创建一个测试中需要使用到的类：

```java
public class City {
    String name;
    String state;

    // standard constructor, getters and setters
}
```

### 3.1 hasProperty

这个匹配器**检查某个bean是否包含由属性名称标识的特定属性**：

```java
@Test
void givenACity_whenHasProperty_thenCorrect() {
    City city = new City("San Francisco", "CA");
    
    assertThat(city, hasProperty("state"));
}
```

以上测试将通过，因为我们的City bean有一个名为state的属性。

按照这个思路，我们还可以测试一个bean是否具有特定的属性，并且该属性是否具有特定的值：

```java
@Test
void givenACity_whenHasPropertyWithValueEqualTo_thenCorrect() {
    City city = new City("San Francisco", "CA");
        
    assertThat(city, hasProperty("name", equalTo("San Francisco")));
}
```

如我们所见，**hasProperty是重载的，可以与第二个匹配器一起使用，来检查属性上的特定条件**。

因此，我们也可以这样做：

```java
@Test
void givenACity_whenHasPropertyWithValueEqualToIgnoringCase_thenCorrect() {
    City city = new City("San Francisco", "CA");

    assertThat(city, hasProperty("state", equalToIgnoringCase("ca")));
}
```

### 3.2 samePropertyValuesAs

有时，**当我们必须对bean的许多属性进行检查时，创建具有所需值的新bean可能会更简单**,然后，我们可以检查测试的bean和新的bean是否相等.当然，Hamcrest为这种情况提供了一个匹配器：

```java
@Test
void givenACity_whenSamePropertyValuesAs_thenCorrect() {
    City city = new City("San Francisco", "CA");
    City city2 = new City("San Francisco", "CA");

    assertThat(city, samePropertyValuesAs(city2));
}
```

同样的方法，我们可以测试否定的情况：

```java
@Test
void givenACity_whenNotSamePropertyValuesAs_thenCorrect() {
    City city = new City("San Francisco", "CA");
    City city2 = new City("Los Angeles", "CA");

    assertThat(city, not(samePropertyValuesAs(city2)));
}
```

接下来，看看几个用于检查类属性的工具方法。

### 3.3 getPropertyDescriptor

**在某些情况下，获取类结构可能会派上用场**，Hamcrest提供了一些工具方法来做到这一点：

```java
@Test
void givenACity_whenGetPropertyDescriptor_thenCorrect() {
	City city = new City("San Francisco", "CA");
	PropertyDescriptor descriptor = getPropertyDescriptor("state", city);
    
	assertThat(descriptor
			.getReadMethod()
			.getName(), is(equalTo("getState")));
}
```

**PropertyDescriptor检索大量关于属性状态的信息**，在本例中，我们提取了getter方法的名称并断言它等于某个预期值；请注意，我们还可以应用其他文本匹配器。

### 3.4 propertyDescriptorsFor

**此方法与上一节中的方法基本相同，只是针对bean的所有属性**，我们还需要指定我们希望在类层次结构中达到的高度：

```java
@Test
void givenACity_whenGetPropertyDescriptorsFor_thenCorrect() {
	City city = new City("San Francisco", "CA");
	PropertyDescriptor[] descriptors = propertyDescriptorsFor(city, Object.class);
    
	List<String> getters = Arrays
			.stream(descriptors)
			.map(x -> x.getReadMethod().getName())
			.collect(toList());
    
	assertThat(getters, containsInAnyOrder("getName", "getState"));
}
```

我们从city bean获取所有的属性描述符，并在Object级别停止。然后，我们使用Java 8的特性来过滤getter方法。最后，我们使用集合匹配器来检查getter方法集合中的内容。

## 4. 总结

Bean匹配器提供了一种对POJO进行断言的有效方法，这是编写单元测试时经常需要的东西。