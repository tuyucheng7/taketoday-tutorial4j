## 1. 概述

在本教程中，我们将学习如何为我们的类配置MongoDB集合名称，以及一个实际示例。我们将使用[Spring Data](https://www.baeldung.com/spring-data-mongodb-tutorial)，它为我们提供了一些选项，只需很少的配置即可实现此目的。我们将通过构建一个简单的音乐商店来探索每个选项。这样，我们就可以找出何时使用它们是有意义的。

## 2. 用例和设置

我们的用例有四个简单的类：MusicAlbum、Compilation、MusicTrack和Store，**每个类都将以不同的方式配置其集合名称**。此外，每个类都有自己的MongoRepository，不需要自定义查询方法。此外，我们需要一个正确配置的[MongoDB](https://www.baeldung.com/spring-data-mongodb-tutorial)数据库实例。

### 2.1 按名称列出集合内容的服务

**首先，让我们编写一个[控制器](https://www.baeldung.com/spring-controllers)来断言我们的配置正在生效。我们将通过按集合名称搜索来做到这一点**。请注意，在使用Repository时，集合名称配置是透明的：

```java
@RestController
@RequestMapping("/collection")
public class CollectionController {
    @Autowired
    private MongoTemplate mongoDb;

    @GetMapping("/{name}")
    public List<DBObject> get(@PathVariable String name) {
        return mongoDb.findAll(DBObject.class, name);
    }
}
```

该控制器基于MongoTemplate并使用通用类型DBObject，它不依赖于类和Repository。此外，通过这种方式，我们将能够看到内部MongoDB属性。最重要的是，Spring Data用来解组JSON对象的“_class”属性保证了我们的配置是正确的。

### 2.2 API服务

其次，我们将开始构建我们的Service，它只是保存对象并检索它们的集合。**稍后将在我们的第一个配置示例中创建Compilation类**：

```java
@Service
public class MusicStoreService {
    @Autowired
    private CompilationRepository compilationRepository;

    public Compilation add(Compilation item) {
        return compilationRepository.save(item);
    }

    public List<Compilation> getCompilationList() {
        return compilationRepository.findAll();
    }

    // other service methods
}
```

### 2.3 API端点

最后，让我们编写一个控制器来与我们的应用程序交互。我们将为Service方法公开端点：

```java
@RestController
@RequestMapping("/music")
public class MusicStoreController {
    @Autowired
    private MusicStoreService service;

    @PostMapping("/compilation")
    public Compilation post(@RequestBody Compilation item) {
        return service.add(item);
    }

    @GetMapping("/compilation")
    public List<Compilation> getCompilationList() {
        return service.getCompilationList();
    }

    // other endpoint methods
}
```

之后，我们就可以开始配置我们的类了。

## 3. 配置@Document注解

从Spring Data版本1.9开始可用，@Document注解可以完成我们需要的一切。它特定于MongoDB，但类似于[JPA的@Entity](https://www.baeldung.com/jpa-entities)注解。**在撰写本文时，无法为集合名称定义[命名策略](https://www.baeldung.com/hibernate-naming-strategy)，只能为字段名称定义命名策略**。因此，让我们探索一下可用的内容。

### 3.1 默认行为

默认行为认为集合名称与类名称相同，但以小写字母开头。**简而言之，我们只需要添加Document注解，它就会起作用**：

```java
@Document
public class Compilation {
    @Id
    private String id;

    // getters and setters
}
```

之后，我们CompilationRepository中的所有插入都将进入MongoDB中名为“compilation”的集合：

```shell
$ curl -X POST http://localhost:8080/music/compilation -H 'Content-Type: application/json' -d '{
    "name": "Spring Hits"
}'

{ "id": "6272e26e04a673360d926ca1" }
```

让我们列出“compilation”集合的内容来验证我们的配置：

```shell
$ curl http://localhost:8080/collection/compilation

[
    {
        "name": "Spring Hits",
        "_class": "cn.tuyucheng.taketoday.boot.collection.name.data.Compilation"
    }
]
```

这是配置集合名称最干净的方法，因为它与我们的类名基本相同。**一个缺点是，如果我们决定改变我们的数据库命名约定，则需要重构我们所有的类**。例如，如果我们决定使用蛇形大小写作为集合名称，我们将无法利用默认行为。

### 3.2 重写value属性

@Document注解允许我们可以使用collection属性覆盖默认行为。由于此属性是value属性的别名，我们可以隐式设置它：

```java
@Document("albums")
public class MusicAlbum {
    @Id
    private String id;

    private String name;

    private String artist;

    // getters and setters
}
```

现在，文档不再进入名为“musicAlbum”的集合，而是进入“albums”集合。这是使用**Spring Data配置集合名称的最简单方法**。要查看它的实际效果，让我们添加一个MusicAlbum到我们的集合中：

```shell
$ curl -X POST 'http://localhost:8080/music/album' -H 'Content-Type: application/json' -d '{ 
    "name": "Album 1",
    "artist": "Artist A"
}'

{ "id": "62740de003d2452a61a75c35" }
```

然后，我们可以获取我们的“albums”集合，确保我们的配置有效：

```shell
$ curl 'http://localhost:8080/collection/albums'

[
    {
        "name": "Album 1",
        "artist": "Artist A",
        "_class": "cn.tuyucheng.taketoday.boot.collection.name.data.MusicAlbum"
    }
]
```

此外，它非常适合让我们的应用程序适应现有数据库，其中的集合名称与我们的类不匹配。一个缺点是如果我们需要添加默认前缀，则需要为每个类添加它。

### 3.3 将配置属性与SpEL一起使用

**这种组合可以帮助完成单独使用@Document注解无法完成的事情**，我们将从我们希望在我们的类中重用的特定于应用程序的属性开始。

**首先，让我们向[application.properties](https://www.baeldung.com/properties-with-spring)添加一个属性，我们将该属性用作集合名称的后缀**：

```properties
collection.suffix=db
```

现在，让我们通过Environment bean使用[SpEL](https://www.baeldung.com/spring-expression-language)引用它来创建我们的下一个类：

```java
@Document("store-#{@environment.getProperty('collection.suffix')}")
public class Store {
    @Id
    private String id;

    private String name;

    // getters and setters
}
```

然后，保存我们的第一个Store对象：

```shell
$ curl -X POST 'http://localhost:8080/music/store' -H 'Content-Type: application/json' -d '{
    "name": "Store A"
}'

{ "id": "62744c6267d3a034ec5e5719" }
```

因此，我们的类将存储在名为“store-db”的集合中。同样，我们可以通过列出其内容来验证我们的配置：

```shell
$ curl 'http://localhost:8080/collection/store-db'

[
    {
        "name": "Store A",
        "_class": "cn.tuyucheng.taketoday.boot.collection.name.data.Store"
    }
]
```

这样，如果我们更改后缀，就不必在每个类中手动更改它。相反，我们只是更新我们的属性文件。缺点是样板代码多，无论如何我们仍然必须编写类名。但是，它也可以帮助提供[多租户](https://www.baeldung.com/hibernate-5-multitenancy)支持。例如，**我们可以使用租户ID来标记未共享的集合，而不是后缀**。

### 3.4 将Bean方法与SpEL结合使用

使用SpEL的另一个缺点是它需要额外的工作来以编程方式进行评估。但是，它开辟了很多可能性，比如调用任何bean方法来确定我们的集合名称。因此，在我们的下一个示例中，**我们将创建一个bean来修复集合名称以符合我们的命名规则**。

在我们的命名策略中，我们将使用蛇形大小写。首先，让我们从Spring Data的SnakeCaseFieldNamingStrategy中借用代码来创建我们的实用程序bean：

```java
public class Naming {
    public String fix(String name) {
        List<String> parts = ParsingUtils.splitCamelCaseToLower(name);
        List<String> result = new ArrayList<>();

        for (String part : parts) {
            if (StringUtils.hasText(part)) {
                result.add(part);
            }
        }

        return StringUtils.collectionToDelimitedString(result, "_");
    }
}
```

接下来，让我们将该bean添加到我们的应用程序中：

```java
@SpringBootApplication
public class SpringBootCollectionNameApplication {

    // main method

    @Bean
    public Naming naming() {
        return new Naming();
    }
}
```

之后，我们就能够通过SpEL引用我们的bean：

```java
@Document("#{@naming.fix('MusicTrack')}")
public class MusicTrack {
    @Id
    private String id;

    private String name;

    private String artist;

    // getters and setters
}
```

让我们通过向我们的集合中添加一个元素来尝试一下：

```shell
$ curl -X POST 'http://localhost:8080/music/track' -H 'Content-Type: application/json' -d '{
    "name": "Track 1",
    "artist":"Artist A"
}'

{ "id": "62755987ae94c5278b9530cc" }
```

因此，我们的MusicTrack将存储在名为“music_track”的集合中：

```shell
$ curl 'http://localhost:8080/collection/music_track'

[
    {
        "name": "Track 1",
        "artist": "Artist A",
        "_class": "cn.tuyucheng.taketoday.boot.collection.name.data.MusicTrack"
    }
]
```

**不幸的是，没有办法动态获取类名，这是一个主要缺点**，但它允许更改我们的数据库命名规则，而无需手动重命名我们所有的类。

## 4. 总结

在本文中，我们探讨了使用Spring Data为我们提供的工具配置集合名称的方法。**此外，我们看到了每种方法的优点和缺点，因此我们可以决定哪种方法最适合特定场景**。在此期间，我们构建了一个简单的用例来演示不同的方法。