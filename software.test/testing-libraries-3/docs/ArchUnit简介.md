## 1. 概述

在本文中，我们将展示如何使用[ArchUnit](https://www.archunit.org/)检查系统的架构。

## 2. 什么是ArchUnit？

架构特征和可维护性之间的联系是软件行业中一个[经过充分研究的话题](https://www.diva-portal.org/smash/get/diva2:837619/FULLTEXT01.pdf)。但是，为我们的系统定义一个健全的架构是不够的。我们需要验证实现的代码是否符合它。

**简单地说，ArchUnit是一个测试库，它允许我们验证应用程序是否符合一组给定的架构规则**。但是，什么是架构规则？更重要的是，在这种情况下，我们所说的架构是什么意思？

让我们从后者开始。**在这里，我们使用术语架构来指代我们将应用程序中的不同类组织到包中的方式**。

系统的架构还定义了包或包组(也称为层)的交互方式。用更实际的术语来说，它定义了给定包中的代码是否可以调用属于另一个类的方法。例如，假设我们的应用程序架构包含三层：表示层、服务层和持久层。

可视化这些层如何交互的一种方法是使用UML包图，其中包含代表每个层的包：

<img src="../assets/img_8.png">

只要看这张图，我们就可以找出一些规律：

-   Presentation类应该只依赖于Service类
-   Service类应该只依赖于Persistence类
-   Persistence类不应依赖于其他任何类

查看这些规则，我们现在可以回过头来回答我们最初的问题。在这种情况下，架构规则是关于我们的应用程序类相互交互方式的断言。

那么现在，我们如何检查我们的实现是否遵守了这些规则？这就是ArchUnit的用武之地。它允许我们使用流式的API表达我们的架构约束，并在常规构建期间与其他测试一起验证它们。

## 3. ArchUnit项目设置

ArchUnit与[JUnit](https://www.baeldung.com/junit)测试框架很好地集成在一起，因此它们通常一起使用。我们所要做的就是添加[archunit-junit4](https://central.sonatype.com/artifact/com.tngtech.archunit/archunit-junit4/1.0.1)依赖项来匹配我们的JUnit版本：

```xml
<dependency>
    <groupId>com.tngtech.archunit</groupId>
    <artifactId>archunit-junit4</artifactId>
    <version>0.14.1</version>
    <scope>test</scope>
</dependency>
```

正如其artifactId所暗示的那样，此依赖项特定于JUnit 4框架。

如果我们使用JUnit 5，还有一个[archunit-junit5](https://central.sonatype.com/artifact/com.tngtech.archunit/archunit-junit5/1.0.1)依赖项：

```xml
<dependency>
    <groupId>com.tngtech.archunit</groupId>
    <artifactId>archunit-junit5</artifactId>
    <version>0.14.1</version>
    <scope>test</scope>
</dependency>
```

## 4. 编写ArchUnit测试

将适当的依赖项添加到项目后，让我们开始编写我们的架构测试。我们的测试应用程序将是一个查询[Smurfs](https://www.smurf.com/)的简单Spring Boot REST应用程序。为简单起见，此测试应用程序仅包含Controller、Service和Repository类。

我们要验证此应用程序是否符合我们之前提到的规则。所以，让我们从一个简单的测试规则“Presentation类应该只依赖于Service类”开始。

### 4.1 我们的第一个测试

**第一步是创建一组Java类，这些类将被检查是否存在规则冲突**。我们通过实例化ClassFileImporter类然后使用它的importXXX()方法之一来做到这一点：

```java
JavaClasses jc = new ClassFileImporter()
    .importPackages("cn.tuyucheng.taketoday.archunit.smurfs");
```

在本例中，JavaClasses实例包含我们主应用程序包及其子包中的所有类。我们可以将此对象视为类似于常规单元测试中使用的典型测试对象，因为它将是规则评估的目标。

架构规则使用ArchRuleDefinition类中的一种静态方法作为其流式API调用的起点。让我们尝试使用这个API来实现上面定义的第一个规则。我们将使用classes()方法作为我们的锚点，并从那里添加额外的约束：

```java
ArchRule r1 = classes()
    .that().resideInAPackage("..presentation..")
    .should().onlyDependOnClassesThat()
    .resideInAPackage("..service..");
r1.check(jc);
```

请注意，我们需要调用我们创建的规则的check()方法来运行检查。此方法接收一个JavaClasses对象，如果存在违规，将抛出异常。

这一切看起来都不错，但是如果我们尝试针对我们的代码运行它，我们会得到一个错误列表：

```bash
java.lang.AssertionError: Architecture Violation [Priority: MEDIUM] - 
  Rule 'classes that reside in a package '..presentation..' should only 
  depend on classes that reside in a package '..service..'' was violated (6 times):
... error list omitted
```

为什么？**此规则的主要问题是onlyDependsOnClassesThat()**。尽管我们在包图中放了什么，**但我们的实际实现依赖于JVM和Spring框架类，因此出现了错误**。

### 4.2 重写我们的第一个测试

解决此错误的一种方法是添加一个考虑这些附加依赖项的子句：

```java
ArchRule r1 = classes()
    .that().resideInAPackage("..presentation..")
    .should().onlyDependOnClassesThat()
    .resideInAPackage("..service..", "java..", "javax..", "org.springframework..");
```

通过此更改，我们的检查将不再失败。然而，这种方法存在可维护性问题，并且感觉有点笨拙。我们可以使用noClasses()静态方法作为起点重写我们的规则，从而避免这些问题：

```java
ArchRule r1 = noClasses()
    .that().resideInAPackage("..presentation..")
    .should().dependOnClassesThat()
    .resideInAPackage("..persistence..");
```

当然，**我们也可以指出这种方法是deny-based的，而不是我们之前的allow-based方法**。关键点是，无论我们选择哪种方法，ArchUnit通常都足够灵活来表达我们的规则。

## 5. 使用Library API

由于其内置规则，ArchUnit使创建复杂的架构规则变得容易。反过来，这些也可以组合在一起，允许我们使用更高级别的抽象来创建规则。**开箱即用，ArchUnit提供了Library API，这是一组预先打包的规则，可以解决常见的架构问题**：

-   Architectures：支持分层和洋葱(又名六边形或“端口和适配器”)架构规则检查
-   Slices：用于检测循环依赖或“循环”
-   General：与最佳编码实践(如日志记录、异常的使用等)相关的规则集合
-   PlantUML：检查我们的代码库是否遵循给定的UML模型
-   Freeze Arch Rules：保存违规以供以后使用，只允许报告新的违规行为。对管理技术债务特别有用

涵盖所有这些规则超出了本文的范围，但让我们看一下Architectures规则包。特别是，让我们使用分层架构规则重写上一节中的规则。使用这些规则需要两个步骤：首先，我们定义应用程序的层。然后，我们定义允许哪些层访问：

```java
LayeredArchitecture arch = layeredArchitecture()
     // Define layers
    .layer("Presentation").definedBy("..presentation..")
    .layer("Service").definedBy("..service..")
    .layer("Persistence").definedBy("..persistence..")
    // Add constraints
    .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
    .whereLayer("Service").mayOnlyBeAccessedByLayers("Presentation")
    .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service");
arch.check(jc);
```

在这里，layeredArchitecture()是来自Architectures类的静态方法。当被调用时，它返回一个新的LayeredArchitecture对象，然后我们使用它来定义名称层和关于它们的依赖关系的断言。该对象实现了 ArchRule接口，因此我们可以像使用任何其他规则一样使用它。

这个特殊API很酷的一点是，它允许我们只用几行代码创建规则，否则我们需要组合多个单独的规则。

## 6. 总结

在本文中，我们探讨了在项目中使用ArchUnit的基础知识。采用此工具是一项相对简单的任务，从长远来看可以对整体质量产生积极影响并降低维护成本。