## 1. 概述

作为Java开发人员，我们经常编写迭代一组元素并对每个元素执行操作的代码。Java 8的Stream API及其 forEach方法允许我们以干净、声明性的方式编写代码。

虽然这与循环类似，但我们缺乏用于中止迭代的break语句的等效方式。一个流可能很长，或者可能是无限的，如果我们没有理由继续处理它，我们会希望中断处理，而不是等待它直到处理完最后一个元素。

在本教程中，我们介绍一些允许我们在Stream.forEach操作上模拟break语句的机制。

## 2. Java9的Stream.takeWhile()

假设我们有一个字符串元素流，并且当字符串的长度是奇数时，我们希望处理该元素。

让我们看看Java 9中的Stream.takeWhile方法：

```java
Stream.of("cat", "dog", "elephant", "fox", "rabbit", "duck")
		.takeWhile(n -> n.length() % 2 != 0)
		.forEach(System.out::println);
```

如果我们运行它，得到的输出如下：

```plaintext
cat
dog
```

让我们将其与使用for循环和break语句的普通Java中的等效代码进行比较，以帮助我们了解其工作原理：

```java
public static void plainForLoopWithBreak() {
	List<String> list = asList("cat", "dog", "elephant", "fox", "rabbit", "duck");
	for (int i = 0; i < list.size(); i++) {
		String item = list.get(i);
		if (item.length() % 2 == 0) {
			break;
		}
		System.out.println(item);
	}
}
```

正如我们所见，takeWhile方法允许我们准确地实现我们所需要的。

但是，如果我们还没有升级到Java 9怎么办？我们如何使用Java 8实现类似的功能？

## 3. 自定义Spliterator

让我们创建一个自定义Spliterator，它将用作Stream.spliterator的装饰器。我们可以让这个Spliterator为我们执行中断。

首先，我们将从流中获取Spliterator，然后我们将使用CustomSpliterator装饰它并提供Predicate来控制中断操作。最后，我们将从CustomSpliterator创建一个新流：

```java
public static <T> Stream<T> takeWhile(Stream<T> stream, Predicate<T> predicate) {
	CustomSpliterator<T> customSpliterator = new CustomSpliterator<>(stream.spliterator(), predicate);
	return StreamSupport.stream(customSpliterator, false);
}
```

下面是CustomSpliterator：

```java
public class CustomSpliterator<T> extends Spliterators.AbstractSpliterator<T> {

	private final Spliterator<T> splitr;
	private final Predicate<T> predicate;
	private boolean isMatched = true;

	public CustomSpliterator(Spliterator<T> splitr, Predicate<T> predicate) {
		super(splitr.estimateSize(), 0);
		this.splitr = splitr;
		this.predicate = predicate;
	}

	@Override
	public boolean tryAdvance(Consumer<? super T> consumer) {
		boolean hadNext = splitr.tryAdvance(elem -> {
			if (predicate.test(elem) && isMatched) {
				consumer.accept(elem);
			} else {
				isMatched = false;
			}
		});
		return hadNext && isMatched;
	}
}
```

在tryAdvance方法中可以看到，自定义Spliterator处理装饰Spliterator的元素。只要我们的谓词匹配并且初始流仍然有元素，处理就完成了。当任一条件变为false时，我们的Spliterator“中断”并且流操作结束。

下面是对应的测试方法：

```java
@Test
void whenCustomTakeWhileIsCalled_ThenCorrectItemsAreReturned() {
	Stream<String> initialStream = Stream.of("cat", "dog", "elephant", "fox", "rabbit", "duck");
    
	List<String> result = CustomTakeWhile.takeWhile(initialStream, x -> x.length() % 2 != 0)
			.collect(Collectors.toList());
    
	assertEquals(asList("cat", "dog"), result);
}
```

正如我们所见，在满足条件后，流就停止了。出于测试目的，我们将结果收集到一个集合中，但我们也可以使用forEach调用或Stream的任何其他函数。

## 4. 自定义forEach

虽然提供嵌入中断机制的Stream可能很有用，但只关注forEach操作可能更简单。

让我们直接使用没有装饰器的Stream.spliterator：

```java
public class CustomForEach {

	public static class Breaker {
		private boolean shouldBreak = false;

		public void stop() {
			shouldBreak = true;
		}

		boolean get() {
			return shouldBreak;
		}
	}

	public static <T> void forEach(Stream<T> stream, BiConsumer<T, Breaker> consumer) {
		Spliterator<T> spliterator = stream.spliterator();
		boolean hadNext = true;
		Breaker breaker = new Breaker();

		while (hadNext && !breaker.get()) {
			hadNext = spliterator.tryAdvance(elem -> {
				consumer.accept(elem, breaker);
			});
		}
	}
}
```

正如我们所见，新的自定义forEach方法调用了BiConsumer，为我们的代码提供了下一个元素和可用于停止流的breaker对象。

下面是对应的测试方法：

```java
@Test
void whenCustomForEachIsCalled_ThenCorrectItemsAreReturned() {
	Stream<String> initialStream = Stream.of("cat", "dog", "elephant", "fox", "rabbit", "duck");
	List<String> result = new ArrayList<>();
    
	CustomForEach.forEach(initialStream, (elem, breaker) -> {
		if (elem.length() % 2 == 0) {
			breaker.stop();
		} else {
			result.add(elem);
		}
	});
    
	assertEquals(asList("cat", "dog"), result);
}
```

## 5. 总结

在本文中，我们介绍了提供等效于在流上调用break的方法。Java 9的takeWhile为我们解决了大部分问题，并且如果需要，我们也可以使用Java 8自己实现。最后，我们引出了一个工具方法，它可以在迭代Stream时为我们提供等效的中断操作 。
