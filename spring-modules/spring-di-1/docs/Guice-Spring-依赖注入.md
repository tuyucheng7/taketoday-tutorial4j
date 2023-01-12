## 1. 概述

Google Guice和Spring是用于依赖注入的两个强大框架。
这两个框架都涵盖了依赖注入的所有概念，但它们都有自己的实现方式。

在本教程中，我们将讨论Guice和Spring框架在配置和实现方面的差异。

## 2. Maven依赖

```text
<dependency>
    <groupId>com.google.inject</groupId>
    <artifactId>guice</artifactId>
    <version>5.0.1</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.3.13</version>
</dependency>
```

## 3. 依赖注入配置

依赖注入是一种编程技术，通过依赖注入使我们的类独立于它们的依赖关系。

在本节中，我们介绍Spring和Guice在配置依赖注入的方式上不同的几个核心特性。

### 3.1 Spring注入

**Spring在一个特殊的配置类中声明了依赖注入配置，此类必须由@Configuration注解进行标注**。
Spring容器使用这个类作为bean定义的来源。

**Spring管理的类称为Spring bean**。

Spring使用@Autowired注解自动注入依赖bean。
@Autowired是Spring内置核心注解的一部分，我们可以在成员变量、setter方法和构造函数上使用@Autowired。

Spring还支持@Inject，@Inject是Java CDI(Contexts and Dependency Injection)的一部分，它定义了依赖注入的标准。

假设我们要自动将依赖bean注入到成员变量，可以简单地使用@Autowired对其进行标注：

```java

@Component
public class UserService {

    @Autowired
    private AccountService accountService;
}
```

```java

@Component
public class AccountServiceImpl implements AccountService {

}
```

其次，我们创建一个配置类，在加载应用程序上下文时用作bean定义的来源：

```java

@Configuration
@ComponentScan("cn.tuyucheng.taketoday.di.spring")
public class SpringMainConfig {

}
```

请注意，我们使用@Component标注了UserService和AccountServiceImpl以将它们注册为bean。
**SpringMainConfig类上的@ComponentScan注解告诉Spring在哪里扫描带注解的组件**。

尽管我们的@Component注解标注的是AccountServiceImpl，但Spring可以将其映射到AccountService，因为它实现了AccountService。

然后，我们需要定义一个应用程序上下文来访问bean。请注意，我们会在所有Spring单元测试中引用此上下文：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringMainConfig.class})
class SpringUnitTest {

    @Autowired
    ApplicationContext context;
}
```

在运行时，我们可以从UserService bean中检索AccountService实例：

```java
class SpringUnitTest {

    @Test
    void givenAccountServiceAutowiredToUserService_whenGetAccountServiceInvoked_thenReturnValueIsNotNull() {
        UserService userService = context.getBean(UserService.class);
        assertNotNull(userService.getAccountService());
    }
}
```

### 3.2 Guice绑定

**Guice在一个称为Module的特殊类中管理其依赖关系**，Module必须继承AbstractModule类并重写其configure()方法。

Guice中使用的绑定等同于Spring中的注入。
**简单地说，绑定允许我们定义如何将依赖项注入到一个类中，Guice绑定在Module的configure()方法中声明**。

**Guice使用@Inject注解来注入依赖项，而不是@Autowired**。

让我们编写一个与上述Spring等效的Guice示例：

```java
public class GuiceUserService {

    @Inject
    private AccountService accountService;
}
```

其次，我们编写一个Module类，它是绑定定义的来源：

```java
public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AccountService.class).to(AccountServiceImpl.class);
    }
}
```

通常，如果在configure()方法中没有显式定义任何绑定，我们希望Guice从它们的默认构造函数中实例化每个依赖对象。
但是由于接口不能直接实例化，我们需要定义绑定来告诉Guice哪个接口将与哪个实现配对。

然后，我们需要使用GuiceModule定义一个Injector来获取类的实例。
请注意，我们所有的Guice测试都将使用这个Injector：

```java
class GuiceUnitTest {

    private final Injector injector = Guice.createInjector(new GuiceModule());
}
```

最后，在运行时，我们检索一个具有非空accountService属性的GuiceUserService实例：

```java
class GuiceUnitTest {

    @Test
    void givenAccountServiceAutowiredToUserService_whenGetAccountServiceInvoked_thenReturnValueIsNotNull() {
        GuiceUserService guiceUserService = injector.getInstance(GuiceUserService.class);
        assertNotNull(guiceUserService.getAccountService());
    }
}
```

### 3.3 Spring @Bean注解

Spring还提供了一个方法级别的@Bean注解来注册bean，作为其类级别注解(如@Component)的替代方案。
@Bean注解方法的返回值在容器中注册为bean。

假设我们有一个BookServiceImpl实例，我们希望它可以用于注入，就可以使用@Bean来注册我们的实例：

```java
public class SpringMainConfig {

    @Bean
    public BookService bookServiceGenerator() {
        return new BookServiceImpl();
    }
}
```

现在我们可以得到一个BookService bean：

```java
class SpringUnitTest {

    @Test
    void givenBookServiceIsRegisteredAsBeanInContext_WhenBookServiceIsRetrievedFromContext_ThenReturnValueIsNotNull() {
        BookService bookService = context.getBean(BookService.class);
        assertNotNull(bookService);
    }
}
```

### 3.4 Guice @Provides注解

**作为Spring中的@Bean注解的等价物，Guice有一个内置的@Provides注解来实现同样功能**。与@Bean一样，@Provides只能应用于方法。

现在让我们用Guice实现前面的Spring bean示例，我们需要做的就是将以下代码添加到我们的Module类中：

```java
public class GuiceModule extends AbstractModule {

    @Provides
    public BookService bookServiceGenerator() {
        return new BookServiceImpl();
    }
}
```

现在，我们可以检索BookService的一个实例：

```java
class GuiceUnitTest {

    @Test
    void givenBookServiceIsProvideByGuiceModuleWhenBookServiceIsRetrievedFromGuiceThenReturnValueIsNotNull() {
        BookService bookService = injector.getInstance(BookService.class);
        assertNotNull(bookService);
    }
}
```

### 3.5 Spring中的类路径组件扫描

Spring提供了一个@ComponentScan注解，通过扫描预定义的包来自动检测和实例化带注解的组件。

@ComponentScan注解告诉Spring将扫描哪些包以查找带注解的组件，它与@Configuration注解一起使用。

### 3.6 Guice中的类路径组件扫描

**与Spring不同，Guice没有这样的组件扫描功能**。
但实现它并不难，有一些像Governator这样的插件可以把这个特性引入到Guice中。

### 3.7 Spring中的对象识别

Spring通过名称识别对象。**Spring将对象保存在一个大致类似于Map<String, Object\>的结构中**，这意味着我们不能有两个同名的对象。

由于具有多个同名bean而导致的bean冲突是Spring开发人员遇到的一个常见问题，例如，让我们考虑以下bean声明：

```java

@Configuration
@Import({SpringBeansConfig.class})
@ComponentScan("cn.tuyucheng.taketoday.di.spring")
public class SpringMainConfig {

    @Bean
    public BookService bookServiceGenerator() {
        return new BookServiceImpl();
    }
}

@Configuration
public class SpringBeansConfig {

    @Bean
    public AudioBookService bookServiceGenerator() {
        return new AudioBookServiceImpl();
    }
}
```

我们已经在SpringMainConfig类中为BookService定义了一个bean，其名称为bookServiceGenerator()的方法名。

要演示出bean的冲突问题，我们需要声明具有相同名称的bean方法，但是我们不能在一个类中有两个同名的不同方法。
出于这个原因，我们在另一个配置类中声明了AudioBookService bean，它的bean名称也为bookServiceGenerator()的方法名。

现在，让我们在单元测试中获取这些bean：

```text
BookService bookService = context.getBean(BookService.class);
assertNotNull(bookService); 
AudioBookService audioBookService = context.getBean(AudioBookService.class);
assertNotNull(audioBookService);
```

运行后，单元测试将失败：

```text
org.springframework.beans.factory.NoSuchBeanDefinitionException:
No qualifying bean of type 'AudioBookService' available
```

首先，Spring在保存bean的map中注册了名为“bookServiceGenerator”的AudioBookService bean。
然后，由于HashMap数据结构的“不允许重复key”性质，它必须通过BookService的bean定义覆盖它。

最后，**我们可以通过使bean方法名称唯一或将name属性设置为每个@Bean的唯一名称来解决这个问题**。

### 3.8 Guice中的对象识别

与Spring不同，Guice的结构大致为Map<Class<?\>, Object\>，这意味着在不使用额外元数据的情况下，我们就不能将多个绑定绑定到同一类型。

Guice提供了绑定注解来为同一类型定义多个绑定，让我们看看如果在Guice中为同一类型使用两个不同的绑定会发生什么。

```java
public class Person {
}
```

现在，我们为Person类声明两个不同的绑定：

```java
public class GuiceModule extends AbstractModule {

    @SneakyThrows
    @Override
    protected void configure() {
        bind(Person.class).toConstructor(Person.class.getConstructor());
        bind(Person.class).toProvider(Person::new);
    }
}
```

下面我们获取Person类的实例：

```text
Person person = injector.getInstance(Person.class);
assertNotNull(person);
```

当我们运行该测试时，这将失败：

```text
com.google.inject.CreationException: Unable to create injector, see the following errors:

1) [Guice/BindingAlreadySet]: Person was bound multiple times.
```

我们可以通过简单地去掉Person类的一个绑定来解决这个问题。

### 3.9 Spring中的可选依赖

可选依赖是自动装配或注入bean时不需要的依赖bean。

对于已经被@Autowired注解标注的字段，如果在上下文中没有找到匹配数据类型的bean，Spring会抛出NoSuchBeanDefinitionException。

**但是，有时我们可能希望跳过某些依赖bean的自动装配并将它们保留为null，而不是引发异常**。

让我们看看下面的例子：

```java

@Component
public class BookServiceImpl implements BookService {

    @Autowired
    private AuthorService authorService;
}
```

```java
public class AuthorServiceImpl implements AuthorService {
}
```

从上面的代码中我们可以看到，AuthorServiceImpl类没有使用@Component注解标注，并且假设在配置类中也没有针对它声明@Bean方法。

现在，让我们运行以下测试，看看会发生什么：

```java
class SpringUnitTest {

    @Test
    void givenBookServiceIsRegisteredAsBeanInContext_WhenBookServiceIsRetrievedFromContext_ThenReturnValueIsNotNull() {
        BookService bookService = context.getBean(BookService.class);
        assertNotNull(bookService);
    }
}
```

毫不奇怪，它将失败：

```text
org.springframework.beans.factory.NoSuchBeanDefinitionException: 
No qualifying bean of type 'AuthorService' available
```

**我们可以通过使用Java 8的Optional类型使authorService依赖成为可选的，以避免此异常**。

```java
public class BookServiceImpl implements BookService {

    @Autowired
    private Optional<AuthorService> authorService;
}
```

现在，我们的authorService依赖更像是一个容器，可能包含也可能不包含AuthorService类型的bean。
即使在我们的应用程序上下文中没有AuthorService的bean，我们的authorService字段仍然是非空的空容器。
因此，Spring不会抛出NoSuchBeanDefinitionException。

作为Optional的替代方案，我们可以通过将@Autowired注解的required属性设置为false(默认设置为true)来使依赖成为可选的。

因此，如果其数据类型的bean在上下文中不可用，Spring会跳过注入依赖，依赖项将保持设置为null：

```java

@Component
public class BookServiceImpl implements BookService {

    @Autowired(required = false)
    private AuthorService authorService;
}
```

有时将依赖标记为可选可能是有用的，因为并非所有依赖总是必需的。

考虑到这一点，我们应该记住在开发过程中使用额外null检查，以避免由于null依赖导致的任何NullPointerException。

### 3.10 Guice中的可选依赖

就像Spring一样，Guice也可以使用Java 8的Optional类型来使依赖成为可选的。

假设我们要创建一个类并具有Foo依赖：

```java
public class FooProcessor {
    @Inject
    private Foo foo;
}
```

现在，让我们为Foo类定义一个绑定：

```java
public class GuiceModule extends AbstractModule {

    @SneakyThrows
    @Override
    protected void configure() {
        bind(Foo.class).toProvider(() -> null);
    }
}
```

现在我们在单元测试中获取FooProcessor的实例：

```text
com.google.inject.ProvisionException:
null returned by binding at GuiceModule.configure(..)
but the 1st parameter of FooProcessor.[...] is not @Nullable
```

为了跳过这个异常，我们可以将Foo的类型声明为Optional：

```java
public class FooProcessor {
    @Inject
    private Optional<Foo> foo;
}
```

@Inject没有将依赖标记为可选的required属性，**在Guice中使依赖变为可选的另一种方法是使用@Nullable注解**。

Guice允许在使用@Nullable的情况下注入空值，如上面的异常消息中所述：

```java
public class FooProcessor {
    @Inject
    @Nullable
    private Foo foo;
}
```

## 4. 依赖注入类型的实现

在本节中，我们通过几个示例来了解依赖注入类型并比较Spring和Guice提供的实现。

### 4.1 Spring中的构造注入

**在基于构造函数的依赖注入中，我们在实例化时将所需的依赖传递给一个类**。

假设我们想要一个Spring组件，并且想要通过它的构造函数添加依赖，我们可以使用@Autowired标注该构造函数：

```java

@Component
public class SpringPersonService {

    private PersonDao personDao;

    @Autowired
    public SpringPersonService(PersonDao personDao) {
        this.personDao = personDao;
    }
}
```

从Spring 4开始，如果类只有一个构造函数，则这种类型的注入不需要显示指定@Autowired。

```java
class SpringUnitTest {

    @Test
    void givenSpringPersonServiceConstructorAnnotatedByAutowired_WhenSpringPersonServiceIsRetrievedFromContext_ThenInstanceWillBeCreatedFromTheConstructor() {
        SpringPersonService personService = context.getBean(SpringPersonService.class);
        assertNotNull(personService);
    }
}
```

### 4.2 Guice中的构造注入

我们可以重新编写之前的示例以在Guice中实现构造函数注入。
请注意，Guice使用@Inject而不是@Autowired。

```java
public class GuicePersonService {

    private PersonDao personDao;

    @Inject
    public GuicePersonService(PersonDao personDao) {
        this.personDao = personDao;
    }
}
```

以下在测试中从Injector获取GuicePersonService类的实例：

```java
class GuiceUnitTest {

    @Test
    void givenGuicePersonServiceConstructorAnnotatedByInject_WhenGuicePersonServiceIsRetrievedFromModule_ThenInstanceWillBeCreatedFromTheConstructor() {
        GuicePersonService guicePersonService = injector.getInstance(GuicePersonService.class);
        assertNotNull(guicePersonService);
    }
}
```

### 4.3 Spring中的Setter注入

**在基于setter的依赖注入中，容器在调用构造函数实例化组件后，会调用类的setter方法**。

假设我们希望Spring使用setter方法自动注入依赖，我们可以使用@Autowired标注该setter方法：

```java

@Component
public class SpringPersonService {

    private PersonDao personDao;

    @Autowired
    public void setPersonDao(PersonDao personDao) {
        this.personDao = personDao;
    }
}
```

每当我们需要SpringPersonService类的实例时，Spring将通过调用setPersonDao()方法自动注入personDao字段。

我们可以在测试中获取SpringPersonService bean并访问它的personDao字段，如下所示：

```java
class SpringUnitTest {

    @Test
    void givenPersonDaoAutowiredToSpringPersonServiceBySetterInjection_WhenSpringPersonServiceRetrievedFromContext_ThenPersonDaoInitializedByTheSetter() {
        SpringPersonService personService = context.getBean(SpringPersonService.class);
        assertNotNull(personService);
        assertNotNull(personService.getPersonDao());
    }
}
```

### 4.4 Guice中的Setter注入

我们可以简单地更改前面小节中的例子，使用setter注入：

```java
public class GuicePersonService {

    private PersonDao personDao;

    @Inject
    public void setPersonDao(PersonDao personDao) {
        this.personDao = personDao;
    }
}
```

每次我们从Injector获取GuicePersonService类的实例时，我们都会将personDao字段传递给上面的setter方法。

我们可以在测试中获取SpringPersonService实例并访问它的personDao字段，如下所示：

```java
class GuiceUnitTest {

    @Test
    void givenGuicePersonServiceConstructorAnnotatedByInject_WhenGuicePersonServiceIsRetrievedFromModule_ThenInstanceWillBeCreatedFromTheConstructor() {
        GuicePersonService guicePersonService = injector.getInstance(GuicePersonService.class);
        assertNotNull(guicePersonService);
        assertNotNull(guicePersonService.getPersonDao());
    }
}
```

### 4.5 Spring和Guice中的字段注入

**在基于字段依赖注入的情况下，我们通过使用@Autowired或@Inject标记字段来注入依赖**。

## 5. 总结

在本教程中，我们探讨了Guice和Spring框架在实现依赖注入方面的几个核心差异。