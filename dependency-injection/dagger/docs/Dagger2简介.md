## 1. 简介

在本教程中，我们将了解 Dagger 2——一种快速且轻量级的依赖注入框架。

该框架可用于Java和 Android，但源自编译时注入的高性能使其成为后者的领先解决方案。

## 2.依赖注入

提醒一下，[依赖注入](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)是更通用的控制反转原则的具体应用，其中程序流由程序本身控制。

它是通过一个外部组件实现的，该组件提供其他对象所需的对象(或依赖项)的实例。

并且不同的框架以不同的方式实现依赖注入。特别是，这些差异中最显着的差异之一是注入是发生在运行时还是编译时。

运行时 DI 通常基于反射，它使用起来更简单，但运行时速度较慢。[运行时 DI 框架](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)的一个示例是 Spring 。

另一方面，编译时 DI 基于代码生成。这意味着所有重量级操作都在编译期间执行。编译时 DI 增加了复杂性，但通常执行速度更快。

Dagger 2 属于此类。

## 3. Maven/Gradle配置

为了在项目中使用 Dagger，我们需要将 [dagger](https://search.maven.org/classic/#artifactdetails|com.google.dagger|dagger|2.16|jar)[依赖项](https://search.maven.org/classic/#artifactdetails|com.google.dagger|dagger|2.16|jar)[添加](https://search.maven.org/classic/#artifactdetails|com.google.dagger|dagger|2.16|jar) 到我们的 pom.xml中：

```xml
<dependency>
    <groupId>com.google.dagger</groupId>
    <artifactId>dagger</artifactId>
    <version>2.16</version>
</dependency>
```

此外，我们还需要包括 用于将带注解的类转换为用于注入的代码的[Dagger 编译器：](https://search.maven.org/classic/#artifactdetails|com.google.dagger|dagger-compiler|2.16|jar)

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.6.1</version>
    <configuration>
         <annotationProcessorPaths>
              <path>
                  <groupId>com.google.dagger</groupId>
                  <artifactId>dagger-compiler</artifactId>
                  <version>2.16</version>
              </path>
         </annotationProcessorPaths>
    </configuration>
</plugin>
```

使用此配置，Maven 会将生成的代码输出到 target/generated-sources/annotations中。

出于这个原因， 如果我们想使用它的任何代码完成功能，我们可能需要进一步配置我们的 IDE 。一些 IDE 直接支持注解处理器，而其他 IDE 可能需要我们将此目录添加到构建路径。

或者，如果我们将 Android 与 Gradle 一起使用，我们可以包括两个依赖项：

```groovy
compile 'com.google.dagger:dagger:2.16'
annotationProcessor 'com.google.dagger:dagger-compiler:2.16'
```

现在我们的项目中有了 Dagger，让我们创建一个示例应用程序看看它是如何工作的。

## 4.实施

对于我们的示例，我们将尝试通过注入其组件来制造汽车。

现在，Dagger 在许多地方使用标准的 JSR-330 注解 ，其中之一是 @Inject。

我们可以将注解添加到字段或构造函数。但是，由于Dagger 不支持对私有字段的注入，我们将使用构造函数注入来保留封装：

```java
public class Car {

    private Engine engine;
    private Brand brand;

    @Inject
    public Car(Engine engine, Brand brand) {
        this.engine = engine;
        this.brand = brand;
    }

    // getters and setters

}
```

接下来，我们将实现执行注入的代码。更具体地说，我们将创建：

-   一个模块，它是一个提供或构建对象依赖项的类，以及
-   一个组件，它是用于生成注入器的接口

复杂的项目可能包含多个模块和组件，但由于我们处理的是一个非常基本的程序，所以每一个都足够了。

让我们看看如何实现它们。

### 4.1. 模块

要创建模块，我们需要使用@Module注解来注解类。此注解表示该类可以使依赖项对容器可用：

```java
@Module
public class VehiclesModule {
}
```

然后， 我们需要 在构造依赖项的方法上添加@Provides注解：

```java
@Module
public class VehiclesModule {
    @Provides
    public Engine provideEngine() {
        return new Engine();
    }

    @Provides
    @Singleton
    public Brand provideBrand() { 
        return new Brand("Baeldung"); 
    }
}
```

另外请注意，我们可以配置给定依赖项的范围。在这种情况下，我们将单例范围赋予我们的 Brand实例，以便所有 car 实例共享相同的 brand 对象。

### 4.2. 零件

继续，我们将创建我们的组件接口。 这是将生成 Car 实例的类，注入VehiclesModule提供的依赖项。

简单地说，我们需要一个返回Car的方法签名，我们需要用@Component注解来标记这个类：

```java
@Singleton
@Component(modules = VehiclesModule.class)
public interface VehiclesComponent {
    Car buildCar();
}
```

请注意我们如何将模块类作为参数传递给@Component注解。 如果我们不这样做，Dagger 将不知道如何构建汽车的依赖项。

此外，由于我们的模块提供了一个单例对象，我们必须为我们的组件提供相同的范围，因为Dagger 不允许未定义范围的组件引用范围绑定。

### 4.3. 客户代码

最后，我们可以运行 mvn compile以触发注解处理器并生成注入器代码。

之后，我们将找到与接口同名的组件实现，只是以“ Dagger ”为前缀：

```java
@Test
public void givenGeneratedComponent_whenBuildingCar_thenDependenciesInjected() {
    VehiclesComponent component = DaggerVehiclesComponent.create();

    Car carOne = component.buildCar();
    Car carTwo = component.buildCar();

    Assert.assertNotNull(carOne);
    Assert.assertNotNull(carTwo);
    Assert.assertNotNull(carOne.getEngine());
    Assert.assertNotNull(carTwo.getEngine());
    Assert.assertNotNull(carOne.getBrand());
    Assert.assertNotNull(carTwo.getBrand());
    Assert.assertNotEquals(carOne.getEngine(), carTwo.getEngine());
    Assert.assertEquals(carOne.getBrand(), carTwo.getBrand());
}
```

## 5. 春天类比

熟悉 Spring 的人可能已经注意到这两个框架之间的一些相似之处。

Dagger 的@Module注解使容器以与任何 Spring 的构造型注解(例如， @ Service 、 @ Controller …)非常相似的方式识别一个类。同样， @Provides和@Component 几乎分别相当于Spring 的@Bean和@Lookup。

Spring 也有它的@Scope注解，与@Singleton相关，尽管这里已经注意到另一个区别，即 Spring 默认采用单例作用域，而 Dagger 默认使用 Spring 开发人员可能称为原型作用域的东西，每次调用提供者方法依赖是必需的。

## 六. 总结

在本文中，我们通过一个基本示例介绍了如何设置和使用 Dagger 2。我们还考虑了运行时和编译时注入之间的差异。