## 1. 概述

在Spring应用程序中，将一个bean注入另一个bean是很常见的。但是，**有时需要将bean注入到普通对象中**。
例如，我们可能希望从实体对象中获取对Service对象的引用。

幸运的是，实现这一目标并不像看上去那么难。以下部分将介绍如何使用@Configurable注解和AspectJ weaver来实现这一点。

## 2. @Configurable注解

这个注解允许被修饰类的实例保存对Spring bean的引用。

### 2.1 定义和注册Spring bean

在介绍@Configurable注解之前，让我们配置一个Spring bean定义：

```java

@Service
public class IdService {
    private static int count;

    int generateId() {
        return ++count;
    }
}
```

这个类用@Service注解修饰；因此它可以通过组件扫描在Spring上下文中注册。

下面是一个启用该机制的简单配置类：

```java

@ComponentScan
public class AspectJConfig {

}
```

### 2.2 使用@Configurable

在最简单的形式中，**我们可以使用不带任何属性的@Configurable**：

```java

@Configurable
public class PersonObject {
    private int id;
    private String name;

    public PersonObject(String name) {
        this.name = name;
    }
    // getters and other code shown in the next subsection
}
```

在这种情况下，**@Configurable注解将PersonObject类标记为符合Spring驱动配置的条件**。

### 2.3 将Spring Bean注入非托管对象

我们可以将IdService注入到PersonObject中，就像在任何Spring bean中一样：

```java

@Configurable
public class PersonObject {
    @Autowired
    private IdService idService;

    // fields, constructor and getters - shown in the previous subsection

    void generateId() {
        this.id = idService.generateId();
    }
}
```

但是，注解只有在被处理程序识别和处理时才有用。这就是AspectJ weaver发挥作用的地方。
具体来说，**AnnotationBeanConfiguerAspect将对@Configurable的存在起作用，并进行必要的处理**。

## 3. 开启AspectJ Weaving

### 3.1 插件声明

要启用AspectJ Weaving，我们首先需要AspectJ Maven插件：

```xml

<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>aspectj-maven-plugin</artifactId>
    <version>1.14.0</version>
</plugin>
```

它需要一些额外的配置：

```xml

<configuration>
    <complianceLevel>1.8</complianceLevel>
    <Xlint>ignore</Xlint>
    <aspectLibraries>
        <aspectLibrary>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aspects</artifactId>
        </aspectLibrary>
    </aspectLibraries>
</configuration>
```

第一个必需元素是complianceLevel，1.8表示将源JDK版本和目标JDK版本都设置为1.8。
如果未明确设置，则源版本为1.3，目标版本为1.1。这些值显然已经过时，对于现代Java应用程序来说远远不够。

要将bean注入到非托管对象中，我们必须依赖spring-aspects.jar中提供的AnnotationBeanConfigurerAspect类。
**由于这是一个预编译的aspect，我们需要将包含的工件添加到插件配置中**。

请注意，这样的引用工件必须作为依赖项存在于项目中：

```xml

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aspects</artifactId>
    <version>5.3.13</version>
</dependency>
```

### 3.2 插件Execution

为了指示插件织入所有相关的类，我们需要以下executions配置：

```xml

<executions>
    <execution>
        <goals>
            <goal>compile</goal>
        </goals>
    </execution>
</executions>
```

**注意，默认情况下，插件的compile goal绑定到compile生命周期阶段**。

### 3.2 Bean配置

启用AspectJ weaving的最后一步是将@EnableSpringConfigured添加到配置类中：

```java

@ComponentScan
@EnableSpringConfigured
public class AspectJConfig {

}
```

额外的注解配置了AnnotationBeanConfigurerAspect，而AnnotationBeanConfigurerAspect向Spring IoC容器注册PersonObject实例。

## 4. 测试

现在，让我们测试IdService bean是否已成功注入PersonObject：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AspectJConfig.class)
class PersonUnitTest {

    @Test
    void givenUnmanagedObjects_whenInjectingIdService_thenIdValueIsCorrectlySet() {
        PersonObject personObject = new PersonObject("tuyucheng");
        personObject.generateId();

        assertEquals(1, personObject.getId());
        assertEquals("tuyucheng", personObject.getName());
    }
}
```

## 5. 将Bean注入JPA实体

**从Spring容器的角度来看，实体只不过是一个普通的对象**。因此，将Spring bean注入JPA实体并没有什么特别之处。

然而，由于注入JPA实体是一个典型的用例，在这里详细的说明几点。

### 5.1 实体类

```java

@Entity
@Configurable(preConstruction = true)
public class PersonEntity {
    @Id
    private int id;
    private String name;

    public PersonEntity() {
    }

    // other code - shown in the next subsection
}
```

注意@Configurable注解中的preConstruction属性：**它使我们能够在对象完全构造之前将依赖项注入到对象中**。

### 5.2 注入Service

现在我们可以将IdService注入到PersonEntity中，类似于PersonObject一样:

```java

@Entity
@Configurable(preConstruction = true)
public class PersonEntity {

    @Autowired
    @Transient
    private IdService idService;

    // fields and no-arg constructor

    public PersonEntity(String name) {
        id = idService.generateId();
        this.name = name;
    }

    // getters
}
```

@Transient注解用于告诉JPA idService是一个不需要被持久化的字段。

### 5.3 修改测试

最后，我们可以修改测试方法来验证IdService对象正确注入到PersonEntity中：

```java
class PersonUnitTest {

    @Test
    void givenUnmanagedObjects_whenInjectingIdService_thenIdValueIsCorrectlySet() {
        // existing statements

        PersonEntity personEntity = new PersonEntity("tuyucheng");
        assertEquals(2, personEntity.getId());
        assertEquals("tuyucheng", personEntity.getName());
    }
}
```

## 6. 注意事项

尽管从非Spring托管的对象访问Spring组件很方便，但这样做通常不是一个好的做法。

问题是非托管对象(包括实体)通常是域模型的一部分，这些对象应该只携带可跨不同服务重用的数据。

**将bean注入到此类对象中可能会将组件和对象联系在一起，从而使维护和增强应用程序变得更加困难**。

## 7. 总结

本教程介绍了将Spring bean注入到非托管对象的问题。