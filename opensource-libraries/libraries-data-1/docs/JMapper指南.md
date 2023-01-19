## 1. 概述

 在本教程中 ，我们将探索[JMapper——](https://jmapper-framework.github.io/jmapper-core/)一种快速且易于使用的映射框架。

我们将讨论配置 JMapper 的不同方法、如何执行自定义转换以及关系映射。

## 2.Maven配置

首先，我们需要将[JMapper 依赖](https://search.maven.org/classic/#search|ga|1|jmapper-core)项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.googlecode.jmapper-framework</groupId>
    <artifactId>jmapper-core</artifactId>
    <version>1.6.0.1</version>
</dependency>
```

## 3.源和目标模型

在开始配置之前，让我们看一下我们将在整个教程中使用的简单 bean。

首先，这是我们的源 bean——一个基本的User：

```java
public class User {
    private long id;    
    private String email;
    private LocalDate birthDate;
}
```

我们的目标 bean，UserDto：

```java
public class UserDto {
    private long id;
    private String username;
}
```

我们将使用该库将属性从我们的源 bean User映射到我们的目标 bean UserDto。

配置JMapper的方式有三种：使用API、注解和XML配置。

在以下部分中，我们将逐一介绍这些内容。

## 4. 使用 API

让我们看看如何使用 API配置JMapper 。

在这里，我们不需要向我们的源类和目标类添加任何配置。相反，所有配置都可以使用JMapperAPI完成，这使其成为最灵活的配置方法：

```java
@Test
public void givenUser_whenUseApi_thenConverted(){
    JMapperAPI jmapperApi = new JMapperAPI() 
      .add(mappedClass(UserDto.class)
        .add(attribute("id").value("id"))
        .add(attribute("username").value("email")));

    JMapper<UserDto, User> userMapper = new JMapper<>
      (UserDto.class, User.class, jmapperApi);
    User user = new User(1L,"john@test.com", LocalDate.of(1980,8,20));
    UserDto result = userMapper.getDestination(user);

    assertEquals(user.getId(), result.getId());
    assertEquals(user.getEmail(), result.getUsername());
}
```

在这里，我们使用mappedClass()方法来定义我们的映射类UserDto。然后，我们使用attribute()方法来定义每个属性及其映射值。

接下来，我们根据配置创建了一个JMapper对象，并使用它的getDestination()方法获取了UserDto结果。

## 5.使用注解

让我们看看如何使用 @JMap注解来配置我们的映射：

```java
public class UserDto {  
    @JMap
    private long id;

    @JMap("email")
    private String username;
}
```

下面是我们将如何使用我们的JMapper：

```java
@Test
public void givenUser_whenUseAnnotation_thenConverted(){
    JMapper<UserDto, User> userMapper = new JMapper<>(UserDto.class, User.class);
    User user = new User(1L,"john@test.com", LocalDate.of(1980,8,20));
    UserDto result = userMapper.getDestination(user);

    assertEquals(user.getId(), result.getId());
    assertEquals(user.getEmail(), result.getUsername());        
}
```

请注意，对于id 属性，我们不需要提供目标字段名称，因为它与源 bean 同名，而对于用户名字段，我们提到它对应于User类中的电子邮件字段。

然后，我们只需要将源和目标 bean 传递给我们的JMapper——不需要进一步的配置。

总的来说，这种方法很方便，因为它使用的代码量最少。

## 6. 使用 XML 配置

我们还可以使用 XML 配置来定义我们的映射。

这是user_jmapper.xml中的示例 XML 配置：

```xml
<jmapper>
  <class name="com.baeldung.jmapper.UserDto">
    <attribute name="id">
      <value name="id"/>
    </attribute>
    <attribute name="username">
      <value name="email"/>
    </attribute>
  </class>
</jmapper>
```

我们需要将 XML 配置传递给JMapper：

```java
@Test
public void givenUser_whenUseXml_thenConverted(){
    JMapper<UserDto, User> userMapper = new JMapper<>
      (UserDto.class, User.class,"user_jmapper.xml");
    User user = new User(1L,"john@test.com", LocalDate.of(1980,8,20));
    UserDto result = userMapper.getDestination(user);

    assertEquals(user.getId(), result.getId());
    assertEquals(user.getEmail(), result.getUsername());            
}
```

我们还可以将 XML 配置作为字符串直接传递给JMapper，而不是文件名。

## 7. 全球测绘

如果我们在源 bean 和目标 bean 中有多个同名字段，我们可以利用全局映射。

例如，如果我们有一个 UserDto1，它有两个字段， id 和 email：

```java
public class UserDto1 {  
    private long id;
    private String email;
    
    // standard constructor, getters, setters
}
```

全局映射将更易于使用，因为它们被映射到用户源 bean 中具有相同名称的字段。

### 7.1. 使用 API

对于JMapperAPI配置，我们将使用global()：

```java
@Test
public void givenUser_whenUseApiGlobal_thenConverted() {
    JMapperAPI jmapperApi = new JMapperAPI()
      .add(mappedClass(UserDto.class).add(global())) ;
    JMapper<UserDto1, User> userMapper1 = new JMapper<>
      (UserDto1.class, User.class,jmapperApi);
    User user = new User(1L,"john@test.com", LocalDate.of(1980,8,20));
    UserDto1 result = userMapper1.getDestination(user);

    assertEquals(user.getId(), result.getId());
    assertEquals(user.getEmail(), result.getEmail());
}
```

### 7.2. 使用注解

对于注解配置，我们将在类级别使用@JGlobalMap ：

```java
@JGlobalMap
public class UserDto1 {  
    private long id;
    private String email;
}
```

这是一个简单的测试：

```java
@Test
public void whenUseGlobalMapAnnotation_thenConverted(){
    JMapper<UserDto1, User> userMapper= new JMapper<>(
      UserDto1.class, User.class);
    User user = new User(
      1L,"john@test.com", LocalDate.of(1980,8,20));
    UserDto1 result = userMapper.getDestination(user);

    assertEquals(user.getId(), result.getId());
    assertEquals(user.getEmail(), result.getEmail());        
}
```

### 7.3. XML配置

对于 XML 配置，我们有<global/>元素：

```xml
<jmapper>
  <class name="com.baeldung.jmapper.UserDto1">
    <global/>
  </class>
</jmapper>
```

然后传递 XML 文件名：

```java
@Test
public void givenUser_whenUseXmlGlobal_thenConverted(){
    JMapper<UserDto1, User> userMapper = new JMapper<>
      (UserDto1.class, User.class,"user_jmapper1.xml");
    User user = new User(1L,"john@test.com", LocalDate.of(1980,8,20));
    UserDto1 result = userMapper.getDestination(user);

    assertEquals(user.getId(), result.getId());
    assertEquals(user.getEmail(), result.getEmail());            
}
```

## 8.自定义转换

现在，让我们看看如何使用JMapper应用自定义转换。

我们的UserDto中有一个新字段age，我们需要根据 User birthDate属性计算它： 

```java
public class UserDto {
    @JMap
    private long id;

    @JMap("email")
    private String username;
    
    @JMap("birthDate")
    private int age;

    @JMapConversion(from={"birthDate"}, to={"age"})
    public int conversion(LocalDate birthDate){
        return Period.between(birthDate, LocalDate.now())
          .getYears();
    }
}
```

因此，我们使用@JMapConversion来应用从 User 的出生日期到 UserDto 的 age属性的复杂转换。因此，当我们将User映射到UserDto时，将计算age字段：

```java
@Test
public void whenUseAnnotationExplicitConversion_thenConverted(){
    JMapper<UserDto, User> userMapper = new JMapper<>(
      UserDto.class, User.class);
    User user = new User(
      1L,"john@test.com", LocalDate.of(1980,8,20));
    UserDto result = userMapper.getDestination(user);

    assertEquals(user.getId(), result.getId());
    assertEquals(user.getEmail(), result.getUsername());     
    assertTrue(result.getAge() > 0);
}
```

## 9.关系映射

最后，我们将讨论关系映射。使用这种方法，我们需要每次使用一个目标类来定义我们的JMapper 。

如果我们已经知道目标类，我们可以为每个映射字段定义它们并使用RelationalJMapper。

在这个例子中，我们有一个源 bean User：

```java
public class User {
    private long id;    
    private String email;
}
```

还有两个目标 bean UserDto1：

```java
public class UserDto1 {  
    private long id;
    private String username;
}
```

和 UserDto2：

```java
public class UserDto2 {
    private long id;
    private String email;
}
```

让我们看看如何利用我们的RelationalJMapper。

### 9.1. 使用 API

对于我们的 API 配置，我们可以使用targetClasses()为每个属性定义目标类：

```java
@Test
public void givenUser_whenUseApi_thenConverted(){
    JMapperAPI jmapperApi = new JMapperAPI()
      .add(mappedClass(User.class)
      .add(attribute("id")
        .value("id")
        .targetClasses(UserDto1.class,UserDto2.class))
      .add(attribute("email")
        .targetAttributes("username","email")
        .targetClasses(UserDto1.class,UserDto2.class)));
    
    RelationalJMapper<User> relationalMapper = new RelationalJMapper<>
      (User.class,jmapperApi);
    User user = new User(1L,"john@test.com");
    UserDto1 result1 = relationalMapper
      .oneToMany(UserDto1.class, user);
    UserDto2 result2 = relationalMapper
      .oneToMany(UserDto2.class, user);

    assertEquals(user.getId(), result1.getId());
    assertEquals(user.getEmail(), result1.getUsername());
    assertEquals(user.getId(), result2.getId());
    assertEquals(user.getEmail(), result2.getEmail());            
}
```

请注意，对于每个目标类，我们需要定义目标属性名称。

RelationalJMapper只采用 一个类——映射类。

### 9.2. 使用注解

对于注解方法，我们还将定义 类：

```java
public class User {
    @JMap(classes = {UserDto1.class, UserDto2.class})
    private long id;    
    
    @JMap(
      attributes = {"username", "email"}, 
      classes = {UserDto1.class, UserDto2.class})
    private String email;
}
```

像往常一样，当我们使用注解时不需要进一步的配置：

```java
@Test
public void givenUser_whenUseAnnotation_thenConverted(){
    RelationalJMapper<User> relationalMapper
      = new RelationalJMapper<>(User.class);
    User user = new User(1L,"john@test.com");
    UserDto1 result1 = relationalMapper
      .oneToMany(UserDto1.class, user);
    UserDto2 result2= relationalMapper
      .oneToMany(UserDto2.class, user);

    assertEquals(user.getId(), result1.getId());
    assertEquals(user.getEmail(), result1.getUsername());  
    assertEquals(user.getId(), result2.getId());
    assertEquals(user.getEmail(), result2.getEmail());          
}
```

### 9.3. XML配置

对于 XML 配置，我们使用<classes>为每个属性定义目标类。

这是我们的user_jmapper2.xml：

```xml
<jmapper>
  <class name="com.baeldung.jmapper.relational.User">
    <attribute name="id">
      <value name="id"/>
      <classes>
        <class name="com.baeldung.jmapper.relational.UserDto1"/>
        <class name="com.baeldung.jmapper.relational.UserDto2"/>
      </classes>
    </attribute>
    <attribute name="email">
      <attributes>
        <attribute name="username"/>
        <attribute name="email"/>
      </attributes>
      <classes>
        <class name="com.baeldung.jmapper.relational.UserDto1"/>
        <class name="com.baeldung.jmapper.relational.UserDto2"/>
      </classes>      
    </attribute>
  </class>
</jmapper>
```

然后将 XML 配置文件传递给 RelationalJMapper：

```java
@Test
public void givenUser_whenUseXml_thenConverted(){
    RelationalJMapper<User> relationalMapper
     = new RelationalJMapper<>(User.class,"user_jmapper2.xml");
    User user = new User(1L,"john@test.com");
    UserDto1 result1 = relationalMapper
      .oneToMany(UserDto1.class, user);
    UserDto2 result2 = relationalMapper
      .oneToMany(UserDto2.class, user);

    assertEquals(user.getId(), result1.getId());
    assertEquals(user.getEmail(), result1.getUsername());
    assertEquals(user.getId(), result2.getId());
    assertEquals(user.getEmail(), result2.getEmail());         
}
```

## 10.总结

在本教程中，我们学习了配置 JMapper 的不同方法以及如何执行自定义转换。