## 1. 概述

在本教程中，我们将了解Spring Data MongoDB中的[@DBRef](https://www.baeldung.com/cascading-with-dbref-and-lifecycle-events-in-spring-data-mongodb#dbref)注解。我们将使用此注解连接MongoDB文档。此外，我们还将看到MongoDB数据库引用的类型并进行比较。

## 2. MongoDB数据库手动引用

我们讨论的第一种类型称为手动引用。在MongoDB中，每个文档都必须有一个[id字段](https://www.baeldung.com/java-mongodb-last-inserted-id#what-is-the-id-of-a-mongodb-document)。因此，我们可以依靠使用它并将文档与它连接起来。

**使用手动引用时，我们将被引用文档的_id存储在另一个文档中**。

稍后，当我们从第一个集合中查询数据时，我们可以启动第二个查询来获取引用的文档。

## 3. Spring Data MongoDB @DBRef注解

DBRef类似于手动引用，因为它们也包含被引用文档的_id。**但是，它们在$ref字段中包含引用文档的集合，并且可选地在$db字段中包含其数据库**。

与手动引用相比，它的优势在于它阐明了我们引用的是哪个集合。

## 4. 应用程序设置

### 4.1 依赖

首先，我们需要添加所需的依赖项才能将MongoDB与Spring Boot结合使用。

让我们将[spring-boot-starter-data-mongodb](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-mongodb)添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

### 4.2 配置

现在，我们通过将以下配置添加到application.properties文件来设置连接：

```properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=person_database
```

让我们运行应用程序来测试我们是否可以连接到数据库。我们应该在日志中看到类似这样的消息：

```shell
Opened connection [connectionId{localValue:2, serverValue:37}] to localhost:27017
```

这意味着应用程序可以成功连接到MongoDB。

### 4.3 集合

**在MongoDB中，我们使用[集合](https://docs.mongodb.com/manual/core/databases-and-collections/#collections)来存储单个文档。它们是关系型数据库中表的对应物**。

在此示例中，我们将使用三种不同的数据类型：Person、Dog和Cat。我们会将人与他们的宠物联系起来。

首先，我们创建一个名为person_database的数据库，并在其中创建两个名为Dog和Cat的集合。我们将在每个文档中插入一个文档。为简化起见，它们都只有一个属性：宠物的名字。

让我们将此文档插入到Dog集合中：

```json
{
    _id: ObjectID("622112d71f9dac417b84227d"), 
    name: "Max"
}
```

然后，让我们将此文档插入Cat集合：

```json
{
    _id: ObjectID("622112781f9dac417b84227b"),
    name: "Loki"
}
```

现在，让我们创建Person集合并向其中插入一个文档：

```json
{
    _id: ObjectId(),
    name: "Bob",
    pets: [
        {
          "$ref": "Cat",
          "$id": "622112781f9dac417b84227b",
          "$db": ""
        },    
        {
          "$ref": "Dog",
          "$id": "622112d71f9dac417b84227d",
          "$db": ""
        }
    ]
}
```

我们将宠物作为数组提供。**数组的元素需要遵循[特定的格式](https://docs.mongodb.com/manual/reference/database-references/#format)才能将它们用作DBRefs**。我们需要在$ref属性中提供集合的名称，在这种情况下，它是Cat和Dog。然后，我们包括引用文档的ID。最后，如果我们想引用来自不同数据库的集合，我们可以选择在$db属性中包含一个数据库名称。

## 5. 使用@DBRef注解

**我们可以将之前创建的集合映射到Java类，类似于我们在使用关系数据库时所做的事情**。

为简化起见，我们不会为Dog和Cat数据类型创建单独的类。相反，我们将使用包含id和name的Pet类：

```java
public class Pet {
    private String id;
    private String name;

    @Override
    public String toString() {
        return "Pet [id=" + id + ", name=" + name + "]";
    }

    // standard getters and setters
}
```

现在，我们将创建Person类并通过@DBRef注解包含与Pet类的关联：

```java
@Document(collection = "Person")
public class Person {

    @Id
    private String id;

    private String name;

    @DBRef
    private List<Pet> pets;

    @Override
    public String toString() {
        return "Person [id=" + id + ", name=" + name + ", pets=" + pets + "]";
    }

    // standard getters and setters
}
```

接下来，让我们创建一个简单的[Repository](https://www.baeldung.com/spring-data-mongodb-tutorial#using-mongorepository)来查询数据：

```java
public interface PersonRepository extends MongoRepository<Person, String> {
}
```

我们将通过创建一个在启动应用程序时执行MongoDB查询的[ApplicationRunner](https://www.baeldung.com/running-setup-logic-on-startup-in-spring#7-spring-boot-applicationrunner)来测试所有内容。让我们重写run()方法并添加日志输出，以便我们可以看到Person集合的内容：

```java
@Override
public void run(ApplicationArguments args) throws Exception {
    logger.info("{}", personRepository.findAll());
}
```

这将生成类似于以下内容的日志输出，因为我们已经重写了实体类中的toString()方法：

```shell
dbref.mongodb.cn.tuyucheng.taketoday.DbRefTester : [Person [id=62249c5c7ffe83c50ad12700, name=Bob, pets=[Pet [id=622112781f9dac417b84227b, name=Loki], Pet [id=622112d71f9dac417b84227d, name=Max]]]]
```

这意味着我们成功读取并连接了来自不同集合的文档。

### 5.1 引用不同的数据库

**@DBRef注解接收两个参数。其中一个是db参数，它可以用于引用其他数据库中的文档**。

这是可选的，这意味着如果我们不提供此值，应用程序将在同一数据库中查找引用的文档。

在我们的例子中，如果Cat或Dog集合驻留在名为pet_database的不同数据库中，我们需要将注解更改为：@DBRef(db = "pet_database")。

### 5.2 延迟加载

**注解接收的另一个参数称为lazy。这是一个布尔值，用于确定是否应延迟加载引用的文档**。

默认情况下，它是false，这意味着当我们查询主实体时，将急切地加载引用实体。如果我们启用此功能，引用的文档将不会被加载，直到它们被首先访问。

## 6. 总结

在本文中，我们将MongoDB手动引用与Spring Data MongoDB @DBRef进行了比较。我们创建了三个集合并将它们与此注解联系起来。我们创建了一个Spring Boot应用程序来使用MongoRepository查询这些集合并显示相关文档。