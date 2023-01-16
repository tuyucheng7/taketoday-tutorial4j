## 1. 概述

[MicroStream](https://github.com/microstream-one/microstream)是为 JVM 构建的对象图持久性引擎。我们可以用它来存储Java对象图并在内存中恢复它们。使用自定义序列化概念，MicroStream 使我们能够存储任何Java类型并加载整个对象图、部分子图或单个对象。

在本教程中，我们将首先了解开发此类对象图持久性引擎的原因。然后，我们将这种方法与传统关系数据库和标准Java序列化进行比较。我们将看到如何创建对象图存储并使用它来保存、加载和删除数据。

最后，我们将使用本地系统内存和纯JavaAPI 查询数据。

## 2. 对象-关系不匹配

让我们先看看开发 MicroStream 的动机。在大多数Java项目中，我们需要某种数据库存储。

然而，Java 和流行的关系型或 NoSQL 数据库使用不同的数据结构。因此，我们需要一种方法将Java对象映射到数据库结构，反之亦然。此映射需要编程工作和执行时间。例如，我们可以使用映射到表的实体和与关系数据库中的字段匹配的属性。

要从数据库加载数据，我们通常需要执行复杂的多表 SQL 查询。尽管[Hibernate](https://www.baeldung.com/tag/hibernate/)等对象关系映射框架帮助开发人员弥合了这一差距，但在许多复杂场景中，框架生成的查询并未得到充分优化。

MicroStream 希望通过对内存操作使用与持久化数据相同的结构来解决这种数据结构不匹配问题。

## 3.使用JVM作为存储

MicroStream 使用 JVM 作为其存储，以纯Java实现快速的内存数据处理。它没有使用与 JVM 分离的存储，而是为我们提供了一个现代的本地数据存储库。

### 3.1. 数据库管理系统

MicroStream 是一个持久性引擎，而不是数据库管理系统(DBMS)。一些标准的 DBMS 功能，如用户管理、连接管理和会话处理，已被设计遗漏。

相反，MicroStream 专注于为我们提供一种简单的方法来存储和恢复我们的应用程序数据。

### 3.2. Java序列化

MicroStream 使用自定义序列化概念，旨在为遗留 DBMS 提供性能更高的替代方案。

由于以下几个限制，它不使用Java的内置序列化：

-   只能存储和恢复完整的对象图
-   在存储大小和性能方面效率低下
-   更改类结构时所需的手动操作

另一方面，自定义 MicroStream 数据存储可以：

-   部分和按需保留、加载或更新对象图
-   有效处理存储大小和性能
-   通过内部启发式或用户定义的映射策略映射数据来处理不断变化的类结构

## 4. 对象图存储

MicroStream 试图通过只使用一种数据结构和一种数据模型来简化软件开发。

对象实例存储为字节流，它们之间的引用使用唯一标识符进行映射。因此，可以以简单快捷的方式存储对象图。此外，它可以全部或部分装载。

### 4.1. 依赖关系

在我们开始使用 MicroStream 存储对象图之前，我们需要添加两个[依赖](https://search.maven.org/search?q=microstream-storage-embedded)项：

```xml
<dependency>
    <groupId>one.microstream</groupId>
    <artifactId>microstream-storage-embedded</artifactId>
    <version>07.00.00-MS-GA</version>
</dependency>
<dependency>
    <groupId>one.microstream</groupId>
    <artifactId>microstream-storage-embedded-configuration</artifactId>
    <version>07.00.00-MS-GA</version>
</dependency>
```

### 4.2. 根实例

使用对象图存储时，我们的整个数据库都是从根实例开始访问的。此实例称为 MicroStream 持久保存的对象图的根对象。

对象图实例(包括根实例)可以是任何Java类型。因此，一个简单的[String](https://www.baeldung.com/java-string)实例可以注册为实体图的根：

```java
EmbeddedStorageManager storageManager = EmbeddedStorage.start(directory);
storageManager.setRoot("baeldung-demo");
storageManager.storeRoot();
```

然而，由于这个根实例不包含子实例，我们的String实例包含了我们的整个数据库。因此，我们通常需要 定义一个特定于我们的应用程序的自定义根类型：

```java
public class RootInstance {

    private final String name;
    private final List<Book> books;

    public RootInstance(String name) {
        this.name = name;
        books = new ArrayList<>();
    }

    // standard getters, hashcode and equals
}
```

我们可以通过调用setRoot()和storeRoot()方法，以类似的方式使用自定义类型注册根实例：

```java
EmbeddedStorageManager storageManager = EmbeddedStorage.start(directory);
storageManager.setRoot(new RootInstance("baeldung-demo"));
storageManager.storeRoot();
```

现在，我们的书籍列表是空的，但是有了我们的自定义根目录，我们以后就可以存储书籍实例了：

```java
RootInstance rootInstance = (RootInstance) storageManager.root();
assertThat(rootInstance.getName()).isEqualTo("baeldung-demo");
assertThat(rootInstance.getBooks()).isEmpty()
storageManager.shutdown();
```

我们应该注意，一旦我们的应用程序完成对存储的使用，建议为了安全起见调用shutdown()方法。

## 5. 操纵数据

让我们检查一下如何通过 MicroStream 持久化的对象图来执行标准的 CRUD 操作。

### 5.1. 收藏

存储新实例时，我们需要确保在正确的对象上调用store()方法。正确的对象是新创建实例的所有者——在我们的例子中是一个列表：

```java
RootInstance rootInstance = (RootInstance) storageManager.root();
List<Book> books = rootInstance.getBooks();
books.addAll(booksToStore);
storageManager.store(books);
assertThat(books).hasSize(2);
```

存储一个新对象也会存储该对象引用的所有实例。此外，执行store() 方法可确保数据已物理写入底层存储层，通常是文件系统。

### 5.2. 预加载

使用 MicroStream 加载数据可以通过两种方式完成，eager 和 lazy。预先加载是从存储的对象图中加载对象的默认方式。如果在启动时发现一个已经存在的数据库，那么存储对象图的所有对象都被加载到内存中。

启动EmbeddedStorageManager实例后，我们可以通过获取对象图的根实例来加载数据：

```java
EmbeddedStorageManager storageManager = EmbeddedStorage.start(directory);
if (storageManager.root() == null) {
    RootInstance rootInstance = new RootInstance("baeldung-demo");
    storageManager.setRoot(rootInstance);
    storageManager.storeRoot();
} else {
    RootInstance rootInstance = (RootInstance) storageManager.root();
    // Use existing root loaded from storage
}
```

根实例的空值表示底层存储中不存在数据库。

### 5.3. 延迟加载

当我们处理大量数据时，一开始就将所有数据直接加载到内存中可能不是一个可行的选择。因此，MicroStream 也通过将实例包装到Lazy字段中来支持延迟加载。

Lazy是一个简单的包装类，类似于 JDK 的[WeakReference](https://www.baeldung.com/java-weak-reference)。它的实例在内部包含一个标识符和对实际实例的引用：

```java
private final Lazy<List<Book>> books;
```

可以使用Reference()方法实例化包装在Lazy中的新ArrayList ：

```java
books = Lazy.Reference(new ArrayList<>());
```

与WeakReference一样，要获取实际实例，我们需要调用一个简单的get()方法：

```java
public List<Book> getBooks() {
    return Lazy.get(books);
}
```

get()方法调用将在需要时重新加载数据，开发人员无需处理任何低级数据库标识符。

### 5.4. 删除

使用 MicroStream 删除数据不需要执行显式删除操作。相反，我们只需要清除对象图中对象的任何引用 并存储这些更改：

```java
List<Book> books = rootInstance.getBooks();
books.remove(1);
storageManager.store(books);
```

我们应该注意，删除的数据不会立即从存储中删除。相反，后台内务处理进程会运行预定的清理。

## 6.查询系统

与标准 DBMS 不同，MicroStream 查询不直接在存储上操作，而是在我们本地系统内存中的数据上运行。因此，无需学习任何特殊的查询语言，因为所有操作都是使用纯Java完成的。

一种常见的方法可能是将[Streams](https://www.baeldung.com/java-streams)与标准 Java[集合](https://www.baeldung.com/java-collections)一起使用：

```java
List<Book> booksFrom1998 = rootInstance.getBooks().stream()
    .filter(book -> book.getYear() == 1998)
    .collect(Collectors.toList());
```

鉴于查询在内存中运行，内存消耗可能很高，但查询可以快速运行。

数据存储和加载过程可以通过使用多线程并行化。目前，水平扩展是不可能的，但 MicroStream 宣布他们目前正在开发一种对象图方法。这将在未来实现跨多个节点的集群和数据。

## 七. 总结

在本文中，我们探讨了MicroStream，它是 JVM 的对象图持久化引擎。我们了解了 MicroStream 如何通过为内存操作和数据持久化应用相同的结构来解决对象-关系数据结构不匹配问题。

我们探讨了如何使用自定义根实例创建对象图。此外，我们还了解了如何使用预加载和延迟加载方法来存储、删除和加载数据。最后，我们查看了 MicroStream 的查询系统，该系统基于纯Java的内存操作。