## 1. 概述

通常，在测试大量使用I/O操作的组件时，我们的测试可能会遇到一些问题，例如性能不佳、平台依赖性和意外状态。

在本教程中，**我们介绍如何使用内存文件系统[Jimfs](https://github.com/google/jimfs)来缓解这些问题**。

## 2. Jimfs简介

**Jimfs是一个内存文件系统，它实现了Java NIO API**，并支持它的几乎所有功能。这特别有用，因为这意味着我们可以mock一个虚拟的内存文件系统，并使用现有的java.nio层与之交互。

正如我们将要看到的，使用模拟文件系统而不是真正的文件系统可能会有益于：

-   避免依赖当前运行测试的文件系统
-   确保文件系统在每次测试运行时都以预期状态组装
-   帮助加快我们的测试

**由于文件系统差异很大，使用Jimfs还可以方便地使用来自不同操作系统的文件系统进行测试**。

## 3. Maven依赖

首先，我们添加示例所需的项目依赖项：

```xml
<dependency>
    <groupId>com.google.jimfs</groupId>
    <artifactId>jimfs</artifactId>
    <version>1.1</version>
</dependency>
```

[jimfs](https://search.maven.org/search?q=g:com.google.jimfs AND a:jimfs)依赖项包含我们使用mock文件系统所需的一切。此外，我们使用JUnit 5编写测试。

## 4. 一个简单的文件Repository

首先，我们定义一个实现一些标准CRUD操作的FileRepository类：

```java
public class FileRepository {

	void create(final Path path, final String fileName) {
		final Path filePath = path.resolve(fileName);
		try {
			Files.createFile(filePath);
		} catch (final IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	String read(final Path path) {
		try {
			return new String(Files.readAllBytes(path));
		} catch (final IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	String update(final Path path, final String newContent) {
		try {
			Files.write(path, newContent.getBytes());
			return newContent;
		} catch (final IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	void delete(final Path path) {
		try {
			Files.deleteIfExists(path);
		} catch (final IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}
}
```

正如我们所见，每个方法都使用标准的java.nio类。

## 4.1 创建文件

在本节中，我们将编写一个测试来测试我们Repository中的create方法：

```java
@Test
@DisplayName("Should create a file on a file system")
void givenUnixSystem_whenCreatingFile_thenCreatedInPath() {
    FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
    String fileName = "newFile.txt";
    Path pathToStore = fileSystem.getPath("");

    fileRepository.create(pathToStore, fileName);

    assertTrue(Files.exists(pathToStore.resolve(fileName)));
}
```

在这个例子中，我们使用了静态方法Jimfs.newFileSystem()来创建一个新的内存文件系统。我们传递了一个配置对象Configuration.unix()，它为 Unix 文件系统创建了一个不可变的配置。这包括重要的操作系统特定信息，例如路径分隔符和有关符号链接的信息。

现在我们已经创建了一个文件，我们可以检查该文件是否在基于 Unix 的系统上成功创建。

## 4.2 读取文件

接下来，我们将测试读取文件内容的方法：

```java
@Test
@DisplayName("Should read the content of the file")
void givenOSXSystem_whenReadingFile_thenContentIsReturned() throws Exception {
    FileSystem fileSystem = Jimfs.newFileSystem(Configuration.osX());
    Path resourceFilePath = fileSystem.getPath(RESOURCE_FILE_NAME);
    Files.copy(getResourceFilePath(), resourceFilePath);

    String content = fileRepository.read(resourceFilePath);

    assertEquals(FILE_CONTENT, content);
}
```

这一次，我们检查了是否可以通过简单地使用不同类型的配置 - Jimfs.newFileSystem(Configuration.osX())在 macOS（以前的 OSX）系统上读取文件的内容。

## 4.3. 更新文件

我们也可以使用 Jimfs 来测试更新文件内容的方法：

```java
@Test
@DisplayName("Should update the content of the file")
void givenWindowsSystem_whenUpdatingFile_thenContentHasChanged() throws Exception {
    FileSystem fileSystem = Jimfs.newFileSystem(Configuration.windows());
    Path resourceFilePath = fileSystem.getPath(RESOURCE_FILE_NAME);
    Files.copy(getResourceFilePath(), resourceFilePath);
    String newContent = "I'm updating you.";

    String content = fileRepository.update(resourceFilePath, newContent);

    assertEquals(newContent, content);
    assertEquals(newContent, fileRepository.read(resourceFilePath));
}
```

同样，这次我们使用Jimfs.newFileSystem(Configuration.windows())检查了该方法在基于 Windows 的系统上的行为方式。

## 4.4. 删除文件

结束测试我们的 CRUD 操作，让我们测试删除文件的方法：

```java
@Test
@DisplayName("Should delete file")
void givenCurrentSystem_whenDeletingFile_thenFileHasBeenDeleted() throws Exception {
    FileSystem fileSystem = Jimfs.newFileSystem();
    Path resourceFilePath = fileSystem.getPath(RESOURCE_FILE_NAME);
    Files.copy(getResourceFilePath(), resourceFilePath);

    fileRepository.delete(resourceFilePath);

    assertFalse(Files.exists(resourceFilePath));
}
```

与前面的示例不同，我们使用Jimfs.newFileSystem()没有指定文件系统配置。在这种情况下，Jimfs 将使用适合当前操作系统的默认配置创建一个新的内存文件系统。

## 5. 移动文件

在本节中，我们将学习如何测试将文件从一个目录移动到另一个目录的方法。

首先，让我们使用标准的java.nio.file.File类来实现move方法：

```java
void move(Path origin, Path destination) {
    try {
        Files.createDirectories(destination);
        Files.move(origin, destination, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException ex) {
        throw new UncheckedIOException(ex);
    }
}
```

我们将使用参数化测试来确保此方法适用于多个不同的文件系统：

```java
private static Stream<Arguments> provideFileSystem() {
    return Stream.of(
            Arguments.of(Jimfs.newFileSystem(Configuration.unix())),
            Arguments.of(Jimfs.newFileSystem(Configuration.windows())),
            Arguments.of(Jimfs.newFileSystem(Configuration.osX())));
}

@ParameterizedTest
@DisplayName("Should move file to new destination")
@MethodSource("provideFileSystem")
void givenEachSystem_whenMovingFile_thenMovedToNewPath(FileSystem fileSystem) throws Exception {
    Path origin = fileSystem.getPath(RESOURCE_FILE_NAME);
    Files.copy(getResourceFilePath(), origin);
    Path destination = fileSystem.getPath("newDirectory", RESOURCE_FILE_NAME);

    fileManipulation.move(origin, destination);

    assertFalse(Files.exists(origin));
    assertTrue(Files.exists(destination));
}
```

正如我们所看到的，我们还能够使用 Jimfs 来测试我们可以从单个单元测试中移动各种不同文件系统上的文件。

## 6. 操作系统相关测试

为了演示使用 Jimfs 的另一个好处，让我们创建一个FilePathReader类。该类负责返回真实的系统路径，当然，这取决于操作系统：

```java
class FilePathReader {

    String getSystemPath(Path path) {
        try {
            return path
              .toRealPath()
              .toString();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
```

现在，让我们为这个类添加一个测试：

```java
class FilePathReaderUnitTest {

    private static String DIRECTORY_NAME = "baeldung";

    private FilePathReader filePathReader = new FilePathReader();

    @Test
    @DisplayName("Should get path on windows")
    void givenWindowsSystem_shouldGetPath_thenReturnWindowsPath() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.windows());
        Path path = getPathToFile(fileSystem);

        String stringPath = filePathReader.getSystemPath(path);

        assertEquals("C:work" + DIRECTORY_NAME, stringPath);
    }

    @Test
    @DisplayName("Should get path on unix")
    void givenUnixSystem_shouldGetPath_thenReturnUnixPath() throws Exception {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path path = getPathToFile(fileSystem);

        String stringPath = filePathReader.getSystemPath(path);

        assertEquals("/work/" + DIRECTORY_NAME, stringPath);
    }

    private Path getPathToFile(FileSystem fileSystem) throws Exception {
        Path path = fileSystem.getPath(DIRECTORY_NAME);
        Files.createDirectory(path);

        return path;
    }
}
```

正如我们所看到的，Windows 的输出与 Unix 的输出不同，正如我们所期望的那样。此外，我们不必使用两个不同的文件系统来运行这些测试——Jimfs 自动为我们mock了它。

值得一提的是，Jimfs 不支持 返回java.io.File的toFile()方法。这是Path类中唯一不受支持的方法。因此，对InputStream而不是File进行操作可能会更好。

## 7. 结论

在本文中，我们学习了如何使用内存文件系统 Jimfs 从我们的单元测试中mock文件系统交互。

首先，我们首先定义一个包含多个 CRUD 操作的简单文件Repository。然后我们看到了如何使用不同的文件系统类型测试每种方法的示例。最后，我们看到了如何使用 Jimfs 测试依赖于操作系统的文件系统处理的示例。