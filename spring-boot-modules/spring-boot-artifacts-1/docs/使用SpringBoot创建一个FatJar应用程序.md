## 1. 简介

近年来最令人振奋的发展之一是不断简化Web应用程序的部署方式。

跳过所有无聊的中间历史步骤，我们到达了今天-那时我们不仅可以免除繁琐的Servlet和XML样板，而且可以免除大部分服务器本身。

本文重点介绍如何从Spring Boot应用程序**创建一个“fat jar”**-基本上是创建一个易于部署和运行的工件。

Boot提供开箱即用的无容器部署功能：我们需要做的就是在pom.xml中添加一些配置：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>2.4.0</version>
    </dependency>
</dependencies>

<plugins>
    <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <version>2.4.0</version>
    </plugin>
</plugins>
```

## 2. 构建并运行

有了这个配置，我们现在可以使用标准的mvn clean install简单地构建项目，这里没有什么不寻常的。

我们使用以下命令运行它：java -jar <artifact-name\>，非常简单直观。

适当的进程管理超出了本文的范围，但是即使在我们注销服务器时也能保持进程运行的一种简单方法是使用nohup命令：nohup java -jar <artifact-name\>。

停止spring-boot项目也与停止常规进程没有什么不同，无论我们是简单地ctrl+c还是kill <pid\>。

## 3. Fat Jar/Fat War

在幕后，spring-boot将所有项目依赖项与项目类一起打包到最终工件中(因此是“fat(胖)” jar)，嵌入式Tomcat服务器也是内置的。

因此，生成的工件是完全独立的，易于使用标准Unix工具(scp、sftp等)进行部署，并且可以在任何带有JVM的服务器上运行。

默认情况下，Boot会创建一个jar文件，但如果我们将pom.xml中的packaging属性更改为war，Maven将自然而然地构建一个war。

这当然既可以独立执行，也可以部署到Web容器中。

## 4. 进一步配置

大多数时候不需要额外的配置，一切都“正常工作”，但在某些特定情况下，我们可能需要明确告诉spring-boot主类是什么。一种方法是添加属性：

```xml
<properties>
    <start-class>cn.tuyucheng.taketoday.boot.Application</start-class>
</properties>
```

如果我们没有继承spring-boot-starter-parent，我们需要在Maven插件中执行此操作：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <version>2.4.0</version>
    <configuration>
        <mainClass>cn.tuyucheng.taketoday.boot.Application</mainClass>
        <layout>ZIP</layout>
    </configuration>
</plugin>
```

在极少数情况下，我们可能需要做的另一件事是指示Maven解压缩一些依赖项：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <requiresUnpack>
            <dependency>
                <groupId>org.jruby</groupId>
                <artifactId>jruby-complete</artifactId>
            </dependency>
        </requiresUnpack>
    </configuration>
</plugin>
```

## 5. 总结

在本文中，我们研究了使用由spring-boot构建的“胖”jar的无服务器部署。