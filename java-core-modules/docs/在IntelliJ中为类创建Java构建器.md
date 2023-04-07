## 一、简介

Builder[设计模式](https://www.baeldung.com/creational-design-patterns#builder)是使用最广泛的创建模式之一。它帮助我们构建复杂的对象。

**手工编写构建器既麻烦又容易出错。因此，我们应该尽可能使用专用工具自动生成它们。**

[在本教程中，我们将探讨在IntelliJ](https://www.jetbrains.com/idea/) IDE中自动创建构建器类的不同方法。我们将了解 IntelliJ 开箱即用的内置功能，以及第三方插件。

## 2.初始设置

在本文中，我们将使用 IntelliJ IDEA 社区版 2019.1.3，这是撰写本文时的最新版本。**然而，示例中介绍的所有技术也应该适用于任何其他版本的 IDEA。**

让我们从定义*Book*类开始，我们将为其生成构建器：

```java
public class Book {
    private String title;
    private Author author;
    private LocalDate publishDate;
    private int pageCount;

    // standard constructor(s), getters and setters
}复制
```

## 3. 使用 IntelliJ 的内置功能

**要使用 IntelliJ 的内置工具为\*Book\*类生成构建器，我们需要一个合适的构造器。**

让我们创建一个：

```java
public Book(String title, Author author, LocalDate publishDate, int pageCount) {
    this.title = title;
    this.author = author;
    this.publishDate = publishDate;
    this.pageCount = pageCount;
}复制
```

现在，我们准备创建一个构建器。因此，让我们将光标放在创建的构造函数上，然后按*Ctrl+Alt+Shift+T*（在 PC 上）打开[*Refactor This弹出窗口，然后选择*](https://www.jetbrains.com/help/idea/refactoring-source-code.html#refactoring_invoke)[*Replace Constructor with Builder*](https://www.jetbrains.com/help/idea/replace-constructor-with-builder.html)重构：

[![截图-2019-07-27-at-20.51.48](https://www.baeldung.com/wp-content/uploads/2019/07/Screenshot-2019-07-27-at-20.51.48-1024x704.png)](https://www.baeldung.com/wp-content/uploads/2019/07/Screenshot-2019-07-27-at-20.51.48.png)

我们可以进一步调整构建器类的一些选项，比如它的名称和目标包：

[![截图-2019-07-27-at-20.53.03](https://www.baeldung.com/wp-content/uploads/2019/07/Screenshot-2019-07-27-at-20.53.03-889x1024.png)](https://www.baeldung.com/wp-content/uploads/2019/07/Screenshot-2019-07-27-at-20.53.03.png)

因此，我们生成了*BookBuilder*类：

```java
public class BookBuilder {
    private String title;
    private Author author;
    private LocalDate publishDate;
    private int pageCount;

    public BookBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public BookBuilder setAuthor(Author author) {
        this.author = author;
        return this;
    }

    public BookBuilder setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
        return this;
    }

    public BookBuilder setPageCount(int pageCount) {
        this.pageCount = pageCount;
        return this;
    }

    public Book createBook() {
        return new Book(title, author, publishDate, pageCount);
    }
}复制
```

### 3.1. 自定义 Setter 前缀

在构建器类中为 setter 方法使用*with* 前缀是一种常见的做法。

**要更改默认前缀，我们需要选择选项窗口右上角的\*Rename Setters Prefix\***图标：

[![截图-2019-07-27-at-20.54.03](https://www.baeldung.com/wp-content/uploads/2019/07/Screenshot-2019-07-27-at-20.54.03-888x1024.png)](https://www.baeldung.com/wp-content/uploads/2019/07/Screenshot-2019-07-27-at-20.54.03.png)

### 3.2. 静态内部生成器

**我们中的一些人可能更喜欢将构建器实现为静态内部类，正如[Joshua Bloch 在 Effective Java 中所描述的那样](http://www.informit.com/articles/article.aspx?p=1216151&seqNum=2)**。

如果是这种情况，我们需要采取一些额外的步骤来使用 IntelliJ 的*Replace Constructor with Builder*功能来实现这一点。

首先，我们需要手动创建一个空的内部类，并将构造函数设为私有：

```java
public class Book {

    private String title;
    private Author author;
    private LocalDate publishDate;
    private int pageCount;

    public static class Builder {
        
    }

    private Book(String title, Author author, LocalDate publishDate, int pageCount) {
        this.title = title;
        this.author = author;
        this.publishDate = publishDate;
        this.pageCount = pageCount;
    }

    // standard getters and setters
}复制
```

此外，我们必须在选项窗口中选择*使用现有的*并指向我们新创建的类：

[![截图-2019-07-27-at-20.55.02](https://www.baeldung.com/wp-content/uploads/2019/07/Screenshot-2019-07-27-at-20.55.02-885x1024.png)](https://www.baeldung.com/wp-content/uploads/2019/07/Screenshot-2019-07-27-at-20.55.02.png)

 

## 4. 使用 InnerBuilder 插件

**现在让我们看看如何使用[InnerBuilder插件为](https://plugins.jetbrains.com/plugin/7354-innerbuilder)\*Book\*类生成构建器。**

安装插件后，我们可以通过按*Alt+Insert*（在 PC 上）并选择*Builder…选项来打开*[*Generate*](https://www.jetbrains.com/help/idea/generating-code.html)弹出窗口：

[![截图-2019-07-27-at-20.56.07](https://www.baeldung.com/wp-content/uploads/2019/07/Screenshot-2019-07-27-at-20.56.07-1024x589.png)](https://www.baeldung.com/wp-content/uploads/2019/07/Screenshot-2019-07-27-at-20.56.07.png)

*或者，我们可以通过按Alt+Shift+B*（在 PC 上）直接调用 InnerBuilder 插件：

[![截图-2019-07-27-at-20.56.14](https://www.baeldung.com/wp-content/uploads/2019/07/Screenshot-2019-07-27-at-20.56.14-963x1024.png)](https://www.baeldung.com/wp-content/uploads/2019/07/Screenshot-2019-07-27-at-20.56.14.png)

如我们所见，我们可以选择几个选项来自定义生成的构建器。

让我们看看在所有选项都未选中时生成的构建器：

```java
public static final class Builder {
    private String title;
    private Author author;
    private LocalDate publishDate;
    private int pageCount;

    public Builder() {
    }

    public Builder title(String val) {
        title = val;
        return this;
    }

    public Builder author(Author val) {
        author = val;
        return this;
    }

    public Builder publishDate(LocalDate val) {
        publishDate = val;
        return this;
    }

    public Builder pageCount(int val) {
        pageCount = val;
        return this;
    }

    public Book build() {
        return new Book(this);
    }
}复制
```

**InnerBuilder 插件默认将构建器实现为静态内部类。**

## 5. 使用 Builder 生成器插件

最后，让我们看看[Builder Generator](https://plugins.jetbrains.com/plugin/6585-builder-generator)是如何工作的。

同样，对于 InnerBuilder，我们可以按*Alt+Insert*（在 PC 上）并选择*Builder*选项或使用*Alt+Shift+B*快捷键。

[![截图-2019-07-27-at-20.57.51](https://www.baeldung.com/wp-content/uploads/2019/07/Screenshot-2019-07-27-at-20.57.51-1024x555.png)](https://www.baeldung.com/wp-content/uploads/2019/07/Screenshot-2019-07-27-at-20.57.51.png)

如我们所见，我们可以从三个选项中选择自定义*BookBuilder*：

[![截图-2019-07-27-at-20.57.56](https://www.baeldung.com/wp-content/uploads/2019/07/Screenshot-2019-07-27-at-20.57.56.png)](https://www.baeldung.com/wp-content/uploads/2019/07/Screenshot-2019-07-27-at-20.57.56.png)

让我们取消选中所有选项并查看生成的构建器类：

```java
public final class BookBuilder {
    private String title;
    private Author author;
    private LocalDate publishDate;
    private int pageCount;

    private BookBuilder() {
    }

    public static BookBuilder aBook() {
        return new BookBuilder();
    }

    public BookBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public BookBuilder withAuthor(Author author) {
        this.author = author;
        return this;
    }

    public BookBuilder withPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
        return this;
    }

    public BookBuilder withPageCount(int pageCount) {
        this.pageCount = pageCount;
        return this;
    }

    public Book build() {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublishDate(publishDate);
        book.setPageCount(pageCount);
        return book;
    }
}复制
```

Builder Generator 插件提供的用于自定义创建的构建器类的第一个选项——*内部构建器——*是不言自明的。

**然而，另外两个更有趣，我们将在以下部分中探讨它们。**

### 5.1. *“但是”*方法选项

如果我们选择这个选项，插件将向*BookBuilder类添加一个**but()*方法：

```java
public BookBuilder but() {
    return aBook().withTitle(title).withAuthor(author)
      .withPublishDate(publishDate).withPageCount(pageCount);
}复制
```

现在，假设我们要创建三本书，作者相同，页数相同，但标题和出版日期不同。**我们可以创建一个已经设置了通用属性的基础构建器，然后使用\*but()\*方法从中创建新的\*BookBuilder\*（以及稍后的\*Book ）。\***

让我们看一个例子：

```java
BookBuilder commonBuilder = BookBuilder.aBook().withAuthor(johnDoe).withPageCount(123);

Book my_first_book = commonBuilder.but()
  .withPublishDate(LocalDate.of(2017, 12, 1))
  .withTitle("My First Book").build();

Book my_second_book = commonBuilder.but()
  .withPublishDate(LocalDate.of(2018, 12, 1))
  .withTitle("My Second Book").build();

Book my_last_book = commonBuilder.but()
  .withPublishDate(LocalDate.of(2019, 12, 1))
  .withTitle("My Last Book").build();复制
```

### 5.2. 使用单一字段选项

*如果我们选择这个选项，生成的构建器将保存对创建的Book*对象的引用，而不是所有书籍的属性：

```java
public final class BookBuilder {
    private Book book;

    private BookBuilder() {
        book = new Book();
    }

    public static BookBuilder aBook() {
        return new BookBuilder();
    }

    public BookBuilder withTitle(String title) {
        book.setTitle(title);
        return this;
    }

    public BookBuilder withAuthor(Author author) {
        book.setAuthor(author);
        return this;
    }

    public BookBuilder withPublishDate(LocalDate publishDate) {
        book.setPublishDate(publishDate);
        return this;
    }

    public BookBuilder withPageCount(int pageCount) {
        book.setPageCount(pageCount);
        return this;
    }

    public Book build() {
        return book;
    }
}复制
```

**这是一种创建构建器类的不同方法，在某些情况下可能会派上用场。**

## 六，结论

在本教程中，我们探讨了在 IntelliJ 中生成构建器类的不同方法。

**通常最好使用这些工具来自动生成我们的构建器**。我们提供的每个选项都有其优点和缺点。我们实际选择哪种方法完全取决于品味和个人喜好。