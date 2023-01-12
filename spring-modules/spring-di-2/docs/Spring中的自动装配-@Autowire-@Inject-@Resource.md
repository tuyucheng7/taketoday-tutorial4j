## 1. 概述

在本文中，我们演示如何使用与依赖注入相关的注解，即@Resource、@Inject和@Autowired。
这些注解为类提供了一种声明性的方式来解析依赖关系：

```text
@Autowired 
ArbitraryClass arbObject;
```

与直接实例化它们相反(命令式)：

```text
ArbitraryClass arbObject = new ArbitraryClass();
```

这三个注解中有两个来自Java扩展包javax.annotation.Resource和javax.inject.Inject。
@Autowired注解属于org.springframework.beans.factory.annotation包。

这些注解中的每一个都可以通过字段注入或setter注入来解决依赖关系。
我们将使用一个简化但实用的示例来演示三个注解之间的区别，基于每个注解所采用的执行路径。

案例将重点介绍如何在集成测试期间使用三个注解，测试所需的依赖bean可以是任意类。

## 2. @Resource注解

@Resource注解是JSR-250注解集合的一部分，并与Jakarta EE一起打包。此注解具有以下执行方式，按优先级列出：

1. 按名称匹配
2. 按类型匹配
3. 按限定符匹配

这些执行方式适用于Setter和字段注入。

### 2.1 字段注入

我们可以通过使用@Resource注解标注实例变量来通过字段注入来解决依赖关系。

#### 2.1.1 按名称匹配

我们使用以下集成测试来演示按名称匹配字段注入：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = ApplicationContextTestResourceNameType.class
)
class FieldResourceInjectionIntegrationTest {

    @Resource(name = "namedFile")
    private File defaultFile;

    @Test
    void givenResourceAnnotation_WhenOnField_ThenDependencyValid() {
        assertNotNull(defaultFile);
        assertEquals("namedFile.txt", defaultFile.getName());
    }
}
```

在FieldResourceInjectionIntegrationTest中第8行，我们通过将bean名称作为name属性传递给@Resource注解来按名称解析依赖bean：

```text
@Resource(name="namedFile")
private File defaultFile;
```

此配置通过按名称匹配的执行方式解析依赖关系。
我们必须在ApplicationContextTestResourceNameType应用程序上下文配置类中定义一个名为namedFile的bean。

注意bean名称和对应的name属性值必须匹配：

```java

@Configuration
public class ApplicationContextTestResourceNameType {

    @Bean(name = "namedFile")
    public File namedFile() {
        return new File("namedFile.txt");
    }
}
```

如果我们没有在应用程序上下文中定义名为namedFile的bean，
会抛出org.springframework.beans.factory.NoSuchBeanDefinitionException。
我们可以通过更改ApplicationContextTestResourceNameType应用程序上下文中传递给@Bean注解的属性值，
或者更改FieldResourceInjectionTest集成测试中传递给@Resource注解的属性值来证明这一点。

#### 2.1.2 按类型匹配

为了演示按类型匹配的执行方式，我们只需删除FieldResourceInjectionIntegrationTest测试类第8行@Resource注解中的name属性配置：

```text
@Resource
private File defaultFile;
```

然后我们再次运行测试，测试仍然可以通过。
因为如果@Resource注解没有配置name属性作为bean名称，Spring会继续执行按类型匹配来尝试解决依赖关系。

#### 2.1.3 按限定符匹配

为了演示按限定符匹配的执行方式，我们需要修改集成测试场景，
以便在ApplicationContextTestResourceQualifier应用程序上下文中定义两个bean：

```java

@Configuration
public class ApplicationContextTestResourceQualifier {

    @Bean(name = "defaultFile")
    public File defaultFile() {
        return new File("defaultFile.txt");
    }

    @Bean(name = "namedFile")
    public File namedFile() {
        return new File("namedFile.txt");
    }
}
```

我们使用QualifierResourceInjectionIntegrationTest集成测试来演示通过限定符匹配的依赖关系解析。
在这种情况下，需要将特定的bean依赖注入到每个引用变量中：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = ApplicationContextTestResourceQualifier.class
)
class QualifierResourceInjectionIntegrationTest {

    @Resource
    private File dependency1;

    @Resource
    private File dependency2;

    @Test
    void givenResourceAnnotation_WhenField_ThenDependency1Valid() {
        assertNotNull(dependency1);
        assertEquals("defaultFile.txt", dependency1.getName());
    }

    @Test
    void givenResourceQualifier_WhenField_ThenDependency2Valid() {
        assertNotNull(dependency2);
        assertEquals("namedFile.txt", dependency2.getName());
    }
}
```

当我们运行集成测试时，会抛出org.springframework.beans.factory.NoUniqueBeanDefinitionException。
因为应用程序上下文会找到两个File类型的bean定义，并且不知道应该使用哪个bean解析依赖关系。

要解决这个问题，我们需要更改QualifierResourceInjectionIntegrationTest集成测试的第8行到第12行，

```text
@Resource
private File dependency1;

@Resource
private File dependency2;
```

为以下形式：

```text
@Resource
@Qualifier("defaultFile")
private File dependency1;

@Resource
@Qualifier("namedFile")
private File dependency2;
```

当我们再次运行集成测试时，它应该通过。
测试表明，即使我们在应用程序上下文中定义了多个同类型bean，我们也可以使用@Qualifier注解通过允许我们将特定的依赖项注入到一个类中来消除任何混淆。

### 2.2 Setter注入

在字段上注入依赖项时所采用的执行方式也适用于基于Setter的注入。

#### 2.2.1 按名称匹配

唯一的区别是MethodResourceInjectionIntegrationTest集成测试有一个Setter方法：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = ApplicationContextTestResourceNameType.class
)
class MethodResourceInjectionIntegrationTest {

    private File defaultFile;

    @Resource(name = "namedFile")
    protected void setDefaultFile(File defaultFile) {
        this.defaultFile = defaultFile;
    }

    @Test
    void givenResourceAnnotation_WhenSetter_ThenDependencyValid() {
        assertNotNull(defaultFile);
        assertEquals("namedFile.txt", defaultFile.getName());
    }
}
```

我们通过使用@Resource注解标注引用变量的相应setter方法，通过setter注入来解决依赖关系。
然后我们将bean依赖的名称作为name属性值传递给@Resource注解：

```text
private File defaultFile;

@Resource(name = "namedFile")
protected void setDefaultFile(File defaultFile) {
    this.defaultFile = defaultFile;
}
```

在本例中，我们将重用之前配置的namedFile bean，bean名称和相应的属性值必须匹配。

当我们运行集成测试时，它将通过。

为了验证按名称匹配的执行方式是否解决了依赖关系，我们需要将传递给@Resource注解的属性值更改为我们选择的值并再次运行测试。
这一次，测试将失败并出现NoSuchBeanDefinitionException。

#### 2.2.2 按类型匹配

为了演示基于setter按类型匹配的方式，我们使用MethodByTypeResourceIntegrationTest集成测试：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = ApplicationContextTestResourceNameType.class
)
class MethodByTypeResourceIntegrationTest {

    private File defaultFile;

    @Resource
    protected void setDefaultFile(File defaultFile) {
        this.defaultFile = defaultFile;
    }

    @Test
    void givenResourceAnnotation_WhenSetter_ThenValidDependency() {
        assertNotNull(defaultFile);
        assertEquals("namedFile.txt", defaultFile.getName());
    }
}
```

当我们运行这个测试时，它会通过。

为了验证按类型匹配的执行方式是否解析了File依赖，我们需要将defaultFile变量的类型更改为另一个类型，如String。
然后我们可以再次执行MethodByTypeResourceIntegrationTest集成测试，这次会抛出NoSuchBeanDefinitionException。

该异常验证是否确实使用了按类型匹配来解决File依赖关系。
NoSuchBeanDefinitionException确认引用变量名称不需要与bean名称匹配，相反，依赖解析取决于bean的类型与引用变量的类型是否匹配。

#### 2.2.3 按限定符匹配

我们使用MethodByQualifierResourceTest集成测试来演示按限定符匹配的执行方式：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = ApplicationContextTestResourceQualifier.class
)
class MethodByQualifierResourceIntegrationTest {

    private File arbDependency;

    private File anotherArbDependency;

    @Resource
    @Qualifier("namedFile")
    public void setArbDependency(File arbDependency) {
        this.arbDependency = arbDependency;
    }

    @Resource
    @Qualifier("defaultFile")
    public void setAnotherArbDependency(File anotherArbDependency) {
        this.anotherArbDependency = anotherArbDependency;
    }

    @Test
    void givenResourceQualifier_WhenSetter_ThenValidDependencies() {
        assertNotNull(arbDependency);
        assertEquals("namedFile.txt", arbDependency.getName());
        assertNotNull(anotherArbDependency);
        assertEquals("defaultFile.txt", anotherArbDependency.getName());
    }
}
```

测试表明，即使我们在应用程序上下文中定义了特定类型的多个bean，我们也可以使用@Qualifier注解和@Resource注解来解决依赖关系。

类似于基于字段的依赖注入，如果我们在一个应用上下文中定义多个bean，
我们必须使用@Qualifier注解来指定使用哪个bean来解析依赖，否则会抛出NoUniqueBeanDefinitionException。

## 3. @Inject注解

@Inject注解属于JSR-330注解集合。此注解具有以下执行方式，按优先级列出：

1. 按类型匹配
2. 按限定符匹配
3. 按名称匹配

这些执行方式适用于Setter和字段注入。为了使用@Inject注解，我们必须在pom.xml中添加javax.inject依赖。

```xml

<dependency>
    <groupId>javax.inject</groupId>
    <artifactId>javax.inject</artifactId>
    <version>1</version>
</dependency>
```

### 3.1 字段注入

#### 3.1.1 按类型匹配

我们修改集成测试示例以使用另一种类型的依赖项，即ArbitraryDependency类。
ArbitraryDependency类依赖仅作为一个简单的依赖，并没有任何其他意义：

```java

@Component
public class ArbitraryDependency {
    private final String label = "Arbitrary Dependency";

    public String toString() {
        return label;
    }
}
```

下面的FieldInjectIntegrationTest集成测试是有问题的：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = ApplicationContextTestInjectType.class
)
class FieldInjectIntegrationTest {

    @Inject
    private ArbitraryDependency fieldInjectDependency;

    @Test
    void givenInjectAnnotation_WhenOnField_ThenValidDependency() {
        assertNotNull(fieldInjectDependency);
        assertEquals("Arbitrary Dependency", fieldInjectDependency.toString());
    }
}
```

与@Resource注解不同的是，@Inject注解的默认行为是按类型解析依赖关系。

这意味着即使引用变量名称与bean名称不同，依赖关系仍然会被解析，前提是bean是在应用程序上下文中定义的。
请注意以下测试中引用变量的名称：

```text
@Inject
private ArbitraryDependency fieldInjectDependency;
```

与应用程序上下文中配置的bean名称不同：

```java

@Configuration
public class ApplicationContextTestInjectType {

    @Bean
    public ArbitraryDependency injectDependency() {
        return new ArbitraryDependency();
    }
}
```

当我们执行测试时，Spring能够解决依赖关系。

#### 3.1.2 按限定符匹配

如果一个特定的类型有多个实现，并且某个类需要一个特定的bean，该怎么办？让我们修改集成测试示例，添加另一个依赖项。

在本例中，我们将ArbitraryDependency类进行子类化，创建AnotherArbitraryDependency类：

```java

@Component
public class AnotherArbitraryDependency extends ArbitraryDependency {
    private final String label = "Another Arbitrary Dependency";

    public String toString() {
        return label;
    }
}
```

每个测试用例的目标是确保我们将每个依赖项正确地注入每个引用变量中：

```text
@Inject
private ArbitraryDependency defaultDependency;

@Inject
private ArbitraryDependency namedDependency;
```

我们可以使用FieldQualifierInjectIntegrationTest集成测试来演示按限定符匹配：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = ApplicationContextTestInjectQualifier.class
)
class FieldQualifierInjectIntegrationTest {

    @Inject
    private ArbitraryDependency defaultDependency;

    @Inject
    private ArbitraryDependency namedDependency;

    @Test
    void givenInjectQualifier_WhenOnField_ThenDefaultFileValid() {
        assertNotNull(defaultDependency);
        assertEquals("Arbitrary Dependency", defaultDependency.toString());
    }

    @Test
    void givenInjectQualifier_WhenOnField_ThenNamedFileValid() {
        assertNotNull(defaultDependency);
        assertEquals("Another Arbitrary Dependency", namedDependency.toString());
    }
}
```

如果我们在一个应用程序上下文中有一个特定类的多个实现，
并且FieldQualifierInjectIntegrationTest集成测试尝试以下面列出的方式注入依赖项，
则会抛出NoUniqueBeanDefinitionException：

```text
@Inject 
private ArbitraryDependency defaultDependency;

@Inject 
private ArbitraryDependency namedDependency;
```

抛出这个异常是因为Spring框架发现某个类有多个bean存在，它对使用哪一个感到困惑。
为了阐明混淆，我们可以修改FieldQualifierInjectIntegrationTest集成测试的第8行和第11行：

```text
@Inject
private ArbitraryDependency defaultDependency;

@Inject
private ArbitraryDependency namedDependency;
```

我们可以将所需的bean名称传递给@Qualifier注解，将其与@Inject注解一起使用。以下是代码修改后的样子：

```text
@Inject
@Qualifier("defaultFile")
private ArbitraryDependency defaultDependency;

@Inject
@Qualifier("namedFile")
private ArbitraryDependency namedDependency;
```

@Qualifier注解在指定bean名称时要求严格匹配。
我们必须确保将bean名称正确传递给@Qualifier，否则将抛出NoUniqueBeanDefinitionException。如果我们再次运行测试，它应该会通过。

#### 3.1.3 按名称匹配

用于演示按名称匹配的FieldByNameInjectIntegrationTest集成测试类似于按类型匹配的执行方式。
唯一的区别是现在我们需要一个特定的bean，而不是一个特定类型的bean。
在本例中，我们再次对ArbitraryDependency类进行子类，新建YetAnotherArbitraryDependency类：

```java

@Component
public class YetAnotherArbitraryDependency extends ArbitraryDependency {
    private final String label = "Yet Another Arbitrary Dependency";

    public String toString() {
        return label;
    }
}
```

为了演示按名称匹配的执行方式，我们使用以下集成测试类：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = ApplicationContextTestInjectName.class
)
class FieldByNameInjectIntegrationTest {

    @Inject
    @Named("yetAnotherFieldInjectDependency")
    private ArbitraryDependency yetAnotherFieldInjectDependency;

    @Test
    void givenInjectQualifier_WhenSetOnField_ThenDependencyValid() {
        assertNotNull(yetAnotherFieldInjectDependency);
        assertEquals("Yet Another Arbitrary Dependency", yetAnotherFieldInjectDependency.toString());
    }
}
```

以下是对应的应用程序上下文配置类：

```java

@Configuration
public class ApplicationContextTestInjectName {

    @Bean
    public ArbitraryDependency yetAnotherFieldInjectDependency() {
        return new YetAnotherArbitraryDependency();
    }
}
```

如果我们运行集成测试，它将通过。

为了验证我们是否通过按名称匹配的执行方式注入了依赖项，
我们需要将传入@Named注解的值yetAnotherFieldInjectDependency更改为我们选择的另一个名称。
当我们再次运行测试时，会抛出NoSuchBeanDefinitionException。

### 3.2 Setter注入

@Inject注解的基于setter注入方式类似于使用@Resource基于setter的注入。
我们不使用注解标注引用变量，而是标注相应的setter方法。基于字段的依赖注入所遵循的执行方式也适用于基于setter的注入。

## 4. @Autowired注解

@Autowired注解的行为类似于@Inject注解，唯一的区别是@Autowired注解是Spring框架的一部分。
此注解与@Inject注解具有相同的匹配方式，按优先顺序列出：

1. 按类型匹配
2. 按限定符匹配
3. 按名称匹配

这些执行方式适用于setter和字段注入。

### 4.1 字段注入

#### 4.1.1 按类型匹配

用于演示@Autowired按类型匹配的执行方式的集成测试示例类似于用于演示@Inject按类型匹配的执行方式的测试。
我们使用以下FieldAutowiredIntegrationTest集成测试来演示使用@Autowired注解的按类型匹配：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = ApplicationContextTestAutowiredType.class
)
class FieldAutowiredIntegrationTest {

    @Autowired
    private ArbitraryDependency fieldDependency;

    @Test
    void givenAutowired_WhenSetOnField_ThenDependencyResolved() {
        assertNotNull(fieldDependency);
        assertEquals("Arbitrary Dependency", fieldDependency.toString());
    }
}
```

以下是用于该集成测试的应用程序上下文配置类：

```java

@Configuration
public class ApplicationContextTestAutowiredType {

    @Bean
    public ArbitraryDependency autowiredFieldDependency() {
        return new ArbitraryDependency();
    }
}
```

我们使用这个集成测试来证明按类型匹配优先于其他的执行方式。注意FieldAutowiredIntegrationTest集成测试第9行的引用变量名称：

```text
@Autowired
private ArbitraryDependency fieldDependency;
```

这与应用程序上下文中的bean名称不同：

```text
@Bean
public ArbitraryDependency autowiredFieldDependency() {
    return new ArbitraryDependency();
}
```

当我们运行测试时，它应该通过。

为了确认依赖确实是使用按类型匹配的执行方式解决的，我们需要更改fieldDependency引用变量的类型并再次运行集成测试。
这一次，FieldAutowiredIntegrationTest集成测试将失败，并引发NoSuchBeanDefinitionException。

#### 4.1.2 按限定符匹配

如果我们在应用程序上下文中定义了多个bean实现，会怎么样：

```java

@Configuration
public class ApplicationContextTestAutowiredQualifier {

    @Bean
    public ArbitraryDependency autowiredFieldDependency() {
        return new ArbitraryDependency();
    }

    @Bean
    public ArbitraryDependency anotherAutowiredFieldDependency() {
        return new AnotherArbitraryDependency();
    }
}
```

当我们执行以下FieldQualifierAutowiredIntegrationTest集成测试，则会抛出NoUniqueBeanDefinitionException：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = ApplicationContextTestAutowiredQualifier.class
)
class FieldQualifierAutowiredIntegrationTest {

    @Autowired
    private ArbitraryDependency fieldDependency1;

    @Autowired
    private ArbitraryDependency fieldDependency2;

    @Test
    void givenAutowiredQualifier_WhenOnField_ThenDep1Valid() {
        assertNotNull(fieldDependency1);
        assertEquals("Arbitrary Dependency", fieldDependency1.toString());
    }

    @Test
    void givenAutowiredQualifier_WhenOnField_ThenDep2Valid() {
        assertNotNull(fieldDependency2);
        assertEquals("Another Arbitrary Dependency", fieldDependency2.toString());
    }
}
```

这个异常是由于应用程序上下文中定义的两个bean引起的歧义造成的，Spring框架不知道哪个bean依赖应该自动注入到哪个引用变量。
我们可以通过在FieldQualifierAutowiredIntegrationTest集成测试的第8行和第11行添加@Qualifier注解来解决此问题：

```text
@Autowired
private FieldDependency fieldDependency1;

@Autowired
private FieldDependency fieldDependency2;
```

使代码如下所示：

```text
@Autowired
@Qualifier("autowiredFieldDependency")
private ArbitraryDependency fieldDependency1;

@Autowired
@Qualifier("anotherAutowiredFieldDependency")
private ArbitraryDependency fieldDependency2;
```

当我们再次运行测试时，它将通过。

#### 4.1.3 按名称匹配

我们使用相同的集成测试场景来演示使用@Autowired注解注入字段依赖项的按名称匹配的执行方式。
当按名称自动注入依赖项时，@ComponentScan注解必须与应用程序上下文配置类一起使用：

```java

@Configuration
@ComponentScan(basePackages = {"cn.tuyucheng.taketoday.dependency"})
public class ApplicationContextTestAutowiredName {

}
```

我们使用@ComponentScan注解在包中搜索已使用@Component注解进行标注的Java类。
例如，在应用程序上下文配置类中，将扫描cn.tuyucheng.taketoday.dependency包以查找使用@Component注解进行标注的类。
在这种情况下，Spring框架必然会检测到带有@Component注解的ArbitraryDependency类：

```java

@Component(value = "autowiredFieldDependency")
public class ArbitraryDependency {

    private final String label = "Arbitrary Dependency";

    public String toString() {
        return label;
    }
}
```

传递给@Component注解的value属性值autowiredFieldDependency告诉Spring，ArbitraryDependency类是一个名为autowiredFieldDependency的组件。
为了让@Autowired注解通过名称解析依赖，组件名称必须与FieldAutowiredNameIntegrationTest集成测试中定义的字段名称相对应；请参考第9行：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        loader = AnnotationConfigContextLoader.class,
        classes = ApplicationContextTestAutowiredName.class
)
class FieldAutowiredNameIntegrationTest {

    @Autowired
    private ArbitraryDependency autowiredFieldDependency;

    @Test
    void givenAutowiredAnnotation_WhenOnField_ThenDependencyValid() {
        assertNotNull(autowiredFieldDependency);
        assertEquals("Arbitrary Dependency", autowiredFieldDependency.toString());
    }
}
```

当我们运行FieldAutowiredNameIntegrationTest集成测试时，它应该通过。

但是我们怎么知道@Autowired注解确实是使用按名称匹配的执行方式呢？
我们可以将引用变量autowiredFieldDependency的名称更改为另一个名称，然后再次运行测试。

这一次，测试将失败并抛出NoUniqueBeanDefinitionException。
也可以将@Component属性值autowiredFieldDependency更改为另一个值并再次运行测试，也会抛出NoUniqueBeanDefinitionException。

### 4.2 setter注入

@Autowired注解基于setter的注入类似于@Resource基于setter注入的方式，我们不使用@Inject注解来标注引用变量，而是标注对应的setter方法。
基于字段的依赖注入所遵循的执行方式也适用于基于setter的注入。

## 5. 注解的应用

一个常见的问题是上面的三个注解应该使用哪种注解以及在什么情况下使用。
这些问题的答案取决于相关应用程序面临的设计场景，以及开发人员希望如何利用基于每个注解的默认执行方式的多态性。

### 5.1 通过多态性在应用程序范围内使用单例

如果设计使得应用程序行为基于接口或抽象类的实现，并且这些行为在整个应用程序中使用，那么我们可以使用@Inject或@Autowired注解。

这种方法的好处是，当我们升级应用程序来解决错误时，可以将类换出，而对整体应用程序行为的负面影响最小。
在这种情况下，主要的默认执行方式是按类型匹配。

### 5.2 通过多态进行细粒度的应用程序行为配置

如果设计使得应用程序具有复杂的行为，每个行为都基于不同的接口/抽象类，并且每个实现的使用在应用程序中都不同，那么我们可以使用@Resource注解。
在这种情况下，主要的默认执行方式是按名称匹配。

### 5.3 依赖注入应该由Jakarta EE平台单独处理

如果我们使用Jakarta EE而不是Spring作为依赖注入的解决方案，那么我们可以在@Resource注解和@Inject注解之间进行选择。
我们应该根据需要的默认匹配方式来缩小两个注解之间的最终决定。

### 5.4 依赖注入应该由Spring框架单独处理

如果要求所有依赖项都由Spring框架处理，则唯一的选择是@Autowired注解。

### 5.5 摘要

下表总结了上面的说明：

| 场景  | @Resource  | @Inject |  @Autowired  |
|:----|:----------:|:-------:|:------------:|
| 通过多态性广泛应用单例    |     x      |    √    |      √       |
|通过多态进行细粒度的应用程序行为配置     |     √      |    x    |      x       |
|    依赖注入应该由 Jakarta EE 平台单独处理                   |     √      |    √    |      x       |
|    依赖注入应该由 Spring Framework 单独处理    |     x      |    x    |      √       |

## 6. 总结

在本文中，我们深入地介绍了用于依赖注入的三个注解的不同行为。