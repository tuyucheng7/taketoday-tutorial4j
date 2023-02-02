## 1. 概述

在本教程中，我们将回顾Java 16中引入的Stream::mapMulti方法，我们将编写简单的示例来说明如何使用它。特别是，**我们会看到此方法类似于Stream::flatMap，因此需要知道在什么情况下我们更倾向于使用mapMulti而不是flatMap**。

请务必查看我们关于[Java Streams](https://www.baeldung.com/java-streams)的文章，以更深入地了解Stream API。

## 2. 方法签名

省略了通配符，mapMulti方法可以写得更简洁：

```java
<R> Stream<R> mapMulti(BiConsumer<T, Consumer<R>> mapper)
```

这是一个流中间操作，它需要将BiConsumer函数接口的实现作为参数。**BiConsumer的实现接收Stream元素T，如有必要，将其转换为类型R，并调用映射器的Consumer::accept**。

在Java的mapMulti方法实现中，映射器是一个实现了Consumer函数接口的缓冲区。

每次我们调用Consumer::accept时，它都会在缓冲区中累积元素并将它们传递到流管道。

## 3. 简单实现示例

让我们考虑一个整数集合来执行以下操作：

```java
List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
double percentage = .01;
List<Double> evenDoubles = integers.stream()
	.<Double>mapMulti((integer, consumer) -> {
		if (integer % 2 == 0) {
			consumer.accept((double) integer * (1 + percentage));
		}
	})
	.collect(toList());
assertThat(evenDoubles).containsAll(Arrays.asList(2.02D, 4.04D));
```

在BiConsumer<T, Consumer<R\>>映射器的lambda实现中，我们首先过滤奇数，然后将剩余的偶数加上自身的百分比，将结果转换为Double数，并完成调用consumer.accept。

正如我们之前看到的，消费者只是一个将返回元素传递到流管道的缓冲区。(作为补充，请注意我们必须使用类型见证<Double\>mapMulti作为返回值，否则编译器无法在方法签名中推断出R的正确类型)

这是一对零或一对一的转换，具体取决于元素是奇数还是偶数。

请注意，前面代码示例中的if语句充当Stream::filter的角色，并将整数转换为double数，即Stream::map的角色。因此，我们可以使用Stream的filter和map来达到相同的效果：

```java
List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
double percentage = .01;
List<Double> evenDoubles = integers.stream()
	.filter(integer -> integer % 2 == 0)
	.<Double>map(integer -> ((double) integer * (1 + percentage)))
	.collect(toList());
assertThat(evenDoubles).containsAll(Arrays.asList(2.02D, 4.04D));
```

但是，**mapMulti实现更直接，因为我们不需要调用那么多流中间操作**。

**另一个优点是mapMulti的实现是命令式的，让我们可以更自由地进行元素转换**。

为了支持int、long和double基本类型，我们可以使用mapMultiToDouble、mapMultiToInt和mapMultiToLong这些mapMulti方法的变体。

例如，我们可以使用mapMultiToDouble求出前面的double集合的总和：

```java
List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
double percentage = .01;
double sum = integers.stream()
	.mapMultiToDouble((integer, consumer) -> {
		if (integer % 2 == 0) {
			consumer.accept(integer * (1 + percentage));
		}
	})
	.sum();
assertThat(sum).isEqualTo(12.12, offset(0.001d));
```

## 4. 更现实的例子

考虑一个Album(专辑)集合：

```java
public class Album {

    private String albumName;
    private int albumCost;
    private List<Artist> artists;

    Album(String albumName, int albumCost, List<Artist> artists) {
        this.albumName = albumName;
        this.albumCost = albumCost;
        this.artists = artists;
    }
    // ...
}
```

每个Album都有一个Artists(作家)列表：

```java
public class Artist {

    private final String name;
    private boolean associatedMajorLabels;
    private List<String> majorLabels;

    Artist(String name, boolean associatedMajorLabels, List<String> majorLabels) {
        this.name = name;
        this.associatedMajorLabels = associatedMajorLabels;
        this.majorLabels = majorLabels;
    }
    // ...
}
```

如果我们想要收集作家-专辑名称对的集合，我们可以使用mapMulti来实现：

```java
List<Pair<String, String>> artistAlbum = albums.stream()
	.<Pair<String, String>>mapMulti((album, consumer) -> {
		for (Artist artist : album.getArtists()) {
			consumer.accept(new ImmutablePair<String, String>(artist.getName(), album.getAlbumName()));
		}
	})
	.collect(toList());
```

对于流中的每个album，我们遍历artists，创建作家-专辑名称对的Apache Commons ImmutablePair，并调用Consumer::accept。mapMulti的实现累积消费者接收的元素并将它们传递给流管道。

这具有一对多转换的效果，其中结果在消费者中累积，但最终被扁平化为一个新的流。这本质上就是Stream::flatMap所做的，也就是说我们可以通过以下实现完成相同的效果：

```java
List<Pair<String, String>> artistAlbum = albums.stream()
	.flatMap(album -> album.getArtists()
		.stream()
		.map(artist -> new ImmutablePair<String, String>(artist.getName(), album.getAlbumName())))
	.collect(toList());
```

我们看到这两种方法给出了相同的结果，接下来我们将介绍在哪些情况下使用mapMulti更为有利。

## 5. 何时使用mapMulti而不是flatMap

### 5.1 用少量元素替换流元素

正如Java文档中所述：“当用少量(可能为零)元素替换每个流元素时，使用此方法可以避免为每组结果元素创建新Stream实例的开销，这是flatMap所要求的”。

让我们写一个简单的例子来说明这个场景：

```java
int upperCost = 9;
List<Pair<String, String>> artistAlbum = albums.stream()
	.<Pair<String, String>>mapMulti((album, consumer) -> {
		if (album.getAlbumCost() < upperCost) {
			for (Artist artist : album.getArtists()) {
				consumer.accept(new ImmutablePair<String, String>(artist.getName(), album.getAlbumName()));
			}
		}
	})
	.collect(toList());
```

对于每个album，我们迭代artists并累积零个或几个artist-album对，具体取决于album.getAlbumCost() < upperCost的比较结果。

要使用flatMap实现相同的结果，请执行以下操作：

```java
int upperCost = 9;
List<Pair<String, String>> artistAlbum = albums.stream()
	.flatMap(album -> album.getArtists()
		.stream()
		.filter(artist -> upperCost > album.getAlbumCost())
		.map(artist -> new ImmutablePair<String, String>(artist.getName(), album.getAlbumName())))
	.collect(toList());
```

**我们看到mapMulti的命令式实现性能更好，我们不必像使用flatMap的声明式方法那样为每个处理过的元素创建中间流**。

### 5.2 何时更容易生成结果元素

让我们在Album类中编写一个方法，将所有artist-album对及其关联的主要标签传递给consumer：

```java
public class Album {

    //...
    public void artistAlbumPairsToMajorLabels(Consumer<Pair<String, String>> consumer) {

        for (Artist artist : artists) {
            if (artist.isAssociatedMajorLabels()) {
                String concatLabels = artist.getMajorLabels().stream().collect(Collectors.joining(","));
                consumer.accept(new ImmutablePair<>(artist.getName() + ":" + albumName, concatLabels));
            }
        }
    }
    // ...
}
```

如果artist与主要标签有关联，则该实现会将标签连接成逗号分隔的字符串。然后它创建一对带有标签的artist-album名称并调用Consumer::accept。

如果我们想要获得所有对的集合，只需使用mapMulti和方法引用Album::artistAlbumPairsToMajorLabels：

```java
List<Pair<String, String>> copyrightedArtistAlbum = albums.stream()
    .<Pair<String, String>> mapMulti(Album::artistAlbumPairsToMajorLabels)
    .collect(toList());
```

我们看到，在更复杂的情况下，我们可以有非常复杂的方法引用实现。例如，[Java文档](https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/util/stream/Stream.html#mapMulti(java.util.function.BiConsumer))给出了一个使用递归的示例。

通常，使用flatMap实现相同的结果将非常困难。因此，**在生成结果元素比按照flatMap要求以Stream的形式返回它们要容易得多的情况下，我们应该使用mapMulti**。

## 6. 总结

在本教程中，我们通过不同示例介绍了mapMulti的使用方法，了解了它与flatMap的区别以及何时使用它更有优势。

特别是，当需要替换一些流元素或者使用命令式方法生成流管道的元素更容易时，建议使用mapMulti 。