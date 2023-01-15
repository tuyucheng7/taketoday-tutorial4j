## 1. 概述

在这个简短的教程中，我们将看到如何使用Java11 否定Predicate方法引用。

我们将从在Java11 之前为实现此目标而遇到的限制开始。然后我们将看到 Predicate.not() 方法如何提供帮助。

## 2.Java 11 之前

首先，让我们看看在Java11 之前我们是如何设法否定Predicate的。

首先，让我们创建一个带有 年龄 字段和 isAdult()方法的Person类：

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

现在，让我们假设我们有一个人员列表：

```html
List<Person> people = Arrays.asList(
  new Person(1),
  new Person(18),
  new Person(2)
);
```

我们想要检索所有的成人。为了在Java8 中实现这一点，我们可以：

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

不幸的是，我们不得不放弃方法参考，即使我们发现它更容易阅读。一种可能的解决方法是在Person类上创建一个isNotAdult()方法 ，然后使用对该方法的引用：

```java
people.stream()                 
  .filter(Person::isNotAdult)   
  .collect(Collectors.toList());
```

但也许我们不想将这个方法添加到我们的 API 中，或者我们不能因为这个类不是我们的。这就是Java11 推出Predicate.not()方法的时候，我们将在下一节中看到。

## 3. Predicate.not()方法

Predicate.not() 静态方法已添加到Java11 以否定现有 的 Predicate。

让我们以前面的例子来看看这意味着什么。我们可以使用这个新方法，而不是使用 lambda 或在Person类上创建新方法：

```java
people.stream()                          
  .filter(Predicate.not(Person::isAdult))
  .collect(Collectors.toList());
```

这样，我们不必修改 API，仍然可以依赖方法引用的可读性。

我们可以通过静态导入使这一点更加清晰：

```java
people.stream()                  
  .filter(not(Person::isAdult))  
  .collect(Collectors.toList());
```

## 4。总结

在这篇简短的文章中，我们看到了如何利用Predicate .not()方法来维护谓词的方法引用的使用，即使它们被否定。