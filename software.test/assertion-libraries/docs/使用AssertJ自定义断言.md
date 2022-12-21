## 1. 概述

在本教程中，我们介绍如何创建自定义[AssertJ](https://joel-costigliola.github.io/assertj/)断言；AssertJ的基础知识可以在[这里]()找到。

简单地说，自定义断言允许创建特定于我们自己的类的断言，让我们的测试更好地反映领域模型。

## 2. 被测类

本教程中的测试用例将围绕Person类构建：

```java
public class Person {
	private String fullName;
	private int age;
	private List<String> nicknames;

	public Person(String fullName, int age) {
		this.fullName = fullName;
		this.age = age;
		this.nicknames = new ArrayList<>();
	}

	public void addNickname(String nickname) {
		nicknames.add(nickname);
	}

    // getters
}
```

## 3. 自定义断言类

编写自定义AssertJ断言类非常简单，**我们需要做的就是声明一个扩展AbstractAssert的类，添加一个必需的构造函数，并提供自定义断言方法**。

断言类必须扩展AbstractAssert类，以使我们能够访问API的基本断言方法，例如isNotNull和isEqualTo。

以下是Person的自定义断言类的骨架：

```java
public class PersonAssert extends AbstractAssert<PersonAssert, Person> {

	public PersonAssert(Person actual) {
		super(actual, PersonAssert.class);
	}

    // assertion methods described later
}
```

在扩展AbstractAssert类时，我们必须指定两个泛型参数：第一个是自定义断言类本身，它是编写方法链所必需的，第二个是被测类。

为了给我们的断言类提供一个入口点，我们可以定义一个静态方法，用于启动断言链：

```java
public static PersonAssert assertThat(Person actual) {
    return new PersonAssert(actual);
}
```

接下来，我们介绍PersonAssert类中包含的几个自定义断言。

第一个方法验证Person的fullname是否与String参数匹配：

```java
public PersonAssert hasFullName(String fullName) {
	isNotNull();
	if (!actual.getFullName().equals(fullName)) {
		failWithMessage("Expected person to have full name %s but was %s", fullName, actual.getFullName());
	}
	return this;
}
```

以下方法根据Person的age测试是否为成年人：

```java
public PersonAssert isAdult() {
    isNotNull();
    if (actual.getAge() < 18) {
        failWithMessage("Expected person to be adult");
    }
    return this;
}
```

最后检查是否存在nickname：

```java
public PersonAssert hasNickname(String nickName) {
	isNotNull();
	if (!actual.getNicknames().contains(nickName)) {
		failWithMessage("Expected person to have nickname %s", nickName);
	}
	return this;
}
```

当有多个自定义断言类时，我们可以将所有assertThat方法包装在一个类中，为每个断言类提供一个静态工厂方法：

```java
public class Assertions {

	public static PersonAssert assertThat(Person actual) {
		return new PersonAssert(actual);
	}

	// static factory methods of other assertion classes
}
```

上面显示的Assertions类是所有自定义断言类的方便入口点，此类的静态方法具有相同的名称，并且通过其参数类型相互区分。

## 4. 实践

以下测试用例用于测试我们在上一节中创建的自定义断言方法，请注意，assertThat方法是从我们的自定义Assertions类导入的，而不是核心AssertJ API。

下面是hasFullName方法的使用方式：

```java
@Test
public void whenPersonNameMatches_thenCorrect() {
    Person person = new Person("John Doe", 20);
    assertThat(person)
        .hasFullName("John Doe");
}
```

下面是isAdult方法的使用方式：

```java
@Test
public void whenPersonAgeLessThanEighteen_thenNotAdult() {
    Person person = new Person("Jane Roe", 16);

    // assertion fails
    assertThat(person).isAdult();
}
```

以及hasNickname方法的使用方式：

```java
@Test
public void whenPersonDoesNotHaveAMatchingNickname_thenIncorrect() {
    Person person = new Person("John Doe", 20);
    person.addNickname("Nick");

    // assertion will fail
    assertThat(person)
      .hasNickname("John");
}
```

## 5. 断言生成器

编写与对象模型相对应的自定义断言类为非常易读的测试用例奠定了基础。

**但是，如果我们有很多类，那么手动为所有这些类创建自定义断言类会很痛苦**，这就是AssertJ断言生成器发挥作用的地方。

要在Maven中使用断言生成器，我们需要在pom.xml文件中添加一个插件：

```xml
<plugin>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-assertions-generator-maven-plugin</artifactId>
    <version>2.1.0</version>
    <configuration>
        <classes>
            <param>cn.tuyucheng.taketoday.testing.assertj.custom.Person</param>
        </classes>
    </configuration>
</plugin>
```

可以在[此处](https://search.maven.org/classic/#search|ga|1|g%3A"org.assertj" AND a%3A"assertj-assertions-generator-maven-plugin")找到最新版本的assertj-assertions-generator-maven-plugin。

上述插件中的classes标签指定了我们要为其生成断言的类，有关插件的其他配置，请参阅[此帖](https://joel-costigliola.github.io/assertj/assertj-assertions-generator-maven-plugin.html#configuration)。

**AssertJ断言生成器为目标类的每个公共属性创建断言**，每个断言方法的具体名称取决于字段或属性的类型，有关断言生成器的完整描述，请查看[此文](https://joel-costigliola.github.io/assertj/assertj-assertions-generator.html)。

在项目根目录中执行以下Maven命令：

```bash
mvn assertj:generate-assertions
```

然后我们应该可以在target/generated-test-sources/assertj-assertions文件夹中看到生成的断言类，例如，generated断言生成的入口点类如下所示：

```java
// generated comments are stripped off for brevity

package cn.tuyucheng.taketoday.testing.assertj.custom;

@javax.annotation.Generated(value="assertj-assertions-generator")
public class Assertions {

    @org.assertj.core.util.CheckReturnValue
    public static cn.tuyucheng.taketoday.testing.assertj.custom.PersonAssert
      assertThat(cn.tuyucheng.taketoday.testing.assertj.custom.Person actual) {
        return new cn.tuyucheng.taketoday.testing.assertj.custom.PersonAssert(actual);
    }

    protected Assertions() {
        // empty
    }
}
```

现在，我们可以将生成的源文件复制到测试目录，然后添加自定义断言方法来满足我们的测试需求。

需要注意的重要一点是：生成的代码不能保证完全正确。在这一点上，生成器还不是成品，社区团队还在努力完善。因此，我们应该使用生成器作为支持工具，而不是毫无顾虑地使用。

## 6. 总结

在本教程中，我们演示了如何手动和自动创建自定义断言，以使用AssertJ库创建可读的测试代码。

如果我们只有少量的待测类，手动解决就足够了；否则，使用生成器可能是更好的选择。