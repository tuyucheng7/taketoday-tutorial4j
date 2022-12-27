## 1. 概述

在本教程中，我们将了解从Swagger API文档生成PDF文件的不同方法。要熟悉Swagger，请参阅我们的[使用Spring REST API设置Swagger 2](https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api)的教程。

## 2. 使用Maven插件生成PDF

从Swagger API文档生成PDF文件的第一个解决方案是基于一组Maven插件，通过这种方法，我们将在构建Java项目时获得PDF文件。

**生成所需PDF文件的步骤包括在Maven构建期间以特定顺序应用多个插件**，插件应配置为选取资源并将前一阶段的输出传播为下一阶段的输入。因此，让我们看看它们各自是如何工作的。

### 2.1 swagger-maven-plugin插件

我们将使用的第一个插件是[swagger-maven-plugin](https://search.maven.org/artifact/com.github.kongchen/swagger-maven-plugin/3.1.8/maven-plugin)，**这个插件为我们的REST API生成了swagger.json文件**：

```xml
<plugin>
    <groupId>com.github.kongchen</groupId>
    <artifactId>swagger-maven-plugin</artifactId>
    <version>3.1.3</version>
    <configuration>
        <apiSources>
            <apiSource>
                <springmvc>false</springmvc>
                <locations>cn.tuyucheng.taketoday.swagger2pdf.controller.UserController</locations>
                <basePath>/api</basePath>
                <info>
                    <title>DEMO REST API</title>
                    <description>A simple DEMO project for REST API documentation</description>
                    <version>v1</version>
                </info>
                <swaggerDirectory>${project.build.directory}/api</swaggerDirectory>
                <attachSwaggerArtifact>true</attachSwaggerArtifact>
            </apiSource>
        </apiSources>
    </configuration>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

我们需要指向API的位置，并定义构建过程中插件生成swagger.json文件的阶段。在这里，在<execution\>标签中，我们指定它应该在package阶段执行此操作。

### 2.2 swagger2markup -maven-plugin插件

我们需要的第二个插件是[swagger2markup-maven-plugin](https://search.maven.org/artifact/io.github.robwin/swagger2markup-maven-plugin/0.9.3/jar)，**它将前一个插件的swagger.json输出作为其输入来生成**[Asciidoc](https://www.baeldung.com/asciidoctor)：

```xml
<plugin>
    <groupId>io.github.robwin</groupId>
    <artifactId>swagger2markup-maven-plugin</artifactId>
    <version>0.9.3</version>
    <configuration>
        <inputDirectory>${project.build.directory}/api</inputDirectory>
        <outputDirectory>${generated.asciidoc.directory}</outputDirectory>
        <markupLanguage>asciidoc</markupLanguage>
    </configuration>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>process-swagger</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

在这里，我们指定了<inputDirectory\>和<outputDirectory\>标签。同样，我们将package定义为为我们的REST API生成Asciidoc的构建阶段。

### 2.3 asciidoctor -maven-plugin插件

我们将使用的第三个也是最后一个插件是[asciidoctor-maven-plugin](https://search.maven.org/artifact/org.asciidoctor/asciidoctor-maven-plugin/2.2.1/maven-plugin)，作为三个插件中的最后一个，**这个插件从**[Asciidoc](https://www.baeldung.com/asciidoctor)**生成一个PDF文件**：

```xml
<plugin>
    <groupId>org.asciidoctor</groupId>
    <artifactId>asciidoctor-maven-plugin</artifactId>
    <version>2.2.1</version>
    <dependencies>
        <dependency>
            <groupId>org.asciidoctor</groupId>
            <artifactId>asciidoctorj-pdf</artifactId>
            <version>1.6.0</version>
        </dependency>
    </dependencies>
    <configuration>
        <sourceDirectory>${project.build.outputDirectory}/../asciidoc</sourceDirectory>
        <sourceDocumentName>overview.adoc</sourceDocumentName>
        <attributes>
            <doctype>book</doctype>
            <toc>left</toc>
            <toclevels>2</toclevels>
            <generated>${generated.asciidoc.directory}</generated>
        </attributes>
    </configuration>
    <executions>
        <execution>
            <id>asciidoc-to-pdf</id>
            <phase>package</phase>
            <goals>
                <goal>process-asciidoc</goal>
            </goals>
            <configuration>
                <backend>pdf</backend>
                <outputDirectory>${project.build.outputDirectory}/api/pdf</outputDirectory>
            </configuration>
        </execution>
    </executions>
</plugin>
```

基本上，我们提供了在前一阶段生成Asciidoc的位置。然后，我们为其定义一个生成PDF文档的位置，并指定它应该生成PDF的阶段。我们再次使用package阶段。

## 3. 使用SwDoc生成PDF

Swagger toPDF是一个在线工具，可从[swdoc.org](https://www.swdoc.org/)获得，它使用提供的swagger.json规范在PDF文件中生成API文档，它依赖于[Swagger2Markup](https://github.com/Swagger2Markup/swagger2markup-cli)转换器和[AsciiDoctor ](https://asciidoctor.org/)。

它的原理与前面的解决方案类似，首先，Swagger2Markup将swagger.json转换为AsciiDoc文件。然后，Asciidoctor将这些文件解析为文档模型并将它们转换为PDF文件。

该解决方案易于使用，**如果我们已经拥有我们的Swagger 2 API文档，它是一个不错的选择**。

我们可以通过两种方式生成PDF：

-   提供我们的swagger.json文件的URL
-   将我们的swagger.json文件的内容粘贴到工具的文本框中

我们将使用[Swagger](https://petstore.swagger.io/)上公开提供的PetStore API文档。

出于我们的目的，我们将复制JSON文件并将其粘贴到文本框中：

<img src="../assets/img.png">

然后，我们点击“Generate”按钮并下载后，我们将得到PDF格式的文档：

<img src="../assets/img_1.png">

## 4. 总结

在这个简短的教程中，我们讨论了两种从Swagger API文档生成PDF的方法。

第一种方法依赖于Maven插件，我们可以在构建应用程序时使用三个插件并生成REST API文档。

第二种解决方案描述了如何使用SwDoc在线工具生成PDF文档，我们可以从指向swagger.json的链接生成文档，也可以将JSON文件内容复制粘贴到工具中。