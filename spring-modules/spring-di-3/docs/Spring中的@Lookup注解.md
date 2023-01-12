## 1. 概述

在本文中，我们通过@Lookup注解介绍Spring的方法级依赖注入支持。

## 2. 为什么需要@Lookup？

使用@Lookup标注的方法告诉Spring在我们调用它时返回该方法的返回类型的实例。

本质上，Spring会重写我们的注解方法，并使用方法的返回类型和参数作为BeanFactory#getBean的参数。

@Lookup可用于：

+ 将原型作用域的bean注入单例bean(类似于Provider)
+ 以程序方式注入依赖项

还要注意，@Lookup是XML标签lookup-method的Java等效方式。

## 3. @Lookup的使用

### 3.1 将原型作用域的bean注入单例bean

如果我们碰巧有一个原型Spring bean，那么我们几乎面临这样一个问题：我们的单例Spring bean将如何访问这些原型Spring bean？

现在，Provider肯定是一种方式，尽管@Lookup在某些方面更通用。

首先，让我们创建一个原型bean，稍后我们将把它注入到单例bean中：

```java

@Component
@Scope("prototype")
public class SchoolNotification {
    // ...
}
```

如果我们创建一个使用@Lookup的单例bean：

```java

@Component
public class StudentServices {

    // member variables, etc ...

    @Lookup
    public SchoolNotification getNotification() {
        return null;
    }

    // getters and setters ... 
}
```

使用@Lookup，我们可以通过单例bean获取SchoolNotification的实例：

```java
class StudentIntegrationTest {

    private ConfigurableApplicationContext context;

    @AfterEach
    void tearDown() {
        context.close();
    }

    @Test
    public void whenLookupMethodCalled_thenNewInstanceReturned() {
        StudentServices first = this.context.getBean(StudentServices.class);
        StudentServices second = this.context.getBean(StudentServices.class);

        assertEquals(first, second);
        assertNotEquals(first.getNotification(), second.getNotification());
    }
}
```

请注意，在StudentServices中，我们将getNotification方法保留为stub。

这是因为Spring通过调用beanFactory.getBean(StudentNotification.class)重写了该方法，因此我们可以将其保留为空。

### 3.2 以程序方式注入依赖项

不过，更强大的是，@Lookup允许我们以程序方式注入依赖项，这是Provider无法做到的。

我们向StudentNotification类中添加一些属性：

```java

@Component
@Scope("prototype")
public class SchoolNotification {

    @Autowired
    Grader grader;

    private String name;
    private Collection<Integer> marks;

    public SchoolNotification(String name) {
        // set fields ...
    }

    // getters and setters ...

    public String addMark(Integer mark) {
        this.marks.add(mark);
        return this.grader.grade(this.marks);
    }
}
```

现在，它依赖于一些Spring上下文以及我们将在程序上提供的其他上下文。

然后我们可以向StudentServices添加一个方法来获取学生数据并将其持久化：

```java

@Component("studentService")
public abstract class StudentServices {

    private final Map<String, SchoolNotification> notes = new HashMap<>();

    @Lookup
    protected abstract SchoolNotification getNotification(String name);

    public String appendMark(String name, Integer mark) {
        SchoolNotification notification = notes.computeIfAbsent(name, exists -> getNotification(name));
        return notification.addMark(mark);
    }
}
```

在运行时，Spring将以相同的方式实现该方法，并带有一些额外的技巧。

首先，请注意，它可以调用复杂的构造函数以及注入其他Spring bean，从而允许我们将SchoolNotification更像是一个Spring感知方法。

它通过调用beanFactory.getBean(SchoolNotification.class, name)来实现getSchoolNotification。

其次，我们有时可以使@Lookup-annotated方法抽象，就像上面的例子一样。

使用abstract比stub看起来更好看，但我们只能在不组件扫描或@Bean-manage周围的bean时使用它：

```java
class StudentIntegrationTest {

    @Test
    void whenAbstractGetterMethodInjects_thenNewInstanceReturned() {
        context = new ClassPathXmlApplicationContext("beans.xml");

        StudentServices services = context.getBean("studentServices", StudentServices.class);

        assertEquals("PASS", services.appendMark("Alex", 76));
        assertEquals("FAIL", services.appendMark("Bethany", 44));
        assertEquals("PASS", services.appendMark("Claire", 96));
    }
}
```

通过此设置，我们可以将Spring依赖项以及方法依赖项添加到SchoolNotification。

## 4. 局限性

