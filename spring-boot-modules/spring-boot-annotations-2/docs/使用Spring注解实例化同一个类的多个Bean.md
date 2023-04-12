## 1. 概述

[Spring IoC容器](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)创建和管理作为我们应用程序核心的Spring bean。创建bean实例与从普通Java类创建对象相同。但是，生成同一类的多个bean可能具有挑战性。

在本教程中，我们将学习如何在Spring框架中使用注解来创建同一类的多个bean。

## 2. 使用Java配置

**这是使用注解创建同一类的多个bean的最简单和最简单的方法**。在这种方法中，我们将使用基于Java的配置类来配置同一类的多个bean。

让我们考虑一个简单的例子。我们有一个Person类，它有两个类成员firstName和lastName：

```java
public class Person {
    private String firstName;
    private String lastName;

    public Person(String firstName, String secondName) {
        super();
        this.firstName = firstName;
        this.lastName = secondName;
    }

    @Override
    public String toString() {
        return "Person [firstName=" + firstName + ", secondName=" + lastName + "]";
    }
}
```

接下来，我们将构建一个名为PersonConfig的配置类，并在其中定义Person类的多个bean：

```java
@Configuration
public class PersonConfig {
    @Bean
    public Person personOne() {
        return new Person("Harold", "Finch");
    }

    @Bean
    public Person personTwo() {
        return new Person("John", "Reese");
    }
}
```

**在这里，[@Bean](https://www.baeldung.com/spring-bean)实例化了两个ID与方法名相同的Bean，并将它们注册到BeanFactory(Spring容器)接口中**。接下来，我们可以初始化Spring容器并从Spring容器请求任何bean。

这种策略也使得实现[依赖注入](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)变得简单。我们可以使用[自动装配](https://www.baeldung.com/spring-autowire)直接将一个bean(比如personOne)注入到另一个相同类型的bean(比如personTwo)中。

**这种方法的局限性在于我们需要在典型的基于Java的配置风格中使用new关键字手动实例化bean**。

**因此，如果同一个类的bean数量增加，我们需要先注册，在配置类中创建bean**。这使它成为一种更特定于Java的方法，而不是特定于Spring的方法。

## 3. 使用@Component注解

在这种方法中，我们将使用[@Component](https://www.baeldung.com/spring-component-annotation)注解来创建多个从Person类继承其属性的bean。

首先，我们将创建多个子类，即PersonOne和PersonTwo，它们扩展了Person超类：

```java
@Component
public class PersonOne extends Person {

    public PersonOne() {
        super("Harold", "Finch");
    }
}
```

```java
@Component
public class PersonTwo extends Person {

    public PersonTwo() {
        super("John", "Reese");
    }
}
```

接下来，在PersonConfig文件中，我们将使用[@ComponentScan](https://www.baeldung.com/spring-component-scanning)注解在整个包中启用组件扫描。**这使Spring容器能够自动创建任何用@Component标注的类的bean**：

```java
@Configuration
@ComponentScan("cn.tuyucheng.taketoday.multibeaninstantiation.solution2")
public class PersonConfig {
}
```

现在，我们可以只使用Spring容器中的PersonOne或PersonTwo bean。在其他任何地方，我们都可以使用Person类bean。

**这种方法的问题在于它不会创建同一类的多个实例。相反，它创建从超类继承属性的类的bean**。因此，我们只能在继承类没有定义任何其他属性的情况下使用此解决方案。**此外，继承的使用增加了代码的整体复杂性**。

## 4. 使用BeanFactoryPostProcessor

**第三种也是最后一种方法利用[BeanFactoryPostProcessor](https://www.baeldung.com/spring-beanpostprocessor)接口的自定义实现来创建同一类的多个bean实例**。这可以通过以下步骤实现：

-   创建自定义bean类并使用FactoryBean接口对其进行配置
-   使用BeanFactoryPostProcessor接口实例化同一类型的多个bean

### 4.1 自定义Bean实现

为了更好地理解这种方法，我们将进一步扩展相同的示例。假设有一个Human类依赖于Person类的多个实例：

```java
public class Human implements InitializingBean {

    private Person personOne;

    private Person personTwo;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(personOne, "Harold is alive!");
        Assert.notNull(personTwo, "John is alive!");
    }

    /* Setter injection */
    @Autowired
    public void setPersonOne(Person personOne) {
        this.personOne = personOne;
        this.personOne.setFirstName("Harold");
        this.personOne.setSecondName("Finch");
    }

    @Autowired
    public void setPersonTwo(Person personTwo) {
        this.personTwo = personTwo;
        this.personTwo.setFirstName("John");
        this.personTwo.setSecondName("Reese");
    }
}
```

**[InitializingBean](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/InitializingBean.html)接口调用afterPropertiesSet()方法来检查BeanFactory是否已设置所有bean属性并满足其他依赖项**。此外，我们使用setter注入和初始化两个Person类bean，personOne和personTwo。

接下来，我们将创建一个实现[FactoryBean](https://www.baeldung.com/spring-factorybean)接口的Person类。**FactoryBean充当在IoC容器中创建其他bean的工厂**。

此接口旨在创建实现它的bean的更多实例。在我们的例子中，它生成类型Person类的实例并自动配置它：

```java
@Qualifier(value = "personOne, personTwo")
public class Person implements FactoryBean<Object> {
    private String firstName;
    private String secondName;

    public Person() {
        // initialization code (optional)
    }

    @Override
    public Class<Person> getObjectType() {
        return Person.class;
    }

    @Override
    public Object getObject() throws Exception {
        return new Person();
    }

    public boolean isSingleton() {
        return true;
    }

    // code for getters & setters
}
```

**这里要注意的第二件重要的事情是[@Qualifier](https://www.baeldung.com/spring-qualifier-annotation)注解的使用，它在类级别包含多个Person类型的bean名称或bean id**。在这种情况下，在类级别使用@Qualifier是有原因的，我们接下来将看到。

### 4.2 自定义BeanFactory实现

现在，我们将使用[BeanFactoryPostProcessor](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/config/BeanFactoryPostProcessor.html)接口的自定义实现。**任何实现BeanFactoryPostProcessor的类都会在创建任何Spring bean之前执行**。这允许我们配置和操作bean生命周期。

**BeanFactoryPostProcessor扫描所有用@Qualifier标注的类。此外，它从该注解中提取名称(bean id)并手动创建具有指定名称的该类类型的实例**：

```java
public class PersonFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Map<String, Object> map = beanFactory.getBeansWithAnnotation(Qualifier.class);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            createInstances(beanFactory, entry.getKey(), entry.getValue());
        }
    }

    private void createInstances(ConfigurableListableBeanFactory beanFactory, String beanName, Object bean) {
        Qualifier qualifier = bean.getClass().getAnnotation(Qualifier.class);
        for (String name : extractNames(qualifier)) {
            Object newBean = beanFactory.getBean(beanName);
            beanFactory.registerSingleton(name.trim(), newBean);
        }
    }

    private String[] extractNames(Qualifier qualifier) {
        return qualifier.value().split(",");
    }
}
```

**在这里，自定义BeanFactoryPostProcessor实现在Spring容器初始化后被调用**。

接下来，为了简单起见，这里我们将使用Java配置类来初始化自定义以及BeanFactory实现：

```java
@Configuration
public class PersonConfig {
    @Bean
    public PersonFactoryPostProcessor PersonFactoryPostProcessor() {
        return new PersonFactoryPostProcessor();
    }

    @Bean
    public Person person() {
        return new Person();
    }

    @Bean
    public Human human() {
        return new Human();
    }
}
```

**这种方法的局限性在于它的复杂性。此外，不鼓励使用它，因为它不是在典型的Spring应用程序中配置bean的自然方式**。

尽管存在这些限制，但这种方法更特定于Spring，并且用于使用注解实例化多个相似类型的bean。

## 5. 总结

在本文中，我们了解了使用三种不同方法使用Spring注解实例化同一类的多个bean。

前两种方法是实例化多个Spring bean的简单且特定于Java的方法。而第三种方法有点棘手和复杂。但是，它用于使用注解创建bean的目的。