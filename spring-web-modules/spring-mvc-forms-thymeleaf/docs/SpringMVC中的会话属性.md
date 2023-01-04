## 1. 概述

在开发Web应用程序时，我们经常需要在多个视图中引用相同的属性。例如，我们可能有购物车内容需要在多个页面上展示。

存储这些属性的一个好位置是在用户的会话中。

在本教程中，我们将专注于一个简单的示例并检查使用会话属性的2种不同策略：

-   使用作用域代理
-   使用@SessionAttributes注解

## 2. Maven设置

我们将使用Spring Boot启动器来引导我们的项目并引入所有必要的依赖项。

我们的设置需要父声明、Web启动器和thymeleaf启动器。

我们还将包括spring test starter以在我们的单元测试中提供一些额外的实用程序：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.2</version>
    <relativePath/>
</parent>
 
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
     </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

这些依赖项的最新版本可以在[Maven Central](https://search.maven.org/search?q=a:spring-boot-starter-web)上找到。

## 3. 示例用例

我们的示例将实现一个简单的“TODO”应用程序。我们将有一个用于创建TodoItem实例的表单和一个显示所有TodoItem的列表视图。

如果我们使用表单创建TodoItem，则随后对表单的访问将使用最近添加的TodoItem的值进行预填充。我们将使用此功能来演示如何“记住”存储在会话作用域内的表单值。

我们的2个模型类被实现为简单的POJO：

```java
public class TodoItem {

    private String description;
    private LocalDateTime createDate;

    // getters and setters
}
```

```java
public class TodoList extends ArrayDeque<TodoItem>{

}
```

我们的TodoList类扩展了ArrayDeque ，使我们能够通过peekLast方法方便地访问最近添加的项目。

我们需要2个控制器类：1个用于我们将要查看的每个策略。它们会有细微差别，但核心功能将在两者中体现。每个都有3个@RequestMapping：

-   @GetMapping("/form")：此方法将负责初始化表单和呈现表单视图。如果TodoList不为空，该方法将使用最近添加的TodoItem预填充表单。
-   @PostMapping("/form")：此方法将负责将提交的TodoItem添加到TodoList并重定向到列表URL。
-   @GetMapping("/todos.html")：此方法将简单地将TodoList添加到模型中以进行显示和呈现列表视图。

## 4. 使用作用域代理

### 4.1 设置

在此设置中，我们的TodoList配置为由代理支持的会话作用域的@Bean。@Bean是代理的事实意味着我们能够将它注入我们的单例作用域的@Controller中。

由于上下文初始化时没有会话，Spring将创建一个TodoList的代理以作为依赖项注入。TodoList的目标实例将在请求需要时根据需要实例化。

有关Spring中bean作用域的更深入讨论，请参阅我们[关于该主题的文章](https://www.baeldung.com/spring-bean-scopes)。

首先，我们在@Configuration类中定义我们的bean：

```java
@Bean
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public TodoList todos() {
    return new TodoList();
}
```

接下来，我们将bean声明为@Controller的依赖项，并像注入任何其他依赖项一样注入它：

```java
@Controller
@RequestMapping("/scopedproxy")
public class TodoControllerWithScopedProxy {

    private TodoList todos;

    // constructor and request mappings
}
```

最后，在请求中使用bean只需要调用它的方法：

```java
@GetMapping("/form")
public String showForm(Model model) {
    if (!todos.isEmpty()) {
        model.addAttribute("todo", todos.peekLast());
    } else {
        model.addAttribute("todo", new TodoItem());
    }
    return "scopedproxyform";
}
```

### 4.2 单元测试

为了使用作用域代理测试我们的实现，我们首先配置一个SimpleThreadScope，这将确保我们的单元测试准确地模拟我们正在测试的代码的运行时条件。

首先，我们定义一个TestConfig和一个CustomScopeConfigurer：

```java
@Configuration
public class TestConfig {

    @Bean
    public CustomScopeConfigurer customScopeConfigurer() {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.addScope("session", new SimpleThreadScope());
        return configurer;
    }
}
```

现在我们可以开始测试表单的初始请求是否包含未初始化的TodoItem：

```java
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
public class TodoControllerWithScopedProxyIntegrationTest {

    // ...

    @Test
    public void whenFirstRequest_thenContainsUnintializedTodo() throws Exception {
        MvcResult result = mockMvc.perform(get("/scopedproxy/form"))
              .andExpect(status().isOk())
              .andExpect(model().attributeExists("todo"))
              .andReturn();

        TodoItem item = (TodoItem) result.getModelAndView().getModel().get("todo");

        assertTrue(StringUtils.isEmpty(item.getDescription()));
    }
}
```

我们还可以确认我们的提交发出了重定向，并且后续的表单请求已使用新添加的TodoItem进行了预填充：

```java
@Test
public void whenSubmit_thenSubsequentFormRequestContainsMostRecentTodo() throws Exception {
    mockMvc.perform(post("/scopedproxy/form")
        .param("description", "newtodo"))
        .andExpect(status().is3xxRedirection())
        .andReturn();

    MvcResult result = mockMvc.perform(get("/scopedproxy/form"))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("todo"))
         .andReturn();
    TodoItem item = (TodoItem) result.getModelAndView().getModel().get("todo");
 
    assertEquals("newtodo", item.getDescription());
}
```

### 4.3 讨论

使用作用域代理策略的一个关键特性是它对请求映射方法签名没有影响。与@SessionAttributes策略相比，这使可读性保持在非常高的水平。

回想一下控制器在默认情况下具有单例作用域可能会有所帮助。

这就是为什么我们必须使用代理而不是简单地注入非代理会话作用域bean的原因。我们不能将作用域较小的bean注入到作用域较大的bean中。

在这种情况下，尝试这样做会触发异常并显示一条消息，其中包含：Scope 'session' is not active for the current thread。

如果我们愿意用会话作用域定义我们的控制器，我们可以避免指定proxyMode。这可能有缺点，特别是如果创建控制器的成本很高，因为必须为每个用户会话创建一个控制器实例。

请注意，TodoList可用于其他组件进行注入。根据用例，这可能是一个好处或一个缺点。如果使bean对整个应用程序可用是有问题的，则可以将实例的作用域限定为控制器，而不是使用@SessionAttributes，我们将在下一个示例中看到。

## 5. 使用@SessionAttributes注解

### 5.1 设置

在此设置中，我们没有将TodoList定义为Spring管理的@Bean。相反，我们将其声明为@ModelAttribute并指定@SessionAttributes注解以将其作用域限定为控制器的会话。

第一次访问我们的控制器时，Spring将实例化一个实例并将其放入模型中。由于我们还在@SessionAttributes中声明了bean，因此Spring将存储该实例。

有关Spring中@ModelAttribute的更深入讨论，请参阅我们[关于该主题的文章](https://www.baeldung.com/spring-mvc-and-the-modelattribute-annotation)。

首先，我们通过在控制器上提供一个方法来声明我们的bean，并使用@ModelAttribute注解该方法：

```java
@ModelAttribute("todos")
public TodoList todos() {
    return new TodoList();
}
```

接下来，我们使用@SessionAttributes通知控制器将我们的TodoList视为会话作用域的：

```java
@Controller
@RequestMapping("/sessionattributes")
@SessionAttributes("todos")
public class TodoControllerWithSessionAttributes {
    // ... other methods
}
```

最后，要在请求中使用bean，我们在@RequestMapping的方法签名中提供对它的引用：

```java
@GetMapping("/form")
public String showForm(Model model, @ModelAttribute("todos") TodoList todos) {
    if (!todos.isEmpty()) {
        model.addAttribute("todo", todos.peekLast());
    } else {
        model.addAttribute("todo", new TodoItem());
    }
    return "sessionattributesform";
}

```

在@PostMapping方法中，我们注入RedirectAttributes并在返回我们的RedirectView之前调用addFlashAttribute。与我们的第一个示例相比，这是实现上的一个重要区别：

```java
@PostMapping("/form")
public RedirectView create(@ModelAttribute TodoItem todo, @ModelAttribute("todos") TodoList todos, RedirectAttributes attributes) {
    todo.setCreateDate(LocalDateTime.now());
    todos.add(todo);
    attributes.addFlashAttribute("todos", todos);
    return new RedirectView("/sessionattributes/todos.html");
}
```

Spring使用Model的专门RedirectAttributes实现用于重定向场景，以支持URL参数的编码。在重定向期间，存储在模型上的任何属性通常只有在包含在URL中时才对框架可用。

通过使用addFlashAttribute，我们告诉框架我们希望我们的TodoList在重定向后继续存在，而不需要在URL中对其进行编码。

### 5.2 单元测试

表单视图控制器方法的单元测试与我们在第一个示例中看到的测试相同。但是，@PostMapping的测试有点不同，因为我们需要访问flash属性以验证行为：

```java
@Test
public void whenTodoExists_thenSubsequentFormRequestContainsesMostRecentTodo() throws Exception {
    FlashMap flashMap = mockMvc.perform(post("/sessionattributes/form")
        .param("description", "newtodo"))
        .andExpect(status().is3xxRedirection())
        .andReturn().getFlashMap();

    MvcResult result = mockMvc.perform(get("/sessionattributes/form")
        .sessionAttrs(flashMap))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("todo"))
        .andReturn();
    TodoItem item = (TodoItem) result.getModelAndView().getModel().get("todo");
 
    assertEquals("newtodo", item.getDescription());
}
```

### 5.3 讨论

用于在会话中存储属性的@ModelAttribute和@SessionAttributes策略是一个简单的解决方案，不需要额外的上下文配置或Spring管理的@Bean。

与我们的第一个示例不同，有必要在@RequestMapping方法中注入TodoList。

此外，我们必须在重定向场景中使用flash属性。

## 6. 总结

在本文中，我们研究了使用作用域代理和@SessionAttributes作为在Spring MVC中处理会话属性的两种策略。请注意，在这个简单的示例中，存储在会话中的任何属性只会在会话的生命周期内存在。

如果我们需要在服务器重启或会话超时之间保留属性，我们可以考虑使用Spring Session来透明地处理保存信息。有关更多信息，请查看我们[关于Spring Session的文章](https://www.baeldung.com/spring-session)。