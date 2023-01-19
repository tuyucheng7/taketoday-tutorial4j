## 一、概述

在本入门教程中，我们将探讨[Groovy](https://www.baeldung.com/groovy-language)中模板引擎的概念。

在 Groovy 中，我们可以使用[GString](https://www.baeldung.com/groovy-strings)轻松生成动态文本[。](https://www.baeldung.com/groovy-strings)然而，模板引擎提供了一种使用静态模板处理动态文本的更好方法。

这些模板可以方便地为各种通知(如 SMS 和电子邮件)定义静态模板。

## 2. Groovy 的TemplateEngine是什么？

Groovy 的TemplateEngine是一个包含createTemplate方法的抽象类。


Groovy 中可用的所有模板框架引擎都扩展了TemplateEngine并实现了createTemplate。此外，每个引擎都返回模板接口对象。

Template接口有一个make方法，它采用映射来绑定变量。因此，它必须由每个模板框架实现。

让我们讨论一下 Groovy 中所有可用模板框架的功能和行为。

## 3.简单模板引擎

SimpleTemplateEngine使用字符串插值和小脚本生成动态文本。该引擎对于简单的通知(例如 SMS 和简单的文本电子邮件)非常有用。

例如：

```groovy
def smsTemplate = 'Dear <% print user %>, Thanks for reading our Article. ${signature}'
def bindMap = [user: "Norman", signature: "Baeldung"]
def smsText = new SimpleTemplateEngine().createTemplate(smsTemplate).make(bindMap)

assert smsText.toString() == "Dear Norman, Thanks for reading our Article. Baeldung"
```

## 4.流模板引擎

在一般意义上，StreamingTemplateEngine的工作方式类似于SimpleTemplateEngine。但是，它在内部使用[Writable](http://docs.groovy-lang.org/latest/html/api/groovy/lang/Writable.html)闭包来生成模板。

出于同样的原因，它在处理更大的字符串(> 64K)时也有好处。因此，它比SimpleTemplateEngine 更高效。

让我们写一个简单的例子来使用静态模板生成动态电子邮件内容。

首先，我们将创建一个静态文章电子邮件模板：

```plaintext
Dear <% out << (user) %>,
Please read the requested article below.
<% out << (articleText) %>
From,
<% out << (signature) %>
```

在这里，我们将<% %>小脚本用于动态文本并用于编写器。

现在，我们将使用StreamingTemplateEngine生成电子邮件的内容：

```groovy
def articleEmailTemplate = new File('src/main/resources/articleEmail.template')
def bindMap = [user: "Norman", signature: "Baeldung"]

bindMap.articleText = """1. Overview
This is a tutorial article on Template Engines...""" //can be a string larger than 64k

def articleEmailText = new StreamingTemplateEngine().createTemplate(articleEmailTemplate).make(bindMap)

assert articleEmailText.toString() == """Dear Norman,
Please read the requested article below.
1. Overview
This is a tutorial article on Template Engines...
From,
Baeldung"""
```

## 5.GString模板引擎

顾名思义，GStringTemplateEngine使用GString从静态模板生成动态文本。

首先，让我们使用GString编写一个简单的电子邮件模板：

```plaintext
Dear $user,
Thanks for subscribing our services.
${signature}
```

现在，我们将使用GStringTemplateEngine来创建动态内容：

```groovy
def emailTemplate = new File('src/main/resources/email.template')
def emailText = new GStringTemplateEngine().createTemplate(emailTemplate).make(bindMap)

```

## 6.Xml模板引擎

当我们想要创建动态 XML 输出时， XmlTemplateEngine很有用。它需要 XML 模式作为输入并允许两个特殊标记，<gsp:scriptlet>用于注入脚本，<gsp:expression>用于注入表达式。

例如，让我们将已经讨论过的电子邮件模板转换为 XML：

```groovy
def emailXmlTemplate = '''
<xs xmlns:gsp='groovy-server-pages'>
    <gsp:scriptlet>def emailContent = "Thanks for subscribing our services."</gsp:scriptlet>
    <email>
        <greet>Dear ${user}</greet>
        <content><gsp:expression>emailContent</gsp:expression></content>
        <signature>${signature}</signature>
    </email>
</xs>'''

def emailXml = new XmlTemplateEngine().createTemplate(emailXmlTemplate).make(bindMap)
```

因此，emailXml将呈现 XML，内容为：

```shell
<xs>
  <email>
    <greet>
      Dear Norman
    </greet>
    <content>
      Thanks for subscribing our services.
    </content>
    <signature>
      Baeldung
    </signature>
  </email>
</xs>

```

有趣的是，模板框架对 XML 输出进行了缩进和美化。

## 7.标记模板引擎

这个模板框架是一个完整的包，用于生成 HTML 和其他标记语言。

此外，它使用领域特定语言来处理模板，并且是 Groovy 中可用的所有模板框架中最优化的。

### 7.1. HTML

让我们写一个简单的例子来为已经讨论过的电子邮件模板呈现 HTML：

```groovy
def emailHtmlTemplate = """
html {
    head {
        title('Service Subscription Email')
    }
    body {
        p('Dear Norman')
        p('Thanks for subscribing our services.')
        p('Baeldung')
    }
}"""
def emailHtml = new MarkupTemplateEngine().createTemplate(emailHtmlTemplate).make()
```

因此，emailHtml的内容将是：

```html
<html><head><title>Service Subscription Email</title></head>
<body><p>Dear Norman</p><p>Thanks for subscribing our services.</p><p>Baeldung</p></body></html>
```

### 7.2. XML

同样，我们可以呈现 XML：

```groovy
def emailXmlTemplate = """
xmlDeclaration()  
    xs{
        email {
            greet('Dear Norman')
            content('Thanks for subscribing our services.')
            signature('Baeldung')
        }  
    }"""
def emailXml = new MarkupTemplateEngine().createTemplate(emailXmlTemplate).make()
```

因此，emailXml的内容将是：

```xml
<?xml version='1.0'?>
<xs><email><greet>Dear Norman</greet><content>Thanks for subscribing our services.</content>
<signature>Baeldung</signature></email></xs>
```

### 7.3. 模板配置

请注意，与XmlTemplateEngine不同，此框架的模板输出不会自行缩进和美化。

对于这样的配置，我们将使用TemplateConfiguration类：

```groovy
TemplateConfiguration config = new TemplateConfiguration()
config.autoIndent = true
config.autoEscape = true
config.autoNewLine = true
                               
def templateEngine = new MarkupTemplateEngine(config)
```

### 7.4. 国际化

此外，TemplateConfiguration的locale属性可用于启用国际化支持。

首先，我们将创建一个静态模板文件email.tpl并将已经讨论过的emailHtmlTemplate字符串到其中。这将被视为默认模板。

同样，我们将创建基于语言环境的模板文件，例如日语的email_ja_JP.tpl、法语的 email_fr_FR.tpl 等。

最后，我们只需要在TemplateConfiguration对象中设置语言环境：

```groovy
config.locale = Locale.JAPAN
```

因此，将选择相应的基于语言环境的模板。

## 八、总结

在本文中，我们看到了 Groovy 中可用的各种模板框架。

我们可以利用这些方便的模板引擎来使用静态模板生成动态文本。因此，它们有助于动态生成各种通知或屏幕消息和错误。