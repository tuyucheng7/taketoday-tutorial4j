## 1. 概述

在许多其他文章中，我们提到了BeanPostProcessor。在本文中，我们将在使用Guava的EventBus的真实示例中使用它们。

**Spring的BeanPostProcessor为我们提供了连接到Spring bean生命周期的hook，以修改其配置**。

BeanPostProcessor允许直接修改bean本身。

在本文中，我们将看看这些类集成Guava的EventBus的具体示例。

## 2. 项目构建

首先，我们需要构建我们的环境。让我们将spring-context、spring-expression和guava依赖项添加到我们的build.gradle中：

```groovy
ext {
    guava = '31.0.1-jre'
    spring = '5.3.13'
}

dependencies {
    implementation "org.springframework:spring-context:${spring}"
    implementation "org.springframework:spring-expression:${spring}"
    implementation "com.google.guava:guava:${guava}"
}
```

## 3. 目标和实现

对于我们的第一个目标，我们想**利用Guava的EventBus在系统的各个方面异步传递消息**。

接下来，我们希望在bean创建/销毁时自动注册和注销事件对象，而不是使用EventBus提供的手动方法。

所以，我们现在准备开始编码！

我们的实现将包括Guava的EventBus的包装类、自定义标记注解、BeanPostProcessor、模型对象和从EventBus接收股票交易事件的bean。
此外，我们将创建一个测试用例来验证所需的功能。

### 3.1 EventBus包装器

首先，我们将定义一个EventBus包装器来提供一些静态方法，以便为BeanPostProcessor使用的事件轻松注册和注销bean：

```java
public final class GlobalEventBus {
    public static final String GLOBAL_EVENT_BUS_EXPRESSION = "T(cn.tuyucheng.taketoday.beanpostprocessor.GlobalEventBus).getEventBus()";
    private static final String IDENTIFIER = "global-event-bus";
    private static final GlobalEventBus GLOBAL_EVENT_BUS = new GlobalEventBus();
    private final EventBus eventBus = new AsyncEventBus(IDENTIFIER, Executors.newCachedThreadPool());

    private GlobalEventBus() {
    }

    public static GlobalEventBus getInstance() {
        return GlobalEventBus.GLOBAL_EVENT_BUS;
    }

    public static EventBus getEventBus() {
        return GlobalEventBus.GLOBAL_EVENT_BUS.eventBus;
    }

    public static void subscribe(Object obj) {
        getEventBus().register(obj);
    }

    public static void unsubscribe(Object obj) {
        getEventBus().unregister(obj);
    }

    public static void post(Object event) {
        getEventBus().post(event);
    }
}
```

上述代码提供了用于访问GlobalEventBus和底层EventBus以及注册和注销事件以及发布事件的静态方法。
它还有一个SpEL表达式，用作我们自定义注解中的默认表达式，以定义我们要使用的EventBus。

### 3.2 自定义标记注解

接下来，让我们定义一个自定义标记注解，BeanPostProcessor将使用该注解来识别要自动注册/注销事件的bean：

```java
/**
 * An annotation which indicates which Guava {@link com.google.common.eventbus.EventBus} a Spring bean wishes to subscribe to.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Subscriber {

    /**
     * A SpEL expression which selects the {@link com.google.common.eventbus.EventBus}.
     */
    String value() default GlobalEventBus.GLOBAL_EVENT_BUS_EXPRESSION;
}
```

### 3.3 BeanPostProcessor

现在，我们将定义BeanPostProcessor，它将检查每个bean的Subscriber注解。
这个类也是一个DestructionAwareBeanPostProcessor，它是一个Spring接口，向BeanPostProcessor添加了一个销毁前的回调。
如果存在注解，我们将在bean初始化时将其注册到由注解的SpEL表达式标识的EventBus中，并在bean销毁时取消注册：

```java
/**
 * A {@link DestructionAwareBeanPostProcessor} which registers/un-registers subscribers to a Guava {@link EventBus}. The class must
 * be annotated with {@link Subscriber} and each subscribing method must be annotated with
 * {@link com.google.common.eventbus.Subscribe}.
 */
@SuppressWarnings("ALL")
public class GuavaEventBusBeanPostProcessor implements DestructionAwareBeanPostProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SpelExpressionParser expressionParser = new SpelExpressionParser();

    @Override
    public void postProcessBeforeDestruction(final Object bean, final String beanName) throws BeansException {
        this.process(bean, EventBus::unregister, "destruction");
    }

    @Override
    public boolean requiresDestruction(Object bean) {
        return true;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        this.process(bean, EventBus::register, "initialization");
        return bean;
    }

    private void process(final Object bean, final BiConsumer<EventBus, Object> consumer, final String action) {
        // See implementation below
    }
}
```

上面的代码获取每个bean并通过，下面定义的process方法运行它。它在bean初始化之后和销毁之前对其进行处理。
requiresDestruction方法默认返回true，当我们在postProcessBeforeDestruction回调中检查@Subscriber注解是否存在时，我们会保留该行为。

现在让我们看一下process方法：

```java

@SuppressWarnings("ALL")
public class GuavaEventBusBeanPostProcessor implements DestructionAwareBeanPostProcessor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SpelExpressionParser expressionParser = new SpelExpressionParser();

    private void process(final Object bean, final BiConsumer<EventBus, Object> consumer, final String action) {
        Object proxy = this.getTargetObject(bean);
        final Subscriber annotation = AnnotationUtils.getAnnotation(proxy.getClass(), Subscriber.class);
        if (annotation == null)
            return;
        this.logger.info("{}: processing bean of type {} during {}",
                this.getClass().getSimpleName(), proxy.getClass().getName(), action);
        final String annotationValue = annotation.value();
        try {
            final Expression expression = this.expressionParser.parseExpression(annotationValue);
            final Object value = expression.getValue();
            if (!(value instanceof EventBus)) {
                this.logger.error("{}: expression {} did not evaluate to an instance of EventBus for bean of type {}",
                        this.getClass().getSimpleName(), annotationValue, proxy.getClass().getSimpleName());
                return;
            }
            final EventBus eventBus = (EventBus) value;
            consumer.accept(eventBus, proxy);
        } catch (ExpressionException ex) {
            this.logger.error("{}: unable to parse/evaluate expression {} for bean of type {}",
                    this.getClass().getSimpleName(), annotationValue, proxy.getClass().getName());
        }
    }
}
```

以上代码检查是否存在名为Subscriber的自定义标记注解，如果存在，则从其value属性中读取SpEL表达式。
然后，SpEL表达式被解析为一个Expression对象。如果它是EventBus的实例，我们将BiConsumer函数参数应用于bean。
BiConsumer用于从EventBus注册和注销bean。

getTargetObject方法的实现如下：

```java

@SuppressWarnings("ALL")
public class GuavaEventBusBeanPostProcessor implements DestructionAwareBeanPostProcessor {

    private Object getTargetObject(Object proxy) throws BeansException {
        if (AopUtils.isJdkDynamicProxy(proxy)) {
            try {
                return ((Advised) proxy).getTargetSource().getTarget();
            } catch (Exception e) {
                throw new FatalBeanException("Error getting target of JDK proxy", e);
            }
        }
        return proxy;
    }
}
```

### 3.4 StockTrade模型对象

接下来，让我们定义StockTrade模型对象：

```java
public record StockTrade(String symbol, int quantity, double price, Date tradeDate) {

}
```

### 3.5 StockTradePublisher事件接收器

然后，让我们定义一个监听器类来通知我们收到了交易，这样我们就可以编写我们的测试了：

```java

@FunctionalInterface
public interface StockTradeListener {
    void stockTradePublished(StockTrade trade);
}
```

最后，我们将为新的StockTrade事件定义一个接收者：

```java

@Subscriber
public class StockTradePublisher {
    private final Set<StockTradeListener> stockTradeListeners = new HashSet<>();

    public void addStockTradeListener(StockTradeListener listener) {
        synchronized (this.stockTradeListeners) {
            this.stockTradeListeners.add(listener);
        }
    }

    public void removeStockTradeListener(StockTradeListener listener) {
        synchronized (this.stockTradeListeners) {
            this.stockTradeListeners.remove(listener);
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    private void handleNewStockTradeEvent(StockTrade trade) {
        // publish to DB, send to PubNub, whatever you want here
        final Set<StockTradeListener> listeners;
        synchronized (this.stockTradeListeners) {
            listeners = new HashSet<>(this.stockTradeListeners);
        }
        listeners.forEach(li -> li.stockTradePublished(trade));
    }
}
```

上面的代码将这个类标记为Guava EventBus事件的订阅者，而Guava的@Subscribe注解将方法handleNewStockTradeEvent标记为事件的接收者。
它将接收的事件类型基于方法的参数；在这种情况下，我们将接收StockTrade类型的事件。

@AllowConcurrentEvents注解允许并发调用此方法。一旦我们收到一笔交易，我们就会进行任何我们希望的处理，然后通知任何监听器。

### 3.6 测试

现在我们编写一个集成测试，以验证BeanPostProcessor是否正常工作。首先，我们需要一个Spring上下文：

```java

@Configuration
public class PostProcessorConfiguration {

    @Bean
    public GlobalEventBus eventBus() {
        return GlobalEventBus.getInstance();
    }

    @Bean
    public GuavaEventBusBeanPostProcessor eventBusBeanPostProcessor() {
        return new GuavaEventBusBeanPostProcessor();
    }

    @Bean
    public StockTradePublisher stockTradePublisher() {
        return new StockTradePublisher();
    }
}
```

现在我们可以实现我们的测试：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PostProcessorConfiguration.class})
class StockTradeIntegrationTest {

    @Autowired
    private StockTradePublisher stockTradePublisher;

    @Test
    void givenValidConfig_whenTradePublished_thenTradeReceived() {
        Date tradeDate = new Date();
        StockTrade stockTrade = new StockTrade("AMZN", 100, 2483.52d, tradeDate);
        AtomicBoolean assertionsPassed = new AtomicBoolean(false);
        StockTradeListener listener = trade -> assertionsPassed.set(this.verifyExact(stockTrade, trade));
        this.stockTradePublisher.addStockTradeListener(listener);
        try {
            GlobalEventBus.post(stockTrade);
            await().atMost(Duration.ofSeconds(2L)).untilAsserted(() -> assertThat(assertionsPassed.get()).isTrue());
        } finally {
            this.stockTradePublisher.removeStockTradeListener(listener);
        }
    }

    private boolean verifyExact(StockTrade stockTrade, StockTrade trade) {
        return Objects.equals(stockTrade.symbol(), trade.symbol())
                && Objects.equals(stockTrade.tradeDate(), trade.tradeDate())
                && stockTrade.quantity() == trade.quantity()
                && stockTrade.price() == trade.price();
    }
}
```

上面的测试代码生成StockTrade并将其发布到GlobalEventBus。我们最多等待两秒钟，等待操作完成，并通知stockTradePublisher收到交易。
此外，我们验证收到的交易在运输过程中没有被修改。

## 4. 总结

总而言之，Spring的BeanPostProcessor允许我们**自定义bean本身**，为我们提供了一种自动化bean操作的方法，否则我们必须手动执行。