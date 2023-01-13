## 1. 概述

在本教程中，我们将讨论[Jackson](https://www.baeldung.com/jackson-annotations)对 Kotlin 的支持。

我们将探讨如何序列化和反序列化Object和Collection。我们还将使用@JsonProperty和@JsonInclude注解。

## 2.Maven配置

首先，我们需要将jackson-module-kotlin依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.module</groupId>
    <artifactId>jackson-module-kotlin</artifactId>
    <version>2.9.8</version>
</dependency>
```

可以在 Maven Central 上找到最新版本的[jackson-module-kotlin 。](https://search.maven.org/search?q=a:jackson-module-kotlin AND g:com.fasterxml.jackson.module)

## 3.对象序列化

让我们从对象序列化开始。

这里我们有一个简单的数据Movie类，我们将在示例中使用它：

```java
data class Movie(
  var name: String,
  var studio: String,
  var rating: Float? = 1f)
```

为了序列化和反序列化对象，我们需要有一个用于 Kotlin的ObjectMapper实例。

我们可以使用jacksonObjectMapper()创建一个：

```java
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

val mapper = jacksonObjectMapper()
```

或者我们可以创建一个ObjectMapper然后注册KotlinModule：

```java
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

val mapper = ObjectMapper().registerModule(KotlinModule())
```

现在我们有了映射器，让我们用它来序列化一个简单的Movie对象。

我们可以使用writeValueAsString()方法将对象序列化为 JSON 字符串：

```java
@Test
fun whenSerializeMovie_thenSuccess() {
    val movie = Movie("Endgame", "Marvel", 9.2f)
    val serialized = mapper.writeValueAsString(movie)
    
    val json = """
      {
        "name":"Endgame",
        "studio":"Marvel",
        "rating":9.2
      }"""
    assertEquals(serialized, json)
}
```

## 4.对象反序列化

接下来，我们将使用映射器将 JSON 字符串反序列化为Movie实例。

我们将使用方法readValue()：

```java
@Test
fun whenDeserializeMovie_thenSuccess() {
    val json = """{"name":"Endgame","studio":"Marvel","rating":9.2}"""
    val movie: Movie = mapper.readValue(json)
    
    assertEquals(movie.name, "Endgame")
    assertEquals(movie.studio, "Marvel")
    assertEquals(movie.rating, 9.2f)
}
```

请注意，我们不需要为readValue()方法提供TypeReference；我们只需要指定变量类型。

我们还可以用不同的方式指定类类型：

```java
val movie = mapper.readValue<Movie>(json)
```

在反序列化时，如果JSON字符串中缺少某个字段，则映射器将使用我们类中为该字段声明的默认值。

这里我们的 JSON 字符串缺少评级字段，因此使用默认值1：

```java
@Test
fun whenDeserializeMovieWithMissingValue_thenUseDefaultValue() {
    val json = """{"name":"Endgame","studio":"Marvel"}"""
    val movie: Movie = mapper.readValue(json)
    
    assertEquals(movie.name, "Endgame")
    assertEquals(movie.studio, "Marvel")
    assertEquals(movie.rating, 1f)
}
```

### 4.1. 缺少必需的参数

正如我们所知，我们不能将 null 值传递给 [Kotlin中的](https://www.baeldung.com/kotlin/null-safety)[不可为 null 的 ](https://www.baeldung.com/kotlin/null-safety)类型。例如， Movie 类中的name 和 studio 参数 由于其类型不接受 空 值。

因此，杰克逊应该在反序列化过程中以某种方式处理这个问题。事实上，当 JSON 字符串中缺少一个必需的属性时，Jackson 将无法将该 JSON 反序列化为目标 Kotlin 类型。[为了表示这种失败，它使用了一个名为MissingKotlinParameterException](https://github.com/FasterXML/jackson-module-kotlin/blob/a4efc61a351e3ddfd6d855c2e18903cbe224548f/src/main/kotlin/com/fasterxml/jackson/module/kotlin/Exceptions.kt#L12)的特殊异常：

```kotlin
@Test
fun whenMissingRequiredParameterOnDeserialize_thenFails() {
    val json = """{"studio":"Marvel","rating":9.2}""" // name is missing
    val exception = assertThrows<MissingKotlinParameterException> { mapper.readValue<Movie>(json) }
    assertEquals("name", exception.parameter.name)
    assertEquals(String::class, exception.parameter.type.classifier)
}
```

如上所示，异常使用其 参数属性描述了缺少的参数。在这里，我们正在检查属性类型和名称。

## 5.使用地图

接下来，我们将看到如何使用 Jackson序列化和反序列化Map 。

这里我们将序列化一个简单的Map<Int, String>：

```java
@Test
fun whenSerializeMap_thenSuccess() {
    val map = mapOf(1 to "one", 2 to "two")
    val serialized = mapper.writeValueAsString(map)
    
    val json = """
      {
        "1":"one",
        "2":"two"
      }"""
    assertEquals(serialized, json)
}
```

接下来，当我们反序列化地图时，我们需要确保指定键和值类型：

```java
@Test
fun whenDeserializeMap_thenSuccess() {
    val json = """{"1":"one","2":"two"}"""
    val aMap: Map<Int,String> = mapper.readValue(json)
    
    assertEquals(aMap[1], "one")
    assertEquals(aMap[2], "two")
}
```

## 6.使用Collection

现在，我们将了解如何在 Kotlin 中序列化集合。

这里我们有一个我们想要序列化为 JSON 字符串的电影列表：

```java
@Test
fun whenSerializeList_thenSuccess() {
    val movie1 = Movie("Endgame", "Marvel", 9.2f)
    val movie2 = Movie("Shazam", "Warner Bros", 7.6f)
    val movieList = listOf(movie1, movie2)
    val serialized = mapper.writeValueAsString(movieList)
    
    val json = """
      [
        {
          "name":"Endgame",
          "studio":"Marvel",
          "rating":9.2
        },
        {
          "name":"Shazam",
          "studio":"Warner Bros",
          "rating":7.6
        }
      ]"""
    assertEquals(serialized, json)
}
```

现在当我们反序列化一个List时，我们需要提供对象类型Movie——就像我们对Map所做的那样：

```java
@Test
fun whenDeserializeList_thenSuccess() {
    val json = """[{"name":"Endgame","studio":"Marvel","rating":9.2}, 
      {"name":"Shazam","studio":"Warner Bros","rating":7.6}]"""
    val movieList: List<Movie> = mapper.readValue(json)
        
    val movie1 = Movie("Endgame", "Marvel", 9.2f)
    val movie2 = Movie("Shazam", "Warner Bros", 7.6f)
    assertTrue(movieList.contains(movie1))
    assertTrue(movieList.contains(movie2))
}
```

## 7.更改字段名称

接下来，我们可以使用@JsonProperty annotation在序列化和反序列化期间更改字段名称。

在此示例中，我们将authorName字段重命名为Book数据类的作者：

```java
data class Book(
  var title: String,
  @JsonProperty("author") var authorName: String)
```

现在，当我们序列化一个Book对象时，使用author而不是authorName：

```java
@Test
fun whenSerializeBook_thenSuccess() {
    val book = Book("Oliver Twist", "Charles Dickens")
    val serialized = mapper.writeValueAsString(book)
    
    val json = """
      {
        "title":"Oliver Twist",
        "author":"Charles Dickens"
      }"""
    assertEquals(serialized, json)
}
```

反序列化也是如此：

```java
@Test
fun whenDeserializeBook_thenSuccess() {
    val json = """{"title":"Oliver Twist", "author":"Charles Dickens"}"""
    val book: Book = mapper.readValue(json)
    
    assertEquals(book.title, "Oliver Twist")
    assertEquals(book.authorName, "Charles Dickens")
}
```

## 8.排除空白字段

最后，我们将讨论如何从序列化中排除空字段。

让我们在Book类中添加一个名为genres的新字段。该字段默认初始化为emptyList()：

```java
data class Book(
  var title: String,
  @JsonProperty("author") var authorName: String) {
    var genres: List<String>? = emptyList()
}
```

默认情况下，所有字段都包含在序列化中——即使它们为null或空：

```java
@Test
fun whenSerializeBook_thenSuccess() {
    val book = Book("Oliver Twist", "Charles Dickens")
    val serialized = mapper.writeValueAsString(book)
    
    val json = """
      {
        "title":"Oliver Twist",
        "author":"Charles Dickens",
        "genres":[]
      }"""
    assertEquals(serialized, json)
}
```

我们可以使用@JsonInclude注解从 JSON 中排除空字段：

```java
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Book(
  var title: String, 
  @JsonProperty("author") var authorName: String) {
    var genres: List<String>? = emptyList()
}
```

这将排除null、空Collection、空Map、长度为零的数组等字段：

```java
@Test
fun givenJsonInclude_whenSerializeBook_thenEmptyFieldExcluded() {
    val book = Book("Oliver Twist", "Charles Dickens")
    val serialized = mapper.writeValueAsString(book)
    
    val json = """
      {
        "title":"Oliver Twist",
        "author":"Charles Dickens"
      }"""
    assertEquals(serialized, json)
}
```

## 9.总结

在本文中，我们学习了如何使用 Jackson 在 Kotlin 中序列化和反序列化对象。

我们还学习了如何使用@JsonProperty和@JsonInclude注解。