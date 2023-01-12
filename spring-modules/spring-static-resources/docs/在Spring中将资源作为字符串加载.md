## 1. 概述

在本教程中，我们将研究将包含文本的资源内容作为 String 注入到我们的 Spring bean中的各种方法。

我们将着眼于定位资源并读取其内容。

此外，我们将演示如何跨多个 bean 共享加载的资源。我们将通过使用[与依赖注入相关的注解来](https://www.baeldung.com/spring-annotations-resource-inject-autowire)展示这一点，尽管同样可以通过使用[基于 XML 的注入](https://www.baeldung.com/spring-xml-injection)并在 XML 属性文件中声明 bean 来实现。

## 2. 使用资源

我们可以使用[Resource](https://www.baeldung.com/spring-classpath-file-access)接口简化定位资源文件的过程。Spring 帮助我们使用资源加载器查找和读取资源，资源加载器根据提供的路径决定选择哪个资源实现。资源实际上是一种访问资源内容的方式，而不是内容本身。 

让我们看看一些为类路径上的资源[获取Resource实例的](https://www.baeldung.com/spring-classpath-file-access)方法。

### 2.1. 使用资源加载器

如果我们更喜欢使用延迟加载，我们可以使用ResourceLoader类：

```java
ResourceLoader resourceLoader = new DefaultResourceLoader();
Resource resource = resourceLoader.getResource("classpath:resource.txt");
```

我们还可以使用@Autowired将ResourceLoader注入到我们的 bean 中：

```java
@Autowired
private ResourceLoader resourceLoader;
```

### 2.2. 使用@Value

我们可以使用@Value将Resource直接注入到 Spring bean 中 ：

```java
@Value("classpath:resource.txt")
private Resource resource;
```

## 3. 从资源到字符串的转换

一旦我们有权访问资源，我们就需要能够将其读入String。让我们创建一个带有静态方法asString的ResourceReader实用程序类来为我们执行此操作。

首先，我们必须获取一个InputStream：

```java
InputStream inputStream = resource.getInputStream();
```

我们的下一步是获取此InputStream并将其转换为String。我们可以使用Spring自带的FileCopyUtils#copyToString方法：

```java
public class ResourceReader {

    public static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // more utility methods
}
```

还有[许多其他方法可以实现这一点](https://www.baeldung.com/convert-input-stream-to-string)，例如，使用Spring 的StreamUtils类的copyToString

我们还创建另一个实用程序方法readFileToString，它将检索路径的Resource，并调用asString方法将其转换为String。

```java
public static String readFileToString(String path) {
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    Resource resource = resourceLoader.getResource(path);
    return asString(resource);
}
```

## 4.添加配置类

如果每个 bean 都必须单独注入资源String，那么有可能会出现代码重复和 bean 拥有自己的String副本而更多地使用内存。

我们可以通过在加载应用程序上下文时将资源的内容注入一个或多个 Spring bean 来实现更简洁的解决方案。通过这种方式，我们可以隐藏从需要使用此内容的各种 bean 读取资源的实现细节。

```java
@Configuration
public class LoadResourceConfig {

    // Bean Declarations
}
```

### 4.1. 使用持有资源字符串的 Bean

让我们声明 bean 以在@Configuration类中保存资源内容：

```java
@Bean
public String resourceString() {
    return ResourceReader.readFileToString("resource.txt");
}
```

[现在让我们通过添加@Autowired](https://www.baeldung.com/spring-autowire)注解将注册的 bean 注入到字段中：

```java
public class LoadResourceAsStringIntegrationTest {
    private static final String EXPECTED_RESOURCE_VALUE = "...";  // The string value of the file content

    @Autowired
    @Qualifier("resourceString")
    private String resourceString;

    @Test
    public void givenUsingResourceStringBean_whenConvertingAResourceToAString_thenCorrect() {
        assertEquals(EXPECTED_RESOURCE_VALUE, resourceString);
    }
}
```

在这种情况下，我们使用@Qualifier注解和 bean 的名称，因为我们可能需要注入相同类型的多个字段– String。

我们应该注意，限定符中使用的 bean 名称派生自配置类中创建 bean 的方法的名称。

## 5. 使用游戏

最后，让我们看看如何使用 Spring 表达式语言来描述将资源文件直接加载到我们类中的字段中所需的代码。

让我们使用@Value注解将文件内容注入到字段resourceStringUsingSpel中：

```java
public class LoadResourceAsStringIntegrationTest {
    private static final String EXPECTED_RESOURCE_VALUE = "..."; // The string value of the file content

    @Value(
      "#{T(com.baeldung.loadresourceasstring.ResourceReader).readFileToString('classpath:resource.txt')}"
    )
    private String resourceStringUsingSpel;

    @Test
    public void givenUsingSpel_whenConvertingAResourceToAString_thenCorrect() {
        assertEquals(EXPECTED_RESOURCE_VALUE, resourceStringUsingSpel);
    }
}
```

在这里，我们调用了ResourceReader#readFileToString，通过使用“类路径：”来描述文件的位置—— @Value注解中的前缀路径。

为了减少 SpEL 中的代码量，我们在ResourceReader类中创建了一个辅助方法， 它使用 Apache Commons FileUtils从提供的路径访问文件：

```java
public class ResourceReader {
    public static String readFileToString(String path) throws IOException {
        return FileUtils.readFileToString(ResourceUtils.getFile(path), StandardCharsets.UTF_8);
    }
}
```

## 六. 总结

在本教程中，我们回顾了一些将资源转换为String的方法。

首先，我们看到了如何生成一个Resource来访问文件，以及如何从Resource读取到String。

接下来，我们还展示了如何隐藏资源加载实现，并通过在@Configuration中创建合格的 beans 允许字符串内容在 beans 之间共享，从而允许字符串自动装配。

最后，我们使用了 SpEL，它提供了一个紧凑而直接的解决方案，尽管它需要一个自定义的辅助函数来阻止它变得太复杂。