## 1. 概述

在本教程中，我们将了解如何使用site-maven插件在GitHub上托管带有源的Maven仓库。这是使用像Nexus这样的仓库的一种经济实惠的替代方法。

## 2. 先决条件

如果我们还没有GitHub上的Maven项目，我们需要创建一个仓库。在本文中，我们使用一个仓库“host-maven-repo-example”和分支“main”，这是GitHub上的一个空仓库：

<img src="../assets/img.png">

## 3. Maven项目

让我们创建一个简单的Maven项目，我们会将此项目生成的工件推送到GitHub。

这是该项目的pom.xml：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>cn.tuyucheng.taketoday.maven.plugin</groupId>
    <artifactId>host-maven-repo-example</artifactId>
    <version>1.0.0</version>
    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <configuration>
                        <source>17</source>
                        <target>17</target>
                    </configuration>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
```

首先，我们需要**在我们的项目本地创建一个内部仓库**，在推送到GitHub之前，Maven工件将部署到项目构建目录中的这个位置。

我们将本地仓库定义添加到我们的pom.xml：

```xml
<distributionManagement> 
    <repository>
        <id>internal.repo</id> 
        <name>Temporary Staging Repository</name> 
        <url>file://${project.build.directory}/mvn-artifact</url> 
    </repository> 
</distributionManagement>

```

现在，让我们**将**[maven-deploy-plugin](https://maven.apache.org/plugins/maven-deploy-plugin/)**配置添加到我们的pom.xml中**，我们将使用此插件将我们的工件添加到目录${project.build.directory}/mvn-artifact中的本地仓库中：

```xml
<plugin>
    <artifactId>maven-deploy-plugin</artifactId>
    <version>2.8.2</version>
    <configuration>
        <altDeploymentRepository>
            internal.repo::default::file://${project.build.directory}/mvn-artifact
        </altDeploymentRepository>
    </configuration>
</plugin>
```

此外，**如果我们想将带有Maven工件的源文件推送到GitHub，那么我们还需要包含source插件**：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-source-plugin</artifactId>
    <version>3.1.0</version>
    <executions>
        <execution>
            <id>attach-sources</id>
                <goals>
                    <goal>jar</goal>
                </goals>
        </execution>
    </executions>
</plugin>
```

将上述配置和插件添加到pom.xml后，构建**将在本地目录target/mvn-artifact中部署Maven工件**。

现在，**下一步是将这些工件从本地目录部署到GitHub**。

## 4. 配置GitHub身份验证

在将工件部署到GitHub之前，我们将在${MAVEN_HOME}/conf/settings.xml中配置身份验证信息，这是为了使site-maven-plugin能够将工件推送到GitHub。

根据我们想要进行身份验证的方式，我们将向我们的settings.xml添加两个配置之一，接下来让我们检查这些选项。

### 4.1 使用GitHub用户名和密码

要使用GitHub用户名和密码，我们将在settings.xml中配置它们：

```xml
<settings>
    <servers>
        <server>
            <id>github</id>
            <username>your Github username</username>
            <password>your Github password</password>
        </server>
    </servers>
</settings>
```

### 4.2 使用个人访问令牌

**使用GitHub API或命令行时，推荐的身份验证方法是使用**[个人访问令牌(PAT)](https://docs.github.com/en/github/authenticating-to-github/keeping-your-account-and-data-secure/creating-a-personal-access-token)：

```xml
<settings>
    <servers> 
        <server>
            <id>github</id>
            <password>YOURGitHubOAUTH-TOKEN</password>
        </server>
    </servers>
</settings>
```

## 5. 使用site-maven-plugin将工件推送到GitHub

最后一步是**配置**[site-maven插件](https://github.com/github/maven-plugins)**来推送我们本地的暂存仓库**，此暂存仓库存在于target目录中：

```xml
<plugin>
    <groupId>com.github.github</groupId>
    <artifactId>site-maven-plugin</artifactId>
    <version>0.12</version>
    <configuration>
        <message>Maven artifacts for ${project.version}</message>
        <noJekyll>true</noJekyll>
        <outputDirectory>${project.build.directory}</outputDirectory>
        <branch>refs/heads/${branch-name}</branch>
        <includes>
            <include>**/*</include>
        </includes>
        <merge>true</merge>
        <repositoryName>${repository-name}</repositoryName>
        <repositoryOwner>${repository-owner}</repositoryOwner>
        <server>github</server>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>site</goal>
            </goals>
            <phase>deploy</phase>
        </execution>
    </executions>
</plugin>
```

例如，对于本教程，假设我们有仓库tu-yucheng/host-maven-repo-example，然后<repositoryName\>标签值将是host-maven-repo-example并且<repositoryOwner\>标签值将是tu-yucheng。

现在，**我们将执行mvn deploy命令将工件上传到GitHub**，如果main不存在，将自动创建。构建成功后，在浏览器和main分支下查看GitHub上的仓库，我们所有的二进制文件都将出现在仓库中。

在我们的例子中，它看起来像这样：

<img src="../assets/img_1.png">

## 6. 总结

最后，我们了解了如何使用site-maven-plugin在GitHub上托管Maven工件。