## 1. 概述

在本教程中，我们将介绍AuthenticationManagerResolver，然后展示如何将其用于Basic和OAuth2身份验证流程。

## 2. 什么是AuthenticationManager？

简单来说，AuthenticationManager就是身份验证的主要策略接口。

如果输入身份验证的主体有效且经过验证，则AuthenticationManager#authenticate返回一个Authentication实例，
并将authenticated标志设置为true。否则，如果主体无效，它将抛出AuthenticationException。
对于最后一种情况，如果无法决定，则返回null。

ProviderManager是AuthenticationManager的默认实现。它将身份验证过程委托给AuthenticationProvider实例集合。

如果继承WebSecurityConfigurerAdapter，我们可以设置全局或本地AuthenticationManager。
对于本地AuthenticationManager，我们可以重写configure(AuthenticationManagerBuilder)。

AuthenticationManagerBuilder是一个工具类，它简化了UserDetailsService、
AuthenticationProvider和其他依赖项的设置，以构建AuthenticationManager。

对于全局AuthenticationManager，我们应该将AuthenticationManager定义为bean。

## 3. 为什么是AuthenticationManagerResolver？

AuthenticationManagerResolver允许Spring根据上下文选择AuthenticationManager。
这是Spring Security 5.2.0版本中添加的新功能：

```java
public interface AuthenticationManagerResolver<C> {
    AuthenticationManager resolve(C context);
}
```

AuthenticationManagerResolver#resolve可以基于泛型上下文返回AuthenticationManager的实例。
换句话说，如果我们想根据类来解析AuthenticationManager，我们可以将类设置为上下文。

**Spring Security在身份验证流程中集成了AuthenticationManagerResolver，
并将HttpServletRequest和ServerWebExchange作为上下文**。

## 4. 使用场景

让我们看看如何在实践中使用AuthenticationManagerResolver。

假设一个系统有两组用户：员工(employees)和客户(customers)。这两组用户具有特定的身份验证逻辑并具有单独的数据库存储。
此外，这两个组中的任何一个用户都只能调用他们的相关URL。

## 5. AuthenticationManagerResolver是如何工作的？

我们可以在需要动态选择AuthenticationManager的任何地方使用AuthenticationManagerResolver，
但在本教程中，我们的重点是在内置身份验证流程中使用它。

首先，让我们配置一个AuthenticationManagerResolver，然后将其用于Basic和OAuth2身份验证。

### 5.1 设置AuthenticationManagerResolver

让我们首先创建一个用于安全配置的类。我们应该继承WebSecurityConfigurerAdapter：

```java

@Configuration
public class CustomWebSecurityConfigurer extends WebSecurityConfigurerAdapter {
    // ...
}
```

然后，让我们添加一个为客户返回AuthenticationManager的方法：

```java

@Configuration
public class CustomWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    public AuthenticationManager customersAuthenticationManager() {
        return authentication -> {
            if (isCustomer(authentication)) {
                return new UsernamePasswordAuthenticationToken(
                        authentication.getPrincipal(),
                        authentication.getCredentials(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
            }
            throw new UsernameNotFoundException(authentication.getPrincipal().toString());
        };
    }

    private boolean isCustomer(Authentication authentication) {
        return authentication.getPrincipal().toString().startsWith("customer");
    }
}
```

对于员工的AuthenticationManager在逻辑上是相同的，只是我们将isCustomer()替换为isEmployee()：

```java

@Configuration
public class CustomWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    public AuthenticationManager employeesAuthenticationManager() {
        return authentication -> {
            if (isEmployee(authentication)) {
                return new UsernamePasswordAuthenticationToken(
                        authentication.getPrincipal(),
                        authentication.getCredentials(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
            }
            throw new UsernameNotFoundException(authentication.getPrincipal().toString());
        };
    }

    private boolean isEmployee(Authentication authentication) {
        return authentication.getPrincipal().toString().startsWith("employee");
    }
}
```

最后，我们添加一个AuthenticationManagerResolver，它根据请求的URL进行解析，返回相应的AuthenticationManager：

```java

@Configuration
public class CustomWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    public AuthenticationManagerResolver<HttpServletRequest> resolver() {
        return request -> {
            if (request.getPathInfo().startsWith("employee"))
                return employeesAuthenticationManager();
            return customersAuthenticationManager();
        };
    }
}
```

### 5.2 Basic身份验证

我们可以使用AuthenticationFilter动态解析每个请求的AuthenticationManager。
AuthenticationFilter在5.2版本中添加到Spring Security。

如果我们将它添加到我们的Security过滤器链中，那么对于每个匹配的请求，它首先检查它是否可以提取任何authentication对象。
如果是，则它要求AuthenticationManagerResolver提供合适的AuthenticationManager并继续流程。

首先，让我们在CustomWebSecurityConfigurer中添加一个方法来创建AuthenticationFilter：

```java

@Configuration
public class CustomWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    public AuthenticationFilter authenticationFilter() {
        AuthenticationFilter filter = new AuthenticationFilter(resolver(), authenticationConverter());
        filter.setSuccessHandler((request, response, auth) -> {

        });
        return filter;
    }

    private AuthenticationConverter authenticationConverter() {
        return new BasicAuthenticationConverter();
    }
}
```

**将AuthenticationFilter#successHandler设置为无操作SuccessHangler的原因是为了防止成功身份验证后的默认重定向行为**。

然后，我们可以通过在CustomWebSecurityConfigurer中重写
WebSecurityConfigurerAdapter#configure(HttpSecurity)来将此过滤器添加到我们的Security过滤器链中：

```java

@Configuration
public class CustomWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(authenticationFilter(), BasicAuthenticationFilter.class);
    }
}
```

### 5.3 用于OAuth2身份验证

## 7. 总结

在本文中，我们在一个简单的场景中使用AuthenticationManagerResolver进行Basic和OAuth2身份验证。

而且，我们还介绍了ReactiveAuthenticationManagerResolver在响应式Spring Web应用程序中用于Basic和OAuth2身份验证的用法。