## 1. 概述

有时出于各种原因，我们可能需要明确要求Maven不要从其仓库中下载任何内容。

在这个简短的教程中，我们将了解如何在Maven中启用离线模式。

## 2. 准备

在进入离线模式之前，必须下载必要的工件。否则，我们可能无法有效地使用这种模式。

**为了准备离线模式，我们可以使用**[maven-dependency-plugin](https://maven.apache.org/plugins/maven-dependency-plugin/go-offline-mojo.html)**中的go-offline目标**：

```bash
mvn dependency:go-offline
```

这个目标解决了所有项目依赖关系，包括插件和报告及其依赖关系。运行这个目标后，我们就可以安全地在离线模式下工作了。

## 3. 离线模式

**要在离线模式下执行**[Maven目标和阶段](https://www.baeldung.com/maven-goals-phases)**，我们只需使用-o或–offline选项**。例如，为了在离线模式下运行集成测试：

```bash
mvn -o verify
```

如果我们已经下载了所有必需的工件，则此命令将成功执行所有测试；否则，它将失败。

也可以通过在~/.m2/settings.xml文件中设置offline属性来全局配置离线模式：

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <offline>true</offline>
</settings>
```

此设置将应用于所有Maven项目，默认情况下，offline属性设置为false。因此，当我们使用-o选项时，它将在该命令的持续时间内暂时覆盖该默认设置。

## 4. 总结

在这个快速教程中，我们了解了如何使用maven-dependency-plugin为Maven离线模式做准备。此外，我们还熟悉了命令行方法和基于设置的方法来启用离线模式。