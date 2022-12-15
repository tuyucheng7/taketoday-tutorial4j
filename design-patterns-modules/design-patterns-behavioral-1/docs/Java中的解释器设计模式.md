## 1. 概述

在本教程中，我们介绍GoF行为型设计模式之一：解释器。

首先，我们概述其目的并解释它试图解决的问题。然后，我们看看解释器模式的UML图和实际示例的实现。

## 2. 解释器设计模式

**简而言之，该模式以面向对象的方式定义了特定语言的语法，可以由解释器本身进行评估**。

考虑到这一点，从技术上讲，我们可以构建自定义正则表达式、自定义DSL解释器，或者我们可以解析任何人类语言，**构建抽象语法树，然后运行解释**。

这些只是一些潜在的用例，但如果我们仔细想一想，我们会发现它的更多用途，例如在我们的IDE中，因为它们不断地解释我们正在编写的代码，从而为我们提供无价的提示。

解释器模式一般应该在语法比较简单的时候使用。否则，它可能会变得难以维护。

## 3. UML图

<img src="../assets/img_1.png">

上图显示了两个主要实体：上下文和表达式。

现在，任何语言都需要以某种方式表达，并且单词(表达式)将根据给定的上下文具有某种含义。

AbstractExpression定义了一个将上下文作为参数的抽象方法。因此，**每个表达式都会影响上下文**，改变其状态并继续解释或返回结果本身。

因此，上下文将成为全局处理状态的持有者，并且将在整个解释过程中重复使用。

那么TerminalExpression和NonTerminalExpression有什么区别呢？

NonTerminalExpression可能关联一个或多个其他AbstractExpressions，因此它可以被递归解释。最后，**解释过程必须以返回结果的TerminalExpression完成**。

值得注意的是NonTerminalExpression是一个[复合]()的。

最后，客户端的作用是创建或使用一个已经创建的**抽象语法树**，它无非是在**创建的语言中定义的一个句子**。

## 4. 实现

为了演示该模式的实际应用，我们将以面向对象的方式构建一个简单的类似SQL的语法，然后对其进行解释并返回结果。

首先，我们定义Select、From和Where表达式，在客户端类中构建语法树并运行解释。

Expression接口具有interpret方法：

```java
List<String> interpret(Context ctx);
```

接下来，我们定义第一个表达式，即Select类：

```java
class Select implements Expression {

    private String column;
    private From from;

    // constructor

    @Override
    public List<String> interpret(Context ctx) {
        ctx.setColumn(column);
        return from.interpret(ctx);
    }
}
```

它获取要选择的列名称和另一个具体的From类型的表达式作为构造函数中的参数。

请注意，在重写的interpret()方法中，它设置了上下文的状态，并将解释与上下文一起进一步传递给另一个表达式。

这样，我们看到它是一个NonTerminalExpression。

另一个表达式是From类：

```java
class From implements Expression {

    private String table;
    private Where where;

    // constructors

    @Override
    public List<String> interpret(Context ctx) {
        ctx.setTable(table);
        if (where == null) {
            return ctx.search();
        }
        return where.interpret(ctx);
    }
}
```

现在，在SQL中，where子句是可选的，因此此类是TerminalExpression或NonTerminalExpression。

如果用户决定不使用where子句，则From表达式将以ctx.search()调用终止并返回结果；否则它将被进一步解释。

Where表达式再次通过设置必要的过滤器来修改上下文，并通过search调用终止解释：

```java
class Where implements Expression {

    private Predicate<String> filter;

    // constructor

    @Override
    public List<String> interpret(Context ctx) {
        ctx.setFilter(filter);
        return ctx.search();
    }
}
```

例如，Context类保存模仿数据库表的数据。

请注意，它具有三个关键字段，它们由Expression的每个子类和search方法修改：

```java
class Context {

    private static Map<String, List<Row>> tables = new HashMap<>();

    static {
        List<Row> list = new ArrayList<>();
        list.add(new Row("John", "Doe"));
        list.add(new Row("Jan", "Kowalski"));
        list.add(new Row("Dominic", "Doom"));

        tables.put("people", list);
    }

    private String table;
    private String column;
    private Predicate<String> whereFilter;

    // ... 

    List<String> search() {
        List<String> result = tables.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(table))
                .flatMap(entry -> Stream.of(entry.getValue()))
                .flatMap(Collection::stream)
                .map(Row::toString)
                .flatMap(columnMapper)
                .filter(whereFilter)
                .collect(Collectors.toList());

        clear();

        return result;
    }
}
```

搜索完成后，上下文会自行清除，因此column、table和whereFilter都设置为默认值，这样每个解释之间不会相互影响。

## 5. 测试

出于测试目的，让我们看一下InterpreterDemo类：

```java
public class InterpreterDemo {
    
    public static void main(String[] args) {
        Expression query = new Select("name", new From("people"));
        Context ctx = new Context();
        List<String> result = query.interpret(ctx);
        System.out.println(result);

        Expression query2 = new Select("", new From("people"));
        List<String> result2 = query2.interpret(ctx);
        System.out.println(result2);

        Expression query3 = new Select("name",
                new From("people", new Where(name -> name.toLowerCase().startsWith("d"))));
        List<String> result3 = query3.interpret(ctx);
        System.out.println(result3);
    }
}
```

首先，我们使用创建的表达式构建语法树，初始化上下文，然后运行解释。上下文是重用的，但正如我们上面所演示的，它会在每次搜索调用后自行清理。

通过运行该程序，输出应如下所示：

```shell
[John, Jan, Dominic]
[John Doe, Jan Kowalski, Dominic Doom]
[Dominic]
```

## 6. 缺点

当语法变得越来越复杂时，它变得更难维护。

从给出的例子中可以看出，添加另一个表达式(如Limit)会相当容易，但如果我们决定用所有其他表达式继续扩展它，则维护起来不会太容易。

## 7. 总结

**解释器设计模式非常适合相对简单的语法解释，不需要太多的重构和扩展**。

在上面的例子中，我们演示了在解释器模式的帮助下以面向对象的方式构建类似SQL的查询是可能的。

最后，你可以在JDK中找到这种模式的用法，特别是在java.util.Pattern、java.text.Format或java.text.Normalizer类中。