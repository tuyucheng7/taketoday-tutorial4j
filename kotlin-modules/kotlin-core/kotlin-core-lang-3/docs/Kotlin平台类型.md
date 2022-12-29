## 1. 概述

在本教程中，我们将定义Kotlin的术语平台类型。之后，我们将展示如何处理平台类型。最后，我们将演示在Kotlin中支持可空性检查的Java注解。

## 2. 平台类型定义

我们先来解释一下什么是平台类型。简而言之，**平台类型是Java声明的类型**。Java语言中的对象可以为空，因此，Kotlin无法在编译时检查它们的可空性。此外，**Kotlin的编译器无法发出错误**；此外，**平台类型可能会在运行时导致错误**，因此，Kotlin的严格[空安全](https://www.baeldung.com/kotlin/null-safety)范式被打破了。

**平台类型的表示法定义为带有感叹号的类型**。例如，String!表示该对象可以为空，也可以不为空。

## 3. 平台类型的可空性检查

让我们看看如何处理平台类型的可空性，首先，让我们定义一个Client类：

```java
public class Client {
    private String name;

    public Client() {
    }

    public String getName() {
        return name;
    }
}
```

Client的name可以为空，此外，让我们测试一下调用getName方法时会发生什么：

```kotlin
@Test
fun givenNullable_whenCall_thenFail() {
    val client = Client()
    assertFailsWith(NullPointerException::class) {
        client.name.length
    }
}
```

首先，没有编译错误提醒我们name可以为空；此外，测试运行成功，它抛出NullPointerException，因为字段name等于null。

**默认情况下，Kotlin将平台类型视为不可为空的**，然而，我们可以通过定义可为空的引用类型来改变这一点：

```kotlin
@Test
fun givenNullable_whenCall_thenNotFail() {
    val client = Client()
    val name: String? = client.name
    assertThat(name?.length).isNull()
}
```

之后，**由于对name对象的**[安全调用](https://www.baeldung.com/kotlin/null-safety#safe-calls)，**我们处理了它的可空性**。

## 4. 支持可空性检查的Java注解

现在让我们看看如何为Kotlin提供Java的对象可空性，提供可空性信息的Java类型不是平台类型，因此，当我们在没有安全调用的情况下访问可为空的对象时，Kotlin的编译器会抛出错误。

支持的注解列表包括：

-   JetBrains：来自org.jetbrains.annotations包的@Nullable和@NotNull
-   JSpecify：org.jspecify.nullness
-   Android：com.android.annotations和android.support.annotations
-   JSR-305：javax.annotation
-   FindBugs：edu.umd.cs.findbugs.annotations
-   Eclipse：org.eclipse.jdt.annotation
-   Lombok：lombok.NonNull
-   RxJava 3：io.reactivex.rxjava3.annotations

此外，[JvmAnnotationNames](https://github.com/JetBrains/kotlin/blob/master/core/compiler.common.jvm/src/org/jetbrains/kotlin/load/java/JvmAnnotationNames.kt)类包含支持的注解的完整列表。此外，我们可以指定编译器必须报告哪些注解以及如何报告，编译器选项-Xnullability-annotations=@ <package-name\>:<report-level\>定义注解包和报告级别。

级别可能是以下级别之一：

-   ignore：忽略可空性不匹配
-   warn：警告可空性不匹配
-   strict：在可空性不匹配时抛出错误

现在让我们通过向公开的getter添加@Nullable注解来修改Client类：

```java
@Nullable
public String getName() {
    return name;
}
```

之后，我们将无法运行测试givenNullable_whenCall_thenFail，编译器抛出异常并显示错误消息：

```shell
Only safe (?.) or non-null asserted (!!.) calls are allowed on a nullable receiver of type String?
```

多亏了注解，Kotlin的编译器知道getName可能会返回null。

## 5. 总结

在这篇简短的文章中，我们解释了Kotlin中的平台类型，此外，我们还展示了如何处理平台类型的可空性。最后，我们回顾了支持Kotlin中可空性检查的注解。