## 1. 概述

在这个简短的教程中，我们将了解如何使用Java 11否定Predicate方法引用。

我们将从在Java 11之前为实现此目标而遇到的限制开始，然后我们将看到Predicate.not()方法如何提供帮助。

## 2. Java 11之前

首先，让我们看看在Java 11之前我们是如何设法否定Predicate的。

首先，让我们创建一个带有age字段和isAdult()方法的Person类：

```java
public class Person {
    private static final int ADULT_AGE = 18;

    private int age;

    public Person(int age) {
        this.age = age;
    }

    public boolean isAdult() {
        return age >= ADULT_AGE;
    }
}
```

现在，让我们假设我们有一个Person列表：

```java
List<Person> people = Arrays.asList(
    new Person(1),
    new Person(18),
    new Person(2)
);
```

我们想要检索所有的成年人(age > 18)。为了在Java 8中实现这一点，我们可以：

```java
people.stream()                      
    .filter(Person::isAdult)           
    .collect(Collectors.toList());
```

但是，如果我们想检索非成年人怎么办？然后我们必须否定谓词：

```java
people.stream()                       
    .filter(person -> !person.isAdult())
    .collect(Collectors.toList());
```

**不幸的是，我们不得不放弃方法引用，即使我们发现它更容易阅读**。一种可能的解决方法是在Person类中创建一个isNotAdult()方法，然后使用对该方法的引用：

```java
people.stream()                 
    .filter(Person::isNotAdult)   
    .collect(Collectors.toList());
```

但也许我们不想将这个方法添加到我们的API中，或者我们只是不能，因为该类不是我们的。这就是Java 11推出Predicate.not()方法的时候，我们将在下一节中看到。

## 3. Predicate.not()方法

**Predicate.not()静态方法已添加到Java 11中，以否定现有的Predicate**。

让我们以前面的例子来看看这意味着什么。我们可以使用这个新方法，而不是使用lambda或在Person类上创建新方法：

```java
people.stream()                          
    .filter(Predicate.not(Person::isAdult))
    .collect(Collectors.toList());
```

这样，我们就不必修改我们的API，仍然可以依赖方法引用的可读性。

我们可以通过静态导入使这一点更加清晰：

```java
people.stream()                  
    .filter(not(Person::isAdult))  
    .collect(Collectors.toList());
```

## 4. 总结

在这篇简短的文章中，我们看到了如何利用Predicate.not()方法来维护谓词的方法引用的使用，即使它们被否定。