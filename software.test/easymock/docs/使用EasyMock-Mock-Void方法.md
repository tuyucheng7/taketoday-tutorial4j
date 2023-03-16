## 1. 概述

mock框架用于mock与依赖项的交互，以便单独测试我们的类。通常，我们mock依赖项以返回各种可能的值。这样，我们可以确保我们的类能够处理这些值中的每一个。

但是，有时我们可能不得不mock不返回任何内容的依赖方法。

在本教程中，**我们将了解何时以及如何使用EasyMock mock void方法**。

## 2. Maven依赖

首先，让我们将[EasyMock依赖项](https://central.sonatype.com/artifact/org.easymock/easymock/5.1.0)添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.easymock</groupId>
    <artifactId>easymock</artifactId>
    <version>4.0.2</version>
    <scope>test</scope>
</dependency>
```

## 3. 何时Mock Void方法

当我们测试具有依赖项的类时，我们通常希望涵盖依赖项返回的所有值。但有时，依赖方法不返回值。那么，**如果方法什么都不返回，我们为什么要mock一个void方法呢**？

> 原因是：**即使void方法不返回值，它们也可能具有副作用**。这方面的一个例子是Session.save()方法。当我们保存一个新实体时，save()方法会生成一个id并将其设置在传递给它的实体上。

出于这个原因，我们必须mock void方法来模拟各种处理结果。

当测试void方法抛出的异常时，mock也可能会派上用场。

## 4. 如何Mock Void方法

现在，让我们看看如何使用EasyMock mock void方法。

假设，我们必须mock WeatherService类的populateTemperature方法，该方法接收Location作为参数并设置它的minimumTemperature和maximumTemperature属性：

```java
public interface WeatherService {
    void populateTemperature(Location location) throws ServiceUnavailableException;
}
```

```java
public class Location {
    private String name;
    private BigDecimal minimumTemperature;
    private BigDecimal maximumTemperature;

    public Location(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // other setters and getters method for minimumTemperature、maximumTemperature.
}
```

### 4.1 创建Mock对象

首先我们需要为WeatherService创建一个mock：

```java
@Mock
private WeatherService mockWeatherService;
```

在这里，我们使用EasyMock的@Mock注解创建mock。但是，我们也可以使用EasyMock.mock()方法来做到这一点。

接下来，我们将通过调用populateTemperature()来记录与mock的预期交互：

```java
mockWeatherService.populateTemperature(EasyMock.anyObject(Location.class));
```

此时，如果我们不想模拟这个方法的处理，这个调用本身就足以mock这个方法。

### 4.2 抛出异常

首先，让我们来看一下我们要测试我们的类是否可以处理**void方法抛出的异常的情况**。为此，我们必须以抛出异常的方式mock该方法。

在我们的示例中，该方法抛出ServiceUnavailableException：

```java
EasyMock.expectLastCall().andThrow(new ServiceUnavailableException());
```

如上所示，这涉及简单地调用andThrow(Throwable)方法。

### 4.3 Mock方法行为

如前所述，我们有时可能需要**模拟void方法的行为**。

在我们的例子中，这将涉及填充传递Location对象的minimumTemperature和maximumTemperature属性：

```java
private static final int MAX_TEMP = 90;

EasyMock.expectLastCall()
    .andAnswer(() -> {
        Location passedLocation = (Location) EasyMock.getCurrentArguments()[0];
        passedLocation.setMaximumTemperature(new BigDecimal(MAX_TEMP));
        passedLocation.setMinimumTemperature(new BigDecimal(MAX_TEMP - 10));
        return null;
    });
```

在这里，我们使用了andAnswer(IAnswer)方法来定义populateTemperature()方法在调用时的行为。然后，我们使用EasyMock.getCurrentArguments()方法(返回传递给mock方法的参数)来修改传递的Location。

请注意，**我们最后返回了null，这是因为我们mock的是一个void方法**。

还值得注意的是，这种方法不仅限于mock void方法。我们也可以将它用于返回值的方法。当我们想要mock方法以根据传递的参数返回值时，它会派上用场。

### 4.4 重播Mock方法

最后，我们将使用EasyMock.replay()方法将mock更改为“重播(replay)”模式，以便在调用时可以重播录制的动作：

```java
EasyMock.replay(mockWeatherService);
```

因此，当我们调用测试方法时，应该执行定义的自定义行为。

## 5. 总结

在本教程中，我们介绍了如何使用EasyMock mock void方法。