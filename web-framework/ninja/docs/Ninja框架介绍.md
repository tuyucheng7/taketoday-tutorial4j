## 1. 概述

如今，有许多基于 JEE 的框架，如[Spring](https://www.baeldung.com/spring-tutorial)、[Play](https://www.baeldung.com/java-intro-to-the-play-framework)和[Grails](https://www.baeldung.com/grails-gorm-tutorial)可用于 Web 应用程序开发。

我们可能有理由选择其中之一而不是其他。但是，我们的选择还取决于用例和我们要解决的问题。

在本入门教程中，我们将探索 Ninja Web 框架并创建一个简单的 Web 应用程序。同时，我们将研究它提供的一些基本功能。

## 2. 忍者

[Ninja](https://www.ninjaframework.org/)是一个全栈但轻量级的 Web 框架，它利用现有的Java库来完成工作。

具有从 HTML 到 JSON 渲染、持久化到测试的特性，它是构建可扩展 Web 应用程序的一站式解决方案。

它遵循 约定优于配置范例，并将代码分类为模型、控制器和服务等包。

Ninja 使用流行的Java库来实现关键功能，例如用于 JSON/XML 呈现的[Jackson](https://www.baeldung.com/jackson) 、用于依赖管理的[Guice](https://www.baeldung.com/guice) 、用于持久性的[Hibernate](https://www.baeldung.com/hibernate-5-bootstrapping-api)以及用于数据库迁移的[Flyway](https://www.baeldung.com/database-migrations-with-flyway)。

为了快速开发，它提供了用于热重载代码的[SuperDevMode 。](https://www.ninjaframework.org/documentation/basic_concepts/super_dev_mode.html)因此，它使我们能够立即看到开发环境中的变化。

## 3.设置

Ninja 需要一组标准工具来创建 Web 应用程序：

-  Java1.8 或更高版本
-   Maven 3 或更高版本
-   IDE(Eclipse 或 IntelliJ)

我们将使用[Maven 原型](https://www.baeldung.com/maven-archetype#creating-archetype)快速设置 Ninja 项目。它会提示我们提供组 ID、工件 ID 和版本号，然后是项目名称：

```shell
mvn archetype:generate -DarchetypeGroupId=org.ninjaframework 
  -DarchetypeArtifactId=ninja-servlet-archetype-simple
```

或者，对于现有的 Maven 项目，我们可以将最新的[ninja-core](https://search.maven.org/search?q=g:org.ninjaframework a:ninja-core)依赖项添加到pom.xml：

```xml
<dependency>
    <groupId>org.ninjaframework</groupId>
    <artifactId>ninja-core</artifactId>
    <version>6.5.0</version>
</dependency>
```

然后，我们将运行 Maven 命令来第一次编译文件：

```shell
mvn clean install
```

最后，让我们使用 Ninja 提供的 Maven 命令运行应用程序：

```shell
mvn ninja:run
```

瞧！我们的应用程序已启动，可以在localhost:8080访问：
[![家庭比例 1](https://www.baeldung.com/wp-content/uploads/2019/12/home-scaled-1.png)](https://www.baeldung.com/wp-content/uploads/2019/12/home-scaled-1.png)

## 4.项目结构

我们来看看Ninja创建的类Maven项目结构：

[![日食项目Structure-1](https://www.baeldung.com/wp-content/uploads/2019/12/eclipse_projectStructure-1.png)](https://www.baeldung.com/wp-content/uploads/2019/12/eclipse_projectStructure-1.png)该框架根据约定创建了一些包。

Java 类分类在src/main/java中的conf、controllers、models和services目录下。


同样， src/test/java包含相应的单元测试类。

src/main/java下的views目录包含 HTML 文件。而且，src/main/java/assets目录包含图像、样式表和 JavaScript 文件等资源。

## 5.控制器

我们都准备好讨论框架的一些基本功能。控制器是一个类，它接收请求并返回具有特定结果的响应。

首先，让我们讨论一些要遵循的约定：

-   在controllers包中创建一个类，名称后缀为Controller
-   为请求服务的方法必须返回[Result](http://www.ninjaframework.org/apidocs/ninja/Result.html)类的对象

让我们用一个简单的方法来创建ApplicationController类来呈现 HTML：

```java
@Singleton
public class ApplicationController {
    public Result index() {
        return Results.html();
    }
}
```

在这里，index方法将通过调用[Results](http://www.ninjaframework.org/apidocs/ninja/Results.html)类的html方法来呈现 HTML 。Result对象包含呈现内容所需的所有内容，例如响应代码、标头和 cookie。

注意：Guice 的[@Singleton](https://google.github.io/guice/api-docs/latest/javadoc/index.html?com/google/inject/Singleton.html)注解在整个应用程序中只允许一个控制器实例。

## 6.查看

对于index方法，Ninja会在views/ApplicationController目录下寻找 HTML 文件——index.ftl.html。

Ninja 使用[Freemarker](https://www.baeldung.com/freemarker-operations)模板引擎进行 HTML 渲染。因此，视图下的所有文件都应具有.ftl.html扩展名。

让我们为index方法创建索引.ftl.html文件：

```html
<html>  
<head>
    <title>Ninja: Index</title>
</head>
<body>
    <h1>${i18n("helloMsg")}</h1>
    <a href="/userJson">User Json</a>
</body>
</html>
```

在这里，我们使用了 Ninja 提供的i18n标签从message.properties文件中获取helloMsg属性。我们将在稍后的国际化部分进一步讨论这个问题。

## 7.路线

接下来，我们将定义请求到达索引方法的路由。

Ninja 使用conf包中的Routes类将 URL 映射到控制器的特定方法。

让我们添加一个路由来访问ApplicationController的index方法：

```java
public class Routes implements ApplicationRoutes {
    @Override
    public void init(Router router) {          
        router.GET().route("/index").with(ApplicationController::index);
    }
}
```

而已！我们都准备好访问位于localhost:8080/index 的索引页面：
[![索引缩放 1](https://www.baeldung.com/wp-content/uploads/2019/12/index-scaled-1-1024x169.png)](https://www.baeldung.com/wp-content/uploads/2019/12/index-scaled-1.png)

## 8. JSON 渲染

如前所述，Ninja 使用 Jackson 进行 JSON 渲染。要呈现 JSON 内容，我们可以使用Results类的json方法。

让我们在ApplicationController类中添加userJson方法，并在 JSON 中呈现一个简单的HashMap的内容：

```java
public Result userJson() {
    HashMap<String, String> userMap = new HashMap<>();
    userMap.put("name", "Norman Lewis");
    userMap.put("email", "norman@email.com");    
    return Results.json().render(user);
}
```

然后，我们将添加访问userJson所需的路由：

```java
router.GET().route("/userJson").with(ApplicationController::userJson);
```

现在，我们可以使用localhost:8080/userJson渲染 JSON ：
[![userJson 缩放 1](https://www.baeldung.com/wp-content/uploads/2019/12/userJson-scaled-1.png)](https://www.baeldung.com/wp-content/uploads/2019/12/userJson-scaled-1.png)

## 九、服务

我们可以创建一个服务来保持业务逻辑与控制器分离，并在需要的地方注入我们的服务。

首先，让我们创建一个简单的UserService接口来定义抽象：

```java
public interface UserService {
    HashMap<String, String> getUserMap();
}
```

然后，我们将在UserServiceImpl类中实现UserService接口并覆盖getUserMap方法：

```java
public class UserServiceImpl implements UserService {
    @Override
    public HashMap<String, String> getUserMap() {
        HashMap<String, String> userMap = new HashMap<>(); 
        userMap.put("name", "Norman Lewis"); 
        userMap.put("email", "norman@email.com"); 
        return userMap;
    }
}
```

然后，我们将使用 Guice 提供的 Ninja 依赖注入功能将UserService接口与UserServiceImpl类绑定。

让我们在conf包中可用的Module类中添加绑定：

```java
@Singleton
public class Module extends AbstractModule {
    protected void configure() {        
        bind(UserService.class).to(UserServiceImpl.class);
    }
}
```

最后，我们将使用[@Inject](https://google.github.io/guice/api-docs/latest/javadoc/index.html?com/google/inject/Inject.html)注解在ApplicationController类中注入UserService依赖项：

```java
public class ApplicationController {
    @Inject
    UserService userService;
    
    // ...
}
```

因此，我们都准备好在ApplicationController中使用UserService的getUserMap方法：

```java
public Result userJson() {
    HashMap<String, String> userMap = userService.getUserMap();
    return Results.json().render(userMap);
}
```

## 10.闪光范围

Ninja 通过其称为 Flash Scope 的功能提供了一种简单而有效的方法来处理来自请求的成功和错误消息。

要在控制器中使用它，我们将FlashScope参数添加到方法中：

```java
public Result showFlashMsg(FlashScope flashScope) {
    flashScope.success("Success message");
    flashScope.error("Error message");
    return Results.redirect("/home");
}
```

注意：Results类的重定向方法将目标重定向到提供的 URL。

然后，我们将添加一个路由/flash到showFlashMsg方法并修改视图以显示 flash 消息：

```html
<#if (flash.error)??>
    <div class="alert alert-danger">
        ${flash.error}
    </div>
</#if>
<#if (flash.success)??>
    <div class="alert alert-success">
        ${flash.success}
    </div>
</#if>
```

现在，我们可以在localhost:8080/flash看到正在运行的FlashScope：

[![闪光灯 1 缩放 2](https://www.baeldung.com/wp-content/uploads/2019/12/flash-1-scaled-2.png)](https://www.baeldung.com/wp-content/uploads/2019/12/flash-1-scaled-2.png)

## 11.国际化

Ninja 提供易于配置的内置国际化功能。

首先，我们将在application.conf文件中定义支持的语言列表：

```plaintext
application.languages=fr,en
```

然后，我们将创建默认属性文件——英语的messages.properties——带有消息的键值对：

```plaintext
header.home=Home!
helloMsg=Hello, welcome to Ninja Framework!
```

同样，我们可以在特定于语言的属性文件的文件名中添加语言代码——例如，法语的message_fr.properties文件：

```plaintext
header.home=Accueil!
helloMsg=Bonjour, bienvenue dans Ninja Framework!
```

配置准备就绪后，我们可以轻松地在ApplicationController类中启用国际化。

我们有两种方法，使用[Lang](https://www.ninjaframework.org/apidocs/ninja/i18n/Lang.html)类或[Messages](https://www.ninjaframework.org/apidocs/ninja/i18n/Messages.html)类：

```java
@Singleton
public class ApplicationController {
    @Inject
    Lang lang;

    @Inject
    Messages msg;
    
    // ...
}
```

然后，使用Lang类，我们可以设置结果的语言：

```java
Result result = Results.html();
lang.setLanguage("fr", result);
```

同样，使用Messages类，我们可以获得特定于语言的消息：

```java
Optional<String> language = Optional.of("fr");        
String helloMsg = msg.get("helloMsg", language).get();
```

## 12.坚持

Ninja 支持 JPA 2.0 并利用 Hibernate 在 Web 应用程序中启用持久性。此外，它还提供内置的 H2 数据库支持以实现快速开发。

### 12.1. 模型

我们需要一个实体类来连接数据库中的表。为此，Ninja 遵循在模型包中查找实体类的惯例。因此，我们将在那里创建User实体类：

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    Long id;
    public String firstName;
    public String email;  
}
```

然后，我们将配置 Hibernate 并设置数据库连接的详细信息。

### 12.2. 配置

对于 Hibernate 配置，Ninja 期望persistence.xml文件位于src/main/java/META-INF目录中：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://java.sun.com/xml/ns/persistence"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
  version="2.0">
   
    <!-- Database settings for development -->
    <persistence-unit name="dev_unit"
      transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        <properties>
            <property name="hibernate.connection.driver_class" value="org.h2.Driver" />
            <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect" />
            <property name="hibernate.show_sql" value="true" />
            <property name="hibernate.format_sql" value="true" />
            <property name="hibernate.hbm2ddl.auto" value="update" />
            <property name="hibernate.connection.autocommit" value="true" />
        </properties>
    </persistence-unit>
</persistence>
```

然后，我们将数据库连接详细信息添加到application.conf：

```plaintext
ninja.jpa.persistence_unit_name=dev_unit
db.connection.url=jdbc:h2:./devDb
db.connection.username=sa
db.connection.password=
```

### 12.3. 实体管理器

最后，我们将使用 Guice 的[Provider](https://google.github.io/guice/api-docs/latest/javadoc/index.html?com/google/inject/Provider.html)类在ApplicationController中注入[EntityManager](https://www.baeldung.com/hibernate-entitymanager)的实例：

```java
public class ApplicationController {
    @Inject 
    Provider<EntityManager> entityManagerProvider;

    // ...
}
```

所以，我们准备好使用EntityManager来持久化User对象：

```java
@Transactional
public Result insertUser(User user) {
    EntityManager entityManager = entityManagerProvider.get();
    entityManager.persist(user);
    entityManager.flush();
    return Results.redirect("/home");
}
```

同样，我们可以使用EntityManager从数据库中读取用户对象：

```java
@UnitOfWork
public Result fetchUsers() {
    EntityManager entityManager = entityManagerProvider.get();
    Query q = entityManager.createQuery("SELECT x FROM User x");
    List<User> users = (List<User>) q.getResultList();
    return Results.json().render(users);
}
```

在这里，Ninja 的[@UnitOfWork](https://www.ninjaframework.org/apidocs/ninja/jpa/UnitOfWork.html)注解将处理所有关于数据库连接的事情，而不处理事务。因此，它可以证明对只读查询很方便，我们通常不需要事务。

## 13. 验证

Ninja 通过遵循 JSR303 规范为 bean 验证提供内置支持。

让我们通过使用[@NotNull](https://docs.oracle.com/javaee/7/api/javax/validation/constraints/NotNull.html)注解注解User实体中的属性来检查该功能：

```java
public class User {
    // ...
    
    @NotNull
    public String firstName;
}
```

然后，我们将修改ApplicationController中已经讨论过的insertUser方法以启用验证：

```java
@Transactional
public Result insertUser(FlashScope flashScope, @JSR303Validation User user, Validation validation) {
    if (validation.getViolations().size() > 0) {
        flashScope.error("Validation Error: User can't be created");
    } else {
        EntityManager entityManager = entitiyManagerProvider.get();
        entityManager.persist(user);
        entityManager.flush();
        flashScope.success("User '" + user + "' is created successfully");
    }
    return Results.redirect("/home");
}
```

我们使用了 Ninja 的[@JSR303Validation](https://www.ninjaframework.org/apidocs/ninja/validation/JSR303Validation.html) 注解来启用用户对象的验证。然后，我们添加了[Validation](https://www.ninjaframework.org/apidocs/ninja/validation/Validation.html)参数以通过hasViolations、getViolations和addViolation等方法处理验证。

最后，FlashScope对象用于在屏幕上显示验证错误。

注意：Ninja 遵循 Bean 验证的 JSR303 规范。但是，JSR380 规范([Bean Validation 2.0](https://www.baeldung.com/javax-validation))是新标准。

## 14.总结

在本文中，我们探索了 Ninja Web 框架——一个使用流行的Java库提供便利功能的全栈框架。

首先，我们使用控制器、模型和服务创建了一个简单的 Web 应用程序。然后，我们在应用程序中启用 JPA 支持以实现持久性。

同时，我们看到了一些基本功能，如路由、JSON 渲染、国际化和 Flash 范围。

最后，我们探讨了框架提供的验证支持。