## 1. 概述

本文将展示在项目中使用Spring工件时要使用的Maven仓库，请参阅[Spring wiki](https://github.com/SpringSource/spring-framework/wiki/SpringSource-repository-FAQ#what-repositories-are-available)上的完整仓库列表。以前的SpringSource工件管理基础设施是maven.springframework.org，现在已经弃用，取而代之的是更强大的repo.spring.io。

## 2. Maven发布

所有GA/Release工件都发布到Maven Central，因此如果只需要release，则无需将任何新的仓库添加到pom中。但是，**如果由于某种原因Maven Central不可用，则还有一个自定义的、**[可浏览的](https://repo.spring.io/release/)**Maven仓库可用于Spring Releases**：

```xml
<repositories>
    <repository> 
        <id>repository.spring.release</id> 
        <name>Spring GA Repository</name> 
        <url>http://repo.spring.io/release</url> 
    </repository>
</repositories>
```

Spring工件版本控制规则在[项目wiki](https://github.com/SpringSource/spring-framework/wiki/Downloading-Spring-artifacts#wiki-artifact_versioning)上有解释。

里程碑和快照不会直接发布到Maven Central，因此它们有自己特定的仓库。

## 3. Maven里程碑和发布候选版本

对于里程碑和RC(Release Candidates)，需要将以下仓库添加到pom中：

```xml
<repositories>
    <repository> 
        <id>repository.spring.milestone</id> 
        <name>Spring Milestone Repository</name> 
        <url>http://repo.spring.io/milestone</url> 
    </repository>
</repositories>
```

一旦定义了这个仓库，项目就可以开始使用Spring[里程碑依赖项](https://repo.spring.io/milestone/)了：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>4.2.0.RC3</version>
</dependency>
```

## 4. Maven快照

与里程碑类似，Spring快照托管在自定义仓库中：

```xml
<repositories>
    <repository> 
        <id>repository.spring.snapshot</id> 
        <name>Spring Snapshot Repository</name> 
        <url>http://repo.spring.io/snapshot</url> 
    </repository>
</repositories>
```

一旦在pom中启用了仓库，项目就可以开始使用Spring快照：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>4.2.5.BUILD-SNAPSHOT</version>
</dependency>
```

甚至：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>4.3.0.BUILD-SNAPSHOT</version>
</dependency>
```

现在也可以[浏览](https://repo.spring.io/snapshot/)快照仓库。

## 5. Spring OSGI的Maven仓库

与OSGI兼容的Spring工件在SpringSource[企业捆绑包仓库(Enterprise Bundle Repository-简称EBR)](https://docs.spring.io/s2-dmserver/2.0.0.M5/user-guide/html/ch04s04.html)中进行维护。这些仓库包含整个Spring框架的有效OSGI捆绑包和库，以及这些库的一组完整的依赖项。对于捆绑包：

```xml
<repository>
    <id>com.springsource.repository.bundles.release</id> 
    <name>SpringSource Enterprise Bundle Repository - SpringSource Bundle Releases</name> 
    <url>http://repository.springsource.com/maven/bundles/release</url> 
</repository>
<repository> 
    <id>com.springsource.repository.bundles.external</id> 
    <name>SpringSource Enterprise Bundle Repository - External Bundle Releases</name> 
    <url>http://repository.springsource.com/maven/bundles/external</url> 
</repository>
```

对于OSGI兼容库：

```xml
<repository>
    <id>com.springsource.repository.libraries.release</id>
    <name>SpringSource Enterprise Bundle Repository - SpringSource Library Releases</name>
    <url>http://repository.springsource.com/maven/libraries/release</url>
</repository>
<repository>
    <id>com.springsource.repository.libraries.external</id>
    <name>SpringSource Enterprise Bundle Repository - External Library Releases</name>
    <url>http://repository.springsource.com/maven/libraries/external</url>
</repository>
```

注意：**SpringSource EBR现在是只读的**，不会再在那里发布更多的Spring Framework 3.2.x版本。

## 6. 总结

本文介绍了有关在pom中设置特定于Spring的Maven仓库的实用信息，以便使用发布候选、里程碑和快照版本。