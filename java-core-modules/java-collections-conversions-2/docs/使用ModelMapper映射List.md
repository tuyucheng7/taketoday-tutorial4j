## 1. 概述

在本教程中，我们将解释如何使用[ModelMapper](http://modelmapper.org/getting-started/)框架映射不同元素类型的列表。这涉及使用Java中的泛型类型作为将不同类型的数据从一个列表转换为另一个列表的解决方案。

## 2.模型映射器

ModelMapper 的主要作用是通过确定一个对象模型如何映射到另一个对象模型(称为数据转换对象 (DTO))来映射对象。

为了使用[ModelMapper](https://search.maven.org/artifact/org.modelmapper/modelmapper)，我们首先将依赖项添加到我们的pom.xml：

```xml
<dependency> 
    <groupId>org.modelmapper</groupId>
    <artifactId>modelmapper</artifactId>
    <version>2.3.7</version>
</dependency>
```

### 2.1. 配置

ModelMapper 提供了多种配置来简化映射过程。我们通过在配置中启用或禁用适当的属性来自定义配置。将fieldMatchingEnabled属性设置为 true 并允许私有字段匹配是一种常见的做法：

```java
modelMapper.getConfiguration()
  .setFieldMatchingEnabled(true)
  .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);

```

通过这样做，ModelMapper 可以比较映射类(对象)中的私有字段。在此配置中，两个类中都存在具有相同名称的所有字段并不是绝对必要的。允许多种[匹配策略。](http://modelmapper.org/user-manual/configuration/)默认情况下，标准匹配策略要求所有源和目标属性必须以任何顺序匹配。这非常适合我们的场景。

### 2.2. 类型令牌

ModelMapper 使用[TypeToken](http://modelmapper.org/javadoc/org/modelmapper/TypeToken.html)来映射泛型类型。要了解为什么这是必要的，让我们看看将整数列表映射到字符列表时会发生什么：

```java
List<Integer> integers = new ArrayList<Integer>();
integers.add(1);
integers.add(2);
integers.add(3);

List<Character> characters = new ArrayList<Character>();
modelMapper.map(integers, characters);
```

此外，如果我们打印出字符列表的元素，我们会看到一个空列表。这是由于在运行时执行期间发生了类型擦除。

但是，如果我们将map调用更改为使用TypeToken，我们可以为List<Character>创建类型文字：

```java
List<Character> characters 
    = modelMapper.map(integers, new TypeToken<List<Character>>() {}.getType());
```

在编译时，TokenType匿名内壳保留了 List<Character>参数类型，此时我们的转换成功了。

## 3.使用自定义类型映射

Java 中的列表可以使用自定义元素类型进行映射。

例如，假设我们要将User实体列表映射到UserDTO列表。为此，我们将为每个元素调用map ：

```java
List<UserDTO> dtos = users
  .stream()
  .map(user -> modelMapper.map(user, UserDTO.class))
  .collect(Collectors.toList());
```

当然，通过更多的工作，我们可以制作一个通用的参数化方法：

```java
<S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
    return source
      .stream()
      .map(element -> modelMapper.map(element, targetClass))
      .collect(Collectors.toList());
}
```

那么，我们可以改为：

```java
List<UserDTO> userDtoList = mapList(users, UserDTO.class);
```

## 4.类型映射和属性映射

可以将列表或集合等特定属性添加到User-UserDTO模型中。[TypeMap](http://modelmapper.org/javadoc/org/modelmapper/TypeMap.html) 提供了一种显式定义这些属性映射的方法。TypeMap对象存储特定类型(类)的映射信息：

```java
TypeMap<UserList, UserListDTO> typeMap = modelMapper.createTypeMap(UserList.class, UserListDTO.class);
```

UserList类包含User的集合。在这里，我们要将此集合中的用户名列表映射到UserListDTO类的属性列表。为此，我们将创建第一个UsersListConverter类并将List <User>和List <String>作为转换的参数类型传递给它：

```java
public class UsersListConverter extends AbstractConverter<List<User>, List<String>> {

    @Override
    protected List<String> convert(List<User> users) {

        return users
          .stream()
          .map(User::getUsername)
          .collect(Collectors.toList());
    }
}
```

从创建的TypeMap对象中，我们通过调用UsersListConverter类的实例显式添加[Property Mapping ：](http://modelmapper.org/user-manual/property-mapping/)

```java
 typeMap.addMappings(mapper -> mapper.using(new UsersListConverter())
   .map(UserList::getUsers, UserListDTO::setUsernames));
```

在addMappings方法内部，表达式映射允许我们使用 lambda 表达式定义源到目标属性。最后，它将用户列表转换为结果用户名列表。

## 5.总结

在本教程中，我们解释了如何通过在 ModelMapper 中操作泛型类型来映射列表。我们可以利用TypeToken、泛型类型映射和属性映射 来创建对象列表类型并进行复杂的映射。 