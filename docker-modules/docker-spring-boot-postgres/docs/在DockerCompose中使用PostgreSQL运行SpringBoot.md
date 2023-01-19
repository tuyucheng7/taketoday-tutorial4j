## 1. 简介

在本教程中，我们介绍如何使用流行的开源数据库PostgreSQL运行Spring Boot应用程序。在[上一篇]()文章中，**我们介绍了[Docker Compose]()一次处理多个容器**，因此，我们将**使用Docker Compose来运行Spring Boot和PostgreSQL**，而不是将PostgreSQL作为单独的应用程序安装。

## 2. 创建Spring Boot项目

**首先通过[Spring Initializer](https://start.spring.io/)创建我们的Spring Boot项目**，添加PostgreSQL驱动程序和Spring Data JPA模块，下载生成的ZIP文件并将其解压缩到一个文件夹后，我们可以运行创建的新应用程序：

```shell
./mvnw spring-boot:run
```

程序的运行将失败，因为它无法连接到数据库：

```shell
APPLICATION FAILED TO START


Description:

Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.

Reason: Failed to determine a suitable driver class
```

## 3. Dockerfile

在我们可以使用Docker Compose启动PostgreSQL之前，**我们需要将我们的Spring Boot应用程序转换为Docker镜像**。第一步是将应用程序打包为JAR文件：

```shell
./mvnw clean package -DskipTests
```

在这里，我们首先清理之前以前的构建，然后再打包应用程序。此外，我们跳过了测试，因此测试用例在没有PostgreSQL的情况下会运行失败。

此时，我们的target目录中有一个应用程序JAR文件，该JAR文件的名称中包含项目名称和版本号，并以-SNAPSHOT.jar结尾，因此它的名字应该是docker-spring-boot-postgres-0.0.1-SNAPSHOT.jar。

然后我们创建一个新的src/main/docker目录，然后，我们将target目录中的JAR文件复制到该目录中：

```shell
cp target/docker-spring-boot-postgres-0.0.1-SNAPSHOT.jar src/main/docker
```

最后，我们在docker目录中创建一个Dockerfile：

```dockerfile
FROM adoptopenjdk:11-jre-hotspot
ARG JAR_FILE=.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]
```

**该文件描述了Docker应该如何运行我们的Spring Boot应用程序**，它使用来自[AdoptOpenJDK](https://adoptopenjdk.net/)的Java 11并将应用程序JAR文件复制到application.jar，然后它运行该JAR文件以启动我们的Spring Boot应用程序。

## 4. Docker Compose文件

现在我们编写一个Docker Compose文件docker-compose.yml，并将其保存在src/main/docker目录中：

```yaml
version: '2'

services:
    app:
        image: 'docker-spring-boot-postgres:latest'
        build:
            context: .
        container_name: app
        depends_on:
            - db
        environment:
            - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/compose-postgres
            - SPRING_DATASOURCE_USERNAME=compose-postgres
            - SPRING_DATASOURCE_PASSWORD=compose-postgres
            - SPRING_JPA_HIBERNATE_DDL_AUTO=update

    db:
        image: 'postgres:13.1-alpine'
        container_name: db
        environment:
            - POSTGRES_USER=compose-postgres
            - POSTGRES_PASSWORD=compose-postgres
```

**我们的应用程序的名称是app，这是两项服务中的第一项(第4-15行)**：

-   Spring Boot Docker镜像的名称为docker-spring-boot-postgres:latest(第5行)，Docker从当前目录中的Dockerfile构建该镜像(第6-7行)
-   容器名称为app(第8行)，该服务依赖于数据库服务(第10行)，这就是为什么它在db容器之后启动
-   我们的应用程序使用db PostgreSQL容器作为数据源(第12行)，数据库名、用户名、密码都是compose-postgres(12-14行)
-   Hibernate将自动创建或更新所需的任何数据库表(第15行)

**PostgreSQL数据库名为db，它是第二个服务(第17-22行)**：

-   我们使用的PostgreSQL版本为13.1(第18行)
-   容器名称为db(第19行)
-   用户名和密码都是compose-postgres(第21-22行)

## 5. 使用Docker Compose运行

**然后，我们可以使用Docker Compose运行我们的Spring Boot应用程序和PostgreSQL**：

```shell
docker-compose up
```

首先，这会为我们的Spring Boot应用程序构建Docker镜像；接下来，它将启动一个PostgreSQL容器；最后，它将启动我们的应用程序Docker映像。现在，我们的应用程序应该成功运行：

```shell
Starting DemoApplication v0.0.1-SNAPSHOT using Java 11.0.9 on f94e79a2c9fc with PID 1 (/application.jar started by root in /)
[...]
Finished Spring Data repository scanning in 28 ms. Found 0 JPA repository interfaces.
[...]
Started DemoApplication in 4.751 seconds (JVM running for 6.512)
```

从上面的输出中可以看到，Spring Data没有找到任何Repository接口，没关系，我们稍后会创建。

**如果我们想停止所有容器，我们需要先按[Ctrl-C]，然后我们可以停止Docker Compose**：

```shell
docker-compose down
```

## 6. 创建Custom实体和Repository

要在我们的应用程序中使用PostgreSQL数据库，我们首先创建一个简单的Custom实体：

```java
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;
}
```

Customer有一个生成的id属性和两个必填属性：firstName和lastName。

现在，我们可以**为这个实体创建一个Repository接口**：

```java
public interface CustomerRepository extends JpaRepository<Customer, Long> { }
```

通过简单地扩展JpaRepository，我们继承了创建和查询Customer实体的方法。

最后，我们在我们的应用程序中使用这些方法：

```java
@SpringBootApplication
public class DemoApplication {
    
    @Autowired
    private CustomerRepository repository;

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        List allCustomers = this.repository.findAll();
        logger.info("Number of customers: " + allCustomers.size());

        Customer newCustomer = new Customer();
        newCustomer.setFirstName("John");
        newCustomer.setLastName("Doe");
        logger.info("Saving new customer...");
        this.repository.save(newCustomer);

        allCustomers = this.repository.findAll();
        logger.info("Number of customers: " + allCustomers.size());
    }
}
```

-   我们通过依赖注入访问CustomRepository
-   我们使用Repository查询现有Custom的数量，这将为0
-   然后我们创建并保存一个Custom
-   当我们再次查询现有Custom时，我们应该能够得到刚刚创建的Custom

## 7. 再次运行Docker Compose

**要运行更新后的Spring Boot应用程序，我们需要先重新构建它**；因此，我们在项目根目录下再次执行这些命令：

```shell
./mvnw clean package -DskipTests
cp target/docker-spring-boot-postgres-0.0.1-SNAPSHOT.jar src/main/docker
```

我们如何使用这个更新后的应用程序JAR文件重建我们的Docker镜像？最好的方法是删除我们在docker-compose.yml中指定了名称的现有Docker镜像，这会强制Docker在我们下次启动Docker Compose文件时再次构建镜像：

```shell
cd src/main/docker
docker-compose down
docker rmi docker-spring-boot-postgres:latest
docker-compose up
```

因此，在停止我们的容器后，我们删除了应用程序Docker镜像。然后我们再次启动Docker Compose文件，它会重新构建应用程序镜像。

下面是应用程序输出：

```shell
Finished Spring Data repository scanning in 180 ms. Found 1 JPA repository interfaces.
[...]
Number of customers: 0
Saving new customer...
Number of customers: 1
```

Spring Boot找到我们新创建的CustomRepository，因此我们成功地创建了一个Custom。

## 8. 总结

在这个简短的教程中，我们首先为PostgreSQL创建一个Spring Boot应用程序；接下来，我们编写了一个Docker Compose文件来使用PostgreSQL容器运行我们的应用程序容器。

最后，我们创建了一个Custom实体和Repository，这使我们能够将Custom对象保存到PostgreSQL。