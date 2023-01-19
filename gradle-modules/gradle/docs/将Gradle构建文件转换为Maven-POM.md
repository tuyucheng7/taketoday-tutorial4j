## 1. 简介

在本教程中，我们介绍如何将Gradle构建文件转换为Maven POM文件，我们使用Gradle 7.4作为演示版本，并探索一些可用的自定义选项。

## 2. Gradle构建文件

首先使用Gradle命令或IDE构建一个标准的Gradle Java项目gradle-to-maven，使用以下build.gradle文件：

```groovy
repositories {
    mavenCentral()
}

group = 'cn.tuyucheng.taketoday'
version = '1.0.0'

apply plugin: 'java'

dependencies {
    implementation 'org.slf4j:slf4j-api:1.7.32'
    testImplementation 'junit:junit:4.13.2'
}
```

## 3. Maven插件

Gradle附带一个[Maven插件](https://docs.gradle.org/current/userguide/maven_plugin.html)，它提供了将Gradle文件转换为Maven POM文件的支持，并且还可以将工件部署到Maven仓库。

要使用它，我们需要将maven-publish插件添加到build.gradle文件中：

```groovy
apply plugin: 'maven-publish'
```

该插件使用Gradle文件中存在的group和version，并将它们添加到POM文件中；此外，它会自动将项目目录名称用作artifactId。

该插件也会自动添加publish任务；因此，为了完成转换，让我们将publishing的基本定义添加到build文件中：

```groovy
publishing {
    publications {
        customLibrary(MavenPublication) {
            from components.java
        }
    }

    repositories {
        maven {
            name = 'sampleRepo'
            url = layout.buildDirectory.dir("repo")
        }
    }
}
```

现在我们可以将customLibrary发布到基于本地目录的仓库中，以下用于演示：

```shell
gradle publish
```

运行上述命令会在项目根目录下生成一个包含以下子目录的build目录：

-   libs：包含名称为${artifactId}-${version}.jar的jar包
-   ))publications/customLibrary：包含转换后的POM文件，名称为pom-default.xml))
-   tmp/jar：包含MANIFEST.MF文件
-   repo：用作包含已发布工件的仓库的文件系统位置

生成的POM文件如下所示：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <modelVersion>4.0.0</modelVersion>
    <groupId>cn.tuyucheng.taketoday</groupId>
    <artifactId>gradle-to-maven</artifactId>
    <version>1.0.0</version>
    <dependencies>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.32</version>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
</project>
```

请注意，))POM中不包括测试范围依赖项，默认runtime范围分配给所有其他依赖项))。

publish任务还将这个POM文件和JAR上传到指定的仓库(在本例中为repo目录)。

## 4. 自定义Maven插件

在某些情况下，在生成的POM文件中自定义项目信息可能很有用，让我们来看看这些用例。

### 4.1 groupId、artifactId和version

可以在publications/{publicationName}块中处理对groupId、artifactId和POM版本的更改：

```groovy
publishing {
    publications {
        customLibrary(MavenPublishing) {
            groupId = 'cn.tuyucheng.taketoday.sample'
            artifactId = 'gradle-maven-converter'
            version = '2.0.0'
        }
    }
}
```

现在运行publish任务会生成包含上面提供的信息的POM文件：

```xml
<groupId>cn.tuyucheng.taketoday.sample</groupId>
<artifactId>gradle-maven-converter</artifactId>
<version>2.0.0</version>
```

### 4.2 自动生成的内容

Maven插件还可以直接更改任何生成的POM元素。例如，要将默认runtime范围更改为compile，我们可以将以下闭包添加到pom.withXml方法：

```groovy
pom.withXml {
    asNode().dependencies
            .dependency
            .findAll { dependency ->
                // find all dependencies with runtime scope
                dependency.scope.text() == 'runtime'
            }
            .each { dependency ->
                // set the scope to 'compile'
                dependency.scope.value = 'compile'
            }
}
```

这将更改生成的POM文件中所有依赖项的范围为compile：

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.32</version>
    <scope>compile</scope>
</dependency>
```

### 4.3 附加信息

最后，))如果我们想添加其他的信息，我们可以在pom方法中包含这些Maven支持的元素))。

下面添加一些许可证信息：

```groovy
//...
pom {
    licenses {
        license {
            name = 'The Apache License, Version 2.0'
            url = 'http://www.apache.org/licenses/LICENSE-2.0.txt'
        }
    }
}
//...
```

之后，我们可以看到添加到POM的许可证信息：

```xml
<!-- ...  -->
<licenses>
    <license>
        <name>The Apache License, Version 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
    </license>
</licenses>
<!-- ... -->
```

## 5. 总结

在这个教程中，我们学习了如何将Gradle构建文件转换为Maven POM。