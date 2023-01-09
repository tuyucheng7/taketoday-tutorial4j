## 1. 概述

ClassToInstanceMap <B>是一种特殊的映射，它将类与相应的实例相关联。它确保所有键和值都是上限类型B的子类型。

ClassToInstanceMap扩展了Java的Map接口并提供了两个额外的方法：T getInstance(Class<T>)和T putInstance(Class<T>, T)。该映射的优点是可以使用这两种方法来执行类型安全操作并避免强制转换。

在本教程中，我们将展示如何使用 Google Guava 的ClassToInstanceMap接口及其实现。

## 2. Google Guava 的ClassToInstanceMap

让我们看看如何使用该实现。

我们将从在pom.xml中添加 Google Guava 库依赖项开始：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")检查最新版本的依赖项。

ClassToInstanceMap接口有两种实现：可变的和不可变的。让我们分别看一下它们中的每一个。

## 3. 创建一个ImmutableClassToInstanceMap

我们可以通过多种方式创建ImmutableClassToInstanceMap的实例：

-   使用

    of()

    方法创建一个空地图：

    ```java
    ImmutableClassToInstanceMap.of()
    ```

-   使用

    of(Class<T> type, T value)

    方法创建单个条目映射：

    ```java
    ImmutableClassToInstanceMap.of(Save.class, new Save());
    ```

-   使用接受另一个地图作为参数的

    copyOf()方法。

    它将创建一个地图，其条目与作为参数提供的地图相同：

    ```java
    ImmutableClassToInstanceMap.copyOf(someMap)
    ```

-   使用生成器：

    ```java
    ImmutableClassToInstanceMap.<Action>builder()
      .put(Save.class, new Save())
      .put(Open.class, new Open())
      .put(Delete.class, new Delete())
      .build();
    ```

## 4. 创建一个MutableClassToInstanceMap

我们还可以创建MutableClassToInstanceMap的实例：

-   使用

    create()

    方法创建一个由

    HashMap

    支持的实例：

    ```java
    MutableClassToInstanceMap.create();
    ```

-   使用

    create(Map<Class<? extends B>, B> backingMap)

    使实例由提供的空映射支持：

    ```java
    MutableClassToInstanceMap.create(new HashMap());
    ```

## 5.用法

让我们看看如何使用添加到常规Map接口的两个新方法：

-   第一种方法是

    <T extends B> T getInstance(Class<T> type)

    ：

    ```java
    Action openAction = map.get(Open.class);
    Delete deleteAction = map.getInstance(Delete.class);
    ```

-   第二种方法是

    <T extends B> T putInstance(Class<T> type, @Nullable T value)

    ：

    ```java
    Action newOpen = map.put(Open.class, new Open());
    Delete newDelete = map.putInstance(Delete.class, new Delete());
    ```

## 六. 总结

在本快速教程中，我们展示了如何使用Guava 库中的ClassToInstanceMap的示例。