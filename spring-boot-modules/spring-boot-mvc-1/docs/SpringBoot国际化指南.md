## 1. 概述

在本快速教程中，我们将了解如何**将国际化支持添加到Spring Boot应用程序中**。

## 2. Maven依赖

对于开发，我们需要以下依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
    <version>1.5.2.RELEASE</version>
</dependency>
```

可以从Maven Central下载最新版本的[spring-boot-starter-thymeleaf](https://search.maven.org/search?q=a:spring-boot-starter-thymeleaf)。

## 3. LocaleResolver

为了让我们的应用程序能够确定当前正在使用哪个语言环境，我们需要添加一个LocaleResolver bean：

```java
@Bean
public LocaleResolver localeResolver() {
    SessionLocaleResolver slr = new SessionLocaleResolver();
    slr.setDefaultLocale(Locale.CHINA);
    return slr;
}
```

LocaleResolver接口具有根据会话、cookie、Accept-Language标头或固定值确定当前语言环境的实现。

在我们的示例中，我们使用了基于会话的解析器SessionLocaleResolver并设置了值为CHINA的默认语言环境。

## 4. LocaleChangeInterceptor

接下来，我们需要添加一个拦截器bean，它将根据附加到请求的lang参数的值切换到新的语言环境：

```java
@Bean
public LocaleChangeInterceptor localeChangeInterceptor() {
    LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
    lci.setParamName("lang");
    return lci;
}
```

**为了生效，需要将此bean添加到应用程序的拦截器注册表中**。

为此，我们的@Configuration类必须实现WebMvcConfigurer接口并覆盖addInterceptors()方法：

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(localeChangeInterceptor());
}
```

## 5. 定义消息源

默认情况下，Spring Boot应用程序将在src/main/resources文件夹中查找包含国际化键和值的消息文件。

通常，每个语言环境的文件将命名为messages_XX.properties，其中XX是语言环境代码，我们还可以定义一个默认文件messages.properties。

**默认语言环境是当请求的语言环境不可用时默认使用的语言环境，或者为空**。

**另一方面，默认文件是在语言环境转换失败时查找属性的地方**。

如果特定语言环境文件中不存在键，则应用程序将简单地回退到默认文件。

将被本地化的值的键在每个文件中必须相同，并具有适合于它们对应的语言的值。

例如，让我们在默认文件messages.properties中定义中文属性：

```properties
email.notempty=请提供有效的邮箱id
greeting=你好！欢迎来到我们的网站！
lang.change=更换语言
lang.eng=英文
lang.ch=中文
```

接下来，让我们使用相同的键为英文创建一个名为messages_en.properties的文件：

```properties
email.notempty=Please provide valid email id.
greeting=Hello! Welcome to our website!
lang.change=Change the language
lang.eng=English
lang.ch=Chinese
```

## 6. 控制器和HTML页面

让我们创建一个控制器映射，它将返回一个名为international.html的简单HTML页面，我们希望以两种不同的语言显示该页面：

```java
@Controller
public class PageController {

    @GetMapping("/international")
    public String getInternationalPage() {
        return "international";
    }
}
```

由于我们使用thymeleaf来显示HTML页面，因此将使用语法为#{key}的模板访问特定于区域设置的值：

```html
<h1 th:text="#{greeting}"></h1>
```

如果使用JSP文件，则语法为：

```html
<h1><spring:message code="greeting" text="default"/></h1>
```

如果我们想访问具有两个不同区域设置的页面，我们必须以以下形式将参数lang添加到URL：/international?lang=en

如果URL上没有lang参数，应用程序将使用默认语言环境，在我们的例子中是中文语言环境。

让我们在我们的HTML页面中添加一个下拉列表，其中包含两个区域设置，它们的名称也在我们的属性文件中进行了国际化：

```html
<span th:text="#{lang.change}"></span>:
<select id="locales">
    <option value=""></option>
    <option value="en" th:text="#{lang.eng}"></option>
    <option value="ch" th:text="#{lang.ch}"></option>
</select>
```

然后我们可以添加一个jQuery脚本，该脚本将根据选择的下拉选项使用相应的lang参数调用/international URL：

```html
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js">
</script>
<script type="text/javascript">
    $(document).ready(function () {
        $("#locales").change(function () {
            var selectedOption = $('#locales').val();
            if (selectedOption != '') {
                window.location.replace('international?lang=' + selectedOption);
            }
        });
    });
</script>
```

## 7. 运行应用程序

为了初始化我们的应用程序，我们必须添加带有@SpringBootApplication注解的主类：

```java
@SpringBootApplication
public class InternationalizationApp {

    public static void main(String[] args) {
        SpringApplication.run(InternationalizationApp.class, args);
    }
}
```

根据所选的区域设置，我们将在运行应用程序时以中文或英文查看页面。

我们来看中文版：

<img src="../assets/img_6.png">

现在让我们看看英文版：

<img src="../assets/img_7.png">

## 8. 总结

在本教程中，我们展示了如何在Spring Boot应用程序中使用对国际化的支持。