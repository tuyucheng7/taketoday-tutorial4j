## 1. 概述

在这个[新系列](https://www.baeldung.com/spring-rest-api-query-search-language-tutorial)的第一篇文章中，我们将探索一种用于REST API的简单查询语言。我们将充分利用Spring的REST API和JPA 2 Criteria的持久层方面。

为什么是查询语言？因为——对于任何足够复杂的API，通过非常简单的字段搜索/过滤你的资源是不够的。查询语言更灵活，允许你精确过滤到你需要的资源。

## 2. 用户实体

首先，让我们提出我们将用于我们的过滤器/搜索API的简单实体，一个基本的User：

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;

    private int age;
}
```

## 3. 使用CriteriaBuilder进行过滤

现在——让我们进入问题的核心——持久层中的查询。

构建查询抽象是一个平衡问题，一方面我们需要很大的灵活性，另一方面我们需要保持复杂性可控。高层，功能很简单-你传递一些约束，然后你得到一些结果。

让我们看看它是如何工作的：

```java
@Repository
public class UserDAO implements IUserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> searchUser(List<SearchCriteria> params) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = builder.createQuery(User.class);
        Root r = query.from(User.class);

        Predicate predicate = builder.conjunction();

        UserSearchQueryCriteriaConsumer searchConsumer =
              new UserSearchQueryCriteriaConsumer(predicate, builder, r);
        params.stream().forEach(searchConsumer);
        predicate = searchConsumer.getPredicate();
        query.where(predicate);

        List<User> result = entityManager.createQuery(query).getResultList();
        return result;
    }

    @Override
    public void save(User entity) {
        entityManager.persist(entity);
    }
}
```

让我们看一下UserSearchQueryCriteriaConsumer类：

```java
public class UserSearchQueryCriteriaConsumer implements Consumer<SearchCriteria>{

    private Predicate predicate;
    private CriteriaBuilder builder;
    private Root r;

    @Override
    public void accept(SearchCriteria param) {
        if (param.getOperation().equalsIgnoreCase(">")) {
            predicate = builder.and(predicate, builder
                  .greaterThanOrEqualTo(r.get(param.getKey()), param.getValue().toString()));
        } else if (param.getOperation().equalsIgnoreCase("<")) {
            predicate = builder.and(predicate, builder.lessThanOrEqualTo(
                  r.get(param.getKey()), param.getValue().toString()));
        } else if (param.getOperation().equalsIgnoreCase(":")) {
            if (r.get(param.getKey()).getJavaType() == String.class) {
                predicate = builder.and(predicate, builder.like(
                      r.get(param.getKey()), "%" + param.getValue() + "%"));
            } else {
                predicate = builder.and(predicate, builder.equal(
                      r.get(param.getKey()), param.getValue()));
            }
        }
    }

    // standard constructor, getter, setter
}
```

如你所见，searchUser API采用非常简单的约束列表，根据这些约束组成查询，进行搜索并返回结果。

约束类也很简单：

```java
public class SearchCriteria {
    private String key;
    private String operation;
    private Object value;
}
```

SearchCriteria实现包含我们的查询参数：

-   key：用于保存字段名称，例如：firstName、age...等。
-   operation：用于保存操作，例如：相等、小于...等。
-   value：用于保存字段值，例如：john、25...等。

## 4. 测试搜索查询

现在，让我们测试我们的搜索机制以确保它站得住脚。

首先，让我们通过添加两个用户来初始化我们的数据库以进行测试，如以下示例所示：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceConfig.class })
@Transactional
@TransactionConfiguration
public class JPACriteriaQueryTest {

    @Autowired
    private IUserDAO userApi;

    private User userJohn;

    private User userTom;

    @Before
    public void init() {
        userJohn = new User();
        userJohn.setFirstName("John");
        userJohn.setLastName("Doe");
        userJohn.setEmail("john@doe.com");
        userJohn.setAge(22);
        userApi.save(userJohn);

        userTom = new User();
        userTom.setFirstName("Tom");
        userTom.setLastName("Doe");
        userTom.setEmail("tom@doe.com");
        userTom.setAge(26);
        userApi.save(userTom);
    }
}
```

现在，让我们获取具有特定名字和姓氏的用户——如以下示例所示：

```java
@Test
public void givenFirstAndLastName_whenGettingListOfUsers_thenCorrect() {
    List<SearchCriteria> params = new ArrayList<SearchCriteria>();
    params.add(new SearchCriteria("firstName", ":", "John"));
    params.add(new SearchCriteria("lastName", ":", "Doe"));

    List<User> results = userApi.searchUser(params);

    assertThat(userJohn, isIn(results));
    assertThat(userTom, not(isIn(results)));
}
```

接下来，让我们获取具有相同lastName的用户列表：

```java
@Test
public void givenLast_whenGettingListOfUsers_thenCorrect() {
    List<SearchCriteria> params = new ArrayList<SearchCriteria>();
    params.add(new SearchCriteria("lastName", ":", "Doe"));

    List<User> results = userApi.searchUser(params);
    assertThat(userJohn, isIn(results));
    assertThat(userTom, isIn(results));
}
```

接下来，让我们获取年龄大于或等于25岁的用户：

```java
@Test
public void givenLastAndAge_whenGettingListOfUsers_thenCorrect() {
    List<SearchCriteria> params = new ArrayList<SearchCriteria>();
    params.add(new SearchCriteria("lastName", ":", "Doe"));
    params.add(new SearchCriteria("age", ">", "25"));

    List<User> results = userApi.searchUser(params);

    assertThat(userTom, isIn(results));
    assertThat(userJohn, not(isIn(results)));
}
```

接下来，让我们搜索实际上不存在的用户：

```java
@Test
public void givenWrongFirstAndLast_whenGettingListOfUsers_thenCorrect() {
    List<SearchCriteria> params = new ArrayList<SearchCriteria>();
    params.add(new SearchCriteria("firstName", ":", "Adam"));
    params.add(new SearchCriteria("lastName", ":", "Fox"));

    List<User> results = userApi.searchUser(params);
    assertThat(userJohn, not(isIn(results)));
    assertThat(userTom, not(isIn(results)));
}
```

最后，让我们搜索仅给出部分firstName的用户：

```java
@Test
public void givenPartialFirst_whenGettingListOfUsers_thenCorrect() {
    List<SearchCriteria> params = new ArrayList<SearchCriteria>();
    params.add(new SearchCriteria("firstName", ":", "jo"));

    List<User> results = userApi.searchUser(params);

    assertThat(userJohn, isIn(results));
    assertThat(userTom, not(isIn(results)));
}
```

## 6. UserController

最后，现在让我们将对这种灵活搜索的持久性支持连接到我们的REST API。

我们将设置一个简单的UserController，使用findAll()使用“search”来传递整个搜索/过滤器表达式：

```java
@Controller
public class UserController {

    @Autowired
    private IUserDao api;

    @RequestMapping(method = RequestMethod.GET, value = "/users")
    @ResponseBody
    public List<User> findAll(@RequestParam(value = "search", required = false) String search) {
        List<SearchCriteria> params = new ArrayList<SearchCriteria>();
        if (search != null) {
            Pattern pattern = Pattern.compile("(\w+?)(:|<|>)(\w+?),");
            Matcher matcher = pattern.matcher(search + ",");
            while (matcher.find()) {
                params.add(new SearchCriteria(matcher.group(1),
                      matcher.group(2), matcher.group(3)));
            }
        }
        return api.searchUser(params);
    }
}
```

请注意我们是如何从搜索表达式中简单地创建我们的搜索条件对象的。

我们现在可以开始使用API并确保一切正常工作：

```bash
http://localhost:8080/users?search=lastName:doe,age>25
```

这是它的响应：

```json
[{
    "id":2,
    "firstName":"tom",
    "lastName":"doe",
    "email":"tom@doe.com",
    "age":26
}]
```

## 7. 总结

这个简单而强大的实现可以在REST API上实现相当多的智能过滤。是的，它的边缘仍然很粗糙并且可以改进(并将在下一篇文章中改进)，但它是在你的API上实现这种过滤功能的可靠起点。