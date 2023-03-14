## 1. 概述

Java 6 引入了一种用于发现和加载与给定接口匹配的实现的功能：服务提供者接口 (SPI)。

在本教程中，我们将介绍JavaSPI 的组件，并展示如何将其应用于实际用例。

## 2.JavaSPI术语和定义

Java SPI 定义了四个主要组件

### 2.1. 服务

一组众所周知的编程接口和类，提供对某些特定应用程序功能或特性的访问。

### 2.2. 服务提供商接口

充当服务的代理或端点的接口或抽象类。

如果服务是一个接口，那么它就等同于一个服务提供者接口。

服务和 SPI 作为 API 在Java生态系统中是众所周知的。

### 2.3. 服务提供者

SPI 的特定实现。服务提供者包含一个或多个实现或扩展服务类型的具体类。

服务提供者是通过我们放在资源目录META-INF/services中的提供者配置文件来配置和识别的。文件名是 SPI 的完全限定名称，其内容是 SPI 实现的完全限定名称。

Service Provider 以扩展的形式安装，一个 jar 文件，我们将其放在应用程序类路径、Java 扩展类路径或用户定义的类路径中。

### 2.4. 服务加载器

SPI 的核心是[ServiceLoader](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html)类。这具有延迟发现和加载实现的作用。它使用上下文类路径来定位提供者实现并将它们放入内部缓存中。

## 3.Java生态系统中的 SPI 示例

Java 提供了许多 SPI。以下是服务提供者接口及其提供的服务的一些示例：

-   [CurrencyNameProvider：](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/spi/CurrencyNameProvider.html)为Currency类提供本地化的货币符号
-   [LocaleNameProvider](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/spi/LocaleNameProvider.html)：为Locale类提供本地化名称。
-   [TimeZoneNameProvider：](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/spi/TimeZoneNameProvider.html)为TimeZone类提供本地化的时区名称。
-   [DateFormatProvider](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/text/spi/DateFormatProvider.html)：为指定的语言环境提供日期和时间格式。
-   [NumberFormatProvider](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/text/spi/NumberFormatProvider.html)：为NumberFormat类提供货币、整数和百分比值。
-   [驱动程序：](https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/Driver.html)从 4.0 版开始，JDBC API 支持 SPI 模式。旧版本使用[Class.forName()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Class.html#forName(java.lang.String))方法加载驱动程序。
-   [PersistenceProvider：](https://docs.oracle.com/javaee/7/api/javax/persistence/spi/PersistenceProvider.html)提供JPA API的实现。
-   [JsonProvider：](https://docs.oracle.com/javaee/7/api/javax/json/spi/JsonProvider.html)提供JSON处理对象。
-   [JsonbProvider：](https://javaee.github.io/javaee-spec/javadocs/javax/json/bind/spi/JsonbProvider.html)提供JSON绑定对象。
-   [扩展：](https://docs.oracle.com/javaee/7/api/javax/enterprise/inject/spi/Extension.html)为 CDI 容器提供扩展。
-   [ConfigSourceProvider：](https://openliberty.io/docs/20.0.0.7/reference/javadoc/microprofile-1.2-javadoc.html#package=org/eclipse/microprofile/config/spi/package-frame.html&class=org/eclipse/microprofile/config/spi/ConfigSourceProvider.html)提供用于检索配置属性的源。

## 4. 展示：货币汇率应用程序

现在我们了解了基础知识，让我们描述设置汇率应用程序所需的步骤。

为了突出显示这些步骤，我们至少需要使用三个项目：exchange-rate-api、exchange-rate-impl和exchange-rate-app。

在 4.1 小节中，我们将 通过模块exchange-rate-api介绍Service、SPI 和ServiceLoader ，然后在 4.2 小节中介绍。我们将在exchange-rate-impl模块中实现我们的服务提供商，最后，我们将通过模块exchange-rate-app将所有内容整合到 4.3 小节中。

事实上，我们可以为服务提供者提供尽可能多的模块，并使它们在模块 exchange-rate-app 的类路径中可用。

### 4.1. 构建我们的 API

我们首先创建一个名为exchange-rate-api的 Maven 项目。名称以术语api结尾是一种很好的做法，但我们可以随意称呼它。

然后我们创建一个模型类来表示汇率货币：

```java
package com.baeldung.rate.api;

public class Quote {
    private String currency;
    private LocalDate date;
    ...
}
```

然后我们通过创建接口QuoteManager来定义我们的服务来检索报价：

```java
package com.baeldung.rate.api

public interface QuoteManager {
    List<Quote> getQuotes(String baseCurrency, LocalDate date);
}
```

接下来，我们为我们的服务创建一个SPI：

```java
package com.baeldung.rate.spi;

public interface ExchangeRateProvider {
    QuoteManager create();
}
```

最后，我们需要创建一个可供客户端代码使用的实用程序类ExchangeRate.java 。这个类委托给ServiceLoader。

首先，我们调用静态工厂方法load()来获取ServiceLoader 的实例：

```java
ServiceLoader<ExchangeRateProvider> loader = ServiceLoader .load(ExchangeRateProvider.class);

```

然后我们调用iterate()方法来搜索和检索所有可用的实现。

```java
Iterator<ExchangeRateProvider> = loader.iterator();

```

搜索结果被缓存，因此我们可以调用ServiceLoader.reload()方法来发现新安装的实现：

```java
Iterator<ExchangeRateProvider> = loader.reload();

```

这是我们的实用程序类：

```java
public class ExchangeRate {

    ServiceLoader<ExchangeRateProvider> loader = ServiceLoader
      .load(ExchangeRateProvider.class);
 
    public Iterator<ExchangeRateProvider> providers(boolean refresh) {
        if (refresh) {
            loader.reload();
        }
        return loader.iterator();
    }
}
```

现在我们有了获取所有已安装实现的服务，我们可以在我们的客户端代码中使用所有这些来扩展我们的应用程序，或者通过选择一个首选实现来只使用一个。

请注意，此实用程序类不需要成为api项目的一部分。客户端代码可以选择自己调用ServiceLoader 方法。

### 4.2. 构建服务提供者

现在让我们创建一个名为exchange-rate-impl的 Maven 项目，并将 API 依赖项添加到pom.xml：

```xml
<dependency>
    <groupId>com.baeldung</groupId>
    <artifactId>exchange-rate-api</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

然后我们创建一个实现我们的 SPI 的类：

```java
public class YahooFinanceExchangeRateProvider 
  implements ExchangeRateProvider {
 
    @Override
    public QuoteManager create() {
        return new YahooQuoteManagerImpl();
    }
}
```

这里是QuoteManager接口的实现：

```java
public class YahooQuoteManagerImpl implements QuoteManager {

    @Override
    public List<Quote> getQuotes(String baseCurrency, LocalDate date) {
        // fetch from Yahoo API
    }
}
```

为了被发现，我们创建一个提供者配置文件：

```bash
META-INF/services/com.baeldung.rate.spi.ExchangeRateProvider

```

该文件的内容是 SPI 实现的完全限定类名：

```bash
com.baeldung.rate.impl.YahooFinanceExchangeRateProvider

```

### 4.3. 把它放在一起

最后，让我们创建一个名为exchange-rate-app的客户端项目，并将依赖项 exchange-rate-api 添加到类路径中：

```xml
<dependency>
    <groupId>com.baeldung</groupId>
    <artifactId>exchange-rate-api</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

此时，我们可以从我们的应用程序中调用 SPI ：

```java
ExchangeRate.providers().forEach(provider -> ... );
```

### 4.4. 运行应用程序

现在让我们专注于构建我们所有的模块：

```bash
mvn clean package

```

然后我们使用Java命令运行我们的应用程序而不考虑提供程序：

```bash
java -cp ./exchange-rate-api/target/exchange-rate-api-1.0.0-SNAPSHOT.jar:./exchange-rate-app/target/exchange-rate-app-1.0.0-SNAPSHOT.jar com.baeldung.rate.app.MainApp
```

现在我们将我们的提供程序包含在java.ext.dirs扩展中，然后再次运行该应用程序：

```bash
java -Djava.ext.dirs=$JAVA_HOME/jre/lib/ext:./exchange-rate-impl/target:./exchange-rate-impl/target/depends -cp ./exchange-rate-api/target/exchange-rate-api-1.0.0-SNAPSHOT.jar:./exchange-rate-app/target/exchange-rate-app-1.0.0-SNAPSHOT.jar com.baeldung.rate.app.MainApp

```

我们可以看到我们的提供者已加载。

## 5.总结

现在我们已经通过明确定义的步骤探索了JavaSPI 机制，应该清楚地了解如何使用JavaSPI 创建易于扩展或可替换的模块。

尽管我们的示例使用 Yahoo 汇率服务来展示插入其他现有外部 API 的强大功能，但生产系统不需要依赖第三方 API 来创建出色的 SPI 应用程序。