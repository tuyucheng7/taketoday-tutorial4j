## 1. 简介

在本快速教程中，我们将解释如何使用[JPA](https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa)规范定义的注解来管理 PostgreSQL TEXT 类型。

## 2. PostgreSQL 中的 TEXT 类型

使用 PostgresSQL 时，我们可能会周期性地需要存储任意长度的字符串。

为此，PostgreSQL 提供了三种字符类型：

-   字符(n)
-   变量(n)
-   文本

不幸的是，TEXT 类型不是 SQL 标准管理的类型的一部分。这意味着如果我们想在我们的持久化实体中使用 JPA 注解，我们可能会遇到问题。

这是因为 JPA 规范使用了 SQL 标准。因此，它没有定义一种简单的方法来处理这种类型的对象，例如使用@Text注解。

幸运的是，我们有几种可能来管理 PostgreSQL 数据库的 TEXT 数据类型：

-   我们可以使用@Lob注解
-   或者，我们也可以使用@Column注解，结合columnDefinition属性

现在让我们看一下以@Lob注解开头的两种解决方案。

## 3. @Lob

顾名思义，lob 是一个大对象。在数据库术语中，lob 列用于存储非常长的文本或二进制文件。

我们可以从两种吊球中进行选择：

-   CLOB – 用于存储文本的字符 lob
-   BLOB – 可用于存储二进制数据的二进制 lob

我们可以使用 JPA @Lob注解将大型字段映射到大型数据库对象类型。

当我们在String类型的属性上使用@Lob记录时，JPA 规范规定持久性提供程序应该使用大字符类型对象来存储属性的值。因此，PostgreSQL 可以将字符 lob 转换为 TEXT 类型。

假设我们有一个简单的Exam实体对象，带有一个描述字段，它可以有任意长度：

```java
@Entity
public class Exam {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Lob
    private String description;
}

```

在描述字段上使用@Lob注解，我们指示 Hibernate 使用 PostgreSQL TEXT 类型管理该字段。

## 4. @专栏

管理 TEXT 类型的另一个选项是使用@Column注解和columnDefinition 属性。

让我们再次使用相同的Exam实体对象，但这次我们将添加一个 TEXT 字段，它可以是任意长度：

```java
@Entity
public class Exam {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    
    @Lob
    private String description;
    
    @Column(columnDefinition="TEXT")
    private String text;

}
```

在此示例中，我们使用注解@Column(columnDefinition=”TEXT”)。使用columnDefinition属性允许我们指定在为此类型构造数据列时将使用的 SQL 片段。

## 5. 综合考虑

在本节中，我们将编写一个简单的单元测试来验证我们的解决方案是否有效：

```java
@Test
public void givenExam_whenSaveExam_thenReturnExpectedExam() {
    Exam exam = new Exam();
    exam.setDescription("This is a description. Sometimes the description can be very very long! ");
    exam.setText("This is a text. Sometimes the text can be very very long!");

    exam = examRepository.save(exam);

    assertEquals(examRepository.find(exam.getId()), exam);
}
```

在此示例中，我们首先创建一个新的考试对象并将其保存到我们的数据库中。然后我们从数据库中检索考试对象并将结果与我们创建的原始考试进行比较。

为了证明这一点，如果我们快速修改考试实体的描述字段：

```java
@Column(length = 20)
private String description;

```

当我们再次运行测试时，我们会看到一个错误：

```plaintext
ERROR o.h.e.jdbc.spi.SqlExceptionHelper - Value too long for column "TEXT VARCHAR(20)"
```

## 六. 总结

在本教程中，我们介绍了两种使用 PostgreSQL TEXT 类型的 JPA 注解的方法。

我们首先解释了 TEXT 类型的用途，然后我们了解了如何使用 JPA 注解@Lob和@Column来使用 PostgreSQL 定义的 TEXT 类型来保存String对象。