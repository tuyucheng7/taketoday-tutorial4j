## 1. 概述

[Arquillian](http://arquillian.org/)是Jakarta EE的容器不可知集成测试框架。使用Arquillian可以最大限度地减少管理容器、部署、框架初始化等的负担。

我们可以专注于编写实际测试，而不是引导测试环境。

## 2. 核心概念

### 2.1 部署存档

在容器内运行时，有一种简单的方法可以测试我们的应用程序。

首先，ShrinkWrap类提供了一个API来创建可部署的*.jar、*.war和*.ear文件。

然后，Arquillian允许我们使用@Deployment注解配置测试部署-在返回ShrinkWrap对象的方法上。

### 2.2 容器

Arquillian区分了三种不同类型的容器：

-   远程：使用JMX等远程协议进行测试
-   托管：远程容器，但它们的生命周期由Arquillian管理
-   嵌入式：使用本地协议执行测试的本地容器

此外，我们可以根据容器的功能对容器进行分类：

-   部署在Glassfish或JBoss等应用程序服务器上的Jakarta EE应用程序
-   部署在Tomcat或Jetty上的Servlet容器
-   独立容器
-   OSGI容器

它检查运行时类路径并自动选择可用的容器。

### 2.3 测试扩充

Arquillian通过提供依赖注入来丰富测试，这样我们就可以轻松编写测试。

我们可以使用@Inject注入依赖项，使用@Resource注入资源，使用@EJB等注入EJB会话bean。

### 2.4 多个测试运行程序

我们可以使用注解创建多个部署：

```java
@Deployment(name="myname" order = 1)
```

其中name是部署文件的名称，order参数是部署的执行顺序，因此我们现在可以使用注解同时对多个部署进行测试：

```java
@Test @OperateOnDeployment("myname")
```

使用@Deployment注解中定义的顺序在myname部署容器上执行之前的测试。

### 2.5 Arquillian扩展

Arquillian提供了多种扩展，以防我们的测试需求未被核心运行时覆盖。我们有持久性、事务、客户端/服务器、REST扩展等。

我们可以通过向Maven或Gradle配置文件添加适当的依赖项来启用这些扩展。

常用的扩展有Drone、Graphene和Selenium。

## 3. Maven依赖和设置

让我们将以下依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.jboss.arquillian</groupId>
    <artifactId>arquillian-bom</artifactId>
    <version>1.1.13.Final</version>
    <scope>import</scope>
    <type>pom</type>
</dependency>
<dependency>
    <groupId>org.glassfish.main.extras</groupId>
    <artifactId>glassfish-embedded-all</artifactId>
    <version>4.1.2</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.jboss.arquillian.container</groupId>
    <artifactId>arquillian-glassfish-embedded-3.1</artifactId>
    <version>1.0.0.Final</version>
    <scope>test</scope>
</dependency>
```

可以在此处找到最新版本的依赖项：[arquillian-bom](https://central.sonatype.com/artifact/org.jboss.arquillian/arquillian-bom/1.7.0.Alpha14)、[glassfish-embedded-all](https://central.sonatype.com/artifact/org.glassfish.main.extras/glassfish-embedded-all/7.0.3)、[arquillian-glassfish-embedded-3.1](https://central.sonatype.com/artifact/org.jboss.arquillian.container/arquillian-glassfish-embedded-3.1/1.0.2)。

## 4. 简单测试

### 4.1 创建组件

让我们从一个简单的组件开始。我们在这里不包含任何高级逻辑，以便能够专注于测试：

```java
public class Component {
    public void sendMessage(PrintStream to, String msg) {
        to.println(message(msg));
    }

    public String message(String msg) {
        return "Message, " + msg;
    }
}
```

使用Arquillian，我们希望测试这个类在作为CDI bean调用时的行为是否正确。

### 4.2 编写我们的第一个Arquillian测试

首先，我们需要指定我们的测试类应该使用框架特定的运行器运行：

```java
@RunWith(Arquillian.class)
```

如果我们要在容器内运行测试，我们需要使用@Deployment注解。

Arquillian不使用整个类路径来隔离测试存档。相反，它使用ShrinkWrap类，这是一个用于创建存档的Java API。当我们创建要测试的存档时，我们指定要在类路径中包含哪些文件以使用测试。在部署期间，ShrinkWrap仅隔离测试所需的类。

使用addClass()方法，我们可以指定所有必要的类，还可以添加一个空的清单资源。

JavaArchive.class创建一个名为test.war的模型Web存档，该文件被部署到容器中，然后由Arquillian使用来执行测试：

```java
@Deployment
public static JavaArchive createDeployment() {
    return ShrinkWrap.create(JavaArchive.class)
        .addClass(Component.class)
        .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
}
```

然后我们在测试中注入我们的组件：

```java
@Inject
private Component component;
```

最后，我们执行测试：

```java
assertEquals("Message, MESSAGE",component.message(("MESSAGE")));
 
component.sendMessage(System.out, "MESSAGE");
```

## 5. 测试EJB

### 5.1 EJB

使用Arquillian，我们可以测试Enterprise Java Bean的依赖注入，为此我们创建一个类，该类具有将任何单词转换为小写的方法：

```java
public class ConvertToLowerCase {
    public String convert(String word) {
        return word.toLowerCase();
    }
}
```

使用这个类，我们创建一个无状态类来调用之前创建的方法：

```java
@Stateless
public class CapsConvertor {
    public ConvertToLowerCase getLowerCase(){
        return new ConvertToLowerCase();
    }
}
```

CapsConvertor类被注入到服务bean中：

```java
@Stateless
public class CapsService {

    @Inject
    private CapsConvertor capsConvertor;

    public String getConvertedCaps(final String word){
        return capsConvertor.getLowerCase().convert(word);
    }
}
```

### 5.2 测试

现在我们可以使用Arquillian来测试我们的EJB，注入CapsService：

```java
@Inject
private CapsService capsService;
    
@Test
public void givenWord_WhenUppercase_ThenLowercase(){
    assertTrue("capitalize".equals(capsService.getConvertedCaps("CAPITALIZE")));
    assertEquals("capitalize", capsService.getConvertedCaps("CAPITALIZE"));
}
```

使用ShrinkWrap，我们确保所有类都正确注入：

```java
@Deployment
public static JavaArchive createDeployment() {
    return ShrinkWrap.create(JavaArchive.class)
        .addClasses(CapsService.class, CapsConvertor.class, ConvertToLowerCase.class)
        .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
}
```

## 6. 测试JPA

### 6.1 持久化

我们也可以使用Arquillian来测试持久化。首先，创建我们的实体：

```java
@Entity
public class Car {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String name;

    // getters and setters
}
```

我们有一张包含汽车名称的表。

然后我们将创建我们的EJB来对我们的数据执行基本操作：

```java
@Stateless
public class CarEJB {
    @PersistenceContext(unitName = "defaultPersistenceUnit")
    private EntityManager em;

    public Car saveCar(final Car car) {
        em.persist(car);
        return car;
    }

    @SuppressWarnings("unchecked")
    public List<Car> findAllCars() {
        final Query query = em.createQuery("SELECT b FROM Car b ORDER BY b.name ASC");
        List<Car> entries = query.getResultList();
        if (entries == null) {
            entries = new ArrayList<Car>();
        }
        return entries;
    }

    public void deleteCar(Car car) {
        car = em.merge(car);
        em.remove(car);
    }
}
```

使用saveCar，我们可以将汽车名称保存到数据库中，我们可以使用findAllCars获取所有存储的汽车，也可以使用deleteCar从数据库中删除汽车。

### 6.2 使用Arquillian测试持久层

现在我们可以使用Arquillian执行一些基本测试。

首先，我们将类添加到ShrinkWrap中：

```java
.addClasses(Car.class, CarEJB.class)
.addAsResource("META-INF/persistence.xml")
```

然后创建我们的测试：

```java
@Test
public void testCars() {
    assertTrue(carEJB.findAllCars().isEmpty());
    Car c1 = new Car();
    c1.setName("Impala");
    Car c2 = new Car();
    c2.setName("Lincoln");
    carEJB.saveCar(c1);
    carEJB.saveCar(c2);
 
    assertEquals(2, carEJB.findAllCars().size());
 
    carEJB.deleteCar(c1);
 
    assertEquals(1, carEJB.findAllCars().size());
}
```

在这个测试中，我们首先创建四个汽车实例，并检查数据库中的行数是否与我们创建的相同。

## 7. 总结

在本教程中，我们：

-   介绍了Arquillian核心概念
-   将组件注入到Arquillian测试中
-   测试了一个EJB
-   测试持久层
-   使用Maven执行Arquillian测试