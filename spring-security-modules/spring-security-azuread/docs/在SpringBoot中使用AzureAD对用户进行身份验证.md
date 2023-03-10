## 一、简介

在本教程中，我们将展示如何轻松使用 AzureAD 作为 Spring Boot 应用程序的身份提供者。

## 2.概述

Microsoft 的 AzureAD 是一种综合身份管理产品，全球许多组织都在使用它。它支持多种登录机制和控件，可为组织应用程序组合中的用户提供单点登录体验。

**此外，与 Microsoft 的起源一样，AzureAD 与现有的 Active Directory 安装很好地集成，许多组织已经将其用于企业网络中的身份和访问管理**。这允许管理员向现有用户授予对应用程序的访问权限，并使用他们已经习惯的相同工具来管理他们的权限。

## 3. 集成 AzureAD

**从基于 Spring Boot 的应用程序的角度来看，AzureAD 充当 OIDC 兼容的身份提供者**。这意味着我们可以通过配置所需的属性和依赖项来将它与 Spring Security 一起使用。

为了说明 AzureAD 集成，我们将实现一个[机密客户端](https://www.rfc-editor.org/rfc/rfc6749#section-2.1)，其中用于访问代码交换的授权代码发生在服务器端。**此流程从不向用户的浏览器公开访问令牌，因此它被认为比公共客户端替代方案更安全。**

## 4.Maven依赖

我们首先为基于 Spring Security 的 WebMVC 应用程序添加所需的 Maven 依赖项：

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
复制
```

Maven Central 上提供了这些依赖项的最新版本：

-   *[spring-boot-stater-oauth2-客户端](https://search.maven.org/search?q=a:spring-boot-starter-oauth2-client AND g:org.springframework.boot)*
-   *[spring-boot-starter-web](https://search.maven.org/search?q=a:spring-boot-starter-web AND g:org.springframework.boot)*

## 5.配置属性

接下来，我们将添加用于配置客户端所需的[Spring Security属性。](https://www.baeldung.com/learn-spring-security-course)**一个好的做法是将这些属性放在专用的 Spring 配置文件中，这使得随着应用程序的增长维护起来更容易一些**。我们将此配置文件命名*为 azuread*，以明确其目的。*因此，我们将在application-azuread.yml*文件中添加相关属性：

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
复制
```

在提供者部分，我们定义了一个*azure*提供者。**AzureAD 支持 OIDC 标准端点发现机制，因此我们唯一需要配置的属性是 \*issuer-uri。\***

此属性有双重用途：首先，它是客户端附加发现资源名称以获取要下载的实际 URL 的基本 URI。[其次，它是否也用于检查JSON Web Token](https://www.rfc-editor.org/rfc/rfc7519) (JWT)的真实性。例如，身份提供者创建的 JWT 的*iss声明必须与**issuer-uri*值相同。

对于 AzureAD，*issuer-uri始终采用**https://login.microsoftonline.com/my-tenant-id/v2.0*形式，其中*my-tenant-id*是您的租户的标识符。

在*注册*部分，我们定义了*azure-dev*客户端，它使用之前定义的提供程序。*我们还必须通过client-id*和 client *-secret*属性提供客户端凭证。在介绍如何在 Azure 中注册此应用程序时，我们将在本文后面返回到这些属性。

最后，范围属性定义了该客户端将包含在授权请求中的范围集。在这里，我们请求*配置*文件范围，它允许此客户端应用程序请求标准的[*userinfo*](https://openid.net/specs/openid-connect-core-1_0.html#UserInfo)端点。此端点返回存储在 AzureAD 用户目录中的一组可配置信息。这些可能包括用户的首选语言和区域设置数据等。

## 6. 客户注册

如前所述，我们需要在 AzureAD 中注册我们的客户端应用程序以获取所需属性***client-id\*****和*****client-secret\*****的实际值**。假设我们已经有一个 Azure 帐户，第一步是登录 Web 控制台并使用左上角的菜单选择*Azure Active Directory*服务页面：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/1_BAEL-6160-AD_Home-1024x536.png)](https://www.baeldung.com/wp-content/uploads/2023/02/1_BAEL-6160-AD_Home.png)

在*Overview部分，我们可以获得我们需要在**issuer-uri*配置属性中使用的租户标识符。接下来，我们将点击*App Registrations*，这会将我们带到现有应用程序列表，然后点击“New Registration”，这会显示客户注册表。在这里，我们必须提供三个信息：

-   应用名称
-   支持的账户类型
-   重定向 URI

让我们详细介绍这些项目中的每一项。

### 6.1. 应用名称

**我们放在这里的值将在身份验证过程中显示给最终用户。**因此，我们应该选择一个对目标受众有意义的名称。让我们使用一个非常缺乏想象力的：“Baeldung Test App”：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/2_BAEL-6160-NewApp_Name.png)](https://www.baeldung.com/wp-content/uploads/2023/02/2_BAEL-6160-NewApp_Name.png)

不过，我们不必太担心名称是否正确。**AzureAD 允许我们随时更改它而不会影响已注册的应用程序**。重要的是要注意，虽然这个名称不必是唯一的，但让多个应用程序使用相同的显示名称并不是一个聪明的主意。

### 6.2. 支持的账户类型

在这里，我们有几个选项可以根据应用程序的目标受众进行选择。**对于供组织内部使用的应用程序，第一个选项（“仅此组织目录中的帐户”）通常是我们想要的**。这意味着即使可以从 Internet 访问该应用程序，也只有组织内的用户可以登录：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/2_BAEL-6160-NewApp_AccountType.png)](https://www.baeldung.com/wp-content/uploads/2023/02/2_BAEL-6160-NewApp_AccountType.png)

其他可用选项增加了接受来自其他 AzureAD 支持目录的用户的能力，例如使用 Office 365 的任何学校或组织以及在 Skype 和/或 Xbox 上使用的个人帐户。

虽然不是那么常见，但我们也可以稍后更改此设置，但如文档中所述，用户在进行此更改后可能会收到错误消息。

### 6.3. 重定向 URI

最后，我们需要提供一个或多个作为可接受的授权流目标的重定向 URI。我们必须选择一个与 URI 关联的“平台”，它转换为我们正在注册的应用程序类型：

-   *Web*：授权代码与访问令牌交换发生在后端
-   *SPA*：授权码与访问令牌交换发生在前端
-   *公共客户端*：用于桌面和移动应用程序。

在我们的例子中，我们将选择第一个选项，因为这是我们进行用户身份验证所需要的。

至于 URI，我们将使用值*http://localhost:8080/login/oauth2/code/azure-dev*。该值来自 Spring Security 的 OAuth 回调控制器使用的路径，默认情况下，它期望响应代码位于*/login/oauth2/code/{registration-name}*。在这里，*{registration-name}必须匹配配置的**注册*部分中存在的键之一， 在我们的例子中是*azure-dev 。*

***同样重要的是，AzureAD 要求对这些 URI 使用 HTTPS，但localhost\*是一个例外。**这使得本地开发无需设置证书。稍后，当我们移动到目标部署环境（例如 Kubernetes 集群）时，我们可以添加额外的 URI。

请注意，此键的值与 AzureAD 的注册名称没有直接关系，但使用与其使用位置相关的名称是有意义的。

### 6.4. 添加客户端密码

一旦我们按下初始注册表单上的注册按钮，我们就会看到客户的信息页面：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/1_BAEL-6160-App_Overview2-1024x440.png)](https://www.baeldung.com/wp-content/uploads/2023/02/1_BAEL-6160-App_Overview2.png)

Essentials部分在左侧有应用程序 ID，对应于我们属性文件中的*client* *-id属性。*要生成新的客户端密码，我们现在单击*添加证书或密码*，这会将我们带到*“证书和密码”*页面。接下来，我们将选择*Client Secrets*选项卡并单击*New client secret* 打开秘密创建表单：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/2_BAEL-6160-App_AddSecret.png)](https://www.baeldung.com/wp-content/uploads/2023/02/2_BAEL-6160-App_AddSecret.png)

在这里，我们将为该机密提供一个描述性名称并定义其到期日期。我们可以从预配置的持续时间之一中进行选择，也可以选择自定义选项，这样我们就可以定义开始日期和结束日期。

在撰写本文时，客户端机密最多会在两年后过期。**这意味着我们必须实施秘密轮换程序，最好使用 Terraform 等自动化工具。**两年看似很长一段时间，但在企业环境中，应用程序运行多年后才被替换或更新的情况非常普遍。

一旦我们点击*添加*，新创建的秘密就会出现在客户端凭证列表中：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/1_BAEL-6160-App_CredentialsList-1024x221.png)](https://www.baeldung.com/wp-content/uploads/2023/02/1_BAEL-6160-App_CredentialsList.png)

**我们必须立即将秘密值复制到安全的地方，因为一旦我们离开此页面，它就不会再次显示**。在我们的例子中，我们会将值直接复制到应用程序的属性文件中，位于*client-secret*属性下。

无论如何，我们必须记住这是一个敏感值！将应用程序部署到生产环境时，通常会通过某种动态机制提供此值，例如 Kubernetes 秘密。

## 七、申请代码

我们的测试应用程序有一个控制器处理对根路径的请求，记录有关传入身份验证的信息，并将请求转发到[*Thymeleaf*](https://www.baeldung.com/thymeleaf-in-spring-mvc)视图。在那里，它将呈现一个页面，其中包含有关当前用户的信息。

实际控制人的代码很简单：

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
复制
```

视图代码使用*用户*模型属性创建一个漂亮的表，其中包含有关身份验证对象和所有可用声明的信息。

## 8. 运行测试应用

一切就绪后，我们现在可以运行该应用程序。**由于我们使用了具有 AzureAD 属性的特定配置文件，因此我们需要激活它**。*当通过 Spring Boot 的 maven 插件运行应用程序时，我们可以使用spring-boot.run.profiles*属性来做到这一点：

```bash
mvn -Dspring-boot.run.profiles=azuread spring-boot:run复制
```

现在，我们可以打开浏览器并访问*http://localhost:8080*。Spring Security 将检测到此请求尚未经过身份验证，并将我们重定向到 AzureAD 的通用登录页面：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/2_BAEL-6160-Sign-in.png)](https://www.baeldung.com/wp-content/uploads/2023/02/2_BAEL-6160-Sign-in.png)

具体的登录顺序将根据组织的设置而有所不同，但通常包括填写用户名或电子邮件并提供密码。如果已配置，它还可以请求第二个身份验证因素。但是，如果我们当前在同一个浏览器中登录到同一个 AzureAD 租户中的另一个应用程序，它将跳过登录序列——毕竟这就是单点登录的全部内容。

我们第一次访问我们的应用程序时，AzureAD 还会显示该应用程序的同意书：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/1_BAEL-6160-Sign-in-to-your-account_v2-240x300.png)](https://www.baeldung.com/wp-content/uploads/2023/02/1_BAEL-6160-Sign-in-to-your-account_v2.png)

虽然此处未介绍，但 AzureAD 支持自定义登录 UI 的多个方面，包括特定于区域设置的自定义。此外，可以完全绕过授权表单，这在授权内部应用程序时很有用。

一旦我们授予权限，我们将看到我们应用程序的主页，部分显示如下：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/1_BAEL-6160-UserInfoPage-1024x559.png)](https://www.baeldung.com/wp-content/uploads/2023/02/1_BAEL-6160-UserInfoPage.png)

我们可以看到我们已经可以访问有关用户的基本信息，包括他/她的姓名、电子邮件，甚至是获取他/她图片的 URL。但是，有一个烦人的细节：Spring 为用户名选择的值不是很友好。

让我们看看如何改进它。

## 9. 用户名映射

Spring Security 使用*Authentication*接口来表示经过身份验证的*Principal*。此接口的具体实现必须提供*getName()*方法，该方法返回一个值，该值通常用作身份验证域内用户的唯一标识符。

**当使用基于 JWT 的身份验证时，Spring Security 将默认使用标准的\*sub\*声明值作为\*Principal\*的名称**。查看声明，我们看到 AzureAD 使用不适合显示目的的内部标识符填充此字段。

幸运的是，在这种情况下有一个简单的修复方法。我们所要做的就是选择可用的可用属性之一，并将其名称放在提供者的*用户名*属性上：

```yaml
spring:
  security:
    oauth2:
      client:
        provider:
          azure:
            issuer-uri: https://login.microsoftonline.com/xxxxxxxxxxxxx/v2.0
            user-name-attribute: name
... other properties omitted复制
```

在这里，我们选择了名称声明，因为它对应于完整的用户名。另一个合适的候选者是*电子邮件*属性，如果我们的应用程序需要将其值用作某些数据库查询的一部分，这可能是一个不错的选择。

我们现在可以重新启动应用程序并查看此更改的影响：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/1_BAEL-6160-UserInfoPage_v2-1024x407.png)](https://www.baeldung.com/wp-content/uploads/2023/02/1_BAEL-6160-UserInfoPage_v2.png)

现在好多了！

## 10. 检索组成员

**对可用声明的仔细检查表明没有关于用户组成员身份的信息**。*身份验证*中唯一可用的*GrantedAuthority*值是那些与请求的范围相关联的值，包括在客户端配置中。

如果我们只需要限制对组织成员的访问，这可能就足够了。但是，通常情况下我们会根据分配给当前用户的角色授予不同的访问级别。此外，将这些角色映射到 AzureAD 组允许重用可用流程，例如用户入职和/或重新分配。

**为此，我们必须指示 AzureAD 在我们将在授权流程中收到的\*idToken中包含组成员身份。\***

首先，我们必须转到我们的应用程序页面并在右侧菜单中选择*令牌配置。*接下来，我们将点击*Add groups claim*，这将打开一个对话框，我们将在其中定义此声明类型所需的详细信息：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/2_BAEL-6160-Group-mapping_1.png)](https://www.baeldung.com/wp-content/uploads/2023/02/2_BAEL-6160-Group-mapping_1.png)

我们将使用常规的 AzureAD 组，因此我们将选择第一个选项（“安全组”）。此对话框还为每种受支持的令牌类型提供了其他配置选项。我们暂时保留默认值。

单击*Save*后，应用程序的声明列表将显示组声明：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/2_BAEL-6160-Group-mapping_2.png)](https://www.baeldung.com/wp-content/uploads/2023/02/2_BAEL-6160-Group-mapping_2.png)

现在，我们可以回到我们的应用程序来查看此配置的效果：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/1_BAEL-6160-UserInfoPage_v3-1024x185.png)](https://www.baeldung.com/wp-content/uploads/2023/02/1_BAEL-6160-UserInfoPage_v3.png)

## 11. 将组映射到 Spring 权限

组声明包含与用户分配的组相对应的对象标识符列表。然而，Spring 不会自动将这些组映射到*GrantedAuthority*实例。

这样做需要自定义*OidcUserService ，如 Spring Security 的*[文档](https://docs.spring.io/spring-security/reference/5.7.7/servlet/oauth2/login/advanced.html#oauth2login-advanced-map-authorities-oauth2userservice)中所述。我们的实现（[可在线获取](https://github.com/eugenp/tutorials/blob/master/spring-security-modules/spring-security-azuread/src/main/java/com/baeldung/security/azuread/config/JwtAuthorizationConfiguration.java)）使用外部映射来“丰富”具有额外权限的标准*OidcUser*实现。我们使用了一个*@ConfigurationProperties*类，我们在其中放置了所需的信息：

-   我们将从中获取组列表（“组”）的声明名称
-   从此提供者映射的权限的前缀
-   *对象标识符到GrantedAuthority*值的映射

使用组到列表的映射策略使我们能够应对我们想要使用现有组的情况。它还有助于保持应用程序的角色集与组分配策略分离。

这是典型配置的样子：

```yaml
baeldung:
  jwt:
    authorization:
      group-to-authorities:
        "ceef656a-fca9-49b6-821b-xxxxxxxxxxxx": BAELDUNG_RW
        "eaaecb69-ccbc-4143-b111-xxxxxxxxxxxx": BAELDUNG_RO,BAELDUNG_ADMIN复制
```

*对象标识符在组*页面上可用：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/1_BAEL-6160-Group-list-1024x138.png)](https://www.baeldung.com/wp-content/uploads/2023/02/1_BAEL-6160-Group-list.png)

一旦完成所有这些映射并重新启动我们的应用程序，我们就可以测试我们的应用程序。这是我们为属于两个组的用户获得的结果：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/2_BAEL-6160-UserInfoPage_v4.png)](https://www.baeldung.com/wp-content/uploads/2023/02/2_BAEL-6160-UserInfoPage_v4.png)

它现在具有对应于映射组的三个新权限。

## 12.结论

在本文中，我们展示了如何将 AzureAD 与 Spring Security 结合使用来对用户进行身份验证，包括演示应用程序所需的配置步骤。