## 1. 概述

在本教程中，我们正在研究使用Spring Data JPA和[Querydsl](https://www.baeldung.com/intro-to-querydsl)为REST API构建查询语言。

在[本系列](https://www.baeldung.com/spring-rest-api-query-search-language-tutorial)的前两篇文章中，我们使用JPA Criteria和Spring Data JPA Specifications构建了相同的搜索/过滤功能。

那么为什么要使用查询语言？因为对于任何足够复杂的API，通过非常简单的字段搜索/过滤你的资源是不够的。查询语言更灵活，并允许你精确过滤到你需要的资源。

## 2. Querydsl配置

首先，让我们看看如何配置我们的项目以使用Querydsl。

我们需要将以下依赖项添加到pom.xml：

```xml
<dependency> 
    <groupId>com.querydsl</groupId> 
    <artifactId>querydsl-apt</artifactId> 
    <version>4.2.2</version>
    </dependency>
<dependency> 
    <groupId>com.querydsl</groupId> 
    <artifactId>querydsl-jpa</artifactId> 
    <version>4.2.2</version> 
</dependency>
```

我们还需要配置APT-Annotation处理工具-插件如下：

```xml
<plugin>
    <groupId>com.mysema.maven</groupId>
    <artifactId>apt-maven-plugin</artifactId>
    <version>1.1.3</version>
    <executions>
        <execution>
            <goals>
                <goal>process</goal>
            </goals>
            <configuration>
                <outputDirectory>target/generated-sources/java</outputDirectory>
                <processor>com.mysema.query.apt.jpa.JPAAnnotationProcessor</processor>
            </configuration>
        </execution>
    </executions>
</plugin>
```

这将为我们的实体生成[Q-Type](https://www.baeldung.com/intro-to-querydsl#2-exploring-generated-classes)。 

## 3. MyUser实体

接下来——让我们看一下我们将在搜索API中使用的“MyUser”实体：

```java
@Entity
public class MyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;

    private int age;
}
```

## 4. 使用PathBuilder自定义谓词

现在让我们基于一些任意约束创建一个自定义谓词。

我们在这里使用PathBuilder而不是自动生成的Q-Type，因为我们需要动态创建路径以实现更抽象的用法：

```java
public class MyUserPredicate {

    private SearchCriteria criteria;

    public BooleanExpression getPredicate() {
        PathBuilder<MyUser> entityPath = new PathBuilder<>(MyUser.class, "user");

        if (isNumeric(criteria.getValue().toString())) {
            NumberPath<Integer> path = entityPath.getNumber(criteria.getKey(), Integer.class);
            int value = Integer.parseInt(criteria.getValue().toString());
            switch (criteria.getOperation()) {
                case ":":
                    return path.eq(value);
                case ">":
                    return path.goe(value);
                case "<":
                    return path.loe(value);
            }
        } else {
            StringPath path = entityPath.getString(criteria.getKey());
            if (criteria.getOperation().equalsIgnoreCase(":")) {
                return path.containsIgnoreCase(criteria.getValue().toString());
            }
        }
        return null;
    }
}
```

请注意谓词的实现如何一般地处理多种类型的操作。这是因为根据定义，查询语言是一种开放语言，你可以使用任何受支持的操作按任何字段进行过滤。

为了表示那种开放的过滤条件，我们使用了一个简单但非常灵活的实现-SearchCriteria：

```java
public class SearchCriteria {
    private String key;
    private String operation;
    private Object value;
}
```

SearchCriteria包含表示约束所需的详细信息：

-   key：字段名，例如：firstName，age...等
-   operation：操作，例如：相等，小于...等
-   value：字段值，例如：john，25...等

## 5. MyUserRepository

现在让我们来看看我们的MyUserRepository。

我们需要我们的MyUserRepository来扩展QuerydslPredicateExecutor以便我们以后可以使用Predicates来过滤搜索结果：

```java
public interface MyUserRepository extends JpaRepository<MyUser, Long>, QuerydslPredicateExecutor<MyUser>, QuerydslBinderCustomizer<QMyUser> {
    @Override
    default public void customize(QuerydslBindings bindings, QMyUser root) {
        bindings.bind(String.class)
              .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
        bindings.excluding(root.email);
    }
}
```

请注意，我们在这里使用为MyUser实体生成的Q-Type，它将被命名为QMyUser。

## 6. 组合谓词

接下来– 让我们看一下组合谓词以在结果过滤中使用多个约束。

在以下示例中，我们使用构建器MyUserPredicatesBuilder来组合Predicates：

```java
public class MyUserPredicatesBuilder {
    private List<SearchCriteria> params;

    public MyUserPredicatesBuilder() {
        params = new ArrayList<>();
    }

    public MyUserPredicatesBuilder with(
          String key, String operation, Object value) {

        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public BooleanExpression build() {
        if (params.size() == 0) {
            return null;
        }

        List predicates = params.stream().map(param -> {
            MyUserPredicate predicate = new MyUserPredicate(param);
            return predicate.getPredicate();
        }).filter(Objects::nonNull).collect(Collectors.toList());

        BooleanExpression result = Expressions.asBoolean(true).isTrue();
        for (BooleanExpression predicate : predicates) {
            result = result.and(predicate);
        }
        return result;
    }
}
```

## 7. 测试搜索查询

接下来，让我们测试我们的搜索API。

我们将从使用一些用户初始化数据库开始让这些用户准备好并可用于测试：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceConfig.class })
@Transactional
@Rollback
public class JPAQuerydslIntegrationTest {

    @Autowired
    private MyUserRepository repo;

    private MyUser userJohn;
    private MyUser userTom;

    @Before
    public void init() {
        userJohn = new MyUser();
        userJohn.setFirstName("John");
        userJohn.setLastName("Doe");
        userJohn.setEmail("john@doe.com");
        userJohn.setAge(22);
        repo.save(userJohn);

        userTom = new MyUser();
        userTom.setFirstName("Tom");
        userTom.setLastName("Doe");
        userTom.setEmail("tom@doe.com");
        userTom.setAge(26);
        repo.save(userTom);
    }
}
```

接下来，让我们看看如何查找具有给定姓氏的用户：

```java
@Test
public void givenLast_whenGettingListOfUsers_thenCorrect() {
    MyUserPredicatesBuilder builder = new MyUserPredicatesBuilder().with("lastName", ":", "Doe");

    Iterable<MyUser> results = repo.findAll(builder.build());
    assertThat(results, containsInAnyOrder(userJohn, userTom));
}
```

现在，让我们看看如何找到同时具有名字和姓氏的用户：

```java
@Test
public void givenFirstAndLastName_whenGettingListOfUsers_thenCorrect() {
    MyUserPredicatesBuilder builder = new MyUserPredicatesBuilder()
        .with("firstName", ":", "John")
        .with("lastName", ":", "Doe");

    Iterable<MyUser> results = repo.findAll(builder.build());

    assertThat(results, contains(userJohn));
    assertThat(results, not(contains(userTom)));
}
```

接下来，让我们看看如何找到同时具有姓氏和最小年龄的用户

```java
@Test
public void givenLastAndAge_whenGettingListOfUsers_thenCorrect() {
    MyUserPredicatesBuilder builder = new MyUserPredicatesBuilder()
        .with("lastName", ":", "Doe")
        .with("age", ">", "25");

    Iterable<MyUser> results = repo.findAll(builder.build());

    assertThat(results, contains(userTom));
    assertThat(results, not(contains(userJohn)));
}
```

现在，让我们看看如何搜索实际不存在的MyUser：

```java
@Test
public void givenWrongFirstAndLast_whenGettingListOfUsers_thenCorrect() {
    MyUserPredicatesBuilder builder = new MyUserPredicatesBuilder()
        .with("firstName", ":", "Adam")
        .with("lastName", ":", "Fox");

    Iterable<MyUser> results = repo.findAll(builder.build());
    assertThat(results, emptyIterable());
}
```

最后——让我们看看如何找到只给出部分名字的MyUser，如以下示例所示：

```java
@Test
public void givenPartialFirst_whenGettingListOfUsers_thenCorrect() {
    MyUserPredicatesBuilder builder = new MyUserPredicatesBuilder()
        .with("firstName", ":", "jo");

    Iterable<MyUser> results = repo.findAll(builder.build());

    assertThat(results, contains(userJohn));
    assertThat(results, not(contains(userTom)));
}
```

## 8. UserController

最后，让我们将所有内容放在一起并构建REST API。

我们正在定义一个UserController，它定义了一个简单的方法findAll()，带有一个“search”参数来传递查询字符串：

```java
@Controller
public class UserController {

    @Autowired
    private MyUserRepository myUserRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/myusers")
    @ResponseBody
    public Iterable<MyUser> search(@RequestParam(value = "search") String search) {
        MyUserPredicatesBuilder builder = new MyUserPredicatesBuilder();

        if (search != null) {
            Pattern pattern = Pattern.compile("(\w+?)(:|<|>)(\w+?),");
            Matcher matcher = pattern.matcher(search + ",");
            while (matcher.find()) {
                builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
            }
        }
        BooleanExpression exp = builder.build();
        return myUserRepository.findAll(exp);
    }
}
```

这是一个快速测试URL示例：

```bash
http://localhost:8080/myusers?search=lastName:doe,age>25
```

和响应：

```bash
[{
    "id":2,
    "firstName":"tom",
    "lastName":"doe",
    "email":"tom@doe.com",
    "age":26
}]
```

## 9. 总结

第三篇文章介绍了为REST API构建查询语言的第一步，充分利用了Querydsl库。

实施当然是早期的，但它可以很容易地发展以支持额外的操作。