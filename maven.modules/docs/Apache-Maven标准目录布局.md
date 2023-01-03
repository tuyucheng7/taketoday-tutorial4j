## 1. 概述

Apache Maven是Java项目中最受欢迎的构建工具之一，除了去中心化依赖项和仓库之外，促进跨项目的统一目录结构也是其重要方面之一。

在这篇简短的文章中，我们将探讨典型Maven项目的标准目录布局。

## 2. 目录布局

一个典型的Maven项目有一个pom.xml文件和一个基于定义的约定的目录结构：

```bash
└───maven-project
    ├───pom.xml
    ├───README.txt
    ├───NOTICE.txt
    ├───LICENSE.txt
    └───src
        ├───main
        │   ├───java
        │   ├───resources
        │   ├───filters
        │   └───webapp
        ├───test
        │   ├───java
        │   ├───resources
        │   └───filters
        ├───it
        ├───site
        └───assembly
```

可以使用项目描述符覆盖默认目录布局，但这并不常见且不建议使用。

在本文中，我们将揭示有关每个标准文件和子目录的更多详细信息。

## 3. 根目录

此目录用作每个Maven项目的根目录。

让我们仔细看看通常位于根目录下的标准文件和子目录：

-   maven-project/pom.xml：定义Maven项目构建生命周期中所需的依赖项和模块
-   maven-project/LICENSE.txt：项目的许可信息
-   maven-project/README.txt：项目摘要
-   maven-project/NOTICE.txt：有关项目中使用的第三方库的信息
-   maven-project/src/main：包含成为工件一部分的源代码和资源
-   maven-project/src/test：包含所有测试代码和资源
-   maven-project/src/it：通常保留用于Maven Failsafe插件使用的集成测试
-   maven-project/src/site：使用Maven Site插件创建的站点文档
-   maven-project/src/assembly：用于打包二进制文件的程序集配置

## 4. src/main目录

顾名思义，src/main是Maven项目最重要的目录，任何应该属于打包后的工件的东西，无论是jar还是war，都应该出现在这里。

它的子目录是：

-   src/main/java：工件的Java源代码
-   src/main/resources：配置文件和其他文件，例如i18n文件、每个环境的配置文件和XML配置
-   src/main/webapp：用于Web应用程序，包含JavaScript、CSS、HTML文件、视图模板和图像等资源
-   src/main/filters：包含在构建阶段将值注入资源文件夹中的配置属性的文件

## 5. src/test目录 

src/test目录是应用程序中每个组件的测试所在的位置。

请注意，这些目录或文件都不会成为工件的一部分。让我们看看它的子目录：

-   src/test/java：用于测试的Java源代码
-   src/test/resources：配置文件和测试使用的其他文件
-   src/test/filters：包含在测试阶段将值注入资源文件夹中的配置属性的文件

## 6. 总结

在本文中，我们了解了Apache Maven项目的标准目录布局。