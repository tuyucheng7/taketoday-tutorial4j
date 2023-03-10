## 1. 概述

在本Spring Data教程中，我们将讨论如何使用Spring Data Repository和模板抽象为Couchbase文档设置持久层，以及准备Couchbase以使用视图和/或索引支持这些抽象所需的步骤。

## 2. Maven依赖

首先，我们将以下Maven依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-couchbase</artifactId>
    <version>2.0.0.RELEASE</version>
</dependency>
```

请注意，通过包含此依赖项，我们会自动获得原生Couchbase SDK的兼容版本，因此我们不需要显式包含它。

为了添加对JSR-303 Bean验证的支持，我们还包括以下依赖项：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>5.2.4.Final</version>
</dependency>
```

Spring Data Couchbase通过传统的Date和Calendar类以及通过JodaTime库支持日期和时间持久化，我们包括如下：

```xml
<dependency>
    <groupId>joda-time</groupId>
    <artifactId>joda-time</artifactId>
    <version>2.9.2</version>
</dependency>
```

## 3. 配置

接下来，我们需要通过指定Couchbase集群的一个或多个节点以及存储文档的存储桶的名称和密码来配置Couchbase环境。

### 3.1 Java配置

对于Java类配置，我们只需扩展AbstractCouchbaseConfiguration类：

```java
@Configuration
@EnableCouchbaseRepositories(basePackages={"cn.tuyucheng.taketoday.spring.data.couchbase"})
public class MyCouchbaseConfig extends AbstractCouchbaseConfiguration {

    @Override
    protected List<String> getBootstrapHosts() {
        return Arrays.asList("localhost");
    }

    @Override
    protected String getBucketName() {
        return "tuyucheng";
    }

    @Override
    protected String getBucketPassword() {
        return "";
    }
}
```

如果你的项目需要对Couchbase环境进行更多自定义，你可以通过覆盖getEnvironment()方法来提供一个：

```java
@Override
protected CouchbaseEnvironment getEnvironment() {
    // ...
}
```

### 3.2 XML配置

这是XML中的等效配置：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans:beans xmlns:beans="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns="http://www.springframework.org/schema/data/couchbase
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/data/couchbase
        http://www.springframework.org/schema/data/couchbase/spring-couchbase.xsd">

    <couchbase:cluster>
        <couchbase:node>localhost</couchbase:node>
    </couchbase:cluster>

    <couchbase:clusterInfo login="tuyucheng" password="" />

    <couchbase:bucket bucketName="tuyucheng" bucketPassword=""/>

    <couchbase:repositories base-package="cn.tuyucheng.taketoday.spring.data.couchbase"/>
</beans:beans>
```

注意：“clusterInfo”节点接收集群凭据或存储桶凭据并且是必需的，以便库可以确定你的Couchbase集群是否支持N1QL(适用于NoSQL数据库的SQL超集，在Couchbase4.0及更高版本中可用)。

如果你的项目需要自定义Couchbase环境，你可以使用couchbase:env标签提供一个。

## 4. 数据模型

让我们创建一个实体类来表示要保存的JSON文档。我们首先用@Document注解该类，然后我们用@Id标注一个String字段来表示Couchbase文档键。

你可以使用来自Spring Data的@Id注解或来自原生Couchbase SDK的注解。请注意，如果你在同一个类中的两个不同字段上同时使用@Id注解，则使用Spring Data @Id注解标注的字段将优先，并将用作文档键。

为了表示JSON文档的属性，我们添加了用@Field标注的私有成员变量。我们使用@NotNull注解将某些字段标记为必填字段：

```java
@Document
public class Person {
    @Id
    private String id;

    @Field
    @NotNull
    private String firstName;

    @Field
    @NotNull
    private String lastName;

    @Field
    @NotNull
    private DateTime created;

    @Field
    private DateTime updated;

    // standard getters and setters
}
```

请注意，用@Id标注的属性仅表示文档键，不一定是存储的JSON文档的一部分，除非它也使用@Field进行标注，如下所示：

```java
@Id
@Field
private String id;
```

如果你想命名实体类中的字段与存储在JSON文档中的字段不同，只需限定其@Field注解，如以下示例所示：

```java
@Field("fname")
private String firstName;
```

这是一个示例，显示持久化的Person文档的外观：

```json
{
    "firstName": "John",
    "lastName": "Smith",
    "created": 1457193705667
    "_class": "cn.tuyucheng.taketoday.spring.data.couchbase.model.Person"
}
```

请注意，Spring Data会自动向每个文档添加一个包含实体的完整类名的属性。默认情况下，此属性名为“_class”，但你可以通过覆盖typeKey()方法在Couchbase配置类中覆盖它。

例如，如果你想指定一个名为“dataType”的字段来保存类名，你可以将它添加到你的Couchbase配置类中：

```java
@Override
public String typeKey() {
    return "dataType";
}
```

覆盖typeKey()的另一个常见原因是，如果你使用的Couchbase Mobile版本不支持以下划线前缀的字段。在这种情况下，你可以像前面的示例一样选择你自己的替代类型字段，或者你可以使用Spring提供的替代类型：

```java
@Override
public String typeKey() {
    // use "javaClass" instead of "_class"
    return MappingCouchbaseConverter.TYPEKEY_SYNCGATEWAY_COMPATIBLE;
}
```

## 5. Couchbase Repository

Spring Data Couchbase提供与其他Spring Data模块(如JPA)相同的内置查询和派生查询机制。

我们通过扩展CrudRepository<String, Person\>并添加一个可派生的查询方法来为Person类声明一个Repository接口：

```java
public interface PersonRepository extends CrudRepository<Person, String> {
    List<Person> findByFirstName(String firstName);
}
```

## 6. 通过索引支持N1QL

如果使用Couchbase 4.0或更高版本，则默认情况下，使用N1QL引擎处理自定义查询(除非它们相应的Repository方法使用@View注解以指示使用下一节中描述的后备视图)。

要添加对N1QL的支持，你必须在存储桶上创建主索引。你可以使用cbq命令行查询处理器创建索引(有关如何为你的环境启动cbq工具，请参阅Couchbase文档)并发出以下命令：

```sql
CREATE PRIMARY INDEX ON tuyucheng USING GSI;
```

在上面的命令中，GSI代表全局二级索引，它是一种特别适合优化临时N1QL查询以支持OLTP系统的索引类型，如果没有特别说明，它是默认的索引类型。

与基于视图的索引不同，GSI索引不会自动复制到集群中的所有索引节点，因此如果你的集群包含多个索引节点，你将需要在集群中的每个节点上创建每个GSI索引，并且你必须提供每个节点上的不同索引名称。

你还可以创建一个或多个二级索引。当你这样做时，Couchbase将根据需要使用它们来优化其查询处理。

例如，要在firstName字段上添加索引，请在cbq工具中发出以下命令：

```sql
CREATE INDEX idx_firstName ON tuyucheng(firstName) USING GSI;
```

## 7. 后备视图

对于每个Repository接口，你将需要在目标存储桶中创建一个Couchbase设计文档和一个或多个视图。设计文档名称必须是实体类名称的小驼峰形式(例如“person”)。

无论你运行的是哪个版本的Couchbase Server，都必须创建一个名为“all”的后备视图以支持内置的“findAll” Repository方法。这是我们的Person类的“all”视图的映射函数：

```javascript
function (doc, meta) {
    if(doc._class == "cn.tuyucheng.taketoday.spring.data.couchbase.model.Person") {
        emit(meta.id, null);
    }
}
```

使用4.0之前的Couchbase版本时，自定义Repository方法必须每个都有一个后备视图(后备视图的使用在4.0或更高版本中是可选的)。

后备视图的自定义方法必须使用@View注解，如下例所示：

```java
@View
List<Person> findByFirstName(String firstName);
```

后备视图的默认命名约定是使用“find”关键字后面的方法名称部分的小驼峰版本(例如“byFirstName”)。

以下是如何为“byFirstName”视图编写映射函数：

```javascript
function (doc, meta) {
    if(doc._class == "cn.tuyucheng.taketoday.spring.data.couchbase.model.Person" && doc.firstName) {
        emit(doc.firstName, null);
    }
}
```

你可以覆盖此命名约定并使用你自己的视图名称，方法是使用相应的后备视图名称限定每个@View注解。例如：

```java
@View("myCustomView")
List<Person> findByFirstName(String lastName);
```

## 8. 服务层

对于我们的服务层，我们定义了一个接口和两个实现：一个使用Spring Data Repository抽象，另一个使用Spring Data模板抽象。这是我们的PersonService接口：

```java
public interface PersonService {
    Person findOne(String id);

    List<Person> findAll();

    List<Person> findByFirstName(String firstName);

    void create(Person person);

    void update(Person person);

    void delete(Person person);
}
```

### 8.1 Repository Service

这是一个使用我们上面定义的Repository的实现：

```java
@Service
@Qualifier("PersonRepositoryService")
public class PersonRepositoryService implements PersonService {

    @Autowired
    private PersonRepository repo;

    public Person findOne(String id) {
        return repo.findOne(id);
    }

    public List<Person> findAll() {
        List<Person> people = new ArrayList<Person>();
        Iterator<Person> it = repo.findAll().iterator();
        while(it.hasNext()) {
            people.add(it.next());
        }
        return people;
    }

    public List<Person> findByFirstName(String firstName) {
        return repo.findByFirstName(firstName);
    }

    public void create(Person person) {
        person.setCreated(DateTime.now());
        repo.save(person);
    }

    public void update(Person person) {
        person.setUpdated(DateTime.now());
        repo.save(person);
    }

    public void delete(Person person) {
        repo.delete(person);
    }
}
```

### 8.2 Template Service

对于基于模板的实现，我们必须创建上面第7节中列出的后备视图。CouchbaseTemplate对象在我们的Spring上下文中可用，并且可以注入到服务类中。

下面是使用模板抽象的实现：

```java
@Service
@Qualifier("PersonTemplateService")
public class PersonTemplateService implements PersonService {
    private static final String DESIGN_DOC = "person";
    @Autowired
    private CouchbaseTemplate template;

    public Person findOne(String id) {
        return template.findById(id, Person.class);
    }

    public List<Person> findAll() {
        return template.findByView(ViewQuery.from(DESIGN_DOC, "all"), Person.class);
    }

    public List<Person> findByFirstName(String firstName) {
        return template.findByView(ViewQuery.from(DESIGN_DOC, "byFirstName"), Person.class);
    }

    public void create(Person person) {
        person.setCreated(DateTime.now());
        template.insert(person);
    }

    public void update(Person person) {
        person.setUpdated(DateTime.now());
        template.update(person);
    }

    public void delete(Person person) {
        template.remove(person);
    }
}
```

## 9. 总结

我们已经展示了如何配置项目以使用Spring Data Couchbase模块以及如何编写简单的实体类及其Repository接口。我们编写了一个简单的服务接口，并提供了一个使用Repository的实现和另一个使用Spring Data模板API的实现。

有关更多信息，请访问[Spring Data Couchbase](https://spring.io/projects/spring-data-couchbase)项目站点。