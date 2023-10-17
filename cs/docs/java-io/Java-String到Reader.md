## 1. 使用纯 Java

让我们从Java解决方案开始：

```java
@Test
public void givenUsingPlainJava_whenConvertingStringIntoReader_thenCorrect() throws IOException {
    String initialString = "With Plain Java";
    Reader targetReader = new StringReader(initialString);
    targetReader.close();
}
```

如你所见，StringReader开箱即用，可用于这种简单的转换。

## 2.用番石榴

接下来——番石榴解决方案：

```java
@Test
public void givenUsingGuava_whenConvertingStringIntoReader_thenCorrect() throws IOException {
    String initialString = "With Google Guava";
    Reader targetReader = CharSource.wrap(initialString).openStream();
    targetReader.close();
}
```

我们在这里使用通用的CharSource抽象，允许我们从中打开一个 Reader。

## 3.使用Apache Commons IO

最后 - 这是 Commons IO 解决方案，也使用现成的Reader实现：

```java
@Test
public void givenUsingCommonsIO_whenConvertingStringIntoReader_thenCorrect() throws IOException {
    String initialString = "With Apache Commons IO";
    Reader targetReader = new CharSequenceReader(initialString);
    targetReader.close();
}
```

所以我们有了它 - 3 种非常简单的方法来将 String 转换为Java中的 Reader。