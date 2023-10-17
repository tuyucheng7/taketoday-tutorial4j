## 1. 概述

简单地说，在我们可以在JVM上使用一个对象之前，它必须被初始化。

在本教程中，我们将研究初始化基本类型和对象的各种方法。

## 2. 声明与初始化

让我们首先确保我们在同一页面上。

**声明是定义变量及其类型和名称的过程**。

在这里，我们声明了id变量：

```java
int id;
```

**另一方面，初始化就是分配一个值**：

```java
id = 1;
```

为了演示，我们将创建一个具有name和id属性的User类：

```java
public class User {
    private String name;
    private int id;

    // standard constructor, getters, setters,
}
```

接下来，我们将看到初始化的工作方式因我们正在初始化的字段类型而异。

## 3. 对象与原始类型

Java提供了两种类型的数据表示：原始类型和引用类型。在本节中，我们将讨论两者在初始化方面的差异。

Java有八种内置数据类型，称为Java原始类型；这种类型的变量直接保存它们的值。

引用类型保存对对象(类的实例)的引用。**与将其值保存在分配变量的内存中的基本类型不同，引用不保存它们引用的对象的值**。

相反，**引用通过存储对象所在的内存地址来指向对象**。

请注意，Java不允许我们发现物理内存地址是什么。相反，我们只能使用引用来引用对象。

让我们看一个从我们的User类中声明和初始化引用类型的示例：

```java
@Test
public void whenIntializedWithNew_thenInstanceIsNotNull() {
    User user = new User();
 
    assertThat(user).isNotNull();
}
```

正如我们所见，可以使用关键字new将引用分配给新对象，该关键字负责创建新的User对象。

让我们继续学习更多关于对象创建的知识。

## 4. 创建对象

与原始类型不同，对象的创建要复杂一些。这是因为我们不只是将值添加到该字段；相反，我们使用new关键字触发初始化。作为回报，这将调用构造函数并在内存中初始化对象。

让我们更详细地讨论构造函数和new关键字。

**new关键字负责通过构造函数为新对象分配内存**。

**构造函数通常用于初始化表示所创建对象的主要属性的实例变量**。

如果我们不显式提供构造函数，编译器将创建一个没有参数的默认构造函数，并且只为对象分配内存。

**一个类可以有多个构造函数，只要它们的参数列表不同(重载)即可**。每个不调用同一类中另一个构造函数的构造函数都会调用其父构造函数，无论它是显式编写的还是由编译器通过super()插入的。

让我们向User类添加一个构造函数：

```java
public User(String name, int id) {
    this.name = name;
    this.id = id;
}
```

现在我们可以使用我们的构造函数创建一个User对象，其属性具有初始值：

```java
User user = new User("Alice", 1);
```

## 5. 变量作用域

在接下来的部分中，我们将了解Java中变量可以存在的不同类型的作用域，以及这如何影响初始化过程。

### 5.1 实例和类变量

**实例和类变量不需要我们初始化它们**。一旦我们声明了这些变量，它们就会被赋予一个默认值：

<img src="../assets/img.png">

现在让我们尝试定义一些与实例和类相关的变量，并测试它们是否具有默认值：

```java
@Test
public void whenValuesAreNotInitialized_thenUserNameAndIdReturnDefault() {
    User user = new User();
 
    assertThat(user.getName()).isNull();
    assertThat(user.getId() == 0);
}
```

### 5.2 局部变量

**局部变量必须在使用前初始化**，因为它们没有默认值，编译器不会让我们使用未初始化的值。

例如，以下代码会生成编译器错误：

```java
public void print(){
    int i;
    System.out.println(i);
}
```

## 6. final关键词

应用于字段的final关键字意味着该字段的值在初始化后不能再更改。这样，我们就可以在Java中定义常量了。

让我们向User类添加一个常量：

```java
private static final int YEAR = 2000;
```

常量必须在声明时或在构造函数中初始化。

## 7. Java中的初始化器

在Java中，**初始化器是一段没有关联名称或数据类型的代码块**，它位于任何方法、构造函数或其他代码块之外。

Java提供两种类型的初始化器，静态初始化器和实例初始化器。让我们看看我们如何使用它们中的每一个。

### 7.1 实例初始化器

我们可以使用这些来初始化实例变量。

为了演示，我们将在我们的User类中使用实例初始化器为用户ID提供一个值：

```java
{
    id = 0;
}
```

### 7.2 静态初始化器

静态初始化器或静态块是用于初始化静态字段的代码块。换句话说，它是一个标有关键字static的简单初始化程序：

```java
private static String forum;
static {
    forum = "Java";
}
```

## 8. 初始化顺序

在编写初始化不同类型字段的代码时，我们必须注意初始化的顺序。

在Java中，初始化语句的顺序如下：

-   静态变量和静态初始化器顺序
-   实例变量和实例初始化器顺序
-   构造函数

## 9. 对象生命周期

现在我们已经学习了如何声明和初始化对象，让我们看看对象在不使用时会发生什么。

与其他我们必须担心对象销毁的语言不同，Java通过其垃圾收集器处理过时的对象。

**Java中的所有对象都存储在我们程序的堆内存中**。事实上，堆代表为我们的Java应用程序分配的大量未使用内存。

另一方面，**垃圾收集器是一个Java程序，它通过删除不再可达(访问)的对象来负责自动内存管理**。

对于一个Java对象变得不可访问，它必须遇到以下情况之一：

-   该对象不再有任何指向它的引用。
-   所有指向该对象的引用都超出了范围。

总之，一个对象首先是从一个类创建的，通常使用关键字new。然后这个对象就开始了它的生命，并为我们提供了访问它的方法和字段的权限。

最后，当不再需要它时，垃圾收集器将其销毁。

## 10. 其他创建对象的方法

在本节中，我们将简要**了解除new关键字之外的用于创建对象的方法，并学习如何应用它们，特别是反射、克隆和序列化**。

**反射是一种我们可以用来在运行时检查类、字段和方法的机制**。下面是使用反射创建User对象的示例：

```java
@Test
public void whenInitializedWithReflection_thenInstanceIsNotNull() throws Exception {
    User user = User.class.getConstructor(String.class, int.class)
        .newInstance("Alice", 2);
 
    assertThat(user).isNotNull();
}
```

在这种情况下，我们使用反射来查找和调用User类的构造函数。

**下一种方法是克隆，这是一种创建对象的精确副本的方法**。为此，我们的User类必须实现Cloneable接口：

```java
public class User implements Cloneable { //... }
```

现在我们可以使用clone()方法创建一个新的clonedUser对象，它具有与user对象相同的属性值：

```java
@Test
public void whenCopiedWithClone_thenExactMatchIsCreated() throws CloneNotSupportedException {
    User user = new User("Alice", 3);
    User clonedUser = (User) user.clone();
 
    assertThat(clonedUser).isEqualTo(user);
}
```

**我们还可以使用[sun.misc.Unsafe](https://www.baeldung.com/java-unsafe)类为对象分配内存，而无需调用构造函数**：

```java
User u = (User) unsafeInstance.allocateInstance(User.class);
```

## 11. 总结

在本文中，我们介绍了Java中字段的初始化。然后我们研究了Java中的不同数据类型以及如何使用它们。我们还探讨了在Java中创建对象的几种方法。