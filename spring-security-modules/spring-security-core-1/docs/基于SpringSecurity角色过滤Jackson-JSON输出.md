## 1. 概述

在本快速教程中，我们将展示如何根据Spring Security中定义的用户角色过滤JSON序列化输出。

## 2. 为什么我们需要过滤？

让我们考虑一个简单但常见的用例，其中我们有一个为具有不同角色的用户提供服务的Web应用程序。例如，假设这些角色为User和Admin。

首先，让我们定义一个需求，即**Admin可以完全访问通过公共REST API公开的对象属性**。相反，**User应该只能看到一组预定义的对象属性**。

我们将使用[Spring Security框架](https://www.baeldung.com/security-spring)来防止对Web应用程序资源的未经授权的访问。

让我们定义一个对象，我们将在API中将其作为REST响应有效负载返回：

```java
class Item {
    private int id;
    private String name;
    private String ownerName;
    // getters ...
}
```

当然，我们可以为应用程序中的每个角色定义一个单独的数据传输对象类(DTO)。但是，这种方法会在我们的代码库中引入无用的重复或复杂的类层次结构。

另一方面，我们可以使用Jackson库的[JSON视图功能](https://www.baeldung.com/jackson-json-view-annotation)。正如我们将在下一节中看到的，**它使自定义JSON响应就像在字段上添加注解一样简单**。

## 3. @JsonView注解

Jackson库支持通过使用@JsonView注解标记我们想要包含在JSON响应中的字段来定义**多个序列化/反序列化上下文**。此注解具有**Class类型的必需参数**，用于区分上下文。

当使用@JsonView标记我们类中的字段时，我们应该记住，默认情况下，序列化上下文包括所有未明确标记为视图一部分的属性。为了覆盖此行为，我们可以禁用DEFAULT_VIEW_INCLUSION映射器功能。

首先，让我们**定义一个包含一些内部类的View类，我们将使用这些内部类作为@JsonView注解的参数**：

```java
public class View {
    public static class User { }

    public static class Admin extends User { }
}
```

接下来，我们将@JsonView注解添加到我们的Item类中，使ownerName只能由Admin角色访问：

```java
public class Item {
    @JsonView(View.User.class)
    private int id;
    @JsonView(View.User.class)
    private String name;
    @JsonView(View.Admin.class)
    private String ownerName;
}
```

## 4. 如何将@JsonView注解与Spring Security集成

现在，让我们添加一个包含所有角色及其名称的枚举。然后，我们创建一个JSON视图和角色之间映射的Map：

```java
public enum Role {
    ROLE_USER,
    ROLE_ADMIN
}

public class View {
    public static final Map<Role, Class<?>> MAPPING = new HashMap<>();

    static {
        MAPPING.put(Role.ROLE_ADMIN, Admin.class);
        MAPPING.put(Role.ROLE_USER, User.class);
    }
}
```

**为了绑定JSON视图和Spring Security角色，我们需要定义适用于我们应用程序中所有控制器方法的ControllerAdvice**。

到目前为止，我们唯一需要做的就是**覆盖AbstractMappingJacksonResponseBodyAdvice类的beforeBodyWriteInternal方法**：

```java
@RestControllerAdvice
public class SecurityJsonViewControllerAdvice extends AbstractMappingJacksonResponseBodyAdvice {

    @Override
    protected void beforeBodyWriteInternal(MappingJacksonValue bodyContainer, MediaType contentType, MethodParameter returnType, ServerHttpRequest request, ServerHttpResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
              && authentication.getAuthorities() != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            List<? extends Class<?>> jsonViews = authorities.stream()
                  .map(GrantedAuthority::getAuthority)
                  .map(AppConfig.Role::valueOf)
                  .map(View.MAPPING::get)
                  .collect(Collectors.toList());
            if (jsonViews.size() == 1) {
                bodyContainer.setSerializationView(jsonViews.get(0));
                return;
            }
            throw new IllegalArgumentException("Ambiguous @JsonView declaration for roles " +
                  authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")));
        }
    }
}
```

这样，**我们应用程序的每个响应都会通过这个ControllerAdvice**，并且它将根据我们定义的角色映射找到合适的视图表示。请注意，这种方法需要我们在**处理具有多个角色的用户时要小心**。

## 5. 总结

在这个简短的教程中，我们学习了如何在基于Spring Security角色的Web应用程序中过滤JSON输出。