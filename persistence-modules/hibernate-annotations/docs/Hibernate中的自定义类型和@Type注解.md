## 1. 概述

Hibernate 通过将Java中的面向对象模型与数据库中的关系模型进行映射，简化了 SQL 和 JDBC 之间的数据处理。虽然基本Java类的映射是内置在 Hibernate 中的，但是自定义类型的映射通常很复杂。

在本教程中，我们将看到 Hibernate 如何允许我们将基本类型映射扩展到自定义Java类。除此之外，我们还将看到自定义类型的一些常见示例，并使用 Hibernate 的类型映射机制来实现它们。

## 2. Hibernate 映射类型

Hibernate 使用映射类型将Java对象转换为用于存储数据的 SQL 查询。同样，它在检索数据时使用映射类型将 SQL ResultSet 转换为Java对象。

通常，Hibernate 将类型分为实体类型和值类型。具体来说，实体类型用于映射特定于域的Java实体，因此独立于应用程序中的其他类型存在。相反，值类型用于映射数据对象，并且几乎总是由实体拥有。

在本教程中，我们将重点关注值类型的映射，这些类型进一步分为：

-   基本类型——基本Java类型的映射
-   可嵌入——复合 java 类型/POJO 的映射
-   集合——映射基本和复合 java 类型的集合

## 3.Maven依赖

要创建我们的自定义 Hibernate 类型，我们需要 [hibernate-core](https://search.maven.org/search?q=g:org.hibernate a:hibernate-core) 依赖项：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.6.7.Final</version>
</dependency>
```

## 4. Hibernate 中的自定义类型

我们可以为大多数用户域使用 Hibernate 基本映射类型。但是，有很多用例，我们需要实现自定义类型。

Hibernate 使得实现自定义类型相对容易。在 Hibernate 中实现自定义类型有三种方法。让我们详细讨论它们中的每一个。

### 4.1. 实现 基本类型

我们可以通过实现 Hibernate 的BasicType 或其特定实现之一 AbstractSingleColumnStandardBasicType来创建自定义基本类型 。

在我们实现第一个自定义类型之前，让我们看一下实现基本类型的常见用例。假设我们必须使用一个遗留数据库，它将日期存储为 VARCHAR。通常，Hibernate 会将其映射到 StringJava类型。因此，使应用程序开发人员更难进行日期验证。 

因此，让我们实现我们的 LocalDateString 类型，它将 LocalDateJava类型存储为 VARCHAR：

```java
public class LocalDateStringType 
  extends AbstractSingleColumnStandardBasicType<LocalDate> {

    public static final LocalDateStringType INSTANCE = new LocalDateStringType();

    public LocalDateStringType() {
        super(VarcharTypeDescriptor.INSTANCE, LocalDateStringJavaDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "LocalDateString";
    }
}
```

这段代码中最重要的是构造函数参数。首先，是SqlTypeDescriptor的一个实例 ，它是 Hibernate 的 SQL 类型表示，在我们的示例中是 VARCHAR。 并且，第二个参数是表示Java类型的JavaTypeDescriptor实例 。

现在，我们可以实现一个 LocalDateStringJavaDescriptor， 用于将LocalDate存储和检索 为 VARCHAR：

```java
public class LocalDateStringJavaDescriptor extends AbstractTypeDescriptor<LocalDate> {

    public static final LocalDateStringJavaDescriptor INSTANCE = 
      new LocalDateStringJavaDescriptor();

    public LocalDateStringJavaDescriptor() {
        super(LocalDate.class, ImmutableMutabilityPlan.INSTANCE);
    }
	
    // other methods
}
```

接下来，我们需要覆盖 将Java类型转换为 SQL 的wrap 和unwrap 方法。让我们从 解包开始：

```java
@Override
public <X> X unwrap(LocalDate value, Class<X> type, WrapperOptions options) {

    if (value == null)
        return null;

    if (String.class.isAssignableFrom(type))
        return (X) LocalDateType.FORMATTER.format(value);

    throw unknownUnwrap(type);
}
```

接下来， 包装 方法：

```java
@Override
public <X> LocalDate wrap(X value, WrapperOptions options) {
    if (value == null)
        return null;

    if(String.class.isInstance(value))
        return LocalDate.from(LocalDateType.FORMATTER.parse((CharSequence) value));

    throw unknownWrap(value.getClass());
}
```

PreparedStatement 绑定时 调用 unwrap() 将LocalDate 转换为String类型，映射为VARCHAR。同样， 在ResultSet 检索 期间调用 wrap() 以将String 转换为JavaLocalDate。

最后，我们可以在实体类中使用自定义类型：

```java
@Entity
@Table(name = "OfficeEmployee")
public class OfficeEmployee {

    @Column
    @Type(type = "com.baeldung.hibernate.customtypes.LocalDateStringType")
    private LocalDate dateOfJoining;

    // other fields and methods
}
```

稍后，我们将看到如何在 Hibernate 中注册这种类型。因此，使用注册码而不是完全限定的类名来引用此类型。

### 4.2. 实现 用户类型

由于 Hibernate 中的基本类型多种多样，我们很少需要实现自定义基本类型。相比之下，一个更典型的用例是将复杂的Java域对象映射到数据库。此类域对象通常存储在多个数据库列中。

那么让我们通过实现UserType来实现一个复杂的 PhoneNumber 对象 ：

```java
public class PhoneNumberType implements UserType {
    @Override
    public int[] sqlTypes() {
        return new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER};
    }

    @Override
    public Class returnedClass() {
        return PhoneNumber.class;
    }

    // other methods
}	


```

在这里，重写的 sqlTypes 方法返回字段的 SQL 类型，其顺序与它们在我们的 PhoneNumber 类中声明的顺序相同。同样， returnedClass 方法返回我们的PhoneNumberJava类型。

唯一剩下要做的就是实现在Java类型和 SQL 类型之间转换的方法，就像我们为BasicType所做的那样。

首先是 nullSafeGet 方法：

```java
@Override
public Object nullSafeGet(ResultSet rs, String[] names, 
  SharedSessionContractImplementor session, Object owner) 
  throws HibernateException, SQLException {
    int countryCode = rs.getInt(names[0]);

    if (rs.wasNull())
        return null;

    int cityCode = rs.getInt(names[1]);
    int number = rs.getInt(names[2]);
    PhoneNumber employeeNumber = new PhoneNumber(countryCode, cityCode, number);

    return employeeNumber;
}
```

接下来， nullSafeSet 方法：

```java
@Override
public void nullSafeSet(PreparedStatement st, Object value, 
  int index, SharedSessionContractImplementor session) 
  throws HibernateException, SQLException {

    if (Objects.isNull(value)) {
        st.setNull(index, Types.INTEGER);
        st.setNull(index + 1, Types.INTEGER);
        st.setNull(index + 2, Types.INTEGER);
    } else {
        PhoneNumber employeeNumber = (PhoneNumber) value;
        st.setInt(index,employeeNumber.getCountryCode());
        st.setInt(index+1,employeeNumber.getCityCode());
        st.setInt(index+2,employeeNumber.getNumber());
    }
}
```

最后，我们可以 在 OfficeEmployee 实体类中声明我们的自定义PhoneNumberType ：

```java
@Entity
@Table(name = "OfficeEmployee")
public class OfficeEmployee {

    @Columns(columns = { @Column(name = "country_code"), 
      @Column(name = "city_code"), @Column(name = "number") })
    @Type(type = "com.baeldung.hibernate.customtypes.PhoneNumberType")
    private PhoneNumber employeeNumber;
	
    // other fields and methods
}
```

### 4.3. 实现 CompositeUserType

实现 UserType 适用于简单的类型。但是，映射复杂的Java类型(使用 Collections 和 Cascaded 复合类型)需要更加复杂。Hibernate 允许我们通过实现 CompositeUserType 接口来映射这些类型。

因此，让我们通过为 我们之前使用的OfficeEmployee 实体实现一个 AddressType 来实际操作一下：

```java
public class AddressType implements CompositeUserType {

    @Override
    public String[] getPropertyNames() {
        return new String[] { "addressLine1", "addressLine2", 
          "city", "country", "zipcode" };
    }

    @Override
    public Type[] getPropertyTypes() {
        return new Type[] { StringType.INSTANCE, 
          StringType.INSTANCE, 
          StringType.INSTANCE, 
          StringType.INSTANCE, 
          IntegerType.INSTANCE };
    }

    // other methods
}
```

与映射类型属性索引的UserTypes相反， CompositeType 映射我们的 Address 类的属性名称。更重要的是，getPropertyType 方法返回每个属性的映射类型。

此外，我们还需要实现 getPropertyValue 和 setPropertyValue 方法，以将PreparedStatement 和 ResultSet 索引映射到类型属性。例如，考虑 我们的 AddressType 的 getPropertyValue ：

```java
@Override
public Object getPropertyValue(Object component, int property) throws HibernateException {

    Address empAdd = (Address) component;

    switch (property) {
    case 0:
        return empAdd.getAddressLine1();
    case 1:
        return empAdd.getAddressLine2();
    case 2:
        return empAdd.getCity();
    case 3:
        return empAdd.getCountry();
    case 4:
        return Integer.valueOf(empAdd.getZipCode());
    }

    throw new IllegalArgumentException(property + " is an invalid property index for class type "
      + component.getClass().getName());
}
```

最后，我们需要实现 nullSafeGet 和 nullSafeSet 方法以在Java和 SQL 类型之间进行转换。这类似于我们之前在 PhoneNumberType 中所做的。

请注意， CompositeType通常作为 可嵌入 类型的替代映射机制来实现。

### 4.4. 类型参数化

除了创建自定义类型，Hibernate 还允许我们根据参数改变类型的行为。

例如，假设我们需要存储 OfficeEmployee 的 薪水 。更重要的是，应用程序必须将工资金额转换为地理当地货币金额。 

因此，让我们实现 接受 货币 作为参数的参数化SalaryType ：

```java
public class SalaryType implements CompositeUserType, DynamicParameterizedType {

    private String localCurrency;
	
    @Override
    public void setParameterValues(Properties parameters) {
        this.localCurrency = parameters.getProperty("currency");
    }
	
    // other method implementations from CompositeUserType
}
```

请注意，我们已经跳过示例中的CompositeUserType方法以专注于参数化。在这里，我们简单地实现了 Hibernate 的 DynamicParameterizedType，并覆盖了 setParameterValues() 方法。现在， SalaryType 接受一个 货币 参数，并在存储之前转换任何金额。

我们将 在声明薪水时将货币 作为参数 传递：

```java
@Entity
@Table(name = "OfficeEmployee")
public class OfficeEmployee {

    @Type(type = "com.baeldung.hibernate.customtypes.SalaryType", 
      parameters = { @Parameter(name = "currency", value = "USD") })
    @Columns(columns = { @Column(name = "amount"), @Column(name = "currency") })
    private Salary salary;

    // other fields and methods
}
```

## 5. 基本类型注册表

Hibernate 在BasicTypeRegistry中维护所有内置基本类型的映射。因此，无需为此类类型注解映射信息。

此外，Hibernate 允许我们在BasicTypeRegistry中注册自定义类型，就像基本类型一样。通常，应用程序会在引导 SessionFactory 时注册自定义类型。 让我们通过注册我们之前实现的LocalDateString 类型来理解这一点 ：

```java
private static SessionFactory makeSessionFactory() {
    ServiceRegistry serviceRegistry = StandardServiceRegistryBuilder()
      .applySettings(getProperties()).build();
														  
    MetadataSources metadataSources = new MetadataSources(serviceRegistry);
    Metadata metadata = metadataSources
      .addAnnotatedClass(OfficeEmployee.class)
      .getMetadataBuilder()
      .applyBasicType(LocalDateStringType.INSTANCE)
      .build();
														  
    return metadata.buildSessionFactory()
}

private static Properties getProperties() {
    // return hibernate properties
}
```

因此，它消除了在类型映射中使用完全限定类名的限制：

```java
@Entity
@Table(name = "OfficeEmployee")
public class OfficeEmployee {

    @Column
    @Type(type = "LocalDateString")
    private LocalDate dateOfJoining;
	
    // other methods
}
```

此处，LocalDateString 是LocalDateStringType 映射到的键 。

或者，我们可以通过定义 TypeDef 来跳过类型注册：

```java
@TypeDef(name = "PhoneNumber", typeClass = PhoneNumberType.class, 
  defaultForType = PhoneNumber.class)
@Entity
@Table(name = "OfficeEmployee")
public class OfficeEmployee {

    @Columns(columns = {@Column(name = "country_code"),
    @Column(name = "city_code"),
    @Column(name = "number")})
    private PhoneNumber employeeNumber;
	
    // other methods
}
```

## 六. 总结

在本教程中，我们讨论了在 Hibernate 中定义自定义类型的多种方法。此外，我们根据一些常见的用例为我们的实体类实现了一些自定义类型，在这些用例中，新的自定义类型可以派上用场。