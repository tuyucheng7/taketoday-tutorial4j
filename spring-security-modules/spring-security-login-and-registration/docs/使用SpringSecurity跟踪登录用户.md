## 1. 概述

在本快速教程中，我们将展示一个示例，说明如何**使用Spring Security跟踪应用程序中当前登录的用户**。

为此，我们将通过在用户登录时添加用户并在他们注销时删除用户来跟踪已登录用户的列表。

我们将利用HttpSessionBindingListener来更新登录用户的列表，只要根据用户登录系统或从系统注销将用户信息添加到会话或从会话中删除。

## 2. 活跃用户存储

为简单起见，我们将定义一个类作为登录用户的内存存储：

```java
public class ActiveUserStore {

    public List<String> users;

    public ActiveUserStore() {
        users = new ArrayList<String>();
    }

    // standard getter and setter
}
```

我们将在Spring上下文中将其定义为标准bean：

```java
@Bean
public ActiveUserStore activeUserStore(){
    return new ActiveUserStore();
}
```

## 3. HTTPSessionBindingListener

现在，我们将使用HTTPSessionBindingListener接口并创建一个包装类来表示当前登录的用户。

这基本上会监听HttpSessionBindingEvent类型的事件，每当设置或删除值时(或者换句话说，绑定或解除绑定到HTTP会话时)，就会触发这些事件：

```java
@Component
public class LoggedUser implements HttpSessionBindingListener, Serializable {

    private static final long serialVersionUID = 1L;
    private String username;
    private ActiveUserStore activeUserStore;

    public LoggedUser(String username, ActiveUserStore activeUserStore) {
        this.username = username;
        this.activeUserStore = activeUserStore;
    }

    public LoggedUser() {}

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        List<String> users = activeUserStore.getUsers();
        LoggedUser user = (LoggedUser) event.getValue();
        if (!users.contains(user.getUsername())) {
            users.add(user.getUsername());
        }
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        List<String> users = activeUserStore.getUsers();
        LoggedUser user = (LoggedUser) event.getValue();
        if (users.contains(user.getUsername())) {
            users.remove(user.getUsername());
        }
    }

    // standard getter and setter
}
```

监听器有两个需要实现的方法，valueBound()和valueUnbound()用于触发它正在监听的事件的两种类型的操作。每当从会话中设置或删除实现监听器的类型的值，或者会话无效时，都将调用这两个方法。

在我们的例子中，当用户登录时将调用valueBound()方法，当用户注销或会话过期时将调用valueUnbound()方法。

在每个方法中，我们检索与事件关联的值，然后从我们的登录用户列表中添加或删除用户名，具体取决于该值是绑定还是取消绑定到会话中。

## 4. 跟踪登录和注销

现在我们需要跟踪用户何时成功登录或注销，以便我们可以在会话中添加或删除活动用户。在Spring Security应用程序中，这可以通过实现AuthenticationSuccessHandler和LogoutSuccessHandler接口来实现。

### 4.1 实现AuthenticationSuccessHandler

对于登录操作，我们将通过覆盖onAuthenticationSuccess()方法将登录用户的用户名设置为会话的属性，该方法为我们提供对session和authentication对象的访问：

```java
@Component("myAuthenticationSuccessHandler")
public class MySimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    ActiveUserStore activeUserStore;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            LoggedUser user = new LoggedUser(authentication.getName(), activeUserStore);
            session.setAttribute("user", user);
        }
    }
}
```

### 4.2 实现LogoutSuccessHandler

对于注销操作，我们将通过覆盖LogoutSuccessHandler接口的onLogoutSuccess()方法来删除用户属性：

```java
@Component("myLogoutSuccessHandler")
public class MyLogoutSuccessHandler implements LogoutSuccessHandler{
    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession();
        if (session != null){
            session.removeAttribute("user");
        }
    }
}
```

## 5. 控制器和视图

为了演示以上所有操作，我们将为URL “/users”创建一个控制器映射，它将检索用户列表，将其添加为模型属性并返回users.html视图：

### 5.1 控制器

```java
@Controller
public class UserController {

    @Autowired
    ActiveUserStore activeUserStore;

    @GetMapping("/loggedUsers")
    public String getLoggedUsers(Locale locale, Model model) {
        model.addAttribute("users", activeUserStore.getUsers());
        return "users";
    }
}
```

### 5.2 users.html

```xml
<html>
    <body>
        <h2>Currently logged in users</h2>
        <div th:each="user : ${users}">
            <p th:text="${user}">user</p>
        </div>
    </body>
</html>
```

## 6. 使用SessionRegistry的替代方法

另一种检索当前登录用户的方法是利用Spring的SessionRegistry，这是一个管理用户和会话的类。此类具有获取用户列表的方法getAllPrincipals()。

对于每个用户，我们可以通过调用方法getAllSessions()来查看他们所有会话的列表。为了仅获取当前登录的用户，我们必须通过将getAllSessions()的第二个参数设置为false来排除过期的会话：

```java
@Autowired
private SessionRegistry sessionRegistry;

@Override
public List<String> getUsersFromSessionRegistry() {
    return sessionRegistry.getAllPrincipals().stream()
        .filter(u -> !sessionRegistry.getAllSessions(u, false).isEmpty())
        .map(Object::toString)
        .collect(Collectors.toList());
}
```

为了使用SessionRegistry类，我们必须定义bean并将其应用于会话管理，如下所示：

```java
http.sessionManagement()
    .maximumSessions(1).sessionRegistry(sessionRegistry())
// ...

@Bean
public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
}
```

## 7. 总结

在本文中，我们演示了如何确定Spring Security应用程序中当前登录的用户是谁。