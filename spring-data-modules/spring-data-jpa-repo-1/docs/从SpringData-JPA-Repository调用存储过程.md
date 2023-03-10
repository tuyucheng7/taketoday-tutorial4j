## 1. 概述

[存储过程](https://www.baeldung.com/jpa-stored-procedures)是一组存储在数据库中的预定义SQL语句。在Java中，有几种访问存储过程的方法。在本教程中，我们学习如何从Spring Data JPA Repository调用存储过程。

## 2. 项目构建

**我们将使用[Spring Boot Starter Data JPA](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)模块作为数据访问层**。我们还将使用MySQL作为后端数据库。因此，我们的项目pom.xml文件中需要[Spring Data JPA](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa/3.0.3)、[Spring Data JDBC](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-data-jdbs/3.0.3)和[MySQL Connector](https://central.sonatype.com/artifact/mysql/mysql-connector-java/8.0.32)依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

一旦我们有了MySQL依赖定义，我们就可以在application.properties文件中配置数据库连接：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tuyucheng
spring.datasource.username=tuyucheng
spring.datasource.password=tuyucheng
```

## 3. 实体类

**在Spring Data JPA中，[实体](https://www.baeldung.com/jpa-entities)表示存储在数据库中的表**。因此，我们可以构造一个实体类来映射car数据库表：

```java
@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String model;

    @Column
    private Integer year;

    // standard getters and setters
}
```

## 4. 存储过程创建

**存储过程可以有参数**，这样我们就可以根据输入得到不同的结果。例如，我们可以创建一个存储过程，它接收一个整数类型的输入参数并返回一个汽车列表：

```sql
CREATE PROCEDURE FIND_CARS_AFTER_YEAR(IN year_in INT)
BEGIN
SELECT * FROM car WHERE year >= year_in ORDER BY year;
END
```

**存储过程还可以使用输出参数将数据返回给调用应用程序**。例如，我们可以创建一个存储过程，它接收一个字符串类型的输入参数，并将查询结果存储到一个输出参数中：

```sql
CREATE PROCEDURE GET_TOTAL_CARS_BY_MODEL(IN model_in VARCHAR(50), OUT count_out INT)
BEGIN
SELECT COUNT(*) into count_out from car WHERE model = model_in;
END
```

## 5. 在Repository中引用存储过程

**在Spring Data JPA中，Repository是我们提供数据库操作的地方**。我们可以为Car实体上的数据库操作构建一个Repository，并在这个Repository中引用存储过程：

```java
@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {
    // ...
}
```

接下来，让我们向我们的Repository添加一些调用存储过程的方法。

### 5.1 直接映射存储过程名称

**我们可以使用@Procedure注解定义一个存储过程方法，直接映射存储过程名称**。

有四种等效的方法可以做到这一点。比如我们可以直接使用存储过程名作为方法名：

```java
@Procedure
int GET_TOTAL_CARS_BY_MODEL(String model);
```

如果我们想定义一个不同的方法名，我们可以将存储过程名作为@Procedure注解的参数指定：

```java
@Procedure("GET_TOTAL_CARS_BY_MODEL")
int getTotalCarsByModel(String model);
```

**我们还可以使用procedureName属性来映射存储过程名称**：

```java
@Procedure(procedureName = "GET_TOTAL_CARS_BY_MODEL")
int getTotalCarsByModelProcedureName(String model);
```

最后，也可以使用value属性来映射存储过程名称：

```java
@Procedure(value = "GET_TOTAL_CARS_BY_MODEL")
int getTotalCarsByModelValue(String model);
```

### 5.2 引用实体中定义的存储过程

**我们也可以使用@NamedStoredProcedureQuery注解在实体类中定义一个存储过程**：

```java
@Entity
@NamedStoredProcedureQuery(
      name = "Car.getTotalCardsbyModelEntity",
      procedureName = "GET_TOTAL_CARS_BY_MODEL",
      parameters = {
            @StoredProcedureParameter(mode = ParameterMode.IN, name = "model_in", type = String.class),
            @StoredProcedureParameter(mode = ParameterMode.OUT, name = "count_out", type = Integer.class)
      }
)
public class Car {
    // class definition
}
```

然后我们可以在Repository中引用这个定义：

```java
@Procedure(name = "Car.getTotalCardsbyModelEntity")
int getTotalCarsByModelEntiy(@Param("model_in") String model);
```

**我们使用name属性来引用实体类中定义的存储过程**。对于Repository方法，我们使用@Param注解来匹配存储过程的入参。我们还将存储过程的输出参数与Repository方法的返回值相匹配。

### 5.3 使用@Query注解引用存储过程

我们也可以直接使用@Query注解调用存储过程：

```java
@Query(value = "CALL FIND_CARS_AFTER_YEAR(:year_in);", nativeQuery = true)
List<Car> findCarsAfterYear(@Param("year_in") Integer year_in);
```

在这种方法中，我们使用原生查询来调用存储过程。我们将查询存储在注解的value属性中。

同样，我们使用@Param来匹配存储过程的输入参数，并将存储过程输出映射到实体Car对象列表。

## 6. 总结

在本文中，我们探讨了如何通过JPA Repository访问存储过程。我们还讨论了在JPA Repository中引用存储过程的两种简单方法。