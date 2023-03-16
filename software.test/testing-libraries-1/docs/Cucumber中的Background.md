## 1. 概述

在这个简短的教程中，我们将介绍[Cucumber](https://www.baeldung.com/cucumber-scenario-outline)中的Background，这是一个允许我们为Cucumber Feature的每次测试执行一些语句的功能。

## 2. Cucumber Background

首先，让我们解释一下[Cucumber Background](https://cucumber.io/docs/gherkin/reference/#background)是什么。它的目的是在每次运行Feature之前执行一个或多个语句。

但是Background在这里试图解决的问题是什么呢？

假设我们有一个想要使用Cucumber进行测试的书店应用程序。首先，让我们创建该应用程序，它只是一个Java类：

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

    public Optional<Book> bookByTitle(String title) {
        return books.stream()
              .filter(book -> book.getTitle().equals(title))
              .findFirst();
    }
}
```

如我们所见，可以在BookStore中添加和搜索Book对象。现在，让我们创建一些Cucumber语句来与BookStore交互：

```java
public class BookStoreRunSteps {
    private BookStore store;
    private List<Book> foundBooks;
    private Book foundBook;

    @Before
    public void setUp() {
        store = new BookStore();
        foundBooks = new ArrayList<>();
    }

    @Given("^I have the following books in the store$")
    public void haveBooksInTheStore(DataTable table) {
        List<List<String>> rows = table.asLists(String.class);

        for (List<String> columns: rows) {
            store.addBook(new Book(columns.get(0), columns.get(1)));
        }
    }

    @When("^I search for books by author (.+)$")
    public void searchForBooksByAuthor(String author) {
        foundBooks = store.booksByAuthor(author);
    }

    @When("^I search for a book titled (.+)$")
    public void searchForBookByTitle(String title) {
        foundBook = store.bookByTitle(title).orElse(null);
    }

    @Then("^I find (\\d+) books$")
    public void findBooks(int count) {
        assertEquals(count, foundBooks.size());
    }

    @Then("^I find a book$")
    public void findABook() {
        assertNotNull(foundBook);
    }

    @Then("^I find no book$")
    public void findNoBook() {
        assertNull(foundBook);
    }
}
```

通过这些语句定义，我们可以添加Book对象，按作者姓名或书名搜索Book，并检查Book是否存在。

接下来可以创建我们的Feature文件。我们可以按作者名搜索书籍，也可以按书名搜索书籍：

```gherkin
Feature: Book Store Without Background

    Scenario: Find books by author
        Given I have the following books in the store
            | The Devil in the White City          | Erik Larson |
            | The Lion, the Witch and the Wardrobe | C.S. Lewis  |
            | In the Garden of Beasts              | Erik Larson |
        When I search for books by author Erik Larson
        Then I find 2 books

    Scenario: Find books by author, but isn't there
        Given I have the following books in the store
            | The Devil in the White City          | Erik Larson |
            | The Lion, the Witch and the Wardrobe | C.S. Lewis  |
            | In the Garden of Beasts              | Erik Larson |
        When I search for books by author Marcel Proust
        Then I find 0 books

    Scenario: Find book by title
        Given I have the following books in the store
            | The Devil in the White City          | Erik Larson |
            | The Lion, the Witch and the Wardrobe | C.S. Lewis  |
            | In the Garden of Beasts              | Erik Larson |
        When I search for a book titled The Lion, the Witch and the Wardrobe
        Then I find a book

    Scenario: Find book by title, but isn't there
        Given I have the following books in the store
            | The Devil in the White City          | Erik Larson |
            | The Lion, the Witch and the Wardrobe | C.S. Lewis  |
            | In the Garden of Beasts              | Erik Larson |
        When I search for a book titled Swann's Way
        Then I find no book
```

**这个Feature测试可以正常运行，但它似乎有点冗长，因为我们为每个测试初始化测试数据**。这不仅会重复很多行，而且如果我们想要更新测试数据，则必须为每个测试执行此操作。这就是Cucumber Background的用武之地。

## 3. 例子

那么，如何为这个Feature通过Background创建测试数据呢？**为此，我们必须使用关键字Background，给它起一个标题，就像我们为Scenario所做的那样，并定义要执行的语句**：

```gherkin
Background: The Book Store
    Given I have the following books in the store
        | The Devil in the White City          | Erik Larson |
        | The Lion, the Witch and the Wardrobe | C.S. Lewis  |
        | In the Garden of Beasts              | Erik Larson |
```

完成此操作后，我们可以去掉之前Feature文件中每个测试的Given子句，让测试的定义专注于自己的功能，而不包含任何数据定义：

```gherkin
Scenario: Find books by author
    When I search for books by author Erik Larson
    Then I find 2 books

Scenario: Find books by author, but isn't there
    When I search for books by author Marcel Proust
    Then I find 0 books

Scenario: Find book by title
    When I search for a book titled The Lion, the Witch and the Wardrobe
    Then I find a book

Scenario: Find book by title, but isn't there
    When I search for a book titled Swann's Way
    Then I find no book
```

正如我们所看到的，现在的Feature文件比以前简单地多。

## 4. 与@Before的区别

现在，让我们讨论Cucumber Background和[@Before钩子](https://cucumber.io/docs/cucumber/api/#before)之间的区别。钩子还允许我们在场景之前执行代码，但是**这些代码对那些只阅读Feature文件的人是隐藏的**。另一方面，Background由Feature文件中可见的语句组成。

## 5. 总结

在这篇简短的文章中，我们学习了如何使用Cucumber的Background功能。它允许我们在功能的每个场景之前执行一些语句。我们还讨论了此功能与@Before钩子之间的区别。