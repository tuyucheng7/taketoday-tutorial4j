## 1. 简介

[Project Jigsaw](https://openjdk.java.net/projects/jigsaw/)是一个伞形项目，具有针对两个方面的新功能：

-   Java语言中模块系统的引入
-   及其在JDK源代码和Java运行时中的实现

在本文中，我们为你介绍Jigsaw项目及其特性，最后用一个简单的模块化应用程序作为演示案例。

## 2. 模块化

简单地说，模块化是一种设计原则，可以帮助我们实现：

-   组件之间的松耦合
-   明确组件之间的契约和依赖关系
-   使用强封装的隐藏实现

### 2.1 模块化单元

现在问题来了，模块化的单位是什么？在Java世界中，尤其是在OSGi中，JAR被认为是模块化的单元。

JAR确实有助于将相关组件组合在一起，但它们也有一些限制：

-   JAR之间的显式契约和依赖关系
-   JAR中元素的弱封装

### 2.2 JAR地狱

JAR还有另一个问题，即JAR地狱。位于类路径上的JAR的多个版本导致ClassLoader从JAR加载第一个找到的类，结果非常意外。

JVM使用类路径的另一个问题是应用程序的编译会成功，但由于运行时类路径上缺少JAR，应用程序将在运行时失败并抛出ClassNotFoundException。

### 2.3 新的模块化单元

由于所有这些限制，当使用JAR作为模块化单元时，Java语言的创建者提出了一种新的语言结构，称为模块。这样，就有了一个全新的Java模块化系统。

## 3. Jigsaw项目

该项目的主要动机是：

-   为该语言创建一个模块系统：在[JEP 261下实现](https://openjdk.java.net/jeps/261)
-   将其应用于JDK源代码：在[JEP 201下实现](https://openjdk.java.net/jeps/201)
-   模块化JDK库：在[JEP 200下实现](https://openjdk.java.net/jeps/200)
-   更新运行时以支持模块化：在[JEP 220下实现](https://openjdk.java.net/jeps/220)
-   能够使用来自JDK的模块子集创建更小的运行时：在[JEP 282下实现](https://openjdk.java.net/jeps/282)

另一个重要的举措是封装JDK中的内部API，即那些在sun.包和其他非标准API下的API。这些API从未打算供公众使用，也从未计划维护。但是这些API的强大功能使Java开发人员可以利用它们来开发不同的库、框架和工具。为少数内部API提供了替代品，其他的已移至内部模块中。

## 4. 模块化的新工具

-   jdeps：帮助分析代码库以识别对JDK API和第三方JAR的依赖关系，它还提到了可以找到JDK API的模块的名称，这使得模块化代码库变得更容易
-   jdeprscan：帮助分析代码库中不推荐的API的使用情况
-   jlink：通过组合应用程序和JDK的模块来帮助创建更小的运行时
-   jmod：帮助处理jmod文件，jmod是一种用于打包模块的新格式。这种格式允许包含本机代码、配置文件和其他不适合JAR文件的数据

## 5. 模块系统架构

以该语言实现的模块系统支持这些作为顶级结构，就像包一样。开发人员可以将他们的代码组织成模块，并在各自的模块定义文件中声明它们之间的依赖关系。

名为module-info.java的模块定义文件包含：

-   其名称
-   它公开提供的软件包
-   它所依赖的模块
-   它使用的任何服务
-   它提供的服务的任何实现

上面提到的最后两项并不常用，它们仅在通过java.util.ServiceLoader接口提供和使用服务时使用。

模块的一般结构如下所示：

```shell
src
 |----cn.tuyucheng.taketoday.reader
 |     |----module-info.java
 |     |----cn
 |          |----tuyucheng
 |               |----taketoday
 |                    |----reader
 |                         |----Test.java
 |----cn.tuyucheng.taketoday.writer
      |----module-info.java
           |----cn
                |----tuyucheng
                     |----taketoday
                          |----writer
                               |----AnotherTest.java
```

上图定义了两个模块：cn.tuyucheng.taketoday.reader和cn.tuyucheng.taketoday.writer，它们中的每一个都在module-info.java中指定了其定义，并且代码文件分别位于cn/tuyucheng/taketoday/reader和cn/tuyucheng/taketoday/writer文件夹下。

### 5.1 模块定义术语

让我们看一些术语；我们将在定义模块时使用(即在module-info.java中)：

-   module：模块定义文件以这个关键字开头，后跟其名称和定义
-   requires: 用于表示它所依赖的模块；必须在此关键字之后指定模块名称
-   transitive：在requires关键字之后指定；这意味着任何依赖于模块定义requires transitive <modulename>的模块都会隐式依赖于<modulename>
-   export：用于指示模块中公开可用的包；必须在此关键字之后指定包名称
-   opens：用于指示只能在运行时访问的包，也可以通过反射API进行自省；这对于像Spring和Hibernate这样高度依赖反射API的库来说非常重要；opens也可以在模块级别使用，在这种情况下，整个模块都可以在运行时访问
-   uses：用于表示该模块正在使用的服务接口；必须在此关键字之后指定类型名称，即完整的类/接口名称
-   provides...with...：它们用于表示它为provides关键字后标识的服务接口提供实现，在with关键字之后标识

## 6. 简单的模块化应用

让我们创建一个简单的模块化应用程序，其中包含模块及其依赖关系，如下图所示：

<img src="../assets/img_1.png" align="left">

cn.tuyucheng.taketoday.student.model是根模块，它定义了模型类cn.tuyucheng.taketoday.student.model.Student，包含以下属性：

```java
public class Student {
    private String registrationId;
    //other relevant fields, getters and setters
}
```

它为其他模块提供了在cn.tuyucheng.taketoday.student.model包中定义的模型类，这是通过在module-info.java文件中定义它来实现的：

```java
module cn.tuyucheng.taketoday.student.model {
	exports cn.tuyucheng.taketoday.student.model;
}
```

cn.tuyucheng.taketoday.student.service模块提供了一个带有抽象CRUD操作的接口cn.tuyucheng.taketoday.student.service.StudentService ：

```java
public interface StudentService {
	String create(Student student);
	Student read(String registrationId);
	Student update(Student student);
	String delete(String registrationId);
}
```

它依赖于cn.tuyucheng.taketoday.student.model模块，并允许cn.tuyucheng.taketoday.student.service包中定义的类型可供其他模块使用：

```java
module cn.tuyucheng.taketoday.student.service {
	requires transitive cn.tuyucheng.taketoday.student.model;
	exports cn.tuyucheng.taketoday.student.service;
}
```

我们提供了另一个模块cn.tuyucheng.taketoday.student.service.dbimpl，它为cn.tuyucheng.taketoday.student.service模块中的接口提供了实现：

```java
public class StudentDbService implements StudentService {

	private static final Logger logger = Logger.getLogger("StudentDbService");

	public String create(Student student) {
		logger.log(Level.INFO, "Creating student in DB...");
		return student.getRegistrationId();
	}

	public Student read(String registrationId) {
		logger.log(Level.INFO, "Reading student from DB...");
		return new Student();
	}

	public Student update(Student student) {
		logger.log(Level.INFO, "Updating student in DB...");
		return student;
	}

	public String delete(String registrationId) {
		logger.log(Level.INFO, "Deleting student in DB...");
		return registrationId;
	}
}
```

它直接依赖于cn.tuyucheng.taketoday.student.service并传递地依赖于cn.tuyucheng.taketoday.student.model，其模块定义如下：

```java
module cn.tuyucheng.taketoday.student.service.dbimpl {
	requires transitive cn.tuyucheng.taketoday.student.service;
	exports cn.tuyucheng.taketoday.student.service.dbimpl;
	requires java.logging;
}
```

最后一个模块是一个客户端模块，它通过服务实现模块cn.tuyucheng.taketoday.student.service.dbimpl来执行其操作：

```java
public class StudentClient {

	public static void main(String[] args) {
		StudentService service = new StudentDbService();
		service.create(new Student());
		service.read("17SS0001");
		service.update(new Student());
		service.delete("17SS0001");
	}
}
```

它的模块定义为：

```java
module cn.tuyucheng.taketoday.student.client {
	requires cn.tuyucheng.taketoday.student.service.dbimpl;
}
```

## 7. 编译和运行案例

我们提供了用于在Windows和Linux平台上编译和运行上述模块的脚本，这些可以在仓库代码中该模块的根目录下找到到。Windows平台的执行顺序为：

1.  compile-student-model
2.  compile-student-service
3.  compile-student-service-dbimpl
4.  compile-student-client
5.  run-student-client

Linux平台的执行顺序非常简单：

1.  compile-modules
2.  run-student-client

在上面的脚本中，你需要了解下面的两个命令行参数：

-   --module-source-path
-   --module-path

Java 9取消了类路径(classpath)的概念，而是引入了模块路径(modulepath)，此路径是可以发现模块的位置。

我们可以使用命令行参数进行设置：–module-path。

为了同时编译多个模块，我们使用–module-source-path，此参数用于提供模块源代码的位置。

## 8. 模块系统在JDK源码中的应用

每个JDK安装包都提供了一个src.zip，这个压缩包包含JDKJavaAPI的代码库。如果你解压它，你会看到多个文件夹，其中少数以java开头，少数以javafx开头，其余的以jdk开头，这些文件夹每个都代表一个模块。

<img src="../assets/img.png">

以java开头的模块为JDK模块，以javafx开头的为JavaFX模块，其他以jdk开头的为JDK工具模块。

所有JDK模块和所有用户定义的模块都隐式地依赖于java.base模块，java.base模块包含常用的JDK API，如 Utils、Collections、IO、Concurrency等。以下是JDK模块的依赖关系图：

<img src="../assets/img_2.png">

你还可以直接查看JDK模块中的模块定义文件，以了解JDK本身是如何在module-info.java中定义模块语法的。

## 9. 总结

在本文中，我们着眼于创建、编译和运行一个简单的模块化应用程序。我们还看到了JDK源代码是如何模块化的。

还有一些更令人兴奋的功能，例如使用链接器工具创建更小的运行时-jlink和创建模块化jar以及其他功能。我们将在以后的文章中详细介绍这些功能。

Jigsaw项目是一个巨大的变化，我们将不得不等待并观察它如何被开发者生态系统所接受，尤其是工具和库创建者。