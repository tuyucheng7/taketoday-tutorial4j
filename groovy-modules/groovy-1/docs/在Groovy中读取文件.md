## 一、概述

在本快速教程中，我们将探讨在[Groovy](https://www.baeldung.com/groovy-language)中读取文件的不同方式。

Groovy 提供了处理文件的便捷方式。我们将专注于 File类，它有一些用于读取文件的辅助方法。

让我们在以下部分中一一探讨。

## 2.逐行读取文件

有许多[Groovy IO 方法](http://docs.groovy-lang.org/2.4.7/html/gapi/org/codehaus/groovy/runtime/IOGroovyMethods.html)，如readLine 和 eachLine可用于逐行读取文件。

### 2.1. 使用File.withReader

让我们从File.withReader[方法](http://docs.groovy-lang.org/next/html/groovy-jdk/java/io/File.html)开始。 它在幕后创建了一个新的BufferedReader ，我们可以使用它来使用readLine方法读取内容。

例如，让我们逐行读取文件并打印每一行。我们还将返回行数：

```groovy
int readFileLineByLine(String filePath) {
    File file = new File(filePath)
    def line, noOfLines = 0;
    file.withReader { reader ->
        while ((line = reader.readLine()) != null) {
            println "${line}"
            noOfLines++
        }
    }
    return noOfLines
}
```

让我们创建一个包含以下内容的纯文本文件fileContent.txt并将其用于测试：

```plaintext
Line 1 : Hello World!!!
Line 2 : This is a file content.
Line 3 : String content
```

让我们测试一下我们的实用方法：

```groovy
def 'Should return number of lines in File given filePath' () {
    given:
        def filePath = "src/main/resources/fileContent.txt"
    when:
        def noOfLines = readFile.readFileLineByLine(filePath)
    then:
        noOfLines
        noOfLines instanceof Integer
        assert noOfLines, 3
}

```

withReader方法也可以与字符集参数一起使用，如 UTF-8 或 ASCII 来读取编码文件。让我们看一个例子：

```groovy
new File("src/main/resources/utf8Content.html").withReader('UTF-8') { reader ->
def line
    while ((line = reader.readLine()) != null) { 
        println "${line}"
    }
}
```

### 2.2. 使用File.eachLine

我们还可以使用 eachLine方法：

```groovy
new File("src/main/resources/fileContent.txt").eachLine { line ->
    println line
}

```

### 2.3. 将File.newInputStream 与InputStream.eachLine结合使用

让我们看看如何使用带有eachLine的InputStream来读取文件：

```groovy
def is = new File("src/main/resources/fileContent.txt").newInputStream()
is.eachLine { 
    println it
}
is.close()
```

当我们使用newInputStream方法时，我们必须处理关闭InputStream的问题。

如果我们改用 withInputStream方法，它将为我们关闭InputStream：

```groovy
new File("src/main/resources/fileContent.txt").withInputStream { stream ->
    stream.eachLine { line ->
        println line
    }
}
```

## 3. 将文件读入列表

有时我们需要将文件的内容读入行列表。

### 3.1. 使用File.readLines

为此，我们可以使用readLines方法将文件读入字符串列表。

让我们快速看一下读取文件内容并返回行列表的示例：

```groovy
List<String> readFileInList(String filePath) {
    File file = new File(filePath)
    def lines = file.readLines()
    return lines
}
```

让我们使用fileContent.txt编写一个快速测试 ：

```groovy
def 'Should return File Content in list of lines given filePath' () {
    given:
        def filePath = "src/main/resources/fileContent.txt"
    when:
        def lines = readFile.readFileInList(filePath)
    then:
        lines
        lines instanceof List<String>
        assert lines.size(), 3
}
```

### 3.2. 使用File.collect

我们还可以使用 collect API将文件内容读入 字符串列表：

```groovy
def list = new File("src/main/resources/fileContent.txt").collect {it}

```

### 3.3. 使用as运算符

我们甚至可以利用as 运算符将文件内容读入String数组：

```groovy
def array = new File("src/main/resources/fileContent.txt") as String[]
```

## 4. 将文件读入单个字符串

### 4.1. 使用File.text

只需使用File类的text属性， 我们就可以将整个文件读取到一个 String中。

让我们看一个例子：

```groovy
String readFileString(String filePath) {
    File file = new File(filePath)
    String fileContent = file.text
    return fileContent
}

```

让我们用单元测试来验证一下：

```groovy
def 'Should return file content in string given filePath' () {
    given:
        def filePath = "src/main/resources/fileContent.txt"
    when:
        def fileContent = readFile.readFileString(filePath)
    then:
        fileContent
        fileContent instanceof String
        fileContent.contains("""Line 1 : Hello World!!!
Line 2 : This is a file content.
Line 3 : String content""")
}
```

### 4.2. 使用File.getText

如果我们使用 getTest(charset)方法，我们可以通过提供字符集参数(如 UTF-8 或 ASCII )将编码文件的内容读入 字符串：

```groovy
String readFileStringWithCharset(String filePath) {
    File file = new File(filePath)
    String utf8Content = file.getText("UTF-8")
    return utf8Content
}
```

 让我们为单元测试创建一个名为utf8Content.html的包含 UTF-8 内容的 HTML 文件：

```html
ᚠᛇᚻ᛫ᛒᛦᚦ᛫ᚠᚱᚩᚠᚢᚱ᛫ᚠᛁᚱᚪ᛫ᚷᛖᚻᚹᛦᛚᚳᚢᛗ
ᛋᚳᛖᚪᛚ᛫ᚦᛖᚪᚻ᛫ᛗᚪᚾᚾᚪ᛫ᚷᛖᚻᚹᛦᛚᚳ᛫ᛗᛁᚳᛚᚢᚾ᛫ᚻᛦᛏ᛫ᛞᚫᛚᚪᚾ
ᚷᛁᚠ᛫ᚻᛖ᛫ᚹᛁᛚᛖ᛫ᚠᚩᚱ᛫ᛞᚱᛁᚻᛏᚾᛖ᛫ᛞᚩᛗᛖᛋ᛫ᚻᛚᛇᛏᚪᚾ

```

让我们看看单元测试：

```groovy
def 'Should return UTF-8 encoded file content in string given filePath' () {
    given:
        def filePath = "src/main/resources/utf8Content.html"
    when:
        def encodedContent = readFile.readFileStringWithCharset(filePath)
    then:
        encodedContent
        encodedContent instanceof String
}
```

## 5. 使用File.bytes读取二进制文件

Groovy 使读取非文本或二进制文件变得容易。通过使用 bytes属性，我们可以将文件的内容作为字节数组获取：

```groovy
byte[] readBinaryFile(String filePath) {
    File file = new File(filePath)
    byte[] binaryContent = file.bytes
    return binaryContent
}
```

我们将使用 带有以下内容的 png 图像文件sample.png进行单元测试：

[![样本](https://www.baeldung.com/wp-content/uploads/2019/02/sample.png)](https://www.baeldung.com/wp-content/uploads/2019/02/sample.png)

 

让我们看看单元测试：

```groovy
def 'Should return binary file content in byte array given filePath' () {
    given:
        def filePath = "src/main/resources/sample.png"
    when:
        def binaryContent = readFile.readBinaryFile(filePath)
    then:
        binaryContent
        binaryContent instanceof byte[]
        binaryContent.length == 329
}
```

## 六，总结

在这个快速教程中，我们看到了使用 File类的各种方法以及BufferedReader和InputStream在Groovy中读取文件的不同方法。

这些实现和单元测试用例的完整源代码可以在 [GitHub](https://github.com/eugenp/tutorials/tree/master/core-groovy-modules/core-groovy)项目中找到。