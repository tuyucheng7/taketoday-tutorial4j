## 1. 概述

在 JPA 2.0 及以下版本中，没有方便的方法将[Enum](https://www.baeldung.com/a-guide-to-java-enums)值映射到数据库列。每个选项都有其局限性和缺点。使用 JPA 2.1 功能可以避免这些问题。

在本教程中，我们将了解使用 JPA 将枚举持久保存在数据库中的不同可能性。我们还将描述它们的优点和缺点，并提供简单的代码示例。

## 2.使用@Enumerated注解

在 2.1 之前的 JPA 中，将枚举值映射到其数据库表示形式的最常见选项是使用@Enumerated注解。这样，我们可以指示 JPA 提供程序将枚举转换为其序数或字符串值。

我们将在本节中探讨这两个选项。

但是让我们首先创建一个我们将在整个教程中使用的简单的@Entity ：

```java
@Entity
public class Article {
    @Id
    private int id;

    private String title;

    // standard constructors, getters and setters
}
```

### 2.1. 映射序值

如果我们将@Enumerated(EnumType.ORDINAL)注解放在枚举字段上，JPA 将在数据库中持久化给定实体时使用[Enum.ordinal()值。](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Enum.html#ordinal())

让我们介绍第一个枚举：

```java
public enum Status {
    OPEN, REVIEW, APPROVED, REJECTED;
}
```

接下来，让我们将它添加到Article类中并用@Enumerated(EnumType.ORDINAL)注解它：

```java
@Entity
public class Article {
    @Id
    private int id;

    private String title;

    @Enumerated(EnumType.ORDINAL)
    private Status status;
}
```

现在当持久化一个Article实体时：

```java
Article article = new Article();
article.setId(1);
article.setTitle("ordinal title");
article.setStatus(Status.OPEN);

```

JPA 将触发以下 SQL 语句：

```sql
insert 
into
    Article
    (status, title, id) 
values
    (?, ?, ?)
binding parameter [1] as [INTEGER] - [0]
binding parameter [2] as [VARCHAR] - [ordinal title]
binding parameter [3] as [INTEGER] - [1]
```

当我们需要修改我们的枚举时，这种映射会出现问题。如果我们在中间添加一个新值或重新排列枚举的顺序，我们将破坏现有的数据模型。

由于我们必须更新所有数据库记录，因此此类问题可能难以发现且难以修复。

### 2.2. 映射字符串值

类似地，如果我们用@Enumerated(EnumType.STRING)注解枚举字段，JPA 将在存储实体时使用[Enum.name()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Enum.html#name())值。

让我们创建第二个枚举：

```java
public enum Type {
    INTERNAL, EXTERNAL;
}
```

让我们将它添加到我们的Article类中并用@Enumerated(EnumType.STRING)注解它：

```java
@Entity
public class Article {
    @Id
    private int id;

    private String title;

    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Type type;
}
```

现在当持久化一个Article实体时：

```java
Article article = new Article();
article.setId(2);
article.setTitle("string title");
article.setType(Type.EXTERNAL);
```

JPA 将执行以下 SQL 语句：

```sql
insert 
into
    Article
    (status, title, type, id) 
values
    (?, ?, ?, ?)
binding parameter [1] as [INTEGER] - [null]
binding parameter [2] as [VARCHAR] - [string title]
binding parameter [3] as [VARCHAR] - [EXTERNAL]
binding parameter [4] as [INTEGER] - [2]
```

使用@Enumerated(EnumType.STRING)，我们可以安全地添加新的枚举值或更改枚举的顺序。但是，重命名枚举值仍会破坏数据库数据。

此外，尽管与@Enumerated(EnumType.ORDINAL)选项相比，这种数据表示形式的可读性要高得多，但它也消耗了比必要更多的空间。当我们需要处理大量数据时，这可能会成为一个重要问题。

## 3. 使用@PostLoad和@PrePersist注解

我们必须处理数据库中持久枚举的另一种选择是使用标准的 JPA 回调方法。我们可以在@PostLoad和 @PrePersist事件中来回映射我们的枚举 。

这个想法是在一个实体中有两个属性。第一个映射到数据库值，第二个是一个包含真实枚举值的@Transient字段。然后业务逻辑代码使用瞬态属性。

为了更好地理解这个概念，让我们创建一个新的枚举并在映射逻辑中使用它的int值：

```java
public enum Priority {
    LOW(100), MEDIUM(200), HIGH(300);

    private int priority;

    private Priority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public static Priority of(int priority) {
        return Stream.of(Priority.values())
          .filter(p -> p.getPriority() == priority)
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);
    }
}
```

我们还添加了Priority.of()方法，以便根据其int值轻松获取Priority实例。

现在，要在我们的Article类中使用它，我们需要添加两个属性并实现回调方法：

```java
@Entity
public class Article {

    @Id
    private int id;

    private String title;

    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Basic
    private int priorityValue;

    @Transient
    private Priority priority;

    @PostLoad
    void fillTransient() {
        if (priorityValue > 0) {
            this.priority = Priority.of(priorityValue);
        }
    }

    @PrePersist
    void fillPersistent() {
        if (priority != null) {
            this.priorityValue = priority.getPriority();
        }
    }
}
```

现在当持久化一个Article实体时：

```java
Article article = new Article();
article.setId(3);
article.setTitle("callback title");
article.setPriority(Priority.HIGH);
```

JPA 将触发以下 SQL 查询：

```sql
insert 
into
    Article
    (priorityValue, status, title, type, id) 
values
    (?, ?, ?, ?, ?)
binding parameter [1] as [INTEGER] - [300]
binding parameter [2] as [INTEGER] - [null]
binding parameter [3] as [VARCHAR] - [callback title]
binding parameter [4] as [VARCHAR] - [null]
binding parameter [5] as [INTEGER] - [3]
```

与之前描述的解决方案相比，尽管此选项使我们在选择数据库值的表示形式方面更加灵活，但它并不理想。让两个属性代表实体中的单个枚举只是感觉不对。此外，如果我们使用这种类型的映射，我们将无法在 JPQL 查询中使用枚举的值。 

## 4. 使用 JPA 2.1 @Converter注解

为了克服上述解决方案的局限性，JPA 2.1 版本引入了一个新的标准化 API，可用于将实体属性转换为数据库值，反之亦然。我们需要做的就是创建一个实现javax.persistence.AttributeConverter的新类，并用@Converter注解它。

让我们看一个实际的例子。

首先，我们将创建一个新的枚举：

```java
public enum Category {
    SPORT("S"), MUSIC("M"), TECHNOLOGY("T");

    private String code;

    private Category(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
```

我们还需要将它添加到Article类中：

```java
@Entity
public class Article {

    @Id
    private int id;

    private String title;

    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Basic
    private int priorityValue;

    @Transient
    private Priority priority;

    private Category category;
}
```

现在让我们创建一个新的CategoryConverter：

```java
@Converter(autoApply = true)
public class CategoryConverter implements AttributeConverter<Category, String> {
 
    @Override
    public String convertToDatabaseColumn(Category category) {
        if (category == null) {
            return null;
        }
        return category.getCode();
    }

    @Override
    public Category convertToEntityAttribute(String code) {
        if (code == null) {
            return null;
        }

        return Stream.of(Category.values())
          .filter(c -> c.getCode().equals(code))
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);
    }
}
```

我们已将@Converter的autoApply值设置为true，以便 JPA 自动将转换逻辑应用到Category类型的所有映射属性。否则，我们必须将@Converter注解直接放在实体的字段上。

现在让我们持久化一个Article实体：

```java
Article article = new Article();
article.setId(4);
article.setTitle("converted title");
article.setCategory(Category.MUSIC);
```

然后 JPA 将执行以下 SQL 语句：

```sql
insert 
into
    Article
    (category, priorityValue, status, title, type, id) 
values
    (?, ?, ?, ?, ?, ?)
Converted value on binding : MUSIC -> M
binding parameter [1] as [VARCHAR] - [M]
binding parameter [2] as [INTEGER] - [0]
binding parameter [3] as [INTEGER] - [null]
binding parameter [4] as [VARCHAR] - [converted title]
binding parameter [5] as [VARCHAR] - [null]
binding parameter [6] as [INTEGER] - [4]
```

如我们所见，如果我们使用AttributeConverter接口，我们可以简单地设置自己的将枚举转换为相应数据库值的规则。此外，我们可以安全地添加新的枚举值或更改现有枚举值，而不会破坏已经持久化的数据。

整体解决方案易于实施，并解决了前面部分中介绍的选项的所有缺点。

## 5. 在 JPQL 中使用枚举

现在让我们看看在 JPQL 查询中使用枚举是多么容易。

要查找具有Category.SPORT类别的所有Article实体，我们需要执行以下语句：

```java
String jpql = "select a from Article a where a.category = com.baeldung.jpa.enums.Category.SPORT";

List<Article> articles = em.createQuery(jpql, Article.class).getResultList();
```

重要的是要注意，在这种情况下我们需要使用完全限定的枚举名称。

当然，我们不限于静态查询。

使用命名参数是完全合法的：

```java
String jpql = "select a from Article a where a.category = :category";

TypedQuery<Article> query = em.createQuery(jpql, Article.class);
query.setParameter("category", Category.TECHNOLOGY);

List<Article> articles = query.getResultList();
```

此示例提供了一种非常方便的方式来形成动态查询。

此外，我们不需要使用完全限定名称。

## 六. 总结

在本文中，我们介绍了在数据库中持久保存枚举值的各种方法。我们介绍了在 2.0 及以下版本中使用 JPA 时的选项以及 JPA 2.1 及更高版本中可用的新 API。

值得注意的是，这些并不是在 JPA 中处理枚举的唯一可能性。[一些数据库，如 PostgreSQL，提供专门的列类型来存储枚举值。](https://www.postgresql.org/docs/current/datatype-enum.html)但是，此类解决方案超出了本文的范围。

根据经验，如果我们使用 JPA 2.1 或更高版本，我们应该始终使用AttributeConverter接口和@Converter注解。