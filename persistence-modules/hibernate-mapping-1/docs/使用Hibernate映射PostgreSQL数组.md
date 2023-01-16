## 1. 概述

PostgreSQL支持将任何类型的数组(内置或用户定义的)定义为表的列类型。在本教程中，我们将探讨几种将 PostgreSQL 数组映射到[Hibernate 的](https://www.baeldung.com/learn-jpa-hibernate)方法。

## 2. 基本设置

作为连接 PostgreSQL 数据库的先决条件，我们应该将最新的[postgresql](https://search.maven.org/search?q=g:org.postgresql a:postgresql) Maven 依赖项与 Hibernate 配置一起添加到我们的pom.xml中。此外，让我们创建一个名为[User](https://www.baeldung.com/jpa-entities)[的实体类](https://www.baeldung.com/jpa-entities)，其中包含String数组角色：

```java
@Entity
public class User {
    @Id
    private Long id;
    private String name;

    private String[] roles;

    //getters and setters 
}

```

## 3.自定义休眠类型

[Hibernate 支持自定义类型](https://www.baeldung.com/hibernate-custom-types)将用户定义的类型映射到 SQL 查询中。因此，我们可以创建自定义类型来将 PostgreSQL 数组映射到 Hibernate以存储/获取数据。首先，让我们创建[实现 Hibernate 的](https://www.baeldung.com/hibernate-custom-types#2-implementingusertype)[UserType](https://www.baeldung.com/hibernate-custom-types#2-implementingusertype)[类的](https://www.baeldung.com/hibernate-custom-types#2-implementingusertype)CustomStringArrayType类，以提供自定义类型来映射String数组：

```java
public class CustomStringArrayType implements UserType {
    @Override
    public int[] sqlTypes() {
        return new int[]{Types.ARRAY};
    }

    @Override
    public Class returnedClass() {
        return String[].class;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
      throws HibernateException, SQLException {
        Array array = rs.getArray(names[0]);
        return array != null ? array.getArray() : null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
      throws HibernateException, SQLException {
        if (value != null && st != null) {
            Array array = session.connection().createArrayOf("text", (String[])value);
            st.setArray(index, array);
        } else {
            st.setNull(index, sqlTypes()[0]);
        }
    }
    //implement equals, hashCode, and other methods 
}

```

这里要注意returnedClass方法的返回类型是String数组。此外，nullSafeSet方法创建一个 PostgreSQL 类型text的数组。

## 4. 使用自定义 Hibernate 类型映射数组

### 4.1. 用户实体

然后，我们将使用CustomStringArrayType类将字符串数组角色映射到 PostgreSQL文本数组：

```java
@Entity
public class User {
    //...

    @Column(columnDefinition = "text[]")
    @Type(type = "com.baeldung.hibernate.arraymapping.CustomStringArrayType")
    private String[] roles;
  
   //getters and setters 
}

```

而已！我们已准备好使用自定义类型实现和数组映射来对 用户 实体执行 CRUD 操作。

### 4.2. 单元测试

为了测试我们的自定义类型，让我们首先插入一个User对象以及String数组角色：

```java
@Test
public void givenArrayMapping_whenArraysAreInserted_thenPersistInDB() 
  throws HibernateException, IOException {
    transaction = session.beginTransaction();

    User user = new User();
    user.setId(2L);
    user.setName("smith");

    String[] roles = {"admin", "employee"};
    user.setRoles(roles);

    session.persist(user);
    session.flush();
    session.clear();

    transaction.commit();

    User userDBObj = session.find(User.class, 2L);

    assertEquals("smith", userDBObj.getName());
}
```

此外，我们可以获取包含PostgreSQL文本数组形式的角色的用户记录：

```java
@Test
public void givenArrayMapping_whenQueried_thenReturnArraysFromDB() 
  throws HibernateException, IOException {
    User user = session.find(User.class, 2L);

    assertEquals("smith", user.getName());
    assertEquals("admin", user.getRoles()[0]);
    assertEquals("employee", user.getRoles()[1]);
}
```

### 4.3. 自定义整数数组类型

同样，我们可以为 PostgreSQL 支持的各种数组类型创建自定义类型。例如，让我们创建CustomIntegerArrayType来映射 PostgreSQL int数组：

```java
public class CustomIntegerArrayType implements UserType {
    @Override
    public int[] sqlTypes() {
        return new int[]{Types.ARRAY};
    }

    @Override
    public Class returnedClass() {
        return Integer[].class;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
      throws HibernateException, SQLException {
        Array array = rs.getArray(names[0]);
        return array != null ? array.getArray() : null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
      throws HibernateException, SQLException {
        if (value != null && st != null) {
            Array array = session.connection().createArrayOf("int", (Integer[])value);
            st.setArray(index, array);
        } else {
            st.setNull(index, sqlTypes()[0]);
        }
    }

    //implement equals, hashCode, and other methods 
}

```

与我们在CustomStringArrayType类中注意到的类似，returnedClass方法的返回类型是Integer数组。此外，nullSafeSet方法的实现创建了一个 PostgreSQL 类型的数组int。最后，我们可以使用CustomIntegerArrayType类将整数数组位置映射到 PostgreSQL int数组：

```java
@Entity
public class User {
    //...
    
    @Column(columnDefinition = "int[]")
    @Type(type = "com.baeldung.hibernate.arraymapping.CustomIntegerArrayType")
    private Integer[] locations;

    //getters and setters
}

```

## 5. 使用休眠类型映射数组

另一方面，我们可以使用著名的 Hibernate 专家 Vlad Mihalcea 开发的[hibernate-types](https://www.baeldung.com/hibernate-types-library)[库，而不是为每种类型(如](https://www.baeldung.com/hibernate-types-library)String、Integer和Long )实现自定义类型。

### 5.1. 设置

首先，我们将最新的[hibernate-types-52](https://search.maven.org/search?q=g:com.vladmihalcea a:hibernate-types-52) Maven 依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.vladmihalcea</groupId>
    <artifactId>hibernate-types-52</artifactId>
    <version>2.10.4</version>
</dependency>
```

### 5.2. 用户实体

接下来，我们将在User实体中添加集成代码以映射String数组phoneNumbers：

```java
@TypeDefs({
    @TypeDef(
        name = "string-array",
        typeClass = StringArrayType.class
    )
})
@Entity
public class User {
    //...
    @Type(type = "string-array")
    @Column(
        name = "phone_numbers",
        columnDefinition = "text[]"
    )
    private String[] phoneNumbers;

    //getters and setters
}
```

在这里，与自定义类型CustomStringArrayType类似，我们使用了由hibernate-types库提供的StringArrayType类作为String数组的映射器。同样，我们可以在库中找到一些其他方便的映射器，如DateArrayType、EnumArrayType和DoubleArrayType。

### 5.3. 单元测试

而已！我们已经准备好使用hibernate-types库进行数组映射。让我们更新已经讨论过的单元测试来验证插入操作：

```java
@Test
public void givenArrayMapping_whenArraysAreInserted_thenPersistInDB() 
  throws HibernateException, IOException {
    transaction = session.beginTransaction();
    
    User user = new User();
    user.setId(2L);
    user.setName("smith");
    
    String[] roles = {"admin", "employee"};
    user.setRoles(roles);
    
    String[] phoneNumbers = {"7000000000", "8000000000"};
    user.setPhoneNumbers(phoneNumbers);
    
    session.persist(user);
    session.flush();
    session.clear();
    
    transaction.commit();
}
```

同样，我们可以验证读操作：

```java
@Test
public void givenArrayMapping_whenQueried_thenReturnArraysFromDB() 
  throws HibernateException, IOException {
    User user = session.find(User.class, 2L);

    assertEquals("smith", user.getName());
    assertEquals("admin", user.getRoles()[0]);
    assertEquals("employee", user.getRoles()[1]);
    assertEquals("7000000000", user.getPhoneNumbers()[0]);
    assertEquals("8000000000", user.getPhoneNumbers()[1]);
}
```

## 六. 总结

在本文中，我们探索了使用 Hibernate 映射 PostgreSQL 数组。首先，我们使用 Hibernate 的UserType类创建了一个自定义类型来映射String数组。然后，我们使用自定义类型将 PostgreSQL文本数组映射到 Hibernate。最后，我们使用hibernate-types库来映射 PostgreSQL 数组。