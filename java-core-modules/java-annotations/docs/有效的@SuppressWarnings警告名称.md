## 1. 概述

在本教程中，我们将了解与[@SuppressWarnings](https://www.baeldung.com/java-suppresswarnings) Java注解一起使用的不同警告名称，它允许我们抑制编译器警告。这些警告名称允许我们抑制特定的警告。可用的警告名称将取决于我们的IDE或Java编译器。Eclipse IDE是本文的参考。

## 2. 警告名称

以下是@SuppressWarnings注解中可用的有效警告名称列表：

-   all：这是一种抑制所有警告的通配符
-   boxing：抑制与装箱/拆箱操作相关的警告
-   unused：抑制未使用代码的警告
-   cast：抑制与对象转换操作相关的警告
-   deprecation：抑制与弃用相关的警告，例如弃用的类或方法
-   restriction：抑制与使用不鼓励或禁止的引用相关的警告
-   dep-ann：抑制与不推荐使用的注解相关的警告
-   fallthrough ：抑制与switch语句中缺少break语句相关的警告
-   finally ：抑制与不返回的finally块相关的警告
-   hiding：抑制与隐藏变量的局部变量相关的警告
-   incomplete-switch ：抑制与switch语句中缺失条目相关的警告(枚举大小写)
-   nls：抑制与非nls字符串文字相关的警告
-   null ：抑制与null分析相关的警告
-   serial ：抑制与缺少的serialVersionUID字段相关的警告，该字段通常在Serializable类
-   static-access：抑制与不正确的静态变量访问相关的警告
-   synthetic-access：抑制与来自内部类的未优化访问相关的警告
-   unchecked：禁止与未经检查的操作相关的警告
-   unqualified-field-access：抑制与不合格字段访问相关的警告
-   javadoc：抑制与Javadoc相关的警告
-   rawtypes：抑制与使用原始类型相关的警告
-   resource：抑制与使用Closeable
-   super：超级调用的方法相关的警告
-   sync-override：在覆盖同步方法抑制由于缺少同步

## 3. 使用警告名称

本节将展示使用不同警告名称的示例。

### 3.1 @SuppressWarnings("unused")

在下面的示例中，警告名称抑制了方法中unusedVal的警告：

```java
@SuppressWarnings("unused")
void suppressUnusedWarning() {
    int usedVal = 5;
    int unusedVal = 10;  // no warning here
    List<Integer> list = new ArrayList<>();
    list.add(usedVal);
}
```

### 3.2 @SuppressWarnings("deprecated")

在下面的示例中，警告名称禁止使用@deprecated方法的警告：

```java
@SuppressWarnings("deprecated")
void suppressDeprecatedWarning() {
    ClassWithSuppressWarningsNames cls = new ClassWithSuppressWarningsNames();
    cls.deprecatedMethod(); // no warning here
}

@Deprecated
String deprecatedMethod() {
    return "deprecated method";
}
```

### 3.3 @SuppressWarnings("fallthrough")

在下面的示例中，警告名称抑制了缺少break语句的警告-我们将它们包含在此处并注解掉，以显示否则我们会在哪里收到警告：

```java
@SuppressWarnings("fallthrough")
String suppressFallthroughWarning() {
    int day = 5;
    switch (day) {
        case 5:
            return "This is day 5";
//          break; // no warning here
        case 10:
            return "This is day 10";
//          break; // no warning here   
        default:
            return "This default day";
    }
}
```

### 3.4 @SuppressWarnings("serial")

此警告名称位于类级别。在下面的示例中，警告名称抑制了Serializable类中缺少serialVersionUID(我们已将其注解掉)的警告：

```java
@SuppressWarnings("serial")
public class ClassWithSuppressWarningsNames implements Serializable {
//    private static final long serialVersionUID = -1166032307853492833L; // no warning even though this is commented
```

## 4. 组合多个警告名称

@SuppressWarnings注解需要一个String数组，因此我们可以组合多个警告名称：

```java
@SuppressWarnings({"serial", "unchecked"})
```

## 5. 总结

本文提供了一个有效的@SuppressWarnings警告名称列表。