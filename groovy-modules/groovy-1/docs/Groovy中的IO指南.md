## 一、简介

虽然在[Groovy](https://www.baeldung.com/groovy-language)中我们可以像在Java中一样使用 I/O，但Groovy 使用许多辅助方法扩展了Java的 I/O 功能。

在本教程中，我们将了解通过Groovy的文件扩展方法读取和写入文件、遍历文件系统以及序列化数据和对象。

在适用的情况下，我们将链接到相关的Java文章，以便与Java等价物进行轻松比较。

## 2.读取文件

Groovy以eachLine方法、获取BufferedReader和InputStream的方法以及使用一行代码获取所有文件数据的方式添加了方便的[文件读取功能。](https://www.baeldung.com/groovy-file-read)

[Java 7 和Java8 对读取Java文件](https://www.baeldung.com/reading-file-in-java)有类似的支持。

### 2.1. 用每一行阅读

在处理文本文件时，我们往往需要读取每一行并进行处理。Groovy使用eachLine方法为java.io.File提供了一个方便的扩展：

```groovy
def lines = []

new File('src/main/resources/ioInput.txt').eachLine { line ->
    lines.add(line)
}
```

提供给eachLine的闭包还有一个有用的可选行号。让我们使用行号仅从文件中获取特定行：

```groovy
def lineNoRange = 2..4
def lines = []

new File('src/main/resources/ioInput.txt').eachLine { line, lineNo ->
    if (lineNoRange.contains(lineNo)) {
        lines.add(line)
    }
}
```

默认情况下，行号从一开始。我们可以通过将值作为第一个参数传递给eachLine方法来提供用作第一行号的值。

让我们从零开始我们的行号：

```groovy
new File('src/main/resources/ioInput.txt').eachLine(0, { line, lineNo ->
    if (lineNoRange.contains(lineNo)) {
        lines.add(line)
    }
})
```

如果在eachLine 中抛出异常，Groovy确保文件资源被关闭。很像Java中的try-with-resources或try-finally。

### 2.2. 与读者一起阅读

我们还可以轻松地从GroovyFile对象中获取BufferedReader 。我们可以使用withReader获取文件对象的BufferedReader并将其传递给闭包：

```groovy
def actualCount = 0
new File('src/main/resources/ioInput.txt').withReader { reader ->
    while(reader.readLine()) {
        actualCount++
    }
}
```

与eachLine一样， withReader方法会在抛出异常时自动关闭资源。

有时，我们可能希望BufferedReader对象可用。例如，我们可能计划调用一个以 one 作为参数的方法。我们可以为此使用newReader方法：

```groovy
def outputPath = 'src/main/resources/ioOut.txt'
def reader = new File('src/main/resources/ioInput.txt').newReader()
new File(outputPath).append(reader)
reader.close()
```

与我们目前所见的其他方法不同，当我们以这种方式获取Buffered Reader时，我们负责关闭BufferedReader资源。

### 2.3. 使用InputStream读取

与withReader和newReader类似，Groovy 也提供了方便地使用InputStream的方法。尽管我们可以使用InputStream读取文本并且Groovy甚至为其添加了功能，但InputStream最常用于二进制数据。

让我们使用 withInputStream将InputStream传递给闭包并读入字节：

```groovy
byte[] data = []
new File("src/main/resources/binaryExample.jpg").withInputStream { stream ->
    data = stream.getBytes()
}
```

如果我们需要InputStream对象，我们可以使用newInputStream获得一个：

```groovy
def outputPath = 'src/main/resources/binaryOut.jpg'
def is = new File('src/main/resources/binaryExample.jpg').newInputStream()
new File(outputPath).append(is)
is.close()
```

与BufferedReader一样，我们需要在使用 newInputStream 时自己关闭InputStream资源，但在使用withInputStream时则不需要。

### 2.4. 阅读其他方式

让我们通过查看Groovy在一条语句中获取所有文件数据的几种方法来结束阅读主题。

如果我们希望我们的文件行在List中，我们可以使用collect和它传递给闭包的迭代器：

```groovy
def actualList = new File('src/main/resources/ioInput.txt').collect {it}
```

要将文件的行放入Strings数组中，我们可以使用as String[]：

```groovy
def actualArray = new File('src/main/resources/ioInput.txt') as String[]
```

对于短文件，我们可以使用text获取String中的全部内容：

```groovy
def actualString = new File('src/main/resources/ioInput.txt').text
```

在处理二进制文件时，有bytes方法：

```groovy
def contents = new File('src/main/resources/binaryExample.jpg').bytes
```

## 3.写文件

在开始[写入文件](https://www.baeldung.com/java-write-to-file)之前，让我们设置要输出的文本：

```groovy
def outputLines = [
    'Line one of output example',
    'Line two of output example',
    'Line three of output example'
]
```

### 3.1. 与作家一起写作

与读取文件一样，我们也可以轻松地从File对象中获取BufferedWriter。

让我们使用withWriter获取BufferedWriter并将其传递给闭包：

```groovy
def outputFileName = 'src/main/resources/ioOutput.txt'
new File(outputFileName).withWriter { writer ->
    outputLines.each { line ->
        writer.writeLine line
    }
}
```

如果发生异常，使用withReader将关闭资源。

Groovy 也有一个获取BufferedWriter对象的方法。让我们使用newWriter得到一个BufferedWriter：

```groovy
def outputFileName = 'src/main/resources/ioOutput.txt'
def writer = new File(outputFileName).newWriter()
outputLines.forEach {line ->
    writer.writeLine line
}
writer.flush()
writer.close()
```

当我们使用newWriter时，我们负责刷新和关闭BufferedWriter对象。

### 3.2. 使用输出流写入

如果我们正在写出二进制数据，我们可以使用withOutputStream或newOutputStream获得一个OutputStream。

让我们使用withOutputStream将一些字节写入文件：

```groovy
byte[] outBytes = [44, 88, 22]
new File(outputFileName).withOutputStream { stream ->
    stream.write(outBytes)
}
```

让我们用newOutputStream获取一个OutputStream对象并用它来写入一些字节：

```groovy
byte[] outBytes = [44, 88, 22]
def os = new File(outputFileName).newOutputStream()
os.write(outBytes)
os.close()
```

与InputStream、BufferedReader和BufferedWriter类似，我们负责在使用newOutputStream时自己关闭OutputStream。

### 3.3. 使用 << 运算符编写

由于将文本写入文件非常普遍，<<运算符直接提供了此功能。

让我们使用<<运算符来编写一些简单的文本行：

```groovy
def ln = System.getProperty('line.separator')
def outputFileName = 'src/main/resources/ioOutput.txt'
new File(outputFileName) << "Line one of output example${ln}" + 
  "Line two of output example${ln}Line three of output example"
```

### 3.4. 用字节写二进制数据

我们在文章前面看到，我们可以简单地通过访问字节字段来从二进制文件中获取所有字节。

让我们以同样的方式写入二进制数据：

```groovy
def outputFileName = 'src/main/resources/ioBinaryOutput.bin'
def outputFile = new File(outputFileName)
byte[] outBytes = [44, 88, 22]
outputFile.bytes = outBytes
```

## 4.遍历文件树

Groovy 还为我们提供了处理文件树的简单方法。在本节中，我们将使用eachFile、eachDir及其变体和遍历方法来做到这一点。

### 4.1. 用eachFile列出文件

让我们使用eachFile列出目录中的所有文件和目录：

```groovy
new File('src/main/resources').eachFile { file ->
    println file.name
}
```

使用文件时的另一个常见场景是需要根据文件名过滤文件。让我们使用eachFileMatch和正则表达式仅列出以“io”开头并以“.txt”结尾的文件：

```groovy
new File('src/main/resources').eachFileMatch(~/io..txt/) { file ->
    println file.name
}
```

eachFile和eachFileMatch方法仅列出顶级目录的内容。Groovy 还允许我们通过将FileType传递给方法来限制eachFile方法返回的内容。选项是ANY、FILES和DIRECTORIES。

让我们使用eachFileRecurse递归地列出所有文件，并为其提供FILES的文件类型：

```groovy
new File('src/main').eachFileRecurse(FileType.FILES) { file ->
    println "$file.parent $file.name"
}
```

如果我们为它们提供文件路径而不是目录路径，则eachFile方法会抛出IllegalArgumentException 。

Groovy 还提供了 用于仅处理目录的eachDir方法。我们可以使用eachDir及其变体来完成与使用带有DIRECTORIES的FileType的eachFile相同的事情。

让我们使用eachFileRecurse递归地列出目录：

```groovy
new File('src/main').eachFileRecurse(FileType.DIRECTORIES) { file ->
    println "$file.parent $file.name"
}
```

现在，让我们对eachDirRecurse做同样的事情：

```groovy
new File('src/main').eachDirRecurse { dir ->
    println "$dir.parent $dir.name"
}
```

### 4.2. 使用遍历列出文件

对于[更复杂的目录遍历](https://www.baeldung.com/java-nio2-file-visitor)用例，我们可以使用遍历方法。它的功能类似于eachFileRecurse，但提供了返回FileVisitResult对象以控制处理的能力。

让我们在我们的src/main目录上使用遍历并跳过处理groovy目录下的树：

```groovy
new File('src/main').traverse { file ->
   if (file.directory && file.name == 'groovy') {
        FileVisitResult.SKIP_SUBTREE
    } else {
        println "$file.parent - $file.name"
    }
}
```

## 5. 使用数据和对象

### 5.1. 序列化基元

在Java中，我们可以使用DataInputStream和DataOutputStream来[序列化原始数据字段](https://www.baeldung.com/java-serialization)。Groovy 在这里也添加了有用的扩展。

让我们设置一些原始数据：

```groovy
String message = 'This is a serialized string'
int length = message.length()
boolean valid = true
```

现在，让我们使用withDataOutputStream将我们的数据序列化到一个文件中：

```groovy
new File('src/main/resources/ioData.txt').withDataOutputStream { out ->
    out.writeUTF(message)
    out.writeInt(length)
    out.writeBoolean(valid)
}
```

并使用withDataInputStream读回它：

```groovy
String loadedMessage = ""
int loadedLength
boolean loadedValid

new File('src/main/resources/ioData.txt').withDataInputStream { is ->
    loadedMessage = is.readUTF()
    loadedLength = is.readInt()
    loadedValid = is.readBoolean()
}
```

与其他with方法类似，withDataOutputStream和withDataInputStream将流传递给闭包并确保它正确关闭。

### 5.2. 序列化对象

Groovy 还建立在Java的ObjectInputStream和ObjectOutputStream之上，使我们能够轻松地序列化实现了Serializable的对象。

让我们首先定义一个实现Serializable的类：

```groovy
class Task implements Serializable {
    String description
    Date startDate
    Date dueDate
    int status
}
```

现在让我们创建一个可以序列化到文件的Task实例：

```groovy
Task task = new Task(description:'Take out the trash', startDate:new Date(), status:0)
```

有了我们的Task对象，让我们使用withObjectOutputStream将它序列化为一个文件：

```groovy
new File('src/main/resources/ioSerializedObject.txt').withObjectOutputStream { out ->
    out.writeObject(task)
}
```

最后，让我们回顾使用withObjectInputStream时的任务：

```groovy
Task taskRead

new File('src/main/resources/ioSerializedObject.txt').withObjectInputStream { is ->
    taskRead = is.readObject()
}
```

我们使用的方法withObjectOutputStream和withObjectInputStream将流传递给闭包并适当地处理关闭资源，就像其他with方法一样。

## 六，总结

在本文中，我们探讨了Groovy添加到现有Java文件 I/O 类的功能。我们使用此功能来读写文件、处理目录结构以及序列化数据和对象。

我们只涉及了几个辅助方法，因此值得深入研究[Groovy 的文档](http://docs.groovy-lang.org/docs/next/html/groovy-jdk/java/io/package-summary.html)，看看它还为Java的 I/O 功能添加了什么。