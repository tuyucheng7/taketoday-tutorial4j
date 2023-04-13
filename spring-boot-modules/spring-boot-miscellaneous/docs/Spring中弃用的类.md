## 1. 概述

在本教程中，我们将查看Spring和Spring Boot中已弃用的类，并解释它们被什么内容替换。

我们将探索从Spring 4和Spring Boot 1.4开始的类。

## 2. Spring中弃用的类

为了便于阅读，我们列出了基于Spring版本的类及其替代品。而且，在每组类中，我们都按类名对它们进行排序，而不考虑包。

### 2.1 Spring 4.0.x

-   org.springframework.cache.interceptor.DefaultKeyGenerator：替换为SimpleKeyGenerator或基于哈希码的自定义KeyGenerator实现
-   org.springframework.jdbc.support.lob.OracleLobHandler：适用于Oracle 10g及更高版本的DefaultLobHandler；我们甚至应该针对Oracle 9i数据库考虑它
-   org.springframework.test.AssertThrows：我们应该使用JUnit 4的@Test(expected=...)支持
-   org.springframework.http.converter.xml.XmlAwareFormHttpMessageConverter：AllEncompassingFormHttpMessageConverter

以下类从Spring 4.0.2开始弃用，取而代之的是CGLIB 3.1的默认策略，并在Spring 4.1中删除：

-   org.springframework.cglib.transform.impl.MemorySafeUndeclaredThrowableStrategy

所有已弃用的类，以及这个Spring版本不推荐使用的接口、字段、方法、构造函数和枚举常量都可以在[官方文档页面](https://docs.spring.io/spring-framework/docs/4.0.x/javadoc-api/)上找到。

### 2.2 Spring 4.1.x

-   org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper：BeanPropertyRowMapper
-   org.springframework.jdbc.core.simple.ParameterizedSingleColumnRowMapper：SingleColumnRowMapper

我们可以在[Spring 4.1.x JavaDoc中](https://docs.spring.io/spring-framework/docs/4.1.x/javadoc-api/deprecated-list.html)找到完整的列表。

### 2.3 Spring 4.2.x

-   org.springframework.web.servlet.view.document.AbstractExcelView：AbstractXlsView及其AbstractXlsxView和AbstractXlsxStreamingView变体
-   org.springframework.format.number.CurrencyFormatter：CurrencyStyleFormatter
-   org.springframework.messaging.simp.user.DefaultUserSessionRegistry：我们应该结合使用SimpUserRegistry和监听AbstractSubProtocolEvent事件的ApplicationListener
-   org.springframework.messaging.handler.HandlerMethodSelector：广义和细化的MethodIntrospector
-   org.springframework.core.JdkVersion：我们应该通过反射直接检查所需的JDK API变体
-   org.springframework.format.number.NumberFormatter：NumberStyleFormatter
-   org.springframework.format.number.PercentFormatter：PercentStyleFormatter
-   org.springframework.test.context.transaction.TransactionConfigurationAttributes：此类在Spring 5中与@TransactionConfiguration一起被删除
-   org.springframework.oxm.xmlbeans.XmlBeansMarshaller：继XMLBeans在Apache弃用之后

为了支持Apache Log4j2，不推荐使用以下类：

-   org.springframework.web.util.Log4jConfigListener
-   org.springframework.util.Log4jConfigurer
-   org.springframework.web.filter.Log4jNestedDiagnosticContextFilter
-   org.springframework.web.context.request.Log4jNestedDiagnosticContextInterceptor
-   org.springframework.web.util.Log4jWebConfigurer

[Spring 4.2.x JavaDoc](https://docs.spring.io/spring-framework/docs/4.2.x/javadoc-api/deprecated-list.html)中提供了更多详细信息。

### 2.4 Spring 4.3.x

这个版本的Spring带来了很多弃用的类：

-   org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice：这个类在Spring框架5.1中被移除；我们应该改用CORS
-   org.springframework.oxm.castor.CastorMarshaller：由于Castor项目缺乏活跃度而被弃用
-   org.springframework.web.servlet.mvc.method.annotation.CompletionStageReturnValueHandler：DeferredResultMethodReturnValueHandler，现在通过适配器机制支持CompletionStage返回值
-   org.springframework.jdbc.support.incrementer.DB2MainframeSequenceMaxValueIncrementer：重命名为Db2MainframeMaxValueIncrementer
-   org.springframework.jdbc.support.incrementer.DB2SequenceMaxValueIncrementer：重命名为Db2LuwMaxValueIncrementer
-   org.springframework.core.GenericCollectionTypeResolver：已弃用，取而代之的是直接使用ResolvableType
-   org.springframework.web.servlet.mvc.method.annotation.ListenableFutureReturnValueHandler：DeferredResultMethodReturnValueHandler，现在通过适配器机制支持ListenableFuture返回值
-   org.springframework.jdbc.support.incrementer.PostgreSQLSequenceMaxValueIncrementer：我们应该改用PostgresSequenceMaxValueIncrementer
-   org.springframework.web.servlet.ResourceServlet：ResourceHttpRequestHandler

这些类已被弃用，取而代之的是基于HandlerMethod的MVC基础结构：

-   org.springframework.web.servlet.mvc.support.ControllerClassNameHandlerMapping
-   org.springframework.web.bind.annotation.support.HandlerMethodInvoker
-   org.springframework.web.bind.annotation.support.HandlerMethodResolver

为了支持注解驱动的处理程序方法，以下类被弃用：

-   org.springframework.web.servlet.mvc.support.AbstractControllerUrlHandlerMapping
-   org.springframework.web.servlet.mvc.multiaction.AbstractUrlMethodNameResolver
-   org.springframework.web.servlet.mvc.support.ControllerBeanNameHandlerMapping
-   org.springframework.web.servlet.mvc.multiaction.InternalPathMethodNameResolver
-   org.springframework.web.servlet.mvc.multiaction.ParameterMethodNameResolver
-   org.springframework.web.servlet.mvc.multiaction.PropertiesMethodNameResolver

还有很多来自Spring的类，我们应该用它们的Hibernate 4.x/5.x等价物替换它们：

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

为了支持[FreeMarker](https://www.baeldung.com/freemarker-in-spring-mvc-tutorial)，以下类被弃用：

-   org.springframework.web.servlet.view.velocity.VelocityConfigurer
-   org.springframework.ui.velocity.VelocityEngineFactory
-   org.springframework.ui.velocity.VelocityEngineFactoryBean
-   org.springframework.ui.velocity.VelocityEngineUtils
-   org.springframework.web.servlet.view.velocity.VelocityLayoutView
-   org.springframework.web.servlet.view.velocity.VelocityLayoutViewResolver
-   org.springframework.web.servlet.view.velocity.VelocityToolboxView
-   org.springframework.web.servlet.view.velocity.VelocityView
-   org.springframework.web.servlet.view.velocity.VelocityViewResolver

这些类在Spring Framework 5.1中被删除：

-   org.springframework.web.socket.sockjs.transport.handler.JsonpPollingTransportHandler
-   org.springframework.web.socket.sockjs.transport.handler.JsonpReceivingTransportHandler

最后，还有一些类没有合适的替代品：

-   org.springframework.core.ControlFlowFactory
-   org.springframework.util.WeakReferenceMonitor

与往常一样，[Spring 4.3.x JavaDoc](https://docs.spring.io/spring-framework/docs/4.3.x/javadoc-api/deprecated-list.html)包含完整的列表。

### 2.5 Spring 5.0.x

-   org.springframework.web.reactive.support.AbstractAnnotationConfigDispatcherHandlerInitializer：已弃用，取而代之的是AbstractReactiveWebInitializer
-   org.springframework.web.util.AbstractUriTemplateHandler：DefaultUriBuilderFactory
-   org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer：已弃用，支持简单地使用具有默认方法的WebSocketMessageBrokerConfigurer，可通过Java 8基线实现
-   org.springframework.web.client.AsyncRestTemplate：WebClient
-   org.springframework.web.context.request.async.CallableProcessingInterceptorAdapter：已弃用，因为CallableProcessingInterceptor具有默认方法
-   org.springframework.messaging.support.ChannelInterceptorAdapter：已弃用，因为ChannelInterceptor具有默认方法(Java 8基线使之成为可能)并且可以直接实现而无需此无操作适配器
-   org.springframework.util.comparator.CompoundComparator：已弃用，转而支持标准JDK 8 Comparator.thenComparing(Comparator)
-   org.springframework.web.util.DefaultUriTemplateHandler：DefaultUriBuilderFactory；我们应该注意到DefaultUriBuilderFactory的parsePath属性具有不同的默认值(从false更改为true)
-   org.springframework.web.context.request.async.DeferredResultProcessingInterceptorAdapter：因为DeferredResultProcessingInterceptor有默认方法
-   org.springframework.util.comparator.InvertibleComparator：已弃用，转而支持标准JDK 8 Comparator.reversed()
-   org.springframework.http.client.Netty4ClientHttpRequestFactory：已弃用，取而代之的是ReactorClientHttpConnector
-   org.apache.commons.logging.impl.SimpleLog：移动到spring-jcl(实际上等同于NoOpLog)
-   org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter：WebMvcConfigurer具有默认方法(由Java 8基线实现)并且可以直接实现而无需此适配器
-   org.springframework.beans.factory.config.YamlProcessor.StrictMapAppenderConstructor：被SnakeYAML自己的重复键处理取代

有两个类被弃用以支持AbstractReactiveWebInitializer：

-   org.springframework.web.reactive.support.AbstractDispatcherHandlerInitializer
-   org.springframework.web.reactive.support.AbstractServletHttpHandlerAdapterInitializer

并且，以下类没有替代品：

-   org.springframework.http.client.support.AsyncHttpAccessor
-   org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory
-   org.springframework.http.client.InterceptingAsyncClientHttpRequestFactory
-   org.springframework.http.client.support.InterceptingAsyncHttpAccessor
-   org.springframework.mock.http.client.MockAsyncClientHttpRequest

完整列表可在[Spring 5.0.x JavaDoc](https://docs.spring.io/spring-framework/docs/5.0.x/javadoc-api/deprecated-list.html)中找到。

### 2.6 Spring 5.1.x

-   org.springframework.http.client.support.BasicAuthorizationInterceptor：已弃用，取而代之的是BasicAuthenticationInterceptor，它重用HttpHeaders.setBasicAuth(java.lang.String, java.lang.String)，现在共享其默认字符集ISO-8859-1而不是像以前一样使用UTF-8
-   org.springframework.jdbc.core.BatchUpdateUtils：不再被JdbcTemplate使用 
-   org.springframework.web.reactive.function.client.ExchangeFilterFunctions.Credentials：我们应该在构建请求时使用HttpHeaders.setBasicAuth(String, String)方法
-   org.springframework.web.filter.reactive.ForwardedHeaderFilter：不推荐使用此过滤器，以支持使用 ForwardedHeaderTransformer，它可以声明为名为“forwardedHeaderTransformer”的bean，或者在WebHttpHandlerBuilder中显式注册
-   org.springframework.jdbc.core.namedparam.NamedParameterBatchUpdateUtils：不再被NamedParameterJdbcTemplate使用 
-   org.springframework.core.io.PathResource：FileSystemResource.FileSystemResource(Path)
-   org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor：我们应该使用构造函数注入来进行所需的设置(或自定义InitializingBean实现)
-   org.springframework.remoting.caucho.SimpleHessianServiceExporter：HessianServiceExporter
-   org.springframework.remoting.httpinvoker.SimpleHttpInvokerServiceExporter：HttpInvokerServiceExporter
-   org.springframework.remoting.support.SimpleHttpServerFactoryBean：嵌入式Tomcat/Jetty/Undertow
-   org.springframework.remoting.jaxws.SimpleHttpServerJaxWsServiceExporter：SimpleJaxWsServiceExporter

这些被弃用，取而代之的是EncodedResourceResolver：

-   org.springframework.web.reactive.resource.GzipResourceResolver
-   org.springframework.web.servlet.resource.GzipResourceResolver

有几个类已弃用，以支持Java EE 7的DefaultManagedTaskScheduler：

-   org.springframework.scheduling.commonj.DelegatingTimerListener
-   org.springframework.scheduling.commonj.ScheduledTimerListener
-   org.springframework.scheduling.commonj.TimerManagerAccessor
-   org.springframework.scheduling.commonj.TimerManagerFactoryBean
-   org.springframework.scheduling.commonj.TimerManagerTaskScheduler

并且，这些类已被弃用，取而代之的是Java EE 7的DefaultManagedTaskExecutor：

-   org.springframework.scheduling.commonj.DelegatingWork
-   org.springframework.scheduling.commonj.WorkManagerTaskExecutor

最后，这个类被弃用，没有对应的替代品：

-   org.apache.commons.logging.LogFactoryService

有关详细信息，请参阅官方[Spring 5.1.x JavaDoc已弃用类文档](https://docs.spring.io/spring-framework/docs/5.1.x/javadoc-api/deprecated-list.html)。

## 3. Spring Boot中弃用的类

现在，让我们看一下Spring Boot中已弃用的类，回到1.4版本。

这里需要注意的是，对于Spring Boot 1.4和1.5， 大多数替换类保留了它们的原始名称，但已移动到不同的包中。因此，在接下来的两个小节中，我们对已弃用的类和替换类使用完全限定的类名。

### 3.1 Spring Boot 1.4.x

-   org.springframework.boot.actuate.system.ApplicationPidFileWriter：已弃用，取而代之的是org.springframework.boot.system.ApplicationPidFileWriter
-   org.springframework.boot.yaml.ArrayDocumentMatcher：已弃用，支持基于String的精确匹配
-   org.springframework.boot.test.ConfigFileApplicationContextInitializer：org.springframework.boot.test.context.ConfigFileApplicationContextInitializer
-   org.springframework.boot.yaml.DefaultProfileDocumentMatcher：不再使用
-   org.springframework.boot.context.embedded.DelegatingFilterProxyRegistrationBean：org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean
-   org.springframework.boot.actuate.system.EmbeddedServerPortFileWriter：org.springframework.boot.system.EmbeddedServerPortFileWriter
-   org.springframework.boot.test.EnvironmentTestUtils：org.springframework.boot.test.util.EnvironmentTestUtils
-   org.springframework.boot.context.embedded.ErrorPage：org.springframework.boot.web.servlet.ErrorPage
-   org.springframework.boot.context.web.ErrorPageFilter：org.springframework.boot.web.support.ErrorPageFilter
-   org.springframework.boot.context.embedded.FilterRegistrationBean：org.springframework.boot.web.servlet.FilterRegistrationBean
-   org.springframework.boot.test.IntegrationTestPropertiesListener：不再被@IntegrationTest使用
-   org.springframework.boot.context.embedded.MultipartConfigFactory：org.springframework.boot.web.servlet.MultipartConfigFactory
-   org.springframework.boot.context.web.OrderedCharacterEncodingFilter：org.springframework.boot.web.filter.OrderedCharacterEncodingFilter
-   org.springframework.boot.context.web.OrderedHiddenHttpMethodFilter：org.springframework.boot.web.filter.OrderedHiddenHttpMethodFilter
-   org.springframework.boot.context.web.OrderedHttpPutFormContentFilter：org.springframework.boot.web.filter.OrderedHttpPutFormContentFilter
-   org.springframework.boot.context.web.OrderedRequestContextFilter：org.springframework.boot.web.filter.OrderedRequestContextFilter
-   org.springframework.boot.test.OutputCapture：org.springframework.boot.test.rule.OutputCapture
-   org.springframework.boot.context.web.ServerPortInfoApplicationContextInitializer：org.springframework.boot.context.embedded.ServerPortInfoApplicationContextInitializer
-   org.springframework.boot.context.web.ServletContextApplicationContextInitializer：org.springframework.boot.web.support.ServletContextApplicationContextInitializer
-   org.springframework.boot.context.embedded.ServletListenerRegistrationBean：org.springframework.boot.web.servlet.ServletListenerRegistrationBean
-   org.springframework.boot.context.embedded.ServletRegistrationBean：org.springframework.boot.web.servlet.ServletRegistrationBean
-   org.springframework.boot.test.SpringApplicationContextLoader：已弃用，取而代之的是@SpringBootTest；如果有必要，我们也可以使用org.springframework.boot.test.context.SpringBootContextLoader
-   org.springframework.boot.test.SpringBootMockServletContext：org.springframework.boot.test.mock.web.SpringBootMockServletContext
-   org.springframework.boot.context.web.SpringBootServletInitializer：org.springframework.boot.web.support.SpringBootServletInitializer
-   org.springframework.boot.test.TestRestTemplate：org.springframework.boot.test.web.client.TestRestTemplate

由于在Spring Framework 4.3中弃用了Velocity支持，因此在Spring Boot中也弃用了以下类：

-   org.springframework.boot.web.servlet.view.velocity.EmbeddedVelocityViewResolver
-   org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration
-   org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration.VelocityConfiguration
-   org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration.VelocityNonWebConfiguration
-   org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration.VelocityWebConfiguration
-   org.springframework.boot.autoconfigure.velocity.VelocityProperties
-   org.springframework.boot.autoconfigure.velocity.VelocityTemplateAvailabilityProvider

[Spring Boot 1.4.x JavaDoc](https://docs.spring.io/spring-boot/docs/1.4.x/api/deprecated-list.html)有完整的列表。

### 3.2 Spring Boot 1.5.x

-   org.springframework.boot.context.event.ApplicationStartedEvent：已弃用，取而代之的是org.springframework.boot.context.event.ApplicationStartingEvent
-   org.springframework.boot.autoconfigure.EnableAutoConfigurationImportSelector：已弃用，取而代之的是org.springframework.boot.autoconfigure.AutoConfigurationImportSelector
-   org.springframework.boot.actuate.cache.GuavaCacheStatisticsProvider：在Spring Framework 5中移除Guava支持之后
-   org.springframework.boot.loader.tools.Layouts.Module：已弃用，取而代之的是自定义LayoutFactory
-   org.springframework.boot.autoconfigure.MessageSourceAutoConfiguration：已弃用，取而代之的是org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
-   org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration：已弃用，取而代之的是org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration
-   org.springframework.boot.actuate.autoconfigure.ShellProperties：已弃用，因为CRaSH未得到积极维护

这两个类已被弃用，因为CRaSH没有得到积极维护：

-   org.springframework.boot.actuate.autoconfigure.CrshAutoConfiguration
-   org.springframework.boot.actuate.autoconfigure.CrshAutoConfiguration.AuthenticationManagerAdapterConfiguration

还有一些没有替换品的类：

-   org.springframework.boot.autoconfigure.cache.CacheProperties.Hazelcast
-   org.springframework.boot.autoconfigure.jdbc.metadata.CommonsDbcpDataSourcePoolMetadata
-   org.springframework.boot.autoconfigure.mustache.MustacheCompilerFactoryBean

要查看已弃用内容的完整列表，我们可以查阅[官方Spring Boot 1.5.x JavaDoc站点](https://docs.spring.io/spring-boot/docs/1.5.x/api/deprecated-list.html)。

### 3.3 Spring Boot 2.0.x

-   org.springframework.boot.test.util.EnvironmentTestUtils：已弃用，取而代之的是TestPropertyValues
-   org.springframework.boot.actuate.metrics.web.reactive.server.RouterFunctionMetrics：已弃用，取而代之的是自动配置的MetricsWebFilter

一个没有替代品的类：

-   org.springframework.boot.actuate.autoconfigure.couchbase.CouchbaseHealthIndicatorProperties

请查看[Spring Boot 2.0.x的弃用列表](https://docs.spring.io/spring-boot/docs/2.0.x/api/deprecated-list.html)以获取更多详细信息。

### 3.4 Spring Boot 2.1.x

-   org.springframework.boot.actuate.health.CompositeHealthIndicatorFactory：已弃用，取而代之的是CompositeHealthIndicator.CompositeHealthIndicator(HealthAggregator, HealthIndicatorRegistry)
-   org.springframework.boot.actuate.health.CompositeReactiveHealthIndicatorFactory：已弃用，取而代之的是CompositeReactiveHealthIndicator.CompositeReactiveHealthIndicator(HealthAggregator, ReactiveHealthIndicatorRegistry)

最后，我们可以查阅[Spring Boot 2.1.x中弃用的类和接口](https://docs.spring.io/spring-boot/docs/2.1.x/api/deprecated-list.html)的完整列表。

## 4. 总结

在本教程中，我们探讨了Spring自版本4和Spring Boot版本1.4以来弃用的类，以及它们相应的替代品(如果可用)。