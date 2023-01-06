## 一、概述

在本教程中，我们将学习如何将 Spring Boot 与 Hibernate 结合使用。

我们将构建一个简单的 Spring Boot 应用程序，并演示将它与 Hibernate 集成是多么容易。

## 2. 引导应用程序

我们将使用[Spring Initializr](https://start.spring.io/)来引导我们的 Spring Boot 应用程序。对于此示例，我们将仅使用所需的配置和依赖项来集成 Hibernate，添加Web、JPA和H2依赖项。我们将在下一节中解释这些依赖关系。

现在让我们生成项目并在我们的 IDE 中打开它。我们可以检查生成的项目结构并确定我们需要的配置文件。

这是项目结构的样子：

[![spring boot 休眠项目](https://www.baeldung.com/wp-content/uploads/2019/03/spring_boot_hibernate_project.png)](https://www.baeldung.com/wp-content/uploads/2019/03/spring_boot_hibernate_project.png)

## 3.Maven依赖

如果我们打开pom.xml，我们会看到我们有spring-boot-starter-web和spring-boot-starter-test作为 maven 依赖项。顾名思义，这些是 Spring Boot 中的起始依赖项。

让我们快速浏览一下引入 JPA 的依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

这种依赖包括 JPA API、JPA 实现、JDBC 和其他必要的库。由于默认的 JPA 实现是 Hibernate，因此这种依赖实际上也足以将其引入。

最后，我们将使用H2作为此示例的非常轻量级的数据库：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

我们可以使用 H2 控制台来检查数据库是否已启动并正在运行，还可以为我们的数据输入提供一个用户友好的 GUI。我们将继续并在application.properites中启用它：

```xml
spring.h2.console.enabled=true
```

这就是我们需要配置的所有内容，以便在我们的示例中包含 Hibernate 和 H2。我们可以在启动Spring Boot应用的时候在日志中查看配置是否成功：

HHH000412：休眠核心 {#Version}

HHH000206: 未找到 hibernate.properties

HCANN000001：Hibernate Commons 注解 {#Version}

HHH000400：使用方言：org.hibernate.dialect.H2Dialect

我们现在可以访问本地主机http://localhost:8080/h2-console/上的 H2 控制台。

## 4.创建实体

为了检查我们的 H2 是否正常工作，我们将首先在新模型文件夹中创建一个 JPA 实体：

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

我们现在有了一个基本实体，H2 可以从中创建一个表。重新启动应用程序并检查 H2 控制台，将创建一个名为Book的新表。

要向我们的应用程序添加一些初始数据，我们需要创建一个包含一些插入语句的新 SQL 文件，并将其放在我们的资源文件夹中。我们可以使用 import.sql (Hibernate 支持)或 data.sql(Spring JDBC 支持)文件来加载数据。

这是我们的示例数据：

```sql
insert into book values(1, 'The Tartar Steppe');
insert into book values(2, 'Poem Strip');
insert into book values(3, 'Restless Nights: Selected Stories of Dino Buzzati');
```

同样，我们可以重新启动 Spring Boot 应用程序并检查 H2 控制台；数据现在位于Book表中。

## 5.创建存储库和服务

我们将继续创建基本组件以测试我们的应用程序。首先，我们将在新的存储库文件夹中添加 JPA 存储库：

```java
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
```

我们可以使用 Spring 框架中的JpaRepository接口，它为基本的CRUD操作提供了默认实现。

接下来，我们将BookService添加到一个新的服务文件夹中：

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

我们将编写以下SpringBootTest：

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

通过运行这个测试，我们可以检查 Hibernate 是否创建了Book数据，然后我们的服务成功获取了这些数据。就是这样，Hibernate 与 Spring Boot 一起运行。

## 6.大写表名

有时，我们可能需要将数据库中的表名以大写字母书写。正如我们已经知道的，Hibernate 默认情况下会以小写字母生成表的名称。

我们可以尝试显式设置表名：

```java
@Entity(name="BOOK")
public class Book {
    // members, standard getters and setters
}
```

但是，那是行不通的。我们需要在application.properties中设置这个属性：

```xml
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
```

然后我们可以在我们的数据库中检查是否成功创建了带有大写字母的表。

## 七、总结

在本文中，我们发现将 Hibernate 与 Spring Boot 集成是多么容易。我们使用 H2 数据库作为一种非常轻量级的内存解决方案。

我们给出了一个使用所有这些技术的应用程序的完整示例。然后我们给出了一个小提示，说明如何在我们的数据库中将表名设置为大写。