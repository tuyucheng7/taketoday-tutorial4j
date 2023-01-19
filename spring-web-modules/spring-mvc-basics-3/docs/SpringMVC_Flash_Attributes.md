## 1. 概述

Web应用程序通常依赖于用户输入来满足它们的几个用例。
因此，表单提交是收集和处理此类应用程序数据的常用机制。

在本教程中，我们介绍Spring的flash属性如何帮助我们安全可靠地完成表单提交工作流。

## 2. Flash属性基础

首先，我们需要对表单提交工作流和一些关键的相关概念有一定了解。

### 2.1 Post/Redirect/Get模式

设计Web表单的一种简单方法是使用单个HTTP POST请求来处理提交并通过其响应返回确认。
但是，这样的设计暴露了重复处理POST请求的风险，不能防止用户最终刷新页面。

**为了减轻重复处理的问题，我们可以将工作流创建为按特定顺序互连的请求序列，即POST、REDIRECT和GET**。
简而言之，我们称之为表单提交的Post/Redirect/Get(PRG)模式。

在接收到POST请求后，服务器对其进行处理，然后转移控制权并发出GET请求。
随后，根据GET请求的响应显示确认页面。理想情况下，即使最后一次GET请求被多次尝试，也不会有任何不利的副作用。

### 2.2 Flash属性的生命周期

要使用PRG模式完成表单提交，我们需要将信息从初始POST请求传输到重定向后的最终GET请求。

不幸的是，我们既不能使用RequestAttributes也不能使用SessionAttributes。
这是因为前者无法在不同控制器之间进行重定向，而后者即使在表单提交结束后也会持续整个会话。

但是，我们不需要担心，因为Spring的web框架提供了可以解决这个确切问题的flash属性。

让我们看看RedirectAttributes接口中的方法，这些方法可以帮助我们在项目中使用flash属性：

```java
public interface RedirectAttributes extends Model {

    RedirectAttributes addFlashAttribute(String attributeName, @Nullable Object attributeValue);

    RedirectAttributes addFlashAttribute(Object attributeValue);

    Map<String, ?> getFlashAttributes();
}
```

**Flash属性是短暂的**，因此，在重定向之前，这些数据被临时存储在一些底层存储中。
它们在重定向后仍然可用于后续请求，然后它们就消失了。

### 2.3 FlashMap数据结构

Spring提供了一个名为FlashMap的抽象数据结构，用于将flash属性存储为键值对。

下面是FlashMap类的定义：

```java
public final class FlashMap extends HashMap<String, Object> implements Comparable<FlashMap> {

    @Nullable
    private String targetRequestPath;

    private final MultiValueMap<String, String> targetRequestParams = new LinkedMultiValueMap<>(3);

    private long expirationTime = -1;
}
```

我们可以注意到FlashMap类继承了HashMap类的行为，因此，**FlashMap实例可以存储属性的键值对**。
此外，我们可以将FlashMap实例绑定为仅由特定重定向URL使用。

此外，每个请求都有两个FlashMap实例，即Input FlashMap和Output FlashMap，它们在PRG模式中起着重要作用：

+ POST请求中使用Output FlashMap临时保存flash属性，并在重定向后发送到下一个GET请求。
+ Input FlashMap用于在最终的GET请求中访问重定向之前由上一个POST请求发送的只读flash属性。

### 2.4 FlashMapManager和RequestContextUtils

顾名思义，我们可以使用FlashMapManager来管理FlashMap实例。

首先让我们看看这个策略接口的定义：

```java
public interface FlashMapManager {

    @Nullable
    FlashMap retrieveAndUpdate(HttpServletRequest request, HttpServletResponse response);

    void saveOutputFlashMap(FlashMap flashMap, HttpServletRequest request, HttpServletResponse response);
}
```

简单地说，FlashMapManager允许我们在一些底层存储中读取、更新和保存FlashMap实例。

接下来，让我们熟悉一下RequestContextUtils抽象工具类中可用的一些静态方法。

为了将我们的重点放在本教程的范围内，这里只列出与flash属性相关的方法：

```java
public abstract class RequestContextUtils {

    public static Map<String, ?> getInputFlashMap(HttpServletRequest request);

    public static FlashMap getOutputFlashMap(HttpServletRequest request);

    public static FlashMapManager getFlashMapManager(HttpServletRequest request);

    public static void saveOutputFlashMap(String location, HttpServletRequest request, HttpServletResponse response);
}
```

我们可以使用这些方法来检索input/output FlashMap实例，获取请求的FlashMapManager，并保存FlashMap实例。

## 3. 表单提交用例

到目前为止，我们对Flash属性的不同概念有了基本的了解，现在通过一个例子演示它们的使用。

我们的诗歌比赛应用程序有一个简单的用例，通过提交表单接收来自不同诗人的诗歌条目。
此外，参赛作品将包含与诗歌相关的必要信息，例如标题、正文和作者姓名。

### 3.1 Thymeleaf配置

我们将使用Thymeleaf，这是一个Java模板引擎，用于通过简单的HTML模板创建动态网页。

首先，我们需要将spring-boot-starter-thymeleaf依赖添加到我们项目的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
    <version>2.6.1</version>
</dependency>
```

接下来，我们可以在位于src/main/resources目录中的application.properties文件中定义一些Thymeleaf特定的属性：

```properties
spring.thymeleaf.cache=false
spring.thymeleaf.enabled=true
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
```

定义了这些属性后，我们可以在/src/main/resources/templates目录下创建所有视图。
然后，Spring会将.html后缀附加到我们控制器内命名的所有视图。

### 3.2 域模型

接下来，我们定义一个Poem模型类：

```java
@Getter
@Setter
public class Poem {
    private String title;
    private String author;
    private String body;
}
```

此外，我们可以在Poem类中添加isValidPoem()静态方法，用于校验字段不允许为空字符串：

```java
public class Poem {

    public static boolean isValidPoem(Poem poem) {
        return poem != null && Strings.isNotBlank(poem.getAuthor()) && Strings.isNotBlank(poem.getBody()) && Strings.isNotBlank(poem.getTitle());
    }
}
```

### 3.3 创建表单

现在，我们准备开始创建提交表单。为此，**我们需要一个端点“/poem/submit”来提供GET请求以向用户显示表单**：

```java
@Controller
public class PoemSubmission {

    @GetMapping("/poem/submit")
    public String submitGet(Model model) {
        model.addAttribute("poem", new Poem());
        return "submit";
    }
}
```

在这里，我们使用Model作为容器来保存用户提供的特定于Poem的数据。
此外，submitGet方法返回一个由“submit”视图提供的视图。

并且我们希望将POST表单与模型属性poem绑定：

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Poetry Contest: Submission</title>
</head>
<body>
<form action="#" method="post" th:action="@{/poem/submit}" th:object="${poem}">
    <!-- form fields for poem title, body, and author -->
</form>
</body>
</html>
```

### 3.4 Post/Redirect/Get提交流

现在，让我们为表单启用POST操作。为此，我们将**在PoemSubmission控制器中创建“/poem/submit”端点来响应POST请求**：

```java
@Controller
public class PoemSubmission {

    @PostMapping("/poem/submit")
    public RedirectView submitPost(HttpServletRequest request, @ModelAttribute Poem poem, RedirectAttributes redirectAttributes) {
        if (Poem.isValidPoem(poem)) {
            redirectAttributes.addFlashAttribute("poem", poem);
            return new RedirectView("/poem/success", true);
        } else {
            return new RedirectView("/poem/submit", true);
        }
    }
}
```

我们可以注意到，**如果提交成功，则控制权转移到“/poem/success”端点**。
此外，在启动重定向之前，我们将poem数据添加为flash属性。

现在，我们需要向用户显示一个确认页面，因此让我们实现“/point/success”端点的功能，它将为GET请求提供服务：

```java
@Controller
public class PoemSubmission {

    @GetMapping("/poem/success")
    public String getSuccess(HttpServletRequest request) {
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
        if (inputFlashMap != null) {
            Poem poem = (Poem) inputFlashMap.get("poem");
            return "success";
        } else {
            return "redirect:/poem/submit";
        }
    }
}
```

**这里需要注意的是，在决定重定向到成功页面之前，我们需要验证FlashMap**。

最后，我们使用成功页面中的flash属性poem来显示用户提交的诗歌的标题：

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Poetry Contest: Thank You</title>
</head>
<body>
<h1 th:if="${poem}">
    <p th:text="${'You have successfully submitted poem titled - '+ poem?.title}"/>
    Click <a th:href="@{/poem/submit}"> here</a> to submit more.
</h1>
<h1 th:unless="${poem}">
    Click <a th:href="@{/poem/submit}"> here</a> to submit a poem.
</h1>
</body>
</html>
```

## 4. 总结

在本教程中，我们介绍了一些关于Post/Redirect/Get模式和flash属性的概念。
并且，我们通过一个简单的表单提交演示了flash属性的用法。