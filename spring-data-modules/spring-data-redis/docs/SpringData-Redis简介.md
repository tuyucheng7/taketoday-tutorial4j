## 1. 概述

本教程是**对Spring Data Redis的介绍**，它为[Redis](http://redis.io/)(流行的内存数据结构存储)提供了Spring Data平台的抽象。

Redis由基于键值对的数据结构驱动以持久化数据，可用作数据库、缓存、消息代理等。

我们将能够使用Spring Data的通用模式(模板等)，同时还具有所有Spring Data项目的传统简单性。

## 2. Maven依赖

让我们首先在pom.xml中声明Spring Data Redis依赖项：

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
</dependency>
```

最新版本的[spring-data-redis](https://central.sonatype.com/artifact/org.springframework.data/spring-data-redis/3.0.3)和[jedis](https://central.sonatype.com/artifact/redis.clients/jedis/4.4.0-m2)可以从Maven Central下载。

或者，我们可以使用Redis的Spring Boot Starter，这消除了对单独的spring-data和jedis依赖项的需要：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <version>2.7.2</version>
</dependency>
```

同样，[Maven Central](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-data-redis/3.0.3)提供最新版本的信息。

## 3. Redis配置

要定义应用程序客户端和Redis服务器实例之间的连接设置，我们需要使用Redis客户端。

有许多可用于Java的Redis客户端实现。在本教程中，**我们将使用[Jedis](https://github.com/xetorthio/jedis)-一个简单而强大的Redis客户端实现**。

框架中对XML和Java配置都有很好的支持。对于本教程，我们将使用基于Java的配置。

### 3.1 Java配置

让我们从配置bean定义开始：

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
```

配置非常简单。

首先，使用Jedis客户端，我们定义了一个connectionFactory。

然后我们使用jedisConnectionFactory定义了一个RedisTemplate。这可用于使用自定义Repository查询数据。

### 3.2 自定义连接属性

请注意，上述配置中缺少与连接相关的常用属性。例如，配置中缺少服务器地址和端口。原因很简单：我们使用的是默认值。

但是，如果我们需要配置连接细节，我们可以随时修改jedisConnectionFactory配置：

```java
@Bean
JedisConnectionFactory jedisConnectionFactory() {
    JedisConnectionFactory jedisConFactory = new JedisConnectionFactory();
    jedisConFactory.setHostName("localhost");
    jedisConFactory.setPort(6379);
    return jedisConFactory;
}
```

## 4. Redis Repository

让我们使用一个Student实体：

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
}
```

### 4.1 Spring Data Repository

现在让我们创建StudentRepository：

```java
@Repository
public interface StudentRepository extends CrudRepository<Student, String> {}
```

## 5. 使用StudentRepository访问数据

**通过在StudentRepository中扩展CrudRepository，我们自动获得了一套完整的执行CRUD功能的持久化方法**。

### 5.1 保存新的学生对象

让我们在数据存储中保存一个新的学生对象：

```java
Student student = new Student("Eng2015001", "John Doe", Student.Gender.MALE, 1);
studentRepository.save(student);
```

### 5.2 检索现有学生对象

我们可以通过获取学生数据来验证上一节中学生是否正确插入：

```java
Student retrievedStudent = studentRepository.findById("Eng2015001").get();
```

### 5.3.更新现有学生对象

让我们更改上面检索到的学生的姓名并再次保存：

```java
retrievedStudent.setName("Richard Watson");
studentRepository.save(student);
```

最后，我们可以再次检索学生的数据并验证数据存储中的名称是否已更新。

### 5.4 删除现有学生数据

我们可以删除插入的学生数据：

```java
studentRepository.deleteById(student.getId());
```

现在我们可以获取学生对象并验证结果是否为null。

### 5.5 查找所有学生数据

我们可以插入一些学生对象：

```java
Student engStudent = new Student("Eng2015001", "John Doe", Student.Gender.MALE, 1);
Student medStudent = new Student("Med2015001", "Gareth Houston", Student.Gender.MALE, 2);
studentRepository.save(engStudent);
studentRepository.save(medStudent);
```

我们也可以通过插入一个集合来实现这一点。为此，有一个不同的saveAll()方法-它接收一个包含我们想要持久化的多个学生对象的Iterable对象。

要获取所有插入的学生，我们可以使用findAll()方法：

```java
List<Student> students = new ArrayList<>();
studentRepository.findAll().forEach(students::add);
```

## 6. 总结

在本文中，我们介绍了Spring Data Redis的基础知识。