## 一、概述

在本入门教程中，我们将探讨[Groovy](https://www.baeldung.com/groovy-language)中闭包的概念，这是这种动态且强大的 JVM 语言的一个关键特性。

许多其他语言，包括 Javascript 和 Python，都支持闭包的概念。但是，闭包的特性和功能因语言而异。

我们将触及Groovy闭包的关键方面，展示如何使用它们的示例。

## 2. 什么是闭包？

[闭包](https://www.baeldung.com/cs/closure)是一个匿名的代码块。在Groovy中，它是[Closure](http://docs.groovy-lang.org/latest/html/api/groovy/lang/Closure.html)类的一个实例。闭包可以接受 0 个或多个参数并始终返回一个值。

此外，闭包可以访问其范围之外的周围变量，并在执行期间使用它们——连同它的局部变量。

此外，我们可以将闭包分配给变量或将其作为参数传递给方法。因此，闭包提供延迟执行的功能。

## 3. 关闭声明

Groovy 闭包包含参数、箭头 -> 和要执行的代码。参数是可选的，并且在提供时以逗号分隔。

### 3.1. 基本声明

```groovy
def printWelcome = {
    println "Welcome to Closures!"
}
```

在这里，闭包printWelcome在被调用时打印一条语句。现在，让我们编写一个一元闭包的简单示例：

```groovy
def print = { name ->
    println name 
}
```

在这里，闭包print接受一个参数——名称——并在调用时打印它。

由于闭包的定义看起来类似于方法，让我们比较一下：

```groovy
def formatToLowerCase(name) {
    return name.toLowerCase()
}
def formatToLowerCaseClosure = { name ->
    return name.toLowerCase()
}

```

在这里，该方法和相应的闭包行为相似。然而，闭包和方法之间存在细微差别，我们将在稍后的闭包与方法部分讨论。

### 3.2. 执行

我们可以通过两种方式执行闭包——我们可以像调用任何其他方法一样调用它，或者我们可以使用call方法。

例如，作为常规方法：

```groovy
print("Hello! Closure")
formatToLowerCaseClosure("Hello! Closure")

```

并使用调用方法执行：

```groovy
print.call("Hello! Closure") 
formatToLowerCaseClosure.call("Hello! Closure")
```

## 四、参数

Groovy 闭包的参数与常规方法的参数类似。

### 4.1. 隐式参数

我们可以定义一个没有参数的一元闭包，因为当没有定义参数时，Groovy 假定一个名为“ it”的隐式参数：

```groovy
def greet = {
    return "Hello! ${it}"
}
assert greet("Alex") == "Hello! Alex"
```

### 4.2. 多个参数

这是一个带有两个参数并返回它们相乘结果的闭包：

```groovy
def multiply = { x, y -> 
    return xy 
}
assert multiply(2, 4) == 8
```

### 4.3. 参数类型

到目前为止，在示例中，我们的参数没有提供任何类型。我们还可以设置闭包参数的类型。例如，让我们重写multiply方法以考虑其他操作：

```groovy
def calculate = {int x, int y, String operation ->
    def result = 0    
    switch(operation) {
        case "ADD":
            result = x+y
            break
        case "SUB":
            result = x-y
            break
        case "MUL":
            result = xy
            break
        case "DIV":
            result = x/y
            break
    }
    return result
}
assert calculate(12, 4, "ADD") == 16
assert calculate(43, 8, "DIV") == 5.375
```

### 4.4. 可变参数

我们可以在闭包中声明可变数量的参数，类似于常规方法。例如：

```groovy
def addAll = { int... args ->
    return args.sum()
}
assert addAll(12, 10, 14) == 36
```

## 5. 闭包作为参数

我们可以将闭包作为参数传递给常规Groovy方法。这允许该方法调用我们的闭包来完成它的任务，允许我们自定义它的行为。

让我们讨论一个简单的用例：计算常规图形的体积。

在此示例中，体积定义为面积乘以高度。但是，面积的计算可能因不同的形状而异。

因此，我们将编写volume方法，它以一个闭包areaCalculator作为参数，我们将在调用期间传递面积计算的实现：

```groovy
def volume(Closure areaCalculator, int... dimensions) {
    if(dimensions.size() == 3) {
        return areaCalculator(dimensions[0], dimensions[1])  dimensions[2]
    } else if(dimensions.size() == 2) {
        return areaCalculator(dimensions[0])  dimensions[1]
    } else if(dimensions.size() == 1) {
        return areaCalculator(dimensions[0])  dimensions[0]
    }    
}
assert volume({ l, b -> return lb }, 12, 6, 10) == 720

```

让我们使用相同的方法找到圆锥体的体积：

```groovy
assert volume({ radius -> return Math.PIradiusradius/3 }, 5, 10) == Math.PI  250
```

## 6.嵌套闭包

我们可以在闭包内声明和调用闭包。

例如，让我们为已经讨论过的计算闭包添加一个日志功能：

```groovy
def calculate = {int x, int y, String operation ->
        
    def log = {
        println "Performing $it"
    }
        
    def result = 0    
    switch(operation) {
        case "ADD":
            log("Addition")
            result = x+y
            break
        case "SUB":
            log("Subtraction")
            result = x-y
            break
        case "MUL":
            log("Multiplication")
            result = xy
            break
        case "DIV":
            log("Division")
            result = x/y
            break
    }
    return result
}
```

## 7. 字符串的惰性求值

Groovy String通常在创建时进行求值和插值。例如：

```groovy
def name = "Samwell"
def welcomeMsg = "Welcome! $name"
        
assert welcomeMsg == "Welcome! Samwell"
```

即使我们修改了name变量的值，welcomeMsg也不会改变：

```groovy
name = "Tarly"
assert welcomeMsg != "Welcome! Tarly"
```

闭包插值允许我们提供String s 的延迟评估，从它们周围的当前值重新计算。例如：

```groovy
def fullName = "Tarly Samson"
def greetStr = "Hello! ${-> fullName}"
        
assert greetStr == "Hello! Tarly Samson"
```

只有这一次，更改变量也会影响内插字符串的值：

```groovy
fullName = "Jon Smith"
assert greetStr == "Hello! Jon Smith"
```

## 8. 集合中的闭包

Groovy Collections 在它们的许多 API 中使用闭包。例如，让我们定义一个项目列表并使用一元闭包each打印它们，它有一个隐式参数：

```groovy
def list = [10, 11, 12, 13, 14, true, false, "BUNTHER"]

list.each {
    println it
}

assert [13, 14] == list.findAll{ it instanceof Integer && it >= 13 }
```

通常，基于某些标准，我们可能需要从地图创建列表。例如：

```groovy
def map = [1:10, 2:30, 4:5]

assert [10, 60, 20] == map.collect{it.key  it.value}

```

## 9.闭包与方法

到目前为止，我们已经了解了闭包的语法、执行和参数，它们与方法非常相似。现在让我们比较一下闭包和方法。

与常规的Groovy方法不同：

-   我们可以将闭包作为参数传递给方法
-   一元闭包可以使用隐式it参数
-   我们可以将Closure分配给一个变量并在以后执行它，无论是作为方法还是通过调用
-  Groovy在运行时确定闭包的返回类型
-   我们可以在闭包中声明和调用闭包
-   闭包总是返回一个值

因此，闭包具有优于常规方法的优势，并且是Groovy的强大功能。

## 10.总结

在本文中，我们了解了如何在Groovy中创建闭包并探讨了如何使用它们。

闭包提供了一种将功能注入对象和方法以延迟执行的有效方法。