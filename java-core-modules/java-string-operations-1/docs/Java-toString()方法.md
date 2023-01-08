## 1. 概述

Java 中的每个类都是Object类的直接或间接子类。由于 Object类包含一个 toString() 方法，我们可以在任何实例上调用toString()并获取其字符串表示形式。

在本教程中，我们将了解toString()的 默认行为 并学习如何更改其行为。

## 2. 默认行为

每当我们打印对象引用时，它都会在内部调用toString()方法。因此，如果我们没有在类中定义 toString()方法， 则会调用Object# toString() 。

对象的 toString()方法非常通用：

```java
public String toString() {
    return getClass().getName()+"@"+Integer.toHexString(hashCode());
}
```

为了了解这是如何工作的，让我们创建一个 我们将在整个教程中使用的Customer 对象：

```java
public class Customer {
    private String firstName;
    private String lastName;
    // standard getters and setters. No toString() implementation
}
```

现在，如果我们尝试打印我们的C ustomer对象， Object # toString() 将被调用，输出将类似于：

```java
tostring.cn.tuyucheng.taketoday.Customer@6d06d69c
```

## 3.覆盖默认行为

查看上面的输出，我们可以看到它没有给我们提供太多关于 Customer对象内容的信息。通常，我们对了解对象的哈希码不感兴趣，而是对对象属性的内容感兴趣。

通过覆盖toString() 方法的默认行为，我们可以使方法调用的输出更有意义。

现在，让我们看一下使用对象的几个不同场景，看看我们如何覆盖此默认行为。

## 4. 原始类型和字符串

我们的 Customer对象同时具有 String和 primitive 属性。我们需要重写 toString()方法以获得更有意义的输出：

```java
public class CustomerPrimitiveToString extends Customer {
    private long balance;

    @Override
    public String toString() {
        return "Customer [balance=" + balance + ", getFirstName()=" + getFirstName()
          + ", getLastName()=" + getLastName() + "]";
    }
}

```

让我们看看现在调用toString() 会得到什么：

```java
@Test
public void givenPrimitive_whenToString_thenCustomerDetails() {
    CustomerPrimitiveToString customer = new CustomerPrimitiveToString();
    customer.setFirstName("Rajesh");
    customer.setLastName("Bhojwani");
    customer.setBalance(110);
    assertEquals("Customer [balance=110, getFirstName()=Rajesh, getLastName()=Bhojwani]", 
      customer.toString());
}
```

## 5.复杂的Java对象

现在让我们考虑一个场景，其中我们的Customer对象还包含一个类型为 Order 的订单 属性。我们的 Order 类同时具有 String和原始数据类型字段。

所以，让我们再次覆盖toString()：

```java
public class CustomerComplexObjectToString extends Customer {
    private Order order;
    //standard setters and getters
    
    @Override
    public String toString() {
        return "Customer [order=" + order + ", getFirstName()=" + getFirstName()
          + ", getLastName()=" + getLastName() + "]";
    }      
}
```

由于订单 是一个复杂的对象，如果我们只打印 客户对象，而不重写 订单类中的toString() 方法 ，它将打印订单为 Order@ <hashcode>。

为了解决这个问题，让我们也覆盖 Order中的toString()：

```java
public class Order {
    
    private String orderId;
    private String desc;
    private long value;
    private String status;
 
    @Override
    public String toString() {
        return "Order [orderId=" + orderId + ", desc=" + desc + ", value=" + value + "]";
    }
}

```

现在，让我们看看当我们调用 包含 订单 属性的Customer对象的toString() 方法时会发生什么：

```java
@Test
public void givenComplex_whenToString_thenCustomerDetails() {
    CustomerComplexObjectToString customer = new CustomerComplexObjectToString();    
    // .. set up customer as before
    Order order = new Order();
    order.setOrderId("A1111");
    order.setDesc("Game");
    order.setStatus("In-Shiping");
    customer.setOrders(order);
        
    assertEquals("Customer [order=Order [orderId=A1111, desc=Game, value=0], " +
      "getFirstName()=Rajesh, getLastName()=Bhojwani]", customer.toString());
}
```

## 6.对象数组

接下来，让我们将Customer 更改为包含Order的数组 。如果我们只打印我们的 Customer对象，而不对我们的订单对象进行特殊处理 ，它将打印订单为 Order;@<hashcode>。

为了解决这个问题，让我们 对 订单字段使用[Arrays.toString() ：](https://www.baeldung.com/java-array-to-string) 

```java
public class CustomerArrayToString  extends Customer {
    private Order[] orders;

    @Override
    public String toString() {
        return "Customer [orders=" + Arrays.toString(orders) 
          + ", getFirstName()=" + getFirstName()
          + ", getLastName()=" + getLastName() + "]";
    }    
}

```

让我们看看调用上面的toString()方法的结果：

```java
@Test
public void givenArray_whenToString_thenCustomerDetails() {
    CustomerArrayToString customer = new CustomerArrayToString();
    // .. set up customer as before
    // .. set up order as before
    customer.setOrders(new Order[] { order });         
    
    assertEquals("Customer [orders=[Order [orderId=A1111, desc=Game, value=0]], " +
      "getFirstName()=Rajesh, getLastName()=Bhojwani]", customer.toString());
}
```

## 7. 包装器、集合和StringBuffer

当一个对象完全由[wrappers](https://www.baeldung.com/java-wrapper-classes)、[collections](https://www.baeldung.com/java-collections)或[StringBuffer s](https://www.baeldung.com/java-collections)组成时，不需要自定义toString() 实现，因为这些对象已经用有意义的表示覆盖了toString()方法：

```java
public class CustomerWrapperCollectionToString extends Customer {
    private Integer score; // Wrapper class object
    private List<String> orders; // Collection object
    private StringBuffer fullname; // StringBuffer object
  
    @Override
    public String toString() {
        return "Customer [score=" + score + ", orders=" + orders + ", fullname=" + fullname
          + ", getFirstName()=" + getFirstName() + ", getLastName()=" + getLastName() + "]";
    }
}

```

让我们再次查看调用 toString()的结果：

```java
@Test
public void givenWrapperCollectionStrBuffer_whenToString_thenCustomerDetails() {
    CustomerWrapperCollectionToString customer = new CustomerWrapperCollectionToString();
    // .. set up customer as before
    // .. set up orders as before 
    customer.setOrders(new Order[] { order }); 
    
    StringBuffer fullname = new StringBuffer();
    fullname.append(customer.getLastName()+ ", " + customer.getFirstName());
    
    assertEquals("Customer [score=8, orders=[Book, Pen], fullname=Bhojwani, Rajesh, getFirstName()=Rajesh, "
      + "getLastName()=Bhojwani]", customer.toString());
}
```

## 八、总结

在本文中，我们着眼于创建我们自己的 toString() 方法实现。