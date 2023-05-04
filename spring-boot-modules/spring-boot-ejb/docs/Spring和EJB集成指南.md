## 1. 概述

在本文中，我们将展示如何**集成Spring和远程Enterprise Java Beans(EJB)**。

为此，我们将创建一些EJB和必要的远程接口，然后在JEE容器中运行它们。之后，我们将启动我们的Spring应用程序，并使用远程接口实例化我们的bean，以便它们可以执行远程调用。

如果对EJB是什么或它们如何工作有任何疑问，我们已经在[此处](https://www.baeldung.com/ejb-intro)发布了关于该主题的介绍性文章。

## 2. EJB设置

我们需要创建远程接口和EJB实现。为了使它们可用，我们还需要一个容器来保存和管理bean。

### 2.1 EJB远程接口

让我们从定义两个非常简单的beans开始-一个是无状态的，一个是有状态的。

我们将从它们的接口开始：

```java
@Remote
public interface HelloStatefulWorld {
    int howManyTimes();
    String getHelloWorld();
}
```

```java
@Remote
public interface HelloStatelessWorld {
    String getHelloWorld();
}
```

### 2.2 EJB实现

现在，让我们实现远程EJB接口：

```java
@Stateful(name = "HelloStatefulWorld")
public class HelloStatefulWorldBean implements HelloStatefulWorld {

    private int howManyTimes = 0;

    public int howManyTimes() {
        return howManyTimes;
    }

    public String getHelloWorld() {
        howManyTimes++;
        return "Hello Stateful World";
    }
}
```

```java
@Stateless(name = "HelloStatelessWorld")
public class HelloStatelessWorldBean implements HelloStatelessWorld {

    public String getHelloWorld() {
        return "Hello Stateless World!";
    }
}
```

如果有状态和无状态bean对你来说很陌生，这篇[介绍性文章](https://www.baeldung.com/ejb-intro)可能会派上用场。

### 2.3 EJB容器

我们可以在任何JEE容器中运行我们的代码，但出于实用目的，我们将使用Wildfly和cargo Maven插件来为我们完成繁重的工作：

```xml
<plugin>
    <groupId>org.codehaus.cargo</groupId>
    <artifactId>cargo-maven2-plugin</artifactId>
    <version>1.6.1</version>
    <configuration>
        <container>
            <containerId>wildfly10x</containerId>
            <zipUrlInstaller>
                <url>
                    http://download.jboss.org/wildfly/10.1.0.Final/wildfly-10.1.0.Final.zip
                </url>
            </zipUrlInstaller>
        </container>
        <configuration>
            <properties>
                <cargo.hostname>127.0.0.1</cargo.hostname>
                <cargo.jboss.configuration>standalone-full</cargo.jboss.configuration>
                <cargo.jboss.management-http.port>9990</cargo.jboss.management-http.port>
                <cargo.servlet.users>testUser:admin1234!</cargo.servlet.users>
            </properties>
        </configuration>
    </configuration>
</plugin>
```

### 2.4 运行EJB

配置这些后，我们可以直接从Maven命令行运行容器：

```shell
mvn clean package cargo:run -Pwildfly-standalone
```

我们现在有一个Wildfly的工作实例来托管我们的bean。我们可以通过日志行确认这一点：

```shell
java:global/ejb-remote-for-spring/HelloStatefulWorld!cn.tuyucheng.taketoday.ejb.tutorial.HelloStatefulWorld
java:app/ejb-remote-for-spring/HelloStatefulWorld!cn.tuyucheng.taketoday.ejb.tutorial.HelloStatefulWorld
java:module/HelloStatefulWorld!cn.tuyucheng.taketoday.ejb.tutorial.HelloStatefulWorld
java:jboss/exported/ejb-remote-for-spring/HelloStatefulWorld!cn.tuyucheng.taketoday.ejb.tutorial.HelloStatefulWorld
java:global/ejb-remote-for-spring/HelloStatefulWorld
java:app/ejb-remote-for-spring/HelloStatefulWorld
java:module/HelloStatefulWorld
```

```shell
java:global/ejb-remote-for-spring/HelloStatelessWorld!cn.tuyucheng.taketoday.ejb.tutorial.HelloStatelessWorld
java:app/ejb-remote-for-spring/HelloStatelessWorld!cn.tuyucheng.taketoday.ejb.tutorial.HelloStatelessWorld
java:module/HelloStatelessWorld!cn.tuyucheng.taketoday.ejb.tutorial.HelloStatelessWorld
java:jboss/exported/ejb-remote-for-spring/HelloStatelessWorld!cn.tuyucheng.taketoday.ejb.tutorial.HelloStatelessWorld
java:global/ejb-remote-for-spring/HelloStatelessWorld
java:app/ejb-remote-for-spring/HelloStatelessWorld
java:module/HelloStatelessWorld
```

## 3. Spring设置

现在我们已经启动并运行了JEE容器，并且部署了EJB，我们可以启动Spring应用程序了。我们将使用spring-boot-web来简化手动测试，但对于远程调用来说，这不是强制性的。

### 3.1 Maven依赖项

为了能够连接到远程EJB，我们需要Wildfly EJB客户端库和我们的远程接口：

```xml
<dependency>
    <groupId>org.wildfly</groupId>
    <artifactId>wildfly-ejb-client-bom</artifactId>
    <version>10.1.0.Final</version>
    <type>pom</type>
</dependency>
<dependency>
    <groupId>cn.tuyucheng.taketoday.spring.ejb</groupId>
    <artifactId>ejb-remote-for-spring</artifactId>
    <version>1.0.1</version>
    <type>ejb</type>
</dependency>
```

可以在[此处](https://central.sonatype.com/artifact/org.wildfly/wildfly-ejb-client-bom/28.0.0.Final)找到最新版本的wildfly-ejb-client-bom。

### 3.2 命名策略上下文

有了类路径中的这些依赖项，我们就可以**实例化一个javax.naming.Context来查找我们的远程bean**。我们将它创建为一个Spring Bean，这样我们就可以在需要时自动装配它：

```java
@Bean   
public Context context() throws NamingException {
    Properties jndiProps = new Properties();
    jndiProps.put("java.naming.factory.initial", "org.jboss.naming.remote.client.InitialContextFactory");
    jndiProps.put("jboss.naming.client.ejb.context", true);
    jndiProps.put("java.naming.provider.url", "http-remoting://localhost:8080");
    return new InitialContext(jndiProps);
}
```

**这些属性对于通知远程URL和命名策略上下文是必需的**。

### 3.3 JNDI模式

在我们可以将远程bean注入到Spring容器之前，我们需要知道如何访问它们。为此，我们将使用他们的JNDI绑定。让我们看看这些绑定的标准模式：

```plaintext
${appName}/${moduleName}/${distinctName}/${beanName}!${viewClassName}
```

请记住，**由于我们部署了一个简单的jar而不是ear并且没有明确设置名称，因此我们没有appName和distinctName**。

我们将使用此模式将远程bean绑定到我们的Spring beans。

### 3.4 构建我们的Spring Bean

**为了访问我们的EJB，我们将使用前面提到的JNDI**。还记得我们用来检查我们的企业Bean是否已部署的日志行吗？

我们将看到现在正在使用的信息：

```java
@Bean
public HelloStatelessWorld helloStatelessWorld(Context context) throws NamingException {
    return (HelloStatelessWorld) context.lookup(this.getFullName(HelloStatelessWorld.class));
}
```

```java
@Bean
public HelloStatefulWorld helloStatefulWorld(Context context) throws NamingException {
    return (HelloStatefulWorld) context.lookup(this.getFullName(HelloStatefulWorld.class));
}
```

```java
private String getFullName(Class classType) {
    String moduleName = "ejb-remote-for-spring/";
    String beanName = classType.getSimpleName();
    String viewClassName = classType.getName();
    return moduleName + beanName + "!" + viewClassName;
}
```

**我们需要非常小心正确的完整JNDI绑定**，否则上下文将无法访问远程EJB并创建必要的底层基础结构。

请记住，如果找不到你需要的bean，从Context查找的方法将抛出NamingException。

## 4. 整合

一切就绪后，我们可以**将bean注入到控制器中**，这样我们就可以测试注入是否正确：

```java
@RestController
public class HomeEndpoint {

    // ...

    @GetMapping("/stateless")
    public String getStateless() {
        return helloStatelessWorld.getHelloWorld();
    }

    @GetMapping("/stateful")
    public String getStateful() {
        return helloStatefulWorld.getHelloWorld() + " called " + helloStatefulWorld.howManyTimes() + " times";
    }
}
```

让我们启动我们的Spring服务器并检查一些日志。我们将看到以下行，表明一切正常：

```shell
EJBCLIENT000013: Successful version handshake completed
```

现在，让我们测试我们的无状态bean。我们可以尝试一些curl命令来验证它们是否按预期运行：

```shell
curl http://localhost:8081/stateless
Hello Stateless World!
```

下面是对有状态bean的测试：

```shell
curl http://localhost:8081/stateful
Hello Stateful World called 1 times

curl http://localhost:8081/stateful
Hello Stateful World called 2 times
```

## 5. 总结

在本文中，我们学习了如何将Spring集成到EJB中，以及如何远程调用JEE容器。我们创建了两个远程EJB接口，并且能够以透明的方式使用Spring Beans调用它们。

尽管Spring被广泛采用，但EJB在企业环境中仍然很流行，在这个快速示例中，我们展示了可以利用Jakarta EE的分布式收益和Spring应用程序的易用性。