## 1. 概述

在本快速教程中，我们将学习如何在 java.time.LocalDate和java.sql.Date之间进行转换。

## 2.直接转换

要从[LocalDate](https://www.baeldung.com/java-date-to-localdate-and-localdatetime)[转换](https://www.baeldung.com/java-date-to-localdate-and-localdatetime)为java.sql.Date，我们可以简单地使用java.sql.Date中可用的valueOf()方法。同样，要转换当前日期，我们可以使用：

```java
Date date = Date.valueOf(LocalDate.now());
```

或者，任何其他特定日期：

```java
Date date = Date.valueOf(LocalDate.of(2019, 01, 10));
```

此外，如果参数为 null ， valueOf()将抛出NullPointerException。

现在，让我们从java.sql.Date 转换为LocalDate。为此，我们可以使用 toLocalDate()方法：

```java
LocalDate localDate = Date.valueOf("2019-01-10").toLocalDate();
```

## 3. 使用属性转换器

首先，让我们了解这个问题。

Java 8 有很多有用的特性，包括[Date/Time API](https://www.baeldung.com/java-8-date-time-intro)。

但是，将它与某些数据库或持久性框架一起使用需要比预期更多的工作。例如，JPA 会将LocalDate属性映射到 blob 而不是java.sql.Date对象。因此，数据库不会将LocalDate属性识别为Date类型。

通常，我们不想在LocalDate和Date之间执行显式转换。

例如，假设我们有一个带有LocalDate字段的实体对象。当持久化这个实体时，我们需要告诉持久化上下文如何将LocalDate映射到 java.sql.Date中。

让我们通过创建一个[AttributeConverter ](https://www.baeldung.com/jpa-attribute-converters)类来应用一个简单的解决方案：

```java
@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, Date> {

    @Override
    public Date convertToDatabaseColumn(LocalDate localDate) {
        return Optional.ofNullable(localDate)
          .map(Date::valueOf)
          .orElse(null);
    }

    @Override
    public LocalDate convertToEntityAttribute(Date date) {
        return Optional.ofNullable(date)
          .map(Date::toLocalDate)
          .orElse(null);
    }
}
```

正如我们所见， AttributeConverter接口接受两种类型：在我们的例子中是LocalDate和Date。

简而言之， convertToDatabaseColumn()和convertToEntityAttribute() 方法将负责转换过程。在实现内部，我们使用 [Optional](https://www.baeldung.com/java-optional) 轻松处理可能的 空引用。

此外，我们还使用了@Converter注解。使用 autoApply=true 属性，转换器将应用于实体类型的所有映射属性。

## 4. 总结

在本快速教程中，我们展示了两种在 java.time.LocalDate和java.sql.Date 之间进行转换的方法。此外，我们还展示了使用直接转换和使用自定义 AttributeConverter类的示例。