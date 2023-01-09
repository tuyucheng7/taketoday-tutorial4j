## 1. 概述

在本教程中，我们将了解Mockito的AdditionalAnswers类及其方法。

## 2. 返回参数

AdditionalAnswers类的主要用途是返回传递给mock方法的参数。

例如，当更新一个对象时，被mock的方法通常只返回更新后的对象。
**使用AdditionalAnswers中的方法，我们可以根据其在参数列表中的位置，返回作为参数传递给方法的特定参数**。

此外，AdditionalAnswers有不同的Answer类实现。

首先,我们创建一个简单的模型类：

```java
public class Book {
    private Long bookId;
    private String title;
    private String author;
    private int numberOfPages;
    // constructors, getters and setters ...
}
```

此外，我们需要一个用于操作Book对象的Repository类：

```java
public class BookRepository {

    public Book getByBookId(Long bookId) {
        return new Book(bookId, "To Kill a Mocking Bird", "Harper Lee", 256);
    }

    public Book save(Book book) {
        return new Book(book.getBookId(), book.getTitle(), book.getAuthor(), book.getNumberOfPages());
    }

    public Book selectRandomBook(Book bookOne, Book bookTwo, Book bookThree) {
        List<Book> selection = new ArrayList<>();
        selection.add(bookOne);
        selection.add(bookTwo);
        selection.add(bookThree);
        Random random = new Random();
        return selection.get(random.nextInt(selection.size()));
    }
}
```

相应地，我们有一个调用Repository方法的Service类：

```java
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book getByBookId(Long id) {
        return bookRepository.getByBookId(id);
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public Book selectRandomBook(Book book1, Book book2, Book book3) {
        return bookRepository.selectRandomBook(book1, book2, book3);
    }
}
```

考虑到这一点，让我们创建一些测试。

### 2.1 返回第一个参数

对于测试类，我们需要通过标注JUnit测试类以使用MockitoExtension来启用对Mockito测试的注解支持。
此外，我们需要mock Service和Repository类：

```java

@ExtendWith(MockitoExtension.class)
public class BookServiceUnitTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;
    // ...
}
```

首先，让我们创建一个返回第一个参数的测试 - AdditionalAnswers.returnsFirstArg()：

```java
class BookServiceUnitTest {

    @Test
    void givenSaveMethodMocked_whenSaveInvoked_thenReturnFirstArgument_UnitTest() {
        Book book = new Book("To Kill a Mocking Bird", "Harper Lee", 256);
        when(bookRepository.save(any(Book.class))).then(AdditionalAnswers.returnsFirstArg());

        Book savedBook = bookService.save(book);

        assertEquals(savedBook, book);
    }
}
```

换句话说，我们将mock BookRepository类中的save方法，它接收Book对象作为参数。

**当我们运行这个测试时，它确实会返回第一个参数**，它等于我们保存的Book对象。

### 2.2 返回第二个参数

其次，我们使用AdditionalAnswers.returnsSecondArg()创建一个测试：

```java
class BookServiceUnitTest {

    @Test
    void givenCheckIfEqualsMethodMocked_whenCheckIfEqualsInvoked_thenReturnSecondArgument_UnitTest() {
        Book book1 = new Book(1L, "The Stranger", "Albert Camus", 456);
        Book book2 = new Book(2L, "Animal Farm", "George Orwell", 300);
        Book book3 = new Book(3L, "Romeo and Juliet", "William Shakespeare", 200);

        when(bookRepository.selectRandomBook(any(Book.class), any(Book.class), any(Book.class))).then(AdditionalAnswers.returnsSecondArg());

        Book secondBook = bookService.selectRandomBook(book1, book2, book3);

        assertEquals(secondBook, book2);
    }
}
```

在这种情况下，当我们的selectRandomBook方法执行时，该方法将返回第二个Book对象。

### 2.3 返回最后一个参数

同样，我们可以使用AdditionalAnswers.returnsLastArg()方法获取我们传递给方法的最后一个参数：

```java
class BookServiceUnitTest {

    @Test
    void givenCheckIfEqualsMethodMocked_whenCheckIfEqualsInvoked_thenReturnLastArgument_UnitTest() {
        Book book1 = new Book(1L, "The Stranger", "Albert Camus", 456);
        Book book2 = new Book(2L, "Animal Farm", "George Orwell", 300);
        Book book3 = new Book(3L, "Romeo and Juliet", "William Shakespeare", 200);

        when(bookRepository.selectRandomBook(any(Book.class), any(Book.class), any(Book.class))).then(AdditionalAnswers.returnsLastArg());

        Book lastBook = bookService.selectRandomBook(book1, book2, book3);

        assertEquals(lastBook, book3);
    }
}
```

在这里，调用的方法将返回第三个Book对象，因为它是最后一个参数。

### 2.4 返回指定索引的参数

最后，我们可以使用AdditionalAnswers.returnsArgAt(int index)方法返回指定索引处的参数：

```java
class BookServiceUnitTest {

    @Test
    void givenCheckIfEqualsMethodMocked_whenCheckIfEqualsInvoked_ThenReturnArgumentAtIndex_UnitTest() {
        Book book1 = new Book(1L, "The Stranger", "Albert Camus", 456);
        Book book2 = new Book(2L, "Animal Farm", "George Orwell", 300);
        Book book3 = new Book(3L, "Romeo and Juliet", "William Shakespeare", 200);

        when(bookRepository.selectRandomBook(any(Book.class), any(Book.class), any(Book.class))).then(AdditionalAnswers.returnsArgAt(1));

        Book bookOnIndex = bookService.selectRandomBook(book1, book2, book3);

        assertEquals(bookOnIndex, book2);
    }
}
```

这里我们传递的索引值为1(索引从0开始)，因此我们得到的是第二个Book对象。

## 3. 从函数式接口创建Answer

AdditionalAnswers提供了一种从函数式接口创建Answer的简洁方法。为此，它提供了两个方便的方法：answer()和answerVoid()。

请记住，这两个方法都使用@Incubating注解进行了标注。这意味着它们可能会在以后根据社区反馈进行更改。

### 3.1 使用AdditionalAnswers.answer()

该方法主要用于在Java 8中使用函数式接口创建强类型Answer。

通常，Mockito带有一组现成的通用接口，我们可以使用它们来配置mock的Answer。**例如，它为单个参数调用提供Answer1<T,A0\>**。

现在，让我们演示如何使用AdditionalAnswers.answer()创建返回Book对象的Answer：

```java
class BookServiceUnitTest {

    @Test
    void givenMockedMethod_whenMethodInvoked_thenReturnBook() {
        Long id = 1L;
        when(bookRepository.getByBookId(anyLong())).thenAnswer(answer(BookServiceUnitTest::buildBook));

        assertNotNull(bookService.getByBookId(id));
        assertEquals("The Stranger", bookService.getByBookId(id).getTitle());
    }

    private static Book buildBook(Long bookId) {
        return new Book(bookId, "The Stranger", "Albert Camus", 456);
    }
}
```

如上所示，我们使用方法引用来表示Answer1接口。

### 3.2 使用AdditionalAnswers.answerVoid()

同样，我们可以使用answerVoid()为不返回任何结果的参数调用配置mock的Answer。

接下来，我们用一个测试用例来举例说明AdditionalAnswers.answerVoid()方法的使用：

```java
class BookServiceUnitTest {

    @Test
    void givenMockedMethod_whenMethodInvoked_thenReturnVoid() {
        Long id = 2L;
        when(bookRepository.getByBookId(anyLong())).thenAnswer(answerVoid(BookServiceUnitTest::printBookId));

        bookService.getByBookId(id);

        verify(bookRepository, times(1)).getByBookId(id);
    }

    private static void printBookId(Long bookId) {
        System.out.println(bookId);
    }
}
```

如我们所见，**我们使用VoidAnswer1<A0\>接口为不返回任何结果的单个参数调用创建Answer**。

answer方法指定了我们与mock交互时执行的操作。在我们的例子中，我们只是打印传递的Book id。

## 4. 总结

本教程涵盖了Mockito中AdditionalAnswers类的方法案例。