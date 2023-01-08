## 1. 概述

在本教程中，我们将讨论将文件读入ArrayList的不同方法。

在Java中有多种[读取文件](https://www.baeldung.com/java-read-file)的方法。一旦我们读取了一个文件，我们就可以对该文件的内容执行很多操作。

其中一些操作，如排序，可能需要将文件的全部内容处理到内存中。为了执行此类操作，我们可能需要将文件读取为数组或行 或词列表。

## 2.使用文件阅读器

在Java中读取文件的最基本方法是使用FileReader。根据定义， FileReader是一个方便的类，用于从文件中读取字符流。

有多个构造函数可用于初始化FileReader：

```java
FileReader f = new FileReader(String filepath);
FileReader f = new FileReader(File f);
FileReader f = new FileReader(FileDescriptor fd);
```

所有这些构造函数都假定默认字符编码和默认字节缓冲区大小是合适的。

但是，如果我们想提供自定义字符编码和字节缓冲区大小，我们可以使用 InputStreamReader或 FileInputStream。

在下面的代码中，我们将演示如何使用 FileReader将文件中的行读入ArrayList ：

```java
ArrayList<String> result = new ArrayList<>();

try (FileReader f = new FileReader(filename)) {
    StringBuffer sb = new StringBuffer();
    while (f.ready()) {
        char c = (char) f.read();
        if (c == 'n') {
            result.add(sb.toString());
            sb = new StringBuffer();
        } else {
            sb.append(c);
        }
    }
    if (sb.length() > 0) {
        result.add(sb.toString());
    }
}       
return result;
```

## 3.使用 缓冲阅读器

尽管 FileReader非常易于使用，但建议 在读取文件时始终使用BuffereReader包装它。

这是因为 BufferedReader 使用字符缓冲区同时从字符输入流中读取多个值 ，从而减少了 底层FileStream调用read()的次数。

BufferedReader的构造函数 将 Reader作为输入。此外，我们还可以在构造函数中提供缓冲区大小，但对于大多数用例来说，默认大小已经足够大了：

```java
BufferedReader br = new BufferedReader(new FileReader(filename));
BufferedReader br = new BufferedReader(new FileReader(filename), size);
```

BufferedReader 除了继承自 Reader 类的方法外，还提供了readLine() 方法，将整行读入一个String：

```java
ArrayList<String> result = new ArrayList<>();

try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
    while (br.ready()) {
        result.add(br.readLine());
    }
}

```

## 4.使用扫描仪

读取文件的另一种常见方式是通过Scanner。

Scanner是一个简单的文本扫描器，用于使用正则表达式解析原始类型和字符串。

读取文件时，使用File或FileReader对象初始化Scanner ：

```java
Scanner s = new Scanner(new File(filename));
Scanner s = new Scanner(new FileReader(filename));
```

与 BufferedReader 类似，Scanner提供了readLine()方法来读取整行。此外， 它还提供了一个hasNext()方法来指示是否有更多值可供读取：

```java
ArrayList<String> result = new ArrayList<>();

try (Scanner s = new Scanner(new FileReader(filename))) {
    while (s.hasNext()) {
        result.add(s.nextLine());
    }
    return result;
}
```

扫描器使用定界符将其输入分解为标记，默认定界符是空格。通过使用各种可用的next (nextInt、nextLong等)方法，可以将这些标记转换为不同类型的值：

```java
ArrayList<Integer> result = new ArrayList<>();

try (Scanner s = new Scanner(new FileReader(filename))) {
    while (s.hasNext()) {
        result.add(s.nextInt());
    }
    return result;
}
```

## 5. 使用Files.readAllLines

读取文件并将其所有行解析为ArrayList 的最简单方法可能是使用 Files类中可用的readAllLines() 方法：

```java
List<String> result = Files.readAllLines(Paths.get(filename));
```

这个方法也可以带一个字符集参数，按照特定的字符编码来读取：

```java
Charset charset = Charset.forName("ISO-8859-1");
List<String> result = Files.readAllLines(Paths.get(filename), charset);
```

## 六，总结

总而言之，我们讨论了一些将File的内容读入ArrayList的常用方法。此外，我们还介绍了各种方法的一些优点和缺点。

例如，我们可以使用 BufferedReader 来缓冲字符以提高效率。或者，我们可以使用 Scanner 来使用定界符读取原语。或者，我们可以简单地使用 Files.readAllLines()，而不用担心底层实现。