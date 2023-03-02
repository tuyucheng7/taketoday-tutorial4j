## 1. 概述

简而言之，[Spring Shell](https://spring.io/projects/spring-shell)项目提供了一个交互式shell，用于使用Spring编程模型处理命令和构建功能齐全的CLI。

在本文中，我们将探讨它的特性、关键类和注解，并实现几个自定义命令和定制。

## 2. Maven依赖

首先，我们需要将spring-shell依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.shell</groupId>
    <artifactId>spring-shell</artifactId>
    <version>1.2.0.RELEASE</version>
</dependency>
```

可以在[此处](https://central.sonatype.com/artifact/org.springframework.shell/spring-shell/1.2.0.RELEASE)找到此工件的最新版本。

## 3. 访问Shell

在我们的应用程序中访问shell有两种主要方式。

第一种是在我们应用程序的入口点引导shell并让用户输入命令：

```java
public static void main(String[] args) throws IOException {
    Bootstrap.main(args);
}
```

第二种是获取一个JLineShellComponent并以编程方式执行命令：

```java
Bootstrap bootstrap = new Bootstrap();
JLineShellComponent shell = bootstrap.getJLineShellComponent();
shell.executeCommand("help");
```

我们将使用第一种方法，因为它最适合本文中的示例，但是，在源代码中你可以找到使用第二种形式的测试用例。

## 4. 命令

shell中已经有几个内置命令，例如clear、help、exit等，它们提供每个CLI的标准功能。

可以通过在实现CommandMarker接口的Spring组件中添加标有@CliCommand注解的方法来公开自定义命令。

该方法的每个参数都必须标有@CliOption注解，如果我们不这样做，我们将在尝试执行命令时遇到几个错误。

### 4.1 向Shell添加命令

首先，我们需要让shell知道我们的命令在哪里。为此，它需要文件META-INF/spring/spring-shell-plugin.xml存在于我们的项目中，在那里，我们可以使用Spring的组件扫描功能：

```xml
<beans ... >
    <context:component-scan base-package="cn.tuyucheng.taketoday.shell.simple" />
</beans>
```

一旦组件被Spring注册和实例化，它们就会被注册到shell解析器，并处理它们的注解。

让我们创建两个简单的命令，一个用于获取URL的内容并显示它们，另一个用于将这些内容保存到文件中：

```java
@Component
public class SimpleCLI implements CommandMarker {

    @CliCommand(value = { "web-get", "wg" })
    public String webGet(@CliOption(key = "url") String url) {
        return getContentsOfUrlAsString(url);
    }

    @CliCommand(value = { "web-save", "ws" })
    public String webSave(
          @CliOption(key = "url") String url,
          @CliOption(key = { "out", "file" }) String file) {
        String contents = getContentsOfUrlAsString(url);
        try (PrintWriter out = new PrintWriter(file)) {
            out.write(contents);
        }
        return "Done.";
    }
}
```

请注意，我们可以将多个字符串分别传递给@CliCommand和@CliOption的key和value属性，这允许我们公开多个行为相同的命令和参数。

现在，让我们检查一切是否按预期工作：

```shell
spring-shell>web-get --url https://www.google.com
<!doctype html ... 
spring-shell>web-save --url https://www.google.com --out contents.txt
Done.
```

### 4.2 命令的可用性

我们可以在返回布尔值的方法上使用@CliAvailabilityIndicator注解，以在运行时更改命令是否应暴露给shell。

首先，让我们创建一个方法来修改web-save命令的可用性：

```java
private boolean adminEnableExecuted = false;

@CliAvailabilityIndicator(value = "web-save")
public boolean isAdminEnabled() {
    return adminEnableExecuted;
}
```

现在，让我们创建一个命令来更改adminEnableExecuted变量：

```java
@CliCommand(value = "admin-enable")
public String adminEnable() {
    adminEnableExecuted = true;
    return "Admin commands enabled.";
}
```

最后我们来验证一下：

```shell
spring-shell>web-save --url https://www.google.com --out contents.txt
Command 'web-save --url https://www.google.com --out contents.txt'
  was found but is not currently available
  (type 'help' then ENTER to learn about this command)
spring-shell>admin-enable
Admin commands enabled.
spring-shell>web-save --url https://www.google.com --out contents.txt
Done.
```

### 4.3 必需的参数

默认情况下，所有命令参数都是可选的。但是，我们可以使用@CliOption注解的mandatory属性使它们成为必需的：

```java
@CliOption(key = { "out", "file" }, mandatory = true)
```

现在，我们可以测试如果我们不引入它，会导致错误：

```shell
spring-shell>web-save --url https://www.google.com
You should specify option (--out) for this command
```

### 4.4 默认参数

@CliOption的空key值使该参数成为默认值。在那里，我们将收到在shell中引入的不属于任何命名参数的值：

```java
@CliOption(key = { "", "url" })
```

现在，让我们检查它是否按预期工作：

```shell
spring-shell>web-get https://www.google.com
<!doctype html ...
```

### 4.5 帮助用户

@CliCommand和@CliOption注解提供了一个help属性，允许我们在使用内置help命令或使用Tab键获得自动完成时指导我们的用户。

让我们修改我们的web-get以添加自定义帮助消息：

```java
@CliCommand(
    // ...
    help = "Displays the contents of an URL")
public String webGet(
    @CliOption(
        // ...
        help = "URL whose contents will be displayed."
    ) String url) {
    // ...
}
```

现在，用户可以确切地知道我们的命令是做什么的：

```bash
spring-shell>help web-get
Keyword:                    web-get
Keyword:                    wg
Description:                Displays the contents of a URL.
  Keyword:                  ** default **
  Keyword:                  url
    Help:                   URL whose contents will be displayed.
    Mandatory:              false
    Default if specified:   '__NULL__'
    Default if unspecified: '__NULL__'

* web-get - Displays the contents of a URL.
* wg - Displays the contents of a URL.
```

## 5. 自定义

可以通过实现BannerProvider、PromptProvider和HistoryFileNameProvider接口来自定义shell的三种方法，所有这些接口都已提供默认实现。

此外，我们需要使用@Order注解来允许我们的提供者优先于那些实现。

让我们创建一个新的banner来开始我们的自定义：

```java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleBannerProvider extends DefaultBannerProvider {

    public String getBanner() {
        StringBuffer buf = new StringBuffer();
        buf.append("=======================================")
              .append(OsUtils.LINE_SEPARATOR);
        buf.append("*          Tuyucheng Shell             *")
              .append(OsUtils.LINE_SEPARATOR);
        buf.append("=======================================")
              .append(OsUtils.LINE_SEPARATOR);
        buf.append("Version:")
              .append(this.getVersion());
        return buf.toString();
    }

    public String getVersion() {
        return "1.0.1";
    }

    public String getWelcomeMessage() {
        return "Welcome to Tuyucheng CLI";
    }

    public String getProviderName() {
        return "Tuyucheng Banner";
    }
}
```

请注意，我们还可以更改版本号和欢迎消息。

现在，让我们更改提示：

```java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimplePromptProvider extends DefaultPromptProvider {

    public String getPrompt() {
        return "tuyucheng-shell";
    }

    public String getProviderName() {
        return "Tuyucheng Prompt";
    }
}
```

最后，让我们修改历史文件的名称：

```java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleHistoryFileNameProvider
      extends DefaultHistoryFileNameProvider {

    public String getHistoryFileName() {
        return "tuyucheng-shell.log";
    }

    public String getProviderName() {
        return "Tuyucheng History";
    }
}
```

历史文件将记录在shell中执行的所有命令，并将与我们的应用程序放在一起。

一切就绪后，我们可以调用我们的shell并查看它的运行情况：

```shell
=======================================
*          Tuyucheng Shell             *
=======================================
Version:1.0.1
Welcome to Tuyucheng CLI
tuyucheng-shell>
```

## 6. 转换器

到目前为止，我们只使用简单类型作为命令的参数。常见类型，如Integer、Date、Enum、File等，都有一个已经注册的默认转换器。

通过实现Converter接口，我们还可以添加我们的转换器来接收自定义对象。

让我们创建一个可以将String转换为URL的转换器：

```java
@Component
public class SimpleURLConverter implements Converter<URL> {

    public URL convertFromText(String value, Class<?> requiredType, String optionContext) {
        return new URL(value);
    }

    public boolean getAllPossibleValues(
          List<Completion> completions,
          Class<?> requiredType,
          String existingData,
          String optionContext,
          MethodTarget target) {
        return false;
    }

    public boolean supports(Class<?> requiredType, String optionContext) {
        return URL.class.isAssignableFrom(requiredType);
    }
}
```

最后，让我们修改我们的web-get和web-save命令：

```java
public String webSave(... URL url) {
    // ...
}

public String webSave(... URL url) {
    // ...
}
```

正如你可能已经猜到的那样，这些命令的行为是相同的。

## 7. 总结

在本文中，我们简要介绍了Spring Shell项目的核心功能。我们能够提供我们的命令并使用我们的提供者自定义shell，我们根据不同的运行时条件更改命令的可用性并创建一个简单的类型转换器。