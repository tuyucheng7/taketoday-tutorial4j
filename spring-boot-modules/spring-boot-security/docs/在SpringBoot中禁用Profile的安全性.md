## 1. 概述

在本教程中，我们将了解如何**为给定的Profile禁用Spring Security**。

## 2. 配置

首先，让我们定义一个简单地允许所有请求的安全配置。

我们可以通过注册WebSecurityCustomizer bean并忽略对所有路径的请求来实现这一点：

```java
@Configuration
public class ApplicationNoSecurity {

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			.antMatchers("/**");
	}
}
```

请记住，这不仅会关闭身份验证，**还会关闭XSS等任何安全保护**。

## 3. 指定Profile

现在我们只想为给定的Profile激活此配置。

假设我们有一个不需要安全性的单元测试套件，如果此测试套件使用名为“test”的Profile运行，我们可以简单地**使用@Profile注解标注我们的配置**：

```java
@Configuration
@Profile("test")
public class ApplicationNoSecurity {

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			.antMatchers("/**");
	}
}
```

因此，我们的测试环境会有所不同，这可能是我们不希望的。或者，我们可以保留安全性并使用[Spring Security的测试支持](https://www.baeldung.com/spring-security-method-security#testing-method-security)。

## 4. 总结

在本教程中，我们说明了如何为特定的Profile禁用Spring Security。