## 1. 简介

在本文中，我们将比较用于将 JSON 数据序列化和反序列化为Java对象的[Gson](https://code.google.com/p/google-gson)和[Jackson](https://github.com/FasterXML/jackson) API，反之亦然。

Gson 和 Jackson 是为Java提供 JSON 数据绑定支持的完整库。每个都是积极开发的开源项目，可以处理复杂的数据类型并支持Java泛型。

在大多数情况下，两个库都可以在不修改实体类的情况下反序列化为实体，这在开发人员无权访问实体源代码的情况下很重要。

## 2.Gson Maven 依赖

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>${gson.version}</version>
</dependency>
```

[你可以在此处](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.code.gson" AND a%3A"gson")获取最新版本的 Gson 。

## 3.Gson序列化

序列化将Java对象转换为 JSON 输出。考虑以下实体：

```java
public class ActorGson {
    private String imdbId;
    private Date dateOfBirth;
    private List<String> filmography;
    
    // getters and setters, default constructor and field constructor omitted
}

public class Movie {
    private String imdbId;
    private String director;
    private List<ActorGson> actors;
    
    // getters and setters, default constructor and field constructor omitted
}
```

### 3.1. 简单序列化

让我们从一个Java到 JSON 序列化的例子开始：

```java
SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

ActorGson rudyYoungblood = new ActorGson(
  "nm2199632",
  sdf.parse("21-09-1982"), 
  Arrays.asList("Apocalypto",
  "Beatdown", "Wind Walkers")
);
Movie movie = new Movie(
  "tt0472043", 
  "Mel Gibson",
  Arrays.asList(rudyYoungblood));

String serializedMovie = new Gson().toJson(movie);
```

这将导致：

```javascript
{
    "imdbId": "tt0472043",
    "director": "Mel Gibson",
    "actors": [{
        "imdbId": "nm2199632",
        "dateOfBirth": "Sep 21, 1982 12:00:00 AM",
        "filmography": ["Apocalypto", "Beatdown", "Wind Walkers"]
    }]
}
```

默认：

-   所有属性都被序列化，因为它们没有空值
-   dateOfBirth字段已使用默认的 Gson 日期模式进行翻译
-   输出未格式化且 JSON 属性名称对应于Java实体

### 3.2. 自定义序列化

使用自定义序列化程序允许我们修改标准行为。我们可以使用 HTML 引入输出格式化程序、处理空值、从输出中排除属性或添加新输出。

ActorGsonSerializer修改ActorGson元素的 JSON 代码的生成：

```java
public class ActorGsonSerializer implements JsonSerializer<ActorGson> {
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
     
    @Override
    public JsonElement serialize(ActorGson actor, Type type,
        JsonSerializationContext jsonSerializationContext) {
        
        JsonObject actorJsonObj = new JsonObject();
        
        actorJsonObj.addProperty("<strong>IMDB Code</strong>", actor.getImdbId());
        
        actorJsonObj.addProperty("<strong>Date Of Birth</strong>", 
          actor.getDateOfBirth() != null ? 
          sdf.format(actor.getDateOfBirth()) : null);
        
        actorJsonObj.addProperty("<strong>N° Film:</strong> ",  
          actor.getFilmography()  != null ?  
          actor.getFilmography().size() : null);
       
        actorJsonObj.addProperty("filmography", actor.getFilmography() != null ? 
          convertFilmography(actor.getFilmography()) : null);
        
        return actorJsonObj;
    }
 
    private String convertFilmography(List<String> filmography) {
        return filmography.stream()
          .collect(Collectors.joining("-"));
    }
}
```

为了排除director属性，@Expose注解用于我们要考虑的属性：

```java
public class MovieWithNullValue {
    
    @Expose
    private String imdbId;
    private String director;
    
    @Expose
    private List<ActorGson> actors;
}
```

现在我们可以使用GsonBuilder类继续创建 Gson 对象：

```java
Gson gson = new GsonBuilder()
  .setPrettyPrinting()
  .excludeFieldsWithoutExposeAnnotation()
  .serializeNulls()
  .disableHtmlEscaping()
  .registerTypeAdapter(ActorGson.class, new ActorGsonSerializer())
  .create();
 
SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
 
ActorGson rudyYoungblood = new ActorGson("nm2199632",
  sdf.parse("21-09-1982"), Arrays.asList("Apocalypto","Beatdown", "Wind Walkers"));

MovieWithNullValue movieWithNullValue = new MovieWithNullValue(null,
  "Mel Gibson", Arrays.asList(rudyYoungblood));
 
String serializedMovie = gson.toJson(movieWithNullValue);
```

结果如下：

```java
{
  "imdbId": null,
  "actors": [
    {
      "<strong>IMDB Code</strong>": "nm2199632",
      "<strong>Date Of Birth</strong>": "21-09-1982",
      "<strong>N° Film:</strong> ": 3,
      "filmography": "Apocalypto-Beatdown-Wind Walkers"
    }
  ]
}
```

请注意：

-   输出被格式化
-   一些属性名称已更改并包含 HTML
-   包含空值，并省略director字段
-   日期现在采用dd-MM-yyyy格式
-   出现了一个新属性——N° Film
-   filmography 是一个格式化的属性，而不是默认的 JSON 列表

## 4.Gson反序列化

### 4.1. 简单的反序列化

反序列化将 JSON 输入转换为Java对象。为了说明输出，我们在两个实体类中实现了toString()方法：

```java
public class Movie {
    @Override
    public String toString() {
      return "Movie [imdbId=" + imdbId + ", director=" + director + ",actors=" + actors + "]";
    }
    ...
}

public class ActorGson {
    @Override
    public String toString() {
        return "ActorGson [imdbId=" + imdbId + ", dateOfBirth=" + dateOfBirth +
          ",filmography=" + filmography + "]";
    }
    ...
}
```

然后我们利用序列化的 JSON 并通过标准 Gson 反序列化运行它：

```java
String jsonInput = "{"imdbId":"tt0472043","actors":" +
  "[{"imdbId":"nm2199632","dateOfBirth":"1982-09-21T12:00:00+01:00"," +
  ""filmography":["Apocalypto","Beatdown","Wind Walkers"]}]}";
        
Movie outputMovie = new Gson().fromJson(jsonInput, Movie.class);
outputMovie.toString();
```

输出是我们的实体，填充了来自 JSON 输入的数据：

```java
Movie [imdbId=tt0472043, director=null, actors=[ActorGson 
  [imdbId=nm2199632, dateOfBirth=Tue Sep 21 04:00:00 PDT 1982, 
  filmography=[Apocalypto, Beatdown, Wind Walkers]]]]
```

与简单序列化程序的情况一样：

-   JSON 输入名称必须与Java实体名称相对应，否则它们将设置为 null。
-   dateOfBirth字段使用默认的 Gson 日期模式进行翻译，忽略了时区。

### 4.2. 自定义反序列化

使用自定义反序列化器允许我们修改标准反序列化器行为。在这种情况下，我们希望日期反映dateOfBirth的正确时区。我们在ActorGson实体上使用自定义ActorGsonDeserializer来实现这一点：

```java
public class ActorGsonDeserializer implements JsonDeserializer<ActorGson> {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public ActorGson deserialize(JsonElement json, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        
        JsonObject jsonObject = json.getAsJsonObject();

        JsonElement jsonImdbId = jsonObject.get("imdbId");
        JsonElement jsonDateOfBirth = jsonObject.get("dateOfBirth");
        JsonArray jsonFilmography = jsonObject.getAsJsonArray("filmography");

        ArrayList<String> filmList = new ArrayList<String>();
        if (jsonFilmography != null) {
            for (int i = 0; i < jsonFilmography.size(); i++) {
                filmList.add(jsonFilmography.get(i).getAsString());
            }
        }

    ActorGson actorGson = new ActorGson(jsonImdbId.getAsString(),
      sdf.parse(jsonDateOfBirth.getAsString()), filmList);
        return actorGson;
    }
}
```

我们使用了一个SimpleDateFormat解析器来解析输入日期，并考虑时区。

请注意，我们本可以决定只为 Date 编写自定义反序列化程序，但ActorGsonDeserializer提供了反序列化过程的更详细视图。

另请注意，Gson 方法不需要修改ActorGson实体，这是理想的，因为我们可能并不总是能够访问输入实体。我们在这里使用自定义反序列化器：

```java
String jsonInput = "{"imdbId":"tt0472043","actors":"
  + "[{"imdbId":"nm2199632","dateOfBirth":"1982-09-21T12:00:00+01:00",
  + "filmography":["Apocalypto","Beatdown","Wind Walkers"]}]}";

Gson gson = new GsonBuilder()
  .registerTypeAdapter(ActorGson.class,new ActorGsonDeserializer())
  .create();

Movie outputMovie = gson.fromJson(jsonInput, Movie.class);
outputMovie.toString();
```

输出类似于简单的反序列化器结果，除了日期使用正确的时区：

```java
Movie [imdbId=tt0472043, director=null, actors=[ActorGson
  [imdbId=nm2199632, dateOfBirth=Tue Sep 21 12:00:00 PDT 1982, 
  filmography=[Apocalypto, Beatdown, Wind Walkers]]]]
```

## 5. Jackson Maven 依赖

```xml
<dependency> 
    <groupId>com.fasterxml.jackson.core</groupId> 
    <artifactId>jackson-databind</artifactId>   
    <version>${jackson.version}</version> 
</dependency>
```

[你可以在此处](https://search.maven.org/classic/#search|ga|1|g%3A"com.fasterxml.jackson.core" AND a%3A"jackson-databind")获取最新版本的 Jackson 。

## 6.杰克逊连载

### 6.1. 简单序列化

在这里，我们将使用 Jackson 使用以下实体获取与 Gson 相同的序列化内容。请注意，实体的 getters/setters 必须是公开的：

```java
public class ActorJackson {
    private String imdbId;
    private Date dateOfBirth;
    private List<String> filmography;
    
    // required getters and setters, default constructor 
    // and field constructor details omitted
}

public class Movie {
    private String imdbId;
    private String director;
    private List<ActorJackson> actors;
    
    // required getters and setters, default constructor 
    // and field constructor details omitted
}
SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); 
ActorJackson rudyYoungblood = new ActorJackson("nm2199632",sdf.parse("21-09-1982"),
  Arrays.asList("Apocalypto","Beatdown","Wind Walkers") ); 
Movie movie = new Movie("tt0472043","Mel Gibson", Arrays.asList(rudyYoungblood)); 
ObjectMapper mapper = new ObjectMapper(); 
String jsonResult = mapper.writeValueAsString(movie);
```

输出如下：

```plaintext
{"imdbId":"tt0472043","director":"Mel Gibson","actors":
[{"imdbId":"nm2199632","dateOfBirth":401439600000,
"filmography":["Apocalypto","Beatdown","Wind Walkers"]}]}
```

一些有趣的笔记：

-   ObjectMapper是我们的 Jackson 序列化器/反序列化器
-   输出 JSON 未格式化
-   默认情况下，Java Date 被转换为long值

### 6.2. 自定义序列化

我们可以通过为实体扩展 StdSerializer 来为ActorJackson元素生成创建 Jackson 序列化程序。再次注意实体 getters/setters 必须是公开的：

```java
public class ActorJacksonSerializer extends StdSerializer<ActorJackson> {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public ActorJacksonSerializer(Class t) {
        super(t);
    }

    @Override
    public void serialize(ActorJackson actor, JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("imdbId", actor.getImdbId());
        jsonGenerator.writeObjectField("dateOfBirth",
          actor.getDateOfBirth() != null ?
          sdf.format(actor.getDateOfBirth()) : null);
    
        jsonGenerator.writeNumberField("N° Film: ", 
          actor.getFilmography() != null ? actor.getFilmography().size() : null);
    jsonGenerator.writeStringField("filmography", actor.getFilmography()
          .stream().collect(Collectors.joining("-")));

        jsonGenerator.writeEndObject();
    }
}
```

我们创建一个 Movie 实体以允许忽略director字段：

```java
public class MovieWithNullValue {
    
    private String imdbId;
    
    @JsonIgnore
    private String director;
    
    private List<ActorJackson> actors;
    
    // required getters and setters, default constructor
    // and field constructor details omitted
}
```

现在我们可以继续创建和设置自定义ObjectMapper ：

```java
SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

ActorJackson rudyYoungblood = new ActorJackson(
  "nm2199632", 
  sdf.parse("21-09-1982"), 
  Arrays.asList("Apocalypto", "Beatdown","Wind Walkers"));
MovieWithNullValue movieWithNullValue = 
  new MovieWithNullValue(null,"Mel Gibson", Arrays.asList(rudyYoungblood));

SimpleModule module = new SimpleModule();
module.addSerializer(new ActorJacksonSerializer(ActorJackson.class));
ObjectMapper mapper = new ObjectMapper();
String jsonResult = mapper.registerModule(module)
  .writer(new DefaultPrettyPrinter())
  .writeValueAsString(movieWithNullValue);
```

输出是格式化的 JSON，它处理空值、格式化日期、排除导演字段并显示N°的新输出：

```java
{
  "actors" : [ {
    "imdbId" : "nm2199632",
    "dateOfBirth" : "21-09-1982",
    "N° Film: " : 3,
    "filmography" : "Apocalypto-Beatdown-Wind Walkers"
  } ],
  "imdbID" : null
}
```

## 7.杰克逊反序列化

### 7.1. 简单的反序列化

为了说明输出，我们在两个 Jackson 实体类中实现了toString()方法：

```java
public class Movie {
    @Override
    public String toString() {
        return "Movie [imdbId=" + imdbId + ", director=" + director
          + ", actors=" + actors + "]";
    }
    ...
}

public class ActorJackson {
    @Override
    public String toString() {
        return "ActorJackson [imdbId=" + imdbId + ", dateOfBirth=" + dateOfBirth
          + ", filmography=" + filmography + "]";
    }
    ...
}
```

然后我们利用序列化的 JSON 并通过 Jackson 反序列化运行它：

```java
String jsonInput = "{"imdbId":"tt0472043","actors":
  [{"imdbId":"nm2199632","dateOfBirth":"1982-09-21T12:00:00+01:00",
  "filmography":["Apocalypto","Beatdown","Wind Walkers"]}]}";
ObjectMapper mapper = new ObjectMapper();
Movie movie = mapper.readValue(jsonInput, Movie.class);
```

输出是我们的实体，填充了来自 JSON 输入的数据：

```java
Movie [imdbId=tt0472043, director=null, actors=[ActorJackson 
  [imdbId=nm2199632, dateOfBirth=Tue Sep 21 04:00:00 PDT 1982, 
  filmography=[Apocalypto, Beatdown, Wind Walkers]]]]
```

与简单序列化程序的情况一样：

-   JSON 输入名称必须与Java实体名称相对应，否则它们将设置为null，
-   dateOfBirth字段使用默认的 Jackson 日期模式进行翻译，忽略了时区。

### 7.2. 自定义反序列化

使用自定义反序列化器允许我们修改标准反序列化器行为。

在这种情况下，我们希望日期反映出生日期的正确时区，因此我们将 DateFormatter 添加到 Jackson ObjectMapper中：

```java
String jsonInput = "{"imdbId":"tt0472043","director":"Mel Gibson",
  "actors":[{"imdbId":"nm2199632","dateOfBirth":"1982-09-21T12:00:00+01:00",
  "filmography":["Apocalypto","Beatdown","Wind Walkers"]}]}";

ObjectMapper mapper = new ObjectMapper();
DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
mapper.setDateFormat(df);
        
Movie movie = mapper.readValue(jsonInput, Movie.class);
movie.toString();
```

输出反映了正确的时区和日期：

```java
Movie [imdbId=tt0472043, director=Mel Gibson, actors=[ActorJackson 
  [imdbId=nm2199632, dateOfBirth=Tue Sep 21 12:00:00 PDT 1982, 
  filmography=[Apocalypto, Beatdown, Wind Walkers]]]]
```

这个解决方案简洁明了。

或者，我们可以为ActorJackson类创建一个自定义反序列化器，用我们的ObjectMapper注册这个模块，并使用ActorJackson实体上的@JsonDeserialize注解反序列化日期。

该方法的缺点是需要修改实体，这对于我们无法访问输入实体类的情况可能并不理想。

## 八. 总结

Gson 和 Jackson 都是序列化/反序列化 JSON 数据的好选择，使用简单且文档齐全。

Gson的优点：

-   简单情况下toJson / fromJson的简单性
-   对于反序列化，不需要访问Java实体

杰克逊的优势：

-   内置于所有 JAX-RS(Jersey、Apache CXF、RESTEasy、Restlet)和 Spring 框架中
-   广泛的注解支持