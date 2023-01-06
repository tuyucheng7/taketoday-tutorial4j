## 1. 概述

Spring Boot使得使用不同的数据库系统变得非常容易，没有手动依赖管理的麻烦。

更具体地说，[Spring Data JPA starter提供了与多个]()[DataSource](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/javax/sql/DataSource.html)实现无缝集成所需的所有功能。


在本教程中，**我们将学习如何将Spring Boot与[HSQLDB](http://hsqldb.org/)集成**。

## 2. Maven依赖

为了演示将Spring Boot与HSQLDB集成有多么容易，我们将创建一个简单的JPA Repository层，该层使用内存中的HSQLDB数据库对客户实体执行CRUD操作。

下面是本文中需要用到的依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>2.2.2.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.hsqldb</groupId>
    <artifactId>hsqldb</artifactId>
    <version>2.4.0</version>
    <scope>runtime</scope>
</dependency>
```

请注意，我们还包含了[HSQLDB](https://search.maven.org/search?q=g:org.hsqldb AND a:hsqldb)依赖项。如果没有它，Spring Boot会尝试通过[HikariCP自动为我们配置一个]()DataSource bean和一个JDBC连接池。

因此，**如果我们没有在pom.xml文件中指定有效的DataSource依赖项，我们的构建将失败**。

## 3. 连接到HSQLDB数据库

作为演示我们将使用内存数据库，但是也可以使用基于文件的数据库。我们会在以下部分探讨这些方法中的每一种。

### 3.1 运行外部HSQLDB服务器

让我们来看看如何运行外部HSQLDB服务器并创建基于文件的数据库。总的来说，安装HSQLDB和运行服务器非常简单。

以下是我们应该遵循的步骤：

-   首先，我们[下载HSQLDB](https://sourceforge.net/projects/hsqldb/files/latest/download)并将其解压缩到一个文件夹中

-   由于HSQLDB不提供开箱即用的默认数据库，我们创建一个名为“testdb”的数据库作为演示使用

-   启动命令提示符并进入到HSQLDB的data文件夹

-   在data文件夹中，运行以下命令：

    ```bash
    java -cp ../lib/hsqldb.jar org.hsqldb.server.Server --database.0 file.testdb --dbname0.testdb
    ```

-   上面的命令将启动HSQLDB服务器并创建我们的数据库，其源文件将存储在data文件夹中

-   我们可以通过转到data文件夹来确保数据库确实已创建，该文件夹应包含一组名为“testdb.lck”、“testdb.log”、“testdb.properties”和“testdb.script”的文件(文件的数量取决于我们正在创建的数据库类型)

**数据库设置完成后，我们需要创建一个到它的连接**。

要在Windows上执行此操作，我们转到数据库bin文件夹并运行runManagerSwing.bat文件。这将打开HSQLDB数据库管理器的初始屏幕，我们可以在其中输入连接凭据：

-   **Type**：HSQL数据库引擎
-   **URL**：jdbc:hsqldb:hsql://localhost/testdb
-   **User**：“SA”(系统管理员)
-   **Password**：保留为空

在Linux/Unix/Mac上，我们可以使用NetBeans、Eclipse或IntelliJ IDEA通过IDE的可视化工具使用相同的凭据创建数据库连接。

在这些工具中，都可以通过在数据库管理器或IDE中执行SQL脚本来直接创建数据库表。

连接到数据库后，我们可以创建一个customers表：

```sql
CREATE TABLE customers
(
    id    INT NOT NULL,
    name  VARCHAR(45),
    email VARCHAR(45),
    PRIMARY KEY (ID)
);
```

通过几个简单的步骤，我们创建了一个包含customers表的基于文件的HSQLDB数据库。

### 3.2 application.properties 文件_

如果我们希望从Spring Boot连接到上面配置的基于文件的数据库，以下是我们应该包含在application.properties文件中的设置：

```properties
spring.datasource.driver-class-name=org.hsqldb.jdbc.JDBCDriver
spring.datasource.url=jdbc:hsqldb:hsql://localhost/testdb
spring.datasource.username=sa
spring.datasource.password= 
spring.jpa.hibernate.ddl-auto=update
```

或者，如果我们使用内存数据库，则配置如下：

```properties
spring.datasource.driver-class-name=org.hsqldb.jdbc.JDBCDriver
spring.datasource.url=jdbc:hsqldb:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create
```

请注意附加到数据库URL末尾的DB_CLOSE_DELAY=-1参数。当使用内存数据库时，我们需要指定它，这样JPA 实现(即Hibernate)在应用程序运行时不会关闭数据库。

## 4. 客户实体

接下来定义我们的Customer实体：

```java
@Entity
@Table(name = "customers")
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    private String name;
    
    private String email;

    // standard constructors / setters / getters / toString
}
```

## 5. Repository

此外，我们需要实现一个轻量的持久层，它允许我们在Customer JPA实体上拥有基本的CRUD功能。

我们可以通过扩展[CrudRepository](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html)接口简单实现这一层：

```java
@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {}
```

## 6. 测试Repository

最后，我们应该确保Spring Boot确实可以连接到HSQLDB，我们可以通过仅测试Repository层来轻松实现这一点。

以下的测试用例测试Repository的findById()和findAll()方法：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerRepositoryTest {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Test
    public void whenFindingCustomerById_thenCorrect() {
        customerRepository.save(new Customer("John", "john@domain.com"));
        assertThat(customerRepository.findById(1L)).isInstanceOf(Optional.class);
    }
    
    @Test
    public void whenFindingAllCustomers_thenCorrect() {
        customerRepository.save(new Customer("John", "john@domain.com"));
        customerRepository.save(new Customer("Julie", "julie@domain.com"));
        assertThat(customerRepository.findAll()).isInstanceOf(List.class);
    }
}
```

最后测试一下我们的save()方法：

```java
@Test
public void whenSavingCustomer_thenCorrect() {
    customerRepository.save(new Customer("Bob", "bob@domain.com"));
    Customer customer = customerRepository.findById(1L).orElseGet(() -> new Customer("john", "john@domain.com"));
    assertThat(customer.getName()).isEqualTo("Bob");
}
```

## 7. 总结

在本文中，我们学习了如何将Spring Boot与HSQLDB集成，以及如何在基本JPA Repository层的开发中使用基于文件或内存的数据库。