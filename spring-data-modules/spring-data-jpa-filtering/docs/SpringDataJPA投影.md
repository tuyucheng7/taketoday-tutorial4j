## 1. 概述

当使用[Spring Data JPA]()实现持久层时，Repository通常会返回根类的一个或多个实例。然而，通常情况下，我们并不需要返回对象的所有属性。

在这种情况下，我们可能希望将数据作为自定义类型的对象进行检索。**这些类型反映了根类的部分视图，只包含我们关心的属性**，这就是投影派上用场的地方。

## 2. 初始设置

第一步是构建项目并填充数据库。

### 2.1 实体类

首先我们定义两个实体类：

```java
@Entity
public class Address {
 
    @Id
    private Long id;
 
    @OneToOne
    private Person person;
 
    private String state;
 
    private String city;
 
    private String street;
 
    private String zipCode;

    // getters and setters
}
```

和：

```java
@Entity
public class Person {
 
    @Id
    private Long id;
 
    private String firstName;
 
    private String lastName;
 
    @OneToOne(mappedBy = "person")
    private Address address;

    // getters and setters
}
```

Person和Address实体之间的关系是一对一的双向关系；Address是拥有方，Person是相反方。

请注意，在本教程中，我们使用嵌入式数据库H2。**配置嵌入式数据库后，Spring Boot会自动为我们定义的实体生成底层表**。

### 2.2 SQL脚本

我们使用projection-insert-data.sql脚本来为两个表添加一些初始数据：

```sql
INSERT INTO person(id, first_name, last_name)
VALUES (1, 'John', 'Doe');
INSERT INTO address(id, person_id, state, city, street, zip_code)
VALUES (1, 1, 'CA', 'Los Angeles', 'Standford Ave', '90001');
```

要在每次测试运行后清理数据库，我们可以使用另一个脚本projection-clean-up-data.sql：

```sql
DELETE FROM address;
DELETE FROM person;
```

### 2.3 测试类

然后，为了确认投影生成正确的数据，我们需要一个测试类：

```java
@DataJpaTest
@ExtendWith(SpringExtension.class)
@Sql(scripts = "/projection-insert-data.sql")
@Sql(scripts = "/projection-clean-up-data.sql", executionPhase = AFTER_TEST_METHOD)
class JpaProjectionIntegrationTest {
    // injected fields and test methods
}
```

使用给定的注解，**Spring Boot会创建数据库，注入依赖项，并在每个测试方法执行之前和之后填充和清理表**。

## 3. 基于接口的投影

在投影实体时，依赖接口是很一种很自然的方式，因为我们不需要提供实现。

### 3.1 封闭投影

回顾我们的Address类，我们可以看到它有很多属性，但并不是所有的属性都有用。例如，有时邮政编码足以表示地址。

下面我们为Address类声明一个投影接口：

```java
public interface AddressView {
	String getZipCode();
}
```

然后在我们的Repository接口中使用它：

```java
public interface AddressRepository extends Repository<Address, Long> {
	List<AddressView> getAddressByState(String state);
}
```

很容易看出，使用投影接口定义Repository方法与使用实体类几乎相同。**唯一的区别是，在返回的集合中使用投影接口而不是实体类作为元素类型**。

下面是AddressView投影的一个测试：

```java
@Autowired
private AddressRepository addressRepository;

@Test
void whenUsingClosedProjections_thenViewWithRequiredPropertiesIsReturned() {
	AddressView addressView = addressRepository.getAddressByState("CA").get(0);
    
	assertThat(addressView.getZipCode()).isEqualTo("90001");
}
```

在幕后，**Spring为每个实体对象创建了一个投影接口的代理实例，所有对代理的调用都转发给该对象**。

我们可以递归地使用投影。例如，下面是Person类的投影接口：

```java
public interface PersonView {
	String getFirstName();

	String getLastName();
}
```

现在我们将在AddressView中添加一个返回类型为PersonView的方法，这是一个嵌套投影：

```java
public interface AddressView {
	// ...
	PersonView getPerson();
}
```

**请注意，返回嵌套投影的方法必须与根类中返回相关实体的方法同名**。下面通过向我们刚刚编写的测试方法添加一些代码来验证嵌套投影：

```java
// ...
PersonView personView = addressView.getPerson();
assertThat(personView.getFirstName()).isEqualTo("John");
assertThat(personView.getLastName()).isEqualTo("Doe");
```

请注意，**递归投影仅在我们从拥有方遍历到相反方时才有效**。如果我们反过来做，嵌套投影将被设置为null。

### 3.2 开放投影

到目前为止，我们实现的是封闭投影，它表示投影接口的方法与实体属性的名称完全匹配。还有另一种基于接口的投影，开放式投影。**这些投影使我们能够定义具有不匹配名称和在运行时计算的返回值的接口方法**。

我们回到PersonView投影接口，并添加一个新的方法：

```java
public interface PersonView {
    // ...

    @Value("#{target.firstName + ' ' + target.lastName}")
    String getFullName();
}
```

@Value注解的参数是一个SpEL表达式，其中target指示符指示支持实体对象。

现在我们定义另一个Repository接口：

```java
public interface PersonRepository extends Repository<Person, Long> {
    PersonView findByLastName(String lastName);
}
```

为了简单起见，我们只返回单个投影对象而不是一个集合。下面的测试用于确认开放投影按预期工作：

```java
@Autowired
private PersonRepository personRepository;

@Test
void whenUsingOpenProjections_thenViewWithRequiredPropertiesIsReturned() {
	PersonView personView = personRepository.findByLastName("Doe");
    
	assertThat(personView.getFullName()).isEqualTo("John Doe");
}
```

不过，开放投影也有缺点；Spring Data无法优化查询执行，因为它事先不知道将使用哪些属性。**因此，我们应该只在封闭投影无法满足我们的需求时才使用开放投影**。

## 4. 基于类的投影

我们可以定义自己的投影类，而不是使用Spring Data从投影接口创建的代理。

例如，下面是Person实体的投影类：

```java
public class PersonDto {
	private String firstName;
	private String lastName;

	public PersonDto(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	// getters, equals and hashCode
}
```

**要使投影类与Repository接口协同工作，其构造函数的参数名称必须与根实体类的属性相匹配**。我们还必须定义equals和hashCode实现；它们允许Spring Data处理集合中的投影对象。

现在让我们向PersonRepository添加一个方法：

```java
public interface PersonRepository extends Repository<Person, Long> {
	// ...

	PersonDto findByFirstName(String firstName);
}
```

下面的测试用于验证基于类的投影能够按预期工作：

```java
@Test
void whenUsingClassBasedProjections_thenDtoWithRequiredPropertiesIsReturned() {
	PersonDto personDto = personRepository.findByFirstName("John");
    
	assertThat(personDto.getFirstName()).isEqualTo("John");
	assertThat(personDto.getLastName()).isEqualTo("Doe");
}
```

**注意，对于基于类的方法，我们不能使用嵌套投影**。

## 5. 动态投影

一个实体类可能有很多投影，在某些情况下，我们可能会使用某种类型，但在其他情况下，我们可能需要另一种类型。有时，我们还需要使用实体类本身。仅仅是为了支持多个返回类型而定义单独的Repository接口或方法是很麻烦的。针对这个问题，Spring Data提供了一个更好的解决方案，即动态投影。

**我们可以通过使用Class参数声明一个Repository方法来应用动态投影**：

```java
public interface PersonRepository extends Repository<Person, Long> {
	// ...

	<T> T findByLastName(String lastName, Class<T> type);
}
```

通过将投影类型或实体类传递给这样的方法，我们可以检索所需类型的对象：

```java
@Test
void whenUsingDynamicProjections_thenObjectWithRequiredPropertiesIsReturned() {
	Person person = personRepository.findByLastName("Doe", Person.class);
	PersonView personView = personRepository.findByLastName("Doe", PersonView.class);
	PersonDto personDto = personRepository.findByLastName("Doe", PersonDto.class);
    
	assertThat(person.getFirstName()).isEqualTo("John");
	assertThat(personView.getFirstName()).isEqualTo("John");
	assertThat(personDto.getFirstName()).isEqualTo("John");
}
```

## 6. 总结

在本文中，我们介绍了如何实现各种类型的Spring Data JPA投影。