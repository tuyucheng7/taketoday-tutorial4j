## 1. 概述

java.io.File类具有三种方法——getPath ()、getAbsolutePath()和getCanonicalPath() ——来获取文件系统路径。

在本文中，我们将快速了解它们之间的区别，并讨论一个你可以选择使用其中一个的用例。

## 2. 方法定义和例子

让我们首先回顾一下这三种方法的定义，以及基于用户主目录中存在以下目录结构的示例：

```shell
|-- baeldung
    |-- baeldung.txt
    |-- foo
    |   |-- foo-one.txt
    |   -- foo-two.txt
    -- bar
        |-- bar-one.txt
        |-- bar-two.txt
        -- baz
            |-- baz-one.txt
            -- baz-two.txt
```

### 2.1. 获取路径()

简单地说，getPath()返回文件抽象路径名的字符串表示形式。这实际上是传递给File构造函数的路径名。

因此，如果File对象是使用相对路径创建的，则getPath()方法的返回值也将是相对路径。

如果我们从{user.home}/baeldung目录调用以下代码：

```java
File file = new File("foo/foo-one.txt");
String path = file.getPath();
```

路径变量将具有以下值：

```plaintext
foo/foo-one.txt  // on Unix systems
foofoo-one.txt  // on Windows systems
```

请注意，对于 Windows 系统，名称分隔符已从传递给构造函数的正斜杠 (/) 字符更改为反斜杠 () 字符。这是因为返回的String始终使用平台的默认名称分隔符。

### 2.2. 获取绝对路径()

getAbsolutePath ()方法在解析当前用户目录的路径后返回文件的路径名——这称为绝对路径名。因此，对于我们之前的示例，file.getAbsolutePath()将返回：

```plaintext
/home/username/baeldung/foo/foo-one.txt     // on Unix systems
C:Usersusernamebaeldungfoofoo-one.txt  // on Windows systems
```

此方法仅解析相对路径的当前目录。速记表示法(例如“ .”和“ ..”)没有进一步解析。因此，当我们从目录{user.home}/baeldung 执行以下代码时：

```java
File file = new File("bar/baz/../bar-one.txt");
String path = file.getAbsolutePath();
```

变量路径的值将是：

```plaintext
/home/username/baeldung/bar/baz/../bar-one.txt      // on Unix systems
C:Usersusernamebaeldungbarbaz..bar-one.txt   // on Windows systems
```

### 2.3. getCanonicalPath()

getCanonicalPath ()方法更进一步，解析绝对路径名以及简写或冗余名称，如“ . ” 和 “ .. “根据目录结构。它还解析Unix 系统上的符号链接，并将驱动器号转换为Windows 系统上的标准大小写。

因此对于前面的示例，getCanonicalPath()方法将返回：

```perl
/home/username/baeldung/bar/bar-one.txt     // on Unix systems
C:Usersusernamebaeldungbarbar-one.txt  // on Windows systems
```

让我们再举一个例子。给定当前目录${user.home}/baeldung和使用参数new File(“bar/baz/./baz-one.txt”)创建的文件对象， getCanonicalPath()的输出将是：

```bash
/home/username/baeldung/bar/baz/baz-one.txt     // on Unix systems
C:Usersusernamebaeldungbarbazbaz-one.txt  // on Windows Systems
```

值得一提的是，文件系统上的单个文件可以有无数个绝对路径，因为可以使用无数种速记表示法。但是，规范路径将始终是唯一的，因为所有此类表示都已解析。

与后两种方法不同，getCanonicalPath()可能会抛出IOException，因为它需要文件系统查询。

例如，在 Windows 系统上，如果我们创建一个具有非法字符之一的File对象，解析规范路径将抛出IOException：

```java
new File("").getCanonicalPath();
```

## 3.用例

假设我们正在编写一个方法，将File对象作为参数并将其[完全限定名称](https://en.wikipedia.org/wiki/Fully_qualified_name#Filenames_and_paths)保存到数据库中。我们不知道路径是相对路径还是包含简写。在这种情况下，我们可能想要使用getCanonicalPath()。

但是，由于getCanonicalPath()读取文件系统，因此会产生性能成本。如果我们确定没有冗余名称或符号链接并且驱动器字母大小写是标准化的(如果使用 Windows 操作系统)，那么我们应该更喜欢使用getAbsoultePath()。

## 4。总结

在本快速教程中，我们介绍了获取文件系统路径的三种File方法之间的差异。我们还展示了一个用例，其中一种方法可能优于另一种方法。