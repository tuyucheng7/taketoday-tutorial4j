## 1. 概述

LDAP目录服务器是读取优化的分层数据存储。通常，它们用于存储用户身份验证和授权所需的用户相关信息。

在本文中，我们将探索Spring LDAP API来验证和搜索用户，以及在目录服务器中创建和修改用户。同一组API可用于管理LDAP中的任何其他类型的条目。

## 2. Maven依赖

让我们首先添加所需的Maven依赖项：

```xml
<dependency>
    <groupId>org.springframework.ldap</groupId>
    <artifactId>spring-ldap-core</artifactId>
    <version>2.3.6.RELEASE</version>
</dependency>
```

这个依赖的最新版本可以在[spring-ldap-core](https://central.sonatype.com/artifact/org.springframework.ldap/spring-ldap-core/3.0.1)找到。

## 3. 数据准备

出于本文的目的，我们首先创建以下LDAP条目：

```ldif
ou=users,dc=example,dc=com (objectClass=organizationalUnit)
```

在这个节点下，我们将创建新用户、修改现有用户、验证现有用户和搜索信息。

## 4. Spring LDAP API

### 4.1 ContextSource & LdapTemplate Bean定义

ContextSource用于创建LdapTemplate。我们将在下一节中看到ContextSource在用户认证过程中的使用：

```java
@Bean
public LdapContextSource contextSource() {
      LdapContextSource contextSource = new LdapContextSource();

      contextSource.setUrl(env.getRequiredProperty("ldap.url"));
      contextSource.setBase(env.getRequiredProperty("ldap.partitionSuffix"));
      contextSource.setUserDn(env.getRequiredProperty("ldap.principal"));
      contextSource.setPassword(env.getRequiredProperty("ldap.password"));

      return contextSource;
      }
```

LdapTemplate用于创建和修改LDAP条目：

```java
@Bean
public LdapTemplate ldapTemplate() {
    return new LdapTemplate(contextSource());
}
```

### 4.2 使用Spring Boot

**当我们处理Spring Boot项目时，我们可以使用[Spring Boot Starter Data Ldap](https://search.maven.org/search?q=a:spring-boot-starter-data-ldap)依赖项，它会自动为我们检测LdapContextSource和LdapTemplate**。 

要启用自动配置，我们需要确保在pom.xml中将spring-boot-starter-data-ldap或spring-ldap-core定义为依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-ldap</artifactId>
</dependency>
```

要连接到LDAP，我们需要在application.properties中提供连接设置：

```properties
spring.ldap.url=ldap://localhost:18889
spring.ldap.base=dc=example,dc=com
spring.ldap.username=uid=admin,ou=system
spring.ldap.password=secret
```

然后我们可以将自动配置的LdapTemplate注入到所需的服务类中。

```java
@Autowired
private LdapTemplate ldapTemplate;
```

### 4.3 用户身份验证

现在让我们实现一个简单的逻辑来验证现有用户：

```java
public void authenticate(String username, String password) {
    contextSource
        .getContext("cn=" + username + ",ou=users," + env.getRequiredProperty("ldap.partitionSuffix"), password);
}
```

### 4.4 用户创建

接下来，让我们创建一个新用户并将密码的SHA哈希存储在LDAP中。

在身份验证时，LDAP服务器生成所提供密码的SHA哈希，并将其与存储的密码进行比较：

```java
public void create(String username, String password) {
    Name dn = LdapNameBuilder
        .newInstance()
        .add("ou", "users")
        .add("cn", username)
        .build();
    DirContextAdapter context = new DirContextAdapter(dn);

    context.setAttributeValues("objectclass", 
        new String[]{"top", "person", "organizationalPerson", "inetOrgPerson"});
    context.setAttributeValue("cn", username);
    context.setAttributeValue("sn", username);
    context.setAttributeValue("userPassword", digestSHA(password));

    ldapTemplate.bind(context);
}
```

digestSHA()是一个自定义方法，它返回所提供密码的SHA哈希的Base64编码字符串。

最后，使用LdapTemplate的bind()方法在LDAP服务器中创建一个条目。

### 4.5 用户修改

我们可以使用以下方法修改现有用户或条目：

```java
public void modify(String username, String password) {
    Name dn = LdapNameBuilder.newInstance()
        .add("ou", "users")
        .add("cn", username)
        .build();
    DirContextOperations context = ldapTemplate.lookupContext(dn);

    context.setAttributeValues("objectclass", 
          new String[]{"top", "person", "organizationalPerson", "inetOrgPerson"});
    context.setAttributeValue("cn", username);
    context.setAttributeValue("sn", username);
    context.setAttributeValue("userPassword", digestSHA(password));

    ldapTemplate.modifyAttributes(context);
}
```

lookupContext()方法用于查找提供的用户。

### 4.6 用户搜索

我们可以使用搜索过滤器搜索现有用户：

```java
public List<String> search(String username) {
    return ldapTemplate
        .search("ou=users", "cn=" + username, (AttributesMapper<String>) attrs -> (String) attrs.get("cn").get());
}
```

AttributesMapper用于从找到的条目中获取所需的属性值。在内部，Spring LdapTemplate为找到的所有条目调用AttributesMapper并创建属性值列表。

## 5. 测试

spring-ldap-test提供了一个基于ApacheDS 1.5.5的嵌入式LDAP服务器。要设置嵌入式LDAP服务器进行测试，我们需要配置以下Spring bean：

```java
@Bean
public TestContextSourceFactoryBean testContextSource() {
    TestContextSourceFactoryBean contextSource = new TestContextSourceFactoryBean();
    
    contextSource.setDefaultPartitionName(env.getRequiredProperty("ldap.partition"));
    contextSource.setDefaultPartitionSuffix(env.getRequiredProperty("ldap.partitionSuffix"));
    contextSource.setPrincipal(env.getRequiredProperty("ldap.principal"));
    contextSource.setPassword(env.getRequiredProperty("ldap.password"));
    contextSource.setLdifFile(resourceLoader.getResource(env.getRequiredProperty("ldap.ldiffile")));
    contextSource.setPort(Integer.valueOf(env.getRequiredProperty("ldap.port")));
    return contextSource;
}
```

让我们用JUnit测试我们的用户搜索方法：

```java
@Test
public void givenLdapClient_whenCorrectSearchFilter_thenEntriesReturned() {
    List<String> users = ldapClient.search(SEARCH_STRING);
 
    assertThat(users, Matchers.containsInAnyOrder(USER2, USER3));
}
```

## 6. 总结

在本文中，我们介绍了Spring LDAP API，并开发了用于在LDAP服务器中进行用户身份验证、用户搜索、用户创建和修改的简单方法。

与往常一样，完整的源代码在[此Github项目](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/spring-security-modules/spring-security-ldap)中可用。测试是在Maven Profile “live”下创建的，因此可以使用选项“-P live”运行。