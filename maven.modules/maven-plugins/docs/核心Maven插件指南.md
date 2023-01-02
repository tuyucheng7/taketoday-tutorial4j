## 1. 概述

Maven是Java世界中最常用的构建工具，主要是，它只是一个插件执行框架，其中所有作业都由插件实现。

在本教程中，我们将介绍核心Maven插件，提供指向其他教程的链接，重点介绍这些插件的功能以及它们的目标如何绑定到构建生命周期。

## 2. Maven构建生命周期

核心插件与构建生命周期密切相关。

Maven定义了三个构建生命周期：default、site和clean。每个生命周期由多个阶段组成，这些阶段按顺序运行，直到mvn命令中指定的阶段。

**最重要的生命周期是default，负责构建过程中的所有步骤**，从项目验证到包部署。

site生命周期负责构建站点，显示项目的Maven相关信息，而clean生命周期负责删除在上一次构建中生成的文件。

所有三个生命周期中的许多阶段都自动绑定到核心插件的目标，参考文章将详细介绍这些目标和内置绑定。

所有插件都包含在POM的<build\>元素中：

```xml
<build>
    <plugins>
        <!-- plugins go here -->
    </plugins>
</build>
```

## 3. 绑定默认生命周期的插件 

默认生命周期的内置绑定取决于POM的<packaging\>元素的值，为了简洁起见，我们将介绍最常见的打包类型的绑定：jar和war。

以下是绑定到默认生命周期每个阶段的目标列表，格式为“phase -> plugin : goal”：

-   process-resources -> resources:resources
-   compile -> compiler:compile
-   process-test-resources -> resources:testResources
-   test-compile -> compiler:testCompile
-   test -> surefire:test
-   package -> ejb:ejb或者ejb3:ejb3或者jar:jar或者par:par或者rar:rar或者war:war
-   install -> install:install
-   deploy -> deploy:deploy

上述目标包含在以下插件中，点击链接获取有关每个插件的文章：

-   [Resources插件](https://www.baeldung.com/maven-resources-plugin)
-   [Compiler插件](https://www.baeldung.com/maven-compiler-plugin)
-   [Surefire插件](https://www.baeldung.com/maven-surefire-plugin)
-   [Failsafe插件](https://www.baeldung.com/maven-failsafe-plugin)
-   [Verifier插件](https://www.baeldung.com/maven-verifier-plugin)
-   [Install插件](https://www.baeldung.com/maven-install-plugin)
-   [Deploy插件](https://www.baeldung.com/maven-deploy-plugin)

## 4. 其他插件

除了上一节提到的插件之外，还有另外两个核心插件，它们的目标是绑定到site的各个阶段和clean的生命周期：

-   [Site插件](https://www.baeldung.com/maven-site-plugin)
-   [Clean插件](https://www.baeldung.com/maven-clean-plugin)

## 5. 总结

在本文中，我们回顾了Maven构建生命周期，并提供了对详细介绍Maven构建工具核心插件的教程的参考。