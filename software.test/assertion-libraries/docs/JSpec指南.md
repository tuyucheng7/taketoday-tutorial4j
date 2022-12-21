## 1. 概述

[JUnit](http://junit.org/)和[TestNG](http://testng.org/)等测试运行器框架提供了一些基本的断言方法(assertTrue、assertNotNull等)。

然后还有诸如[Hamcrest](http://hamcrest.org/)、[AssertJ](https://joel-costigliola.github.io/assertj/)和[Truth](https://github.com/google/truth)之类的断言框架，它们提供了流式而丰富的断言方法，其名称通常以“assertThat”开头。

**[JSpec](http://javalite.io/jspec)是另一个框架，它允许我们编写更接近于我们用自然语言编写规范的方式来编写流畅的断言，尽管与其他框架的方式略有不同**。

在本文中，我们学习如何使用JSpec，并演示编写规范所需的方法以及测试失败时将打印的消息。

## 2. Maven依赖

首先添加JSpec的javalite-common依赖项：

```xml
<dependency>
    <groupId>org.javalite</groupId>
    <artifactId>javalite-common</artifactId>
    <version>1.4.13</version>
</dependency>
```

## 3. 比较断言风格

与典型的基于规则的断言方式不同，我们只编写行为规范。让我们看一个在JUnit、AssertJ和JSpec中断言相等性的快速例子。

在JUnit中，我们会这样写：

```java
assertEquals(1 + 1, 2);
```

在AssertJ中，我们会这样写：

```java
assertThat(1 + 1).isEqualTo(2);
```

下面是在JSpec中编写的相同测试：

```java
$(1 + 1).shouldEqual(2);
```

JSpec使用与流式断言框架相同的样式，但省略了前导的assert/assertThat关键字并使用should代替。

**以这种方式编写断言可以更容易地表示真正的规范，接近TDD和BDD概念**。

看看以下例子如何非常接近我们自然编写的规范：

```java
String message = "Welcome to JSpec demo";
the(message).shouldNotBe("empty");
the(message).shouldContain("JSpec");
```

## 4. 规格结构

**规范声明语句由两部分组成：期望创建者和期望方法**。

### 4.1 期望创建者

期望创建者使用以下静态导入的方法之一**生成Expectation对象**：a()、the()、it()、$()：

```java
$(1 + 2).shouldEqual(3);
a(1 + 2).shouldEqual(3);
the(1 + 2).shouldEqual(3);
it(1 + 2).shouldEqual(3);
```

所有这些方法本质上是相同的，它们都只是为了提供各种方式来表达我们的规范。

唯一的区别是**it()方法是类型安全的**，只允许比较相同类型的对象：

```java
it(1 + 2).shouldEqual("3");
```

使用it()比较不同类型的对象会导致编译错误。

### 4.2 期望方法

规范声明语句的第二部分是期望方法，**它讲述了所需的规范**，如shouldEqual、shouldContain。

当测试失败时，会出现javalite.test.jspec.TestException类型的异常，TestException显示一条表达性消息。我们会在下面的部分中介绍这些失败消息的例子。

## 5. 内置期望

### 5.1 相等性期望

**shouldEqual(), shouldBeEqual(), shouldNotBeEqual()**

这些指定两个对象应该/不应该相等，使用java.lang.Object.equals()方法检查是否相等：

```java
$(1 + 2).shouldEqual(3);
```

失败场景：

```java
$(1 + 2).shouldEqual(4);
```

生成的消息如下：

```plaintext
Test object:java.lang.Integer == <3>
and expected java.lang.Integer == <4>
are not equal, but they should be.
```

### 5.2 布尔属性期望

**shouldHave(), shouldNotHave()**

我们使用这些方法来**指定对象的命名布尔属性是否应该/不应该返回true**：

```java
Cage cage = new Cage();
cage.put(tomCat, boltDog);
the(cage).shouldHave("animals");
```

这要求Cage类包含一个带有签名的方法：

```java
boolean hasAnimals() {...}
```

失败场景：

```java
the(cage).shouldNotHave("animals");
```

生成的消息如下：

```plaintext
Method: hasAnimals should return false, but returned true
```

**shouldBe(), shouldNotBe()**

我们使用这些来指定测试对象应该/不应该是什么：

```java
the(cage).shouldNotBe("empty");
```

这要求Cage类包含一个签名为“boolean isEmpty()”的方法。

失败场景：

```java
the(cage).shouldBe("empty");
```

生成的消息如下：

```plaintext
Method: isEmpty should return true, but returned false
```

### 5.3 类型期望

**shouldBeType(), shouldBeA()**

我们可以使用这些方法来指定一个对象应该是一个特定的类型：

```java
cage.put(boltDog);
Animal releasedAnimal = cage.release(boltDog);
the(releasedAnimal).shouldBeA(Dog.class);
```

失败场景：

```java
the(releasedAnimal).shouldBeA(Cat.class);
```

生成的消息如下：

```plaintext
class cn.tuyucheng.taketoday.jspec.Dog is not class cn.tuyucheng.taketoday.jspec.Cat
```

### 5.4 可空性期望

**shouldBeNull(), shouldNotBeNull()**

我们使用这些来指定测试对象应该/不应该为null：

```java
cage.put(boltDog);
Animal releasedAnimal = cage.release(dogY);
the(releasedAnimal).shouldBeNull();
```

失败场景：

```java
the(releasedAnimal).shouldNotBeNull();
```

生成的消息如下：

```plaintext
Object is null, while it is not expected
```

### 5.5 引用预期

**shouldBeTheSameAs(), shouldNotBeTheSameAs()**

这些方法用于指定对象的引用应与预期的引用相同：

```java
Dog firstDog = new Dog("Rex");
Dog secondDog = new Dog("Rex");
$(firstDog).shouldEqual(secondDog);
$(firstDog).shouldNotBeTheSameAs(secondDog);
```

失败场景：

```java
$(firstDog).shouldBeTheSameAs(secondDog);
```

生成的消息如下：

```plaintext
references are not the same, but they should be
```

### 5.6 集合和字符串内容期望

**shouldContain(), shouldNotContain()**
我们使用这些来指定测试的Collection或Map应该/不应该包含给定元素：

```java
cage.put(tomCat, felixCat);
the(cage.getAnimals()).shouldContain(tomCat);
the(cage.getAnimals()).shouldNotContain(boltDog);
```

失败场景：

```java
the(animals).shouldContain(boltDog);
```

生成的消息如下：

```plaintext
tested value does not contain expected value: Dog [name=Bolt]
```

**我们还可以使用这些方法来指定String应该/不应该包含给定的子字符串**：

```java
$("Welcome to JSpec demo").shouldContain("JSpec");
```

虽然看起来很奇怪，但我们可以将这种行为扩展到其他对象类型，使用它们的toString()方法进行比较：

```java
cage.put(tomCat, felixCat);
the(cage).shouldContain(tomCat);
the(cage).shouldNotContain(boltDog);
```

澄清一下，Cat对象tomcat的toString()方法生成的输出如下：

```plaintext
Cat [name=Tom]
```

这是cage对象的toString()输出的子字符串：

```plaintext
Cage [animals=[Cat [name=Tom], Cat[name=Felix]]]
```

## 6. 自定义期望

除了内置的期望之外，JSpec还允许我们编写自定义期望。

### 6.1 差异期望

我们可以编写一个DifferenceExpectation来指定执行某些代码的返回值不应等于特定值。

在这个简单的例子中，我们确保操作(2 + 3)不会给我们结果(4)：

```java
expect(new DifferenceExpectation<Integer>(4) {
    @Override
    public Integer exec() {
        return 2 + 3;
    }
});
```

我们还可以使用它来确保执行某些代码会更改某些变量或方法的状态或值。

例如，当从包含两只动物的笼子中释放动物时，尺寸应该不同：

```java
cage.put(tomCat, boltDog);
expect(new DifferenceExpectation<Integer>(cage.size()) {
    @Override
    public Integer exec() {
        cage.release(tomCat);
        return cage.size();
    }
});
```

失败场景：

在这里，我们试图释放笼子中不存在的动物：

```java
cage.release(felixCat);
```

大小不会改变，我们会得到以下消息：

```plaintext
Objects: '2' and '2' are equal, but they should not be
```

### 6.2 异常预期

我们可以编写ExceptionExpectation来指定被测试的代码应该抛出一个Exception，只需将预期的异常类型传递给构造函数并将其作为泛型类型提供：

```java
expect(new ExceptionExpectation<ArithmeticException>(ArithmeticException.class) {
    @Override
    public void exec() throws ArithmeticException {
        System.out.println(1 / 0);
    }
});
```

**失败场景#1**：

```java
System.out.println(1 / 1);
```

由于上面代码不会导致任何异常，因此执行它会产生以下消息：

```plaintext
Expected exception: class java.lang.ArithmeticException, but instead got nothing
```

**失败场景#2**：

```java
Integer.parseInt("x");
```

这会导致与预期异常不同的异常：

```plaintext
class java.lang.ArithmeticException,
but instead got: java.lang.NumberFormatException: For input string: "x"
```

## 7. 总结

其他流式的断言框架为集合断言、异常断言和Java 8集成提供了更好的方法，但JSpec提供了一种以规范形式编写断言的独特方式。

它有一个简单的API，可以让我们像自然语言一样编写断言，并提供描述性的测试失败消息。