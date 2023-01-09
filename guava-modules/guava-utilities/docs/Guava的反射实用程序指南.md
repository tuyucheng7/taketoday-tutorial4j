## 1. 概述

在本文中，我们将研究Guava 反射API——与标准Java反射 API 相比，它绝对更加通用。

我们将使用Guava在运行时捕获泛型类型，我们还将充分利用[Invokable](https://google.github.io/guava/releases/21.0/api/docs/com/google/common/reflect/Invokable.html)。

## 2. 在运行时捕获通用类型

在Java中，泛型是通过类型擦除实现的。这意味着泛型类型信息仅在编译时可用，在运行时不再可用。

例如，List<String>，有关泛型类型的信息[在运行时被擦除](https://docs.oracle.com/javase/tutorial/java/generics/erasure.html)。由于这个事实，在运行时传递通用类对象是不安全的。

我们最终可能会将具有不同泛型类型的两个列表分配给同一个引用，这显然不是一个好主意：

```java
List<String> stringList = Lists.newArrayList();
List<Integer> intList = Lists.newArrayList();

boolean result = stringList.getClass()
  .isAssignableFrom(intList.getClass());

assertTrue(result);
```

由于类型擦除，方法isAssignableFrom()无法知道列表的实际泛型类型。它基本上比较两种类型，这两种类型只是一个列表，没有任何关于实际类型的信息。

通过使用标准的Java反射 API，我们可以检测方法和类的泛型类型。如果我们有一个返回List<String>的方法，我们可以使用反射来获取该方法的返回类型——表示List<String>的[ParameterizedType](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/reflect/ParameterizedType.html)。

TypeToken类使用此解决方法来允许对泛型类型进行操作。我们可以使用TypeToken类来捕获泛型列表的实际类型，并检查它们是否真的可以被相同的引用引用：

```java
TypeToken<List<String>> stringListToken
  = new TypeToken<List<String>>() {};
TypeToken<List<Integer>> integerListToken
  = new TypeToken<List<Integer>>() {};
TypeToken<List<? extends Number>> numberTypeToken
  = new TypeToken<List<? extends Number>>() {};

assertFalse(stringListToken.isSubtypeOf(integerListToken));
assertFalse(numberTypeToken.isSubtypeOf(integerListToken));
assertTrue(integerListToken.isSubtypeOf(numberTypeToken));
```

只有integerListToken可以分配给nubmerTypeToken类型的引用，因为Integer类扩展了Number类。

## 3.使用TypeToken捕获复杂类型

假设我们想要创建一个泛型参数化类，并且我们想要在运行时获得关于泛型类型的信息。我们可以创建一个类，将TypeToken作为字段来捕获该信息：

```java
abstract class ParametrizedClass<T> {
    TypeToken<T> type = new TypeToken<T>(getClass()) {};
}
```

然后，在创建该类的实例时，泛型类型将在运行时可用：

```java
ParametrizedClass<String> parametrizedClass = new ParametrizedClass<String>() {};

assertEquals(parametrizedClass.type, TypeToken.of(String.class));
```

我们还可以创建一个具有多个泛型类型的复杂类型的TypeToken ，并在运行时检索有关每种类型的信息：

```java
TypeToken<Function<Integer, String>> funToken
  = new TypeToken<Function<Integer, String>>() {};

TypeToken<?> funResultToken = funToken
  .resolveType(Function.class.getTypeParameters()[1]);

assertEquals(funResultToken, TypeToken.of(String.class));
```

我们得到Function的实际返回类型，即String。我们甚至可以在地图中获取条目的类型：

```java
TypeToken<Map<String, Integer>> mapToken
  = new TypeToken<Map<String, Integer>>() {};

TypeToken<?> entrySetToken = mapToken
  .resolveType(Map.class.getMethod("entrySet")
  .getGenericReturnType());

assertEquals(
  entrySetToken,
  new TypeToken<Set<Map.Entry<String, Integer>>>() {});


```

这里我们使用Java标准库中的反射方法getMethod()来捕获方法的返回类型。

## 4.可调用

Invokable是[java.lang.reflect.Method](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/reflect/Method.html)和[java.lang.reflect.Constructor](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/reflect/Constructor.html)的流畅包装器。它在标准 Java反射API之上提供了一个更简单的API。假设我们有一个类有两个公共方法，其中一个是 final 方法：

```java
class CustomClass {
    public void somePublicMethod() {}

    public final void notOverridablePublicMethod() {}
}
```

现在让我们使用 Guava API 和Java标准反射API 检查somePublicMethod()：

```java
Method method = CustomClass.class.getMethod("somePublicMethod");
Invokable<CustomClass, ?> invokable 
  = new TypeToken<CustomClass>() {}
  .method(method);

boolean isPublicStandradJava = Modifier.isPublic(method.getModifiers());
boolean isPublicGuava = invokable.isPublic();

assertTrue(isPublicStandradJava);
assertTrue(isPublicGuava);
```

这两个变体之间没有太大区别，但是检查方法是否可重写在Java中确实是一项非常重要的任务。幸运的是，Invokable 类中的isOverridable ()方法使它变得更容易：

```java
Method method = CustomClass.class.getMethod("notOverridablePublicMethod");
Invokable<CustomClass, ?> invokable
 = new TypeToken<CustomClass>() {}.method(method);

boolean isOverridableStandardJava = (!(Modifier.isFinal(method.getModifiers()) 
  || Modifier.isPrivate(method.getModifiers())
  || Modifier.isStatic(method.getModifiers())
  || Modifier.isFinal(method.getDeclaringClass().getModifiers())));
boolean isOverridableFinalGauava = invokable.isOverridable();

assertFalse(isOverridableStandardJava);
assertFalse(isOverridableFinalGauava);
```

我们看到即使是这样一个简单的操作也需要使用标准反射API 进行大量检查。Invokable类将此隐藏在易于使用且非常简洁的 API 后面。

## 5.总结

在本文中，我们研究了 Guava 反射 API 并将其与标准Java进行了比较。我们看到了如何在运行时捕获泛型类型，以及Invokable类如何为使用反射的代码提供优雅且易于使用的 API。