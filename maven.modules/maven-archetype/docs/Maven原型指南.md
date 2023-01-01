## 1. 概述

Maven原型是一种项目的抽象，可以实例化为具体的自定义Maven项目。简而言之，**它是一个模板项目模板，可以从中创建其他项目**。

使用原型的主要好处是使项目开发标准化，并使开发人员能够轻松遵循最佳实践，同时更快地引导他们的项目。

在本教程中，我们将了解如何创建自定义原型，以及如何使用它通过maven-archetype-plugin生成Maven项目。

## 2. Maven原型描述符

**Maven原型描述符是原型项目的核心**，它是一个名为archetype-metadata.xml的XML文件，位于jar的META-INF/maven目录中。

它用于描述原型的元数据：

```xml
<archetype-descriptor
        xsi:schemaLocation="http://maven.apache.org/plugins/maven-archetype-plugin/archetype-descriptor/1.0.0 http://maven.apache.org/xsd/archetype-descriptor-1.0.0.xsd
        http://maven.apache.org/plugins/maven-archetype-plugin/archetype-descriptor/1.0.0"
        xmlns="http://maven.apache.org/plugins/maven-archetype-plugin/archetype-descriptor/1.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        name="microprofile12">

    <requiredProperties>
        <requiredProperty key="app-path">
            <defaultValue>resources</defaultValue>
        </requiredProperty>
        <requiredProperty key="greeting-msg">
            <defaultValue>Hi, I was generated from an archetype!</defaultValue>
        </requiredProperty>
        <requiredProperty key="liberty-plugin-version">
            <defaultValue>2.4.2</defaultValue>
        </requiredProperty>
    </requiredProperties>

    <fileSets>
        <fileSet filtered="true" packaged="true">
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.java</include>
            </includes>
        </fileSet>
        <fileSet>
            <directory>src/main/liberty/config</directory>
            <includes>
                <include>server.xml</include>
            </includes>
        </fileSet>
    </fileSets>
</archetype-descriptor>
```

<requiredProperties\>标签用于在项目生成期间提供属性,因此，系统将提示我们为它们提供值，并选择接受默认值。

另一方面，<fileSets\>用于配置将哪些资源复制到具体生成的项目中，过滤文件意味着占位符将在生成过程中替换为提供的值。

并且，通过在<fileSet\>中使用packaged=”true”，我们表示所选文件将添加到package属性指定的文件夹层次结构中。

如果我们要生成一个多模块项目，那么标签<modules\>可以帮助配置生成项目的所有模块。

请注意，此文件是关于Archetype 2及更高版本的，在1.0.x版本中，该文件名为archetype.xml，并且具有不同的结构。

有关更多信息，请务必查看[Apache官方文档](https://maven.apache.org/guides/mini/guide-creating-archetypes.html)。

## 3. 如何创建原型

**原型是一个普通的Maven项目，具有以下额外内容**：

-   src/main/resources/archetype-resources：是从中将资源复制到新创建的项目的模板
-   src/main/resources/META-INF/maven/archetype-metadata.xml：是用于描述原型元数据的描述符

要手动创建原型，我们可以从一个新创建的Maven项目开始，然后我们可以添加上面提到的资源。

或者，我们可以通过使用archetype-maven-plugin生成，然后自定义archetype-resources目录和archetype-metadata.xml文件的内容。

要生成原型，我们可以使用：

```bash
mvn archetype:generate -B -DarchetypeArtifactId=maven-archetype-archetype \
  -DarchetypeGroupId=maven-archetype \
  -DgroupId=cn.tuyucheng.taketoday \
  -DartifactId=test-archetype
```

我们还可以从现有的Maven项目创建原型：

```bash
mvn archetype:create-from-project
```

它在target/generated-sources/archetype中生成，随时可以使用。

无论我们如何创建原型，我们最终都会得到以下结构：

```powershell
archetype-root/
├── pom.xml
└── src
    └── main
        ├── java
        └── resources
            ├── archetype-resources
            │   ├── pom.xml
            │   └── src
            └── META-INF
                └── maven
                    └── archetype-metadata.xml
```

现在，我们可以通过将资源放入archetype-resources目录并在archetype-metadata.xml文件中配置它们来开始构建我们的原型。

## 4. 构建原型

现在，我们已准备好自定义原型，对于此过程的亮点，我们将展示创建一个简单的Maven原型，用于生成基于JAX-RS 2.1的RESTful应用程序。

让我们称之为maven-archetype。

### 4.1 原型的打包方式

让我们从修改位于maven-archetype目录下的原型项目的pom.xml开始：

```xml
<packaging>maven-archetype</packaging>
```

由于archetype-packaging扩展，这种类型的打包可用：

```xml
<build>
    <extensions>
        <extension>
            <groupId>org.apache.maven.archetype</groupId>
            <artifactId>archetype-packaging</artifactId>
            <version>3.0.1</version>
        </extension>
    </extensions>
    <!--....-->
</build>
```

### 4.2 添加pom.xml

现在让我们在archetype-resources目录下创建一个pom.xml文件：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>${groupId}</groupId>
    <artifactId>${artifactId}</artifactId>
    <version>${version}</version>
    <packaging>war</packaging>

    <dependencies>
        <dependency>
            <groupId>javax.ws.rs</groupId>
            <artifactId>javax.ws.rs-api</artifactId>
            <version>2.1</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

正如我们所看到的，groupId、artifactId和version是参数化的，在从此原型创建新项目期间，它们将被替换。

我们可以将生成的项目所需的所有内容(例如依赖项和插件)放入pom.xml中。在这里，我们添加了JAX RS依赖项，因为原型将用于生成基于RESTful的应用程序。

### 4.3 添加所需资源

接下来，我们可以在archetype-resources/src/main/java中为我们的应用程序添加一些Java代码。

用于配置JAX-RS应用程序的类：

```java
package ${package};
// import
@ApplicationPath("${app-path}")
public class AppConfig extends Application {
}
```

以及一个ping资源类：

```java
@Path("ping")
public class PingResource{
    // ...
}
```

最后，将服务器配置文件server.xml放在archetype-resources/src/main/config/liberty中。

### 4.4 配置元数据

添加所有需要的资源后，我们现在可以通过archetype-metadata.xml文件配置在生成过程中复制哪些资源。


我们可以告诉我们的原型，我们希望复制所有Java源文件：

```xml
<archetype-descriptor name="maven-archetype">
    <!-- ... -->
    <fileSets>
        <fileSet filtered="true" packaged="true">
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.java</include>
            </includes>
        </fileSet>
        <fileSet>
            <directory>src/main/config/liberty</directory>
            <includes>
                <include>server.xml</include>
            </includes>
        </fileSet>
    </fileSets>
</archetype-descriptor>
```

在这里，我们希望复制来自src/main/java目录中的所有Java文件，以及来自src/main/config/liberty中的server.xml文件。

### 4.5 安装原型

现在我们已经完成了所有内容，我们可以通过调用以下命令来安装原型：

```bash
mvn install
```

此时，原型已在文件archetype-catalog.xml中注册，该文件位于Maven本地仓库中，因此可以使用了。

## 5. 使用已安装的原型

**maven-archetype-plugin允许用户通过generate目标和现有原型创建Maven项目**，有关此插件的更多信息，你可以访问[主页](https://maven.apache.org/archetype/maven-archetype-plugin/index.html)。

此命令使用此插件从我们的原型生成Maven项目：

```bash
mvn archetype:generate -DarchetypeGroupId=cn.tuyucheng.taketoday.archetypes
                       -DarchetypeArtifactId=maven-archetype
                       -DarchetypeVersion=1.0.0
                       -DgroupId=cn.tuyucheng.taketoday.restful
                       -DartifactId=cool-jaxrs-sample
                       -Dversion=1.0.0
```

然后我们应该将原型的GAV作为参数传递给maven-archetype-plugin:generate目标，我们也可以传递我们要生成的具体项目的GAV，否则，我们可以以交互方式提供它们。

因此，具体的cool-jaxrs-sample生成的项目无需任何更改即可运行。所以，我们可以通过调用以下命令来运行它：

```bash
mvn package liberty:run
```

然后我们可以访问这个URL：

```bash
http://localhost:9080/cool-jaxrs-sample/<app-path>/ping
```

## 6. 总结

在本文中，我们展示了如何构建和使用Maven原型。

我们已经演示了如何创建原型以及如何通过archetype-metadata.xml文件配置模板资源。