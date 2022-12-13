## 1. 简介

在本快速教程中，我们将探讨在Java中使用[Streams]()时如何使用 Stream.filter()方法。我们介绍如何使用它，以及如何处理带有受检异常的特殊情况。

## 2. 使用Stream.filter()

filter()方法是Stream接口的中间操作，它允许我们过滤与给定Predicate匹配的流元素：

```java
Stream<T> filter(Predicate<? super T> predicate)
```

要了解这是如何工作的，让我们创建一个Customer类：

```java
public class Customer {
	private String name;
	private int points;
	// Constructor and standard getters
}
```

此外，我们创建一个Customer集合：

```java
Customer john = new Customer("John P.", 15);
Customer sarah = new Customer("Sarah M.", 200);
Customer charles = new Customer("Charles B.", 150);
Customer mary = new Customer("Mary T.", 1);

List<Customer> customers = Arrays.asList(john, sarah, charles, mary);
```

### 2.1 过滤集合

filter()方法的一个常见用例是[处理集合]()。

让我们列出points超过100的客户。为此，我们可以使用lambda表达式：

```java
List<Customer> customersWithMoreThan100Points = customers
	.stream()
    .filter(c -> c.getPoints() > 100)
    .collect(Collectors.toList());
```

我们还可以使用[方法引用]()，它是lambda表达式的简写：

```java
List<Customer> customersWithMoreThan100Points = customers
	.stream()
    .filter(Customer::hasOverHundredPoints)
    .collect(Collectors.toList());
```

在这种情况下，我们需要将hasOverHundredPoints方法添加到我们的Customer类中：

```java
public boolean hasOverHundredPoints() {
    return this.points > 100;
}
```

在这两种情况下，我们得到相同的结果：

```java
assertThat(customersWithMoreThan100Points).hasSize(2);
assertThat(customersWithMoreThan100Points).contains(sarah, charles);
```

### 2.2 使用多个条件过滤集合

此外，我们可以在filter()中使用多个条件。例如，我们可以按points和name进行过滤：

```java
List<Customer> charlesWithMoreThan100Points = customers
	.stream()
    .filter(c -> c.getPoints() > 100 && c.getName().startsWith("Charles"))
    .collect(Collectors.toList());

assertThat(charlesWithMoreThan100Points).hasSize(1);
assertThat(charlesWithMoreThan100Points).contains(charles);
```

## 3. 处理异常

到目前为止，我们一直在使用带有不抛出异常的谓词的过滤器。实际上，**Java中的函数式接口不声明任何受检或非受检的异常**。

接下来我们将演示一些不同的方法来处理[lambda表达式中的异常]()。

### 3.1 使用自定义包装器

首先，我们向Customer添加一个profilePhotoUrl字段：

```java
private String profilePhotoUrl;
```

此外，让我们添加一个简单的hasValidProfilePhoto()方法来检查profilePhotoUrl的可用性：

```java
public boolean hasValidProfilePhoto() throws IOException {
    URL url = new URL(this.profilePhotoUrl);
    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
}
```

我们可以看到hasValidProfilePhoto()方法抛出了一个IOException。现在，如果我们尝试使用此方法过滤客户：

```java
List<Customer> customersWithValidProfilePhoto = customers
	.stream()
    .filter(Customer::hasValidProfilePhoto)
    .collect(Collectors.toList());
```

我们会看到以下错误：

```java
Incompatible thrown types java.io.IOException in functional expression
```

为了处理它，我们可以使用的替代方法之一是用try-catch块包装它：

```java
List<Customer> customersWithValidProfilePhoto = customers
	.stream()
    .filter(c -> {
        try {
            return c.hasValidProfilePhoto();
        } catch (IOException e) {
            //handle exception
        }
        return false;
    })
    .collect(Collectors.toList());
```

如果我们需要从我们的谓词中抛出异常，我们可以将它包装在一个非受检的异常中，比如RuntimeException。

### 3.2 使用ThrowingFunction

或者，我们可以使用ThrowingFunction库。ThrowingFunction是一个开源库，它允许我们在Java函数接口中处理受检的异常。

首先我们需要添加[throwing-function](https://search.maven.org/search?q=g:pl.touk AND a:throwing-function%26core%3Dgav)依赖项：

```xml
<dependency>
    <groupId>pl.touk</groupId>
    <artifactId>throwing-function</artifactId>
    <version>1.3</version>
</dependency>
```

为了处理谓词中的异常，该库为我们提供了ThrowingPredicate类，这个类有一个unchecked()方法来包装受检的异常。

让我们看看它的实际效果：

```java
List customersWithValidProfilePhoto = customers
    .stream()
    .filter(ThrowingPredicate.unchecked(Customer::hasValidProfilePhoto))
    .collect(Collectors.toList());
```

## 4. 总结

在本文中，我们演示了如何使用filter()方法处理流的示例，并介绍了一些处理异常的替代方案。