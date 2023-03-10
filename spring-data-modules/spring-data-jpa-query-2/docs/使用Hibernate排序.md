## 1. 概述

本文说明了如何**使用Hibernate查询语言(HQL)和Criteria API对Hibernate进行排序**。

## 延伸阅读

### [Hibernate：save、persist、update、merge、saveOrUpdate](https://www.baeldung.com/hibernate-save-persist-update-merge-saveorupdate)

Hibernate写入方法的快速实用指南：save、persist、update、merge、saveOrUpdate。

[阅读更多](https://www.baeldung.com/hibernate-save-persist-update-merge-saveorupdate)→

### [使用Hibernate删除对象](https://www.baeldung.com/delete-with-hibernate)

在Hibernate中删除实体的快速指南。

[阅读更多](https://www.baeldung.com/delete-with-hibernate)→

### [Hibernate拦截器](https://www.baeldung.com/hibernate-interceptor)

创建Hibernate拦截器的快速实用指南。

[阅读更多](https://www.baeldung.com/hibernate-interceptor)→

## 2. 用HQL排序

使用Hibernate的HQL进行排序就像将Order By子句添加到HQL查询字符串一样简单：

```java
String hql = "FROM Foo f ORDER BY f.name";
Query query = sess.createQuery(hql);
```

执行这段代码后，Hibernate将生成如下SQL查询：

```hql
Hibernate: select foo0_.ID as ID1_0_, foo0_.NAME as NAME2_0_ from 
    FOO foo0_ order by foo0_.NAME
```

默认排序方向是升序。这就是为什么顺序条件asc不包含在生成的SQL查询中的原因。

### 2.1 使用显式排序顺序

要手动指定排序方向-你需要在HQL查询字符串中包含排序方式：

```java
String hql = "FROM Foo f ORDER BY f.name ASC";
Query query = sess.createQuery(hql);
```

在此示例中，在生成的SQL查询中包含在HQL中设置asc子句：

```hql
Hibernate: select foo0_.ID as ID1_0_, foo0_.NAME as NAME2_0_ 
    from FOO foo0_ order by foo0_.NAME ASC
```

### 2.2 按多个属性排序

可以将多个属性以及可选的排序顺序添加到HQL查询字符串中的Order By子句中：

```java
String hql = "FROM Foo f ORDER BY f.name DESC, f.id ASC";
Query query = sess.createQuery(hql);
```

生成的SQL查询将相应改变：

```hql
Hibernate: select foo0_.ID as ID1_0_, foo0_.NAME as NAME2_0_ 
    from FOO foo0_ order by foo0_.NAME DESC, foo0_.ID ASC
```

### 2.3 设置空值的排序优先级

默认情况下，当排序依据的属性具有null值时，由RDMS决定优先级。可以通过**在HQL查询字符串中添加NULLS FIRST或NULLS LAST子句来覆盖此默认处理**。

这个简单的示例将所有null值放在结果列表的末尾：

```java
String hql = "FROM Foo f ORDER BY f.name NULLS LAST";
Query query = sess.createQuery(hql);
```

让我们看看生成的SQL查询中的is null then 1 else 0子句：

```hql
Hibernate: select foo0_.ID as ID1_1_, foo0_.NAME as NAME2_1_, 
foo0_.BAR_ID as BAR_ID3_1_, foo0_.idx as idx4_1_ from FOO foo0_ 
order by case when foo0_.NAME is null then 1 else 0 end, foo0_.NAME
```

### 2.4 排序一对多关系

让我们分析一个复杂的排序案例：**在一对多关系中对实体进行排序**-Bar包含Foo实体的集合。

我们将通过使用**Hibernate @OrderBy注解**对集合进行标注来做到这一点；我们将指定完成排序的字段以及方向：

```java
@OrderBy(clause = "NAME DESC")
Set<Foo> fooList = new HashSet();
```

请注意@OrderBy注解的clause参数，与类似的@OrderBy JPA注解相比，这是Hibernate的@OrderBy所独有的。将此方法与其JPA等效方法区分开来的另一个特征是，clause参数指示排序是基于FOO表的NAME列而不是Foo实体的name属性完成的。

现在让我们看看Bars和Foos的实际排序：

```java
String hql = "FROM Bar b ORDER BY b.id";
Query query = sess.createQuery(hql);
```

**生成的SQL语句**显示排序后的Foo被放置在fooList中：

```hql
Hibernate: select bar0_.ID as ID1_0_, bar0_.NAME as NAME2_0_ from BAR bar0_ 
    order by bar0_.ID Hibernate: select foolist0_.BAR_ID as BAR_ID3_0_0_, 
    foolist0_.ID as ID1_1_0_, foolist0_.ID as ID1_1_1_, foolist0_.NAME as 
    NAME2_1_1_, foolist0_.BAR_ID as BAR_ID3_1_1_, foolist0_.idx as idx4_1_1_ 
    from FOO foolist0_ where foolist0_.BAR_ID=? order by foolist0_.NAME desc
```

要记住的一件事是，**不可能像JPA那样对列表进行排序**，Hibernate文档指出：

>   “Hibernate目前忽略了@ElementCollection上的@OrderBy，例如List<String>。元素的顺序由数据库返回的一样，未定义。”

作为补充说明，可以通过为Hibernate使用旧的XML配置并将<List...>元素替换为<Bag...>元素来解决此限制。

## 3. 使用Hibernate Criteria排序

Criteria API提供Order类作为管理排序的主要API。

### 3.1 设置排序顺序

Order类有两种设置排序顺序的方法：

-   **asc**(String attribute)：按attribute升序对查询进行排序
-   **desc**(String attribute)：按attribute降序对查询进行排序

让我们从一个简单的例子开始-按单个id属性排序：

```java
Criteria criteria = sess.createCriteria(Foo.class, "FOO");
criteria.addOrder(Order.asc("id"));
```

请注意，asc方法的参数区分大小写，并且应与要排序的属性名称相匹配。

Hibernate的Criteria API显式设置排序顺序方向，这反映在代码生成的SQL语句中：

```hql
Hibernate: select this_.ID as ID1_0_0_, this_.NAME as NAME2_0_0_ 
    from FOO this_ order by this_.ID asc
```

### 3.2 按多个属性排序

按多个属性排序只需要在Criteria实例中添加一个Order对象，如下例所示：

```java
Criteria criteria = sess.createCriteria(Foo.class, "FOO");
criteria.addOrder(Order.asc("name"));
criteria.addOrder(Order.asc("id"));
```

在SQL中生成的查询是：

```hql
Hibernate: select this_.ID as ID1_0_0_, this_.NAME as NAME2_0_0_ from 
    FOO this_ order by this_.NAME asc, this_.ID asc
```

### 3.3 设置空值的排序优先级

默认情况下，当排序依据的属性具有null值时，由RDMS决定优先级。Hibernate Criteria API使得更改默认值并**将null值放在升序列表的末尾**变得简单：

```java
Criteria criteria = sess.createCriteria(Foo.class, "FOO");
criteria.addOrder(Order.asc("name").nulls(NullPrecedence.LAST));
```

这是底层的SQL查询-带有**is null then 1 else 0**子句：

```hql
Hibernate: select this_.ID as ID1_1_1_, this_.NAME as NAME2_1_1_, 
    this_.BAR_ID as BAR_ID3_1_1_, this_.idx as idx4_1_1_, bar2_.ID as
    ID1_0_0_, bar2_.NAME as NAME2_0_0_ from FOO order by case when 
    this_.NAME is null then 1 else 0 end, this_.NAME asc
```

或者，我们也可以**将null值放在降序列表的开头**：

```java
Criteria criteria = sess.createCriteria(Foo.class, "FOO");
criteria.addOrder(Order.desc("name").nulls(NullPrecedence.FIRST));
```

相应的SQL查询如下-带有**is null then 0 else 1**子句：

```hql
Hibernate: select this_.ID as ID1_1_1_, this_.NAME as NAME2_1_1_, 
    this_.BAR_ID as BAR_ID3_1_1_, this_.idx as idx4_1_1_, bar2_.ID as 
    ID1_0_0_, bar2_.NAME as NAME2_0_0_ from FOO order by case when 
    this_.NAME is null then 0 else 1 end, this_.NAME desc
```

请注意，**如果要排序的属性是原始类型(如int)，则会抛出PersistenceException**。

例如，如果f.anIntVariable的值为null，则执行查询：

```java
String jql = "Select f from Foo as f order by f.anIntVariable desc NULLS FIRST";
Query sortQuery = entityManager.createQuery(jql);
```

会抛出：

```shell
javax.persistence.PersistenceException: org.hibernate.PropertyAccessException:
Null value was assigned to a property of primitive type setter of 
cn.tuyucheng.taketoday.jpa.example.Foo.anIntVariable
```

## 4. 总结

本文探讨了使用Hibernate进行排序-将可用的API用于简单实体以及一对多关系中的实体。