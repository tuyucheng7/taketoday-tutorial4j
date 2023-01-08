## 1.避免重复代码

Java 是一种很棒的语言，但对于我们必须在代码中完成的常见任务或遵守某些框架实践，它有时会变得过于冗长。这通常不会给我们程序的业务方面带来任何实际价值，而这正是 Lombok 的用武之地，它可以提高我们的工作效率。

它的工作方式是插入我们的构建过程，并根据我们在代码中引入的许多项目注解，将Java字节码自动生成到我们的.class文件中。

## 延伸阅读：

## [具有默认值的 Lombok Builder](https://www.baeldung.com/lombok-builder-default-value)

了解如何使用 Lombok 创建构建器默认属性值

[阅读更多](https://www.baeldung.com/lombok-builder-default-value)→

## [使用 Eclipse 和 Intellij 设置 Lombok](https://www.baeldung.com/lombok-ide)

了解如何使用流行的 IDE 设置 Lombok

[阅读更多](https://www.baeldung.com/lombok-ide)→

将它包含在我们的构建中，无论我们使用哪个系统，都非常简单。Project Lombok 的[项目页面](https://projectlombok.org/features/index.html)有关于细节的详细说明。我的大部分项目都是基于 Maven 的，所以我通常只是在提供的范围内放弃它们的依赖，我很高兴：

```xml
<dependencies>
    ...
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.20</version>
        <scope>provided</scope>
    </dependency>
    ...
</dependencies>
```

[我们可以在此处](https://projectlombok.org/changelog.html)检查最新的可用版本。

请注意，依赖 Lombok 不会让我们的.jar用户也依赖它，因为它是一个纯粹的构建依赖，而不是运行时。

## 2. Getters/Setters，构造函数——如此重复

通过公共 getter 和 setter 方法封装对象属性是Java世界中的一种常见做法，许多框架广泛地依赖于这种“Java Bean”模式(一个具有空构造函数和“属性”的 get/set 方法的类)。

这很常见，以至于大多数 IDE 都支持为这些模式(以及更多)自动生成代码。但是，此代码需要存在于我们的源代码中，并在添加新属性或重命名字段时进行维护。

让我们考虑一下我们想要用作 JPA 实体的类：

```java
@Entity
public class User implements Serializable {

    private @Id Long id; // will be set when persisting

    private String firstName;
    private String lastName;
    private int age;

    public User() {
    }

    public User(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    // getters and setters: ~30 extra lines of code
}
```

这是一个相当简单的类，但想象一下，如果我们为 getter 和 setter 添加了额外的代码。我们最终会得到一个定义，其中样板零值代码多于相关业务信息：“用户有名字和姓氏，以及年龄。”

现在让我们对这个类进行 Lombok 化处理：

```java
@Entity
@Getter @Setter @NoArgsConstructor // <--- THIS is it
public class User implements Serializable {

    private @Id Long id; // will be set when persisting

    private String firstName;
    private String lastName;
    private int age;

    public User(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }
}
```

通过添加@Getter和@Setter注解，我们告诉 Lombok 为类的所有字段生成这些注解。@NoArgsConstructor将导致生成一个空的构造函数。

请注意，这是整个类代码，与上面版本不同的是，我们没有省略任何带有// getters 和 setters注解的代码。对于具有三个相关属性的类，这可以显着节省代码！

如果我们进一步为我们的User类添加属性(properties)，同样会发生；我们将注解应用到类型本身，因此默认情况下它们会注意所有字段。

如果我们想细化某些属性的可见性怎么办？例如，如果我们希望保持实体的id字段修饰符package或protected可见，因为它们应该被读取，但不由应用程序代码显式设置，我们可以为这个特定字段使用更细粒度的@Setter ：

```java
private @Id @Setter(AccessLevel.PROTECTED) Long id;
```

## 3.懒惰的吸气剂

应用程序通常需要执行昂贵的操作并保存结果以供后续使用。

例如，假设我们需要从文件或数据库中读取静态数据。检索此数据一次，然后将其缓存以允许在应用程序中进行内存读取通常是一种很好的做法。这使应用程序免于重复昂贵的操作。

另一种常见模式是仅在首次需要时才检索此数据。也就是说，我们只有在第一次调用对应的getter时才拿到数据。我们称之为延迟加载。

假设此数据被缓存为类中的一个字段。该类现在必须确保对该字段的任何访问都返回缓存的数据。实现此类的一种可能方法是让 getter 方法仅在字段为null时检索数据。我们称之为惰性吸气剂。

Lombok 通过我们在上面看到的@Getter注解中的惰性参数使这成为可能。

例如，考虑这个简单的类：

```java
public class GetterLazy {

    @Getter(lazy = true)
    private final Map<String, Long> transactions = getTransactions();

    private Map<String, Long> getTransactions() {

        final Map<String, Long> cache = new HashMap<>();
        List<String> txnRows = readTxnListFromFile();

        txnRows.forEach(s -> {
            String[] txnIdValueTuple = s.split(DELIMETER);
            cache.put(txnIdValueTuple[0], Long.parseLong(txnIdValueTuple[1]));
        });

        return cache;
    }
}
```

这会将文件中的一些事务读取到Map中。由于文件中的数据不会改变，我们会将其缓存一次并允许通过 getter 进行访问。

如果我们现在看一下这个类的编译代码，我们会看到一个getter 方法，如果缓存为null ，它会更新缓存，然后返回缓存的数据：

```java
public class GetterLazy {

    private final AtomicReference<Object> transactions = new AtomicReference();

    public GetterLazy() {
    }

    //other methods

    public Map<String, Long> getTransactions() {
        Object value = this.transactions.get();
        if (value == null) {
            synchronized(this.transactions) {
                value = this.transactions.get();
                if (value == null) {
                    Map<String, Long> actualValue = this.readTxnsFromFile();
                    value = actualValue == null ? this.transactions : actualValue;
                    this.transactions.set(value);
                }
            }
        }

        return (Map)((Map)(value == this.transactions ? null : value));
    }
}
```

有趣的是，Lombok 将数据字段包装在[AtomicReference](https://www.baeldung.com/java-atomic-variables)中。 这确保了对事务字段的原子更新。getTransactions()方法还确保在事务为空时读取文件。

我们不鼓励直接从类中使用AtomicReference transactions字段。我们建议使用getTransactions()方法来访问该字段。

出于这个原因，如果我们在同一个类中使用另一个像ToString这样的 Lombok 注解，它将使用getTransactions()而不是直接访问该字段。

## 4. 值类/DTO

在很多情况下，我们想要定义一种数据类型，其唯一目的是将复杂的“值”表示为“数据传输对象”，大多数情况下，我们以不可变数据结构的形式构建一次，永远不想改变。

我们设计一个类来表示一次成功的登录操作。我们希望所有字段都是非空的并且对象是不可变的，以便我们可以线程安全地访问它的属性：

```java
public class LoginResult {

    private final Instant loginTs;

    private final String authToken;
    private final Duration tokenValidity;
    
    private final URL tokenRefreshUrl;

    // constructor taking every field and checking nulls

    // read-only accessor, not necessarily as get() form
}
```

同样，我们必须为注解部分编写的代码量将比我们想要封装的信息量大得多。我们可以使用 Lombok 来改进这一点：

```java
@RequiredArgsConstructor
@Accessors(fluent = true) @Getter
public class LoginResult {

    private final @NonNull Instant loginTs;

    private final @NonNull String authToken;
    private final @NonNull Duration tokenValidity;
    
    private final @NonNull URL tokenRefreshUrl;

}
```

添加@RequiredArgsConstructor注解后，我们将为类中的所有最终字段获取一个构造函数，就像我们声明它们一样。将@NonNull添加到属性使我们的构造函数检查可空性并相应地抛出NullPointerExceptions。如果字段是非最终字段并且我们为它们添加了@Setter ，也会发生这种情况。

我们是否希望我们的属性使用无聊的旧get()形式？由于我们在此示例中添加了@Accessors(fluent=true)，因此“getters”将具有与属性相同的方法名称； getAuthToken()简单地变成authToken()。

这种“流畅”的形式将适用于属性设置器的非最终字段，并允许链式调用：

```java
// Imagine fields were no longer final now
return new LoginResult()
  .loginTs(Instant.now())
  .authToken("asdasd")
  . // and so on
```

## 5.核心Java样板

我们最终编写需要维护的代码的另一种情况是生成toString()、equals()和hashCode()方法时。IDE 试图帮助模板根据我们的类属性自动生成这些。

我们可以通过其他 Lombok 类级注解来自动执行此操作：

-   [@ToString](https://projectlombok.org/features/ToString.html)：将生成一个包含所有类属性的toString()方法。无需自己编写和维护它，因为我们丰富了我们的数据模型。
-   [@EqualsAndHashCode](https://projectlombok.org/features/EqualsAndHashCode.html)：默认情况下将生成equals()和hashCode()方法，考虑到所有相关字段，并且根据[非常好的语义](http://www.artima.com/lejava/articles/equality.html)。

这些生成器提供非常方便的配置选项。例如，如果我们带注解的类属于层次结构的一部分，我们可以只使用callSuper=true参数，并且在生成方法代码时将考虑父结果。

为了证明这一点，假设我们的User JPA 实体示例包含对与该用户关联的事件的引用：

```java
@OneToMany(mappedBy = "user")
private List<UserEvent> events;
```

我们不希望在调用 User 的toString()方法时转储整个事件列表，只是因为我们使用了@ToString注解。相反，我们可以像这样对其进行参数化 @ToString(exclude = {“events”})，而这不会发生。这也有助于避免循环引用，例如，如果UserEvent引用了User。

对于LoginResult示例，我们可能只想根据令牌本身而不是我们类中的其他最终属性来定义相等性和哈希码计算。然后我们可以简单地写一些像@EqualsAndHashCode(of = {“authToken”})这样的东西。

如果到目前为止我们已经审查过的注解中的特性感兴趣，我们可能还想检查[@Data](https://projectlombok.org/features/Data.html)和[@Value](https://projectlombok.org/features/Value.html)注解，因为它们的行为就像它们中的一组已应用于我们的类一样。毕竟，在许多情况下，这些讨论的用法很常见。

### 5.1. (不)将@EqualsAndHashCode与 JPA 实体一起使用

我们是应该使用默认的equals()和hashCode()方法，还是为 JPA 实体创建自定义方法，这是开发人员经常讨论的话题。我们可以采用[多种方法](https://www.baeldung.com/jpa-entity-equality)，每种方法各有利弊。

默认情况下，@EqualsAndHashCode包括实体类的所有非最终属性。我们可以尝试通过使用 @EqualsAndHashCode 的onlyExplicitlyIncluded属性来“修复”这个问题，使 Lombok 仅使用实体的主键。不过，生成的equals()方法可能会导致一些问题。Thorben Janssen 在[他的一篇博文](https://thorben-janssen.com/lombok-hibernate-how-to-avoid-common-pitfalls)中更详细地解释了这种情况。

一般来说，我们应该避免使用 Lombok 为我们的 JPA 实体生成equals()和hashCode()方法。

## 6.建造者模式

以下内容可以构成 REST API 客户端的示例配置类：

```java
public class ApiClientConfiguration {

    private String host;
    private int port;
    private boolean useHttps;

    private long connectTimeout;
    private long readTimeout;

    private String username;
    private String password;

    // Whatever other options you may thing.

    // Empty constructor? All combinations?

    // getters... and setters?
}
```

我们可以有一个基于使用类默认空构造函数并为每个字段提供 setter 方法的初始方法；然而，理想情况下，我们希望配置一旦被构建(实例化)就不会被重置，从而有效地使它们不可变。因此，我们希望避免 setter，但编写这样一个可能很长的 args 构造函数是一种反模式。

相反，我们可以告诉该工具生成一个构建器模式，这使我们不必编写额外的Builder类和相关的类似 setter 的流畅方法，只需将 @Builder 注解添加到我们的ApiClientConfiguration 即可：

```java
@Builder
public class ApiClientConfiguration {

    // ... everything else remains the same

}
```

保留上面的类定义(不声明构造函数或设置器 + @Builder)，我们最终可以将其用作：

```java
ApiClientConfiguration config = 
    ApiClientConfiguration.builder()
        .host("api.server.com")
        .port(443)
        .useHttps(true)
        .connectTimeout(15_000L)
        .readTimeout(5_000L)
        .username("myusername")
        .password("secret")
    .build();
```

## 7.检查异常负担

许多JavaAPI 被设计为可以抛出大量已检查的异常；客户端代码被迫捕获或声明抛出。我们有多少次将这些我们知道不会发生的异常变成这样的事情？：

```java
public String resourceAsString() {
    try (InputStream is = this.getClass().getResourceAsStream("sure_in_my_jar.txt")) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        return br.lines().collect(Collectors.joining("n"));
    } catch (IOException | UnsupportedCharsetException ex) {
        // If this ever happens, then its a bug.
        throw new RuntimeException(ex); <--- encapsulate into a Runtime ex.
    }
}
```

如果我们想避免这种代码模式，因为编译器会不高兴(而且我们知道已检查的错误不会发生)，请使用恰当命名的[@SneakyThrows](https://projectlombok.org/features/SneakyThrows.html)：

```java
@SneakyThrows
public String resourceAsString() {
    try (InputStream is = this.getClass().getResourceAsStream("sure_in_my_jar.txt")) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        return br.lines().collect(Collectors.joining("n"));
    } 
}
```

## 8. 确保我们的资源得到释放

Java 7 引入了 try-with-resources 块以确保我们的资源由任何实现java.lang的实例持有。AutoCloseable在退出时被释放。

Lombok 提供了另一种更灵活的方法来通过[@Cleanup](https://projectlombok.org/features/Cleanup.html)实现这一点。我们可以将它用于我们想要确保释放其资源的任何局部变量。他们不需要实现任何特定的接口，我们只需调用close()方法即可：

```java
@Cleanup InputStream is = this.getClass().getResourceAsStream("res.txt");
```

我们的释放方法有不同的名称？没问题，我们只要自定义注解即可：

```java
@Cleanup("dispose") JFrame mainFrame = new JFrame("Main Window");
```

## 9. 注解我们的类以获得记录器

我们中的许多人通过从我们选择的框架中创建一个Logger实例来谨慎地将日志记录语句添加到我们的代码中。比方说 SLF4J：

```java
public class ApiClientConfiguration {

    private static Logger LOG = LoggerFactory.getLogger(ApiClientConfiguration.class);

    // LOG.debug(), LOG.info(), ...

}
```

这是一个如此常见的模式，Lombok 开发人员为我们简化了它：

```java
@Slf4j // or: @Log @CommonsLog @Log4j @Log4j2 @XSlf4j
public class ApiClientConfiguration {

    // log.debug(), log.info(), ...

}
```

支持很多[日志框架](https://projectlombok.org/features/Log.html)，当然我们可以自定义实例名、主题等。

## 10. 编写线程安全的方法

在Java中，我们可以使用synchronized关键字来实现临界区；但是，这不是 100% 安全的方法。其他客户端代码最终也可以在我们的实例上同步，这可能会导致意外的死锁。

这就是[@Synchronized ](https://projectlombok.org/features/Synchronized.html)的用武之地。我们可以用它来注解我们的方法(实例和静态)，我们将获得一个自动生成的、私有的、未公开的字段，我们的实现将使用该字段进行锁定：

```java
@Synchronized
public / better than: synchronized / void putValueInCache(String key, Object value) {
    // whatever here will be thread-safe code
}
```

## 11. 自动化对象合成

Java 没有语言级别的构造来平滑“有利于组合继承”的方法。其他语言有内置的概念，如Traits或Mixins来实现这一点。

当我们想要使用这种编程模式时，Lombok 的[@Delegate就派上用场了。](https://projectlombok.org/features/experimental/Delegate.html)让我们考虑一个例子：

-   我们希望User和Customer共享一些共同的命名和电话号码属性。
-   我们为这些字段定义了一个接口和一个适配器类。
-   我们将让我们的模型实现接口和@Delegate到它们的适配器，有效地将它们与我们的联系信息组合在一起。

首先，让我们定义一个接口：

```java
public interface HasContactInformation {

    String getFirstName();
    void setFirstName(String firstName);

    String getFullName();

    String getLastName();
    void setLastName(String lastName);

    String getPhoneNr();
    void setPhoneNr(String phoneNr);

}
```

现在一个适配器作为支持类：

```java
@Data
public class ContactInformationSupport implements HasContactInformation {

    private String firstName;
    private String lastName;
    private String phoneNr;

    @Override
    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}
```

现在是有趣的部分；看看将联系信息组合到两个模型类中是多么容易：

```java
public class User implements HasContactInformation {

    // Whichever other User-specific attributes

    @Delegate(types = {HasContactInformation.class})
    private final ContactInformationSupport contactInformation =
            new ContactInformationSupport();

    // User itself will implement all contact information by delegation
    
}
```

Customer的情况非常相似，为了简洁起见，我们可以省略示例。

## 12. 回滚龙目岛？

简短的回答：一点也不。

可能会担心，如果我们在我们的一个项目中使用 Lombok，我们可能会在以后想要回滚该决定。潜在的问题可能是有大量为其注解的类。在这种情况下，由于来自同一项目的delombok工具，我们得到了保护。

通过delombok-ing我们的代码，我们获得了自动生成的Java源代码，其功能与 Lombok 构建的字节码完全相同。然后我们可以简单地用这些新的delmboked文件替换我们原来的带注解的代码，而不再依赖它。

这是我们可以[集成到我们的构建中的](https://projectlombok.org/features/delombok.html)东西。

## 13.总结

还有一些我们没有在本文中介绍的其他功能。我们可以更深入地了解[功能概述](https://projectlombok.org/features/index.html)以获取更多详细信息和用例。

此外，我们展示的大多数函数都有许多自定义选项，我们可能会觉得它们很方便。可用的内置[配置系统](https://projectlombok.org/features/configuration.html)也可以帮助我们。

现在我们可以让 Lombok 有机会进入我们的Java开发工具集，我们可以提高我们的生产力。