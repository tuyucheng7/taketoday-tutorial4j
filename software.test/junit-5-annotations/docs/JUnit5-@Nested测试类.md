## 1. 概述

在这个简短的教程中，我们将讨论JUnit 5的[@Nested](https://junit.org/junit5/docs/current/user-guide/#writing-tests-nested)注解。我们将从查看一个简单示例开始，了解嵌套测试类是如何运行的。之后，我们将学习如何利用新功能来实现更类似于生产的用例。

## 2. @Nested如何工作

我们可以使用JUnit 5的@Nested注解来创建嵌套的测试类。对于包含测试的每个内部类，必须在类级别添加注解：

```java
public class NestedTest {
    @Nested
    class FirstNestedClass {
        @Test
        void test() {
            System.out.println("FirstNestedClass.test()");
        }
    }

    @Nested
    class SecondNestedClass {
        @Test
        void test() {
            System.out.println("SecondNestedClass.test()");
        }
    }
}
```

结果，我们可以运行父测试类，并且应该执行嵌套测试类中的所有测试。大多数IDE将显示测试的良好分层表示：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/nested_tests_intellij-300x88.jpg)](https://www.baeldung.com/wp-content/uploads/2023/02/nested_tests_intellij.jpg)

此外，如果我们添加设置或拆卸方法，它们将在它们的声明中执行。例如，如果我们为三个类中的每一个添加一个@BeforeEach方法，我们将期望每个测试都执行父类的设置方法，然后是它自己类中的方法，然后是测试本身：

```java
public class NestedTest {
    @BeforeEach()
    void beforeEach() {
        System.out.println("NestedTest.beforeEach()");
    }

    @Nested
    class FirstNestedClass {
        @BeforeEach()
        void beforeEach() {
            System.out.println("FirstNestedClass.beforeEach()");
        }

        @Test
        void test() {
            System.out.println("FirstNestedClass.test()");
        }
    }

    @Nested
    class SecondNestedClass {
        @BeforeEach()
        void beforeEach() {
            System.out.println("SecondNestedClass.beforeEach()");
        }

        @Test
        void test() {
            System.out.println("SecondNestedClass.test()");
        }
    }
}
```

让我们运行测试类并检查控制台中打印语句的顺序：

[![img](https://www.baeldung.com/wp-content/uploads/2023/02/nested_tests_execution-300x92.jpg)](https://www.baeldung.com/wp-content/uploads/2023/02/nested_tests_execution.jpg)

正如预期的那样，两个测试都使用父类定义的通用设置。之后，他们从自己的类运行设置方法，然后执行测试。此外，其他设置和拆卸方法：@BeforeAll、@AfterEach和@AfterAll遵循相同的模式。

## 3. 何时使用@Nested

如果我们有设置或拆卸方法重复某些测试，但不是所有测试，@Nested测试类可能非常有用。

此外，使用嵌套类来设置测试组会导致更具表现力的测试场景和我们测试之间的清晰关系。

### 3.1 重用测试场景

让我们使用@Nested注解为更复杂的用例编写一些测试。

假设我们正在测试在线出版物的后端应用程序。本出版物的客户可以拥有三种类型的成员之一：

```java
public enum Membership {
    FREE, SILVER, GOLD
}
```

根据他们的成员身份，用户可以打开并阅读文章或查看它们是否已锁定。

让我们从创建一个Publication和三个Article对象开始：

```java
class OnlinePublicationTest {
    private Publication publication;

    @BeforeEach
    void setupArticlesAndPublication() {
        Article freeArticle = new Article("free article", Membership.FREE);
        Article silverArticle = new Article("silver level article", Membership.SILVER);
        Article goldArticle = new Article("gold level article", Membership.GOLD);
        publication = new Publication(Arrays.asList(freeArticle, silverArticle, goldArticle));
    }

    @Test
    void shouldHaveThreeArticlesInTotal() {
        List<Article> allArticles = publication.getArticles();
        assertThat(allArticles).hasSize(3);
    }
}
```

现在，让我们假设一个拥有免费会员资格的用户检查该出版物。我们希望他只能阅读一篇文章，而将其他两篇文章视为“已锁定”。让我们在嵌套测试类中将其编写为两个不同的单元测试：

```java
@Nested
class UserWithoutMembership {
    User freeFreya = new User("Freya", Membership.FREE);

    @Test
    void shouldOnlyReadFreeArticles() {
        List<String> articles = publication.getReadableArticles(freeFreya);
        assertThat(articles).containsExactly("free article");
    }

    @Test
    void shouldSeeSilverAndGoldLevelArticlesAsLocked() {
        List<String> articles = publication.getLockedArticles(freeFreya);
        assertThat(articles).containsExactlyInAnyOrder("silver level article", "gold level article");
    }
}
```

现在，让我们为“银牌”和“金牌”会员用户运行类似的测试类并运行整个类：



[![img](https://www.baeldung.com/wp-content/uploads/2023/02/onlibe_publication_test_scenario-300x142.jpg)](https://www.baeldung.com/wp-content/uploads/2023/02/onlibe_publication_test_scenario.jpg)

### 3.2 使用带@DisplayName的嵌套测试

值得注意的是类的名称以及测试的名称是如何描述整个测试场景的。同样，我们可以使用@DisaplyName注解我们的测试类和方法，以进一步自定义它们的显示方式。例如，我们可以使用given-when-then模式：



[![img](https://www.baeldung.com/wp-content/uploads/2023/02/nested_test_with_display_name-300x119.png)](https://www.baeldung.com/wp-content/uploads/2023/02/nested_test_with_display_name.png)

## 4. 总结

在这篇简短的文章中，我们了解了JUnit 5的@Nested注解如何工作以及如何使用它来创建富有表现力的测试场景。我们看到，当多个测试的设置或拆卸非常相似时，我们可以创建一个嵌套测试类，但它并不适用于类中的所有测试。