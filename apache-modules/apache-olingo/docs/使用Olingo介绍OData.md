## 1. 简介

[本教程是我们的OData 协议指南](https://www.baeldung.com/odata)的后续教程，我们在其中探索了[OData](https://www.odata.org/)协议的基础知识。

现在，我们将了解如何使用[Apache Olingo](https://olingo.apache.org/)库实现简单的 OData 服务。

该库提供了一个框架来使用 OData 协议公开数据，从而允许轻松地、基于标准地访问信息，否则这些信息将被锁定在内部数据库中。

## 2. 什么是 Olingo？

Olingo 是可用于Java环境的“特色”OData 实现之一——另一个是[SDL OData Framework](https://github.com/sdl/odata)。它由 Apache 基金会维护，由三个主要模块组成：

-  JavaV2——支持 OData V2 的客户端和服务器库
-  JavaV4 – 支持 OData V4 的服务器库
-   Javascript V4 – 支持 OData V4 的 Javascript，仅限客户端的库

在本文中，我们将仅介绍支持与 JPA 直接集成的服务器端 V2Java库。生成的服务支持 CRUD 操作和其他 OData 协议功能，包括排序、分页和过滤。

另一方面，Olingo V4 仅处理协议的较低级别方面，例如内容类型协商和 URL 解析。这意味着将由我们开发人员来编写有关元数据生成、基于 URL 参数生成后端查询等所有细节的代码。

至于 JavaScript 客户端库，我们暂时将其排除在外，因为由于 OData 是基于 HTTP 的协议，我们可以使用任何 REST 库来访问它。

## 3. OlingoJavaV2 服务

让我们使用我们在协议本身[的简要介绍中使用的两个](https://www.baeldung.com/odata)EntitySet创建一个简单的 OData 服务。Olingo V2 的核心只是一组 JAX-RS 资源，因此，我们需要提供所需的基础设施才能使用它。也就是说，我们需要一个 JAX-RS 实现和一个兼容的 servlet 容器。

对于这个例子，我们选择使用 Spring Boot——因为它提供了一种快速创建合适环境来托管我们服务的方法。我们还将使用 Olingo 的 JPA 适配器，它直接与用户提供的EntityManager “对话” ，以便收集创建 OData 的EntityDataModel 所需的所有数据。

虽然不是严格要求，但包含 JPA 适配器会大大简化创建服务的任务。

除了标准的Spring Boot依赖项之外，我们还需要添加几个 Olingo 的 jar：

```xml
<dependency>
    <groupId>org.apache.olingo</groupId>
    <artifactId>olingo-odata2-core</artifactId>
    <version>2.0.11</version>
    <exclusions>
        <exclusion>
            <groupId>javax.ws.rs</groupId>
            <artifactId>javax.ws.rs-api</artifactId>
         </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.apache.olingo</groupId>
    <artifactId>olingo-odata2-jpa-processor-core</artifactId>
    <version>2.0.11</version>
</dependency>
<dependency>
    <groupId>org.apache.olingo</groupId>
    <artifactId>olingo-odata2-jpa-processor-ref</artifactId>
    <version>2.0.11</version>
    <exclusions>
        <exclusion>
            <groupId>org.eclipse.persistence</groupId>
            <artifactId>eclipselink</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

这些库的最新版本可在 Maven 的中央存储库中获得：

-   [olingo-odata2-核心](https://search.maven.org/search?q=a:olingo-odata2-core)
-   [olingo-odata2-jpa-处理器核心](https://search.maven.org/search?q=a:olingo-odata2-jpa-processor-core)
-   [olingo-odata2-jpa-处理器-ref](https://search.maven.org/search?q=a:olingo-odata2-jpa-processor-ref)

我们需要此列表中的那些排除项，因为 Olingo 依赖于 EclipseLink 作为其 JPA 提供程序，并且还使用与Spring Boot不同的 JAX-RS 版本。

### 3.1. 领域类

使用 Olingo 实现基于 JPA 的 OData 服务的第一步是创建我们的域实体。在这个简单的示例中，我们将仅创建两个具有一对多关系的类——CarMaker和CarModel ：

```java
@Entity
@Table(name="car_maker")
public class CarMaker {    
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)    
    private Long id;
    @NotNull
    private String name;
    @OneToMany(mappedBy="maker",orphanRemoval = true,cascade=CascadeType.ALL)
    private List<CarModel> models;
    // ... getters, setters and hashcode omitted 
}

@Entity
@Table(name="car_model")
public class CarModel {
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @NotNull
    private String name;
    
    @NotNull
    private Integer year;
    
    @NotNull
    private String sku;
    
    @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="maker_fk")
    private CarMaker maker;
    
    // ... getters, setters and hashcode omitted
}
```

### 3.2. ODataJPAServiceFactory实现

为了从 JPA 域提供数据，我们需要提供给 Olingo 的关键组件是一个名为ODataJPAServiceFactory 的抽象类的具体实现。此类应扩展ODataServiceFactory并充当 JPA 和 OData 之间的适配器。我们将在我们域的主要主题之后将此工厂命名为CarsODataJPAServiceFactory：

```java
@Component
public class CarsODataJPAServiceFactory extends ODataJPAServiceFactory {
    // other methods omitted...

    @Override
    public ODataJPAContext initializeODataJPAContext() throws ODataJPARuntimeException {
        ODataJPAContext ctx = getODataJPAContext();
        ODataContext octx = ctx.getODataContext();
        HttpServletRequest request = (HttpServletRequest) octx.getParameter(
          ODataContext.HTTP_SERVLET_REQUEST_OBJECT);
        EntityManager em = (EntityManager) request
          .getAttribute(EntityManagerFilter.EM_REQUEST_ATTRIBUTE);
        
        ctx.setEntityManager(em);
        ctx.setPersistenceUnitName("default");
        ctx.setContainerManaged(true);                
        return ctx;
    }
}

```

如果此类要获取用于处理每个 OData 请求的新ODataJPAContext ， Olingo 将调用initializeJPAContext()方法。在这里，我们使用基类中的getODataJPAContext()方法来获取一个“普通”实例，然后我们对其进行一些自定义。

这个过程有些复杂，所以让我们画一个 UML 序列来可视化这一切是如何发生的：

[![Olingo 请求处理](https://www.baeldung.com/wp-content/uploads/2019/05/Olingo-Request-Processing.png)](https://www.baeldung.com/wp-content/uploads/2019/05/Olingo-Request-Processing.png)

请注意，我们有意使用setEntityManager()而不是setEntityManagerFactory()。我们可以从 Spring 获得一个，但是，如果我们将它传递给 Olingo，它将与Spring Boot处理其生命周期的方式发生冲突——尤其是在处理事务时。

出于这个原因，我们将求助于传递一个已经存在的EntityManager实例并通知它它的生命周期是外部管理的。注入的EntityManager实例来自当前请求可用的属性。我们稍后会看到如何设置这个属性。

### 3.3. 泽西资源注册

下一步是将我们的 ServiceFactory注册到 Olingo 的运行时，并将 Olingo 的入口点注册到 JAX-RS 运行时。我们将在ResourceConfig派生类中执行此操作，我们还将服务的 OData 路径定义为/odata：

```java
@Component
@ApplicationPath("/odata")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig(CarsODataJPAServiceFactory serviceFactory, EntityManagerFactory emf) {        
        ODataApplication app = new ODataApplication();        
        app
          .getClasses()
          .forEach( c -> {
              if ( !ODataRootLocator.class.isAssignableFrom(c)) {
                  register(c);
              }
          });
        
        register(new CarsRootLocator(serviceFactory)); 
        register(new EntityManagerFilter(emf));
    }
    
    // ... other methods omitted
}
```

Olingo 提供的ODataApplication是一个常规的 JAX-RS Application类，它使用标准回调getClasses()注册了一些提供者。 

我们可以按原样使用除ODataRootLocator类之外的所有类。这个特定的对象负责使用Java的newInstance()方法实例化我们的ODataJPAServiceFactory 实现。但是，由于我们希望 Spring 为我们管理它，我们需要用自定义定位器替换它。

这个定位器是一个非常简单的 JAX-RS 资源，它扩展了 Olingo 的常用 ODataRootLocator，并在需要时返回我们的 Spring 管理的ServiceFactory ：

```java
@Path("/")
public class CarsRootLocator extends ODataRootLocator {
    private CarsODataJPAServiceFactory serviceFactory;
    public CarsRootLocator(CarsODataJPAServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public ODataServiceFactory getServiceFactory() {
       return this.serviceFactory;
    } 
}

```

### 3.4. 实体管理过滤器

我们的 OData 服务的最后一个剩余部分EntityManagerFilter 。此过滤器在当前请求中注入一个EntityManager，因此它可用于ServiceFactory。这是一个简单的 JAX-RS @Provider类，实现了ContainerRequestFilter 和 ContainerResponseFilter接口，因此它可以正确处理事务：

```java
@Provider
public static class EntityManagerFilter implements ContainerRequestFilter, 
  ContainerResponseFilter {

    public static final String EM_REQUEST_ATTRIBUTE = 
      EntityManagerFilter.class.getName() + "_ENTITY_MANAGER";
    private final EntityManagerFactory emf;

    @Context
    private HttpServletRequest httpRequest;

    public EntityManagerFilter(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        EntityManager em = this.emf.createEntityManager();
        httpRequest.setAttribute(EM_REQUEST_ATTRIBUTE, em);
        if (!"GET".equalsIgnoreCase(ctx.getMethod())) {
            em.getTransaction().begin();
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, 
      ContainerResponseContext responseContext) throws IOException {
        EntityManager em = (EntityManager) httpRequest.getAttribute(EM_REQUEST_ATTRIBUTE);
        if (!"GET".equalsIgnoreCase(requestContext.getMethod())) {
            EntityTransaction t = em.getTransaction();
            if (t.isActive() && !t.getRollbackOnly()) {
                t.commit();
            }
        }
        
        em.close();
    }
}

```

第一个 filter()方法在资源请求开始时调用，它使用提供的EntityManagerFactory创建一个新的 EntityManager实例，然后将其放在一个属性下，以便稍后可以由ServiceFactory恢复。我们还跳过 GET 请求，因为它不应该有任何副作用，因此我们不需要事务。

第二个 filter() 方法在 Olingo 处理完请求后调用。在这里，我们也检查请求方法，并在需要时提交事务。

### 3.5. 测试

让我们使用简单的curl命令测试我们的实现。我们可以做的第一件事是获取服务$metadata文档：

```bash
curl http://localhost:8080/odata/$metadata
```

正如预期的那样，该文档包含两种类型——CarMaker和CarModel——以及一个关联。现在，让我们更多地使用我们的服务，检索顶级集合和实体：

```bash
curl http://localhost:8080/odata/CarMakers
curl http://localhost:8080/odata/CarModels
curl http://localhost:8080/odata/CarMakers(1)
curl http://localhost:8080/odata/CarModels(1)
curl http://localhost:8080/odata/CarModels(1)/CarMakerDetails

```

现在，让我们测试一个简单的查询，返回其名称以“B”开头的所有CarMakers ：

```bash
curl http://localhost:8080/odata/CarMakers?$filter=startswith(Name,'B')

```

[我们的OData 协议指南文章](https://www.baeldung.com/odata)中提供了更完整的示例 URL 列表。

## 5.总结

在本文中，我们了解了如何使用 Olingo V2 创建由 JPA 域支持的简单 OData 服务。

在撰写本文时，[Olingo 的 JIRA 上有一个未解决的问题，它](https://issues.apache.org/jira/browse/OLINGO-549)跟踪 V4 的 JPA 模块上的工作，但最后一条评论可以追溯到 2016 年。还有一个第三方开源 JPA 适配器[托管在 SAP 的 GitHub 存储库](https://github.com/SAP/olingo-jpa-processor-v4) 中，它，尽管尚未发布，但在这一点上似乎比 Olingo 的功能更完整。