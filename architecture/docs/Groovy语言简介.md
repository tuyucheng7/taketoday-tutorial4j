## 1. 概述

Groovy 是一种用于 JVM 的动态脚本语言。它编译为字节码并与 Java 代码和库无缝融合。

在本文中，我们将了解[Groovy](http://www.groovy-lang.org/)的一些基本特性，包括基本语法、控制结构和集合。

然后我们将研究使其成为一种有吸引力的语言的一些主要特性，包括 null 安全、隐式真值、运算符和字符串。

## 2. 环境

如果我们想在 Maven 项目中使用 Groovy，我们需要在 pom.xml 中添加以下内容：

```xml
<build>
    <plugins>
        // ...
        <plugin>
            <groupId>org.codehaus.gmavenplus</groupId>
            <artifactId>gmavenplus-plugin</artifactId>
            <version>1.5</version>
       </plugin>
   </plugins>
</build>
<dependencies>
    // ...
    <dependency>
        <groupId>org.codehaus.groovy</groupId>
        <artifactId>groovy-all</artifactId>
        <version>2.4.10</version>
    </dependency>
</dependencies>
```

最新的 Maven 插件可以[在这里](https://mvnrepository.com/artifact/org.codehaus.gmavenplus/gmavenplus-plugin)找到，最新版本的groovy-all可以 [在这里找到](https://mvnrepository.com/artifact/org.codehaus.groovy/groovy-all)。

## 三、基本特点

Groovy 中有许多有用的特性。现在，让我们看看该语言的基本构建块以及它与 Java 的不同之处。

现在，让我们看看该语言的基本构建块以及它与 Java 的不同之处。

### 3.1. 动态打字

Groovy 最重要的特性之一是它对动态类型的支持。

类型定义是可选的，实际类型在运行时确定。我们来看看这两个类：

```groovy
class Duck {
    String getName() {
        'Duck'
    }
}
class Cat {
    String getName() {
        'Cat'
    }
}
复制
```

这两个类定义了相同的getName方法，但并未在契约中明确定义。

现在，假设我们有一个对象列表，其中包含具有getName方法的鸭子和猫。使用 Groovy，我们可以执行以下操作：

```groovy
Duck duck = new Duck()
Cat cat = new Cat()

def list = [duck, cat]
list.each { obj ->
    println obj.getName()
}复制
```

代码将编译，上面代码的输出将是：

```groovy
Duck
Cat
```

3.2. 隐式真实转换

与在 JavaScript 中一样，如果需要，Groovy 会将每个对象计算为布尔值，例如，在if语句中使用它时或取反值时：

```groovy
if("hello") {...}
if(15) {...}
if(someObject) {...}
```

关于此转换，需要记住一些简单的规则：

-   非空集合、数组、映射评估为真
-   具有至少一个匹配项的匹配器评估为真
-   具有更多元素的迭代器和枚举被强制为真
-   非空字符串、GString和CharSequences被强制为真
-   非零数字被评估为真
-   非空对象引用被强制为真

如果我们想要自定义隐式真值转换，我们可以定义我们的asBoolean()方法。

### 3.3. 进口

默认情况下会导入一些包，我们不需要显式导入它们：

```groovy
import java.lang.* 
import java.util.* 
import java.io.* 
import java.net.* 

import groovy.lang.* 
import groovy.util.* 

import java.math.BigInteger 
import java.math.BigDecimal
```

## 4. AST 转换

AST(抽象语法树)转换允许我们挂钩到 Groovy 编译过程并自定义它以满足我们的需要。这是在编译时完成的，因此在运行应用程序时没有性能损失。我们可以创建我们的 AST 转换，但我们也可以使用内置的。

我们可以创建我们的转换，或者我们可以从内置的转换中受益。

让我们来看看一些值得了解的注解。

### 4.1. 注释类型检查

此注释用于强制编译器对带注释的代码段进行严格的类型检查。类型检查机制是可扩展的，所以我们甚至可以在需要时提供比 Java 中更严格的类型检查。

让我们看看下面的例子：

```groovy
class Universe {
    @TypeChecked
    int answer() { "forty two" }
}
```

如果我们尝试编译这段代码，我们会观察到以下错误：

```groovy
[Static type checking] - Cannot return value of type java.lang.String on method returning type int
```

@TypeChecked注释可以应用于类和方法。

### 4.2. 注解CompileStatic

此批注允许编译器执行编译时检查，就像对 Java 代码所做的那样。之后，编译器执行静态编译，从而绕过 Groovy 元对象协议。

当一个类被注解时，被注解类的所有方法、属性、文件、内部类等都会被类型检查。注释方法时，静态编译仅应用于该方法包含的那些项目(闭包和匿名内部类)。

## 5.属性

在 Groovy 中，我们可以创建 POGO(Plain Old Groovy Objects)，其工作方式与 Java 中的 POJO 相同，尽管它们更紧凑，因为在编译期间会为公共属性自动生成 getter 和 setter。重要的是要记住它们只有在它们尚未定义时才会生成。

这使我们可以灵活地将属性定义为开放字段，同时保留在设置或获取值时覆盖行为的能力。

考虑这个对象：

```groovy
class Person {
    String name
    String lastName
}
```

由于类、字段和方法的默认范围是公共的——这是一个公共类，两个字段都是公共的。

编译器会将它们转换为私有字段并添加getName()、setName()、getLastName()和setLasfName()方法。如果我们为特定字段定义setter和getter ，编译器将不会创建公共方法。

### 5.1. 快捷符号

Groovy 提供了获取和设置属性的快捷方式。我们可以使用类似字段的访问符号，而不是调用 getter 和 setter 的 Java 方式：

```groovy
resourceGroup.getResourcePrototype().getName() == SERVER_TYPE_NAME
resourceGroup.resourcePrototype.name == SERVER_TYPE_NAME

resourcePrototype.setName("something")
resourcePrototype.name = "something"
```

## 6. 运营商

现在让我们看一下在纯 Java 中已知的运算符之上添加的新运算符。

### 6.1. 空安全取消引用

最流行的是空安全取消引用运算符“？” 这允许我们在调用方法或访问null对象的属性时避免NullPointerException。它在链式调用中特别有用，在链式调用中，空值可能出现在链中的某个点。

例如，我们可以安全地调用：

```groovy
String name = person?.organization?.parent?.name
```

在上面的示例中，如果person、person.organization或organization.parent为null，则返回null 。

### 6.2. 猫王接线员

Elvis 运算符“?: ”让我们压缩三元表达式。这两个是等价的：

```groovy
String name = person.name ?: defaultName
```

和

```groovy
String name = person.name ? person.name : defaultName
```

他们都将person.name的值分配给 name 变量，如果它是Groovy true(在这种情况下，不为null并且具有非零长度)。

### 6.3. 飞船操作员

飞船运算符“<=>”是一个关系运算符，其执行方式类似于 Java 的compareTo()，它比较两个对象并根据两个参数的值返回 -1、0 或 +1。

如果左参数大于右参数，则运算符返回 1。如果左参数小于右参数，则运算符返回 −1。如果参数相等，则返回 0。

使用比较运算符的最大优点是可以顺利处理空值，这样x <=> y永远不会抛出NullPointerException：

```groovy
println 5 <=> null复制
```

上面的示例将打印 1 作为结果。

## 7. 字符串

有多种表达字符串文字的方法。支持 Java 中使用的方法(双引号字符串)，但也允许在首选时使用单引号。

还支持多行字符串，有时在其他语言中称为 heredocs，使用三重引号(单引号或双引号)。

还支持多行字符串，有时在其他语言中称为 heredocs，使用三重引号(单引号或双引号)。

用双引号定义的字符串支持使用${}语法进行插值：

```groovy
def name = "Bill Gates"
def greeting = "Hello, ${name}"
```

事实上，任何表达式都可以放在${}中：

```groovy
def name = "Bill Gates"
def greeting = "Hello, ${name.toUpperCase()}"
```

带双引号的字符串如果包含表达式${}则称为 GString ，否则它是普通字符串对象。

下面的代码将在测试失败的情况下运行：

```groovy
def a = "hello" 
assert a.class.name == 'java.lang.String'

def b = 'hello'
assert b.class.name == 'java.lang.String'

def c = "${b}"
assert c.class.name == 'org.codehaus.groovy.runtime.GStringImpl'
```

## 8. 收藏和地图

我们来看看一些基本的数据结构是如何处理的。

### 8.1. 列表

下面是一些代码，用于在 Java 中向ArrayList的新实例添加一些元素：

```java
List<String> list = new ArrayList<>();
list.add("Hello");
list.add("World");
```

这是 Groovy 中的相同操作：

```groovy
List list = ['Hello', 'World']
```

列表默认为java.util.ArrayList类型，也可以通过调用相应的构造函数显式声明。

Set没有单独的语法，但我们可以为此使用类型强制。要么使用：

```groovy
Set greeting = ['Hello', 'World']
```

或者：

```groovy
def greeting = ['Hello', 'World'] as Set
```

### 8.2. 地图

Map的语法类似，虽然有点冗长，因为我们需要能够指定用冒号分隔的键和值：

```groovy
def key = 'Key3'
def aMap = [
    'Key1': 'Value 1', 
    Key2: 'Value 2',
    (key): 'Another value'
]
```

初始化之后，我们将获得一个新的LinkedHashMap，其中包含以下条目：Key1 -> Value1, Key2 -> Value 2, Key3 -> Another Value。

我们可以通过多种方式访问地图中的条目：

```groovy
println aMap['Key1']
println aMap[key]
println aMap.Key1
```

## 9. 控制结构

### 9.1. 条件：if-else

Groovy按预期支持条件if/else语法：

```groovy
if (...) {
    // ...
} else if (...) {
    // ...
} else {
    // ...
}

```

### 9.2. 条件：switch-case

switch语句向后兼容 Java 代码，因此我们可以解决多个匹配项共享相同代码的情况。

最重要的区别是switch可以针对多个不同的值类型执行匹配：

```groovy
def x = 1.23
def result = ""

switch ( x ) {
    case "foo":
        result = "found foo"
        break

    case "bar":
        result += "bar"
        break

    case [4, 5, 6, 'inList']:
        result = "list"
        break

    case 12..30:
        result = "range"
        break

    case Number:
        result = "number"
        break

    case ~/fo*/: 
        result = "foo regex"
        break

    case { it < 0 }: // or { x < 0 }
        result = "negative"
        break

    default:
        result = "default"
}

println(result)
```

上面的示例将打印数字。

### 9.3. 循环：同时

Groovy 像 Java 一样支持通常的while循环：

```groovy
def x = 0
def y = 5

while ( y-- > 0 ) {
    x++
}
```

### 9.4. 循环：对于

Groovy 拥抱这种简单性并强烈鼓励遵循这种结构的循环：

```groovy
for (variable in iterable) { body }复制
```

for循环遍历iterable。经常使用的可迭代对象是范围、集合、映射、数组、迭代器和枚举。事实上，任何对象都可以是可迭代的。

如果它只包含一个语句，则主体周围的大括号是可选的。下面是遍历range、list、array、map和strings的示例：

```groovy
def x = 0
for ( i in 0..9 ) {
    x += i
}

x = 0
for ( i in [0, 1, 2, 3, 4] ) {
    x += i
}

def array = (0..4).toArray()
x = 0
for ( i in array ) {
    x += i
}

def map = ['abc':1, 'def':2, 'xyz':3]
x = 0
for ( e in map ) {
    x += e.value
}

x = 0
for ( v in map.values() ) {
    x += v
}

def text = "abc"
def list = []
for (c in text) {
    list.add(c)
}
```

对象迭代使 Groovy for循环成为一个复杂的控制结构。它是使用闭包遍历对象的方法的有效对应物，例如使用Collection 的 each方法。

主要区别在于for循环的主体不是闭包，这意味着该主体是一个块：

```groovy
for (x in 0..9) { println x }复制
```

而这个主体是一个闭包：

```groovy
(0..9).each { println it }复制
```

尽管它们看起来很相似，但它们的构造却大不相同。

闭包是它自己的对象并且具有不同的特性。它可以在不同的地方构建并传递给每个方法。然而， for循环的主体在其出现时直接生成为字节码。没有特殊的范围规则适用。

## 10.异常处理

最大的区别是不强制执行已检查的异常处理。

为了处理一般异常，我们可以将可能导致异常的代码放在try/catch块中：

```groovy
try {
    someActionThatWillThrowAnException()
} catch (e)
    // log the error message, and/or handle in some way
}
```

通过不声明我们捕获的异常类型，任何异常都会在这里被捕获。

## 11. 闭包

简而言之，闭包是一个匿名的可执行代码块，可以将其传递给变量，并可以访问定义它的上下文中的数据。

它们也类似于匿名内部类，尽管它们不实现接口或扩展基类。它们类似于 Java 中的 lambda。

有趣的是，Groovy 可以充分利用已引入的 JDK 附加功能来支持 lambda，尤其是流式 API。我们总是可以在需要 lambda 表达式的地方使用闭包。

让我们考虑下面的例子：

```groovy
def helloWorld = {
    println "Hello World"
}复制
```

变量helloWorld现在持有闭包的引用，我们可以通过调用它的call方法来执行它：

```groovy
helloWorld.call()复制
```

Groovy 让我们使用更自然的方法调用语法——它为我们调用调用方法：

```groovy
helloWorld()复制
```

### 11.1. 参数

与方法一样，闭包也可以有参数。共有三种变体。

在后一个示例中，因为没有 declpersistence_startared，所以只有一个默认名称为it 的参数。打印发送内容的修改后的闭包是：

```groovy
def printTheParam = { println it }复制
```

我们可以这样称呼它：

```groovy
printTheParam('hello')
printTheParam 'hello'复制
```

我们还可以期待闭包中的参数并在调用时传递它们：

```groovy
def power = { int x, int y ->
    return Math.pow(x, y)
}
println power(2, 3)
```

参数的类型定义与变量相同。如果我们定义一个类型，我们只能使用这个类型，但也可以使用它并传入我们想要的任何东西：

```groovy
def say = { what ->
    println what
}
say "Hello World"
```

### 11.2. 可选退货

闭包的最后一条语句可以隐式返回而无需编写 return 语句。这可用于将样板代码减少到最少。因此，计算数字平方的闭包可以缩短如下：

```groovy
def square = { it * it }
println square(4)复制
```

此闭包使用隐式参数it和可选的 return 语句。

## 12.总结

本文简要介绍了 Groovy 语言及其主要特性。我们首先介绍了基本语法、条件语句和运算符等简单概念。我们还演示了一些更高级的功能，例如运算符和闭包。

如果你想找到有关该语言及其语义的更多信息，你可以直接访问[官方网站](http://www.groovy-lang.org/semantics.html)。