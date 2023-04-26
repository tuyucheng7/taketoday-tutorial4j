## 一、简介

虽然跳过测试通常不是一个好主意，但在某些情况下它可能会有用，它可以节省我们一些时间。例如，假设我们正在开发一项新功能，我们希望在中间构建中看到结果。在这种情况下，我们可能会暂时跳过测试以减少编译和运行它们的开销。**毫无疑问，忽视测试会导致许多严重的问题。**

在这个简短的教程中，我们将学习如何在使用[Gradle](https://www.baeldung.com/gradle)构建工具时跳过测试。

## 2. 使用命令行标志

首先，让我们创建一个我们想跳过的简单测试：

```java
@Test
void skippableTest() {
    Assertions.assertTrue(true);
}复制
```

当我们运行*构建*命令时：

```bash
gradle build复制
```

我们将看到正在运行的任务：

```bash
> ...
> Task :compileTestJava
> Task :processTestResources NO-SOURCE
> Task :testClasses
> Task :test
> ...复制
```

**要跳过 Gradle 构建中的任何任务，我们可以使用\*-x\*或\*–exclude-task\*选项。**在这种情况下，**我们将使用“ \*-x test\* ”从构建中跳过测试。**

要查看它的实际效果，让我们使用*-x选项运行**构建*命令：

```bash
gradle build -x test复制
```

我们将看到正在运行的任务：

```bash
> Task :compileJava NO-SOURCE 
> Task :processResources NO-SOURCE 
> Task :classes UP-TO-DATE 
> Task :jar 
> Task :assemble 
> Task :check 
> Task :build复制
```

因此，测试源代码不会被编译，因此也不会被执行。

## 3. 使用 Gradle 构建脚本

我们有更多选项可以使用 Gradle 构建脚本跳过测试。例如，**我们可以根据某些条件跳过测试，或者仅在特定环境中使用\*onlyIf()\*方法**。*如果此方法返回false ，*将跳过测试。

让我们跳过基于检查项目属性的测试：

```bash
test.onlyIf { !project.hasProperty('someProperty') }复制
```

现在我们将运行*构建*命令，并将*someProperty*传递给 Gradle：

```bash
gradle build -PsomeProperty复制
```

因此，Gradle 跳过运行测试：

```bash
> ...
> Task :compileTestJava 
> Task :processTestResources NO-SOURCE 
> Task :testClasses 
> Task :test SKIPPED 
> Task :check UP-TO-DATE 
> ...复制
```

此外，我们可以使用*build.gradle*文件中的*exclude属性***根据包名或类名排除测试**：

```bash
test {
    exclude 'org/boo/**'
    exclude '**/Bar.class'
}复制
```

**我们还可以跳过基于正则表达式模式的测试。**例如，我们可以跳过所有类名以单词“ *Integration* ”结尾的测试：

```bash
test {
    exclude '**/**Integration'
}复制
```

## 4。结论

在本文中，我们学习了如何在使用 Gradle 构建工具时跳过测试。我们还介绍了可以在命令行上使用的所有相关选项，以及可以在 Gradle 构建脚本中使用的选项。