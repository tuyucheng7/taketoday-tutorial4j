## 1. 概述

在本教程中，我们将学习如何将Spring Boot与Hibernate结合使用。

我们将构建一个简单的Spring Boot应用程序，并演示将它与Hibernate集成是多么容易。

## 2. 引导应用程序

我们将使用[Spring Initializr](https://start.spring.io/)来引导我们的Spring Boot应用程序。对于此示例，我们将仅使用所需的配置和依赖项来集成Hibernate，添加Web、JPA和H2依赖项。我们将在下一节中解释这些依赖关系。

现在让我们生成项目并在我们的IDE中打开它。我们可以检查生成的项目结构并确定我们需要的配置文件。

这是项目结构的样子：

<img src="../assets/img.png">

## 3. Maven依赖

如果我们打开pom.xml，我们会看到我们有spring-boot-starter-web和spring-boot-starter-test作为Maven依赖项。顾名思义，这些是Spring Boot中的启动器依赖项。

让我们快速浏览一下引入JPA的依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

该依赖项包括JPA API、JPA实现、JDBC和其他必要的库。由于默认的JPA实现是Hibernate，因此该依赖实际上也足以将其引入。

最后，我们将使用H2作为此示例的非常轻量级的数据库：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

我们可以使用H2控制台来检查数据库是否已启动并正在运行，还可以为我们的数据输入提供一个用户友好的GUI。我们将继续并在application.properties中启用它：

```properties
spring.h2.console.enabled=true
```

这就是我们需要配置的所有内容，以便在我们的示例中包含Hibernate和H2。当我们启动Spring Boot应用程序时，我们可以在日志上检查配置是否成功：

```shell
HHH000412: Hibernate Core {#Version}

HHH000206: hibernate.properties not found

HCANN000001: Hibernate Commons Annotations {#Version}

HHH000400: Using dialect: org.hibernate.dialect.H2Dialect
```
我们现在可以访问本地主机[http://localhost:8080/h2-console/](http://localhost:8080/h2-console/)上的H2控制台。

## 4. 创建实体

为了检查我们的H2是否正常工作，我们将首先在新models文件夹中创建一个JPA实体：

```java
@Entity
public class Book {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

    // standard constructors

    // standard getters and setters
}
```

我们现在有了一个基本实体，H2可以从中创建一个表。重新启动应用程序并检查H2控制台，将创建一个名为Book的新表。

要向我们的应用程序添加一些初始数据，我们需要创建一个包含一些insert语句的新SQL文件，并将其放在我们的resources文件夹中。**我们可以使用import.sql(Hibernate支持)或data.sql(Spring JDBC支持)文件来加载数据**。

以下是我们的示例数据：

```sql
insert into book values(1, 'The Tartar Steppe');
insert into book values(2, 'Poem Strip');
insert into book values(3, 'Restless Nights: Selected Stories of Dino Buzzati');
```

同样，我们可以重新启动Spring Boot应用程序并检查H2控制台；数据现在位于Book表中。

## 5. 创建Repository和Service

我们将继续创建基本组件以测试我们的应用程序。首先，我们将在新的repositories文件夹中添加JPA Repository：

```java
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
```

我们可以使用Spring框架中的JpaRepository接口，它为基本的CRUD操作提供了默认实现。

接下来，我们将BookService添加到一个新的services文件夹中：

```java
@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> list() {
        return bookRepository.findAll();
    }
}
```

为了测试我们的应用程序，我们需要检查创建的数据是否可以从服务的list()方法中获取。

我们将编写以下Spring Boot Test：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class BookServiceUnitTest {

    @Autowired
    private BookService bookService;

    @Test
    public void whenApplicationStarts_thenHibernateCreatesInitialRecords() {
        List<Book> books = bookService.list();

        Assert.assertEquals(books.size(), 3);
    }
}
```

通过运行这个测试，我们可以检查Hibernate是否创建了Book数据，然后由我们的服务成功获取了这些数据。就是这样，Hibernate与Spring Boot一起运行。

## 6. 大写表名

有时，我们可能需要将数据库中的表名以大写字母书写。正如我们已经知道的，**Hibernate默认情况下会以小写字母生成表的名称**。

我们可以尝试显式设置表名：

```java
@Entity(name="BOOK")
public class Book {
    // members, standard getters and setters
}
```

但是，那是行不通的。我们需要在application.properties中设置这个属性：

```properties
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
```

然后我们可以在我们的数据库中检查表是否使用大写字母成功创建。

## 7. 总结

在本文中，我们发现将Hibernate与Spring Boot集成是多么容易。我们使用H2数据库作为一种非常轻量级的内存解决方案。

我们给出了一个使用所有这些技术的应用程序的完整示例。然后我们给出了一个小提示，说明如何在我们的数据库中将表名设置为大写。