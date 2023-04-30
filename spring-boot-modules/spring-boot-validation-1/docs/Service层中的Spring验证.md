## 1. 概述

在本教程中，我们将讨论Java应用程序的Service层中的Spring验证。**尽管Spring Boot支持与自定义验证器的无缝集成，但执行验证的实际标准是**[Hibernate Validator](http://hibernate.org/validator/)。

在这里，我们将学习如何将我们的验证逻辑从我们的控制器中移出并转移到一个单独的服务层中。此外，我们将在Spring应用程序的服务层中实现验证。

## 2. 应用分层

根据需求，Java业务应用程序可以采用多种不同的形式和类型。例如，我们必须根据这些标准确定我们的应用程序需要哪些层。除非有特定需求，否则许多应用程序不会从服务或持久层增加的复杂性和维护成本中受益。

我们可以通过使用多层来解决所有这些问题，这些层是：

<img src="../assets/img.png">

消费者层或Web层是Web应用程序的最顶层，**它负责解释用户的输入并提供适当的响应，其他层抛出的异常也必须由Web层处理**。由于Web层是我们应用程序的入口点，因此它负责身份验证并充当防止未经授权用户的第一道防线。

Web层之下是Service层，它充当事务屏障，并包含应用程序和基础设施服务。此外，服务层的公共API由应用服务提供，**它们通常作为事务边界，负责授权事务**。基础架构服务提供连接到外部工具(包括文件系统、数据库和电子邮件服务器)的“管道代码”，这些方法通常由多个应用程序服务使用。

**Web应用程序的最低层是持久层**，换句话说，它负责与用户的数据存储进行交互。

## 3. 服务层中的验证

服务层是应用程序中促进控制器和持久层之间通信的层，此外，业务逻辑存储在服务层中。它特别包括验证逻辑，模型状态用于在控制器层和服务层之间进行通信。

将验证作为业务逻辑处理有优点也有缺点，Spring的验证(和数据绑定)架构也不排除这一点。**特别是，验证不应该绑定到Web层，应该易于本地化，并且应该允许使用任何可用的验证程序**。

此外，客户端输入数据并不总是通过REST控制器进程，**如果我们不在服务层也进行验证，则不可接受的数据可能会通过，从而导致多个问题**。在这种情况下，**我们将使用标准的Java JSR-303验证方案**。

## 4. 示例

让我们考虑一个使用Spring Boot开发的简单用户帐户注册表单。

### 4.1 简单域类

首先，我们只有姓名、年龄、电话和密码属性：

```java
public class UserAccount {

    @NotNull(message = "Password must be between 4 to 15 characters")
    @Size(min = 4, max = 15)
    private String password;

    @NotBlank(message = "Name must not be blank")
    private String name;

    @Min(value = 18, message = "Age should not be less than 18")
    private int age;

    @NotBlank(message = "Phone must not be blank")
    private String phone;

    // standard constructors / setters / getters / toString
}
```

在上面的类中，我们使用了四个注解(@NotNull、@Size、@NotBlank和@Min)，以确保输入属性既不为null也不为empty，并符合大小要求。

### 4.2 在服务层中实现验证

有许多可用的验证解决方案，由Spring或Hibernate处理实际验证。**另一方面，手动验证是一种可行的替代方法**，在将验证集成到我们应用程序的正确部分时，这为我们提供了很大的灵活性。

接下来，让我们在服务类中实现我们的验证：

```java
@Service
public class UserAccountService {

    @Autowired
    private Validator validator;

    @Autowired
    private UserAccountDao dao;

    public String addUserAccount(UserAccount useraccount) {
        Set<ConstraintViolation<UserAccount>> violations = validator.validate(useraccount);

        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<UserAccount> constraintViolation : violations) {
                sb.append(constraintViolation.getMessage());
            }
            throw new ConstraintViolationException("Error occurred: " + sb, violations);
        }

        dao.addUserAccount(useraccount);
        return "Account for " + useraccount.getName() + " Added!";
    }
}
```

**Validator是Bean Validation API的一部分，负责验证Java对象**。此外，Spring自动提供了一个Validator实例，我们可以将其注入到我们的UserAccountService中。Validator用于验证 validate(..)函数中传递的对象，结果是一组ConstraintViolation。

如果没有违反验证约束(对象有效)，则Set为空；否则，我们抛出一个ConstraintViolationException。

### 4.3 实现REST控制器

在此之后，让我们构建Spring REST Controller类以向客户端或最终用户显示服务并评估应用程序的输入验证：

```java
@RestController
public class UserAccountController {

    @Autowired
    private UserAccountService service;

    @PostMapping("/addUserAccount")
    public Object addUserAccount(@RequestBody UserAccount userAccount) {
        return service.addUserAccount(userAccount);
    }
}
```

我们没有在上面的REST控制器表单中使用@Valid注解来阻止任何验证。

### 4.4 测试REST控制器

现在，让我们通过运行Spring Boot应用程序来测试此方法。之后，使用Postman或任何其他API测试工具，我们将JSON输入发布到localhost:8080/addUserAccount URL：

```json
{
    "name": "Tuyucheng",
    "age": 25,
    "phone": "1234567890",
    "password": "test",
    "useraddress": {
        "countryCode": "CN"
    }
}
```

确认测试成功运行后，现在让我们检查验证是否按预期进行。下一个合乎逻辑的步骤是使用少量无效输入测试应用程序，因此，我们将使用无效值更新我们的输入JSON：

```json
{
    "name": "",
    "age": 25,
    "phone": "1234567890",
    "password": "",
    "useraddress": {
        "countryCode": "CN"
    }
}
```

控制台现在显示错误消息，因此，**我们可以看到Validator的使用对于验证是多么重要**：

```shell
Error occurred: Password must be between 4 to 15 characters, Name must not be blank
```

## 5. 优点和缺点

**在服务/业务层，这通常是一种成功的验证方法**，它不限于方法参数，可以应用于各种对象。例如，我们可以从数据库中加载一个对象，对其进行更改，然后在继续之前对其进行验证。

我们还可以将此方法用于单元测试，这样我们就可以实际Mock Service类。**为了便于在单元测试中进行真正的验证，我们可以手动生成必要的Validator实例**。

这两种情况都不需要在我们的测试中引导Spring应用程序上下文。

## 6. 总结

在本快速教程中，我们探讨了Java业务应用程序的不同层，我们学习了如何将验证逻辑从我们的控制器中移出并转移到一个单独的服务层中。此外，我们实现了一种在Spring应用程序的服务层中执行验证的方法。