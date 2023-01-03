## 1. 简介

[Maven](https://www.baeldung.com/maven)是一个项目管理和理解工具，它基于项目对象模型的概念(也称为POM)。使用POM作为信息的中心部分，Maven可以管理项目的构建、报告和文档。

**Maven的很大一部分是依赖管理**，大多数开发人员在开发应用程序时都会与Maven的此功能进行交互。

Maven卓越的依赖管理提供了自动更新和依赖关闭，公司使用Maven进行依赖项管理的另一种方式是使用[自定义中央仓库](https://www.baeldung.com/maven-deploy-nexus)。通过这样做，开发人员可以在公司内的其他项目中使用自己的依赖项。

在本教程中，我们将学习如何查找Maven依赖项。

## 2. 什么是Maven依赖

在Maven的上下文中，**依赖项只是Java应用程序使用的JAR文件**。基于POM文件，Maven将下载JAR文件并将其添加到我们的Java路径中，然后Java将能够找到并使用JAR文件中的类。

**同样重要的是要注意，**[Maven有一个本地仓库，它下载所有依赖项](https://www.baeldung.com/maven-local-repository)。默认情况下，它位于{user-home}/.m2/repository中。

## 3. POM文件

POM文件使用XML语法，其中所有内容都在标签之间。

默认情况下，POM文件仅填充了我们的项目信息，为了添加我们的项目将使用的依赖项，我们需要添加dependencies部分：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>maven.dependency</artifactId>
    <version>1.0.0</version>

    <dependencies>
        ....
    </dependencies>
</project>
```

**只有在我们手动编辑POM文件的时候才需要这个**。

## 4. 查找Maven依赖的方法

在处理新项目或添加新功能时，我们可能会意识到我们需要向项目添加新的依赖项。让我们举一个简单的例子，我们需要添加JDBC依赖。

根据我们的IDE和设置，有不同的方法可以找到JDBC依赖项所需的详细信息。

### 4.1 IntelliJ IDEA

如果我们使用IntelliJ IDEA，我们可以通过以下几个步骤向项目的POM添加新的依赖项。

首先，我们打开POM文件，然后按下ALT+INSERT，然后点击Add dependency选项：

<img src="../assets/img.png">

然后，我们可以搜索需要的依赖并点击ADD：

<img src="../assets/img_1.png">

### 4.2 Eclipse

Eclipse IDE具有与IntelliJ类似的添加新依赖项的方法。

我们需要在package explorer中或打开文件后右键单击POM文件，然后转到Maven-> Add dependency选项：

<img src="../assets/img_2.png">

然后，我们可以搜索需要的依赖，然后点击OK：

<img src="../assets/img_3.png">

### 4.3 互联网搜索

**如果我们可以手动编辑POM文件，那么我们可以简单地在**[search.maven.org](https://search.maven.org/)**或Google上搜索以找到依赖项的所有详细信息**。

当转到[search.maven.org](https://search.maven.org/)时，我们只需在搜索栏中输入依赖项，我们就会找到大量版本的依赖项。除非我们项目中有其他限制，否则我们应该使用最新的稳定版本：

<img src="../assets/img_4.png">

**在我们的POM文件中，在dependency部分，只需粘贴找到的依赖项详细信息即可**。

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>5.3.21</version>
</dependency>

```

我们还可以使用和搜索许多类似于[search.maven.org](https://search.maven.org/)的其他网站。

## 5. 总结

在本教程中，我们了解了将Maven依赖项添加到项目的不同方法。

我们还学习了如何编辑POM文件，以便让Maven下载并使用添加的依赖项。