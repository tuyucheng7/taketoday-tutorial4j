## 1. 概述

在这篇简短的文章中，我们将探讨JVM世界中最常见的安全问题之一——日志伪造。我们还将展示一个可以保护我们免受此安全问题影响的示例技术。

## 2. 什么是日志锻造？

根据[OWASP](https://www.owasp.org/)的说法，日志伪造是最常见的攻击技术之一。

当数据从不受信任的来源进入应用程序或数据被某些外部实体写入应用程序/系统日志文件时，就会发生日志伪造漏洞。

根据[OWASP指南](https://owasp.org/www-community/attacks/Log_Injection)，日志伪造或注入是一种将未经验证的用户输入写入日志文件的技术，这样它就可以允许攻击者伪造日志条目或将恶意内容注入日志。

简单地说，通过日志伪造，攻击者试图通过探索应用程序的安全漏洞来添加/修改记录内容。

## 3.例子

考虑一个用户从 Web 提交付款请求的示例。从应用程序级别来看，一旦此请求得到处理，将记录一个条目，其中包含以下金额：

```java
private final Logger logger 
  = LoggerFactory.getLogger(LogForgingDemo.class);

public void addLog( String amount ) {
    logger.info( "Amount credited = {}" , amount );
}

public static void main( String[] args ) {
    LogForgingDemo demo = new LogForgingDemo();
    demo.addLog( "300" );
}
```

如果我们查看控制台，我们将看到如下内容：

```plaintext
web - 2017-04-12 17:45:29,978 [main] 
  INFO  logforging.cn.tuyucheng.taketoday.LogForgingDemo - Amount credited = 300
```

现在，假设攻击者提供的输入为“nnweb – 2017-04-12 17:47:08,957 [main] INFO Amount reversed successfully”，那么日志将是：

```plaintext
web - 2017-04-12 17:52:14,124 [main] INFO  com.baeldung.logforging.
  LogForgingDemo - Amount credited = 300

web - 2017-04-12 17:47:08,957 [main] INFO Amount reversed successfully
```

攻击者有意在应用程序日志中创建一个伪造的条目，它破坏了日志的值并混淆了未来的任何审计类型活动。这就是日志锻造的本质。

## 4、预防

最明显的解决方案是不将任何用户输入写入日志文件。

但是，这可能并非在所有情况下都可行，因为用户提供的数据对于将来调试或审核应用程序活动是必需的。

我们必须使用其他一些替代方法来解决这种情况。

### 4.1. 引入验证

最简单的解决方案之一是始终在记录之前验证输入。这种方法的一个问题是我们必须在运行时验证大量数据，这将影响整体系统性能。

此外，如果验证失败，数据将不会被记录并永远丢失，这通常是不可接受的情况。

### 4.2. 数据库记录

另一种选择是将数据记录到数据库中。这比其他方法更安全，因为'n'或换行符在此上下文中没有任何意义。然而，这会引发另一个性能问题，因为大量的数据库连接将用于记录用户数据。

更重要的是，这种技术引入了另一个安全漏洞——即[SQL 注入](https://owasp.org/www-community/attacks/SQL_Injection)。为了解决这个问题，我们最终可能会编写许多额外的代码行。

### 4.3. ESAPI

根据此上下文，使用ESAPI是最共享和最可取的技术。在这里，每个用户数据在写入日志之前都经过编码。ESAPI是OWASP提供的开源 API ：

```xml
<dependency>
    <groupId>org.owasp.esapi</groupId>
    <artifactId>esapi</artifactId>
    <version>2.5.0.0</version>
</dependency>
```

它在[Central Maven Repository](https://search.maven.org/classic/#search|gav|1|g%3A"org.owasp.esapi" AND a%3A"esapi")中可用。

我们可以使用ESAPI的[编码器](https://static.javadoc.io/org.owasp.esapi/esapi/2.0.1/org/owasp/esapi/Encoder.html)接口对数据进行编码：

```java
public String encode(String message) {
    message = message.replace( 'n' ,  '_' ).replace( 'r' , '_' )
      .replace( 't' , '_' );
    message = ESAPI.encoder().encodeForHTML( message );
    return message;
}
```

在这里，我们创建了一个包装器方法，它用下划线替换所有回车符和换行符并对修改后的消息进行编码。

在前面的示例中，如果我们使用此包装函数对消息进行编码，则日志应如下所示：

```plaintext
web - 2017-04-12 18:15:58,528 [main] INFO  com.baeldung.logforging.
  LogForgingDemo - Amount credited = 300
__web - 2017-04-12 17:47:08,957 [main] INFO Amount reversed successfully
```

在这里，损坏的字符串片段被编码并且可以很容易地识别。

需要注意的重要一点是，要使用ESAPI，我们需要在类路径中包含ESAPI.properties文件，否则ESAPI API 将在运行时抛出异常。可[在此处](https://github.com/OWASP/EJSF/blob/master/esapi_master_FULL/WebContent/ESAPI.properties)获得。

## 5.总结

在本快速教程中，我们了解了日志伪造和克服此安全问题的技术。