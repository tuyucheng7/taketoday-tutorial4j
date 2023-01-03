## 1. 概述

Eclipse IDE是Java应用程序开发最常用的工具之一，它带有默认设置，使我们能够在IDE中立即构建和执行代码。

但是，当我们尝试在Eclipse中使用Maven进行构建时，这些默认设置有时是不够的。因此，我们会遇到构建错误。

在本快速教程中，我们将演示需要进行的配置更改，以便我们可以在IDE中构建基于Maven的Java项目。

## 2. Eclipse中的Java编译

在我们开始之前，让我们试着稍微了解一下Eclipse中的编译过程。

**Eclipse IDE捆绑了自己的Java编译器，称为Eclipse Compiler for Java(ECJ)**。这是一个增量编译器，可以只编译修改过的文件，而不必总是编译整个应用程序。

此功能使我们通过IDE进行的代码更改能够在我们键入时立即编译并检查错误。

由于使用了Eclipse的内部Java编译器，我们不需要在我们的系统中安装JDK来使Eclipse工作。

## 3. 在Eclipse中编译Maven项目

[Maven](https://www.baeldung.com/maven)构建工具帮助我们自动化软件构建过程，Eclipse作为插件与Maven捆绑在一起。但是，**Maven并没有与任何Java编译器捆绑在一起。相反，它期望我们安装了JDK**。

为了了解当我们尝试在Eclipse中构建Maven项目时会发生什么，假设Eclipse具有默认设置，让我们在Eclipse中打开任何Maven项目。

然后，在Package Explorer窗口中，右键单击项目文件夹，然后左键单击Run As > 3 Maven build：

<img src="../assets/img_8.png">

这将触发Maven构建过程，正如预期的那样，我们将遇到失败：

```bash
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.8.0:compile
  (default-compile) on project one: Compilation failure
[ERROR] No compiler is provided in this environment. Perhaps you are running on a JRE rather than a JDK?
```

错误消息表明Maven无法找到Java编译器，它只带有JDK而没有带有JRE。

## 4. Eclipse中的JDK配置

现在让我们修复Eclipse中的Maven构建问题。

首先，我们需要下载最新版本的JDK并将其安装到我们的系统中。

之后，让我们通过导航到Window > Preferences > Java> Installed JREs将JDK添加为Eclipse中的运行时：

<img src="../assets/img_9.png">

我们可以看到Eclipse已经配置了Java，但是，这是JRE而不是JDK，所以让我们继续接下来的步骤。

现在，让我们单击“Add...”按钮来调用“Add JRE”向导，这将要求我们选择JRE的类型。

在这里，我们选择了默认选项Standard VM：

<img src="../assets/img_10.png">

单击Next将带我们进入窗口，在该窗口中我们将JRE主目录的位置指定为JDK安装的主目录。

在此之后，向导将验证路径并获取其他详细信息：

<img src="../assets/img_11.png">

我们现在可以单击“Finish”来关闭向导。

这会将我们带回到Installed JREs窗口，在这里我们可以看到我们新添加的JDK并选择它作为我们在Eclipse中的运行时：

<img src="../assets/img_12.png">

让我们点击Apply and Close来保存我们的更改。

## 5. 测试JDK配置

现在让我们以**与以前相同的方式再次触发Maven构建**。

我们可以看到它是成功的：

```bash
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

## 6. 总结

在本教程中，我们了解了如何为Maven构建配置Eclipse，以便在IDE中正常工作。

通过进行这种一次性配置，我们能够利用IDE本身进行构建，而无需在外部设置Maven。