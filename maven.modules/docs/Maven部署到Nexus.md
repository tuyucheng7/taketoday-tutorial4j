## 1. 概述

在[上一篇文章](https://www.baeldung.com/install-local-jar-with-maven)中，我讨论了Maven项目如何在本地安装尚未部署在Maven Central(或任何其他大型公共托管仓库)上的第三方jar。

该解决方案应该只适用于安装、运行和维护完整的Nexus服务器可能有点矫枉过正的小型项目。但是，随着项目的发展，Nexus很快成为托管第三方工件以及跨开发流重用内部工件的唯一真实且成熟的选择。

本文将展示如何**使用Maven将项目的工件部署到Nexus**。

## 2. pom.xml中的Nexus要求

为了让Maven能够部署它在构建的package阶段创建的工件，它需要通过distributionManagement元素定义将在其中部署打包工件的仓库信息：


```xml
<distributionManagement>
    <snapshotRepository>
        <id>nexus-snapshots</id>
        <url>http://localhost:8081/nexus/content/repositories/snapshots</url>
    </snapshotRepository>
</distributionManagement>
```

托管的公共快照仓库在Nexus上开箱即用，因此无需进一步创建或配置任何内容。Nexus可以轻松确定其托管仓库的URL，每个仓库都显示要添加到项目pom的<distributionManagement\>中的确切条目，在Summary选项卡下。

## 3. 插件

默认情况下，Maven通过maven-deploy-plugin处理部署机制，这映射到默认Maven生命周期的deploy阶段：

```xml
<plugin>
    <artifactId>maven-deploy-plugin</artifactId>
    <version>2.8.1</version>
    <executions>
        <execution>
            <id>default-deploy</id>
            <phase>deploy</phase>
            <goals>
                <goal>deploy</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

maven-deploy-plugin是处理将项目的工件部署到Nexus的任务的可行选择，但它并不是为了充分利用Nexus必须提供的功能而构建的。正因为如此，Sonatype构建了一个特定于Nexus的插件nexus-staging-maven-plugin，它实际上是为了充分利用Nexus必须提供的更高级的功能而设计的，比如暂存之类的功能。

虽然对于一个简单的部署过程，我们不需要暂存功能，但我们将继续使用这个自定义Nexus插件，因为它的构建目的很明确，就是为了与Nexus进行良好的对话。

使用maven-deploy-plugin的唯一原因是保留在未来使用Nexus替代品的选项，例如，一个[Artifactory](https://www.jfrog.com/open-source/#os-arti)仓库。然而，与可能在项目的整个生命周期中实际发生变化的其他组件不同，Maven仓库管理器极不可能发生变化，因此不需要灵活性。

因此，在deploy阶段使用另一个部署插件的第一步是禁用现有的默认映射：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-deploy-plugin</artifactId>
    <version>${maven-deploy-plugin.version}</version>
    <configuration>
        <skip>true</skip>
    </configuration>
</plugin>
```

现在，我们可以定义：

```xml
<plugin>
    <groupId>org.sonatype.plugins</groupId>
    <artifactId>nexus-staging-maven-plugin</artifactId>
    <version>1.5.1</version>
    <executions>
        <execution>
            <id>default-deploy</id>
            <phase>deploy</phase>
            <goals>
                <goal>deploy</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <serverId>nexus</serverId>
        <nexusUrl>http://localhost:8081/nexus/</nexusUrl>
        <skipStaging>true</skipStaging>
    </configuration>
</plugin>
```

插件的deploy目标映射到Maven构建的deploy阶段。

另请注意，如前所述，我们不需要在将-SNAPSHOT工件简单部署到Nexus时使用暂存功能，因此可以通过<skipStaging\>元素将其完全禁用。

默认情况下，deploy目标包括暂存工作流，推荐将其用于发布构建。

## 4. 全局settings.xml

**部署到Nexus是一项安全操作**，为此目的，在任何Nexus实例上都存在开箱即用的部署用户。

使用此部署用户的凭据配置Maven，以便它可以与Nexus正确交互。不能在项目的pom.xml中完成，这是因为pom的语法不允许这样做，更不用说pom可能是一个公共工件，因此不太适合保存凭证信息。

服务器的凭据必须在全局Maven的setting.xml中定义：

```xml
<servers>
    <server>
        <id>nexus-snapshots</id>
        <username>deployment</username>
        <password>the_pass_for_the_deployment_user</password>
    </server>
</servers>
```

还可以将服务器配置为使用基于密钥的安全性，而不是原始凭据和纯文本凭据。

## 5. 部署过程

执行部署过程是一项简单的任务：

```shell
mvn clean deploy -Dmaven.test.skip=true
```

在部署作业的上下文中跳过测试是可以的，因为该作业应该是项目**部署管道**中的最后一个作业。

这种部署管道的一个常见示例是连续的Jenkins作业，每个作业只有在成功完成时才会触发下一个作业。因此，管道中之前的作业有责任运行项目中的所有测试套件，在部署作业运行时，所有测试都应该已经通过了。

如果运行单个命令，则测试可以在部署阶段执行之前保持激活状态以运行：

```shell
mvn clean deploy
```

## 6. 总结

这是一个将Maven工件部署到Nexus的简单但高效的解决方案。

它也有些自以为是，使用了nexus-staging-maven-plugin而不是默认的maven-deploy-plugin，暂存功能被禁用等，正是这些选择使解决方案简单实用。

可能激活完整的暂存功能可能是未来文章的主题。

最后，我们将在下一篇文章中讨论发布过程。