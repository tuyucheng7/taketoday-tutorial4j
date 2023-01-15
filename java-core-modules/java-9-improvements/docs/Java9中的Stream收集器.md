## 1. 概述

[Java 8中添加了收集器](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Collectors.html)，有助于将输入元素累积到可变容器中，例如Map、List和Set。

在本文中，我们介绍Java 9中添加的两个新收集器：Collectors.filtering和Collectors.flatMapping与Collectors.grouping结合使用，提供智能的元素收集。

## 2. filtering收集器

Collectors.filtering类似于Stream filter()；它用于过滤输入元素，但用于不同的场景。Stream的过滤器用于流链中，而filtering是一个收集器，它被设计为与groupingBy一起使用。

使用Stream的filter首先对值进行过滤，然后对其进行分组。这样，被过滤掉的值就消失了，不会参与分组。如果我们需要跟踪所有的值，那么我们需要先分组，然后应用Collectors.filtering实际执行的过滤。

Collectors.filtering接收一个用于过滤输入元素的函数和一个用于收集过滤后的元素的收集器：

```java
@Test
void givenList_whenSatifyPredicate_thenMapValueWithOccurences() {
	List<Integer> numbers = List.of(1, 2, 3, 5, 5);

	Map<Integer, Long> result = numbers.stream().filter(val -> val > 3)
			.collect(Collectors.groupingBy(
					Function.identity(), 
					Collectors.counting())
			);

	assertEquals(1, result.size());

	result = numbers.stream()
			.collect(Collectors.groupingBy(
					Function.identity(), 
					Collectors.filtering(val -> val > 3, Collectors.counting()))
			);

	assertEquals(4, result.size());
}
```

## 3. flatMapping收集器

Collectors.flatMapping与Collectors.mapping类似，但具有更细粒度的目标。这两个收集器都接收一个函数和一个收集器，其中元素被收集，但flatMapping函数接收一个元素流，然后由收集器累积。

让我们看看下面的模型类：

```java
class Blog {
    private String authorName;
    private List<String> comments;
    
    Blog(String authorName, String... comments) {
        this.authorName = authorName;
        this.comments = List.of(comments);
    }
    
    String getAuthorName() {
        return this.authorName;
    }
    
    List<String> getComments() {
        return this.comments;
    }
}
```

Collectors.flatMapping允许我们跳过中间收集并直接写入单个容器，该容器映射到Collectors.groupingBy定义的组：

```java
@Test
void givenListOfBlogs_whenAuthorName_thenMapAuthorWithComments() {
	Blog blog1 = new Blog("1", "Nice", "Very Nice");
	Blog blog2 = new Blog("2", "Disappointing", "Ok", "Could be better");
	List<Blog> blogs = List.of(blog1, blog2);

	Map<String, List<List<String>>> authorComments1 = blogs.stream()
			.collect(Collectors.groupingBy(
					Blog::getAuthorName, 
					Collectors.mapping(
							Blog::getComments, 
							Collectors.toList()))
			);

	assertEquals(2, authorComments1.size());
	assertEquals(2, authorComments1.get("1").get(0).size());
	assertEquals(3, authorComments1.get("2").get(0).size());

	Map<String, List<String>> authorComments2 = blogs.stream()
			.collect(Collectors.groupingBy(
					Blog::getAuthorName, 
					Collectors.flatMapping(
							blog -> blog.getComments().stream(), Collectors.toList()))
			);

	assertEquals(2, authorComments2.size());
	assertEquals(2, authorComments2.get("1").size());
	assertEquals(3, authorComments2.get("2").size());
}
```

Collectors.mapping将所有分组作者的评论映射到收集器的容器，即List，而这个中间集合通过flatMapping删除，因为它提供了要映射到收集器容器的评论集合的直接流。

## 4. 总结

本文介绍了Java 9中引入的新收集器的使用，即与Collectors.groupingBy()结合使用的Collectors.filtering( )和Collectors.flatMapping()。

这些收集器也可以与Collectors.partitioningBy()一起使用，但它仅根据条件创建两个分区，并且收集器的实际作用没有得到利用；因此在本教程中省略了。