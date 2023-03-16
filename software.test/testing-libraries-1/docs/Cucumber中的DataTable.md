## 1. 概述

Cucumber是一个行为驱动开发(BDD)框架，允许开发人员使用Gherkin语言创建基于文本的测试场景。

在许多情况下，这些场景需要模拟数据来执行功能，这些数据的注入可能很麻烦-特别是对于复杂或很多数据的情况。

在本教程中，我们将了解如何使用Cucumber DataTable以可读的方式包含模拟数据。

## 2. 场景语法

在定义[Cucumber场景](https://www.baeldung.com/cucumber-scenario-outline)时，我们通常会注入场景其余部分使用的测试数据：

```gherkin
Scenario: Correct non-zero number of books found by author
    Given I have the a book in the store called The Devil in the White City by Erik Larson
    When I search for books by author Erik Larson
    Then I find 1 book
```

### 2.1 DataTable

虽然内联数据对于测试一个Book对象就足够了，但当需要测试多个Book对象时，我们的场景可能会变得混乱。

为了解决这个问题，我们可以在场景中创建一个数据表：

```gherkin
Scenario: Correct non-zero number of books found by author
    Given I have the following books in the store
        | The Devil in the White City          | Erik Larson |
        | The Lion, the Witch and the Wardrobe | C.S. Lewis  |
        | In the Garden of Beasts              | Erik Larson |
    When I search for books by author Erik Larson
    Then I find 2 books
```

**通过在Given子句文本下方缩进表格，我们将数据表定义为Given子句的一部分**。使用此数据表，我们可以通过添加或删除行将任意数量的Book对象作为测试数据。

此外，**数据表可以与任何子句一起使用**，而不仅仅是Given子句。

### 2.2 包含标题

很明显，数据表的第一列代表书名，第二列代表书的作者。但是，每一列的含义并不总是那么明显。

如果需要更直观的表达，**我们可以通过添加一个首行来包含标题**：

```gherkin
Scenario: Correct non-zero number of books found by author
    Given I have the following books in the store
        | title                                | author      |
        | The Devil in the White City          | Erik Larson |
        | The Lion, the Witch and the Wardrobe | C.S. Lewis  |
        | In the Garden of Beasts              | Erik Larson |
    When I search for books by author Erik Larson
    Then I find 2 books
```

虽然标题似乎只是表中的另一行，但当我们在下一节中将表解析为Map列表时，第一行具有特殊含义。

## 3. 步骤定义

创建场景后，我们实现Given步骤的定义。

对于包含数据表的步骤，**我们使用DataTable参数实现我们的方法**：

```java
@Given("some phrase")
public void somePhrase(DataTable table) {
    // ...
}
```

DataTable对象包含我们在场景中定义的数据表中的表格数据，以及**将这些数据转换为可用信息的方法**。通常，在Cucumber中转换数据表有三种方法：

1. 集合的集合
2. Map的集合
3. 表格转换器

为了演示每种方法，我们将使用一个简单的Book域类：

```java
public class Book {

    private String title;
    private String author;

    // standard constructors, getters & setters ...
}
```

此外，我们将创建一个管理Book对象的BookStore类：

```java
public class BookStore {

    private final List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }

    public void addAllBooks(Collection<Book> books) {
        this.books.addAll(books);
    }

    public List<Book> booksByAuthor(String author) {
        return books.stream()
              .filter(book -> Objects.equals(author, book.getAuthor()))
              .collect(Collectors.toList());
    }
}
```

对于以下每种情况，我们将从基本步骤定义开始：

```java
public class BookStoreRunSteps {

    private BookStore store;
    private List<Book> foundBooks;

    @Before
    public void setUp() {
        store = new BookStore();
        foundBooks = new ArrayList<>();
    }

    // When & Then definitions ...
}
```

### 3.1 集合的集合

**处理表格数据的最基本方法是将DataTable参数转换为List<List<>>**。

我们可以创建一个没有标题的表格来演示：

```gherkin
Scenario: Correct non-zero number of books found by author by list
    Given I have the following books in the store by list
        | The Devil in the White City          | Erik Larson |
        | The Lion, the Witch and the Wardrobe | C.S. Lewis  |
        | In the Garden of Beasts              | Erik Larson |
    When I search for books by author Erik Larson
    Then I find 2 books
```

**Cucumber通过将每一行视为集合的子集合，将上表转换为List<List<>>**。

因此，Cucumber将每一行解析为一个列表，其中书名作为第一个元素，作者作为第二个元素：

```gherkin
[
    ["The Devil in the White City", "Erik Larson"],
    ["The Lion, the Witch and the Wardrobe", "C.S. Lewis"],
    ["In the Garden of Beasts", "Erik Larson"]
]
```

我们使用asLists方法(提供一个String.class参数)将DataTable参数转换为List<List<String\>>。**这个Class参数告诉asLists方法我们期望每个元素是什么数据类型**。

在我们的例子中，我们希望title和author都是String值，所以我们指定参数为String.class：

```java
@Given("^I have the following books in the store by list$")
public void haveBooksInTheStoreByList(DataTable table) {
	List<List<String>> rows = table.asLists(String.class);
    
	for (List<String> columns : rows) {
		store.addBook(new Book(columns.get(0), columns.get(1)));
	}
}
```

然后我们遍历子列表的每个元素并创建一个对应的Book对象。最后，我们将每个创建的Book对象添加到BookStore对象中。

如果我们解析包含标题的数据表，我们需要**跳过第一行**，因为Cucumber不区分数据表的标题和实际数据。

### 3.2 Map的集合

虽然List<List<>>提供了从数据表中提取元素的基本机制，但步骤实现可能很神秘。Cucumber提供了一个List<Map<>>机制作为一种更具可读性的替代方案。

在这种情况下，**我们必须为我们的表格提供一个标题**：

```gherkin
Scenario: Correct non-zero number of books found by author by map
    Given I have the following books in the store by map
        | title                                | author      |
        | The Devil in the White City          | Erik Larson |
        | The Lion, the Witch and the Wardrobe | C.S. Lewis  |
        | In the Garden of Beasts              | Erik Larson |
    When I search for books by author Erik Larson
    Then I find 2 books
```

与List<List\>>机制类似，Cucumber创建一个包含每一行的列表，**将列标题映射到每个列值**。

Cucumber对每个后续行重复此过程：

```gherkin
[
    {"title": "The Devil in the White City", "author": "Erik Larson"},
    {"title": "The Lion, the Witch and the Wardrobe", "author": "C.S. Lewis"},
    {"title": "In the Garden of Beasts", "author": "Erik Larson"}
]
```

我们使用asMaps方法(提供两个String.class参数)将DataTable参数转换为List<Map<String, String>>。**第一个参数表示key(title)的数据类型，第二个参数表示每个value(author)的数据类型**。因此，我们提供了两个String.class参数，因为我们的title(key)和author(value)都是字符串。

然后我们遍历每个Map对象并使用title列作为key提取每个列值：

```java
@Given("^I have the following books in the store by map$")
public void haveBooksInTheStoreByMap(DataTable table) {
	List<Map<String, String>> rows = table.asMaps(String.class, String.class);
    
	for (Map<String, String> columns : rows) {
		store.addBook(new Book(columns.get("title"), columns.get("author")));
	}
}
```

### 3.3 表转换器

将数据表转换为可用对象的最后一个(也是最灵活的)机制是创建一个TableTransformer。

**TableTransformer是一个对象，它指示Cucumber如何将DataTable对象转换为我们所需的域对象**：

<img src="../assets/img_6.png">

让我们看一个示例场景：

```gherkin
Scenario: Correct non-zero number of books found by author with transformer
    Given I have the following books in the store with transformer
        | title                                | author      |
        | The Devil in the White City          | Erik Larson |
        | The Lion, the Witch and the Wardrobe | C.S. Lewis  |
        | In the Garden of Beasts              | Erik Larson |
    When I search for books by author Erik Larson
    Then I find 2 books
```

虽然List<Map<>>比List<List<>>更精确，但这种方式仍然将步骤定义与数据的转换逻辑代码糅合在一起。

相反，**我们应该使用所需的域对象(在本例中为BookCatalog)作为参数来定义我们的步骤**：

```java
@Given("^I have the following books in the store with transformer$")
public void haveBooksInTheStoreByTransformer(BookCatalog catalog) {
	store.addAllBooks(catalog.getBooks());
}
```

为此，**我们必须创建TypeRegistryConfigurer接口的自定义实现**。该实现必须执行两件事：

1. 创建一个新的TableTransformer实现
2. 使用configureTypeRegistry方法注册这个新实现

要将DataTable捕获到可用的域对象中，我们将创建一个BookCatalog类：

```java
public class BookCatalog {

    private final List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }

    // standard getter ...
}
```

为了执行实际的转换，让我们实现TypeRegistryConfigurer接口：

```java
public class BookStoreRegistryConfigurer implements TypeRegistryConfigurer {

    @Override
    public Locale locale() {
        return Locale.ENGLISH;
    }

    @Override
    public void configureTypeRegistry(TypeRegistry typeRegistry) {
        typeRegistry.defineDataTableType(new DataTableType(BookCatalog.class, new BookTableTransformer()));
    }

    // ...
}
```

然后我们将为BookCatalog类以内部类的形式实现TableTransformer接口：

```java
public class BookStoreRegistryConfigurer implements TypeRegistryConfigurer {

    private static class BookTableTransformer implements TableTransformer<BookCatalog> {

        @Override
        public BookCatalog transform(DataTable table) throws Throwable {

            BookCatalog catalog = new BookCatalog();

            table.cells().stream()
                  .skip(1)        // Skip header row
                  .map(fields -> new Book(fields.get(0), fields.get(1)))
                  .forEach(catalog::addBook);

            return catalog;
        }
    }
}
```

请注意，我们转换表格中的英语数据，因此我们从locale()方法返回英语语言环境。**当在不同的语言环境中解析数据时，我们必须将locale()方法的返回类型更改为适当的语言环境**。

**由于我们在场景中包含了DataTable标题，因此在遍历表格单元格时必须跳过第一行(因此调用skip(1))**。如果我们的DataTable不包含标题，则不需要skip(1)调用。

**默认情况下，Cucumber假设与测试关联的步骤定义代码与[Runner类位于同一包中](https://cucumber.io/docs/cucumber/api/#junit)**。因此，如果我们将BookStoreRegistryConfigurer放置在与Runner类相同的包中，则不需要额外的配置。

**如果我们将BookStoreRegistryConfigurer添加到不同的包中，则必须在[Runner](https://www.baeldung.com/cucumber-scenario-outline#3-a-runner-class)类的@CucumberOptions注解中配置gule属性，指定BookStoreRegistryConfigurer的类名(包括全包名)**。

## 4. 总结

在本文中，我们介绍了如何使用DataTable定义包含表格数据的Gherkin场景。

此外，我们探索了三种实现使用Cucumber数据表的步骤定义的方法。

虽然List<List\>>和List<Map<>>足以用于简单的需求，但表转换器提供了更丰富的机制，能够处理更复杂的数据。