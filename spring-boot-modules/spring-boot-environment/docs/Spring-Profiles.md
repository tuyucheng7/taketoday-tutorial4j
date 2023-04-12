## 1. 概述

在本文中，我们将重点介绍Spring中的Profile。

Profile是Spring框架的核心特性，**允许我们将bean映射到不同的profiles - 例如dev、test和prod**。

然后，我们可以在不同的环境中激活不同的profile，只加载我们需要的bean。

## 2. 在Bean上使用@Profile

**我们可以使用@Profile注解将bean映射到一个特定的profile**；注解可以指定一个(或多个)profile的名称。

考虑一个基本场景：假设我们有一个bean，它应该只在开发期间处于激活状态，而不应该在生产环境中存在。

我们使用@Profile("dev")标注该bean，它只会在dev环境下出现在容器中。在生产中，dev根本不会处于激活状态：

```java

@Component
@Profile("dev")
public class DevDatasourceConfig {

}
```

作为一个简短的补充说明，profile名称也可以加上非运算符作为前缀，例如!dev。

在该示例中，仅当当前环境不是dev时，bean才会处于激活状态：

```java

@Component
@Profile("!dev")
public class DevDatasourceConfig {

}
```

## 3. 在XML中声明Profiles

Profiles也可以在XML中配置。<beans\>标签有一个profile属性，它采用逗号分隔的方式定义profiles的值：

```
<beans profile="dev">
    <bean id="devDatasourceConfig" class="cn.tuyucheng.taketoday.profiles.DevDataSourceConfig" />
</beans>
```

## 4. 设置Profiles

下一步是激活并设置profiles，以便在容器中注册相应的bean。

这可以通过多种方式实现，我们将在以下部分中进行介绍。

### 4.1 通过WebApplicationInitializer接口以编程方式实现

在web应用程序中，可以使用WebApplicationInitializer以编程方式配置ServletContext。

它也是以编程方式设置我们的profile的非常方便的方式：

```java

@Configuration
public class MyWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) {
        servletContext.setInitParameter("spring.profiles.active", "dev");
    }
}
```

### 4.2 通过ConfigurableEnvironment以编程方式实现

我们还可以直接在ConfigurableEnvironment上设置profiles：

```
@Autowired
private ConfigurableEnvironment env;
...
env.setActiveProfiles("someProfile");
```

### 4.3 web.xml中的Context Parameter

同样，我们可以使用<context-param>在Web应用程序的web.xml文件中定义激活的profiles：

```
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>/WEB-INF/app-config.xml</param-value>
</context-param>
<context-param>
    <param-name>spring.profiles.active</param-name>
    <param-value>dev</param-value>
</context-param>
```

### 4.4 JVM系统参数

profile名称也可以通过JVM系统参数传入。这些profile将在应用程序启动期间激活：

```
-Dspring.profiles.active=dev
```

### 4.5 环境变量

在Unix环境中，**还可以通过环境变量激活profile**：

```
export spring_profiles_active=dev
```

### 4.6 Maven Profile

**通过指定spring.profiles.active配置属性**，也可以通过Maven profile激活Spring profile。

在每个Maven profile中，我们可以设置一个spring.profiles.active属性：

```
<profiles>
    <profile>
        <id>dev</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <spring.profiles.active>dev</spring.profiles.active>
        </properties>
    </profile>
    <profile>
        <id>prod</id>
        <properties>
            <spring.profiles.active>prod</spring.profiles.active>
        </properties>
    </profile>
</profiles>
```

**它的值将用于替换application.properties中的@spring.profiles.active@占位符**：

```properties
spring.profiles.active=@spring.profiles.active@
```

现在我们需要在pom.xml中启用资源过滤：

```
<build>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>true</filtering>
        </resource>
    </resources>
    ...
</build>
```

并添加-P参数来切换将应用哪个Maven profile：

```
mvn clean package -Pprod
```

此命令将使用prod profile打包应用程序。它还在此应用程序运行时将spring.profiles.active的值设置为prod。

### 4.7 测试中的@ActiveProfile

测试类可以很容易地使用@ActiveProfile注解指定哪些profile处于激活状态。

```
@ActiveProfiles("dev")
```

到目前为止，我们已经介绍了多种激活profile的方法。它们从最高到最低优先级依次为：

1. web.xml中的上下文参数
2. WebApplicationInitializer
3. JVM系统参数
4. 环境变量
5. Maven Profile

## 5. 默认的Profile

任何未指定profile的bean都属于default profile。

Spring还提供了一种在没有其他profile处于激活状态时设置默认profile的方法 - 通过使用spring.profiles.default属性。

## 6. 获取激活的Profiles

Spring的激活profile驱动@Profile注解的行为以启用/禁用bean。但是，我们也可能希望以编程方式访问激活的profiles集合。

我们有两种方法可以做到这一点，**使用Environment或spring.active.profile**。

### 6.1 使用Environment

我们可以通过注入Environment对象来访问激活的profile：

```java
public class ProfileManager {
    @Autowired
    private Environment environment;

    public void getActiveProfiles() {
        for (String profileName : environment.getActiveProfiles()) {
            System.out.println("Currently active profile - " + profileName);
        }
    }
}
```

### 6.2 使用spring.active.profile

或者，我们可以通过注入属性spring.profiles.active来访问profile：

```
@Value("${spring.profiles.active}")
private String activeProfile;
```

在这里，我们的activeProfile变量将包含当前激活的profile的名称，如果有多个，它将包含它们的名称，用逗号分隔。

但是，我们应该考虑如果根本没有激活的profile会发生什么。使用我们上面的代码，缺少激活的profile会阻止创建应用程序上下文。
由于缺少用于注入变量的占位符，这将导致IllegalArgumentException。

为了避免这种情况，我们可以**定义一个默认值**：

```
@Value("${spring.profiles.active:}")
private String activeProfile;
```

现在，如果没有激活的profile，我们的activeProfile仅仅为一个空字符串。

如果我们想像前面的例子一样以集合的方式访问它们，我们可以通过拆分activeProfile变量来实现：

```java
public class ProfileManager {
    @Value("${spring.profiles.active:}")
    private String activeProfiles;

    public String getActiveProfiles() {
        for (String profileName : activeProfiles.split(",")) {
            System.out.println("Currently active profile - " + profileName);
        }
    }
}
```

## 7. 使用profile进行单独的数据源配置

介绍了Profile的基础工作方式后，让我们看一个真实的例子。

考虑一个场景，**我们必须为开发和生产环境维护不同的DataSource配置**。

让我们创建一个需要由两个数据源实现的通用接口DataSourceConfig：

```java
public interface DataSourceConfig {
    void setup();
}
```

下面是开发环境的配置：

```java

@Component
@Profile("dev")
public class DevDataSourceConfig implements DataSourceConfig {

    @Override
    public void setup() {
        System.out.println("Setting up datasource for DEV environment. ");
    }
}
```

以及生产环境的配置：

```java

@Component
@Profile("production")
public class ProductionDataSourceConfig implements DataSourceConfig {

    @Override
    public void setup() {
        System.out.println("Setting up datasource for PRODUCTION environment. ");
    }
}
```

现在让我们创建一个测试并注入我们的DatasourceConfig bean；
根据激活的profile，Spring将注入DevDataSourceConfig或ProductionDataSourceConfig bean：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringProfilesConfig.class}, loader = AnnotationConfigContextLoader.class)
class SpringProfilesWithMavenPropertiesIntegrationTest {

    @Autowired
    DataSourceConfig datasourceConfig;

    @Test
    void setupDatasource() {
        datasourceConfig.setup();
        assertTrue(datasourceConfig instanceof DevDataSourceConfig);
    }
}
```

当dev profile处于激活状态时，Spring注入DevDataSourceConfig对象，然后调用setup()方法时，输出如下：

```
Setting up datasource for DEV environment. 
```

## 8. Spring Boot中的profile

Spring Boot支持目前为止提到的所有profile配置，并提供一些额外功能。

### 8.1 激活或设置Profile

第4节中介绍的初始化参数spring.profiles.active也可以设置为Spring Boot中的属性，以定义当前激活的profile。
这是Spring Boot将自动获取的标准属性：

```
spring.profiles.active=dev
```

但是，从Spring Boot 2.4开始，此属性不能与spring.config.activate.on-profile结合使用，
因为这可能会引发ConfigDataException(即InvalidConfigDataPropertyException或InactiveConfigDataAccessException)。

要以编程方式设置profile，我们还可以使用SpringApplication类：

```
SpringApplication.setAdditionalProfiles("dev");
```

要在Spring Boot中使用Maven设置profile，我们可以在pom.xml中的spring-boot-maven-plugin下指定profile名称：

```
<plugins>
    <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <configuration>
            <profiles>
                <profile>dev</profile>
            </profiles>
        </configuration>
    </plugin>
    ...
</plugins>
```

并执行Spring Boot特定的Maven goal：

```
mvn spring-boot:run
```

### 8.2 Profile特定的属性文件

Spring Boot带来的最重要的profiles相关功能是**profile特定的属性文件**。
这些文件必须以application-{profile}.properties格式命名。

Spring Boot将自动为所有profiles加载application.properties文件中的属性，
并且仅为指定的profile加载特定于profile的.properties文件中的属性。

例如，我们可以使用名为application-dev.properties和application-production.properties的两个文件为dev和production
profile配置不同的数据源：

在application-production.properties文件中，我们可以配置一个MySql数据源：

```properties
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/db
spring.datasource.username=root
spring.datasource.password=root
```

然后我们可以在application-dev.properties文件中为dev profile配置相同的属性，使用内存H2数据库：

```properties
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=sa
```

通过这种方式，我们可以很容易地为不同的环境提供不同的配置。

在Spring Boot 2.4之前，可以从特定于profile的文档中激活profile。但情况已不再如此；
对于更高版本，在这些情况下，框架将再次抛出InvalidConfigDataPropertyException或InactiveConfigDataAccessException。

### 8.3 多文档文件

为了进一步简化为不同环境定义属性，我们甚至可以将所有属性合并到同一个文件中，并使用分隔符来标识profile。

从2.4版本开始，除了之前支持的YAML之外，Spring Boot还扩展了对属性文件的多文档文件的支持。
现在，**我们可以在同一个application.properties中指定dev和production profile**：

```properties
my.prop=used-always-in-all-profiles
#---
spring.config.activate.on-profile=dev
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/db
spring.datasource.username=root
spring.datasource.password=root
#---
spring.config.activate.on-profile=production
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=sa
```

此文件由Spring Boot按从上到下的顺序读取。也就是说，如果某个属性，比如my.prop，在上述示例的末尾再次定义，则将考虑末尾定义的值。

### 8.4 Profile组

Spring Boot 2.4中添加的另一个功能是Profile组。顾名思义，它**允许我们将相似的profiles组合在一起**。

让我们考虑一个用例，其中我们有多个用于生产环境的profiles。比如说，一个用于数据库的proddb和一个用于生产环境中调度器的prodquartz。

要通过我们的application.properties文件一次性启用这些profiles，我们可以指定：

```properties
spring.profiles.group.production=proddb,prodquartz
```

因此，激活production profile也将激活proddb和prodquartz。

## 9. 总结

在本文中，我们讨论了如何在bean上定义Profile，以及如何在我们的应用程序中启用正确的Profile。

最后，我们通过一个简单但真实的示例巩固我们对Profile的理解。