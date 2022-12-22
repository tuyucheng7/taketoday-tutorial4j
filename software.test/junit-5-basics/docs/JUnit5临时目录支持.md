## 1. 概述

在测试时，我们经常需要访问一个临时文件。但是，自己管理这些文件的创建和删除可能很麻烦。

在本教程中，**我们将介绍JUnit 5如何通过提供TempDirectory Extension来处理这种情况**。

有关使用JUnit进行测试的深入介绍，请阅读[Guide to JUnit 5](JUnit5指南.md) 。

## 2. TempDirectory Extension

从5.4.2版本开始，JUnit 5提供了TempDirectory Extension。

正如我们稍后将看到的，**我们可以使用此Extension来为单个测试或测试类中的所有测试创建和清理临时目录**。

通常在使用Extension时，我们需要使用@ExtendWith注解从JUnit 5测试中注册它。
但这对于内置并默认注册的TempDirectory Extension是不必要的。

## 3. maven依赖

首先，让我们添加所需的项目依赖项。除了主要的JUnit 5依赖junit-jupiter-engine，我们还需要junit-jupiter-api：

```xml

<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.8.1</version>
    <scope>test</scope>
</dependency>
```

除此之外，我们还需要添加junit-jupiter-params依赖项：

```xml

<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-params</artifactId>
    <version>5.8.1</version>
    <scope>test</scope>
</dependency>
```

## 4. 使用@TempDir注解

为了使用TempDirectory Extension，**我们需要使用@TempDir注解**。我们只能将此注解与以下两种类型一起使用：

+ java.io.file.Path
+ java.io.File

事实上，如果我们尝试将它与不同的类型一起使用，则会抛出org.junit.jupiter.api.extension.ParameterResolutionException。

接下来，让我们探索几种使用此注解的不同方式。

### 4.1 @TempDir作为方法参数

让我们首先看看如何将带有@TempDir注解的参数注入到单个测试方法中：

```java
class TemporaryDirectoryUnitTest {

    @Test
    void givenTestMethodWithTempDirectoryPath_whenWriteToFile_thenContentIsCorrect(@TempDir Path tempDir) throws IOException {
        Path numbers = tempDir.resolve("numbers.txt");

        List<String> lines = Arrays.asList("1", "2", "3");
        Files.write(numbers, lines);

        assertAll(
                () -> assertTrue(Files.exists(numbers), "File should exist"),
                () -> assertLinesMatch(lines, Files.readAllLines(numbers))
        );
    }
}
```

如我们所见，我们的测试方法在临时目录tempDir中创建了一个名为numbers.txt的文件，并向其添加一些内容。

然后我们检查该文件是否存在以及内容是否与最初写入的内容相匹配。

### 4.2 @TempDir标注实例变量

在下面的例子中，我们使用@TempDir注解在测试类中标注一个字段：

```java
class TemporaryDirectoryUnitTest {

    @TempDir
    File anotherTempDir;

    @Test
    void givenFieldWithTempDirectoryFile_whenWriteToFile_thenContentIsCorrect() throws IOException {
        assertTrue(this.anotherTempDir.isDirectory(), "Should be a directory ");

        File letters = new File(anotherTempDir, "letters.txt");
        List<String> lines = Arrays.asList("x", "y", "z");

        Files.write(letters.toPath(), lines);

        assertAll(
                () -> assertTrue(Files.exists(letters.toPath()), "File should exist"),
                () -> assertLinesMatch(lines, Files.readAllLines(letters.toPath()))
        );
    }
}
```

这一次，我们使用java.io.File作为临时目录。同样，我们写入了一些内容并检查它们是否成功写入。

**如果我们在其他测试方法中再次使用这个anotherTempDir变量，那么每个测试都将使用它自己的临时目录**。

### 4.3 共享的临时目录

有时，我们可能希望在测试方法之间共享一个临时目录。

我们可以通过将字段声明为static来做到这一点：

```java

@TestMethodOrder(OrderAnnotation.class)
class SharedTemporaryDirectoryUnitTest {

    @TempDir
    static Path sharedTempDir;

    @Test
    @Order(1)
    void givenFieldWithSharedTempDirectoryPath_whenWriteToFile_thenContentIsCorrect() throws IOException {
        Path numbers = sharedTempDir.resolve("numbers.txt");

        List<String> lines = Arrays.asList("1", "2", "3");
        Files.write(numbers, lines);

        assertAll(
                () -> assertTrue(Files.exists(numbers), "File should exist"),
                () -> assertLinesMatch(lines, Files.readAllLines(numbers))
        );

        Files.createTempDirectory("bpb");
    }

    @Test
    @Order(2)
    void givenAlreadyWrittenToSharedFile_whenCheckContents_thenContentIsCorrect() throws IOException {
        Path numbers = sharedTempDir.resolve("numbers.txt");

        assertLinesMatch(Arrays.asList("1", "2", "3"), Files.readAllLines(numbers));
    }
}
```

**这里的关键点是我们使用一个静态字段sharedTempDir，我们在两个测试方法之间共享它**。

在第一个测试中，我们将一些内容写入名为numbers.txt的文件中。然后我们在下一个测试中检查文件和内容是否已经存在。

我们还通过@Order注解定义测试方法的顺序，以确保行为始终一致。

## 5. Gotchas

现在让我们回顾一下使用TempDirectory Extension时应该注意的一些细微之处。

### 5.1 创建

好奇的读者可能想知道这些临时文件实际上是在哪里创建的？

实际上，JUnit TemporaryDirectory类在内部使用了Files.createTempDirectory(String prefix)方法。同样，此方法使用默认的系统临时文件目录。

这通常在环境变量TEMP中指定：

```text
TEMP=C:\Users\27597\AppData\Local\Temp\
```

例如，在我的电脑上临时文件的位置为：

```text
C:\Users\27597\AppData\Local\Temp\junit5311844216996678616\numbers.txt
```

同时，如果无法创建临时目录，则会抛出ExtensionConfigurationException，或者之前所说的ParameterResolutionException。

### 5.2 删除

**当测试方法或类完成执行并且临时目录超出范围时，JUnit框架将尝试递归删除该目录中的所有文件和目录，最后是临时目录本身**。

如果在此删除阶段出现问题，将抛出IOException并且测试或测试类执行失败。

## 6. 总结

在本教程中，我们介绍了JUnit 5提供的TempDirectory Extension。

首先，我们介绍了这个Extension并了解了使用它需要哪些Maven依赖项。接下来，我们演示了几个如何在单元测试中使用该Extension的示例。

最后，有些值得需要注意的地方，即包括临时文件的创建位置以及删除过程中发生的情况。