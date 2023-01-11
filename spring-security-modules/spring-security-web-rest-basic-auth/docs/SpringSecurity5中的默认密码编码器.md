## 1. 概述

在Spring Security 4中，可以使用基于内存的身份验证以纯文本形式存储密码。

版本5中对密码管理过程进行了重大改革，引入了一种更安全的默认密码编码和解码机制。这意味着如果你的Spring应用程序以纯文本形式存储密码，升级到Spring Security 5可能会导致问题。

在这个简短的教程中，我们将描述其中一个可能的潜在问题并演示解决方案。

## 2. Spring Security 4

首先，下面是一个标准的Security配置，该配置提供简单的内存验证(对Spring 4有效)：

```java
@Configuration
public class InMemoryAuthWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("spring")
                .password("secret")
                .roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/private/")
                .authenticated()
                .antMatchers("/public/")
                .permitAll()
                .and()
                .httpBasic();
    }
}
```

此配置定义为所有/private/请求需要身份验证，以及/public/下的所有内容可以公共访问。

如果我们在Spring Security 5下使用相同的配置，当我们启动应用发送请求时，将出现以下错误：

```shell
java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null"
```

该错误告诉我们，**由于没有为我们的内存身份验证配置密码编码器，因此无法解码给定的密码**。

### 3. Spring Security 5

我们可以通过使用PasswordEncoderFactories类定义DelegatingPasswordEncoder来解决此错误。

我们使用此编码器通过AuthenticationManagerBuilder配置我们的用户：

```java
@Configuration
public class InMemoryAuthWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserDetails user = User.withUsername("spring")
                .password(encoder.encode("secret"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
```

现在，通过这种配置，我们使用BCrypt以下列格式存储我们的内存密码：

```shell
{bcrypt}$2a$10$MF7hYnWLeLT66gNccBgxaONZHbrSMjlUofkp50sSpBw2PJjUqU.zS
```

尽管我们可以定义自己的一组密码编码器，但建议坚持使用PasswordEncoderFactories中提供的默认编码器。自Spring Security 5.7.0-M2版本以来，Spring不赞成使用WebSecurityConfigureAdapter，并建议在没有WebSecurityConfigure Adapter的情况下创建配置，[本文]()将对此进行了更详细的解释。

## 4. NoOpPasswordEncoder

如果出于任何原因，我们不想对配置的密码进行编码，我们可以使用NoOpPasswordEncoder。为此，我们只需在提供给password()方法的密码字符串前加上{noop}前缀标识符：

```java
@Configuration
public class InMemoryNoOpAuthWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("spring")
                .password("{noop}secret")
                .roles("USER");
    }
}
```

这样，当Spring Security将用户提供的密码与我们上面配置的密码进行比较时，Spring Security将在背后使用NoOpPasswordEncoder。

但是请注意，**我们绝不应该在生产环境下使用这种方法**！正如官方文档所说，**NoOpPasswordEncoder已被弃用，表明它是一个遗留实现，并且使用它被认为是不安全的**。

## 5. 更换现有密码编码

我们可以通过以下方式将现有密码更新为推荐的Spring Security 5标准：

+ 使用编码的值更新纯文本存储的密码：

```java
String encoded = new BCryptPasswordEncoder().encode(plainTextPassword);
```

+ 使用已知的编码器标识符为哈希存储密码添加前缀：

```java
{bcrypt}$2a$10$MF7hYnWLeLT66gNccBgxaONZHbrSMjlUofkp50sSpBw2PJjUqU.zS
{sha256}97cde38028ad898ebc02e690819fa220e88c62e0699403e94fff291cfffaf8410849f27605abcbc0
```

+ 当存储密码的编码机制未知时，请求用户更新他们的密码。

## 6. 总结

在这个教程中，我们介绍了使用新的密码存储机制将有效的Spring 4内存验证配置更新为Spring 5。