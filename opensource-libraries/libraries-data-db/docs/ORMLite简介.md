## 1. 概述

[ORMLite](http://ormlite.com/)是用于Java应用程序的轻量级 ORM 库。它为最常见的用例提供 ORM 工具的标准功能，而不会增加其他 ORM 框架的复杂性和开销。

它的主要特点是：

-   使用Java注解定义实体类
-   可扩展的 DAO类
-   用于创建复杂查询的QueryBuilder类
-   为创建和删除数据库表生成的类
-   交易支持
-   支持实体关系

在接下来的部分中，我们将了解如何设置库、定义实体类以及使用库对数据库执行操作。

## 2.Maven依赖

要开始使用 ORMLite，我们需要将[ormlite-jdbc](https://search.maven.org/classic/#search|ga|1|a%3A"ormlite-jdbc")依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.j256.ormlite</groupId>
    <artifactId>ormlite-jdbc</artifactId>
    <version>5.0</version>
</dependency>
```

默认情况下，这也会[引入 h2](https://search.maven.org/classic/#search|ga|1|a%3A"h2" AND g%3A"com.h2database")依赖项。在我们的示例中，我们将使用H2内存数据库，因此我们不需要其他 JDBC 驱动程序。

如果你想使用不同的数据库，你还需要相应的依赖。

## 3.定义实体类

要使用 ORMLite 设置我们的模型类以实现持久化，我们可以使用两个主要注解：

-   实体类的@DatabaseTable
-   @DatabaseField属性

让我们首先定义一个Library实体，它有一个name字段和一个libraryId字段，它也是一个主键：

```java
@DatabaseTable(tableName = "libraries")
public class Library {	
 
    @DatabaseField(generatedId = true)
    private long libraryId;

    @DatabaseField(canBeNull = false)
    private String name;

    public Library() {
    }
    
    // standard getters, setters
}
```

@DatabaseTable注解有一个可选的tableName属性，如果我们不想依赖默认类名，它指定表的名称。

对于我们希望作为数据库表中的列保留的每个字段，我们必须添加@DatabaseField注解。

将用作表主键的属性可以用id、generatedId或generatedSequence属性标记。在我们的示例中，我们选择generatedId=true属性，以便主键自动递增。

另外，请注意该类需要有一个至少具有包范围可见性的无参数构造函数。

我们可以用来配置字段的其他一些熟悉的属性是columnName、dataType、defaultValue、canBeNull、unique。

### 3.1. 使用JPA注解

除了 ORMLite 特定的注解之外，我们还可以使用JPA 样式的注解来定义我们的实体。

我们在使用JPA标准注解之前定义的Library实体的等价物是：

```java
@Entity
public class LibraryJPA {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long libraryId;

    @Column
    private String name;
    
    // standard getters, setters
}
```

虽然 ORMLite 可以识别这些注解，但我们仍然需要添加[javax.persistence-api](https://search.maven.org/classic/#search|ga|1|a%3A"javax.persistence-api")依赖才能使用它们。

受支持的JPA注解的完整列表是 @ Entity、 @ Id、 @Column、@GeneratedValue 、 @ OneToOne、 @ ManyToOne 、 @ JoinColumn、@ Version。

## 4.连接源

要使用定义的对象，我们需要设置一个ConnectionSource。

为此，我们可以使用创建单个连接的JdbcConnectionSource类，或表示简单池连接源的JdbcPooledConnectionSource类：

```java
JdbcPooledConnectionSource connectionSource 
  = new JdbcPooledConnectionSource("jdbc:h2:mem:myDb");

// work with the connectionSource

connectionSource.close();
```

也可以使用其他具有更好性能的外部数据源，方法是将它们包装在DataSourceConnectionSource对象中。

## 5. TableUtils类

基于ConnectionSource，我们可以使用TableUtils类中的静态方法对数据库模式执行操作：

-   createTable() – 根据实体类定义或DatabaseTableConfig对象创建表
-   createTableIfNotExists() – 类似于前面的方法，除了它只会在表不存在时创建表；这仅适用于支持它的数据库
-   dropTable() – 删除一个表
-   clearTable () – 从表中删除数据

让我们看看如何使用TableUtils为我们的Library类创建表：

```java
TableUtils.createTableIfNotExists(connectionSource, Library.class);
```

## 6. DAO对象

ORMLite 包含一个DaoManager类，它可以为我们创建具有 CRUD 功能的DAO对象：

```java
Dao<Library, Long> libraryDao 
  = DaoManager.createDao(connectionSource, Library.class);
```

DaoManager不会为每个后续的createDao() 调用重新生成类，而是重用它以获得更好的性能。

接下来，我们可以对Library对象执行 CRUD 操作：

```java
Library library = new Library();
library.setName("My Library");
libraryDao.create(library);
        
Library result = libraryDao.queryForId(1L);
        
library.setName("My Other Library");
libraryDao.update(library);
        
libraryDao.delete(library);
```

DAO也是一个可以遍历所有记录的迭代器：

```java
libraryDao.forEach(lib -> {
    System.out.println(lib.getName());
});
```

然而，ORMLite 只会在循环执行到最后时关闭底层的 SQL 语句。异常或返回语句可能会导致代码中的资源泄漏。

出于这个原因，ORMLite 文档建议我们直接使用迭代器：

```java
try (CloseableWrappedIterable<Library> wrappedIterable 
  = libraryDao.getWrappedIterable()) {
    wrappedIterable.forEach(lib -> {
        System.out.println(lib.getName());
    });
 }
```

这样，我们可以使用try-with-resources或finally块关闭迭代器，避免任何资源泄漏。

### 6.1. 自定义 DAO 类

如果我们想扩展提供的DAO对象的行为，我们可以创建一个扩展Dao类型的新接口：

```java
public interface LibraryDao extends Dao<Library, Long> {
    public List<Library> findByName(String name) throws SQLException;
}
```

然后，让我们添加一个实现此接口并扩展BaseDaoImpl类的类：

```java
public class LibraryDaoImpl extends BaseDaoImpl<Library, Long> 
  implements LibraryDao {
    public LibraryDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Library.class);
    }

    @Override
    public List<Library> findByName(String name) throws SQLException {
        return super.queryForEq("name", name);
    }
}
```

请注意，我们需要有一个这种形式的构造函数。

最后，要使用我们的自定义DAO，我们需要将类名添加到库类定义中：

```java
@DatabaseTable(tableName = "libraries", daoClass = LibraryDaoImpl.class)
public class Library { 
    // ...
}
```

这使我们能够使用DaoManager来创建自定义类的实例：

```java
LibraryDao customLibraryDao 
  = DaoManager.createDao(connectionSource, Library.class);
```

然后我们可以使用标准DAO类中的所有方法，以及我们的自定义方法：

```java
Library library = new Library();
library.setName("My Library");

customLibraryDao.create(library);
assertEquals(
  1, customLibraryDao.findByName("My Library").size());
```

## 7.定义实体关系

ORMLite 使用“外部”对象或集合的概念来定义实体之间的关系以实现持久性。

让我们来看看如何定义每种类型的字段。

### 7.1. 异物场

我们可以通过在用@DatabaseField注解的字段上使用foreign=true属性来在两个实体类之间创建单向的一对一关系。该字段的类型必须也保存在数据库中。

首先，让我们定义一个名为Address的新实体类：

```java
@DatabaseTable(tableName="addresses")
public class Address {
    @DatabaseField(generatedId = true)
    private long addressId;

    @DatabaseField(canBeNull = false)
    private String addressLine;
    
    // standard getters, setters 
}
```

接下来，我们可以将一个地址类型的字段添加到我们的库类中，它被标记为foreign：

```java
@DatabaseTable(tableName = "libraries")
public class Library {      
    //...

    @DatabaseField(foreign=true, foreignAutoCreate = true, 
      foreignAutoRefresh = true)
    private Address address;

    // standard getters, setters
}
```

请注意，我们还向@DatabaseField注解添加了另外两个属性：foreignAutoCreate和foreignAutoRefresh，均设置为true。

foreignAutoCreate =true属性意味着当我们保存一个带有地址字段的Library对象时，外部对象也将被保存，前提是它的id不为 null 并且具有generatedId=true属性。

如果我们将foreignAutoCreate设置为默认值false，那么我们需要在保存引用它的Library对象之前明确地持久化外部对象。

同样，foreignAutoRefresh = true属性指定在检索库对象时，也将检索关联的外部对象。否则，我们需要手动刷新它。

让我们添加一个带有Address字段的新Library对象，并调用libraryDao来持久化两者：

```java
Library library = new Library();
library.setName("My Library");
library.setAddress(new Address("Main Street nr 20"));

Dao<Library, Long> libraryDao 
  = DaoManager.createDao(connectionSource, Library.class);
libraryDao.create(library);
```

然后，我们可以调用addressDao来验证地址是否也已保存：

```java
Dao<Address, Long> addressDao 
  = DaoManager.createDao(connectionSource, Address.class);
assertEquals(1, 
  addressDao.queryForEq("addressLine", "Main Street nr 20")
  .size());
```

### 7.2. 国外收藏

对于关系的多边，我们可以使用带有@ForeignCollectionField注解的ForeignCollection<T>或Collection<T>类型。

让我们像上面那样创建一个新的Book实体，然后在Library类中添加一对多关系：

```java
@DatabaseTable(tableName = "libraries")
public class Library {  
    // ...
    
    @ForeignCollectionField(eager=false)
    private ForeignCollection<Book> books;
    
    // standard getters, setters
}
```

除此之外，我们还需要在Book类中添加一个Library类型的字段：

```java
@DatabaseTable
public class Book {
    // ...
    @DatabaseField(foreign = true, foreignAutoRefresh = true) 
    private Library library;

    // standard getters, setters
}
```

ForeignCollection具有对Book类型的记录进行操作的add ()和remove()方法：

```java
Library library = new Library();
library.setName("My Library");
libraryDao.create(library);

libraryDao.refresh(library);

library.getBooks().add(new Book("1984"));
```

在这里，我们创建了一个图书馆对象，然后将一个新的Book对象添加到books字段中，该对象也将其保存到数据库中。

请注意，由于我们的集合被标记为延迟加载(eager=false)，因此我们需要在能够使用 book 字段之前调用refresh()方法。

我们还可以通过在Book类中设置library字段来创建关系：

```java
Book book = new Book("It");
book.setLibrary(library);
bookDao.create(book);
```

为了验证这两个Book对象都已添加到图书馆中，我们可以使用queryForEq()方法来查找具有给定library_id的所有Book记录：

```java
assertEquals(2, bookDao.queryForEq("library_id", library).size());
```

这里，library_id是外键列的默认名称，主键是从库对象中推断出来的。

## 8.查询生成器

每个DAO都可用于获取一个QueryBuilder对象，然后我们可以利用该对象来构建更强大的查询。

此类包含与 SQL 查询中使用的常见操作相对应的方法，例如：selectColumns()、where()、groupBy()、 having()、countOf()、distinct()、orderBy()、join()。

让我们看一个示例，说明我们如何找到所有关联了不止一本书的图书馆记录：

```java
List<Library> libraries = libraryDao.queryBuilder()
  .where()
  .in("libraryId", bookDao.queryBuilder()
    .selectColumns("library_id")
    .groupBy("library_id")
    .having("count() > 1"))
  .query();
```

## 9.总结

在本文中，我们了解了如何使用 ORMLite 定义实体，以及可用于操作对象及其相关关系数据库的库的主要特性。