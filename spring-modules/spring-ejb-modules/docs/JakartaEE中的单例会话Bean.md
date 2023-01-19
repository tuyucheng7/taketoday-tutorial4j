## 1. 概述

每当给定用例需要会话 Bean 的单个实例时，我们都可以使用单例会话 Bean。

在本教程中，我们将通过一个 Jakarta EE 应用程序示例来探索这一点。

## 2.专家

首先，我们需要在pom.xml中定义所需的 Maven 依赖项。

让我们为 EJB API 和用于部署 EJB 的嵌入式 EJB 容器定义依赖关系：

```xml
<dependency>
    <groupId>javax</groupId>
    <artifactId>javaee-api</artifactId>
    <version>8.0</version>
    <scope>provided</scope>
</dependency>

<dependency>
    <groupId>org.apache.openejb</groupId>
    <artifactId>tomee-embedded</artifactId>
    <version>1.7.5</version>
</dependency>
```

你可以找到最新版本的JavaEE [API](https://search.maven.org/classic/#search|gav|1|g%3A"javax" AND a%3A"javaee-api")和[tomEE](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.tomee.maven" AND a%3A"tomee-embedded-maven-plugin")

## 3.会话Bean的类型

存在三种类型的会话 Bean。在我们探索单例会话 Bean 之前，让我们看看这三种类型的生命周期之间有什么区别。

### 3.1. 有状态会话 Bean

有状态会话 Bean 维护与正在通信的客户端的会话状态。

每个客户端都会创建一个新的有状态 Bean 实例，并且不会与其他客户端共享。

当客户端和 bean 之间的通信结束时，会话 Bean 也将终止。

### 3.2. 无状态会话 Bean

无状态会话 Bean 不维护与客户端的任何对话状态。该 bean 仅在方法调用期间包含特定于客户端的状态。

与有状态会话 Bean 不同，连续的方法调用是独立的。

容器维护一个无状态 Bean 池，这些实例可以在多个客户端之间共享。

### 3.3. 单例会话 Bean

单例会话 Bean 在应用程序的整个生命周期内维护 bean 的状态。

单例会话 Bean 类似于无状态会话 Bean，但在整个应用程序中只创建一个单例会话 Bean 实例，并且在应用程序关闭之前不会终止。

bean 的单个实例在多个客户端之间共享，并且可以并发访问。

## 4. 创建单例会话 Bean

让我们从为它创建一个接口开始。

对于这个例子，让我们使用javax.ejb.Local注解来定义接口：

```java
@Local
public interface CountryState {
   List<String> getStates(String country);
   void setStates(String country, List<String> states);
}
```

使用@Local意味着在同一个应用程序中访问 bean。我们还可以选择使用javax.ejb.Remote注解，它允许我们远程调用 EJB。

现在，我们将定义实现 EJB bean 类。我们使用注解javax.ejb.Singleton将该类标记为 Singleton Session Bean 。

另外，我们也给bean打上javax .ejb.Startup注解，通知EJB容器在启动时初始化bean：

```java
@Singleton
@Startup
public class CountryStateContainerManagedBean implements CountryState {
    ...
}
```

这称为急切初始化。如果我们不使用@Startup，EJB 容器将决定何时初始化 bean。

我们还可以定义多个会话 Bean 来初始化数据并按特定顺序加载 Bean。因此，我们将使用javax.ejb.DependsOn注解来定义我们的 bean 对其他会话 Bean 的依赖性。

@DependsOn注解的值是我们的 Bean 所依赖的 Bean 类名称的名称数组：

```java
@Singleton 
@Startup 
@DependsOn({"DependentBean1", "DependentBean2"}) 
public class CountryStateCacheBean implements CountryState { 
    ...
}
```

我们将定义一个初始化 bean 的initialize()方法，并使用javax.annotation.PostConstruct注解使其成为生命周期回调方法。

使用此注解，容器将在实例化 bean 时调用它：

```java
@PostConstruct
public void initialize() {

    List<String> states = new ArrayList<String>();
    states.add("Texas");
    states.add("Alabama");
    states.add("Alaska");
    states.add("Arizona");
    states.add("Arkansas");

    countryStatesMap.put("UnitedStates", states);
}
```

## 5.并发

接下来，我们将设计Singleton Session Bean 的并发管理。EJB 提供了两种方法来实现对 Singleton Session Bean 的并发访问：容器管理的并发和 Bean 管理的并发。

注解javax.ejb.ConcurrencyManagement定义方法的并发策略。默认情况下，EJB 容器使用容器管理的并发。

@ConcurrencyManagement注解采用javax.ejb.ConcurrencyManagementType值。选项是：

-   ConcurrencyManagementType.CONTAINER用于容器管理的并发。
-   ConcurrencyManagementType.BEAN用于 bean 管理的并发。

### 5.1. 容器管理的并发

简单地说，在容器管理的并发中，容器控制客户端如何访问方法。

让我们使用值为javax.ejb.ConcurrencyManagementType.CONTAINER的@ConcurrencyManagement注解：

```java
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class CountryStateContainerManagedBean implements CountryState {
    ...
}
```

要指定对每个单例业务方法的访问级别，我们将使用javax.ejb.Lock注解。javax.ejb.LockType包含@Lock注解的值。javax.ejb.LockType定义了两个值：

-   LockType.WRITE – 该值为调用客户端提供独占锁，并防止所有其他客户端访问该 bean 的所有方法。将其用于更改单例 bean 状态的方法。
-   LockType.READ –该值为多个客户端提供并发锁以访问一个方法。
    将此用于仅从 bean 读取数据的方法。

考虑到这一点，我们将使用@Lock(LockType.WRITE)注解定义setStates()方法，以防止客户端同时更新状态。

为了允许客户端并发读取数据，我们将使用@Lock(LockType.READ)注解getStates( ) ：

```java
@Singleton 
@Startup 
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER) 
public class CountryStateContainerManagedBean implements CountryState { 

    private final Map<String, List<String> countryStatesMap = new HashMap<>();

    @Lock(LockType.READ) 
    public List<String> getStates(String country) { 
        return countryStatesMap.get(country);
    }

    @Lock(LockType.WRITE)
    public void setStates(String country, List<String> states) {
        countryStatesMap.put(country, states);
    }
}
```

要长时间停止方法执行并无限期地阻止其他客户端，我们将使用javax.ejb.AccessTimeout注解来使长时间等待的调用超时。

使用@AccessTimeout注解来定义方法超时的毫秒数。超时后，容器抛出javax.ejb.ConcurrentAccessTimeoutException并且方法执行终止。

### 5.2. Bean 管理的并发

在 Bean 管理的并发中，容器不控制客户端对 Singleton Session Bean 的同时访问。需要开发者自己实现并发。

除非开发人员实现并发，否则所有客户端都可以同时访问所有方法。Java 提供了用于实现并发的[同步](https://www.baeldung.com/java-synchronized)和[易变原语。](https://www.baeldung.com/java-volatile)

要了解有关并发性的更多信息，请阅读此处的[java.util.concurrent](https://www.baeldung.com/java-atomic-variables)和 [此处](https://www.baeldung.com/java-util-concurrent)的原子变量。

对于 bean 管理的并发，让我们为 Singleton Session Bean 类定义带有javax.ejb.ConcurrencyManagementType.BEAN值的@ConcurrencyManagement注解：

```java
@Singleton 
@Startup 
@ConcurrencyManagement(ConcurrencyManagementType.BEAN) 
public class CountryStateBeanManagedBean implements CountryState { 
   ... 
}
```

接下来，我们将编写使用synchronized关键字更改 bean 状态的setStates()方法：

```java
public synchronized void setStates(String country, List<String> states) {
    countryStatesMap.put(country, states);
}
```

synchronized关键字使该方法一次只能由一个线程访问。

getStates()方法不会更改 Bean的状态，因此不需要使用synchronized关键字。

## 6.客户

现在我们可以编写客户端来访问我们的 Singleton Session Bean。

我们可以在 JBoss、Glassfish 等应用程序容器服务器上部署会话 Bean。为简单起见，我们将使用javax.ejb.embedded.EJBContainer类。EJBContainer与客户端运行在相同的 JVM 中，并提供企业 bean 容器的大部分服务。

首先，我们将创建一个EJBContainer实例。此容器实例将搜索并初始化类路径中存在的所有 EJB 模块：

```java
public class CountryStateCacheBeanTest {

    private EJBContainer ejbContainer = null;

    private Context context = null;

    @Before
    public void init() {
        ejbContainer = EJBContainer.createEJBContainer();
        context = ejbContainer.getContext();
    }
}
```

接下来，我们将从初始化的容器对象中获取javax.naming.Context对象。使用Context实例，我们可以获得对CountryStateContainerManagedBean的引用并调用方法：

```java
@Test
public void whenCallGetStatesFromContainerManagedBean_ReturnsStatesForCountry() throws Exception {

    String[] expectedStates = {"Texas", "Alabama", "Alaska", "Arizona", "Arkansas"};

    CountryState countryStateBean = (CountryState) context
      .lookup("java:global/singleton-ejb-bean/CountryStateContainerManagedBean");
    List<String> actualStates = countryStateBean.getStates("UnitedStates");

    assertNotNull(actualStates);
    assertArrayEquals(expectedStates, actualStates.toArray());
}

@Test
public void whenCallSetStatesFromContainerManagedBean_SetsStatesForCountry() throws Exception {

    String[] expectedStates = { "California", "Florida", "Hawaii", "Pennsylvania", "Michigan" };
 
    CountryState countryStateBean = (CountryState) context
      .lookup("java:global/singleton-ejb-bean/CountryStateContainerManagedBean");
    countryStateBean.setStates(
      "UnitedStates", Arrays.asList(expectedStates));
 
    List<String> actualStates = countryStateBean.getStates("UnitedStates");
    assertNotNull(actualStates);
    assertArrayEquals(expectedStates, actualStates.toArray());
}
```

同样，我们可以使用Context实例来获取 Bean-Managed Singleton Bean 的引用并调用相应的方法：

```java
@Test
public void whenCallGetStatesFromBeanManagedBean_ReturnsStatesForCountry() throws Exception {

    String[] expectedStates = { "Texas", "Alabama", "Alaska", "Arizona", "Arkansas" };

    CountryState countryStateBean = (CountryState) context
      .lookup("java:global/singleton-ejb-bean/CountryStateBeanManagedBean");
    List<String> actualStates = countryStateBean.getStates("UnitedStates");

    assertNotNull(actualStates);
    assertArrayEquals(expectedStates, actualStates.toArray());
}

@Test
public void whenCallSetStatesFromBeanManagedBean_SetsStatesForCountry() throws Exception {

    String[] expectedStates = { "California", "Florida", "Hawaii", "Pennsylvania", "Michigan" };

    CountryState countryStateBean = (CountryState) context
      .lookup("java:global/singleton-ejb-bean/CountryStateBeanManagedBean");
    countryStateBean.setStates("UnitedStates", Arrays.asList(expectedStates));

    List<String> actualStates = countryStateBean.getStates("UnitedStates");
    assertNotNull(actualStates);
    assertArrayEquals(expectedStates, actualStates.toArray());
}
```

通过在close()方法中关闭EJBContainer来结束我们的测试：

```java
@After
public void close() {
    if (ejbContainer != null) {
        ejbContainer.close();
    }
}
```

## 七、总结

单例会话 Bean 与任何标准会话 Bean 一样灵活和强大，但允许我们应用单例模式在应用程序的客户端之间共享状态。

Singleton Bean 的并发管理可以使用 Container-Managed Concurrency 轻松实现，其中容器负责多个客户端的并发访问，或者你也可以使用 Bean-Managed Concurrency 实现你自己的自定义并发管理。