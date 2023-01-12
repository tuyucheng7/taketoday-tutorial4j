## 1. 概述

在本文中，我们将学习**如何使用Spring的@Import注解，同时阐明它与@ComponentScan的不同之处**。

## 2. 配置和Beans

在介绍@Import注解之前，我们需要了解什么是Spring Bean，并且对@Configuration注解有一个基本的认识。

假设我们已经准备了**三个bean，Bird、Cat和Dog**，每个bean都有自己的配置类。

然后，我们可以**为我们的上下文提供这些配置类**：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {BirdConfig.class, CatConfig.class, DogConfig.class})
class ConfigUnitTest {

    @Autowired
    ApplicationContext context;

    @Test
    void givenImportedBeans_whenGettingEach_shallFindIt() {
        assertThatBeanExists("dog", Dog.class);
        assertThatBeanExists("cat", Cat.class);
        assertThatBeanExists("bird", Bird.class);
    }

    private void assertThatBeanExists(String beanName, Class<?> beanClass) {
        assertTrue(context.containsBean(beanName));
        assertNotNull(context.getBean(beanClass));
    }
}
```

## 3. 使用@Import对配置进行分组

声明所有配置类并没有问题，但是**想象一下在不同的源中控制几十个配置类的麻烦**。应该有更好的方法。

@Import注解有一个解决方案，它可以对配置类进行分组：

```java

@Configuration
@Import({DogConfig.class, CatConfig.class})
class MammalConfiguration {

}
```

现在，我们只需要指定MammalConfiguration：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MammalConfiguration.class})
class MammalConfigUnitTest {

    @Autowired
    ApplicationContext context;

    @Test
    void givenImportedBeans_whenGettingEach_shallFindOnlyTheImportedBeans() {
        assertThatBeanExists("dog", Dog.class);
        assertThatBeanExists("cat", Cat.class);
        assertFalse(context.containsBean("bird"));
    }

    private void assertThatBeanExists(String beanName, Class<?> beanClass) {
        assertTrue(context.containsBean(beanName));
        assertNotNull(context.getBean(beanClass));
    }
}
```

同时，我们还可以**再做一组来包含所有的动物配置类**：

```java

@Configuration
@Import({MammalConfiguration.class, BirdConfig.class})
class AnimalConfiguration {

}
```

这不会缺少任何Bean的配置，我们只需要指定一个配置类AnimalConfiguration：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AnimalConfiguration.class})
class AnimalConfigUnitTest {

    @Autowired
    ApplicationContext context;

    @Test
    void givenImportedBeans_whenGettingEach_shallFindOnlyTheImportedBeans() {
        assertThatBeanExists("dog", Dog.class);
        assertThatBeanExists("cat", Cat.class);
        assertThatBeanExists("bird", Cat.class);
    }

    private void assertThatBeanExists(String beanName, Class<?> beanClass) {
        assertTrue(context.containsBean(beanName));
        assertNotNull(context.getBean(beanClass));
    }
}
```

## 4. @Import与@ComponentScan

在继续讲解@Import示例之前，让我们将其与@ComponentScan进行比较。

### 4.1 相似之处

两个注解都可以**接收任何@Component或@Configuration类**。

让我们使用@Import添加一个新的@Component：

```java

@Configuration
@Import(Bug.class)
class BugConfig {

}

@Component(value = "bug")
class Bug {

}
```

现在，Bug bean就像任何其他bean一样可用。

### 4.2 概念差异

简而言之，**我们可以使用两个注解实现相同的效果**。那么，它们之间有什么区别吗？

为了回答这个问题，让我们记住，Spring通常提倡约定优于配置的方法。

与我们的注解类比，**@ComponentScan更像约定，而@Import看起来像配置**。

### 4.3 实际应用程序中会发生什么

通常，我们**在根包中使用@ComponentScan**启动我们的应用程序，以便它可以为我们找到所有组件。
如果我们使用的是Spring Boot，那么@SpringBootApplication已经包含了@ComponentScan，我们就可以使用了。这就是约定的好处。

现在，让我们想象一下我们的应用程序正在大量增长。
现在我们需要处理来自所有不同地方的bean，比如组件、不同的包结构以及我们自己和第三方构建的模块。

在这种情况下，将所有内容添加到上下文中可能会引发关于使用哪个bean的冲突。除此之外，我们的启动时间可能会很缓慢。

另一方面，**我们不想为每个新组件编写@Import，因为这样做会适得其反**。

以我们的动物为例。我们确实可以从上下文声明中隐藏导入，但我们仍然需要记住每个配置类的@Import。

### 4.4 一起使用

我们可以两全其美。让我们想象一下，我们有一个package仅包含我们的动物类。它也可以是一个组件或模块。

然后我们可以为我们的**动物类包创建一个@ComponentScan**：

```java
package cn.tuyucheng.taketoday.importannotation.animal;

// imports...

@Configuration
@ComponentScan
public class AnimalScanConfiguration {

}
```

还有一个@Import来控制我们将添加到上下文中的内容：

```java
package cn.tuyucheng.taketoday.importannotation.zoo;

// imports...

@Configuration
@Import(AnimalScanConfiguration.class)
class ZooApplication {

}
```

最后，任何添加到animal包中的新bean都会被我们的上下文自动找到。而且我们仍然可以明确控制我们正在使用的配置。

## 5. 总结

在本文中，我们学习了如何使用@Import来组织我们的配置。

我们还了解到，**@Import与@ComponentScan非常相似**，只是 **@Import使用显式方法，而@ComponentScan使用隐式方法**。

此外，我们还研究了在实际应用程序中控制我们的配置可能存在的困难，以及如何通过结合两个注解来处理这些问题。