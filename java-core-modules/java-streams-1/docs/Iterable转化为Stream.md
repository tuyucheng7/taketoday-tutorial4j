## 1. 概述

在这个简短的教程中，我们将Java Iterable对象转换为Stream并对其执行一些标准操作。

## 2. 将Iterable转换为Stream

Iterable接口的设计考虑到了通用性，并且本身不提供任何stream()方法。

简单地说，你可以将它传递给StreamSupport.stream()方法并从给定的Iterable实例中获取Stream。

考虑以下的Iterable实例：

```java
Iterable<String> iterable = Arrays.asList("Testing", "Iterable", "conversion", "to", "Stream");
```

下面是我们如何将这个Iterable实例转换为Stream：

```java
StreamSupport.stream(iterable.spliterator(), false);
```

请注意，StreamSupport.stream()中的第二个参数确定生成的Stream应该是并行的还是顺序的。对于并行Stream，你应该将其设置为true。

下面编写一个简单的测试：

```java
@Test 
void givenIterable_whenConvertedToStream_thenNotNull() {
    Iterable<String> iterable = Arrays.asList("Testing", "Iterable", "conversion", "to", "Stream");

    assertNotNull(StreamSupport.stream(iterable.spliterator(), false));
}
```

另外，一个注意点：流是不可重用的，而Iterable是可重用的；它还提供了一个spliterator()方法，该方法返回给定Iterable描述的元素的java.lang.Spliterator实例。

## 3. 执行流操作

下面我们执行一个简单的流操作：

```java
@Test
void whenConvertedToList_thenCorrect() {
	Iterable<String> iterable = Arrays.asList("Testing", "Iterable", "conversion", "to", "Stream");
    
	List<String> result = StreamSupport.stream(iterable.spliterator(), false)
		.map(String::toUpperCase)
		.collect(Collectors.toList());
    
	assertThat(result, contains("TESTING", "ITERABLE", "CONVERSION", "TO", "STREAM"));
}
```

## 4. 总结

这个简单的教程演示了如何将Iterable实例转换为Stream实例并对其执行标准操作，就像对任何其他集合实例所做的那样。