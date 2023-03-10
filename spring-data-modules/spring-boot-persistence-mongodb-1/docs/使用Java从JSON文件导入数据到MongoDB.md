## 1. 简介

在本教程中，我们将学习如何使用[Spring Boot](https://www.baeldung.com/spring-data-mongodb-tutorial)从文件中读取JSON数据并将其导入MongoDB 。**出于多种原因，这可能很有用：恢复数据、批量插入新数据或插入默认值**。MongoDB在内部使用JSON来构建其文档，因此很自然地，这就是我们将用来存储可导入文件的内容。作为纯文本，该策略还具有[易于压缩](https://www.baeldung.com/json-reduce-data-size)的优点。

此外，我们将学习如何在必要时根据我们的自定义类型验证我们的输入文件。**最后，我们将公开一个API，以便可以在运行时在我们的Web应用程序中使用它**。

## 2. 依赖

让我们将这些Spring Boot依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

我们还需要一个正在运行的MongoDB实例，它需要一个正确配置的application.properties文件。

## 3. 导入JSON字符串

**将JSON导入MongoDB的最简单方法是首先将其转换为“org.bson.Document”对象**。此类表示没有特定类型的通用MongoDB文档。因此，我们不必担心为我们可能导入的所有类型的对象创建Repository。

我们的策略采用JSON(来自文件、[资源](https://www.baeldung.com/spring-classpath-file-access)或字符串)，将其转换为Document，并使用MongoTemplate保存它们。**批处理操作通常性能更好，因为与单独插入每个对象相比，往返次数减少了**。

最重要的是，我们将考虑我们的输入每个换行符只有一个JSON对象。这样，我们可以很容易地分隔我们的对象。我们将这些功能封装到我们将创建的两个类中：ImportUtils和ImportJsonService。让我们从我们的服务类开始：

```java
@Service
public class ImportJsonService {

    @Autowired
    private MongoTemplate mongo;
}
```

接下来，让我们添加一个将JSON行解析为文档的方法：

```java
private List<Document> generateMongoDocs(List<String> lines) {
    List<Document> docs = new ArrayList<>();
    for (String json : lines) {
        docs.add(Document.parse(json));
    }
    return docs;
}
```

然后我们添加一个方法，将Document对象列表插入到所需的集合中。此外，批处理操作可能会部分失败。在这种情况下，我们可以通过检查异常原因来返回插入文档的数量：

```java
private int insertInto(String collection, List<Document> mongoDocs) {
    try {
        Collection<Document> inserts = mongo.insert(mongoDocs, collection);
        return inserts.size();
    } catch (DataIntegrityViolationException e) {
        if (e.getCause() instanceof MongoBulkWriteException) {
            return ((MongoBulkWriteException) e.getCause())
                .getWriteResult()
                .getInsertedCount();
        }
        return 0;
    }
}
```

最后，让我们组合这些方法。该方法接收输入并返回一个字符串，显示读取了多少行与成功插入了多少行：

```java
public String importTo(String collection, List<String> jsonLines) {
    List<Document> mongoDocs = generateMongoDocs(jsonLines);
    int inserts = insertInto(collection, mongoDocs);
    return inserts + "/" + jsonLines.size();
}
```

## 4. 用例

现在我们已经准备好处理输入，我们可以构建一些用例。让我们创建ImportUtils类来帮助我们，**此类负责将输入转换为JSON行**，它只包含静态方法。让我们从读取一个简单的String开始：

```java
public static List<String> lines(String json) {
    String[] split = json.split("[\\r\\n]+");
    return Arrays.asList(split);
}
```

由于我们使用换行符作为分隔符，因此[正则表达式](https://www.baeldung.com/java-split-string)非常适合将字符串分隔成多行。此正则表达式处理Unix和Windows行尾。接下来，将[File](https://www.baeldung.com/reading-file-in-java)转换为字符串列表的方法：

```java
public static List<String> lines(File file) {
    return Files.readAllLines(file.toPath());
}
```

同样，我们完成了一个将[类路径资源](https://www.baeldung.com/spring-classpath-file-access)转换为列表的方法：

```java
public static List<String> linesFromResource(String resource) {
    Resource input = new ClassPathResource(resource);
    Path path = input.getFile().toPath();
    return Files.readAllLines(path);
}
```

### 4.1 使用CLI在启动期间导入文件

在我们的第一个用例中，我们将实现通过应用程序参数导入文件的功能。我们利用Spring Boot的[ApplicationRunner](https://www.baeldung.com/running-setup-logic-on-startup-in-spring)接口在启动时执行此操作。**例如，我们可以读取命令行参数来定义要导入的文件**：

```java
@SpringBootApplication
public class SpringBootJsonConvertFileApplication implements ApplicationRunner {
    private static final String RESOURCE_PREFIX = "classpath:";

    @Autowired
    private ImportJsonService importService;

    public static void main(String ... args) {
        SpringApplication.run(SpringBootPersistenceApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        if (args.containsOption("import")) {
            String collection = args.getOptionValues("collection")
                  .get(0);

            List<String> sources = args.getOptionValues("import");
            for (String source : sources) {
                List<String> jsonLines = new ArrayList<>();
                if (source.startsWith(RESOURCE_PREFIX)) {
                    String resource = source.substring(RESOURCE_PREFIX.length());
                    jsonLines = ImportUtils.linesFromResource(resource);
                } else {
                    jsonLines = ImportUtils.lines(new File(source));
                }

                String result = importService.importTo(collection, jsonLines);
                log.info(source + " - result: " + result);
            }
        }
    }
}
```

使用getOptionValues()，我们可以处理一个或多个文件。**这些文件可以来自类路径，也可以来自文件系统**。我们使用RESOURCE_PREFIX来区分它们。每个以“classpath:”开头的参数都将从我们的资源文件夹中读取，而不是从文件系统中读取。之后，它们将全部导入到所需的集合中。

让我们通过在src/main/resources/data.json.log下创建一个文件来开始使用我们的应用程序：

```json
{"name":"Book A", "genre": "Comedy"}
{"name":"Book B", "genre": "Thriller"}
{"name":"Book C", "genre": "Drama"}
```

使用[Maven](https://www.baeldung.com/maven)构建项目后，我们可以使用以下示例来运行它(为了便于阅读而添加了换行符)。在我们的示例中，将导入两个文件，一个来自类路径，另一个来自文件系统：

```shell
java -cp target/spring-boot-persistence-mongodb/WEB-INF/lib/*:target/spring-boot-persistence-mongodb/WEB-INF/classes \
  -Djdk.tls.client.protocols=TLSv1.2 \
  cn.tuyucheng.taketoday.SpringBootPersistenceApplication \
  --import=classpath:data.json.log \
  --import=/tmp/data.json \
  --collection=books
```

### 4.2 来自HTTP POST上传的JSON文件

此外，如果我们创建一个[REST控制器](https://www.baeldung.com/spring-controller-vs-restcontroller)，我们将有一个端点来上传和导入JSON文件。为此，我们需要一个[MultipartFile](https://www.baeldung.com/spring-rest-template-multipart-upload)参数：

```java
@RestController
@RequestMapping("/import-json")
public class ImportJsonController {
    @Autowired
    private ImportJsonService service;

    @PostMapping("/file/{collection}")
    public String postJsonFile(@RequestPart("parts") MultipartFile jsonStringsFile, @PathVariable String collection)  {
        List<String> jsonLines = ImportUtils.lines(jsonStringsFile);
        return service.importTo(collection, jsonLines);
    }
}
```

现在我们可以像这样使用[POST](https://www.baeldung.com/curl-rest)导入文件，其中“/tmp/data.json”指的是现有文件：

```shell
curl -X POST http://localhost:8082/import-json/file/books -F "parts=@/tmp/books.json"
```

### 4.3 将JSON映射到特定的Java类型

我们一直只使用JSON，不绑定任何类型，这是使用MongoDB的优势之一。**现在我们要验证我们的输入**。在这种情况下，让我们通过对我们的Service进行此更改来添加一个[ObjectMapper](https://www.baeldung.com/jackson-object-mapper-tutorial)：

```java
private <T> List<Document> generateMongoDocs(List<String> lines, Class<T> type) {
    ObjectMapper mapper = new ObjectMapper();

    List<Document> docs = new ArrayList<>();
    for (String json : lines) {
        if (type != null) {
            mapper.readValue(json, type);
        }
        docs.add(Document.parse(json));
    }
    return docs;
}
```

这样，如果指定了type参数，我们的映射器将尝试将我们的JSON字符串解析为该类型。**并且，对于默认配置，如果存在任何未知属性，将抛出异常**。下面是我们使用MongoDB [Repository](https://www.baeldung.com/spring-data-crud-repository-save)的简单bean定义：

```java
@Document("books")
public class Book {
    @Id
    private String id;
    private String name;
    private String genre;
    // getters and setters
}
```

现在，为了使用文档生成器的改进版本，让我们也更改此方法：

```java
public String importTo(Class<?> type, List<String> jsonLines) {
    List<Document> mongoDocs = generateMongoDocs(jsonLines, type);
    String collection = type.getAnnotation(org.springframework.data.mongodb.core.mapping.Document.class).value();
    int inserts = insertInto(collection, mongoDocs);
    return inserts + "/" + jsonLines.size();
}
```

现在，我们传递的不是集合的名称，而是传递一个Class。我们假设它具有我们在Book中使用的@Document注解，因此它可以检索集合名称。但是，由于注解类和文档类同名，因此我们必须指定整个包。

## 5. 总结

在本文中，我们介绍了从文件、资源或简单字符串中分离JSON输入并将其导入MongoDB。我们将此功能集中在一个Service类和一个工具类中，以便我们可以在任何地方重用它。**我们的用例包括一个CLI和一个REST端点选项，以及有关如何使用它的示例命令**。