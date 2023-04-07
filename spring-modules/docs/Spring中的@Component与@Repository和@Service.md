## **一、简介**

在本快速教程中，我们将了解Spring Framework 中的*@Component、@Repository*和*@Service注解之间的区别。*

## 延伸阅读：

## [Spring @Autowired 指南](https://www.baeldung.com/spring-autowire)

Springs @Autowired 注释和限定符最常见用法的指南

[阅读更多](https://www.baeldung.com/spring-autowire)→

## [Spring @Qualifier 注解](https://www.baeldung.com/spring-qualifier-annotation)

@Autowired 有时不足以消除依赖关系的歧义。您可以使用 @Qualifier 注释更精确地连接。@Primary 也可以提供帮助。

[阅读更多](https://www.baeldung.com/spring-qualifier-annotation)→

## 2. 弹簧注解

在大多数典型的应用程序中，我们有不同的层，如数据访问、表示、服务、业务等。

此外，在每一层中我们都有各种 bean。为了自动检测这些 bean，**Spring 使用类路径扫描注解**。

*然后它在ApplicationContext*中注册每个 bean 。

以下是其中一些注释的快速概述：

-   *@Component*是任何 Spring 管理的组件的通用构造型。
-   *@Service*在服务层注释类。
-   *@Repository*在持久层注释类，它将充当数据库存储库。

我们已经有一篇关于这些注释的[扩展文章](https://www.baeldung.com/spring-bean-annotations)，因此我们将在此处将重点放在它们之间的区别上。

## **3.有什么不同？**

**这些刻板印象之间的主要区别在于它们用于不同的分类。**当我们注释一个类以进行自动检测时，我们应该使用相应的构造型。

现在让我们更详细地了解它们。

### **3.1. \*@成分\***

**我们可以在整个应用程序中使用 @Component 将 bean 标记为 Spring 的托管组件**。*Spring 只会使用@Component*获取和注册bean ，一般不会查找*@Service* 和 *@Repository 。*

它们在*ApplicationContext*中注册，因为它们带有*@Component*注释：

```java
@Component
public @interface Service {
}
复制
@Component
public @interface Repository {
}
复制
```

*@Service* 和 *@Repository是**@Component*的特例。它们在技术上是相同的，但我们将它们用于不同的目的。

### **3.2. \*@存储库\***

***@Repository\*的工作是捕获特定于持久性的异常并将它们作为 Spring 统一的未检查异常之一重新抛出**。

为此，Spring 提供了*PersistenceExceptionTranslationPostProcessor*，我们需要将其添加到我们的应用程序上下文中（如果我们使用 Spring Boot，则已经包含）：

```xml
<bean class=
  "org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor"/>复制
```

这个 bean 后处理器向任何用 @Repository 注释的 bean 添加顾问 *。*

### **3.3. \*@服务\***

**我们用 @Service 标记 bean 以指示它们持有业务逻辑**。除了在服务层使用外，该注解没有其他特殊用途。

## **4。结论**

***在本文中，我们了解了@Component、@Repository\*和\*@Service\*注解之间的区别**。我们分别检查了每个注释以了解它们的使用领域。

总之，根据图层约定选择注释总是一个好主意。