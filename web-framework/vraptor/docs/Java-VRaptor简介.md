## 1. 概述

在本文中，我们将了解[VRaptor](http://www.vraptor.org/en/)，这是一个简单明了的JavaMVC Web 框架，它利用Java上下文和依赖注入技术并且易于掌握。

就像 Spring——它在很大程度上依赖于注解并且与 Hibernate 一起工作得很好。

它还带有一些有用的插件——例如用于内部化和单元测试。

因此，让我们探索 VRaptor 的不同组件并创建一个示例项目。

## 2. Maven依赖和设置

启动和运行的一种快速方法是从[官方存储库下载](https://bintray.com/caelum/VRaptor4/br.com.caelum.vraptor/)vraptor-blank-project-distribution。

空白项目只是一个骨架，可以充实它成为一个成熟的 Web 应用程序选择。

下载并解压缩项目后，让我们将目录重命名为vraptor(或任何其他名称)。

该目录应包含：

-   来源/
-   pom.xml
-   和README.md

该项目基于 Maven 并附带tomcat7 Maven 插件，该插件提供用于运行应用程序的 servlet 容器。

它还带有一个默认的IndexController，它只有一个方法——index ()。

默认情况下，此方法要呈现的视图位于webapp/WEB-INF/jsp/index/index.jsp – 这遵循约定WEB-INF/jsp/ controller_name/method_name。

要启动服务器，我们将执行命令mvn tomcat7：从项目的根目录运行。

如果成功，我们访问http://localhost:8080，浏览器会显示“ It works!! 猛禽！“。

如果我们遇到“ java.lang.LinkageError: loader constraint violation”，那么，我们必须修改pom.xml中的以下依赖项：

```xml
<dependency>
    <groupId>org.jboss.weld.servlet</groupId>
    <artifactId>weld-servlet-core</artifactId>
    <version>2.1.2.Final</version>
    <exclusions>
        <exclusion>
	    <groupId>org.jboss.spec.javax.el</groupId>
	    <artifactId>jboss-el-api_3.0_spec</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.jboss.weld</groupId>
    <artifactId>weld-core-impl</artifactId>
    <version>2.1.2.Final</version>
    <exclusions>
       <exclusion>
          <groupId>org.jboss.spec.javax.el</groupId>
  	  <artifactId>jboss-el-api_3.0_spec</artifactId>
       </exclusion>
    </exclusions>
</dependency>
```

罪魁祸首是包含在具有编译范围的weld-servlet-core和weld-core-impl中的el-api；这会导致依赖冲突。

沿线将需要以下依赖项，因此让我们将它们包含在pom.xml中：

```xml
<dependency>
    <groupId>br.com.caelum.vraptor</groupId>
    <artifactId>vraptor-freemarker</artifactId>
    <version>4.1.0-RC3</version>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.8-dmr</version>
</dependency>

<dependency>
    <groupId>org.freemarker</groupId>
    <artifactId>freemarker</artifactId>
    <version>2.3.27-incubating</version>
</dependency>
```

可以在 Maven Central 中找到最新版本的[vraptor-freemarker、](https://search.maven.org/classic/#search|gav|1|g%3A"br.com.caelum.vraptor" AND a%3A"vraptor-freemarker") [mysql-connector-java](https://search.maven.org/classic/#search|gav|1|g%3A"mysql" AND a%3A"mysql-connector-java")和[freemarker工件。](https://search.maven.org/classic/#search|gav|1|g%3A"org.freemarker" AND a%3A"freemarker")

现在一切顺利，让我们构建一个简单的博客站点。

## 3.休眠支持

VRaptor 提供了各种与数据库交互的插件，其中之一是与 Hibernate 4 一起工作的vraptor-hibernate 。

该插件使 Hibernate 的SessionFactory bean 在运行时通过 CDI 可用。

有了插件，我们需要一个标准的 Hibernate 配置文件——一个示例可以在存储库中找到。

VRaptor 使用一种称为 Producers 的技术来使对象可用于 DI 管理。有关此的更多详细信息，[请参见此处](http://www.vraptor.org/en/docs/components/)。

## 4.在 VRaptor 中定义 Web 路由

在 VRaptor 中，路由定义驻留在控制器中，这些控制器只是@Controller注解的Java 对象——就像在 Spring 中一样。

@Path注解用于将请求路径映射到特定控制器，@Get、@Post、@Put、@Delete和@Patch注解用于指定 HTTP 请求类型。

路由映射配置看起来类似于 JAX-RS 的方式，但没有正式实现标准。

此外，在定义路径时，可以在花括号中指定路径变量：

```java
@Get("/posts/{id}")
```

然后可以在控制器方法中访问id的值：

```java
@Get("/posts/{id}")
public void view(int id) {
    // ...
}
```

当表单被提交到特定路径时，VRaptor 可以自动使用提交的表单数据填充对象。

让我们在本文的下一节中看看实际情况。

## 5. 视图和模板引擎

默认情况下，可以使用 JSP 实现视图。但是，也可以使用其他模板引擎——在本文中，我们将使用 Freemarker。

让我们从创建index.ftl 并将其保存在默认视图目录 (src/main/resources/templates) 开始：

```html
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>VRaptor Blank Project</title>
</head>
<body>
It works!! ${variable}
</body>
</html>
```

现在，我们可以使用带有FreemarkerView类的定义视图来进行视图渲染：

```java
@Path("/")
public void index() {
    result.include("variable", "VRaptor!");
    result.use(FreemarkerView.class).withTemplate("index");
}
```

结果对象保存模型状态——它有重定向到另一个页面、URL 或控制器方法的方法；它可以使用 CDI 注入到控制器中。

在我们的示例中，变量由 Freemarker 解析。因此index.ftl中的${variable}占位符被替换为“VRaptor！”。

[此处](http://www.vraptor.org/en/docs/view-and-ajax/)记录了更高级的用法。

## 6. 表单提交处理示例

让我们看看如何处理带有验证的表单提交：

```java
@Post("/post/add")
public void add(Post post) {
    post.setAuthor(userInfo.getUser());
    validator.validate(post);
    if(validator.hasErrors()) {
        result.include("errors", validator.getErrors());
    }
    validator.onErrorRedirectTo(this).addForm();
  
    Object id = postDao.add(post);
  
    if(Objects.nonNull(id)) {
       result.include("status", "Post Added Successfully");
         result.redirectTo(IndexController.class).index();
    } else {
        result.include(
          "error", "There was an error creating the post. Try Again");
        result.redirectTo(this).addForm();
    }
}
```

Post对象首先使用[Java bean 验证](https://www.baeldung.com/javax-validation)进行验证，然后使用postDao.add()持久化到数据库中。

Post对象的字段是根据提交的表单数据的值自动填充的——对应于视图文件中表单的输入字段。

请注意，输入字段的名称必须以小写的对象名称为前缀。

例如，负责添加新帖子的视图具有输入字段：post.title和post.post，它们对应于Post中的字段title和post。分别是java：

```html
<input type="text" class="form-control" placeholder="Title" 
  id="title" name="post.title" required />

<textarea rows="10" class="form-control" placeholder="Post" 
  id="post" name="post.post" required></textarea>
```

完整的add.ftl文件可以在源代码中找到。

如果表单提交有错误，错误消息会被包含进来，用户会被重定向到相同的add()方法：

```java
if(validator.hasErrors()) {
    result.include("errors", validator.getErrors());
}
validator.onErrorRedirectTo(this).addForm();
```

## 七. 总结

总之，我们已经快速了解了 VRaptor 并了解了如何实现基本的 MVC 功能。

该[文档](http://www.vraptor.org/en/docs/one-minute-guide/)包含有关框架以及可用插件的更多详细信息。