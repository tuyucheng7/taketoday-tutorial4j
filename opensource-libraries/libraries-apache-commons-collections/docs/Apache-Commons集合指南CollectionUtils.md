## 1. 概述

简而言之，Apache CollectionUtils为涵盖广泛用例的常见操作提供了实用方法，有助于避免编写样板代码。该库针对较旧的 JVM 版本，因为目前，Java 8 的Stream API 提供了类似的功能。

## 2.Maven依赖

我们需要添加以下依赖项才能开始使用CollectionUtils：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.1</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.commons" AND a%3A"commons-collections4")找到该库的最新版本。

## 3.设置

让我们添加Customer和Address 类：

```java
public class Customer {
    private Integer id;
    private String name;
    private Address address;

    // standard getters and setters
}

public class Address {
    private String locality;
    private String city;
   
    // standard getters and setters
}
```

我们还将随时准备好以下Customer和List实例来测试我们的实现：

```java
Customer customer1 = new Customer(1, "Daniel", "locality1", "city1");
Customer customer2 = new Customer(2, "Fredrik", "locality2", "city2");
Customer customer3 = new Customer(3, "Kyle", "locality3", "city3");
Customer customer4 = new Customer(4, "Bob", "locality4", "city4");
Customer customer5 = new Customer(5, "Cat", "locality5", "city5");
Customer customer6 = new Customer(6, "John", "locality6", "city6");

List<Customer> list1 = Arrays.asList(customer1, customer2, customer3);
List<Customer> list2 = Arrays.asList(customer4, customer5, customer6);
List<Customer> list3 = Arrays.asList(customer1, customer2);

List<Customer> linkedList1 = new LinkedList<>(list1);
```

## 4.收集工具

让我们来看看Apache Commons CollectionUtils类中一些最常用的方法。

### 4.1. 仅添加非空元素

我们可以使用CollectionUtils 的 addIgnoreNull方法只将非空元素添加到提供的集合中。

此方法的第一个参数是我们要添加元素的集合，第二个参数是我们要添加的元素：

```java
@Test
public void givenList_whenAddIgnoreNull_thenNoNullAdded() {
    CollectionUtils.addIgnoreNull(list1, null);
 
    assertFalse(list1.contains(null));
}
```

请注意，null未添加到列表中。

### 4.2. 整理清单

我们可以使用collate方法来整理两个已经排序的列表。此方法将我们要合并的两个列表作为参数并返回一个排序列表：

```java
@Test
public void givenTwoSortedLists_whenCollated_thenSorted() {
    List<Customer> sortedList = CollectionUtils.collate(list1, list2);

    assertEquals(6, sortedList.size()); 
    assertTrue(sortedList.get(0).getName().equals("Bob"));
    assertTrue(sortedList.get(2).getName().equals("Daniel"));
}
```

### 4.3. 转换对象

我们可以使用transform方法将类 A 的对象转换为类 B 的不同对象。此方法将类 A 的对象列表和转换器作为参数。

此操作的结果是 B 类对象的列表：

```java
@Test
public void givenListOfCustomers_whenTransformed_thenListOfAddress() {
    Collection<Address> addressCol = CollectionUtils.collect(list1, 
      new Transformer<Customer, Address>() {
        public Address transform(Customer customer) {
            return customer.getAddress();
        }
    });
    
    List<Address> addressList = new ArrayList<>(addressCol);
    assertTrue(addressList.size() == 3);
    assertTrue(addressList.get(0).getLocality().equals("locality1"));
}
```

### 4.4. 过滤对象

使用过滤器，我们可以从列表中删除不满足给定条件的对象。 该方法将列表作为第一个参数，将谓词作为第二个参数。

filterInverse方法做相反的事情。当Predicate返回 true 时，它从列表中删除对象。

如果修改了输入列表，则filter和filterInverse都返回true，即如果从列表中过滤掉至少一个对象：

```java
@Test
public void givenCustomerList_WhenFiltered_thenCorrectSize() {
    
    boolean isModified = CollectionUtils.filter(linkedList1, 
      new Predicate<Customer>() {
        public boolean evaluate(Customer customer) {
            return Arrays.asList("Daniel","Kyle").contains(customer.getName());
        }
    });
     
    assertTrue(linkedList1.size() == 2);
}
```

如果我们希望返回结果列表而不是布尔标志，我们可以使用select和selectRejected 。

### 4.5. 检查非空

当我们想要检查列表中是否至少有单个元素时，isNotEmpty方法非常方便。另一种检查方法是：

```java
boolean isNotEmpty = (list != null && list.size() > 0);
```

虽然上面的代码行做同样的事情，但CollectionUtils.isNotEmpty使我们的代码更简洁：

```java
@Test
public void givenNonEmptyList_whenCheckedIsNotEmpty_thenTrue() {
    assertTrue(CollectionUtils.isNotEmpty(list1));
}
```

isEmpty做相反的事情。它检查给定列表是否为空或列表中是否有零个元素：

```java
List<Customer> emptyList = new ArrayList<>();
List<Customer> nullList = null;
 
assertTrue(CollectionUtils.isEmpty(nullList));
assertTrue(CollectionUtils.isEmpty(emptyList));
```

### 4.6. 检查包含

我们可以使用isSubCollection来检查一个集合是否包含在另一个集合中。isSubCollection将两个集合作为参数，如果第一个集合是第二个集合的子集合，则返回true ：

```java
@Test
public void givenCustomerListAndASubcollection_whenChecked_thenTrue() {
    assertTrue(CollectionUtils.isSubCollection(list3, list1));
}
```

如果一个对象在第一个集合中出现的次数小于或等于它在第二个集合中出现的次数，则该集合是另一个集合的子集合。

### 4.7. 集合的交集

我们可以使用CollectionUtils.intersection方法来获取两个集合的交集。此方法采用两个集合并返回两个输入集合中共有的元素集合：

```java
@Test
public void givenTwoLists_whenIntersected_thenCheckSize() {
    Collection<Customer> intersection = CollectionUtils.intersection(list1, list3);
    assertTrue(intersection.size() == 2);
}
```

元素在结果集合中出现的次数是它在每个给定集合中出现的次数的最小值。

### 4.8. 减去集合

CollectionUtils.subtract将两个集合作为输入并返回一个集合，该集合包含第一个集合中存在但第二个集合中不存在的元素：

```java
@Test
public void givenTwoLists_whenSubtracted_thenCheckElementNotPresentInA() {
    Collection<Customer> result = CollectionUtils.subtract(list1, list3);
    assertFalse(result.contains(customer1));
}
```

一个集合在结果中出现的次数是它在第一个集合中出现的次数减去它在第二个集合中出现的次数。

### 4.9. 收藏联盟

CollectionUtils.union执行两个集合的并集并返回一个集合，该集合包含第一个或第二个集合中的所有元素。

```java
@Test
public void givenTwoLists_whenUnioned_thenCheckElementPresentInResult() {
    Collection<Customer> union = CollectionUtils.union(list1, list2);
 
    assertTrue(union.contains(customer1));
    assertTrue(union.contains(customer4));
}
```

元素在结果集合中出现的次数是它在每个给定集合中出现的次数的最大值。

## 5.总结

我们完成了。

我们了解了CollectionUtils的一些常用方法——当我们在Java项目中处理集合时，这对于避免样板代码非常有用。