## 1. 概述

由于记录了大量数据，因此在记录时屏蔽用户的敏感细节非常重要。在新的 GDPR 世界中，在许多问题中，我们必须特别注意记录个人的敏感数据。

在本教程中，我们将了解如何使用 Logback 屏蔽日志中的敏感数据。总的来说，这种方法并不是解决问题的真正方法——它是我们日志文件的最后一道防线。

## 2. 登录

[Logback](https://www.baeldung.com/logback)是Java社区中使用最广泛的日志记录框架之一。它是其前身 Log4j 的替代品。它提供了比 Log4j 更快的实现，并且提供了更多的配置选项和更灵活的归档旧日志文件。

敏感数据是任何旨在防止未经授权访问的信息。这可以包括从个人身份信息 (PII)(如社会安全号码)到银行信息、登录凭据、地址、电子邮件等的任何内容。

在我们的应用程序中登录时，我们将屏蔽属于用户的敏感数据。

## 3.屏蔽数据

假设我们在 Web 请求的上下文中记录用户详细信息。我们需要屏蔽与用户相关的敏感数据。假设我们的应用程序收到我们记录的以下请求或响应：

```json
{
    "user_id":"87656",
    "ssn":"786445563",
    "address":"22 Street",
    "city":"Chicago",
    "Country":"U.S.",
    "ip_address":"192.168.1.1",
    "email_id":"spring@baeldung.com"
 }
```

在这里，我们可以看到我们有敏感数据，如ssn、address、ip_address和email_id。因此，我们必须在记录时屏蔽这些数据。

我们将通过为 Logback 生成的所有日志条目配置屏蔽规则来集中屏蔽日志。为此，我们必须实现自定义ch.qos.logback.classic.PatternLayout。

### 3.1. 图案布局

配置背后的想法是用自定义布局扩展我们需要的每个 Logback appender。在我们的例子中，我们将编写一个MaskingPatternLayout类作为PatternLayout 的实现。每个掩码模式代表匹配一种类型的敏感数据的正则表达式。

让我们构建MaskingPatternLayout类：

```java
public class MaskingPatternLayout extends PatternLayout {

    private Pattern multilinePattern;
    private List<String> maskPatterns = new ArrayList<>();

    public void addMaskPattern(String maskPattern) {
        maskPatterns.add(maskPattern);
        multilinePattern = Pattern.compile(maskPatterns.stream().collect(Collectors.joining("|")), Pattern.MULTILINE);
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        return maskMessage(super.doLayout(event));
    }

    private String maskMessage(String message) {
        if (multilinePattern == null) {
            return message;
        }
        StringBuilder sb = new StringBuilder(message);
        Matcher matcher = multilinePattern.matcher(sb);
        while (matcher.find()) {
            IntStream.rangeClosed(1, matcher.groupCount()).forEach(group -> {
                if (matcher.group(group) != null) {
                    IntStream.range(matcher.start(group), matcher.end(group)).forEach(i -> sb.setCharAt(i, ''));
                }
            });
        }
        return sb.toString();
    }
}
```

PatternLayout的实现。doLayout() 负责屏蔽我们应用程序的每条日志消息中的匹配数据(如果它与配置的模式之一匹配)。

logback.xml中的maskPatterns列表构造了一个多行模式。不幸的是，Logback 引擎不支持构造函数注入。如果它作为属性列表出现，则会为每个配置条目调用addMaskPattern 。因此，每次向列表中添加新的正则表达式时，我们都必须编译模式。

### 3.2. 配置

通常，我们可以使用正则表达式模式来屏蔽敏感的用户详细信息。

例如，对于 SSN，我们可以使用如下正则表达式：

```plaintext
"SSN"s:s"(.)"
```

对于地址，我们可以使用：

```plaintext
"address"s:s"(.?)" 
```

此外，对于 IP 地址数据模式 (192.169.0.1)，我们可以使用正则表达式：

```plaintext
(d+.d+.d+.d+)
```

最后，对于电子邮件，我们可以这样写：

```plaintext
(w+@w+.w+)
```

现在，我们将在logback.xml文件中的maskPattern标签中添加这些正则表达式模式：

```xml
<configuration>
    <appender name="mask" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
           <layout class="com.baeldung.logback.MaskingPatternLayout">
	       <maskPattern>"SSN"s:s"(.?)"</maskPattern> <!-- SSN JSON pattern -->
	       <maskPattern>"address"s:s"(.?)"</maskPattern> <!-- Address JSON pattern -->
	       <maskPattern>(d+.d+.d+.d+)</maskPattern> <!-- Ip address IPv4 pattern -->
	       <maskPattern>(w+@w+.w+)</maskPattern> <!-- Email pattern -->
	       <pattern>%-5p [%d{ISO8601,UTC}] [%thread] %c: %m%n%rootException</pattern>
            </layout>
        </encoder>
    </appender>
</ configuration>
```

### 3.3. 执行

现在，我们将为上面的示例创建 JSON 并使用logger.info()来记录详细信息：

```java
Map<String, String> user = new HashMap<String, String>();
user.put("user_id", "87656");
user.put("SSN", "786445563");
user.put("address", "22 Street");
user.put("city", "Chicago");
user.put("Country", "U.S.");
user.put("ip_address", "192.168.1.1");
user.put("email_id", "spring@baeldung.com");
JSONObject userDetails = new JSONObject(user);

logger.info("User JSON: {}", userDetails);
```

执行后，我们可以看到输出：

```plaintext
INFO  [2021-06-01 16:04:12,059] [main] com.baeldung.logback.MaskingPatternLayoutExample: User JSON: 
{"email_id":"","address":"","user_id":"87656","city":"Chicago","Country":"U.S.", "ip_address":"","SSN":""}

```

在这里，我们可以看到记录器中的用户 JSON 已被屏蔽：

```json
{
    "user_id":"87656",
    "ssn":"",
    "address":"",
    "city":"Chicago",
    "Country":"U.S.",
    "ip_address":"",
    "email_id":""
 }
```

使用这种方法，我们只能屏蔽日志文件中的那些数据，我们已经在logback.xml的maskPattern中为其定义了正则表达式。

## 4. 总结

在本教程中，我们介绍了如何使用PatternLayout 功能通过 Logback 屏蔽应用程序日志中的敏感数据。此外，我们还了解了如何在logback.xml中添加正则表达式模式以屏蔽特定数据。