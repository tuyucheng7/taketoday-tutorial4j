## 1. 概述

**Java 8中引入的Spliterator接口可用于遍历和分区序列**。它是Stream的基本实用程序，尤其是并行流。

在本文中，我们将介绍它的用法、特征、方法以及如何创建我们自己的自定义实现。

## 2. Spliterator API

### 2.1 tryAdvance

这是用于单步执行序列的主要方法。**该方法接收一个Consumer，用于顺序地消费Spliterator的元素**，如果没有要遍历的元素，则返回false。

在这里，我们将看看如何使用它来遍历和划分元素。

首先，假设我们有一个包含35000篇文章的ArrayList，并且文章类定义为：

```java
public class Article {
    private List<Author> listOfAuthors;
    private int id;
    private String name;
    
    // standard constructors/getters/setters
}
```

现在，让我们实现一个处理文章列表的任务，并在每个文章名称中添加后缀“- published by Tuyucheng”：

```java
public String call() {
	int current = 0;
	while (spliterator.tryAdvance(article -> article.setName(article.getName().concat("- published by Tuyucheng")))) {
		current++;
	}
    
	return Thread.currentThread().getName() + ":" + current;
}
```

请注意，此任务在完成执行时会输出处理的文章数。

另一个关键点是我们使用了tryAdvance()方法来处理下一个元素。

### 2.2 trySplit

接下来，让我们拆分Spliterators并独立处理分区。

trySplit方法尝试将其拆分为两部分。然后调用方处理元素，最后，返回的实例将处理其他元素，从而使两者并行处理。

让我们首先生成我们的列表：

```java
public static List<Article> generateElements() {
	return Stream.generate(() -> new Article("Java"))
        .limit(35000)
        .collect(Collectors.toList());
}
```

接下来，我们使用spliterator()方法获取我们的Spliterator实例。然后我们应用我们的trySplit()方法：

```java
@Test
public void givenSpliterator_whenAppliedToAListOfArticle_thenSplitInHalf() {
    Spliterator<Article> split1 = Executor.generateElements().spliterator(); 
    Spliterator<Article> split2 = split1.trySplit(); 
    
    assertThat(new Task(split1).call()) 
        .containsSequence(Executor.generateElements().size() / 2 + ""); 
    assertThat(new Task(split2).call()) 
        .containsSequence(Executor.generateElements().size() / 2 + ""); 
}
```

**拆分过程按预期进行，并平均分配记录**。

### 2.3 estimatedSize

estimatedSize方法为我们提供了估计的元素数量：

```java
LOG.info("Size: " + split1.estimateSize());
```

这将输出：

```plaintext
Size: 17500
```

### 2.4 hasCharacteristics

此API检查给定特征是否与Spliterator的属性匹配。然后，如果我们调用上面的方法，输出将是这些特征的int表示：

```java
LOG.info("Characteristics: " + split1.characteristics());
Characteristics: 16464
```

## 3. Spliterator特性

**它有八个不同的特征来描述它的行为，这些可以用作外部工具的提示**：

-   **SIZED**：如果它能够使用estimateSize()方法返回准确数量的元素
-   **SORTED**：如果它正在遍历已排序的源
-   **SUBSIZED**：如果我们使用trySplit()方法拆分实例并获得SIZED的Spliterators
-   **CONCURRENT**：如果可以同时安全地修改源
-   **DISTINCT**：如果对于每对遇到的元素x, y, !x.equals(y)
-   **IMMUTABLE**：如果源持有的元素不能在结构上修改
-   **NONNULL**：源是否包含空值
-   **ORDERED**：如果迭代有序序列

## 4. 自定义Spliterator

### 4.1 何时自定义

首先，让我们假设以下场景：

我们有一个包含作者列表的文章类，以及可以有多个作者的文章。此外，如果作者相关文章的id与文章id匹配，则我们认为该作者与该文章相关。

我们的作者类将如下所示：

```java
public class Author {
    private String name;
    private int relatedArticleId;

    // standard getters, setters & constructors
}
```

接下来，我们将实现一个类来在遍历作者流时对作者进行计数。然后**该类将对流执行缩减**。

让我们看一下类的实现：

```java
public class RelatedAuthorCounter {
    private int counter;
    private boolean isRelated;

    // standard constructors/getters

    public RelatedAuthorCounter accumulate(Author author) {
        if (author.getRelatedArticleId() == 0) {
            return isRelated ? this : new RelatedAuthorCounter( counter, true);
        } else {
            return isRelated ? new RelatedAuthorCounter(counter + 1, false) : this;
        }
    }

    public RelatedAuthorCounter combine(RelatedAuthorCounter RelatedAuthorCounter) {
        return new RelatedAuthorCounter(
              counter + RelatedAuthorCounter.counter,
              RelatedAuthorCounter.isRelated);
    }
}
```

上述类中的每个方法在遍历时都会执行特定的操作来计数。

首先，**accumulate()方法以迭代的方式逐个遍历作者，然后combine()使用它们的值对两个计数器求和**。最后，getCounter()返回计数器。

现在，测试一下我们到目前为止所做的事情。让我们将文章的作者列表转换为作者流：

```java
Stream<Author> stream = article.getListOfAuthors().stream();
```

并实现一个**countAuthor()方法来使用RelatedAuthorCounter对流执行缩减**：

```java
private int countAutors(Stream<Author> stream) {
    RelatedAuthorCounter wordCounter = stream.reduce(
        new RelatedAuthorCounter(0, true), 
        RelatedAuthorCounter::accumulate, 
        RelatedAuthorCounter::combine);
    return wordCounter.getCounter();
}
```

如果我们使用顺序流，则输出将按预期为“count = 9”，但是，当我们尝试并行化操作时就会出现问题。

让我们看一下下面的测试用例：

```java
@Test
void givenAStreamOfAuthors_whenProcessedInParallel_countProducesWrongOutput() {
    assertThat(Executor.countAutors(stream.parallel())).isGreaterThan(9);
}
```

显然，出了点问题-在随机位置拆分流会导致作者被计数两次。

### 4.2 如何自定义

**为了解决这个问题，我们需要实现一个Spliterator，它仅在相关id和articleId匹配时才拆分作者**。以下是我们的自定义Spliterator的实现：

```java
public class RelatedAuthorSpliterator implements Spliterator<Author> {
    private final List<Author> list;
    AtomicInteger current = new AtomicInteger();
    // standard constructor/getters

    @Override
    public boolean tryAdvance(Consumer<? super Author> action) {
        action.accept(list.get(current.getAndIncrement()));
        return current.get() < list.size();
    }

    @Override
    public Spliterator<Author> trySplit() {
        int currentSize = list.size() - current.get();
        if (currentSize < 10) {
            return null;
        }
        for (int splitPos = currentSize / 2 + current.intValue();
             splitPos < list.size(); splitPos++) {
            if (list.get(splitPos).getRelatedArticleId() == 0) {
                Spliterator<Author> spliterator
                      = new RelatedAuthorSpliterator(
                      list.subList(current.get(), splitPos));
                current.set(splitPos);
                return spliterator;
            }
        }
        return null;
    }

    @Override
    public long estimateSize() {
        return list.size() - current.get();
    }

    @Override
    public int characteristics() {
        return CONCURRENT;
    }
}
```

现在应用countAuthors()方法将给出正确的输出。以下代码演示了这一点：

```java
@Test
public void givenAStreamOfAuthors_whenProcessedInParallel_countProducesRightOutput() {
    Stream<Author> stream2 = StreamSupport.stream(spliterator, true);
 
    assertThat(Executor.countAutors(stream2.parallel())).isEqualTo(9);
}
```

此外，自定义Spliterator是从作者列表中创建的，并通过保持当前位置来遍历它。

让我们更详细地讨论每个方法的实现：

-   **tryAdvance**：在当前索引位置将作者传递给消费者并增加其位置
-   **trySplit**：定义拆分机制，在我们的例子中，RelatedAuthorSpliterator在ids匹配时创建，并且拆分将列表分成两部分
-   **estimateSize**：列表大小与当前迭代作者的位置之间的差异
-   **characteristics**：返回Spliterator特征，在我们的例子中是SIZED，因为estimatedSize()方法返回的值是准确的；此外，CONCURRENT表示此Spliterator的源可以被其他线程安全地修改

## 5. 支持原始类型值

**Spliterator API支持原始类型值，包括double、int和long**。

使用泛型和原始类型专用Spliterator之间的唯一区别是给定的Consumer和Spliterator的类型。

例如，当我们需要它作为int值时，我们需要传递一个intConsumer。此外，以下是原始类型专用Spliterators的列表：

-   OfPrimitive<T, T_CONS, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>>：其他原始类型的父接口
-   OfInt：专用于int的Spliterator
-   OfDouble：专用于double的Spliterator
-   OfLong：专用于long的Spliterator

## 6. 总结

在本文中，我们介绍了Java 8 Spliterator的用法、方法、特性、拆分过程、原始类型支持以及如何自定义它。