## 1. 概述

在这篇简短的文章中，我们将做一些新的事情。我们将改进现有的RESTSpringAPI 并使其使用命令查询责任分离 – [CQRS](http://squirrel.pl/blog/2015/08/31/introduction-to-event-sourcing-and-command-query-responsibility-segregation/)。

目标是明确分离服务层和控制器层，以分别处理读取 - 查询和写入 - 命令进入系统。

请记住，这只是迈向这种架构的早期第一步，而不是“到达点”。话虽这么说——我对这个很兴奋。

最后 - 我们将要使用的示例API是发布用户资源，并且是我们正在进行的[Reddit 应用案例研究的](https://www.baeldung.com/case-study-a-reddit-app-with-spring)一部分，以举例说明其工作原理 - 当然，任何API都可以。

## 2.服务层

我们将从简单开始——通过识别我们之前的用户服务中的读取和写入操作——我们将把它分成 2 个独立的服务——UserQueryService和UserCommandService：

```java
public interface IUserQueryService {

    List<User> getUsersList(int page, int size, String sortDir, String sort);

    String checkPasswordResetToken(long userId, String token);

    String checkConfirmRegistrationToken(String token);

    long countAllUsers();

}
public interface IUserCommandService {

    void registerNewUser(String username, String email, String password, String appUrl);

    void updateUserPassword(User user, String password, String oldPassword);

    void changeUserPassword(User user, String password);

    void resetPassword(String email, String appUrl);

    void createVerificationTokenForUser(User user, String token);

    void updateUser(User user);

}
```

通过阅读此 API，你可以清楚地看到查询服务是如何进行所有读取操作的，而命令服务并未读取任何数据——全部返回 void。

## 3.控制器层

接下来 - 控制器层。

### 3.1. 查询控制器

这是我们的UserQueryRestController：

```java
@Controller
@RequestMapping(value = "/api/users")
public class UserQueryRestController {

    @Autowired
    private IUserQueryService userService;

    @Autowired
    private IScheduledPostQueryService scheduledPostService;

    @Autowired
    private ModelMapper modelMapper;

    @PreAuthorize("hasRole('USER_READ_PRIVILEGE')")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<UserQueryDto> getUsersList(...) {
        PagingInfo pagingInfo = new PagingInfo(page, size, userService.countAllUsers());
        response.addHeader("PAGING_INFO", pagingInfo.toString());
        
        List<User> users = userService.getUsersList(page, size, sortDir, sort);
        return users.stream().map(
          user -> convertUserEntityToDto(user)).collect(Collectors.toList());
    }

    private UserQueryDto convertUserEntityToDto(User user) {
        UserQueryDto dto = modelMapper.map(user, UserQueryDto.class);
        dto.setScheduledPostsCount(scheduledPostService.countScheduledPostsByUser(user));
        return dto;
    }
}
```

这里有趣的是查询控制器只注入查询服务。

更有趣的是切断这个控制器对命令服务的访问——通过将它们放在一个单独的模块中。

### 3.2. 命令控制器

现在，这是我们的命令控制器实现：

```java
@Controller
@RequestMapping(value = "/api/users")
public class UserCommandRestController {

    @Autowired
    private IUserCommandService userService;

    @Autowired
    private ModelMapper modelMapper;

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void register(
      HttpServletRequest request, @RequestBody UserRegisterCommandDto userDto) {
        String appUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        
        userService.registerNewUser(
          userDto.getUsername(), userDto.getEmail(), userDto.getPassword(), appUrl);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/password", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void updateUserPassword(@RequestBody UserUpdatePasswordCommandDto userDto) {
        userService.updateUserPassword(
          getCurrentUser(), userDto.getPassword(), userDto.getOldPassword());
    }

    @RequestMapping(value = "/passwordReset", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void createAResetPassword(
      HttpServletRequest request, 
      @RequestBody UserTriggerResetPasswordCommandDto userDto) 
    {
        String appUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        userService.resetPassword(userDto.getEmail(), appUrl);
    }

    @RequestMapping(value = "/password", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void changeUserPassword(@RequestBody UserchangePasswordCommandDto userDto) {
        userService.changeUserPassword(getCurrentUser(), userDto.getPassword());
    }

    @PreAuthorize("hasRole('USER_WRITE_PRIVILEGE')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@RequestBody UserUpdateCommandDto userDto) {
        userService.updateUser(convertToEntity(userDto));
    }

    private User convertToEntity(UserUpdateCommandDto userDto) {
        return modelMapper.map(userDto, User.class);
    }
}
```

这里发生了一些有趣的事情。首先——注意这些API实现中的每一个是如何使用不同命令的。这主要是为了给我们提供一个良好的基础，以便进一步改进API的设计并在出现不同资源时将其提取出来。

另一个原因是，当我们朝着事件溯源迈出下一步时——我们有一组干净的命令可以使用。

### 3.3. 单独的资源表示

在分成命令和查询之后，现在让我们快速浏览一下用户资源的不同表示形式：

```java
public class UserQueryDto {
    private Long id;

    private String username;

    private boolean enabled;

    private Set<Role> roles;

    private long scheduledPostsCount;
}
```

这是我们的命令 DTO：

-   UserRegisterCommandDto用来表示用户注册数据：

```java
public class UserRegisterCommandDto {
    private String username;
    private String email;
    private String password;
}
```

-   UserUpdatePasswordCommandDto用来表示更新当前用户密码的数据：

```java
public class UserUpdatePasswordCommandDto {
    private String oldPassword;
    private String password;
}
```

-   UserTriggerResetPasswordCommandDto用于表示用户的电子邮件以通过发送带有重置密码令牌的电子邮件来触发重置密码：

```java
public class UserTriggerResetPasswordCommandDto {
    private String email;
}
```

-   UserChangePasswordCommandDto用于表示新用户密码——此命令在用户使用密码重置令牌后调用。

```java
public class UserChangePasswordCommandDto {
    private String password;
}
```

-   UserUpdateCommandDto用于表示修改后的新用户数据：

```java
public class UserUpdateCommandDto {
    private Long id;

    private boolean enabled;

    private Set<Role> roles;
}
```

## 4。总结

在本教程中，我们为 SpringREST API的干净CQRS实现奠定了基础。

下一步将是通过将一些单独的职责(和资源)确定到他们自己的服务中来不断改进 API，以便我们更紧密地与以资源为中心的架构保持一致。