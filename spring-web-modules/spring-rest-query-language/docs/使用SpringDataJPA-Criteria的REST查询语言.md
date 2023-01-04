## 1. 概述

在本教程中，我们将使用Spring Data JPA和规范构建一个搜索/过滤器REST API。

[在本系列的](https://www.baeldung.com/spring-rest-api-query-search-language-tutorial)[第一篇文章](https://www.baeldung.com/rest-search-language-spring-jpa-criteria)中，我们使用基于JPA Criteria的解决方案开始研究查询语言。

那么，为什么要使用查询语言呢？因为通过非常简单的字段搜索/过滤我们的资源对于过于复杂的API来说是不够的。查询语言更灵活，允许我们精确过滤到我们需要的资源。

## 2. 用户实体

首先，让我们从搜索API的简单用户实体开始：

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
    
    // standard getters and setters
}
```

## 3. 过滤使用规范

现在让我们直接进入问题中最有趣的部分：使用自定义Spring Data JPA规范进行查询。

我们将创建一个实现Specification接口的UserSpecification，我们将传入我们自己的约束来构造实际的查询：

```java
public class UserSpecification implements Specification<User> {

    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate
          (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        if (criteria.getOperation().equalsIgnoreCase(">")) {
            return builder.greaterThanOrEqualTo(
                  root.<String> get(criteria.getKey()), criteria.getValue().toString());
        }
        else if (criteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lessThanOrEqualTo(
                  root.<String> get(criteria.getKey()), criteria.getValue().toString());
        }
        else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                      root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return null;
    }
}
```

如我们所见，我们根据在以下SearchCriteria类中表示的一些简单约束创建了规范：

```java
public class SearchCriteria {
    private String key;
    private String operation;
    private Object value;
}
```

SearchCriteria实现包含约束的基本表示，我们将基于此约束构造查询：

-   key：字段名称，例如firstName、age等。
-   operation：操作，例如相等、小于等。
-   value：字段值，例如john、25等。

当然，实现很简单，可以改进。但是，它是我们需要的强大而灵活的操作的坚实基础。

## 4. UserRepository

接下来，让我们看一下UserRepository。

我们只是简单地扩展JpaSpecificationExecutor以获得新的规范API：

```java
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {}
```

## 5. 测试搜索查询

现在让我们测试新的搜索API。

首先，让我们创建一些用户，让他们在测试运行时做好准备：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceJPAConfig.class })
@Transactional
@TransactionConfiguration
public class JPASpecificationIntegrationTest {

    @Autowired
    private UserRepository repository;

    private User userJohn;
    private User userTom;

    @Before
    public void init() {
        userJohn = new User();
        userJohn.setFirstName("John");
        userJohn.setLastName("Doe");
        userJohn.setEmail("john@doe.com");
        userJohn.setAge(22);
        repository.save(userJohn);

        userTom = new User();
        userTom.setFirstName("Tom");
        userTom.setLastName("Doe");
        userTom.setEmail("tom@doe.com");
        userTom.setAge(26);
        repository.save(userTom);
    }
}
```

接下来，让我们看看如何查找具有给定姓氏的用户：

```java
@Test
public void givenLast_whenGettingListOfUsers_thenCorrect() {
    UserSpecification spec = new UserSpecification(new SearchCriteria("lastName", ":", "doe"));
    
    List<User> results = repository.findAll(spec);

    assertThat(userJohn, isIn(results));
    assertThat(userTom, isIn(results));
}
```

现在我们将找到一个同时具有名字和姓氏的用户：

```java
@Test
public void givenFirstAndLastName_whenGettingListOfUsers_thenCorrect() {
    UserSpecification spec1 = new UserSpecification(new SearchCriteria("firstName", ":", "john"));
    UserSpecification spec2 = new UserSpecification(new SearchCriteria("lastName", ":", "doe"));
    
    List<User> results = repository.findAll(Specification.where(spec1).and(spec2));

    assertThat(userJohn, isIn(results));
    assertThat(userTom, not(isIn(results)));
}
```

注意：我们使用where和and来组合Specifications。

接下来，让我们找到一个同时具有姓氏和最小年龄的用户：

```java
@Test
public void givenLastAndAge_whenGettingListOfUsers_thenCorrect() {
    UserSpecification spec1 = new UserSpecification(new SearchCriteria("age", ">", "25"));
    UserSpecification spec2 = new UserSpecification(new SearchCriteria("lastName", ":", "doe"));

    List<User> results = repository.findAll(Specification.where(spec1).and(spec2));

    assertThat(userTom, isIn(results));
    assertThat(userJohn, not(isIn(results)));
}
```

现在我们将看到如何搜索实际上不存在的用户：

```java
@Test
public void givenWrongFirstAndLast_whenGettingListOfUsers_thenCorrect() {
    UserSpecification spec1 = new UserSpecification(new SearchCriteria("firstName", ":", "Adam"));
    UserSpecification spec2 = new UserSpecification(new SearchCriteria("lastName", ":", "Fox"));

    List<User> results = repository.findAll(Specification.where(spec1).and(spec2));

    assertThat(userJohn, not(isIn(results)));
    assertThat(userTom, not(isIn(results)));  
}
```

最后，我们会找到一个只给出部分名字的用户：

```java
@Test
public void givenPartialFirst_whenGettingListOfUsers_thenCorrect() {
    UserSpecification spec = new UserSpecification(new SearchCriteria("firstName", ":", "jo"));
    
    List<User> results = repository.findAll(spec);

    assertThat(userJohn, isIn(results));
    assertThat(userTom, not(isIn(results)));
}
```

## 6. 组合规范

接下来，让我们看看结合我们的自定义规范来使用多个约束并根据多个条件进行过滤。

我们将实施一个构建器UserSpecificationsBuilder以轻松流畅地组合规范，但在让我们检查SpecSearchCriteria对象之前：

```java
public class SpecSearchCriteria {

    private String key;
    private SearchOperation operation;
    private Object value;
    private boolean orPredicate;

    public boolean isOrPredicate() {
        return orPredicate;
    }
}
```

```java
public class UserSpecificationsBuilder {

    private final List<SpecSearchCriteria> params;

    public UserSpecificationsBuilder() {
        params = new ArrayList<>();
    }

    public final UserSpecificationsBuilder with(String key, String operation, Object value,
                                                String prefix, String suffix) {
        return with(null, key, operation, value, prefix, suffix);
    }

    public final UserSpecificationsBuilder with(String orPredicate, String key, String operation,
                                                Object value, String prefix, String suffix) {
        SearchOperation op = SearchOperation.getSimpleOperation(operation.charAt(0));
        if (op != null) {
            if (op == SearchOperation.EQUALITY) { // the operation may be complex operation
                boolean startWithAsterisk = prefix != null &&
                      prefix.contains(SearchOperation.ZERO_OR_MORE_REGEX);
                boolean endWithAsterisk = suffix != null &&
                      suffix.contains(SearchOperation.ZERO_OR_MORE_REGEX);

                if (startWithAsterisk && endWithAsterisk) {
                    op = SearchOperation.CONTAINS;
                } else if (startWithAsterisk) {
                    op = SearchOperation.ENDS_WITH;
                } else if (endWithAsterisk) {
                    op = SearchOperation.STARTS_WITH;
                }
            }
            params.add(new SpecSearchCriteria(orPredicate, key, op, value));
        }
        return this;
    }

    public Specification build() {
        if (params.size() == 0)
            return null;

        Specification result = new UserSpecification(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = params.get(i).isOrPredicate()
                  ? Specification.where(result).or(new UserSpecification(params.get(i)))
                  : Specification.where(result).and(new UserSpecification(params.get(i)));
        }

        return result;
    }
}
```

## 7. UserController

最后，让我们使用这个新的持久性搜索/过滤器功能，并通过创建一个具有简单搜索操作的UserController来设置REST API：

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
            Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
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

请注意，为了支持其他非英语系统，可以更改Pattern对象：

```java
Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),", Pattern.UNICODE_CHARACTER_CLASS);
```

这是一个测试API的测试URL：

```bash
http://localhost:8080/users?search=lastName:doe,age>25
```

这是响应：

```json
[{
    "id":2,
    "firstName":"tom",
    "lastName":"doe",
    "email":"tom@doe.com",
    "age":26
}]
```

由于在我们的模式示例中搜索由“,”分隔，因此搜索词不能包含此字符。该模式也不匹配空格。

如果我们要搜索包含逗号的值，我们可以考虑使用不同的分隔符，例如“;”。

另一种选择是更改模式以搜索引号之间的值，然后从搜索词中删除这些值：

```java
Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\"([^\"]+)\")");
```

## 8. 总结

本文介绍了一个简单的实现，它可以作为强大的REST查询语言的基础。

我们充分利用了Spring Data Specifications来确保我们让API远离域，并可以选择处理许多其他类型的操作。