## 1. 概述

本文介绍install插件，它是Maven构建工具的核心插件之一。

有关其他核心插件的概述，请参阅[本文](https://www.baeldung.com/core-maven-plugins)。

## 2. 插件目标

**我们使用install插件将工件添加到本地仓库**，这个插件包含在super POM中，因此我们不需要再POM中显式包含它。

这个插件最值得注意的目标是install，默认情况下它绑定到install阶段。

其他目标是install-file，用于自动将外部工件安装到本地仓库中，并帮助显示有关插件本身的信息。

在大多数情况下，install插件不需要任何自定义配置，这就是为什么我们不会深入研究这个插件的原因。

## 3. 总结

这篇文章简要介绍了install插件。

我们可以在[Maven网站](https://maven.apache.org/plugins/maven-install-plugin/)上找到关于这个插件的更多信息。