## 1. 概述

在本教程中，我们将查看 Spring 和Spring Boot中已弃用的类，并解释它们已被替换的内容。

我们将从Spring 4和Spring Boot1.4 开始探索类。

## 2. Spring中弃用的类

为了便于阅读，我们列出了基于 Spring 版本的类及其替代品。而且，在每组类中，我们都按类名对它们进行排序，而不考虑包。

### 2.1. 春季 4.0.x

-   org.springframework.cache.interceptor.DefaultKeyGenerator –替换为 基于哈希码的SimpleKeyGenerator或自定义KeyGenerator实现
-   org.springframework.jdbc.support.lob.OracleLobHandler – Oracle 10g 驱动程序及更高版本的DefaultLobHandler ；我们甚至应该针对 Oracle 9i 数据库考虑它
-   org.springframework.test.AssertThrows –我们应该使用 JUnit 4 的@Test(expected=…)支持
-   org.springframework.http.converter.xml.XmlAwareFormHttpMessageConverter – AllEncompassingFormHttpMessageConverter

以下类从 Spring 4.0.2 开始弃用，支持 CGLIB 3.1 的默认策略，并在 Spring 4.1 中删除：

-   org.springframework.cglib.transform.impl.MemorySafeUndeclaredThrowableStrategy

所有不推荐使用的类，以及这个 Spring 版本不推荐使用的接口、字段、方法、构造函数和枚举常量都可以在 [官方文档页面](https://docs.spring.io/spring-framework/docs/4.0.x/javadoc-api/)上找到。

### 2.2. 春季 4.1.x

-   org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper – BeanPropertyRowMapper
-   org.springframework.jdbc.core.simple.ParameterizedSingleColumnRowMapper – SingleColumnRowMapper

[我们可以在 Spring 4.1.x JavaDoc 中](https://docs.spring.io/spring-framework/docs/4.1.x/javadoc-api/deprecated-list.html)找到完整列表。

### 2.3. 春季 4.2.x

-   org.springframework.web.servlet.view.document.AbstractExcelView – AbstractXlsView及其AbstractXlsxView和AbstractXlsxStreamingView变体
-   org.springframework.format.number.CurrencyFormatter – CurrencyStyleFormatter
-   org.springframework.messaging.simp.user.DefaultUserSessionRegistry –我们应该 结合 使用SimpUserRegistry和监听 AbstractSubProtocolEvent事件的ApplicationListener
-   org.springframework.messaging.handler.HandlerMethodSelector –广义和细化的MethodIntrospector
-   org.springframework.core.JdkVersion – 我们应该通过反射直接检查所需的 JDK API 变体
-   org.springframework.format.number.NumberFormatter – NumberStyleFormatter
-   org.springframework.format.number.PercentFormatter – PercentStyleFormatter
-   org.springframework.test.context.transaction.TransactionConfigurationAttributes –此类 在Spring 5中与@TransactionConfiguration 一起被删除
-   org.springframework.oxm.xmlbeans.XmlBeansMarshaller—— 继XMLBeans在 Apache 退役之后

为了支持 Apache Log4j 2，不推荐使用以下类：

-   org.springframework.web.util.Log4jConfigListener
-   org.springframework.util.Log4jConfigurer
-   org.springframework.web.filter.Log4jNestedDiagnosticContextFilter
-   org.springframework.web.context.request.Log4jNestedDiagnosticContextInterceptor
-   org.springframework.web.util.Log4jWebConfigurer

[Spring 4.2.x JavaDoc](https://docs.spring.io/spring-framework/docs/4.2.x/javadoc-api/deprecated-list.html)中提供了更多详细信息。

### 2.4. 春季 4.3.x

这个版本的 Spring 带来了很多弃用的类：

-   org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice—— 这个类在Spring Framework 5.1中被移除；我们应该改用 CORS
-   org.springframework.oxm.castor.CastorMarshaller –由于 Castor 项目缺乏活动而被弃用
-   org.springframework.web.servlet.mvc.method.annotation.CompletionStageReturnValueHandler – DeferredResultMethodReturnValueHandler，现在 通过适配器机制支持CompletionStage返回值
-   org.springframework.jdbc.support.incrementer.DB2MainframeSequenceMaxValueIncrementer –重命名为 Db2MainframeMaxValueIncrementer
-   org.springframework.jdbc.support.incrementer.DB2SequenceMaxValueIncrementer –重命名为Db2LuwMaxValueIncrementer
-   org.springframework.core.GenericCollectionTypeResolver –已弃用，取而代之的是直接使用ResolvableType
-   org.springframework.web.servlet.mvc.method.annotation.ListenableFutureReturnValueHandler – DeferredResultMethodReturnValueHandler，现在通过适配器机制支持ListenableFuture返回值
-   org.springframework.jdbc.support.incrementer.PostgreSQLSequenceMaxValueIncrementer –我们应该 改用PostgresSequenceMaxValueIncrementer
-   org.springframework.web.servlet.ResourceServlet – ResourceHttpRequestHandler

这些类已弃用，取而代之的是基于HandlerMethod的 MVC 基础结构：

-   org.springframework.web.servlet.mvc.support.ControllerClassNameHandlerMapping
-   org.springframework.web.bind.annotation.support.HandlerMethodInvoker
-   org.springframework.web.bind.annotation.support.HandlerMethodResolver

为了支持注解驱动的处理程序方法，不推荐使用几个类：

-   org.springframework.web.servlet.mvc.support.AbstractControllerUrlHandlerMapping
-   org.springframework.web.servlet.mvc.multiaction.AbstractUrlMethodNameResolver
-   org.springframework.web.servlet.mvc.support.ControllerBeanNameHandlerMapping
-   org.springframework.web.servlet.mvc.multiaction.InternalPathMethodNameResolver
-   org.springframework.web.servlet.mvc.multiaction.ParameterMethodNameResolver
-   org.springframework.web.servlet.mvc.multiaction.PropertiesMethodNameResolver

还有很多来自 Spring 的类，我们应该用它们的 Hibernate 4.x/5.x 等价物替换它们：

-   org.springframework.orm.hibernate3.support.AbstractLobType
-   org.springframework.orm.hibernate3.AbstractSessionFactoryBean
-   org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean
-   org.springframework.orm.hibernate3.support.BlobByteArrayType
-   org.springframework.orm.hibernate3.support.BlobSerializableType
-   org.springframework.orm.hibernate3.support.BlobStringType
-   org.springframework.orm.hibernate3.support.ClobStringType
-   org.springframework.orm.hibernate3.FilterDefinitionFactoryBean
-   org.springframework.orm.hibernate3.HibernateAccessor
-   org.springframework.orm.hibernate3.support.HibernateDaoSupport
-   org.springframework.orm.hibernate3.HibernateExceptionTranslator
-   org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean
-   org.springframework.orm.hibernate3.HibernateTemplate
-   org.springframework.orm.hibernate3.HibernateTransactionManager
-   org.springframework.orm.hibernate3.support.IdTransferringMergeEventListener
-   org.springframework.orm.hibernate3.LocalDataSourceConnectionProvider
-   org.springframework.orm.hibernate3.LocalJtaDataSourceConnectionProvider
-   org.springframework.orm.hibernate3.LocalRegionFactoryProxy
-   org.springframework.orm.hibernate3.LocalSessionFactoryBean
-   org.springframework.orm.hibernate3.LocalTransactionManagerLookup
-   org.springframework.orm.hibernate3.support.OpenSessionInterceptor
-   org.springframework.orm.hibernate3.support.OpenSessionInViewFilter
-   org.springframework.orm.hibernate3.support.OpenSessionInViewInterceptor
-   org.springframework.orm.hibernate3.support.ScopedBeanInterceptor
-   org.springframework.orm.hibernate3.SessionFactoryUtils
-   org.springframework.orm.hibernate3.SessionHolder
-   org.springframework.orm.hibernate3.SpringSessionContext
-   org.springframework.orm.hibernate3.SpringTransactionFactory
-   org.springframework.orm.hibernate3.TransactionAwareDataSourceConnectionProvider
-   org.springframework.orm.hibernate3.TypeDefinitionBean

为了支持 [FreeMarker，](https://www.baeldung.com/freemarker-in-spring-mvc-tutorial)不推荐使用几个类：

-   org.springframework.web.servlet.view.velocity.VelocityConfigurer
-   org.springframework.ui.velocity.VelocityEngineFactory
-   org.springframework.ui.velocity.VelocityEngineFactoryBean
-   org.springframework.ui.velocity.VelocityEngineUtils
-   org.springframework.web.servlet.view.velocity.VelocityLayoutView
-   org.springframework.web.servlet.view.velocity.VelocityLayoutViewResolver
-   org.springframework.web.servlet.view.velocity.VelocityToolboxView
-   org.springframework.web.servlet.view.velocity.VelocityView
-   org.springframework.web.servlet.view.velocity.VelocityViewResolver

这些类在 Spring Framework 5.1 中被删除，我们应该使用其他传输：

-   org.springframework.web.socket.sockjs.transport.handler.JsonpPollingTransportHandler
-   org.springframework.web.socket.sockjs.transport.handler.JsonpReceivingTransportHandler

最后，还有一些类没有合适的替代品：

-   org.springframework.core.ControlFlowFactory
-   org.springframework.util.WeakReferenceMonitor

与往常一样， [Spring 4.3.x JavaDoc](https://docs.spring.io/spring-framework/docs/4.3.x/javadoc-api/deprecated-list.html) 包含完整列表。

### 2.5. 春季 5.0.x

-   org.springframework.web.reactive.support.AbstractAnnotationConfigDispatcherHandlerInitializer –已弃用，取而代之的是AbstractReactiveWebInitializer
-   org.springframework.web.util.AbstractUriTemplateHandler – DefaultUriBuilderFactory
-   org ． _ _ _
-   org.springframework.web.client.AsyncRestTemplate  – WebClient
-   org.springframework.web.context.request.async.CallableProcessingInterceptorAdapter  –已弃用，因为 CallableProcessingInterceptor具有默认方法
-   org ． _ _ _
-   org.springframework.util.comparator.CompoundComparator –弃用标准 JDK 8 Comparator.thenComparing(Comparator)
-   org.springframework.web.util.DefaultUriTemplateHandler  – DefaultUriBuilderFactory；我们应该注意到 DefaultUriBuilderFactory 的parsePath属性具有不同的默认值(从false更改为true)
-   org.springframework.web.context.request.async.DeferredResultProcessingInterceptorAdapter—— 因为 DeferredResultProcessingInterceptor有默认方法
-   org.springframework.util.comparator.InvertibleComparator  –弃用标准 JDK 8 Comparator.reversed()
-   org.springframework.http.client.Netty4ClientHttpRequestFactory  –已弃用，取而代之的是ReactorClientHttpConnector
-   org.apache.commons.logging.impl.SimpleLog  –移动到 spring-jcl(实际上等同于NoOpLog)
-   org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter  – WebMvcConfigurer具有默认方法(由Java 8基线实现)并且可以直接实现而无需此适配器
-   org.springframework.beans.factory.config.YamlProcessor.StrictMapAppenderConstructor  –被 SnakeYAML 自己的重复键处理取代

我们弃用了两个类以支持AbstractReactiveWebInitializer：

-   org.springframework.web.reactive.support.AbstractDispatcherHandlerInitializer
-   org.springframework.web.reactive.support.AbstractServletHttpHandlerAdapterInitializer

并且，以下类没有替代品：

-   org.springframework.http.client.support.AsyncHttpAccessor
-   org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory
-   org.springframework.http.client.InterceptingAsyncClientHttpRequestFactory
-   org.springframework.http.client.support.InterceptingAsyncHttpAccessor
-   org.springframework.mock.http.client.MockAsyncClientHttpRequest

完整列表可在[Spring 5.0.x JavaDoc](https://docs.spring.io/spring-framework/docs/5.0.x/javadoc-api/deprecated-list.html)中找到。

### 2.6. 春季 5.1.x

-   org.springframework.http.client.support.BasicAuthorizationInterceptor  –弃用BasicAuthenticationInterceptor，它重用 HttpHeaders.setBasicAuth(java.lang.String, java.lang.String) 现在共享其默认字符集 ISO-8859-1 而不是像以前一样使用 UTF-8
-   org.springframework.jdbc.core.BatchUpdateUtils  —— JdbcTemplate不再使用 
-   org.springframework.web.reactive.function.client.ExchangeFilterFunctions.Credentials  –我们应该 在构建请求时使用HttpHeaders.setBasicAuth(String, String)方法
-   org ． _ _ _ _ _ _
-   org.springframework.jdbc.core.namedparam.NamedParameterBatchUpdateUtils  – NamedParameterJdbcTemplate不再使用 
-   org.springframework.core.io.PathResource – FileSystemResource.FileSystemResource(路径)
-   org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor  –我们应该使用构造函数注入来进行所需的设置(或自定义InitializingBean实现)
-   org.springframework.remoting.caucho.SimpleHessianServiceExporter  – HessianServiceExporter
-   org.springframework.remoting.httpinvoker.SimpleHttpInvokerServiceExporter – HttpInvokerServiceExporter
-   org.springframework.remoting.support.SimpleHttpServerFactoryBean  –嵌入式 Tomcat/Jetty/Undertow
-   org.springframework.remoting.jaxws.SimpleHttpServerJaxWsServiceExporter  – SimpleJaxWsServiceExporter

这些已弃用，取而代之的是 EncodedResourceResolver：

-   org.springframework.web.reactive.resource.GzipResourceResolver
-   org.springframework.web.servlet.resource.GzipResourceResolver

有几个类已弃用，以支持Java EE 7的DefaultManagedTaskScheduler：

-   org.springframework.scheduling.commonj.DelegatingTimerListener
-   org.springframework.scheduling.commonj.ScheduledTimerListener
-   org.springframework.scheduling.commonj.TimerManagerAccessor
-   org.springframework.scheduling.commonj.TimerManagerFactoryBean
-   org.springframework.scheduling.commonj.TimerManagerTaskScheduler

并且，有一些已被弃用，取而代之的是Java EE 7的DefaultManagedTaskExecutor：

-   org.springframework.scheduling.commonj.DelegatingWork
-   org.springframework.scheduling.commonj.WorkManagerTaskExecutor

最后，一个类在没有替代品的情况下被弃用：

-   org.apache.commons.logging.LogFactoryService

有关详细信息，请参阅官方[Spring 5.1.x JavaDoc on deprecated classes](https://docs.spring.io/spring-framework/docs/5.1.x/javadoc-api/deprecated-list.html)。

## 3.Spring Boot中弃用的类

现在，让我们看看Spring Boot回到 1.4 版本后弃用的类。

这里需要注意的是，对于Spring Boot1.4 和 1.5， 大多数替换类保留了它们的原始名称，但已移动到不同的包中。因此，我们在接下来的两个小节中为弃用类和替换类使用完全限定的类名。

### 3.1. 弹簧引导 1.4.x

-   org.springframework.boot.actuate.system.ApplicationPidFileWriter  –已弃用，取而代之的是org.springframework.boot.system.ApplicationPidFileWriter
-   org.springframework.boot.yaml.ArrayDocumentMatcher  –已弃用，支持基于String 的精确匹配
-   org.springframework.boot.test.ConfigFileApplicationContextInitializer  – org.springframework.boot.test.context.ConfigFileApplicationContextInitializer
-   org.springframework.boot.yaml.DefaultProfileDocumentMatcher—— 不再使用
-   org.springframework.boot.context.embedded.DelegatingFilterProxyRegistrationBean  – org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean
-   org.springframework.boot.actuate.system.EmbeddedServerPortFileWriter  – org.springframework.boot.system.EmbeddedServerPortFileWriter
-   org.springframework.boot.test.EnvironmentTestUtils  – org.springframework.boot.test.util.EnvironmentTestUtils
-   org.springframework.boot.context.embedded.ErrorPage  – org.springframework.boot.web.servlet.ErrorPage
-   org.springframework.boot.context.web.ErrorPageFilter  – org.springframework.boot.web.support.ErrorPageFilter
-   org.springframework.boot.context.embedded.FilterRegistrationBean  – org.springframework.boot.web.servlet.FilterRegistrationBean
-   org.springframework.boot.test.IntegrationTestPropertiesListener  – @IntegrationTest不再使用它 
-   org.springframework.boot.context.embedded.MultipartConfigFactory  – org.springframework.boot.web.servlet.MultipartConfigFactory
-   org.springframework.boot.context.web.OrderedCharacterEncodingFilter  – org.springframework.boot.web.filter.OrderedCharacterEncodingFilter
-   org.springframework.boot.context.web.OrderedHiddenHttpMethodFilter  – org.springframework.boot.web.filter.OrderedHiddenHttpMethodFilter
-   org.springframework.boot.context.web.OrderedHttpPutFormContentFilter  – org.springframework.boot.web.filter.OrderedHttpPutFormContentFilter
-   org.springframework.boot.context.web.OrderedRequestContextFilter  – org.springframework.boot.web.filter.OrderedRequestContextFilter
-   org.springframework.boot.test.OutputCapture  – org.springframework.boot.test.rule.OutputCapture
-   org.springframework.boot.context.web.ServerPortInfoApplicationContextInitializer – org.springframework.boot.context.embedded.ServerPortInfoApplicationContextInitializer
-   org.springframework.boot.context.web.ServletContextApplicationContextInitializer – org.springframework.boot.web.support.ServletContextApplicationContextInitializer
-   org.springframework.boot.context.embedded.ServletListenerRegistrationBean  – org.springframework.boot.web.servlet.ServletListenerRegistrationBean
-   org.springframework.boot.context.embedded.ServletRegistrationBean  – org.springframework.boot.web.servlet.ServletRegistrationBean
-   org.springframework.boot.test.SpringApplicationContextLoader  –已弃用，取而代之的是@SpringBootTest；如果有必要，我们也可以使用 org.springframework.boot.test.context.SpringBootContextLoader
-   org.springframework.boot.test.SpringBootMockServletContext  – org.springframework.boot.test.mock.web.SpringBootMockServletContext
-   org.springframework.boot.context.web.SpringBootServletInitializer  – org.springframework.boot.web.support.SpringBootServletInitializer
-   org.springframework.boot.test.TestRestTemplate  – org.springframework.boot.test.web.client.TestRestTemplate

由于在 Spring Framework 4.3 中弃用了 Velocity 支持，因此在Spring Boot中也弃用了以下类：

-   org.springframework.boot.web.servlet.view.velocity.EmbeddedVelocityViewResolver
-   org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration
-   org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration.VelocityConfiguration
-   org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration.VelocityNonWebConfiguration
-   org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration.VelocityWebConfiguration
-   org.springframework.boot.autoconfigure.velocity.VelocityProperties
-   org.springframework.boot.autoconfigure.velocity.VelocityTemplateAvailabilityProvider

Spring [Boot 1.4.x JavaDoc](https://docs.spring.io/spring-boot/docs/1.4.x/api/deprecated-list.html) 有完整列表。

### 3.2. 弹簧引导 1.5.x

-   org.springframework.boot.context.event.ApplicationStartedEvent  –已弃用，取而代之的是org.springframework.boot.context.event.ApplicationStartingEvent
-   org.springframework.boot.autoconfigure.EnableAutoConfigurationImportSelector  –已弃用，取而代之的是org.springframework.boot.autoconfigure.AutoConfigurationImportSelector
-   org.springframework.boot.actuate.cache.GuavaCacheStatisticsProvider  –在 Spring Framework 5 中移除 Guava 支持之后
-   org.springframework.boot.loader.tools.Layouts.Module – 已弃用，取而代之的是自定义LayoutFactory
-   org.springframework.boot.autoconfigure.MessageSourceAutoConfiguration  –已弃用，取而代之的是org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
-   org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration  –已弃用，取而代之的是org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration
-   org.springframework.boot.actuate.autoconfigure.ShellProperties  –已弃用，因为 CRaSH 未得到积极维护

这两个类已被弃用，因为 CRaSH 没有得到积极维护：

-   org.springframework.boot.actuate.autoconfigure.CrshAutoConfiguration
-   org.springframework.boot.actuate.autoconfigure.CrshAutoConfiguration.AuthenticationManagerAdapterConfiguration

还有一些类没有替换：

-   org.springframework.boot.autoconfigure.cache.CacheProperties.Hazelcast
-   org.springframework.boot.autoconfigure.jdbc.metadata.CommonsDbcpDataSourcePoolMetadata
-   org.springframework.boot.autoconfigure.mustache.MustacheCompilerFactoryBean

要查看弃用内容的完整列表，我们可以查阅 [官方Spring Boot1.5.x JavaDoc 站点](https://docs.spring.io/spring-boot/docs/1.5.x/api/deprecated-list.html)。

### 3.3. 春季启动 2.0.x

-   org.springframework.boot.test.util.EnvironmentTestUtils  –已弃用，取而代之的是TestPropertyValues
-   org.springframework.boot.actuate.metrics.web.reactive.server.RouterFunctionMetrics  –已弃用，取而代之的是自动配置的MetricsWebFilter

而且一类没有替代品：

-   org.springframework.boot.actuate.autoconfigure.couchbase.CouchbaseHealthIndicator属性

请查看[Spring Boot 2.0.x 的弃用列表](https://docs.spring.io/spring-boot/docs/2.0.x/api/deprecated-list.html)以获取更多详细信息。

### 3.4. 春季启动 2.1.x

-   org.springframework.boot.actuate.health.CompositeHealthIndicatorFactory  –已弃用，取而代之的是CompositeHealthIndicator.CompositeHealthIndicator(HealthAggregator, HealthIndicatorRegistry)
-   org.springframework.boot.actuate.health.CompositeReactiveHealthIndicatorFactory  –已弃用，取而代之的是CompositeReactiveHealthIndicator.CompositeReactiveHealthIndicator(HealthAggregator, ReactiveHealthIndicatorRegistry)

[最后，我们可以查阅Spring Boot 2.1.x 中弃用的类和接口](https://docs.spring.io/spring-boot/docs/2.1.x/api/deprecated-list.html)的完整列表 。

## 4。总结

在本教程中，我们探讨了 Spring 自版本 4 和Spring Boot版本 1.4 以来弃用的类，以及它们相应的替代品(如果可用)。