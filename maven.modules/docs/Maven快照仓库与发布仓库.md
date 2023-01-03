## 1. 概述

在本教程中，我们将解释Maven快照和发布仓库之间的区别。

## 2. Maven仓库

Maven仓库包含一组预编译的工件，我们可以在我们的应用程序中将其用作依赖项。对于传统的Java应用程序，这些通常是.jar文件。

通常，有两种类型的仓库：本地仓库和远程仓库。

本地仓库是Maven在其构建所在的计算机上创建的仓库，它通常位于[$HOME/.m2/repository](https://www.baeldung.com/maven-local-repository)目录下。

当我们构建应用程序时，Maven将在我们的本地仓库中搜索依赖项，如果未找到某个依赖项，Maven将在远程仓库(在[settings.xml](https://www.baeldung.com/maven-settings-xml)或pom.xml文件中定义)中搜索它。此外，它会将依赖项复制到我们的本地仓库以供将来使用。

远程仓库是包含工件的外部仓库，一旦Maven从远程仓库下载了工件，它会优先在本地仓库中查找工件以限制工件下载。

此外，我们还可以根据快照仓库和发布仓库之间的工件类型来区分仓库。

## 3. 快照仓库

快照仓库是用于增量的、未发布的工件版本的仓库。

**快照版本是尚未发布的版本**，一般的想法是在发布版本之前有一个快照版本。它允许我们以增量方式部署相同的临时版本，而不需要项目升级他们正在使用的工件版本，这些项目可以使用相同的版本来获取更新的快照版本。

例如，在发布版本1.0.0之前，我们可以拥有它的快照版本，快照版本在版本之后有一个SNAPSHOT后缀(例如，1.0.0-SNAPSHOT)。

### 3.1 部署工件

持续开发通常使用快照版本控制，使用快照版本，我们可以部署一个工b件，其编号由时间戳和内部版本号组成。

假设我们有一个正在开发的项目，其版本为SNAPSHOT：

```xml
<groupId>cn.tuyucheng.taketoday</groupId>
<artifactId>maven-snapshot-repository</artifactId>
<version>1.0.0-SNAPSHOT</version>
```

我们将把项目部署到一个自托管的[Nexus](https://www.baeldung.com/maven-deploy-nexus)仓库。

首先，让我们定义要在其中部署项目的发布仓库信息，我们可以使用分发管理插件：

```xml
<distributionManagement>
    <snapshotRepository>
        <id>nexus</id>
        <name>nexus-snapshot</name>
        <url>http://localhost:8081/repository/maven-snapshots/</url>
    </snapshotRepository>
</distributionManagement>
```

此外，我们将使用mvn deploy命令部署我们的项目。

**部署后，实际工件版本将包含时间戳值而不是SNAPSHOT值**。例如，当我们部署1.0.0-SNAPSHOT时，实际值将包含当前时间戳和内部版本号(例如1.0.0-20230103.063105-3)。

时间戳值是在工件部署期间计算的，Maven生成校验和并上传具有相同时间戳的工件文件。

maven-metadata.xml文件包含有关快照版本及其指向最新时间戳值的链接的精确信息：

```xml
<metadata modelVersion="1.1.0">
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>maven-snapshot-repository</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <versioning>
        <snapshot>
            <timestamp>20230103.063105</timestamp>
            <buildNumber>3</buildNumber>
        </snapshot>
        <lastUpdated>20230103063105</lastUpdated>
        <snapshotVersions>
            <snapshotVersion>
                <extension>jar</extension>
                <value>1.0.0-20230103.063105-3</value>
                <updated>20230103063105</updated>
            </snapshotVersion>
            <snapshotVersion>
                <extension>pom</extension>
                <value>1.0.0-20230103.063105-3</value>
                <updated>20230103063105</updated>
            </snapshotVersion>
        </snapshotVersions>
    </versioning>
</metadata>
```

元数据文件有助于管理从快照版本到时间戳值的转换。

每次我们在同一个快照版本下部署项目时，Maven都会生成包含新时间戳值和新内部版本号的版本。

### 3.2 下载工件

在下载快照工件之前，Maven会下载其关联的maven-metadata.xml文件。这样，Maven就可以根据时间戳值和内部版本号检查是否有更新的版本。

**检索此类工件仍然可以使用SNAPSHOT版本**。

要从仓库下载工件，首先，我们需要定义一个依赖项仓库：

```xml
<repositories>
    <repository>
        <id>nexus</id>
        <name>nexus-snapshot</name>
        <url>http://localhost:8081/repository/maven-snapshots/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
        <releases>
            <enabled>false</enabled>
        </releases>
    </repository>
</repositories>
```

**默认情况下不启用快照版本**，我们需要手动启用它们：

```xml
<snapshots>
    <enabled>true</enabled>
</snapshots>
```

通过启用快照，我们可以定义我们希望多久检查一次SNAPSHOT工件的更新版本。但是，默认更新策略设置为每天一次，我们可以通过设置不同的更新策略来覆盖此行为：

```xml
<snapshots>
    <enabled>true</enabled>
    <updatePolicy>always</updatePolicy>
</snapshots>
```

我们可以在updatePolicy元素中放置四个不同的值：

-   always - 每次检查是否有更新的版本
-   daily(默认值) - 每天检查一次更新版本
-   interval:mm - 根据以分钟为单位设置的时间间隔检查更新版本
-   never - 永远不要尝试获得更新的版本(与我们本地已有的版本相比)

此外，我们可以通过在命令中传递-U参数来强制更新所有快照工件，而不是定义updatePolicy：

```bash
mvn install -U
```

此外，如果依赖项已经下载并且校验和与我们本地仓库中已有的校验和相同，则不会重新下载依赖项。

接下来，我们可以将工件的快照版本添加到我们的项目中：

```xml
<dependencies>
    <dependency>
        <groupId>cn.tuyucheng.taketoday</groupId>
        <artifactId>maven-snapshot-repository</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

**在开发阶段使用快照版本可以防止工件有多个版本**，我们可以使用相同的SNAPSHOT版本，其构建将包含我们代码在给定时间的快照。

## 4. 发布仓库

发布仓库包含工件的最终版本(发布)，**简单地说，发布工件代表其内容不应被修改的工件**。

默认情况下，为我们的settings.xml或pom.xml文件中定义的所有仓库启用发布仓库。

### 4.1 部署工件

现在，让我们将项目部署到本地Nexus仓库中，假设我们已经完成开发并准备发布项目：

```xml
<groupId>cn.tuyucheng.taketoday</groupId>
<artifactId>maven-release-repository</artifactId>
<version>1.0.0</version>
```

让我们在distributionManagement中定义发布仓库：

```xml
<distributionManagement>
    <repository>
        <id>nexus</id>
        <name>nexus-release</name>
        <url>http://localhost:8081/repository/maven-releases/</url>
    </repository>
    <snapshotRepository>
        <id>nexus</id>
        <name>nexus-snapshot</name>
        <url>http://localhost:8081/repository/maven-snapshots/</url>
    </snapshotRepository>
</distributionManagement>
```

一旦我们从项目版本中删除SNAPSHOT一词，在部署期间将自动选择发布仓库而不是快照仓库。

此外，如果我们想在同一版本下重新部署工件，我们可能会得到一个错误：“Repository does not allow updating assets”。**一旦我们部署了已发布的工件版本，我们就无法更改其内容**。因此，要解决这个问题，我们只需要发布下一个版本。

### 4.2 下载工件

Maven默认从[Maven Central Repository](https://repo1.maven.org/maven2)寻找组件，默认情况下，此仓库使用发布版本策略。

**发布仓库将仅解析已发布的工件**，换句话说，它应该只包含已发布的工件版本，其内容在未来不应更改。

如果我们想下载已发布的工件，我们需要定义仓库：

```xml
<repository>
    <id>nexus</id>
    <name>nexus-release</name>
    <url>http://localhost:8081/repository/maven-releases/</url>
</repository>
```

最后，让我们简单地将已发布的版本添加到我们的项目中：

```xml
<dependencies>
    <dependency>
        <groupId>cn.tuyucheng.taketoday</groupId>
        <artifactId>maven-release-repository</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

## 5. 总结

在本教程中，我们了解了Maven快照和发布仓库之间的区别。综上所述，我们应该为仍在开发中的项目使用快照仓库，并为准备生产的项目使用发布仓库。