## 1. 概述

Spring提供了两个可以在任何Spring应用程序中使用的标准bean作用域(“singleton”和“prototype”)，
以及三个额外的bean作用域(“request”、“session”和“Application”)供在web应用程序中使用。

标准bean作用域不能被覆盖，并且通常认为覆盖Web作用域是一种不好的做法。
但是，你的应用程序可能需要与所提供作用域中的不同功能或额外的功能。

例如，如果你正在开发多租户系统，你可能希望为每个租户提供特定bean或一组bean的单独实例。
Spring提供了一种为诸如此类的场景创建自定义作用域的机制。

在本文中，我们将演示如何在**Spring应用程序中创建、注册和使用自定义作用域**。

## 2. 创建自定义作用域类

为了创建自定义作用域，**我们必须实现Scope接口**。在这样做时，我们还必须**确保实现是线程安全的**，因为作用域可以被多个bean工厂同时使用。

### 2.1 管理作用域对象和回调

在实现自定义作用域时，首先要考虑的是如何存储和管理作用域对象和销毁回调。例如，这可以使用map或专用类来完成。

对于本文，我们使用同步map以线程安全的方式实现这一点。

让我们开始定义我们的自定义作用域类：

```java
public class TenantScope implements Scope {
    private Map<String, Object> scopedObjects = Collections.synchronizedMap(new HashMap<>());
    private Map<String, Runnable> destructionCallbacks = Collections.synchronizedMap(new HashMap<>());
}
```

### 2.2 从作用域检索对象

要从我们的作用域中按名称检索对象，我们需要实现getObject方法。
**如果名为name的对象不存在于作用域中，则该方法必须创建并返回一个新对象**。

在我们的实现中，我们检查名为name的对象是否在我们的map中。如果是，我们返回它，如果不是，我们使用ObjectFactory创建一个新对象，
将它添加到我们的map中，然后返回它：

```java
public class TenantScope implements Scope {
    private Map<String, Object> scopedObjects = Collections.synchronizedMap(new HashMap<>());

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        if (!scopedObjects.containsKey(name))
            scopedObjects.put(name, objectFactory.getObject());
        return scopedObjects.get(name);
    }
}
```

在Scope接口定义的五个方法中，**只有get方法需要完整实现所描述的行为**。
其他四个方法是可选的，如果它们不需要或不支持某个功能，则可能会抛出UnsupportedOperationException。

### 2.3 注册销毁回调

我们还必须实现registerDestructionCallback方法。此方法提供了一个回调，当名为name的对象被销毁或作用域本身被应用程序销毁时，将执行该回调：

```java
public class TenantScope implements Scope {
    private Map<String, Runnable> destructionCallbacks = Collections.synchronizedMap(new HashMap<>());

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        destructionCallbacks.put(name, callback);
    }
}
```

### 2.4 从作用域中删除对象

接下来，让我们实现remove方法，该方法将名为name的对象从作用域中移除，同时移除其注册的销毁回调，然后返回移除的对象：

```java
public class TenantScope implements Scope {
    private Map<String, Object> scopedObjects = Collections.synchronizedMap(new HashMap<>());
    private Map<String, Runnable> destructionCallbacks = Collections.synchronizedMap(new HashMap<>());

    @Override
    public Object remove(String name) {
        destructionCallbacks.remove(name);
        return scopedObjects.remove(name);
    }
}
```

请注意，**调用方负责实际执行回调并销毁删除的对象**。

### 2.5 获取会话ID

现在，让我们实现getConversationId方法。如果你的作用域支持会话ID的概念，你需要在这里返回它。否则可以返回null：

```java
public class TenantScope implements Scope {

    @Override
    public String getConversationId() {
        return "tenant";
    }
}
```

### 2.6 解析上下文对象

最后，让我们实现resolveContextualObject方法。如果你的作用域支持多个上下文对象，你可以将每个对象与一个key相关联，
并返回与提供的key参数对应的对象。否则可以返回null：

```java
public class TenantScope implements Scope {

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }
}
```

## 3. 注册自定义作用域

为了让Spring容器知道我们的作用域，需要**通过ConfigurableBeanFactory实例上的registerScope方法注册它**。我们来看看这个方法的定义：

```
void registerScope(String scopeName, Scope scope);
```

第一个参数scopeName用于通过其唯一名称来标识/指定作用域。第二个参数scope是你希望注册和使用的自定义Scope实现的实际实例。

让我们创建一个自定义BeanFactoryPostProcessor并使用ConfigurableListableBeanFactory注册我们的自定义作用域：

```java
public class TenantBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
        factory.registerScope("tenant", new TenantScope());
    }
}
```

现在，让我们编写一个加载BeanFactoryPostProcessor实现的Spring配置类：

```java

@Configuration
public class TenantScopeConfig {

    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return new TenantBeanFactoryPostProcessor();
    }
}
```

## 4. 使用自定义作用域

现在我们已经注册了我们的自定义作用域，我们可以将它应用到我们的任何bean，
就像我们使用任何其他使用单例以外的作用域(默认作用域)的bean一样，通过使用@Scope注解并指定我们的自定义作用域的名字。

让我们创建一个简单的TenantBean类，稍后我们将声明该bean的作用域为tenant：

```java
public record TenantBean(String name) {

    public void sayHello() {
        System.out.printf("Hello from %s of type %s%n", this.name, this.getClass().getName());
    }
}
```

请注意，我们没有在此类上使用类级别的@Component和@Scope注解。

现在，让我们在配置类中定义一些tenant作用域的bean：

```java

@Configuration
public class TenantBeansConfig {

    @Scope(scopeName = "tenant")
    @Bean
    public TenantBean foo() {
        return new TenantBean("foo");
    }

    @Scope(scopeName = "tenant")
    @Bean
    public TenantBean bar() {
        return new TenantBean("bar");
    }
}
```

## 5. 测试自定义作用域

让我们编写一个测试来通过加载ApplicationContext、注册我们的配置类和检索我们的tenant作用域bean来验证这些实现：

```java
class TenantScopeIntegrationTest {

    @Test
    final void whenRegisterScopeAndBeans_thenContextContainsFooAndBar() {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()) {
            ctx.register(TenantScopeConfig.class);
            ctx.register(TenantBeansConfig.class);
            ctx.refresh();

            TenantBean foo = ctx.getBean("foo", TenantBean.class);
            foo.sayHello();
            TenantBean bar = ctx.getBean("bar", TenantBean.class);
            bar.sayHello();
            Map<String, TenantBean> foos = ctx.getBeansOfType(TenantBean.class);

            assertThat(foo, not(equalTo(bar)));
            assertThat(foos.size(), equalTo(2));
            assertTrue(foos.containsValue(foo));
            assertTrue(foos.containsValue(bar));

            BeanDefinition fooDefinition = ctx.getBeanDefinition("foo");
            BeanDefinition barDefinition = ctx.getBeanDefinition("bar");

            assertThat(fooDefinition.getScope(), equalTo("tenant"));
            assertThat(barDefinition.getScope(), equalTo("tenant"));
        }
    }
}
```

控制台的输出为：

```
Hello from foo of type cn.tuyucheng.taketoday.customscope.TenantBean
Hello from bar of type cn.tuyucheng.taketoday.customscope.TenantBean
```

## 6. 总结

在这个教程中，我们演示了如何在Spring中定义、注册和使用自定义作用域。