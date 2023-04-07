## **一、概述**

本教程将重点关注**在 Spring 中实现重定向**，并将讨论每种策略背后的原因。

## 延伸阅读：

## [使用 Spring Security 登录后重定向到不同的页面](https://www.baeldung.com/spring-redirect-after-login)

使用 Spring Security 登录后如何重定向到不同页面的示例。

[阅读更多](https://www.baeldung.com/spring-redirect-after-login)→

## [Spring Security - 登录后重定向到以前的URL](https://www.baeldung.com/spring-security-redirect-login)

Spring Security登录后重定向的一个简短例子

[阅读更多](https://www.baeldung.com/spring-security-redirect-login)→

## [使用 Spring Security 控制会话](https://www.baeldung.com/spring-security-session)

使用 Spring Security 配置会话 - 设置并发会话，启用会话固定保护并防止 URL 包含会话信息。

[阅读更多](https://www.baeldung.com/spring-security-session)→



## **2. 为什么要重定向？**

让我们首先考虑我们可能需要在 Spring 应用程序中**进行重定向的原因。**

当然有很多可能的例子和原因。例如，我们可能需要 POST 表单数据，解决双重提交问题或将执行流程委托给另一个控制器方法。

这里有一个简短的旁注：典型的 Post/Redirect/Get 模式不足以解决双重提交问题，并且在初始提交完成之前刷新页面等问题仍可能导致双重提交。

## **3. 使用\*RedirectView重定向\*** 

让我们从这个简单的方法开始，**直接看一个例子**：

```java
@Controller
@RequestMapping("/")
public class RedirectController {
    
    @GetMapping("/redirectWithRedirectView")
    public RedirectView redirectWithUsingRedirectView(
      RedirectAttributes attributes) {
        attributes.addFlashAttribute("flashAttribute", "redirectWithRedirectView");
        attributes.addAttribute("attribute", "redirectWithRedirectView");
        return new RedirectView("redirectedUrl");
    }
}复制
```

在幕后，*RedirectView*将触发*HttpServletResponse.sendRedirect()*，它将执行实际的重定向。

注意这里**我们是如何将重定向属性注入到方法中的。**该框架将完成繁重的工作，并允许我们与这些属性进行交互。

我们正在添加模型属性*attribute*，它将作为 HTTP 查询参数公开。模型必须只包含对象——通常是字符串或可以转换为字符串的对象。

**现在让我们借助一个简单的\*curl\*命令**测试我们的重定向：

```bash
curl -i http://localhost:8080/spring-rest/redirectWithRedirectView复制
```

这是我们的结果：

```bash
HTTP/1.1 302 Found
Server: Apache-Coyote/1.1
Location: 
  http://localhost:8080/spring-rest/redirectedUrl?attribute=redirectWithRedirectView复制
```

## **4.重定向使用前缀\*重定向：\***

之前的方法——使用*RedirectView——*由于一些原因不是最优的。

首先，我们现在耦合到 Spring API，因为我们在代码中直接使用*RedirectView 。*

其次，我们现在需要从一开始就知道，在实现该控制器操作时，结果将始终是重定向，但情况可能并非总是如此。

更好的选择是使用前缀*redirect:*。重定向视图名称像任何其他逻辑视图名称一样被注入到控制器中。**控制器甚至不知道重定向正在发生。**

这是它的样子：

```java
@Controller
@RequestMapping("/")
public class RedirectController {
    
    @GetMapping("/redirectWithRedirectPrefix")
    public ModelAndView redirectWithUsingRedirectPrefix(ModelMap model) {
        model.addAttribute("attribute", "redirectWithRedirectPrefix");
        return new ModelAndView("redirect:/redirectedUrl", model);
    }
}
复制
```

当返回带有前缀*redirect:*的视图名称时，*UrlBasedViewResolver*（及其所有子类）会将此识别为需要进行重定向的特殊指示。视图名称的其余部分将用作重定向 URL。

一个快速但重要的注意事项是，当我们在这里使用这个逻辑视图名称时 — *redirect:/redirectedUrl* — 我们正在做一个**相对于当前 Servlet 上下文的重定向。**

如果我们需要重定向到绝对 URL，我们可以使用诸如重定向的名称*：http://localhost:8080/spring-redirect-and-forward/redirectedUrl 。*

**所以，现在当我们执行\*curl\*命令**时：

```bash
curl -i http://localhost:8080/spring-rest/redirectWithRedirectPrefix复制
```

我们会立即被重定向：

```bash
HTTP/1.1 302 Found
Server: Apache-Coyote/1.1
Location: 
  http://localhost:8080/spring-rest/redirectedUrl?attribute=redirectWithRedirectPrefix复制
```

## **5. Forward With the Prefix \*forward：\***

现在让我们看看如何做一些稍微不同的事情：前锋。

在代码之前，让我们**快速、高级地概述 forward 与 redirect 的语义**：

-   *redirect*将以 302 和*Location*标头中的新 URL 进行响应；然后浏览器/客户端将向新 URL 发出另一个请求。
-   *转发*完全发生在服务器端。Servlet 容器将相同的请求转发到目标 URL；URL 在浏览器中不会改变。

现在让我们看一下代码：

```java
@Controller
@RequestMapping("/")
public class RedirectController {
    
    @GetMapping("/forwardWithForwardPrefix")
    public ModelAndView redirectWithUsingForwardPrefix(ModelMap model) {
        model.addAttribute("attribute", "forwardWithForwardPrefix");
        return new ModelAndView("forward:/redirectedUrl", model);
    }
}
复制
```

与*redirect:*相同，*forward:*前缀将由*UrlBasedViewResolver*及其子类解析。在内部，这将创建一个*InternalResourceView*，它对新视图执行*RequestDispatcher.forward() 。*

***当我们使用curl\*****执行命令时**：

```bash
curl -I http://localhost:8080/spring-rest/forwardWithForwardPrefix
复制
```

我们将得到 HTTP 405（方法不允许）：

```bash
HTTP/1.1 405 Method Not Allowed
Server: Apache-Coyote/1.1
Allow: GET
Content-Type: text/html;charset=utf-8复制
```

总而言之，与我们在重定向解决方案中的两个请求相比，在这种情况下，我们只有一个请求从浏览器/客户端发送到服务器端。当然，先前由重定向添加的属性也丢失了。

## **6. \*RedirectAttributes的属性\*** 

接下来，让我们仔细看看**在 redirect 中传递属性，充分利用带有***RedirectAttributes*的框架：

```java
@GetMapping("/redirectWithRedirectAttributes")
public RedirectView redirectWithRedirectAttributes(RedirectAttributes attributes) {
 
    attributes.addFlashAttribute("flashAttribute", "redirectWithRedirectAttributes");
    attributes.addAttribute("attribute", "redirectWithRedirectAttributes");
    return new RedirectView("redirectedUrl");
}
复制
```

正如我们之前看到的，我们可以直接在方法中注入属性对象，这使得这种机制非常易于使用。

另请注意，**我们还添加了 flash 属性。**这是一个不会进入 URL 的属性。

**使用这种属性，我们稍后可以仅在重定向的最终目标方法中使用***@ModelAttribute(“flashAttribute”)* 访问 flash 属性：

```java
@GetMapping("/redirectedUrl")
public ModelAndView redirection(
  ModelMap model, 
  @ModelAttribute("flashAttribute") Object flashAttribute) {
     
     model.addAttribute("redirectionAttribute", flashAttribute);
     return new ModelAndView("redirection", model);
 }
复制
```

**因此，总结一下，如果我们使用\*curl\***测试功能：

```bash
curl -i http://localhost:8080/spring-rest/redirectWithRedirectAttributes复制
```

我们将被重定向到新位置：

```bash
HTTP/1.1 302 Found
Server: Apache-Coyote/1.1
Set-Cookie: JSESSIONID=4B70D8FADA2FD6C22E73312C2B57E381; Path=/spring-rest/; HttpOnly
Location: http://localhost:8080/spring-rest/redirectedUrl;
  jsessionid=4B70D8FADA2FD6C22E73312C2B57E381?attribute=redirectWithRedirectAttributes复制
```

这样，使用*RedirectAttributes*而不是*ModelMap*使我们能够仅在重定向操作中涉及的**两个方法之间共享一些属性。**

## **7. 没有前缀的替代配置**

现在让我们探索另一种配置：不使用前缀的重定向。

为此，我们需要使用*org.springframework.web.servlet.view.XmlViewResolver*：

```xml
<bean class="org.springframework.web.servlet.view.XmlViewResolver">
    <property name="location">
        <value>/WEB-INF/spring-views.xml</value>
    </property>
    <property name="order" value="0" />
</bean>
复制
```

这不是我们在之前的配置中使用的*org.springframework.web.servlet.view.InternalResourceViewResolver ：*

```html
<bean 
  class="org.springframework.web.servlet.view.InternalResourceViewResolver">
</bean>
复制
```

我们还需要在配置中定义一个*RedirectView bean：*

```html
<bean id="RedirectedUrl" class="org.springframework.web.servlet.view.RedirectView">
    <property name="url" value="redirectedUrl" />
</bean>
复制
```

现在我们可以**通过 id 引用这个新 bean 来触发重定向**：

```java
@Controller
@RequestMapping("/")
public class RedirectController {
    
    @GetMapping("/redirectWithXMLConfig")
    public ModelAndView redirectWithUsingXMLConfig(ModelMap model) {
        model.addAttribute("attribute", "redirectWithXMLConfig");
        return new ModelAndView("RedirectedUrl", model);
    }
}
复制
```

**为了测试它，我们将再次使用\*curl\*命令**：

```bash
curl -i http://localhost:8080/spring-rest/redirectWithRedirectView复制
```

这是我们的结果：

```bash
HTTP/1.1 302 Found
Server: Apache-Coyote/1.1
Location: 
  http://localhost:8080/spring-rest/redirectedUrl?attribute=redirectWithRedirectView复制
```

## **8. 重定向 HTTP POST 请求**

对于银行支付等用例，我们可能需要重定向 HTTP POST 请求。根据返回的 HTTP 状态代码，POST 请求可以重定向到 HTTP GET 或 POST。

根据 HTTP 1.1 协议[参考](https://tools.ietf.org/html/rfc7238)，状态代码 301（永久移动）和 302（已找到）允许将请求方法从 POST 更改为 GET。该规范还定义了相应的 307（临时重定向）和 308（永久重定向）状态代码，不允许将请求方法从 POST 更改为 GET。

让我们看一下将一个 post 请求重定向到另一个 post 请求的代码：

```java
@PostMapping("/redirectPostToPost")
public ModelAndView redirectPostToPost(HttpServletRequest request) {
    request.setAttribute(
      View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
    return new ModelAndView("redirect:/redirectedPostToPost");
}复制
@PostMapping("/redirectedPostToPost")
public ModelAndView redirectedPostToPost() {
    return new ModelAndView("redirection");
}复制
```

***现在我们将使用curl\*命令测试 POST 的重定向**：

```bash
curl -L --verbose -X POST http://localhost:8080/spring-rest/redirectPostToPost复制
```

我们被重定向到指定位置：

```bash
> POST /redirectedPostToPost HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.49.0
> Accept: */*
> 
< HTTP/1.1 200 
< Content-Type: application/json;charset=UTF-8
< Transfer-Encoding: chunked
< Date: Tue, 08 Aug 2017 07:33:00 GMT

{"id":1,"content":"redirect completed"}复制
```

## 9.带参数转发

现在让我们考虑一个场景，我们希望将一些参数发送到另一个带有*前向前缀的**RequestMapping*。

在这种情况下，**我们可以使用\*HttpServletRequest\*在调用之间传递参数。**

这是一个*forwardWithParams*方法，需要将*param1*和*param2*发送到另一个映射*forwardedWithParams*：

```java
@RequestMapping(value="/forwardWithParams", method = RequestMethod.GET)
public ModelAndView forwardWithParams(HttpServletRequest request) {
    request.setAttribute("param1", "one");
    request.setAttribute("param2", "two");
    return new ModelAndView("forward:/forwardedWithParams");
}复制
```

事实上，映射*forwardedWithParams*可以存在于一个全新的控制器中，不需要在同一个控制器中：

```java
@RequestMapping(value="/forwardWithParams", method = RequestMethod.GET)
@Controller
@RequestMapping("/")
public class RedirectParamController {

    @RequestMapping(value = "/forwardedWithParams", method = RequestMethod.GET)
    public RedirectView forwardedWithParams(
      final RedirectAttributes redirectAttributes, HttpServletRequest request) {
        redirectAttributes.addAttribute("param1", request.getAttribute("param1"));
        redirectAttributes.addAttribute("param2", request.getAttribute("param2"));

        redirectAttributes.addAttribute("attribute", "forwardedWithParams");
        return new RedirectView("redirectedUrl");
    }
}复制
```

为了说明，让我们试试这个*curl*命令：

```bash
curl -i http://localhost:8080/spring-rest/forwardWithParams复制
```

结果如下：

```bash
HTTP/1.1 302 Found
Date: Fri, 19 Feb 2021 05:37:14 GMT
Content-Language: en-IN
Location: http://localhost:8080/spring-rest/redirectedUrl?param1=one¶m2=two&attribute=forwardedWithParams
Content-Length: 0
复制
```

正如我们所见，*param1*和*param2*从第一个控制器传输到第二个控制器。最后，它们出现在*forwardedWithParams*指向的名为*redirectedUrl的重定向中。*

## **10.结论**

本文说明了**在 Spring 中实现重定向的三种不同方法**，执行这些重定向时如何处理/传递属性以及如何处理 HTTP POST 请求的重定向。