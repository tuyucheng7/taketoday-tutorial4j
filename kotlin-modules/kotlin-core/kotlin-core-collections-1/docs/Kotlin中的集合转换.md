## 1. 简介

我们经常需要在Kotlin代码中使用集合，并且在很多情况下，我们需要能够将集合的元素或整个集合转换为其他形式。**Kotlin标准库为我们提供了许多内置的方法来实现这一点，以便我们可以更好地专注于我们的代码**。

## 2. 过滤值

**我们可以对集合执行的更基本的转换之一是确保所有元素都满足特定条件**，我们可以通过将一个集合转换为另一个集合并过滤所有元素以便仅匹配我们想要匹配的元素来执行此操作——本质上就像通过筛子运行集合一样。

基本过滤器调用将[lambda函数](https://www.baeldung.com/kotlin-lambda-expressions)作为谓词，将其应用于集合中的每个元素，并且仅在谓词返回true时才允许该元素通过：

```kotlin
val input = listOf(1, 2, 3, 4, 5)
val filtered = input.filter { it <= 3 }
assertEquals(listOf(1, 2, 3), filtered)
```

**我们可以使用任何方式为此提供函数，包括对另一个方法的引用**：

```kotlin
val filtered = input.filter(this::isSmall)
```

在某些情况下，我们需要能够扭转这一局面。一种选择是我们将调用包装在我们自己的lambda中并取反返回值，但是Kotlin也给了我们一个直接调用，我们可以用它来代替这个：

```kotlin
val large = input.filter { !isSmall(it) }
val alsoLarge = input.filterNot(this::isSmall)
assertEquals(listOf(4, 5), filtered)
```

在某些情况下，我们在过滤元素时可能还需要知道集合中的索引。为此，我们有filterIndexed方法，它将调用具有索引和元素的lambda：

```kotlin
val input = listOf(5, 4, 3, 2, 1)
val filtered = input.filterIndexed { index, element -> index < 3 }
assertEquals(listOf(5, 4, 3), filtered)
```

**我们还可以使用一些特殊的过滤方法，这些方法将在途中巧妙地改变集合**。我们有一个用于将[可空值](https://www.baeldung.com/kotlin-null-safety)的集合转换为有保证的非空值，并且我们可以将值的集合转换为子类型：

```kotlin
val nullable: List<String?> = ...
val nonnull: List<String> = nullable.filterNotNull()

val superclasses: List<Superclass> = ...
val subclasses: List<Subclass> = superclasses.filterIsInstance<Subclass>()
```

请注意，这些通过确保集合的内容满足某些要求来转换集合本身的类型。

## 3. 映射值

我们刚刚看到了如何获取一个集合并过滤掉不符合我们需求的元素，**我们还可以获取一个集合并将值从一个值转换(或映射)到另一个**。此映射根据需要简单或复杂：

```kotlin
val input = listOf("one", "two", "three")

val reversed = input.map { it.reversed() }
assertEquals(listOf("eno", "owt", "eerht"), reversed) 

val lengths = input.map { it.length }
assertEquals(listOf(3, 3, 5), lengths)
```

其中第一个将String转换为不同的String，其中字符被反转。第二个将String转换为完全不同的类型，表示String中的字符数。对于我们的代码，这两者的工作方式完全相同。

与过滤一样，我们还有一个版本可以知道集合中元素的索引：

```kotlin
val input = listOf(3, 2, 1)
val result = input.mapIndexed { index, value -> index * value }
assertEquals(listOf(0, 2, 2), result)
```

**在某些情况下，我们会有一个映射函数，它可能会返回null值并需要删除这些**。我们已经看到我们可以通过在调用后链接filterNotNull来做到这一点，但Kotlin实际上以mapNotNull和mapIndexedNotNull的形式为我们提供了这个内置函数：

```kotlin
val input = listOf(1, 2, 3, 4, 5)
val smallSquares = input.mapNotNull { 
    if (it <= 3) {
        it * it
    } else {
        null
    }
}
assertEquals(listOf(1, 4, 9), smallSquares)
```

### 3.1 转换Map

到目前为止，我们所看到的一切都适用于标准Java集合，例如List和Set。**我们也有一些可以专门应用于Map类型，允许我们转换map的键或值**。在这两种情况下，我们的lambda都是用Map.Entry<>调用的，它表示映射中的条目，但返回值将决定转换的内容：

```kotlin
val inputs = mapOf("one" to 1, "two" to 2, "three" to 3)

val squares = inputs.mapValues { it.value * it.value }
assertEquals(mapOf("one" to 1, "two" to 4, "three" to 9), squares)

val uppercases = inputs.mapKeys { it.key.toUpperCase() }
assertEquals(mapOf("ONE" to 1, "TWO" to 2, "THREE" to 3), uppercases)
```

## 4. 扁平化集合

我们已经了解了如何通过映射单个值来转换集合，这通常用于将一个简单值映射到另一个，但它也可用于将简单值映射到值集合中：

```kotlin
val inputs = listOf("one", "two", "three")
val characters = inputs.map(String::toList)
assertEquals(listOf(listOf('o', 'n', 'e'), listOf('t', 'w', 'o'), 
  listOf('t', 'h', 'r', 'e', 'e')), characters)
```

如果我们愿意，**我们可以使用flatten方法将其转换为单个列表而不是嵌套列表**：

```kotlin
val flattened = characters.flatten()
assertEquals(listOf('o', 'n', 'e', 't', 'w', 'o', 't', 'h', 'r', 'e', 'e'), flattened)
```

除此之外，**因为两者经常在一起，所以我们有了flatMap方法**。这可以被认为是内置在单个方法调用中的map和flatten的组合，传递给它的lambda需要返回一个Collection<T\>而不仅仅是一个T，并且它将构建一个扁平值的单个Collection<T\>：

```kotlin
val inputs = listOf("one", "two", "three")
val characters = inputs.flatMap(String::toList)
assertEquals(listOf('o', 'n', 'e', 't', 'w', 'o', 't', 'h', 'r', 'e', 'e'), characters)
```

## 5. 压缩集合

到目前为止，我们已经看到了几种用于转换单个集合的工具，或者通过过滤掉不符合条件的值，或者通过转换集合内的值。

**在某些情况下，我们想要执行一个转换，将两个不同的集合组合在一起以生成一个集合**。这被称为将两个集合压缩在一起，从每个集合中获取元素并生成一个对列表：

```kotlin
val left = listOf("one", "two", "three")
val right = listOf(1, 2, 3)
val zipped = left.zip(right)
assertEquals(listOf(Pair("one", 1), Pair("two", 2), Pair("three", 3)), zipped)
```

新列表包含Pair<L, R>元素，由左侧列表中的一个元素和右侧列表中的一个元素组成。

在某些情况下，列表的长度不同。在这种情况下，**结果列表与最短输入列表的长度相同**：

```kotlin
val left = listOf("one", "two")
val right = listOf(1, 2, 3)
val zipped = left.zip(right)
assertEquals(listOf(Pair("one", 1), Pair("two", 2)), zipped)
```

**由于这本身返回一个集合，因此我们可以对其执行任何其他转换**，包括过滤和映射它们。这将作用于这个新列表中的Pair<L, R>元素，它允许一些有趣的功能：

```kotlin
val posts = ...
posts.map(post -> authorService.getAuthor(post.author)) // Returns a collection of authors
    .zip(posts) // Returns a collection of Pair<Author, Post>
    .map((author, post) -> "Post ${post.title} was written by ${author.name}")
```

有时我们需要朝另一个方向前进-获取Collection<Pair<L, R>>，并将其转换回两个列表。这被称为解压缩集合并从字面上将Collection<Pair<L, R>>转换为Pair<List<L\>, List<R\>>：

```kotlin
val left = listOf("one", "two", "three")
val right = listOf(1, 2, 3)
val zipped = left.zip(right)

val (newLeft, newRight) = zipped.unzip()
assertEquals(left, newLeft)
assertEquals(right, newRight)
```

## 6. 将集合转换为Map

**到目前为止，我们所看到的一切都将集合转换为相同的类型，并且只是操作集合中的数据**。不过，在某些情况下，我们也可以通过多种机制将集合转换为Map<K, V>。

最简单的方法是使用toMap()方法直接将Collection<Pair<L, R>>转换为Map<L, R>。在这种情况下，我们的集合已经包含所有Map条目，但它的结构不是Map：

```kotlin
val input = listOf(Pair("one", 1), Pair("two", 2))
val map = input.toMap()
assertEquals(mapOf("one" to 1, "two" to 2), map)
```

我们如何得到我们的Collection<Pair<L, R>>完全取决于我们-例如，它可以直接构建，但也可以是map或zip操作的结果。

**我们还可以通过将我们集合中的值与来自某处的其他值相关联来一起执行其中的一些操作**。我们在这里可以选择将集合元素视为Map键、Map值，或者作为生成键和值的某种方式：

```kotlin
val inputs = listOf("Hi", "there")

// Collection elements as keys
val map = inputs.associateWith { k -> k.length }
assertEquals(mapOf("Hi" to 2, "there" to 5), map)

// Collection elements as values
val map = inputs.associateBy { v -> v.length }
assertEquals(mapOf(2 to "Hi", 5 to "there"), map)

// Collection elements generate key and value
val map = inputs.associate { e -> Pair(e.toUpperCase(), e.reversed()) }
assertEquals(mapOf("HI" to "iH", "THERE" to "ereht"), map)
```

在这种情况下，重复项会被过滤掉。集合中任何会产生相同Map键的元素都只会导致最后一个元素出现在Map中：

```kotlin
val inputs = listOf("one", "two")
val map = inputs.associateBy { v -> v.length }
assertEquals(mapOf(3 to "two"), map)
```

**如果我们想保留所有重复项以便我们知道映射在一起的每个实例，那么我们可以改用groupBy方法**。它们返回一个Map<K, List<V\>>，其中Map值是映射到同一键的整个元素列表：

```kotlin
val inputs = listOf("one", "two", "three")
val map = inputs.groupBy { v -> v.length }
assertEquals(mapOf(3 to listOf("one", "two"), 5 to listOf("three")), map)
```

## 7. 将集合连接成字符串

**我们可以将集合中的所有元素连接到一个String中**，而不是转换为另一个集合或Map。

执行此操作时，我们可以选择提供值以用于元素之间的分隔符、新字符串开头的前缀和字符串末尾的后缀：

```kotlin
val inputs = listOf("Jan", "Feb", "Mar", "Apr", "May")

val simpleString = inputs.joinToString()
assertEquals("Jan, Feb, Mar, Apr, May", simpleString)

val detailedString = inputs.joinToString(separator = ",", prefix="Months: ", postfix=".") 
assertEquals("Months: Jan,Feb,Mar,Apr,May.", detailedString)
```

可以看到，省略前缀和后缀就是空串，省略分隔符就是字符串“,”。

我们还可以指定要组合的元素数量的限制。这样做时，如果我们确实限制了字符串，我们还可以指定一个截断后缀：

```kotlin
val inputs = listOf("Jan", "Feb", "Mar", "Apr", "May")

val simpleString = inputs.joinToString(limit = 3)
assertEquals("Jan, Feb, Mar, ...", simpleString)
```

**此调用还具有转换元素的内置功能**，这个功能与我们在joinToString调用之前调用map完全相同，但只需要转换实际包含的元素——换句话说，任何超过limit调用的元素都不会被转换：

```kotlin
val inputs = listOf("Jan", "Feb", "Mar", "Apr", "May")

val simpleString = inputs.joinToString(transform = String::toUpperCase)
assertEquals("JAN, FEB, MAR, APR, MAY", simpleString)
```

**我们还可以访问此调用的另一个版本，它将输出字符串写入一个Appendable实例**-例如，一个StringBuilder。这可以让我们将一个集合加入到我们正在构建的字符串的中间-本质上是作为前缀和后缀参数的更强大版本：

```kotlin
val inputs = listOf("Jan", "Feb", "Mar", "Apr", "May")

val output = StringBuilder()
output.append("My ")
    .append(inputs.size)
    .append(" elements: ")
inputs.joinTo(output)
assertEquals("My 5 elements: Jan, Feb, Mar, Apr, May", output.toString())
```

## 8. 将集合归约为值

上面，我们看到了如何执行特定操作以将集合组合成单个值-将所有值连接成一个字符串。

**我们可以使用一个更强大的替代方法，称为归约集合**。这是通过提供一个lambda来实现的，该lambda知道如何将集合中的下一个元素组合到我们到目前为止的累积值上。最后，我们得到总的累加值。

例如，我们可以使用此功能来实现一个简单版本的joinToString：

```kotlin
val inputs = listOf("Jan", "Feb", "Mar", "Apr", "May")
val result = inputs.reduce { acc, next -> "$acc, $next" }
assertEquals("Jan, Feb, Mar, Apr, May", result)
```

这使用数组中的第一个元素作为我们的初始累加值，并调用我们的lambda将每个后续值组合到其中：

-   调用1 – acc = “Jan”, next = “Feb”
-   调用2 – acc = “Jan, Feb”, next = “Mar”
-   调用3 – acc = “Jan, Feb, Mar”, next = “Apr”
-   调用4 – acc = “Jan, Feb, Mar, Apr”, next = “May”

这仅适用于我们的元素与所需输出类型相同的情况，它也只适用于包含至少一个元素的集合。

**另一种选择是fold方法，它几乎相同，但我们提供了一个额外的参数作为初始累加值**。通过这样做，我们可以让我们的累加值成为我们想要的任何类型，并且我们可以支持任何大小的集合——包括空集合：

```kotlin
val inputs = listOf("Jan", "Feb", "Mar", "Apr", "May")
val result = inputs.fold(0) { acc, next -> acc + next.length }
assertEquals(15, totalLength)
```

这次我们的lambda将被调用五次-对列表中的每个元素调用一次，并且初始累加值为0。

## 9. 总结

我们已经看到了Kotlin标准库中内置的各种工具，用于以各种方式操作集合。