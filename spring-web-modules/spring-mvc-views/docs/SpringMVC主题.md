## 1. 概述

在设计Web应用程序时，其外观或主题是一个关键组件。它会影响我们应用程序的可用性和可访问性，并可以进一步树立我们公司的品牌。

在本教程中，我们将完成在[Spring MVC应用程序](https://www.baeldung.com/spring-mvc-tutorial)中配置主题所需的步骤。

## 2. 用例

简而言之，主题是一组静态资源，通常是样式表和图像，它们会影响我们的Web应用程序的视觉风格。

我们可以使用主题来：

-   建立具有固定主题的通用外观
-   为具有品牌主题的品牌定制，这在SAAS应用程序中很常见，每个客户都希望获得不同的外观和感觉
-   使用可用性主题解决可访问性问题，例如，我们可能需要深色或高对比度主题

## 3. Maven依赖

所以，首先，让我们添加我们将在本教程的第一部分中使用的Maven依赖项。

我们需要[Spring WebMVC](https://search.maven.org/search?q=a:spring-webmvc)和[Spring Context](https://search.maven.org/artifact/spring/spring-context)依赖项：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>5.2.1.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.2.1.RELEASE</version>
</dependency>
```

由于我们要在示例中使用JSP，因此我们需要[Java Servlets](https://search.maven.org/search?q=a:javax.servlet-api)、[JSP](https://search.maven.org/search?q=a:javax.servlet.jsp-api)和[JSTL](https://search.maven.org/search?q=a:jstl)：

```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>4.0.1</version>
</dependency>
<dependency>
     <groupId>javax.servlet.jsp</groupId>
     <artifactId>javax.servlet.jsp-api</artifactId>
     <version>2.3.3</version>
</dependency>
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>jstl</artifactId>
    <version>1.2</version>
</dependency>
```

## 4. 配置Spring主题

### 4.1 主题属性

现在，让我们为我们的应用程序配置浅色和深色主题。

对于深色主题，让我们创建dark.properties：

```properties
styleSheet=themes/black.css
background=black
```

对于浅色主题，light.properties：

```properties
styleSheet=themes/white.css
background=white
```

从上面的属性中，我们注意到一个是指一个CSS文件，另一个是指一个CSS样式。我们稍后会看到这些在我们的观点中是如何体现的。

### 4.2 ResourceHandler

阅读上面的属性，文件black.css和white.css必须放在名为/themes的目录中。

而且，我们必须配置ResourceHandler以使Spring MVC能够在请求时正确定位文件：

```java
@Override 
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/themes/**").addResourceLocations("classpath:/themes/");
}
```

### 4.3 ThemeSource

我们可以通过ResourceBundleThemeSource将这些特定于主题的.properties文件作为[ResourceBundle]((https://www.baeldung.com/java-resourcebundle))进行管理：

```java
@Bean
public ResourceBundleThemeSource resourceBundleThemeSource() {
    return new ResourceBundleThemeSource();
}
```

### 4.4 ThemeResolver

接下来，我们需要一个ThemeResolver来解析应用程序的正确主题。根据我们的设计需求，我们可以在现有实现之间进行选择或创建我们自己的实现。

对于我们的示例，让我们配置CookieThemeResolver。顾名思义，这会从浏览器cookie中解析主题信息，或者在该信息不可用时回退到默认值：

```java
@Bean
public ThemeResolver themeResolver() {
    CookieThemeResolver themeResolver = new CookieThemeResolver();
    themeResolver.setDefaultThemeName("light");
    return themeResolver;
}
```

框架附带的ThemeResolver的其他变体是：

-   FixedThemeResolver：当应用程序有固定主题时使用
-   SessionThemeResolver：用于允许用户切换活动会话的主题

### 4.5 视图

为了将主题应用到我们的视图中，我们必须配置一种机制来查询资源包。

我们将只保留JSP的范围，尽管也可以为备用视图呈现引擎配置类似的查找机制。

对于JSP，我们可以导入一个标签库来为我们完成这项工作：

```html
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
```

然后我们可以引用指定适当属性名称的任何属性：

```html
<link rel="stylesheet" href="<spring:theme code='styleSheet'/>"/>
```

或者：

```html
<body bgcolor="<spring:theme code='background'/>">
```

所以，现在让我们在我们的应用程序中添加一个名为index.jsp的视图，并将其放在WEB-INF/目录中：

```html
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="<spring:theme code='styleSheet'/>"/>
    <title>Themed Application</title>
</head>
<body>
<header>
    <h1>Themed Application</h1>
    <hr/>
</header>
<section>
    <h2>Spring MVC Theme Demo</h2>
    <form action="<c:url value='/'/>" method="POST" name="themeChangeForm" id="themeChangeForm">
        <div>
            <h4>
                Change Theme
            </h4>
        </div>
        <select id="theme" name="theme" onChange="submitForm()">
            <option value="">Reset</option>
            <option value="light">Light</option>
            <option value="dark">Dark</option>
        </select>
    </form>
</section>

<script type="text/javascript">
    function submitForm() {
        document.themeChangeForm.submit();
    }
</script>
</body>
</html>
```

实际上，我们的应用程序此时可以正常工作，始终选择我们的浅色主题。

让我们看看我们如何允许用户更改他们的主题。

### 4.6 ThemeChangeInterceptor

ThemeChangeInterceptor的工作是了解主题更改请求。

现在让我们添加一个ThemeChangeInterceptor并将其配置为查找主题请求参数：

```java
@Override
public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(themeChangeInterceptor());
}

@Bean
public ThemeChangeInterceptor themeChangeInterceptor() {
    ThemeChangeInterceptor interceptor = new ThemeChangeInterceptor();
    interceptor.setParamName("theme");
    return interceptor;
}
```

## 5. 进一步的依赖

接下来，让我们实现我们自己的ThemeResolver，将用户的偏好存储到数据库中。

为此，我们需要[Spring Security](https://search.maven.org/search?q=a:spring-security-web)来识别用户：

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-web</artifactId>
    <version>5.2.1.RELEASE</version>
</dependency>

<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
    <version>5.2.1.RELEASE</version>
</dependency>

<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-taglibs</artifactId>
    <version>5.2.1.RELEASE</version>
</dependency>
```

以及用于存储用户偏好的[Spring Data](https://search.maven.org/search?q=a:spring-data-jpa-parent)、[Hibernate](https://search.maven.org/artifact/org.hibernate/hibernate-core)和[HSQLDB](https://search.maven.org/search?q=a:hsqldb)：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-jpa</artifactId>
    <version>2.2.2.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.4.9.Final</version>
</dependency>

<dependency>
    <groupId>org.hsqldb</groupId>
    <artifactId>hsqldb</artifactId>
    <version>2.5.0</version>
</dependency>
```

## 6. 自定义ThemeResolver

现在让我们更深入地研究ThemeResolver并实现我们自己的一个。这个自定义的ThemeResolver会将用户的主题偏好保存到数据库中。

为此，我们首先添加一个UserPreference实体：

```java
@Entity
@Table(name = "preferences")
public class UserPreference {
    @Id
    private String username;

    private String theme;
}
```

接下来，我们将创建UserPreferenceThemeResolver，它必须实现ThemeResolver接口。它的主要职责是解析和保存主题信息。

让我们首先通过实现UserPreferenceThemeResolver#resolveThemeName解决名称解析问题：

```java
@Override
public String resolveThemeName(HttpServletRequest request) {
    String themeName = findThemeFromRequest(request)
      .orElse(findUserPreferredTheme().orElse(getDefaultThemeName()));
    request.setAttribute(THEME_REQUEST_ATTRIBUTE_NAME, themeName);
    return themeName;
}

private Optional<String> findUserPreferredTheme() {
    Authentication authentication = SecurityContextHolder.getContext()
            .getAuthentication();
    UserPreference userPreference = getUserPreference(authentication).orElse(new UserPreference());
    return Optional.ofNullable(userPreference.getTheme());
}

private Optional<String> findThemeFromRequest(HttpServletRequest request) {
    return Optional.ofNullable((String) request.getAttribute(THEME_REQUEST_ATTRIBUTE_NAME));
}
    
private Optional<UserPreference> getUserPreference(Authentication authentication) {
    return isAuthenticated(authentication) ? 
      userPreferenceRepository.findById(((User) authentication.getPrincipal()).getUsername()) : 
      Optional.empty();
}
```

现在我们可以在UserPreferenceThemeResolver#setThemeName中编写用于保存主题的实现：

```java
@Override
public void setThemeName(HttpServletRequest request, HttpServletResponse response, String theme) {
    Authentication authentication = SecurityContextHolder.getContext()
        .getAuthentication();
    if (isAuthenticated(authentication)) {
        request.setAttribute(THEME_REQUEST_ATTRIBUTE_NAME, theme);
        UserPreference userPreference = getUserPreference(authentication).orElse(new UserPreference());
        userPreference.setUsername(((User) authentication.getPrincipal()).getUsername());
        userPreference.setTheme(StringUtils.hasText(theme) ? theme : null);
        userPreferenceRepository.save(userPreference);
    }
}
```

最后，让我们现在更改应用程序中的ThemeResolver：

```java
@Bean 
public ThemeResolver themeResolver() { 
    return new UserPreferenceThemeResolver();
}
```

现在，用户的主题偏好保存在数据库中，而不是作为cookie。

另一种保存用户偏好的方法是通过Spring MVC控制器和单独的API。

## 7. 总结

在本文中，我们了解了配置Spring MVC主题的步骤。