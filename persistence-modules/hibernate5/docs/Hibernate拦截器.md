## 1. 概述

在本次讨论中，我们将研究在[Hibernate 的](http://hibernate.org/)抽象关系映射实现中拦截操作的各种方法。

## 2. 定义 Hibernate 拦截器

Hibernate 拦截器是一个接口，它允许我们对 Hibernate 中的某些事件做出反应。

这些拦截器被注册为回调并提供 Hibernate 会话和应用程序之间的通信链接。通过这样的回调，应用程序可以拦截 Hibernate 的核心操作，如保存、更新、删除等。

有两种定义拦截器的方法：

1.  实现org.hibernate.Interceptor接口
2.  扩展org.hibernate.EmptyInterceptor类

### 2.1. 实现拦截器接口

实施org.hibernate.Interceptor需要实施大约 14 个伴随方法。这些方法包括onLoad、onSave、onDelete、findDirty等等。

确保任何实现 Interceptor 接口的类都是可序列化的(实现 java.io.Serializable)也很重要。

一个典型的例子是这样的：

```java
public class CustomInterceptorImpl implements Interceptor, Serializable {

    @Override
    public boolean onLoad(Object entity, Serializable id, 
      Object[] state, String[] propertyNames, Type[] types) 
      throws CallbackException {
        // ...
        return false;
    }

    // ...

    @Override
    public String onPrepareStatement(String sql) {
        // ...   
        return sql;
    }

}
```

如果没有特殊要求，强烈建议扩展EmptyInterceptor类并只覆盖所需的方法。

### 2.2. 扩展EmptyInterceptor

扩展org.hibernate.EmptyInterceptor类提供了一种定义拦截器的更简单方法。我们现在只需要覆盖与我们要拦截的操作相关的方法。

例如，我们可以将CustomInterceptor定义为：

```java
public class CustomInterceptor extends EmptyInterceptor {
}
```

而如果我们需要在数据保存操作执行之前拦截它们，我们需要重写onSave方法：

```java
@Override
public boolean onSave(Object entity, Serializable id, 
  Object[] state, String[] propertyNames, Type[] types) {
    
    if (entity instanceof User) {
        logger.info(((User) entity).toString());
    }
    return super.onSave(entity, id, state, propertyNames, types);
}
```

注意这个实现是如何简单地打印出实体的——如果它是一个用户的话。

虽然可以返回true或false的值，但最好通过调用super.onSave()来允许传播onSave事件。

另一个用例是为数据库交互提供审计跟踪。我们可以使用onFlushDirty()方法来了解实体何时更改。

对于User对象，我们可以决定在User类型的实体发生更改时更新其lastModified日期属性。

这可以通过以下方式实现：

```java
@Override
public boolean onFlushDirty(Object entity, Serializable id, 
  Object[] currentState, Object [] previousState, 
  String[] propertyNames, Type[] types) {
    
    if (entity instanceof User) {
        ((User) entity).setLastModified(new Date());
        logger.info(((User) entity).toString());
    }
    return super.onFlushDirty(entity, id, currentState, 
      previousState, propertyNames, types);
}
```

其他的事件如delete、load(对象初始化)可以通过分别实现相应的onDelete和onLoad方法来拦截。

## 3.注册拦截器

Hibernate 拦截器可以注册为Session范围或SessionFactory 范围。

### 3.1. 会话范围的拦截器

会话范围的拦截器链接到特定会话。它是在会话被定义或打开时创建的：

```java
public static Session getSessionWithInterceptor(Interceptor interceptor) 
  throws IOException {
    return getSessionFactory().withOptions()
      .interceptor(interceptor).openSession();
}
```

在上面，我们明确地向特定的休眠会话注册了一个拦截器。

### 3.2. SessionFactory范围内的拦截器

在构建 SessionFactory 之前注册SessionFactory范围的拦截器。这通常是通过SessionFactoryBuilder实例上的applyInterceptor方法完成的：

```java
ServiceRegistry serviceRegistry = configureServiceRegistry();
SessionFactory sessionFactory = getSessionFactoryBuilder(serviceRegistry)
  .applyInterceptor(new CustomInterceptor())
  .build();
```

请务必注意，SessionFactory范围的拦截器将应用于所有会话。因此，我们需要注意不要存储特定于会话的状态——因为这个拦截器将同时被不同的会话使用。

对于特定于会话的行为，建议如前所示使用不同的拦截器显式打开会话。

对于SessionFactory范围的拦截器，我们自然需要确保它是线程安全的。这可以通过在属性文件中指定会话上下文来实现：

```shell
hibernate.current_session_context_class=org.hibernate.context.internal.ThreadLocalSessionContext
```

或者将其添加到我们的 XML 配置文件中：

```xml
<property name="hibernate.current_session_context_class">
    org.hibernate.context.internal.ThreadLocalSessionContext
</property>
```

此外，为了确保可序列化，SessionFactory范围内的拦截器必须实现Serializable接口的readResolve方法。

## 4. 总结

我们已经了解了如何将 Hibernate 拦截器定义和注册为Session范围或SessionFactory范围。在任何一种情况下，我们都必须确保拦截器是可序列化的，特别是如果我们想要一个可序列化的会话。

拦截器的其他替代方案包括 Hibernate Events 和 JPA Callbacks。