## 1. 概述

在本系列的上一篇文章中，我们设置了一个[使用Maven到Nexus的部署过程](https://www.baeldung.com/maven-deploy-nexus)。在本文中，我们将在项目的pom和Jenkins作业中使用**Maven配置发布流程**。

## 2. pom中的仓库

为了让Maven能够发布到Nexus仓库服务器，我们需要**通过distributionManagement元素定义仓库信息**：

```xml
<distributionManagement>
   <repository>
      <id>nexus-releases</id>
      <url>http://localhost:8081/nexus/content/repositories/releases</url>
   </repository>
</distributionManagement>
```

托管的发布仓库在Nexus上开箱即用，因此无需显式创建它。

## 3. Maven pom中的SCM

发布过程将与项目的源代码控制系统交互，这意味着我们首先需要在pom.xml中定义<scm\>元素：

```xml
<scm>
    <connection>scm:git:https://github.com/user/project.git</connection>
    <url>http://github.com/user/project</url>
    <developerConnection>scm:git:https://github.com/user/project.git</developerConnection>
</scm>
```

或者，使用git协议：

```xml
<scm>
    <connection>scm:git:git@github.com:user/project.git</connection>
    <url>scm:git:git@github.com:user/project.git</url>
    <developerConnection>scm:git:git@github.com:user/project.git</developerConnection>
</scm>
```

## 4. release插件

发布过程使用的标准Maven插件是maven-release-plugin，这个插件的配置是最小的：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-release-plugin</artifactId>
    <version>2.4.2</version>
    <configuration>
        <tagNameFormat>v@{project.version}</tagNameFormat>
        <autoVersionSubmodules>true</autoVersionSubmodules>
        <releaseProfiles>releases</releaseProfiles>
    </configuration>
</plugin>
```

这里重要的是releaseProfiles配置实际上会强制一个Maven profile(releases profile)，在发布过程中变为激活状态。

正是在这个过程中，nexus-staging-maven-plugin用于执行部署到nexus-releases Nexus仓库：

```xml
<profiles>
    <profile>
        <id>releases</id>
        <build>
            <plugins>
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
                        <serverId>nexus-releases</serverId>
                        <nexusUrl>http://localhost:8081/nexus/</nexusUrl>
                        <skipStaging>true</skipStaging>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

该插件配置为在**没有暂存机制**的情况下执行发布过程，与[之前](https://www.baeldung.com/maven-deploy-nexus)相同，用于部署过程(skipStaging=true)。

并且与部署过程类似，**发布到Nexus是一项安全操作**，因此我们将再次使用开箱即用的部署用户表单Nexus。

我们还需要在全局settings.xml(%USER_HOME%/.m2/settings.xml)中配置nexus-releases服务器的凭据：

```xml
<servers>
    <server>
        <id>nexus-releases</id>
        <username>deployment</username>
        <password>the_pass_for_the_deployment_user</password>
    </server>
</servers>
```

这是完整的配置。

## 5. 发布过程

让我们将发布过程分解为小而集中的步骤，当项目的当前版本是SNAPSHOT版本(比如0.1-SNAPSHOT)时，我们正在执行发布。

### 5.1 release:prepare

**清理发布将**：

-   删除发布描述符(release.properties)
-   删除任何备份POM文件

### 5.2 release:prepare

发布过程的下一部分是**准备发布**；这将：

-   执行一些检查-不应该有未提交的更改，并且项目不应该依赖于任何SNAPSHOT依赖项
-   将pom文件中的项目版本更改为完整发布版本号(删除SNAPSHOT后缀)，在我们的示例中为0.1
-   运行项目**测试**套件
-   提交并推送更改
-   从这个非SNAPSHOT版本控制的代码中创建**标签**
-   **增加POM中项目的版本**，在我们的示例中为0.2-SNAPSHOT
-   提交并推送更改

### 5.3 release:perform

发布过程的后半部分是**执行发布**；这将：

-   从SCM签出发布标签
-   构建和部署发布的代码

该过程的第二步依赖于准备步骤的输出-release.properties。

## 6. 关于Jenkins

Jenkins可以通过以下两种方式之一执行发布过程，它可以使用它自己的发布插件，或者它也可以简单地使用运行正确发布步骤的标准maven作业来执行发布。

现有的专注于发布过程的Jenkins插件有：

-   [Release插件](https://wiki.jenkins-ci.org/display/JENKINS/Release+Plugin)
-   [M2 Release插件](https://wiki.jenkins-ci.org/display/JENKINS/M2+Release+Plugin)

然而，由于用于执行发布的Maven命令非常简单，我们可以简单地定义一个标准的Jenkins作业来执行该操作，不需要插件。

因此，对于一个新的Jenkins作业(构建一个maven2/3项目)，我们将定义2个字符串参数：releaseVersion=0.1和developmentVersion=0.2-SNAPSHOT。

在**构建**配置部分，我们可以简单地配置以下Maven命令来运行：

```shell
release:clean release:prepare release:perform 
   -DreleaseVersion=${releaseVersion} -DdevelopmentVersion=${developmentVersion}
```

在运行参数化作业时，Jenkins会提示用户为这些参数指定值，因此每次运行作业时，我们都需要为releaseVersion和developmentVersion填写正确的值。

此外，值得使用[Workspace Cleanup插件](https://wiki.jenkins-ci.org/display/JENKINS/Workspace+Cleanup+Plugin)并为此构建选中“Delete workspace before build starts”选项。但是请记住，发布的**perform**步骤必须由与**prepare**步骤相同的命令运行，这是因为后面的**perform**步骤将使用由**prepare**创建的release.properties文件。这意味着我们不能让一个Jenkins Job运行**prepare**而另一个运行**perform**。

## 7. 总结

本文展示了如何在有或没有Jenkins的情况下实现**发布Maven项目**的过程，与[部署](https://www.baeldung.com/maven-deploy-nexus)类似，此过程使用nexus-staging-maven-plugin与Nexus交互并专注于git项目。