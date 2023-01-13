## 1. 概述

**Hamcrest提供了匹配器，使单元测试断言更简单、更清晰**；在这个快速教程中，我们深入介绍对象匹配器。

## 2. Maven

我们只需将以下Maven依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.hamcrest</groupId>
    <artifactId>java-hamcrest</artifactId>
    <version>2.0.0.0</version>
    <scope>test</scope>
</dependency>
```

## 3. 对象匹配器

对象匹配器旨在对对象的属性进行检查，首先我们创建几个bean。

第一个对象为Location并且没有属性：

```java
public class Location {}
```

第二个类为City并为其添加以下实现：

```java
public class City extends Location {
    String name;
    String state;

    // standard constructor, getters and setters

    	@Override
	public String toString() {
		if (this.name == null && this.state == null) return null;
		return "[" +
				"Name: " +
				this.name +
				", " +
				"State: " +
				this.state +
				"]";
	}
}
```

请注意，City继承Location了，我们稍后会用到它。

### 3.1 hasToString

顾名思义，**hasToString方法验证某个对象是否有一个返回特定String的toString方法**：

```java
@Test
void givenACity_whenHasToString_thenCorrect() {
    City city = new City("San Francisco", "CA");
    
    assertThat(city, hasToString("[Name: San Francisco, State: CA]"));
}
```

我们创建一个City并验证其toString方法是否返回了我们想要的字符串。

我们也可以检查其他条件：

```java
@Test
void givenACity_whenHasToStringEqualToIgnoringCase_thenCorrect() {
    City city = new City("San Francisco", "CA");

    assertThat(city, hasToString(equalToIgnoringCase("[NAME: SAN FRANCISCO, STATE: CA]")));
}
```

如我们所见，**hasToString是重载的，可以接收String或文本匹配器作为参数**；因此，我们还可以执行以下操作：

```java
@Test
void givenACity_whenHasToStringEmptyOrNullString_thenCorrect() {
    City city = new City(null, null);
    
    assertThat(city, hasToString(emptyOrNullString()));
}
```

### 3.2 typeCompatibleWith

这个匹配器代表一种**is-a关系**，这里使用Location作为演示：

```java
@Test
void givenACity_whenTypeCompatibleWithLocation_thenCorrect() {
    City city = new City("San Francisco", "CA");

    assertThat(city.getClass(), is(typeCompatibleWith(Location.class)));
}
```

上面的测试表示City是一个Location，这是正确的，因此该测试应该通过；另外，如果我们想测试否定的情况：

```java
@Test
void givenACity_whenTypeNotCompatibleWithString_thenCorrect() {
    City city = new City("San Francisco", "CA");

    assertThat(city.getClass(), is(not(typeCompatibleWith(String.class))));
}
```

当然，我们的City类不是字符串。最后，所有Java对象都应该通过以下测试：

```java
@Test
void givenACity_whenTypeCompatibleWithObject_thenCorrect() {
    City city = new City("San Francisco", "CA");

    assertThat(city.getClass(), is(typeCompatibleWith(Object.class)));
}
```

注意，**匹配器由另一个匹配器的包装器组成，目的是使整个断言更具可读性**。

## 4. 总结

**Hamcrest提供了一种简单而干净的方法来创建断言**，有各种各样的匹配器可以供开发人员使用。**对象匹配器是检查类属性的一种简单方式**。