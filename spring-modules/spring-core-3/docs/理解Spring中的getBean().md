## 1. 概述

在本文中，我们将介绍BeanFactory.getBean()方法的不同重载。

简单地说，正如该方法的名称所示，**它负责从Spring容器中检索一个bean实例**。

## 2. Spring Beans设置

首先，让我们定义一些用于测试的Spring bean。我们可以通过多种方式为Spring容器提供bean定义。
在我们的示例中，我们将使用基于注解的Java配置：

```java

@Configuration
class AnnotationConfig {

    @Bean(name = {"tiger", "kitty"})
    @Scope(value = "prototype")
    Tiger getTiger(String name) {
        return new Tiger(name);
    }

    @Bean(name = "lion")
    Lion getLion() {
        return new Lion("Hardcoded lion name");
    }

    interface Animal {
    }
}

class Tiger implements AnnotationConfig.Animal {
    private final String name;

    Tiger(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }
}

class Lion implements AnnotationConfig.Animal {
    private final String name;

    Lion(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }
}
```

我们创建了两个bean。Lion具有默认的单例作用域。 Tiger明确设置为原型作用域。
此外，请注意，我们为每个bean定义了名称，我们将在下一步的请求中使用这些名称。

## 3. getBean() APIs

BeanFactory提供了五种不同的getBean()方法重载，我们将在下面的小节中进行讲解。

### 3.1 按名称检索Bean

让我们看看如何使用名称检索Lion bean实例：

```java
class GetBeanByNameUnitTest {
    private ApplicationContext context;

    @BeforeAll
    void setup() {
        context = new AnnotationConfigApplicationContext(AnnotationConfig.class);
    }

    @Test
    void whenGivenExistingBeanName_shouldReturnThatBean() {
        Object lion = context.getBean("lion");
        assertEquals(lion.getClass(), Lion.class);
    }
}
```

在这个方法中，我们提供了一个名称，如果具有给定名称的bean存在于应用程序上下文中，我们将获得一个Object类的实例。
否则，如果bean查找失败，此实现和所有其他实现都会抛出NoSuchBeanDefinitionException。

主要缺点是在检索bean后，**我们必须将其转换为所需的类型。如果返回的bean的类型与我们预期的不同，这可能会产生另一个异常**。

假设我们尝试使用名称“lion”来获取Tiger对象。当我们将结果转换为Tiger时，它会抛出ClassCastException：

```java
class GetBeanByNameUnitTest {
    private ApplicationContext context;

    @BeforeAll
    void setup() {
        context = new AnnotationConfigApplicationContext(AnnotationConfig.class);
    }

    @Test
    void whenCastingToWrongType_thenShouldThrowException() {
        assertThrows(ClassCastException.class, () -> {
            Tiger tiger = (Tiger) context.getBean("lion");
        });
    }
}
```

### 3.2 按名称和类型检索Bean

这里我们需要指定请求bean的名称和类型：

```java

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetBeanByNameAndTypeUnitTest {
    private ApplicationContext context;

    @BeforeAll
    void setup() {
        context = new AnnotationConfigApplicationContext(AnnotationConfig.class);
    }

    @Test
    void whenSpecifiedMatchingNameAndType_thenShouldReturnRelatedBean() {
        Lion lion = context.getBean("lion", Lion.class);
        assertEquals("Hardcoded lion name", lion.getName());
    }
}
```

与前一种方法相比，这种方法更安全，因为我们可以立即获得有关类型不匹配的信息：

```java

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetBeanByNameAndTypeUnitTest {
    private ApplicationContext context;

    @BeforeAll
    void setup() {
        context = new AnnotationConfigApplicationContext(AnnotationConfig.class);
    }

    @Test
    void whenSpecifiedNotMatchingNameAndType_thenShouldThrowException() {
        assertThrows(BeanNotOfRequiredTypeException.class, () -> context.getBean("lion", Tiger.class));
    }
}
```

### 3.3 按类型检索Bean

使用getBean()的第三个重载，只指定bean类型就足够了：

```java
class GetBeanByTypeUnitTest {
    private ApplicationContext context;

    @BeforeAll
    void setup() {
        context = new AnnotationConfigApplicationContext(AnnotationConfig.class);
    }

    @Test
    void whenGivenExistingUniqueType_thenShouldReturnRelatedBean() {
        Lion lion = context.getBean(Lion.class);
        assertNotNull(lion);
    }
}
```

在这种情况下，**我们需要特别注意一个潜在的存在多个相同类型的bean的情况**：

```java

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetBeanByTypeUnitTest {
    private ApplicationContext context;

    @BeforeAll
    void setup() {
        context = new AnnotationConfigApplicationContext(AnnotationConfig.class);
    }

    @Test
    void whenGivenAmbiguousType_thenShouldThrowException() {
        assertThrows(NoUniqueBeanDefinitionException.class, () -> context.getBean(AnnotationConfig.Animal.class));
    }
}
```

在上面的例子中，因为Lion和Tiger都实现了Animal接口，仅仅指定类型并不足以明确地告诉Spring我们需要注入哪个名称的Animal bean。
因此，我们会得到一个NoUniqueBeanDefinitionException。

### 3.4 使用构造函数参数按名称检索Bean

除了bean名称，我们还可以传递构造函数参数：

```java
class GetBeanByNameWithConstructorParametersUnitTest {
    private ApplicationContext context;

    @BeforeAll
    void setup() {
        context = new AnnotationConfigApplicationContext(AnnotationConfig.class);
    }

    @Test
    void whenGivenCorrectName_thenShouldReturnBeanWithSpecifiedName() {
        Tiger tiger = (Tiger) context.getBean("tiger", "Siberian");
        assertEquals("Siberian", tiger.getName());
    }
}
```

**这个方法有点不同，因为它只适用于具有原型作用域的bean**。

在单例的情况下，我们将得到一个BeanDefinitionStoreException。

因为原型bean每次从应用程序容器请求时都会返回一个新创建的实例，所以我们可以在调用getBean()时**即时提供构造函数参数**：

```java

class GetBeanByNameWithConstructorParametersUnitTest {
    private ApplicationContext context;

    @BeforeAll
    void setup() {
        context = new AnnotationConfigApplicationContext(AnnotationConfig.class);
    }

    @Test
    void whenGivenCorrectNameOrAlias_shouldReturnBeanWithSpecifiedName() {
        Tiger tiger = (Tiger) context.getBean("tiger", "Siberian");
        Tiger secondTiger = (Tiger) context.getBean("tiger", "Striped");
        assertEquals("Siberian", tiger.getName());
        assertEquals("Striped", secondTiger.getName());
    }
}
```

正如我们所见，根据我们在请求bean时指定的第二个参数，每个Tiger的name都会返回不同的值。

### 3.5 使用构造函数参数按类型检索Bean

此方法类似于上一个方法，但第一个参数我们需要传递类型而不是名称：

```java

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetBeanByTypeWithConstructorParametersUnitTest {
    private ApplicationContext context;

    @BeforeAll
    void setup() {
        context = new AnnotationConfigApplicationContext(AnnotationConfig.class);
    }

    @Test
    void whenGivenExistingTypeAndValidParameters_thenShouldReturnRelatedBean() {
        Tiger tiger = context.getBean(Tiger.class, "Shere Khan");
        assertEquals("Shere Khan", tiger.getName());
    }
}
```

与使用构造函数参数按名称检索bean类似，**此方法仅适用于具有原型作用域的bean**。;

## 4. 使用注意事项

尽管getBean()方法在BeanFactory接口中定义，但getBean()方法最常通过ApplicationContext访问。
**通常，我们不会在程序中直接使用getBean()方法**。

bean应该由容器管理。如果我们想使用其中一个bean，我们应该使用依赖注入而不是直接调用ApplicationContext.getBean()。
这样，我们就可以避免将应用程序逻辑与框架相关的细节混在一起。