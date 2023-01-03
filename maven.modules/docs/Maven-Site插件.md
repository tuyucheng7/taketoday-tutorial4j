## 1. 概述

本教程介绍site插件，它是Maven构建工具的核心插件之一。

有关其他核心插件的概述，请参阅[本教程](https://www.baeldung.com/core-maven-plugins)。

## 2. 插件目标

Maven site生命周期默认有两个绑定到site插件目标的阶段：site阶段绑定到site目标，site-deploy阶段绑定到deploy目标。

以下是这些目标的描述：

-   site：为单个项目生成一个站点；生成的站点仅显示有关POM中指定的工件的信息
-   deploy：将生成的站点部署到POM的distributionManagement元素中指定的URL

除了site和deploy之外，site插件还有其他几个目标，可以自定义生成文件的内容并控制部署过程。

## 3. 目标执行

我们可以在不将它添加到POM的情况下使用这个插件，因为super POM已经包含了它。

要生成一个站点，只需运行mvn site:site或mvn site。

若要在本地计算机上查看生成的站点，请运行mvn site:run。此命令会将站点部署到地址为localhost:8080的Jetty Web服务器。

该插件的run目标并未隐式绑定到site生命周期中的某个阶段，因此我们需要直接调用它。

如果我们想停止服务器，我们可以简单地按下Ctrl + C。

## 4. 总结

本文介绍了site插件以及如何执行其目标。

我们可以在[Maven网站](https://maven.apache.org/plugins/maven-site-plugin/)上找到关于这个插件的更多信息。