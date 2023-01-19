## 1. 概述

早在 1 月份，Spring 生态系统就发布了一个重大公告：Spring Framework 5 即将支持 Kotlin。这意味着Spring Boot 2.x将对Kotlin提供一流的支持。

这当然并不意外，因为 Pivotal 的团队以接受 Scala 和 Groovy 等 JVM 语言而闻名。

让我们使用Spring Boot应用程序 2.x 构建一个Kotlin应用程序！

## 2.设置

### 2.1. 环境

Kotlin 支持在[IntelliJ](https://kotlinlang.org/docs/tutorials/getting-started.html)、[Eclipse](https://kotlinlang.org/docs/tutorials/getting-started-eclipse.html)和[命令行](https://kotlinlang.org/docs/tutorials/command-line.html)中进行开发。根据你的喜好，按照说明设置你的环境。

### 2.2. 设置

首先，让我们创建一个Spring Boot 2项目并修改 POM 以包含指定Java和Kotlin版本及其依赖项的条目：

```xml
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-stdlib-jre8</artifactId>
    <version>1.2.71</version>
</dependency>
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-reflect</artifactId>
    <version>1.2.71</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.module</groupId>
    <artifactId>jackson-module-kotlin</artifactId>
    <version>2.9.9</version>
</dependency>
```

请注意，我们正在为Kotlin源文件和测试文件指定文件位置：

```xml
<sourceDirectory>${project.basedir}/src/main/kotlin</sourceDirectory>
<testSourceDirectory>${project.basedir}/src/test/kotlin</testSourceDirectory>
```

如果我们的Kotlin文件位于不同的位置，你将需要修改 POM 中的这些条目。

要编译Kotlin模块和源代码，我们需要使用[kotlin-maven-plugin](https://search.maven.org/classic/#search|gav|1|g%3A"org.jetbrains.kotlin" AND a%3A"kotlin-maven-plugin")：

```xml
<plugin>
    <artifactId>kotlin-maven-plugin</artifactId>
    <groupId>org.jetbrains.kotlin</groupId>
    <version>1.1.2</version>
    <configuration>
        <compilerPlugins>
            <plugin>spring</plugin>
        </compilerPlugins>
        <jvmTarget>1.8</jvmTarget>
    </configuration>
    <executions>
        <execution>
            <id>compile</id>
            <phase>compile</phase>
            <goals>
                <goal>compile</goal>
            </goals>
        </execution>
        <execution>
            <id>test-compile</id>
            <phase>test-compile</phase>
            <goals>
                <goal>test-compile</goal>
            </goals>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-maven-allopen</artifactId>
            <version>1.1.2</version>
        </dependency>
    </dependencies>
</plugin>
```

好了，现在我们拥有构建Kotlin应用程序所需的一切。作为参考：你可以找到最新版本的Maven Central([spring-boot-starter-web](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-web")、[kotlin-stdlib-jre8](https://search.maven.org/classic/#search|gav|1|g%3A"org.jetbrains.kotlin" AND a%3A"kotlin-stdlib-jre8")、[kotlin-reflect](https://search.maven.org/classic/#search|gav|1|g%3A"org.jetbrains.kotlin" AND a%3A"kotlin-reflect")、[jackson-module-kotlin](https://search.maven.org/classic/#search|gav|1|g%3A"com.fasterxml.jackson.module" AND a%3A"jackson-module-kotlin")、[test](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-test"))。

接下来，让我们设置我们的应用程序上下文。

## 3.应用场景

让我们跳入一些Kotlin代码并编写我们熟悉的Spring Boot应用程序上下文：

```java
@SpringBootApplication
class KotlinDemoApplication

fun main(args: Array<String>) {
    SpringApplication.run(KotlinDemoApplication::class.java, args)
}
```

我们看到了我们熟悉的@SpringBootApplication注解。这与我们将在Java类中使用的注解相同。

下面是我们的KotlinDemoApplication类的类定义。在Kotlin中，类的默认作用域是公共的，因此我们可以忽略它。此外，如果一个类没有变量也没有函数，它可以不用花括号声明。所以，本质上，我们只是定义了一个类。

继续方法。这是标准的Java入口点方法，在Java中：public static void main(String[] args)。

同样，默认情况下方法或函数是公共的，所以我们不必在这里声明。此外，不返回任何内容的函数不需要指定 void 返回类型。

最后，在类主体之外定义的任何函数都是自动静态的。这使得该函数符合启动执行条件。

现在让我们使用mvn spring-boot: run从根目录运行我们的应用程序。应用程序应该启动，我们应该看到我们的应用程序在端口 8080 上运行。

接下来，让我们构建一个控制器。

## 4.控制器

让我们看一下向我们的服务添加控制器：

```java
@RestController
class HelloController {
 
    @GetMapping("/hello")
    fun helloKotlin(): String {
        return "hello world"
    }
}
```

与标准的Spring控制器没有太大区别，但代码肯定更少。让我们为这个控制器添加一个测试类和用例来验证我们的工作：

```java
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(KotlinDemoApplication::class), 
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KotlinDemoApplicationTests {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun whenCalled_shouldReturnHello() {
        val result = testRestTemplate
          // ...
          .getForEntity("/hello", String::class.java)

        assertNotNull(result)
        assertEquals(result?.statusCode, HttpStatus.OK)
        assertEquals(result?.body, "hello world")
    }
}
```

这个测试展示了Kotlin的一个非常强大的特性——空安全！可以为 null 的Kotlin变量必须使用“?”声明。然后编译器知道在访问该属性之前需要防御性编码。

在我们的测试中，TestRestTemplate被定义为可空类型，每次访问它时，我们都会使用空合并运算符“?”。– 如果被调用对象为空，它将返回空。

这澄清了 null 在程序中的使用，并强制开发人员在使用它们时编写安全代码。

接下来，让我们添加一个服务并将其集成到我们的控制器中。

## 5.服务

正如你现在可能猜到的那样，我们的服务将非常容易添加到我们的项目中。让我们现在这样做：

```java
@Service
class HelloService {
 
    fun getHello(): String {
        return "hello service"
    }
}
```

这里的服务非常简单，只有一个函数返回一个字符串。接下来，让我们将我们的服务连接到控制器中并使用它返回一个值：

```java
@RestController
class HelloController(val helloService: HelloService) {
 
    // ...
 
    @GetMapping("/hello-service")
    fun helloKotlinService(): String {
        return helloService.getHello()
    }
}
```

啊，这看起来不错！在Kotlin中，主构造函数可以与类声明内联定义。我们已经从构造函数中省略了@Autowired注解，因为一段时间以来它不是强制性的。

这些参数会自动转换为类中的字段。在Kotlin中它们被称为属性。没有定义 getter 或 setter；它们是自动创建的。当然，你可以根据需要覆盖这些默认值。

在Kotlin中，可以使用var或val定义类中的属性和函数中的变量。Var表示可变属性，val表示最终属性。这允许编译器检查非法访问。由于我们的HelloService是单例，我们将其连接为一个val以防止突变。

接下来，让我们为这个控制器方法添加一个测试：

```java
@Test
fun whenCalled_shouldReturnHelloService() {
    var result = testRestTemplate
      // ...
      .getForEntity("/hello-service", String::class.java)

    assertNotNull(result)
    assertEquals(result?.statusCode, HttpStatus.OK)
    assertEquals(result?.body, "hello service")
}
```

最后，让我们看看Kotlin中的 POJO 是什么样子的。

## 6.Kotlin数据类

在Java中，我们用普通的旧Java对象 POJO 表示数据对象。在Kotlin中，我们有一些东西可以让我们更简洁地表达这种类型的对象——数据类。

让我们编写一个数据对象以在我们的控制器中返回：

```java
data class HelloDto(val greeting: String)
```

那不是把戏。我不会遗漏我们班上的任何东西。有了数据修饰符，我们得到了很多好处。该关键字自动创建一个equals/hashcode对、一个toString函数和一个函数。所有这一切都来自 53 个字符的单行！

现在让我们添加一个方法来返回我们的新数据类：

```java
// ...
@GetMapping("/hello-dto")
fun helloDto(): HelloDto {
    return HelloDto("Hello from the dto")
}
```

数据修饰符不添加默认构造函数，这对某些库(如 Jackson)很重要。为了支持这种类型的类，我们在 POM 文件中添加了jackson-module-kotlin以支持封送处理。这是在第 2 节中完成的，你可以在那里看到依赖关系。

最后，让我们为这个控制器功能添加一个测试：

```java
@Test
fun whenCalled_shoudlReturnJSON() {
    val result = testRestTemplate
      // ...
      .getForEntity("/hello-dto", HelloDto::class.java)

    assertNotNull(result)
    assertEquals(result?.statusCode, HttpStatus.OK)
    assertEquals(result?.body, HelloDto("Hello from the dto"))
}
```

## 七、总结

在本文中，我们了解了Spring Boot 2.x中的Kotlin支持。我们从示例中看到，Kotlin 可以通过强制我们编写更短、更安全的代码来简化和增强我们的应用程序。

Kotlin 还支持一些惊人的特性，如数据类、类扩展，并且与现有的Java代码完全兼容。这意味着你可以编写Kotlin代码并从你的Java类中调用它，反之亦然。此外，Kotlin 是从头开始构建的，以在 IDE 中提供出色的支持，而且确实如此。

尝试Kotlin的原因有很多，在 Google 和Spring的支持下，现在是时候尝试一下了。让我们知道你决定使用它构建什么！