## 1. 概述

简单地说，[ByteBuddy](http://bytebuddy.net/#/)是一个用于在运行时动态生成Java类的库。

在这篇切中要点的文章中，我们将使用该框架来操作现有类、按需创建新类，甚至拦截方法调用。

## 2.依赖关系

让我们首先将依赖项添加到我们的项目中。对于基于 Maven 的项目，我们需要将此依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>net.bytebuddy</groupId>
    <artifactId>byte-buddy</artifactId>
    <version>1.12.13</version>
</dependency>
```

对于基于 Gradle 的项目，我们需要将相同的工件添加到我们的build.gradle文件中：

```groovy
compile net.bytebuddy:byte-buddy:1.12.13
```

最新版本可以在[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"net.bytebuddy" AND a%3A"byte-buddy")上找到。

## 3. 在运行时创建Java类

让我们首先通过对现有类进行子类化来创建动态类。我们将看看经典的Hello World项目。

在此示例中，我们创建了一个类型 ( Class )，它是Object.class的子类并覆盖了toString()方法：

```java
DynamicType.Unloaded unloadedType = new ByteBuddy()
  .subclass(Object.class)
  .method(ElementMatchers.isToString())
  .intercept(FixedValue.value("Hello World ByteBuddy!"))
  .make();
```

我们刚才做的是创建一个ByteBuddy 的实例。然后，我们使用subclass() API来扩展Object.class，并使用ElementMatchers选择超类 ( Object.class ) 的toString()。

最后，通过intercept()方法，我们提供了toString()的实现并返回一个固定值。

make()方法触发新类的生成。

此时，我们的类已经创建但尚未加载到 JVM 中。它由DynamicType.Unloaded的实例表示，它是生成类型的二进制形式。

因此，我们需要将生成的类加载到JVM中才能使用：

```java
Class<?> dynamicType = unloadedType.load(getClass()
  .getClassLoader())
  .getLoaded();
```

现在，我们可以实例化dynamicType并对其调用toString()方法：

```java
assertEquals(
  dynamicType.newInstance().toString(), "Hello World ByteBuddy!");
```

请注意，调用dynamicType.toString()将不起作用，因为它只会调用ByteBuddy.class的toString()实现。

newInstance()是一个Java反射方法，它创建一个由该ByteBuddy对象表示的类型的新实例；以类似于将new关键字与无参数构造函数一起使用的方式。

到目前为止，我们只能覆盖动态类型超类中的一个方法并返回我们自己的固定值。在接下来的部分中，我们将着眼于使用自定义逻辑定义我们的方法。

## 4. 方法委托和自定义逻辑

在我们前面的示例中，我们从toString()方法返回一个固定值。

实际上，应用程序需要比这更复杂的逻辑。为动态类型提供和提供自定义逻辑的一种有效方法是委托方法调用。

让我们创建一个动态类型，它是Foo.class的子类，它具有sayHelloFoo()方法：

```java
public String sayHelloFoo() { 
    return "Hello in Foo!"; 
}
```

此外，让我们创建另一个Bar类，它具有与sayHelloFoo()相同的签名和返回类型的静态sayHelloBar ()：

```java
public static String sayHelloBar() { 
    return "Holla in Bar!"; 
}
```

现在，让我们使用ByteBuddy的 DSL将 sayHelloFoo( )的所有调用委托给sayHelloBar() 。这允许我们在运行时向我们新创建的类提供用纯Java编写的自定义逻辑：

```java
String r = new ByteBuddy()
  .subclass(Foo.class)
  .method(named("sayHelloFoo")
    .and(isDeclaredBy(Foo.class)
    .and(returns(String.class))))        
  .intercept(MethodDelegation.to(Bar.class))
  .make()
  .load(getClass().getClassLoader())
  .getLoaded()
  .newInstance()
  .sayHelloFoo();
        
assertEquals(r, Bar.sayHelloBar());
```

调用sayHelloFoo()将相应地调用sayHelloBar()。

ByteBuddy如何知道要调用Bar.class中的哪个方法？它根据方法签名、返回类型、方法名称和注解来选择一个匹配的方法。

sayHelloFoo()和sayHelloBar()方法的名称不同，但它们具有相同的方法签名和返回类型。

如果 Bar.class 中有多个具有匹配签名和返回类型的可调用方法，我们可以使用@BindingPriority注解来解决歧义。

@BindingPriority接受一个整数参数——整数值越高，调用特定实现的优先级越高。因此，在下面的代码片段中， sayHelloBar()将优于sayBar() ：

```java
@BindingPriority(3)
public static String sayHelloBar() { 
    return "Holla in Bar!"; 
}

@BindingPriority(2)
public static String sayBar() { 
    return "bar"; 
}
```

## 5.方法和字段定义

我们已经能够覆盖在动态类型的超类中声明的方法。让我们更进一步，向我们的类添加一个新方法(和一个字段)。

我们将使用Java反射来调用动态创建的方法：

```java
Class<?> type = new ByteBuddy()
  .subclass(Object.class)
  .name("MyClassName")
  .defineMethod("custom", String.class, Modifier.PUBLIC)
  .intercept(MethodDelegation.to(Bar.class))
  .defineField("x", String.class, Modifier.PUBLIC)
  .make()
  .load(
    getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
  .getLoaded();

Method m = type.getDeclaredMethod("custom", null);
assertEquals(m.invoke(type.newInstance()), Bar.sayHelloBar());
assertNotNull(type.getDeclaredField("x"));
```

我们创建了一个名为MyClassName的类，它是Object.class的子类。然后我们定义一个方法custom，它返回一个String并有一个public访问修饰符。

就像我们在前面的示例中所做的那样，我们通过拦截对它的调用并将它们委托给我们在本教程前面创建的Bar.class来实现我们的方法。

## 6. 重新定义现有类

虽然我们一直在使用动态创建的类，但我们也可以使用已经加载的类。这可以通过重新定义(或变基)现有类并使用ByteBuddyAgent将它们重新加载到 JVM 中来完成。

首先，让我们将ByteBuddyAgent添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>net.bytebuddy</groupId>
    <artifactId>byte-buddy-agent</artifactId>
    <version>1.7.1</version>
</dependency>
```

最新版本可以在[这里找到](https://search.maven.org/classic/#search|gav|1|g%3A"net.bytebuddy" AND a%3A"byte-buddy-agent")。

现在，让我们重新定义之前在Foo.class中创建的sayHelloFoo()方法：

```java
ByteBuddyAgent.install();
new ByteBuddy()
  .redefine(Foo.class)
  .method(named("sayHelloFoo"))
  .intercept(FixedValue.value("Hello Foo Redefined"))
  .make()
  .load(
    Foo.class.getClassLoader(), 
    ClassReloadingStrategy.fromInstalledAgent());
  
Foo f = new Foo();
 
assertEquals(f.sayHelloFoo(), "Hello Foo Redefined");
```

## 七. 总结

在这份详尽的指南中，我们深入研究了ByteBuddy库的功能以及如何使用它来有效地创建动态类。

它的[文档](http://bytebuddy.net/#/tutorial)提供了对内部工作原理和库其他方面的深入解释。