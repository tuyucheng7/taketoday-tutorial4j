## 一、概述

在这个简短的教程中，我们将了解@JsonIgnore和@Transient注解之间的区别。

## 2. @JsonIgnore

我们使用[@JsonIgnore](https://www.baeldung.com/jackson-annotations#2-jsonignore)注解来指定在序列化和反序列化过程中应该忽略的方法或字段。此标记注解属于[Jackson](https://www.baeldung.com/jackson)库。

我们经常应用此注解来排除可能不相关或可能包含敏感信息的字段。我们在字段或方法上使用它来标记我们想要忽略的属性。

首先，让我们创建一个包含多个字段的简单Person类：

```java
class Person implements Serializable {

    private Long id;

    private String firstName;

    private String lastName;

    // getters and setters
}
```

现在，假设我们不想在Person对象的 JSON 表示中显示id字段。让我们使用@JsonIgnore注解排除该字段：

```java
class Person implements Serializable {

    @JsonIgnore
    private Long id;

    private String firstName;

    private String lastName;

    // getters and setters
}复制
```

最后，让我们编写一个测试来确保ObjectMapper忽略id字段：

```java
@Test
void givenPerson_whenSerializing_thenIdFieldIgnored() 
  throws JsonProcessingException {

    Person person = new Person(1L, "My First Name", "My Last Name");
    String result = new ObjectMapper().writeValueAsString(person);

    assertThat(result, containsString("firstName"));
    assertThat(result, containsString("lastName"));
    assertThat(result, not(containsString("id")));
}复制
```

## 3. @瞬态

另一方面，我们使用[@Transient](https://www.baeldung.com/jpa-transient-ignore-field)注解来指示[Java Persistence API](https://www.baeldung.com/learn-jpa-hibernate) (JPA) 在将对象映射到数据库时应忽略该字段。当我们用这个注解标记一个字段时，JPA 不会保留该字段，也不会从数据库中检索它的值。

现在，让我们创建一个用户类：

```java
@Entity
@Table(name = "Users")
class User implements Serializable {

    @Id
    private Long id;

    private String username;

    private String password;

    private String repeatedPassword;

    // getters and setters
}复制
```

让我们从带有@Transient注解的User对象的数据库表示中排除repeatedPassword字段：

```java
@Entity
@Table(name = "User")
class User implements Serializable {

    @Id
    private Long id;

    private String username;

    private String password;

    @Transient
    private String repeatedPassword;

    // getters and setters
}复制
```

当我们将User对象保存到数据库时，JPA 不会将repeatedPassword值保存到数据库。此外，JPA 在从数据库加载对象时会忽略该字段：

```java
@Test
void givenUser_whenSave_thenSkipTransientFields() {
    User user = new User(1L, "user", "newPassword123", "newPassword123");
    User savedUser = userRepository.save(user);

    assertNotNull(savedUser);
    assertNotNull(savedUser.getPassword());
    assertNull(savedUser.getRepeatedPassword());
}复制
```

但是，@ Transient注解不会从序列化中排除字段：

```java
@Test
void givenUser_whenSerializing_thenTransientFieldNotIgnored() throws JsonProcessingException {

    User user = new User(1L, "user", "newPassword123", "newPassword123");
    String result = new ObjectMapper().writeValueAsString(user);

    assertThat(result, containsString("user"));
    assertThat(result, containsString("repeatedPassword"));
}复制
```

## 4。结论

在本文中，我们了解了@JsonIgnore和@Transient注解之间的区别。

综上所述，这两个注解都用于指示在执行某些操作时应忽略的字段。@JsonIgnore注解从 JSON 表示中排除字段。**@Transient*注解从数据库表示中省略了它们。