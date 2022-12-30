## 1. 简介

在Kotlin中创建目录与在[Java](https://www.baeldung.com/java-create-directory)中一样简单，在本教程中，我们将研究**创建单个目录或嵌套目录结构的不同方法**。

## 2. 创建单一目录

首先我们看一下java.nio.file.Files暴露的创建./createDirectory目录的方法：

```kotlin
Files.createDirectory(Paths.get("./createDirectory"))
```

如果可以访问提供的路径(稍后会详细介绍)并且该目录尚不存在，则此简单方法会创建一个目录。

**如果不满足这些条件中的任何一个，则createDirectory()方法会抛出IOException**：

```kotlin
Files.createDirectory(Paths.get("./createDirectory_2/inner"))
```

在这里，由于createDirectory_2尚不存在，因此inner的创建失败，我们将在下一节中看看如何处理这个问题。

我们还有另一种创建单个目录的方法-File(...).mkdir():

```kotlin
val resultMkdir: Boolean = File("./mkdir").mkdir()
```

虽然Files.createDirectory()会抛出异常，**但mkdir()会返回一个简单的布尔值**；true结果表示文件创建成功，false结果表示文件创建失败。

这里有一点需要注意：由于缺少Files.createDirectory()提供的异常，**我们无法确定mkdir()创建目录失败的确切原因**，我们需要谨慎选择最适合我们需要的方法。

现在我们已经了解了创建单个目录的两种选择，让我们看看如何创建整个目录树。

## 3. 创建嵌套目录

我们再来看看java.nio.file.Files暴露的创建/createDirectories/inner目录结构的方法：

```kotlin
Files.createDirectories(Paths.get("./createDirectories/inner"))
```

如我们所见，**用法和创建单目录场景完全一样，唯一的区别是它会创建所有中间目录，如果目录已经存在，它也不会失败**。在上面的例子中，createDirectories是中间目录，inner是我们的目标目录。

因此，虽然对同一路径运行两次Files.createDirectory()会导致异常，但对于Files.createDirectories()，不会出现错误：

```kotlin
Files.createDirectories(Paths.get("./createDirectories/inner"))
println("Running Files.createDirectories 2nd time does nothing")
Files.createDirectories(Paths.get("./createDirectories/inner"))
```

与createDirectory()如何以mkdirs()的形式有一个方法类似，createDirectories()以mkdirs()的形式也有一个相应的方法：

```kotlin
val resultMkdirs: Boolean = File("./mkdirs/inner").mkdirs()
```

此方法还返回一个布尔值，表示成功或失败。因此，**我们再次无法确定未创建目录的确切原因**，如果我们需要这些信息，那么使用Files.createDirectories()可能是一个更好的方法。

## 4. 总结

在本文中，我们研究了两种创建单个目录或嵌套目录的方法，Files.createDirectory()和Files.createDirectories()有一些限制，但作为回报，可以抛出异常让我们更好地了解为什么我们可能无法创建目录。

另一方面，我们有mkdir()和mkdirs()方法，它们返回简单的布尔值而不是抛出异常，我们需要根据我们的要求选择我们合适的方法。