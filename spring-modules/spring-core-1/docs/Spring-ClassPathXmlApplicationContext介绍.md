## 1. 概述

简单地说，Spring框架的核心就是一个用于管理bean的IoC容器。

Spring中有两种基本类型的容器 - BeanFactory和ApplicationContext。
前者提供基本功能，本文将介绍这些功能；后者是前者的扩展，使用最广泛。

ApplicationContext是org.springframework.context包中的一个接口。它有几个实现类，ClassPathXmlApplicationContext就是其中之一。

在本文中，我们将重点讨论ClassPathXmlApplicationContext提供的有用功能。

## 2. 基本用法

### 2.1 初始化容器并管理bean

ClassPathXmlApplicationContext可以从类路径加载XML配置并管理其中定义的bean：

假设我们有一个Student类：

```java

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private int no;
    private String name;
}
```

我们在classpathxmlapplicationcontext-example.xml中配置一个Student bean：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd   http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>

    <bean id="student" class="cn.tuyucheng.taketoday.applicationcontext.Student">
        <property name="no" value="15"/>
        <property name="name" value="Tom"/>
    </bean>
</beans>
```

现在，我们可以使用ClassPathXmlApplicationContext加载XML配置并获取Student bean：

```java
class ClasspathXmlApplicationContextIntegrationTest {

    @Test
    void testBasicUsage() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpathxmlapplicationcontext-example.xml");
        Student student = (Student) context.getBean("student");
        assertThat(student.getNo(), equalTo(15));
        assertThat(student.getName(), equalTo("Tom"));

        Student sameStudent = context.getBean("student", Student.class);// do not need to cast class
        assertThat(sameStudent.getNo(), equalTo(15));
        assertThat(sameStudent.getName(), equalTo("Tom"));
    }
}
```

### 2.2 多个XML配置

有时，我们希望使用多个XML配置来初始化一个Spring容器。在这种情况下，我们只需在构建ApplicationContext时指定多个配置文件：

```text
ApplicationContext context = new ClassPathXmlApplicationContext("ctx.xml", "ctx2.xml");
```

## 3. 其他功能

### 3.1 优雅地关闭Spring IoC容器

当我们在web应用程序中使用Spring IoC容器时，Spring基于web的ApplicationContext实现将在应用程序关闭时优雅地关闭容器，
但如果我们在非web环境中使用它，我们必须自己向JVM注册一个关机钩子，以确保Spring IoC容器正常关闭，并调用destroy()方法来释放资源。

让我们在Student类中添加一个destroy()方法：

```java
public class Student {

    public void destroy() {
        log.info("Student(no: {}) is destroyed", no);
    }
}
```

我们现在可以将此方法配置为Student bean的destroy-method：

```xml

<bean id="student" class="cn.tuyucheng.taketoday.applicationcontext.Student" destroy-method="destroy">
    <property name="no" value="15"/>
    <property name="name" value="Tom"/>
</bean>
```

接下来我们注册一个关机钩子：

```java
class ClasspathXmlApplicationContextIntegrationTest {

    @Test
    void testRegisterShutdownHook() {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("classpathxmlapplicationcontext-example.xml");
        context.registerShutdownHook();
    }
}
```

当我们运行测试时，可以看到destroy()方法被调用。

### 3.2 使用MessageSource进行国际化

ApplicationContext接口继承了MessageSource，因此提供了国际化功能。

ApplicationContext容器在其初始化时会自动搜索MessageSource bean，该bean必须命名为messageSource。

下面是一个通过MessageSource使用不同语言的示例：

首先，让我们在resources目录中添加一个dialog目录，并在dialog目录中添加两个文件：dialog_en.properties和dialog_zh_CN.properties。

dialog_en.properties:

```properties
hello=hello
you=you
thanks=thank {0}
```

dialog_zh_CN.properties:

```properties
hello=你好
you=你
thanks=谢谢{0}
```

接下来在classpathxmlapplicationcontext-internationalization.xml中配置MessageSource bean：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns="http://www.springframework.org/schema/beans"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="messageSource" class="org.springframework.context.support.ResourceBundleMessageSource">
        <property name="basenames">
            <list>
                <value>dialog/dialog</value>
            </list>
        </property>
    </bean>
</beans>
```

然后，我们用MessageSource获取不同语言的属性：

```java
class ClasspathXmlApplicationContextIntegrationTest {

    @Test
    void testInternationalization() {
        MessageSource resources = new ClassPathXmlApplicationContext("classpathxmlapplicationcontext-internationalization.xml");

        String enHello = resources.getMessage("hello", null, "Default", Locale.ENGLISH);
        String enYou = resources.getMessage("you", null, Locale.ENGLISH);
        String enThanks = resources.getMessage("thanks", new Object[]{enYou}, Locale.ENGLISH);
        assertThat(enHello, equalTo("hello"));
        assertThat(enThanks, equalTo("thank you"));

        String chHello = resources.getMessage("hello", null, "Default", Locale.SIMPLIFIED_CHINESE);
        String chYou = resources.getMessage("you", null, Locale.SIMPLIFIED_CHINESE);
        String chThanks = resources.getMessage("thanks", new Object[]{chYou}, Locale.SIMPLIFIED_CHINESE);
        assertThat(chHello, equalTo("你好"));
        assertThat(chThanks, equalTo("谢谢你"));
    }
}
```

## 4. 对ApplicationContext的引用

有时我们需要在代码中获取ApplicationContext的引用，我们可以使用ApplicationContextAware或@Autowired来做到这一点。
让我们看看如何使用ApplicationContextAware：

```java

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private String name;
}
```

我们有一个Teacher类，根据容器的bean来组合Course：

```java
public class Teacher implements ApplicationContextAware {
    private ApplicationContext context;
    private List<Course> courses = new ArrayList<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.context = applicationContext;
    }

    @PostConstruct
    public void addCourse() {
        if (context.containsBean("math")) {
            Course math = context.getBean("math", Course.class);
            courses.add(math);
        }
        if (context.containsBean("physics")) {
            Course physics = context.getBean("physics", Course.class);
            courses.add(physics);
        }
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}
```

然后让我们在classpathxmlapplicationcontext-example.xml中配置Course和Teacher bean。

```text
<bean id="math" class="cn.tuyucheng.taketoday.applicationcontext.Course">
    <property name="name" value="math"/>
</bean>

<bean name="teacher" class="cn.tuyucheng.taketoday.applicationcontext.Teacher"/>
```

最后测试courses属性的注入：

```java
class ClasspathXmlApplicationContextIntegrationTest {

    @Test
    void testApplicationContextAware() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpathxmlapplicationcontext-example.xml");
        Teacher teacher = context.getBean("teacher", Teacher.class);
        List<Course> courses = teacher.getCourses();
        assertThat(courses.size(), equalTo(1));
        assertThat(courses.get(0).getName(), equalTo("math"));
    }
}
```

除了实现ApplicationContextAware接口之外，使用@Autowired注解也可以实现同样的效果。

让我们将Teacher类改为：

```java
public class Teacher {

    @Autowired
    private ApplicationContext context;
    private List<Course> courses = new ArrayList<>();

    @PostConstruct
    public void addCourse() {
        if (context.containsBean("math")) {
            Course math = context.getBean("math", Course.class);
            courses.add(math);
        }
        if (context.containsBean("physics")) {
            Course physics = context.getBean("physics", Course.class);
            courses.add(physics);
        }
    }
    // standard constructors, getters and setters
}
```

然后运行该测试，我们可以看到结果是相同的。

## 5. 总结

ApplicationContext是一个Spring容器，与BeanFactory相比具有更多的企业特定功能，ClassPathXmlApplicationContext是其最常用的实现之一。

在本文中，我们介绍了ClassPathXmlApplicationContext的几个方面，包括它的基本用法、它的关机注册功能、它的国际化功能以及引用的获取。