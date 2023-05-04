## 1. 概述

[Enterprise Java Beans](https://docs.oracle.com/javaee/6/tutorial/doc/gijsz.html)(EJB)是[Java EE规范](https://en.wikipedia.org/wiki/Java_Platform,_Enterprise_Edition)的核心部分，旨在简化分布式企业级应用程序的开发。EJB的生命周期由应用服务器处理，例如[JBoss WildFly](http://wildfly.org/)或[Oracle GlassFish](https://www.oracle.com/middleware/technologies/glassfish-server.html)。

EJB提供了一个健壮的编程模型，有助于企业级软件模块的实现，因为它取决于应用程序服务器来处理非业务逻辑相关的问题，例如事务处理、组件生命周期管理或依赖注入。

此外，我们已经发表了两篇涵盖EJB基本概念的文章，所以请随时在[此处](https://www.baeldung.com/ejb-intro)和[此处](https://www.baeldung.com/ejb-session-beans)查看它们。

在本教程中，我们将展示如何在WildFly上实现基本的EJB模块，以及如何通过[JNDI](https://en.wikipedia.org/wiki/Java_Naming_and_Directory_Interface)从远程客户端调用EJB。

## 2. 实现EJB模块

业务逻辑由一个或多个本地/远程业务接口(也称为本地/远程视图)，或直接通过不实现任何接口的类(非视图接口)实现。

值得注意的是，当要从驻留在相同环境中的客户端(即相同的EAR或WAR文件)访问bean时，将使用本地业务接口，而当从不同环境访问bean时(即不同的JVM或应用程序服务器)，则需要远程业务接口。

让我们创建一个基本的EJB模块，该模块仅由一个bean组成。bean的业务逻辑将很简单，仅限于将给定的String转换为其大写版本。

### 2.1 定义远程业务接口

让我们首先定义一个单一的远程业务接口，用@Remote注解修饰。根据[EJB 3.x规范](https://download.oracle.com/otn-pub/jcp/ejb-3.1-fr-eval-oth-JSpec/ejb-3_1-fr-spec.pdf)，这是强制性的，因为要从远程客户端访问bean：

```java
@Remote
public interface TextProcessorRemote {
    String processText(String text);
}
```

### 2.2 定义无状态Bean

接下来我们通过实现上述远程接口来实现业务逻辑：

```java
@Stateless
public class TextProcessorBean implements TextProcessorRemote {
    public String processText(String text) {
        return text.toUpperCase();
    }
}
```

TextProcessorBean类是一个简单的Java类，用@Stateless注解标注。

根据定义，无状态bean不维护与客户端的任何对话状态，即使它们可以跨不同请求维护实例状态。它们的对应物有状态bean会保留它们的对话状态，例如为应用程序服务器创建它们的成本更高。

在这种情况下，上面的类没有任何实例状态，所以它可以成为无状态的。如果它有一个状态，那么在不同的客户端请求中使用它根本就没有意义。

bean的行为是确定性的，即它没有副作用，就像一个设计良好的bean应该的那样：它只需要一个输入字符串并返回它的大写版本。

### 2.3 Maven依赖项

接下来，我们需要将[javaee-api](https://central.sonatype.com/artifact/javax/javaee-api/8.0.1) Maven工件添加到模块中，它提供所有Java EE 7规范API，包括EJB所需的API：

```xml
<dependency>
    <groupId>javax</groupId>
    <artifactId>javaee-api</artifactId>
    <version>7.0</version>
    <scope>provided</scope>
</dependency>
```

至此，我们已经成功地创建了一个基本的但功能齐全的EJB模块。为了让所有潜在客户端都可以使用它，我们必须将工件作为JAR文件添加到我们的本地Maven仓库中。

### 2.4 将EJB模块安装到本地仓库

有几种方法可以实现这一点。最直接的一个包括执行Maven生命周期clean – install构建阶段：

```shell
mvn clean install
```

此命令将EJB模块作为ejbmodule-1.0.jar(或pom.xml文件中指定的任意工件ID)安装到我们的本地仓库中。有关如何使用Maven安装本地JAR的更多信息，请查看[这篇文章](https://www.baeldung.com/install-local-jar-with-maven)。

假设EJB模块已正确安装到我们的本地仓库中，下一步是开发一个使用我们的TextProcessorBean API的远程客户端应用程序。

## 3. 远程EJB客户端

我们将使远程EJB客户端的业务逻辑非常简单：首先，它执行JNDI查找以获取TextProcessorBean代理。之后，它调用代理的processText()方法。

### 3.1 Maven依赖项

我们需要包含以下Maven工件，以便EJB客户端按预期工作：

```xml
<dependency>
    <groupId>javax</groupId>
    <artifactId>javaee-api</artifactId>
    <version>7.0</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>org.wildfly</groupId>
    <artifactId>wildfly-ejb-client-bom</artifactId>
    <version>10.1.0.Final</version>
</dependency>
<dependency>
    <groupId>cn.tuyucheng.taketoday.ejbmodule</groupId>
    <artifactId>ejbmodule</artifactId>
    <version>1.0.0</version>
</dependency>
```

虽然我们包含javaee-api工件的原因非常明显，但包含[wildfly-ejb-client-bom](https://search.maven.org/classic/#search|ga|1|wildfly-ejb-client-bom)却不是。**在WildFly上执行远程EJB调用需要该工件**。

最后但同样重要的是，我们需要使以前的EJB模块对客户端可用，因此我们也添加了ejbmodule依赖项。

### 3.2 EJB客户端类

考虑到EJB客户端调用TextProcessorBean的代理，我们将非常务实并将客户端类命名为TextApplication：

```java
public class TextApplication {

    public static void main(String[] args) throws NamingException {
        TextProcessorRemote textProcessor = EJBFactory
                .createTextProcessorBeanFromJNDI("ejb:");
        System.out.print(textProcessor.processText("sample text"));
    }

    private static class EJBFactory {
        private static TextProcessorRemote createTextProcessorBeanFromJNDI(String namespace) throws NamingException {
            return lookupTextProcessorBean(namespace);
        }

        private static TextProcessorRemote lookupTextProcessorBean(String namespace) throws NamingException {
            Context ctx = createInitialContext();
            String appName = "";
            String moduleName = "EJBModule";
            String distinctName = "";
            String beanName = TextProcessorBean.class.getSimpleName();
            String viewClassName = TextProcessorRemote.class.getName();
            return (TextProcessorRemote) ctx.lookup(namespace
                                                    + appName + "/" + moduleName
                                                    + "/" + distinctName + "/" + beanName + "!" + viewClassName);
        }

        private static Context createInitialContext() throws NamingException {
            Properties jndiProperties = new Properties();
            jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
            jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
            jndiProperties.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
            jndiProperties.put("jboss.naming.client.ejb.context", true);
            return new InitialContext(jndiProperties);
        }
    }
}
```

简而言之，TextApplication类所做的就是检索bean代理并使用示例字符串调用它的processText()方法。

实际查找由嵌套类EJBFactory执行，它首先创建一个JNDI [InitialContext](https://docs.oracle.com/en/java/javase/11/docs/api/java.naming/javax/naming/InitialContext.html)实例，然后将所需的JNDI参数传递给构造函数，最后使用它来查找bean代理。

请注意，查找是使用WildFly专有的“ejb:”命名空间执行的。这优化了查找过程，因为客户端延迟到服务器的连接，直到显式调用代理。

还值得注意的是，根本不用求助于“ejb”命名空间就可以查找bean代理。但是，**我们会失去惰性网络连接的所有额外好处，从而使客户端的性能大大降低**。

### 3.3 设置EJB上下文

客户端应该知道要与哪个主机和端口建立连接以执行bean查找。为此，**客户端需要设置专有的WildFly EJB上下文，该上下文由放置在其类路径中的jboss-ejb-client.properties文件定义**，通常位于src/main/resources文件夹下：

```properties
endpoint.name=client-endpoint
remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED=false
remote.connections=default
remote.connection.default.host=127.0.0.1
remote.connection.default.port=8080
remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS=false
remote.connection.default.username=myusername
remote.connection.default.password=mypassword
```

该文件不言自明，因为它提供了与WildFly建立连接所需的所有参数，包括远程连接的默认数量、默认主机和端口以及用户凭据。在这种情况下，连接未加密，但可以在启用SSL时加密。

最后要考虑的是，**如果连接需要身份验证，则有必要通过[add-user.sh/add-user.bat实用程序](https://docs.jboss.org/author/display/WFLY8/add-user%20utility.html)将用户添加到WildFly**。

## 4. 总结

在WildFly上执行EJB查找非常简单，只要我们严格遵守概述的流程即可。