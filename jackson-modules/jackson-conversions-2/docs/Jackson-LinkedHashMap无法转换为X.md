## 1. 概述

[Jackson](https://www.baeldung.com/jackson)是一个广泛使用的Java库，可以让我们方便地序列化/反序列化 JSON 或 XML。

有时， 当我们尝试将 JSON 或 XML 反序列化为对象集合时，可能会遇到java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to X。

在本教程中，我们将讨论为什么会出现此异常以及如何解决该问题。

## 2. 理解问题

让我们创建一个简单的Java应用程序来重现此异常，以了解何时会发生异常。

### 2.1. 创建 POJO 类

让我们从一个简单的 POJO 类开始：

```java
public class Book {
    private Integer bookId;
    private String title;
    private String author;
    //getters, setters, constructors, equals and hashcode omitted
}
```

假设我们有一个books.json文件，该文件由一个包含三本书的 JSON 数组组成：

```json
[ {
    "bookId" : 1,
    "title" : "A Song of Ice and Fire",
    "author" : "George R. R. Martin"
}, {
    "bookId" : 2,
    "title" : "The Hitchhiker's Guide to the Galaxy",
    "author" : "Douglas Adams"
}, {
    "bookId" : 3,
    "title" : "Hackers And Painters",
    "author" : "Paul Graham"
} ]

```

接下来，我们将看看当我们尝试将 JSON 示例反序列化为List<Book>时会发生什么。

### 2.2. 将 JSON 反序列化为List<Book>

让我们看看是否可以通过将此 JSON 文件反序列化为List<Book>对象并从中读取元素来重现类转换问题：

```java
@Test
void givenJsonString_whenDeserializingToList_thenThrowingClassCastException() 
  throws JsonProcessingException {
    String jsonString = readFile("/to-java-collection/books.json");
    List<Book> bookList = objectMapper.readValue(jsonString, ArrayList.class);
    assertThat(bookList).size().isEqualTo(3);
    assertThatExceptionOfType(ClassCastException.class)
      .isThrownBy(() -> bookList.get(0).getBookId())
      .withMessageMatching(".java.util.LinkedHashMap cannot be cast to .tocollection.cn.tuyucheng.taketoday.jackson.Book.");
}
```

我们已经使用[AssertJ](https://www.baeldung.com/assertj-exception-assertion)库来验证当我们调用bookList.get(0).getBookId()时是否抛出了预期的异常，并且它的消息是否与我们的问题陈述中记录的消息相匹配。

测试通过，意味着我们已经成功重现了问题。

### 2.3. 为什么抛出异常

如果我们仔细查看异常消息class java.util.LinkedHashMap cannot be cast to class … Book，可能会出现几个问题。

我们已经声明了类型为 List<Book>的变量bookList，但为什么 Jackson 试图将LinkedHashMap类型转换为我们的Book类？此外，LinkedHashMap从何而来？

首先，我们确实声明了类型为List<Book>的bookList。但是，当我们调用[objectMapper.readValue()](https://fasterxml.github.io/jackson-databind/javadoc/2.7/com/fasterxml/jackson/databind/ObjectMapper.html#readValue(java.lang.String, java.lang.Class))方法时，我们将ArrayList.class作为Class对象传递 。因此，Jackson 会将 JSON 内容反序列化为一个[ArrayList](https://www.baeldung.com/java-arraylist)对象，但它并不知道ArrayList对象中应该包含什么类型的元素。

其次，当 Jackson 尝试反序列化 JSON 中的对象但未提供目标类型信息时，它将使用默认类型：[LinkedHashMap](https://www.baeldung.com/java-linked-hashmap)。换句话说，在反序列化之后，我们将得到一个ArrayList<LinkedHashMap>对象。在 Map中，键是属性的名称，例如“bookId”、“title” 等。

这些值是相应属性的值：

[![图书地图](https://www.baeldung.com/wp-content/uploads/2021/01/booksMap.png)](https://www.baeldung.com/wp-content/uploads/2021/01/booksMap.png)

现在我们了解了问题的原因，让我们讨论如何解决它。

## 3.将TypeReference传递给objectMapper.readValue()

要解决这个问题，我们需要以某种方式让 Jackson 知道元素的类型。但是，编译器不允许我们执行类似objectMapper.readValue(jsonString, ArrayList<Book>.class)的操作。

相反，我们可以将[TypeReference](https://fasterxml.github.io/jackson-core/javadoc/2.2.0/com/fasterxml/jackson/core/type/TypeReference.html)对象传递给[objectMapper.readValue(String content, TypeReference valueTypeRef)](https://fasterxml.github.io/jackson-databind/javadoc/2.7/com/fasterxml/jackson/databind/ObjectMapper.html#readValue(java.lang.String, com.fasterxml.jackson.core.type.TypeReference))方法。

在这种情况下，我们只需要传递new TypeReference<List<Book>>() {}作为第二个参数：

```java
@Test
void givenJsonString_whenDeserializingWithTypeReference_thenGetExpectedList() 
  throws JsonProcessingException {
    String jsonString = readFile("/to-java-collection/books.json");
    List<Book> bookList = objectMapper.readValue(jsonString, new TypeReference<List<Book>>() {});
    assertThat(bookList.get(0)).isInstanceOf(Book.class);
    assertThat(bookList).isEqualTo(expectedBookList);
}

```

如果我们运行测试，它就会通过。因此，传递一个TypeReference对象解决了我们的问题。

## 4.将JavaType传递给objectMapper.readValue()

在上一节中，我们谈到了将Class对象或TypeReference对象作为第二个参数来调用objectMapper.readValue()方法。

objectMapper.readValue ()方法仍然接受[JavaType](https://fasterxml.github.io/jackson-databind/javadoc/2.7/com/fasterxml/jackson/databind/JavaType.html)对象作为第二个参数。JavaType 是类型标记类的基类。反序列化器将使用它，以便反序列化器在反序列化期间知道目标类型是什么。 

我们可以通过 TypeFactory 实例构造一个[JavaType](https://fasterxml.github.io/jackson-databind/javadoc/2.7/com/fasterxml/jackson/databind/type/TypeFactory.html)对象 ，我们可以从objectMapper.getTypeFactory()中检索TypeFactory对象。

让我们回到书中的例子。在这个例子中，我们想要的目标类型是ArrayList<Book>。

因此，我们可以构造一个具有此需求的CollectionType ：

```java
objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, Book.class);
```

现在让我们编写一个单元测试，看看将JavaType传递给 readValue() 方法是否可以解决我们的问题：

```java
@Test
void givenJsonString_whenDeserializingWithJavaType_thenGetExpectedList() 
  throws JsonProcessingException {
    String jsonString = readFile("/to-java-collection/books.json");
    CollectionType listType = 
      objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, Book.class);
    List<Book> bookList = objectMapper.readValue(jsonString, listType);
    assertThat(bookList.get(0)).isInstanceOf(Book.class);
    assertThat(bookList).isEqualTo(expectedBookList);
}

```

如果我们运行它，测试就会通过。所以，这个问题也可以这样解决。

## 5. 使用JsonNode对象和objectMapper.convertValue()方法

我们已经看到将TypeReference或JavaType对象传递给objectMapper.readValue()方法的解决方案。

或者，我们可以[使用 Jackson 中的树模型节点，](https://www.baeldung.com/jackson-json-node-tree-model) 然后通过调用[objectMapper.convertValue()](https://fasterxml.github.io/jackson-databind/javadoc/2.7/com/fasterxml/jackson/databind/ObjectMapper.html#convertValue(java.lang.Object, com.fasterxml.jackson.core.type.TypeReference))方法将[JsonNode](https://fasterxml.github.io/jackson-databind/javadoc/2.7/com/fasterxml/jackson/databind/JsonNode.html)对象转换为所需的类型。

同样，我们可以将TypeReference或JavaType的对象传递给objectMapper.convertValue()方法。

让我们看看每种方法的实际应用。

首先，让我们使用TypeReference 对象和objectMapper.convertValue()方法创建一个测试方法：

```java
@Test
void givenJsonString_whenDeserializingWithConvertValueAndTypeReference_thenGetExpectedList() 
  throws JsonProcessingException {
    String jsonString = readFile("/to-java-collection/books.json");
    JsonNode jsonNode = objectMapper.readTree(jsonString);
    List<Book> bookList = objectMapper.convertValue(jsonNode, new TypeReference<List<Book>>() {});
    assertThat(bookList.get(0)).isInstanceOf(Book.class);
    assertThat(bookList).isEqualTo(expectedBookList);
}

```

现在让我们看看当我们将JavaType对象传递给objectMapper.convertValue()方法时会发生什么：

```java
@Test
void givenJsonString_whenDeserializingWithConvertValueAndJavaType_thenGetExpectedList() 
  throws JsonProcessingException {
    String jsonString = readFile("/to-java-collection/books.json");
    JsonNode jsonNode = objectMapper.readTree(jsonString);
    List<Book> bookList = objectMapper.convertValue(jsonNode, 
      objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, Book.class));
    assertThat(bookList.get(0)).isInstanceOf(Book.class);
    assertThat(bookList).isEqualTo(expectedBookList);
}

```

如果我们运行这两个测试，它们都会通过。因此，使用objectMapper.convertValue()方法是解决问题的另一种方法。

## 6. 创建通用反序列化方法

到目前为止，我们已经解决了在将 JSON 数组反序列化为Java集合时如何解决类转换问题。在现实世界中，我们可能希望创建一个通用方法来处理不同的元素类型。

现在对我们来说这不会是一项艰巨的工作。

我们可以 在调用objectMapper.readValue()方法时传递一个JavaType对象：

```java
public static <T> List<T> jsonArrayToList(String json, Class<T> elementClass) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    CollectionType listType = 
      objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, elementClass);
    return objectMapper.readValue(json, listType);
}

```

接下来，让我们创建一个单元测试方法来验证它是否按我们预期的那样工作：

```java
@Test
void givenJsonString_whenCalljsonArrayToList_thenGetExpectedList() throws IOException {
    String jsonString = readFile("/to-java-collection/books.json");
    List<Book> bookList = JsonToCollectionUtil.jsonArrayToList(jsonString, Book.class);
    assertThat(bookList.get(0)).isInstanceOf(Book.class);
    assertThat(bookList).isEqualTo(expectedBookList);
}

```

如果我们运行它，测试将通过。

为什么不使用TypeReference方法来构建泛型方法，因为它看起来更紧凑？

让我们创建一个通用实用程序方法并将相应的TypeReference对象传递给objectMapper.readValue()方法：

```java
public static <T> List<T> jsonArrayToList(String json, Class<T> elementClass) throws IOException {
    return new ObjectMapper().readValue(json, new TypeReference<List<T>>() {});
}

```

该方法看起来很简单。

如果我们再次运行测试方法，我们将得到以下结果：

```bash
java.lang.ClassCastException: class java.util.LinkedHashMap cannot be cast to class com.baeldung...Book ...
```

糟糕，出现异常！

我们已经将一个TypeReference对象传递给 readValue()方法，我们之前已经看到这种方式可以解决类转换问题。那么，为什么我们在这种情况下会看到相同的异常？

这是因为我们的方法是通用的。类型参数T无法在运行时具体化，即使我们传递带有类型参数T的TypeReference实例也是如此。

## 7. 使用 Jackson 进行 XML 反序列化

除了 JSON 序列化/反序列化，Jackson 库也可用于[序列化/反序列化 XML](https://www.baeldung.com/jackson-xml-serialization-and-deserialization)。

让我们举一个简单的例子来检查将 XML 反序列化为Java集合时是否会发生同样的问题。

首先，让我们创建一个 XML 文件books.xml：

```xml
<ArrayList>
    <item>
        <bookId>1</bookId>
        <title>A Song of Ice and Fire</title>
        <author>George R. R. Martin</author>
    </item>
    <item>
        <bookId>2</bookId>
        <title>The Hitchhiker's Guide to the Galaxy</title>
        <author>Douglas Adams</author>
    </item>
    <item>
        <bookId>3</bookId>
        <title>Hackers And Painters</title>
        <author>Paul Graham</author>
    </item>
</ArrayList>

```

接下来，就像我们对 JSON 文件所做的那样，我们创建另一个单元测试方法来验证是否会抛出类转换异常：

```java
@Test
void givenXml_whenDeserializingToList_thenThrowingClassCastException() 
  throws JsonProcessingException {
    String xml = readFile("/to-java-collection/books.xml");
    List<Book> bookList = xmlMapper.readValue(xml, ArrayList.class);
    assertThat(bookList).size().isEqualTo(3);
    assertThatExceptionOfType(ClassCastException.class)
      .isThrownBy(() -> bookList.get(0).getBookId())
      .withMessageMatching(".java.util.LinkedHashMap cannot be cast to .tocollection.cn.tuyucheng.taketoday.jackson.Book.");
}

```

如果我们试一试，我们的测试就会通过。也就是说，XML反序列化也会出现同样的问题。

但是，如果我们知道如何解决 JSON 反序列化，那么在 XML 反序列化中修复它就非常简单。

由于[XmlMapper](https://fasterxml.github.io/jackson-dataformat-xml/javadoc/2.7/com/fasterxml/jackson/dataformat/xml/XmlMapper.html)是ObjectMapper的子类，我们针对 JSON 反序列化提出的所有解决方案也适用于 XML 反序列化。

例如，我们可以将一个TypeReference对象传递给xmlMapper.readValue()方法来解决问题：

```java
@Test
void givenXml_whenDeserializingWithTypeReference_thenGetExpectedList() 
  throws JsonProcessingException {
    String xml = readFile("/to-java-collection/books.xml");
    List<Book> bookList = xmlMapper.readValue(xml, new TypeReference<List<Book>>() {});
    assertThat(bookList.get(0)).isInstanceOf(Book.class);
    assertThat(bookList).isEqualTo(expectedBookList);
}

```

## 八. 总结

在本文中，我们讨论了为什么 在使用 Jackson 反序列化 JSON 或 XML 时会出现 java.util.LinkedHashMap cannot be cast to X异常。

之后，我们通过示例介绍了解决问题的不同方法。