## 1. 概述

[Maven Polyglot](https://github.com/takari/polyglot-maven/)是一组Maven核心扩展，允许使用任何语言编写POM模型，这包括除XML之外的许多脚本和标记语言。

**Maven多语言的主要目标是摆脱XML，因为它现在不再是首选语言**。

在本教程中，我们将首先了解Maven核心扩展概念和Maven Polyglot项目。

然后，我们将展示如何编写一个允许从JSON文件而不是著名的pom.xml构建POM模型的Maven核心扩展。

## 2. Maven核心扩展加载机制

**Maven核心扩展是在Maven初始化时和Maven项目构建开始之前加载的插件**，这些插件允许在不改变核心的情况下改变Maven的行为。

例如，在启动时加载的插件可以覆盖Maven默认行为，并且可以从pom.xml之外的另一个文件读取POM模型。

从技术上讲，**Maven核心扩展是在extensions.xml文件中声明的Maven工件**：

```bash
${projectDirectory}/.mvn/extensions.xml
```

下面是一个扩展的例子：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<extensions>
    <extension>
        <groupId>cn.tuyucheng.taketoday.maven.polyglot</groupId>
        <artifactId>maven-polyglot-json-extension</artifactId>
        <version>1.0.0</version>
    </extension>
</extensions>
```

最后需要注意的是，**该机制需要Maven 3.3.1或更高版本**。

## 3. Maven Polyglot

**Maven Polyglot是核心扩展的集合**，其中每一个都负责从脚本或标记语言中读取POM模型。

Maven Polyglot为以下语言提供扩展：

```plaintext
+-----------+-------------------+--------------------------------------+
| Language  | Artifact Id       | Accepted POM files                   |
+-----------+-------------------+--------------------------------------+
| Atom      | polyglot-atom     | pom.atom                             |
| Clojure   | polyglot-clojure  | pom.clj                              |
| Groovy    | polyglot-groovy   | pom.groovy, pom.gy                   |
| Java      | polyglot-java     | pom.java                             |
| Kotlin    | polyglot-kotlin   | pom.kts                              |
| Ruby      | polyglot-ruby     | pom.rb,Mavenfile, Jarfile, Gemfile   |
| Scala     | polyglot-scala    | pom.scala                            |
| XML       | polyglot-xml      | pom.xml                              |
| YAML      | polyglot-yaml     | pom.yaml, pom.yml                    |
+-----------+-------------------+--------------------------------------+
```

在接下来的部分中，我们将首先了解如何使用上述支持的语言之一构建Maven项目。

然后，我们将编写我们的扩展来支持基于JSON的POM。

## 4. 使用Maven多语言扩展

基于与XML不同的语言构建Maven项目的一种选择是使用Polyglot项目提供的工件之一。

在我们的示例中，**我们将创建一个带有pom.yaml配置文件的Maven项目**。

第一步是创建Maven核心扩展文件：

```bash
${projectDirectory}/.mvn/extensions.xml
```

然后我们将添加以下内容：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<extensions>
    <extension>
        <groupId>io.takari.polyglot</groupId>
        <artifactId>polyglot-yaml</artifactId>
        <version>0.3.1</version>
    </extension>
</extensions>
```

根据上述语言，随意将artifactId调整为你选择的语言，并检查是否有任何[新版本](https://search.maven.org/search?q=io.takari.polyglot)可用。

最后一步是在YAML文件中提供项目元数据：

```yaml
modelVersion: 4.0.0
groupId: cn.tuyucheng.taketoday.maven.polyglot
artifactId: maven-polyglot-yml-app
version: 1.0.0
name: 'YAML Demo'

properties: { maven.compiler.source: 1.8, maven.compiler.target: 1.8 }
```

现在我们可以像往常一样运行我们的构建。例如，我们可以调用以下命令：

```bash
mvn clean install
```

## 5. 使用Polyglot翻译插件

获取基于其中一种受支持语言的项目的另一种选择是使用[polyglot-translate-plugin](https://search.maven.org/search?q=a:polyglot-translate-plugin)。

这意味着我们可以从具有传统pom.xml的现有Maven项目开始。

然后，**我们可以使用translate插件将现有的pom.xml项目转换为所需的多语言**：

```bash
mvn io.takari.polyglot:polyglot-translate-plugin:translate -Dinput=pom.xml -Doutput=pom.yml
```

## 6. 编写自定义扩展

由于JSON不是Maven Polyglot项目提供的语言之一，**我们将实现一个简单的扩展，允许从JSON文件读取项目元数据**。

我们的扩展将提供Maven ModelProcessor API的自定义实现，它将覆盖Maven默认实现。

为实现这一点，我们将更改如何定位POM文件以及如何读取元数据并将其转换为Maven Model API的行为。

### 6.1 Maven依赖项

首先，我们将创建一个具有以下依赖项的Maven项目：

```xml
<dependency>
    <groupId>org.apache.maven</groupId>
    <artifactId>maven-core</artifactId>
    <version>3.5.4</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.0</version>
</dependency>
```

**在这里我们使用**[maven-core](https://search.maven.org/artifact/org.apache.maven/maven-core)**依赖项，因为我们将实现一个核心扩展**。[Jackson](https://search.maven.org/search?q=a:jackson-databind)依赖项用于反序列化JSON文件。

由于Maven使用Plexus依赖注入容器，**我们需要我们的实现是一个Plexus组件**，所以我们需要这个插件来生成Plexus元数据：

```xml
<plugin>
    <groupId>org.codehaus.plexus</groupId>
    <artifactId>plexus-component-metadata</artifactId>
    <version>1.7.1</version>
    <executions>
        <execution>
            <goals>
                <goal>generate-metadata</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### 6.2 自定义ModelProcessor实现

Maven通过调用ModelBuilder.build()方法来构建POM模型，该方法又委托给ModelProcessor.read()方法。

Maven提供了一个DefaultModelProcessor实现，它默认从位于根目录或指定为参数命令的pom.xml文件中读取POM模型。

**因此，我们将提供一个自定义的ModelProcessor实现，该实现将覆盖默认行为。也就是POM模型文件所在位置，以及如何读取**。

因此，让我们首先创建一个CustomModelProcessor实现并将其标记为Plexus组件：

```java
@Component(role = ModelProcessor.class)
public class CustomModelProcessor implements ModelProcessor {

    @Override
    public File locatePom(File projectDirectory) {
        return null;
    }

    @Override
    public Model read(InputStream input, Map<String, ?> options) 
          throws IOException, ModelParseException {
        return null;
    }
    // ...
}
```

**@Component注解将使实现可用于DI容器(Plexus)的注入**，因此，当Maven需要在ModelBuilder中注入ModelProcessor时，Plexus容器将提供此实现而不是DefaultModelProcessor。

接下来，**我们将提供locatePom()方法的实现**，此方法返回Maven将在其中读取项目元数据的文件。

所以我们将返回一个pom.json文件(如果它存在)，否则返回pom.xml，就像我们通常所做的那样：

```java
@Override
public File locatePom(File projectDirectory) {
    File pomFile = new File(projectDirectory, "pom.json");
    if (!pomFile.exists()) {
        pomFile = new File(projectDirectory, "pom.xml");
    }
    return pomFile;
}
```

下一步是读取此文件并将其转换为Maven Model，这是通过read()方法实现的：

```java
@Requirement
private ModelReader modelReader;

@Override
public Model read(InputStream input, Map<String, ?> options) throws IOException, ModelParseException {
 
    FileModelSource source = getFileFromOptions(options);
    try (InputStream is = input) {
        //JSON FILE ==> Jackson
        if (isJsonFile(source)) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(input, Model.class);
        } else {
            // XML FILE ==> DefaultModelReader
            return modelReader.read(input, options);
        }
    }
    return model;
}
```

在此示例中，我们检查该文件是否为JSON文件，并使用Jackson将其反序列化为Maven Model。否则，它就是一个普通的XML文件，它将被Maven DefaultModelReader读取。

我们需要构建扩展，它就可以使用了：

```bash
mvn clean install
```

### 6.3 使用扩展

为了演示扩展的使用，我们将使用Spring Boot Web项目。

首先，我们将创建一个Maven项目，并删除pom.xml。

然后，**我们将在${projectDirectory}/.mvn/extensions.xml中添加上面实现的扩展**：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<extensions>
    <extension>
        <groupId>cn.tuyucheng.taketoday.maven.polyglot</groupId>
        <artifactId>maven-polyglot-json-extension</artifactId>
        <version>1.0.0</version>
    </extension>
</extensions>
```

最后，我们创建包含以下内容的pom.json：

```json
{
    "modelVersion": "4.0.0",
    "groupId": "cn.tuyucheng.taketoday.maven.polyglot",
    "artifactId": "maven-polyglot-json-app",
    "version": "1.0.0",
    "name": "JsonMavenPolyglot",
    "parent": {
        "groupId": "org.springframework.boot",
        "artifactId": "spring-boot-starter-parent",
        "version": "2.0.5.RELEASE",
        "relativePath": null
    },
    "properties": {
        "project.build.sourceEncoding": "UTF-8",
        "project.reporting.outputEncoding": "UTF-8",
        "maven.compiler.source": "1.8",
        "maven.compiler.target": "1.8",
        "java.version": "1.8"
    },
    "dependencies": [
        {
            "groupId": "org.springframework.boot",
            "artifactId": "spring-boot-starter-web"
        }
    ],
    "build": {
        "plugins": [
            {
                "groupId": "org.springframework.boot",
                "artifactId": "spring-boot-maven-plugin"
            }
        ]
    }
}
```

现在，我们可以使用以下命令运行该项目：

```bash
mvn spring-boot:run
```

## 7. 总结

在本文中，我们演示了如何通过Maven Polyglot项目更改默认的Maven行为，为了实现这一目标，我们使用了简化核心组件加载的新Maven 3.3.1功能。