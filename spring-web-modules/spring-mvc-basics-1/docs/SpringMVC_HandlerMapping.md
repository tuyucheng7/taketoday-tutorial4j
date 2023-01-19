## 1. 概述

在Spring MVC中，DispatcherServlet充当前端控制器，接收所有传入的HTTP请求并处理它们。

简单地说，在HandlerMapping的帮助下，通过将请求传递给相关组件来进行处理。

HandlerMapping是一个接口，它定义了请求和处理程序对象之间的映射。而Spring MVC框架提供了一些现成的实现，也可以由开发者实现，提供自定义的映射策略。

本文介绍Spring MVC提供的一些实现，例如BeanNameUrlHandlerMapping、SimpleUrlHandlerMapping、
ControllerClassNameHandlerMapping，它们的配置，以及它们之间的区别。

## 2. BeanNameUrlHandlerMapping

BeanNameUrlHandlerMapping是默认的HandlerMapping实现。BeanNameUrlHandlerMapping将请求URL映射到具有相同名称的bean。

这种特殊的映射支持直接名称匹配，也支持使用“*”模式的模式匹配。

例如，一个传入的URL“/foo”映射到一个名为“/foo”的bean。
模式映射的一个例子是将“/foo*”的请求映射到名称以“/foo”开头的bean，例如“/foo2/”或“/fooOne/”。

让我们在这里配置这个示例并注册一个控制器bean来处理对“/beanNameUrl”的请求：

```java

@Configuration
public class BeanNameUrlHandlerMappingConfig {

    @Bean
    BeanNameUrlHandlerMapping beanNameUrlHandlerMapping() {
        return new BeanNameUrlHandlerMapping();
    }

    @Bean("/beanNameUrl")
    public WelcomeController welcomeBeanNameMappingConfig() {
        return new WelcomeController();
    }
}
```

下面是等效的XML配置：

```xml

<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans     
        http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean class="org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping"/>
    <bean name="beanNameUrl" class="cn.tuyucheng.web.controller.handlermapping.WelcomeController"/>
</beans>
```

需要注意的是，在这两种配置中，都不需要为BeanNameUrlHandlerMapping单独定义bean，因为它是由Spring MVC提供的。
删除这个bean定义不会导致任何问题，请求仍将映射到其注册的处理程序bean。

现在对“/beanNameUrl”的所有请求都将由DispatcherServlet转发到“WelcomeController”。
WelcomeController返回一个名为“welcome”的视图名称：

```java

@Controller
public class WelcomeController extends AbstractController {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("welcome");
    }
}
```

以下代码测试此配置并确保返回正确的视图名称：

```java

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = BeanNameUrlHandlerMappingConfig.class)
class BeanNameMappingConfigIntegrationTest {

    @Autowired
    private WebApplicationContext webAppContext;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    void whenBeanNameMapping_thenMappedOK() throws Exception {
        mockMvc.perform(get("/beanNameUrl")).
                andExpect(status().isOk()).
                andExpect(view().name("welcome")).
                andDo(print());
    }
}
```

## 3. SimpleUrlHandlerMapping

SimpleUrlHandlerMapping是最灵活的HandlerMapping实现。它允许在bean实例和URL之间或bean名称和URL之间进行直接和声明式映射。

让我们将请求“/simpleUrlWelcome”和“/*/simpleUrlWelcome”映射到“welcome” bean：

```java

@Configuration
public class SimpleUrlHandlerMappingConfig {

    @Bean
    public ViewResolver viewResolverSimpleMappingConfig() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    @Bean
    public SimpleUrlHandlerMapping simpleUrlHandlerMapping() {
        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        Map<String, Object> urlMap = new HashMap<>();
        urlMap.put("/simpleUrlWelcome", welcome());
        simpleUrlHandlerMapping.setUrlMap(urlMap);
        return simpleUrlHandlerMapping;
    }

    @Bean
    public WelcomeController welcome() {
        return new WelcomeController();
    }
}
```

下面是等效的XML配置：

```xml

<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans     
                 http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
        <property name="mappings">
            <value>
                /simpleUrlWelcome=welcome
                /*/simpleUrlWelcome=welcome
            </value>
        </property>
    </bean>

    <bean id="welcome" class="cn.tuyucheng.web.controller.handlermapping.WelcomeController"/>
</beans>
```

需要注意的是，在XML配置中，“<value\>”标签之间的映射必须以java.util.Properties类接受的形式配置，
并且应该遵循以下语法：path= Handler_Bean_Name。

URL通常应该带有斜杠，但是，如果路径不以斜杠开头，Spring MVC会自动添加它。

在XML中配置上述示例的另一种方法是使用“props”属性而不是“value”。props有一个“prop”标签列表，
其中每个标签都定义了一个映射，“key”指的是映射的URL，标签的值是bean的名称。

```text
<bean class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
    <property name="mappings">
        <props>
            <prop key="/simpleUrlWelcome">welcome</prop>
            <prop key="/*/simpleUrlWelcome">welcome</prop>
        </props>
    </property>
</bean>
```

以下测试用例确保对“/simpleUrlWelcome”的请求由“WelcomeController”处理，该控制器返回一个名为“welcome”的视图名称：

```java

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = SimpleUrlHandlerMappingConfig.class)
class SimpleUrlMappingConfigIntegrationTest {

    @Autowired
    private WebApplicationContext webAppContext;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    void whenSimpleUrlMapping_thenMappedOK() throws Exception {
        mockMvc.perform(get("/simpleUrlWelcome"))
                .andExpect(status().isOk())
                .andExpect(view().name("welcome"))
                .andDo(print());
    }
}
```

## 4. ControllerClassNameHandlerMapping(Spring 5中移除)

ControllerClassNameHandlerMapping将URL映射到具有相同名称或以相同名称开头的已注册控制器bean(或使用@Controller注解标注的控制器)。

在许多情况下它可能更方便，特别是对于处理单个请求类型的简单控制器实现。
Spring MVC使用类的名称并删除“Controller”后缀，然后将名称更改为小写，并将其作为带有“/”的映射返回。

例如，“WelcomeController”将作为映射返回到“/welcome*”，即任何以“welcome”开头的URL。

让我们配置ControllerClassNameHandlerMapping：

```java

@Configuration
public class ControllerClassNameHandlerMappingConfig {

    @Bean
    public ControllerClassNameHandlerMapping controllerClassNameHandlerMapping() {
        return new ControllerClassNameHandlerMapping();
    }

    @Bean
    public WelcomeController welcome() {
        return new WelcomeController();
    }
}
```

请注意，Spring 4.3不推荐使用ControllerClassNameHandlerMapping，而是支持注解驱动的处理程序方法。

另一个重要的注意事项是控制器名称将始终以小写形式返回(去掉“Controller”后缀)。
因此，如果我们有一个名为“WelcomeTuyuchengController”的控制器，它将只处理对“/welcometuyucheng”的请求，而不是对“/welcomeTuyucheng”的请求。

在下面的Java配置和XML配置中，我们定义了ControllerClassNameHandlerMapping bean，并为用来处理请求的控制器注册bean。
我们还注册了一个“WelcomeController”类型的bean，该bean将处理所有以“/welcome”开头的请求。

下面是等效的XML配置：

```xml

<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans     
        http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean class="org.springframework.web.servlet.mvc.support.ControllerClassNameHandlerMapping"/>

    <bean class="cn.tuyucheng.web.controller.handlermapping.WelcomeController"/>
</beans>
```

使用上述配置时，对“/welcome”的请求将由“WelcomeController”处理。

以下代码将确保对“/welcome*”的请求(例如“/welcometest”)由“WelcomeController”处理，该控制器返回一个名为“welcome”的视图名称：

```java
class ControllerClassNameHandlerMappingTest {
    // ...

    @Test
    void whenControllerClassNameMapping_thenMappedOK() {
        mockMvc.perform(get("/welcometest"))
                .andExpect(status().isOk())
                .andExpect(view().name("welcome"));
    }
}
```

## 5. 配置优先级

Spring MVC框架允许同时实现多个HandlerMapping接口。

让我们创建一个配置并注册两个控制器，都映射到“/welcome”，只是使用不同的映射并返回不同的视图名称：

```java

@Configuration
public class HandlerMappingDefaultConfig {

    @Bean("/welcome")
    public BeanNameHandlerMappingController beanNameHandlerMapping() {
        return new BeanNameHandlerMappingController();
    }

    @Bean
    public WelcomeController welcomeDefaultMappingConfig() {
        return new WelcomeController();
    }
}

public class BeanNameHandlerMappingController extends AbstractController {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("bean-name-handler-mapping");
    }
}
```

在没有显示注册HandlerMapping的情况下，将使用默认的BeanNameHandlerMapping。让我们通过测试来断言这种行为：

```java

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = HandlerMappingDefaultConfig.class)
class HandlerMappingDefaultConfigIntegrationTest {

    @Autowired
    private WebApplicationContext webAppContext;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    void whenDefaultConfig_thenMappedOK() throws Exception {
        mockMvc.perform(get("/welcome"))
                .andExpect(status().isOk())
                .andExpect(view().name("bean-name-handler-mapping"))
                .andDo(print());
    }
}
```

为了控制使用哪个HandlerMapping，我们使用setOrder(int order)方法设置优先级。此方法接收一个int参数，其中值越低表示优先级越高。

在XML配置中，你可以使用名为“order”的属性来配置优先级：

```text
<bean class="org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping">
    <property name="order" value="2" />
</bean>
```

让我们通过beanNameUrlHandlerMapping.setOrder(1)和simpleUrlHandlerMapping.setOrder(0)
分别为两个HandlerMapping bean设置优先级：

```java

@Configuration
public class HandlerMappingPrioritiesConfig {

    @Bean
    BeanNameUrlHandlerMapping beanNameUrlHandlerMappingOrder1() {
        BeanNameUrlHandlerMapping beanNameUrlHandlerMapping = new BeanNameUrlHandlerMapping();
        beanNameUrlHandlerMapping.setOrder(1);
        return beanNameUrlHandlerMapping;
    }

    @Bean
    public SimpleUrlHandlerMapping simpleUrlHandlerMappingOrder0() {
        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        simpleUrlHandlerMapping.setOrder(0);
        Map<String, Object> urlMap = new HashMap<>();
        urlMap.put("/welcome", simpleUrlMapping());
        simpleUrlHandlerMapping.setUrlMap(urlMap);
        return simpleUrlHandlerMapping;
    }

    @Bean
    public SimpleUrlMappingController simpleUrlMapping() {
        return new SimpleUrlMappingController();
    }

    @Bean("/welcome-priorities")
    public BeanNameHandlerMappingController beanNameHandlerMapping() {
        return new BeanNameHandlerMappingController();
    }
}

public class SimpleUrlMappingController extends AbstractController {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView("simple-url-handler-mapping");
    }
}
```

order属性的值越低，优先级越高。让我们通过测试来确定这种行为：

```java

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = HandlerMappingPrioritiesConfig.class)
class HandlerMappingPriorityConfigIntegrationTest {

    @Autowired
    private WebApplicationContext webAppContext;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @Test
    void whenConfiguringPriorities_thenMappedOK() throws Exception {
        mockMvc.perform(get("/welcome"))
                .andExpect(status().isOk())
                .andExpect(view().name("simple-url-handler-mapping"))
                .andDo(print());
    }
}
```

在上面的测试中，你会看到对“/welcome”的请求将由SimpleUrlHandlerMapping bean处理，
该bean调用SimpleUrlHandlerController并返回simple-url-handler-mapping视图。
通过相应地调整order属性的值，我们可以轻松地将BeanNameHandlerMapping配置为更高优先级。

## 6. 总结

在本文中，我们通过定义框架中的不同实现，介绍了如何在Spring MVC框架中处理URL映射。