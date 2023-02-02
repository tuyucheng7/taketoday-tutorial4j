## 1. 概述

Java 15引入了很多[特性](https://www.baeldung.com/java-15-new)，在本文中，我们将讨论[JEP-371](https://openjdk.java.net/jeps/371)下称为隐藏类的新功能之一。该特性是作为[Unsafe API](https://www.baeldung.com/java-unsafe)的替代方式引入的，不推荐在JDK之外使用。

隐藏类特性对于任何使用动态字节码或JVM语言的人都很有帮助。

## 2. 什么是隐藏类？

动态生成的类为低延迟应用程序提供了效率和灵活性，它们只在有限的时间内需要。在静态生成的类的生命周期内保留它们会增加内存占用，针对这种情况的现有解决方案，例如按类加载器，既麻烦又低效。

从Java 15开始，隐藏类已经成为生成动态类的标准方式。

**隐藏类是字节码或其他类不能直接使用的类**。尽管它被称为一个类，但它应该被理解为一个隐藏类或接口。也可以定义为[访问控制嵌套](https://openjdk.org/jeps/181)的成员，并且可以独立于其他类卸载。

## 3. 隐藏类的属性

让我们看一下这些动态生成的类的属性：

-   不可发现：隐藏类在字节码链接期间无法被JVM发现，也不能被显式使用类加载器的程序发现。反射方法Class::forName、ClassLoader::findLoadedClass和Lookup::findClass将找不到它们。
-   我们不能将隐藏类用作超类、字段类型、返回类型或参数类型。
-   隐藏类中的代码可以直接使用它，而不依赖于类对象。
-   无论其可访问标志如何，在隐藏类中声明的final字段都是不可修改的。
-   它使用不可发现的类扩展了访问控制嵌套。
-   它可能会被卸载，即使它的概念定义类加载器仍然可以访问。
-   默认情况下，堆栈跟踪不会显示隐藏类的方法或名称，但是，调整JVM选项可以显示它们。

## 4. 创建隐藏类

**隐藏类不是由任何类加载器创建的**，它具有与查找类相同的定义类加载器、运行时包和保护域。

首先，让我们创建一个Lookup对象：

```java
MethodHandles.Lookup lookup = MethodHandles.lookup();
```

Lookup::defineHiddenClass方法用于创建隐藏类，此方法接收一个字节数组。

为了简单起见，我们将定义一个名为HiddenClass的简单类，该类包含一个将给定字符串转换为大写的方法：

```java
public class HiddenClass {
    public String convertToUpperCase(String s) {
        return s.toUpperCase();
    }
}
```

让我们获取类的路径并将其加载到输入流中，然后使用IOUtils.toByteArray()将此类转换为字节：

```java
Class<?> clazz = HiddenClass.class;
String className = clazz.getName();
String classAsPath = className.replace('.', '/') + ".class";
InputStream stream = clazz.getClassLoader()
    .getResourceAsStream(classAsPath);
byte[] bytes = IOUtils.toByteArray();
```

最后，我们将这些构造的字节传递给Lookup::defineHiddenClass：

```java
Class<?> hiddenClass = lookup.defineHiddenClass(IOUtils.toByteArray(stream), true, ClassOption.NESTMATE).lookupClass();
```

第二个布尔参数true初始化类。第三个参数ClassOption.NESTMATE指定创建的隐藏类将作为nestmate添加到查找类中，以便它可以访问同一嵌套中所有类和接口的私有成员。

假设我们想将隐藏类与其类加载器ClassOption.STRONG强绑定，这意味着隐藏类只有在它的定义加载器不可访问时才能被卸载。

## 5. 使用隐藏类

隐藏类由在运行时生成class并通过反射间接使用它们的框架使用。

在上一节中，我们介绍了如何创建一个隐藏类。在本节中，我们将了解如何使用它并创建一个实例。

由于无法将从Lookup.defineHiddenClass获得的类强制转换为任何其他类对象，因此我们使用Object来存储隐藏类实例。如果我们希望转换隐藏类，我们可以定义一个接口并创建一个实现该接口的隐藏类：

```java
Object hiddenClassObject = hiddenClass.getConstructor().newInstance();
```

现在，让我们从隐藏类中获取方法。获取该方法后，我们将作为任何其他标准方法调用它：

```java
Method method = hiddenClassObject.getClass()
    .getDeclaredMethod("convertToUpperCase", String.class);
Assertions.assertEquals("HELLO", method.invoke(hiddenClassObject, "Hello"));
```

现在，我们可以通过调用它的一些方法来验证隐藏类的一些属性：

对于此类，方法isHidden()将返回true：

```java
Assertions.assertEquals(true, hiddenClass.isHidden());
```

此外，由于隐藏类没有实际名称，因此其规范名称将为null：

```java
Assertions.assertEquals(null, hiddenClass.getCanonicalName());
```

隐藏类将具有与执行查找的类相同的定义加载器。由于查找发生在同一个类中，因此以下断言将成功：

```java
Assertions.assertEquals(this.getClass().getClassLoader(), hiddenClass.getClassLoader());
```

如果我们尝试通过任何方法访问隐藏类，它们将抛出ClassNotFoundException。这是显而易见的，因为隐藏类名非常不寻常，不符合条件，其他类无法看到。让我们检查几个断言来证明隐藏类是不可发现的：

```java
Assertions.assertThrows(ClassNotFoundException.class, () -> Class.forName(hiddenClass.getName()));
Assertions.assertThrows(ClassNotFoundException.class, () -> lookup.findClass(hiddenClass.getName()));
```

请注意，其他类可以使用隐藏类的唯一方法是通过其Class对象。

## 6. 匿名类与隐藏类

我们在前面的部分中创建了一个隐藏类并使用了它的一些属性。现在，我们详细说明匿名类(没有显式名称的内部类)和隐藏类之间的区别：

-   匿名类有一个动态生成的名称，中间有一个$符号，而一个从cn.tuyucheng.taketoday.reflection.hiddenclass.HiddenClass派生的隐藏类将是cn.tuyucheng.taketoday.reflection.hiddenclass.HiddenClass/1234。
-   匿名类是使用不推荐使用的Unsafe::defineAnonymousClass实例化，而Lookup::defineHiddenClass实例化隐藏类。
-   隐藏类不支持常量池修补。它有助于定义匿名类，其常量池条目已解析为具体值。
-   与隐藏类不同，匿名类可以访问宿主类的受保护成员，即使它位于不同的包中而不是子类中。
-   匿名类可以包含其他类以访问其成员，但隐藏类不能包含其他类。

虽然**隐藏类不是匿名类的替代品**，但它们正在替代JDK中匿名类的一些用法。**从Java 15开始，lambda表达式使用隐藏类**。

## 7. 总结

在本文中，我们详细讨论了一种称为隐藏类的新语言功能。