## 1. 概述

在本教程中，我们将学习如何在Spring Boot中为MongoDB实现一个顺序的、自动生成的字段。

**当我们使用MongoDB作为Spring Boot应用程序的数据库时，我们不能在我们的模型中使用@GeneratedValue注解，因为它不可用**。因此，我们需要一种方法来产生与我们使用JPA和SQL数据库时相同的效果。

这个问题的一般解决方案很简单。我们将创建一个集合(表)，用于存储为其他集合生成的序列。在创建新记录期间，我们将使用它来获取下一个值。

## 2. 依赖

让我们将以下Spring Boot启动器添加到我们的 pom.xml中：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <versionId>2.2.2.RELEASE</versionId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
        <versionId>2.2.2.RELEASE</versionId>
    </dependency>
</dependencies>
```

依赖项的最新版本由[spring-boot-starter-parent](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-parent/3.0.3)管理。

## 3. Collections

正如概述中所讨论的，我们将创建一个集合，用于存储其他集合的自动递增序列。我们将这个集合称为database_sequences。它可以使用mongo shell或MongoDB Compass创建。让我们创建一个相应的模型类：

```java
@Document(collection = "database_sequences")
public class DatabaseSequence {

    @Id
    private String id;

    private long seq;

    // getters and setters omitted
}
```

然后让我们创建一个users集合和一个相应的模型对象，该对象将存储使用我们系统的人的详细信息：

```java
@Document(collection = "users")
public class User {

    @Transient
    public static final String SEQUENCE_NAME = "users_sequence";

    @Id
    private long id;

    private String email;

    // getters and setters omitted
}
```

在上面创建的User模型中，我们添加了一个静态字段SEQUENCE_NAME，它是对users集合的自增序列的唯一引用。

我们还使用@Transient对其进行标注，以防止它与模型的其他属性一起持久化。

## 4. 创建新记录

到目前为止，我们已经创建了所需的集合和模型。现在，我们创建一个Service，该Service将生成可以用作我们实体id的自动递增值。

让我们创建一个包含generateSequence()方法的SequenceGeneratorService：

```java
@Service
public class SequenceGeneratorService {

    private MongoOperations mongoOperations;

    @Autowired
    public SequenceGeneratorService(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public long generateSequence(String seqName) {
        DatabaseSequence counter = mongoOperations.findAndModify(Query.query(where("_id").is(seqName)),
              new Update().inc("seq", 1), options().returnNew(true).upsert(true),
              DatabaseSequence.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }
}
```

现在，我们可以在创建新记录时使用generateSequence()：

```java
User user = new User();
user.setId(sequenceGenerator.generateSequence(User.SEQUENCE_NAME));
user.setEmail("john.doe@example.com");
userRepository.save(user);
```

要列出所有用户，我们使用UserRepository：

```java
List<User> storedUsers = userRepository.findAll();
storedUsers.forEach(System.out::println);
```

**就像现在一样，我们每次创建模型的新实例时都必须设置id字段。我们可以通过为Spring Data MongoDB生命周期事件创建一个监听器来规避这个过程**。

**为此，我们将创建一个扩展AbstractMongoEventListener<User\>的UserModelListener类，然后我们将重写onBeforeConvert()方法**：

```java
@Component
public class UserModelListener extends AbstractMongoEventListener<User> {

    private SequenceGeneratorService sequenceGenerator;

    @Autowired
    public UserModelListener(SequenceGeneratorService sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }

    @Override
    public void onBeforeConvert(BeforeConvertEvent<User> event) {
        if (event.getSource().getId() < 1) {
            event.getSource().setId(sequenceGenerator.generateSequence(User.SEQUENCE_NAME));
        }
    }
}
```

现在，每次我们保存一个新用户时，id都会自动设置。

## 5. 总结

总之，我们已经了解了如何为id字段生成顺序的、自动递增的值，并模拟了SQL数据库中看到的相同的行为。

默认情况下，Hibernate使用类似的方法来生成自动递增的值。