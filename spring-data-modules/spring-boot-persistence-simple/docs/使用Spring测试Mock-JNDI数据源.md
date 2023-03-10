## 1. 概述

通常，在测试使用JNDI的应用程序时，我们可能希望使用Mock数据源而不是真实数据源。这是测试时的常见做法，目的是使我们的单元测试变得简单并与任何外部上下文完全分离。

在本教程中，**我们将展示如何使用Spring框架和Simple-JNDI库测试Mock JNDI数据源**。

在本教程中，我们只关注单元测试。但请务必查看我们关于如何[使用JPA和JNDI数据源创建Spring应用程序](https://www.baeldung.com/spring-persistence-jpa-jndi-datasource)的文章。

## 2. JNDI快速回顾

简而言之，**[JNDI](https://www.baeldung.com/jndi)将逻辑名称绑定到外部资源(如数据库连接)**。其主要思想是应用程序不需要知道关于已定义数据源的任何信息，除了它的JNDI名称。

简单地说，所有的命名操作都是相对于一个上下文的，所以要使用JNDI访问一个命名服务，我们需要先创建一个InitialContext对象。顾名思义，**InitialContext类封装了提供命名操作起点的初始(根)上下文**。

简而言之，根上下文充当入口点。没有它，JNDI就无法绑定或查找我们的资源。

## 3. 如何使用Spring测试JNDI数据源

Spring通过SimpleNamingContextBuilder提供了与JNDI的开箱即用集成。这个工具类提供了一种Mock JNDI环境以进行测试的好方法。

那么，让我们看看如何使用SimpleNamingContextBuilder类对JNDI数据源进行单元测试。

首先，**我们需要构建一个用于绑定和检索数据源对象的初始命名上下文**：

```java
@BeforeEach
void init() throws Exception {
    SimpleNamingContextBuilder.emptyActivatedContextBuilder();
    this.initContext = new InitialContext();
}
```

我们使用emptyActivatedContextBuilder()方法创建了根上下文，它提供了比构造函数更多的灵活性，因为它创建一个新的构建器或返回现有的构建器。

现在我们有了上下文，让我们实现一个单元测试，看看如何使用JNDI存储和检索JDBC DataSource对象：

```java
@Test
void whenMockJndiDataSource_thenReturnJndiDataSource() throws Exception {
	this.initContext.bind("java:comp/env/jdbc/datasource", new DriverManagerDataSource("jdbc:h2:mem:testdb"));
	DataSource ds = (DataSource) this.initContext.lookup("java:comp/env/jdbc/datasource");
    
	assertNotNull(ds.getConnection());
}
```

如我们所见，我们使用bind()方法将我们的JDBC DataSource对象映射到名称java:comp/env/jdbc/datasource。

然后，我们使用lookup()方法从我们的JNDI上下文中检索DataSource引用，使用我们之前用于绑定JDBC数据源对象的确切逻辑名称。

请注意，如果在上下文中找不到指定的对象，JNDI将简单地抛出异常。

值得一提的是，**自Spring 5.2以来，SimpleNamingContextBuilder类已被弃用，取而代之的是其他解决方案，例如[Simple-JNDI](https://github.com/h-thurow/Simple-JNDI)**。

## 4. 使用Simple-JNDI Mock和测试JNDI数据源

Simple-JNDI允许我们**将属性文件中定义的对象绑定到Mock的JNDI环境**。它非常支持从Java EE容器外部的JNDI获取类型为javax.sql.DataSource的对象。

那么，让我们看看如何使用它。首先，我们需要将Simple-JNDI依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.github.h-thurow</groupId>
    <artifactId>simple-jndi</artifactId>
    <version>0.23.0</version>
</dependency>
```
可以在[Maven Central](https://central.sonatype.com/artifact/com.github.h-thurow/simple-jndi/0.23.0)上找到最新版本的Simple-JNDI库。

接下来，我们将使用设置JNDI上下文所需的所有详细信息来配置Simple-JNDI。为此，**我们需要创建一个放在类路径中的jndi.properties文件**：

```properties
java.naming.factory.initial=org.osjava.sj.SimpleContextFactory
org.osjava.sj.jndi.shared=true
org.osjava.sj.delimiter=.
jndi.syntax.separator=/
org.osjava.sj.space=java:/comp/env
org.osjava.sj.root=src/main/resources/jndi
```

java.naming.factory.initial指定将用于创建初始上下文的上下文工厂类。

org.osjava.sj.jndi.shared=true意味着所有InitialContext对象将共享相同的内存。

如我们所见，我们使用org.osjava.sj.space属性将java:/comp/env定义为所有JNDI查找的起点。

同时使用org.osjava.sj.delimiter和jndi.syntax.separator属性背后的基本思想是避免[ENC](https://github.com/h-thurow/Simple-JNDI/issues/1)问题。

org.osjava.sj.root属性允许我们定义**存储属性文件的路径**。在我们的例子中，所有文件都位于src/main/resources/jndi文件夹下。

因此，让我们在datasource.properties文件中定义一个javax.sql.DataSource对象：

```properties
ds.type=javax.sql.DataSource
ds.driver=org.h2.Driver
ds.url=jdbc:jdbc:h2:mem:testdb
ds.user=sa
ds.password=password
```

现在，我们为单元测试创建一个InitialContext对象：

```java
@BeforeEach
void setup() throws Exception {
    this.initContext = new InitialContext();
}
```

最后，我们实现一个单元测试来**检索已经在datasource.properties文件中定义的DataSource对象**：

```java
@Test
void whenMockJndiDataSource_thenReturnJndiDataSource() throws Exception {
	String dsString = "org.h2.Driver::::jdbc:jdbc:h2:mem:testdb::::sa";
	Context envContext = (Context) this.initContext.lookup("java:/comp/env");
	DataSource ds = (DataSource) envContext.lookup("datasource/ds");
    
	assertEquals(dsString, ds.toString());
}
```

## 5. 总结

在本教程中，我们解释了如何应对在J2EE容器外测试JNDI的挑战。我们研究了如何使用Spring框架和Simple-JNDI库测试Mock JNDI数据源。