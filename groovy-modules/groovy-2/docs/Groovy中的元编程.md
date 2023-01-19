## 一、概述

[Groovy](https://www.baeldung.com/groovy-language)是一种动态且功能强大的 JVM 语言，具有许多特性，例如[闭包](https://www.baeldung.com/groovy-closures)和[特征](https://www.baeldung.com/groovy-traits)。

在本教程中，我们将探讨 Groovy 中元编程的概念。

## 2. 什么是元编程？

元编程是一种编写程序以使用元数据修改自身或另一个程序的编程技术。

在 Groovy 中，可以在运行时和编译时执行元编程。展望未来，我们将探讨这两种技术的一些显着特征。

## 3.运行时元编程

运行时元编程使我们能够改变类的现有属性和方法。此外，我们可以附加新的属性和方法；全部在运行时。

Groovy 提供了一些方法和属性来帮助在运行时改变类的行为。

### 3.1. 属性缺失

当我们尝试访问 Groovy 类的未定义属性时，它会抛出MissingPropertyException。为了避免异常，Groovy 提供了propertyMissing方法。

首先，让我们编写一个具有一些属性的Employee类：

```groovy
class Employee {
    String firstName
    String lastName  
    int age
}
```

其次，我们将创建一个Employee对象并尝试显示一个未定义的属性地址。因此，它将抛出MissingPropertyException：


```groovy
Employee emp = new Employee(firstName: "Norman", lastName: "Lewis")
println emp.address

groovy.lang.MissingPropertyException: No such property: 
address for class: cn.tuyucheng.taketoday.metaprogramming.Employee
```

Groovy 提供了propertyMissing方法来捕获丢失的属性请求。因此，我们可以在运行时避免MissingPropertyException。

为了捕获缺失属性的 getter 方法调用，我们将使用属性名称的单个参数来定义它：

```groovy
def propertyMissing(String propertyName) {
    "property '$propertyName' is not available"
}
assert emp.address == "property 'address' is not available"
```

此外，同一个方法可以将第二个参数作为属性的值，以捕获缺少的属性的 setter 方法调用：

```groovy
def propertyMissing(String propertyName, propertyValue) { 
    println "cannot set $propertyValue - property '$propertyName' is not available" 
}
```

### 3.2. 方法缺失

methodMissing方法类似于propertyMissing 。但是，methodMissing 会拦截对任何缺失方法的调用，从而避免MissingMethodException。

让我们尝试调用Employee对象的getFullName方法。由于缺少getFullName ，执行将在运行时抛出MissingMethodException ：

```groovy
try {
    emp.getFullName()
} catch (MissingMethodException e) {
    println "method is not defined"
}
```

因此，我们可以定义methodMissing，而不是将方法调用包装在try-catch中：

```groovy
def methodMissing(String methodName, def methodArgs) {
    "method '$methodName' is not defined"
}
assert emp.getFullName() == "method 'getFullName' is not defined"
```

### 3.3. 扩展元类

Groovy 在它的所有类中都提供了一个metaClass属性。metaClass属性引用ExpandoMetaClass的一个实例。

[ExpandoMetaClass](http://docs.groovy-lang.org/latest/html/api/groovy/lang/ExpandoMetaClass.html)类提供了多种在运行时转换现有类的方法。例如，我们可以添加属性、方法或构造函数。

首先，让我们使用metaClass属性将缺少的地址属性添加到Employee类：

```groovy
Employee.metaClass.address = ""
Employee emp = new Employee(firstName: "Norman", lastName: "Lewis", address: "US")
assert emp.address == "US"
```

更进一步，让我们在运行时将缺少的getFullName方法添加到Employee类对象：

```groovy
emp.metaClass.getFullName = {
    "$lastName, $firstName"
}
assert emp.getFullName() == "Lewis, Norman"
```

同样，我们可以在运行时为Employee类添加一个构造函数：

```groovy
Employee.metaClass.constructor = { String firstName -> 
    new Employee(firstName: firstName) 
}
Employee norman = new Employee("Norman")
assert norman.firstName == "Norman"
assert norman.lastName == null
```

同样，我们可以使用metaClass.static添加静态方法。

metaClass属性不仅可以方便地修改用户定义的类，还可以在运行时修改现有的 Java 类。

例如，让我们在String类中添加一个capitalize方法：

```groovy
String.metaClass.capitalize = { String str ->
    str.substring(0, 1).toUpperCase() + str.substring(1)
}
assert "norman".capitalize() == "Norman"
```

### 3.4. 扩展

扩展可以在运行时向类添加方法，并使其可全局访问。

扩展中定义的方法应该始终是静态的，将自身类对象作为第一个参数。

例如，让我们编写一个BasicExtension类来为Employee类添加一个getYearOfBirth方法：

```groovy
class BasicExtensions {
    static int getYearOfBirth(Employee self) {
        return Year.now().value - self.age
    }
}
```

要启用BasicExtension，我们需要在项目的META-INF/services目录中添加配置文件。

因此，让我们添加具有以下配置的org.codehaus.groovy.runtime.ExtensionModule文件：

```plaintext
moduleName=core-groovy-2 
moduleVersion=1.0-SNAPSHOT 
extensionClasses=com.baeldung.metaprogramming.extension.BasicExtensions
```

让我们验证一下在Employee类中添加的getYearOfBirth方法：

```groovy
def age = 28
def expectedYearOfBirth = Year.now() - age
Employee emp = new Employee(age: age)
assert emp.getYearOfBirth() == expectedYearOfBirth.value
```

同样，要在类中添加静态方法，我们需要定义一个单独的扩展类。

例如，让我们通过定义StaticEmployeeExtension类向我们的Employee类添加一个静态方法getDefaultObj：

```groovy
class StaticEmployeeExtension {
    static Employee getDefaultObj(Employee self) {
        return new Employee(firstName: "firstName", lastName: "lastName", age: 20)
    }
}
```

然后，我们通过将以下配置添加到ExtensionModule文件来启用StaticEmployeeExtension ：

```plaintext
staticExtensionClasses=com.baeldung.metaprogramming.extension.StaticEmployeeExtension
```

现在，我们只需要在Employee类上测试我们的静态 getDefaultObj方法：

```groovy
assert Employee.getDefaultObj().firstName == "firstName"
assert Employee.getDefaultObj().lastName == "lastName"
assert Employee.getDefaultObj().age == 20
```

同样，使用扩展，我们可以向预编译的 Java 类添加一个方法，如Integer和Long：

```groovy
public static void printCounter(Integer self) {
    while (self > 0) {
        println self
        self--
    }
    return self
}
assert 5.printCounter() == 0

public static Long square(Long self) {
    return selfself
}
assert 40l.square() == 1600l

```

## 4. 编译时元编程

使用特定的注解，我们可以毫不费力地在编译时改变类结构。也就是说，我们可以在编译时使用注解来修改类的抽象语法树。

让我们讨论一些在 Groovy 中非常方便的注解，以减少样板代码。其中许多在groovy.transform包中可用。

如果我们仔细分析，我们会发现一些注解提供了类似于 Java 的[Project Lombok](https://www.baeldung.com/intro-to-project-lombok)的功能。

### 4.1. @ToString

@ToString注解在编译时将toString方法的默认实现添加到类中。我们所需要的只是将注解添加到类中。

例如，让我们将@ToString注解添加到我们的Employee类：

```groovy
@ToString
class Employee {
    long id
    String firstName
    String lastName
    int age
}
```

现在，我们将创建一个Employee类的对象并验证toString方法返回的字符串：

```groovy
Employee employee = new Employee()
employee.id = 1
employee.firstName = "norman"
employee.lastName = "lewis"
employee.age = 28

assert employee.toString() == "cn.tuyucheng.taketoday.metaprogramming.Employee(1, norman, lewis, 28)"
```

我们还可以使用@ToString声明诸如excludes、includes、includePackage和ignoreNulls等参数来修改输出字符串。

例如，让我们从 Employee 对象的字符串中排除id和package ：

```groovy
@ToString(includePackage=false, excludes=['id'])
assert employee.toString() == "Employee(norman, lewis, 28)"
```

### 4.2. @TupleConstructor

在 Groovy 中使用@TupleConstructor在类中添加参数化构造函数。此注解为每个属性创建一个带有参数的构造函数。

例如，让我们将@TupleConstructor添加到Employee类：

```groovy
@TupleConstructor 
class Employee { 
    long id 
    String firstName 
    String lastName 
    int age 
}
```

现在，我们可以按照类中定义的属性的顺序创建Employee对象传递参数。

```groovy
Employee norman = new Employee(1, "norman", "lewis", 28)
assert norman.toString() == "Employee(norman, lewis, 28)"

```

如果我们在创建对象时不为属性提供值，Groovy 将考虑默认值：

```groovy
Employee snape = new Employee(2, "snape")
assert snape.toString() == "Employee(snape, null, 0)"
```

与@ToString类似，我们可以使用@TupleConstructor声明诸如excludes、includes和includeSuperProperties等参数，以根据需要更改其关联构造函数的行为。

### 4.3. @EqualsAndHashCode

我们可以使用@EqualsAndHashCode在编译时生成equals和hashCode方法的默认实现。

让我们通过将@EqualsAndHashCode添加到Employee类来验证它的行为：

```groovy
Employee normanCopy = new Employee(1, "norman", "lewis", 28)

assert norman == normanCopy
assert norman.hashCode() == normanCopy.hashCode()
```

### 4.4. @典范

@Canonical是@ToString、@TupleConstructor和@EqualsAndHashCode注解的组合。

只需添加它，我们就可以轻松地将所有这三个都包含到一个 Groovy 类中。此外，我们可以使用所有三个注解的任何特定参数来声明@Canonical 。

### 4.5. @自动克隆

实现Cloneable接口的一种快速可靠的方法是添加@AutoClone注解。

在Employee类中添加@AutoClone后，我们来验证clone方法：

```groovy
try {
    Employee norman = new Employee(1, "norman", "lewis", 28)
    def normanCopy = norman.clone()
    assert norman == normanCopy
} catch (CloneNotSupportedException e) {
    e.printStackTrace()
}
```

### 4.6. 使用@Log、@Commons、@Log4j、@Log4j2和@Slf4j 进行日志记录支持

要向任何 Groovy 类添加日志记录支持，我们需要的只是添加groovy.util.logging包中可用的注解。

让我们通过向Employee类添加@Log注解来启用 JDK 提供的日志记录。之后，我们将添加logEmp方法：

```groovy
def logEmp() {
    log.info "Employee: $lastName, $firstName is of $age years age"
}
```

在Employee对象上调用logEmp方法将在控制台上显示日志：

```groovy
Employee employee = new Employee(1, "Norman", "Lewis", 28)
employee.logEmp()
INFO: Employee: Lewis, Norman is of 28 years age
```

同样，@Commons注解可用于添加 Apache Commons 日志记录支持。@Log4j可用于 Apache Log4j 1.x 日志记录支持，@ Log4j2可用于[Apache Log4j 2.x。](https://www.baeldung.com/java-logging-intro)最后，使用@Slf4j[为Java](https://www.baeldung.com/slf4j-with-log4j2-logback)支持添加Simple Logging Facade。

## 5.总结

在本教程中，我们探讨了 Groovy 中元编程的概念。

在此过程中，我们看到了一些值得注意的运行时和编译时元编程特性。

同时，我们探索了 Groovy 中可用的其他方便的注解，以获得更清晰和动态的代码。