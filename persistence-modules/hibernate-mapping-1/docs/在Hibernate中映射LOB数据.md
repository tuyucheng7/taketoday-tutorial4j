## 1. 概述

LOB 或 Large OBject 是指用于存储大型对象的可变长度数据类型。

该数据类型有两种变体：

-   CLOB – Character Large Object将存储大文本数据
-   BLOB – Binary Large Object用于存储图像、音频或视频等二进制数据

在本教程中，我们将展示如何利用 Hibernate ORM 来持久化大型对象。

## 2.设置

例如，我们将使用 Hibernate 5 和 H2 数据库。因此，我们必须在我们的pom.xml 中将它们声明为依赖项：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.4.12.Final</version>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.196</version>
</dependency>
```

最新版本的依赖项位于[Maven Central Repositories](https://search.maven.org/classic/#search|ga|1|(g%3A"com.h2database" OR g%3A"org.hibernate") AND (a%3A"h2" OR a%3A"hibernate-core"))中。

要更深入地了解配置 Hibernate，请参阅我们的一篇[介绍性](https://www.baeldung.com/persistence-with-spring-series)文章。

## 3. LOB数据模型

我们的模型“User”具有 id、name 和 photo 作为属性。我们将在User的照片属性中存储一张图片，并将其映射到一个 BLOB：

```java
@Entity
@Table(name="user")
public class User {

    @Id
    private String id;
	
    @Column(name = "name", columnDefinition="VARCHAR(128)")
    private String name;
	
    @Lob
    @Column(name = "photo", columnDefinition="BLOB")
    private byte[] photo;

    // ...
}
```

@Lob注解指定数据库应将属性存储为Large Object。@Column注解中的columnDefinition定义属性的列类型。

因为我们要保存字节数组，所以我们使用BLOB。

## 4.用法

### 4.1. 启动休眠会话

```java
session = HibernateSessionUtil
  .getSessionFactory("hibernate.properties")
  .openSession();
```

使用助手类，我们将使用hibernate.properties文件中提供的数据库信息构建Hibernate Session。

### 4.2. 创建用户实例

假设用户将照片作为图像文件上传：

```java
User user = new User();
		
InputStream inputStream = this.getClass()
  .getClassLoader()
  .getResourceAsStream("profile.png");

if(inputStream == null) {
    fail("Unable to get resources");
}
user.setId("1");
user.setName("User");
user.setPhoto(IOUtils.toByteArray(inputStream));

```

我们借助[Apache Commons IO](https://www.baeldung.com/apache-commons-io)库将图像文件转换为字节数组，最后，我们将字节数组分配为新创建的User对象的一部分。

### 4.3. 持久化大对象

通过使用Session存储用户，Hibernate会将对象转换为数据库记录：

```java
session.persist(user);

```

由于类User上声明的@Lob注解，Hibernate理解它应该将“photo”属性存储为BLOB数据类型。

### 4.4. 数据验证

我们将从数据库中取回数据并使用Hibernate将其映射回Java对象以将其与插入的数据进行比较。

由于我们知道插入的用户的ID，我们将使用它从数据库中检索数据：

```java
User result = session.find(User.class, "1");

```

让我们将查询结果与输入User的数据进行比较：

```java
assertNotNull(
  "Query result is null", 
  result);
 
assertEquals(
  "User's name is invalid", 
  user.getName(), result.getName() );
 
assertTrue(
  "User's photo is corrupted", 
  Arrays.equals(user.getPhoto(), result.getPhoto()) );

```

Hibernate会使用注解上的相同映射信息将数据库中的数据映射到Java对象。

因此，检索到的用户对象将具有与插入数据相同的信息。

## 5.总结

LOB是用于存储大对象数据的数据类型。LOB有两种，分别称为BLOB和CLOB。BLOB用于存储二进制数据，而CLOB用于存储文本数据。

通过使用Hibernate ，我们已经演示了将数据映射到Java对象和从Java对象映射数据是多么容易，只要我们在数据库中定义正确的数据模型和适当的表结构即可。