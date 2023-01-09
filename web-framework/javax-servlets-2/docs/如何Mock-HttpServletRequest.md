## 1. 概述

在本快速教程中，我们将了解几种模拟HttpServletRequest对象的方法。

首先，我们将从功能齐全的模拟类型开始——来自 Spring Test 库的[MockHttpServletRequest 。](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/mock/web/MockHttpServletRequest.html)然后，我们将了解如何使用两个流行的模拟库——Mockito 和 JMockit 进行测试。最后，我们将看到如何使用匿名子类进行测试。

## 2. 测试HttpServletRequest

当我们想要模拟[HttpServletRequest](https://tomcat.apache.org/tomcat-5.5-doc/servletapi/javax/servlet/http/HttpServletRequest.html)等客户端请求信息时，测试[Servlet](https://www.baeldung.com/tag/servlet/)可能会很棘手。此外，此接口定义了各种方法，并且有不同的方法可用于模拟这些方法。

让我们看看我们要测试的目标UserServlet类：

```java
public class UserServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");

        response.getWriter().append("Full Name: " + firstName + " " + lastName);
    }
}
```

要对doGet()方法进行单元测试，我们需要模拟请求和响应参数以模拟实际的运行时行为。

## 3. 使用Spring 的MockHttpServletRequest

[Spring-Test](https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html#integration-testing-overview)库提供了一个功能齐全的类[MockHttpServletRequest](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/mock/web/MockHttpServletRequest.html) ，它实现了HttpServletRequest接口。

虽然这个库主要用于测试 Spring 应用程序，但我们可以使用它的MockHttpServletRequest类而无需实现任何 Spring 特定的功能。换句话说，即使应用程序不使用 Spring，我们仍然可以拥有这种依赖性，只是为了模拟HttpServletRequest对象。

让我们将此[依赖](https://search.maven.org/search?q=a:spring-test AND g:org.springframework)项添加到 pom.xml 中：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-test</artifactId>
    <version>5.3.20</version>
    <scope>test</scope>
</dependency>
```

现在，让我们看看如何使用此类来测试UserServlet：

```java
@Test
void givenHttpServletRequest_whenUsingMockHttpServletRequest_thenReturnsParameterValues() throws IOException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setParameter("firstName", "Spring");
    request.setParameter("lastName", "Test");
    MockHttpServletResponse response = new MockHttpServletResponse();

    servlet.doGet(request, response);

    assertThat(response.getContentAsString()).isEqualTo("Full Name: Spring Test");
}
```

在这里，我们可以注意到没有涉及实际的模拟。我们已经使用了功能齐全的请求和响应对象，并仅用几行代码就测试了目标类。因此，测试代码是干净的、可读的和可维护的。 

## 4. 使用模拟框架

或者，模拟框架提供了一个干净简单的 API 来测试[模仿](https://www.baeldung.com/mockito-vs-easymock-vs-jmockit#3-mock-concepts-and-definition)原始对象运行时行为的模拟对象。

它们的一些优点是它们的可表达性和开箱即用的模拟静态和私有方法的能力。此外，我们可以避免模拟所需的大部分样板代码(与自定义实现相比)，而是专注于测试。

### 4.1. 使用 Mockito

[Mockito](https://www.baeldung.com/tag/mockito/)是一种流行的开源测试自动化框架，它在内部使用[Java 反射](https://www.baeldung.com/java-reflection)API 来创建模拟对象。

让我们开始将 mockito-core[依赖项](https://search.maven.org/search?q=mockito-core)添加到我们的pom.xml：

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>4.4.0</version>
    <scope>test</scope>
</dependency>
```

接下来，让我们看看如何从HttpServletRequest对象模拟getParameter()方法：

```java
@Test
void givenHttpServletRequest_whenMockedWithMockito_thenReturnsParameterValues() throws IOException {
    // mock HttpServletRequest & HttpServletResponse
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    // mock the returned value of request.getParameterMap()
    when(request.getParameter("firstName")).thenReturn("Mockito");
    when(request.getParameter("lastName")).thenReturn("Test");
    when(response.getWriter()).thenReturn(new PrintWriter(writer));

    servlet.doGet(request, response);

    assertThat(writer.toString()).isEqualTo("Full Name: Mockito Test");
}
```

### 4.2. 使用 JMockit

[JMockit](https://www.baeldung.com/jmockit-101)是一个模拟 API，提供有用的记录和验证语法(我们可以将它用于[JUnit](http://junit.org/junit4/) 和 [TestNG](http://testng.org/doc/index.html))。它是用于JavaEE 和基于 Spring 的应用程序的容器外集成测试库。让我们看看如何使用 JMockit模拟HttpServletRequest 。

首先，我们将jmockit [依赖项](https://search.maven.org/search?q=a:jmockit AND g:org.jmockit)添加到我们的项目中：

```java
<dependency> 
    <groupId>org.jmockit</groupId> 
    <artifactId>jmockit</artifactId> 
    <version>1.49</version>
    <scope>test</scope>
</dependency>
```

接下来我们继续进行测试类中的mock实现：

```java
@Mocked
HttpServletRequest mockRequest;
@Mocked
HttpServletResponse mockResponse;

@Test
void givenHttpServletRequest_whenMockedWithJMockit_thenReturnsParameterValues() throws IOException {
    new Expectations() {{
        mockRequest.getParameter("firstName"); result = "JMockit";
        mockRequest.getParameter("lastName"); result = "Test";
        mockResponse.getWriter(); result = new PrintWriter(writer);
    }};

    servlet.doGet(mockRequest, mockResponse);

    assertThat(writer.toString()).isEqualTo("Full Name: JMockit Test");
}
```

正如我们在上面看到的，仅通过几行设置，我们就成功地使用模拟HttpServletRequest对象测试了目标类。

因此，模拟框架可以节省我们大量的跑腿工作，并使编写单元测试的速度更快。相反，要使用模拟对象，需要了解模拟 API，而且通常需要一个单独的框架。

## 5. 使用匿名子类

一些项目可能有依赖约束或更喜欢直接控制他们自己的测试类实现。具体来说，这在自定义实现的可重用性很重要的较大 servlet 代码库的情况下可能很有用。在这些情况下，匿名类就派上用场了。

[匿名类](https://www.baeldung.com/java-anonymous-classes)是没有名称的内部类。此外，它们可以快速实施并提供对实际对象的直接控制。如果我们不想包含额外的测试依赖项，可以考虑这种方法。

现在，让我们创建一个实现HttpServletRequest接口的匿名子类，并用它来测试doGet()方法：

```java
public static HttpServletRequest getRequest(Map<String, String[]> params) {
    return new HttpServletRequest() {
        public Map<String, String[]> getParameterMap() {
            return params;
        }

        public String getParameter(String name) {
            String[] values = params.get(name);
            if (values == null || values.length == 0) {
                return null;
            }
            return values[0];
        }

        // More methods to implement
    }
};
```

接下来，让我们将这个请求传递给被测类：

```java
@Test
void givenHttpServletRequest_whenUsingAnonymousClass_thenReturnsParameterValues() throws IOException {
    final Map<String, String[]> params = new HashMap<>();
    params.put("firstName", new String[] { "Anonymous Class" });
    params.put("lastName", new String[] { "Test" });

    servlet.doGet(getRequest(params), getResponse(writer));

    assertThat(writer.toString()).isEqualTo("Full Name: Anonymous Class Test");
}
```

该解决方案的缺点是需要为所有抽象方法创建一个具有虚拟实现的匿名类。此外， 像HttpSession这样的嵌套对象 可能需要特定的实现。

## 六. 总结

在本文中，我们讨论了在为 servlet 编写单元测试时模拟 HttpServletRequest对象的几个选项。除了使用模拟框架之外，我们还看到使用MockHttpServletRequest类进行测试似乎比自定义实现更干净、更高效。