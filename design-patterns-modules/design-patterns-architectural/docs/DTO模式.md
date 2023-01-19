## 一、概述

在本教程中，我们将讨论 [DTO 模式](https://martinfowler.com/eaaCatalog/dataTransferObject.html)、它是什么以及如何以及何时使用它。到最后，我们将知道如何正确使用它。

## 2. 模式

DTO 或数据传输对象是在进程之间传输数据以减少方法调用次数的对象。该模式首先由 Martin Fowler 在他的[EAA](https://martinfowler.com/books/eaa.html)一书中介绍。

Fowler 解释说，该模式的主要目的是通过在单个调用中批量处理多个参数来减少到服务器的往返次数。这减少了此类远程操作中的网络开销。

另一个好处是序列化逻辑的封装(将对象结构和数据转换为可以存储和传输的特定格式的机制)。它提供了序列化细微差别的单一变化点。它还将域模型与表示层分离，允许两者独立更改。

## 3. 如何使用？

DTO 通常创建为[POJO](https://www.baeldung.com/java-pojo-class)。它们是不包含业务逻辑的平面数据结构。它们只包含存储、访问器和最终与序列化或解析相关的方法。

数据从[领域模型](https://martinfowler.com/eaaCatalog/domainModel.html)映射到 DTO，通常是通过表示层或外观层中的映射器组件。

下图说明了组件之间的交互：[![第 4 层](https://www.baeldung.com/wp-content/uploads/2021/08/layers-4.svg)](https://www.baeldung.com/wp-content/uploads/2021/08/layers-4.svg)

## 4.什么时候用？

DTO 在具有远程调用的系统中派上用场，因为它们有助于减少远程调用的数量。

当域模型由许多不同的对象组成并且表示模型一次需要它们的所有数据时，DTO 也有帮助，或者它们甚至可以减少客户端和服务器之间的往返。

使用 DTO，我们可以从我们的域模型构建不同的视图，允许我们创建同一域的其他表示，但在不影响我们的域设计的情况下根据客户的需求优化它们。这种灵活性是解决复杂问题的有力工具。

## 5.用例

为了演示该模式的实现，我们将使用一个具有两个主要领域模型的简单应用程序，在本例中为User和Role。为了专注于该模式，让我们看一下两个功能示例 — 用户检索和新用户的创建。

### 5.1. DTO 与域

下面是两个模型的定义：

```java
public class User {

    private String id;
    private String name;
    private String password;
    private List<Role> roles;

    public User(String name, String password, List<Role> roles) {
        this.name = Objects.requireNonNull(name);
        this.password = this.encrypt(password);
        this.roles = Objects.requireNonNull(roles);
    }

    // Getters and Setters

   String encrypt(String password) {
       // encryption logic
   }
}
public class Role {

    private String id;
    private String name;

    // Constructors, getters and setters
}
```

现在让我们看一下 DTO，以便我们可以将它们与域模型进行比较。

此时，重要的是要注意 DTO 表示从 API 客户端发送或发送到 API 客户端的模型。

因此，细微差别要么是将发送到服务器的请求打包在一起，要么是优化客户端的响应：

```java
public class UserDTO {
    private String name;
    private List<String> roles;
    
    // standard getters and setters
}
```

上面的 DTO 仅向客户端提供相关信息，隐藏密码，例如出于安全原因。

下一个 DTO 将创建用户所需的所有数据分组并在单个请求中将其发送到服务器，这优化了与 API 的交互：

```java
public class UserCreationDTO {

    private String name;
    private String password;
    private List<String> roles;

    // standard getters and setters
}
```

### 5.2. 连接双方

接下来，连接两个类的层使用映射器组件将数据从一侧传递到另一侧，反之亦然。

这通常发生在表示层：

```java
@RestController
@RequestMapping("/users")
class UserController {

    private UserService userService;
    private RoleService roleService;
    private Mapper mapper;

    // Constructor

    @GetMapping
    @ResponseBody
    public List<UserDTO> getUsers() {
        return userService.getAll()
          .stream()
          .map(mapper::toDto)
          .collect(toList());
    }


    @PostMapping
    @ResponseBody
    public UserIdDTO create(@RequestBody UserCreationDTO userDTO) {
        User user = mapper.toUser(userDTO);

        userDTO.getRoles()
          .stream()
          .map(role -> roleService.getOrCreate(role))
          .forEach(user::addRole);

        userService.save(user);

        return new UserIdDTO(user.getId());
    }

}

```

最后，我们有传输数据的Mapper组件，确保 DTO 和域模型不需要相互了解：

```java
@Component
class Mapper {
    public UserDTO toDto(User user) {
        String name = user.getName();
        List<String> roles = user
          .getRoles()
          .stream()
          .map(Role::getName)
          .collect(toList());

        return new UserDTO(name, roles);
    }

    public User toUser(UserCreationDTO userDTO) {
        return new User(userDTO.getName(), userDTO.getPassword(), new ArrayList<>());
    }
}
```

## 6. 常见错误

尽管 DTO 模式是一种简单的设计模式，但我们在实现该技术的应用程序中可能会犯一些错误。

第一个错误是为每个场合创建不同的 DTO。这将增加我们需要维护的类和映射器的数量。尽量使它们简洁明了，并评估添加一个或重用现有一个的权衡。

我们还希望避免尝试在多个场景中使用单个类。这种做法可能会导致许多属性经常不被使用的大合同。

另一个常见的错误是向这些类添加业务逻辑，这是不应该发生的。该模式的目的是优化数据传输和合同结构。因此，所有的业务逻辑都应该存在于领域层。

最后，我们有所谓的[LocalDTO](https://martinfowler.com/bliki/LocalDTO.html)，其中 DTO 跨域传递数据。问题再次是所有映射的维护成本。

支持这种方法的最常见论点之一是领域模型的封装。但这里的问题是将我们的领域模型与持久性模型结合起来。通过解耦它们，暴露域模型的风险几乎消失了。

其他模式也有类似的结果，但它们通常用于更复杂的场景，例如CQRS 、[Data ](https://martinfowler.com/eaaCatalog/dataMapper.html)[Mappers](https://cqrs.files.wordpress.com/2010/11/cqrs_documents.pdf)、[CommandQuerySeparation](https://martinfowler.com/bliki/CommandQuerySeparation.html)等。

## 七、总结

在本文中，我们了解了[DTO 模式](https://martinfowler.com/eaaCatalog/dataTransferObject.html)的定义、它存在的原因以及如何实现它。

我们还看到了与其实施相关的一些常见错误以及避免这些错误的方法。