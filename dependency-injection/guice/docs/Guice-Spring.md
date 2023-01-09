## 1. 简介

[Google Guice](https://www.baeldung.com/guice)和[Spring](https://www.baeldung.com/spring-tutorial)是两个用于依赖注入的强大框架。这两个框架都涵盖了依赖注入的所有概念，但每个框架都有自己的实现方式。

在本教程中，我们将讨论 Guice 和 Spring 框架在配置和实现方面的不同之处。

## 2.Maven依赖

让我们首先将 Guice 和 Spring Maven 依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.1.4.RELEASE</version>
</dependency>

<dependency>
    <groupId>com.google.inject</groupId>
    <artifactId>guice</artifactId>
    <version>4.2.2</version>
</dependency>
```

 我们始终可以从 Maven Central访问最新的[spring-context](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework" AND a%3A"spring-context") 或 [guice依赖项。](https://search.maven.org/classic/#search|ga|1|g%3A"com.google.inject" a%3Aguice)

## 3.依赖注入配置

[依赖注入](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)是一种编程技术，我们用它来让我们的类独立于它们的依赖。

在本节中，我们将介绍 Spring 和 Guice 在配置依赖注入的方式上的不同之处。

### 3.1. 弹簧接线

Spring 在一个特殊的配置类中声明依赖注入配置。此类必须由 @Configuration注解进行注解。Spring 容器使用此类作为 bean 定义的来源。

由 Spring 管理的类称为 [Spring beans](https://www.baeldung.com/spring-bean)。

Spring 使用[@Autowired](https://www.baeldung.com/spring-autowire)批注[自动连接依赖项](https://www.baeldung.com/spring-annotations-resource-inject-autowire)。@Autowired是[Spring 内置的核心注解](https://www.baeldung.com/spring-core-annotations)的一部分。我们可以 在成员变量、setter 方法和构造函数上使用@Autowired 。

Spring 也支持 @Inject。@Inject是定义依赖注入标准的[Java CDI(上下文和依赖注入)的一部分。](https://www.baeldung.com/java-ee-cdi)

假设我们想要自动将依赖关系连接到成员变量。我们可以简单地用@Autowired注解它：

```java
@Component
public class UserService {
    @Autowired
    private AccountService accountService;
}
@Component
public class AccountServiceImpl implements AccountService {
}
```

其次，让我们创建一个配置类，在加载我们的应用程序上下文时用作 beans 的源：

```java
@Configuration
@ComponentScan("com.baeldung.di.spring")
public class SpringMainConfig {
}
```

请注意，我们还使用@Component注解了UserService 和AccountServiceImpl以 将它们注册为 bean。@ComponentScan注解将告诉 Spring 在哪里搜索带 注解的组件。

即使我们已经注解了AccountServiceImpl ， Spring 也可以将它映射到AccountService 因为它实现了AccountService。

然后，我们需要定义一个应用程序上下文来访问 beans。请注意，我们将在所有 Spring 单元测试中引用此上下文：

```java
ApplicationContext context = new AnnotationConfigApplicationContext(SpringMainConfig.class);
```

现在在运行时，我们可以从我们的UserService bean中检索A ccountService实例 ：

```java
UserService userService = context.getBean(UserService.class);
assertNotNull(userService.getAccountService());
```

### 3.2. Guice绑定

Guice 在称为模块的特殊类中管理其依赖项。Guice 模块必须扩展 AbstractModule 类并覆盖其 configure()方法。

Guice 使用绑定等同于 Spring 中的连接。简而言之，绑定允许我们定义如何将依赖项注入到类中。Guice 绑定在我们模块的 configure() 方法中声明。

Guice 没有使用 @Autowired ，而是使用[@Inject](https://www.baeldung.com/spring-annotations-resource-inject-autowire)注解来注入依赖项。 

让我们创建一个等效的 Guice 示例：

```java
public class GuiceUserService {
    @Inject
    private AccountService accountService;
}
```

其次，我们将创建模块类，它是我们绑定定义的来源：

```java
public class GuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AccountService.class).to(AccountServiceImpl.class);
    }
}
```

通常，如果没有在configure()方法中显式定义任何绑定，我们希望 Guice 从它们的默认构造函数实例化每个依赖对象。但是由于接口不能直接实例化，我们需要定义绑定来告诉 Guice 哪个接口将与哪个实现配对。

然后，我们需要使用 GuiceModule定义一个Injector来获取我们类的实例。请注意，我们所有的 Guice 测试都将使用此Injector：

```java
Injector injector = Guice.createInjector(new GuiceModule());
```

最后，在运行时我们检索一个 具有非空 accountService 依赖项的GuiceUserService实例：

```java
GuiceUserService guiceUserService = injector.getInstance(GuiceUserService.class);
assertNotNull(guiceUserService.getAccountService());
```

### 3.3. Spring 的@Bean 注解

Spring 还提供了一个方法级别的注解 @Bean 来注册 bean作为其类级别注解的替代方法，如@Component。@Bean注解方法的返回值在容器中注册为一个 bean。

假设我们有一个BookServiceImpl实例，我们希望它可以用于注入。我们可以使用@Bean来注册我们的实例：

```java
@Bean 
public BookService bookServiceGenerator() {
    return new BookServiceImpl();
}
```

现在我们可以获得一个BookService bean：

```java
BookService bookService = context.getBean(BookService.class);
assertNotNull(bookService);
```

### 3.4. Guice 的@Provides 注解

作为 Spring 的@Bean注解的等价物， Guice 有一个内置的注解 [@Provides](https://github.com/google/guice/blob/master/core/src/com/google/inject/Provides.java) 来完成同样的工作。与@Bean一样，@Provides仅应用于方法。

现在让我们用 Guice 实现前面的 Spring bean 示例。我们需要做的就是将以下代码添加到我们的模块类中：

```java
@Provides
public BookService bookServiceGenerator() {
    return new BookServiceImpl();
}
```

现在，我们可以检索BookService的实例：

```java
BookService bookService = injector.getInstance(BookService.class);
assertNotNull(bookService);
```

### 3.5. Spring中的类路径组件扫描

Spring 提供了一个 @ComponentScan注解，通过扫描预定义的包来自动检测和实例化注解的组件。

@ComponentScan注解告诉 Spring 将扫描哪些包以查找带注解的组件。它与@Configuration注解一起使用。

### 3.6. Guice 中的类路径组件扫描

与 Spring 不同，Guice 没有这样的组件扫描功能。但是模拟它并不难。有一些插件，例如[Governator](https://github.com/Netflix/governator)，可以将此功能引入 Guice。

### 3.7. Spring 中的对象识别

Spring 通过名称识别对象。Spring 将对象保存在一个大致类似于Map<String, Object> 的结构中。这意味着我们不能有两个同名的对象。

由于具有多个同名 bean 而导致的 bean 冲突是Spring 开发人员遇到的一个常见问题。例如，让我们考虑以下 bean 声明：

```java
@Configuration
@Import({SpringBeansConfig.class})
@ComponentScan("com.baeldung.di.spring")
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

我们记得，我们已经在SpringMainConfig 类中为BookService定义了一个 bean。

要在这里创建一个 bean 冲突，我们需要声明具有相同名称的 bean 方法。但是我们不允许在一个类中有两个同名的不同方法。出于这个原因，我们在另一个配置类中声明了AudioBookService bean。

现在，让我们在单元测试中引用这些 bean：

```java
BookService bookService = context.getBean(BookService.class);
assertNotNull(bookService); 
AudioBookService audioBookService = context.getBean(AudioBookService.class);
assertNotNull(audioBookService);
```

单元测试将失败并显示：

```java
org.springframework.beans.factory.NoSuchBeanDefinitionException:
No qualifying bean of type 'AudioBookService' available
```

首先，Spring在其 bean 映射中注册了名为“bookServiceGenerator”的AudioBookService bean 。然后，由于HashMap数据结构的“不允许重复名称”性质，它必须通过BookService的 bean 定义覆盖它。

最后，我们可以通过使 bean 方法名称唯一或将name属性设置为每个@Bean 的唯一名称来解决这个问题。

### 3.8. Guice 中的对象识别

与 Spring 不同，Guice 基本上有一个Map <Class<?>, Object> 结构。这意味着我们不能在不使用额外元数据的情况下对同一类型进行多个绑定。

Guice 提供[绑定注解](https://github.com/google/guice/wiki/BindingAnnotations)来为同一类型定义多个绑定。让我们看看如果我们在 Guice 中对同一类型有两个不同的绑定会发生什么。

```java
public class Person {
}
```

现在，让我们为Person类声明两个不同的绑定：

```java
bind(Person.class).toConstructor(Person.class.getConstructor());
bind(Person.class).toProvider(new Provider<Person>() {
    public Person get() {
        Person p = new Person();
        return p;
    }
});
```

下面是我们如何获得Person类的实例：

```java
Person person = injector.getInstance(Person.class);
assertNotNull(person);
```

这将失败：

```java
com.google.inject.CreationException: A binding to Person was already configured at GuiceModule.configure()
```

我们可以通过简单地丢弃Person类的绑定之一来解决这个问题。

### 3.9. Spring 中的可选依赖

[可选依赖项](https://www.baeldung.com/spring-autowire#dependencies)是在自动装配或注入 bean 时不需要的依赖项。

对于被 @Autowired注解的字段，如果在上下文中没有找到匹配数据类型的bean，Spring会抛出 NoSuchBeanDefinitionException。

但是，有时我们可能希望跳过某些依赖项的自动装配并将它们保留为null 而不抛出异常：

现在让我们看看下面的例子：

```java
@Component
public class BookServiceImpl implements BookService {
    @Autowired
    private AuthorService authorService;
}
public class AuthorServiceImpl implements AuthorService {
}
```

从上面的代码可以看出，AuthorServiceImpl类没有被注解为组件。我们假设在我们的配置文件中没有它的 bean 声明方法。

现在，让我们运行以下测试，看看会发生什么：

```java
BookService bookService = context.getBean(BookService.class);
assertNotNull(bookService);
```

毫不奇怪，它会失败：

```java
org.springframework.beans.factory.NoSuchBeanDefinitionException: 
No qualifying bean of type 'AuthorService' available
```

我们可以通过使用[Java 8 的](https://www.baeldung.com/java-optional)[Optional](https://www.baeldung.com/java-optional)[类型使](https://www.baeldung.com/java-optional)authorService依赖项成为可选的来避免这种异常。

```java
public class BookServiceImpl implements BookService {
    @Autowired
    private Optional<AuthorService> authorService;
}
```

现在，我们的authorService依赖项更像是一个容器，可能包含也可能不包含AuthorService类型的 bean。即使在我们的应用程序上下文中没有AuthorService的 bean ，我们的authorService字段仍然是非null空容器。因此，Spring 没有任何理由抛出 NoSuchBeanDefinitionException。

作为Optional的替代方案，我们可以使用@Autowired的required属性(默认情况下设置为true)来使依赖项成为可选的。 我们可以将required属性设置为false以使依赖项成为自动装配的可选项。

因此，如果其数据类型的 bean 在上下文中不可用，Spring 将跳过注入依赖项。依赖项将保持设置为null：

```java
@Component
public class BookServiceImpl implements BookService {
    @Autowired(required = false)
    private AuthorService authorService;
}
```

有时将依赖项标记为可选可能很有用，因为并非所有依赖项总是必需的。

考虑到这一点，我们应该记住，我们需要在开发过程中格外小心并进行null检查，以避免由于null依赖项而导致任何NullPointerException。

### 3.10. Guice 中的可选依赖项

就像Spring一样，Guice也可以使用Java8 的Optional类型来使依赖项成为可选的。

假设我们要创建一个具有Foo依赖项的类：

```java
public class FooProcessor {
    @Inject
    private Foo foo;
}
```

现在，让我们为Foo类定义一个绑定：

```java
bind(Foo.class).toProvider(new Provider<Foo>() {
    public Foo get() {
        return null;
    }
});
```

现在让我们尝试在单元测试中获取FooProcessor的实例：

```java
FooProcessor fooProcessor = injector.getInstance(FooProcessor.class);
assertNotNull(fooProcessor);
```

我们的单元测试将失败：

```java
com.google.inject.ProvisionException:
null returned by binding at GuiceModule.configure(..)
but the 1st parameter of FooProcessor.[...] is not @Nullable
```

为了跳过这个异常，我们可以通过简单的更新使foo依赖项成为可选的：

```java
public class FooProcessor {
    @Inject
    private Optional<Foo> foo;
}
```

@Inject没有将依赖项标记为可选的必需属性。在 Guice中使依赖项可选的另一种方法是使用 @Nullable注解。

Guice 允许在使用@Nullable的情况下注入空值，如上面的异常消息中所述。让我们应用 @Nullable注解：

```java
public class FooProcessor {
    @Inject
    @Nullable
    private Foo foo;
}
```

## 4.依赖注入类型的实现

 在本节中，我们将通过几个示例了解[依赖注入类型并比较 Spring 和 Guice 提供的实现。](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)

### 4.1. Spring 中的构造函数注入

在[基于构造函数的依赖注入](https://www.baeldung.com/constructor-injection-in-spring)中，我们在实例化时将所需的依赖项传递到类中。

假设我们想要一个 Spring 组件，并且我们想通过它的构造函数添加依赖项。我们可以使用@Autowired注解该构造函数 ：

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

从 Spring 4 开始，如果类只有一个构造函数，则这种类型的注入不需要@Autowired依赖项。

让我们在测试中检索一个SpringPersonService bean：

```java
SpringPersonService personService = context.getBean(SpringPersonService.class);
assertNotNull(personService);
```

### 4.2. Guice 中的构造函数注入

我们可以重新安排前面的例子来实现 Guice 中的构造函数注入。请注意，Guice 使用@Inject而不是@Autowired。

```java
public class GuicePersonService {

    private PersonDao personDao;

    @Inject
    public GuicePersonService(PersonDao personDao) {
        this.personDao = personDao;
    }
}
```

以下是我们如何在测试中 从注入器获取GuicePersonService类的实例：

```java
GuicePersonService personService = injector.getInstance(GuicePersonService.class);
assertNotNull(personService);
```

### 4.3. Spring 中的 Setter 或方法注入

在基于 setter 的依赖注入中，容器将在调用构造函数实例化组件后调用类的 setter 方法。

假设我们希望 Spring 使用 setter 方法自动装配依赖项。我们可以使用@Autowired注解 setter 方法 ：

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

每当我们需要SpringPersonService类的实例时，Spring 将 通过调用setPersonDao()方法自动装配personDao字段。

我们可以获得一个SpringPersonService bean 并在测试中访问它的personDao字段，如下所示：

```java
SpringPersonService personService = context.getBean(SpringPersonService.class);
assertNotNull(personService);
assertNotNull(personService.getPersonDao());
```

### 4.4. Guice 中的 Setter 或方法注入

我们将简单地稍微改变我们的示例以在 Guice 中实现 setter 注入。

```java
public class GuicePersonService {

    private PersonDao personDao;

    @Inject
    public void setPersonDao(PersonDao personDao) {
        this.personDao = personDao;
    }
}
```

每次我们从注入器获取GuicePersonService 类的实例时，我们都会将personDao 字段传递给上面的 setter 方法。

以下是我们如何创建GuicePersonService类的实例并在测试中访问其personDao 字段：

```java
GuicePersonService personService = injector.getInstance(GuicePersonService.class);
assertNotNull(personService);
assertNotNull(personService.getPersonDao());
```

### 4.5. 春季现场注入

我们已经在所有示例中看到了如何为 Spring 和 Guice 应用字段注入。所以，这对我们来说不是一个新概念。但为了完整起见，让我们再次列出它。

在基于字段的依赖项注入的情况下，我们通过使用@Autowired 或@Inject标记它们来注入依赖项。

### 4.6. Guice 中的字段注入

正如我们在上一节中提到的，我们已经使用@Inject介绍了Guice 的字段注入。

## 5.总结

在本教程中，我们探讨了 Guice 和 Spring 框架在实现依赖注入的方式上的几个核心差异。