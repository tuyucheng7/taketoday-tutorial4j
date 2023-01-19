## 一、概述

在本快速教程中，我们将探索[Groovy中](https://www.baeldung.com/groovy-language)def关键字的概念。它为这种动态 JVM 语言提供了可选的类型化功能。

## 2. def关键字的含义

def关键字用于在 Groovy 中定义无类型变量或函数，因为它是一种可选类型的语言。

当我们不确定变量或字段的类型时，我们可以利用def让 Groovy 在运行时根据分配的值决定类型：

```groovy
def firstName = "Samwell"  
def listOfCountries = ['USA', 'UK', 'FRANCE', 'INDIA']

```

在这里，firstName将是一个String，而listOfCountries将是一个ArrayList。

我们还可以使用def关键字来定义方法的返回类型：

```groovy
def multiply(x, y) {
    return xy
}
```

在这里，multiply可以返回任何类型的对象，这取决于我们传递给它的参数。

## 3. def变量

让我们了解def如何处理变量。

当我们使用def声明变量时，Groovy 将其声明为[NullObject](http://docs.groovy-lang.org/docs/groovy-2.3.2/html/api/org/codehaus/groovy/runtime/NullObject.html)并为其分配空值：

```groovy
def list
assert list.getClass() == org.codehaus.groovy.runtime.NullObject
assert list.is(null)

```

当我们为列表赋值时，Groovy 会根据赋值定义它的类型：

```groovy
list = [1,2,4]
assert list instanceof ArrayList

```

假设我们想让我们的变量类型动态化并随着赋值而改变：

```groovy
int rate = 20
rate = [12] // GroovyCastException
rate = "nill" // GroovyCastException
```

我们不能将List或String分配给int类型的变量，因为这会抛出运行时异常。

因此，为了克服这个问题并调用 Groovy 的动态特性，我们将使用def关键字：

```groovy
def rate
assert rate == null
assert rate.getClass() == org.codehaus.groovy.runtime.NullObject

rate = 12
assert rate instanceof Integer
        
rate = "Not Available"
assert rate instanceof String
        
rate = [1, 4]
assert rate instanceof List
```

## 4. def方法

def关键字进一步用于定义方法的动态返回类型。当我们可以为一个方法提供不同类型的返回值时，这很方便：

```groovy
def divide(int x, int y) {
    if (y == 0) {
        return "Should not divide by 0"
    } else {
        return x/y
    }
}

assert divide(12, 3) instanceof BigDecimal
assert divide(1, 0) instanceof String
```

我们还可以使用def来定义一个没有显式返回的方法：

```groovy
def greetMsg() {
    println "Hello! I am Groovy"
}
```

## 5. def与 Type

让我们讨论一些围绕使用def的最佳实践。

虽然我们可以在声明变量时同时使用def和 type：

```groovy
def int count
assert count instanceof Integer
```

def关键字在那里是多余的，所以我们应该使用def或类型。

此外，我们应该避免在方法中对非类型化参数使用def。

因此，而不是：

```groovy
void multiply(def x, def y)
```

我们应该更喜欢：

```groovy
void multiply(x, y)
```

此外，我们应该避免在定义构造函数时使用def 。

## 6. Groovy def 与 Java 对象

由于我们已经通过示例了解了def关键字的大部分功能及其用法，我们可能想知道它是否类似于在 Java 中使用Object类声明某些内容。是的，def可以被认为类似于Object：

```groovy
def fullName = "Norman Lewis"
```

同样，我们可以在 Java中使用Object ：

```java
Object fullName = "Norman Lewis";
```

## 7. def与@TypeChecked

正如我们中的许多人都来自严格类型语言的世界，我们可能想知道如何在 Groovy 中强制进行编译时类型检查。我们可以使用@TypeChecked注解轻松实现这一点。

例如，我们可以在一个类上使用@TypeChecked来为其所有方法和属性启用类型检查：

```groovy
@TypeChecked
class DefUnitTest extends GroovyTestCase {

    def multiply(x, y) {
        return x  y
    }
    
    int divide(int x, int y) {
        return x / y
    }
}
```

在这里，DefUnitTest类将被类型检查，并且编译将由于multiply方法被取消类型而失败。Groovy 编译器将显示错误：

```shell
[Static type checking] - Cannot find matching method java.lang.Object#multiply(java.lang.Object).
Please check if the declared type is correct and if the method exists.
```

所以，要忽略一个方法，我们可以使用TypeCheckingMode.SKIP：

```groovy
@TypeChecked(TypeCheckingMode.SKIP)
def multiply(x, y)
```

## 八、总结

在本快速教程中，我们了解了如何使用def关键字来调用 Groovy 语言的动态特性，并让它在运行时确定变量和方法的类型。

这个关键字可以方便地编写动态和健壮的代码。