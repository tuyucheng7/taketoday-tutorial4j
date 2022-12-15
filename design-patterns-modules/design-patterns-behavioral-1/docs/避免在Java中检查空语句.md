## 1. 概述

通常，空变量、引用和集合在Java代码中很难处理，它们不仅难以识别，而且处理起来也很复杂。

事实上，在处理null时的任何错误都无法在编译时识别出来，并会在运行时导致NullPointerException。

在本教程中，我们介绍在Java中检查空值的必要性以及帮助我们避免在代码中进行空值检查的各种替代方法。

## 2. 什么是空指针异常？

根据[NullPointerException的Javadoc](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/NullPointerException.html)，当应用程序在需要对象的情况下尝试使用null时会抛出它，例如：

-   调用空对象的实例方法
-   访问或修改空对象的字段
-   将null的长度当作一个数组
-   像访问数组一样访问或修改null的槽
-   将null当作一个Throwable值来抛出

让我们快速查看导致此异常的Java代码的几个示例：

```java
public void doSomething() {
    String result = doSomethingElse();
    if (result.equalsIgnoreCase("Success")) 
        // success
    }
}

private String doSomethingElse() {
    return null;
}
```

在这里，**我们尝试调用一个空引用的方法调用，这将导致NullPointerException**。

另一个常见的例子是，如果我们尝试访问一个空数组：

```java
public static void main(String[] args) {
    findMax(null);
}

private static void findMax(int[] arr) {
    int max = arr[0];
    //check other elements in loop
}
```

这会在第6行导致NullPointerException。因此，从上面的示例中可以看出，访问null对象的任何字段、方法或索引都会导致NullPointerException。

避免NullPointerException的常见方法是检查null值：

```java
public void doSomething() {
    String result = doSomethingElse();
    if (result != null && result.equalsIgnoreCase("Success")) {
        // success
    }
    else
        // failure
}

private String doSomethingElse() {
    return null;
}
```

**在现实世界中，程序员发现很难识别哪些对象可以为空，一种非常安全的策略可能是检查每个对象的空性。然而，这会导致大量冗余的null检查并降低我们的代码的可读性**。

在接下来的几节中，我们介绍Java中避免此类冗余的一些替代方法。

## 3. 通过API契约处理null

如上一节所述，访问null对象的方法或变量会导致NullPointerException，我们还讨论了在访问对象之前对其进行空检查可以消除NullPointerException的可能性。

但是，通常有一些API可以处理空值：

```java
public void print(Object param) {
    System.out.println("Printing " + param);
}

public Object process() throws Exception {
    Object result = doSomething();
    if (result == null) {
        throw new Exception("Processing fail. Got a null response");
    } else {
        return result;
    }
}
```

print()方法调用只会打印“null”但不会抛出异常。同样，process()永远不会在其响应中返回null，而是抛出一个Exception。

因此，对于访问上述API的客户端代码，不需要进行空检查。

但是，此类API需要在其契约中明确说明，**API发布此类契约的常见位置是Javadoc**。

**但这并没有明确指明API契约，因此依赖于客户端代码开发人员来确保其合规性**。

在下一节中，我们将看到一些IDE和其他开发工具如何帮助开发人员实现这一点。

## 4. 自动化API契约

### 4.1 使用静态代码分析

[静态代码分析]()工具有助于极大地提高代码质量，一些这样的工具还允许开发人员维护空契约，其中一个例子是[FindBugs](https://www.baeldung.com/intro-to-findbugs)。

**FindBugs通过@Nullable和@NonNull注解帮助管理空契约**，我们可以在任何方法、字段、局部变量或参数上使用这些注解，这使得注解类型是否可以为null对客户端代码是明确的。

让我们看一个例子：

```java
public void accept(@NonNull Object param) {
    System.out.println(param.toString());
}
```

在这里，@NonNull明确表示参数不能为null，**如果客户端代码在不检查参数是否为null的情况下调用此方法，FindBugs将在编译时生成警告**。

### 4.2 使用IDE支持

开发人员通常依靠IDE来编写Java代码，智能代码完成和有用的警告等功能，例如当变量可能未分配时，肯定会有很大帮助。

一些IDE还允许开发人员管理API契约，从而消除了对静态代码分析工具的需求。**IntelliJ IDEA提供了@NonNull和@Nullable注解**。

要在IntelliJ中添加对这些注解的支持，我们需要添加以下Maven依赖项：

```xml
<dependency>
    <groupId>org.jetbrains</groupId>
    <artifactId>annotations</artifactId>
    <version>16.0.2</version>
</dependency>
```

现在，**如果缺少null检查，IntelliJ将生成警告**，如上一个示例所示。

IntelliJ还提供了一个[@Contract](https://www.jetbrains.com/help/idea/contract-annotations.html)注解来处理复杂的API契约。

## 5. 断言

到目前为止，我们只讨论了从客户端代码中删除对空检查的需要，但这在实际应用中很少适用。

现在**假设我们使用的API不能接收空参数或者可以返回必须由客户端处理的空响应，这表明我们需要检查参数或响应是否为空值**。

在这里，我们可以使用[Java断言]()来代替传统的空检查条件语句：

```java
public void accept(Object param){
    assert param != null;
    doSomething(param);
}
```

在第2行中，我们检查空参数。**如果启用断言，这将导致AssertionError**。

虽然这是断言非空参数等先决条件的好方法，但这种方法有两个主要问题：

1.  断言通常在JVM中被禁用
2.  错误的断言会导致无法恢复的未经检查的错误

**因此，不建议程序员使用断言来检查条件**。在以下部分中，我们将讨论处理空值验证的其他方法。

## 6. 通过编码实践避免空检查

### 6.1 先决条件

编写早期失败的代码通常是一个好习惯，因此，如果API接收不允许为null的多个参数，**则最好检查每个非null参数作为API的前提条件**。

让我们看一下两种方法-一种会提前失败，另一种不会：

```java
public void goodAccept(String one, String two, String three) {
    if (one == null || two == null || three == null) {
        throw new IllegalArgumentException();
    }

    process(one);
    process(two);
    process(three);
}

public void badAccept(String one, String two, String three) {
    if (one == null) {
        throw new IllegalArgumentException();
    } else {
        process(one);
    }

    if (two == null) {
        throw new IllegalArgumentException();
    } else {
        process(two);
    }

    if (three == null) {
        throw new IllegalArgumentException();
    } else {
        process(three);
    }
}
```

显然，我们应该更倾向于goodAccept()而不是badAccept()。

作为替代方案，我们也可以使用Guava的[Preconditions]()来验证API参数。

### 6.2 使用原始类型代替包装类

由于null对于像int这样的原始类型来说不是一个可接受的值，我们应该尽可能地使用它们而不是像Integer这样的包装对应物。

考虑对两个整数求和的方法的两种实现：

```java
public static int primitiveSum(int a, int b) {
    return a + b;
}

public static Integer wrapperSum(Integer a, Integer b) {
    return a + b;
}
```

现在让我们在客户端代码中调用这些API：

```java
int sum = primitiveSum(null, 2);
```

**这将导致编译时错误，因为null不是int的有效值**。

当使用带有包装器类的API时，我们得到一个NullPointerException：

```java
assertThrows(NullPointerException.class, () -> wrapperSum(null, 2));
```

正如我们在另一个教程[Java Primitives Versus Objects]()中介绍的那样，在包装器上使用原始类型还有其他因素。

### 6.3 空集合

有时，我们需要返回一个集合作为方法的响应，对于此类方法，我们应该始终尝试**返回一个空集合而不是null**：

```java
public List<String> names() {
    if (userExists()) {
        return Stream.of(readName()).collect(Collectors.toList());
    } else {
        return Collections.emptyList();
    }
}
```

这样，我们就避免了客户端在调用此方法时执行空检查的需要。

## 7. 使用Objects

Java 7引入了新的Objects API，这个API有几个静态工具方法，可以去掉很多冗余代码。

让我们看一个[requireNonNull()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Objects.html#requireNonNull(T))方法：

```java
public void accept(Object param) {
    Objects.requireNonNull(param);
    // doSomething()
}
```

现在我们测试accept()方法：

```java
assertThrows(NullPointerException.class, () -> accept(null));
```

因此，如果将null作为参数传递，accept()将抛出NullPointerException。

**该类还有isNull()和nonNull()方法，可用作谓词来检查对象是否为null**。

## 8. 使用Optional

### 8.1 使用orElseThrow

Java 8在该语言中引入了一个新的[Optional]() API。与null相比，这为处理可选值提供了更好的契约。

让我们看看Optional如何消除对空检查的需要：

```java
public Optional<Object> process(boolean processed) {
    String response = doSomething(processed);

    if (response == null) {
        return Optional.empty();
    }

    return Optional.of(response);
}

private String doSomething(boolean processed) {
    if (processed) {
        return "passed";
    } else {
        return null;
    }
}
```

如上所示，通过返回一个Optional，**process方法向调用方明确表示响应可以为空，需要在编译时处理**。

这明显消除了在客户端代码中进行任何空检查的需要，可以使用Optional API的声明式风格以不同方式处理空响应：

```java
assertThrows(Exception.class, () -> process(false).orElseThrow(() -> new Exception()));
```

此外，它还为API开发人员**提供了一个更好的契约，以向客户端表明API可以返回空响应**。

虽然我们消除了对该API的调用者进行空检查的需要，但我们使用它来返回空响应。

为了避免这种情况，**Optional提供了一个ofNullable方法，该方法返回一个具有指定值的Optional，如果值为null则返回空**：

```java
public Optional<Object> process(boolean processed) {
    String response = doSomething(processed);
    return Optional.ofNullable(response);
}
```

### 8.2 对集合使用Optional

在处理空集合时，Optional就可以派上用场了：

```java
public String findFirst() {
    return getList().stream()
        .findFirst()
        .orElse(DEFAULT_VALUE);
}
```

此函数应该返回集合的第一项，当没有数据时，Stream API的findFirst函数将返回一个空的Optional。在这里，我们使用orElse来提供默认值。

这允许我们处理空集合或集合，在我们使用Stream库的filter方法后，没有任何项目可提供。

或者，我们也可以通过从该方法返回Optional来让客户端决定如何处理空值：

```java
public Optional<String> findOptionalFirst() {
    return getList().stream()
        .findFirst();
}
```

**因此，如果getList的结果为空，则该方法将向客户端返回一个空的Optional**。

将Optional与集合一起使用允许我们设计一定会返回非空值的API，从而避免在客户端上进行显式空检查。

这里需要注意的是，这个实现依赖于getList不返回null。但是，正如我们在上一节中讨论的那样，返回空集合通常比返回null更好。

### 8.3 组合Optional

当我们开始让我们的函数返回Optional时，我们需要一种方法将它们的结果组合成一个值。

让我们以之前的getList为例，如果它要返回一个Optional集合，或者用一个使用ofNullable用Optional包装null的方法包装怎么办？

我们的findFirst方法想要返回Optional集合的第一个元素：

```java
public Optional<String> optionalListFirst() {
   return getOptionalList()
        .flatMap(list -> list.stream().findFirst());
}
```

通过对从getOptional返回的Optional使用flatMap函数，我们可以解压返回Optional的内部表达式的结果。如果没有flatMap，结果将是Optional<Optional<String>>，只有当Optional不为空时才会执行flatMap操作。

## 9. 第三方库

### 9.1 Lombok

[Lombok]()是一个很棒的库，可以减少我们项目中样板代码的数量。它带有一组注解，这些注解取代了我们经常在Java应用程序中自己编写的代码的公共部分，例如getter、setter和toString()等等。

它的另一个注解是@NonNull，因此，如果一个项目已经使用Lombok来消除样板代码，**@NonNull可以取代对空检查的需求**。

首先，我们需要为Lombok添加[Maven依赖项](https://search.maven.org/search?q=g:org.projectlombok AND a:lombok&core=gav)：

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.20</version>
</dependency>
```

现在我们可以在需要空检查的地方使用@NonNull：

```java
public void accept(@NonNull Object param){
    System.out.println(param);
}
```

所以，我们简单地标注了需要空检查的对象，Lombok生成编译后的类：

```java
public void accept(@NonNull Object param) {
    if (param == null) {
        throw new NullPointerException("param");
    } else {
        System.out.println(param);
    }
}
```

如果param为null，则此方法抛出NullPointerException。**该方法必须在其契约中明确说明这一点，并且客户端代码必须处理该异常**。

### 9.2 使用StringUtils

通常，字符串验证除了检查空值外，还包括检查空字符串值。

因此，这将是一个常见的验证语句：

```java
public void accept(String param){
    if (null != param && !param.isEmpty())
        System.out.println(param);
}
```

如果我们必须处理很多String类型，这很快就会变得多余，这就是StringUtils派上用场的地方。

首先我们为[commons-lang3](https://search.maven.org/search?q=g:org.apache.commons AND a:commons-lang3&core=gav)添加Maven依赖项：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

现在我们使用StringUtils重构上面的代码：

```java
public void accept(String param) {
    if (StringUtils.isNotEmpty(param))
        System.out.println(param);
}
```

因此，我们使用静态工具方法isNotEmpty()替换了null或empty检查，此API提供其他强大的[工具方法](https://www.baeldung.com/string-processing-commons-lang)来处理常见的字符串函数。

## 10. 总结

在本文中，我们介绍了NullPointerException的各种原因以及难以识别的原因，然后我们提出了各种方法来避免围绕使用参数、返回类型和其他变量检查null的代码冗余。