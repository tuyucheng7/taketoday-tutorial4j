## 1. 概述

**在这个教程中，我们将重点关注Spring框架中的BeanNameAware和BeanFactoryAware接口**。

我们将分别描述每个接口及其使用的优缺点。

## 2. Aware接口

BeanNameAware和BeanFactoryAware都继承自org.springframework.beans.factory.Aware接口。
它使用setter注入在应用程序上下文启动期间获取对象。

**Aware接口是回调、监听器和观察者设计模式的组合**。它表明bean有资格通过回调方法被Spring容器通知。

## 3. BeanNameAware

**BeanNameWare可以获取对象在容器中定义的bean名称**。

让我们看一个例子：

```java
public class MyBeanName implements BeanNameAware {

    @Override
    public void setBeanName(String beanName) {
        System.out.println(beanName);
    }
}
```

beanName参数表示在Spring容器中注册的bean id。在我们的实现中，我们只是输出bean名称。

接下来，让我们在Spring配置类中注册一个MyBeanName类型的bean：

```java

@Configuration
public class Config {

    @Bean(name = "myCustomBeanName")
    public MyBeanName getMyBeanName() {
        return new MyBeanName();
    }
}
```

这里，我们用@Bean(name="myCustomBeanName")显式地为MyBeanName类型的bean指定了一个名称。

现在，我们可以启动应用程序上下文并从中获取MyBeanName bean：

```java
public class AwareExample {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        MyBeanName myBeanName = context.getBean(MyBeanName.class);
    }
}
```

正如我们所料，setBeanName()方法会输出“myCustomBeanName”。

如果我们删除@Bean注解中的name = "myCustomBeanName"，
那么在这种情况下，容器会将getMyBeanName()方法名称分配给bean，所以输出将是“getMyBeanName”。

## 4. BeanFactoryAware

**BeanFactoryAware用于注入BeanFactory对象，这样我们就可以访问创建对象的BeanFactory**。

下面是MyBeanFactory类的示例：

```java
public class MyBeanFactory implements BeanFactoryAware {
    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void getMyBeanName() {
        MyBeanName myBeanName = beanFactory.getBean(MyBeanName.class);
        System.out.println(beanFactory.isSingleton("myCustomBeanName"));
    }
}
```

通过setBeanFactory()方法，我们将IoC容器中的BeanFactory赋值给beanFactory成员变量。

之后，我们可以在getMyBeanName()方法中直接使用它。

让我们初始化MyBeanFactory并调用getMyBeanName()方法：

```java
public class AwareExample {

    public static void main(String[] args) {
        MyBeanFactory myBeanFactory = context.getBean(MyBeanFactory.class);
        myBeanFactory.getMyBeanName();
    }
}
```

由于我们已经在前面的示例中实例化了MyBeanName类，Spring将在这里调用现有实例。

beanFactory.isSingleton("myCustomBeanName")验证了这一点。

## 5. 何时使用？

BeanNameAware的典型用例可能是获取bean名称以进行日志记录或注入。
对于BeanFactoryAware，它可能是使用遗留代码中的spring bean的能力。

**在大多数情况下，我们应该避免使用任何Aware接口，除非我们需要它们。实现这些接口会将代码耦合到Spring框架**。

## 6. 总结

在这篇文章中，我们介绍了BeanNameAware和BeanFactoryAware接口以及如何在实践中使用它们。