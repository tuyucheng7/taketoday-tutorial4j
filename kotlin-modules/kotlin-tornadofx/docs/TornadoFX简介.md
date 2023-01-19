## 一、简介

在本教程中，我们将了解什么是[TornadoFX](https://tornadofx.io/)以及如何在 Oracle JDK 和 OpenJDK 上设置它。我们还将描述架构并查看它的不同部分。最后，我们将概述如何使用它的一些小部件。

在我们开始之前，最好(但不是强制性的)熟悉[JavaFX](https://www.baeldung.com/javafx)。由于 TornadoFX 是 Kotlin 的 JavaFX 框架。它旨在释放 Kotlin 的强大功能，用于各种目的，例如定义视图、依赖项注入、委托属性、控制扩展功能和其他实用功能。

此外，请务必注意TornadoFX 尚不兼容 Java 9 和 10，因此我们将使用 Java 11。

## 2.设置

在本节中，我们将演示如何从头开始使用 Maven 和 Gradle 设置 TornadoFX 项目。

### 2.1. OpenJDK

由于我们很可能会在 Linux 上使用 OpenJDK，因此我们将从它开始。正如我们将看到的，使用 OpenJDK 设置 TornadoFX 非常棘手。

首先，我们需要将 OpenJDK 升级到版本 11。然后对于 Gradle，我们的build.gradle应该如下所示：

```groovy
plugins {
    id 'org.jetbrains.kotlin.jvm' version '1.5.10'
    id 'org.openjfx.javafxplugin' version '0.0.8'
    id 'application'
}

compileKotlin {
    kotlinOptions.jvmTarget = "11"
}

compileTestKotlin {
    kotlinOptions.jvmTarget = "11"
}
javafx {
    version = "11.0.2"
    modules = ['javafx.controls', 'javafx.graphics']
}
repositories {
    mavenCentral()
}

dependencies {
    // Align versions of all Kotlin components
    implementation platform('org.jetbrains.kotlin:kotlin-bom')

    implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk8'

    // Use the tornadofx
    implementation "no.tornado:tornadofx:1.7.20"
}
```

要查看最新版本的 TornadoFX 库，我们可以访问[TornadoFX Maven 页面。](https://mvnrepository.com/artifact/no.tornado/tornadofx)对于OpenJFX插件版本，我们可以查看[OpenJFX插件Maven页面](https://plugins.gradle.org/plugin/org.openjfx.javafxplugin)。对于 Maven，我们需要声明我们将在pom.xml
中使用的 TornadoFX 版本：

```xml
<properties>
    <tornadofx.version>1.7.20</tornadofx.version>
</properties>
```

然后我们需要添加[Tornado 库](https://mvnrepository.com/artifact/no.tornado/tornadofx)作为依赖项：

```xml
<dependency>
    <groupId>no.tornado</groupId>
    <artifactId>tornadofx</artifactId>
    <version>1.7.20</version>
</dependency>
```

[然后我们应该为JavaFX UI](https://mvnrepository.com/artifact/org.openjfx/javafx-base)工具包定义基本 API ：

```xml
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-base</artifactId>
    <version>17-ea+11</version>
</dependency>
```

为了使用ListView、Menubar等 JavaFX 控件，我们必须定义[javafx-controls](https://mvnrepository.com/artifact/org.openjfx/javafx-controls)依赖项。

```xml
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>17-ea+11</version>
</dependency>
```

最后，我们需要添加 JavaFX 的[maven 插件](https://mvnrepository.com/artifact/org.openjfx/javafx-maven-plugin)以及一些其他插件：

```xml
<plugins>
    <plugin>
        <!--Maven plugin to run JavaFX 11+ applications-->
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-maven-plugin</artifactId>
        <version>0.0.3</version>
        <configuration>
            <mainClass>MyApp</mainClass>
        </configuration>
    </plugin>
    <!--other libraries like kotlin-maven or maven-compiler-->
</plugins>
```

此外，我们需要在我们的src/main/kotlin文件夹中添加module-info.jar：

```java
module TFXSAMPLE {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires tornadofx;
    requires kotlin.stdlib;
    exports com.example.demo.app to javafx.graphics, tornadofx;
    exports com.example.demo.view to tornadofx;
}
```

### 2.2. 甲骨文JDK

如果我们使用的是 Oracle JDK，设置的处理方式会有所不同并且会简单得多，因为 JFX 模块库已经包含在内。

在 Gradle 中，我们需要提供 TornadoFX 库：

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'no.tornado:tornadofx:1.7.20'
}
```

对于 Maven，它是一样的，但我们只需要添加 TornadoFX 库：

```xml
<dependency>
    <groupId>no.tornado</groupId>
    <artifactId>tornadofx</artifactId>
    <version>1.7.20</version>
</dependency>
```

## 3. 应用基础

就像任何其他 Java 应用程序一样，TornadoFX 应用程序需要一个主类作为起点。因此，我们首先需要定义我们的应用程序。让我们创建一个名为app 的包，然后我们添加一个名为MyApp的 Kotlin 类。然后我们用初始视图类初始化应用程序。稍后我们将看到如何制作视图类。

现在，假设我们有一个名为HelloWorld的视图类：

```kotlin
class MyApp: App(HelloWorld::class)
```

## 4.查看类

视图包含我们需要向用户显示的任何信息表示。视图包含显示逻辑以及节点布局，类似于 JavaFX舞台。但与 JavaFX 不同的是，我们不使用单独的语言来定义我们的视图。TornadoFX 提供了使用原生 Kotlin 将类型安全和编译 CSS 引入游戏的选项。

现在让我们创建HelloWorld视图类，创建一个名为 view 的包并添加一个名为HelloWorld的Kotlin类：

```kotlin
import tornadofx.
class HelloWorld : View() {
    override val root = hbox {
        label("Hello world")
    }
}
```

我们还实例化了一个label，并将其赋值给hbox(Horizontal Box)，那么所有的label都会是一个root parent。

另外，请注意tornadofx.导入。这应该出现在我们所有的 TornadoFX 相关文件中。 这很重要，因为如果没有导入，IDE 将无法发现框架的某些功能。此导入启用了一些我们真的不想没有的高级扩展功能。

如果我们看一下上面的例子，我们会看到HelloWorld类是从View 派生的。现在，如果我们运行该应用程序，它应该如下所示：

![tfx 之前](https://www.baeldung.com/wp-content/uploads/sites/5/2021/07/tfx-before.png)

## 5.造型

TornadoFX 提供了将类型安全和编译后的 CSS 引入 JavaFX 的选项。我们可以方便地选择在自己的类中创建样式或在控件声明中内联创建样式。对于上面的例子，我们可以为标签定义一个样式：

```kotlin
class Styles : Stylesheet() {
    init {
        label {
            padding = box(10.px)
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }
    }
}
```

然后我们需要改变我们用来初始化应用程序的构造函数并将Styles类作为第二个参数传递：

```kotlin
class MyApp: App(MainView::class, Styles::class)
```

同样，如果我们运行该应用程序，我们将看到我们的标签样式：

![tfx 之后](https://www.baeldung.com/wp-content/uploads/sites/5/2021/07/tfx-after.png)
我们也可以使用内联样式 来实现同样的目的。此样式将覆盖应用于该节点的任何其他样式。当我们不想干扰一般样式声明时，这尤其有用：

```kotlin
label("Hello world") {
    style {
        padding = box(10.px)
        fontSize = 20.px
        fontWeight = FontWeight.BOLD
    }
}
```

## 6.查看注入

TornadoFX 极大地支持[依赖注入](https://www.baeldung.com/java-ee-cdi)。我们可以很容易地在这个框架中注入不同的层，它本身就支持它。接下来，我们将看到如何注入我们的观点。

可以通过find()或inject()方法完成视图注入。find和inject之间的区别在于，当使用inject()方法时，委托被延迟分配给给定的组件。

延迟分配是指我们第一次调用该组件。它在我们第一次调用注入资源上的函数时创建实际实例。但是对于find()方法，我们检索视图的[单例](https://www.baeldung.com/java-singleton)实例而不是委托。

然而，使用 inject 是首选方式，因为它允许我们的组件具有循环依赖并且具有延迟加载的优点：

```kotlin
class SampleView: View() {
    // Explicitly retrieve HeaderView
    private val headerView = find(HeaderView::class)
    
    // Create a lazy delegate
    private val footerView: FooterView by inject()

    override val root = borderpane {
        top = headerView.root
        bottom = footerView.root
    }
}
```

## 7. 控制器

将逻辑与视图分离可能是一个好主意的原因有很多，例如可测试性和可维护性。

在 TornadoFX 中，我们完全支持三层架构及其变体。实现它的方式类似于我们注入视图的方式。首先，我们定义一个控制器。然后我们让它派生自Controller超类，然后将它注入到我们的视图中：

```kotlin
class SampleView : View() {
    private val controller: SampleController by inject()
    private val input = SimpleStringProperty()

    override val root = form {
        fieldset {
            field() {
                textfield(input)
            }

            button("Post") {
                action {
                    controller.postApi(input.value)
                    input.value = ""
                }
            }
        }
    }
}

class SampleController: Controller() {
    fun postApi(inputValue: String) {
        println("Doing backend stuff with $inputValue")
    }
}
```

## 八、总结

显然，我们可以讨论该领域的许多其他主题，但本教程的主要目标是介绍 TornadoFX 并提供足够的入门信息。