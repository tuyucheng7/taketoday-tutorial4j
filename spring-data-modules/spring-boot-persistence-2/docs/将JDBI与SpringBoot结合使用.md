## 1. 简介

在[之前的教程](https://www.baeldung.com/jdbi)中，我们介绍了JDBI的基础知识，**[JDBI](http://jdbi.org/)是一个用于关系型数据库访问的开源库**，它删除了许多与直接使用JDBC相关的样板代码。

**这一次，我们将了解如何在Spring Boot应用程序中使用JDBI**。我们还将介绍此库的某些方面，使其在某些情况下成为Spring Data JPA的良好替代品。

## 2. 项目设置

首先，让我们将适当的JDBI依赖项添加到我们的项目中。**在本文中，我们将使用JDBI的Spring集成插件，它带来了所有必需的核心依赖项**。我们还将引入SqlObject插件，它向我们将在示例中使用的基本JDBI添加一些额外的功能：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
    <version>2.1.8.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.jdbi</groupId>
    <artifactId>jdbi3-spring4</artifactId>
    <version>3.9.1</version>
</dependency>
<dependency>
    <groupId>org.jdbi</groupId>
    <artifactId>jdbi3-sqlobject</artifactId>
    <version>3.9.1</version> 
</dependency>
```

这些工件的最新版本可以在Maven Central中找到：

-   [Spring Boot Starter JDBC](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-jdbc/3.0.4)
-   [JDBI Spring集成](https://central.sonatype.com/artifact/org.jdbi/jdbi3-spring4/3.19.0)
-   [JDBI SqlObject插件](https://central.sonatype.com/artifact/org.jdbi/jdbi3-sqlobject/3.37.1)

我们还需要一个合适的JDBC驱动程序来访问我们的数据库。在本文中我们将使用[H2](https://central.sonatype.com/artifact/com.h2database/h2/2.1.212)，因此我们也必须将其驱动程序添加到我们的依赖项列表中：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.199</version>
    <scope>runtime</scope>
</dependency>
```

## 3. JDBI实例化和配置

我们已经在上一篇文章中看到，我们需要一个Jdbi实例作为访问JDBI API的入口点。我们在Spring中时，将此类的实例作为bean提供是有意义的。

我们将利用Spring Boot的自动配置功能来初始化DataSource并将其传递给@Bean标注的方法，该方法将创建我们的全局Jdbi实例。

我们还会将任何发现的插件和RowMapper实例传递给此方法，以便预先注册它们：

```java
@Configuration
@Slf4j
public class JdbiConfiguration {

    @Bean
    public Jdbi jdbi(DataSource ds, List<JdbiPlugin> jdbiPlugins, List<RowMapper<?>> rowMappers) {
        TransactionAwareDataSourceProxy proxy = new TransactionAwareDataSourceProxy(ds);
        Jdbi jdbi = Jdbi.create(proxy);

        // Register all available plugins
        LOGGER.info("[I27] Installing plugins... ({} found)", jdbiPlugins.size());
        jdbiPlugins.forEach(jdbi::installPlugin);

        // Register all available rowMappers
        LOGGER.info("[I31] Installing rowMappers... ({} found)", rowMappers.size());
        rowMappers.forEach(jdbi::registerRowMapper);

        return jdbi;
    }
}
```

在这里，我们使用可用的DataSource并将其包装在TransactionAwareDataSourceProxy中。**我们需要这个包装器以便将Spring管理的事务与JDBI集成**，我们将在后面看到。

注册插件和RowMapper实例很简单：我们所要做的就是分别为每个可用的JdbiPlugin和RowMapper调用installPlugin和installRowMapper。之后，我们就有了一个可以在我们的应用程序中使用的完全配置的Jdbi实例。

## 4. 示例域模型

我们的示例使用了一个非常简单的域模型，它只包含两个类：CarMaker和CarModel。由于JDBI不需要对我们的域类进行任何注解的标注，因此我们可以使用简单的POJO：

```java
public class CarMaker {
    private Long id;
    private String name;
    private List<CarModel> models;
    // getters and setters ...
}

public class CarModel {
    private Long id;
    private String name;
    private Integer year;
    private String sku;
    private Long makerId;
    // getters and setters ...
}
```

## 5. 创建DAO

现在，让我们为域类创建数据访问对象(DAO)。JDBI SqlObject插件提供了一种简单的方法来实现这些类，这类似于Spring Data处理此主题的方式。

**我们只需要定义一个带有一些注解的接口，JDBI将自动处理所有低级的事情，例如处理JDBC连接和创建/处理语句和ResultSet**：

```java
@UseClasspathSqlLocator
public interface CarMakerDao {
    @SqlUpdate
    @GetGeneratedKeys
    Long insert(@BindBean CarMaker carMaker);

    @SqlBatch("insert")
    @GetGeneratedKeys
    List<Long> bulkInsert(@BindBean List<CarMaker> carMakers);

    @SqlQuery
    CarMaker findById(Long id);
}

@UseClasspathSqlLocator
public interface CarModelDao {
    @SqlUpdate
    @GetGeneratedKeys
    Long insert(@BindBean CarModel carModel);

    @SqlBatch("insert")
    @GetGeneratedKeys
    List<Long> bulkInsert(@BindBean List<CarModel> models);

    @SqlQuery
    CarModel findByMakerIdAndSku(@Bind("makerId") Long makerId, @Bind("sku") String sku );
}
```

这些接口有很多注释，所以让我们快速浏览一下它们中的每一个。

### 5.1 @UseClasspathSqlLocator

**@UseClasspathSqlLocator注解告诉JDBI与每个方法关联的实际SQL语句位于外部资源文件中**。默认情况下，JDBI将使用接口的完全限定名称和方法查找资源。例如，给定一个接口的完全限定名称a.b.c.Foo和一个findById()方法，JDBI将查找名为a/b/c/Foo/findById.sql的资源。

通过将资源名称作为@SqlXXX注解的值传递，可以为任何给定方法覆盖此默认行为。

### 5.2 @SqlUpdate/@SqlBatch/@SqlQuery

**我们使用@SqlUpdate、@SqlBatch和@SqlQuery注解来标记数据访问方法，这些方法将使用给定的参数执行**。这些注解可以接收可选的字符串值，该值将是要执行的文本SQL语句(包括任何命名参数)，或者当与@UseClasspathSqlLocator一起使用时，包含它的资源名称。

@SqlBatch注解方法可以具有类似集合的参数，并为单个批处理语句中的每个可用项执行相同的SQL语句。在上面的每个DAO类中，我们都有一个说明其用法的bulkInsert方法。使用批处理语句的主要优点是我们在处理大型数据集时可以获得更高的性能。

### 5.3 @GetGeneratedKeys

顾名思义，**@GetGeneratedKeys注解允许我们恢复任何生成的主键作为成功执行的结果**。它主要用于insert语句，其中我们的数据库将自动生成新的id，我们需要在代码中恢复它们。

### 5.4 @BindBean/@Bind

**我们使用@BindBean和@Bind注解将SQL语句中的命名参数与方法参数进行绑定**。@BindBean使用标准的bean约定从POJO中提取属性(包括嵌套的属性)。@Bind使用参数名称或提供的值将其值映射到命名参数。

## 6. 使用DAO

要在我们的应用程序中使用这些DAO，我们必须使用JDBI中可用的工厂方法之一来实例化它们。

在Spring上下文中，最简单的方法是使用onDemand方法为每个DAO创建一个bean：

```java
@Bean
public CarMakerDao carMakerDao(Jdbi jdbi) {        
    return jdbi.onDemand(CarMakerDao.class);       
}

@Bean
public CarModelDao carModelDao(Jdbi jdbi) {
    return jdbi.onDemand(CarModelDao.class);
}
```

**onDemand创建的实例是线程安全的，并且仅在方法调用期间使用数据库连接**。由于JDBI我们将使用提供的TransactionAwareDataSourceProxy，**这意味着我们可以将它与Spring管理的事务无缝地结合使用**。

虽然简单，但当我们必须处理多个表时，我们在这里使用的方法远非理想。避免编写此类样板代码的一种方法是创建自定义BeanFactory。不过，描述如何实现这样一个组件超出了本教程的范围。

## 7. 事务Service

让我们在一个简单的Service类中使用我们的DAO类，该Service类在给定填充有模型的CarMaker的情况下创建一些CarModel实例。首先，我们将检查给定的CarMaker之前是否保存过，如果需要，将其保存到数据库中。然后，我们将逐个插入每个CarModel。

**如果在任何时候存在唯一主键冲突(或其他错误)，则整个操作必须失败并且应该执行完整回滚**。

JDBI提供了一个@Transaction注解，**但我们不能在这里使用它**，因为它不知道可能参与同一业务事务的其他资源。相反，我们将在我们的Service方法中使用Spring的@Transactional注解：

```java
@Service
public class CarMakerService {

    private CarMakerDao carMakerDao;
    private CarModelDao carModelDao;

    public CarMakerService(CarMakerDao carMakerDao,CarModelDao carModelDao) {
        this.carMakerDao = carMakerDao;
        this.carModelDao = carModelDao;
    }

    @Transactional
    public int bulkInsert(CarMaker carMaker) {
        Long carMakerId;
        if (carMaker.getId() == null ) {
            carMakerId = carMakerDao.insert(carMaker);
            carMaker.setId(carMakerId);
        }
        carMaker.getModels().forEach(m -> {
            m.setMakerId(carMaker.getId());
            carModelDao.insert(m);
        });
        return carMaker.getModels().size();
    }
}
```

该操作的实现本身非常简单：我们使用标准约定，即id字段中的null值意味着该实体尚未持久保存到数据库中。如果是这种情况，我们使用在构造函数中注入的CarMakerDao实例在数据库中插入一条新记录并获取生成的id。

获得CarMaker的ID后，我们将遍历模型，为每个模型设置makerId字段，然后再将其保存到数据库中。

**所有这些数据库操作都将使用相同的底层连接进行，并且将成为同一事务的一部分**。这里的技巧在于我们使用TransactionAwareDataSourceProxy将JDBI绑定到Spring并创建onDemand DAO的方式。当JDBI请求一个新的Connection时，它将获得一个与当前事务关联的现有连接，从而将其生命周期集成到可能注册的其他资源中。

## 8. 总结

**在本文中，我们展示了如何将JDBI快速集成到Spring Boot应用程序中**。在我们出于某种原因无法使用Spring Data JPA但仍想使用所有其他功能(如事务管理、集成等)的情况下，这是一个强大的组合。