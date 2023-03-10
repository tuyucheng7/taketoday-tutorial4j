## 1. 概述

在这个快速教程中，我们将了解Spring Data中save()和saveAll()方法之间的性能差异。

## 2. 应用构建

为了测试性能，我们需要一个包含实体和Repository的Spring应用程序。

让我们创建一个书籍实体：

```java
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;
    private String author;

    // constructors, standard getters and setters
}
```

此外，让我们为它创建一个Repository：

```java
public interface BookRepository extends JpaRepository<Book, Long> {
}
```

## 3. 性能

为了测试性能，我们使用这两种方法保存10000本书。

首先，我们将使用save()方法：

```java
for(int i = 0; i < bookCount; i++) {
    bookRepository.save(new Book("Book " + i, "Author " + i));
}
```

然后，我们将创建一个Book集合并使用saveAll()方法一次性保存所有图书：

```java
List<Book> bookList = new ArrayList<>();
for (int i = 0; i < bookCount; i++) {
    bookList.add(new Book("Book " + i, "Author " + i));
}

bookRepository.saveAll(bookList);
```

在我们的测试中，我们注意到第一种方法大约需要0.86秒，而第二种方法大约需要0.2秒。

此外，当我们启用JPA批量插入时，我们观察到save()方法的性能下降了10%，而saveAll()方法的性能提高了60%。

## 4. 差异

查看这两个方法的实现，我们可以看到saveAll()迭代每个元素并在每次迭代中使用save()方法。这意味着它不应该有这么大的性能差异。

但如果仔细观察，我们发现这两种方法都带有@Transactional注解。

此外，默认事务传播类型是REQUIRED，这意味着**如果未提供事务，则每次调用方法时都会创建一个新事务**。

在我们的例子中，每次我们调用save()方法时，都会创建一个新事务，而当我们调用saveAll()时，只会创建一个事务，并且该事务稍后会被save()重用。

这种开销转化为我们之前注意到的性能差异。

最后，启用批处理时的开销更大，因为它是在事务级别完成的。

## 5. 总结

在本文中，我们了解了Spring Data中save()和saveAll()方法之间的性能差异。

最终，选择是否使用一种方法而不是另一种方法会对应用程序的性能产生重大影响。