## 一、简介

在本教程中，我们将比较*PrintStream*和*PrintWriter* Java 类。本文将帮助程序员为这些类中的每一个找到合适的用例。

在深入了解内容之前，我们建议您查看我们之前的文章，其中我们演示了如何使用*[PrintStream](https://www.baeldung.com/java-printstream-printf)*和*[PrintWriter](https://www.baeldung.com/java-write-to-file)*。

## *2. PrintStream*和*PrintWriter*的相似之处

因为*PrintStream*和*PrintWriter*共享它们的一些功能，所以程序员有时很难为这些类找到合适的用例。让我们首先确定它们的相似之处；然后，我们将看看差异。

### 2.1. 字符编码

无论何种系统，**[字符编码](https://www.baeldung.com/java-char-encoding)都允许程序操纵文本，以便跨平台对文本进行一致的解释**。

在 JDK 1.4 版本之后，*PrintStream*类在其构造函数中包含了一个字符编码参数。这允许*PrintStream*类在跨平台实现中编码/解码文本。另一方面，*PrintWriter*从一开始就一直具有字符编码功能。

我们可以参考官方Java代码来确认：

```java
public PrintStream(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
    this(requireNonNull(out, "Null output stream"), autoFlush, toCharset(encoding));
}复制
```

类似地，*PrintWriter*构造函数有一个*charset*参数来指定用于编码目的的*Charset ：*

```java
public PrintWriter(OutputStream out, boolean autoFlush, Charset charset) {
    this(new BufferedWriter(new OutputStreamWriter(out, charset)), autoFlush);

    // save print stream for error propagation
    if (out instanceof java.io.PrintStream) {
        psOut = (PrintStream) out;
    }
}复制
```

**如果没有为这些类中的任何一个提供字符编码，它们将使用默认的平台编码。**

### 2.2. 写入文件

要将文本写入文件，我们可以将*String*或*File*实例传递给相应的构造函数。另外，我们可以传递字符集进行字符编码。

例如，我们将引用具有单个*File*参数的构造函数。在这种情况下，字符编码将默认为平台：

```java
public PrintStream(File file) throws FileNotFoundException {
    this(false, new FileOutputStream(file));
}复制
```

同样，*PrintWriter*类有一个构造函数来指定要写入的文件：

```java
public PrintWriter(File file) throws FileNotFoundException {
    this(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file))), false);
}复制
```

正如我们所见，**这两个类都提供了写入文件的功能。但是，它们的实现因使用不同的流父类而有所不同。***我们将在本文的差异*部分深入探讨为什么会出现这种情况。

## 3. *PrintStream*和*PrintWriter的区别*

在上一节中，我们展示了*PrintStream*和*PrintWriter*共享一些可能适合我们案例的功能。尽管如此，**尽管我们可以对这些类做同样的事情，但它们的实现各不相同**，这使我们能够评估哪个类更适合。

现在，让我们看看*PrintStream*和*PrintWriter*之间的区别。

### 3.1. 数据处理

在上一节中，我们展示了这两个类如何写入文件。让我们看看它们的实现有何不同。

**对于\*PrintStream\*，它是\*OutputStream\*的子类，在 Java 中定义为字节流。换句话说，数据是逐字节处理的。另一方面，\*PrintWriter\***是一个字符流，它一次处理每个字符， 并使用 Unicode 自动转换我们指定的每个字符集。

我们将在两种不同的情况下展示这些实现中的每一个。

### 3.2. 处理非文本数据

**因为这两个类处理数据的方式不同，所以我们在处理非文本文件的时候可以具体区分一下。**在此示例中，我们将使用 png 文件读取数据，然后在将其内容写入每个类的另一个文件后查看差异：

```java
public class DataStream {

    public static void main (String[] args) throws IOException {
        FileInputStream inputStream = new FileInputStream("image.png");
        PrintStream printStream = new PrintStream("ps.png");

        int b;
        while ((b = inputStream.read()) != -1) {
            printStream.write(b);
        }
        printStream.close();

        FileReader reader = new FileReader("image.png");
        PrintWriter writer = new PrintWriter("pw.png");
    
        int c;
        while ((c = reader.read()) != -1) {
            writer.write(c);
        }
        writer.close();
    }
}复制
```

在这个例子中，我们使用*FileInputStream*和*FileReader*来读取图像的内容。然后，我们将数据写入不同的输出文件。

因此，*ps.png*和*pw.png*文件将根据流处理其内容的方式包含数据。**我们知道\*PrintStream\*通过一次读取一个字节来处理数据。因此，生成的文件包含与原始文件相同的原始数据**。

**与\*PrintStream\*类不同，\*PrintWriter\*将数据解释为字符。** **这会导致**系统无法理解其内容的损坏文件。或者，我们可以将 pw.png 的扩展名更改*为 pw.txt*，*并*检查*PrintWriter*如何尝试将图像的原始数据转换为难以辨认的符号。

### 3.3. 处理文本数据

**现在，让我们看一个示例，其中我们使用\*OutputStream （\* \*PrintStream\*的父类）来演示在写入文件时如何处理字符串**：

```java
public class PrintStreamWriter {
    public static void main (String[] args) throws IOException {
        OutputStream out = new FileOutputStream("TestFile.txt");
        out.write("foobar");
        out.flush();
    }
}复制
```

**上面的代码将无法编译，因为\*OutputStream\*不知道如何处理字符串。** **要成功写入文件，输入数据必须是原始字节序列。**以下更改将使我们的代码成功写入文件：

```java
out.write("foobar".getBytes());复制
```

**回到\*PrintStream\***，虽然这个类是*OutputStream*的子类，但**Java 在内部调用了\*getBytes()\*方法**。这允许*PrintStream在调用**print*方法时接受字符串*。*让我们看一个例子：

```java
public class PrintStreamWriter {
    public static void main (String[] args) throws IOException {
        PrintStream out = new PrintStream("TestFile.txt");
        out.print("Hello, world!");
        out.flush();
    }
}复制
```

现在，因为*PrintWriter*知道如何处理字符串，我们调用传递字符串输入的*print方法。*然而，在这种情况下，**Java 并没有将字符串转换为字节，而是在内部将流中的每个字符转换为其对应的 Unicode 编码**：

```java
public class PrintStreamWriter {
    public static void main (String[] args) throws IOException {
        PrintWriter out = new PrintWriter("TestFile.txt");
        out.print("Hello, world!");
        out.flush();
    }
}复制
```

基于这些类在内部处理文本数据**的方式，字符流类（如\*PrintWriter\* ）在对文本进行[I/O 操作](https://www.baeldung.com/java-io)时可以更好地处理这些内容**。此外，在本地字符集的编码过程中将数据翻译成 Unicode 使应用程序的[国际化](https://www.baeldung.com/java-8-localization)更简单。

### 3.4. 法拉盛

在我们前面的示例中，请注意我们必须如何显式调用*flush*方法*。*根据 Java 文档，**这个过程在这两个类之间的工作方式不同**。

对于*PrintStream，我们可以指定仅在写入字节数组、调用**println*方法或写入换行符时自动刷新。然而，*PrintWriter*也可以有自动刷新，但只有当我们调用*println、printf*或*格式*方法时。

这种区别很难证明，因为文档提到在上述情况下会发生刷新，但没有提到何时不会发生。因此，**我们可以演示自动刷新在这两个类中是如何工作的，但我们不能保证它会按预期运行**。

在此示例中，我们将启用自动刷新功能并在末尾写入一个带有换行符的字符串：

```java
public class AutoFlushExample {
    public static void main (String[] args) throws IOException {
        PrintStream printStream = new PrintStream(new FileOutputStream("autoFlushPrintStream.txt"), true);
        printStream.write("Hello, world!\n".getBytes());

        PrintWriter printWriter = new PrintWriter(new FileOutputStream("autoFlushPrintWriter.txt"), true);;
        printWriter.print("Hello, world!");
    }
}复制
```

**确保文件\*autoFlushPrintStream.txt\*将包含写入文件的内容，**因为我们启用了自动刷新功能。此外，我们使用包含换行符的字符串调用*write*方法以强制刷新。

但是，**我们希望看到\*autoFlushPrintWriter.txt\*文件为空**，尽管这不能保证。毕竟，刷新可能发生在程序执行期间。

如果我们想在使用*PrintWriter 时强制刷新，*代码必须满足我们上面提到的所有要求，或者我们可以添加一行代码来显式刷新 writer：

```java
printWriter.flush();复制
```

## 4。结论

在本文中，我们比较了两个数据流类*PrintStream*和*PrintWriter*。首先，我们研究了它们的相似性和使用本地字符集的能力。此外，我们还介绍了如何读取和写入外部文件的示例。**虽然我们可以用这两个类实现类似的事情，但在查看差异之后，我们证明了每个类在不同场景中的表现更好。**

例如，我们在写入所有类型的数据时受益于*PrintStream*，因为*PrintStream*处理原始字节。 另一方面，作为字符流的*PrintWriter在执行 I/O 操作时最适合文本。*此外，由于其 Unicode 内部格式，它有助于复杂的软件实现，例如国际化。最后，我们比较了刷新实现在两个类中的不同之处。