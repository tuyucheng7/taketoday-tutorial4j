## 1. 概述

在本教程中，我们将研究[面向对象编程 (OOP)](https://www.baeldung.com/cs/class-object-differences)中的访问修饰符。此外，我们将介绍私有访问修饰符和受保护访问修饰符之间的主要区别。

## 2. OOP 中的访问修饰符

在面向对象的范例中，访问修饰符控制类、方法或属性的可访问性和安全性。我们也称它们为访问说明符。

因此，它们帮助我们为每个感兴趣的实体定义不同级别的封装。

我们为每个属性和方法设置一个访问修饰符，以指定系统的其他实体如何访问它们。大多数基于 OOP 的语言都有三个关键的访问修饰符：

1.  上市
2.  私人的
3.  受保护

![访问修饰符](https://www.baeldung.com/wp-content/uploads/sites/4/2023/01/Access_Modifiers.png)

 

## 3. 公共访问

当可以从任何地方访问某些内容时，我们使用public访问修饰符。例如，如果我们在Java中定义一个类为public，我们可以从以下地方访问它：

1.  导入后从项目中的任何位置(全局范围)
2.  来自同一班级
3.  在同一个包内
4.  在任何子类的同一个包中

因此，在所有修饰符中， public的范围最广。我们什么时候使用它？例如，假设我们有一个多模块软件，希望每个模块都生成特定格式的日志。我们可以公开记录器类或包，以便每个模块都可以访问其操作以按照规范格式化和生成日志。

## 4.私人访问

这是最安全的访问级别，它最大限度地提高了封装性。当我们希望实体(类、方法、属性)只能从我们定义它的同一位置访问时，我们将它设置为私有。

因此，私有属性仅限于其定义类。我们无法在定义它的类之外访问它。私有方法和类也是如此。

例如，假设我们的软件应用程序使用访问密钥连接到中央数据库以验证安全凭证。我们希望此访问密钥尽可能安全。因此，我们使用private修饰符使其只能通过定义类中精心设计的方法访问。

## 5. 受保护的访问

protected访问修饰符使用实体的继承级别来设置其对外界的可访问性。这意味着我们可以对我们希望在所有继承其定义类的类中可见的实体使用受保护的访问修饰符。然而，在[Java](https://www.baeldung.com/cs/languages-learn-data-structures)中，我们也可以在定义类的包中访问受保护的实体。

让我们来看一个例子。假设我们使用[消息队列](https://www.baeldung.com/cs/buffer)在应用程序的各个模块之间进行[进程间通信。](https://www.baeldung.com/cs/inter-process-communication)然后，我们可以为消息队列定义一个父类，并将操作create_queue()和delete_queue()设置为protected。这样，只有继承父队列类的类才能访问这些方法，因为它们是唯一需要使用它们的方法。使用这些子类的其他类只能通过子类提供的接口间接访问这些方法。

我们可以使类属性、方法或构造函数受保护。但是，我们不能将类标记为protected。

## 6.私有与受保护

现在让我们列举一下私有访问修饰符和受保护访问修饰符之间的主要区别。

### 6.1. 用法

它们之间最大的区别在于我们需要使用它们的时候。当我们想要限制对外部世界的访问并希望它只能从其定义类访问时，我们将方法或属性设为私有。

一个好的设计[启发式](https://www.baeldung.com/cs/heuristics)是将每个实体设为私有，除非它对外界没有用处。这有助于我们使我们的类更加封装、安全和健壮。这样，我们就可以在不破坏我们的应用程序的情况下更改其内部结构。

另一方面，我们拥有子类可见的受保护方法和属性。因此，一旦我们使一个类可继承，我们就必须小心设置可以从其子类覆盖和访问的内容以及必须不透明的内容。

### 6.2. 封装级别

这些访问修饰符具有不同的封装级别。

与受保护的实体相比，私有实体更加封装。我们在不需要IS-A关系的情况下使用私有访问。

另一方面，我们在对IS-A关系有明确要求的情况下使用受保护的访问。同时，我们希望将父类的受保护成员的可访问性限制在其子类中。

### 6.3. 安全隐患

当我们将属性或方法标记为受保护时，我们可能会引发安全漏洞。

假设我们有一个带有受保护方法implement_future()的类A。我们只是将此方法保留为占位符以实现一些未来的功能。几个月后，我们意识到无需为implement_future()编写任何代码。但是任何其他人都可以覆盖它，这会妨碍整体功能。

作为解决方案，开发人员必须正确注释所有受保护的变量和方法。

### 6.4. 代码可读性和可维护性

私有成员使代码更清晰、更易读且可维护性更高。这是因为私有属性和方法被限制在定义类中，所以我们在那里记录它们。但是，必须在我们覆盖它们的所有地方记录受保护的实体。

受保护访问维护的主要问题是它使我们的系统向后不兼容。假设我们有一个![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)具有受保护属性![a_{P}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f00a55014729bdd0881611a9d1c3590e_l3.svg)和受保护方法的类![方法_{P}()](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c3c2af18e8df13ff2bb687d33405147b_l3.svg)。现在，让![C_{child_1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-77a472849811400c5316c3425685b227_l3.svg)和![C_{child_2}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4df279358a5d74166789d49bec81ba1f_l3.svg)继承![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)。更重要的是，我们覆盖![方法_{P}()](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c3c2af18e8df13ff2bb687d33405147b_l3.svg)了这两个子类。现在，我们将![C_{child_1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-77a472849811400c5316c3425685b227_l3.svg)API 实现的一部分暴露给外部世界。因此，![方法_{P}()](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-c3c2af18e8df13ff2bb687d33405147b_l3.svg)成为公共类的[应用程序编程接口 (API)](https://www.baeldung.com/cs/apis-vs-abis)的一部分。这意味着如果以后我们想在类中进行更改![C](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ed12970f60569db1dfd9f13289854a0d_l3.svg)，我们不能在不破坏子类![C_{child_1}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-77a472849811400c5316c3425685b227_l3.svg)和的情况下这样做![C_{child_2}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-4df279358a5d74166789d49bec81ba1f_l3.svg)。

### 6.5. 商业考虑

大多数商业软件都限制了它们的设计和内部结构。因此，他们大多将核心组件设为私有。例如，微软产品的大部分类都是私有的。

另一方面，开源库采用受保护的修饰符。这有助于他们的用户扩展功能并将修改后的库提供回社区以供使用。例如，[TensorFlow](https://www.tensorflow.org/community/contribute/code)库使用受保护的属性和方法，以便开发人员可以根据他们的利基需求扩展他们的功能。

## 7.差异总结

这是一个快速总结：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-d14d625e20a3a469a3e0da3c07f6de85_l3.svg)

## 八、总结

在本文中，我们介绍了面向对象编程中的三种不同访问修饰符，并列举了私有访问修饰符和受保护访问修饰符之间的区别。

作为一种好的设计启发式方法，在设计大型库或大型基于模块的软件时，最好将关键实体标记为受保护。对于商业产品或单体项目，最好将关键实体标记为private。