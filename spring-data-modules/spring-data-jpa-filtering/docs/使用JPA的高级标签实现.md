## 1. 概述

标签是一种设计模式，它允许我们对数据执行高级过滤和排序。本文是[使用JPA实现简单标记](https://www.baeldung.com/jpa-tagging)的续篇。

因此，我们将从该文章停止的地方开始，介绍标签的高级用例。

## 2. 认可标签

可能最著名的高级标记实现是背书标签。我们可以在Linkedin等网站上看到这种模式。

本质上，标签是字符串名称和数值的组合。然后，我们可以用数字来表示标签被投票或“赞同”的次数。

以下是如何创建此类标签的示例：

```java
@Embeddable
public class SkillTag {
    private String name;
    private int value;

    // constructors, getters, setters
}
```

要使用这个标签，我们只需将它们的列表添加到我们的数据对象中：

```java
@ElementCollection
private List<SkillTag> skillTags = new ArrayList<>();
```

我们在上一篇文章中提到，@ElementCollection注解会自动为我们创建一个一对多的映射。

这是此关系的模型用例。因为每个标签都有与其存储的实体相关联的个性化数据，我们无法使用多对多存储机制来节省空间。

在本文的后面，我们将介绍多对多何时有意义的示例。

因为我们已经将skill标签嵌入到我们的原始实体中，所以我们可以像查询任何其他属性一样查询它。

下面是一个示例查询，用于查找任何获得超过一定数量认可的学生：

```java
@Query("SELECT s FROM Student s JOIN s.skillTags t WHERE t.name = LOWER(:tagName) AND t.value > :tagValue")
List<Student> retrieveByNameFilterByMinimumSkillTag(@Param("tagName") String tagName, @Param("tagValue") int tagValue);
```

接下来，让我们看一个如何使用它的例子：

```java
Student student = new Student(1, "Will");
SkillTag skill1 = new SkillTag("java", 5);
student.setSkillTags(Arrays.asList(skill1));
studentRepository.save(student);

Student student2 = new Student(2, "Joe");
SkillTag skill2 = new SkillTag("java", 1);
student2.setSkillTags(Arrays.asList(skill2));
studentRepository.save(student2);

List<Student> students = studentRepository.retrieveByNameFilterByMinimumSkillTag("java", 3);
assertEquals("size incorrect", 1, students.size());
```

现在我们可以搜索标签的存在或标签的一定数量的背书。

因此，我们可以将其与其他查询参数结合起来创建各种复杂的查询。

## 3. 位置标签

另一种流行的标签实现是位置标签。我们可以通过两种主要方式使用位置标签。

首先，它可以用来标记地球物理位置。

此外，它还可用于标记媒体中的位置，例如照片或视频。在所有这些情况下，模型的实现几乎相同。

以下是标记照片的示例：

```java
@Embeddable
public class LocationTag {
    private String name;
    private int xPos;
    private int yPos;

    // constructors, getters, setters
}
```

位置标签最值得注意的方面是仅使用数据库执行地理位置过滤器是多么困难。如果我们需要在地理范围内进行搜索，更好的方法是将模型加载到内置支持地理位置的搜索引擎(如Elasticsearch)中。

因此，对于这些位置标签，我们应该重点关注标签名称的过滤。

该查询看起来类似于我们在上一篇文章中的简单标记实现：

```java
@Query("SELECT s FROM Student s JOIN s.locationTags t WHERE t.name = LOWER(:tag)")
List<Student> retrieveByLocationTag(@Param("tag") String tag);
```

使用位置标签的例子看起来也很熟悉：

```java
Student student = new Student(0, "Steve");
student.setLocationTags(Arrays.asList(new LocationTag("here", 0, 0));
studentRepository.save(student);

Student student2 = studentRepository.retrieveByLocationTag("here").get(0);
assertEquals("name incorrect", "Steve", student2.getName());
```

如果Elasticsearch是不可能的，我们仍然需要在地理范围内搜索，使用简单的几何形状将使查询条件更具可读性。

我们将把判断一个点是否在圆形或矩形内作为读者的简单练习。

## 4. 键值标签

有时，我们需要存储稍微复杂一点的标签。我们可能想用一小部分关键标签来标记一个实体，但它可以包含各种各样的值。

例如，我们可以用department标签标记学生并将其值设置为Computer Science。每个学生都有department键，但他们可能都有不同的值与之关联。

该实现看起来类似于上面的认可标签：

```java
@Embeddable
public class KVTag {
    private String key;
    private String value;

    // constructors, getters and setters
}
```

我们可以像这样将它添加到我们的模型中：

```java
@ElementCollection
private List<KVTag> kvTags = new ArrayList<>();
```

现在我们可以向我们的Repository添加一个新查询：

```java
@Query("SELECT s FROM Student s JOIN s.kvTags t WHERE t.key = LOWER(:key)")
List<Student> retrieveByKeyTag(@Param("key") String key);
```

我们还可以快速添加查询以按值或键和值进行搜索。这为我们搜索数据的方式提供了额外的灵活性。

让我们测试一下并验证它是否正常工作：

```java
@Test
public void givenStudentWithKVTags_whenSave_thenGetByTagOk(){
    Student student = new Student(0, "John");
    student.setKVTags(Arrays.asList(new KVTag("department", "computer science")));
    studentRepository.save(student);

    Student student2 = new Student(1, "James");
    student2.setKVTags(Arrays.asList(new KVTag("department", "humanities")));
    studentRepository.save(student2);

    List<Student> students = studentRepository.retrieveByKeyTag("department");
 
    assertEquals("size incorrect", 2, students.size());
}
```

按照这种模式，我们可以设计更复杂的嵌套对象，并在需要时使用它们来标记我们的数据。

大多数用例都可以通过我们今天讨论的高级实现来满足，但也可以根据需要进行复杂化。

## 5. 重新实现标签

最后，我们将探讨标记的最后一个领域。到目前为止，我们已经了解了如何使用@ElementCollection注解来轻松地向我们的模型添加标签。虽然它使用起来很简单，但它有一个非常重要的权衡。引擎盖下的一对多实现会导致我们的数据存储中出现大量重复数据。

为了节省空间，我们需要创建另一个表，将我们的Student实体连接到我们的Tag实体。幸运的是，Spring JPA将为我们完成大部分繁重的工作。

我们将重新实现我们的Student和Tag实体，看看这是如何完成的。

### 5.1 定义实体

首先，我们需要重新创建我们的模型。我们将从ManyStudent模型开始：

```java
@Entity
public class ManyStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "manystudent_manytags",
          joinColumns = @JoinColumn(name = "manystudent_id",
                referencedColumnName = "id"),
          inverseJoinColumns = @JoinColumn(name = "manytag_id",
                referencedColumnName = "id"))
    private Set<ManyTag> manyTags = new HashSet<>();

    // constructors, getters and setters
}
```

这里有几件事需要注意。

首先，我们正在生成我们的ID，因此表链接更易于内部管理。

接下来，我们使用@ManyToMany注解告诉Spring 我们想要两个类之间的链接。

最后，我们使用@JoinTable注解来设置我们实际的连接表。

现在我们可以继续我们的新标签模型，我们称之为ManyTag：

```java
@Entity
public class ManyTag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;

    @ManyToMany(mappedBy = "manyTags")
    private Set<ManyStudent> students = new HashSet<>();

    // constructors, getters, setters
}
```

因为我们已经在学生模型中设置了连接表，所以我们只需要担心在该模型中设置引用即可。

我们使用mappedBy属性告诉JPA我们想要这个链接到我们之前创建的连接表。

### 5.2 定义Repository

除了模型之外，我们还需要设置两个Repository：每个实体一个。我们将让Spring Data在这里完成所有繁重的工作：

```java
public interface ManyTagRepository extends JpaRepository<ManyTag, Long> {
}
```

由于我们目前不需要只搜索标签，我们可以将Repository类留空。

我们的学生资料库稍微复杂一点：

```java
public interface ManyStudentRepository extends JpaRepository<ManyStudent, Long> {
    List<ManyStudent> findByManyTags_Name(String name);
}
```

同样，我们让Spring Data为我们自动生成查询。

### 5.3 测试

最后，让我们看看这一切在测试中是什么样子的：

```java
@Test
public void givenStudentWithManyTags_whenSave_theyGetByTagOk() {
    ManyTag tag = new ManyTag("full time");
    manyTagRepository.save(tag);

    ManyStudent student = new ManyStudent("John");
    student.setManyTags(Collections.singleton(tag));
    manyStudentRepository.save(student);

    List<ManyStudent> students = manyStudentRepository
        .findByManyTags_Name("full time");
 
    assertEquals("size incorrect", 1, students.size());
}
```

通过将标签存储在单独的可搜索表中所增加的灵活性远远超过添加到代码中的少量复杂性。

这也使我们能够通过删除重复标签来减少存储在系统中的标签总数。

但是，多对多并未针对我们想要存储特定于实体的状态信息以及标签的情况进行优化。

## 6. 总结

这篇文章接上一篇文章结束[的](https://www.baeldung.com/jpa-tagging)地方。

首先，我们介绍了几个在设计标签实现时很有用的高级模型。

最后，我们在多对多映射的上下文中重新检查了上一篇文章中标签的实现。