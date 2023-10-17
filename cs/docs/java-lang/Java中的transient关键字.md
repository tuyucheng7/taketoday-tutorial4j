## 1. 概述

在本文中，我们将首先了解transient关键字，然后我们将通过示例了解它的行为。

## 2.transient的用法

让我们先了解序列化，然后再转到瞬态，因为它在序列化上下文中使用。

[序列化](https://www.baeldung.com/java-serialization)是将一个对象转换为字节流的过程，而反序列化则与之相反。

当我们将任何变量标记为瞬态时，该变量就不会被序列化。由于瞬态字段不存在于对象的序列化形式中，反序列化过程将在从序列化形式创建对象时使用此类字段的默认值。

transient关键字在一些情况下很有用：

-   我们可以将它用于派生字段
-   对于不代表对象状态的字段很有用
-   我们将它用于任何不可序列化的引用

## 3.例子

要查看它的实际效果，让我们首先创建一个Book类，我们希望对其对象进行序列化：

```java
public class Book implements Serializable {
    private static final long serialVersionUID = -2936687026040726549L;
    private String bookName;
    private transient String description;
    private transient int copies;
    
    // getters and setters
}
```

在这里，我们将描述和副本标记为临时字段。

创建类后，我们将创建此类的对象：

```java
Book book = new Book();
book.setBookName("Java Reference");
book.setDescription("will not be saved");
book.setCopies(25);
```

现在，我们将对象序列化到一个文件中：

```java
public static void serialize(Book book) throws Exception {
    FileOutputStream file = new FileOutputStream(fileName);
    ObjectOutputStream out = new ObjectOutputStream(file);
    out.writeObject(book);
    out.close();
    file.close();
}
```

现在让我们从文件中反序列化对象：

```java
public static Book deserialize() throws Exception {
    FileInputStream file = new FileInputStream(fileName);
    ObjectInputStream in = new ObjectInputStream(file);
    Book book = (Book) in.readObject();
    in.close();
    file.close();
    return book;
}
```

最后，我们将验证book对象的值：

```java
assertEquals("Java Reference", book.getBookName());
assertNull(book.getDescription());
assertEquals(0, book.getCopies());
```

在这里我们看到bookName已被正确持久化。另一方面，副本字段的值为0 并且描述为空——它们各自数据类型的默认值——而不是原始值。

## 4.最终行为

现在，让我们看一个将transient与final关键字一起使用的情况。为此，首先，我们将在Book类中添加一个最终的瞬态元素，然后创建一个空的Book对象：

```java
public class Book implements Serializable {
    // existing fields    
    
    private final transient String bookCategory = "Fiction";

    // getters and setters
}
Book book = new Book();
```

final 修饰符没有区别——因为该字段是transient的，没有为该字段保存任何值。在反序列化期间，新的Book对象获得Book类中定义的默认值Fiction，但该值并非来自序列化数据：

```java
assertEquals("Fiction", book.getBookCategory());
```

## 5.总结

在本文中，我们了解了transient关键字的用法及其在序列化和反序列化中的行为。