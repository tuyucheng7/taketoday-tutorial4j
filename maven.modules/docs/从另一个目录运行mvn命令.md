## 1. 概述

在这个快速教程中，我们将了解如何从pom.xml之外的任何目录运行[mvn](https://www.baeldung.com/maven)命令。

## 2. 来自另一个目录的mvn

如果我们从不包含pom.xml文件的目录运行任何mvn子命令，则该命令将失败：

```bash
$ mvn clean compile
The goal you specified requires a project to execute but there is no POM in this directory.
Please verify you invokedMavenfrom the correct directory
```

如上所示，Maven抱怨当前目录中缺少pom.xml文件。

**要解决此问题并从另一个目录调用Maven**[阶段或目标](https://www.baeldung.com/maven-goals-phases)**，我们可以使用-f或–file选项**：

```bash
$ mvn -f taketoday-tutorial4j/ clean compile
```

由于指定目录(taketoday-tutorial4j)中有一个pom.xml文件，因此该命令将实际编译代码。

基本上，此选项强制使用带有pom.xml的备用POM文件或目录，所以我们也可以使用完整的文件路径：

```bash
$ mvn -f taketoday-tutorial4j/pom.xml clean compile
```

## 3. 总结

在这个简短的教程中，我们看到了如何从另一个目录运行mvn命令。