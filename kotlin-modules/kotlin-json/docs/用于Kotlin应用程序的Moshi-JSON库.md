## 一、简介

Moshi 库建立在[Okio](https://github.com/square/okio)之上，并继承了另一个 JSON 解析器[Gson](https://github.com/google/gson)的原理。不过，与 Gson 不同的是，它的速度要快得多，而且与 Jackson 不同的是，它的占用空间要小得多，这对嵌入式应用程序很重要。

我们已经大体[讨论](https://www.baeldung.com/java-json-moshi)了 Moshi 的应用。但是，由于它是主要用 Kotlin 为 Kotlin 开发人员编写的库，所以让我们讨论一下它特定于我们语言的方面。

## 2. 基本的 Moshi 设置

要将 Moshi 与 Kotlin 一起使用，我们需要定义以下依赖项：

```xml
<dependency>
    <groupId>com.squareup.moshi</groupId>
    <artifactId>moshi</artifactId>
    <version>1.14.0</version>
</dependency>
<dependency>
    <groupId>com.squareup.moshi</groupId>
    <artifactId>moshi-adapters</artifactId>
    <version>1.14.0</version>
</dependency>
<dependency>
    <groupId>com.squareup.moshi</groupId>
    <artifactId>moshi-kotlin</artifactId>
    <version>1.14.0</version>
    <!-- Omit if using codegen exclusively -->
</dependency>
```

如果我们还计划为我们的类型适配器使用 Moshi codegen，我们需要[在编译步骤之前在](https://kotlinlang.org/docs/kapt.html#using-in-maven)kotlin-maven-plugin中设置 kapt执行：

```xml
<!-- in kotlin-maven-plugin, executions section -->
<execution>
    <id>kapt</id>
    <goals>
        <goal>kapt</goal>
    </goals>
    <configuration>
        <sourceDirs>
            <sourceDir>src/main/kotlin</sourceDir>
        </sourceDirs>
        <annotationProcessorPaths>
            <annotationProcessorPath>
                <groupId>com.squareup.moshi</groupId>
                <artifactId>moshi-kotlin-codegen</artifactId>
                <version>1.14.0</version>
            </annotationProcessorPath>
        </annotationProcessorPaths>
    </configuration>
</execution>
```

### 2.1. 基本序列化和反序列化

让我们假设我们有一个简单的任务接受 JSON 输入，以某种方式转换它，并用另一个 JSON 消息回复：

```kotlin
data class Department(
    val name: String,
    val code: UUID,
    val employees: List<Employee>
)

data class Employee(
    val firstName: String,
    val lastName: String,
    val title: String,
    val age: Int,
    val salary: BigDecimal
)

data class SalaryRecord(
    val employeeFirstName: String,
    val employeeLastName: String,
    val departmentCode: UUID,
    val departmentName: String,
    val sum: BigDecimal,
    val taxPercentage: BigDecimal
)
```

Moshi 库的工作方式与 Jackson 不同，但与 Gson 相似。它使用反射来序列化和反序列化 Kotlin 中的数据类和 Java 中的 POJO，但它需要显式适配器才能使用UUID或BigDecimal等平台类型进行操作。

它通常具有较少的内置适配器，以防止其用户将自己锁定到特定版本的 JDK 并保持其低占用空间：

```kotlin
class UuidAdapter : JsonAdapter<UUID>() {
    @FromJson
    override fun fromJson(reader: JsonReader): UUID? = UUID.fromString(reader.readJsonValue().toString())

    @ToJson
    override fun toJson(writer: JsonWriter, value: UUID?) {
        writer.jsonValue(value.toString())
    }
}
```

默认情况下，Moshi 为原始类型、它们的盒装对应物、所有标准集合(Arrays、Lists、Maps等)和Strings提供适配器。我们必须手动添加到 Moshi 实例的自定义适配器：

```kotlin
val moshi = Moshi.Builder()
  .add(UuidAdapter())
  .add(BigDecimalAdapter()) // And all other adapters
  .addLast(KotlinJsonAdapterFactory())
  .build()
```

在那之后，我们准备好了：

```kotlin
val adapter = moshi.adapter<Department>()
val department = adapter.fromJson(resource("sales_department.json")!!.source().buffer())

val salaryRecordJsonAdapter = moshi.adapter<SalaryRecord>()
val serialized: String = salaryRecordJsonAdapter.toJson(record)
```

在撰写本文时，特定于 Kotlin 的语法需要在我们的类或方法之上选择加入注解@ExperimentalStdlibApi 。

### 2.2. 即时和预生成的适配器

Moshi 的标准方式是使用用户类型的反射来生成适配器。对于 Kotlin，这意味着额外的依赖性和额外的功能：Moshi 理解 Kotlin 的不可空性概念并正确使用默认值。额外的依赖项大约需要 2.5Mb。此外，反射生成的适配器可以序列化和反序列化私有和受保护的字段。

但是，有一种方法可以通过使用moshi-kotlin-codegen注解处理器来节省时间和磁盘空间。它将在编译时为我们的类型生成适配器。这些适配器可能比反射库占用更少的空间并且工作速度更快。不利的一面是，他们只能在内部和公共领域工作。

要使用 codegen，我们需要设置 KAPT，如上所示。如果我们想独占使用它，我们应该从运行时依赖项中删除moshi-kotlin 。如果我们想同时使用这两种方法，我们应该用特殊注解标记我们想要为其预生成适配器的类型：

```kotlin
@JsonClass(generateAdapter = true)
data class Department( / property declarations/ )
```

编译完成后，我们会在target目录下找到DepartmentJsonAdapter.class文件。

### 2.3. 解析通用类型

很多时候，我们需要解析或生成的类型是一个对象列表。对于 Java 中的 Moshi，它有点复杂，因为我们需要先创建一个新类型。在 Kotlin 中，由于具体化的泛型，它很简单：

```kotlin
val employeeListAdapter = moshi.adapter<List<Employee>>()
```

然后，序列化和反序列化以相同的方式工作：

```kotlin
val list = employeeListAdapter.fromJson(inputStream.source().buffer())
```

### 2.4. 与 Okio 合作

为了访问 IO 操作，Moshi 使用 Okio 库，它又是[OkHttp 客户端](https://www.baeldung.com/guide-to-okhttp)的一部分。无法将包含 JSON的InputStream或File直接解析为对象。相反，我们必须先从它们创建一个BufferedSource或JsonReader：

```kotlin
val bufferedSource = inputStream.source().buffer()
val reader = JsonReader.of(bufferedSource)
```

使用 Okio 库使我们能够节俭地处理大的 JSON 值，如下所示。

## 3. 使用注解修改 Moshi 行为

Square 团队构建 Moshi 是为了提高速度和优化 RAM 利用率。因此，它没有太多可定制的选项，比如属性命名策略或其他什么。

但是，有些事情我们可以通过注解来完成。我们已经看过@JsonClass的用法，它会调用适配器生成。让我们看看我们还能做什么。此功能并非特定于 Kotlin，但它是 Moshi 库功能的重要组成部分。

### 3.1. 在对象和 JSON 中使用不同的名称

运行时和 JSON 中的名称不同的最常见情况是代码使用与 JSON 不同的名称样式。例如，JSON 在snake_case中，代码在camelCase中。在 Moshi 中，这可以通过使用 JSON 名称注解所有字段来解决：

```kotlin
data class SnakeProject(
  @Json(name = "project_name")
  val snakeProjectName: String,
  @Json(name = "responsible_person_name")
  val snakeResponsiblePersonName: String,
  @Json(name = "project_budget")
  val snakeProjectBudget: BigDecimal
)
```

当我们序列化这个类时，Moshi 会使用注解中的名字：

```kotlin
val snakeProjectAdapter = moshi.adapter<SnakeProject>()
val snakeProject = SnakeProject(
  snakeProjectName = "Mayhem",
  snakeResponsiblePersonName = "Tailor Burden",
  snakeProjectBudget = BigDecimal("100000000"),
  snakeProjectSecret = "You're not a snowflake"
)
val stringProject = snakeProjectAdapter.toJson(snakeProject)

// stringProject equals {"project_name":"Mayhem","responsible_person_name":"Tailor Burden","project_budget":"100000000"}
```

### 3.2. 忽略字段

同样，并非每个运行时字段都值得保留或发送给客户端。我们可以使用@Json(ignore = true)跳过字段：

```kotlin
data class SnakeProject(
  // ... existing fields 
  @Json(ignore = true)
  val snakeProjectSecret: String = "No secret"
)

val stringProject = snakeProjectAdapter.toJson(snakeProject)

// stringProject is unchanged
```

不过，需要注意的一件事是，Moshi codegen 将要求我们为忽略的字段设置一个默认值，以便在解析过程中可以正确实例化对象。

### 3.3. 通过自定义注解选择适配器

有时，由相同的 Kotlin 类型表示的值在 JSON 中必须以不同的方式表示。一个典型的例子是 RGB 方案中的颜色表示。我们习惯于 color 是一个十六进制数，但对于 JVM，它只是另一个Int。

幸运的是，Moshi 不仅可以根据值或字段的类型，还可以根据注解来分配适配器。首先，我们需要创建一个注解类：

```kotlin
@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class Hexadecimal
```

然后我们需要在Moshi实例中创建并注册一个适配器，用于序列化十六进制数并解析它们：

```kotlin
class HexadecimalAdapter {
    @ToJson
    fun toJson(@Hexadecimal color: Int): String = "#%06x".format(color)

    @FromJson
    @Hexadecimal
    fun fromJson(color: String): Int = color.substring(1).toInt(16)
}
```

解析器参数和序列化器函数上的注解标记了 Moshi 引擎的适配器，然后可以通过注解将适配器与字段匹配：

```kotlin
val moshi: Moshi = Moshi.Builder()
  // other adapters
  .add(HexadecimalAdapter())
  // the rest of the configuration

val paletteAdapter = moshi.adapter<Palette>()
val palette = Palette(
  83  256  256 + 43  256 + 18,
  160  256 + 18,
  25
)
val result = paletteAdapter.toJson(palette)

// result is {"mainColor":"#532b12","backgroundColor":"#00a012","frameFrequency":25}

```

## 4. 使用 Moshi 解析 JSON 对象流

当应用程序资源稀缺时，Okio 的使用非常方便。正如我们之前注意到的，通常，我们必须解析的不是单个 JSON 对象，而是统一对象的集合——无论是订单商品还是客户记录。如果我们无法将整个 JSON 文档读入内存，那么是时候尝试流式处理了。幸运的是，莫氏对此非常适应。

Moshi 将Source封装到JsonReader 中——一个将 JSON 读取为令牌流的实体。理论上，我们可以使用JsonReader为任何类型创建我们自己的流式解析器。现在，我们只需要一个小的辅助方法来一个一个地读取集合项：

```kotlin
inline fun JsonReader.readArray(body: JsonReader.() -> Unit) {
    beginArray()
    while (hasNext()) {
        body()
    }
    endArray()
}
```

然后很容易填充项目流，然后任何协程都可以使用：

```kotlin
suspend inline fun <reified T> readToFlow(input: InputStream, adapter: JsonAdapter<T>): Flow<T> = flow {
    JsonReader.of(input.source().buffer())
      .readArray {
          emit(adapter.fromJson(this)!!)
      }
}
```

这样读者就不会急切地阅读文档，从而减少应用程序所需的内存。如果任务是减少文档，我们可以做到这一点而无需同时将所有元素都存储在内存中：

```kotlin
val totalSalary = runBlocking {
    readToFlow(it, employeeAdapter)
      .fold(BigDecimal.ZERO) { acc, value -> acc + value.salary }
}
```

## 5.总结

Moshi 是一个简约但功能强大的 JSON 库。它需要一些努力才能开始——我们必须为我们在代码中使用的平台类型创建所有这些适配器。但是一旦开始，Moshi 比它的前身 Gson 更快，并且不会增加我们 APK 的大小。

在本教程中，我们了解了如何使用 Maven 为 Kotlin 项目设置 Moshi。使用 Gradle 进行设置要容易得多。我们讨论了如何自定义 Moshi 的行为以适应我们的特定情况。使用注解，我们可以更改生成的 JSON 文档中的名称，或者使输入文档名称映射到我们使用的命名策略。

或者，我们可以完全丢弃一些字段，或者对相同的数据类型使用不同的适配器，这取决于我们正在实现的协议。最后，我们讨论了使用流技术解析大型 JSON 集合并保存在 RAM 上。