## 1. 概述

[Keycloak](https://www.keycloak.org/)是第三方授权服务器，用于管理我们的Web或移动应用程序的用户。

它提供了一些默认属性，例如要为任何给定用户存储的名字、姓氏和电子邮件。但很多时候，这些还不够，我们可能需要添加一些特定于我们的应用程序的额外用户属性。

在本教程中，我们将了解**如何将自定义用户属性添加到我们的Keycloak授权服务器并在基于Spring的后端访问它们**。

首先，我们将在[独立]()的Keycloak服务器上演示它，然后在[嵌入式]()服务器上实现相同的目的。

## 2. 独立服务器

### 2.1 添加自定义用户属性

这里的第一步是转到Keycloak的管理控制台，为此，我们需要通过从Keycloak发行版的bin文件夹中运行以下命令来启动服务器：

```bash
./standalone.sh -Djboss.socket.binding.port-offset=100
```

然后我们需要转到[管理控制台](http://localhost:8080/admin)并输入initial1/zaq1!QAZ凭据。

接下来，我们将单击“Manage”选项卡下的“Users”，然后单击“User list”：

<img src="../assets/img_17.png">

在这里我们可以看到我们之前添加的用户user1。

现在让我们点击用户名并转到“Attributes”选项卡以添加一个新的ID，出生日期为DOB：

<img src="../assets/img_18.png">

单击Save后，自定义属性将添加到用户的信息中。

接下来，我们需要为此属性添加一个映射作为自定义声明，以便它在用户令牌的JSON负载中可用。

为此，我们需要在管理控制台上转到应用程序的Clients，回想一下，我们之前创建了一个客户端login-app：

<img src="../assets/img_19.png">

现在，让我们单击它并转到其Client scopes选项卡：

<img src="../assets/img_20.png">

然后点击login-app-dedicated进入到Mappers选项卡：

<img src="../assets/img_21.png">

然后点击Configure a new mapper：

<img src="../assets/img_22.png">

最后在Configure a new mapper窗口选择User Attribute：

<img src="../assets/img_23.png">

首先，我们将选择Mapper type作为User Attribute，然后将Name、User Attribute和Token Claim Name设置为DOB，并且Claim JSON Type应设置为String。

单击Save后，我们的映射就准备好了。所以现在，我们从Keycloak端配备了接收DOB作为自定义用户属性的功能。

在下一节中，我们将了解如何**通过API调用访问它**。

### 2.2 访问自定义用户属性

在我们的[Spring Boot应用程序]()之上构建，让我们添加一个新的REST控制器来获取我们添加的用户属性：

```java
@Controller
public class CustomUserAttrController {

    @GetMapping(path = "/users")
    public String getUserInfo(Model model) {
        KeycloakAuthenticationToken authentication = (KeycloakAuthenticationToken) 
              SecurityContextHolder.getContext().getAuthentication();

        Principal principal = (Principal) authentication.getPrincipal();
        String dob="";

        if (principal instanceof KeycloakPrincipal) {
            KeycloakPrincipal kPrincipal = (KeycloakPrincipal) principal;
            IDToken token = kPrincipal.getKeycloakSecurityContext().getIdToken();

            Map<String, Object> customClaims = token.getOtherClaims();

            if (customClaims.containsKey("DOB")) {
                dob = String.valueOf(customClaims.get("DOB"));
            }
        }

        model.addAttribute("username", principal.getName());
        model.addAttribute("dob", dob);
        return "userInfo";
    }
}
```

如我们所见，这里我们首先从SecurityContextHolder中获取了KeycloakAuthenticationToken，然后从中提取了Principal，在将其转换为KeycloakPrincipal后，我们获得了它的IDToken。

然后可以从此IDToken的OtherClaims中提取DOB。

下面是名为userInfo.html的模板，我们将使用它来显示此信息：

```html
<div id="container">
    <h1>Hello, <span th:text="${username}">--name--</span>.</h1>
    <h3>Your Date of Birth as per our records is <span th:text="${dob}"/>.</h3>
</div>
```

### 2.3 测试

在启动Boot应用程序时，我们应该导航到http://localhost:8081/users，系统将首先要求我们输入凭据。

输入user1的凭据后，我们应该会看到此页面：

<img src="../assets/img_24.png">

## 3. 嵌入式服务器

现在让我们看看如何在嵌入式Keycloak实例上实现相同的目标。

### 3.1 添加自定义用户属性

基本上，我们需要在这里执行相同的步骤，**只是我们需要将它们作为**[预配置]()**保存在我们的realm定义文件tuyucheng-realm.json中**。

要将属性DOB添加到我们的用户john@test.com，首先，我们需要配置它的属性：

```json
"attributes": {
    "DOB" : "1984-07-01"
},
```

然后为DOB添加协议映射器：

```json
"protocolMappers": [
    {
    "id": "c5237a00-d3ea-4e87-9caf-5146b02d1a15",
    "name": "DOB",
    "protocol": "openid-connect",
    "protocolMapper": "oidc-usermodel-attribute-mapper",
    "consentRequired": false,
    "config": {
        "userinfo.token.claim": "true",
        "user.attribute": "DOB",
        "id.token.claim": "true",
        "access.token.claim": "true",
        "claim.name": "DOB",
        "jsonType.label": "String"
        }
    }
]
```

这就是我们在这里所需要的。

现在我们已经看到了添加自定义用户属性的授权服务器部分，是时候看看[资源服务器]()如何访问用户的DOB了。

### 3.2 访问自定义用户属性

在资源服务器端，自定义属性将作为[AuthenticationPrincipal](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/core/annotation/AuthenticationPrincipal.html)中的声明值简单地提供给我们。

让我们为它编写一个API：

```java
@RestController
public class CustomUserAttrController {
    @GetMapping("/user/info/custom")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal Jwt principal) {
        return Collections.singletonMap("DOB", principal.getClaimAsString("DOB"));
    }
}
```

### 3.3 测试

现在让我们使用JUnit对其进行测试。

我们首先需要获取访问令牌，然后调用资源服务器上的/user/info/custom API端点：

```java
@Test
void givenUserWithReadScope_whenGetUserInformationResource_thenSuccess() {
    String accessToken = obtainAccessToken("read");
    Response response = RestAssured.given()
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        .get(userInfoResourceUrl);

    assertThat(response.as(Map.class)).containsEntry("DOB", "1984-07-01");
}
```

正如我们所见，我们在这里验证了我们获得的DOB值与我们在用户属性中添加的值相同。

## 4. 总结

在本教程中，我们学习了如何在Keycloak中为用户添加额外的属性，我们在独立实例和嵌入式实例中都演示了这一点，并了解了如何在两种情况下在后端的REST API中访问这些自定义声明。