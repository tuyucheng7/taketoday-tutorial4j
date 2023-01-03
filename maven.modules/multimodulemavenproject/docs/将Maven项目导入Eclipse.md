## 1. 概述

在本教程中，我们将了解如何将现有的Maven项目导入Eclipse。为此，**我们可以使用Maven的Eclipse插件或Apache Maven Eclipse插件**。

## 2. Eclipse和Maven项目设置

对于我们的示例，我们将使用从[Eclipse下载](https://www.eclipse.org/downloads/)页面获得的最新版本的Eclipse，版本2022-09(4.21.0)。

### 2.1 示例Maven项目

对于我们的示例，我们将使用来自我们的[GitHub仓库](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/maven-modules/multimodulemavenproject)的多模块Maven项目，一旦我们克隆了仓库或下载了项目，我们的多模块Maven项目的根目录应该如下所示：

```bash
|--multimodulemavenproject
    |--daomodule
    |--entitymodule
    |--mainappmodule
    |--userdaomodule
    |--pom.xml
    |--README.md
```

### 2.2 Maven项目中的小改动

我们的多模块Maven项目本身就是一个子项目，因此，为了限制我们练习的范围，我们需要对multimodulemavenproject目录中的pom.xml进行一些小的更改，这将是我们的项目根目录。在这里，让我们**删除引用multimodulemavenproject的父级的行**：

```xml
<parent>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>taketoday-tutorial4j</artifactId>
    <version>1.0.0</version>
    <relativePath>../../</relativePath>
</parent>
```

去掉这些行后，我们就可以将Maven项目导入到Eclipse中了。

## 3. 使用Maven的m2e Eclipse插件导入

让我们**使用菜单路径File::Import::Maven::Existing Maven Projects**将Maven项目导入Eclipse，我们可以通过单击“File”菜单下的“Import”选项开始：

<img src="../assets/img.png">

然后，让我们展开Maven文件夹，选择Existing Maven Projects，然后单击Next按钮：

<img src="../assets/img_1.png">

最后，让我们提供Maven项目的根目录路径，然后单击“Finish”按钮：

<img src="../assets/img_2.png">

在这一步之后，我们应该能够在Eclipse中看到Package Explorer视图：

<img src="../assets/img_3.png">

这个视图可能有点混乱，因为我们是分开查看所有模块的，而不是以分层方式查看的，这是由于Eclipse中的默认视图Package Explorer。但是，我们可以轻松地将视图切换到Project Explorer并以树状结构查看多模块项目：

<img src="../assets/img_4.png">

Maven项目的顺利导入是由**Maven的Eclipse插件**[m2e](https://projects.eclipse.org/projects/technology.m2e)实现的，我们不必将它单独添加到我们的Eclipse，因为它**内置于Eclipse安装中**，可以通过路径Help::About Eclipse IDE::Installation Details::Installed Software查看：

<img src="../assets/img_5.png">

如果我们有一个没有内置m2e插件的旧版本Eclipse，我们总是可以使用Eclipse Marketplace添加这个插件。

## 4. Apache Maven Eclipse插件

Apache Maven Eclipse插件还可用于生成用于项目的Eclipse IDE文件(\*.classpath、\*.project、\*wtpmodules和.settings文件夹)，**不过这个插件现在已经被Maven淘汰了，推荐使用Eclipse的m2e插件**。可以在[Apache Maven插件页面](https://maven.apache.org/plugins/maven-eclipse-plugin/)上找到更多详细信息。

## 5. 总结

在本教程中，我们了解了将现有Maven项目导入Eclipse的两种方法。由于Apache Maven Eclipse插件现已停用，**我们应该使用Maven的Eclipse插件m2e**，它内置于最新版本的Eclipse中。