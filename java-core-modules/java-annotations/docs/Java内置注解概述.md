## 1. 概述

在本文中，我们将讨论Java语言的一个核心特性-JDK中可用的默认注解。

## 2. 什么是注解

简单地说，注解就是在Java类型前面加上一个“@”符号。

Java从1.5版本开始就有了注解。从那时起，他们塑造了我们设计应用程序的方式。

Spring和Hibernate是非常依赖注解来支持各种设计技术的框架示例。

基本上，注解将额外的元数据分配给它绑定的源代码。通过向方法、接口、类或字段添加注解，我们可以：

1.  通知编译器警告和错误
2.  在编译时操作源代码
3.  在运行时修改或检查行为

## 3. Java内置注解

现在我们已经回顾了基础知识，让我们看一下核心Java附带的一些注解。首先，有几个通知编译：

1.  @Override
2.  @SuppressWarnings
3.  @Deprecated
4.  @SafeVarargs
5.  @FunctionalInterface
6.  @Native

这些注解生成或抑制编译器警告和错误。始终如一地应用它们通常是一种很好的做法，因为添加它们可以防止未来的程序员错误。

[@Override](https://www.baeldung.com/java-override)注解用于指示方法覆盖或替换继承方法的行为。

[@SuppressWarnings](https://www.baeldung.com/java-suppresswarnings)表示我们要忽略部分代码中的某些警告。@SafeVarargs[注解还作用于与使用可变参数相关的](https://www.baeldung.com/java-safevarargs)一种警告。

[@Deprecated](https://www.baeldung.com/java-deprecated)注解可用于将API标记为不再使用。此外，此注解已在[Java 9](https://www.baeldung.com/java-deprecated#optional-attributes)中进行了改进，以表示有关弃用的更多信息。

对于所有这些，你可以在链接的文章中找到更详细的信息。

### 3.1 @FunctionalInterface

Java 8允许我们以更函数式的方式编写代码。

[单一抽象方法接口](https://www.baeldung.com/java-8-functional-interfaces)是其中的重要组成部分。如果我们打算让lambda使用SAM接口，我们可以选择使用@FunctionalInterface将其标记为这样：

```java
@FunctionalInterface
public interface Adder {
    int add(int a, int b);
}
```

与方法的@Override一样，@FunctionalInterface使用Adder声明我们的意图。

现在，无论我们是否使用@FunctionalInterface，我们仍然可以以相同的方式使用Adder：

```java
Adder adder = (a,b) -> a + b;
int result = adder.add(4,5);
```

但是，如果我们向Adder添加第二个方法，那么编译器会报错：

```java
@FunctionalInterface
public interface Adder { 
    // compiler complains that the interface is not a SAM
    
    int add(int a, int b);
    int div(int a, int b);
}
```

现在，这将在没有@FunctionalInterface注解的情况下编译。那么，它给了我们什么？

像@Override一样，这个注解可以保护我们免受未来程序员错误的影响。尽管在一个接口上使用多个方法是合法的，但当该接口被用作lambda目标时就不合法了。如果没有此注解，编译器将在Adder用作lambda的许多地方中断。现在，它只是破坏了Adder本身。

### 3.2 @Native

从Java 8开始，java.lang.annotation包中有一个名为[Native](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/annotation/Native.html)的新注解。@Native注解只适用于字段。它表示带注解的字段是一个常量，可以从本机代码中引用。例如，这是它在Integer类中的使用方式：

```java
public final class Integer {
    @Native public static final int MIN_VALUE = 0x80000000;
    // omitted
}
```

这个注解也可以作为工具生成一些辅助头文件的提示。

## 4. 元注解

接下来，元注解是可以应用于其他注解的注解。

例如，这些元注解用于注解配置：

1.  @Target
2.  @Retention
3.  @Inherited
4.  @Documented
5.  @Repeatable

### 4.1 @Target

注解的范围可以根据要求而变化。虽然一个注解仅用于方法，但另一个注解可以与构造函数和字段声明一起使用。

要确定自定义注解的目标元素，我们需要用@Target注解来标记它。

@Target可以处理[12种不同的元素类型](https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/lang/annotation/ElementType.html)。如果我们查看@SafeVarargs的源代码，那么我们可以看到它必须只附加到构造函数或方法：

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface SafeVarargs {
}
```

### 4.2 @Retention

一些注解旨在用作编译器的提示，而其他注解则在运行时使用。

我们使用@Retention注解来说明我们的注解在我们程序的生命周期中应用的位置。

为此，我们需要使用三种保留策略之一配置@Retention：

1.  RetentionPolicy.SOURCE：编译器和运行时都不可见
2.  RetentionPolicy.CLASS：编译器可见
3.  RetentionPolicy.RUNTIME：编译器和运行时可见

如果注解声明中没有@Retention注解，则[保留策略默认为RetentionPolicy.CLASS](https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/lang/annotation/Retention.html)。

如果我们有一个在运行时应该可以访问的注解：

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(TYPE)
public @interface RetentionAnnotation {
}
```

那么，如果我们给一个类添加一些注解：

```java
@RetentionAnnotation
@Generated("Available only on source code")
public class AnnotatedClass {
}
```

现在我们可以反思AnnotatedClass以查看保留了多少注解：

```java
@Test
public void whenAnnotationRetentionPolicyRuntime_shouldAccess() {
    AnnotatedClass anAnnotatedClass = new AnnotatedClass();
    Annotation[] annotations = anAnnotatedClass.getClass().getAnnotations();
    assertThat(annotations.length, is(1));
}
```

该值为1，因为@RetentionAnnotation具有RUNTIME的保留策略，而[@Generated](https://docs.oracle.com/en/java/javase/15/docs/api/java.compiler/javax/annotation/processing/Generated.html)没有。

### 4.3 @Inherited

在某些情况下，我们可能需要一个子类来将注解绑定到父类。

我们可以使用@Inherited注解使我们的注解从带注解的类传播到它的子类。

如果我们将@Inherited应用于我们的自定义注解，然后将其应用于BaseClass：

```java
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InheritedAnnotation {
}

@InheritedAnnotation
public class BaseClass {
}

public class DerivedClass extends BaseClass {
}
```

然后，在扩展BaseClass之后，我们应该看到DerivedClass在运行时似乎具有相同的注解：

```java
@Test
public void whenAnnotationInherited_thenShouldExist() {
    DerivedClass derivedClass = new DerivedClass();
    InheritedAnnotation annotation = derivedClass.getClass()
        .getAnnotation(InheritedAnnotation.class);
 
    assertThat(annotation, instanceOf(InheritedAnnotation.class));
}
```

如果没有@Inherited注解，上述测试将失败。

### 4.4 @Documented

默认情况下，Java不会在Javadocs中记录注解的用法。

但是，我们可以使用@Documented注解来改变Java的默认行为。

如果我们创建一个使用@Documented的自定义注解：

```java
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelCell {
    int value();
}
```

并且，将它应用于适当的Java元素：

```java
public class Employee {
    @ExcelCell(0)
    public String name;
}
```

然后，Employee Javadoc将显示注解用法：

![字段详细信息](https://www.baeldung.com/wp-content/uploads/2018/02/Screen-Shot-2018-02-08-at-1.38.45-PM-1-300x140.png)

### 4.5 @Repeatable

有时在给定的Java元素上多次指定相同的注解可能很有用。

在Java 7之前，我们必须将注解组合成一个容器注解：

```java
@Schedules({
    @Schedule(time = "15:05"),
    @Schedule(time = "23:00")
})
void scheduledAlarm() {
}
```

然而，Java 7带来了一种更简洁的方法。使用@Repeatable注解，我们可以使注解可重复：

```java
@Repeatable(Schedules.class)
public @interface Schedule {
    String time() default "09:00";
}
```

要使用@Repeatable，我们也需要有一个容器注解。在这种情况下，我们将重用@Schedules：

```java
public @interface Schedules {
    Schedule[] value();
}
```

当然，这看起来很像我们在Java 7之前所拥有的。但是，现在的价值是当我们需要重复@Schedule时不再指定包装器@Schedules：

```java
@Schedule
@Schedule(time = "15:05")
@Schedule(time = "23:00")
void scheduledAlarm() {
}
```

因为Java需要包装器注解，所以我们很容易从Java 7之前的注解列表迁移到可重复注解。

## 5. 总结

在本文中，我们讨论了每个Java开发人员都应该熟悉的Java内置注解。