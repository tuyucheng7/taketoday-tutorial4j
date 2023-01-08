## 1. 概述

在本教程中，我们将了解如何使用 Jackson 和 Gson 将不同的 JSON 字段映射到单个Java字段。

## 2.Maven依赖

为了使用[Jackson](https://search.maven.org/search?q=a:jackson-databind AND g:com.fasterxml.jackson.core)和[Gson](https://search.maven.org/search?q=a:gson AND g:com.google.code.gson)库，我们需要将以下依赖项添加到我们的 POM 中：

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.5</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.0</version>
    <scope>test</scope>
</dependency>
```

## 3. 示例 JSON

假设我们想要将不同位置的天气详细信息输入到我们的Java应用程序中。我们发现有几个网站将天气数据发布为 JSON 文档。但是，它们使用的格式略有不同：

```plaintext
{
    "location": "London",
    "temp": 15,
    "weather": "Cloudy"
}
```

和：

```plaintext
{
    "place": "Lisbon",
    "temperature": 35,
    "outlook": "Sunny"
}
```

我们想将这两种格式反序列化为同一个Java类，名为 Weather：

```java
public class Weather {
    private String location;
    private int temp;
    private String outlook;
}
```

那么让我们来看看如何使用 Jackson 和 Gson 库来实现这一点。

## 4.使用杰克逊

为此，我们将使用 Jackson 的@JsonProperty和@JsonAlias注解。这些将允许我们将多个 JSON 属性映射到同一个Java字段。

首先，我们将使用@JsonProperty注解，以便 Jackson 知道要映射的 JSON 字段的名称。@JsonProperty注解中的值用于反序列化和序列化。

然后我们可以使用@JsonAlias注解。结果，Jackson 将知道 JSON 文档中映射到Java字段的其他字段的名称。@JsonAlias注解中的值仅用于反序列化：

```java
@JsonProperty("location")
@JsonAlias("place")
private String location;
@JsonProperty("temp")
@JsonAlias("temperature")
private int temp;

@JsonProperty("outlook")
@JsonAlias("weather")
private String outlook;

```

现在我们已经添加了注解，让我们使用 Jackson 的ObjectMapper通过Weather类创建Java对象：

```java
@Test
public void givenTwoJsonFormats_whenDeserialized_thenWeatherObjectsCreated() throws Exception {

    ObjectMapper mapper = new ObjectMapper();

    Weather weather = mapper.readValue("{n"  
      + "  "location": "London",n" 
      + "  "temp": 15,n" 
      + "  "weather": "Cloudy"n" 
      + "}", Weather.class);

    assertEquals("London", weather.getLocation());
    assertEquals("Cloudy", weather.getOutlook());
    assertEquals(15, weather.getTemp());

    weather = mapper.readValue("{n" 
      + "  "place": "Lisbon",n" 
      + "  "temperature": 35,n"
      + "  "outlook": "Sunny"n"
      + "}", Weather.class);

    assertEquals("Lisbon", weather.getLocation());
    assertEquals("Sunny", weather.getOutlook());
    assertEquals(35, weather.getTemp());
}
```

## 5. 使用 Gson

现在，让我们对 Gson 进行同样的尝试。我们需要在@SerializedName注解中使用值和 备用参数。

第一个将用作默认值，而第二个将用于指示我们要映射的 JSON 字段的备用名称：

```java
@SerializedName(value="location", alternate="place")
private String location;
@SerializedName(value="temp", alternate="temperature")
private int temp;

@SerializedName(value="outlook", alternate="weather")
private String outlook;

```

现在我们已经添加了注解，让我们测试我们的示例：

```java
@Test
public void givenTwoJsonFormats_whenDeserialized_thenWeatherObjectsCreated() throws Exception {
        
    Gson gson = new GsonBuilder().create();
    Weather weather = gson.fromJson("{n" 
      + "  "location": "London",n" 
      + "  "temp": 15,n" 
      + "  "weather": "Cloudy"n" 
      + "}", Weather.class);
        
    assertEquals("London", weather.getLocation());
    assertEquals("Cloudy", weather.getOutlook());
    assertEquals(15, weather.getTemp());
        
    weather = gson.fromJson("{n"
      + "  "place": "Lisbon",n"
      + "  "temperature": 35,n"
      + "  "outlook": "Sunny"n"
      + "}", Weather.class);
       
    assertEquals("Lisbon", weather.getLocation());
    assertEquals("Sunny", weather.getOutlook());
    assertEquals(35, weather.getTemp());
        
}
```

## 六. 总结

我们看到，通过使用 Jackson 的@JsonAlias或 Gson 的备用参数，我们可以轻松地将不同的 JSON 格式转换为同一个Java对象。