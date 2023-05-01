## 1. 简介

**模块化单体是一种架构风格，我们的源代码是根据模块的概念构建的**。对于许多组织而言，模块化单体可能是一个很好的选择。它有助于保持一定程度的独立性，这有助于我们在需要时过渡到[微服务架构](https://www.baeldung.com/spring-microservices-guide)。

**[Spring Modulith](https://spring.io/projects/spring-modulith)是Spring的一个实验项目，可用于模块化单体应用程序**。此外，它还支持开发人员构建结构良好、领域对齐的Spring Boot应用程序。

在本教程中，我们将讨论Spring Modulith项目的基础知识，并展示如何在实践中使用它的示例。

## 2. 模块化单体架构

我们有不同的选择来构建我们的应用程序代码。传统上，我们围绕基础架构设计软件解决方案。但是，当我们围绕业务设计应用程序时，它可以更好地理解和维护系统。模块化单体架构就是这样一种设计。

由于其简单性和可维护性，模块化单体架构在架构师和开发人员中越来越受欢迎。如果我们将领域驱动设计(DDD)应用于现有的单体应用程序，我们可以将其重构为模块化单体架构：

<img src="../assets/img_5.png">

我们可以通过识别应用程序的域和定义边界上下文，将单体的核心拆分为模块。

让我们看看如何在Spring Boot框架中实现模块化单体应用程序。Spring Modulith由一组库组成，可帮助开发人员构建模块化的Spring Boot应用程序。

## 3. Spring Modulith基础

Spring Modulith帮助开发人员使用域驱动的应用程序模块。此外，它还支持对此类模块化安排的验证和记录。

### 3.1 Maven依赖项

让我们首先在pom.xml的<dependencyManagement\>部分中将[spring-modulith-bom](https://central.sonatype.com/artifact/org.springframework.experimental/spring-modulith-bom/0.5.1)依赖项作为物料清单(BOM)导入：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.experimental</groupId>
            <artifactId>spring-modulith-bom</artifactId>
            <version>0.5.1</version>
            <scope>import</scope>
            <type>pom</type>
        </dependency>
    </dependencies>
</dependencyManagement>
```

此外，我们还需要一些核心的Spring Modulith依赖项：

```xml
<dependency>
    <groupId>org.springframework.experimental</groupId>
    <artifactId>spring-modulith-api</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.experimental</groupId>
    <artifactId>spring-modulith-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### 3.2 应用模块

Spring Modulith的主要概念是应用程序模块。**应用程序模块是向其他模块公开API的功能单元**。此外，它还有一些不应该被其他模块访问的内部实现。当设计我们的应用程序时，我们会为每个域考虑一个应用程序模块。

Spring Modulith提供了不同的模块表达方式。**我们可以将应用程序的领域或业务模块视为应用程序主包的直接子包**。换句话说，应用程序模块是与Spring Boot主类位于同一级别的包(使用@SpringBootApplication标注)：

```powershell
├───pom.xml            
├───src
    ├───main
    │   ├───java
    │   │   └───main-package
    │   │       └───module A
    │   │       └───module B
    │   │           ├───sub-module B
    │   │       └───module C
    │   │           ├───sub-module C
    │   │       │ MainApplication.java

```

现在，让我们看一个包含product和notification域的简单应用程序。在此示例中，我们从产品模块调用服务，然后产品模块从通知模块调用服务。

首先，我们将创建两个应用程序模块：product和notification。为此，我们需要在主包中创建两个直接子包：

<img src="../assets/img_6.png">

让我们看一下此示例的产品模块。我们在产品模块中有一个简单的Product类：

```java
public class Product {

    private String name;
    private String description;
    private int price;

    public Product(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // getters and setters
}
```

然后，让我们在产品模块中定义ProductService bean：

```java
@Service
public class ProductService {

    private final NotificationService notificationService;

    public ProductService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void create(Product product) {
        notificationService.createNotification(new Notification(new Date(), NotificationType.SMS, product.getName()));
    }
}
```

在此类中，create()方法从通知模块调用公开的NotificationService API，并创建Notification类的实例。

让我们看一下通知模块。通知模块包括Notification、NotificationType和NotificationService类。

让我们看看NotificationService bean：

```java
@Service
public class NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    public void createNotification(Notification notification) {
        LOG.info("Received notification by module dependency for product {} in date {} by {}.",
                notification.getProductName(),
                notification.getDate(),
                notification.getFormat());
    }
}
```

在此服务中，我们只记录创建的产品。

最后，在main()方法中，我们从产品模块调用ProductService API的create()方法：

```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args)
                .getBean(ProductService.class)
                .create(new Product("baeldung", "course", 10));
    }
}
```

目录结构如下图所示：

<img src="../assets/img_7.png">

### 3.3 应用模块模型

**我们可以分析我们的代码库，根据排列派生出一个应用程序模块模型**。ApplicationModules类提供创建应用程序模块排列的功能。

让我们创建一个应用程序模块模型：

```java
@Test
void createApplicationModuleModel() {
    ApplicationModules modules = ApplicationModules.of(Application.class);
    modules.forEach(System.out::println);
}
```

如果我们看一下控制台输出，我们可以看到我们的应用程序模块排列：

```shell
# Notification
> Logical name: notification
> Base package: cn.tuyucheng.taketoday.ecommerce.notification
> Spring beans:
  + ….NotificationService

# Product
> Logical name: product
> Base package: cn.tuyucheng.taketoday.ecommerce.product
> Spring beans:
  + ….ProductService
```

如我们所见，它检测到我们的两个模块：notification和product。此外，它还列出了每个模块的Spring组件。

### 3.4 模块封装

**值得注意的是，当前的设计存在问题。ProductService API可以访问Notification类，这是通知模块的内部功能**。

在模块化设计中，我们必须保护和隐藏特定信息并控制对内部实现的访问。**Spring Modulith使用应用模块基础包的子包提供模块封装**。

**此外，它还隐藏了类型，使其不被驻留在其他包中的代码引用。一个模块可以访问任何其他模块的内容，但不能访问其他模块的子包**。

现在，让我们在每个模块中创建一个internal子包并将内部实现移至其中：

<img src="../assets/img_8.png">

在这样的安排中，notification包被认为是一个API包。来自其他应用程序模块的源代码可以引用其中的类型。但是不得从其他模块引用notification.internal包中的源代码。

### 3.5 验证模块化结构

这种设计还有另一个问题。在上面的示例中，Notification类位于notification.internal包中。但是，我们从其他包中引用了Notification类，例如product：

```java
public void create(Product product) {
    notificationService.createNotification(new Notification(new Date(), NotificationType.SMS, product.getName()));
}
```

不幸的是，这意味着它违反了模块访问规则。在这种情况下，Spring Modulith无法使Java编译失败来阻止这些非法引用。它改用单元测试：

```java
@Test
void verifiesModularStructure() {
    ApplicationModules modules = ApplicationModules.of(Application.class);
    modules.verify();
}
```

**我们在ApplicationModules实例上使用verify()方法来确定我们的代码安排是否符合预期的约束**。Spring Modulith使用[ArchUnit](https://www.baeldung.com/java-archunit-intro)项目来实现此功能。

对于上面的示例，我们的验证测试失败并抛出org.springframework.modulith.core.Violations异常：

```plaintext
org.springframework.modulith.core.Violations:
- Module 'product' depends on non-exposed type cn.tuyucheng.taketoday.modulith.notification.internal.Notification within module 'notification'!
Method <cn.tuyucheng.taketoday.modulith.product.ProductService.create(cn.tuyucheng.taketoday.modulith.product.internal.Product)> calls constructor <cn.tuyucheng.taketoday.modulith.notification.internal.Notification.<init>(java.util.Date, cn.tuyucheng.taketoday.modulith.notification.internal.NotificationType, java.lang.String)> in (ProductService.java:25)
```

测试失败，因为产品模块试图访问通知模块的内部类Notification。

现在，让我们通过向通知模块添加一个NotificationDTO类来修复它：

```java
public class NotificationDTO {
    private Date date;
    private String format;
    private String productName;

    // getters and setters
}
```

之后，我们使用NotificationDTO实例代替产品模块中的Notification：

```java
public void create(Product product) {
    notificationService.createNotification(new NotificationDTO(new Date(), "SMS", product.getName()));
}
```

最终的目录结构如下图所示：

<img src="../assets/img_9.png">

### 3.6 记录模块

**我们可以记录项目模块之间的关系**。Spring Modulith提供基于[PlantUML](https://plantuml.com/)生成图表的功能，带有UML或[C4](https://c4model.com/)皮肤。

让我们将应用程序模块导出为C4组件图：

```java
@Test
void createModuleDocumentation() {
    ApplicationModules modules = ApplicationModules.of(Application.class);
    new Documenter(modules)
        .writeDocumentation()
        .writeIndividualModulesAsPlantUml();
}
```

C4图将作为puml文件在target/modulith-docs目录中创建。

让我们使用在线[PlantUML服务器](http://www.plantuml.com/plantuml/uml/SyfFKj2rKt3CoKnELR1Io4ZDoSa70000)渲染生成的组件图：

<img src="../assets/img_10.png">

此图显示产品模块使用通知模块的API。

## 4. 使用事件的模块间交互

**我们有两种模块间交互的方法：依赖于其他应用程序模块的Spring bean或使用事件**。

在上一节中，我们将通知模块API注入到产品模块中。但是，Spring Modulith鼓励使用[Spring框架应用程序事件](https://www.baeldung.com/spring-events)进行模块间通信。为了使应用程序模块尽可能相互解耦，我们使用事件发布和消费作为交互的主要方式。

### 4.1 发布事件

现在，让我们使用Spring的ApplicationEventPublisher来发布一个域事件：

```java
@Service
public class ProductService {

    private final ApplicationEventPublisher events;

    public ProductService(ApplicationEventPublisher events) {
        this.events = events;
    }

    public void create(Product product) {
        events.publishEvent(new NotificationDTO(new Date(), "SMS", product.getName()));
    }
}
```

我们简单地注入ApplicationEventPublisher并使用了publishEvent() API。

### 4.2 应用程序模块监听器

为了注册监听器，Spring Modulith提供了@ApplicationModuleListener注解：

```java
@Service
public class NotificationService {
    @ApplicationModuleListener
    public void notificationEvent(NotificationDTO event) {
        Notification notification = toEntity(event);
        LOG.info("Received notification by event for product {} in date {} by {}.",
                notification.getProductName(),
                notification.getDate(),
                notification.getFormat());
    }
}
```

我们可以在方法级别使用@ApplicationModuleListener注解。在上面的示例中，我们消费了NotificationDTO事件并记录了详细信息。

### 4.3 异步事件处理

对于异步事件处理，我们需要在监听器中添加@Async注解：

```java
@Async
@ApplicationModuleListener
public void notificationEvent(NotificationDTO event) {
    // ...
}
```

此外，需要使用@EnableAsync注解在Spring上下文中启用异步行为。它可以添加到主应用程序类中：

```java
@EnableAsync
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        // ...
    }
}
```

## 5. 总结

在本指南中，我们重点介绍了Spring Modulith项目的基础知识。我们首先讨论了什么是模块化单体设计。

接下来，我们谈到了应用程序模块。我们还详细介绍了应用程序模块模型的创建及其结构的验证。

最后，我们解释了使用事件的模块间交互。