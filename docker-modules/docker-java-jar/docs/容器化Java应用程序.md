## 1. 概述

在本文中，我们演示如何容器化一个基于Java可运行jar的应用程序。

## 2. 构建可运行的Jar

这里使用Maven构建一个可运行的jar包。

所以，我们的应用程序有一个简单的类HelloWorld.java，它有一个main方法：

```java
public class HelloWorld {
    
    public static void main(String[] args){
        System.out.println("Welcome to our application");
    }
}
```

然后使用maven-jar-plugin生成一个可运行的jar：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-jar-plugin</artifactId>
    <version>${maven-jar-plugin.version}</version>
    <configuration>
        <archive>
            <manifest>
                <mainClass>cn.tuyucheng.taketoday.HelloWorld</mainClass>
            </manifest>
        </archive>
    </configuration>
</plugin>
```

## 3. 编写Dockerfile

接下来在Dockerfile中编写容器化可运行jar包的步骤，Dockerfile位于构建上下文的根目录中：

```dockerfile
FROM openjdk:17
MAINTAINER tuyucheng.com
COPY target/docker-java-jar-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

在第一行中，我们从其官方仓库导入OpenJDK Java版本17镜像作为我们的基础镜像，**随后的命令将在此基础镜像上创建附加层**。

在第二行中，我们指定镜像的维护者，在本例中为tuyucheng.com，此步骤不会创建任何附加层。

在第三行中，我们通过将生成的docker-java-jar-1.0.0.jar从构建上下文的target文件夹复制到容器的根文件夹中来创建一个新层，名称为app.jar。

在最后一行，**我们使用为该镜像执行的统一命令指定主应用程序**。在这种情况下，我们告诉容器使用java -jar命令运行app.jar，此外，这一行也不会引入任何附加层。

## 4. 构建和测试镜像

现在我们有了Dockerfile，让我们使用Maven来构建和打包我们的可运行jar：

```shell
mvn package
```

然后，构建我们的Docker镜像：

```shell
docker image build -t docker-java-jar:latest .
```

在这里，我们**使用-t命令以<name\>:<tag\>格式指定名称和标签**，在本例中，docker-java-jar是我们的镜像名称，标签是latest。”.”表示Dockerfile所在的路径，在本例中，它只是当前目录。

注意：我们可以用相同的名称和不同的标签构建不同的Docker镜像。

最后，从命令行运行我们的Docker镜像：

```shell
docker run docker-java-jar:latest
```

上面的命令运行我们的Docker镜像，通过<name\>:<tag\>格式的名称和标签来识别它。

## 4. 总结

在本文中，我们介绍了对可运行的Java jar包进行Docker化所涉及的步骤。