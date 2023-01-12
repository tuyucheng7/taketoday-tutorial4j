## 1. 概述

在本文中，我们将研究从 JSF 托管 bean 和 JSF 页面访问在 Spring 中定义的 bean 的方法，目的是将业务逻辑的执行委托给 Spring bean。

本文假定读者分别对 JSF 和 Spring 有一定的了解。本文基于JSF[的 Mojarra 实现](https://javaserverfaces.java.net/)。

## 2. 春天

让我们在 Spring 中定义以下 bean。UserManagementDAO bean将用户名添加到内存存储中，它由以下接口定义：

```java
public interface UserManagementDAO {
    boolean createUser(String newUserData);
}
```

使用以下Java配置配置 bean 的实现：

```java
public class SpringCoreConfig {
    @Bean
    public UserManagementDAO userManagementDAO() {
        return new UserManagementDAOImpl();
    }
}
```

或者使用以下 XML 配置：

```xml
<bean class="org.springframework.context.annotation.CommonAnnotationBeanPostProcessor" />
<bean class="com.baeldung.dao.UserManagementDAOImpl" id="userManagementDAO"/>
```

我们在 XML 中定义 bean，并注册CommonAnnotationBeanPostProcessor以确保获取@PostConstruct注解。

## 三、配置

以下部分解释了启用 Spring 和 JSF 上下文集成的配置项。

### 3.1. 没有web.xml 的Java 配置

通过实现WebApplicationInitializer，我们能够以编程方式配置ServletContext。以下是MainWebAppInitializer类中的onStartup()实现：

```java
public void onStartup(ServletContext sc) throws ServletException {
    AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();
    root.register(SpringCoreConfig.class);
    sc.addListener(new ContextLoaderListener(root));
}
```

AnnotationConfigWebApplicationContext引导 Spring的上下文并通过注册SpringCoreConfig类添加 bean。

同样，在 Mojarra 实现中有一个配置FacesServlet的FacesInitializer类。要使用此配置，扩展FacesInitializer就足够了。MainWebAppInitializer的完整实现如下所示：

```java
public class MainWebAppInitializer extends FacesInitializer implements WebApplicationInitializer {
    public void onStartup(ServletContext sc) throws ServletException {
        AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();
        root.register(SpringCoreConfig.class);
        sc.addListener(new ContextLoaderListener(root));
    }
}
```

### 3.2. 使用web.xml

我们将从在应用程序的web.xml文件中配置ContextLoaderListener开始：

```xml
<listener>
    <listener-class>
        org.springframework.web.context.ContextLoaderListener
    </listener-class>
</listener>
```

该侦听器负责在 Web 应用程序启动时启动 Spring 应用程序上下文。默认情况下，此侦听器将查找名为applicationContext.xml的 spring 配置文件。

### 3.3. faces-config.xml

我们现在在face-config.xml文件中配置SpringBeanFacesELResolver ：

```xml
<el-resolver>org.springframework.web.jsf.el.SpringBeanFacesELResolver</el-resolver>
```

EL 解析器是 JSF 框架支持的可插入组件，允许我们在评估表达式语言 (EL) 表达式时自定义 JSF 运行时的行为。这个 EL 解析器将允许 JSF 运行时通过 JSF 中定义的 EL 表达式访问 Spring 组件。

## 4. 在 JSF 中访问 Spring Beans

此时，我们的 JSF Web 应用程序已准备好从 JSF 支持 bean 或从 JSF 页面访问我们的 Spring bean。

### 4.1. 来自支持 Bean JSF 2.0

现在可以从 JSF 支持 bean 访问 Spring bean。根据运行的 JSF 版本，有两种可能的方法。在 JSF 2.0 中，可以在 JSF 托管 bean 上使用@ManagedProperty注解。

```java
@ManagedBean(name = "registration")
@RequestScoped
public class RegistrationBean implements Serializable {
    @ManagedProperty(value = "#{userManagementDAO}")
    transient private IUserManagementDAO theUserDao;

    private String userName;
    // getters and setters
}
```

请注意，在使用@ManagedProperty 时，getter 和 setter 是必需的。
现在——为了从托管 bean 断言 Spring bean 的可访问性，我们将添加createNewUser()方法：

```java
public void createNewUser() {
    FacesContext context = FacesContext.getCurrentInstance();
    boolean operationStatus = userDao.createUser(userName);
    context.isValidationFailed();
    if (operationStatus) {
        operationMessage = "User " + userName + " created";
    }
}

```

该方法的要点是使用userDao Spring bean 并访问其功能。

### 4.2. 来自 JSF 2.2 中的辅助 Bean

另一种仅在 JSF2.2 及更高版本中有效的方法是使用 CDI 的@Inject注解。这适用于 JSF 管理的 bean(带有@ManagedBean注解)和 CDI 管理的 bean(带有@Named注解)。

事实上，使用 CDI 注解，这是注入 bean 的唯一有效方法：

```java
@Named( "registration")
@RequestScoped
public class RegistrationBean implements Serializable {
    @Inject
    UserManagementDAO theUserDao;
}
```

使用这种方法，不需要 getter 和 setter。另请注意，EL 表达式不存在。

### 4.3. 从 JSF 视图

将从以下 JSF 页面触发createNewUser ()方法：

```html
<h:form>
    <h:panelGrid id="theGrid" columns="3">
        <h:outputText value="Username"/>
        <h:inputText id="firstName" binding="#{userName}" required="true"
          requiredMessage="#{msg['message.valueRequired']}" value="#{registration.userName}"/>
        <h:message for="firstName" style="color:red;"/>
        <h:commandButton value="#{msg['label.saveButton']}" action="#{registration.createNewUser}"
          process="@this"/>
        <h:outputText value="#{registration.operationMessage}" style="color:green;"/>
    </h:panelGrid>
</h:form>

```

要呈现页面，请启动服务器并导航至：

```bash
http://localhost:8080/jsf/index.jsf
```

我们还可以在 JSF 视图中使用 EL 来访问 Spring bean。要对其进行测试，只需将之前介绍的 JSF 页面中的第 7 行更改为：

```xml
<h:commandButton value="Save"
  action="#{registration.userDao.createUser(userName.value)}"/>
```

在这里，我们直接在 Spring DAO 上调用createUser方法，将userName的绑定值从 JSF 页面内传递给该方法，一起绕过托管 bean。

## 5.总结

我们检查了 Spring 和 JSF 上下文之间的基本集成，我们可以在其中访问 JSF bean 和页面中的 Spring bean。

值得注意的是，虽然 JSF 运行时提供了可插入架构，使 Spring 框架能够提供集成组件，但 Spring 框架中的注解不能在 JSF 上下文中使用，反之亦然。

这意味着将无法在 JSF 托管 bean 中使用@Autowired或@Component等注解，或在 Spring 托管 bean 上使用@ManagedBean注解。但是，可以在 JSF 2.2+ 托管 bean 和 Spring bean 中使用@Inject注解(因为 Spring 支持 JSR-330)。