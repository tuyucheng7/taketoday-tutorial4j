## **一、概述**

本文讨论如何**在 REST API 的相同 URI 结构上设置基本和摘要式身份验证**。在上一篇文章中，我们讨论了另一种保护 REST 服务的方法——[基于表单的身份验证](https://www.baeldung.com/securing-a-restful-web-service-with-spring-security)，因此基本和摘要式身份验证是自然的选择，也是更符合 REST 风格的方法。

## **2.基本认证的配置**

基于表单的身份验证对于 RESTful 服务不理想的主要原因是 Spring Security 将**使用会话**——这当然是服务器上的状态，因此**REST 中的无状态约束**实际上被忽略了。

我们将从设置基本身份验证开始——首先我们从主要的*<http>*安全元素中删除旧的自定义入口点和过滤器：

```xml
<http create-session="stateless">
   <intercept-url pattern="/api/admin/**" access="ROLE_ADMIN" />

   <http-basic />
</http>复制
```

请注意如何使用单个配置行添加对基本身份验证的支持 - *<http-basic />* - 处理 BasicAuthenticationFilter*和*BasicAuthenticationEntryPoint*的*创建和连接。

### **2.1. 满足无状态约束——摆脱会话**

RESTful 架构风格的主要限制之一是客户端-服务器通信是完全**无状态的**，正如[原始论文](http://www.ics.uci.edu/~fielding/pubs/dissertation/rest_arch_style.htm)所述：

>   **5.1.3 无状态**
>
>   接下来，我们向客户端-服务器交互添加一个约束：通信本质上必须是无状态的，如第 3.4.3 节（图 5-3）的客户端-无状态-服务器 (CSS) 样式，这样客户端的每个请求到服务器必须包含理解请求所需的所有信息，并且不能利用服务器上存储的任何上下文。**因此会话状态完全保存在客户端**。

**服务端Session**的概念在Spring Security中由来已久，直到现在都很难完全去掉，尤其是在使用命名空间配置的情况下。

但是，Spring Security使用**新的*****无状态\*****选项来**[增强](https://jira.springsource.org/browse/SEC-1424)命名空间配置以创建会话，这有效地保证了 Spring 不会创建或使用任何会话。这个新选项的作用是完全从安全过滤器链中删除所有与会话相关的过滤器，确保为每个请求执行身份验证。

## **3.配置摘要认证**

从前面的配置开始，设置摘要式身份验证所需的过滤器和入口点将被定义为 beans。然后，**摘要入口点将覆盖***<http-basic>*在幕后创建的入口点。最后，自定义**摘要过滤器**将在安全过滤器链中引入，使用安全命名空间的*后语义将其直接定位在基本身份验证过滤器之后。*

```xml
<http create-session="stateless" entry-point-ref="digestEntryPoint">
   <intercept-url pattern="/api/admin/**" access="ROLE_ADMIN" />

   <http-basic />
   <custom-filter ref="digestFilter" after="BASIC_AUTH_FILTER" />
</http>

<beans:bean id="digestFilter" class=
 "org.springframework.security.web.authentication.www.DigestAuthenticationFilter">
   <beans:property name="userDetailsService" ref="userService" />
   <beans:property name="authenticationEntryPoint" ref="digestEntryPoint" />
</beans:bean>

<beans:bean id="digestEntryPoint" class=
 "org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint">
   <beans:property name="realmName" value="Contacts Realm via Digest Authentication"/>
   <beans:property name="key" value="acegi" />
</beans:bean>

<authentication-manager>
   <authentication-provider>
      <user-service id="userService">
         <user name="eparaschiv" password="eparaschiv" authorities="ROLE_ADMIN" />
         <user name="user" password="user" authorities="ROLE_USER" />
      </user-service>
   </authentication-provider>
</authentication-manager>复制
```

不幸的是，安全命名空间不[支持](https://jira.springsource.org/browse/SEC-1860)自动配置摘要式身份验证，就像使用*<http-basic>*配置基本身份验证一样。因此，必须定义必要的 bean 并将其手动连接到安全配置中。

## **4. 在同一个 Restful 服务中支持两种身份验证协议**

Basic 或 Digest 身份验证可以在 Spring Security 中轻松实现；它在相同的 URI 映射上为相同的 RESTful Web 服务支持它们，这给服务的配置和测试带来了新的复杂性。

### **4.1. 匿名请求**

**使用安全链中的基本过滤器和摘要过滤器，匿名请求的**方式——不包含身份验证凭证（*授权*HTTP 标头）的请求——由 Spring Security 处理——两个身份验证过滤器将找不到**凭证**并将继续执行过滤器链。然后，查看请求是如何未通过身份验证的，抛出*AccessDeniedException并在**ExceptionTranslationFilter*中捕获，它启动摘要入口点，提示客户端输入凭据。

基本过滤器和摘要过滤器的职责都非常狭窄——如果它们无法识别请求中的身份验证凭证类型，它们将继续执行安全过滤器链。正是因为如此，Spring Security 才可以灵活配置，在同一个 URI 上支持多种认证协议。

当发出包含正确身份验证凭据（基本或摘要）的请求时，该协议将被正确使用。但是，对于匿名请求，客户端只会收到摘要身份验证凭据的提示。这是因为摘要入口点被配置为 Spring Security 链的主要和单一入口点；因此**摘要认证可以被认为是默认的**。

### **4.2. 请求身份验证凭据**

**具有基本身份验证凭据的请求**将由以前缀*“Basic”开头的**授权*标头标识。处理此类请求时，凭据将在基本身份验证过滤器中解码，请求将被授权。同样，带有 Digest 身份验证凭据的请求将使用前缀*“Digest”*作为其*授权*标头。

## **5. 测试两种场景**

在使用基本或摘要进行身份验证后，测试将通过创建新资源来使用 REST 服务：

```java
@Test
public void givenAuthenticatedByBasicAuth_whenAResourceIsCreated_then201IsReceived(){
   // Given
   // When
   Response response = given()
    .auth().preemptive().basic( ADMIN_USERNAME, ADMIN_PASSWORD )
    .contentType( HttpConstants.MIME_JSON ).body( new Foo( randomAlphabetic( 6 ) ) )
    .post( paths.getFooURL() );

   // Then
   assertThat( response.getStatusCode(), is( 201 ) );
}
@Test
public void givenAuthenticatedByDigestAuth_whenAResourceIsCreated_then201IsReceived(){
   // Given
   // When
   Response response = given()
    .auth().digest( ADMIN_USERNAME, ADMIN_PASSWORD )
    .contentType( HttpConstants.MIME_JSON ).body( new Foo( randomAlphabetic( 6 ) ) )
    .post( paths.getFooURL() );

   // Then
   assertThat( response.getStatusCode(), is( 201 ) );
}复制
```

**请注意，使用基本身份验证的测试会先发制人地**向请求添加凭据，而不管服务器是否已挑战身份验证。这是为了确保服务器不需要向客户端质询凭据，因为如果这样做，质询将针对摘要凭据，因为这是默认设置。

## **六，结论**

本文涵盖了 RESTful 服务的基本和摘要身份验证的配置和实现，主要使用 Spring Security 命名空间支持以及框架中的一些新功能。