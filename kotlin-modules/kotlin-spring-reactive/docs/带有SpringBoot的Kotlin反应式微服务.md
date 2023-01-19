## 1. 概述

在本教程中，我们将使用 Kotlin 和 Spring Boot开发[反应式微服务应用程序。](https://spring.io/reactive)

我们的应用程序将公开一个 REST API，将数据保存在数据库中，并具有用于监控的端点。

## 2.用例

如今，我们中的许多人都在与健康问题作斗争，因此我们为我们的教程选择了一个健康追踪器应用程序。它允许人们创建自己的健康档案，保存发烧、血压等症状。稍后，用户可以查看有关其健康日志的报告。

让我们保持安全和健康，继续我们的旅程。

## 3. 应用设置

首先，我们要设置项目依赖项。这里我们使用Maven，但是Maven和Gradle都适合我们。

我们的应用程序继承自[spring-boot-starter-parent](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-parent)：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.2.5.RELEASE</version>
    <relativePath/>
</parent>

```

然后让我们将基本依赖项添加到pom.xml 中，这将允许我们使用 Kotlin 和 Java：

```xml
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-reflect</artifactId>
    <version>1.3.70</version>
</dependency>
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-stdlib-jdk8</artifactId>
    <version>1.3.70</version>
</dependency>

```

现在我们必须为 REST API 和持久性添加依赖项。

我们将为反应式 REST API使用[Spring Reactive Web ，因此我们添加了](https://www.baeldung.com/spring-webflux)[spring-boot-starter-webflux](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-webflux)和[jackson-module-kotlin 的依赖项](https://search.maven.org/search?q=g:com.fasterxml.jackson.module a:jackson-module-kotlin)。

响应式选项也可用于数据持久化，目前，Spring Milestone 存储库中提供了Spring Data [R2DBC的实验版本。](https://www.baeldung.com/r2dbc)让我们补充一点：

```xml
<repositories>
   ...
   <repository>
       <id>spring-milestone</id>
       <name>Spring Milestone Repository</name>
       <url>http://repo.spring.io/milestone</url>
   </repository>
</repositories>
```

我们还在 dependencyManagement 部分导入spring - [boot-bom-r2dbc](https://mvnrepository.com/artifact/org.springframework.boot.experimental/spring-boot-bom-r2dbc)，然后将[spring-boot-starter-data-r2dbc 添加](https://mvnrepository.com/artifact/org.springframework.boot.experimental/spring-boot-starter-data-r2dbc)到 dependencies 中：

```xml
<dependencyManagement>
    <dependencies>
        ...
        <dependency>
            <groupId>org.springframework.boot.experimental</groupId>
            <artifactId>spring-boot-bom-r2dbc</artifactId>
            <version>0.1.0.M3</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

现在我们必须选择我们的数据库。在本教程中，我们使用 H2 作为内存数据库，但此时也可以使用适用于[Postgres、MySQL 和 Microsoft SQL Server](https://r2dbc.io/)的 R2DBC 驱动程序。

这里我们添加 [r2dbc-h2](https://search.maven.org/artifact/io.r2dbc/r2dbc-h2/0.8.2.RELEASE/jar)依赖：

```xml
<dependency>
    <groupId>io.r2dbc</groupId>
    <artifactId>r2dbc-h2</artifactId>
</dependency>

```

最后，为了完成依赖关系，我们将添加[spring-boot-starter-actuator](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-actuator)来为我们的应用程序提供监控和管理 API。

最后，让我们创建应用程序类：

```java
@SpringBootApplication
class HealthTrackerApplication

fun main(args: Array<String>) {
    runApplication<HealthTrackerApplication>(args)
}
```

我们应该知道runApplication<HealthTrackerApplication>(args)是SpringApplication.run(HealthTrackerApplication::class.java, args) 的缩写形式。

## 4.模型

让我们对封装用户配置文件数据的配置文件数据类进行建模：

```java
@Table 
data class Profile(@Id var id:Long?, var firstName : String, var lastName : String,
  var birthDate: LocalDateTime)
```

当我们使用不带表名的@Table时，Spring 根据命名约定从类名生成表名。@Id是主键的标记。

然后，我们有HealthRecord数据类来封装一个配置文件的健康症状：

```java
@Table
data class HealthRecord(@Id var id: Long?, var profileId: Long?, var temperature: Double,
  var bloodPressure: Double, var heartRate: Double, var date: LocalDate)
```

HealthRecord依赖于Profile类，但不幸的是，Spring Data R2DBC目前不支持实体关联，因此我们使用了 profileId而不是 Profile实例。

## 5.数据库配置

目前，Spring Data R2DBC 的模式生成不可用。因此，我们必须自己以编程方式或使用脚本文件来完成。

在这里，让我们通过代码创建一个配置类来执行 DDL：

```java
@Configuration
class DBConfiguration(db: DatabaseClient) {
    init {
        val initDb = db.execute {
            """ CREATE TABLE IF NOT EXISTS profile (
                    id SERIAL PRIMARY KEY,
                    //other columns specifications
                );
                CREATE TABLE IF NOT EXISTS health_record(
                    id SERIAL PRIMARY KEY,
                    profile_id LONG NOT NULL,
                    //other columns specifications
                );
            """
        }
        initDb.then().subscribe()
    }
}
```

现在，我们已准备好设置持久性。

## 6. 资料库

在此步骤中，我们将创建所需的存储库接口。让我们从 ReactiveCrudRepository扩展 ProfileRepository接口：

```java
@Repository
interface ProfileRepository: ReactiveCrudRepository<Profile, Long>
```

ReactiveCrudRepository提供了类似save和findById的方法。

然后我们有 HealthRecordRepository和一个额外的方法来返回配置文件的健康记录列表。

同样，目前 Spring Data R2DBC 不支持查询派生，我们必须手动编写查询：

```java
@Repository
interface HealthRecordRepository: ReactiveCrudRepository<HealthRecord, Long> {
    @Query("select p. from health_record p where p.profile_id = :profileId ")
    fun findByProfileId(profileId: Long): Flux<HealthRecord>
}
```

## 7. 控制器

现在，我们需要公开 REST API 以注册新的配置文件。我们还需要端点来存储和检索健康记录。为简单起见，我们还将实体重用为数据传输对象。

让我们从ProfileController开始，它公开了一个用于配置文件注册的 API。我们通过构造函数在ProfileController中注入一个ProfileRepository的实例：

```java
@RestController
class ProfileController(val repository: ProfileRepository) {
    
    @PostMapping("/profile")
    fun save(@RequestBody profile: Profile): Mono<Profile> = repository.save(profile)
}
```

然后我们通过健康记录端点，一个用于存储数据，一个用于返回平均值。

此外，让我们有一个 AverageHealthStatus数据类，它封装了一个配置文件的平均健康记录：

```java
class AverageHealthStatus(var cnt: Int, var temperature: Double, 
  var bloodPressure: Double, var heartRate: Double)
```

这是 HealthRecordController：

```java
@RestController
class HealthRecordController(val repository: HealthRecordRepository) {

    @PostMapping("/health/{profileId}/record")
    fun storeHealthRecord(@PathVariable("profileId") profileId: Long, @RequestBody record: HealthRecord):
      Mono<HealthRecord> =
        repository.save(record)

    @GetMapping("/health/{profileId}/avg")
    fun fetchHealthRecordAverage(@PathVariable("profileId") profileId: Long): Mono<AverageHealthStatus> =
        repository.findByProfileId(profileId)
            .reduce( / logic to calculate total /)
            .map { s ->
                / logic to calculate average from count and total /
            }

}
```

## 8. 监控端点

[Spring Boot Actuator](https://www.baeldung.com/spring-boot-actuators)公开端点来监控和管理我们的应用程序。当我们将 spring-boot-starter-actuator添加到依赖项时，默认情况下 会启用/health和 /info端点。我们可以通过在application.yml中设置management.endpoints.web.exposure.include属性的值来启用更多端点。

让我们为我们的应用程序启用/health和/metrics：


```plaintext
management.endpoints.web.exposure.include: health,metrics
```

我们还可以通过将值设置为  来启用所有端点。请注意，暴露所有端点可能会导致安全风险，因此我们需要额外的安全配置。

我们可以通过调用/actuator查看所有启用的执行器端点的列表。

要检查我们应用程序的健康状态，让我们调用http://localhost:8080/actuator/health。响应应该是：

```java
{
    "status": "UP"
}
```

这意味着我们的应用程序运行正常。

## 9. 测试

我们可以使用 WebTestClient来测试我们的端点。因此，作为一个实际示例，让我们测试配置文件 API。

首先，我们创建一个ProfileControllerTest类并用@SpringBootTest注解它：

```java
@SpringBootTest
class ProfileControllerTest {}
```

当一个类被@SpringBootTest 注解时， Spring 将在类包中向上搜索一个被@SpringBootConfiguration 注解的类。


然后让我们创建一个WebTestClient实例并将其绑定到ProfileController：


```java
@Autowired
lateinit var controller: ProfileController

@BeforeEach
fun setup() {
    client = WebTestClient.bindToController(controller).build()
}

```

现在我们已经准备好测试我们的/profile端点：

```java
@Test
fun whenRequestProfile_thenStatusShouldBeOk() {
    client.post()
      .uri("/profile")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(profile)
      .exchange()
      .expectStatus().isOk
}
```

## 10.总结

我们已经完成了使用 Kotlin 和 Spring Boot 创建微服务应用程序的旅程。在此过程中，我们学习了如何公开 REST API、监控端点和管理持久性。

安全性、服务调用和 API 网关等高级主题改天再讲。