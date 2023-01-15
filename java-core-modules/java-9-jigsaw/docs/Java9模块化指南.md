## 1. 概述

Java 9在包之上引入了一个新的抽象级别，正式称为Java平台模块系统(JPMS)，或简称为“模块”。

在本教程中，我们将介绍这个新的系统并讨论其各个方面，并构建一个简单的项目来演示我们将在本教程中学习的所有概念。

## 2. 什么是模块？

首先，我们需要了解模块是什么，然后才能了解如何使用它们。

模块是一组密切相关的包和资源，以及一个新的模块描述符文件的集合。换句话说，它是一个“Java包的包”抽象，允许我们使我们的代码更加可重用。

### 2.1 包

模块中的包与我们自Java诞生以来一直使用的Java包相同。当我们创建一个模块时，我们在内部将代码组织在包中，就像我们之前对任何其他项目所做的那样。

除了组织我们的代码外，包还用于确定哪些代码可以在模块之外公开访问。我们将在本文后面花更多时间讨论这个问题。

### 2.2 资源

每个模块负责其资源，如媒体或配置文件。以前，我们会将所有资源放在项目的根级别，并手动管理哪些资源属于应用程序的不同部分。使用模块，我们可以将所需的图像和XML文件与需要它的模块一起发送，从而使我们的项目更易于管理。

### 2.3 模块描述符

当我们创建一个模块时，我们需要包含一个描述符文件，它定义了新模块的几个方面：

-   Name：模块的名称
-   Dependencies：此模块所依赖的其他模块列表
-   Public Packages：我们希望从模块外部访问的所有包的列表(即该模块允许对其他模块可见的包)
-   Services Offered：我们可以提供给其他模块使用的服务实现
-   Services Consumed：允许当前模块成为服务的消费者
-   Reflection Permissions：明确允许其他类使用反射来访问包的私有成员

模块命名规则与我们命名包的方式类似(允许使用点，不允许使用破折号)，使用项目名风格(my.module)或反向域名( cn.tuyucheng.taketoday.mymodule)风格名称是比较常见的，我们在本教程中使用项目名风格。

我们需要列出我们想要公开的所有包，因为默认情况下所有包都是模块私有的，即该模块中的包只能由该模块访问。

反射也是如此，默认情况下，我们不能对从其他模块导入的类使用反射。在本文后面，我们会介绍如何使用模块描述符文件配置反射。

### 2.4 模块类型

新的模块系统中有四种类型的模块：

-   系统模块：这些是我们在运行list-modules命令时列出的模块，它们包括Java SE和JDK模块。
-   应用程序模块：这些模块是我们在决定使用模块系统时通常想要构建的，换句话说是我们自己定义的模块。它们在包含在组装JAR中的编译后的module-info.class文件中命名和定义。
-   原子模块：我们可以通过将现有的JAR文件添加到模块路径来包含非官方模块，模块的名称将派生自JAR的名称。原子模块将对路径加载的每个其他模块具有完全读取权限。
-   未命名模块：当类或JAR加载到类路径而不是模块路径时，它会自动添加到未命名模块中。它是一个包罗万象的模块，用于维护与先前编写的Java代码的向后兼容性。

### 2.5 分发

模块可以通过以下两种方式之一分发：作为JAR文件或作为“分解”的编译项目。当然，这与任何其他Java项目都一样，因此也就不足为奇了。

我们可以创建由一个“主应用程序”和几个库模块组成的多模块项目，但是我们必须小心，因为每个JAR文件只能有一个模块。当我们设置构建文件时，我们需要确保将项目中的每个模块打包为一个单独的jar。

## 3. 默认模块

当我们安装Java 9或以上的版本时，我们可以看到JDK现在有了新的结构。他们已将所有原始软件包移至新的模块系统中，我们可以通过在命令行中输入以下命令来查看这些模块：

```bash
java --list-modules
```

这些模块分为四大类：java、javafx、jdk和Oracle。

java模块是核心SE语言规范的实现类。

javafx模块是FX UI库。

JDK本身需要的任何东西都保存在jdk模块中。

最后，Oracle特定的任何东西都在oracle模块中。

## 4. 模块声明

要构建一个模块，我们需要在包的根目录下添加一个名为module-info.java的特殊文件。该文件称为模块描述符，包含构建和使用我们的新模块所需的所有数据。

我们用一个声明构造模块，该声明的主体要么为空，要么由模块指令组成：

```java
module myModuleName {
    // all directives are optional
}
```

我们以module关键字开始模块声明，然后是模块的名称，该模块将使用此声明，但我们通常需要更多信息，这就是模块指令的用武之地。

### 4.1 Requires

我们介绍的第一个指令是requires，这个模块指令允许我们声明模块依赖项：

```java
module my.module {
    requires module.name;
}
```

现在，my.module对module.name有运行时和编译时依赖，当我们使用这个指令时，我们的模块可以访问从依赖项导出的所有公共类型。

### 4.2 Requires Static

有时我们编写的代码引用了另一个模块，但我们库的用户永远不想使用。例如，我们可以编写一个工具方法，当存在另一个日志模块时，它可以格式化地打印我们的内部程序状态。但是，并不是我们库的每个消费者都想要这个功能，他们也不想包含一个额外的日志库。

在这些情况下，我们希望使用可选依赖项。通过使用requires static指令，我们声明的是一个仅编译时的依赖项：

```java
module my.module {
    requires static module.name;
}
```

### 4.3 Requires Transitive

我们通常会引用大量第三方库来开发我们的功能，但是，我们需要确保任何引入我们代码的模块也会引入这些额外的“传递”依赖项，否则它们不能正常使用。

幸运的是，我们可以使用requires transitive指令来强制任何下游消费者也读取我们所需的依赖项：

```java
module my.module {
    requires transitive module.name;
}
```

现在，当别人需要使用my.module时，也意味着它们依赖了module.name模块，而不必明确指定requires module.name语句。

### 4.4 Exports

默认情况下，模块不向其他模块公开其任何API，这种强大的封装是最初创建模块系统的关键动力之一。我们的代码明显更加安全，但是如果我们希望API可用，现在我们需要明确地向全世界开放我们的API。

我们使用export指令来公开指定包的所有公共成员：

```java
module my.module {
    exports com.my.package.name;
}
```

现在，当有人需要使用my.module时，他们可以访问com.my.package.name包中的公共类型，但不能访问任何其他包。

### 4.5 Exports...To

我们可以使用exports向全世界公开我们的公共类。但是，如果我们不希望全世界都访问我们的API怎么办？

我们可以使用exports...to指令来限制哪些模块可以访问我们的API。与export指令类似，我们将包声明为已导出，但是，我们还列出了只允许哪些模块将此包作为requires导入。下面是它的使用方式：

```java
module my.module {
    export com.my.package.name to com.specific.package;
}
```

### 4.6 Uses

服务是可以由其他类使用的特定接口或抽象类的实现，我们使用uses指令来指定我们的模块使用的服务。

请注意，我们指定的类名是服务的接口或抽象类，而不是实现类：

```java
module my.module {
    uses class.name;
}
```

这里还要注意，requires指令和uses指令之间是有区别的。我们可能需要一个模块来提供我们想要使用的服务，但该服务从它的传递依赖项之一实现了一个接口。

为了以防万一，我们没有强制我们的模块需要所有传递依赖项，而是使用uses指令将所需的接口添加到模块路径中。

### 4.7 Provides...With

一个模块也可以是其他模块可以使用的服务提供者。该指令的第一部分是provides关键字，后面指定服务接口或抽象类的名称。接下来是with指令，我们在with后面提供实现类名称，该类名称要么实现接口，要么扩展抽象类。

看下面的例子：

```java
module my.module {
    provides MyInterface with MyInterfaceImpl;
}
```

### 4.8 Open

我们前面提到，封装是设计这个模块系统的驱动力。在Java 9之前，可以使用反射来获取包中的每个类和成员，甚至是私有的，从某种意义上来说没有什么是真正封装的，这会给库的开发人员带来各种各样的问题。

因为Java 9强制执行强封装，我们现在必须显式地授予其他模块对我们的类进行反射的权限。

如果我们想继续像旧版本的Java那样允许完全反射，我们可以简单地打开整个模块：

```java
open module my.module {
}
```

### 4.9 Opens

如果我们需要允许私有类型的反射，但我们不想暴露所有代码，我们可以使用opens指令来暴露特定的包。

但请记住，这将向全世界打开包，因此请确保这是你想要的：

```java
module my.module {
    opens com.my.package;
}
```

### 4.10 Opens...To

好吧，虽然反射有时很好，但我们仍然希望通过封装获得尽可能多的安全性。我们可以选择性地将我们的包打开到预先批准的模块列表中，在这种情况下，使用opens...to指令：

```java
module my.module {
    opens com.my.package to moduleOne, moduleTwo, etc.;
}
```

## 5. 命令行参数

到目前为止，Maven和Gradle已经添加了对Java 9模块的支持，因此你无需手动构建项目。但是，了解如何从命令行使用模块系统仍然很有意义。

我们将在下面的完整示例中使用命令行来帮助巩固整个模块系统在实践中的工作方式。

-   module-path：我们使用–module-path参数来指定模块路径，这是包含你的模块的一个或多个目录的列表。
-   add-reads：我们可以使用与requires指令等效的命令行，而不是依赖于模块声明文件；--add-reads。
-   add-exports：exports指令的命令行方式。
-   add-opens：替换模块声明文件中的open子句。
-   add-modules：将模块列表添加到默认模块集中。
-   list-modules：打印所有模块及其版本字符串的列表。
-   patch-module：添加或覆盖模块中的类。
-   illegal-access=permit|warn|deny：要么通过显示单个全局警告来放松强封装，要么显示每个警告，要么因错误而失败。默认值为permit。

## 6. 可见性

许多库依靠反射来发挥它们的作用(参考JUnit和Spring)。默认情况下，在Java 9中，我们只能访问导出包中的公共类、方法和字段，即使我们使用反射来访问非公共成员并调用setAccessible(true)，我们也无法访问这些成员。

我们可以使用open、opens和opens...to参数来授予仅限运行时的反射访问权限。请注意，这仅适用于运行时！我们将无法针对私有类型进行编译，无论如何我们都不应该这样做。

如果我们必须访问一个模块进行反射，并且我们不是该模块的所有者(即，我们不能使用opens...to指令)，那么可以使用命令行–add-opens参数来允许自己的模块在运行时对锁定的模块进行反射访问。唯一需要注意的是，你需要能够访问用于运行模块的命令行参数，才能正常工作。

## 7. 综合案例

现在我们知道了模块是什么以及如何使用它们，让我们构建一个简单的项目来演示我们刚刚学到的所有概念。

为了简单起见，我们不会使用Maven或Gradle，而是依靠命令行工具来构建我们的模块。

### 7.1 构建项目

首先，我们需要建立我们的项目结构，我们将创建几个目录来组织我们的文件。

首先创建项目文件夹：

```shell
mkdir module-project
cd module-project
```

这是整个项目的根目录，我们在该目录中添加所有文件，例如Maven或Gradle构建文件、其他源目录和资源。

我们还添加了一个目录来保存所有特定于项目的模块。接下来，我们创建一个模块目录：

```bash
mkdir simple-modules
```

以下是我们的项目结构：

```powershell
module-project
|- // src 如果我们使用默认包
|- // 构建文件也在这个级别
|- simple-modules
  |- hello.modules
    |- cn
      |- tuyucheng
        |- taketoday
          |- modules
            |- hello
  |- main.app
    |- cn
      |- tuyucheng
        |- tuyucheng
          |- modules
            |- main
```

### 7.2 第一个模块

现在我们已经有了基本的目录结构，让我们添加第一个模块。在simple-modules目录下，创建一个名为hello.modules的新目录。我们可以将其命名为任何我们想要的名称，但要遵循包命名规则(即分隔单词的点等)。如果需要，我们甚至可以使用主包的名称作为模块名称，但通常，我们使用与创建该模块JAR时使用的名称相同的名称。

在我们的新模块下，我们可以创建所需的包。在我们的例子中，我们创建以下包结构：

```java
cn.tuyucheng.taketoday.modules.hello
```

接下来，在这个包中创建一个名为HelloModules.java的类，代码非常简单：

```java
package cn.tuyucheng.taketoday.modules.hello;

public class HelloModules implements HelloInterface {
    
	public static void doSomething() {
		System.out.println("Hello, Modules!");
	}
}
```

最后，在hello.modules根目录中，添加模块描述符文件module-info.java：

```java
module hello.modules {
	exports cn.tuyucheng.taketoday.modules.hello;
}
```

为了保持简单，我们所做的只是导出cn.tuyucheng.taketoday.modules.hello包的所有公共成员。

### 7.3 第二个模块

我们的第一个模块目前已经构建完成，但它没有什么实际用途，因此现在我们创建第二个使用它的模块。

在我们的simple-modules目录下，创建另一个名为main.app的模块目录，并添加模块描述符文件：

```java
module main.app {
	requires hello.modules;
}
```

我们不需要向外界暴露任何东西，相反，我们需要做的只是依赖于我们的第一个模块，这样我们就可以访问第一个模块导出的公共类。

现在我们可以创建一个使用它的应用程序。首先创建一个新的包结构cn.tuyucheng.taketoday.modules.main，然后创建一个名为MainApp.java的类文件。

```java
package cn.tuyucheng.taketoday.modules.main;

import cn.tuyucheng.taketoday.modules.hello.HelloModules;

public class MainApp {
    
    public static void main(String[] args) {
        HelloModules.doSomething();
    }
}
```

这就是我们演示模块系统所需的所有代码，我们的下一步是从命令行构建和运行这些代码。

### 7.4 构建模块

为了构建我们的项目，我们可以创建一个简单的bash脚本并将其放在项目的根目录中。我们创建一个名为compile-simple-modules.sh的文件：

```bash
#!/usr/bin/env bash
javac -d outDir --module-source-path simple-modules $(find simple-modules -name ".java")
```

该命令有两个部分，javac和find命令。find命令只是输出所有simple-modules目录下的.java文件，然后我们可以将该列表直接提供给Java编译器。

与旧版本的Java不同，我们唯一需要做的就是提供一个module-source-path参数来通知编译器它正在构建模块。

运行此命令后，会在项目根目录中生成一个outDir文件夹，其中包含两个已编译的模块。

### 7.5 运行代码

现在我们终于可以运行我们的代码来验证模块是否正常工作，在项目的根目录中创建另一个文件：run-simple-module-app.sh。

```bash
#!/usr/bin/env bash
java --module-path outDir -m main.app/cn.tuyucheng.taketoday.modules.main.MainApp
```

要运行一个模块，我们必须至少提供模块路径(module-path)和主类。如果一切正常，你应该可以看到以下输出：

```bash
>$ ./run-simple-module-app.sh 
Hello, Modules!
```

### 7.6 添加服务

现在我们对如何构建模块有了基本的了解，让我们让它变得更复杂一些。本小节中，我们介绍如何使用provide...with和uses指令。

首先在hello.modules模块中定义一个名为HelloInterface.java的新文件，它是一个接口：

```java
public interface HelloInterface {
	void sayHello();
}
```

为了简单起见，我们使用之前的HelloModules.java类来实现这个接口：

```java
public class HelloModules implements HelloInterface {
	public static void doSomething() {
		System.out.println("Hello, Modules!");
	}

	public void sayHello() {
		System.out.println("Hello!");
	}
}
```

这就是我们创建服务所需要做的一切，其中HelloInterface就是我们可以提供给其他模块的服务。

现在，我们需要告诉全世界我们的模块提供了这项服务。因此，更新hello.modules模块的module-info.java文件为：

```java
module hello.modules {
	exports cn.tuyucheng.taketoday.modules.hello;
	provides cn.tuyucheng.taketoday.modules.hello.HelloInterface with cn.tuyucheng.taketoday.modules.hello.HelloModules;
}
```

或者

```java
import cn.tuyucheng.taketoday.modules.hello.HelloInterface;
import cn.tuyucheng.taketoday.modules.hello.HelloModules;

module hello.modules {
	exports cn.tuyucheng.taketoday.modules.hello;
	provides HelloInterface with HelloModules;
}
```

如我们所见，我们声明了服务接口以及实现它的类。接下来，我们需要使用这个服务，在main.app模块中，我们将以下内容添加到module-info.java文件中：

```java
uses cn.tuyucheng.taketoday.modules.hello.HelloInterface;
```

或者：

```java
import cn.tuyucheng.taketoday.modules.hello.HelloInterface;

uses HelloInterface;
```

最后，在程序的main方法中，我们可以通过[ServiceLoader](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html)使用这个服务：

```java
public class MainApp {

	public static void main(String[] args) {
		Iterable<HelloInterface> services = ServiceLoader.load(HelloInterface.class);
		HelloInterface service = services.iterator().next();
		service.sayHello();
	}
}
```

编译并运行：

```java
#> ./run-simple-module-app.sh 
Hello, Modules!
Hello!
```

我们使用这些指令来更明确地说明如何使用我们的代码。我们可以将实现放在私有包中，同时在公共包中公开接口。这使我们的代码更加安全，并且几乎没有额外的开销。

你可以继续尝试使用其他一些指令，以了解有关模块及其工作方式的更多信息。

## 8. 将模块添加到未命名模块

未命名的模块概念类似于默认包。因此，它不被认为是真正的模块，但可以被视为默认模块。如果一个类不是命名模块的成员，那么它将自动被视为该未命名模块的一部分。

有时，为了确保模块图中的特定平台、库或服务提供者模块，我们需要将模块添加到默认根集中。例如，当我们尝试使用Java 9编译器按原样运行Java 8程序时，我们可能需要添加模块。

通常，将命名模块添加到默认根模块集中的参数是–add-modules <module>(,<module>)，其中<module>是模块名称。

例如，要提供对所有java.xml.bind模块的访问，语法应该为：

```bash
--add-modules java.xml.bind
```

要在Maven中使用它，我们可以将其添加到maven-compiler-plugin配置中：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.0</version>
    <configuration>
        <source>9</source>
        <target>9</target>
        <compilerArgs>
            <arg>--add-modules</arg>
            <arg>java.xml.bind</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

## 9. 总结

在这份尽可能详细的指南中，我们重点介绍了新的Java 9模块系统的基础知识。

我们首先给出模块的概念，接下来，我们介绍了如何查看JDK中包含哪些模块，并详细介绍了模块声明文件的使用。

最后，我们将所有的概念应用于实践，并创建了一个构建在模块系统之上的简单应用程序。