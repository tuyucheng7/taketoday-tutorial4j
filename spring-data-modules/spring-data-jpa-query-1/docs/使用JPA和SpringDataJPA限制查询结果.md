## 1. 概述

在本教程中，我们将学习**使用JPA和Spring Data JPA分页查询结果**。

首先，我们看一下我们要查询的表，以及我们要实现的SQL查询。

然后我们将深入探讨如何使用JPA和Spring Data JPA实现这一目标。

## 2. 测试数据

下面是我们将在本文中使用到的表。

我们要回答的问题是，“第一个被占用的座位是什么，谁在占用它？

| First Name  | Last Name  | Seat Number |
|:-----------:|:----------:|:-----------:|
|    Jill     |   Smith    |     50      |
|     Eve     |  Jackson   |     94      |
|    Fred     |   Bloggs   |     22      |
|    Ricki    |   Bobbie   |     36      |
|    Siya     |   Kolisi   |     85      |

## 3. Sql

使用SQL，我们可能会编写一个如下所示的查询：

```sql
SELECT firstName, lastName, seatNumber
FROM passengers
ORDER BY seatNumber LIMIT 1;
```

## 4 JPA构建

对于JPA，我们首先需要一个实体来映射我们的表：

```java
@Entity
class Passenger {

    @Id
    @GeneratedValue
    @Column(nullable = false)
    private Long id;

    @Basic(optional = false)
    @Column(nullable = false)
    private String firstName;

    @Basic(optional = false)
    @Column(nullable = false)
    private String lastName;

    @Basic(optional = false)
    @Column(nullable = false)
    private Integer seatNumber;
    // constructor, getters ...
}
```

接下来我们需要一个封装查询代码的方法，在这里实现为PassengerRepositoryImpl.findOrderedBySeatNumberLimitedTo(int limit)：

```java
@Repository
class PassengerRepositoryImpl implements CustomPassengerRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Passenger> findOrderedBySeatNumberLimitedTo(int limit) {
        return entityManager.createQuery("select p from Passenger p order by p.seatNumber", Passenger.class)
                .setMaxResults(limit).getResultList();
    }
}
```

在我们的Repository方法中，我们使用EntityManager创建一个Query，在这个Query上我们调用setMaxResults()方法。

对Query.setMaxResults()的调用最终会导致将limit语句附加到生成的SQL中：

```sql
select passenger0_.id          as id1_15_,
       passenger0_.fist_name   as fist_nam2_15_,
       passenger0_.last_name   as last_nam3_15_,
       passenger0_.seat_number as seat_num4_15_
from passenger passenger0_
order by passenger0_.seat_number limit ?
```

## 5. Spring Data JPA

我们还可以使用Spring Data JPA生成SQL。

### 5.1 first或者top

我们可以通过使用关键字first或top的方法名派生来实现这一点。

我们可以选择指定一个数字作为将返回的最大结果大小。如果我们省略它，Spring Data JPA默认结果大小为1。

由于我们想知道第一个被占用的座位以及谁占用了它，我们可以通过以下两种方式省略数字：

```java
interface PassengerRepository extends JpaRepository<Passenger, Long>, CustomPassengerRepository {
    Passenger findFirstByOrderBySeatNumberAsc();

    Passenger findTopByOrderBySeatNumberAsc();
}
```

如果我们限制为一个实例结果，如上所述，那么我们也可以使用Optional包装结果：

```java
interface PassengerRepository extends JpaRepository<Passenger, Long>, CustomPassengerRepository {
    Optional<Passenger> findFirstByOrderBySeatNumberAsc();

    Optional<Passenger> findTopByOrderBySeatNumberAsc();
}
```

### 5.2 Pageable

或者，我们可以使用Pageable对象：

```java
Page<Passenger> page = repository.findAll(PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "seatNumber")));
```

如果我们看一下JpaRepository的默认实现SimpleJpaRepository类，我们可以看到它也调用了Query.setMaxResults()：

```java
public class SimpleJpaRepository<T, ID> implements JpaRepositoryImplementation<T, ID> {
    protected <S extends T> Page<S> readPage(TypedQuery<S> query,
                                             Class<S> domainClass, Pageable pageable,
                                             @Nullable Specification<S> spec) {
        if (pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        return PageableExecutionUtils.getPage(query.getResultList(), pageable,
                () -> executeCountQuery(this.getCountQuery(spec, domainClass)));
    }
}
```

### 5.3 比较

这两种替代方案都会生成我们所期望的SQL，first和top偏于约定，而Pageable偏于配置：

```sql
select passenger0_.id          as id1_15_,
       passenger0_.fist_name   as fist_nam2_15_,
       passenger0_.last_name   as last_nam3_15_,
       passenger0_.seat_number as seat_num4_15_
from passenger passenger0_
order by passenger0_.seat_number asc limit ?
```

## 6. 总结

JPA中分页查询结果与SQL略有不同；我们不直接将limit关键字包含在我们的JPQL中。相反，我们只需对Query#maxResults进行单个方法调用，或者在Spring Data JPA方法名称中使用关键字first或top。
