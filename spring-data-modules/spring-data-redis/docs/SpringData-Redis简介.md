## **一、概述**

本教程是**对 Spring Data Redis 的介绍，它为**[Redis](http://redis.io/) — 流行的内存数据结构存储提供了 Spring Data 平台的抽象。

Redis 由基于密钥库的数据结构驱动以持久化数据，可用作数据库、缓存、消息代理等。

我们将能够使用 Spring Data 的通用模式（模板等），同时还具有所有 Spring Data 项目的传统简单性。

## **2.Maven依赖**

让我们首先在*pom.xml*中声明 Spring Data Redis 依赖项：

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-redis</artifactId>
    <version>2.3.3.RELEASE</version>
 </dependency>

<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>3.3.0</version>
    <type>jar</type>
</dependency>复制
```

可以从 Maven Central 下载最新版本的[*spring-data-redis*](https://search.maven.org/classic/#search|ga|1|spring-data-redis)和*[jedis 。](https://search.maven.org/classic/#search|ga|1|a%3A"jedis" AND g%3A"redis.clients")*

或者，我们可以使用 Redis 的 Spring Boot starter，这将消除对单独的*spring-data*和*jedis*依赖项的需要：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <version>2.7.2</version>
</dependency>复制
```

同样，[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-redis")提供最新的版本信息。

## **3. Redis配置**

要定义应用程序客户端和 Redis 服务器实例之间的连接设置，我们需要使用 Redis 客户端。

有许多可用于 Java 的 Redis 客户端实现。在本教程中，**我们将使用[Jedis——](https://github.com/xetorthio/jedis)一个简单而强大的 Redis 客户端实现。**

框架中对 XML 和 Java 配置都有很好的支持。对于本教程，我们将使用基于 Java 的配置。

### **3.1. Java配置**

让我们从配置 bean 定义开始：

```java
@Bean
JedisConnectionFactory jedisConnectionFactory() {
    return new JedisConnectionFactory();
}

@Bean
public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(jedisConnectionFactory());
    return template;
}
复制
```

配置非常简单。

首先，使用 Jedis 客户端，我们定义了一个*connectionFactory*。

然后我们使用*jedisConnectionFactory定义了一个**RedisTemplate*。这可用于使用自定义存储库查询数据。

### **3.2. 自定义连接属性**

请注意，上述配置中缺少与连接相关的常用属性。例如，配置中缺少服务器地址和端口。原因很简单：我们使用默认值。

但是，如果我们需要配置连接细节，我们可以随时修改*jedisConnectionFactory*配置：

```java
@Bean
JedisConnectionFactory jedisConnectionFactory() {
    JedisConnectionFactory jedisConFactory
      = new JedisConnectionFactory();
    jedisConFactory.setHostName("localhost");
    jedisConFactory.setPort(6379);
    return jedisConFactory;
}复制
```

## **4.Redis 存储库**

让我们使用一个*Student*实体：

```java
@RedisHash("Student")
public class Student implements Serializable {
  
    public enum Gender { 
        MALE, FEMALE
    }

    private String id;
    private String name;
    private Gender gender;
    private int grade;
    // ...
}复制
```

### **4.1. 弹簧数据存储库**

现在让我们创建*StudentRepository*：

```java
@Repository
public interface StudentRepository extends CrudRepository<Student, String> {}复制
```

## **5. 使用\*StudentRepository访问数据\***

**通过扩展\*StudentRepository\*中的\*CrudRepository\*，我们自动获得了一套完整的执行 CRUD 功能的持久化方法。**

### **5.1. 保存新的学生对象**

让我们在数据存储中保存一个新的学生对象：

```java
Student student = new Student(
  "Eng2015001", "John Doe", Student.Gender.MALE, 1);
studentRepository.save(student);复制
```

### **5.2. 检索现有学生对象**

我们可以通过获取学生数据来验证上一节中学生是否正确插入：

```java
Student retrievedStudent = 
  studentRepository.findById("Eng2015001").get();复制
```

### **5.3. 更新现有学生对象**

让我们更改上面检索到的学生的姓名并再次保存：

```java
retrievedStudent.setName("Richard Watson");
studentRepository.save(student);复制
```

最后，我们可以再次检索学生的数据并验证姓名是否已在数据存储中更新。

### **5.4. 删除现有学生数据**

我们可以删除插入的学生数据：

```java
studentRepository.deleteById(student.getId());复制
```

现在我们可以搜索学生对象并验证结果是否为*null*。

### **5.5. 查找所有学生数据**

我们可以插入一些学生对象：

```java
Student engStudent = new Student(
  "Eng2015001", "John Doe", Student.Gender.MALE, 1);
Student medStudent = new Student(
  "Med2015001", "Gareth Houston", Student.Gender.MALE, 2);
studentRepository.save(engStudent);
studentRepository.save(medStudent);复制
```

我们也可以通过插入一个集合来实现这一点。为此，有一个不同的方法*——saveAll()* ——它接受一个包含多个我们想要持久化的学生对象的可*迭代对象。*

要找到所有插入的学生，我们可以使用*findAll()*方法：

```java
List<Student> students = new ArrayList<>();
studentRepository.findAll().forEach(students::add);复制
```

然后我们可以快速检查*学生*列表的大小或通过检查每个对象的属性来验证更大的粒度。

## **六，结论**

在本文中，我们了解了 Spring Data Redis 的基础知识。