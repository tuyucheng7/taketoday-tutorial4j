## 1. 概述

在本教程中，我们将展示如何使用 Google Guava 的BiMap接口及其多个实现。

BiMap (或“双向映射”)是一种特殊的映射，它保持映射的反向视图，同时确保不存在重复值，并且始终可以安全地使用值来取回键。

BiMap的基本实现是HashBiMap，它在内部使用两个Map，一个用于键到值的映射，另一个用于值到键的映射。

## 2. Google Guava 的BiMap

让我们看看如何使用BiMap类。

我们将从在pom.xml中添加 Google Guava 库依赖项开始：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")检查最新版本的依赖项。

## 3.创建BiMap

你可以通过多种方式创建BiMap的实例，如下所示：

-   如果你要处理自定义Java对象，请使用类 HashBiMap 中的创建方法：

```java
BiMap<String, String> capitalCountryBiMap = HashBiMap.create();
```

-   如果我们已经有一个现有的地图，你可以使用类HashBiMap中create方法的重载版本创建BiMap的实例：

```java
Map<String, String> capitalCountryBiMap = new HashMap<>();
//...
HashBiMap.create(capitalCountryBiMap);

```

-   如果你要处理Enum 类型的键，请使用EnumHashBiMap类中的create方法：

```java
BiMap<MyEnum, String> operationStringBiMap = EnumHashBiMap.create(MyEnum.class);

```

-   如果你打算创建不可变映射，请使用ImmutableBiMap类(遵循构建器模式)：

```java
BiMap<String, String> capitalCountryBiMap
  = new ImmutableBiMap.Builder<>()
    .put("New Delhi", "India")
    .build();

```

## 4. 使用 BiMap

让我们从一个简单的例子开始展示BiMap 的用法，我们可以在其中根据值获取键，并根据键获取值：

```java
@Test
public void givenBiMap_whenQueryByValue_shouldReturnKey() {
    BiMap<String, String> capitalCountryBiMap = HashBiMap.create();
    capitalCountryBiMap.put("New Delhi", "India");
    capitalCountryBiMap.put("Washington, D.C.", "USA");
    capitalCountryBiMap.put("Moscow", "Russia");

    String keyFromBiMap = capitalCountryBiMap.inverse().get("Russia");
    String valueFromBiMap = capitalCountryBiMap.get("Washington, D.C.");
 
    assertEquals("Moscow", keyFromBiMap);
    assertEquals("USA", valueFromBiMap);
}
```

注意：上面的反向方法返回 BiMap 的反向视图，它将每个 BiMap 的值映射到它的关联键。

当我们尝试存储重复值两次时，BiMap会抛出IllegalArgumentException 。

让我们看一个相同的例子：

```java
@Test(expected = IllegalArgumentException.class)
public void givenBiMap_whenSameValueIsPresent_shouldThrowException() {
    BiMap<String, String> capitalCountryBiMap = HashBiMap.create();
    capitalCountryBiMap.put("Mumbai", "India");
    capitalCountryBiMap.put("Washington, D.C.", "USA");
    capitalCountryBiMap.put("Moscow", "Russia");
    capitalCountryBiMap.put("New Delhi", "India");
}

```

如果我们希望覆盖BiMap中已经存在的值，我们可以使用forcePut方法：

```java
@Test
public void givenSameValueIsPresent_whenForcePut_completesSuccessfully() {
    BiMap<String, String> capitalCountryBiMap = HashBiMap.create();
    capitalCountryBiMap.put("Mumbai", "India");
    capitalCountryBiMap.put("Washington, D.C.", "USA");
    capitalCountryBiMap.put("Moscow", "Russia");
    capitalCountryBiMap.forcePut("New Delhi", "India");

    assertEquals("USA", capitalCountryBiMap.get("Washington, D.C."));
    assertEquals("Washington, D.C.", capitalCountryBiMap.inverse().get("USA"));
}
```

## 5.总结

在这个简洁的教程中，我们举例说明了在 Guava 库中使用BiMap的例子。它主要用于根据地图中的值获取键。