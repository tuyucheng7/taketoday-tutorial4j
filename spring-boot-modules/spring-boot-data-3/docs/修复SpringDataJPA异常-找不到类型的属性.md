## 1. 概述

在这篇简短的文章中，我们将阐明如何修复Spring Data JPA异常PropertyReferenceException: No property found for type。

首先，我们将解释此异常背后的主要原因。然后，我们将使用实际示例说明如何重现它，最后说明如何修复它。

## 2. 原因

在深入细节之前，让我们试着理解异常的含义。

堆栈跟踪“No property found for type”只是告诉我们没有找到指定的属性。**Spring Data在无法访问不存在或尚未定义的属性时会引发此异常**。

通常，Spring Data会根据[派生查询方法](https://www.baeldung.com/spring-data-derived-queries)的名称自动生成SQL查询。因此，异常背后最常见的原因之一是**定义了一个具有无效属性的查询方法**。

另一个原因是在**访问属性时拼写错误的属性名称**。

## 3. 重现异常

现在我们知道了异常的含义，让我们看看如何在实践中重现它。

例如，让我们考虑Person[实体类](https://www.baeldung.com/jpa-entities)：

```java
@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstName;
    private String lastName;

    // standard getters and setters
}
```

现在，让我们为Person类创建一个[Spring Data JPA Repository](https://www.baeldung.com/spring-data-repositories)：

```java
@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
}
```

接下来，我们将添加一个查询方法以通过名字获取Person。但是，让我们假装拼错firstName属性。

例如，让我们改写firsttName：

```java
Person findByFirsttName(String lastName);
```

现在，如果我们启动我们的Spring Boot应用程序，我们将得到：

```shell
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'personRepository' defined in cn.tuyucheng.taketoday.nopropertyfound.repository.PersonRepository ...
...
Caused by: java.lang.IllegalArgumentException: Failed to create query for method public abstract cn.tuyucheng.taketoday.nopropertyfound.model.Person cn.tuyucheng.taketoday.nopropertyfound.repository.PersonRepository.findByFirsttName(java.lang.String)! 
No property 'firsttName' found for type 'Person'; Did you mean 'firstName'
...
Caused by: org.springframework.data.mapping.PropertyReferenceException: No property 'firsttName' found for type 'Person'; Did you mean 'firstName'
...
```

正如我们在日志中看到的那样，Spring Boot失败并出现PropertyReferenceException: No property 'firsttName' found for type 'Person'。

**Spring Data在启动时自动检查生成的SQL查询是否有效**。

由于我们使用的是不存在的属性(firstName)，Spring Data无法生成SQL查询，因此出现异常。

## 4. 解决方案

**只有一种方法可以修复异常：在定义查询方法时使用准确的属性名称**。

因此，要解决此问题，我们需要将findByFirsttName()重命名为findByFirstName()。

```java
Person findByFirstName(String firstName);
```

现在，让我们使用一个测试用例来确认一切都按预期工作：

```java
@Test
void givenQueryMethod_whenUsingValidProperty_thenCorrect() {

    Person person = personRepository.findByFirstName("Azhrioun");

    assertNotNull(person);
    assertEquals("Abderrahim", person.getLastName());
}
```

如上所示，查询方法findByFirstName()不会失败，并且会按名字返回Person。

简而言之，以下是一些避免PropertyReferenceException的关键点：

-   定义查询方法时使用准确的属性名称
-   避免使用缩写或拼写错误的名称
-   仔细检查实体类以确保我们要引用的属性存在并且拼写正确
-   使用具有自动完成功能的代码编辑器来降低属性名称拼写错误的风险
-   为查询方法编写单元测试或集成测试，以确保它们返回预期的结果

## 5. 总结

我们详细讨论了导致Spring Data抛出PropertyReferenceException: No property found for type的原因。

在此过程中，我们使用实际示例来说明如何产生异常以及如何修复它。