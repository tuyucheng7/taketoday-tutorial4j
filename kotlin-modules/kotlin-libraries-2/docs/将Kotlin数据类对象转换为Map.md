## 1. 概述

在 Kotlin 中使用数据类时，有时需要将数据类对象转换为Map。有几个内置或第三方库可以帮助我们解决这个问题。其中包括 Kotlin Reflection、Jackson、Gson 和 Kotlin Serialization。

在本文中，我们将讨论其中的每一种方式。然后，我们将仔细研究这些差异。

## 2.数据类

在我们进入不同的实现之前，让我们定义一个示例数据类。

```kotlin
enum class ProjectType {
    APPLICATION, CONSOLE, WEB
}

data class ProjectRepository(val url: String)

data class Project(
    val name: String,
    val type: ProjectType,
    val createdDate: Date,
    val repository: ProjectRepository,
    val deleted: Boolean = false,
    val owner: String?
) {
    var description: String? = null
}

```

Project包含几个我们可能会在数据类中找到的典型属性：

-   原语——名字
-   枚举——类型
-   日期——创建日期
-   嵌套数据类——存储库
-   具有默认值的属性 - 已删除
-   可空属性 -所有者
-   在类主体中声明的属性 -描述

我们将创建一个Project的实例：

```kotlin
val PROJECT = Project(
    name = "test1",
    type = ProjectType.APPLICATION,
    createdDate = Date(1000),
    repository = ProjectRepository(url = "http://test.baeldung.com/test1"),
    owner = null
).apply {
    description = "a new project"
}

```

将PROJECT 数据对象转换为地图时，我们的解决方案应该正确处理所有这些属性。

## 3.科特林反射

Kotlin Reflection 是一个可以首先尝试的简单库。

### 3.1. Maven 依赖

首先，让我们在pom.xml中包含[kotlin-reflect](https://search.maven.org/artifact/org.jetbrains.kotlin/kotlin-reflect) 依赖 项：

```xml
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>        
    <artifactId>kotlin-reflect</artifactId>
    <version>1.6.10</version>
</dependency>
```

### 3.2. 转换所有属性

使用反射，我们可以构建一个递归方法来检查对象的属性。这会将每个属性添加到地图中：

```kotlin
fun <T : Any> toMap(obj: T): Map<String, Any?> {
    return (obj::class as KClass<T>).memberProperties.associate { prop ->
        prop.name to prop.get(obj)?.let { value ->
            if (value::class.isData) {
                toMap(value)
            } else {
                value
            }
        }
    }
}

```

接下来，让我们验证转换后的地图：

```kotlin
assertEquals(
    mapOf(
        "name" to "test1",
        "type" to ProjectType.APPLICATION,
        "createdDate" to Date(1000),
        "repository" to mapOf(
            "url" to "http://test.baeldung.com/test1"
        ),
        "deleted" to false,
        "owner" to null,
        "description" to "a new project"
    ),
    toMap(project)
)

```

### 3.3. 仅在主构造函数中使用属性的转换

正如我们所知，数据类的默认toString函数会将数据对象转换为字符串。但是，此字符串仅包含来自主构造函数的属性：

```kotlin
assertFalse(project.toString().contains(Project::description.name))
```

如果我们想在我们的toMap函数中匹配这种行为，我们可以添加一个过滤步骤：

```kotlin
fun <T : Any> toMapWithOnlyPrimaryConstructorProperties(obj: T): Map<String, Any?> {
    val kClass = obj::class as KClass<T>
    val primaryConstructorPropertyNames = kClass.primaryConstructor?.parameters?.map { it.name } ?: run {
        return toMap(obj)
    }
    return kClass.memberProperties.mapNotNull { prop ->
        prop.name.takeIf { it in primaryConstructorPropertyNames }?.let {
            it to prop.get(obj)?.let { value ->
                if (value::class.isData) {
                    toMap(value)
                } else {
                    value
                }
            }
        }
    }.toMap()
}

```

在这个函数中，如果类没有主构造函数，我们将退回到原来的toMap 函数。

我们来验证一下结果：

```kotlin
assertEquals(
    mapOf(
        "name" to "test1",
        "type" to ProjectType.APPLICATION,
        "createdDate" to Date(1000),
        "repository" to mapOf(
            "url" to "http://test.baeldung.com/test1"
        ),
        "deleted" to false,
        "owner" to null
    ),
    toMapWithOnlyPrimaryConstructorProperties(project)
)
```

### 3.4. 限制

这种方法有一些局限性：

-   不支持日期属性序列化为特定格式的字符串
-   我们无法从结果中排除特定属性
-   不支持将转换反转回数据对象

因此，如果我们需要这些功能，也许我们应该改用序列化库。

## 4.杰克逊

接下来，让我们试试[Jackson](https://www.baeldung.com/jackson)库。

### 4.1. Maven 依赖

像往常一样，我们需要在pom.xml中包含[jackson-module-kotlin](https://search.maven.org/artifact/com.fasterxml.jackson.module/jackson-module-kotlin) 依赖 项：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.module</groupId>
    <artifactId>jackson-module-kotlin</artifactId>
    <version>2.11.3</version>
</dependency>
```

### 4.2. 转换所有属性

首先，让我们创建一个注册了KotlinModule的ObjectMapper实例：

```kotlin
val DEFAULT_JACKSON_MAPPER = ObjectMapper().registerModule(KotlinModule())
```

Jackson 会将所有值转换为原始类型。默认情况下，Date将转换为 Long：

```kotlin
assertEquals(
    mapOf(
        "name" to "test1",
        "type" to ProjectType.APPLICATION.name,
        "createdDate" to 1000L,
        "repository" to mapOf(
            "url" to "http://test.baeldung.com/test1"
        ),
        "deleted" to false,
        "owner" to null,
        "description" to "a new project"
    ), DEFAULT_JACKSON_MAPPER.convertValue(PROJECT, Map::class.java)
)
```

但是，如果我们可以禁用默认日期格式并设置我们自己的：

```kotlin
val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

val JACKSON_MAPPER_WITH_DATE_FORMAT = ObjectMapper().registerModule(KotlinModule()).apply {
    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    dateFormat = DATE_FORMAT
}
```

我们来验证一下结果：

```kotlin
val expected = mapOf(
    "name" to "test1",
    "type" to ProjectType.APPLICATION.name,
    "createdDate" to DATE_FORMAT.format(PROJECT.createdDate),
    "repository" to mapOf(
        "url" to "http://test.baeldung.com/test1"
    ),
    "deleted" to false,
    "owner" to null,
    "description" to "a new project"
)
assertEquals(expected, JACKSON_MAPPER_WITH_DATE_FORMAT.convertValue(PROJECT, Map::class.java))
```

我们应该注意到 Jackson 还有许多其他有用的特性。这些包括[日期序列化](https://www.baeldung.com/jackson-serialize-dates)、[忽略空字段](https://www.baeldung.com/jackson-ignore-null-fields)、[忽略属性](https://www.baeldung.com/jackson-ignore-properties-on-serialization)等。

### 4.3. 从地图到数据对象的转换

Jackson 的ObjectMapper可以为我们将地图转换回数据对象：

```kotlin
assertEquals(PROJECT, JACKSON_MAPPER_WITH_DATE_FORMAT.convertValue(expected, Project::class.java))
```

在这里，如果映射中缺少任何不可为 null 的属性，Jackson 将抛出IllegalArgumentException：

```kotlin
val mapWithoutCreatedDate = mapOf(
    "name" to "test1",
    "type" to ProjectType.APPLICATION.name,
    "repository" to mapOf(
        "url" to "http://test.baeldung.com/test1"
    ),
    "deleted" to false,
    "owner" to null,
    "description" to "a new project"
)
assertThrows<IllegalArgumentException> { DEFAULT_JACKSON_MAPPER.convertValue(mapWithoutCreatedDate, Project::class.java) }
```

### 4.4. 限制

Jackson 是一个面向 JSON 的库。因此，它尝试将所有值转换为原始类型。所以，当我们想要保持属性对象原样时，它可能不适合场景。这是因为 JSON 没有像Date这样的原始类型。

## 5. 格森

[Gson](https://www.baeldung.com/kotlin/json-convert-data-class)是另一个可用于将对象转换为地图表示的库。

### 5.1. Maven 依赖

首先，我们需要在pom.xml中包含[gson](https://search.maven.org/artifact/com.google.code.gson/gson)依赖 项：

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.5</version>
</dependency>
```

### 5.2. 转换所有属性

同样，让我们用它的构建器创建一个Gson 实例：

```kotlin
val GSON_MAPPER = GsonBuilder().serializeNulls().setDateFormat(DATE_FORMAT).create()
```

使用此构建器，我们已将 Gson 设置为序列化具有空值的属性并指定了日期格式。

我们来验证一下结果：

```kotlin
assertEquals(
    expected,
    GSON_MAPPER.fromJson(GSON_MAPPER.toJson(PROJECT), Map::class.java)
)
```

[Gson](https://github.com/google/gson/blob/master/UserGuide.md)还支持[从序列化中排除属性](https://www.baeldung.com/gson-exclude-fields-serialization)。

### 5.3. 从地图到数据对象的转换

我们可以使用相同的映射器转换回我们的数据类类型：

```kotlin
assertEquals(
    PROJECT,
    GSON_MAPPER.fromJson(GSON_MAPPER.toJson(expected), Project::class.java)
)

```

### 5.4. 不可空属性

与 Jackson 不同，如果缺少任何不可为 null 的属性，Gson 不会抱怨：

```kotlin
val newProject = GSON_MAPPER.fromJson(GSON_MAPPER.toJson(mapWithoutCreatedDate), Project::class.java)
assertNull(newProject.createdDate)

```

如果我们不处理它，这可能会导致意外的异常。

解决此问题的一种方法是定义自定义的TypeAdapterFactory。 

```kotlin
class KotlinTypeAdapterFactory : TypeAdapterFactory {
    override fun <T : Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val delegate = gson.getDelegateAdapter(this, type)
        if (type.rawType.declaredAnnotations.none { it.annotationClass == Metadata::class }) {
            return null
        }
        return KotlinTypeAdaptor(delegate, type)
    }
}

```

首先，KotlinTypeAdapterFactory 会检查目标类是否是 Kotlin 类是否有kotlin。元数据 注解。接下来，它将为 Kotlin 类返回一个KotlinTypeAdapter ：

```kotlin
class KotlinTypeAdaptor<T>(private val delegate: TypeAdapter<T>, private val type: TypeToken<T>) : TypeAdapter<T>() {
    override fun write(out: JsonWriter, value: T?) = delegate.write(out, value)

    override fun read(input: JsonReader): T? {
        return delegate.read(input)?.apply {
            Reflection.createKotlinClass(type.rawType).memberProperties.forEach {
                if (!it.returnType.isMarkedNullable && it.get(this) == null) {
                    throw IllegalArgumentException("Value of non-nullable property [${it.name}] cannot be null")
                }
            }
        }
    }
}
```

在从委托返回结果之前， KotlinTypeAdaptor将保证所有不可为 null 的属性都具有正确的值。 我们在这里使用 kotlin-reflect，所以我们需要之前的依赖项。

我们来验证一下结果。首先，让我们创建一个注册了KotlinTypeAdapterFactory的映射器：

```kotlin
val KOTLIN_GSON_MAPPER = GsonBuilder()
  .serializeNulls()
  .setDateFormat(DATE_FORMAT)
  .registerTypeAdapterFactory(KotlinTypeAdapterFactory())
  .create()
```

然后让我们尝试转换没有createdDate属性的地图：

```kotlin
val exception = assertThrows<IllegalArgumentException> {
    KOTLIN_GSON_MAPPER.fromJson(KOTLIN_GSON_MAPPER.toJson(mapWithoutCreatedDate), Project::class.java)
}
assertEquals(
    "Value of non-nullable property [${Project::createdDate.name}] cannot be null",
    exception.message
)
```

正如我们所见，它抛出了预期消息的异常。

### 5.5. 限制

与 Jackson 类似，Gson 也是一个面向 JSON 的库，并试图将所有值转换为原始类型。

## 6. Kotlin 序列化

[Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md)是一个数据序列化框架。它将对象树转换为字符串并返回。它是与 Kotlin 编译器分发版捆绑在一起的编译器插件。此外，它完全支持并强制执行 Kotlin 类型系统。

### 6.1. Maven 依赖

首先，我们需要将[序列化插件](https://search.maven.org/artifact/org.jetbrains.kotlin/kotlin-maven-serialization)添加到 Kotlin 编译器中：

```xml
<plugin>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-maven-plugin</artifactId>
    <version>1.6.10</version>
    <executions>
        <execution>
            <id>compile</id>
            <phase>compile</phase>
            <goals>
                <goal>compile</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <compilerPlugins>
            <plugin>kotlinx-serialization</plugin>
        </compilerPlugins>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-maven-serialization</artifactId>
            <version>1.6.10</version>
        </dependency>
    </dependencies>
</plugin>

```

然后我们需要添加[序列化运行时库](https://search.maven.org/artifact/org.jetbrains.kotlinx/kotlinx-serialization-json)的依赖：

```xml
<dependency>
    <groupId>org.jetbrains.kotlinx</groupId>
    <artifactId>kotlinx-serialization-json</artifactId>
    <version>1.3.2</version>
</dependency>

```

### 6.2. Kotlin 序列化注解

在使用 Kotlin Serialization 时，我们需要为Date类型定义一个序列化器：

```kotlin
object DateSerializer : KSerializer<Date> {
    override val descriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Date) = encoder.encodeString(DATE_FORMAT.format(value))
    override fun deserialize(decoder: Decoder): Date = DATE_FORMAT.parse(decoder.decodeString())
}

```

然后我们需要用相应的注解来注解我们的类和属性：

```kotlin
@Serializable
data class SerializableProjectRepository(val url: String)

@Serializable
data class SerializableProject(
    val name: String,
    val type: ProjectType,
    @Serializable(KotlinSerializationMapHelper.DateSerializer::class) val createdDate: Date,
    val repository: SerializableProjectRepository,
    val deleted: Boolean = false,
    val owner: String?
) {
    var description: String? = null
}
```

让我们创建一个类似的SerializableProject实例：

```kotlin
val SERIALIZABLE_PROJECT = SerializableProject(
    name = "test1",
    type = ProjectType.APPLICATION,
    createdDate = Date(1000),
    repository = SerializableProjectRepository(url = "http://test.baeldung.com/test1"),
    owner = null
).apply {
    description = "a new project"
}

```

### 6.3. 转换所有属性

首先，让我们创建一个 Json 对象：

```kotlin
val JSON = Json { encodeDefaults = true }
```

如我们所见，属性encodeDefaults设置为true。这意味着具有默认值的属性将包含在转换中。

我们可以先将数据对象转换为JsonObject，然后调用JsonObject.toMap函数来获取地图：

```kotlin
JSON.encodeToJsonElement(obj).jsonObject.toMap()
```

但是，此映射中的值是类JsonPrimitive的对象。因此，我们需要将它们转换为原始类型：

```kotlin
inline fun <reified T> toMap(obj: T): Map<String, Any?> {
    return jsonObjectToMap(JSON.encodeToJsonElement(obj).jsonObject)
}

fun jsonObjectToMap(element: JsonObject): Map<String, Any?> {
    return element.entries.associate {
        it.key to extractValue(it.value)
    }
}

private fun extractValue(element: JsonElement): Any? {
    return when (element) {
        is JsonNull -> null
        is JsonPrimitive -> element.content
        is JsonArray -> element.map { extractValue(it) }
        is JsonObject -> jsonObjectToMap(element)
    }
}
```

接下来，我们可以验证结果：

```kotlin
val map = KotlinSerializationMapHelper.toMap(SERIALIZABLE_PROJECT)
val expected = mapOf(
    "name" to "test1",
    "type" to ProjectType.APPLICATION.name,
    "createdDate" to MapHelper.DATE_FORMAT.format(SERIALIZABLE_PROJECT.createdDate),
    "repository" to mapOf(
        "url" to "http://test.baeldung.com/test1"
    ),
    "deleted" to false.toString(),
    "owner" to null,
    "description" to "a new project"
)
assertEquals(expected, map)
```

我们应该注意，删除 的值也是一个 字符串，因为JsonElement总是将其内容保存为一个字符串。

此外，Kotlin 序列化还支持排除带有注解@kotlinx.serialization.Transient 的属性。

### 6.4. 从地图到数据对象的转换

Json.decodeFromJsonElement只接受一个JsonElement作为参数。我们可以反转转换：

```kotlin
val jsonObject = JSON.encodeToJsonElement(SERIALIZABLE_PROJECT).jsonObject
val newProject = JSON.decodeFromJsonElement<SerializableProject>(jsonObject)
assertEquals(SERIALIZABLE_PROJECT, newProject)
```

### 6.5. 限制

Kotlin 序列化是面向字符串的。因此，它可能不太适合需要原始属性值(布尔值或日期)的场景。

## 七、总结

在本文中，我们了解了将 Kotlin 数据对象转换为地图的不同方法，反之亦然。我们应该选择最适合我们的。