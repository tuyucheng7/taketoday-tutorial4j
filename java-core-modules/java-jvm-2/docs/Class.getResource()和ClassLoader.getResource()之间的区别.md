## 1. 概述

在这个简短的教程中，我们将了解Class.getResource()和ClassLoader.getResource()方法之间的区别。

## 2. getResource() 方法

我们可以在Class或ClassLoader实例上使用getResource()方法来查找具有给定名称的资源。资源被认为是数据——例如，图像、文本、音频等。作为路径分隔符，我们应该始终使用斜杠(“/”)。

该方法返回用于读取资源的[URL](https://www.baeldung.com/java-url)对象，如果找不到资源或调用者没有检索资源的权限，则返回null值。

## 3.类.getResource()

现在，让我们看看如何使用[Class](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)实例获取资源。在使用Class对象定位资源时，我们可以传递绝对路径或相对路径。

搜索与给定类关联的资源的规则由该类的类加载器实现。

查找资源的过程将委托给类对象的类加载器。换句话说，在Class实例上定义的getResouce()方法最终将调用ClassLoader的getResouce()方法。

在委派之前，绝对资源名称将从给定的资源名称派生。创建绝对资源名称时，将使用以下算法：

-   如果资源名称以斜杠(“/”)开头，则表示资源名称是绝对的。绝对资源名称从其前导斜杠中清除，并在不进行任何修改的情况下传递给适当的ClassLoader方法以定位资源。
-   如果提供的资源名称不以斜杠开头，则该名称将被视为相对于类的包。相对名称首先转换为绝对名称，然后传递给ClassLoader方法。

首先，假设我们在com/bealdung/resource目录中定义了example.txt资源。此外，假设我们在com.baeldung.resource包中定义了类ClassGetResourceExample。

现在，我们可以使用绝对路径检索资源：

```java
void givenAbsoluteResourcePath_whenGetResource_thenReturnResource() {
    URL resourceAbsolutePath = ClassGetResourceExample.class
        .getResource("/com/baeldung/resource/example.txt");
    Assertions.assertNotNull(resourceAbsolutePath);
}
```

使用Class.getResource()时，绝对资源路径应以前导斜杠开头。

此外，由于我们的资源与我们的类在同一个包内，我们也可以使用相对路径检索它：

```java
void givenRelativeResourcePath_whenGetResource_thenReturnResource() {
    URL resourceRelativePath = ClassGetResourceExample.class.getResource("example.txt");
    Assertions.assertNotNull(resourceRelativePath);
}
```

但是，重要的是要提到，只有当资源定义在与类相同的包内时，我们才能使用相对路径获取资源。否则，我们将得到一个null作为值。

## 4.类加载器.getResource()

顾名思义，[ClassLoader](https://www.baeldung.com/java-classloaders)代表一个负责加载类的类。每个Class实例都包含对其ClassLoader的引用。

ClassLoader类使用委托模型来搜索类和资源。此外，ClassLoader类的每个实例都有一个关联的父类ClassLoader。

当被要求查找资源时，ClassLoader实例将首先将搜索委托给其父ClassLoader ，然后再尝试自行查找资源。

如果父ClassLoader不存在，则会搜索虚拟机内置ClassLoader的路径，称为引导类加载器。引导类加载器没有父类，但可以作为ClassLoader实例的父类。

或者，如果先前的搜索失败，该方法将调用findResource()方法来查找资源。

指定为输入的资源名称始终被认为是绝对的。重要的是要注意Java从类路径加载资源。

让我们使用绝对路径和ClassLoader实例获取资源：

```java
void givenAbsoluteResourcePath_whenGetResource_thenReturnResource() {
    URL resourceAbsolutePath = ClassLoaderGetResourceExample.class.getClassLoader()
        .getResource("com/baeldung/resource/example.txt");
    Assertions.assertNotNull(resourceAbsolutePath);
}
```

当我们调用ClassLoader.getResource()时，我们应该在定义绝对路径时省略前导斜线。

使用ClassLoader实例，我们无法使用相对路径获取资源：

```java
void givenRelativeResourcePath_whenGetResource_thenReturnNull() {
    URL resourceRelativePath = ClassLoaderGetResourceExample.class.getClassLoader()
        .getResource("example.txt");
    Assertions.assertNull(resourceRelativePath);
}
```

上面的测试表明该方法返回一个空值作为结果。

## 5.总结

这个简短的教程解释了从Class和ClassLoader实例调用getResource()方法之间的区别。综上所述，我们在使用Class实例调用方法时可以传递相对或绝对资源路径，但在调用ClassLoader方法时只能使用绝对路径。