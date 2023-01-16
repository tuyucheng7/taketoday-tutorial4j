## 1. 概述

在本教程中，我们将了解如何在不相关的实体之间构造 JPA 查询。

## 2.Maven依赖

让我们首先向我们的pom.xml添加必要的依赖项。

首先，我们需要为[Java Persistence API](https://search.maven.org/artifact/javax.persistence/javax.persistence-api)添加依赖项：

```xml
<dependency>
   <groupId>javax.persistence</groupId>
   <artifactId>javax.persistence-api</artifactId>
   <version>2.2</version>
</dependency>

```

然后，我们为实现JavaPersistence API的[Hibernate ORM](https://search.maven.org/artifact/org.hibernate/hibernate-core)添加依赖项：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.4.14.Final</version>
</dependency>
```

最后，我们添加一些[QueryDSL](https://www.baeldung.com/querydsl-with-jpa-tutorial)依赖项；即[querydsl-apt](https://search.maven.org/artifact/com.querydsl/querydsl-apt)和[querydsl-jpa](https://search.maven.org/artifact/com.querydsl/querydsl-jpa)：

```xml
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-apt</artifactId>
    <version>4.3.1</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-jpa</artifactId>
    <version>4.3.1</version>
</dependency>

```

## 3.领域模型

我们示例的领域是鸡尾酒吧。这里我们在数据库中有两个表：

-   用于存储我们酒吧出售的鸡尾酒及其价格的菜单表，以及
-   用于存储创建鸡尾酒的说明的食谱表

[![一对一](https://www.baeldung.com/wp-content/uploads/2020/04/one-to-one.png)](https://www.baeldung.com/wp-content/uploads/2020/04/one-to-one.png)

这两个表彼此之间并不严格相关。鸡尾酒可以出现在我们的菜单中，而无需保留其配方说明。此外，我们还可以提供尚未销售的鸡尾酒配方。

在我们的示例中，我们将在我们的菜单上找到我们有可用配方的所有鸡尾酒。

## 4. JPA实体

我们可以轻松地创建两个 JPA 实体来表示我们的表：

```java
@Entity
@Table(name = "menu")
public class Cocktail {
    @Id
    @Column(name = "cocktail_name")
    private String name;

    @Column
    private double price;

    // getters & setters
}

@Entity
@Table(name="recipes")
public class Recipe {
    @Id
    @Column(name = "cocktail")
    private String cocktail;

    @Column
    private String instructions;
    
    // getters & setters
}
```

在菜单表和食谱表之间，存在没有显式外键约束的底层一对一关系。例如，如果我们有一个菜单记录，其cocktail_name列的值为“Mojito”，并且有一个食谱记录，其cocktail_name列的值为“Mojito”，则菜单记录与该食谱记录相关联。

为了在我们的Cocktail实体中表示这种关系，我们添加了带有各种注解的配方字段：

```java
@Entity
@Table(name = "menu")
public class Cocktail {
    // ...
 
    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "cocktail_name", 
       referencedColumnName = "cocktail", 
       insertable = false, updatable = false, 
       foreignKey = @javax.persistence
         .ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Recipe recipe;
   
    // ...
}
```

第一个注解是[@OneToOne](https://www.baeldung.com/jpa-one-to-one)，它声明与Recipe实体的底层一对一关系。

接下来，我们使用@NotFound(action = NotFoundAction.IGNORE) Hibernate 注解来注解食谱字段。这告诉我们的 ORM 在菜单表中不存在鸡尾酒配方时不要抛出异常。

将Cocktail与其关联的Recipe相关联的注解是[@JoinColumn](https://www.baeldung.com/jpa-join-column)。通过使用此注解，我们定义了两个实体之间的伪外键关系。

最后，通过将foreignKey属性设置为@javax.persistence.ForeignKey(value = ConstraintMode.NO_CONSTRAINT)，我们指示 JPA 提供程序不生成外键约束。

## 5. JPA 和 QueryDSL 查询

由于我们有兴趣检索与 Recipe 关联的Cocktail实体，因此我们可以通过将 Cocktail实体与其关联的Recipe实体连接起来来查询Cocktail实体。

我们构造查询的一种方法是使用[JPQL](https://docs.oracle.com/html/E13946_04/ejb3_langref.html)：

```java
entityManager.createQuery("select c from Cocktail c join c.recipe")
```

或者使用 QueryDSL 框架：

```java
new JPAQuery<Cocktail>(entityManager)
  .from(QCocktail.cocktail)
  .join(QCocktail.cocktail.recipe)
```

获得所需结果的另一种方法是将Cocktail与Recipe实体结合起来，并使用 on子句直接定义查询中的基础关系。

我们可以使用 JPQL 来做到这一点：

```java
entityManager.createQuery("select c from Cocktail c join Recipe r on c.name = r.cocktail")
```

或者使用 QueryDSL 框架：

```java
new JPAQuery(entityManager)
  .from(QCocktail.cocktail)
  .join(QRecipe.recipe)
  .on(QCocktail.cocktail.name.eq(QRecipe.recipe.cocktail))
```

## 6.一对一加入单元测试

让我们开始创建用于测试上述查询的单元测试。在我们的测试用例运行之前，我们必须将一些数据插入到我们的数据库表中。

```java
public class UnrelatedEntitiesUnitTest {
    // ...

    @BeforeAll
    public static void setup() {
        // ...

        mojito = new Cocktail();
        mojito.setName("Mojito");
        mojito.setPrice(12.12);
        ginTonic = new Cocktail();
        ginTonic.setName("Gin tonic");
        ginTonic.setPrice(10.50);
        Recipe mojitoRecipe = new Recipe(); 
        mojitoRecipe.setCocktail(mojito.getName()); 
        mojitoRecipe.setInstructions("Some instructions for making a mojito cocktail!");
        entityManager.persist(mojito);
        entityManager.persist(ginTonic);
        entityManager.persist(mojitoRecipe);
      
        // ...
    }

    // ... 
}
```

在设置方法中，我们保存了两个Cocktail实体，mojito和ginTonic。然后，我们添加如何制作“Mojito”鸡尾酒的配方。

现在，我们可以测试上一节的查询结果。我们知道只有莫吉托鸡尾酒有关联的Recipe实体，所以我们希望各种查询只返回莫吉托鸡尾酒：

```java
public class UnrelatedEntitiesUnitTest {
    // ...

    @Test
    public void givenCocktailsWithRecipe_whenQuerying_thenTheExpectedCocktailsReturned() {
        // JPA
        Cocktail cocktail = entityManager.createQuery("select c " +
          "from Cocktail c join c.recipe", Cocktail.class)
          .getSingleResult();
        verifyResult(mojito, cocktail);

        cocktail = entityManager.createQuery("select c " +
          "from Cocktail c join Recipe r " +
          "on c.name = r.cocktail", Cocktail.class).getSingleResult();
        verifyResult(mojito, cocktail);

        // QueryDSL
        cocktail = new JPAQuery<Cocktail>(entityManager).from(QCocktail.cocktail)
          .join(QCocktail.cocktail.recipe)
          .fetchOne();
        verifyResult(mojito, cocktail);

        cocktail = new JPAQuery<Cocktail>(entityManager).from(QCocktail.cocktail)
          .join(QRecipe.recipe)
          .on(QCocktail.cocktail.name.eq(QRecipe.recipe.cocktail))
          .fetchOne();
        verifyResult(mojito, cocktail);
    }

    private void verifyResult(Cocktail expectedCocktail, Cocktail queryResult) {
        assertNotNull(queryResult);
        assertEquals(expectedCocktail, queryResult);
    }

    // ...
}

```

verifyResult方法帮助我们验证查询返回的结果是否等于预期结果。

## 7. 一对多的底层关系

让我们更改示例的域，以展示如何将具有一对多基础关系的两个实体连接起来。

[![一对多](https://www.baeldung.com/wp-content/uploads/2020/04/one-to-many.png)](https://www.baeldung.com/wp-content/uploads/2020/04/one-to-many.png)
我们有multiple_recipes表， 而不是recipes表，我们可以在其中存储相同鸡尾酒所需的任意数量的食谱。

```java
@Entity
@Table(name = "multiple_recipes")
public class MultipleRecipe {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "cocktail")
    private String cocktail;

    @Column(name = "instructions")
    private String instructions;

    // getters & setters
}
```

现在，Cocktail实体通过一对多的基础关系与MultipleRecipe实体相关联：

```java
@Entity
@Table(name = "cocktails")
public class Cocktail {    
    // ...

    @OneToMany
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(
       name = "cocktail", 
       referencedColumnName = "cocktail_name", 
       insertable = false, 
       updatable = false, 
       foreignKey = @javax.persistence
         .ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private List<MultipleRecipe> recipeList;

    // getters & setters
}

```

要查找并获取我们至少有一个可用的MultipleRecipe 的 Cocktail 实体，我们可以通过将Cocktail实体与其关联的MultipleRecipe实体连接来查询它。

我们可以使用 JPQL 来做到这一点：

```java
entityManager.createQuery("select c from Cocktail c join c.recipeList");
```

或者使用 QueryDSL 框架：

```java
new JPAQuery(entityManager).from(QCocktail.cocktail)
  .join(QCocktail.cocktail.recipeList);
```

还可以选择不使用recipeList字段，该字段定义Cocktail和MultipleRecipe实体之间的一对多关系。相反，我们可以为两个实体编写一个连接查询，并使用 JPQL“on”子句确定它们的基础关系：

```java
entityManager.createQuery("select c "
  + "from Cocktail c join MultipleRecipe mr "
  + "on mr.cocktail = c.name");
```

最后，我们可以使用 QueryDSL 框架构造相同的查询：

```java
new JPAQuery(entityManager).from(QCocktail.cocktail)
  .join(QMultipleRecipe.multipleRecipe)
  .on(QCocktail.cocktail.name.eq(QMultipleRecipe.multipleRecipe.cocktail));

```

## 8.一对多连接单元测试

在这里，我们将添加一个新的测试用例来测试以前的查询。在这样做之前，我们必须在设置方法中保留一些MultipleRecipe实例：

```java
public class UnrelatedEntitiesUnitTest {    
    // ...

    @BeforeAll
    public static void setup() {
        // ...
        
        MultipleRecipe firstMojitoRecipe = new MultipleRecipe();
        firstMojitoRecipe.setId(1L);
        firstMojitoRecipe.setCocktail(mojito.getName());
        firstMojitoRecipe.setInstructions("The first recipe of making a mojito!");
        entityManager.persist(firstMojitoRecipe);
        MultipleRecipe secondMojitoRecipe = new MultipleRecipe();
        secondMojitoRecipe.setId(2L);
        secondMojitoRecipe.setCocktail(mojito.getName());
        secondMojitoRecipe.setInstructions("The second recipe of making a mojito!"); 
        entityManager.persist(secondMojitoRecipe);
       
        // ...
    }

    // ... 
}

```

然后我们可以开发一个测试用例，我们在其中验证当我们在上一节中展示的查询被执行时，它们返回与至少一个MultipleRecipe实例关联的Cocktail 实体：

```java
public class UnrelatedEntitiesUnitTest {
    // ...
    
    @Test
    public void givenCocktailsWithMultipleRecipes_whenQuerying_thenTheExpectedCocktailsReturned() {
        // JPQL
        Cocktail cocktail = entityManager.createQuery("select c "
          + "from Cocktail c join c.recipeList", Cocktail.class)
          .getSingleResult();
        verifyResult(mojito, cocktail);

        cocktail = entityManager.createQuery("select c "
          + "from Cocktail c join MultipleRecipe mr "
          + "on mr.cocktail = c.name", Cocktail.class)
          .getSingleResult();
        verifyResult(mojito, cocktail);

        // QueryDSL
        cocktail = new JPAQuery<Cocktail>(entityManager).from(QCocktail.cocktail)
          .join(QCocktail.cocktail.recipeList)
          .fetchOne();
        verifyResult(mojito, cocktail);

        cocktail = new JPAQuery<Cocktail>(entityManager).from(QCocktail.cocktail)
          .join(QMultipleRecipe.multipleRecipe)
          .on(QCocktail.cocktail.name.eq(QMultipleRecipe.multipleRecipe.cocktail))
          .fetchOne();
        verifyResult(mojito, cocktail);
    }

    // ...

}
```

## 9. 多对多的底层关系

在本节中，我们选择按基本成分对菜单中的鸡尾酒进行分类。例如，莫吉托鸡尾酒的基本成分是朗姆酒，所以朗姆酒在我们的菜单中属于鸡尾酒类别。

为了在我们的域中描述上述内容，我们将类别字段添加到Cocktail实体中：

```java
@Entity
@Table(name = "menu")
public class Cocktail {
    // ...

    @Column(name = "category")
    private String category;
    
     // ...
}

```

此外，我们可以将base_ingredient列添加到multiple_recipes表中，以便能够根据特定饮料搜索食谱。

```java
@Entity
@Table(name = "multiple_recipes")
public class MultipleRecipe {
    // ...
    
    @Column(name = "base_ingredient")
    private String baseIngredient;
    
    // ...
}
```

在上面之后，这是我们的数据库模式：

[![多对多](https://www.baeldung.com/wp-content/uploads/2020/04/many_to_many.png)](https://www.baeldung.com/wp-content/uploads/2020/04/many_to_many.png)

现在，我们在Cocktail和MultipleRecipe实体之间建立了多对多的基础关系。许多MultipleRecipe 实体可以与许多Cocktail 实体相关联，它们的类别值等于MultipleRecipe实体的baseIngredient值。

要查找并获取其baseIngredient 作为类别存在于Cocktail 实体中的MultipleRecipe实体，我们可以使用 JPQL 加入这两个实体：

```java
entityManager.createQuery("select distinct r " 
  + "from MultipleRecipe r " 
  + "join Cocktail c " 
  + "on r.baseIngredient = c.category", MultipleRecipe.class)
```

或者使用 QueryDSL：

```java
QCocktail cocktail = QCocktail.cocktail; 
QMultipleRecipe multipleRecipe = QMultipleRecipe.multipleRecipe; 
new JPAQuery(entityManager).from(multipleRecipe)
  .join(cocktail)
  .on(multipleRecipe.baseIngredient.eq(cocktail.category))
  .fetch();

```

## 10. 多对多连接单元测试

在继续我们的测试用例之前，我们必须设置Cocktail实体的类别和MultipleRecipe实体的baseIngredient：

```java
public class UnrelatedEntitiesUnitTest {
    // ...

    @BeforeAll
    public static void setup() {
        // ...

        mojito.setCategory("Rum");
        ginTonic.setCategory("Gin");
        firstMojitoRecipe.setBaseIngredient(mojito.getCategory());
        secondMojitoRecipe.setBaseIngredient(mojito.getCategory());

        // ...
    }

    // ... 
}
```

然后，我们可以验证当我们之前展示的查询被执行时，它们返回了预期的结果：

```java
public class UnrelatedEntitiesUnitTest {
    // ...

    @Test
    public void givenMultipleRecipesWithCocktails_whenQuerying_thenTheExpectedMultipleRecipesReturned() {
        Consumer<List<MultipleRecipe>> verifyResult = recipes -> {
            assertEquals(2, recipes.size());
            recipes.forEach(r -> assertEquals(mojito.getName(), r.getCocktail()));
        };

        // JPQL
        List<MultipleRecipe> recipes = entityManager.createQuery("select distinct r "
          + "from MultipleRecipe r "
          + "join Cocktail c " 
          + "on r.baseIngredient = c.category",
          MultipleRecipe.class).getResultList();
        verifyResult.accept(recipes);

        // QueryDSL
        QCocktail cocktail = QCocktail.cocktail;
        QMultipleRecipe multipleRecipe = QMultipleRecipe.multipleRecipe;
        recipes = new JPAQuery<MultipleRecipe>(entityManager).from(multipleRecipe)
          .join(cocktail)
          .on(multipleRecipe.baseIngredient.eq(cocktail.category))
          .fetch();
        verifyResult.accept(recipes);
    }

    // ...
}

```

## 11.总结

在本教程中，我们介绍了在不相关实体之间以及使用 JPQL 或 QueryDSL 框架构建 JPA 查询的各种方法。