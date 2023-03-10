## 1. 概述

我们有多种使用Java应用程序连接到数据库的选项。通常，我们引用不同的层，从[JDBC](https://www.baeldung.com/java-jdbc)开始。然后，我们转向[JPA](https://www.baeldung.com/learn-jpa-hibernate)，使用Hibernate等实现。JPA最终将使用JDBC，但使用对象-实体管理方法使其对用户更加透明。

最后，我们可以有一个类似框架的集成，例如[Spring Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)，它具有用于访问实体的预定义接口，但仍然在底层使用JPA和实体管理器。

在本教程中，我们将讨论Spring Data JPA和JPA之间的区别。我们还将通过一些高级概述和代码片段来解释它们如何工作。让我们首先解释一下JDBC的一些历史以及JPA是如何产生的。

## 2. 从JDBC到JPA

自1997年JDK(Java开发工具包) 1.1版本以来，我们就可以使用[JDBC](https://docs.oracle.com/javase/tutorial/jdbc/basics/index.html)访问关系型数据库。

关于JDBC的要点对于理解JPA也是必不可少的，包括：

- [DriverManager](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/DriverManager.html)和用于连接和执行查询的接口：这使得通常使用特定驱动程序(例如[MySQL Java连接器)](https://www.baeldung.com/java-connect-mysql)可以连接到任何ODBC可访问的数据源。我们可以连接到数据库并在其上打开/关闭事务。最重要的是，我们只需更改数据库驱动程序就可以使用任何数据库，如MySQL、Oracle或PostgreSQL。
- [数据源](https://docs.oracle.com/javase/tutorial/jdbc/basics/sqldatasources.html)：对于[Java Enterprise](https://www.baeldung.com/java-enterprise-evolution)和像Spring这样的框架，了解我们如何在工作上下文中定义和获取数据库连接很重要。
- 连接池，其作用类似于数据库连接对象的缓存：我们可以重用处于主动/被动状态的打开的连接，并减少它们的创建次数。
- 分布式事务：它们由一个或多个语句组成，这些语句在同一事务中更新多个数据库或资源上的数据。

在JDBC创建之后，像[Hibernate](https://en.wikipedia.org/wiki/Hibernate_(framework))这样的持久性框架(或ORM工具)开始出现，它将数据库资源映射为[普通的旧Java对象。](https://en.wikipedia.org/wiki/Plain_old_Java_object)我们将ORM称为定义模式生成或数据库方言等的层。

此外，EntityJavaBean([EJB](https://en.wikipedia.org/wiki/Jakarta_Enterprise_Beans))创建标准来管理封装应用程序业务逻辑的服务器端组件。事务处理、JNDI和持久性服务等功能现在都是Javabeans。此外，[注解](https://en.wikipedia.org/wiki/Java_annotation)和[依赖注入](https://en.wikipedia.org/wiki/Dependency_injection)现在简化了不同系统的配置和集成。

[随着EJB3.0的发布，持久性框架被合并到JavaPersistenceAPI(JPA)中，Hibernate或EclipseLink](https://en.wikipedia.org/wiki/EclipseLink)等项目已成为JPA规范的实现。

## 3.联合行动计划

使用JPA，我们可以独立于所使用的数据库，以面向对象的语法编写构建块。

为了进行演示，让我们看一个员工表定义的示例。[我们最终可以使用@Entity](https://www.baeldung.com/jpa-entities)注解将表定义为POJO：

```java
@Entity
@Table(name = "employee")
public class Employee implements Serializable {

    @Id
    @Generated
    private Long id;

    @Column(nullable = false)
    private String firstName;

    // other fields, setter and getters
}
```

JPA类可以管理数据库表功能，例如[主键策略](https://www.baeldung.com/jpa-strategies-when-set-primary-key)和[多对多](https://www.baeldung.com/jpa-many-to-many)关系。例如，在使用外键时，这是相关的。JPA可以[延迟初始化集合](https://www.baeldung.com/java-jpa-lazy-collections)并仅在我们需要时访问数据。

[我们可以使用EntityManager](https://www.baeldung.com/hibernate-entitymanager)对实体执行所有CRUD操作(创建、检索、更新、删除)。JPA正在隐式处理事务。[这可以通过像Spring事务管理](https://docs.spring.io/spring-framework/docs/4.2.x/spring-framework-reference/html/transaction.html)这样的容器来完成，或者简单地通过[使用EntityManager](https://www.baeldung.com/hibernate-entitymanager)的Hibernate这样的ORM工具来完成。

一旦我们访问了[EntityManager](https://jakarta.ee/specifications/persistence/3.0/apidocs/jakarta.persistence/jakarta/persistence/entitymanager)，我们就可以，例如，持久化一个Employee：

```java
Employee employee = new Employee();
// set properties
entityManager.persist(employee);
```

### 3.1.条件查询和JPQL

例如，我们可以通过id找到一个Employee：

```java
Employee response = entityManger.find(Employee.class, id);
```

更有趣的是，我们可以以类型安全的方式使用[CriteriaQueries与](https://www.baeldung.com/hibernate-criteria-queries)@Entity进行交互。比如还是通过id查找，我们可以使用[CriteriaQuery](https://jakarta.ee/specifications/persistence/2.2/apidocs/javax/persistence/criteria/criteriaquery)接口：

```java
CriteriaBuilder cb = entityManager.getCriteriaBuilder();
CriteriaQuery<Employee> cr = cb.createQuery(Employee.class);
Root<Employee> root = cr.from(Employee.class);
cr.select(root);
criteriaQuery.where(criteriaBuilder.equal(root.get(Employee_.ID), employee.getId()));
Employee employee = entityManager.createQuery(criteriaQuery).getSingleResult();
```

此外，我们还可以应用[排序](https://www.baeldung.com/jpa-sort)和[分页](https://www.baeldung.com/jpa-pagination)：

```java
criteriaQuery.orderBy(criteriaBuilder.asc(root.get(Employee_.FIRST_NAME)));

TypedQuery<Employee> query = entityManager.createQuery(criteriaQuery);
query.setFirstResult(0);
query.setMaxResults(3);

List<Employee> employeeList = query.getResultList();
```

我们可以使用条件查询来实现持久化。例如，我们可以使用[CriteriaUpdate](https://jakarta.ee/specifications/persistence/3.0/apidocs/jakarta.persistence/jakarta/persistence/criteria/criteriaupdate)接口进行更新。假设我们要更新员工的电子邮件地址：

```java
CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
CriteriaUpdate<Employee> criteriaQuery = criteriaBuilder.createCriteriaUpdate(Employee.class);
Root<Employee> root = criteriaQuery.from(Employee.class);
criteriaQuery.set(Employee_.EMAIL, email);
criteriaQuery.where(criteriaBuilder.equal(root.get(Employee_.ID), employee));

entityManager.createQuery(criteriaQuery).executeUpdate();
```

最后，JPA还提供了[JPQL](https://www.baeldung.com/jpql-hql-criteria-query)(如果我们本机使用Hibernate，则为HQL)，它允许我们以类似SQL的语法创建查询，但仍然引用@Entitybean：

```java
public Employee getEmployeeById(Long id) {
    Query jpqlQuery = getEntityManager().createQuery("SELECT e from Employee e WHERE e.id=:id");
    jpqlQuery.setParameter("id", id);
    return jpqlQuery.getSingleResult();
}
```

### 3.2.JDBC

JPA可以适应许多具有通用接口的不同数据库。然而，在实际应用程序中，我们很可能需要JDBC支持。这是为了使用特定的数据库查询语法或出于性能原因，例如，在批处理中。

即使我们使用JPA，我们仍然可以使用createNativeQuery方法以数据库的本机语言编写。例如，我们可能想使用rownumOracle关键字：

```java
Query query = entityManager
    .createNativeQuery("select * from employee where rownum < :limit", Employee.class);
query.setParameter("limit", limit);
List<Employee> employeeList = query.getResultList();
```

此外，这适用于仍然与特定于数据库的语言相关的各种函数和过程。例如，我们可以创建并执行一个存储过程：

```java
StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery("calculate_something");
// set parameters
storedProcedure.execute();
Double result = (Double) storedProcedure.getOutputParameterValue("output");
```

### 3.3.注解

JPA带有一组注解。我们已经看到了@Table、@Entity、@Id和@Column。

如果我们经常重用一个查询，我们可以在类级别使用@Entity将其注解为[@NamedQuery](https://jakarta.ee/specifications/persistence/2.2/apidocs/javax/persistence/namedquery)，仍然使用JPQL：

```java
@NamedQuery(name="Employee.findById", query="SELECT e FROM Employee e WHERE e.id = :id")
```

然后，我们可以从模板创建一个查询：

```java
Query query = em.createNamedQuery("Employee.findById", Employee.class);
query.setParameter("id", id);
Employee result = query.getResultList();
```

与@NamedQuery类似，我们可以使用[@NamedNativeQuery](https://jakarta.ee/specifications/persistence/2.2/apidocs/javax/persistence/namednativequery)进行数据库原生查询：

```java
@NamedNativeQuery(name="Employee.findAllWithLimit", query="SELECT * FROM employee WHERE rownum < :limit")
```

### 3.4.元模型

我们可能想要生成一个[元模型](https://jakarta.ee/specifications/persistence/2.2/apidocs/javax/persistence/metamodel/package-summary.html)，允许我们以类型安全的方式静态访问表字段。例如，让我们看看从Employee生成的Employee_类：

```java
@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Employee.class)
public abstract class Employee_ {

    public static volatile SingularAttribute<Employee, String> firstName;
    public static volatile SingularAttribute<Employee, String> lastName;
    public static volatile SingularAttribute<Employee, Long> id;
    public static volatile SingularAttribute<Employee, String> email;

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String ID = "id";
    public static final String EMAIL = "email";
}
```

我们可以静态访问这些字段。如果我们更改数据模型，该类将重新生成该类。

## 4.春季数据JPA

作为大型[Spring Data](https://www.baeldung.com/spring-data)系列的一部分，Spring Data JPA是作为JPA之上的抽象层构建的。因此，我们拥有JPA的所有功能以及易于开发的Spring。

多年来，开发人员编写了样板代码来为基本功能创建[JPADAO。](https://www.baeldung.com/spring-dao-jpa)Spring通过提供最少的接口和实际实现来帮助显着减少代码量。

### 4.1.资料库

例如，假设我们要为Employee表创建一个CRUD存储库。我们可以使用[JpaRepository](https://docs.spring.io/spring-data/jpa/docs/current/api/org/springframework/data/jpa/repository/JpaRepository.html)：

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
```

这就是我们开始所需要的。所以，如果我们想要持久化或更新，我们可以获取存储库的一个实例并保存一个员工：

```java
employeeRepository.save(employee);
```

我们也非常支持编写查询。有趣的是，我们可以通过简单地声明它们的方法签名来定义查询方法：

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByFirstName(String firstName);
}
```

Spring将在运行时从存储库接口自动创建存储库实现。

因此，我们可以使用这些方法而无需实现它们：

```java
List<Employee> employees = employeeRepository.findByFirstName("John");
```

我们还支持对存储库[进行排序和分页](https://www.baeldung.com/spring-data-jpa-pagination-sorting)：

```java
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long> {
}
```

然后我们可以创建一个具有页面大小、数量和排序标准的[Pageable对象：](https://docs.spring.io/spring-data/data-commons/docs/current/api/org/springframework/data/domain/Pageable.html)

```java
Pageable pageable = PageRequest.of(5, 10, Sort.by("firstName"));
Page<Employee> employees = employeeRepositorySortAndPaging.findAll(pageable);
```

### 4.2.查询

[另一个很棒的特性是对@Query](https://www.baeldung.com/spring-data-jpa-query)注解的广泛支持。与JPA类似，这有助于定义类似JPQL或本机查询。让我们看一个示例，说明如何在存储库界面中使用它通过应用排序来获取员工列表：

```java
@Query(value = "SELECT e FROM Employee e")
List<Employee> findAllEmployee(Sort sort);
```

同样，我们将使用存储库并获取列表：

```java
List<Employee> employees = employeeRepository.findAllEmployee(Sort.by("firstName"));

```

### 4.3.查询Dsl

与JPA类似，我们有类似条件的支持，称为[QueryDsl](https://www.baeldung.com/rest-api-search-language-spring-data-querydsl)，它也有一个元模型生成。例如，假设我们想要一个员工列表，过滤名称：

```java
QEmployee employee = QEmployee.employee;
List<Employee> employees = queryFactory.selectFrom(employee)
    .where(
        employee.firstName.eq("John"),
        employee.lastName.eq("Doe"))
    .fetch();
```

## 5.JPA测试

让我们创建并测试一个简单的JPA应用程序。我们可以让Hibernate管理事务性。

### 5.1.依赖关系

让我们看一下依赖关系。我们需要在pom.xml中导入[JPA](https://search.maven.org/artifact/javax.persistence/javax.persistence-api/2.2/jar)、[Hibernate核心](https://search.maven.org/artifact/org.hibernate/hibernate-core/5.6.11.Final/jar)和[H2](https://search.maven.org/artifact/com.h2database/h2/2.1.214/jar)数据库。

```xml
<dependency>
    <groupId>javax.persistence</groupId>
    <artifactId>javax.persistence-api</artifactId>
    <version>2.2</version>
</dependency>
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.6.11-Final</version>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.1.214</version>
</dependency>
```

此外，我们需要一个用于元模型生成的插件：

```xml
<plugin>
    <groupId>org.bsc.maven</groupId>
    <artifactId>maven-processor-plugin</artifactId>
    <version>3.3.3</version>
    <executions>
        <execution>
            <id>process</id>
            <goals>
                <goal>process</goal>
            </goals>
            <phase>generate-sources</phase>
            <configuration>
                <outputDirectory>${project.build.directory}/generated-sources</outputDirectory>
                <processors>
                    <processor>org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor</processor>
                </processors>
            </configuration>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-jpamodelgen</artifactId>
            <version>5.6.11.Final</version>
        </dependency>
    </dependencies>
</plugin>
```

### 5.2.配置

为了保持简单的JPA，我们使用一个persistence.xml文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence
             http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd"
             version="2.1">
    <persistence-unit name="pu-test">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        <class>com.baeldung.spring.data.persistence.springdata_jpa_difference.model.Employee</class>
        <properties>
            <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>
            <property name="javax.persistence.jdbc.url" value="jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"/>
            <property name="javax.persistence.jdbc.user" value="sa"/>
            <property name="javax.persistence.jdbc.password" value=""/>
            <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>
            <property name="hibernate.hbm2ddl.auto" value="create-drop"/>
        </properties>
    </persistence-unit>
</persistence>
```

我们不需要任何基于Bean的配置。

### 5.3.测试类定义

为了演示，让我们创建一个测试类。我们将使用createEntityManagerFactory方法获取EntityManager并手动管理事务：

```java
public class JpaDaoIntegrationTest {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu-test");
    private final EntityManager entityManager = emf.createEntityManager();

    @Before
    public void setup() {
        deleteAllEmployees();
    }

    // tests

    private void deleteAllEmployees() {
        entityManager.getTransaction()
              .begin();
        entityManager.createNativeQuery("DELETE from Employee")
              .executeUpdate();
        entityManager.getTransaction()
              .commit();
    }

    public void save(Employee entity) {
        entityManager.getTransaction()
              .begin();
        entityManager.persist(entity);
        entityManager.getTransaction()
              .commit();
    }

    public void update(Employee entity) {
        entityManager.getTransaction()
              .begin();
        entityManager.merge(entity);
        entityManager.getTransaction()
              .commit();
    }

    public void delete(Long employee) {
        entityManager.getTransaction()
              .begin();
        entityManager.remove(entityManager.find(Employee.class, employee));
        entityManager.getTransaction()
              .commit();
    }

    public int update(CriteriaUpdate<Employee> criteriaUpdate) {
        entityManager.getTransaction()
              .begin();
        int result = entityManager.createQuery(criteriaUpdate)
              .executeUpdate();
        entityManager.getTransaction()
              .commit();
        entityManager.clear();

        return result;
    }
}
```

### 5.4.测试JPA

首先，我们要测试是否可以通过id找到员工：

```java
@Test
public void givenPersistedEmployee_whenFindById_thenEmployeeIsFound() {
    // save employee
    assertEquals(employee, entityManager.find(Employee.class, employee.getId()));
}
```

让我们看看我们可以找到Employee的其他方法。例如，我们可以使用CriteriaQuey：

```java
@Test
public void givenPersistedEmployee_whenFindByIdCriteriaQuery_thenEmployeeIsFound() {
    // save employee
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);
    Root<Employee> root = criteriaQuery.from(Employee.class);
    criteriaQuery.select(root);

    criteriaQuery.where(criteriaBuilder.equal(root.get(Employee_.ID), employee.getId()));

    assertEquals(employee, entityManager.createQuery(criteriaQuery)
        .getSingleResult());
}
```

另外，我们可以使用JPQL：

```java
@Test
public void givenPersistedEmployee_whenFindByIdJpql_thenEmployeeIsFound() {
    // save employee
    Query jpqlQuery = entityManager.createQuery("SELECT e from Employee e WHERE e.id=:id");
    jpqlQuery.setParameter("id", employee.getId());

    assertEquals(employee, jpqlQuery.getSingleResult());
}
```

让我们看看如何从@NamedQuery创建一个查询：

```java
@Test
public void givenPersistedEmployee_whenFindByIdNamedQuery_thenEmployeeIsFound() {
    // save employee
    Query query = entityManager.createNamedQuery("Employee.findById");
    query.setParameter(Employee_.ID, employee.getId());

    assertEquals(employee, query.getSingleResult());
}
```

我们还可以看一个如何应用排序和分页的示例：

```java
@Test
public void givenPersistedEmployee_whenFindWithPaginationAndSort_thenEmployeesAreFound() {
    // save John, Frank, Bob, James
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);
    Root<Employee> root = criteriaQuery.from(Employee.class);
    criteriaQuery.select(root);
    criteriaQuery.orderBy(criteriaBuilder.asc(root.get(Employee_.FIRST_NAME)));

    TypedQuery<Employee> query = entityManager.createQuery(criteriaQuery);

    query.setFirstResult(0);
    query.setMaxResults(3);

    List<Employee> employeeList = query.getResultList();

    assertEquals(Arrays.asList(bob, frank, james), employeeList);
}
```

最后，让我们看看如何使用CriteriaUpdate更新员工电子邮件：

```java
@Test
public void givenPersistedEmployee_whenUpdateEmployeeEmailWithCriteria_thenEmployeeHasUpdatedEmail() {
    // save employee
    String updatedEmail = "email@gmail.com";

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaUpdate<Employee> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Employee.class);
    Root<Employee> root = criteriaUpdate.from(Employee.class);

    criteriaUpdate.set(Employee_.EMAIL, updatedEmail);
    criteriaUpdate.where(criteriaBuilder.equal(root.get(Employee_.ID), employee.getId()));

    assertEquals(1, update(criteriaUpdate));
    assertEquals(updatedEmail, entityManager.find(Employee.class, employee.getId())
        .getEmail());
}
```

## 6.Spring Data JPA测试

让我们看看如何通过添加Spring存储库和内置查询支持来改进。

### 6.1.依赖关系

在这种情况下，我们需要添加[Spring Data](https://search.maven.org/artifact/org.springframework.data/spring-data-jpa/2.7.5/jar)依赖项。我们还需要Fluent查询API的[QueryDsl](https://search.maven.org/artifact/com.querydsl/querydsl-jpa/5.0.0/jar)依赖项。

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-jpa</artifactId>
    <version>2.7.5</version>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.2.214</version>
</dependency>
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-apt</artifactId>
    <version>5.0.0</version>
</dependency>
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-jpa</artifactId>
    <version>5.0.0</version>
</dependency>
```

### 6.2.配置

首先，让我们创建我们的配置：

```java
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = EmployeeRepository.class)
public class SpringDataJpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(Employee.class.getPackage().getName());

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

        em.setJpaProperties(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactoryBean.getObject());
        return transactionManager;
    }

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
          .url("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1")
          .driverClassName("org.h2.Driver")
          .username("sa")
          .password("sa")
          .build();
    }

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory((entityManager));
    }
}

```

最后，让我们看看我们的JpaRepository：

```java
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByFirstName(String firstName);

    @Query(value = "SELECT e FROM Employee e")
    List<Employee> findAllEmployee(Sort sort);
}
```

另外，我们想使用PagingAndSortingRepository：

```java
@Repository
public interface EmployeeRepositoryPagingAndSort extends PagingAndSortingRepository<Employee, Long> {
}
```

### 6.3.测试类定义

让我们看看我们用于Spring Data测试的测试类。我们将回滚以保持每个测试的原子性：

```java
@ContextConfiguration(classes = SpringDataJpaConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@Rollback
public class SpringDataJpaIntegrationTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeRepositoryPagingAndSort employeeRepositoryPagingAndSort;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    // tests

}
```

### 6.4.测试Spring Data JPA

让我们从通过id查找员工开始：

```java
@Test
public void givenPersistedEmployee_whenFindById_thenEmployeeIsFound() {
    Employee employee = employee("John", "Doe");

    employeeRepository.save(employee);

    assertEquals(Optional.of(employee), employeeRepository.findById(employee.getId()));
}

```

让我们看看如何通过名字查找员工：

```java
@Test
public void givenPersistedEmployee_whenFindByFirstName_thenEmployeeIsFound() {
    Employee employee = employee("John", "Doe");

    employeeRepository.save(employee);

    assertEquals(employee, employeeRepository.findByFirstName(employee.getFirstName())
      .get(0));
}

```

我们可以应用排序，例如，在查询所有员工时：

```java
@Test
public void givenPersistedEmployees_whenFindSortedByFirstName_thenEmployeeAreFoundInOrder() {
    Employee john = employee("John", "Doe");
    Employee bob = employee("Bob", "Smith");
    Employee frank = employee("Frank", "Brown");

    employeeRepository.saveAll(Arrays.asList(john, bob, frank));

    List<Employee> employees = employeeRepository.findAllEmployee(Sort.by("firstName"));

    assertEquals(3, employees.size());
    assertEquals(bob, employees.get(0));
    assertEquals(frank, employees.get(1));
    assertEquals(john, employees.get(2));
}

```

让我们看看如何使用QueryDsl构建查询：

```java
@Test
public void givenPersistedEmployee_whenFindByQueryDsl_thenEmployeeIsFound() {
    Employee john = employee("John", "Doe");
    Employee frank = employee("Frank", "Doe");

    employeeRepository.saveAll(Arrays.asList(john, frank));

    QEmployee employeePath = QEmployee.employee;

    List<Employee> employees = jpaQueryFactory.selectFrom(employeePath)
      .where(employeePath.firstName.eq("John"), employeePath.lastName.eq("Doe"))
      .fetch();

    assertEquals(1, employees.size());
    assertEquals(john, employees.get(0));
}

```

最后，我们可以检查如何使用PagingAndSortingRepository：

```java
@Test
public void givenPersistedEmployee_whenFindBySortAndPagingRepository_thenEmployeeAreFound() {
    Employee john = employee("John", "Doe");
    Employee bob = employee("Bob", "Smith");
    Employee frank = employee("Frank", "Brown");
    Employee jimmy = employee("Jimmy", "Armstrong");

    employeeRepositoryPagingAndSort.saveAll(Arrays.asList(john, bob, frank, jimmy));

    Pageable pageable = PageRequest.of(0, 2, Sort.by("firstName"));

    Page<Employee> employees = employeeRepositoryPagingAndSort.findAll(pageable);

    assertEquals(Arrays.asList(bob, frank), employees.get()
      .collect(Collectors.toList()));
}

```

## 7.JPA和Spring Data JPA有何不同

JPA定义了对象关系映射(ORM)的标准方法。

它提供了一个抽象层，使其独立于我们正在使用的数据库。JPA还可以处理事务性并且构建在JDBC之上，因此我们仍然可以使用本机数据库语言。

Spring Data JPA是JPA之上的另一层抽象。但是，它比JPA更灵活，并为所有CRUD操作提供简单的存储库和语法。我们可以从JPA应用程序中删除所有样板代码，并使用更简单的接口和注解。此外，我们将拥有Spring的开发便利性，例如，透明地处理事务性。

此外，没有JPA就没有Spring Data JPA，所以无论如何，如果我们想了解更多有关Java数据库访问层的知识，JPA是一个很好的起点。

## 八、结论

在本教程中，我们简要了解了JDBC的历史以及JPA为何成为关系数据库API的标准。我们还看到了用于持久化实体和创建动态查询的JPA和Spring Data JPA示例。最后，我们提供了一些测试用例来展示普通JPA应用程序和Spring Data JPA应用程序之间的区别。