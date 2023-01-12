## 1. 简介

在本教程中，我们将了解如何使用[Apache ShiroJava安全框架实现](https://www.baeldung.com/apache-shiro)细粒度的基于权限的访问控制。

## 2.设置

我们将使用与介绍 Shiro 时相同的设置——也就是说，我们只会将[shiro-core](https://search.maven.org/search?q=g:org.apache.shiro AND a:shiro-core&core=gav)模块添加到我们的依赖项中：

```xml
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-core</artifactId>
    <version>1.4.1</version>
</dependency>
```

此外，出于测试目的，我们将通过将以下shiro.ini文件放置在类路径的根目录中来使用一个简单的 INI 领域：

```plaintext
[users]
jane.admin = password, admin
john.editor = password2, editor
zoe.author = password3, author
 
[roles]
admin = 
editor = articles:
author = articles:create, articles:edit
```

然后，我们将使用上述领域初始化 Shiro：

```java
IniRealm iniRealm = new IniRealm("classpath:shiro.ini");
SecurityManager securityManager = new DefaultSecurityManager(iniRealm);
SecurityUtils.setSecurityManager(securityManager);
```

## 3.角色和权限

通常，当我们谈论身份验证和授权时，我们会围绕用户和角色的概念展开。

特别是，角色是应用程序或服务的用户的横切类。因此，所有具有特定角色的用户都将有权访问某些资源和操作，并且可能对应用程序或服务的其他部分的访问受到限制。

角色集通常是预先设计的，很少更改以适应新的业务需求。然而，角色也可以动态定义——例如，由管理员定义。

使用 Shiro，我们有多种方法来测试用户是否具有特定角色。最直接的方法是使用hasRole方法：

```java
Subject subject = SecurityUtils.getSubject();
if (subject.hasRole("admin")) {       
    logger.info("Welcome Admin");              
}
```

### 3.1. 权限

但是，如果我们通过测试用户是否具有特定角色来检查授权，则会出现问题。事实上，我们正在硬编码角色和权限之间的关系。换句话说，当我们想要授予或撤销对资源的访问权限时，我们将不得不更改源代码。当然，这也意味着重建和重新部署。

我们可以做得更好；这就是为什么我们现在要介绍权限的概念。权限代表软件可以做什么我们可以授权或拒绝，而不是代表谁可以做。例如，“编辑当前用户的个人资料”、“批准文档”或“创建新文章”。

Shiro 对权限的假设很少。在最简单的情况下，权限是纯字符串：

```java
Subject subject = SecurityUtils.getSubject();
if (subject.isPermitted("articles:create")) {
    //Create a new article
}
```

请注意，权限的使用在 Shiro 中是完全可选的。

### 3.2. 将权限关联到用户

Shiro 具有将权限与角色或个人用户相关联的灵活模型。然而，典型的领域，包括我们在本教程中使用的简单 INI 领域，只将权限与角色相关联。

因此，由Principal标识的用户具有多个角色，每个角色都有多个 Permission。

例如，我们可以看到在我们的 INI 文件中，用户zoe.author具有author角色，并赋予他们articles:create和 articles:edit权限：

```plaintext
[users]
zoe.author = password3, author
#Other users...

[roles]
author = articles:create, articles:edit
#Other roles...
```

类似地，其他领域类型(例如内置的 JDBC 领域)可以配置为将权限关联到角色。

## 4.通配符权限

Shiro 中权限的默认实现是通配符权限，一种对多种权限方案的灵活表示。

我们用字符串表示 Shiro 中的通配符权限。权限字符串由一个或多个由冒号分隔的组件组成，例如：

```plaintext
articles:edit:1
```

字符串每个部分的含义取决于应用程序，因为 Shiro 不强制执行任何规则。但是，在上面的示例中，我们可以非常清楚地将字符串解释为层次结构：

1.  我们公开的资源类别(文章)
2.  对此类资源的操作(编辑)
3.  我们要允许或拒绝操作的特定资源的 ID

这种 resource:action:id 的三层结构是 Shiro 应用程序中的常见模式，因为它在表示许多不同场景时既简单又有效。

因此，我们可以重新访问我们之前的示例以遵循此方案：

```java
Subject subject = SecurityUtils.getSubject();
if (subject.isPermitted("articles:edit:123")) {
    //Edit article with id 123
}
```

请注意，通配符权限字符串中的组件数不必为三个，即使通常情况下三个组件也是如此。

### 4.1. 权限暗示和实例级粒度

当我们将通配符权限与 Shiro 权限的另一个特性——隐含结合起来时，通配符权限就会大放异彩。

当我们测试角色时，我们测试的是确切的成员资格：一个Subject要么有一个特定的角色，要么没有。换句话说，Shiro 测试角色的平等性。

另一方面，当我们测试权限时，我们测试的是暗示：Subject的权限是否暗示了我们正在测试的权限？

具体含义取决于权限的实现。实际上，对于通配符权限，顾名思义，隐含的是部分字符串匹配，具有通配符成分的可能性。

因此，假设我们将以下权限分配给author角色：

```plaintext
[roles]
author = articles:
```

然后，每个具有作者 角色的人都将被允许对文章进行所有可能的操作：

```java
Subject subject = SecurityUtils.getSubject();
if (subject.isPermitted("articles:create")) {
    //Create a new article
}
```

也就是说，字符串articles:将匹配第一个组件为articles 的任何通配符权限。

通过这种方案，我们既可以分配非常具体的权限——对具有给定 id 的特定资源的特定操作——也可以分配广泛的权限，例如编辑任何文章或对任何文章执行任何操作。

当然，出于性能原因，由于隐含不是简单的相等比较，我们应该始终针对最具体的权限进行测试：

```java
if (subject.isPermitted("articles:edit:1")) { //Better than "articles:"
    //Edit article
}
```

## 5.自定义权限实现

让我们简要介绍一下权限自定义。尽管通配符权限涵盖了广泛的场景，但我们可能希望将它们替换为为我们的应用程序定制的解决方案。

假设我们需要对路径的权限进行建模，以便对路径的权限意味着对所有子路径的权限。实际上，我们可以使用通配符权限来完成任务，但让我们忽略它。

那么，我们需要什么？

1.  a权限实现
2.  告诉四郎这件事

让我们看看如何实现这两点。

### 5.1. 编写权限实现

Permission实现是一个只有一个方法的类——意味着：

```java
public class PathPermission implements Permission {

    private final Path path;

    public PathPermission(Path path) {
        this.path = path;
    }

    @Override
    public boolean implies(Permission p) {
        if(p instanceof PathPermission) {
            return ((PathPermission) p).path.startsWith(path);
        }
        return false;
    }
}
```

如果 这意味着另一个权限对象，则该方法返回true ，否则返回false。

### 5.2. 告诉 Shiro 我们的实现

然后，有多种方法可以将Permission 实现集成到 Shiro 中，但最直接的方法是将自定义PermissionResolver注入到我们的Realm中：

```java
IniRealm realm = new IniRealm();
Ini ini = Ini.fromResourcePath(Main.class.getResource("/com/.../shiro.ini").getPath());
realm.setIni(ini);
realm.setPermissionResolver(new PathPermissionResolver());
realm.init();

SecurityManager securityManager = new DefaultSecurityManager(realm);
```

PermissionResolver负责将我们权限的字符串表示形式转换为实际的Permission对象：

```java
public class PathPermissionResolver implements PermissionResolver {
    @Override
    public Permission resolvePermission(String permissionString) {
        return new PathPermission(Paths.get(permissionString));
    }
}
```

我们必须使用基于路径的权限修改我们之前的shiro.ini ：

```plaintext
[roles]
admin = /
editor = /articles
author = /articles/drafts
```

然后，我们将能够检查路径的权限：

```java
if(currentUser.isPermitted("/articles/drafts/new-article")) {
    log.info("You can access articles");
}
```

请注意，我们在这里以编程方式配置一个简单的领域。在典型的应用程序中，我们将使用shiro.ini文件或其他方式(如 Spring)来配置 Shiro 和领域。真实世界的 shiro.ini文件可能包含：

```plaintext
[main]
permissionResolver = com.baeldung.shiro.permissions.custom.PathPermissionResolver
dataSource = org.apache.shiro.jndi.JndiObjectFactory
dataSource.resourceName = java://app/jdbc/myDataSource

jdbcRealm = org.apache.shiro.realm.jdbc.JdbcRealm
jdbcRealm.dataSource = $dataSource 
jdbcRealm.permissionResolver = $permissionResolver
```

## 六. 总结

在本文中，我们回顾了 Apache Shiro 如何实现基于权限的访问控制。