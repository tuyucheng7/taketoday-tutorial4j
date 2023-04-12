## 1. 概述

在本文中，我们将介绍Spring的@Order注解。**@Order定义了带注解的component或bean的优先级顺序**。

它有一个可选的value参数，用于确定组件的顺序；默认值为Ordered.LOWEST_PRECEDENCE。这标志着该组件在所有其他组件中的优先级最低。

同样，Ordered.HIGHEST_PRECEDENCE可用于覆盖组件中的最高优先级。

## 2. 何时使用@Order

在Spring 4.0之前，@Order注解仅用于AspectJ执行顺序。这意味着最高级别的通知将首先运行。

从Spring 4.0开始，它支持将注入的组件排序到集合中。因此，Spring将根据它们的order值自动注入相同类型的bean。

让我们用一个简单的例子来说明。

## 3. 如何使用@Order

### 3.1 创建接口

让我们创建决定产品评级的Rating接口：

```java
public interface Rating {
    int getRating();
}
```

### 3.2 创建组件

最后，让我们创建三个组件来定义某些产品的评级：

```java

@Component
@Order(1)
public class Excellent implements Rating {

    @Override
    public int getRating() {
        return 1;
    }
}

@Component
@Order(2)
public class Good implements Rating {

    @Override
    public int getRating() {
        return 2;
    }
}

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class Average implements Rating {

    @Override
    public int getRating() {
        return 3;
    }
}
```

请注意，Average类的优先级最低，因为它的value属性指定为Ordered.LOWEST_PRECEDENCE。

## 4. 测试

到目前为止，我们已经创建了所有必需的组件和接口来测试@Order注解。现在，让我们对其进行测试以确认它是否按预期工作：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
class RatingRetrieverUnitTest {

    @Autowired
    private List<Rating> ratings;

    @Test
    void givenOrderOnComponents_whenInjected_thenAutowireByOrderValue() {
        assertThat(ratings.get(0).getRating(), is(equalTo(1)));
        assertThat(ratings.get(1).getRating(), is(equalTo(2)));
        assertThat(ratings.get(2).getRating(), is(equalTo(3)));
    }

    @Configuration
    @ComponentScan(basePackages = {"cn.tuyucheng.taketoday.spring.order"})
    static class ContextConfiguration {
    }
}
```

## 5. 总结

在这篇文章中我们了解了@Order注解。我们可以在各种用例中找到@Order的应用，其中最重要的是自动注入组件的顺序。一个例子是Spring的请求过滤器。

由于它对注入优先级的影响，它似乎也可能影响单例启动顺序。但相比之下，依赖关系和@DependsOn声明决定了单例启动顺序。