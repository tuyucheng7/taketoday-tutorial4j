## 1. 简介

在这篇简短的文章中，我们将通过一个简单示例来探索**Spring JPA Repository填充器**。Spring Data JPA Repository填充器是data.sql脚本的一个很好的替代品。

Spring Data JPA Repository填充器支持JSON和XML文件格式。在以下部分中，我们将了解如何使用Spring Data JPA Repository填充器。

## 2. 示例应用程序

首先，假设我们有一个Fruit实体类和一个水果清单来填充我们的数据库：

```java
@Entity
public class Fruit {
    @Id
    private long id;
    private String name;
    private String color;

    // getters and setters
}
```

我们将扩展JpaRepository以从数据库中读取Fruit数据：

```java
@Repository
public interface FruitRepository extends JpaRepository<Fruit, Long> {
    // ...
}
```

在下一节中，我们将使用JSON格式来存储和填充初始化水果数据。

## 3. JSON Repository填充器

让我们创建一个包含Fruit数据的JSON文件。我们将该文件命名为fruit-data.json并放在src/main/resources文件夹中：

```json
[
    {
        "_class": "cn.tuyucheng.taketoday.entity.Fruit",
        "name": "apple",
        "color": "red",
        "id": 1
    },
    {
        "_class": "cn.tuyucheng.taketoday.entity.Fruit",
        "name": "guava",
        "color": "green",
        "id": 2
    }
]
```

**实体类名称应在每个JSON对象的_class字段中给出**。其余的key映射到Fruit实体的列。

现在，我们将在pom.xml中添加[jackson-databind](https://central.sonatype.com/artifact/com.fasterxml.jackson.core/jackson-databind/2.14.2)依赖项：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.9.8</version>
</dependency>
```

最后，我们必须添加一个Repository填充器bean。这个Repository填充器bean将从fruit-data.json文件中读取数据，并在应用程序启动时将其填充到数据库中：

```java
@Configuration
public class JpaPopulators {

    @Bean
    public Jackson2RepositoryPopulatorFactoryBean getRespositoryPopulator() throws Exception {
        Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
        factory.setResources(new Resource[]{new ClassPathResource("fruit-data.json")});
        return factory;
    }
}
```

这是一个简单的单元测试：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest
class FruitPopulatorIntegrationTest {

    @Autowired
    private FruitRepository fruitRepository;

    @Test
    void givenFruitJsonPopulatorThenShouldInsertRecordOnStart() {

        List<Fruit> fruits = fruitRepository.findAll();
        assertEquals(2, fruits.size(), "record count is not matching");

        fruits.forEach(fruit -> {
            if (1 == fruit.getId()) {
                assertEquals("apple", fruit.getName());
                assertEquals("red", fruit.getColor());
            } else if (2 == fruit.getId()) {
                assertEquals("guava", fruit.getName());
                assertEquals("green", fruit.getColor());
            }
        });
    }
}
```

## 4. XML Repository填充器

在本节中，我们将了解如何将XML文件与Repository填充器一起使用。首先，我们将创建一个包含所需水果详细信息的XML文件。

在这里，一个XML文件代表一个水果的数据。

apple-fruit-data.xml

```xml
<fruit>
    <id>1</id>
    <name>apple</name>
    <color>red</color>
</fruit>
```

guava-fruit-data.xml：

```xml
<fruit>
    <id>2</id>
    <name>guava</name>
    <color>green</color>
</fruit>
```

同样，我们将这些XML文件保存在src/main/resources文件夹中。

此外，我们需要在pom.xml中添加[spring-oxm](https://central.sonatype.com/artifact/org.springframework/spring-oxm/6.0.6) Maven依赖项：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-oxm</artifactId>
    <version>5.1.5.RELEASE</version>
</dependency>
```

此外，我们需要在实体类中添加@XmlRootElement注解：

```java
@XmlRootElement
@Entity
public class Fruit {
    // ...
}
```

最后，我们将定义一个Repository填充器bean，这个bean将读取XML文件并填充数据：

```java
@Bean
public UnmarshallerRepositoryPopulatorFactoryBean repositoryPopulator() {
	Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
	unmarshaller.setClassesToBeBound(Fruit.class);
	
	UnmarshallerRepositoryPopulatorFactoryBean factory = new UnmarshallerRepositoryPopulatorFactoryBean();
	factory.setUnmarshaller(unmarshaller);
	factory.setResources(new Resource[]{new ClassPathResource("apple-fruit-data.xml"), new ClassPathResource("guava-fruit-data.xml")});
	
	return factory;
}
```

我们可以像使用JSON填充器一样对XML Repository填充器进行单元测试。

## 4. 总结

在本教程中，我们学习了如何使用Spring Data JPA Repository填充器。