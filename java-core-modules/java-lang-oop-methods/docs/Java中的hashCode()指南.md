## 1. 概述

哈希是计算机科学的一个基本概念。

在Java中，高效的哈希算法支持一些最流行的集合，例如[HashMap](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/HashMap.html)(查看[这篇](https://www.baeldung.com/java-hashmap)深入的文章)和[HashSet](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/HashSet.html)。

在本教程中，我们将重点介绍hashCode()的工作原理、它如何在集合中发挥作用以及如何正确实现它。

### 延伸阅读

### [Java equals()和hashCode()约定](https://www.baeldung.com/java-equals-hashcode-contracts)

了解equals()和hasCode()需要履行的契约以及两种方法之间的关系

[阅读更多](https://www.baeldung.com/java-equals-hashcode-contracts)→

### [使用Eclipse生成equals()和hashCode()](https://www.baeldung.com/java-eclipse-equals-and-hashcode)

使用Eclipse IDE生成equals()和hashcode()的快速实用指南

[阅读更多](https://www.baeldung.com/java-eclipse-equals-and-hashcode)→

### [Lombok项目简介](https://www.baeldung.com/intro-to-project-lombok)

对标准Java代码上Project Lombok的许多有用用例的全面且非常实用的介绍。

[阅读更多](https://www.baeldung.com/intro-to-project-lombok)→

## 2. 在数据结构中使用hashCode()

在某些情况下，对集合最简单的操作可能效率低下。

为了说明这一点，这会触发一个线性搜索，这对于大型列表来说是非常无效的：

```java
List<String> words = Arrays.asList("Welcome", "to", "Tuyucheng");
if (words.contains("Tuyucheng")) {
    System.out.println("Tuyucheng is in the list");
}
```

Java提供了很多数据结构来专门处理这个问题。比如几个Map接口的实现就是[哈希表](https://www.baeldung.com/cs/hash-tables)。

使用哈希表时，**这些集合使用hashCode()方法计算给定键的哈希值**。然后他们在内部使用这个值来存储数据，以便访问操作更加高效。

## 3. 了解hashCode()的工作原理

简单地说，hashCode()返回一个由哈希算法生成的整数值。

相等的对象(根据它们的equals())必须返回相同的哈希码。**不同的对象不需要返回不同的哈希码**。

hashCode()的一般契约规定：

-   每当在Java应用程序的执行过程中对同一个对象多次调用它时，hashCode()必须始终返回相同的值，前提是在对象的equals比较中使用的信息没有被修改。从应用程序的一次执行到同一应用程序的另一次执行，此值不需要保持一致。

-   如果根据equals(Object)方法两个对象相等，则对这两个对象中的每一个调用hashCode()方法都必须产生相同的值。

-   如果根据[equals(java.lang.Object)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html#equals(java.lang.Object))方法两个对象不相等，则对这两个对象中的每一个调用hashCode方法不需要产生不同的整数结果。但是，开发人员应该意识到，为不相等的对象生成不同的整数结果可以提高哈希表的性能。

>   “在合理可行的范围内，由类Object定义的hashCode()方法确实会为不同的对象返回不同的整数。(这通常是通过将对象的内部地址转换为整数来实现的，但JavaTM编程语言不需要这种实现技术。)”

## 4. 一个朴素的hashCode()实现

完全遵守上述约定的朴素hashCode()实现实际上非常简单。

为了证明这一点，我们将定义一个覆盖方法默认实现的示例User类：

```java
public class User {

    private long id;
    private String name;
    private String email;

    // standard getters/setters/constructors

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id
              && (name.equals(user.name)
              && email.equals(user.email));
    }

    // getters and setters here
}
```

User类为equals()和hashCode()提供了完全遵循各自协定的自定义实现。更重要的是，让hashCode()返回任何固定值并没有什么不合法的。

**但是，此实现将哈希表的功能降级为基本为零，因为每个对象都将存储在同一个存储桶中**。

在这种情况下，哈希表查找是线性执行的，并没有给我们带来任何真正的好处。我们将在第7节中对此进行更多讨论。

## 5. 改进hashCode()实现

让我们通过包含User类的所有字段来改进当前的hashCode()实现，以便它可以为不相等的对象产生不同的结果：

```java
@Override
public int hashCode() {
    return (int) id * name.hashCode() * email.hashCode();
}
```

这个基本的哈希算法肯定比前一个要好得多。这是因为它仅通过将name和email字段的哈希码与id相乘来计算对象的哈希码。

一般来说，我们可以说这是一个合理的hashCode()实现，只要我们保持equals()实现与它一致。

## 6. 标准hashCode()实现

我们用于计算哈希码的哈希算法越好，哈希表的性能就越好。

让我们看一个“标准”实现，它使用两个质数来为计算的哈希码添加更多的唯一性：

```java
@Override
public int hashCode() {
    int hash = 7;
    hash = 31 * hash + (int) id;
    hash = 31 * hash + (name == null ? 0 : name.hashCode());
    hash = 31 * hash + (email == null ? 0 : email.hashCode());
    return hash;
}
```

虽然我们需要了解hashCode()和equals()方法所扮演的角色，但我们不必每次都从头开始实现它们。这是因为大多数IDE可以生成自定义hashCode()和equals()实现。从Java 7开始，我们有了一个Objects.hash()实用方法来实现舒适的哈希：

```java
Objects.hash(name, email)
```

[IntelliJ IDEA](https://www.jetbrains.com/idea/)生成以下实现：

```java
@Override
public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + name.hashCode();
    result = 31 * result + email.hashCode();
    return result;
}
```

而[Eclipse](https://www.eclipse.org/downloads/?)生成这个：

```java
@Override
public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((email == null) ? 0 : email.hashCode());
    result = prime * result + (int) (id ^ (id >>> 32));
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
}
```

除了上述基于IDE的hashCode()实现之外，还可以自动生成高效的实现，例如使用[Lombok](https://projectlombok.org/features/EqualsAndHashCode)。

在这种情况下，我们需要将[lombok-maven](https://search.maven.org/classic/#search|ga|1|lombok)依赖项添加到pom.xml：

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok-maven</artifactId>
    <version>1.16.18.0</version>
    <type>pom</type>
</dependency>
```

现在使用@EqualsAndHashCode标注User类就足够了：

```java
@EqualsAndHashCode 
public class User {
    // fields and methods here
}
```

同样，如果我们希望[Apache Commons Lang的HashCodeBuilder类](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/builder/HashCodeBuilder.html)为我们生成一个hashCode()实现，我们在pom文件中包含[commons-lang](https://search.maven.org/classic/#search|ga|1|apache-commons-lang) Maven依赖项：

```xml
<dependency>
    <groupId>commons-lang</groupId>
    <artifactId>commons-lang</artifactId>
    <version>2.6</version>
</dependency>
```

hashCode()可以这样实现：

```java
public class User {
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
              append(id).
              append(name).
              append(email).
              toHashCode();
    }
}
```

一般来说，在实现hashCode()时没有通用的方法。我们强烈推荐阅读[Joshua Bloch的Effective Java](https://www.amazon.com/Effective-Java-3rd-Joshua-Bloch/dp/0134685997)。它提供了实现高效哈希算法的[详尽指南](https://es.slideshare.net/MukkamalaKamal/joshua-bloch-effect-java-chapter-3)列表。

请注意，所有这些实现都以某种形式使用了数字31。这是因为31有一个不错的属性。它的乘法可以用位移代替，这比标准乘法更快：

```java
31 * i == (i << 5) - i
```

## 7. 处理哈希冲突

哈希表的内在行为带来了这些数据结构的一个相关方面：即使使用高效的哈希算法，两个或多个对象可能具有相同的哈希码，即使它们不相等。因此，即使它们具有不同的哈希表键，它们的哈希码也会指向同一个存储桶。

这种情况通常称为哈希冲突，并且[存在各种处理它的方法](https://courses.cs.washington.edu/courses/cse373/18au/files/slides/lecture13.pdf)，每种方法都有其优点和缺点。Java的HashMap使用[单独的链接方法](https://en.wikipedia.org/wiki/Hash_table#Separate_chaining_with_linked_lists)来处理冲突：

“**当两个或多个对象指向同一个存储桶时，它们只是存储在一个链表中。在这种情况下，哈希表是一个链表数组，具有相同哈希值的每个对象都附加到数组中存储桶索引处的链表中**。

**在最坏的情况下，几个桶存储将绑定一个链表，并且列表中的对象的检索将线性执行**。”

哈希冲突方法简明扼要地说明了为什么高效实现hashCode()如此重要。

Java 8[为HashMap实现带来了一个有趣的增强](https://openjdk.java.net/jeps/180)。如果一个存储桶的大小超过了一定的阈值，树图就会取代链表。这允许实现O(logn)查找而不是悲观的O(n)。

## 8. 创建一个简单的应用程序

现在我们将测试标准hashCode()实现的功能。

让我们创建一个简单的Java应用程序，它将一些User对象添加到HashMap并使用[SLF4J](https://www.baeldung.com/slf4j-with-log4j2-logback)在每次调用该方法时将消息记录到控制台。

这是示例应用程序的入口点：

```java
public class Application {

    public static void main(String[] args) {
        Map<User, User> users = new HashMap<>();
        User user1 = new User(1L, "John", "john@domain.com");
        User user2 = new User(2L, "Jennifer", "jennifer@domain.com");
        User user3 = new User(3L, "Mary", "mary@domain.com");

        users.put(user1, user1);
        users.put(user2, user2);
        users.put(user3, user3);
        if (users.containsKey(user1)) {
            System.out.print("User found in the collection");
        }
    }
}
```

这是hashCode()实现：

```java
public class User {

    // ...

    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (int) id;
        hash = 31 * hash + (name == null ? 0 : name.hashCode());
        hash = 31 * hash + (email == null ? 0 : email.hashCode());
        logger.info("hashCode() called - Computed hash: " + hash);
        return hash;
    }
}
```

在这里，需要特别注意的是，每次将对象存储在HashMap中并使用containsKey()方法进行检查时，都会调用hashCode()并将计算出的哈希码打印到控制台：

```shell
[main] INFO cn.tuyucheng.taketoday.entities.User - hashCode() called - Computed hash: 1255477819
[main] INFO cn.tuyucheng.taketoday.entities.User - hashCode() called - Computed hash: -282948472
[main] INFO cn.tuyucheng.taketoday.entities.User - hashCode() called - Computed hash: -1540702691
[main] INFO cn.tuyucheng.taketoday.entities.User - hashCode() called - Computed hash: 1255477819
User found in the collection
```

## 9. 总结

很明显，生成高效的hashCode()实现通常需要混合使用一些数学概念(即素数和任意数)、逻辑和基本数学运算。

无论如何，我们完全可以在不借助这些技术的情况下有效地实现hashCode()。我们只需要确保哈希算法为不相等的对象生成不同的哈希码，并且它与equals()的实现一致。