## 1. 概述

当我们在IDE中运行代码分析工具时，对于带有*@Autowired注解的字段，它可能会发出“**不推荐字段注入*”的警告。

在本教程中，我们将探讨为什么不建议使用现场注入以及我们可以使用哪些替代方法。

## 2. 依赖注入

对象使用其依赖对象而不需要定义或创建它们的过程称为[依赖注入](https://www.baeldung.com/spring-dependency-injection)。它是Spring框架的核心功能之一。

我们可以通过三种方式注入依赖对象，使用：

- 构造函数注入
- 二传手注射
- 现场注入

*[这里的第三种方法涉及使用@Autowired](https://www.baeldung.com/spring-autowire)*注释将依赖项直接注入到类中。尽管这可能是最简单的方法，但我们必须了解它可能会导致潜在的问题。

更重要的是，即使是[Spring 官方文档也](https://docs.spring.io/spring-framework/reference/core/beans/dependencies/factory-collaborators.html)不再提供字段注入作为 DI 选项之一。

## 3.零安全

**如果依赖项未正确初始化，字段注入会产生\*NullPointerException\*的风险。**

让我们定义*EmailService*类并使用字段注入添加*EmailValidator依赖项：*

```java
@Service
public class EmailService {

    @Autowired
    private EmailValidator emailValidator;
}复制
```

现在，让我们添加*process()*方法：

```java
public void process(String email) {
    if(!emailValidator.isValid(email)){
        throw new IllegalArgumentException(INVALID_EMAIL);
    }
    // ...
}复制
```

仅当我们提供*EmailValidator依赖项时**，EmailService*才能正常工作。**然而，使用字段注入，我们没有提供一种直接的方法来实例化具有所需依赖项的*****EmailService\*****。**

此外，我们可以使用默认构造函数创建*EmailService实例：*

```java
EmailService emailService = new EmailService();
emailService.process("test@baeldung.com");复制
```

执行上面的代码会导致*NullPointerException*，因为我们没有提供其强制依赖项*EmailValidator*。

现在，我们可以**使用构造函数注入来降低*****NullPointerException\*****的风险**：

```java
private final EmailValidator emailValidator;

public EmailService(final EmailValidator emailValidator) {
   this.emailValidator = emailValidator;
}复制
```

通过这种方法，我们公开了所需的依赖项。此外，我们现在要求客户提供强制性依赖项。换句话说，如果不提供*EmailValidator实例，就无法创建**EmailService*的新实例。

## 4. 不变性

**使用字段注入，我们无法创建不可变的类。**

我们需要在声明或通过构造函数时实例化最终字段。**此外，一旦构造函数被调用，Spring 就会执行自动装配。**因此，不可能使用字段注入自动连接最终字段。

由于依赖项是可变的，因此我们无法确保它们在初始化后保持不变。此外，在运行应用程序时，重新分配非最终字段可能会导致意外的副作用。

或者，我们可以对强制依赖项使用构造函数注入，对可选依赖项使用 setter 注入。这样，我们就可以确保所需的依赖关系保持不变。

## 5. 设计问题

现在，让我们讨论一下场注入时可能出现的一些设计问题。

### 5.1. 单一责任违规

作为[SOLID 原则](https://www.baeldung.com/solid-principles)的一部分，[单一职责](https://www.baeldung.com/java-single-responsibility-principle)原则规定每个类应该只有一个职责。换句话说，一个类应该只负责一项操作，因此只有一个改变的理由。

当我们使用字段注入时，我们最终可能会违反单一职责原则。**我们可以轻松地添加不必要的依赖项，并创建一个执行多项工作的类。**

另一方面，如果我们使用构造函数注入，我们会注意到如果构造函数具有多个依赖项，我们可能会遇到设计问题。此外，如果构造函数中的参数超过七个，即使是 IDE 也会发出警告。

### 5.2. 循环依赖

简而言之，当两个或多个类相互依赖时，就会发生[循环依赖。](https://www.baeldung.com/circular-dependencies-in-spring)由于这些依赖关系，无法构造对象，并且执行可能会出现运行时错误或无限循环。

使用字段注入可能会导致循环依赖被忽视：

```java
@Component
public class DependencyA {

   @Autowired
   private DependencyB dependencyB;
}

@Component
public class DependencyB {

   @Autowired
   private DependencyA dependencyA;
}复制
```

**由于依赖项是在需要时注入的，而不是在上下文加载时注入的，因此 Spring 不会抛出\*BeanCurrentlyInCreationException\*。**

通过构造函数注入，可以在编译时检测循环依赖关系，因为它们会产生无法解决的错误。

此外，如果我们的代码中有循环依赖，这可能表明我们的设计有问题。因此，如果可能的话，我们应该考虑重新设计我们的应用程序。

**然而，从 Spring Boot 2.6 开始。[默认情况下不再允许](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.6-Release-Notes#circular-references-prohibited-by-default)版本循环依赖。**

## 6. 测试

单元测试揭示了现场注入方法的主要缺点之一。

假设我们要编写一个单元测试来检查*EmailService*中定义的*process()*方法是否正常工作。

首先，我们要模拟*EmailValidation*对象。但是，由于我们使用字段注入插入了*EmailValidator*，因此我们无法直接将其替换为模拟版本：

```java
EmailValidator validator = Mockito.mock(EmailValidator.class);
EmailService emailService = new EmailService();复制
```

*此外，在EmailService*类中提供 setter 方法会引入额外的漏洞，因为其他类（而不仅仅是测试类）可以调用该方法。

**然而，我们可以通过反射来实例化我们的类。** 例如，我们可以使用 Mockito：

```java
@Mock
private EmailValidator emailValidator;

@InjectMocks
private EmailService emailService;

@BeforeEach
public void setup() {
   MockitoAnnotations.openMocks(this);
}复制
```

在这里，Mockito 将尝试使用*@InjectMocks*注释注入模拟。**但是，如果字段注入策略失败，Mockito 将不会报告失败。**

另一方面，使用构造函数注入，我们可以提供所需的依赖项而无需反射：

```java
private EmailValidator emailValidator;

private EmailService emailService;

@BeforeEach
public void setup() {
   this.emailValidator = Mockito.mock(EmailValidator.class);
   this.emailService = new EmailService(emailValidator);
}复制
```

## 七、结论

在本文中，我们了解了不推荐现场注入的原因。

总而言之，我们可以使用构造函数注入来实现必需的依赖项，并使用 setter 注入来实现可选依赖项，而不是字段注入。