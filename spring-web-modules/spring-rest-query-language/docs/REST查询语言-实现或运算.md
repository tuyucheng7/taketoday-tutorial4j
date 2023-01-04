## 1. 概述

在这篇快速文章中，我们将扩展我们在[上一篇文章](https://www.baeldung.com/rest-api-query-search-language-more-operations)中实现的高级搜索操作，并将基于OR的搜索条件包含到我们的REST API查询语言中。

## 2. 实现办法

以前，搜索查询参数中的所有条件形成谓词，仅由AND运算符分组。让我们改变它。

我们应该能够将此功能作为对现有方法的简单、快速更改或从头开始的新方法来实现。

使用简单的方法，我们将标记条件以指示它必须使用OR运算符组合。

例如，这里是用于测试“firstName OR lastName” API的URL：

```bash
http://localhost:8080/users?search=firstName:john,'lastName:doe
```

请注意，我们已使用单引号标记条件lastName以区分它。我们将在我们的条件值对象SpecSearchCriteria中为OR运算符捕获此谓词：

```java
public SpecSearchCriteria(String orPredicate, String key, SearchOperation operation, Object value) {
    super();
    
    this.orPredicate = orPredicate != null && orPredicate.equals(SearchOperation.OR_PREDICATE_FLAG);
    
    this.key = key;
    this.operation = operation;
    this.value = value;
}
```

## 3. UserSpecificationBuilder改进

现在，让我们修改规范生成器UserSpecificationBuilder，以在构造Specification<User\>时考虑OR限定条件：

```java
public Specification<User> build() {
    if (params.size() == 0) {
        return null;
    }
    Specification<User> result = new UserSpecification(params.get(0));

    for (int i = 1; i < params.size(); i++) {
        result = params.get(i).isOrPredicate()
          ? Specification.where(result).or(new UserSpecification(params.get(i))) 
          : Specification.where(result).and(new UserSpecification(params.get(i)));
    }
    return result;
 }
```

## 4. UserController改进

最后，让我们在我们的控制器中设置一个新的REST端点，以通过OR运算符使用此搜索功能。改进的解析逻辑提取了特殊标志，有助于使用OR运算符识别条件：

```java
@GetMapping("/users/espec")
@ResponseBody
public List<User> findAllByOrPredicate(@RequestParam String search) {
    Specification<User> spec = resolveSpecification(search);
    return dao.findAll(spec);
}

protected Specification<User> resolveSpecification(String searchParameters) {
    UserSpecificationsBuilder builder = new UserSpecificationsBuilder();
    String operationSetExper = Joiner.on("|").join(SearchOperation.SIMPLE_OPERATION_SET);
    Pattern pattern = Pattern.compile(
      "(\\p{Punct}?)(\\w+?)("
      + operationSetExper 
      + ")(\\p{Punct}?)(\\w+?)(\\p{Punct}?),");
    Matcher matcher = pattern.matcher(searchParameters + ",");
    while (matcher.find()) {
        builder.with(matcher.group(1), matcher.group(2), matcher.group(3), 
        matcher.group(5), matcher.group(4), matcher.group(6));
    }
    
    return builder.build();
}
```

## 5. 带OR条件的实时测试

在此实时测试示例中，使用新的API端点，我们将按名字“john”或姓氏“doe”搜索用户。请注意，参数lastName有一个单引号，将其限定为“OR谓词”：

```java
private String EURL_PREFIX = "http://localhost:8082/spring-rest-full/auth/users/espec?search=";

@Test
public void givenFirstOrLastName_whenGettingListOfUsers_thenCorrect() {
    Response response = givenAuth().get(EURL_PREFIX + "firstName:john,'lastName:doe");
    String result = response.body().asString();

    assertTrue(result.contains(userJohn.getEmail()));
    assertTrue(result.contains(userTom.getEmail()));
}
```

## 6. OR条件下的持久性测试

现在，让我们对名字为“john”或姓氏为“doe”的用户在持久性级别执行与上面相同的测试：

```java
@Test
public void givenFirstOrLastName_whenGettingListOfUsers_thenCorrect() {
    UserSpecificationsBuilder builder = new UserSpecificationsBuilder();

    SpecSearchCriteria spec = new SpecSearchCriteria("firstName", SearchOperation.EQUALITY, "john");
    SpecSearchCriteria spec1 = new SpecSearchCriteria("'","lastName", SearchOperation.EQUALITY, "doe");

    List<User> results = repository.findAll(builder.with(spec).with(spec1).build());

    assertThat(results, hasSize(2));
    assertThat(userJohn, isIn(results));
    assertThat(userTom, isIn(results));
}
```

## 7. 替代方法

在替代方法中，我们可以提供更像是SQL查询的完整WHERE子句的搜索查询。

例如，这是按名字和年龄进行更复杂搜索的URL：

```bash
http://localhost:8080/users?search=( firstName:john OR firstName:tom ) AND age>22
```

请注意，我们用空格分隔了各个条件、运算符和分组括号，以形成有效的中缀表达式。

让我们用CriteriaParser解析中缀表达式，我们的CriteriaParser将给定的中缀表达式拆分为标记(条件、括号、AND & OR运算符)并为其创建一个后缀表达式：

```java
public Deque<?> parse(String searchParam) {

    Deque<Object> output = new LinkedList<>();
    Deque<String> stack = new LinkedList<>();

    Arrays.stream(searchParam.split("\\s+")).forEach(token -> {
        if (ops.containsKey(token)) {
            while (!stack.isEmpty() && isHigerPrecedenceOperator(token, stack.peek())) {
                output.push(stack.pop().equalsIgnoreCase(SearchOperation.OR_OPERATOR)
                  ? SearchOperation.OR_OPERATOR : SearchOperation.AND_OPERATOR);
            }
            stack.push(token.equalsIgnoreCase(SearchOperation.OR_OPERATOR) 
              ? SearchOperation.OR_OPERATOR : SearchOperation.AND_OPERATOR);

        } else if (token.equals(SearchOperation.LEFT_PARANTHESIS)) {
            stack.push(SearchOperation.LEFT_PARANTHESIS);
        } else if (token.equals(SearchOperation.RIGHT_PARANTHESIS)) {
            while (!stack.peek().equals(SearchOperation.LEFT_PARANTHESIS)) { 
                output.push(stack.pop());
            }
            stack.pop();
        } else {
            Matcher matcher = SpecCriteraRegex.matcher(token);
            while (matcher.find()) {
                output.push(new SpecSearchCriteria(
                  matcher.group(1), 
                  matcher.group(2), 
                  matcher.group(3), 
                  matcher.group(4), 
                  matcher.group(5)));
            }
        }
    });

    while (!stack.isEmpty()) {
        output.push(stack.pop());
    }
  
    return output;
}
```

让我们在规范构建器GenericSpecificationBuilder中添加一个新方法，以从后缀表达式构造搜索规范：

```java
public Specification<U> build(Deque<?> postFixedExprStack, Function<SpecSearchCriteria, Specification<U>> converter) {
    Deque<Specification<U>> specStack = new LinkedList<>();

    while (!postFixedExprStack.isEmpty()) {
        Object mayBeOperand = postFixedExprStack.pollLast();

        if (!(mayBeOperand instanceof String)) {
            specStack.push(converter.apply((SpecSearchCriteria) mayBeOperand));
        } else {
            Specification<U> operand1 = specStack.pop();
            Specification<U> operand2 = specStack.pop();
            if (mayBeOperand.equals(SearchOperation.AND_OPERATOR)) {
                specStack.push(Specification.where(operand1)
                    .and(operand2));
            }
            else if (mayBeOperand.equals(SearchOperation.OR_OPERATOR)) {
                specStack.push(Specification.where(operand1)
                    .or(operand2));
            }
        }
    }
    return specStack.pop();
}
```

最后，让我们在UserController中添加另一个REST端点，以使用新的CriteriaParser解析复杂表达式：

```java
@GetMapping("/users/spec/adv")
@ResponseBody
public List<User> findAllByAdvPredicate(@RequestParam String search) {
    Specification<User> spec = resolveSpecificationFromInfixExpr(search);
    return dao.findAll(spec);
}

protected Specification<User> resolveSpecificationFromInfixExpr(String searchParameters) {
    CriteriaParser parser = new CriteriaParser();
    GenericSpecificationsBuilder<User> specBuilder = new GenericSpecificationsBuilder<>();
    return specBuilder.build(parser.parse(searchParameters), UserSpecification::new);
}
```

## 8. 总结

在本教程中，我们改进了REST查询语言，使其能够使用OR运算符进行搜索。