## 1. 概述

Java 8引入了一系列新特性，比如Lambda和Stream。Mockito在其第二个主要版本中充分利用了这些主要的新特性。

在本文中，我们将介绍Mockito中对Java 8的支持。

## 2. mock带有默认方法的接口

从Java 8开始，我们现在可以在接口中编写方法实现，即使用default修饰的方法。
这也许是一个很棒的新特性，但它的引入违反了Java自诞生以来就属于它的一个强大概念，即我们的接口只能包含抽象方法。

Mockito版本1尚未兼容该更改。基本上，因为它不允许我们要求它从接口调用真正的方法。

假设我们有一个带有2个方法声明的接口：第一个是我们很熟悉的抽象方法，另一个是默认方法：

```java
public interface JobService {
    Optional<JobPosition> findCurrentJobPosition(Person person);

    default boolean assignJobPosition(Person person, JobPosition jobPosition) {
        if (findCurrentJobPosition(person).isEmpty()) {
            person.setCurrentJobPosition(jobPosition);
            return true;
        } else {
            return false;
        }
    }

    Stream<JobPosition> listJobs(Person person);
}
```

请注意，assignJobPosition()默认方法调用了未实现的findCurrentJobPosition()方法。

现在，假设我们想要测试assignJobPosition()，而不编写findCurrentJobPosition()的实际实现。
我们可以简单地创建JobService的mock对象，然后告诉Mockito从对未实现方法findCurrentJobPosition()的调用中返回一个已知值，
并在调用assignJobPosition()时调用真实方法：

```java

@ExtendWith(MockitoExtension.class)
class JobServiceUnitTest {
    @Mock
    private JobService jobService;

    @Test
    void givenDefaultMethod_whenCallRealMethod_thenNoExceptionIsRaised() {
        Person person = new Person();

        when(jobService.findCurrentJobPosition(person)).thenReturn(Optional.of(new JobPosition()));
        doCallRealMethod().when(jobService).assignJobPosition(any(Person.class), any(JobPosition.class));

        assertFalse(jobService.assignJobPosition(person, new JobPosition()));
    }
}
```

这是完全合理的，而且如果我们使用的是抽象类而不是接口，那么它也可以正常工作。

然而，Mockito版本1尚不支持这种结构，如果我们用Mockito 2之前的版本运行这段代码，我们会得到以下错误：

```text
org.mockito.exceptions.base.MockitoException:
Cannot call a real method on java interface. The interface does not have any implementation!
Calling real methods is only possible when mocking concrete classes.
```

Mockito告诉我们它不能在接口上调用真正的方法，因为这个操作在Java 8之前是不可取的。

好消息是，只需更改我们正在使用的Mockito版本，我们就可以消除此错误：

```xml

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>4.0.0</version>
    <scope>test</scope>
</dependency>
```

无需对代码进行任何更改。我们再次运行测试时，错误将不再发生。

## 3. 返回Optional和Stream的默认值

Optional和Stream是Java 8新增的其他功能。这两个类之间的一个相似之处是，它们都有一种特殊类型的值来表示一个空对象。
这个空对象可以更容易地避免无处不在的NullPointerException。

### 3.1 Optional案例

假设我们有一个UnemploymentServiceImpl类，它依赖与我们上一小节介绍的JobService，
并有一个调用JobService#findCurrentJobPosition()的方法：

```java
public class UnemploymentServiceImpl implements UnemploymentService {
    private final JobService jobService;

    public UnemploymentServiceImpl(JobService jobService) {
        this.jobService = jobService;
    }

    @Override
    public boolean personIsEntitledToUnemploymentSupport(Person person) {
        Optional<JobPosition> optional = jobService.findCurrentJobPosition(person);
        return !optional.isPresent();
    }
}
```

现在，假设我们要创建一个测试来检查当一个人当前没有工作时，他们是否有权获得失业补助。

在这种情况下，我们将强制findCurrentJobPosition()返回一个空的Optional。在Mockito版本2之前，我们需要mock对该方法的调用：

```java

@ExtendWith(MockitoExtension.class)
class UnemploymentServiceImplUnitTest {

    @Mock
    private JobService jobService;

    @InjectMocks
    private UnemploymentServiceImpl unemploymentService;

    @Test
    void givenReturnIsOfTypeOptional_whenMocked_thenValueIsEmpty() {
        Person person = new Person();

        when(jobService.findCurrentJobPosition(any(Person.class))).thenReturn(Optional.empty());

        assertTrue(unemploymentService.personIsEntitledToUnemploymentSupport(person));
    }
}
```

when(...).thenReturn(...)代码段是必要的，因为Mockito对mock对象的任何方法调用的默认返回值为null。版本2改变了这种行为。

由于我们在处理Optional时很少处理null值，因此**Mockito现在默认返回一个空的Optional**。这与调用Optional.empty()的返回值完全相同。

因此，当使用Mockito版本2时，我们可以去掉when(...).thenReturn(...)代码段，我们的测试仍然会成功：

```java
class UnemploymentServiceImplUnitTest {

    @Test
    void givenReturnIsOfTypeOptional_whenDefaultValueIsReturned_thenValueIsEmpty() {
        Person person = new Person();

        // This will fail when Mockito 1 is used
        assertTrue(unemploymentService.personIsEntitledToUnemploymentSupport(person));
    }
}
```

### 3.2 Stream案例

当我们mock返回Stream的方法时，也会发生相同的行为。

让我们在JobService接口中添加一个新方法，该方法返回一个代表一个人曾经工作过的所有工作职位的Stream：

```java
public interface JobService {
    Stream<JobPosition> listJobs(Person person);
}
```

此方法用于另一个方法，该方法将查询一个人是否曾经从事过与给定searchString匹配的工作：

```java
public class UnemploymentServiceImpl implements UnemploymentService {
    private final JobService jobService;

    public UnemploymentServiceImpl(JobService jobService) {
        this.jobService = jobService;
    }

    @Override
    public Optional<JobPosition> searchJob(Person person, String searchString) {
        Stream<JobPosition> stream = jobService.listJobs(person);
        return stream.filter((j) -> j.getTitle().contains(searchString)).findFirst();
    }
}
```

因此，假设我们想要正确测试searchJob()的实现，而不必担心编写listJobs()，并假设我们想要测试该人尚未从事任何工作时的场景。
在这种情况下，我们希望listJobs()返回一个空Stream。

**在Mockito版本2之前，我们需要mock对listJobs()的调用来编写这样的测试**：

```java
class UnemploymentServiceImplUnitTest {

    @Test
    void givenReturnIsOfTypeStream_whenMocked_thenValueIsEmpty() {
        Person person = new Person();

        when(jobService.listJobs(any(Person.class))).thenReturn(Stream.empty());

        assertFalse(unemploymentService.searchJob(person, "").isPresent());
    }
}
```

**如果我们使用Mockito 2之后的版本，我们可以去掉when(...).thenReturn(...)的调用，
因为现在默认情况下Mockito将在mock方法上返回空Stream**：

```java
class UnemploymentServiceImplUnitTest {

    @Test
    void givenReturnIsOfTypeStream_whenDefaultValueIsReturned_thenValueIsEmpty() {
        Person person = new Person();

        // This will fail when Mockito 1 is used
        assertFalse(unemploymentService.searchJob(person, "").isPresent());
    }
}
```

## 4. Lambda表达式

使用Java 8的lambda表达式，我们可以使代码更紧凑，更易于阅读。其中Lambda表达式带来的最大好处是ArgumentMatchers和自定义Answers。

### 4.1 Lambda和ArgumentMatcher的组合

在Java 8之前，我们需要创建一个实现ArgumentMatcher的类，并在match()方法中编写我们的自定义规则。

在Java 8中，我们可以用一个简单的lambda表达式替换内部类：

```java

@ExtendWith(MockitoExtension.class)
class ArgumentMatcherWithLambdaUnitTest {

    @InjectMocks
    private UnemploymentServiceImpl unemploymentService;

    @Mock
    private JobService jobService;

    @Test
    void whenPersonWithJob_thenIsNotEntitled() {
        Person peter = new Person("Peter");
        Person linda = new Person("Linda");

        JobPosition teacher = new JobPosition("Teacher");

        when(jobService.findCurrentJobPosition(
                ArgumentMatchers.argThat((p) -> p.getName().equals("Peter")))
        ).thenReturn(Optional.of(teacher));

        assertTrue(unemploymentService.personIsEntitledToUnemploymentSupport(linda));
        assertFalse(unemploymentService.personIsEntitledToUnemploymentSupport(peter));
    }
}
```

### 4.2 Lambda和自定义Answer的组合

将lambda表达式与Answer结合使用也可以达到相同的效果。

例如，如果我们想mock对listJobs()方法的调用，以便在Person的名称为“Peter”时使其返回包含单个JobPosition的Stream，
否则返回一个空Stream，那么我们必须创建一个实现Answer接口的类(内部类)。

同样，使用lambda表达式，允许我们内联编写所有mock行为：

```java

@ExtendWith(MockitoExtension.class)
class CustomAnswerWithLambdaUnitTest {

    @InjectMocks
    private UnemploymentServiceImpl unemploymentService;

    @Mock
    private JobService jobService;

    @BeforeEach
    void init() {
        when(jobService.listJobs(any(Person.class))).then((i) ->
                Stream.of(new JobPosition("Teacher"))
                        .filter(p -> ((Person) i.getArgument(0)).getName().equals("Peter")));
    }
}
```

注意，在上面的实现中，不需要PersonAnswer内部类。

## 5. 总结

在本文中，我们介绍了如何利用新的Java 8和Mockito 2以上的功能来编写更简洁、更短的代码。