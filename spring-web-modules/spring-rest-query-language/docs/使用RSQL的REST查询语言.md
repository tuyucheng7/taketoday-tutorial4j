## 1. 概述

在本[系列](https://www.baeldung.com/spring-rest-api-query-search-language-tutorial)的第五篇文章中，我们将说明如何借助一个很酷的库-[rsql-parser](https://github.com/jirutka/rsql-parser)来构建REST API查询语言。

RSQL是Feed Item Query Language([FIQL](https://tools.ietf.org/html/draft-nottingham-atompub-fiql-00))的超集-一种干净简单的feed过滤器语法；所以它很自然地适合REST API。

## 2. 准备工作

首先，让我们向库中添加一个 Maven[依赖项](https://search.maven.org/artifact/cz.jirutka.rsql/rsql-parser)：

```xml
<dependency>
    <groupId>cz.jirutka.rsql</groupId>
    <artifactId>rsql-parser</artifactId>
    <version>2.1.0</version>
</dependency>
```

并且还定义了我们将在整个示例中使用的主要实体-User：

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

## 3. 解析请求

RSQL表达式在内部表示的方式是节点的形式，访问者模式用于解析输入。

考虑到这一点，我们将实现[RSQLVisitor接口](https://github.com/jirutka/rsql-parser/blob/master/src/main/java/cz/jirutka/rsql/parser/ast/RSQLVisitor.java)并创建我们自己的访问者实现CustomRsqlVisitor：

```java
public class CustomRsqlVisitor<T> implements RSQLVisitor<Specification<T>, Void> {

    private GenericRsqlSpecBuilder<T> builder;

    public CustomRsqlVisitor() {
        builder = new GenericRsqlSpecBuilder<T>();
    }

    @Override
    public Specification<T> visit(AndNode node, Void param) {
        return builder.createSpecification(node);
    }

    @Override
    public Specification<T> visit(OrNode node, Void param) {
        return builder.createSpecification(node);
    }

    @Override
    public Specification<T> visit(ComparisonNode node, Void params) {
        return builder.createSecification(node);
    }
}
```

现在我们需要处理持久性并从这些节点中的每一个构建我们的查询。

我们将使用[我们之前](https://www.baeldung.com/rest-api-search-language-spring-data-specifications)使用的Spring Data JPA Specifications，我们将实现一个规范构建器来从我们访问的每个节点中构建规范：

```java
public class GenericRsqlSpecBuilder<T> {

    public Specification<T> createSpecification(Node node) {
        if (node instanceof LogicalNode) {
            return createSpecification((LogicalNode) node);
        }
        if (node instanceof ComparisonNode) {
            return createSpecification((ComparisonNode) node);
        }
        return null;
    }

    public Specification<T> createSpecification(LogicalNode logicalNode) {
        List<Specification> specs = logicalNode.getChildren()
              .stream()
              .map(node -> createSpecification(node))
              .filter(Objects::nonNull)
              .collect(Collectors.toList());

        Specification<T> result = specs.get(0);
        if (logicalNode.getOperator() == LogicalOperator.AND) {
            for (int i = 1; i < specs.size(); i++) {
                result = Specification.where(result).and(specs.get(i));
            }
        } else if (logicalNode.getOperator() == LogicalOperator.OR) {
            for (int i = 1; i < specs.size(); i++) {
                result = Specification.where(result).or(specs.get(i));
            }
        }

        return result;
    }

    public Specification<T> createSpecification(ComparisonNode comparisonNode) {
        Specification<T> result = Specification.where(
              new GenericRsqlSpecification<T>(
                    comparisonNode.getSelector(),
                    comparisonNode.getOperator(),
                    comparisonNode.getArguments()
              )
        );
        return result;
    }
}
```

注意如何：

-   LogicalNode是一个AND/OR节点并且有多个子节点
-   ComparisonNode没有子节点，它包含Selector、Operator和Arguments

例如，对于查询“name==john”，我们有：

1.  选择器：“name”
2.  运算符：“==”
3.  参数：[john]

## 4. 创建自定义规范

在构造查询时，我们使用了规范：

```java
public class GenericRsqlSpecification<T> implements Specification<T> {

    private String property;
    private ComparisonOperator operator;
    private List<String> arguments;

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Object> args = castArguments(root);
        Object argument = args.get(0);
        switch (RsqlSearchOperation.getSimpleOperator(operator)) {

            case EQUAL: {
                if (argument instanceof String) {
                    return builder.like(root.get(property), argument.toString().replace('*', '%'));
                } else if (argument == null) {
                    return builder.isNull(root.get(property));
                } else {
                    return builder.equal(root.get(property), argument);
                }
            }
            case NOT_EQUAL: {
                if (argument instanceof String) {
                    return builder.notLike(root.<String> get(property), argument.toString().replace('*', '%'));
                } else if (argument == null) {
                    return builder.isNotNull(root.get(property));
                } else {
                    return builder.notEqual(root.get(property), argument);
                }
            }
            case GREATER_THAN: {
                return builder.greaterThan(root.<String> get(property), argument.toString());
            }
            case GREATER_THAN_OR_EQUAL: {
                return builder.greaterThanOrEqualTo(root.<String> get(property), argument.toString());
            }
            case LESS_THAN: {
                return builder.lessThan(root.<String> get(property), argument.toString());
            }
            case LESS_THAN_OR_EQUAL: {
                return builder.lessThanOrEqualTo(root.<String> get(property), argument.toString());
            }
            case IN:
                return root.get(property).in(args);
            case NOT_IN:
                return builder.not(root.get(property).in(args));
        }

        return null;
    }

    private List<Object> castArguments(final Root<T> root) {

        Class<? extends Object> type = root.get(property).getJavaType();

        List<Object> args = arguments.stream().map(arg -> {
            if (type.equals(Integer.class)) {
                return Integer.parseInt(arg);
            } else if (type.equals(Long.class)) {
                return Long.parseLong(arg);
            } else {
                return arg;
            }
        }).collect(Collectors.toList());

        return args;
    }

    // standard constructor, getter, setter
}
```

请注意规范如何使用泛型并且不绑定到任何特定实体(例如用户)。

接下来——这是我们的枚举“RsqlSearchOperation”，它包含默认的rsql-parser运算符：

```java
public enum RsqlSearchOperation {
    EQUAL(RSQLOperators.EQUAL),
    NOT_EQUAL(RSQLOperators.NOT_EQUAL),
    GREATER_THAN(RSQLOperators.GREATER_THAN),
    GREATER_THAN_OR_EQUAL(RSQLOperators.GREATER_THAN_OR_EQUAL),
    LESS_THAN(RSQLOperators.LESS_THAN),
    LESS_THAN_OR_EQUAL(RSQLOperators.LESS_THAN_OR_EQUAL),
    IN(RSQLOperators.IN),
    NOT_IN(RSQLOperators.NOT_IN);

    private ComparisonOperator operator;

    private RsqlSearchOperation(ComparisonOperator operator) {
        this.operator = operator;
    }

    public static RsqlSearchOperation getSimpleOperator(ComparisonOperator operator) {
        for (RsqlSearchOperation operation : values()) {
            if (operation.getOperator() == operator) {
                return operation;
            }
        }
        return null;
    }
}
```

## 5. 测试搜索查询

现在让我们开始通过一些真实场景测试我们新的灵活操作：

首先，让我们初始化数据：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceConfig.class })
@Transactional
@TransactionConfiguration
public class RsqlTest {

    @Autowired
    private UserRepository repository;

    private User userJohn;

    private User userTom;

    @Before
    public void init() {
        userJohn = new User();
        userJohn.setFirstName("john");
        userJohn.setLastName("doe");
        userJohn.setEmail("john@doe.com");
        userJohn.setAge(22);
        repository.save(userJohn);

        userTom = new User();
        userTom.setFirstName("tom");
        userTom.setLastName("doe");
        userTom.setEmail("tom@doe.com");
        userTom.setAge(26);
        repository.save(userTom);
    }
}
```

现在让我们测试不同的操作：

### 5.1 测试相等

在以下示例中，我们将按用户的名字和姓氏搜索用户：

```java
@Test
public void givenFirstAndLastName_whenGettingListOfUsers_thenCorrect() {
    Node rootNode = new RSQLParser().parse("firstName==john;lastName==doe");
    Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<User>());
    List<User> results = repository.findAll(spec);

    assertThat(userJohn, isIn(results));
    assertThat(userTom, not(isIn(results)));
}
```

### 5.2 测试否定

接下来，让我们搜索名字不是“john”的用户：

```java
@Test
public void givenFirstNameInverse_whenGettingListOfUsers_thenCorrect() {
    Node rootNode = new RSQLParser().parse("firstName!=john");
    Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<User>());
    List<User> results = repository.findAll(spec);

    assertThat(userTom, isIn(results));
    assertThat(userJohn, not(isIn(results)));
}
```

### 5.3 测试大于

接下来，我们将搜索年龄大于“25”的用户：

```java
@Test
public void givenMinAge_whenGettingListOfUsers_thenCorrect() {
    Node rootNode = new RSQLParser().parse("age>25");
    Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<User>());
    List<User> results = repository.findAll(spec);

    assertThat(userTom, isIn(results));
    assertThat(userJohn, not(isIn(results)));
}
```

### 5.4 测试模糊匹配

接下来。我们将搜索名字以“jo”开头的用户：

```java
@Test
public void givenFirstNamePrefix_whenGettingListOfUsers_thenCorrect() {
    Node rootNode = new RSQLParser().parse("firstName==jo*");
    Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<User>());
    List<User> results = repository.findAll(spec);

    assertThat(userJohn, isIn(results));
    assertThat(userTom, not(isIn(results)));
}
```

### 5.5 测试输入

接下来，我们将搜索他们的名字是“john”或“jack”的用户：

```java
@Test
public void givenListOfFirstName_whenGettingListOfUsers_thenCorrect() {
    Node rootNode = new RSQLParser().parse("firstName=in=(john,jack)");
    Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<User>());
    List<User> results = repository.findAll(spec);

    assertThat(userJohn, isIn(results));
    assertThat(userTom, not(isIn(results)));
}
```

## 6. UserController

最后，让我们把它与控制器联系起来：

```java
@RequestMapping(method = RequestMethod.GET, value = "/users")
@ResponseBody
public List<User> findAllByRsql(@RequestParam(value = "search") String search) {
    Node rootNode = new RSQLParser().parse(search);
    Specification<User> spec = rootNode.accept(new CustomRsqlVisitor<User>());
    return dao.findAll(spec);
}
```

这是一个示例网址：

```bash
http://localhost:8080/users?search=firstName==jo*;age<25
```

和响应：

```bash
[{
    "id":1,
    "firstName":"john",
    "lastName":"doe",
    "email":"john@doe.com",
    "age":24
}]
```

## 7. 总结

本教程说明了如何为REST API构建查询/搜索语言，而无需重新发明语法，而是使用FIQL/RSQL。