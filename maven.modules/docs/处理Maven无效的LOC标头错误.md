## 1. 概述

有时，当我们本地[Maven](https://www.baeldung.com/maven)仓库中的jar损坏时，我们会看到错误：Invalid LOC Header。

在本教程中，我们将了解它何时发生以及如何处理**甚至有时防止它**。

## 2. “Invalid LOC Header”什么时候出现？

Maven将项目的依赖项下载到我们文件系统上称为[本地仓库](https://www.baeldung.com/maven-local-repository)的已知位置，Maven下载的每个工件还附带其SHA1和MD5校验和文件：

<img src="../assets/img_7.png">

这些校验和的目的是确保相关工件的完整性，由于**网络和文件系统可能会出现故障**，就像其他任何事情一样，有时工件会被损坏，从而使工件内容与签名不匹配。

在这些情况下，Maven构建会抛出“invalid LOC header”错误。

**解决方案是从仓库中删除损坏的jar**，让我们看看几种方法。

## 3. 删除本地仓库

该错误的快速修复是**删除整个Maven本地仓库并重新构建项目**：

```bash
rm -rf ${LOCAL_REPOSITORY}
```

这将清除本地缓存并重新下载所有项目依赖项，**但效率不高**。

请注意，默认本地仓库位于${user.home}/.m2/repository，除非我们在[settings.xml](https://maven.apache.org/guides/mini/guide-configuring-maven.html)中的<localRepository\>标签中指定它。我们也可以通过命令找到本地仓库：mvn help:evaluate -Dexpression=settings.localRepository

## 4. 找到损坏的jar

另一种解决方案是**识别特定的损坏jar并将其从本地仓库中删除**。

当我们使用Maven输出堆栈跟踪命令时，它会在处理失败时包含损坏的jar详细信息。

我们可以通过在构建命令中添加-X来启用调试级别的日志记录：

```bash
mvn -X package
```

生成的堆栈跟踪将在日志末尾指示损坏的jar，识别出损坏的jar后，我们就可以在本地仓库中找到并删除它。现在在构建时，Maven将重新尝试下载jar。

此外，我们可以使用zip -T命令测试存档的完整性：

```bash
find ${LOCAL_REPOSITORY} -name "*.jar" | xargs -L 1 zip -T | grep error
```

## 5. 验证校验和

前面提到的两种解决方案只会强制Maven重新下载jar，当然，这个问题可能会在以后的下载中再次出现，**我们可以通过配置Maven在从远程仓库下载工件时验证校验和来防止这种情况发生**。

我们可以在Maven命令中添加–strict-checksums或-C选项，如果计算的校验和与校验和文件中的值不匹配，这将导致Maven构建失败。

有两个选项，如果校验和不匹配则构建失败：

```bash
-C,--strict-checksums
```

或警告哪个是默认选项：

```bash
-c,--lax-checksums
```

如今，Maven在将工件上传到中央仓库时需要[签名文件](https://central.sonatype.org/pages/requirements.html#sign-files-with-gpgpgp)，但是**中央仓库中可能存在没有签名文件的工件，尤其是历史文件**，这就是默认选项为warn的原因。

对于更永久的解决方案，我们可以在Maven的[settings.xml](https://maven.apache.org/ref/current/maven-settings/settings.html)文件中配置checksumPolicy，此属性指定工件校验和验证失败时的行为。为了避免将来出现问题，让我们编辑settings.xml文件以在校验和失败时下载失败：

```xml
<profiles>
    <profile>
        <repositories>
            <repository>
                <id>codehausSnapshots</id>
                <name>Codehaus Snapshots</name>
                <releases>
                    <enabled>false</enabled>
                    <updatePolicy>always</updatePolicy>
                    <checksumPolicy>fail</checksumPolicy>
                </releases>
            </repository>
        </repositories>
    </profile>
</profiles>
```

当然，我们需要为每个配置的仓库执行此操作。

## 6. 总结

在这篇简短的文章中，我们了解了何时会出现”invalid LOC header“错误以及如何处理它的选项。