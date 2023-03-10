## 1. 概述

在Hibernate中使用延迟加载时，我们可能会遇到异常，异常错误表明没有会话。

在本教程中，我们将讨论如何解决这些延迟加载问题。为此，我们将使用Spring Boot来构建一个示例。

## 2. 延迟加载问题

延迟加载的目的是通过在加载主对象时不将相关对象加载到内存中从而节省资源。相反，我们将惰性实体的初始化推迟到需要它们的那一刻。**Hibernate使用代理和集合包装器来实现延迟加载**。

检索延迟加载的数据时，该过程有两个步骤。首先填充主对象，其次，检索其代理中的数据。**加载数据总是需要在Hibernate中打开Session**。

当事务关闭后发生第二步时，问题就出现了，这会导致LazyInitializationException。

推荐的方法是设计我们的应用程序以确保数据检索发生在单个事务中。但是，在无法确定已加载或未加载内容的代码的另一部分中使用惰性实体时，这有时可能很困难。

Hibernate有一个解决方法，即enable_lazy_load_no_trans属性。**启用该属性意味着每次获取惰性实体都将打开一个临时会话并在单独的事务中运行**。

## 3. 懒加载示例

让我们看一下延迟加载在几种情况下的行为。

### 3.1 设置实体和Service

假设我们有两个实体User和Document。一个User可能有多个Document，我们将使用@OneToMany来描述这种关系。为了提高效率，我们将使用@Fetch(FetchMode.SUBSELECT)来提高效率。

我们应该注意，默认情况下，@OneToMany的fetch属性值为LAZY(FetchType枚举值)，即延迟获取。

现在让我们定义我们的用户实体：

```java
@Entity
public class User {

    // other fields are omitted for brevity

    @OneToMany(mappedBy = "userId")
    @Fetch(FetchMode.SUBSELECT)
    private List<Document> docs = new ArrayList<>();
}
```

接下来，我们需要一个包含两种方法的Service层来说明不同的选项，其中一个方法被标注为@Transactional。在这里，这两种方法通过计算所有用户的所有文档来执行相同的逻辑：

```java
@Service
public class ServiceLayer {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public long countAllDocsTransactional() {
        return countAllDocs();
    }

    public long countAllDocsNonTransactional() {
        return countAllDocs();
    }

    private long countAllDocs() {
        return userRepository.findAll()
              .stream()
              .map(User::getDocs)
              .mapToLong(Collection::size)
              .sum();
    }
}
```

现在，让我们仔细看看以下三个示例。我们还将使用SQLStatementCountValidator通过计算执行的查询数来了解解决方案的效率。

### 3.2 延迟加载包含事务

首先，让我们按照推荐的方式使用延迟加载。因此，我们将在服务层调用我们的@Transactional方法：

```java
@Test
void whenCallTransactionalMethodWithPropertyOff_thenTestPass() {
    SQLStatementCountValidator.reset();

    long docsCount = serviceLayer.countAllDocsTransactional();

    assertEquals(EXPECTED_DOCS_COLLECTION_SIZE, docsCount);
    SQLStatementCountValidator.assertSelectCount(2);
}
```

正如我们所看到的，这是有效的，并导致了**数据库的两次往返**。第一次往返选择用户，第二次选择用户的文档的文档。

### 3.3 事务外的延迟加载

现在，我们调用一个非事务方法来模拟我们在没有周围事务的情况下得到的错误：

```java
@Test
void whenCallNonTransactionalMethodWithPropertyOff_thenThrowException() {
	assertThrows(LazyInitializationException.class, () -> serviceLayer.countAllDocsNonTransactional());
}
```

正如预测的那样，**这会导致错误**，因为User的getDocs函数是在事务之外使用的。

### 3.4 延迟加载自动事务

要解决这个问题，我们可以启用该属性：

```properties
spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true
```

**启用该属性后，我们不再获得LazyInitializationException**。

但是，查询计数显示**对数据库进行了6次往返**。在这里，一次往返选择用户，五次往返为五个用户中的每一个选择文档：

```java
@Test
void whenCallNonTransactionalMethodWithPropertyOn_thenGetNplusOne() {
    SQLStatementCountValidator.reset();
    
    long docsCount = serviceLayer.countAllDocsNonTransactional();
    
    assertEquals(EXPECTED_DOCS_COLLECTION_SIZE, docsCount);
    SQLStatementCountValidator.assertSelectCount(EXPECTED_USERS_COUNT + 1);
}
```

**我们遇到了臭名昭著的N+1问题**，尽管我们设置了一个fetch策略来避免它。

## 4. 比较方法

**开启了这个属性后，我们就不用担心事务和它们的边界了**。Hibernate为我们管理它。

然而，该解决方案运行缓慢，因为Hibernate在每次获取时都会为我们启动一个事务。

它非常适合演示以及我们不关心性能问题的情况。如果用于获取仅包含一个元素的集合或一对一关系中的单个相关对象，这可能没问题。

**如果没有该属性，我们就可以对事务进行细粒度的控制**，并且我们不再面临性能问题。

**总的来说，这不是一个生产就绪的功能**，Hibernate文档警告我们：

>   尽管启用此配置可以使LazyInitializationException消失，但最好使用fetch计划来保证在Session关闭之前正确初始化所有属性。

## 5. 总结

在本教程中，我们探讨了如何处理延迟加载。

我们尝试了一个Hibernate属性来帮助解决LazyInitializationException。我们还看到了它如何降低效率，并且可能仅适用于有限数量的用例。