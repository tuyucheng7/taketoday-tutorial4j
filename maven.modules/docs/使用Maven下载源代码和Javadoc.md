## 1. 概述

查看不同库和框架的源代码和文档是了解有关它们的更多信息的好方法。

在这个简短的教程中，我们将了解如何配置Maven，或要求Maven来为我们下载依赖项源代码及其Javadoc。

## 2. 命令行

默认情况下，Maven只下载每个依赖项的实际JAR文件，而不是源文件和文档文件。

**要只下载源代码，首先，我们应该导航到包含pom.xml的目录，然后执行命令**：

```bash
mvn dependency:sources
```

下载源代码可能需要一段时间。同样，**要只下载Javadocs，我们可以使用以下命令**：

```bash
mvn dependency:resolve -Dclassifier=javadoc
```

当然，我们也可以在一个命令中同时下载它们：

```bash
mvn dependency:sources dependency:resolve -Dclassifier=javadoc
```

显然，如果我们在发出这些命令后添加新的依赖项，我们必须重新发出命令以下载新依赖项的源代码和Javadoc。

## 3. Maven设置

**还可以在所有Maven项目上下载系统范围内的源代码和文档**，为此，我们应该编辑~/m2/settings.xml文件或创建一个文件并向其中添加以下配置：

```xml
<settings>
    <!-- ... other settings omitted ... -->
    <profiles>
        <profile>
            <id>downloadSources</id>
            <properties>
                <downloadSources>true</downloadSources>
                <downloadJavadocs>true</downloadJavadocs>
            </properties>
        </profile>
    </profiles>

    <activeProfiles>
        <activeProfile>downloadSources</activeProfile>
    </activeProfiles>
</settings>
```

如上所示，我们正在创建一个profile并默认激活它。在这个profile中，我们设置了两个属性，告诉Maven下载源代码和文档。此外，Maven会将这些设置应用于所有项目。

## 4. pom.xml

**甚至可以将此配置放入pom.xml中，这样，我们可以强制所有项目贡献者下载源代码和文档作为依赖项解析的一部分**：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-dependency-plugin</artifactId>
            <version>3.1.2</version>
            <executions>
                <execution>
                    <goals>
                        <goal>sources</goal>
                        <goal>resolve</goal>
                    </goals>
                    <configuration>
                        <classifier>javadoc</classifier>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

在这里，我们通过配置[maven-dependency-plugin](https://maven.apache.org/plugins/maven-dependency-plugin/)来下载源代码和文档。

## 5. IDE设置

我们还可以设置我们最喜欢的IDE来为我们做这件事。例如，在IntelliJ IDEA中，我们只需转到Settings > Build, Execution, Deployment > Build Tools > Maven > Importing并选中Sources和Documentation复选框：

<img src="../assets/img_6.png">

## 6. 总结

在这个快速教程中，我们了解了如何以各种方式在Maven中下载依赖源代码和文档，从命令行解决方案到每个项目或系统范围的配置。