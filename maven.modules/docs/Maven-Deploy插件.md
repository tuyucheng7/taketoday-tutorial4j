## 1. 概述

本教程介绍了deploy插件，它是Maven构建工具的核心插件之一。

有关其他核心插件的快速概览，请参阅[本文](https://www.baeldung.com/core-maven-plugins)。

## 2. 插件目标

**我们在deploy阶段使用deploy插件，将工件推送到远程仓库以与其他开发人员共享**。

除了工件本身之外，此插件还确保所有关联信息(例如POM、元数据或哈希值)都是正确的。

deploy插件在super POM中指定，因此我们无需将此插件显示添加到POM中。

这个插件最引人注目的目标是deploy，默认绑定到deploy阶段。[本教程](https://www.baeldung.com/maven-deploy-nexus)详细介绍了deploy插件，因此我们不会进一步讨论。

deploy插件有另一个名为deploy-file的目标，即在远程仓库中部署工件，不过这个目标并不常用。

## 3. 总结

本文简要介绍了deploy插件。

我们可以在[Maven网站](https://maven.apache.org/plugins/maven-deploy-plugin/)上找到关于这个插件的更多信息。