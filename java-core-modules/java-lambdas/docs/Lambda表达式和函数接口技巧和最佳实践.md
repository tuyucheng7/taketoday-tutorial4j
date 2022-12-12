## 1. 概述

现在Java 8已得到广泛使用，其一些主要功能的模式和最佳实践已经开始出现。在本教程中，我们将仔细研究函数式接口和lambda表达式。

## 2. 更倾向于标准的函数式接口

函数式接口集中在[java.util.function](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/function/package-summary.html)包中，满足了大多数开发者为lambda表达式和方法引用提供目标类型的需求。这些接口中的每一个都是泛型的和抽象的，这使得它们很容易适应几乎所有的lambda表达式。开发人员应该在创建新的函数式接口之前探索这个包。

让我们考虑一个接口Foo：

```java
@FunctionalInterface
public interface Foo {
    String method(String string);
}
```

此外，我们在某个类UseFoo中有一个方法add()，它将此接口作为参数：

```java
public String add(String string, Foo foo) {
    return foo.method(string);
}
```

要执行它，我们会写以下代码：

```java
Foo foo = parameter -> parameter + " from lambda";
String result = useFoo.add("Message ", foo);
```

如果我们仔细观察，我们会发现Foo只不过是一个接收一个参数并产生一个结果的函数。Java 8已经在[java.util.function包](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/function/package-summary.html)的[Function<T, R>](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/function/Function.html)中提供了这样的接口。

现在我们可以完全删除接口Foo并将我们的代码更改为：

```java
public String add(String string, Function<String, String> fn) {
    return fn.apply(string);
}
```

要执行这个方法，我们可以写：

```java
Function<String, String> fn = parameter -> parameter + " from lambda";
String result = useFoo.add("Message ", fn);
```

## 3. 使用@FunctionalInterface注解

现在让我们用[@FunctionalInterface](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/FunctionalInterface.html)标注我们的函数式接口。起初，这个注解似乎没有用，即使没有它，只要我们的接口只有一个抽象方法，它也将被视为函数式接口。

但是，想象一下我们有一个包含多个接口的大项目；很难手动控制一切。设计为函数性的接口可能会因添加另一个抽象方法而意外更改，从而使其无法用作函数式接口。

通过使用@FunctionalInterface注解，编译器将触发错误以响应任何破坏函数式接口的预定义结构的尝试。它也是一个非常方便的工具，可以使我们的应用程序架构更容易被其他开发人员理解。

因此，我们可以使用该注解标注之前的Foo接口：

```java
@FunctionalInterface
public interface Foo {
    String method();
}
```

而不仅仅是：

```java
public interface Foo {
    String method();
}
```

## 4. 不要在函数式接口中过度使用默认方法

我们可以轻松地将默认方法添加到函数式接中，只要只有一个抽象方法声明，这对于函数式接口契约来说是可以接受的：

```java
@FunctionalInterface
public interface Foo {
    String method(String string);
    default void defaultMethod() {}
}
```

如果函数式接口的抽象方法具有相同的签名，则函数式接口可以由其他函数式接口扩展：

```java
@FunctionalInterface
public interface FooExtended extends Baz, Bar {}
	
@FunctionalInterface
public interface Baz {	
    String method(String string);	
    default String defaultBaz() {}		
}
	
@FunctionalInterface
public interface Bar {	
    String method(String string);	
    default String defaultBar() {}	
}
```

**与常规接口一样，使用相同的默认方法扩展不同的函数式接口可能会出现问题**。

例如，让我们将defaultCommon()方法添加到Bar和Baz接口：

```java
@FunctionalInterface
public interface Baz {
    String method(String string);
    default String defaultBaz() {}
    default String defaultCommon(){}
}

@FunctionalInterface
public interface Bar {
    String method(String string);
    default String defaultBar() {}
    default String defaultCommon() {}
}
```

在这种情况下，我们会得到一个编译时错误：

```shell
interface FooExtended inherits unrelated defaults for defaultCommon() from types Baz and Bar...
```

要解决此问题，应在FooExtended接口中重写defaultCommon()方法。我们可以提供此方法的自定义实现；但是，**我们也可以重用父接口的实现**：

```java
@FunctionalInterface
public interface FooExtended extends Baz, Bar {
    @Override
    default String defaultCommon() {
        return Bar.super.defaultCommon();
    }
}
```

重要的是要注意我们必须小心：**向接口添加太多默认方法并不是一个很好的架构决策**。这应该被认为是一种妥协方案，仅在需要升级现有接口而不破坏向后兼容性时才使用。

## 5. 使用Lambda表达式实例化函数式接口

编译器允许我们使用内部类来实例化函数式接口；然而，这可能会导致非常冗长的代码，我们应该更倾向于使用lambda表达式：

```java
Foo foo = parameter -> parameter + " from Foo";
```

使用内部类的形式为：

```java
Foo fooByIC = new Foo() {
    @Override
    public String method(String string) {
        return string + " from Foo";
    }
};
```

**lambda表达式方法可用于旧库中的任何合适的接口，它可用于Runnable、Comparator等接口；但是，这并不意味着我们应该审查整个旧代码库并更改所有内容**。

## 6. 避免重载以函数式接口为参数的方法

我们应该使用不同名称的方法来避免冲突：

```java
public interface Processor {
    String process(Callable<String> c) throws Exception;
    String process(Supplier<String> s);
}

public class ProcessorImpl implements Processor {
    @Override
    public String process(Callable<String> c) throws Exception {
        // implementation details
    }

    @Override
    public String process(Supplier<String> s) {
        // implementation details
    }
}
```

乍一看，这似乎是合理的，但是任何执行ProcessorImpl方法的尝试：

```java
String result = processor.process(() -> "abc");
```

以错误结束并显示以下消息：

```shell
reference to process is ambiguous
both method process(java.util.concurrent.Callable<java.lang.String>) 
in com.baeldung.java8.lambda.tips.ProcessorImpl 
and method process(java.util.function.Supplier<java.lang.String>) 
in com.baeldung.java8.lambda.tips.ProcessorImpl match
```

为了解决这个问题，我们有两个选择。**第一个选项是使用不同名称的方法**：

```java
String processWithCallable(Callable<String> c) throws Exception;

String processWithSupplier(Supplier<String> s);
```

**第二种选择是手动执行转换**，这不是首选：

```java
String result = processor.process((Supplier<String>) () -> "abc");
```

## 7. 不要将Lambda表达式视为内部类

尽管在我们之前的示例中，我们基本上用lambda表达式替换了内部类，但这两个概念在一个重要方面是不同的：作用域。

当我们使用内部类时，它会创建一个新的作用域。我们可以通过实例化具有相同名称的新局部变量来隐藏封闭作用域中的局部变量，我们还可以在内部类中使用关键字this作为对其实例的引用。

然而，Lambda表达式使用封闭作用域，我们不能在lambda体内的封闭范围内隐藏变量。在这种情况下，关键字this是对封闭实例的引用。

例如，在类UseFoo中，我们有一个实例变量value：

```java
private String value = "Enclosing scope value";
```

然后在这个类的某个方法中，放置如下代码并执行这个方法：

```java
public String scopeExperiment() {
    Foo fooIC = new Foo() {
        String value = "Inner class value";

        @Override
        public String method(String string) {
            return this.value;
        }
    };
    String resultIC = fooIC.method("");

    Foo fooLambda = parameter -> {
        String value = "Lambda value";
        return this.value;
    };
    String resultLambda = fooLambda.method("");

    return "Results: resultIC = " + resultIC + ", resultLambda = " + resultLambda;
}
```

如果我们执行scopeExperiment()方法，我们将得到以下结果：“Results: resultIC = Inner class value, resultLambda = Enclosing scope value”。

如我们所见，通过在IC中调用this.value，我们可以从其实例访问局部变量。在lambda的情况下，this.value调用使我们能够访问在UseFoo类中定义的变量value，但不能访问在lambda主体内定义的变量value。

## 8. 保持Lambda表达式简短且不言自明

如果可能的话，我们应该使用一行结构而不是一大块代码。**请记住，lambda应该是一个表达式，而不是一个叙述。尽管语法简洁，但lambda应该具体表达它们提供的功能**。

这主要是风格上的建议，因为性能不会发生巨大变化。然而，一般来说，理解和使用这样的代码要容易得多。

### 8.1 避免在Lambda的主体中使用代码块

在理想情况下，lambda应该写在一行代码中。通过这种方法，lambda是一个不言自明的结构，它声明了应该用什么数据执行什么操作(在带有参数的lambda的情况下)。

如果我们有一大块代码，lambda的功能就不会立即清晰。考虑到这一点，请执行以下操作：

```java
Foo foo = parameter -> buildString(parameter);
private String buildString(String parameter) {
    String result = "Something " + parameter;
    // many lines of code
    return result;
}
```

代替：

```java
Foo foo = parameter -> { 
	String result = "Something " + parameter; 
    // many lines of code 
    return result; 
};
```

**重要的是要注意，我们不应该将这个“单行lambda”规则用作教条**。如果我们在lambda的定义中有两三行，那么将该代码提取到另一个方法中可能没有价值。

### 8.2 避免指定参数类型

在大多数情况下，编译器能够借助[类型推断](https://docs.oracle.com/javase/tutorial/java/generics/genTypeInference.html)来解析lambda参数的类型。因此，向参数添加类型是可选的，可以省略。

我们可以这样做：

```java
(a, b) -> a.toLowerCase() + b.toLowerCase();
```

而不是这样：

```java
(String a, String b) -> a.toLowerCase() + b.toLowerCase();
```

### 8.3 避免在单个参数两边加上括号

Lambda语法只需要在多个参数两边加上括号，或者当根本没有参数时。这就是为什么让我们的代码更短一点并且在只有一个参数时排除括号是安全的原因。

所以我们可以这样做：

```java
a -> a.toLowerCase();
```

而不是：

```java
(a) -> a.toLowerCase();
```

### 8.4 避免返回语句和大括号

大括号和返回语句在单行lambda主体中是可选的，这意味着为了清晰和简洁可以省略它们。

我们可以这样做：

```java
a -> a.toLowerCase();
```

而不是：

```java
a -> {return a.toLowerCase()};
```

### 8.5 使用方法引用

很多时候，即使在我们之前的例子中，lambda表达式也只是调用已经在其他地方实现的方法。在这种情况下，使用另一个Java 8特性[方法引用](https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html)非常有用。

lambda表达式为：

```java
a -> a.toLowerCase();
```

我们可以将其替换为：

```java
String::toLowerCase;
```

这并不总是更简洁，但它使代码更具可读性。

## 9. 使用“有效最终”变量

**在lambda表达式中访问非final变量会导致编译时错误，但这并不意味着我们应该将每个目标变量标记为final**。

根据“[有效最终](https://docs.oracle.com/javase/tutorial/java/javaOO/localclasses.html)”的概念，只要变量只被赋值一次，编译器也会将变量视为最终变量。

在lambda中使用此类变量是安全的，因为编译器将控制它们的状态并在任何尝试更改它们后立即触发编译时错误。

例如，下面的代码将不会编译：

```java
public void method() {
    String localVariable = "Local";
    Foo foo = parameter -> {
        String localVariable = parameter;
        return localVariable;
    };
}
```

编译器会通知我们：

```shell
Variable 'localVariable' is already defined in the scope.
```

这种方法应该简化使lambda执行线程安全的过程。

## 10. 保护对象变量不被修改

lambda的主要用途之一是用于并行计算，这意味着它们在线程安全方面非常有用。

“有效最终”范式在这里有很大帮助，但并非在所有情况下都如此。Lambda无法从封闭作用域更改对象的值。但是在可变对象变量的情况下，可以在lambda表达式中更改状态。

考虑以下代码：

```java
int[] total = new int[1];
Runnable r = () -> total[0]++;
r.run();
```

这段代码是合法的，因为total变量保持“有效最终”，但它引用的对象在执行lambda后是否会具有相同的状态？不！

请保留此示例作为提醒，以避免可能导致意外突变的代码。

## 11. 总结

在本文中，我们探讨了Java 8的lambda表达式和函数式接口中的一些最佳实践和陷阱。尽管这些新功能具有实用性和强大功能，但它们只是工具。每个开发人员在使用它们时都应该注意。