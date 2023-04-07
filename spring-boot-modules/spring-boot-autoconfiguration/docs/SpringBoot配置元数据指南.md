## 1. 概述

在编写Spring Boot应用程序时，将[配置属性映射到Java Bean](https://www.baeldung.com/configuration-properties-in-spring-boot)上很有帮助。但是，记录这些属性的最佳方法是什么？

在本教程中，我们将探索[Spring Boot配置处理器](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-configuration-metadata.html#configuration-metadata-annotation-processor)和相关的[JSON元数据文件](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-configuration-metadata.html#configuration-metadata-format)，这些文件记录了每个属性的含义、约束等。

## 2. 配置元数据

我们作为开发人员开发的大多数应用程序必须在某种程度上是可配置的。但是，通常，我们并不真正了解配置参数的作用，它是否具有默认值，是否已被弃用，有时我们甚至不知道该属性是否存在。

为了帮助我们，Spring Boot在JSON文件中生成配置元数据，这为我们提供了有关如何使用这些属性的有用信息。因此，**配置元数据是一个描述性文件，其中包含与配置属性交互的必要信息**。

这个文件的真正好处是**IDE也可以读取它**，为我们提供Spring属性的自动完成以及其他配置提示。

## 3. 依赖

为了生成此配置元数据，我们将使用[spring-boot-configuration-processor](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-configuration-processor/3.0.5)依赖项中的配置处理器。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <version>2.1.6.RELEASE</version>
    <optional>true</optional>
</dependency>
```

此依赖项将为我们提供在构建项目时调用的Java注解处理器，稍后我们将详细讨论这一点。

最佳实践是在Maven中添加依赖项作为可选依赖项，以防止@ConfigurationProperties应用于我们项目使用的其他模块。

## 4. 配置属性示例

为了了解处理器的运行情况，让我们假设我们有一些属性需要通过Java Bean包含在Spring Boot应用程序中：

```java
@Configuration
@ConfigurationProperties(prefix = "database")
public class DatabaseProperties {

    private String username;
    private String password;
    private Server server;
    // getter setter ...

    public static class Server {
        private String ip;

        private int port;
        // getter setter ...
    }
}
```

为此，我们将使用[@ConfigurationProperties](https://www.baeldung.com/configuration-properties-in-spring-boot)注解。**配置处理器使用此注解扫描类和方法**，以访问配置参数并生成配置元数据。

让我们将其中几个属性添加到属性文件中。在这种情况下，我们将其命名为databaseproperties-test.properties：

```properties
#Simple Properties
database.username=tuyucheng
database.password=password
#Nested Properties
database.server.ip=127.0.0.1
database.server.port=3306
```

然后添加一个测试以确保一切正确：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AnnotationProcessorApplication.class)
@TestPropertySource(locations = "classpath:databaseproperties-test.properties")
class DatabasePropertiesIntegrationTest {

    @Autowired
    private DatabaseProperties databaseProperties;

    @Test
    void whenSimplePropertyQueried_thenReturnPropertyValue() {
        assertEquals("tuyucheng", databaseProperties.getUsername(), "Incorrectly bound Username property");
        assertEquals("password", databaseProperties.getPassword(), "Incorrectly bound Password property");
    }
}
```

我们还通过内部类Server添加了嵌套属性database.server.id和database.server.port。**注意内部类Server应该同样有对应字段的setter和getter方法**。

同样，编写一个测试以确保我们也可以成功设置和读取嵌套属性：

```java
@Test
void whenNestedPropertyQueriedThenReturnsPropertyValue() throws Exception {
    assertEquals("127.0.0.1", databaseProperties.getServer().getIp(), "Incorrectly bound Server IP nested property");
    assertEquals(3306, databaseProperties.getServer().getPort(), "Incorrectly bound Server Port nested property");
}
```

## 5. 生成配置元数据

我们之前提到，配置处理器会生成一个文件-它使用注解处理器来执行此操作。

因此，在编译我们的项目之后，我们可以**在target/classes/META-INF中看到一个名为spring-configuration-metadata.json的文件**：

<img src="../assets/img.png">

```json
{
    "groups": [
        {
            "name": "database",
            "type": "cn.tuyucheng.taketoday.autoconfiguration.annotationprocessor.DatabaseProperties",
            "sourceType": "cn.tuyucheng.taketoday.autoconfiguration.annotationprocessor.DatabaseProperties"
        },
        {
            "name": "database.server",
            "type": "cn.tuyucheng.taketoday.autoconfiguration.annotationprocessor.DatabaseProperties$Server",
            "sourceType": "cn.tuyucheng.taketoday.autoconfiguration.annotationprocessor.DatabaseProperties",
            "sourceMethod": "getServer()"
        }
    ],
    "properties": [
        {
            "name": "database.password",
            "type": "java.lang.String",
            "sourceType": "cn.tuyucheng.taketoday.autoconfiguration.annotationprocessor.DatabaseProperties"
        },
        {
            "name": "database.server.ip",
            "type": "java.lang.String",
            "sourceType": "cn.tuyucheng.taketoday.autoconfiguration.annotationprocessor.DatabaseProperties$Server"
        },
        {
            "name": "database.server.port",
            "type": "java.lang.Integer",
            "sourceType": "cn.tuyucheng.taketoday.autoconfiguration.annotationprocessor.DatabaseProperties$Server",
            "defaultValue": 0
        },
        {
            "name": "database.username",
            "type": "java.lang.String",
            "sourceType": "cn.tuyucheng.taketoday.autoconfiguration.annotationprocessor.DatabaseProperties"
        }
    ],
    "hints": []
}
```

接下来，让我们看看更改Java bean上的注解如何影响元数据。

### 5.1 有关配置元数据的其他信息

首先，让我们在Server上添加JavaDoc注释。

其次，让我们为database.server.port字段指定一个默认值，最后添加@Min和@Max注解：

```java
public static class Server {

    /**
     * The IP of the database server
     */
    private String ip;

    /**
     * The Port of the database server.
     * The Default value is 443.
     * The allowed values are in the range 400-4000.
     */
    @Min(400)
    @Max(800)
    private int port = 443;

    // standard getters and setters
}
```

如果我们现在检查spring-configuration-metadata.json文件，我们会看到这些额外的信息：

```json
{
    "groups": [
        {
            "name": "database",
            "type": "cn.tuyucheng.taketoday.autoconfiguration.annotationprocessor.DatabaseProperties",
            "sourceType": "cn.tuyucheng.taketoday.autoconfiguration.annotationprocessor.DatabaseProperties"
        },
        {
            "name": "database.server",
            "type": "cn.tuyucheng.taketoday.autoconfiguration.annotationprocessor.DatabaseProperties$Server",
            "sourceType": "cn.tuyucheng.taketoday.autoconfiguration.annotationprocessor.DatabaseProperties",
            "sourceMethod": "getServer()"
        }
    ],
    "properties": [
        {
            "name": "database.password",
            "type": "java.lang.String",
            "sourceType": "cn.tuyucheng.taketoday.autoconfiguration.annotationprocessor.DatabaseProperties"
        },
        {
            "name": "database.server.ip",
            "type": "java.lang.String",
            "description": "The IP of the database server",
            "sourceType": "cn.tuyucheng.taketoday.autoconfiguration.annotationprocessor.DatabaseProperties$Server"
        },
        {
            "name": "database.server.port",
            "type": "java.lang.Integer",
            "description": "The Port of the database server. The Default value is 443. The allowed values are in the range 400-4000",
            "sourceType": "cn.tuyucheng.taketoday.autoconfiguration.annotationprocessor.DatabaseProperties$Server",
            "defaultValue": 443
        },
        {
            "name": "database.username",
            "type": "java.lang.String",
            "sourceType": "cn.tuyucheng.taketoday.autoconfiguration.annotationprocessor.DatabaseProperties"
        }
    ],
    "hints": []
}
```

我们可以检查database.server.ip和database.server.port字段的差异。事实上，额外的信息非常有帮助。因此，开发人员和IDE更容易理解每个属性的作用。

我们还应该确保触发build以获取更新的文件，在Eclipse中，如果我们选中Build Automatically选项，则每个保存操作都会触发一次build。在IntelliJ中，我们应该手动触发build。

### 5.2 了解元数据格式

groups是用于对其他属性进行分组的更高级别的属性，无需指定值本身。在我们的示例中，我们有database group，它也是配置属性的前缀。我们还有一个server组，我们通过内部类创建它，并对ip和port属性进行分组。

properties是我们可以为其指定值的配置项。这些属性在.properties或.yml文件中设置，并且可以包含额外的信息，例如默认值和验证，如我们在上面的示例中看到的那样。

hints是帮助用户设置属性值的附加信息。例如，如果我们有一个属性的一组允许值，我们可以提供每个属性的描述。IDE将为这些提示提供自动完成帮助。

<img src="../assets/img_1.png">

配置元数据上的每个组件都有自己的[属性](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-configuration-metadata.html)，以更详细地解释配置属性。

## 6. 总结

在本文中，我们了解了Spring Boot配置处理器及其创建配置元数据的能力。使用此元数据可以更轻松地与我们的配置参数进行交互。

我们给出了一个生成的配置元数据的示例，并详细解释了它的格式和组件。