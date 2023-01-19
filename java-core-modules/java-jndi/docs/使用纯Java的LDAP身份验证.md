## 1. 概述

在本文中，我们将介绍如何使用纯Java通过[LDAP](https://ldap.com/learn-about-ldap/)对用户进行身份验证。此外，我们将探讨如何搜索用户的[专有名称](https://ldap.com/ldap-dns-and-rdns/)(DN)。这很重要，因为 LDAP 需要 DN 来验证用户。

为了进行搜索和用户身份验证，我们将使用Java命名和目录接口 ( [JNDI](https://www.baeldung.com/jndi) ) 的目录服务访问功能。

首先，我们将简要讨论什么是 LDAP 和 JNDI。然后我们将讨论如何通过 JNDI API 使用 LDAP 进行身份验证。

## 2.什么是LDAP？

轻量级目录访问协议 (LDAP) 定义了客户端发送请求和接收来自目录服务的响应的方式。我们将使用此协议的目录服务称为 LDAP 服务器。

LDAP 服务器提供的数据存储在基于[X.500 的](https://docs.oracle.com/javase/jndi/tutorial/ldap/models/x500.html)信息模型中。这是一组用于电子目录服务的计算机网络标准。

## 3. 什么是 JNDI？

JNDI 为应用程序提供标准 API 以发现和访问命名和目录服务。它的根本目的是为应用程序提供一种访问组件和资源的方法。这是本地和网络上的。

命名服务是此功能的基础，因为它们提供对分层命名空间中按名称的服务、数据或对象的单点访问。为这些本地或网络可访问资源中的每一个指定的名称是在托管命名服务的服务器上配置的。

我们可以使用 JNDI 的命名服务接口访问目录服务，如 LDAP。这是因为目录服务只是一种特殊类型的命名服务，它使每个命名条目都具有与其关联的属性列表。

除了属性之外，每个目录条目可能有一个或多个子项。这使得条目可以分层链接。在 JNDI 中，目录条目的子项表示为其父上下文的子上下文。

JNDI API 的一个主要优点是它独立于任何底层服务提供者实现，例如 LDAP。因此，我们可以使用 JNDI 来访问 LDAP 目录服务，而无需使用特定于协议的 API。

使用 JNDI 不需要外部库，因为它是JavaSE 平台的一部分。此外，作为JavaEE 的核心技术，它被广泛用于实现企业应用程序。

## 4. 使用 LDAP 进行身份验证的 JNDI API 概念

在讨论示例代码之前，让我们介绍一些有关使用 JNDI API 进行基于 LDAP 的身份验证的基础知识。

要连接到 LDAP 服务器，我们首先需要创建一个 JNDI [InitialDirContext](https://docs.oracle.com/en/java/javase/11/docs/api/java.naming/javax/naming/directory/InitialDirContext.html)对象。这样做时，我们需要将环境属性作为[哈希](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Hashtable.html)表传递到其构造函数中以对其进行配置。

其中，我们需要为我们希望用于身份验证的用户名和密码向此哈希表添加属性。为此，我们必须将用户的 DN 和密码分别设置为Context.SECURITY_PRINCIPAL和Context.SECURITY_CREDENTIALS属性。

InitialDirContext实现主要的 JNDI 目录服务接口[DirContext 。](https://docs.oracle.com/en/java/javase/11/docs/api/java.naming/javax/naming/directory/DirContext.html)通过这个接口，我们可以使用我们的新上下文在 LDAP 服务器上执行各种目录服务操作。这些包括将名称和属性绑定到对象以及搜索目录条目。

值得注意的是，JNDI 返回的对象将具有与其底层 LDAP 条目相同的名称和属性。因此，要搜索一个条目，我们可以使用它的名称和属性作为条件来查找它。

通过 JNDI 检索目录条目后，我们可以使用[Attributes](https://docs.oracle.com/en/java/javase/11/docs/api/java.naming/javax/naming/directory/Attributes.html)接口查看其属性。此外，我们可以使用Attribute接口来检查它们中的每一个。

## 5. 如果我们没有用户的 DN 怎么办？

有时我们没有立即可用的用户 DN 来进行身份验证。为了解决这个问题，我们首先需要使用管理员凭据创建一个InitialDirContext。之后，我们可以使用它从目录服务器中搜索相关用户并获取他的 DN。

然后一旦我们有了用户的 DN，我们就可以通过创建一个新的InitialDirContext来验证他的身份，这次使用他的凭据。为此，我们首先需要在环境属性中设置用户的 DN 和密码。之后，我们需要在创建时将这些属性传递给InitDirContext的构造函数。

现在我们已经讨论了使用 JNDI API 通过 LDAP 对用户进行身份验证，让我们看一下示例代码。

## 6. 示例代码

在我们的示例中，我们将使用[ApacheDS](https://directory.apache.org/apacheds/)目录服务器的[嵌入式版本](https://directory.apache.org/apacheds/advanced-ug/7-embedding-apacheds.html)。这是一个使用Java构建的 LDAP 服务器，旨在在单元测试中以嵌入式模式运行。

让我们看看如何设置它。

### 6.1. 设置嵌入式 ApacheDS 服务器

要使用嵌入式 ApacheDS 服务器，我们需要定义 Maven[依赖项](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.directory.server" AND a%3A"apacheds-test-framework")：

```xml
<dependency>
    <groupId>org.apache.directory.server</groupId>
    <artifactId>apacheds-test-framework</artifactId>
    <version>2.0.0.AM26</version>
    <scope>test</scope>
</dependency>

```

接下来，我们需要使用 JUnit 4 创建单元测试类。要在此类中使用嵌入式 ApacheDS 服务器，我们必须声明它从 ApacheDS 库扩展AbstractLdapTestUnit 。由于这个库还不兼容 JUnit 5，我们需要使用 JUnit 4。

此外，我们需要在单元测试类声明上方包含[Java 注解以配置服务器。](https://directory.apache.org/apacheds/advanced-ug/7-embedding-apacheds.html#basic-test)[我们可以从完整的代码示例](https://github.com/eugenp/tutorials/blob/master/core-java-modules/core-java-jndi/src/test/java/com/baeldung/jndi/ldap/auth/JndiLdapAuthManualTest.java)中看到为它们提供哪些值，我们将在稍后探讨。

最后，我们还需要将[users.ldif](https://github.com/eugenp/tutorials/blob/master/core-java-modules/core-java-jndi/src/test/resources/users.ldif)添加到类路径中。这样一来，当我们运行代码示例时，ApacheDS 服务器就可以从该文件中加载[LDIF格式的目录条目。](https://datatracker.ietf.org/doc/html/rfc2849)这样做时，服务器将加载用户Joe Simms的条目。

接下来，我们讨论将对用户进行身份验证的示例代码。要针对 LDAP 服务器运行它，我们需要将代码添加到单元测试类中的方法中。这将使用文件中定义的 DN 和密码通过 LDAP 对 Joe 进行身份验证。

### 6.2. 验证用户

为了对用户Joe Simms进行身份验证，我们需要创建一个新的InitialDirContext对象。这将创建到目录服务器的连接，并使用用户的 DN 和密码通过 LDAP 对用户进行身份验证。

为此，我们首先需要将这些环境属性添加到 Hashtable中：

```java
Hashtable<String, String> environment = new Hashtable<String, String>();

environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
environment.put(Context.PROVIDER_URL, "ldap://localhost:10389");
environment.put(Context.SECURITY_AUTHENTICATION, "simple");
environment.put(Context.SECURITY_PRINCIPAL, "cn=Joe Simms,ou=Users,dc=baeldung,dc=com");
environment.put(Context.SECURITY_CREDENTIALS, "12345");
```

接下来，在一个名为authenticateUser的新方法中，我们将通过将环境属性传递到其构造函数来创建InitialDirContext对象。然后，我们将关闭上下文以释放资源：

```java
DirContext context = new InitialDirContext(environment);
context.close();
```

最后，我们将对用户进行身份验证：

```java
assertThatCode(() -> authenticateUser(environment)).doesNotThrowAnyException();
```

现在我们已经介绍了用户身份验证成功的情况，让我们检查它何时失败。

### 6.3. 处理用户认证失败

应用与之前相同的环境属性，让我们使用错误的密码使身份验证失败：

```java
environment.put(Context.SECURITY_CREDENTIALS, "wrongpassword");
```

然后，我们将检查使用此密码验证用户是否按预期失败：

```java
assertThatExceptionOfType(AuthenticationException.class).isThrownBy(() -> authenticateUser(environment));
```

接下来，让我们讨论在没有用户 DN 的情况下如何对用户进行身份验证。

### 6.4. 以管理员身份查找用户的 DN

有时，当我们想要对用户进行身份验证时，手边并没有他的 DN。在这种情况下，我们首先需要创建一个具有管理员凭据的目录上下文来查找用户的 DN，然后使用它对用户进行身份验证。

和以前一样，我们首先需要在Hashtable中添加一些环境属性。但这次，我们将使用管理员的 DN 作为Context.SECURITY_PRINCIPAL，连同他的默认管理员密码作为Context.SECURITY_CREDENTIALS属性：

```java
Hashtable<String, String> environment = new Hashtable<String, String>();

environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
environment.put(Context.PROVIDER_URL, "ldap://localhost:10389");
environment.put(Context.SECURITY_AUTHENTICATION, "simple");
environment.put(Context.SECURITY_PRINCIPAL, "uid=admin,ou=system");
environment.put(Context.SECURITY_CREDENTIALS, "secret");
```

接下来，我们将创建一个具有这些属性的InitialDirContext对象：

```java
DirContext adminContext = new InitialDirContext(environment);
```

这将创建一个目录上下文，连接到以管理员身份验证的服务器。这为我们提供了搜索用户 DN 的安全权限。

[现在我们将根据用户的 CN(即他的常用名)](https://ldapwiki.com/wiki/CommonName)定义搜索过滤器。

```java
String filter = "(&(objectClass=person)(cn=Joe Simms))";
```

然后，使用此过滤器搜索用户，我们将创建一个[SearchControls](https://docs.oracle.com/en/java/javase/11/docs/api/java.naming/javax/naming/directory/SearchControls.html)对象：

```java
String[] attrIDs = { "cn" };
SearchControls searchControls = new SearchControls();
searchControls.setReturningAttributes(attrIDs);
searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
```

接下来，我们将使用我们的过滤器和SearchControls搜索用户 ：

```java
NamingEnumeration<SearchResult> searchResults
  = adminContext.search("dc=baeldung,dc=com", filter, searchControls);
  
String commonName = null;
String distinguishedName = null;
if (searchResults.hasMore()) {
    
    SearchResult result = (SearchResult) searchResults.next();
    Attributes attrs = result.getAttributes();
    
    distinguishedName = result.getNameInNamespace();
    assertThat(distinguishedName, isEqualTo("cn=Joe Simms,ou=Users,dc=baeldung,dc=com")));

    commonName = attrs.get("cn").toString();
    assertThat(commonName, isEqualTo("cn: Joe Simms")));
}

```

既然我们有了用户的 DN，就让我们对用户进行身份验证。

### 6.5. 使用用户查找的 DN 进行身份验证

现在有了用户的 DN 来进行身份验证，我们将用用户的替换现有环境属性中的管理员 DN 和密码：

```java
environment.put(Context.SECURITY_PRINCIPAL, distinguishedName);
environment.put(Context.SECURITY_CREDENTIALS, "12345");
```

然后，有了这些，让我们对用户进行身份验证：

```java
assertThatCode(() -> authenticateUser(environment)).doesNotThrowAnyException();
```

最后，我们将关闭管理员上下文以释放资源：

```java
adminContext.close();
```

## 七、总结

在本文中，我们讨论了如何使用 JNDI 通过 LDAP 使用用户的 DN 和密码来验证用户。

此外，我们研究了如果没有 DN，如何查找它。