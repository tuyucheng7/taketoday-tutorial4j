## 一、简介

在本教程中，我们将讨论如何使用不同的Groovy作用域，并了解如何利用其可变作用域

## 2.依赖关系

在整个过程中，我们将使用 [groovy-all](https://search.maven.org/artifact/org.codehaus.groovy/groovy-all)和[spock-core](https://search.maven.org/artifact/org.spockframework/spock-core) 依赖项

```groovy
dependencies {
    compile 'org.codehaus.groovy:groovy-all:2.4.13'
    testCompile 'org.spockframework:spock-core:1.1-groovy-2.4'
}
```

## 3.所有范围

Groovy 中的作用域首先遵循所有变量默认创建为公共的规则。这意味着，除非另有说明，否则我们将能够访问我们从代码中的任何其他范围创建的任何变量。

我们将了解这些作用域的含义，并且为了测试这一点，我们将运行Groovy脚本。要运行脚本，我们只需要运行：

```bash
groovy <scriptname>.groovy
```

### 3.1. 全局变量

在Groovy脚本中创建全局变量的最简单方法是将其分配到脚本中的任何位置，无需任何特殊关键字。我们甚至不需要定义类型：

```groovy
x = 200
```

然后，如果我们运行以下Groovy脚本：

```groovy
x = 200
logger = Logger.getLogger("Scopes.groovy")
logger.info("- Global variable")
logger.info(x.toString())
```

我们将看到我们可以从全局范围访问我们的变量。

### 3.2. 从函数范围访问全局变量

访问全局变量的另一种方法是使用函数作用域：

```groovy
def getGlobalResult() { 
   return 1 + x
}
```

此函数在作用域脚本中定义。我们将 1 添加到我们的全局x变量。

如果我们运行以下脚本：

```groovy
x = 200
logger = Logger.getLogger("Scopes.groovy")

def getGlobalResult() {
    logger.info(x.toString())
    return 1 + x
}

logger.info("- Access global variable from inside function")
logger.info(getGlobalResult().toString())
```

结果我们将得到 201。这证明我们可以从函数的局部范围访问我们的全局变量。

### 3.3. 从函数范围创建全局变量

我们还可以在函数范围内创建全局变量。在这个本地范围内，如果我们在创建变量时不使用任何关键字，我们将在全局范围内创建它。然后，让我们 在一个新函数中创建一个全局变量z ：

```groovy
def defineGlobalVariable() {
    z = 234
}

```

并尝试通过运行以下脚本来访问它：

```groovy
logger = Logger.getLogger("Scopes.groovy")
 
def defineGlobalVariable() {
    z = 234
    logger = Logger.getLogger("Scopes.groovy")
    logger.info(z.toString())
}

logger.info("- function called to create variable")
defineGlobalVariable()
logger.info("- Variable created inside a function")
logger.info(z.toString())
```

我们将看到我们可以从全局范围访问z 。所以这最终证明我们的变量已经在全局范围内创建了。

### 3.4. 非全局变量

对于非全局变量，我们将快速查看仅为局部范围创建的变量。

具体来说，我们将查看关键字def。这样，我们将这个变量定义为线程运行范围的一部分。

因此，让我们尝试定义一个全局变量y和一个函数局部变量：

```groovy
logger = Logger.getLogger("ScopesFail.groovy")

y = 2

def fLocal() {
    def q = 333
    println(q)
    q
}

fLocal()

logger.info("- Local variable doesn't exist outside")
logger.info(q.toString())

```

如果我们运行这个脚本，它将失败。它失败的原因是q是一个局部变量，属于函数 fLocal的范围。由于我们 使用def关键字创建q ，我们将无法通过全局范围访问它。

显然，我们可以使用fLocal 函数访问 q：

```groovy
logger = Logger.getLogger("ScopesFail.groovy")

y = 2

def fLocal() {
    def q = 333
    println(q)
    q
}

fLocal()

logger.info("- Value of the created variable")
logger.info(fLocal())
```

所以现在我们可以看到，即使我们创建了一个 q 变量，该变量在其他范围内也不再可用。如果我们再次调用 fLocal，我们将创建一个新变量。

## 4。总结

在本文中，我们了解了Groovy作用域是如何创建的，以及如何从代码中的不同区域访问它们。