## 1. 概述

众所周知，当我们想要快速开始使用Restful Web服务时，[Spring Data Rest]()模块可以让我们的实现更加简单。但是，此模块带有默认行为，有时可能会造成混淆。

在本教程中，我们将了解为什么Spring Data Rest默认情况下不序列化实体ID。此外，我们会介绍更改这种行为的各种解决方案。

## 2. 默认行为

在详细介绍之前，我们通过一个简单的例子来理解序列化实体id的意思。

下面是一个实体Person：

```java
@Entity
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    // getters and setters
}
```

此外，我们还有一个PersonRepository：

```java
public interface PersonRepository extends JpaRepository<Person, Long> {
}
```

如果我们使用Spring Boot，只需添加[spring-boot-starter-data-rest](https://search.maven.org/search?q=a:spring-boot-starter-data-rest)依赖项即可启用Spring Data Rest模块：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-rest</artifactId>
</dependency>
```

通过这两个类和Spring Boot的自动配置，我们的REST控制器就可以自动准备好使用了。

下一步，我们请求资源http://localhost:8080/persons，并观察框架生成的默认JSON响应：

```json
{
    "_embedded": {
        "persons": [
            {
                "name": "John Doe",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/persons/1"
                    },
                    "person": {
                        "href": "http://localhost:8080/persons/1{?projection}",
                        "templated": true
                    }
                }
            },
            ...
        ]
        ...
    }
}
```

为了简洁起见，我们省略了一些部分。正如我们所注意到的，只有name字段被包含在序列化后的结果中。出于某种原因，id字段被删除了。因此，这是Spring Data Rest中的一个设计决策。**在大多数情况下，公开我们的内部ID并不理想，因为它们对外部系统没有任何意义**。在理想情况下，**标识是Restful架构中该资源的URL**。

我们还应该看到，只有当我们使用Spring Data Rest的端点时才会出现这种情况。我们的自定义@Controller或@RestController端点不会受到影响，除非我们使用[Spring HATEOAS]()的RepresentationModel及其子级(如CollectionModel和EntityModel)来构建我们的响应。幸运的是，公开实体ID是可配置的。因此，我们仍然可以灵活地启用它。

在接下来的部分中，我们将介绍在Spring Data Rest中公开实体ID的不同方式。

## 3. 使用RepositoryRestConfigurer

**公开实体ID的最常见解决方案是配置RepositoryRestConfigurer**：

```java
@Configuration
public class RestConfiguration implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(Person.class);
    }
}
```

**在Spring Data Rest 3.1或Spring Boot 2.1版本之前，我们会使用RepositoryRestConfigurerAdapter**：

```java
@Configuration
public class RestConfiguration extends RepositoryRestConfigurerAdapter {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Person.class);
    }
}
```

虽然这两个API大同小异，但还是要注意版本。作为旁注，由于Spring Data Rest 3.1版本[RepositoryRestConfigurerAdapter](https://docs.spring.io/spring-data/rest/docs/3.1.0.RELEASE/api/org/springframework/data/rest/webmvc/config/RepositoryRestConfigurerAdapter.html)已被弃用，并且已在最新的[4.0.x](https://docs.spring.io/spring-data/rest/docs/4.0.x/api/)分支中已被删除。

在我们为实体Person配置之后，响应也为我们提供了id字段：

```json
{
    "_embedded": {
        "persons": [
            {
                "id": 1,
                "name": "John Doe",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/persons/1"
                    },
                    "person": {
                        "href": "http://localhost:8080/persons/1{?projection}",
                        "templated": true
                    }
                }
            },
            ...
        ]
        ...
    }
}
```

显然，当我们想要为所有这些实体启用id公开时，如果我们有很多实体，则此解决方案不切实际。

因此，我们通过通用方法改进我们的RestConfiguration：

```java
@Configuration
public class RestConfiguration implements RepositoryRestConfigurer {

    @Autowired
    private EntityManager entityManager;

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        Class[] classes = entityManager.getMetamodel().getEntities()
                .stream()
                .map(Type::getJavaType)
                .toArray(Class[]::new);
        config.exposeIdsFor(classes);
    }
}
```

当我们使用JPA来管理持久层时，我们可以以通用方式访问实体的元数据。JPA的EntityManager已经存储了我们需要的元数据。因此，**我们实际上可以通过entityManager.getMetamodel()方法收集实体类类型**。

因此，这是一个更全面的解决方案，因为每个实体的ID公开都是自动启用的。

## 4. 使用@Projection

另一种解决方案是使用@Projection注解。通过定义一个PersonView接口，我们也可以公开id字段：

```java
@Projection(name = "person-view", types = Person.class)
public interface PersonView {

    Long getId();

    String getName();
}
```

但是，我们现在应该使用不同的请求来测试http://localhost:8080/persons?projection=person-view：

```json
{
    "_embedded": {
        "persons": [
            {
                "id": 1,
                "name": "John Doe",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/persons/1"
                    },
                    "person": {
                        "href": "http://localhost:8080/persons/1{?projection}",
                        "templated": true
                    }
                }
            },
            ...
        ]
        ...
    }
}
```

**要为Repository生成的所有端点启用投影，我们可以在PersonRepository上使用@RepositoryRestResource注解**：

```java
@RepositoryRestResource(excerptProjection = PersonView.class)
public interface PersonRepository extends JpaRepository<Person, Long> {

}
```

在此更改之后，我们可以使用通常的请求http://localhost:8080/persons来列出Person实体。

**但是，我们应该注意excerptProjection不会自动应用单个元素资源**。我们仍然必须使用http://localhost:8080/persons/1?projection=person-view来获取单个Person及其实体ID的响应。

此外，我们应该记住，投影中定义的字段并不总是按顺序排列的：

```json
{
    ...
    "persons": [
        {
            "name": "John Doe",
            "id": 1,
            ...
        },
        ...
    ]
    ...
}
```

**为了保留字段顺序，我们可以在PersonView类上添加@JsonPropertyOrder注解**：

```java
@JsonPropertyOrder({"id", "name"})
@Projection(name = "person-view", types = Person.class)
public interface PersonView {
    // ...
}
```

## 5. 在Rest Repository上使用DTO

覆盖Rest控制器处理程序是另一种解决方案。Spring Data Rest允许我们插入自定义处理程序。因此，**我们仍然可以使用底层Repository来获取数据，但在响应到达客户端之前覆盖它**。在这种情况下，我们需要编写更多代码，但我们能够实现完全自定义的行为。

### 5.1 实现

首先，我们定义一个DTO对象来表示我们的Person实体：

```java
public class PersonDto {

    private Long id;

    private String name;

    public PersonDto(Person person) {
        this.id = person.getId();
        this.name = person.getName();
    }

    // getters and setters
}
```

可以看到，我们在这里添加了一个id字段，它对应于Person的实体id。然后，我们将使用一些内置的工具类来重用Spring Data Rest的响应构建机制，同时尽可能保持响应结构相同。

所以，我们定义一个PersonController来覆盖内置端点：

```java
@RepositoryRestController
public class PersonController {

    @Autowired
    private PersonRepository repository;

    @GetMapping("/persons")
    ResponseEntity<?> persons(PagedResourcesAssembler resourcesAssembler) {
        Page<Person> persons = this.repository.findAll(Pageable.ofSize(20));
        Page<PersonDto> personDtos = persons.map(PersonDto::new);
        PagedModel<EntityModel<PersonDto>> pagedModel = resourcesAssembler.toModel(personDtos);
        return ResponseEntity.ok(pagedModel);
    }
}
```

我们应该注意这里的一些要点，以确保Spring将我们的控制器类识别为插件，而不是独立的控制器：

1. 必须使用@RepositoryRestController注解，而不是@RestController或@Controller
2. PersonController类必须放在Spring的组件扫描可以发现的包下。或者，我们可以使用@Bean显式定义它。
3. @GetMapping路径必须与PersonRepository提供的路径相同。如果我们使用@RepositoryRestResource(path = "...")自定义路径，那么控制器的get映射也必须反映这一点。

最后，访问我们的端点http://localhost:8080/persons：

```json
{
    "_embedded": {
        "personDtoes": [
            {
                "id": 1,
                "name": "John Doe"
            },
            ...
        ]
    },
    ...
}
```

我们可以在响应中看到id字段。

### 5.2 缺点

如果我们使用DTO而不是Spring Data Rest的Repository，我们应该考虑几个方面。

某些开发人员不习惯将实体模型直接序列化到响应中。当然，它有一些缺点。**公开所有实体字段可能会导致数据泄漏、意外的延迟获取和性能问题**。

然而，**为所有端点编写我们的@RepositoryRestController是一种妥协**。它带走了框架的一些好处。此外，在这种情况下，我们需要维护更多的代码。

## 6. 总结

在本文中，我们介绍了使用Spring Data Rest时公开实体ID的多种方法。