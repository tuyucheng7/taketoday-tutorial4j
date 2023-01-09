## 1. 概述

在本文中，我们将了解Java中 Web 开发的一个核心方面——Servlet。

## 2. Servlet 和容器

简单地说，Servlet 是一个处理请求、处理它们并用响应回复的类。

例如，我们可以使用 Servlet 通过 HTML 表单收集用户的输入、从数据库中查询记录以及动态创建网页。

Servlet 受另一个称为Servlet 容器的Java应用程序的控制。当在 Web 服务器中运行的应用程序收到请求时，服务器将请求交给 Servlet 容器——后者又将其传递给目标 Servlet。

## 3.Maven依赖

要在我们的 Web 应用程序中添加 Servlet 支持，javax . 需要servlet-api依赖项：

```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>3.1.0</version>
</dependency>
```

[可以在此处](https://mvnrepository.com/artifact/javax.servlet/servlet-api)找到最新的 Maven 依赖项。

当然，我们还必须配置一个 Servlet 容器来部署我们的应用程序；[这是开始了解如何在 Tomcat 上部署 WAR 的好地方](https://www.baeldung.com/tomcat-deploy-war)。

## 4. Servlet 生命周期

让我们来看看定义 Servlet 生命周期的一组方法。

### 4.1. 热() 

init方法被设计为只调用一次。如果 servlet 的实例不存在，则 Web 容器：

1.  加载 servlet 类
2.  创建 servlet 类的实例
3.  通过调用init方法对其进行初始化

在 servlet 可以接收任何请求之前，init 方法必须成功完成。如果init方法抛出ServletException或未在 Web 服务器定义的时间段内返回，则 servlet 容器无法将 servlet 投入服务。

```java
public void init() throws ServletException {
    // Initialization code like set up database etc....
}
```

### 4.2. 服务() 

此方法仅在 servlet 的init()方法成功完成后调用。

容器调用service()方法来处理来自客户端的请求，解释 HTTP 请求类型(GET、POST、PUT、DELETE等)并调用doGet、doPost、doPut、doDelete等适当的方法。

```java
public void service(ServletRequest request, ServletResponse response) 
  throws ServletException, IOException {
    // ...
}
```

### 4.3. 破坏() 

由 Servlet 容器调用以使 Servlet 停止服务。

只有在 servlet 的服务方法中的所有线程都退出或超时时间过后，才会调用此方法。容器调用该方法后，不会再调用Servlet上的service方法。

```java
public void destroy() {
    // 
}
```

## 5. 示例 Servlet

首先，将[上下文根](https://www.baeldung.com/tomcat-root-application)从javax-servlets-1.0-SNAPSHOT更改为 / 添加：

```xml
<Context path="/" docBase="javax-servlets-1.0-SNAPSHOT"></Context>
```

在 $CATALINA_HOMEconfserver.xml 的Host标签下。

现在让我们建立一个使用表单处理信息的完整示例。

首先，让我们定义一个带有映射/calculateServlet的 servlet ，它将捕获表单发布的信息并使用[RequestDispatcher](https://docs.oracle.com/javaee/6/api/javax/servlet/RequestDispatcher.html)返回结果：

```java
@WebServlet(name = "FormServlet", urlPatterns = "/calculateServlet")
public class FormServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, 
      HttpServletResponse response)
      throws ServletException, IOException {

        String height = request.getParameter("height");
        String weight = request.getParameter("weight");

        try {
            double bmi = calculateBMI(
              Double.parseDouble(weight), 
              Double.parseDouble(height));
            
            request.setAttribute("bmi", bmi);
            response.setHeader("Test", "Success");
            response.setHeader("BMI", String.valueOf(bmi));

            request.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(request, response);
        } catch (Exception e) {
           request.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(request, response);
        }
    }

    private Double calculateBMI(Double weight, Double height) {
        return weight / (height  height);
    }
}
```

如上所示，用[@WebServlet](https://docs.oracle.com/javaee/6/api/javax/servlet/annotation/WebServlet.html)注解的类必须扩展[javax.servlet.http.HttpServlet](https://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpServlet.html)类。请务必注意，[@WebServlet](https://docs.oracle.com/javaee/6/api/javax/servlet/annotation/WebServlet.html)注解仅在JavaEE 6 之后可用。

@WebServlet注解在部署时由容器处理，相应的 servlet 在指定的 URL 模式下可用[。](https://docs.oracle.com/javaee/6/api/javax/servlet/annotation/WebServlet.html)值得注意的是，通过使用注解来定义 URL 模式，我们可以避免使用名为web.xml的 XML 部署描述符来进行 Servlet 映射。

如果我们希望在没有注解的情况下映射 Servlet，我们可以使用传统的web.xml代替：

```xml
<web-app ...>

    <servlet>
       <servlet-name>FormServlet</servlet-name>
       <servlet-class>com.root.FormServlet</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>FormServlet</servlet-name>
        <url-pattern>/calculateServlet</url-pattern>
    </servlet-mapping>

</web-app>
```

接下来，让我们创建一个基本的 HTML表单：

```html
<form name="bmiForm" action="calculateServlet" method="POST">
    <table>
        <tr>
            <td>Your Weight (kg) :</td>
            <td><input type="text" name="weight"/></td>
        </tr>
        <tr>
            <td>Your Height (m) :</td>
            <td><input type="text" name="height"/></td>
        </tr>
        <th><input type="submit" value="Submit" name="find"/></th>
        <th><input type="reset" value="Reset" name="reset" /></th>
    </table>
    <h2>${bmi}</h2>
</form>
```

最后——为了确保一切都按预期工作，我们还要编写一个快速测试：

```java
public class FormServletLiveTest {

    @Test
    public void whenPostRequestUsingHttpClient_thenCorrect() 
      throws Exception {

        HttpClient client = new DefaultHttpClient();
        HttpPost method = new HttpPost(
          "http://localhost:8080/calculateServlet");

        List<BasicNameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("height", String.valueOf(2)));
        nvps.add(new BasicNameValuePair("weight", String.valueOf(80)));

        method.setEntity(new UrlEncodedFormEntity(nvps));
        HttpResponse httpResponse = client.execute(method);

        assertEquals("Success", httpResponse
          .getHeaders("Test")[0].getValue());
        assertEquals("20.0", httpResponse
          .getHeaders("BMI")[0].getValue());
    }
}
```

## 6. Servlet、HttpServlet 和 JSP

了解Servlet 技术并不局限于 HTTP 协议很重要。

在实践中几乎总是如此，但[Servlet](https://docs.oracle.com/javaee/7/api/javax/servlet/Servlet.html)是一个通用接口，而[HttpServlet](https://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpServlet.html)是该接口的扩展——添加 HTTP 特定支持——例如doGet和doPost等。

最后，Servlet 技术也是许多其他 Web 技术的主要驱动力，例如[JSP – JavaServer Pages](https://www.baeldung.com/jsp)、Spring MVC 等。

## 七. 总结

在这篇快速文章中，我们介绍了JavaWeb 应用程序中 Servlet 的基础。