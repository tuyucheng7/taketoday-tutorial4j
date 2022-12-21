## 1. 概述

在前面的教程中，我们学习了[Spring组件扫描](Spring组件扫描.md)的基础知识。

在这篇文章中，我们介绍[@ComponentScan](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/ComponentScan.html)注解可用的不同类型的过滤选项。

## 2. @ComponentScan过滤

默认情况下，用@Component、@Repository、@Service、@Controller注解标注的类被注册为[Spring beans]()，对于使用@Component注解的自定义注解进行注解的类也是如此。我们可以通过使用@ComponentScan注解的includeFilters和excludeFilters参数来扩展此行为。

**[ComponentScan.Filter](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/ComponentScan.Filter.html)有五种过滤类型可用**：

-   ANNOTATION
-   ASSIGNABLE_TYPE
-   ASPECTJ
-   REGEX
-   CUSTOM

我们将在下一节中详细介绍这些内容。

我们应该注意，所有这些过滤类型都可以在扫描中包含或排除类。在我们的例子中为了简单起见，我们只包含类。

## 3. FilterType.ANNOTATION

**ANNOTATION过滤类型包括或排除组件扫描中标有给定注解的类**。

比方说，我们有一个@Animal注解：

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Animal {}
```

现在，让我们定义一个使用@Animal的Elephant类：

```java
@Animal
public class Elephant {}
```

最后，我们使用FilterType.ANNOTATION告诉Spring扫描带有@Animal注解的类：

```java
@Configuration
@ComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Animal.class))
public class ComponentScanAnnotationFilterApp {}
```

如我们所见，Spring可以很好地获取我们的Elephant：

```java
@Test
void whenAnnotationFilterIsUsed_thenComponentScanShouldRegisterBeanAnnotatedWithAnimalAnootation() {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ComponentScanAnnotationFilterApp.class);
    List<String> beans = Arrays.stream(applicationContext.getBeanDefinitionNames())
            .filter(bean -> !bean.contains("org.springframework") && !bean.contains("componentScanAnnotationFilterApp"))
            .collect(Collectors.toList());
    
    assertThat(beans.size(), equalTo(1));
    assertThat(beans.get(0), equalTo("elephant"));
}
```

## 4. FilterType.ASSIGNABLE_TYPE

**ASSIGNABLE_TYPE在组件扫描期间过滤所有扩展类或实现指定类型接口的类**。

首先，让我们声明Animal接口：

```java
public interface Animal {}
```

再一次，声明我们的Elephant类，这次实现Animal接口：

```java
public class Elephant implements Animal {}
```

同时声明我们的Cat类也实现了Animal：

```java
public class Cat implements Animal {}
```

现在，让我们使用ASSIGNABLE_TYPE来引导Spring扫描Animal实现类：

```java
@Configuration
@ComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = Animal.class))
public class ComponentScanAssignableTypeFilterApp {}
```

我们可以看到Cat和Elephant都被扫描了：

```java
@Test
void whenAssignableTypeFilterIsUsed_thenComponentScanShouldRegisterBean() {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ComponentScanAssignableTypeFilterApp.class);
    List<String> beans = Arrays.stream(applicationContext.getBeanDefinitionNames())
          .filter(bean -> !bean.contains("org.springframework") && !bean.contains("componentScanAssignableTypeFilterApp"))
          .collect(Collectors.toList());
    
    assertThat(beans.size(), equalTo(2));
    assertThat(beans.contains("cat"), equalTo(true));
    assertThat(beans.contains("elephant"), equalTo(true));
}
```

## 5. FilterType.REGEX

**REGEX过滤类型检查类名是否匹配给定的正则表达式模式，FilterType.REGEX检查简单类名和完全限定类名**。

再次声明我们的Elephant类，这次不实现任何接口或使用任何注解进行标注：

```java
public class Elephant {}
```

我们再声明一个类Cat：

```java
public class Cat {}
```

现在，让我们声明Lion类：

```java
public class Lion {}
```

我们使用FilterType.REGEX指示Spring扫描匹配正则表达式.*[nt]的类，我们的正则表达式评估包含nt的所有内容：

```java
@Configuration
@ComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
        pattern = ".*[nt]"))
public class ComponentScanRegexFilterApp {}
```

这次在我们的测试中，我们看到Spring扫描了Elephant，但没有扫描Lion：

```java
@Test
void whenRegexFilterIsUsed_thenComponentScanShouldRegisterBeanMatchingRegex() {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ComponentScanRegexFilterApp.class);
    List<String> beans = Arrays.stream(applicationContext.getBeanDefinitionNames())
          .filter(bean -> !bean.contains("org.springframework") && !bean.contains("componentScanRegexFilterApp"))
          .collect(Collectors.toList());
    
    assertThat(beans.size(), equalTo(1));
    assertThat(beans.contains("elephant"), equalTo(true));
}
```

## 6. FilterType.ASPECTJ

**当我们想要使用表达式来挑选复杂的类子集时，我们需要使用FilterType.ASPECTJ**。

对于这个用例，我们可以重用与上一节中相同的三个类。

让我们使用FilterType.ASPECTJ来指示Spring扫描与我们的AspectJ表达式匹配的类：

```java
@Configuration
@ComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ, 
      pattern = "cn.tuyucheng.taketoday.componentscan.filter.aspectj.* " 
            + "&& !(cn.tuyucheng.taketoday.componentscan.filter.aspectj.L* " 
            + "|| cn.tuyucheng.taketoday.componentscan.filter.aspectj.C*)"))
public class ComponentScanAspectJFilterApp {}
```

虽然有点复杂，但我们这里的逻辑希望bean在类名中既不以“L”也不以“C”开头，这样我们又可以使用Elephant了：

```java
@Test
public void whenAspectJFilterIsUsed_thenComponentScanShouldRegisterBeanMatchingAspectJCreteria() {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ComponentScanAspectJFilterApp.class);
    List<String> beans = Arrays.stream(applicationContext.getBeanDefinitionNames())
          .filter(bean -> !bean.contains("org.springframework") && !bean.contains("componentScanAspectJFilterApp"))
          .collect(Collectors.toList());
    
    assertThat(beans.size(), equalTo(1));
    assertThat(beans.get(0), equalTo("elephant"));
}
```

## 7. FilterType.CUSTOM

**如果以上过滤类型都不符合我们的要求，那么我们还可以创建自定义过滤类型**。例如，假设我们只想扫描名称不超过五个字符的类。

**要创建自定义过滤类型，我们需要实现org.springframework.core.type.filter.TypeFilter**：

```java
public class ComponentScanCustomFilter implements TypeFilter {

    @Override
    public boolean match(MetadataReader metadataReader,
                         MetadataReaderFactory metadataReaderFactory) throws IOException {
        ClassMetadata classMetadata = metadataReader.getClassMetadata();
        String fullyQualifiedName = classMetadata.getClassName();
        String className = fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf(".") + 1);
        return className.length() > 5 ? true : false;
    }
}
```

让我们使用FilterType.CUSTOM指示Spring以使用我们的自定义过滤类型ComponentScanCustomFilter扫描类：

```java
@Configuration
@ComponentScan(includeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, classes = ComponentScanCustomFilter.class))
public class ComponentScanCustomFilterApp {}
```

下面是对自定义过滤类型ComponentScanCustomFilter的简单测试：

```java
@Test
public void whenCustomFilterIsUsed_thenComponentScanShouldRegisterBeanMatchingCustomFilter() {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ComponentScanCustomFilterApp.class);
    List<String> beans = Arrays.stream(applicationContext.getBeanDefinitionNames())
          .filter(bean -> !bean.contains("org.springframework") && !bean.contains("componentScanCustomFilterApp") && !bean.contains("componentScanCustomFilter"))
          .collect(Collectors.toList());
    
    assertThat(beans.size(), equalTo(1));
    assertThat(beans.get(0), equalTo("elephant"));
}
```

## 8. 总结

在本教程中，我们介绍了与@ComponentScan关联的过滤类型。