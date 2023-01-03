## 1. 概述

打包类型是任何Maven项目的一个重要方面，它指定项目生成的工件类型。通常，构建会生成jar、war、pom或其他可执行文件。

**Maven提供了许多默认的打包类型，也提供了定义自定义打包类型的灵活性**。

在本教程中，我们将深入探讨Maven打包类型。首先，我们将查看Maven中的构建生命周期。然后，我们将讨论每种打包类型、它们代表什么以及它们对项目生命周期的影响。最后，我们将看到如何定义自定义打包类型。

## 2. 默认打包类型

[Maven](https://www.baeldung.com/maven)提供了许多默认的打包类型，包括jar、war、ear、pom、rar、ejb和maven-plugin，**每种打包类型都遵循由阶段组成的构建生命周期**。通常，每个阶段都是一系列目标并执行特定的任务。

**不同的打包类型在特定阶段可能有不同的目标**。比如在jar打包类型的package阶段，执行maven-jar-plugin的jar目标。相反，对于一个war项目，maven-war-plugin的war目标在同一阶段执行。

### 2.1 jar

Java存档(或jar)是最流行的打包类型之一，具有这种打包类型的项目会生成一个扩展名为.jar的压缩zip文件，它可能包括纯Java类、接口、资源和元数据文件。

首先，让我们看一下jar的一些默认目标到构建阶段绑定：

-   resources: resources
-   compiler: compile
-   resources: testResources
-   compiler: testCompile
-   surefire: test
-   **jar: jar**
-   install: install
-   deploy: deploy

事不宜迟，我们来定义一个jar项目的打包类型：

```xml
<packaging>jar</packaging>
```

**如果未指定任何内容，Maven假定打包类型为jar**。

### 2.2 war

简而言之，Web应用程序存档(或war)包含与Web应用程序相关的所有文件，它可能包括Java Servlet、JSP、HTML页面、部署描述符和相关资源。总体而言，war与jar具有相同的目标绑定，但有一个例外—war的package阶段具有不同的目标，那就是war。

毫无疑问，jar和war是Java社区最流行的打包类型，这两者之间的[详细区别](https://www.baeldung.com/java-jar-war-packaging)可能会很有趣。

让我们定义一个web应用程序的打包类型：

```xml
<packaging>war</packaging>
```

**其他打包类型ejb、par和rar也具有类似的生命周期，但每个都有不同的package目标**。

```
ejb:ejb或par:par或rar:rar
```

### 2.3 ear

企业应用程序存档(或ear)是一个包含J2EE应用程序的压缩文件，它由一个或多个模块组成，这些模块可以是Web模块(打包为war文件)或EJB模块(打包为jar文件)，也可以同时是两者。

换句话说，ear是jars和wars的超集，需要一个应用服务器来运行应用程序，而war只需要一个web容器或web服务器来部署它。区分Web服务器和应用程序服务器的方面，以及Java中那些[流行的服务器](https://www.baeldung.com/java-servers)是什么，对于Java开发人员来说都是重要的概念。

让我们定义ear的默认目标绑定：

-   ear: generate-application-xml
-   resources: resources
-   **ear: ear**
-   install: install
-   deploy: deploy

以下是我们如何定义此类项目的打包类型：

```xml
<packaging>ear</packaging>
```

### 2.4 pom

在所有的打包类型中，pom是最简单的一种，它有助于创建聚合器和父项目。

**聚合器或**[多模块项目](https://www.baeldung.com/maven-multi-module)**组装来自不同来源的子模块**，这些子模块是常规的Maven项目，并遵循自己的构建生命周期。聚合器POM在modules元素下具有子模块的所有引用。

**父项目允许你定义POM之间的继承关系**，父POM共享某些配置、插件和依赖项，以及它们的版本。父元素的大多数元素都由其子元素继承，例外情况包括artifactId、name和prerequisites。

因为没有要处理的资源，也没有要编译或测试的代码，因此，pom项目的工件会自行生成，而不是生成任何可执行文件。

让我们定义一个多模块项目的打包类型：

```xml
<packaging>pom</packaging>
```

**此类项目具有最简单的生命周期，仅包含两个步骤：install和deploy**。

### 2.5 maven-plugin

Maven提供了多种有用的插件，但是，在某些情况下，默认插件可能不够我们使用。在这种情况下，该工具提供了根据项目需要[创建Maven插件](https://www.baeldung.com/maven-plugin)的灵活性。

要创建插件，请设置项目的打包类型为maven-plugin：

```xml
<packaging>maven-plugin</packaging>
```

maven-plugin的生命周期类似于jar的生命周期，但有两个例外：

-   plugin：descriptor绑定到generate-resources阶段
-   plugin：addPluginArtifactMetadata添加到package阶段

**对于这种类型的项目，需要一个maven-plugin-api依赖**。

### 2.6 ejb

Enterprise Java Beans(或[ejb](https://www.baeldung.com/ejb-intro))有助于创建可扩展的分布式服务器端应用程序，EJB通常提供应用程序的业务逻辑。典型的EJB体系结构由三个组件组成：Enterprise Java Beans(EJB)、EJB容器和应用程序服务器。

现在，让我们定义EJB项目的打包类型：

```xml
<packaging>ejb</packaging>
```

ejb打包类型也具有与jar打包类似的生命周期，但具有不同的package目标，此类项目的package目标是ejb:ejb。

**该项目采用ejb打包类型，需要一个maven-ejb-plugin来执行生命周期目标**。Maven提供对EJB 2和3的支持，如果未指定版本，则使用默认版本2。

### 2.7 rar

资源适配器(或rar)是一个存档文件，作为将资源适配器部署到应用程序服务器的有效格式。基本上，它是将Java应用程序连接到企业信息系统(EIS)的系统级驱动程序。

下面是资源适配器的打包类型的声明：

```xml
<packaging>rar</packaging>
```

每个资源适配器存档由两部分组成：一个包含源代码的jar文件和一个用作部署描述符的ra.xml。

同样，生命周期阶段与jar或war打包相同，但有一个例外：**package阶段执行由maven-rar-plugin组成的rar目标来打包档案**。

## 3. 其他打包类型

到目前为止，我们已经了解了Maven默认提供的各种打包类型。现在，假设我们希望我们的项目生成一个带有.zip扩展名的工件，在这种情况下，默认的打包类型无法帮助我们。

Maven还通过插件提供了一些更多的打包类型，在这些插件的帮助下，我们可以定义自定义打包类型及其构建生命周期，其中一些类型是：

-   msi
-   rpm
-   tar
-   tar.bz2
-   tar.gz
-   tbz
-   zip

要定义自定义类型，我们必须定义其打包类型及其生命周期中的阶段。为此，请在src/main/resources/META-INF/plexus目录下创建一个components.xml文件：

```xml
<component>
    <role>org.apache.maven.lifecycle.mapping.LifecycleMapping</role>
    <role-hint>zip</role-hint>
    <implementation>org.apache.maven.lifecycle.mapping.DefaultLifecycleMapping</implementation>
    <configuration>
        <phases>
            <process-resources>org.apache.maven.plugins:maven-resources-plugin:resources</process-resources>
            <package>cn.tuyucheng.taketoday.maven.plugins:maven-zip-plugin:zip</package>
            <install>org.apache.maven.plugins:maven-install-plugin:install</install>
            <deploy>org.apache.maven.plugins:maven-deploy-plugin:deploy</deploy>
        </phases>
    </configuration>
</component>
```

到目前为止，Maven对我们的新打包类型及其生命周期一无所知。为了使其可见，让我们在项目的pom文件中添加插件并将extensions设置为true：

```xml
<plugins>
    <plugin>
        <groupId>cn.tuyucheng.taketoday.maven.plugins</groupId>
        <artifactId>maven-zip-plugin</artifactId>
        <extensions>true</extensions>
    </plugin>
</plugins>
```

现在，该项目将可用于扫描，系统也会查看plugins和components.xml文件。

除了所有这些类型之外，Maven还通过外部项目和插件提供了许多其他打包类型。例如，nar(本机存档)、swf和swc是用于生成Adobe Flash和Flex内容的项目的打包类型。对于这样的项目，我们需要一个定义自定义打包的插件和一个包含该插件的仓库。

## 4. 总结

在本文中，我们研究了Maven中可用的各种打包类型。此外，我们还熟悉了这些打包类型代表什么以及它们在生命周期中的不同之处。最后，我们还学习了如何定义自定义打包类型和自定义默认构建生命周期。