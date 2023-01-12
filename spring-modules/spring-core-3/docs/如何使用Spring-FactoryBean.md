## 1. 概述

Spring bean容器中有两种类型的bean：普通bean和工厂bean。Spring直接使用前者，而后者可以自己生成对象，这些对象由框架管理。

而且，简单来说，我们可以通过实现org.springframework.beans.factory.FactoryBean接口来构建一个工厂bean。

## 2. 工厂Bean的基础知识

### 2.1 实现FactoryBean

让我们先看看FactoryBean接口：

```java
public interface FactoryBean<T> {
    String OBJECT_TYPE_ATTRIBUTE = "factoryBeanObjectType";

    T getObject() throws Exception;

    Class<?> getObjectType();

    default boolean isSingleton() {
        return true;
    }
}
```

+ getObject() – 返回工厂生成的对象，这是Spring容器将使用的对象。
+ getObjectType() - 返回此FactoryBean生成的对象的类型。
+ isSingleton() - 表示此FactoryBean生成的对象是否为单例。

现在，作为演示，我们将实现一个ToolFactory来生成Tool类型的对象：

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tool {
    private int id;
}
```

ToolFactory：

```java
@Data
public class ToolFactory implements FactoryBean<Tool> {
    private int factoryId;
    private int toolId;

    @Override
    public Tool getObject() throws Exception {
        return new Tool(toolId);
    }

    @Override
    public Class<?> getObjectType() {
        return Tool.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
```

正如我们所见，ToolFactory是一个FactoryBean，它可以生产Tool对象。

### 2.2 将FactoryBean与基于XML的配置结合使用

现在让我们看看如何使用我们的ToolFactory。

我们将开始构建一个基于XML的配置文件factorybean-spring-ctx.xml：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="tool" class="cn.tuyucheng.taketoday.factorybean.ToolFactory">
        <property name="factoryId" value="9090"/>
        <property name="toolId" value="1"/>
    </bean>
</beans>
```

接下来，我们可以测试Tool对象是否被正确注入：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:factorybean-spring-ctx.xml"})
class FactoryBeanXmlConfigIntegrationTest {
    @Autowired
    private Tool tool;

    @Test
    void testConstructWorkerByXml() {
        assertThat(tool.getId(), equalTo(1));
        assertThat(toolFactory.getFactoryId(), equalTo(9090));
    }
}
```

测试结果表明，我们成功地将ToolFactory生成的Tool对象注入到factorybean-spring-ctx.xml中配置的属性中。

测试结果还表明，Spring容器使用FactoryBean产生的对象而不是自身进行依赖注入。

虽然Spring容器使用FactoryBean的getObject()方法的返回值作为bean，但你也可以使用FactoryBean本身。

**要访问FactoryBean，你只需在bean名称前添加一个“&”**。

让我们尝试获取工厂bean及其factoryId属性：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:factorybean-spring-ctx.xml"})
class FactoryBeanXmlConfigIntegrationTest {
    @Resource(name = "&tool")
    private ToolFactory toolFactory;

    @Test
    void testConstructWorkerByXml() {
        assertThat(toolFactory.getFactoryId(), equalTo(9090));
    }
}
```

### 2.3 将FactoryBean与基于Java的配置结合使用

在基于Java的配置中使用FactoryBean与基于XML的配置略有不同，你必须显式调用FactoryBean的getObject()方法。

让我们将上一小节中的示例转换为基于Java的配置示例：

```java

@Configuration
public class FactoryBeanAppConfig {

    @Bean(name = "tool")
    public ToolFactory toolFactory() {
        ToolFactory factory = new ToolFactory();
        factory.setFactoryId(7070);
        factory.setToolId(2);
        return factory;
    }

    @Bean
    public Tool tool() {
        return toolFactory().getObject();
    }
}
```

然后，我们可以测试Tool对象是否被正确注入：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = FactoryBeanAppConfig.class)
class FactoryBeanJavaConfigIntegrationTest {

    @Autowired
    private Tool tool;

    @Resource(name = "&tool")
    private ToolFactory toolFactory;

    @Test
    void testConstructWorkerByJava() {
        assertThat(tool.getId(), equalTo(2));
        assertThat(toolFactory.getFactoryId(), equalTo(7070));
    }
}
```

测试结果显示了与之前基于XML的配置测试类似的效果。

## 3. 初始化的方式

有时，你需要在设置FactoryBean之后但在调用getObject()方法之前执行一些操作，例如属性检查。

你可以通过实现InitializingBean接口或使用@PostConstruct注解来实现这一点。

有关使用这两种解决方案的更多详细信息已在另一篇文章中介绍：[Guide To Running Logic on Startup in Spring]()。

## 4. AbstractFactoryBean

Spring提供AbstractFactoryBean作为FactoryBean实现的简单模板父类。
有了这个父类，我们现在可以更方便地实现一个创建单例或原型对象的工厂bean。

让我们实现一个SingleToolFactory和一个NonSingleToolFactory来展示如何将AbstractFactoryBean用于单例和原型类型：

```java
// no need to set singleton property because default value is true
@Data
public class SingleToolFactory extends AbstractFactoryBean<Tool> {
    private int factoryId;
    private int toolId;

    @Override
    public Class<?> getObjectType() {
        return Tool.class;
    }

    @Override
    protected Tool createInstance() {
        return new Tool(toolId);
    }
}
```

非单例实现：

```java
@Data
public class NonSingleToolFactory extends AbstractFactoryBean<Tool> {
    private int factoryId;
    private int toolId;

    public NonSingleToolFactory() {
        setSingleton(false);
    }

    @Override
    public Class<?> getObjectType() {
        return Tool.class;
    }

    @Override
    protected Tool createInstance() {
        return new Tool(toolId);
    }
}
```

此外，这些工厂bean的XML配置：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="singleTool" class="cn.tuyucheng.taketoday.factorybean.SingleToolFactory">
        <property name="factoryId" value="3001"/>
        <property name="toolId" value="1"/>
    </bean>

    <bean id="nonSingleTool" class="cn.tuyucheng.taketoday.factorybean.NonSingleToolFactory">
        <property name="factoryId" value="3002"/>
        <property name="toolId" value="2"/>
    </bean>
</beans>
```

现在我们可以测试Tool对象的属性是否按预期注入：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:factorybean-abstract-spring-ctx.xml"})
class AbstractFactoryBeanIntegrationTest {

    @Resource(name = "singleTool")
    private Tool tool1;
    @Resource(name = "singleTool")
    private Tool tool2;
    @Resource(name = "nonSingleTool")
    private Tool tool3;
    @Resource(name = "nonSingleTool")
    private Tool tool4;

    @Test
    void testSingleToolFactory() {
        assertThat(tool1.getId(), equalTo(1));
        assertSame(tool1, tool2);
    }

    @Test
    void testNonSingleToolFactory() {
        assertThat(tool3.getId(), equalTo(2));
        assertThat(tool4.getId(), equalTo(2));
        assertNotSame(tool3, tool4);
    }
}
```

从测试中我们可以看出，SingleToolFactory生产单例对象，而NonSingleToolFactory生产原型对象。

请注意，SingleToolFactory中无需设置单例属性，因为在AbstractFactory中，单例属性的默认值为true。

## 5. 总结

使用FactoryBean可以很好地封装复杂的构造逻辑或在Spring中更轻松地配置高度可配置的对象。

所以在本文中，我们介绍了如何实现我们的FactoryBean的基础知识，如何在基于XML的配置和基于Java的配置中使用它，
以及FactoryBean的一些其他方面，例如FactoryBean和AbstractFactoryBean的初始化。