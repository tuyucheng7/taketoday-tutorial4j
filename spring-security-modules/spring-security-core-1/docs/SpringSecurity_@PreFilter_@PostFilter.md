## 1. 概述

在本文中，我们将学习如何使用[@PreFilter](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/access/prepost/PreFilter.html)和[@PostFilter](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/access/prepost/PostFilter.html)注解来保护Spring应用程序中的资源。

当与经过身份验证的主体(principal)信息一起使用时，@PreFilter和@PostFilter允许我们使用SpEL(Spring表达式语言)定义细粒度的安全规则。

## 2. @PreFilter和@PostFilter介绍

简单地说，@PreFilter和@PostFilter注解用于根据我们定义的自定义安全规则**过滤对象列表**。

@PostFilter通过将该规则**应用于列表中的每个元素**来定义用于过滤方法返回列表的规则。如果评估值为true，则该元素将保留在列表中。否则，该元素将被删除。

@PreFilter的工作方式是类似的，但是，过滤应用于作为输入参数传递给注解方法的列表。

这两个注解都可以用于方法或类型(类和接口)。在本文中，我们只在方法上使用它们。

默认情况下，这些注解处于非激活状态-我们需要在我们的Security配置中使用@EnableGlobalMethodSecurity注解和prePostEnabled = true属性来启用它们：

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    // ...
}
```

## 3. 编写安全规则

要在这两个注解中编写安全规则-我们将使用SpEL表达式；我们还可以使用内置对象filterObject来获取对正在测试的特定列表元素的引用。

Spring Security提供了许多其他[内置对象](https://docs.spring.io/spring-security/reference/servlet/authorization/expression-based.html#el-common-built-in)来创建非常具体和精确的规则。

例如，我们可以使用@PreFilter来检查Task对象的assignee属性是否等于当前经过身份验证的用户的名称：

```java
@PostFilter("filterObject.assignee == authentication.name")
List<Task> findAll() {
    // ...
}
```

```java
@Entity
@Getter
@Setter
public class Task {
    @Id
    @GeneratedValue
    private Long id;
    private String description;
    private String assignee;

    public Task() {
    }

    public Task(String description, String assignee) {
        this.description = description;
        this.assignee = assignee;
    }
}
```

我们在这里使用了@PostFilter注解，因为我们希望该方法首先执行并获取所有Task对象，并且在返回的Task集合中的所有Task对象上应用我们的过滤规则。

因此，如果经过身份验证的用户是michael，即使数据库中有分配给jim和pam的任务，findAll()方法返回的最终Task列表也将仅包含分配给michael的任务。

现在让我们改变一下过滤的条件。假设用户是经理，他们可以看到所有任务，无论这些任务被分配给谁：

```java
@PostFilter("hasRole('MANAGER') or filterObject.assignee == authentication.name")
List<Task> findAll() {
    // ...
}
```

我们使用内置方法hasRole来检查经过身份验证的用户是否具有MANAGER角色。如果hasRole返回true，则任务将保留在最终列表中。因此，如果用户是经理，则该规则将为列表中的每个元素返回true。因此，最终返回的列表将包含所有Task对象。

现在，让我们使用@PreFilter过滤作为参数传递给save()方法的列表：

```java
@PreFilter("hasRole('MANAGER') or filterObject.assignee == authentication.name")
Iterable<Task> save(Iterable<Task> entities) {
    // ...
}
```

这里的过滤规则与我们在上面@PostFilter中使用的相同。主要的区别是列表元素将在方法执行之前被过滤，从而允许我们在保存到数据库之前删除一些不匹配的Task对象。

因此，如果jim(不是经理)作为经过身份验证的普通用户，并尝试保存一些Task对象，其中一些任务分配给pam。但是，只有分配给jim的任务才会被包括在内并最终保存到数据库中，其他任务将被忽略。

## 4. 大型集合的性能

@PreFilter很强大且易于使用，但在处理非常大的集合时可能效率很低，因为获取操作将检索所有数据并在之后应用过滤器。

例如，假设我们的数据库中有数千个任务，我们想要检索当前分配给pam的五个任务。如果我们使用@PreFilter，数据库操作将首先获取所有任务，并遍历所有任务以过滤掉不是分配给pam的任务。

## 5. 总结

这篇简短的文章解释了如何使用Spring Security中的@PreFilter和@PostFilter注解创建一个简单但安全的应用程序。