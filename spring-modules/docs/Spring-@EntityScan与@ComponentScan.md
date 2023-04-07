## 一、简介

在编写我们的 Spring 应用程序时，我们可能需要指定包含我们的实体类的特定包列表。同样，在某些时候，我们只需要初始化一个特定的 Spring bean 列表。这是我们可以使用[*@EntityScan*](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/autoconfigure/domain/EntityScan.html)或[*@ComponentScan*](https://www.baeldung.com/spring-component-scanning)注释的地方。

为了澄清我们在这里使用的术语，组件是带有[*@Controller*](https://www.baeldung.com/spring-controller-vs-restcontroller)、[*@Service*、*@Repository*、*@Component*](https://www.baeldung.com/spring-component-repository-service)、[*@Bean*等注解的](https://www.baeldung.com/spring-bean-annotations)类。实体是标有[*@Entity*](https://www.baeldung.com/jpa-entities)注解的类。

*在这个简短的教程中，我们将讨论@EntityScan*和*@ComponentScan*在 Spring 中的用法，解释它们的用途，然后指出它们的区别。

## 2. *@EntityScan*注解

在编写我们的 Spring 应用程序时，我们通常会有实体类——那些使用*@Entity*注释进行注释的类。我们可以考虑两种放置实体类的方法：

-   应用程序主包或其子包下
-   使用完全不同的根包

在第一种情况下，我们可以使用*@EnableAutoConfiguration*来启用 Spring 来自动配置应用程序上下文。

在第二种情况下，我们将向我们的应用程序提供可以找到这些包的信息。为此，我们将使用*@EntityScan。*

*@EntityScan*注解用于实体类不在主应用包或其子包中的情况。*在这种情况下，我们将在@EntityScan*注释中的主配置类中声明包或包列表。这将告诉 Spring 在哪里可以找到我们应用程序中使用的实体：

```java
@Configuration
@EntityScan("com.baeldung.demopackage")
public class EntityScanDemo {
    // ...
}复制
```

**我们应该知道，使用\*@EntityScan\*将禁用 Spring Boot 对实体的自动配置扫描。**

## 3. *@ComponentScan*注解

*与@EntityScan*和实体类似，如果我们希望 Spring 仅使用一组特定的 bean 类，我们将使用*@ComponentScan*注解。**它将指向我们希望 Spring 初始化的 bean 类的具体位置**。

此注释可以带参数或不带参数使用。如果没有参数，Spring 将扫描当前包及其子包，而当参数化时，它会告诉 Spring 具体在哪里搜索包。

关于参数，我们可以提供要扫描的包列表（使用*basePackages*参数），或者我们可以命名特定类，它们所属的包也将被扫描（使用*basePackageClasses*参数）。

让我们看一个 @ComponentScan 注解用法的例子：

```java
@Configuration
@ComponentScan(
  basePackages = {"com.baeldung.demopackage"}, 
  basePackageClasses = DemoBean.class)
public class ComponentScanExample {
    // ...
}复制
```

## 4. *@EntityScan*与*@ComponentScan*

最后，我们可以说这两个注解的目的完全不同。

它们的相似之处在于它们都有助于我们的 Spring 应用程序配置。*@EntityScan*应该指定我们要扫描哪些包以查找实体类。另一方面，*@ComponentScan*是指定应该为 Spring bean 扫描哪些包时的一个选择。

## 5.结论

*在这个简短的教程中，我们讨论了@EntityScan*和*@ComponentScan*注释的用法，并指出了它们的区别。