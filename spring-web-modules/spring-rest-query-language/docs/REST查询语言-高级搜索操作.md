## 1. 概述

在本文中，我们将扩展在[本系列](https://www.baeldung.com/spring-rest-api-query-search-language-tutorial)[前几部分](https://www.baeldung.com/rest-api-search-language-spring-data-specifications)中开发的REST查询语言，以包含更多搜索操作。

我们现在支持以下操作：等于、否定、大于、小于、开始于、结束于、包含和类似。

请注意，我们探索了三种实现-JPA Criteria、Spring Data JPA Specifications和Query DSL；我们在本文中继续使用规范，因为它是一种干净灵活的方式来表示我们的操作。

## 2. SearchOperation枚举

首先——让我们开始定义我们支持的各种搜索操作的更好表示——通过枚举：

```java
public enum SearchOperation {
    EQUALITY, NEGATION, GREATER_THAN, LESS_THAN, LIKE, STARTS_WITH, ENDS_WITH, CONTAINS;

    public static final String[] SIMPLE_OPERATION_SET = {":", "!", ">", "<", "~"};

    public static SearchOperation getSimpleOperation(char input) {
        switch (input) {
            case ':':
                return EQUALITY;
            case '!':
                return NEGATION;
            case '>':
                return GREATER_THAN;
            case '<':
                return LESS_THAN;
            case '~':
                return LIKE;
            default:
                return null;
        }
    }
}
```

我们有两组操作：

1.简单-可以用一个字符来表示：

-   相等性：用冒号(:)表示
-   否定：用感叹号(!)表示
-   大于：用(>)表示
-   小于：用(<)表示
-   喜欢：由代字号(~)表示

2.复杂-需要不止一个字符来表示：

-   开头为：由(=prefix*)表示
-   结束于：由(=*suffix)表示
-   包含：由(=*substring)表示

我们还需要修改我们的SearchCriteria类以使用新的SearchOperation：

```java
public class SearchCriteria {
    private String key;
    private SearchOperation operation;
    private Object value;
}
```

## 3. 修改UserSpecification

现在，让我们将新支持的操作包含到我们的UserSpecification实现中：

```java
public class UserSpecification implements Specification<User> {

    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate(
          Root<User> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        switch (criteria.getOperation()) {
            case EQUALITY:
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION:
                return builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN:
                return builder.greaterThan(root.<String> get(
                      criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN:
                return builder.lessThan(root.<String> get(
                      criteria.getKey()), criteria.getValue().toString());
            case LIKE:
                return builder.like(root.<String> get(
                      criteria.getKey()), criteria.getValue().toString());
            case STARTS_WITH:
                return builder.like(root.<String> get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH:
                return builder.like(root.<String> get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS:
                return builder.like(root.<String> get(
                      criteria.getKey()), "%" + criteria.getValue() + "%");
            default:
                return null;
        }
    }
}
```

## 4. 持久层测试

接下来，我们让我们在持久层级别测试我们的新搜索操作：

### 4.1 测试相等

在以下示例中，我们将按用户的名字和姓氏搜索用户：

```java
@Test
public void givenFirstAndLastName_whenGettingListOfUsers_thenCorrect() {
    UserSpecification spec = new UserSpecification(new SearchCriteria("firstName", SearchOperation.EQUALITY, "john"));
    UserSpecification spec1 = new UserSpecification(new SearchCriteria("lastName", SearchOperation.EQUALITY, "doe"));
    List<User> results = repository.findAll(Specification.where(spec).and(spec1));

    assertThat(userJohn, isIn(results));
    assertThat(userTom, not(isIn(results)));
}
```

### 4.2 测试否定

接下来，让我们搜索名字不是“john”的用户：

```java
@Test
public void givenFirstNameInverse_whenGettingListOfUsers_thenCorrect() {
    UserSpecification spec = new UserSpecification(new SearchCriteria("firstName", SearchOperation.NEGATION, "john"));
    List<User> results = repository.findAll(Specification.where(spec));

    assertThat(userTom, isIn(results));
    assertThat(userJohn, not(isIn(results)));
}
```

### 4.3 测试大于

接下来，我们将搜索年龄大于“25”的用户：

```java
@Test
public void givenMinAge_whenGettingListOfUsers_thenCorrect() {
    UserSpecification spec = new UserSpecification(new SearchCriteria("age", SearchOperation.GREATER_THAN, "25"));
    List<User> results = repository.findAll(Specification.where(spec));

    assertThat(userTom, isIn(results));
    assertThat(userJohn, not(isIn(results)));
}
```

### 4.4 测试开始于

接下来，名字以“jo”开头的用户：

```java
@Test
public void givenFirstNamePrefix_whenGettingListOfUsers_thenCorrect() {
    UserSpecification spec = new UserSpecification(new SearchCriteria("firstName", SearchOperation.STARTS_WITH, "jo"));
    List<User> results = repository.findAll(spec);

    assertThat(userJohn, isIn(results));
    assertThat(userTom, not(isIn(results)));
}
```

### 4.5 测试结束于

接下来我们将搜索名字以“n”结尾的用户：

```java
@Test
public void givenFirstNameSuffix_whenGettingListOfUsers_thenCorrect() {
    UserSpecification spec = new UserSpecification(new SearchCriteria("firstName", SearchOperation.ENDS_WITH, "n"));
    List<User> results = repository.findAll(spec);

    assertThat(userJohn, isIn(results));
    assertThat(userTom, not(isIn(results)));
}
```

### 4.6 测试包含

现在，我们将搜索名字中包含“oh”的用户：

```java
@Test
public void givenFirstNameSubstring_whenGettingListOfUsers_thenCorrect() {
    UserSpecification spec = new UserSpecification(new SearchCriteria("firstName", SearchOperation.CONTAINS, "oh"));
    List<User> results = repository.findAll(spec);

    assertThat(userJohn, isIn(results));
    assertThat(userTom, not(isIn(results)));
}
```

### 4.7 测试范围

最后，我们将搜索年龄在“20”到“25”之间的用户：

```java
@Test
public void givenAgeRange_whenGettingListOfUsers_thenCorrect() {
    UserSpecification spec = new UserSpecification(new SearchCriteria("age", SearchOperation.GREATER_THAN, "20"));
    UserSpecification spec1 = new UserSpecification(new SearchCriteria("age", SearchOperation.LESS_THAN, "25"));
    List<User> results = repository.findAll(Specification.where(spec).and(spec1));

    assertThat(userJohn, isIn(results));
    assertThat(userTom, not(isIn(results)));
}
```

## 5. UserSpecificationBuilder

现在持久化已经完成并经过测试，让我们将注意力转移到Web层。

我们将构建在上一篇文章中的UserSpecificationBuilder实现之上，以合并新的新搜索操作：

```java
public class UserSpecificationsBuilder {

    private List<SearchCriteria> params;

    public UserSpecificationsBuilder with(
          String key, String operation, Object value, String prefix, String suffix) {

        SearchOperation op = SearchOperation.getSimpleOperation(operation.charAt(0));
        if (op != null) {
            if (op == SearchOperation.EQUALITY) {
                boolean startWithAsterisk = prefix.contains("*");
                boolean endWithAsterisk = suffix.contains("*");

                if (startWithAsterisk && endWithAsterisk) {
                    op = SearchOperation.CONTAINS;
                } else if (startWithAsterisk) {
                    op = SearchOperation.ENDS_WITH;
                } else if (endWithAsterisk) {
                    op = SearchOperation.STARTS_WITH;
                }
            }
            params.add(new SearchCriteria(key, op, value));
        }
        return this;
    }

    public Specification<User> build() {
        if (params.size() == 0) {
            return null;
        }

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

## 6. UserController

接下来，我们需要修改我们的UserController以正确解析新操作：

```java
@RequestMapping(method = RequestMethod.GET, value = "/users")
@ResponseBody
public List<User> findAllBySpecification(@RequestParam(value = "search") String search) {
    UserSpecificationsBuilder builder = new UserSpecificationsBuilder();
    String operationSetExper = Joiner.on("|").join(SearchOperation.SIMPLE_OPERATION_SET);
    Pattern pattern = Pattern.compile("(\\w+?)(" + operationSetExper + ")(\p{Punct}?)(\\w+?)(\p{Punct}?),");
    Matcher matcher = pattern.matcher(search + ",");
    while (matcher.find()) {
        builder.with(
            matcher.group(1), 
            matcher.group(2), 
            matcher.group(4), 
            matcher.group(3), 
            matcher.group(5));
    }

    Specification<User> spec = builder.build();
    return dao.findAll(spec);
}
```

我们现在可以访问API并使用任意条件组合返回正确的结果。例如，这是一个使用带有查询语言的API的复杂操作：

```bash
http://localhost:8080/users?search=firstName:jo*,age<25
```

和响应：

```json
[{
    "id":1,
    "firstName":"john",
    "lastName":"doe",
    "email":"john@doe.com",
    "age":24
}]
```

## 7. 搜索API测试

最后，让我们通过编写一套API测试来确保我们的API运行良好。

我们将从测试的简单配置和数据初始化开始：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
      classes = { ConfigTest.class, PersistenceConfig.class },
      loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class JPASpecificationLiveTest {

    @Autowired
    private UserRepository repository;

    private User userJohn;
    private User userTom;

    private final String URL_PREFIX = "http://localhost:8080/users?search=";

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

    private RequestSpecification givenAuth() {
        return RestAssured.given().auth()
              .preemptive()
              .basic("username", "password");
    }
}
```

### 7.1 测试相等

首先，让我们搜索名字为“john”、姓氏为“doe”的用户：

```java
@Test
public void givenFirstAndLastName_whenGettingListOfUsers_thenCorrect() {
    Response response = givenAuth().get(URL_PREFIX + "firstName:john,lastName:doe");
    String result = response.body().asString();

    assertTrue(result.contains(userJohn.getEmail()));
    assertFalse(result.contains(userTom.getEmail()));
}
```

### 7.2 测试否定

现在，我们将搜索名字不是“john”的用户：

```java
@Test
public void givenFirstNameInverse_whenGettingListOfUsers_thenCorrect() {
    Response response = givenAuth().get(URL_PREFIX + "firstName!john");
    String result = response.body().asString();

    assertTrue(result.contains(userTom.getEmail()));
    assertFalse(result.contains(userJohn.getEmail()));
}
```

### 7.3 测试大于

接下来，我们将寻找年龄大于“25”的用户：

```java
@Test
public void givenMinAge_whenGettingListOfUsers_thenCorrect() {
    Response response = givenAuth().get(URL_PREFIX + "age>25");
    String result = response.body().asString();

    assertTrue(result.contains(userTom.getEmail()));
    assertFalse(result.contains(userJohn.getEmail()));
}
```

### 7.4 测试开始于

接下来，名字以“jo”开头的用户：

```java
@Test
public void givenFirstNamePrefix_whenGettingListOfUsers_thenCorrect() {
    Response response = givenAuth().get(URL_PREFIX + "firstName:jo*");
    String result = response.body().asString();

    assertTrue(result.contains(userJohn.getEmail()));
    assertFalse(result.contains(userTom.getEmail()));
}
```

### 7.5 测试结束于

现在，名字以“n”结尾的用户：

```java
@Test
public void givenFirstNameSuffix_whenGettingListOfUsers_thenCorrect() {
    Response response = givenAuth().get(URL_PREFIX + "firstName:*n");
    String result = response.body().asString();

    assertTrue(result.contains(userJohn.getEmail()));
    assertFalse(result.contains(userTom.getEmail()));
}
```

### 7.6 测试包含

接下来，我们将搜索名字中包含“oh”的用户：

```java
@Test
public void givenFirstNameSubstring_whenGettingListOfUsers_thenCorrect() {
    Response response = givenAuth().get(URL_PREFIX + "firstName:*oh*");
    String result = response.body().asString();

    assertTrue(result.contains(userJohn.getEmail()));
    assertFalse(result.contains(userTom.getEmail()));
}
```

### 7.7 测试范围

最后，我们将搜索年龄在“20”到“25”之间的用户：

```java
@Test
public void givenAgeRange_whenGettingListOfUsers_thenCorrect() {
    Response response = givenAuth().get(URL_PREFIX + "age>20,age<25");
    String result = response.body().asString();

    assertTrue(result.contains(userJohn.getEmail()));
    assertFalse(result.contains(userTom.getEmail()));
}
```

## 8. 总结

在本文中，我们将REST搜索API的查询语言推进到成熟的、经过测试的生产级实现。我们现在支持各种各样的操作和约束，这应该使得优雅地跨越任何数据集并获得我们正在寻找的确切资源变得非常容易。