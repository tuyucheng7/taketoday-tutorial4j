## 1. 概述

在本文中，我们介绍如何))在Gradle中创建自定义任务))，我们将使用构建脚本或自定义任务类型演示新的任务定义。

Gradle的介绍请看[这篇文章](Gradle简介.md)，它包含Gradle的基础知识以及对本文最重要的内容 - Gradle任务的介绍。

## 2. build.gradle内部的自定义任务定义

要创建一个简单的Gradle任务，我们需要将其定义添加到build.gradle文件中：

```groovy
task welcome {
    doLast {
        println 'Welcome in the Tuyucheng!'
    }
}
```

上述任务的主要作用只是打印文本“Welcome in the Tuyucheng!”，我们可以通过运行))gradle tasks –all))命令来检查这个任务是否可用：

```shell
gradle tasks --all
```

该任务会显示在other任务组下的列表中：

```shell
Other tasks
-----------
welcome
```

它可以像任何其他Gradle任务一样执行：

```shell
gradle welcome
```

输出如预期一样，打印“Welcome in the Tuyucheng!”信息。

备注：如果参数–all未设置，则属于“other”类别的任务不可见。自定义Gradle任务可以属于与“other”不同的组，并且可以包含描述。

## 3. 设置组和描述

有时按功能对任务进行分组很方便，因此它们在一个类别下可见。))我们可以为自定义任务快速设置组，只需定义一个group属性))：

```groovy
task welcome {
    group 'Sample category'
    doLast {
        println 'Welcome on the Tuyucheng!'
    }
}
```

现在，当我们运行Gradle命令列出所有可用任务时(不需要–all参数)，我们会在新组下看到我们的任务：

```shell
Sample category tasks
---------------------
welcome
```

))通过，一个任务实现的功能对我们来说应该是可寻的；我们可以创建一个包含简短信息的描述))：

```groovy
task welcome {
    group 'Sample category'
    description 'Tasks which shows a welcome message'
    doLast {
        println 'Welcome in the Tuyucheng!'
    }
}

```

当我们打印可用任务的列表时，输出如下所示：

```shell
Sample category tasks
---------------------
welcome - Tasks which shows a welcome message

```

))这种任务定义称为ad-hoc定义))。

此外，创建一个可重用定义的可定制任务是有益的。我们将介绍如何从一个类型创建一个任务，以及如何为这个任务的用户提供一些自定义。

## 4. 在build.gradle中定义Gradle任务类型

上面的“welcome”任务无法自定义，因此在大多数情况下，它不是很有用。我们可以运行它，但是如果我们在不同的项目(或子项目)中需要它，那么我们需要并粘贴它的定义。

我们可以))通过创建任务类型来快速启用任务的自定义))；仅在构建脚本中定义了一个任务类型：

```groovy
class PrintToolVersionTask extends DefaultTask {
    @Input
    String tool

    @TaskAction
    void printToolVersion() {
        switch (tool) {
            case 'java':
                println System.getProperty("java.version")
                break
            case 'groovy':
                println GroovySystem.version
                break
            default:
                throw new IllegalArgumentException("Unknown tool")
        }
    }
}
```

))自定义任务类型是一个简单的Groovy类，它扩展了DefaultTask)) - 定义标准任务实现的类。我们可以扩展其他任务类型，但在大多数情况下，DefaultTask类是合适的选择。

))PrintToolVersionTask任务包含可以通过此任务的实例自定义的tool属性))：

```groovy
String tool
```

我们可以根据需要添加任意数量的属性，记住，它只是一个简单的Groovy类字段。

此外，))它还包含用@TaskAction注解标注的方法，该方法定义了这个任务需要做什么))。在这个简单的示例中，它打印已安装Java或Groovy的版本，具体取决于给定的参数值。

要基于创建的任务类型运行自定义任务，我们需要))创建此类型的新任务实例))：

```groovy
task printJavaVersion(type: PrintToolVersionTask) {
    tool 'java'
}

```

最重要的部分是：

-   我们的任务是一个PrintToolVersionTask类型，因此))当执行时，它将触发在用@TaskAction注解的方法中定义的action))
-   我们添加了一个自定义tool属性值(java)，将由PrintToolVersionTask使用

当我们运行上述任务时，输出如预期一样打印你机器上安装的Java版本：

```groovy
> Task :gradle:printJavaVersion
17.0.5
```

下面我们创建一个打印已安装版本的Groovy的任务：

```groovy
task printGroovyVersion(type : PrintToolVersionTask) {
    tool 'groovy'
}

```

它使用与我们之前定义的相同的任务类型，但它指定不同的tool属性值。当我们执行这个任务时，它会打印Groovy版本：

```groovy
> Task :gradle:printGroovyVersion
3.0.9
```

))如果我们没有太多的自定义任务，那么我们可以直接在build.gradle文件中定义它们))(就像我们上面所做的那样)。但是，如果需要编写很多自定义任务，那么我们的build.gradle文件会变得臃肿、难以阅读。

幸运的是，Gradle提供了一些解决方案。

## 5. 在buildSrc文件夹中定义任务类型

))我们可以在位于根项目级别的buildSrc文件夹中定义任务类型))，Gradle编译内部的所有内容并将类型添加到类路径中，以便我们的构建脚本可以使用它。

我们之前定义的任务类型(PrintToolVersionTask)可以移动到buildSrc/src/main/groovy/cn/tuyucheng/taketoday/PrintToolVersionTask.groovy中。我们只需要从Gradle API中添加一些import语句到移动的类中。

我们可以在buildSrc文件夹中定义无限数量的任务类型；它更易于维护、阅读，并且可以将任务类型声明与任务实例化分离。我们可以像使用在构建脚本中直接定义的类型一样使用这些任务类型，只需记住添加适当的import语句。

## 6. 在插件中定义任务类型

我们可以在自定义Gradle插件中定义自定义任务类型。请参考[这篇文章](编写自定义Gradle插件.md)，它描述了如何定义一个自定义的Gradle插件，该插件定义于：

-   build.gradle文件
-   buildSrc文件夹作为其他Groovy类

当我们定义此插件的依赖项时，这些自定义任务将可用于我们的构建。请注意，ad-hoc任务也可用，不仅是自定义任务类型。

## 7. 总结

在本教程中，我们介绍了如何在Gradle中创建自定义任务。你可以在build.gradle文件中使用很多插件，这些插件将提供你需要的许多自定义任务类型。