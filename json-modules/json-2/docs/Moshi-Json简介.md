## 1. 简介

在本教程中，我们将了解[Moshi](https://github.com/square/moshi)，这是一个用于Java的现代 JSON 库，它将为我们的代码提供强大的 JSON 序列化和反序列化，而且不费吹灰之力。

在不影响功能的情况下，Moshi 的 API 比 Jackson 或 Gson 等其他库更小。这使得它更容易集成到我们的应用程序中，并让我们编写更多可测试的代码。它也是一个较小的依赖项，这对于某些场景可能很重要——比如为 Android 开发。

## 2. 将 Moshi 添加到我们的构建中

在我们使用它之前，我们首先需要将[Moshi JSON 依赖](https://search.maven.org/search?q=g:com.squareup.moshi)项添加到我们的 pom.xml文件中：

```xml
<dependency>
    <groupId>com.squareup.moshi</groupId>
    <artifactId>moshi</artifactId>
    <version>1.9.2</version>
</dependency>
<dependency>
    <groupId>com.squareup.moshi</groupId>
    <artifactId>moshi-adapters</artifactId>
    <version>1.9.2</version>
</dependency>
```

com.squareup.moshi :moshi依赖项是主库，而 com.squareup.moshi:moshi-adapters依赖项是一些标准类型的适配器——稍后我们将对其进行更详细的探讨。

## 3. 使用 Moshi 和 JSON

Moshi 允许我们将任何Java值转换为 JSON 并在我们需要的任何地方再次转换回来，无论出于何种原因——例如文件存储、编写 REST API，无论我们可能有什么需求。

Moshi 使用JsonAdapter类的概念。这是一种类型安全机制，用于将特定类序列化为 JSON 字符串并将 JSON 字符串反序列化回正确的类型：

```java
public class Post {
    private String title;
    private String author;
    private String text;
    // constructor, getters and setters
}

Moshi moshi = new Moshi.Builder().build();
JsonAdapter<Post> jsonAdapter = moshi.adapter(Post.class);
```

一旦我们构建了 JsonAdapter，我们就可以在任何需要的时候使用它，以便使用toJson()方法将我们的值转换为 JSON：

```java
Post post = new Post("My Post", "Baeldung", "This is my post");
String json = jsonAdapter.toJson(post);
// {"author":"Baeldung","text":"This is my post","title":"My Post"}
```

当然，我们可以使用相应的 fromJson()方法将 JSON 转换回预期的Java类型：

```java
Post post = jsonAdapter.fromJson(json);
// new Post("My Post", "Baeldung", "This is my post");
```

## 4. 标准Java类型

Moshi 内置了对标准Java类型的支持，可以完全按照预期与 JSON 进行相互转换。这包括：

-   [所有原语](https://www.baeldung.com/java-primitives)- int、float、char等。
-   [所有Java盒装等价物](https://www.baeldung.com/java-primitives-vs-objects)– Integer、Float、Character等。
-   [细绳](https://www.baeldung.com/java-string)
-   [枚举](https://www.baeldung.com/a-guide-to-java-enums)
-   这些类型的[数组](https://www.baeldung.com/java-arrays-guide)
-   这些类型的[标准Java集合](https://www.baeldung.com/java-collections)——List、Set、Map

除了这些，Moshi 还会自动处理任意Javabean，将其转换为 JSON 对象，其中的值使用与任何其他类型相同的规则进行转换。这显然意味着Javabean 中的Javabean 已正确序列化到我们需要的深度。

moshi-adapters依赖项让我们可以访问一些额外的转换规则，包括： 

-   一个稍微更强大的枚举适配器——在从 JSON 读取未知值时支持回退值
-   支持[RFC-3339 格式的](https://tools.ietf.org/html/rfc3339)java.util.Date 适配器

对这些的支持需要在使用前向Moshi实例注册。当我们添加对我们自己的自定义类型的支持时，我们很快就会看到这个确切的模式：

```java
Moshi moshi = new Moshi.builder()
  .add(new Rfc3339DateJsonAdapter())
  .add(CurrencyCode.class, EnumJsonAdapter.create(CurrencyCode.class).withUnknownFallback(CurrencyCode.USD))
  .build()
```

## 5. Moshi 中的自定义类型

到目前为止，我们完全支持将任何Java对象序列化和反序列化为 JSON 并反序列化。但这并没有让我们对 JSON 的外观有太多控制，通过按原样在对象中逐字写入每个字段来序列化Java对象。这行得通，但并不总是我们想要的。

相反，我们可以为自己的类型编写自己的适配器，并精确控制这些类型的序列化和反序列化的工作方式。

### 5.1. 简单转换

简单的情况是在Java类型和 JSON 类型之间进行转换——例如字符串。当我们需要以特定格式表示复杂数据时，这会非常有用。

例如，假设我们有一个表示帖子作者的Java类型：

```java
public class Author {
    private String name;
    private String email;
    // constructor, getters and setters
}
```

毫不费力，这将序列化为包含两个字段的 JSON 对象—— 名称和 电子邮件。不过，我们希望将其序列化为单个字符串，将名称和电子邮件地址组合在一起。

我们通过编写一个标准类来实现这一点，该类包含一个用 @ToJson注解的方法：

```java
public class AuthorAdapter {
    @ToJson
    public String toJson(Author author) {
        return author.name + " <" + author.email + ">";
    }
}
```

显然，我们也需要走另一条路。我们需要将我们的字符串解析回我们的 Author对象。这是通过添加一个用 @FromJson注解的方法来完成的：

```java
@FromJson
public Author fromJson(String author) {
    Pattern pattern = Pattern.compile("^(.) <(.)>$");
    Matcher matcher = pattern.matcher(author);
    return matcher.find() ? new Author(matcher.group(1), matcher.group(2)) : null;
}
```

完成后，我们需要实际使用它。我们在创建 Moshi时通过将适配器添加到我们的 Moshi.Builder来执行此操作：

```java
Moshi moshi = new Moshi.Builder()
  .add(new AuthorAdapter())
  .build();
JsonAdapter<Post> jsonAdapter = moshi.adapter(Post.class);
```

现在我们可以立即开始将这些对象与 JSON 相互转换，并获得我们想要的结果：

```java
Post post = new Post("My Post", new Author("Baeldung", "baeldung@example.com"), "This is my post");
String json = jsonAdapter.toJson(post);
// {"author":"Baeldung <baeldung@example.com>","text":"This is my post","title":"My Post"}

Post post = jsonAdapter.fromJson(json);
// new Post("My Post", new Author("Baeldung", "baeldung@example.com"), "This is my post");
```

### 5.2. 复杂的转换

这些转换是在Javabean 和 JSON 原始类型之间进行的。我们也可以转换为结构化 JSON——本质上让我们将Java类型转换为不同的结构以在我们的 JSON 中呈现。

例如，我们可能需要将日期/时间值呈现为三个不同的值——日期、时间和时区。

使用 Moshi，我们需要做的就是编写一个表示所需输出的Java类型，然后我们的@ToJson方法可以返回这个新的Java对象，然后 Moshi 将使用其标准规则将其转换为 JSON：

```java
public class JsonDateTime {
    private String date;
    private String time;
    private String timezone;

    // constructor, getters and setters
}
public class JsonDateTimeAdapter {
    @ToJson
    public JsonDateTime toJson(ZonedDateTime input) {
        String date = input.toLocalDate().toString();
        String time = input.toLocalTime().toString();
        String timezone = input.getZone().toString();
        return new JsonDateTime(date, time, timezone);
    }
}
```

正如我们所料，另一种方式是通过编写一个 @FromJson方法来完成，该方法采用我们新的 JSON 结构类型并返回我们想要的类型：

```java
@FromJson
public ZonedDateTime fromJson(JsonDateTime input) {
    LocalDate date = LocalDate.parse(input.getDate());
    LocalTime time = LocalTime.parse(input.getTime());
    ZoneId timezone = ZoneId.of(input.getTimezone());
    return ZonedDateTime.of(date, time, timezone);
}
```

然后我们可以像上面一样使用它来将我们的 ZonedDateTime转换为我们的结构化输出并返回：

```java
Moshi moshi = new Moshi.Builder()
  .add(new JsonDateTimeAdapter())
  .build();
JsonAdapter<ZonedDateTime> jsonAdapter = moshi.adapter(ZonedDateTime.class);

String json = jsonAdapter.toJson(ZonedDateTime.now());
// {"date":"2020-02-17","time":"07:53:27.064","timezone":"Europe/London"}

ZonedDateTime now = jsonAdapter.fromJson(json);
// 2020-02-17T07:53:27.064Z[Europe/London]
```

### 5.3. 替代型适配器

有时我们想为单个字段使用替代适配器，而不是基于字段类型。

例如，我们可能有一个案例，我们需要将日期和时间呈现为距纪元的毫秒数，而不是 ISO-8601 字符串。

Moshi 让我们通过使用特殊注解的注解来做到这一点，然后我们可以将其应用于我们的字段和我们的适配器：

```java
@Retention(RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@JsonQualifier
public @interface EpochMillis {}
```

其中的关键部分是 @JsonQualifier注解，它允许 Moshi 将任何用此注解的字段绑定到适当的适配器方法。

接下来，我们需要编写一个适配器。与往常一样，我们同时使用 @FromJson和 @ToJson方法在我们的类型和 JSON 之间进行转换：

```java
public class EpochMillisAdapter {
    @ToJson
    public Long toJson(@EpochMillis Instant input) {
        return input.toEpochMilli();
    }
    @FromJson
    @EpochMillis
    public Instant fromJson(Long input) {
        return Instant.ofEpochMilli(input);
    }
}
```

在这里，我们在@ToJson 方法的输入参数和@FromJson方法的返回值上 使用了注解。

Moshi 现在可以使用此适配器或任何也用@EpochMillis注解的字段：

```java
public class Post {
    private String title;
    private String author;
    @EpochMillis Instant posted;
    // constructor, getters and setters
}
```

我们现在可以根据需要将带注解的类型转换为 JSON 并返回：

```java
Moshi moshi = new Moshi.Builder()
  .add(new EpochMillisAdapter())
  .build();
JsonAdapter<Post> jsonAdapter = moshi.adapter(Post.class);

String json = jsonAdapter.toJson(new Post("Introduction to Moshi Json", "Baeldung", Instant.now()));
// {"author":"Baeldung","posted":1582095384793,"title":"Introduction to Moshi Json"}

Post post = jsonAdapter.fromJson(json);
// new Post("Introduction to Moshi Json", "Baeldung", Instant.now())
```

## 6. 高级JSON处理

现在我们可以将我们的类型转换为 JSON 并返回，我们可以控制这种转换发生的方式。不过，有时我们可能需要在处理过程中做一些更高级的事情，Moshi 可以轻松实现这些事情。

### 6.1. 重命名 JSON 字段

有时，我们需要我们的 JSON 具有与我们的Javabean 不同的字段名称。这可能就像 在Java中想要camelCase和在 JSON 中想要snake_case一样简单，或者可能是完全重命名字段以匹配所需的模式。

我们可以使用 @Json注解为我们控制的任何 bean 中的任何字段赋予新名称：

```java
public class Post {
    private String title;
    @Json(name = "authored_by")
    private String author;
    // constructor, getters and setters
}
```

完成此操作后，Moshi 立即了解到此字段在 JSON 中具有不同的名称：

```java
Moshi moshi = new Moshi.Builder()
  .build();
JsonAdapter<Post> jsonAdapter = moshi.adapter(Post.class);

Post post = new Post("My Post", "Baeldung");

String json = jsonAdapter.toJson(post);
// {"authored_by":"Baeldung","title":"My Post"}

Post post = jsonAdapter.fromJson(json);
// new Post("My Post", "Baeldung")
```

### 6.2. 瞬变场

在某些情况下，我们可能有不应包含在 JSON 中的字段。Moshi 使用标准的 transient限定符来指示这些字段不被序列化或反序列化：

```java
public static class Post {
    private String title;
    private transient String author;
    // constructor, getters and setters
}
```

然后我们会看到这个字段在序列化和反序列化时都被完全忽略了：

```java
Moshi moshi = new Moshi.Builder()
  .build();
JsonAdapter<Post> jsonAdapter = moshi.adapter(Post.class);

Post post = new Post("My Post", "Baeldung");

String json = jsonAdapter.toJson(post);
// {"title":"My Post"}

Post post = jsonAdapter.fromJson(json);
// new Post("My Post", null)

Post post = jsonAdapter.fromJson("{"author":"Baeldung","title":"My Post"}");
// new Post("My Post", null)
```

### 6.3. 默认值

有时我们解析的 JSON 不包含JavaBean 中每个字段的值。这很好，莫氏会尽最大努力做正确的事。

Moshi 在反序列化我们的 JSON 时无法使用任何形式的参数构造函数，但如果存在的话，它可以使用无参数构造函数。

这将允许我们在序列化 JSON 之前预填充我们的 bean，为我们的字段提供任何必需的默认值：

```java
public class Post {
    private String title;
    private String author;
    private String posted;

    public Post() {
        posted = Instant.now().toString();
    }
    // getters and setters
}
```

如果我们解析的 JSON 缺少 标题或 作者字段，那么这些字段将以值 null 结尾。如果我们缺少 posted字段，那么这将取而代之的是当前日期和时间：

```java
Moshi moshi = new Moshi.Builder()
  .build();
JsonAdapter<Post> jsonAdapter = moshi.adapter(Post.class);

String json = "{"title":"My Post"}";
Post post = jsonAdapter.fromJson(json);
// new Post("My Post", null, "2020-02-19T07:27:01.141Z");
```

### 6.4. 解析 JSON 数组

到目前为止，我们所做的一切都假定我们将单个 JSON 对象序列化和反序列化为单个Javabean。这是一个很常见的案例，但不是唯一的案例。有时我们还想处理值的集合，这些值在我们的 JSON 中表示为数组。

当数组嵌套在我们的 bean 中时，就没有什么可做的了。Moshi 会正常工作。当整个 JSON 是一个数组时，我们必须做更多的工作来实现这一点，这仅仅是因为Java泛型的一些限制。我们需要以一种知道它正在反序列化通用集合以及集合是什么的方式构造我们的JsonAdapter 。

Moshi 提供了一些帮助来构建一个 java.lang.reflect.Type，我们可以 在构建JsonAdapter时将其提供给它，以便我们可以提供这些额外的通用信息：

```java
Moshi moshi = new Moshi.Builder()
  .build();
Type type = Types.newParameterizedType(List.class, String.class);
JsonAdapter<List<String>> jsonAdapter = moshi.adapter(type);
```

完成后，我们的适配器将完全按照预期工作，遵守这些新的通用边界：

```java
String json = jsonAdapter.toJson(Arrays.asList("One", "Two", "Three"));
// ["One", "Two", "Three"]

List<String> result = jsonAdapter.fromJson(json);
// Arrays.asList("One", "Two", "Three");
```

## 七. 总结

我们已经了解了 Moshi 库如何使Java类与 JSON 之间的转换变得非常容易，以及它是多么灵活。我们可以在任何需要在Java和 JSON 之间进行转换的地方使用这个库——无论是从文件、数据库列甚至 REST API 加载和保存。为什么不试试呢？