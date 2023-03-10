## 1. 概述

在本教程中，我们将探讨Spring Data MongoDB的一些核心功能-索引、通用注解和转换器。

## 2. 索引

### 2.1 @Indexed

此注解将字段标记为**在MongoDB中已编入索引**：

```java
@QueryEntity
@Document
public class User {
    @Indexed
    private String name;

    // ...
}
```

现在name字段已被索引-让我们在MongoDB shell中获取所有索引：

```bash
db.user.getIndexes();
```

这是我们得到的：

```json
[
    {
        "v": 1,
        "key": {
            "_id": 1
        },
        "name": "_id_",
        "ns": "test.user"
    }
]
```

我们可能会感到惊讶，到处都没有name字段的迹象！

这是因为，**从Spring Data MongoDB 3.0开始，默认情况下自动索引创建是关闭的**。

但是，我们可以通过在MongoConfig中显式覆盖autoIndexCreation()方法来更改该行为：

```java
public class MongoConfig extends AbstractMongoClientConfiguration {

    // rest of the config goes here

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}
```

让我们再次在MongoDB shell中检查索引：

```json
[
    {
        "v": 1,
        "key": {
            "_id": 1
        },
        "name": "_id_",
        "ns": "test.user"
    },
    {
        "v": 1,
        "key": {
            "name": 1
        },
        "name": "name",
        "ns": "test.user"
    }
]
```

正如我们所看到的，这一次，我们有两个索引-其中一个是_id，这是由于@Id注解而默认创建的，**第二个是我们的name字段**。

或者，**如果我们使用Spring Boot，我们可以将spring.data.mongodb.auto-index-creation属性设置为true**。

### 2.2 以编程方式创建索引

我们还可以通过编程方式创建索引：

```java
mongoOps.indexOps(User.class).
    ensureIndex(new Index().on("name", Direction.ASC));
```

我们现在已经为字段name创建了一个索引，结果将与上一节相同。

### 2.3 复合索引

MongoDB支持复合索引，其中单个索引结构包含对多个字段的引用。

让我们看一个使用复合索引的简单示例：

```java
@QueryEntity
@Document
@CompoundIndexes({
      @CompoundIndex(name = "email_age", def = "{'email.id' : 1, 'age': 1}")
})
public class User {
    //
}
```

我们创建了一个包含电子email和age字段的复合索引。现在让我们检查一下实际的索引：

```json
{
    "v": 1,
    "key": {
        "email.id": 1,
        "age": 1
    },
    "name": "email_age",
    "ns": "test.user"
}
```

请注意，DBRef字段不能用@Index标记-该字段只能是复合索引的一部分。

## 3. 常用注解

### 3.1 @Transient

顾名思义，这个简单的注解排除了字段在数据库中的持久化：

```java
public class User {

    @Transient
    private Integer yearOfBirth;
    // standard getter and setter
}
```

让插入一个设置了yearOfBirth字段的用户：

```java
User user = new User();
user.setName("Alex");
user.setYearOfBirth(1985);
mongoTemplate.insert(user);
```

现在，如果我们查看数据库的状态，我们会看到提交的yearOfBirth未保存：

```json
{
    "_id" : ObjectId("55d8b30f758fd3c9f374499b"),
    "name" : "Alex",
    "age" : null
}
```

因此，如果我们查询并检查：

```java
mongoTemplate.findOne(Query.query(Criteria.where("name").is("Alex")), User.class).getYearOfBirth()
```

结果将为null。

### 3.2 @Field

@Field表示要用于JSON文档中的字段的键：

```java
@Field("email")
private EmailAddress emailAddress;
```

现在emailAddress将使用“email”键保存在数据库中：

```java
User user = new User();
user.setName("Brendan");
EmailAddress emailAddress = new EmailAddress();
emailAddress.setValue("a@gmail.com");
user.setEmailAddress(emailAddress);
mongoTemplate.insert(user);
```

以及数据库的状态：

```json
{
    "_id" : ObjectId("55d076d80bad441ed114419d"),
    "name" : "Brendan",
    "age" : null,
    "email" : {
        "value" : "a@gmail.com"
    }
}
```

### 3.3 @PersistenceConstructor和@Value

@PersistenceConstructor将构造函数(甚至是受包保护的构造函数)标记为持久性逻辑使用的主要构造函数。构造函数参数按名称映射到检索到的DBObject中的键值。

让我们看看我们的User类的这个构造函数：

```java
@PersistenceConstructor
public User(String name, @Value("#root.age ?: 0") Integer age, EmailAddress emailAddress) {
    this.name =  name;
    this.age = age;
    this.emailAddress =  emailAddress;
}
```

注意这里使用了标准的Spring @Value注解。正是在这个注解的帮助下，我们可以使用Spring表达式来转换从数据库中检索到的键值，然后再将其用于构造域对象。这是一个非常强大且非常有用的功能。

在我们的示例中，如果未设置age，则默认情况下它将设置为0。

现在让我们看看它是如何工作的：

```java
User user = new User();
user.setName("Alex");
mongoTemplate.insert(user);
```

我们的数据库为：

```json
{
    "_id" : ObjectId("55d074ca0bad45f744a71318"),
    "name" : "Alex",
    "age" : null
}
```

所以age字段为null，但是当我们查询文档并检索age时：

```java
mongoTemplate.findOne(Query.query(Criteria.where("name").is("Alex")), User.class).getAge();
```

结果将为0。

## 4. 转换器

现在让我们来看看Spring Data MongoDB中另一个非常有用的特性-转换器，特别是MongoConverter。

这用于在存储和查询这些对象时处理所有Java类型到DBObjects的映射。

我们有两个选择-我们可以使用MappingMongoConverter，或早期版本中的SimpleMongoConverter(这在Spring Data MongoDB M3中已被弃用，其功能已移至MappingMongoConverter)。

或者我们可以编写自己的自定义转换器。为此，我们需要实现Converter接口并在MongoConfig中注册实现。

让我们看一个简单的例子。正如我们在这里的一些JSON输出中看到的那样，保存在数据库中的所有对象都具有自动保存的字段_class。但是，如果我们想在持久化期间跳过该特定字段，我们可以使用MappingMongoConverter来做到这一点。

首先-这是自定义转换器实现：

```java
@Component
public class UserWriterConverter implements Converter<User, DBObject> {
    @Override
    public DBObject convert(User user) {
        DBObject dbObject = new BasicDBObject();
        dbObject.put("name", user.getName());
        dbObject.put("age", user.getAge());
        if (user.getEmailAddress() != null) {
            DBObject emailDbObject = new BasicDBObject();
            emailDbObject.put("value", user.getEmailAddress().getValue());
            dbObject.put("email", emailDbObject);
        }
        dbObject.removeField("_class");
        return dbObject;
    }
}
```

请注意我们如何通过在此处直接删除该字段来轻松实现不持久化_class的目标。

现在我们需要注册自定义转换器：

```java
private List<Converter<?,?>> converters = new ArrayList<Converter<?, ?>>();

@Override
public MongoCustomConversions customConversions() {
    converters.add(new UserWriterConverter());
    return new MongoCustomConversions(converters);
}
```

当然，如果需要，我们也可以使用XML配置获得相同的结果：

```xml
<bean id="mongoTemplate" class="org.springframework.data.mongodb.core.MongoTemplate">
    <constructor-arg name="mongo" ref="mongo"/>
    <constructor-arg ref="mongoConverter"/>
    <constructor-arg name="databaseName" value="test"/>
</bean>

<mongo:mapping-converter id="mongoConverter" base-package="cn.tuyucheng.taketoday.converter">
    <mongo:custom-converters base-package="cn.tuyucheng.taketoday.converter" />
</mongo:mapping-converter>
```

现在，当我们保存一个新用户时：

```java
User user = new User();
user.setName("Chris");
mongoOps.insert(user);
```

数据库中生成的文档不再包含类信息：

```json
{
    "_id" : ObjectId("55cf09790bad4394db84b853"),
    "name" : "Chris",
    "age" : null
}
```

## 5. 总结

在本教程中，我们介绍了使用Spring Data MongoDB的一些核心概念-索引、通用注解和转换器。