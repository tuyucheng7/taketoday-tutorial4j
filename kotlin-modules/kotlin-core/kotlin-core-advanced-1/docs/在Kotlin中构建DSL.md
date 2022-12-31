## 1. 概述

在本教程中，**我们将了解如何使用强大的Kotlin语言功能来构建类型安全的DSL**。

对于我们的示例，我们将创建一个简单的工具来构建SQL查询，它的大小足以说明这个概念。

**一般的想法是使用静态类型的用户提供的函数文字，在调用时修改查询构建器的状态**。在调用所有这些之后，将验证构建器的状态并生成生成的SQL字符串。

## 2. 定义入口点

让我们首先为我们的功能定义一个入口点：

```kotlin
class SqlSelectBuilder {
    fun build(): String {
        TODO("implement")
    }
}

fun query(initializer: SqlSelectBuilder.() -> Unit): SqlSelectBuilder {
    return SqlSelectBuilder().apply(initializer)
}
```

然后我们可以简单地使用定义的函数：

```kotlin
val sql = query {
}.build()
```

## 3. 添加列

让我们添加对定义要使用的目标列的支持，下面是它在DSL中的样子：

```kotlin
query {
    select("column1", "column2")
}
```

以及select函数的实现：

```kotlin
class SqlSelectBuilder {

    private val columns = mutableListOf<String>()

    fun select(vararg columns: String) {
        if (columns.isEmpty()) {
            throw IllegalArgumentException("At least one column should be defined")
        }
        if (this.columns.isNotEmpty()) {
            throw IllegalStateException("Detected an attempt to re-define columns to fetch. "
                    + "Current columns list: "
                    + "${this.columns}, new columns list: $columns")
        }
        this.columns.addAll(columns)
    }
}
```

## 4. 定义表

我们还需要允许指定要使用的目标表：

```kotlin
query {
    select ("column1", "column2")
    from ("myTable")
}
```

from函数将简单地设置在类属性中接收到的表名：

```kotlin
class SqlSelectBuilder {

    private lateinit var table: String

    fun from(table: String) {
        this.table = table
    }
}
```

## 5. 第一个里程碑

实际上，我们现在已经足够构建简单的查询并测试它们了，我们开始做吧！

**首先，我们需要实现SqlSelectBuilder.build方法**：

```kotlin
class SqlSelectBuilder {

    fun build(): String {
        if (!::table.isInitialized) {
            throw IllegalStateException("Failed to build an sql select - target table is undefined")
        }
        return toString()
    }

    override fun toString(): String {
        val columnsToFetch =
            if (columns.isEmpty()) {
                ""
            } else {
                columns.joinToString(", ")
            }
        return "select $columnsToFetch from $table"
    }
}
```

现在我们可以引入几个测试：

```kotlin
private fun doTest(expected: String, sql: SqlSelectBuilder.() -> Unit) {
    assertThat(query(sql).build()).isEqualTo(expected)
}

@Test
fun `when no columns are specified then star is used`() {
    doTest("select  from table1") {
        from ("table1")
    }
}
@Test
fun `when no condition is specified then correct query is built`() {
    doTest("select column1, column2 from table1") {
        select("column1", "column2")
        from ("table1")
    }
}
```

## 6. AND条件

大多数时候我们需要在查询中指定条件。

让我们从定义DSL的外观开始：

```kotlin
query {
    from("myTable")
    where {
        "column3" eq 4
        "column3" eq null
    }
}
```

这些条件其实就是SQL AND操作数，所以我们在源码中引入同样的概念：

```kotlin
class SqlSelectBuilder {
    fun where(initializer: Condition.() -> Unit) {
        condition = And().apply(initializer)
    }
}

abstract class Condition

class And : Condition()

class Eq : Condition()
```

让我们一一实现这些类：

```kotlin
abstract class Condition {
    infix fun String.eq(value: Any?) {
        addCondition(Eq(this, value))
    }
}
class Eq(private val column: String, private val value: Any?) : Condition() {

    init {
        if (value != null && value !is Number && value !is String) {
            throw IllegalArgumentException(
              "Only <null>, numbers and strings values can be used in the 'where' clause")
        }
    }

    override fun addCondition(condition: Condition) {
        throw IllegalStateException("Can't add a nested condition to the sql 'eq'")
    }

    override fun toString(): String {
        return when (value) {
            null -> "$column is null"
            is String -> "$column = '$value'"
            else -> "$column = $value"
        }
    }
}
```

最后，我们将创建包含条件列表并实现addCondition方法的And类：

```kotlin
class And : Condition() {

    private val conditions = mutableListOf<Condition>()

    override fun addCondition(condition: Condition) {
        conditions += condition
    }

    override fun toString(): String {
        return if (conditions.size == 1) {
            conditions.first().toString()
        } else {
            conditions.joinToString(prefix = "(", postfix = ")", separator = " and ")
        }
    }
}
```

这里棘手的部分是支持DSL标准，为此，我们将Condition.eq声明为中缀字符串扩展函数，因此，我们可以像column.eq(value)一样传统地使用它，也可以不使用点和括号-column eq value。

该函数是在 Condition类的上下文中定义的，这就是我们可以使用它的原因(请记住SqlSelectBuilder.where接收一个在Condition的上下文中执行的函数文字)。

现在我们可以验证一切是否按预期工作：

```kotlin
@Test
fun `when a list of conditions is specified then it's respected`() {
    doTest("select  from table1 where (column3 = 4 and column4 is null)") {
        from ("table1")
        where {
            "column3" eq 4
            "column4" eq null
        }
    }
}
```

## 7. 或条件

我们练习的最后一部分是支持SQL OR条件，像往常一样，让我们首先定义它在我们的DSL中的样子：

```kotlin
query {
    from("myTable")
    where {
        "column1" eq 4
        or {
            "column2" eq null
            "column3" eq 42
        }
    }
}
```

然后我们将提供一个实现，由于OR和AND非常相似，我们可以重用现有的实现：

```kotlin
open class CompositeCondition(private val sqlOperator: String) : Condition() {
    private val conditions = mutableListOf<Condition>()

    override fun addCondition(condition: Condition) {
        conditions += condition
    }

    override fun toString(): String {
        return if (conditions.size == 1) {
            conditions.first().toString()
        } else {
            conditions.joinToString(prefix = "(", postfix = ")", separator = " $sqlOperator ")
        }
    }
}

class And : CompositeCondition("and")

class Or : CompositeCondition("or")
```

最后，我们将对条件子DSL添加相应的支持：

```kotlin
abstract class Condition {
    fun and(initializer: Condition.() -> Unit) {
        addCondition(And().apply(initializer))
    }

    fun or(initializer: Condition.() -> Unit) {
        addCondition(Or().apply(initializer))
    }
}
```

让我们验证一切正常：

```kotlin
@Test
fun `when 'or' conditions are specified then they are respected`() {
    doTest("select  from table1 where (column3 = 4 or column4 is null)") {
        from ("table1")
        where {
            or {
                "column3" eq 4
                "column4" eq null
            }
        }
    }
}

@Test
fun `when either 'and' or 'or' conditions are specified then they are respected`() {
    doTest("select  from table1 where ((column3 = 4 or column4 is null) and column5 = 42)") {
        from ("table1")
        where {
            or {
                "column3" eq 4
                "column4" eq null
            }
            "column5" eq 42
        }
    }
}
```

## 8. 额外的乐趣

虽然超出了本教程的范围，但可以使用相同的概念来扩展我们的DSL，例如，我们可以通过添加对LIKE、GROUP BY、HAVING、ORDER BY的支持来增强它。

## 9. 总结

在本文中，我们看到了为SQL查询构建简单DSL的示例，它不是详尽无遗的指南，但它建立了良好的基础并提供了整个Kotlin类型安全DSL方法的概述。