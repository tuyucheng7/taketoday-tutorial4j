## 1. 概述

**在最新的Spring Security版本中，发生了很多变化。其中一个改变是我们如何在应用程序中处理密码编码**。在本文中，我们将探索其中的一些变化。

稍后，我们会介绍看到如何配置新的委派机制，以及如何在用户无法识别的情况下更新现有密码编码。

## 2. Spring Security 5.x的相关变化

Spring Security团队将org.springframework.security.authentication.encoding中的PasswordEncoder声明为已过时，这是一个合乎逻辑的举动，因为旧接口不是为随机生成盐设计的。因此，版本5删除了该接口。

此外，Spring Security改变了它处理编码密码的方式。在以前的版本中，每个应用程序仅使用一种密码编码算法。默认情况下，StandardPasswordEncoder会处理这个问题，它使用SHA-256进行编码。通过更改密码编码器，我们可以切换到另一种算法，但是我们的应用程序必须保持使用一种算法。

5.0版本中引入了密码编码委派的概念。现在，我们可以对不同的密码使用不同的编码，Spring通过编码密码前缀的标识符来识别算法。

以下是bcrypt编码密码的示例：

```java
{bcrypt}$2b$12$FaLabMRystU4MLAasNOKb.HUElBAabuQdX59RWHq5X.9Ghm692NEi
```

请注意编码后密码包含的{bcrypt}前缀。

## 3. 委派配置

**如果密码哈希没有前缀，则委托过程使用默认编码器。因此，默认情况下，使用的是StandardPasswordEncoder**，这使得它与以前的Spring Security版本的默认配置兼容。

在版本5中，Spring Security引入了PasswordEncoderFactories.createDelegatingPasswordEncoder()。此工厂方法返回一个已配置的DelegationPasswordEncoder实例。

对于没有前缀的密码，该实例确保了刚才提到的默认行为；对于包含前缀的密码哈希，将相应地执行委派。

Spring Security团队在对应的[JavaDoc](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/crypto/factory/PasswordEncoderFactories.html#createDelegatingPasswordEncoder--)的最新版本中列出了支持的算法。

当然，Spring允许我们配置这种行为。假设我们想要支持：

+ bcrypt作为我们新的默认值
+ scrypt作为替代方案
+ SHA-256作为当前使用的算法

此设置的配置将如下所示：

```java
@Configuration
public class PasswordStorageWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // set up the list of supported encoders and their prefixes
        PasswordEncoder defaultEncoder = new StandardPasswordEncoder();
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("scrypt", new SCryptPasswordEncoder());
        encoders.put("noop", NoOpPasswordEncoder.getInstance());
        
        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("bcrypt", encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(defaultEncoder);
        
        return passwordEncoder;
    }
}
```

## 4. 迁移密码编码算法

在上一节中，我们介绍了如何根据需要配置密码编码。**接下来，现在我们将介绍如何将已编码的密码转换为新算法**。

假设我们想将编码从SHA-256更改为bcrypt，但是，我们不希望我们的用户更改他们的密码。

一种可能的解决方案是使用登录请求，此时，我们可以以纯文本形式访问凭据。之后，我们可以获取当前密码并对其重新编码。

**因此，我们可以为此使用Spring的AuthenticationSuccessEvent，此事件在用户成功登录我们的应用程序后触发**。

下面是示例代码：

```java
@Configuration
public class TuyuchengPasswordEncoderSetup {

	private final static Logger LOG = LoggerFactory.getLogger(TuyuchengPasswordEncoderSetup.class);

	@Bean
	public ApplicationListener<AuthenticationSuccessEvent> authenticationSuccessListener(final PasswordEncoder encoder) {

		return (AuthenticationSuccessEvent event) -> {
			final Authentication auth = event.getAuthentication();

			if (auth instanceof UsernamePasswordAuthenticationToken && auth.getCredentials() != null) {

				final CharSequence clearTextPass = (CharSequence) auth.getCredentials(); // 1
				final String newPasswordHash = encoder.encode(clearTextPass); // 2

				LOG.info("New password hash {} for user {}", newPasswordHash, auth.getName());

				((UsernamePasswordAuthenticationToken) auth).eraseCredentials(); // 3
			}
		};
	}
}
```

+ 我们从提供的身份验证详细信息中以明文形式检索了用户密码
+ 使用新算法创建了新的密码哈希
+ 从身份验证令牌中删除明文密码

**默认情况下，无法以明文形式提取密码，因为Spring Security会尽快将其删除**。

因此，我们需要配置Spring以使其保留明文版本的密码。此外，我们需要注册我们的编码委派：

```java
@Configuration
public class PasswordStorageWebSecurityConfigurer {

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.eraseCredentials(false)
                .userDetailsService(getUserDefaultDetailsService())
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    // ...
}
```

## 5. 总结

在本文中，我们介绍了5.x中提供的一些新密码编码功能，并了解了如何配置多个密码编码算法来对密码进行编码。此外，我们还演示了一种在不破坏现有密码编码的情况下更改密码编码的方法。

最后，我们描述了如何使用Spring事件来透明地更新加密的用户密码，从而使我们能够无缝地更改编码策略，而无需向用户透露。