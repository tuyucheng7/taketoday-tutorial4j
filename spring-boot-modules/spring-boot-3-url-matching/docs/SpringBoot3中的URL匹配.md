## 1. 概述

在本教程中，我们将探讨Spring Boot 3(Spring 6)引入的URL匹配变化。**URL匹配是Spring Boot中的一项强大功能，它使开发人员能够将特定的URL映射到Web应用程序中的控制器和操作**。此功能使应用程序的组织和导航变得容易，从而带来更好的用户体验。

为了处理URL映射，Spring Boot使用称为DispatcherServlet的强大机制，它充当应用程序的前端控制器。Servlet根据URL将请求转发到适当的控制器。DispatcherServlet使用一组称为映射的规则来确定哪个控制器应该处理给定的请求。

我们可以从阅读有关[Spring Boot 2(Spring 5)中旧版本URL匹配](https://www.baeldung.com/spring-5-mvc-url-matching)的更多详细信息开始。

## 2. Spring MVC和Webflux URL匹配变化

Spring Boot 3显著更改了尾部斜杠匹配配置选项，此选项确定是否将带有尾部斜杠的URL与不带斜杠的URL视为相同。以前版本的Spring Boot默认将此选项设置为true，这意味着控制器将默认匹配“GET /some/greeting”和“GET /some/greeting/”：

```java
@RestController
public class GreetingsController {

    @GetMapping("/some/greeting")
    public String greeting() {
        return "Hello";
    }
}
```

因此，如果我们尝试访问带有尾部斜杠的URL，我们将收到404错误，除非控制器专门设置为处理带有尾部斜杠的URL。如果处理不当，这可能会导致混乱和断开的链接。

让我们探讨一下如何适应这种变化的一些选择。

## 3. 附加路径

让我们更新我们现有的应用程序以确保正确处理URL以适应这一变化。这可以通过为每个处理带有尾部斜杠的URL的控制器添加特定的URL映射来完成。

例如，在我们之前处理URL “/some/greeting”的控制器中，我们需要为“/some/greeting/”添加一个单独的映射。这将确保用户即使在URL中包含尾部斜杠也可以访问所需的页面。

```java
@RestController
public class GreetingsController {

    @GetMapping("/some/greeting")
    public String greeting (){
        return "Hello";
    }

    @GetMapping("/some/greeting/")
    public String greeting (){
        return "Hello";
    }
}
```

这是一个使用Webflux的响应式@RestController：

```java
@RestController
public class GreetingsControllerReactive {

    @GetMapping("/some/reactive/greeting")
    public Mono<String> greeting() {
        return Mono.just("Hello reactive");
    }

    @GetMapping("/some/reactive/greeting/")
    public Mono<String> greetingTrailingSlash() {
        return Mono.just("Hello with slash reactive");
    }
}
```

## 4. 覆盖默认配置

覆盖尾部斜杠匹配配置并将其显式设置为true会更容易。我们可以通过覆盖Spring MVC的WebMvcConfigurer:configurePathMatch方法来做到这一点：

```java
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
    }
}
```

如果我们使用Webflux，配置更改是类似的：

```java
@Configuration
class WebConfiguration implements WebFluxConfigurer {

    @Override
    public void configurePathMatching(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
    }
}
```

这将授予我们向后兼容性，直到我们完全适应我们的应用程序。

## 5. 使用自定义过滤器配置重定向

当请求进来时，Spring Boot过滤器链根据请求和注册的过滤器确定要应用的过滤器。如果请求由传统的阻塞I/O端点(例如@RestController或@Controller)接收，过滤器链将应用任何实现Filter接口的过滤器。这些过滤器会阻塞I/O，并可能与Servlet API交互以读取或修改请求或响应。

要使用自定义过滤器来配置URL重定向以阻止请求，我们可以按照以下步骤操作：

首先，我们需要创建一个实现javax.servlet.Filter接口的新类：

```java
public class TrailingSlashRedirectFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        if (path.endsWith("/")) {
            String newPath = path.substring(0, path.length() - 1);
            HttpServletRequest newRequest = new CustomHttpServletRequestWrapper(httpRequest, newPath);
            chain.doFilter(newRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    private static class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {
        private final String newPath;

        public CustomHttpServletRequestWrapper(HttpServletRequest request, String newPath) {
            super(request);
            this.newPath = newPath;
        }

        @Override
        public String getRequestURI() {
            return newPath;
        }

        @Override
        public StringBuffer getRequestURL() {
            StringBuffer url = new StringBuffer();
            url.append(getScheme()).append("://").append(getServerName()).append(":").append(getServerPort())
                    .append(newPath);
            return url;
        }
    }
}
```

我们在这个自定义过滤器中实现Filter接口并重写doFilter方法。首先，我们将ServletRequest转换为HttpServletRequest以访问请求URI。然后，我们检查URI是否以斜杠结尾。如果是，我们使用新的CustomHttpServletRequestWrapper删除尾部斜杠，这是一个扩展HttpServletRequestWrapper的私有静态类。此类重写getRequestURI和getRequestURL方法以返回修改后的URI和URL。

最后，要将我们的自定义过滤器应用于所有端点，我们可以使用URL模式为“/*”的FilterRegistrationBean注册它。这是一个例子：

```java
public class WebConfig {

    @Bean
    public Filter trailingSlashRedirectFilter() {
        return new TrailingSlashRedirectFilter();
    }

    @Bean
    public FilterRegistrationBean<Filter> trailingSlashFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(trailingSlashRedirectFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
```

**请注意，将过滤器应用于所有端点可能会对性能产生影响，并且如果我们有不遵循标准RESTful URL模式的自定义端点，则可能会导致意外行为。通常，建议仅将过滤器应用于需要它们的端点**。

最后，让我们看一下使用过滤器的几个测试：

```java
private static final String BASEURL = "/some";

@Autowired
MockMvc mvc;

@Test
public void testGreeting() throws Exception {
    mvc.perform(get(BASEURL + "/greeting").accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello"));
}

@Test
public void testGreetingTrailingSlashWithFilter() throws Exception {
    mvc.perform(get(BASEURL + "/greeting/").accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello"));
}
```

## 6. 使用自定义WebFilter配置重定向

对于响应式端点，我们可以创建一个实现WebFilter接口并覆盖其filter方法的自定义类：

```java
public class TrailingSlashRedirectFilterReactive implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        if (path.endsWith("/")) {
            String newPath = path.substring(0, path.length() - 1);
            ServerHttpRequest newRequest = request.mutate().path(newPath).build();
            return chain.filter(exchange.mutate().request(newRequest).build());
        }

        return chain.filter(exchange);
    }
}
```

首先，我们从ServerWebExchange参数中提取请求。我们使用getPath方法获取传入请求的路径并检查它是否以斜线结尾。如果是，我们删除尾部的斜杠并使用mutate方法创建一个新的ServerHttpRequest。然后，我们将修改后的exchange对象传递给WebFilterChain参数上的filter方法。如果路径不以斜杠结尾，我们将使用原始exchange对象调用filter方法。

要注册WebFilter，我们用@Component标注它，Spring Boot会自动将它注册到适当的WebFilterChain。

**要指定自定义TrailingSlashRedirectFilterReactive应该应用的路径，我们可以使用@WebFilter注解并将urlPatterns属性设置为URL模式列表**。

最后，让我们看一下使用过滤器的几个测试：

```java
private static final String BASEURL = "/some/reactive";

@Autowired
private WebTestClient webClient;

@Test
public void testGreeting() {
    webClient.get().uri( BASEURL + "/greeting")
        .exchange()
        .expectStatus().isOk()
        .expectBody().consumeWith(result -> {
            String responseBody = new String(result.getResponseBody());
            assertTrue(responseBody.contains("Hello reactive"));
        });
}
   
@Test
public void testGreetingTrailingSlashWithFilter() {
    webClient.get().uri(BASEURL +  "/greeting/")
        .exchange()
        .expectStatus().isOk()
        .expectBody().consumeWith(result -> {
            String responseBody = new String(result.getResponseBody());
            assertTrue(responseBody.contains("Hello reactive"));
        });
}
```

## 7. 通过代理配置重定向

配置Web服务器时，将来自以尾部斜杠结尾的URL的请求重定向到没有尾部斜杠的URL是一项常见任务，这有助于确保你网站上的所有URL具有一致的结构并改进搜索引擎优化(SEO)。

此外，大多数Web服务器都内置了对URL重定向的支持。此支持可用于将带有尾部斜杠的请求重定向到不带尾部斜杠的URL。

让我们探讨如何使用代理为两个流行的Web服务器配置重定向：Apache和Nginx。

### 7.1 Nginx

```nginx
location / {
    if ($request_uri ~ ^(.+)/$) {
        return 301 $1;
    }
    
    proxy_pass http://localhost:8080;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
}
```

在此示例中，我们将if块添加到根location块。此外，if块检查请求URI是否以尾部斜杠结尾。如果URI以尾部斜杠结尾，它会使用301重定向将请求重定向到不带尾部斜杠的同一URI。$request_uri是一个预定义的Nginx变量，它包含从客户端收到的原始请求URI，包括查询字符串(如果有)。正则表达式“^(.+)/$”有一个捕获组“(.+)”，它捕获后跟尾部斜杠的任何字符序列。return中的$1指令指的是第一个捕获的组，即没有尾部斜杠的匹配URI。

然后，我们使用proxy_pass指令指定处理请求的后端服务器的URL，并使用proxy_set_header指令设置必要的标头。请注意，我们需要将URL替换为后端服务器的实际URL。

### 7.2 Apache

```apache
RewriteEngine On
RewriteRule ^(.+)/$ $1 [L,R=301]

ProxyPass / http://localhost:8080/
ProxyPassReverse / http://localhost:8080/
```

在这个例子中，我们使用RewriteRule以及我们在Nginx配置中使用的正则表达式。执行RewriteRule时，它会将匹配的URL替换为第一组捕获的值(没有尾部斜杠的URL)的尾部斜杠，并执行301重定向。

然后，我们使用ProxyPass和ProxyPassReverse指令指定处理请求的后端服务器的URL。我们可以在<VirtualHost\>块中添加此配置，将其应用于整个站点。

## 8. 总结

在本文中，我们讨论了Spring Boot 3对尾部斜线匹配配置选项的弃用，这会显著影响框架中的URL映射，需要一些努力，但为应用程序提供了稳定一致的基础。通过了解这一变化并相应地更新我们的应用程序，我们可以确保无缝和一致的用户体验。