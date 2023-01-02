## 1. 概述

在这个简短的教程中，我们将概述使用[Maven](https://www.baeldung.com/maven)的super、simplest和effective POM之间的区别。

## 2. 什么是POM？

POM(Project Object Model)代表项目对象模型，它是Maven中项目配置的核心。它是一个名为pom.xml的单个配置XML文件，其中包含构建项目所需的大部分信息。

**POM文件的作用是描述项目、管理依赖关系以及声明有助于Maven构建项目的配置详细信息**。

## 3. Super POM

为了更容易地理解super POM，我们可以用Java中的Object类做一个类比：默认情况下，Java中的每个类都扩展了Object类。同样，在POM的情况下，每个POM都扩展了super POM。

**super POM文件定义了所有默认配置，因此，即使是最简单的POM文件形式也会继承super POM文件中定义的所有配置**。

根据我们使用的Maven版本，super POM看起来可能略有不同。例如，如果我们的机器上安装了Maven，我们可以在${MAVEN_HOME}/lib/maven-model-builder-<version\>.jar文件中可视化它。如果我们打开这个JAR文件，我们可以在org/apache/maven/model/pom-4.0.0.xml下找到它。

在接下来的部分中，我们将介绍3.8.4版本的super POM配置元素。

### 3.1 Repositories

Maven使用repositories部分下定义的仓库在Maven构建期间下载所有依赖的工件。

让我们看一个例子：

```xml
<repositories>
    <repository>
        <id>central</id>
        <name>Central Repository</name>
        <url>https://repo.maven.apache.org/maven2</url>
        <layout>default</layout>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>
```

### 3.2 PluginRepositories

默认的插件仓库是中央Maven仓库，让我们看看它在pluginRepository部分是如何定义的：

```xml
<pluginRepositories>
    <pluginRepository>
        <id>central</id>
        <name>Central Repository</name>
        <url>https://repo.maven.apache.org/maven2</url>
        <layout>default</layout>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <releases>
            <updatePolicy>never</updatePolicy>
        </releases>
    </pluginRepository>
</pluginRepositories>
```

正如我们在上面看到的，快照被禁用，updatePolicy被设置为“never”。因此，使用此配置，如果发布新版本，Maven将永远不会自动更新插件。

### 3.3 Build

构建配置部分包括构建项目所需的所有信息。

让我们看一个默认构建部分的示例：

```xml
<build>
    <directory>${project.basedir}/target</directory>
    <outputDirectory>${project.build.directory}/classes</outputDirectory>
    <finalName>${project.artifactId}-${project.version}</finalName>
    <testOutputDirectory>${project.build.directory}/test-classes</testOutputDirectory>
    <sourceDirectory>${project.basedir}/src/main/java</sourceDirectory>
    <scriptSourceDirectory>${project.basedir}/src/main/scripts</scriptSourceDirectory>
    <testSourceDirectory>${project.basedir}/src/test/java</testSourceDirectory>
    <resources>
        <resource>
            <directory>${project.basedir}/src/main/resources</directory>
        </resource>
    </resources>
    <testResources>
        <testResource>
            <directory>${project.basedir}/src/test/resources</directory>
        </testResource>
    </testResources>
    <pluginManagement>
        <!-- NOTE: These plugins will be removed from future versions of the super POM -->
        <!-- They are kept for the moment as they are very unlikely to conflict with lifecycle mappings (MNG-4453) -->
        <plugins>
            <plugin>
                <artifactId>maven-antrun-plugin</artifactId>
                <version>1.3</version>
            </plugin>
            <plugin>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>2.2-beta-5</version>
            </plugin>
            <plugin>
                <artifactId>maven-dependency-plugin</artifactId>
                <version>2.8</version>
            </plugin>
            <plugin>
                <artifactId>maven-release-plugin</artifactId>
                <version>2.5.3</version>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

### 3.4 Reporting

对于报告，super POM只提供输出目录的默认值：

```xml
<reporting>
    <outputDirectory>${project.build.directory}/site</outputDirectory>
</reporting>
```

### 3.5 Profiles

如果我们没有在应用程序级别定义Profile，将执行默认的构建Profile。

默认Profile部分如下所示：

```xml
<profiles>
    <!-- NOTE: The release profile will be removed from future versions of the super POM -->
    <profile>
        <id>release-profile</id>

        <activation>
            <property>
                <name>performRelease</name>
                <value>true</value>
            </property>
        </activation>

        <build>
            <plugins>
                <plugin>
                    <inherited>true</inherited>
                    <artifactId>maven-source-plugin</artifactId>
                    <executions>
                        <execution>
                            <id>attach-sources</id>
                            <goals>
                                <goal>jar-no-fork</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
                <plugin>
                    <inherited>true</inherited>
                    <artifactId>maven-javadoc-plugin</artifactId>
                    <executions>
                        <execution>
                            <id>attach-javadocs</id>
                            <goals>
                                <goal>jar</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
                <plugin>
                    <inherited>true</inherited>
                    <artifactId>maven-deploy-plugin</artifactId>
                    <configuration>
                        <updateReleaseInfo>true</updateReleaseInfo>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

## 4. Simplest POM

**simplest POM是你在Maven项目中声明的POM**，为了声明POM，你至少需要指定这四个元素：<modelVersion\>、<groupId\>、<artifactId\>和<version\>，**simplest POM将继承super POM的所有配置**。

让我们看一下Maven项目所需的最少元素：

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>maven-pom-types</artifactId>
    <version>1.0.0</version>
</project>
```

**Maven中POM层次结构的一个主要优点是我们可以扩展和覆盖从顶部继承的配置**。因此，要覆盖POM层次结构中给定元素或工件的配置，Maven应该能够唯一标识相应的工件。

## 5. Effective POM

**Effective POM结合了super POM文件中的所有默认设置和我们的应用程序POM中定义的配置，当配置元素在应用程序pom.xml中未被覆盖时，Maven使用默认值作为配置元素**。因此，如果我们从simplest POM部分获取相同的示例POM文件，我们将看到effective POM文件将是simplest和super POM之间的合并，我们可以从命令行将其可视化：

```bash
mvn help:effective-pom
```

这也是查看Maven使用的默认值的最佳方式。

## 6. 总结

 在这个简短的教程中，我们讨论了Maven中项目对象模型之间的差异。