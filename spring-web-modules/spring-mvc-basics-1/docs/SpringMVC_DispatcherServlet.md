## 1. 概述

简单地说，在前端控制器设计模式中，**单个控制器负责将传入的HttpRequest定向到应用程序的所有其他控制器和处理程序**。

Spring中的DispatcherServlet实现了这种模式，因此负责将HttpRequest协调到其正确的处理程序。

在本文中，我们将研究Spring DispatcherServlet的请求处理工作流，以及如何实现参与该工作流的几个接口。

## 2. DispatcherServlet请求处理

本质上，DispatcherServlet处理传入的HttpRequest，委托请求，并根据已在Spring应用程序中实现的配置的HandlerAdapter接口以及指定处理程序、
控制器端点和响应对象的注解来处理请求。

+ 在DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE key下与DispatcherServlet关联的WebApplicationContext被搜索并可供流程的所有元素使用。
+ DispatcherServlet使用getHandler()查找为你的dispatcher配置的HandlerAdapter接口的所有实现 -
  每个找到的和配置的实现都通过handle()在流程的其余部分处理请求。
+ LocaleResolver可以选择绑定到请求，以使流程中的元素能够解析语言环境。
+ ThemeResolver可以选择绑定到请求，让元素(如视图)决定使用哪个主题。
+ 如果指定了MultipartResolver，则检查请求是否有MultipartFiles - 找到的任何文件都会包装在MultipartHttpServletRequest中，以便进一步处理。
+ 在WebApplicationContext中声明的HandlerExceptionResolver实现获取在处理请求期间抛出的异常。

## 3. HandlerAdapter接口

HandlerAdapter接口通过几个特定的接口促进了控制器、servlet、HttpRequests和HTTP路径的使用。
因此，**HandlerAdapter接口在DispatcherServlet请求处理工作流的许多阶段中发挥着重要作用**。

首先，每个HandlerAdapter实现都从dispatcher的getHandler()方法放入HandlerExecutionChain。
然后，随着执行链的进行，这些实现中的每一个都会handle() HttpServletRequest对象。

在接下来的部分中，我们将更详细地探讨一些最重要和最常用的HandlerAdapter。

### 3.1 Mappings

为了理解映射，我们首先需要了解如何标注控制器，因为控制器对于HandlerMapping接口非常重要。

SimpleControllerHandlerAdapter允许在没有@Controller注解的情况下显式实现控制器。

RequestMappingHandlerAdapter支持使用@RequestMapping注解标注的方法。

**@RequestMapping注解设置处理程序在与其关联的WebApplicationContext中可用的特定端点**。

让我们看一个公开和处理“/user/example”端点的控制器示例：

```java

@Controller
@RequestMapping("/user")
@ResponseBody
public class UserController {

    @GetMapping("/example")
    public User fetchUserExample() {
        // ...
    }
}
```

@RequestMapping注解指定的路径通过HandlerMapping接口在内部进行管理。

**URL结构自然是相对于DispatcherServlet本身的，并且由servlet mapping决定**。

因此，如果DispatcherServlet映射到“/”，那么所有映射都将被该映射覆盖。

但是，如果servlet映射到“/dispatcher”，那么任何@RequestMapping注解都将与该根URL相关。

对于servlet mapping，“/”与“/*”不同，“/”是默认映射，将所有URL暴露给dispatcher的职责范围。

'/*'让许多新的Spring开发人员感到困惑。它没有指定具有相同URL上下文的所有路径都在dispatcher的职责范围内。
相反，它会覆盖并忽略其他dispatcher映射。因此，'/example'将显示为404。

因此，除非在非常有限的情况下(例如配置过滤器)，否则不应使用“/*”。

### 3.2 HTTP请求处理

DispatcherServlet的核心职责是将传入的HttpRequest分派给使用@Controller或@RestController注解指定的正确处理程序。

作为补充说明，@Controller和@RestController之间的主要区别在于响应的生成方式，默认情况下，@RestController还定义了@ResponseBody。

### 3.3 ViewResolver接口

ViewResolver附加到DispatcherServlet作为ApplicationContext对象的配置设置。

**ViewResolver确定dispatcher提供哪种类型的视图以及从何处提供这些视图**。

下面是一个示例配置，用于呈现JSP页面：

```java

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public UrlBasedViewResolver viewResolver() {
        UrlBasedViewResolver resolver = new UrlBasedViewResolver();
        resolver.setPrefix("/WEB-INF/view/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);
        return resolver;
    }
}
```

这包括三个主要部分：

1. 设置前缀，它设置默认URL路径以在其中找到设置的视图。
2. 通过后缀设置的默认视图类型。
3. 在解析器上设置一个视图类，该类允许JSTL或Tiles等技术与呈现的视图相关联。

**一个常见的问题涉及dispatcher的ViewResolver与整个项目目录结构的关联程度**。

下面是使用Spring的XML配置的InternalViewResolver的路径配置示例：

```text
<property name="prefix" value="/jsp/"/>
```

在我们的示例中，我们假设我们的应用程序托管在：

```text
http://localhost:8080/
```

这是本地托管的Apache Tomcat服务器的默认地址和端口。

假设我们的应用程序名为dispatcher-example，我们的JSP视图可以从以下位置访问：

```text
http://localhost:8080/dispatcher-example/jsp/
```

在使用Maven的普通Spring项目中，这些视图的路径如下：

```text
src -|
    main -|
        java
        resources
        webapp -|
            jsp
            WEB-INF
```

视图的默认位置在WEB-INF中。在上面的代码段中为InternalViewResolver指定的路径决定了“src/main/webapp”的子目录，你的视图将在该子目录中可用。

### 3.4 LocaleResolver接口

**为我们的dispatcher自定义session、request或cookie信息的主要方法是通过LocaleResolver接口**。

CookieLocaleResolver是一种允许使用cookie配置无状态应用程序属性的实现。

```java


public class WebConfig implements WebMvcConfigurer {

    @Bean
    public CookieLocaleResolver cookieLocaleResolverExample() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setDefaultLocale(Locale.CHINESE);
        localeResolver.setCookieName("locale-cookie-resolver-example");
        localeResolver.setCookieMaxAge(3600);
        return localeResolver;
    }

    @Bean
    public LocaleResolver sessionLocaleResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.CHINESE);
        localResolver.setDefaultTimeZone(TimeZone.getTimeZone("UTC"));
        return localeResolver;
    }
}
```

SessionLocaleResolver允许在有状态应用程序中进行特定于会话的配置。

setDefaultLocale()方法表示地理、政治或文化区域，而setDefaultTimeZone()确定相关应用程序Bean的相关TimeZone对象。

这两种方法都可用于上述每个LocaleResolver实现。

### 3.5 ThemeResolver接口

Spring为我们的视图提供了风格主题。

让我们看看如何配置dispatcher来处理主题。

首先，让我们设置查找和使用静态主题文件所需的所有配置。
我们需要为ThemeSource设置一个静态资源位置来配置实际的Themes本身(Theme对象包含这些文件中规定的所有配置信息)。

```java
public class WebConfig implements WebMvcConfigurer {

    /**
     * Static resource locations including themes
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/", "/resources/")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }

    /**
     * BEGIN theme configuration
     */
    @Bean
    public ResourceBundleThemeSource themeSource() {
        ResourceBundleThemeSource themeSource = new ResourceBundleThemeSource();
        themeSource.setDefaultEncoding("UTF-8");
        themeSource.setBasenamePrefix("themes.");
        return themeSource;
    }
}
```

由DispatcherServlet管理的请求可以通过传递给ThemeChangeInterceptor对象上可用的setParamName()的指定参数来修改主题：

```java
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public CookieThemeResolver themeResolver() {
        CookieThemeResolver resolver = new CookieThemeResolver();
        resolver.setDefaultThemeName("default");
        resolver.setCookieName("example-theme-cookie");
        return resolver;
    }

    @Bean
    public ThemeChangeInterceptor themeChangeInterceptor() {
        ThemeChangeInterceptor interceptor = new ThemeChangeInterceptor();
        interceptor.setParamName("theme");
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(themeChangeInterceptor());
    }
}
```

将以下JSP标签添加到我们的视图中，以显示正确的样式：

```text
<link rel="stylesheet" href="${ctx}/<spring:theme code='styleSheet'/>" type="text/css"/>
```

以下URL请求使用传递给我们配置的ThemeChangeInterceptor的“theme”参数呈现example主题：

```text
http://localhost:8080/dispatcher-example/?theme=example
```

### 3.6 MultipartResolver接口

MultipartResolver实现检查对multipart的请求，并将它们包装在MultipartHttpServletRequest中，
以便在找到至少一个multipart时由流程中的其他元素进一步处理：

```java
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public CommonsMultipartResolver multipartResolver() throws IOException {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize(10000000);
        return resolver;
    }
}
```

现在我们已经配置了MultipartResolver bean，让我们添加一个控制器来处理MultipartFile请求：

```java

@Controller
public class MultipartController {

    @Autowired
    ServletContext context;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ModelAndView FileUploadController(@RequestParam("file") MultipartFile file) {
        ModelAndView modelAndView = new ModelAndView("index");
        try {
            InputStream in = file.getInputStream();
            String path = new File(".").getAbsolutePath();
            FileOutputStream f = new FileOutputStream(path.substring(0, path.length() - 1) + "/uploads/" + file.getOriginalFilename());
            try {
                int ch;
                while ((ch = in.read()) != -1) {
                    f.write(ch);
                }
                modelAndView.getModel().put("message", "File uploaded successfully!");
            } catch (Exception e) {
                System.out.println("Exception uploading multipart: " + e);
            } finally {
                f.flush();
                f.close();
                in.close();
            }
        } catch (Exception e) {
            System.out.println("Exception uploading multipart: " + e);
        }
        return modelAndView;
    }
}
```

我们可以使用普通表单将文件提交到指定的端点。上传的文件将在“CATALINA_HOME/bin/uploads”中可用。

### 3.7 HandlerExceptionResolver接口

Spring的HandlerExceptionResolver为整个Web应用程序、单个控制器或一组控制器提供统一的错误处理。

**要提供应用程序范围的自定义异常处理，请创建一个使用@ControllerAdvice标注的类**：

```java

@ControllerAdvice
public class ExampleGlobalExceptionHandler {

    @ExceptionHandler
    @ResponseBody
    public String handleExampleException(Exception e) {
        // ...
    }
}
```

该类中使用@ExceptionHandler标注的任何方法都将在dispatcher职责范围内的每个控制器上可用。

DispatcherServlet的ApplicationContext中的HandlerExceptionResolver接口的实现可用于在将
@ExceptionHandler用作注解时拦截该dispatcher职责范围内的特定控制器，并且正确的类作为参数传入：

```java

@Controller
public class FooController {

    @ExceptionHandler({CustomException1.class, CustomException2.class})
    public void handleException() {
        // ...
    }
    // ...
}
```

如果发生异常CustomException1或CustomException2，handleException()方法将作为我们上面示例中的FooController的异常处理程序。

## 4. 总结

在本教程中，我们重点介绍了Spring的DispatcherServlet和几种配置它的方法。