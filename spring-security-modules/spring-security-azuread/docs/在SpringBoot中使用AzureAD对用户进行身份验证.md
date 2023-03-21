## 1. 简介

在本教程中，我们将展示如何轻松使用AzureAD作为Spring Boot应用程序的身份提供者。

## 2. 概述

微软的AzureAD是一个全面的身份管理产品，全球许多组织都在使用它。它支持多种登录机制和控件，可为组织应用程序组合中的用户提供单点登录体验。

此外，与Microsoft的起源一样，AzureAD与现有的Active Directory安装很好地集成，许多组织已经将其用于企业网络中的身份和访问管理。这允许管理员向现有用户授予对应用程序的访问权限，并使用他们已经习惯的相同工具来管理他们的权限。

## 3. 集成AzureAD

从基于Spring Boot的应用程序的角度来看，AzureAD充当OIDC兼容的身份提供者。这意味着我们可以通过配置所需的属性和依赖项来将它与Spring Security一起使用。

为了说明AzureAD集成，我们将实现一个[机密客户端](https://www.rfc-editor.org/rfc/rfc6749#section-2.1)，其中用于访问代码交换的授权代码发生在服务器端。此流程永远不会向用户的浏览器公开访问令牌，因此它被认为比公共客户端替代方案更安全。

## 4. Maven依赖

我们首先为基于Spring Security的WebMVC应用程序添加所需的Maven依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
    <version>3.0.3</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>3.0.3</version>
</dependency>
```

这些依赖项的最新版本在Maven Central上可用：

-   [spring-boot-stater-oauth2-client](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-oauth2-client/3.0.4)
-   [spring-boot-starter-web](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-web/3.0.4)

## 5. 配置属性

接下来，我们将添加用于配置客户端所需的[Spring Security](https://www.baeldung.com/learn-spring-security-course)属性。一个好的做法是将这些属性放在专用的Spring Profile中，这样随着应用程序的增长，维护会更容易一些。我们将此Profile命名为azuread，以明确其目的。因此，我们将在application-azuread.yml文件中添加相关属性：

```yaml
spring:
    security:
        oauth2:
            client:
                provider:
                    azure:
                        issuer-uri: https://login.microsoftonline.com/your-tenant-id-comes-here/v2.0
                registration:
                    azure-dev:
                        provider: azure
                        #client-id: externally provided
                        #client-secret: externally provided         
                        scope:
                            - openid
                            - email
                            - profile
```

在provider部分，我们定义了一个azure提供者。AzureAD支持OIDC标准端点发现机制，因此我们唯一需要配置的属性是issuer-uri。

此属性具有双重用途：首先，它是客户端附加发现资源名称以获取要下载的实际URL的基本URI。其次，它还用于检查[JSON Web Token](https://www.rfc-editor.org/rfc/rfc7519)(JWT)的真实性。例如，身份提供者创建的JWT的iss claim必须与issuer-uri值相同。

对于AzureAD，issuer-uri始终采用https://login.microsoftonline.com/my-tenant-id/v2.0格式，其中my-tenant-id是你的租户的标识符。

在registration部分，我们定义了azure-dev客户端，它使用之前定义的提供者。我们还必须通过client-id和client-secret属性提供客户端凭据。在介绍如何在Azure中注册此应用程序时，我们将在本文后面返回到这些属性。

最后，scope属性定义了该客户端将包含在授权请求中的作用域集。在这里，我们请求profile范围，它允许此客户端应用程序请求标准的[userinfo](https://openid.net/specs/openid-connect-core-1_0.html#UserInfo)端点。此端点返回存储在AzureAD用户目录中的一组可配置信息。这些可能包括用户的首选语言和区域设置数据等。

## 6. 客户端注册

如前所述，我们需要在AzureAD中注册我们的客户端应用程序以获取所需属性client-id和client-secret的实际值。假设我们已经有一个Azure帐户，第一步是登录Web控制台并使用左上角的菜单选择Azure Active Directory服务页面：

<img src="../assets/img.png">

在Overview部分，我们可以获得我们需要在issuer-uri配置属性中使用的租户标识符。接下来，我们将点击App Registrations，这会将我们带到现有应用程序列表，然后点击“New Registration”，这会显示客户端注册表单。在这里，我们必须提供三个信息：

-   应用程序名称
-   支持的账户类型
-   重定向URI

### 6.1 应用程序名称

我们放在这里的值将在身份验证过程中显示给最终用户。因此，我们应该选择一个对目标受众有意义的名称。让我们使用一个非常缺乏想象力的：“Baeldung Test App”：

<img src="../assets/img_1.png">

不过，我们不必太担心名称是否正确。AzureAD允许我们随时更改它而不会影响已注册的应用程序。重要的是要注意，虽然这个名称不必是唯一的，但让多个应用程序使用相同的显示名称并不是一个聪明的主意。

### 6.2 支持的账户类型

在这里，我们有几个选项可以根据应用程序的目标受众进行选择。对于供组织内部使用的应用程序，第一个选项(“Accounts in this organizational directory only”)通常是我们想要的。这意味着即使可以从互联网访问该应用程序，也只有组织内的用户可以登录：

<img src="../assets/img_2.png">

其他可用选项增加了接受来自其他AzureAD支持目录的用户的能力，例如使用Office 365的任何学校或组织以及在Skype和/或Xbox上使用的个人帐户。

虽然不是那么常见，但我们也可以稍后更改此设置，但如文档中所述，用户在进行此更改后可能会收到错误消息。

### 6.3 重定向URI

最后，我们需要提供一个或多个作为可接受的授权流目标的重定向URI。我们必须选择一个与URI关联的“platform”，它转换为我们正在注册的应用程序类型：

-   Web：授权代码与访问令牌交换发生在后端
-   SPA：授权代码与访问令牌交换发生在前端
-   Public Client：用于桌面和移动应用程序

在我们的例子中，我们将选择第一个选项，因为这是我们进行用户身份验证所需要的。

至于URI，我们将使用值[http://localhost:8080/login/oauth2/code/azure-dev](http://localhost:8080/login/oauth2/code/azure-dev)。该值来自Spring Security的OAuth回调控制器使用的路径，默认情况下，该控制器需要/login/oauth2/code/{registration-name}的响应代码。在这里，{registration-name}必须与配置的registration部分中存在的键之一匹配，在我们的例子中是azure-dev。

同样重要的是，AzureAD要求对这些URI使用HTTPS，但localhost是一个例外。这样就可以进行本地开发，而无需设置证书。稍后，当我们移动到目标部署环境(例如Kubernetes集群)时，我们可以添加额外的URI。

请注意，此键的值与AzureAD的注册名称没有直接关系，但使用与其使用位置相关的名称是有意义的。

### 6.4 添加客户端密码

一旦我们按下初始注册表单上的注册按钮，我们就会看到客户的信息页面：

<img src="../assets/img_3.png">

Essentials部分在左侧有应用程序ID，对应于我们属性文件中的client-id属性。要生成新的客户端密码，我们现在单击Add a certificate or secret，这会将我们带到“Certificates & Secrets”页面。接下来，我们将选择Client Secrets选项卡并单击New client secret打开密钥创建表单：

<img src="../assets/img_4.png">

在这里，我们将为该密钥提供一个描述性名称并定义其到期日期。我们可以从预配置的持续时间之一中进行选择，也可以选择自定义选项，这样我们就可以定义开始日期和结束日期。

在撰写本文时，客户端密钥最多会在两年后过期。这意味着我们必须实施密钥轮换程序，最好使用Terraform等自动化工具。两年看似很长一段时间，但在企业环境中，应用程序运行多年后才被替换或更新的情况非常普遍。

一旦我们点击Add，新创建的密钥就会出现在客户端凭证列表中：

<img src="../assets/img_5.png">

我们必须立即将密钥值复制到安全的地方，因为一旦我们离开此页面，它就不会再次显示。在我们的例子中，我们会将值直接复制到应用程序的属性文件中，位于client-secret属性下。

无论如何，我们必须记住这是一个敏感值！将应用程序部署到生产环境时，通常会通过某种动态机制提供此值，例如Kubernetes secret。

## 7. 应用程序代码

我们的测试应用程序有一个控制器处理对根路径的请求，记录有关传入身份验证的信息，并将请求转发到[Thymeleaf](https://www.baeldung.com/thymeleaf-in-spring-mvc)视图。在那里，它将呈现一个页面，其中包含有关当前用户的信息。

实际控制器的代码很简单：

```java
@Controller
@RequestMapping("/")
public class IndexController {

    @GetMapping
    public String index(Model model, Authentication user) {
        model.addAttribute("user", user);
        return "index";
    }
}
```

视图代码使用user模型属性创建一个漂亮的表，其中包含有关身份验证对象和所有可用声明的信息。

## 8. 运行测试应用程序

一切就绪后，我们现在可以运行该应用程序。由于我们已将特定Profile与AzureAD的属性一起使用，因此我们需要激活它。当通过Spring Boot的maven插件运行应用程序时，我们可以使用spring-boot.run.profiles属性来做到这一点：

```bash
mvn -Dspring-boot.run.profiles=azuread spring-boot:run
```

现在，我们可以打开浏览器并访问[http://localhost:8080](http://localhost:8080)。Spring Security将检测到此请求尚未经过身份验证，并将我们重定向到AzureAD的通用登录页面：

<img src="../assets/img_6.png">

具体的登录顺序将根据组织的设置而有所不同，但通常包括填写用户名或电子邮件并提供密码。如果已配置，它还可以请求第二个身份验证因素。但是，如果我们当前在同一个浏览器中登录到同一个AzureAD租户中的另一个应用程序，它将跳过登录序列-毕竟这就是单点登录的全部内容。

首次访问我们的应用程序时，AzureAD还会显示该应用程序的同意表单：

<img src="../assets/img_7.png">

虽然此处未介绍，但AzureAD支持自定义登录UI的多个方面，包括特定于区域设置的自定义。此外，可以完全绕过授权表单，这在授权内部应用程序时很有用。

一旦我们授予权限，我们将看到我们应用程序的主页，部分显示如下：

<img src="../assets/img_8.png">

我们可以看到我们已经可以访问有关用户的基本信息，包括他/她的姓名、电子邮件，甚至是获取他/她图片的URL。但是，有一个烦人的细节：Spring为用户名选择的值不是很友好。

让我们看看如何改进这一点。

## 9. 用户名映射

Spring Security使用Authentication接口来表示经过身份验证的Principal。此接口的具体实现必须提供getName()方法，该方法返回一个值，该值通常用作authentication域内用户的唯一标识符。

当使用基于JWT的身份验证时，Spring Security将默认使用标准的sub claim作为Principal的名称。查看声明，我们看到AzureAD使用不适合显示目的的内部标识符填充此字段。

幸运的是，在这种情况下有一个简单的解决方法。我们所要做的就是选择可用的可用属性之一，并将其名称放在提供者的user-name-attribute属性上：

```yaml
spring:
    security:
        oauth2:
            client:
                provider:
                    azure:
                        issuer-uri: https://login.microsoftonline.com/xxxxxxxxxxxxx/v2.0
                        user-name-attribute: name
... other properties omitted
```

在这里，我们选择了name claim，因为它对应于完整的用户名。另一个合适的候选者是email属性，如果我们的应用程序需要将其值用作某些数据库查询的一部分，这可能是一个不错的选择。

我们现在可以重新启动应用程序并查看此更改的影响：

<img src="../assets/img_9.png">

现在好多了！

## 10.检索组成员

对可用声明的仔细检查表明没有关于用户组成员身份的信息。身份验证中唯一可用的GrantedAuthority值是那些与请求的范围相关联的值，包括在客户端配置中。

如果我们只需要限制对组织成员的访问，这可能就足够了。但是，通常情况下我们会根据分配给当前用户的角色授予不同的访问级别。此外，将这些角色映射到AzureAD组允许重用可用流程，例如用户入职和/或重新分配。

为此，我们必须指示AzureAD在我们将在授权流程中收到的idToken中包含组成员身份。

首先，我们必须转到我们的应用程序页面并在右侧菜单中选择令牌配置。接下来，我们将点击Addgroupsclaim，这将打开一个对话框，我们将在其中定义此声明类型所需的详细信息：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/2_BAEL-6160-Group-mapping_1.png)](https://www.baeldung.com/wp-content/uploads/2023/02/2_BAEL-6160-Group-mapping_1.png)

我们将使用常规的AzureAD组，因此我们将选择第一个选项(“安全组”)。此对话框还为每种受支持的令牌类型提供了其他配置选项。我们暂时保留默认值。

单击Save后，应用程序的声明列表将显示组声明：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/2_BAEL-6160-Group-mapping_2.png)](https://www.baeldung.com/wp-content/uploads/2023/02/2_BAEL-6160-Group-mapping_2.png)

现在，我们可以回到我们的应用程序来查看此配置的效果：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/1_BAEL-6160-UserInfoPage_v3-1024x185.png)](https://www.baeldung.com/wp-content/uploads/2023/02/1_BAEL-6160-UserInfoPage_v3.png)

## 11.将组映射到Spring权限

组声明包含与用户分配的组相对应的对象标识符列表。然而，Spring不会自动将这些组映射到GrantedAuthority实例。

这样做需要自定义OidcUserService，如Spring Security的[文档](https://docs.spring.io/spring-security/reference/5.7.7/servlet/oauth2/login/advanced.html#oauth2login-advanced-map-authorities-oauth2userservice)中所述。我们的实现([可在线获取](https://github.com/eugenp/tutorials/blob/master/spring-security-modules/spring-security-azuread/src/main/java/com/baeldung/security/azuread/config/JwtAuthorizationConfiguration.java))使用外部映射来“丰富”具有额外权限的标准OidcUser实现。我们使用了一个@ConfigurationProperties类，我们在其中放置了所需的信息：

-我们将从中获取组列表(“组”)的声明名称
-从此提供者映射的权限的前缀
-对象标识符到GrantedAuthority值的映射

使用组到列表的映射策略使我们能够应对我们想要使用现有组的情况。它还有助于保持应用程序的角色集与组分配策略分离。

这是典型配置的样子：

```yaml
baeldung:
  jwt:
    authorization:
      group-to-authorities:
        "ceef656a-fca9-49b6-821b-xxxxxxxxxxxx": BAELDUNG_RW
        "eaaecb69-ccbc-4143-b111-xxxxxxxxxxxx": BAELDUNG_RO,BAELDUNG_ADMIN
```

对象标识符在组页面上可用：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/1_BAEL-6160-Group-list-1024x138.png)](https://www.baeldung.com/wp-content/uploads/2023/02/1_BAEL-6160-Group-list.png)

一旦完成所有这些映射并重新启动我们的应用程序，我们就可以测试我们的应用程序。这是我们为属于两个组的用户获得的结果：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/2_BAEL-6160-UserInfoPage_v4.png)](https://www.baeldung.com/wp-content/uploads/2023/02/2_BAEL-6160-UserInfoPage_v4.png)

它现在具有对应于映射组的三个新权限。

## 12.结论

在本文中，我们展示了如何将AzureAD与Spring Security结合使用来对用户进行身份验证，包括演示应用程序所需的配置步骤。