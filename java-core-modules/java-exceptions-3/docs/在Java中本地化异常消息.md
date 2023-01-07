## 1. 概述

Java 中的异常用于表示程序中出现了错误。除了抛出异常，我们甚至可以添加一条消息来提供额外的信息。

在本文中，我们将利用getLocalizedMessage方法以英语和法语提供异常消息。

## 2. 资源包

我们需要一种方法来查找消息，使用messageKey来标识消息，并使用[Locale](https://www.baeldung.com/java-8-localization#localization)来标识哪个翻译将为messageKey提供值。我们将创建一个简单的类来抽象访问我们的[ResourceBundle](https://www.baeldung.com/java-resourcebundle)以检索英语和法语消息翻译：

```java
public class Messages {

    public static String getMessageForLocale(String messageKey, Locale locale) {
        return ResourceBundle.getBundle("messages", locale)
          .getString(messageKey);
    }

}

```

我们的Messages类使用ResourceBundle将属性文件加载到我们的包中，它位于我们类路径的根目录中。我们有两个文件——一个用于我们的英语消息，一个用于我们的法语消息：

```java
# messages.properties
message.exception = I am an exception.
# messages_fr.properties
message.exception = Je suis une exception.
```

## 3.本地化异常类

我们的Exception子类将使用默认的[Locale](https://www.baeldung.com/java-8-localization#localization)来确定对我们的消息使用哪种翻译。我们将使用Locale#getDefault获取默认的[Locale](https://www.baeldung.com/java-8-localization#localization)。

如果我们的应用程序在服务器上运行，我们将使用 HTTP 请求标头来标识要使用的Locale，而不是设置默认值。为此，我们将创建一个构造函数来接受Locale。

让我们创建我们的异常子类。为此，我们可以扩展RuntimeException或Exception。让我们扩展Exception并覆盖getLocalizedMessage：

```java
public class LocalizedException extends Exception {

    private final String messageKey;
    private final Locale locale;

    public LocalizedException(String messageKey) {
        this(messageKey, Locale.getDefault());
    }

    public LocalizedException(String messageKey, Locale locale) {
        this.messageKey = messageKey;
        this.locale = locale;
    }

    public String getLocalizedMessage() {
        return Messages.getMessageForLocale(messageKey, locale);
    }
}

```

## 4. 把它们放在一起

让我们创建一些单元测试来验证一切正常。我们将为英语和法语翻译创建测试，以验证在构造期间将自定义Locale传递给异常：

```java
@Test
public void givenUsEnglishProvidedLocale_whenLocalizingMessage_thenMessageComesFromDefaultMessage() {
    LocalizedException localizedException = new LocalizedException("message.exception", Locale.US);
    String usEnglishLocalizedExceptionMessage = localizedException.getLocalizedMessage();

    assertThat(usEnglishLocalizedExceptionMessage).isEqualTo("I am an exception.");
}

@Test
public void givenFranceFrenchProvidedLocale_whenLocalizingMessage_thenMessageComesFromFrenchTranslationMessages() {
    LocalizedException localizedException = new LocalizedException("message.exception", Locale.FRANCE);
    String franceFrenchLocalizedExceptionMessage = localizedException.getLocalizedMessage();

    assertThat(franceFrenchLocalizedExceptionMessage).isEqualTo("Je suis une exception.");
}
```

我们的例外也可以使用默认的Locale 。让我们再创建两个测试来验证默认的Locale功能是否有效：

```java
@Test
public void givenUsEnglishDefaultLocale_whenLocalizingMessage_thenMessageComesFromDefaultMessages() {
    Locale.setDefault(Locale.US);

    LocalizedException localizedException = new LocalizedException("message.exception");
    String usEnglishLocalizedExceptionMessage = localizedException.getLocalizedMessage();

    assertThat(usEnglishLocalizedExceptionMessage).isEqualTo("I am an exception.");
}

@Test
public void givenFranceFrenchDefaultLocale_whenLocalizingMessage_thenMessageComesFromFrenchTranslationMessages() {
    Locale.setDefault(Locale.FRANCE);

    LocalizedException localizedException = new LocalizedException("message.exception");
    String franceFrenchLocalizedExceptionMessage = localizedException.getLocalizedMessage();

    assertThat(franceFrenchLocalizedExceptionMessage).isEqualTo("Je suis une exception.");
}

```

## 5. 注意事项

### 5.1. 记录可抛物

我们需要记住我们用来将Exception实例发送到日志的日志记录框架。

Log4J、Log4J2 和 Logback 使用getMessage检索消息以写入日志附加程序。如果我们使用java.util.logging，内容来自getLocalizedMessage。

我们可能要考虑重写getMessage以调用getLocalizedMessage，这样我们就不必担心使用哪个日志记录实现。

### 5.2. 服务器端应用程序

当我们为客户端应用程序本地化我们的异常消息时，我们只需要担心一个系统的当前Locale。但是，如果我们想在服务器端应用程序中本地化异常消息，我们应该记住切换默认Locale将影响我们应用程序服务器中的所有请求。

如果我们决定本地化异常消息，我们将在我们的异常上创建一个构造函数来接受Locale。这将使我们能够在不更新默认Locale的情况下本地化我们的消息。

## 6.总结

本地化异常消息非常简单。我们需要做的就是为我们的消息创建一个ResourceBundle ，然后在我们的Exception子类中实现getLocalizedMessage。