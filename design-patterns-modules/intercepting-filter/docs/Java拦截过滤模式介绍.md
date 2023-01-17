## 1. 概述

在本教程中，我们将介绍拦截过滤器模式表示层核心 J2EE 模式。

这是我们的模式系列中的第二篇教程，也是可以[在此处](https://www.baeldung.com/java-front-controller-pattern)找到的前端控制器模式指南的后续教程。

拦截过滤器是在处理程序处理传入请求之前或之后触发操作的过滤器。

拦截过滤器代表 Web 应用程序中的集中组件，对所有请求都是通用的，并且可以扩展而不影响现有的处理程序。

## 2.用例

[让我们扩展上一指南](https://www.baeldung.com/java-front-controller-pattern)中的示例并实现身份验证机制、请求日志记录和访问者计数器。此外，我们希望能够以各种不同的编码交付我们的页面。

所有这些都是拦截过滤器的用例，因为它们对所有请求都是通用的，应该独立于处理程序。

## 3.过滤策略

让我们介绍不同的过滤策略和示例性用例。要使用 Jetty Servlet 容器运行代码，只需执行：

```bash
$> mvn install jetty:run
```

### 3.1. 自定义过滤策略

自定义过滤器策略用于需要有序处理请求的每个用例，在一个过滤器的意义上是基于执行链中前一个过滤器的结果。

[这些链将通过实现FilterChain](https://tomcat.apache.org/tomcat-7.0-doc/servletapi/javax/servlet/FilterChain.html)接口并向其注册各种Filter类来创建。

当使用具有不同关注点的多个过滤器链时，你可以在过滤器管理器中将它们连接在一起：

[![拦截过滤器-自定义策略](https://www.baeldung.com/wp-content/uploads/2016/11/intercepting_filter-custom_strategy.png)](https://www.baeldung.com/wp-content/uploads/2016/11/intercepting_filter-custom_strategy.png)

 

在我们的示例中，访客计数器通过计算登录用户的唯一用户名来工作，这意味着它基于身份验证过滤器的结果，因此，两个过滤器都必须链接起来。

让我们来实现这个过滤器链。

首先，我们将创建一个身份验证过滤器，检查是否存在针对设置的“用户名”属性的会话，如果不存在，则发出登录过程：

```java
public class AuthenticationFilter implements Filter {
    ...
    @Override
    public void doFilter(
      ServletRequest request,
      ServletResponse response, 
      FilterChain chain) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        
        HttpSession session = httpServletRequest.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            FrontCommand command = new LoginCommand();
            command.init(httpServletRequest, httpServletResponse);
            command.process();
        } else {
            chain.doFilter(request, response);
        }
    }
    
    ...
}
```

现在让我们创建访客计数器。此过滤器维护唯一用户名的HashSet并向请求添加“counter”属性：

```java
public class VisitorCounterFilter implements Filter {
    private static Set<String> users = new HashSet<>();

    ...
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) {
        HttpSession session = ((HttpServletRequest) request).getSession(false);
        Optional.ofNullable(session.getAttribute("username"))
          .map(Object::toString)
          .ifPresent(users::add);
        request.setAttribute("counter", users.size());
        chain.doFilter(request, response);
    }

    ...
}
```

接下来，我们将实现一个FilterChain来迭代已注册的过滤器并执行doFilter方法：

```java
public class FilterChainImpl implements FilterChain {
    private Iterator<Filter> filters;

    public FilterChainImpl(Filter... filters) {
        this.filters = Arrays.asList(filters).iterator();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) {
        if (filters.hasNext()) {
            Filter filter = filters.next();
            filter.doFilter(request, response, this);
        }
    }
}
```

为了将我们的组件连接在一起，让我们创建一个简单的静态管理器，它负责实例化过滤器链、注册其过滤器并启动它：

```java
public class FilterManager {
    public static void process(HttpServletRequest request,
      HttpServletResponse response, OnIntercept callback) {
        FilterChain filterChain = new FilterChainImpl(
          new AuthenticationFilter(callback), new VisitorCounterFilter());
        filterChain.doFilter(request, response);
    }
}
```

作为最后一步，我们必须在FrontCommand中调用我们的FilterManager作为请求处理序列的公共部分：

```java
public abstract class FrontCommand {
    ...

    public void process() {
        FilterManager.process(request, response);
    }

    ...
}
```

### 3.2. 基本过滤策略

在本节中，我们将介绍基本过滤器策略，其中一个公共超类用于所有已实现的过滤器。

该策略与上一节中的自定义策略或我们将在下一节中介绍的标准过滤器策略一起使用效果很好。

抽象基类可用于应用属于过滤器链的自定义行为。我们将在示例中使用它来减少与过滤器配置和调试日志记录相关的样板代码：

```java
public abstract class BaseFilter implements Filter {
    private Logger log = LoggerFactory.getLogger(BaseFilter.class);

    protected FilterConfig filterConfig;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Initialize filter: {}", getClass().getSimpleName());
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {
        log.info("Destroy filter: {}", getClass().getSimpleName());
    }
}
```

让我们扩展这个基类来创建一个请求日志过滤器，它将被集成到下一节中：

```java
public class LoggingFilter extends BaseFilter {
    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(
      ServletRequest request, 
      ServletResponse response,
      FilterChain chain) {
        chain.doFilter(request, response);
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        
        String username = Optional
          .ofNullable(httpServletRequest.getAttribute("username"))
          .map(Object::toString)
          .orElse("guest");
        
        log.info(
          "Request from '{}@{}': {}?{}", 
          username, 
          request.getRemoteAddr(),
          httpServletRequest.getRequestURI(), 
          request.getParameterMap());
    }
}
```

### 3.3. 标准过滤策略

一种更灵活的应用过滤器的方法是实施标准过滤器策略。这可以通过在部署描述符中声明过滤器来完成，或者从 Servlet 规范 3.0 开始，通过注解来完成。

标准过滤器策略 允许在没有明确定义的过滤器管理器的情况下将新过滤器插入默认链：

[![拦截过滤器标准策略](https://www.baeldung.com/wp-content/uploads/2016/11/intercepting_filter-standard_strategy.png)](https://www.baeldung.com/wp-content/uploads/2016/11/intercepting_filter-standard_strategy.png)

 

请注意，应用过滤器的顺序无法通过注解指定。如果你需要有序执行，则必须坚持使用部署描述符或实施自定义过滤策略。

让我们实现一个注解驱动的编码过滤器，它也使用基本过滤器策略：

```java
@WebFilter(servletNames = {"intercepting-filter"}, 
  initParams = {@WebInitParam(name = "encoding", value = "UTF-8")})
public class EncodingFilter extends BaseFilter {
    private String encoding;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        this.encoding = filterConfig.getInitParameter("encoding");
    }

    @Override
    public void doFilter(ServletRequest request,
      ServletResponse response, FilterChain chain) {
        String encoding = Optional
          .ofNullable(request.getParameter("encoding"))
          .orElse(this.encoding);
        response.setCharacterEncoding(encoding); 
        
        chain.doFilter(request, response);
    }
}
```

在具有部署描述符的 Servlet 场景中，我们的web.xml将包含这些额外的声明：

```xml

<filter>
	<filter-name>encoding-filter</filter-name>
	<filter-class>
		cn.tuyucheng.taketoday.patterns.intercepting.filter.filters.EncodingFilter
	</filter-class>
</filter>
<filter-mapping>
<filter-name>encoding-filter</filter-name>
<servlet-name>intercepting-filter</servlet-name>
</filter-mapping>
```

让我们选择我们的日志过滤器并对其进行注解，以便 Servlet 使用：

```java
@WebFilter(servletNames = "intercepting-filter")
public class LoggingFilter extends BaseFilter {
    ...
}
```

### 3.4. 模板过滤策略

模板过滤策略与基本过滤策略几乎相同，只是它使用在基类中声明的模板方法，这些方法必须在实现中被覆盖：

[![拦截过滤模板策略](https://www.baeldung.com/wp-content/uploads/2016/11/intercepting_filter-template_strategy.png)](https://www.baeldung.com/wp-content/uploads/2016/11/intercepting_filter-template_strategy.png)

 

让我们创建一个基本过滤器类，其中包含两个在进一步处理之前和之后调用的抽象过滤器方法。

由于这种策略不太常见并且我们没有在示例中使用它，具体的实现和用例取决于你的想象：

```java
public abstract class TemplateFilter extends BaseFilter {
    protected abstract void preFilter(HttpServletRequest request,
      HttpServletResponse response);

    protected abstract void postFilter(HttpServletRequest request,
      HttpServletResponse response);

    @Override
    public void doFilter(ServletRequest request,
      ServletResponse response, FilterChain chain) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        
        preFilter(httpServletRequest, httpServletResponse);
        chain.doFilter(request, response);
        postFilter(httpServletRequest, httpServletResponse);
    }
}
```

## 4。总结

拦截过滤器模式捕获可以独立于业务逻辑发展的横切关注点。从业务运营的角度来看，过滤器是作为一系列前置或后置操作执行的。

正如我们目前所见，拦截过滤器模式可以使用不同的策略来实现。在“真实世界”的应用程序中，这些不同的方法可以结合使用。