## 1. 概述

Spring Mobile是对流行的Spring Web MVC框架的现代扩展，有助于简化需要与跨设备平台完全或部分兼容的Web应用程序的开发，工作量最小，样板代码更少。

在本文中，我们将了解Spring Mobile项目，并将构建一个示例项目来突出Spring Mobile的用途。

## 2. Spring Mobile的特点

-   **自动设备检测**：Spring Mobile具有内置的服务器端设备解析器抽象层。这会分析所有传入的请求并检测发送方设备信息，例如设备类型、操作系统等
-   **站点偏好管理**：使用站点偏好管理，Spring Mobile允许用户选择网站的手机/平板电脑/普通视图。这是一种相对不推荐使用的技术，因为通过使用DeviceDelegatingViewresolver我们可以根据设备类型保留视图层，而不需要用户端的任何输入
-   **站点切换器**：站点切换器能够根据用户的设备类型(即移动设备、桌面设备等)自动将用户切换到最合适的视图
-   **设备感知视图管理器**：通常，根据设备类型，我们将用户请求转发到旨在处理特定设备的特定站点。Spring Mobile的视图管理器使开发人员可以灵活地将所有视图置于预定义格式中，并且Spring Mobile将根据设备类型自动管理不同的视图

## 3. 构建应用程序

现在让我们使用带有Spring Boot和Freemarker模板引擎的Spring Mobile创建一个演示应用程序，并尝试使用最少的编码来捕获设备详细信息。

### 3.1 Maven依赖项

在开始之前，我们需要在pom.xml中添加以下Spring Mobile依赖项：

```xml
<dependency>
    <groupId>org.springframework.mobile</groupId>
    <artifactId>spring-mobile-device</artifactId>
    <version>2.0.0.M3</version>
</dependency>
```

请注意，最新的依赖项在Spring Milestones仓库中可用，因此我们也将其添加到我们的pom.xml中：

```xml
<repositories>
    <repository>
        <id>spring-milestones</id>
        <name>Spring Milestones</name>
        <url>https://repo.spring.io/libs-milestone</url>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>
```

### 3.2 创建Freemarker模板

首先，让我们使用Freemarker创建我们的index页面。不要忘记添加必要的依赖项以启用Freemarker的自动配置。

由于我们正在尝试检测发送方设备并相应地路由请求，因此我们需要创建三个单独的Freemarker文件来解决这个问题；一个处理移动请求，另一个处理平板电脑，最后一个(默认)处理普通浏览器请求。

我们需要在src/main/resources/templates下创建两个名为“mobile”和“tablet”的文件夹，并相应地放置Freemarker文件。最终的结构应该是这样的：

```powershell
└── src
    └── main
        └── resources
            └── templates
                └── index.ftl
                └── mobile
                    └── index.ftl
                └── tablet
                    └── index.ftl
```

现在，让我们将以下HTML放入index.ftl文件中：

```html
<h1>You are into browser version</h1>
```

根据设备类型，我们将更改<h1\>标签内的内容，

### 3.3 启用DeviceDelegatingViewresolver

要启用Spring Mobile DeviceDelegatingViewresolver服务，我们需要将以下属性放入application.properties中：

```properties
spring.mobile.devicedelegatingviewresolver.enabled=true
```

当你包含Spring Mobile Starter时，在Spring Boot中默认启用站点首选项功能。但是，可以通过将以下属性设置为false来禁用它：

```properties
spring.mobile.sitepreference.enabled=true
```

### 3.4 添加Freemarker属性

为了让Spring Boot能够找到并呈现我们的模板，我们需要将以下内容添加到我们的application.properties中：

```properties
spring.freemarker.template-loader-path=classpath:/templates
spring.freemarker.suffix=.ftl
```

### 3.5 创建控制器

现在我们需要创建一个Controller类来处理传入的请求。我们将使用简单的@GetMapping注解来处理请求：

```java
@Controller
public class IndexController {

    @GetMapping("/")
    public String greeting(Device device) {
        String deviceType = "browser";
        String platform = "browser";
        String viewName = "index";

        if (device.isNormal()) {
            deviceType = "browser";
        } else if (device.isMobile()) {
            deviceType = "mobile";
            viewName = "mobile/index";
        } else if (device.isTablet()) {
            deviceType = "tablet";
            viewName = "tablet/index";
        }

        platform = device.getDevicePlatform().name();

        if (platform.equalsIgnoreCase("UNKNOWN")) {
            platform = "browser";
        }

        return viewName;
    }
}
```

这里有几点需要注意：

-   在处理程序映射方法中，我们传递org.springframework.mobile.device.Device。这是每个请求中注入的设备信息。这是由我们在application.properties中启用的DeviceDelegatingViewresolver完成的
-   org.springframework.mobile.device.Device有几个内置方法，如isMobile()、isTablet()、getDevicePlatform()等。使用这些我们可以捕获我们需要的所有设备信息并使用它

### 3.6 Java配置

要在Spring Web应用程序中启用设备检测，我们还需要添加一些配置：

```java
@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Bean
    public DeviceResolverHandlerInterceptor deviceResolverHandlerInterceptor() {
        return new DeviceResolverHandlerInterceptor();
    }

    @Bean
    public DeviceHandlerMethodArgumentResolver deviceHandlerMethodArgumentResolver() {
        return new DeviceHandlerMethodArgumentResolver();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(deviceResolverHandlerInterceptor());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(deviceHandlerMethodArgumentResolver());
    }
}
```

我们快完成了。最后要做的一件事是构建一个Spring Boot配置类来启动应用程序：

```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## 4. 测试应用程序

一旦我们启动应用程序，它将在http://localhost:8080上运行。

**我们将使用Google Chrome的开发者控制台来模拟不同类型的设备**。我们可以通过按Ctrl + Shift + I或按F12来启用它。

默认情况下，如果我们打开主页，我们可以看到Spring Web正在将设备检测为桌面浏览器。我们应该看到以下结果：

<img src="../assets/img.png">

现在，在控制台面板上，我们单击左上角的第二个图标。它将启用浏览器的移动视图。

我们可以在浏览器的左上角看到一个下拉菜单。在下拉菜单中，我们可以选择不同种类的设备类型。要模拟移动设备，让我们选择Nexus 6P并刷新页面。

**刷新页面后，我们会注意到页面的内容发生了变化，因为DeviceDelegatingViewresolver已经检测到最后一个请求来自移动设备**。因此，它传递在模板的mobile文件夹中的index.ftl文件。

结果如下：

<img src="../assets/img_1.png">

同样，我们将模拟平板电脑版本。让我们像上次一样从下拉列表中选择iPad并刷新页面。内容会被改变，它应该被渲染为平板电脑视图：

<img src="../assets/img_2.png">

现在，我们将查看站点首选项功能是否按预期工作。

要模拟用户希望以移动友好的方式查看网站的实时场景，只需在默认URL的末尾添加以下URL参数：

```plaintext
?site_preference=mobile
```

刷新后，视图应自动移动到移动视图，即会显示以下文本“You are into mobile version”。

以同样的方式模拟平板电脑首选项，只需在默认URL末尾添加以下URL参数：

```plaintext
?site_preference=tablet
```

就像上次一样，视图应该会自动刷新为平板电脑视图。

请注意，默认URL将保持不变，如果用户再次使用默认URL，用户将被重定向到基于设备类型的相应视图。

## 5. 总结

我们刚刚创建了一个Web应用程序并实现了跨平台功能。从生产力的角度来看，这是一个巨大的性能提升。**Spring Mobile消除了许多前端脚本来处理跨浏览器行为，从而减少了开发时间**。