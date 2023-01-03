## 1. 概述

在本教程中，我们将学习如何在本地[Maven](https://www.baeldung.com/maven)仓库损坏时强制更新它。为此，我们将使用一个简单的示例来了解为什么仓库会损坏以及如何修复它。

## 2. 先决条件

要学习和运行本教程中的命令，我们需要使用[Spring Initializr](https://www.baeldung.com/spring-boot-custom-starter)项目并安装JDK和Maven。

## 3. Maven仓库结构

Maven将项目的所有依赖项保存在.m2文件夹中。例如，在下图中，我们可以观察[Maven仓库](https://www.baeldung.com/maven-local-repository)的结构：

<img src="../assets/img_5.png">

正如我们所见，Maven下载了仓库文件夹下的所有依赖项。因此，必须下载到我们的本地仓库才能在运行时访问所有需要的代码。

## 4. 下载依赖

我们知道Maven是基于pom.xml文件配置工作的，**当Maven执行此pom.xml 文件时，依赖项将从中央Maven仓库下载并放入本地Maven仓库中**，如果我们的本地仓库中已经有依赖项，Maven将不会下载它们。

当我们执行以下命令时会发生下载：

```shell
mvn package
mvn install
```

以上两个都包括执行以下命令：

```shell
mvn dependency:resolve 
```

因此，我们可以通过运行dependency:resolve命令来单独解析依赖项而不使用package或install。

## 5. 损坏的依赖

下载依赖项时，可能会发生网络故障，从而导致依赖项损坏。中断的依赖项下载是导致损坏的主要原因，Maven将相应地显示一条消息：

```shell
Could not resolve dependencies for project ...
```

接下来让我们看看如何解决这个问题。

## 6. 自动修复损坏的依赖项

Maven通常会在通知构建失败时显示损坏的依赖项：

```shell
Could not transfer artifact [artifact-name-here] ...
```

为了解决这个问题，我们可以采用自动或手动方法。此外，我们应该在调试模式下运行任何仓库更新，在-U之后添加-X选项，以便更好地了解更新期间发生的情况。

### 6.1 强制更新所有SNAPSHOT依赖项

正如我们已经知道的，Maven不会再次下载现有的依赖项。因此，要强制Maven更新所有损坏的SNAPSHOT依赖项，我们应该在命令中添加-U/–update-snapshots选项：

```shell
mvn package -U
mvn install -U
```

**尽管如此，我们必须记住，如果Maven已经下载了SNAPSHOT依赖项并且校验和相同，则该选项不会重新下载它**。

接下来，这也将打包或安装我们的项目。最后，我们将学习如何在不包括当前工作项目的情况下更新仓库。

### 6.2 dependency:resolve目标

我们可以告诉Maven解决我们的依赖关系并更新快照，而无需任何package或install命令。为此，我们将使用dependency:resolve目标，并包含-U选项：

```shell
mvn dependency:resolve -U
```

### 6.3 dependency:purge-local-repository目标

我们知道-U只是重新下载损坏的SNAPSHOT依赖项，因此，在损坏的本地发布依赖项的情况下，我们可能需要更深入的命令。为此，我们应该使用：

```shell
mvn dependency:purge-local-repository
```

dependency:purge-local-repository的目的是从本地Maven仓库中清除(删除并选择性地重新解析)工件，默认情况下，工件将被重新解析。

### 6.4 purge-local-repository选项

通过为resolutionFuzziness选项指定“groupId”，以及使用include选项搜索的确切groupId，可以将清除本地仓库配置为仅针对特定组的依赖项运行：

```shell
mvn dependency:purge-local-repository -Dinclude:org.slf4j -DresolutionFuzziness=groupId -Dverbose
```

**resolutionFuzziness选项可以具有以下值：version、artifactId、groupId、file**。

上面的示例将搜索并清除org.slf4j组中的所有工件，我们还设置了verbose选项，以便我们可以看到已删除工件的详细日志。

如果找到任何符合条件的文件，日志将显示如下文本：

```shell
Deleting 2 transitive dependencies for project [...] with artifact groupId resolution fuzziness
[INFO] Purging artifact: org.slf4j:jul-to-slf4j:jar:1.7.31
Purging artifact: org.slf4j:slf4j-api:jar:1.7.31 
```

**请注意，要指定包含或排除以进行删除或刷新的工件，我们可以使用选项include/exclude**：

```shell
mvn dependency:purge-local-repository -Dinclude=com.yyy.projectA:projectB -Dexclude=com.yyy.projectA:projectC
```

## 7. 手动删除仓库

**虽然-U选项和purge-local-repository可能会在不刷新所有依赖项的情况下解决损坏的依赖项，但手动删除.m2本地仓库会导致强制重新下载所有依赖项**。

当具有旧的和可能损坏的依赖项时，这可能很有用。然后一个简单的重新打包或重新安装就可以完成这项工作。此外，我们可以使用dependency:resolve选项来单独解决我们项目的依赖关系。

## 8. 总结

在本教程中，我们讨论了强制更新本地仓库的Maven的选项和目标。

使用的示例是简单的Maven命令，可以在配置了适当的pom.xml的任何项目上使用。