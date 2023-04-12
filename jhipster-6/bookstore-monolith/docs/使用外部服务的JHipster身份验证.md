## 1. 概述

默认情况下，[JHipster](https://www.jhipster.tech/)应用程序使用本地数据存储来保存用户名和密码。然而，在许多现实场景中，可能需要使用现有的外部服务进行身份验证。

在本教程中，我们将了解如何在 JHipster 中使用外部服务进行身份验证。这可以是任何众所周知的服务，例如 LDAP、社交登录或任何接受用户名和密码的任意服务。

## 2. JHipster 中的身份验证

JHipster 使用[Spring Security](https://www.baeldung.com/security-spring)进行身份验证。AuthenticationManager类负责验证用户名和密码。

JHipster 中的默认AuthenticationManager只是根据本地数据存储检查用户名和密码。这可能是 MySQL、PostgreSQL、MongoDB 或 JHipster 支持的任何替代方案。

重要的是要注意AuthenticationManager仅用于初始登录。用户通过身份验证后，他们会收到用于后续 API 调用的 JSON Web 令牌 (JWT)。

### 2.1. 在 JHipster 中更改身份验证

但是，如果我们已经有了一个包含用户名和密码的数据存储，或者一个为我们执行身份验证的服务呢？

要提供自定义身份验证方案，我们只需创建一个AuthenticationManager类型的新 bean 。这将优先于默认实现。

下面的示例展示了如何创建自定义AuthenticationManager。它只有一种实现方法：

```java
public class CustomAuthenticationManager implements AuthenticationManager {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            ResponseEntity<LoginResponse> response =
                restTemplate.postForEntity(REMOTE_LOGIN_URL, loginRequest, LoginResponse.class);
            
            if(response.getStatusCode().is2xxSuccessful()) {
                String login = authentication.getPrincipal().toString();
                User user = userService.getUserWithAuthoritiesByLogin(login)
                  .orElseGet(() -> userService.createUser(
                    createUserDTO(response.getBody(), authentication)));
                return createAuthentication(authentication, user);
            }
            else {
                throw new BadCredentialsException("Invalid username or password");
            }
        }
        catch (Exception e) {
            throw new AuthenticationServiceException("Failed to login", e);
        }
    }
}
```

在此示例中，我们将用户名和凭据从身份验证对象传递到外部 API。

如果调用成功，我们将返回一个新的UsernamePasswordAuthenticationToken以指示成功。请注意，我们还创建了一个本地用户条目，稍后我们将对此进行讨论。

如果调用失败，我们会抛出AuthenticationException的一些变体，以便 Spring Security 优雅地为我们回退。

此示例有意简单地展示自定义身份验证的基础知识。但是，它可以执行更复杂的操作，例如 LDAP 绑定和身份验证或[使用 OAuth](https://developer.okta.com/blog/2018/03/01/develop-microservices-jhipster-oauth)。

## 3. 其他注意事项

到目前为止，我们一直关注 JHipster 中的身份验证流程。但是我们必须修改 JHipster 应用程序的其他几个区域。

### 3.1. 前端代码

默认的 JHipster 代码实现了以下用户注册和激活过程：

-   用户使用他们的电子邮件和其他必需的详细信息注册一个帐户
-   JHipster 创建一个帐户并将其设置为非活动状态，然后向新用户发送一封包含激活链接的电子邮件
-   单击链接后，用户的帐户将被标记为活动

密码重置也有类似的流程。

当 JHipster 管理用户帐户时，这些都是有意义的。但是当我们依赖外部服务进行身份验证时，就不需要它们了。

因此，我们需要采取措施确保用户无法访问这些帐户管理功能。

这意味着将它们从 Angular 或 React 代码中删除，具体取决于 JHipster 应用程序中使用的框架。

以 Angular 为例，默认登录提示包括指向密码重置和注册的链接。我们应该从app/shared/login/login.component.html中删除它们：

```html
<div class="alert alert-warning">
  <a class="alert-link" (click)="requestResetPassword()">Did you forget your password?</a>
</div>
<div class="alert alert-warning">
  <span>You don't have an account yet?</span>
   <a class="alert-link" (click)="register()">Register a new account</a>
</div>
```

我们还必须从app/layouts/navbar/navbar.component.html中删除不需要的导航菜单项：

```html
<li *ngSwitchCase="true">
  <a class="dropdown-item" routerLink="password" routerLinkActive="active" (click)="collapseNavbar()">
    <fa-icon icon="clock" fixedWidth="true"></fa-icon>
    <span>Password</span>
  </a>
</li>
```

和

```html
<li *ngSwitchCase="false">
  <a class="dropdown-item" routerLink="register" routerLinkActive="active" (click)="collapseNavbar()">
    <fa-icon icon="user-plus" fixedWidth="true"></fa-icon>
    <span>Register</span>
  </a>
</li>复制
```

即使我们删除了所有链接，用户仍然可以手动导航到这些页面。最后一步是从app/account/account.route.ts中删除未使用的 Angular 路由。

这样做之后，应该只保留设置路由：

```javascript
import { settingsRoute } from './';
const ACCOUNT_ROUTES = [settingsRoute];
```

### 3.2. Java API

在大多数情况下，只需删除前端帐户管理代码就足够了。但是，为了绝对确保不调用帐户管理代码，我们还可以锁定关联的 Java API。

最快的方法是更新SecurityConfiguration类以拒绝对关联 URL 的所有请求：

```java
.antMatchers("/api/register").denyAll()
.antMatchers("/api/activate").denyAll()
.antMatchers("/api/account/reset-password/init").denyAll()
.antMatchers("/api/account/reset-password/finish").denyAll()
```

这将阻止对 API 的任何远程访问，而无需删除任何代码。

### 3.3. 电子邮件模板

JHipster 应用程序带有一组用于帐户注册、激活和密码重置的默认电子邮件模板。前面的步骤将有效地阻止发送默认电子邮件，但在某些情况下，我们可能希望重用它们。

例如，我们可能希望在用户首次登录时发送一封欢迎邮件。默认模板中包含了账户激活的步骤，所以我们要对其进行修改。

所有电子邮件模板都位于resources/templates/mail中。它们是使用[Thymeleaf](https://www.baeldung.com/thymeleaf-in-spring-mvc)将数据从 Java 代码传递到电子邮件中的 HTML 文件。

我们所要做的就是编辑模板以包含所需的文本和布局，然后使用 MailService发送它。

### 3.4. 角色

当我们创建本地 JHipster 用户条目时，我们还必须注意确保它至少具有一个角色。通常，默认的USER角色对于新帐户来说已经足够了。

如果外部服务提供自己的角色映射，我们还有两个额外的步骤：

1.  [确保JHipster 中存在](https://www.baeldung.com/jhipster-new-roles)任何自定义角色
2.  更新我们的自定义AuthenticationManager以在创建新用户时设置自定义角色

JHipster 还提供了一个管理界面，用于为用户添加和删除角色。

### 3.5. 帐户删除

值得一提的是，JHipster 还提供了账户移除管理视图和 API。此视图仅对管理员用户可用。

我们可以像注册账号和重设密码一样，删除和限制这段代码，但不是必须的。当有人登录时，我们的自定义AuthenticationManager总是会创建一个新的帐户条目，因此删除帐户实际上并没有多大作用。

## 4。总结

在本教程中，我们了解了如何用我们自己的身份验证方案替换默认的 JHipster 身份验证代码。这可以是 LDAP、OIDC 或任何其他接受用户名和密码的服务。

我们还看到，使用外部身份验证服务还需要对 JHipster 应用程序的其他区域进行一些更改。这包括前端视图、API 等。
