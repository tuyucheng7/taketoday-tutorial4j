## 1. 概述

[Spring Boot CLI](https://www.baeldung.com/spring-boot-cli)(命令行界面)是一个Spring Boot工具，用于从命令提示符运行和测试Spring Boot应用程序。**该工具提供了一个非常有用的密码编码功能，它的主要目的是避免暴露纯文本密码，并能够生成和使用编码密码**。

在本教程中，我们将深入Spring Security世界，学习**如何使用Spring Boot CLI对密码进行编码**。

## 2. 密码编码

密码编码只是一种以能够保存在存储介质上的二进制格式表示密码的方法。我们可以使用Spring Security对密码进行编码，也可以委托给Spring Boot CLI。

### 2.1 Spring Security PasswordEncoder

Spring Security提供了PasswordEncoder接口，它有大量的实现，例如StandardPasswordEncoder和BCryptPasswordEncoder。

此外，Spring Security推荐使用BCryptPasswordEncoder，它基于一个随机生成的盐的强大算法。在以前的框架版本中，可以使用MD5PasswordEncoder或SHAPasswordEncoder类，但由于其算法的弱点，它们现在已被弃用。

此外，这两个类强制开发人员将盐作为构造函数参数传递，而BCryptPasswordEncoder将在内部生成随机盐。BCryptPasswordEncoder生成的字符串大小为60个字符，因此基列应接受此大小的字符串。

另一方面，StandardPasswordEncoder类基于SHA-256算法。

显然，将在第三方系统中创建的用户密码必须根据Spring Security中选择的编码类型进行编码，才能成功进行身份验证。

### 2.2 Spring Boot CLI密码编码器

Spring Boot CLI附带了一些命令，其中一个是encodepassword。此命令允许对密码进行编码以用于Spring Security。简而言之，**Spring Boot CLI encodepassword命令可以使用以下简单语法将原始密码直接转换为加密密码**：

```shell
spring encodepassword [options] <password to encode>
```

**值得注意的是，从Spring Security 5.0开始，密码编码的默认机制是[BCrypt](https://www.baeldung.com/spring-security-5-default-password-encoder)**。

## 3. 示例

为了阐明Spring Boot CLI密码编码机制的使用，我们将使用基本身份验证服务通过用户名和密码对用户进行身份验证。对于这个例子，我们将简单地使用Spring Security自动配置。

这个想法是为了避免暴露纯文本密码，而是使用编码密码。现在让我们看看如何使用encodepassword命令通过Spring Boot CLI对密码进行编码。我们只需要在命令提示符下执行以下命令：

```text
spring encodepassword tuyuchengPassword
```

上述命令的结果是一个用BCrypt编码的密码，很难破解。例如，在Spring Boot Security配置中使用的编码密码如下所示：

```shell
{bcrypt}$2a$10$JqNiCnZ1B2JQkNXTyAGR7OqrHN5v.NxouIyQyRHZ.wvAdZQbVWLg.
```

现在让我们通过修改属性文件来自定义默认安全配置。例如，我们可以通过添加自己的用户名和密码来覆盖默认的用户名和密码。

我们的编码密码进入spring.security.user.password属性：

```yaml
spring:
  security:
    user:
      name: tuyucheng
      password: '{bcrypt}$2a$10$JqNiCnZ1B2JQkNXTyAGR7OqrHN5v.NxouIyQyRHZ.wvAdZQbVWLg.'
```

## 4. 总结

在本文中，我们学习了如何使用Spring Boot CLI对密码进行编码。此外，我们使用Spring Security简单身份验证来演示如何使用编码后的密码。主要目的是避免暴露纯文本密码并能够轻松生成编码密码。