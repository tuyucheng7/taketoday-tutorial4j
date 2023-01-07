## 1. 概述

在本快速教程中，我们将讨论LocalDate和XMLGregorianCalendar并提供在这两种类型之间进行转换的示例。

## 2.XML公历

XML Schema 标准定义了以 XML 格式指定日期的明确规则。为了使用这种格式，Java 1.5 中引入的Java类XMLGregorianCalendar[是](https://www.baeldung.com/java-gregorian-calendar)W3C [XML Schema 1.0 日期/时间数据类型](https://www.w3.org/TR/xmlschema-2/#isoformats)的表示。

## 3.本地日期_

[LocalDate](https://www.baeldung.com/java-8-date-time-intro)实例表示 ISO-8601 日历系统中没有时区的日期。因此，例如， LocalDate适合存储生日，但不适合存储与时间相关的任何内容。Java 在 1.8 版本中引入了LocalDate 。

## 4. 从LocalDate到XMLGregorianCalendar

首先，我们将了解如何将LocalDate转换为XMLGregorianCalendar。为了生成XMLGregorianCalendar的新实例，我们使用了javax.xml.datatype包中的DataTypeFactory 。

因此，让我们创建一个LocalDate实例并将其转换为XMLGregorianCalendar：

```java
LocalDate localDate = LocalDate.of(2019, 4, 25);

XMLGregorianCalendar xmlGregorianCalendar = 
  DatatypeFactory.newInstance().newXMLGregorianCalendar(localDate.toString());

assertThat(xmlGregorianCalendar.getYear()).isEqualTo(localDate.getYear());
assertThat(xmlGregorianCalendar.getMonth()).isEqualTo(localDate.getMonthValue());
assertThat(xmlGregorianCalendar.getDay()).isEqualTo(localDate.getDayOfMonth());
assertThat(xmlGregorianCalendar.getTimezone()).isEqualTo(DatatypeConstants.FIELD_UNDEFINED);

```

如前所述，XMLGregorianCalendar实例可能具有时区信息。但是，LocalDate没有任何关于时间的信息。

因此，当我们执行转换时，时区值将保持为FIELD_UNDEFINED。

## 5. 从XMLGregorianCalendar到LocalDate

同样，我们现在将了解如何以相反的方式执行转换。事实证明，从XMLGregorianCalendar转换为LocalDate要容易得多。

同样，由于LocalDate没有关于时间的信息，因此LocalDate实例只能包含XMLGregorianCalendar信息的一个子集。

让我们创建一个XMLGregorianCalendar实例并执行转换：

```java
XMLGregorianCalendar xmlGregorianCalendar = 
  DatatypeFactory.newInstance().newXMLGregorianCalendar("2019-04-25");

LocalDate localDate = LocalDate.of(
  xmlGregorianCalendar.getYear(), 
  xmlGregorianCalendar.getMonth(), 
  xmlGregorianCalendar.getDay());

assertThat(localDate.getYear()).isEqualTo(xmlGregorianCalendar.getYear());
assertThat(localDate.getMonthValue()).isEqualTo(xmlGregorianCalendar.getMonth());
assertThat(localDate.getDayOfMonth()).isEqualTo(xmlGregorianCalendar.getDay());

```

## 六，总结

在本快速教程中，我们介绍了LocalDate实例和XMLGregorianCalendar之间的转换，反之亦然。