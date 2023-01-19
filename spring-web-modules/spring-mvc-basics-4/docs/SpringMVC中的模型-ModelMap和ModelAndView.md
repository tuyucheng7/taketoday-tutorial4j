## 1. 概述

在本文中，我们将了解Spring MVC 提供的核心org.springframework.ui.Model 、org.springframework.ui.ModelMap和org.springframework.web.servlet.ModelAndView的使用。

## 2.Maven依赖

让我们从pom.xml文件中的spring-context依赖关系开始：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.2.2.RELEASE</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|ga|1|a%3A"spring-context")找到最新版本的 spring-context 依赖项。

对于ModelAndView，需要spring-web依赖项：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>5.2.2.RELEASE</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|ga|1|a%3A"spring-web")找到最新版本的 spring-web 依赖项。

而且，如果我们使用Thymeleaf作为我们的视图，我们应该将此依赖项添加到 pom.xml：

```xml
<dependency>
    <groupId>org.thymeleaf</groupId>
    <artifactId>thymeleaf-spring5</artifactId>
    <version>3.0.11.RELEASE</version>
</dependency>
```

可以在[此处](https://search.maven.org/search?q=a:thymeleaf-spring5 AND g:org.thymeleaf)找到最新版本的Thymeleaf依赖项。

## 3.型号

让我们从最基本的概念开始——模型。

简单地说，模型可以提供用于渲染视图的属性。

为了提供具有可用数据的视图，我们只需将这些数据添加到它的模型对象中。此外，具有属性的映射可以与模型实例合并：

```java
@GetMapping("/showViewPage")
public String passParametersWithModel(Model model) {
    Map<String, String> map = new HashMap<>();
    map.put("spring", "mvc");
    model.addAttribute("message", "Baeldung");
    model.mergeAttributes(map);
    return "viewPage";
}
```

## 4.模型图

就像上面的Model接口一样，ModelMap也用于传递值来渲染视图。

ModelMap的优点是它使我们能够传递值的集合并将这些值视为在Map中：

```java
@GetMapping("/printViewPage")
public String passParametersWithModelMap(ModelMap map) {
    map.addAttribute("welcomeMessage", "welcome");
    map.addAttribute("message", "Baeldung");
    return "viewPage";
}
```

## 5.模型和视图

将值传递给视图的最终接口是ModelAndView。

这个接口允许我们在一次返回中传递Spring MVC所需的所有信息：

```java
@GetMapping("/goToViewPage")
public ModelAndView passParametersWithModelAndView() {
    ModelAndView modelAndView = new ModelAndView("viewPage");
    modelAndView.addObject("message", "Baeldung");
    return modelAndView;
}

```

## 6. 风景

我们放置在这些模型中的所有数据都由视图使用——通常是模板化视图来呈现网页。

如果我们有一个Thymeleaf模板文件作为控制器方法的目标视图。通过模型传递的参数可以从 thymeleaf HTML 代码中访问：

```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Title</title>
</head>
<body>
    <div>Web Application. Passed parameter : th:text="${message}"</div>
</body>
</html>
```

此处传递的参数通过语法${message}使用，称为占位符。Thymeleaf 模板引擎将用通过模型传递的同名属性的实际值替换此占位符。

## 七. 总结

在本快速教程中，我们讨论了Spring MVC中的三个核心概念—— Model、ModelMap和ModelAndView。我们还查看了视图如何使用这些值的示例。