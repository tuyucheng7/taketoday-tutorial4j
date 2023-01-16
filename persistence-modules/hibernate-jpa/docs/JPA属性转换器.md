## 1. 简介

在这篇简短的文章中，我们将介绍 JPA 2.1 中可用的属性转换器的用法——简单地说，它允许我们将 JDBC 类型映射到Java类。

我们将在这里使用 Hibernate 5 作为我们的 JPA 实现。

## 2. 创建转换器

我们将展示如何为自定义Java类实现属性转换器。

首先，让我们创建一个PersonName类——稍后将对其进行转换：

```java
public class PersonName implements Serializable {

    private String name;
    private String surname;

    // getters and setters
}
```

然后，我们将向@Entity类添加一个PersonName类型的属性：

```java
@Entity(name = "PersonTable")
public class Person {
   
    private PersonName personName;

    //...
}
```

现在我们需要创建一个转换器，将PersonName属性转换为数据库列，反之亦然。在我们的例子中，我们会将属性转换为包含名称和姓氏字段的字符串值。

为此，我们必须使用@Converter注解我们的转换器类并实现AttributeConverter接口。我们将按以下顺序使用类的类型和数据库列对接口进行参数化：

```java
@Converter
public class PersonNameConverter implements 
  AttributeConverter<PersonName, String> {

    private static final String SEPARATOR = ", ";

    @Override
    public String convertToDatabaseColumn(PersonName personName) {
        if (personName == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if (personName.getSurname() != null && !personName.getSurname()
            .isEmpty()) {
            sb.append(personName.getSurname());
            sb.append(SEPARATOR);
        }

        if (personName.getName() != null 
          && !personName.getName().isEmpty()) {
            sb.append(personName.getName());
        }

        return sb.toString();
    }

    @Override
    public PersonName convertToEntityAttribute(String dbPersonName) {
        if (dbPersonName == null || dbPersonName.isEmpty()) {
            return null;
        }

        String[] pieces = dbPersonName.split(SEPARATOR);

        if (pieces == null || pieces.length == 0) {
            return null;
        }

        PersonName personName = new PersonName();        
        String firstPiece = !pieces[0].isEmpty() ? pieces[0] : null;
        if (dbPersonName.contains(SEPARATOR)) {
            personName.setSurname(firstPiece);

            if (pieces.length >= 2 && pieces[1] != null 
              && !pieces[1].isEmpty()) {
                personName.setName(pieces[1]);
            }
        } else {
            personName.setName(firstPiece);
        }

        return personName;
    }
}
```

请注意，我们必须实施 2 个方法：convertToDatabaseColumn()和convertToEntityAttribute()。

这两种方法用于将属性转换为数据库列，反之亦然。

## 3.使用转换器

要使用我们的转换器，我们只需要将@Convert注解添加到属性并指定我们要使用的转换器类：

```java
@Entity(name = "PersonTable")
public class Person {

    @Convert(converter = PersonNameConverter.class)
    private PersonName personName;
    
    // ...
}
```

最后，让我们创建一个单元测试，看看它是否真的有效。

为此，我们将首先在数据库中存储一个Person对象：

```java
@Test
public void givenPersonName_whenSaving_thenNameAndSurnameConcat() {
    String name = "name";
    String surname = "surname";

    PersonName personName = new PersonName();
    personName.setName(name);
    personName.setSurname(surname);

    Person person = new Person();
    person.setPersonName(personName);

    Long id = (Long) session.save(person);

    session.flush();
    session.clear();
}
```

接下来，我们将测试PersonName是否按照我们在转换器中定义的那样存储 - 通过从数据库表中检索该字段：

```java
@Test
public void givenPersonName_whenSaving_thenNameAndSurnameConcat() {
    // ...

    String dbPersonName = (String) session.createNativeQuery(
      "select p.personName from PersonTable p where p.id = :id")
      .setParameter("id", id)
      .getSingleResult();

    assertEquals(surname + ", " + name, dbPersonName);
}
```

我们还通过编写检索整个Person类的查询来测试从存储在数据库中的值到PersonName类的转换是否按照转换器中定义的那样工作：

```java
@Test
public void givenPersonName_whenSaving_thenNameAndSurnameConcat() {
    // ...

    Person dbPerson = session.createNativeQuery(
      "select  from PersonTable p where p.id = :id", Person.class)
        .setParameter("id", id)
        .getSingleResult();

    assertEquals(dbPerson.getPersonName()
      .getName(), name);
    assertEquals(dbPerson.getPersonName()
      .getSurname(), surname);
}
```

## 4. 总结

在这个简短的教程中，我们展示了如何使用 JPA 2.1 中新引入的属性转换器。