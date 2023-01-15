## 1. 概述

在本教程中，我们将了解使用Mockito和Spring mock支持创建mock对象的三种不同方法，并讨论它们之间的区别。

## 2. Mockito.mock()

Mockito.mock()方法允许我们创建类或接口的mock对象。然后，我们可以使用mock对其方法的返回值进行stub，并验证它们是否被调用。

让我们看一个例子：

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private Integer status;
    // constructors getters and setters ... 
}
```

```java
@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Integer> {

}
```

```java
class MockAnnotationUnitTest {

    @Test
    public void givenCountMethodMocked_whenCountInvoked_thenMockedValueReturned() {
        UserRepository localMockRepository = Mockito.mock(UserRepository.class);
        Mockito.when(localMockRepository.count()).thenReturn(111L);
        
        long userCount = localMockRepository.count();
        
        assertEquals(111L, userCount);
        Mockito.verify(localMockRepository).count();
    }
}
```

在使用此方法之前，我们不需要对其进行任何其他操作。我们可以使用它来创建字段mock，以及方法中的局部mock。

## 3. Mockito中的@Mock注解

此注解是Mockito.mock()方法的简单替代方式。需要注意的是，我们应该只在测试类中使用它。与mock()方法不同，我们需要启用Mockito注解支持才能使用此注解。

在Junit 5中，我们可以通过使用MockitoExtension来运行测试，或者通过显式调用MockitoAnnotations.initMocks()方法来做到这一点。

让我们看一个使用MockitoExtension的示例：

```java
@ExtendWith(MockitoExtension.class)
public class MockAnnotationUnitTest {
    
    @Mock
    private UserRepository mockRepository;

    @Test
    void givenCountMethodMocked_whenCountInvoked_thenMockValueReturned() {
        Mockito.when(mockRepository.count()).thenReturn(123L);
        
        long userCount = mockRepository.count();
        
        assertEquals(123L, userCount);
        Mockito.verify(mockRepository).count();
    }
}
```

除了使代码更具可读性之外，**@Mock注解还可以在失败的情况下更轻松地找到出现问题的mock，因为该字段的名称出现在异常消息中**：

```text
Wanted but not invoked:
mockRepository.count();
-> at cn.tuyucheng.taketoday.mockito.MockAnnotationUnitTest.givenCountMethodMocked_whenCountInvoked_thenMockValueReturned(MockAnnotationUnitTest.java:31)
Actually, there were zero interactions with this mock.
```

此外，当与@InjectMocks注解结合使用时，它可以显著减少setup()方法代码的数量。

## 4. SpringBoot的@MockBean注解

我们可以使用@MockBean将mock对象添加到Spring应用程序上下文中，mock对象将替换应用程序上下文中任何现有的相同类型的bean。如果没有定义相同类型的bean，则只是会添加一个新的。在需要mock特定bean(如外部服务)的集成测试中，此注解非常有用。

要使用这个注解，我们必须使用SpringExtension来运行测试：

```java
@ExtendWith(SpringExtension.class)
public class MockBeanAnnotationIntegrationTest {
    
    @MockBean
    UserRepository mockRepository;

    @Autowired
    ApplicationContext context;

    @Test
    public void givenCountMethodMocked_whenCountInvoked_thenMockValueReturned() {
        Mockito.when(mockRepository.count()).thenReturn(123L);
        
        UserRepository userRepositoryFromContext = context.getBean(UserRepository.class);
        long userCount = userRepositoryFromContext.count();
        assertEquals(123L, userCount);
        
        Mockito.verify(mockRepository).count();
    }
}
```

当我们在字段上使用@MockBean注解时，mock将被注入到字段中，并在应用程序上下文中注册。

这在上面的代码中很明显。这里我们使用注入的UserRepository mock来stub count()方法。然后我们从Spring容器中获取一个UserRepository类型的bean，使用该bean来验证它是否确实是mock的bean。

## 5. 总结

在本文中，我们介绍了创建mock对象的三种方法，以及如何使用它们。