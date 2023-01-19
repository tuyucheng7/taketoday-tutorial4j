## 1. 概述

在本教程中，我们将为[基于 Kotlin 的 Spring Boot 应用程序](https://www.baeldung.com/kotlin/spring-boot-kotlin)探索一些测试技术。

首先，让我们创建一个基于 Kotlin 的基本[Spring Boot 应用程序，其中包含一些组件，例如存储库、控制器和服务](https://www.baeldung.com/spring-component-annotation)。然后，我们可以讨论单元和集成测试技术。

## 2.设置

让我们用 Kotlin 复习一下 Spring Boot 应用程序的 Maven 依赖项。

首先，我们将最新的[spring-boot-starter-web](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-web)和[spring-boot-starter-data-jpa](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-data-jpa) Maven 依赖项添加到我们的pom.xml以支持 Web 和 JPA：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.6.1</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>2.6.1</version>
</dependency>
```

然后，让我们添加[h2 嵌入式数据库](https://search.maven.org/search?q=g:com.h2database a:h2)以实现持久化：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
    <version>2.0.202</version>
</dependency>
```

接下来，我们可以定义 Kotlin 代码的源目录：

```xml
<build>
    <sourceDirectory>${project.basedir}/src/main/kotlin</sourceDirectory>
    <testSourceDirectory>${project.basedir}/src/test/kotlin</testSourceDirectory>
    <plugins>
        // ...
    </plugins>
</build>
```

最后，我们将设置[kotlin-maven-plugin](https://search.maven.org/search?q=g:kotlin-maven-plugin a:kotlin-maven-plugin)插件来提供编译器插件，例如[all-open](https://www.baeldung.com/kotlin/allopen-spring)和[no-arg](https://www.baeldung.com/kotlin/jpa#compiler-plugins-jpa-plugin)以支持 Spring 和 JPA：

```xml
<plugin>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-maven-plugin</artifactId>
    <configuration>
        <args>
            <arg>-Xjsr305=strict</arg>
        </args>
        <compilerPlugins>
            <plugin>spring</plugin>
            <plugin>jpa</plugin>
            <plugin>all-open</plugin>
            <plugin>no-arg</plugin>
        </compilerPlugins>
        <pluginOptions>
            <option>all-open:annotation=javax.persistence.Entity</option>
            <option>all-open:annotation=javax.persistence.Embeddable</option>
            <option>all-open:annotation=javax.persistence.MappedSuperclass</option>
        </pluginOptions>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-maven-allopen</artifactId>
            <version>1.6.10</version>
        </dependency>
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-maven-noarg</artifactId>
            <version>1.6.10</version>
        </dependency>
    </dependencies>
</plugin>
```

## 3. Spring Boot 应用

现在我们的设置已经准备就绪。让我们向我们的 Spring Boot 应用程序添加一些组件以进行单元和集成测试。

### 3.1. 实体

首先，我们将创建具有一些属性的BankAccount实体，例如bankCode和accountNumber：

```kotlin
@Entity
data class BankAccount (
    var bankCode:String,
    var accountNumber:String,
    var accountHolderName:String,
    @Id @GeneratedValue var id: Long? = null
)

```

### 3.2. 资料库

然后，让我们创建BankAccountRepository类以使用[Spring Data 的](https://www.baeldung.com/spring-data-repositories#repositories)[CrudRepository](https://www.baeldung.com/spring-data-repositories#repositories)在BankAccount实体上提供 CRUD 功能：

```kotlin
@Repository
interface BankAccountRepository : CrudRepository<BankAccount, Long> {}
```

### 3.3. 服务

此外，我们将使用addBankAccount和getBankAccount等方法创建BankAccountService类：

```kotlin
@Service
class BankAccountService(var bankAccountRepository: BankAccountRepository) {
    fun addBankAccount(bankAccount: BankAccount): BankAccount {
        return bankAccountRepository.save(bankAccount);
    }
    fun getBankAccount(id: Long): BankAccount? {
        return bankAccountRepository.findByIdOrNull(id)
    }
}
```

### 3.4. 控制器

最后，让我们创建BankController类来公开/api/bankAccount端点：

```kotlin
@RestController
@RequestMapping("/api/bankAccount")
class BankController(var bankAccountService: BankAccountService) {

    @PostMapping
    fun addBankAccount(@RequestBody bankAccount:BankAccount) : ResponseEntity<BankAccount> {
        return ResponseEntity.ok(bankAccountService.addBankAccount(bankAccount))
    }

    @GetMapping
    fun getBankAccount(@RequestParam id:Long) : ResponseEntity<BankAccount> {
        var bankAccount: BankAccount? = bankAccountService.getBankAccount(id);
        if (bankAccount != null) {
            return ResponseEntity(bankAccount, HttpStatus.OK)
        } else {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }
}
```

因此，我们已经公开了具有 GET 和 POST 映射的端点，以分别读取和创建BankAccount对象。

## 4. 测试设置

### 4.1. JUnit5

首先，我们将[排除 JUnit 的复古支持](https://www.baeldung.com/junit-5)，它是[spring-boot-starter-test](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-test)依赖项的一部分：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <groupId>org.junit.vintage</groupId>
            <artifactId>junit-vintage-engine</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

然后，让我们包含[JUnit5 支持的 Maven 依赖项](https://www.baeldung.com/junit-5#dependencies)：

```xml
<dependency> 
    <groupId>org.junit.jupiter</groupId> 
    <artifactId>junit-jupiter-engine</artifactId> 
    <version>5.8.1</version> 
    <scope>test</scope> 
</dependency>
```

### 4.2. 模拟K

此外，我们应该在我们的测试中添加模拟功能，这些功能在测试服务和存储库组件时非常方便。

但是，我们将使用[更适合 Kotlin 的 MockK 库](https://www.baeldung.com/kotlin/mockk)[而不是Mockito](https://www.baeldung.com/kotlin/mockito)。

因此，让我们排除spring-boot-starter-test依赖附带的mockito-core依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

然后，我们可以将最新的[mockk](https://search.maven.org/search?q=g:com.ninja-squad a:springmockk) Maven 依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.ninja-squad</groupId>
    <artifactId>springmockk</artifactId>
    <version>3.0.1</version>
    <scope>test</scope>
</dependency>
```

## 5. 单元测试

### 5.1. 使用 MockK 测试服务

首先，让我们创建BankAccountServiceTest类并[使用 MockK](https://www.baeldung.com/kotlin/mockk#basic-example)[模拟BankAccountRepository](https://www.baeldung.com/kotlin/mockk#basic-example)：

```kotlin
class BankAccountServiceTest {
    val bankAccountRepository: BankAccountRepository = mockk();
    val bankAccountService = BankAccountService(bankAccountRepository);
}
```

然后，我们可以使用every块来模拟BankAccountRepository的响应并验证getBankAccount方法的结果：

```kotlin
@Test
fun whenGetBankAccount_thenReturnBankAccount() {
    //given
    every { bankAccountRepository.findByIdOrNull(1) } returns bankAccount;

    //when
    val result = bankAccountService.getBankAccount(1);

    //then
    verify(exactly = 1) { bankAccountRepository.findByIdOrNull(1) };
    assertEquals(bankAccount, result)
}
```

### 5.2. 使用@WebMvcTest测试控制器

我们可以使用[@WebMvcTest注解自动](https://www.baeldung.com/spring-boot-testing#unit-testing-with-webmvctest)为我们的单元测试配置 Spring MVC 基础设施。

首先，我们将注入MockMvc bean 并[使用](https://www.baeldung.com/java-spring-mockito-mock-mockbean#spring-boots-mockbean-annotation)[@MockkBean](https://www.baeldung.com/java-spring-mockito-mock-mockbean#spring-boots-mockbean-annotation)[注解](https://www.baeldung.com/java-spring-mockito-mock-mockbean#spring-boots-mockbean-annotation)[模拟BankAccountService](https://www.baeldung.com/java-spring-mockito-mock-mockbean#spring-boots-mockbean-annotation)：

```kotlin
@WebMvcTest
class BankControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean
    lateinit var bankAccountService: BankAccountService
}
```

然后，我们可以模拟BankAccountService 的响应，使用MockMvc bean的实例执行 GET 请求，并验证 JSON 结果：

```kotlin
@Test
fun givenExistingBankAccount_whenGetRequest_thenReturnsBankAccountJsonWithStatus200() {
    every { bankAccountService.getBankAccount(1) } returns bankAccount;

    mockMvc.perform(get("/api/bankAccount?id=1"))
      .andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.bankCode").value("ING"));
}
```

同样，我们可以验证导致错误请求的 GET 请求：

```kotlin
@Test
fun givenBankAccountDoesntExist_whenGetRequest_thenReturnsStatus400() {
    every { bankAccountService.getBankAccount(2) } returns null;

    mockMvc.perform(get("/api/bankAccount?id=2"))
      .andExpect(status().isBadRequest());
}
```

此外，我们可以使用MockMvc bean 以BankAccount JSON 作为请求主体执行 POST 请求并验证结果：

```kotlin
@Test
fun whenPostRequestWithBankAccountJson_thenReturnsStatus200() {
    every { bankAccountService.addBankAccount(bankAccount) } returns bankAccount;

    mockMvc.perform(post("/api/bankAccount").content(mapper.writeValueAsString(bankAccount)).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.bankCode").value("ING"));
}
```

## 6. 集成测试

### 6.1. 使用@DataJpaTest测试存储库

我们可以使用[@DataJpaTest注解为持久层提供标准设置](https://www.baeldung.com/spring-boot-testing#integration-testing-with-datajpatest)来测试存储库。

首先，我们将创建BankAccountRepositoryTest类并注入测试结束后回滚整个执行的TestEntityManager bean：

```kotlin
@DataJpaTest
class BankAccountRepositoryTest {
    @Autowired
    lateinit var entityManager: TestEntityManager
            
    @Autowired
    lateinit var bankAccountRepository: BankAccountRepository
}
```

然后，让我们使用 TestEntityManager 的实例测试BankAccountRepository的findByIdOrNull扩展方法：

```kotlin
@Test
fun WhenFindById_thenReturnBankAccount() {
    val ingBankAccount = BankAccount("ING", "123ING456", "JOHN SMITH");
    entityManager.persist(ingBankAccount)
    entityManager.flush()
    val ingBankAccountFound = bankAccountRepository.findByIdOrNull(ingBankAccount.id!!)
    assertThat(ingBankAccountFound == ingBankAccount)
}
```

### 6.2. 使用@SpringBootTest测试应用程序

我们可以使用[@SpringBootTest](https://www.baeldung.com/spring-boot-testing#integration-testing-with-springboottest)[注解在沙盒网络环境中启动我们的应用程序](https://www.baeldung.com/spring-boot-testing#integration-testing-with-springboottest)：

```kotlin
@SpringBootTest(
  classes = arrayOf(KotlinTestingDemoApplication::class),
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KotlinTestingDemoApplicationIntegrationTest {

    @Autowired
    lateinit var restTemplate: TestRestTemplate
}
```

此外，我们还注入[了TestRestTemplate](https://www.baeldung.com/spring-boot-testresttemplate) bean 来测试我们的应用公开[的 RESTful 端点。](https://www.baeldung.com/spring-boot-testresttemplate)

因此，让我们在/api/bankAccount端点上测试 GET 请求：

```kotlin
@Test
fun whenGetCalled_thenShouldBadReqeust() {
    val result = restTemplate.getForEntity("/api/bankAccount?id=2", BankAccount::class.java);

    assertNotNull(result)
    assertEquals(HttpStatus.BAD_REQUEST, result?.statusCode)
}
```

同样，我们可以使用TestRestTemplate实例来测试/api/bankAccount端点上的 POST 请求：

```kotlin
@Test
fun whePostCalled_thenShouldReturnBankObject() {
    val result = restTemplate.postForEntity("/api/bankAccount", BankAccount("ING", "123ING456", "JOHN SMITH"), BankAccount::class.java);

    assertNotNull(result)
    assertEquals(HttpStatus.OK, result?.statusCode)
    assertEquals("ING", result.getBody()?.bankCode)
}
```

## 七、总结

在本文中，我们讨论了使用 Kotlin 的 Spring Boot 应用程序的一些单元和集成测试技术。

首先，我们开发了一个基于 Kotlin 的 Spring Boot 应用程序，其中包含实体、存储库、服务和控制器。然后，我们探索了测试此类组件的不同方法。