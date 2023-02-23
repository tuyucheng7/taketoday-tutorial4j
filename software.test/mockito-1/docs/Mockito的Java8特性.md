## 1. 概述

Java 8引入了一系列令人敬畏的新功能，比如Lambda和Stream。自然地，Mockito在其[第二个主要版本](https://github.com/mockito/mockito/wiki/What's-new-in-Mockito-2)中利用了这些最新的创新。

在本文中，我们将探讨这个强大的组合所提供的一切。

## 2. Mock带有默认方法的接口

从Java 8开始，我们现在可以在接口中编写方法实现，即使用default修饰的方法。这可能是一个很棒的新特性，但它的引入违反了Java自诞生以来就属于它的一个重要概念，即我们的接口只能包含抽象方法。

Mockito版本1尚未准备好进行此更改。基本上，因为它不允许我们要求它从接口调用真正的方法。

假设我们有一个带有2个方法声明的接口：第一个是我们都习惯的老式方法签名，另一个是全新的默认方法：

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
}
```

请注意，assignJobPosition()默认方法调用了未实现的findCurrentJobPosition()方法。

现在，假设我们想测试assignJobPosition()的实现，而不编写findCurrentJobPosition()的实际实现。我们可以简单地创建JobService的Mock版本，然后告诉Mockito从对我们未实现的方法findCurrentJobPosition()的调用中返回一个已知值，并在调用assignJobPosition()时调用真实方法：

```java
@ExtendWith(MockitoExtension.class)
class JobServiceUnitTest {
    @Mock
    private JobService jobService;

    @Test
    void givenDefaultMethod_whenCallRealMethod_thenNoExceptionIsRaised() {
        Person person = new Person();

        when(jobService.findCurrentJobPosition(person))
              .thenReturn(Optional.of(new JobPosition()));
        doCallRealMethod().when(jobService)
              .assignJobPosition(any(Person.class), any(JobPosition.class));

        assertFalse(jobService.assignJobPosition(person, new JobPosition()));
    }
}
```

这是完全合理的，而且如果我们使用的是抽象类而不是接口，那么它也可以正常工作。

然而，Mockito版本1的内部工作还没有为这种结构做好准备。如果我们使用Mockito 2之前的版本运行这段代码，我们会得到这个描述得很好的错误：

```shell
org.mockito.exceptions.base.MockitoException:
Cannot call a real method on java interface. The interface does not have any implementation!
Calling real methods is only possible when mocking concrete classes.
```

Mockito告诉我们它不能在接口上调用真正的方法，因为这个操作在Java 8之前是不可想象的。

好消息是，只需更改我们正在使用的Mockito版本，我们就可以消除此错误。例如，使用Maven，我们可以使用版本2.7.5(最新的Mockito版本可以在[这里](https://search.maven.org/search?q=a:mockito-core)找到)：

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>2.7.5</version>
    <scope>test</scope>
</dependency>
```

无需对代码进行任何更改。当我们再次运行测试时，错误将不再发生。

## 3. 返回Optional和Stream的默认值

[Optional](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Optional.html)和[Stream](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Stream.html)是Java 8新增的其他功能。这两个类之间的一个相似之处是它们都有一种特殊类型的值来表示一个空对象。这个空对象可以更容易地避免迄今为止无处不在的[NullPointerException](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/NullPointerException.html)。

### 3.1 Optional示例

假设我们有一个UnemploymentServiceImpl类，它注入我们上一小节中描述的JobService，并有一个调用JobService#findCurrentJobPosition()的方法：

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

现在，假设我们要创建一个测试来检查当一个人目前没有工作职位时，他们是否有权获得失业补助。

在这种情况下，我们将强制findCurrentJobPosition()返回一个空的Optional。**在Mockito版本2之前**，我们需要mock对该方法的调用：

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

        when(jobService.findCurrentJobPosition(any(Person.class)))
              .thenReturn(Optional.empty());

        assertTrue(unemploymentService.personIsEntitledToUnemploymentSupport(person));
    }
}
```

第14行的when(...).thenReturn(...)代码段是必需的，因为Mockito对mock对象的任何方法调用的默认返回值为null。版本2改变了这种行为。

由于我们在处理Optional时很少处理null值，因此**Mockito现在默认返回一个空的Optional**。这与调用[Optional.empty()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Optional.html#empty())的返回值完全相同。

因此，**当使用Mockito版本2时**，我们可以去掉when(...).thenReturn(...)代码段，我们的测试仍然会成功：

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

### 3.2 Stream示例

当我们mock返回Stream的方法时，也会发生相同的行为。

让我们向JobService接口添加一个新方法，该方法返回一个Stream，表示一个人曾经工作过的所有工作职位：

```java
public interface JobService {
    Stream<JobPosition> listJobs(Person person);
}
```

此方法用于另一个新方法，该方法将查询一个人是否曾经从事过与给定searchString匹配的工作：

```java
public class UnemploymentServiceImpl implements UnemploymentService {

    @Override
    public Optional<JobPosition> searchJob(Person person, String searchString) {
        Stream<JobPosition> stream = jobService.listJobs(person);
        return stream
              .filter((j) -> j.getTitle().contains(searchString))
              .findFirst();
    }
}
```

因此，假设我们想要正确测试searchJob()的实现，而不必担心编写listJobs()并假设我们想要测试此人尚未从事任何工作时的场景。在这种情况下，我们希望listJobs()返回一个空的Stream。

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

**如果我们升级到版本2，我们可以删除when(...).thenReturn(...)调用，因为现在Mockito将默认在mock方法上返回一个空Stream**：

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

使用Java 8的lambda表达式，我们可以使语句更加紧凑且更易于阅读。使用Mockito时，Lambda表达式带来的简单性的两个很好的例子是[ArgumentMatchers](https://static.javadoc.io/org.mockito/mockito-core/2.7.10/org/mockito/ArgumentMatcher.html)和自定义[Answers](https://static.javadoc.io/org.mockito/mockito-core/2.7.10/org/mockito/stubbing/Answer.html)。

### 4.1 Lambda和ArgumentMatcher的组合

在Java 8之前，我们需要创建一个实现ArgumentMatcher的类，并在matches()方法中编写我们的自定义规则。

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

将lambda表达式与Mockito的Answer结合使用时也可以达到相同的效果。

例如，如果我们想mock对listJobs()方法的调用，以便在Person的名称为“Peter”时返回一个包含单个JobPosition的Stream，否则返回一个空Stream，那么我们必须创建一个实现Answer接口的类(内部类)。

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

在本文中，我们介绍了如何结合使用新的Java 8和Mockito版本2功能来编写更干净、更简单和更短的代码。如果你不熟悉我们在这里看到的一些Java 8特性，请查看我们的一些文章：

-   [Lambda表达式和函数接口：技巧和最佳实践](https://www.baeldung.com/java-8-lambda-expressions-tips)
-   [Java 8的新特性](https://www.baeldung.com/java-8-new-features)
-   [Java 8 Optional指南](https://www.baeldung.com/java-optional)
-   [Java 8 Stream简介](https://www.baeldung.com/java-8-streams-introduction)