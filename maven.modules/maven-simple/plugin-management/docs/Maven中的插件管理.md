## 1. 概述

[Apache Maven](https://www.baeldung.com/maven)是一个强大的工具，它使用[插件](https://www.baeldung.com/maven#introduction-10)来自动化和执行Java项目中的所有构建和报告任务。

但是，在构建中可能会使用多个这样的插件以及不同的版本和配置，尤其是在[多模块项目](https://www.baeldung.com/maven-multi-module)中，这可能会导致复杂的POM文件出现冗余或重复的插件工件以及分散在各个子项目中的配置的问题。

在本文中，我们将看到如何使用Maven的插件管理机制来处理此类问题，并在整个项目中有效地维护插件。

## 2. 插件配置

Maven有两种类型的插件：

-   构建：在构建过程中执行，示例包括Clean、Install和Surefire插件，这些应该在POM的build部分进行配置。
-   报告：在站点生成期间执行以生成各种项目报告，示例包括Javadoc和Checkstyle插件，这些在项目POM的reporting部分中配置。

Maven插件提供了执行和管理项目构建所需的所有有用功能。

例如，我们可以在POM中声明[Jar](https://maven.apache.org/plugins/maven-jar-plugin/)插件：

```xml
<build>
    ....
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jar-plugin</artifactId>
            <version>3.2.0</version>
            ....
        </plugin>
    ....
    </plugins>
</build>
```

在这里，我们在build部分包含了插件，以添加将我们的项目编译成jar的功能。

## 3. 插件管理

除了plugins，我们还可以在POM的pluginManagement部分声明插件，它以与我们之前看到的大致相同的方式包含插件元素。但是，**通过在pluginManagement部分添加插件，它可以用于此POM和所有继承的子POM**。

这意味着任何子POM都将通过在其plugin部分中引用插件来简单地继承插件执行，我们需要做的就是添加相关的groupId和artifactId，而无需重复配置或管理版本。

类似于[依赖关系管理](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#dependency-management)机制，这在多模块项目中特别有用，因为它提供了一个中心位置来管理插件版本和任何相关配置。

## 4. 示例

让我们从创建一个包含两个子模块的简单多模块项目开始，我们将在父POM中包含[Build Helper插件](https://www.mojohaus.org/build-helper-maven-plugin/index.html)，其中包含几个小目标以协助构建生命周期，在我们的示例中，我们将使用它将一些额外的资源复制到子项目的项目输出中。

### 4.1 父POM配置

首先，我们将插件添加到父POM的pluginManagement部分：

```xml
<pluginManagement>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>build-helper-maven-plugin</artifactId>
            <version>3.3.0</version>
            <executions>
                <execution>
                    <id>add-resource</id>
                    <phase>generate-resources</phase>
                    <goals>
                        <goal>add-resource</goal>
                    </goals>
                    <configuration>
                        <resources>
                            <resource>
                                <directory>src/resources</directory>
                                <targetPath>json</targetPath>
                            </resource>
                        </resources>
                    </configuration>
                </execution>
            </executions>
        </plugin>
   </plugins>
</pluginManagement>
```

这会将插件的add-resource目标绑定到[默认POM生命周期](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#Built-in_Lifecycle_Bindings)中的generate-resources阶段，我们还指定了包含附加资源的src/resources目录，插件执行将根据需要将这些资源复制到项目输出中的目标位置(target)。

接下来让我们运行maven命令，确保配置有效，构建成功：

```bash
$ mvn clean test
```

运行此命令后，target位置还不包含预期的资源。

### 4.2 子POM配置

现在，让我们从子POM中引用这个插件：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>build-helper-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

与依赖管理类似，我们不会声明版本或任何插件配置。相反，子项目从父POM中的声明继承这些值。

最后，让我们再次运行构建并查看输出：

```bash
....
[INFO] --- build-helper-maven-plugin:3.3.0:add-resource (add-resource) @ submodule-1 ---
[INFO]
[INFO] --- maven-resources-plugin:2.6:resources (default-resources) @ submodule-1 ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.

[INFO] Copying 1 resource to json
....
```

在这里，插件在构建期间执行，但仅在具有相应声明的子项目中执行。因此，项目输出现在包含来自指定项目位置的额外资源，正如预期的那样。

我们应该注意，**只有父POM包含插件声明和配置**，而子项目只是根据需要引用它。

如果需要，子项目可以自由[修改继承的配置](https://www.baeldung.com/maven-plugin-override-parent) 。

## 5. 核心插件

默认情况下，有一些[Maven核心插件](https://www.baeldung.com/core-maven-plugins)用作构建生命周期的一部分。例如，clean和compiler插件不需要显式声明。

但是，我们可以在POM的pluginManagement元素中显式声明和配置这些，**主要区别在于核心插件配置会自动生效，无需在子项目中进行任何引用**。

让我们通过将compiler插件添加到熟悉的pluginManagement部分来尝试一下：

```xml
<pluginManagement>
    ....
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.10.0</version>
        <configuration>
            <source>1.8</source>
            <target>1.8</target>
        </configuration>
    </plugin>
    ....
</pluginManagement>
```

在这里，我们锁定了插件版本并将其配置为使用Java 8来构建项目。但是，任何子项目中都不需要额外的插件声明，构建框架默认激活此配置。因此，此配置意味着构建必须使用Java 8跨所有模块编译此项目。

总的来说，**明确声明配置并锁定多模块项目中所需的任何插件的版本可能是一种很好的做法**。因此，不同的子项目只能从父POM继承所需的插件配置，并根据需要应用它们。

这消除了重复的声明，简化了POM文件，并提高了构建的可重复性。

## 6. 总结

在本文中，我们了解了如何集中和管理构建项目所需的Maven插件。

首先，我们查看了插件及其在项目POM中的用法。然后我们更详细地了解了Maven插件管理机制，以及这如何帮助减少重复并提高构建配置的可维护性。