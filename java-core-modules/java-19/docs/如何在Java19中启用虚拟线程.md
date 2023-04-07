## 1. 简介

虚拟线程和结构化并发都处于孵化/预览状态。只有在运行或编译代码时设置了一些额外的标志时，才能在项目中使用它们。在本文中我们将探讨需要在IntelliJ中设置哪些标志以及如何设置。

## 2. 在Java 19中启用虚拟线程

要在Java中使用虚拟线程，你只需要在运行代码时添加此标志/VM选项：

```shell
--enable-preview
```

你必须更改运行配置才能在IntelliJ中添加此VM选项。你可以在右上角的运行图标左侧找到你的配置。

1.  单击下拉菜单并选择“Edit Configurations...”
2.  选择要编辑的配置
3.  在“Build and run”部分，点击“Modify options”
4.  选择“Add VM options”
5.  一个新的文本框将添加到配置屏幕
6.  在文本框中，添加以下不带引号的文本：“-–enable-preview”
7.  单击Apply或OK

使用此配置运行代码时，你可以使用虚拟线程。

假设你希望默认将这些标志用于所有(未来)配置。在这种情况下，你可以按照相同的步骤更改配置模板。要在第2步中执行此操作，请不要选择配置，而是选择弹出屏幕左下角的“Edit configuration template...”按钮。

## 3. 在Java 19中启用结构化并发

要启用结构化并发，我们有两种选择。我们可以添加一个模块文件或向编译器和运行配置添加额外的参数。

### 3.1 使用模块文件启用结构化并发

启用结构化并发的最简单方法是使用模块文件。为此，你需要在java目录中创建一个module-info.java文件。完整的路径看起来像这个：src/main/java/module-info.java。

将以下示例的内容复制并粘贴到模块文件中：

```java
module yourProjectName {
    requires jdk.incubator.concurrent;
}
```

现在可以开始在你的项目中使用结构化并发，但请记住启用虚拟线程！

### 3.2 使用编译器和运行参数启用结构化并发

启用结构化并发的第二个选项是为编译器和运行配置使用参数。

#### 3.2.1 编译参数

这些是Java编译器所需的参数：

```shell
--enable-preview --add-modules jdk.incubator.concurrent
```

要将这些参数传递给IntelliJ中的java编译器，你必须执行以下步骤：

1.  选择左上角的“File”
2.  点击“Settings”
3.  打开“Build, Execution, Deployment”
4.  打开“Compiler”
5.  点击“Java Compiler”
6.  在屏幕的下半部分，你会看到一个名为“Additional command line parameters:”的文本框
7.  粘贴以下不带引号的文本：“–enable-preview –add-modules jdk.incubator.concurrent”
8.  单击Apply或OK

#### 3.2.2 运行参数

我们还需要将标志/选项添加到运行配置中。

1.  单击下拉菜单并选择“Edit Configurations...”
2.  选择要编辑的配置
3.  在“Build and run”部分，点击“Modify options”
4.  选择“Add VM options”
5.  一个新的文本框将添加到配置屏幕
6.  在文本框中添加以下不带引号的文本：“–enable-preview –add-modules jdk.incubator.concurrent”
7.  单击Apply或OK

## 4. 总结

在本文中，我们研究了如何为我们的项目启用虚拟线程和结构化并发。我们讨论了你需要传递的不同参数以及如何在IntelliJ中设置这些参数。