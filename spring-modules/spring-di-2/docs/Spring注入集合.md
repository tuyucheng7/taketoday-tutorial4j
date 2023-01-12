## 1. 概述

在本教程中，我们介绍如何**使用Spring框架注入Java集合**，通过List、Map、Set集合接口来做演示。

## 2. 在List上使用@Autowired

```java
public class CollectionsBean {

    @Autowired
    private List<String> nameList;

    public void printNameList() {
        System.out.println(nameList);
    }
}
```

在这里，我们声明了nameList属性来保存一个字符串集合。

**在本例中，我们对nameList使用字段注入，因此我们加上了@Autowired注解**。

之后，我们在配置类中注册CollectionsBean：

```java

@Configuration
public class CollectionConfig {

    @Bean
    public CollectionsBean getCollectionsBean() {
        return new CollectionsBean();
    }

    @Bean
    public List<String> nameList() {
        return Arrays.asList("John", "Adam", "Harry");
    }
}
```

除了注册Collections Bean之外，我们还通过显式初始化并将其作为单独的@Bean配置返回一个List。

现在，我们可以测试结果：

```java
public class CollectionInjectionDemo {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(CollectionConfig.class);
        CollectionsBean collectionsBean = context.getBean(CollectionsBean.class);
        collectionsBean.printNameList();
    }
}
```

输出的结果为：

```text
[John, Adam, Harry]
```

## 3. 使用构造注入Set

要使用Set集合演示相同的示例，我们修改CollectionsBean类：

```java
public class CollectionsBean {

    private Set<String> nameSet;

    public CollectionsBean(Set<String> strings) {
        this.nameSet = strings;
    }

    public void printNameSet() {
        System.out.println(nameSet);
    }
}
```

**这次我们要使用构造注入来初始化nameSet属性**，这还需要更改配置类：

```java

@Configuration
public class CollectionConfig {

    @Bean
    public CollectionsBean getCollectionsBean() {
        return new CollectionsBean(new HashSet<>(Arrays.asList("John", "Adam", "Harry")));
    }
}
```

## 4. 使用Setter注入Map

按照同样的逻辑，我们添加nameMap字段来演示Map注入：

```java
public class CollectionsBean {

    private Map<Integer, String> nameMap;

    @Autowired
    public void setNameMap(Map<Integer, String> nameMap) {
        this.nameMap = nameMap;
    }

    public void printNameMap() {
        System.out.println(nameMap);
    }
}
```

这次**我们通过setNameMap方法实现setter注入**，还需要在配置类中添加Map初始化代码：

```java

@Configuration
public class CollectionConfig {

    @Bean
    public Map<Integer, String> nameMap() {
        Map<Integer, String> nameMap = new HashMap<>();
        nameMap.put(1, "John");
        nameMap.put(2, "Adam");
        nameMap.put(3, "Harry");
        return nameMap;
    }
}
```

调用printNameMap()方法后的输出结果为：

```text
{1=John, 2=Adam, 3=Harry}
```

## 5. 注入Bean引用

**让我们看一个将bean引用作为集合元素注入的示例**。

首先，我们创建一个bean类：

```java
public class TuyuchengBean {

    private String name;

    public TuyuchengBean(String name) {
        this.name = name;
    }
}
```

并将TuyuchengBean类型的集合作为属性添加到CollectionsBean类中：

```java
public class CollectionsBean {

    @Autowired(required = false)
    private List<TuyuchengBean> beanList = new ArrayList<>();

    public void printBeanList() {
        System.out.println(beanList);
    }
}
```

接下来，我们为每个TuyuchengBean元素添加Java配置工厂方法：

```java

@Configuration
public class CollectionConfig {

    @Bean
    public TuyuchengBean getElement() {
        return new TuyuchengBean("John");
    }

    @Bean
    public TuyuchengBean getAnotherElement() {
        return new TuyuchengBean("Adam");
    }

    @Bean
    public TuyuchengBean getOneMoreElement() {
        return new TuyuchengBean("Harry");
    }
}
```

**Spring容器将TuyuchengBean类型的各个bean注入到一个集合中**。

为了测试这一点，我们调用collectionsBean.printBeanList()方法。输出将bean名称显示为集合元素：

```text
[John, Harry, Adam]
```

现在，**让我们考虑一个容器中没有TuyuchengBean的场景**。
如果应用程序上下文中没有注册TuyuchengBean，Spring将抛出异常，因为缺少所需的依赖项。

我们可以使用@Autowired(required = false)将依赖项标记为可选。beanList不会被初始化，它的值将保持为null，而不是抛出异常。

如果我们需要一个空元素集合而不是null的集合，我们可以用一个新的ArrayList初始化beanList：

```java
public class CollectionsBean {

    @Autowired(required = false)
    private List<TuyuchengBean> beanList = new ArrayList<>();
}
```

### 5.1 使用@Order对Bean进行排序

**我们可以在注入集合时指定bean的顺序**。

为此，我们使用@Order注解并指定顺序值：

```java

@Configuration
public class CollectionConfig {

    @Bean
    @Order(2)
    public TuyuchengBean getElement() {
        return new TuyuchengBean("John");
    }

    @Bean
    @Order(3)
    public TuyuchengBean getAnotherElement() {
        return new TuyuchengBean("Adam");
    }

    @Bean
    @Order(1)
    public TuyuchengBean getOneMoreElement() {
        return new TuyuchengBean("Harry");
    }
}
```

**Spring容器会首先注入名为“Harry”的bean**，因为它的order值最低。然后注入“John”，最后注入“Adam” bean：

```text
[Harry, John, Adam]
```

### 5.2 使用@Qualifier选择Bean

**我们可以使用@Qualifier注解来选择要注入到与@Qualifier名称匹配的特定集合中的bean**。

以下是这种方式的用法：

```java
public class CollectionsBean {

    @Autowired(required = false)
    @Qualifier("CollectionsBean")
    private List<TuyuchengBean> beanList = new ArrayList<>();
}
```

然后，我们用相同的@Qualifier注解标记想要注入到集合中的bean：

```java

@Configuration
public class CollectionConfig {

    @Bean
    @Qualifier("CollectionsBean")
    public TuyuchengBean getElement() {
        return new TuyuchengBean("John");
    }

    @Bean
    public TuyuchengBean getAnotherElement() {
        return new TuyuchengBean("Adam");
    }

    @Bean
    public TuyuchengBean getOneMoreElement() {
        return new TuyuchengBean("Harry");
    }
}
```

在这种情况下，只有名称为“John”的bean会被注入到名为“CollectionsBean”的集合中：

```java
public class CollectionInjectionDemo {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(CollectionConfig.class);
        CollectionsBean collectionsBean = context.getBean(CollectionsBean.class);
        collectionsBean.printBeanList();
    }
}
```

从输出中，我们可以看到集合只有一个元素：

```text
[John]
```

## 6. 为空集合设置为默认值

我们可以使用Collections.emptyList()静态方法将name.list属性注入为集合的默认值：

```java
public class CollectionsBean {

    @Value("${names.list:}#{T(java.util.Collections).emptyList()}")
    private List<String> nameListWithDefaultValue;

    public void printNameListWithDefaults() {
        System.out.println(nameListWithDefaultValue);
    }
}
```

如果我们属性文件不包含“names.list” key：

```java
public class CollectionInjectionDemo {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(CollectionConfig.class);
        CollectionsBean collectionsBean = context.getBean(CollectionsBean.class);
        collectionsBean.printNameListWithDefaults();
    }
}
```

我们会得到一个空集合作为输出：

```text
[ ]
```

## 7. 总结

通过本文，我们学习了如何使用Spring框架注入不同类型的Java集合。

我们还说明了带有引用类型的注入以及如何在集合中选择性的注入或排序它们。