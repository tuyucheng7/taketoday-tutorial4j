## 1. 概述

当我们开发长期系统时，我们应该期待一个可变的环境。

一般来说，我们的功能需求、框架、I/O设备，甚至我们的代码设计，都可能因为各种原因发生变化。考虑到这一点，考虑到我们周围的所有不确定性，Clean Architecture 是高可维护代码的指南。

在本文中，我们将按照[Robert C. Martin 的 Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)创建一个用户注册 API 示例。我们将使用他的原始层——实体、用例、接口适配器和框架/驱动程序。

## 2. 清洁架构概述

干净的架构编译了许多代码设计和原则，如 [SOLID](https://www.baeldung.com/solid-principles)、[稳定的抽象](https://wiki.c2.com/?StableAbstractionsPrinciple)等。但是，核心思想是 根据业务价值将系统划分为多个层次。因此，最高层有业务规则，越低层越靠近 I/O 设备。

此外，我们可以将级别转换为图层。在这种情况下，情况恰恰相反。内层等于最高层，以此类推：

[![用户清理架构层 1](https://www.baeldung.com/wp-content/uploads/2021/01/user-clean-architecture-layers-1.png)](https://www.baeldung.com/wp-content/uploads/2021/01/user-clean-architecture-layers-1.png)

考虑到这一点，我们可以根据业务需要设置尽可能多的级别。但是，始终考虑 依赖规则——较高级别绝不能依赖较低级别。

## 3. 规则

让我们开始为我们的用户注册 API 定义系统规则。一、业务规则：

-   用户密码必须超过五个字符

第二，我们有申请规则。它们可以采用不同的格式，如用例或故事。我们将使用讲故事的短语：

-   系统收到用户名和密码，如果用户不存在则进行验证，并保存新用户和创建时间

请注意没有提及任何数据库、UI 或类似内容。因为 我们的业务不关心这些细节，所以我们的代码也不应该。

## 4. 实体层

正如干净的架构所建议的那样，让我们从我们的业务规则开始：

```java
interface User {
    boolean passwordIsValid();

    String getName();

    String getPassword();
}
```

并且，一个UserFactory：

```java
interface UserFactory {
    User create(String name, String password);
}
```

由于两个原因，我们创建了一个用户 [工厂方法。](https://www.baeldung.com/creational-design-patterns#factory-method)遵循[稳定的抽象原则](https://wiki.c2.com/?StableAbstractionsPrinciple)并隔离用户创建。

接下来，让我们同时实现：

```java
class CommonUser implements User {

    String name;
    String password;

    @Override
    public boolean passwordIsValid() {
        return password != null && password.length() > 5;
    }

    // Constructor and getters
}
class CommonUserFactory implements UserFactory {
    @Override
    public User create(String name, String password) {
        return new CommonUser(name, password);
    }
}
```

如果我们有一个复杂的业务，那么我们应该尽可能清晰地构建我们的领域代码。所以，这一层是应用[设计模式](https://www.baeldung.com/design-patterns-series)的好地方。特别是，应该考虑[领域驱动设计。](https://www.baeldung.com/java-modules-ddd-bounded-contexts)

### 4.1. 单元测试

现在，让我们测试我们的CommonUser：

```java
@Test
void given123Password_whenPasswordIsNotValid_thenIsFalse() {
    User user = new CommonUser("Baeldung", "123");

    assertThat(user.passwordIsValid()).isFalse();
}
```

正如我们所见，单元测试非常清晰。毕竟，没有 mocks 是这一层的一个好信号。

一般来说，如果我们在这里开始考虑模拟，也许我们正在将我们的实体与我们的用例混合在一起。

## 5.用例层

用例是 与我们系统自动化相关的规则。在 Clean Architecture 中，我们称它们为交互器。

### 5.1. 用户注册交互器

首先，我们将构建我们的UserRegisterInteractor以便我们可以看到我们要去哪里。然后，我们将创建并讨论所有使用的部分：

```java
class UserRegisterInteractor implements UserInputBoundary {

    final UserRegisterDsGateway userDsGateway;
    final UserPresenter userPresenter;
    final UserFactory userFactory;

    // Constructor

    @Override
    public UserResponseModel create(UserRequestModel requestModel) {
        if (userDsGateway.existsByName(requestModel.getName())) {
            return userPresenter.prepareFailView("User already exists.");
        }
        User user = userFactory.create(requestModel.getName(), requestModel.getPassword());
        if (!user.passwordIsValid()) {
            return userPresenter.prepareFailView("User password must have more than 5 characters.");
        }
        LocalDateTime now = LocalDateTime.now();
        UserDsRequestModel userDsModel = new UserDsRequestModel(user.getName(), user.getPassword(), now);

        userDsGateway.save(userDsModel);

        UserResponseModel accountResponseModel = new UserResponseModel(user.getName(), now.toString());
        return userPresenter.prepareSuccessView(accountResponseModel);
    }
}
```

如我们所见，我们正在执行所有用例步骤。此外，该层还负责控制实体的舞蹈。不过，我们并未对 UI 或数据库的工作方式做出任何假设。但是，我们正在使用UserDsGateway和UserPresenter。那么，我们怎么可能不认识他们呢？因为，与UserInputBoundary一起，这些是我们的输入和输出边界。

### 5.2. 输入和输出边界

边界是定义组件如何交互的契约。输入边界将我们的 用例暴露给外层：

```java
interface UserInputBoundary {
    UserResponseModel create(UserRequestModel requestModel);
}
```

接下来，我们有使用外层的输出边界。首先，让我们定义数据源网关：

```java
interface UserRegisterDsGateway {
    boolean existsByName(String name);

    void save(UserDsRequestModel requestModel);
}
```

二、view presenter：

```java
interface UserPresenter {
    UserResponseModel prepareSuccessView(UserResponseModel user);

    UserResponseModel prepareFailView(String error);
}

```

请注意，我们正在使用 [依赖倒置原则](https://www.baeldung.com/java-dependency-inversion-principle)使我们的业务摆脱数据库和 UI 等细节。

### 5.3. 解耦模式

在继续之前，请注意 边界是如何定义系统的自然划分的。但我们还必须决定我们的应用程序将如何交付：

-   整体式——可能使用某种包结构进行组织
-   通过使用模块
-   通过使用服务/微服务

考虑到这一点，我们可以 使用任何解耦模式来实现干净的架构目标。因此，我们应该准备根据我们当前和未来的业务需求在这些策略之间进行更改。选择我们的解耦模式后，代码划分应该根据我们的边界进行。

### 5.4. 请求和响应模型

到目前为止，我们已经使用接口创建了跨层的操作。接下来，让我们看看如何跨这些边界传输数据。

请注意我们所有的边界是如何只处理String或Model对象的：

```java
class UserRequestModel {

    String login;
    String password;

    // Getters, setters, and constructors
}
```

基本上只有简单的数据结构才能跨越边界。此外，所有模型都只有 fields 和 accessors 。另外，数据对象属于内侧。所以，我们可以保留依赖规则。

但是为什么我们有这么多相似的对象呢？当我们得到重复的代码时，它可以有两种类型：

-   错误或意外重复——代码相似性是意外，因为每个对象都有不同的更改原因。如果我们试图删除它，我们将面临违反 [单一责任原则](https://www.baeldung.com/java-single-responsibility-principle)的风险。
-   真正的重复——代码因同样的原因而改变。因此，我们应该删除它

由于每个模型都有不同的职责，我们得到了所有这些对象。

### 5.5. 测试UserRegisterInteractor

现在，让我们创建单元测试：

```java
@Test
void givenBaeldungUserAnd12345Password_whenCreate_thenSaveItAndPrepareSuccessView() {
    given(userDsGateway.existsByIdentifier("identifier"))
        .willReturn(true);

    interactor.create(new UserRequestModel("baeldung", "123"));

    then(userDsGateway).should()
        .save(new UserDsRequestModel("baeldung", "12345", now()));
    then(userPresenter).should()
        .prepareSuccessView(new UserResponseModel("baeldung", now()));
}
```

正如我们所见，大多数用例测试都是关于控制实体和边界请求。而且，我们的接口允许我们轻松模拟细节。

## 6.接口适配器

至此，我们完成了所有业务。现在，让我们开始插入我们的细节。

我们的业务应该只处理最方便的数据格式，我们的外部代理也应该如此，如 DB 或 UI。但是，这种格式通常是不同的。为此，接口适配层负责数据的转换。

### 6.1. 使用 JPA的 UserRegisterDsGateway

首先，让我们使用[JPA](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)来映射我们的用户表：

```java
@Entity
@Table(name = "user")
class UserDataMapper {

    @Id
    String name;

    String password;

    LocalDateTime creationTime;

    //Getters, setters, and constructors
}
```

正如我们所见，Mapper的目标是将我们的对象映射到数据库格式。

接下来，JpaRepository使用我们的[实体](https://www.baeldung.com/jpa-entities)：

```java
@Repository
interface JpaUserRepository extends JpaRepository<UserDataMapper, String> {
}
```

假设我们将使用 spring-boot，那么这就是保存用户所需的全部。

现在，是时候实施我们的UserRegisterDsGateway 了：

```java
class JpaUser implements UserRegisterDsGateway {

    final JpaUserRepository repository;

    // Constructor

    @Override
    public boolean existsByName(String name) {
        return repository.existsById(name);
    }

    @Override
    public void save(UserDsRequestModel requestModel) {
        UserDataMapper accountDataMapper = new UserDataMapper(requestModel.getName(), requestModel.getPassword(), requestModel.getCreationTime());
        repository.save(accountDataMapper);
    }
}
```

在大多数情况下，代码不言自明。除了我们的方法，请注意UserRegisterDsGateway 的名称。如果我们改为选择UserDsGateway，那么其他用户用例将很容易违反[接口隔离原则](https://www.baeldung.com/java-interface-segregation)。

### 6.2. 用户注册接口

现在，让我们创建我们的 HTTP 适配器：

```java
@RestController
class UserRegisterController {

    final UserInputBoundary userInput;

    // Constructor

    @PostMapping("/user")
    UserResponseModel create(@RequestBody UserRequestModel requestModel) {
        return userInput.create(requestModel);
    }
}
```

正如我们所见，这里的唯一目标是接收请求并将响应发送给客户端。

### 6.3. 准备回应

在回复之前，我们应该格式化我们的回复：

```java
class UserResponseFormatter implements UserPresenter {

    @Override
    public UserResponseModel prepareSuccessView(UserResponseModel response) {
        LocalDateTime responseTime = LocalDateTime.parse(response.getCreationTime());
        response.setCreationTime(responseTime.format(DateTimeFormatter.ofPattern("hh:mm:ss")));
        return response;
    }

    @Override
    public UserResponseModel prepareFailView(String error) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, error);
    }
}
```

我们的 UserRegisterInteractor 迫使我们创建一个演示者。不过，表示规则仅涉及适配器内部。另外，凡是难测的东西，我们应该把它分为可测的和[不起眼](https://martinfowler.com/bliki/HumbleObject.html)的。 因此， UserResponseFormatter很容易让我们验证我们的表示规则：

```java
@Test
void givenDateAnd3HourTime_whenPrepareSuccessView_thenReturnOnly3HourTime() {
    UserResponseModel modelResponse = new UserResponseModel("baeldung", "2020-12-20T03:00:00.000");
    UserResponseModel formattedResponse = userResponseFormatter.prepareSuccessView(modelResponse);

    assertThat(formattedResponse.getCreationTime()).isEqualTo("03:00:00");
}
```

如我们所见，我们在将其发送到视图之前测试了所有逻辑。因此，只有不起眼的对象才处于较难测试的部分。

## 7. 驱动程序和框架

事实上，我们通常不在这里编码。那是因为这一层代表了与外部代理连接的最低级别。例如，连接数据库或网络框架的 H2 驱动程序。在这种情况下，我们将使用[spring-boot](https://www.baeldung.com/spring-boot)作为[web](https://www.baeldung.com/bootstraping-a-web-application-with-spring-and-java-based-configuration)和[依赖注入](https://www.baeldung.com/spring-dependency-injection)框架。所以，我们需要它的启动点：

```java
@SpringBootApplication
public class CleanArchitectureApplication {
    public static void main(String[] args) {
      SpringApplication.run(CleanArchitectureApplication.class);
    }
}
```

直到现在，我们还没有 在我们的业务中使用任何[spring 注解。](https://www.baeldung.com/spring-bean-annotations)除了 spring-specific 适配器，作为我们的UserRegisterController。这是因为 我们应该 将 spring-boot 视为任何其他细节。

## 8. 可怕的主类

最后，最后的作品！

到目前为止，我们遵循了[稳定抽象原则](https://wiki.c2.com/?StableAbstractionsPrinciple)。[此外，我们通过控制反转](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)保护我们的内层免受外部代理的影响。最后，我们将所有对象的创建与其使用分开。在这一点上，由我们来创建剩余的依赖项并将它们注入到我们的项目中：

```java
@Bean
BeanFactoryPostProcessor beanFactoryPostProcessor(ApplicationContext beanRegistry) {
    return beanFactory -> {
        genericApplicationContext(
          (BeanDefinitionRegistry) ((AnnotationConfigServletWebServerApplicationContext) beanRegistry)
            .getBeanFactory());
    };
}

void genericApplicationContext(BeanDefinitionRegistry beanRegistry) {
    ClassPathBeanDefinitionScanner beanDefinitionScanner = new ClassPathBeanDefinitionScanner(beanRegistry);
    beanDefinitionScanner.addIncludeFilter(removeModelAndEntitiesFilter());
    beanDefinitionScanner.scan("com.baeldung.pattern.cleanarchitecture");
}

static TypeFilter removeModelAndEntitiesFilter() {
    return (MetadataReader mr, MetadataReaderFactory mrf) -> !mr.getClassMetadata()
      .getClassName()
      .endsWith("Model");
}
```

在我们的例子中，我们使用 spring-boot [依赖注入](https://www.baeldung.com/spring-dependency-injection) 来创建我们所有的实例。由于我们没有使用 [@Component](https://www.baeldung.com/spring-bean-annotations#component)，我们正在扫描我们的根包并仅忽略Model对象。

虽然这个策略看起来比较复杂，但是它把我们的业务和 DI 框架解耦了。另一方面，主类获得了我们所有系统的权力。这就是为什么 clean architecture 将它视为包含所有其他层的特殊层：

[![用户清理架构层](https://www.baeldung.com/wp-content/uploads/2021/01/user-clean-architecture-layers.png)](https://www.baeldung.com/wp-content/uploads/2021/01/user-clean-architecture-layers.png)

## 9.总结

在本文中，我们了解到 Bob 大叔的 简洁架构是如何构建在许多设计模式和原则之上的。此外，我们创建了一个使用 Spring Boot 应用它的用例。

尽管如此，我们仍保留了一些原则。但是，所有这些都指向同一个方向。我们可以通过引用它的创建者的话来总结它：“一个好的架构师 必须最大化未做出的决定的数量。”，我们通过 使用边界保护我们的业务代码免受细节影响来做到这一点。