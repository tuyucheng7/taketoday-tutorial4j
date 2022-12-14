## 1. 概述

在本教程中，我们将使用[FreeBuilder库](https://freebuilder.inferred.org/)在Java中生成生成器类。

## 2. 建造者设计模式

Builder是面向对象语言中使用最广泛的[创建型设计模式之一]()，它抽象了复杂域对象的实例化，并提供了一个流式的API来创建实例。因此，它有助于维护一个简洁的域层。

尽管它很有用，但构建器的实现通常很复杂，尤其是在Java中，即使是更简单的值对象也需要大量样板代码。

## 3. Java中的Builder实现

在我们使用FreeBuilder之前，让我们为我们的Employee类实现一个样板构建器：

```java
public class Employee {

    private final String name;
    private final int age;
    private final String department;

    private Employee(String name, int age, String department) {
        this.name = name;
        this.age = age;
        this.department = department;
    }
}
```

还有一个内部Builder类：

```java
public static class Builder {

    private String name;
    private int age;
    private String department;

    public Builder setName(String name) {
        this.name = name;
        return this;
    }

    public Builder setAge(int age) {
        this.age = age;
        return this;
    }

    public Builder setDepartment(String department) {
        this.department = department;
        return this;
    }

    public Employee build() {
        return new Employee(name, age, department);
    }
}
```

因此，我们现在可以使用构建器来实例化Employee对象：

```java
Employee.Builder emplBuilder = new Employee.Builder();

Employee employee = emplBuilder
    .setName("tuyucheng")
    .setAge(12)
    .setDepartment("Builder Pattern")
    .build();
```

如上所示，实现构建器类需要大量样板代码。

在后面的部分中，我们会看到FreeBuilder如何简化这种实现。

## 4. Maven依赖

要添加FreeBuilder库，我们需要在pom.xml中添加[FreeBuilder](https://search.maven.org/search?q=g:org.inferred AND a:freebuilder&core=gav) Maven依赖项：

```xml
<dependency>
    <groupId>org.inferred</groupId>
    <artifactId>freebuilder</artifactId>
    <version>2.4.1</version>
</dependency>
```

## 5. FreeBuilder注解

### 5.1 生成构建器

FreeBuilder是一个开源库，可帮助开发人员在实现构建器类时避免编写样板代码。它利用Java中的注解处理来生成构建器模式的具体实现。

我们将使用@FreeBuilder注解标注前面提到中的Employee类，并查看它如何自动生成构建器类：

```java
@FreeBuilder
public interface Employee {
 
    String name();
    int age();
    String department();
    
    class Builder extends Employee_Builder {
    }
}
```

需要指出的是，Employee现在是一个接口而不是一个POJO类。此外，它还包含Employee对象的所有属性作为方法。

在我们继续使用这个构建器之前，我们必须配置我们的IDE以避免任何编译问题。由于FreeBuilder在编译期间自动生成Employee_Builder类，因此IDE通常会在第8行抱怨ClassNotFoundException。

为了避免此类问题，我们需要在[IntelliJ](https://www.jetbrains.com/help/idea/configuring-annotation-processing.html)或[Eclipse](https://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.jdt.doc.isv%2Fguide%2Fjdt_apt_getting_started.htm)中启用注解处理。在这样做的同时，我们将使用FreeBuilder的注解处理器org.inferred.freebuilder.processor.Processor。此外，用于生成这些源文件的目录应标记为[Generated Sources Root](https://www.jetbrains.com/help/idea/content-roots.html)。

或者，我们也可以执行mvn install来构建项目并生成所需的构建器类。

最后，我们编译了我们的项目，现在可以使用Employee.Builder类：

```java
Employee.Builder builder = new Employee.Builder();
 
Employee employee = builder.name("tuyucheng")
    .age(10)
    .department("Builder Pattern")
    .build();
```

总而言之，这个生成的类与我们之前看到的构建器类之间有两个主要区别。首先，我们必须为Employee类的所有属性设置值；否则，它会抛出IllegalStateException。

我们将在后面的部分中看到FreeBuilder如何处理可选属性。

其次，Employee.Builder的方法名称不遵循JavaBean命名约定，我们会在下一节中看到这一点。

### 5.2 JavaBean命名约定

为了强制FreeBuilder遵循JavaBean命名约定，我们必须重命名Employee中的方法，并在方法前加上get：

```java
@FreeBuilder
public interface Employee {
 
    String getName();
    int getAge();
    String getDepartment();

    class Builder extends Employee_Builder {
    }
}
```

这将生成遵循JavaBean命名约定的getter和setter：

```java
Employee employee = builder
    .setName("tuyucheng")
    .setAge(10)
    .setDepartment("Builder Pattern")
    .build();
```

### 5.3 映射器方法

结合getter和setter，FreeBuilder还在构建器类中添加了映射器方法。这些映射器方法接收[UnaryOperator]()作为输入参数，从而允许开发人员计算复杂的字段值。

假设我们的Employee类也有一个salary字段：

```java
@FreeBuilder
public interface Employee {
    Optional<Double> getSalaryInUSD();
}
```

现在假设我们需要转换作为输入提供的工资的货币：

```java
long salaryInEuros = INPUT_SALARY_EUROS;
Employee.Builder builder = new Employee.Builder();

Employee employee = builder
    .setName("tuyucheng")
    .setAge(10)
    .mapSalaryInUSD(sal -> salaryInEuros  EUROS_TO_USD_RATIO)
    .build();
```

FreeBuilder为所有字段提供了这样的映射器方法。

## 6. 默认值和约束检查

### 6.1 设置默认值

到目前为止我们讨论的Employee.Builder实现期望客户端传递所有字段的值。事实上，在缺少字段的情况下，它会导致初始化过程失败并引发IllegalStateException。

为了避免此类失败，我们可以为字段设置默认值或将其设置为可选。

我们可以在Employee.Builder构造函数中设置默认值：

```java
@FreeBuilder
public interface Employee {

    // getter methods

    class Builder extends Employee_Builder {

        public Builder() {
            setDepartment("Builder Pattern");
        }
    }
}
```

所以我们简单地在构造函数中设置默认department，该值将应用于所有Employee对象。

### 6.2 约束检查

通常，我们对字段值有一定的约束。例如，有效的电子邮件必须包含“@”或员工的年龄必须在某个范围内。

这样的约束要求我们对输入值进行验证，FreeBuilder允许我们仅通过覆盖setter方法来添加这些验证：

```java
@FreeBuilder
public interface Employee {

    // getter methods

    class Builder extends Employee_Builder {

        @Override
        public Builder setEmail(String email) {
            if (checkValidEmail(email))
                return super.setEmail(email);
            else
                throw new IllegalArgumentException("Invalid email");
        }

        private boolean checkValidEmail(String email) {
            return email.contains("@");
        }
    }
}
```

## 7. 可选值

### 7.1 使用Optional字段

某些对象包含可选字段，其值可以为空或null。FreeBuilder允许我们使用[Java Optional]()类型定义此类字段：

```java
@FreeBuilder
public interface Employee {

    String getName();
    int getAge();

    // other getters
    
    Optional<Boolean> getPermanent();

    Optional<String> getDateOfJoining();

    class Builder extends Employee_Builder {
    }
}
```

现在我们可以跳过为可选字段提供任何值：

```java
Employee employee = builder.setName("tuyucheng")
    .setAge(10)
    .setPermanent(true)
    .build();
```

值得注意的是，我们只是传递了强制字段的值而没有提供Optional字段的值，由于我们没有为dateOfJoining字段设置值，因此它将是Optional.empty()，这是Optional字段的默认值。

### 7.2 使用@Nullable字段

尽管在Java中建议使用Optional来处理null，但FreeBuilder允许我们使用[@Nullable]()来实现向后兼容性：

```java
@FreeBuilder
public interface Employee {

    String getName();
    int getAge();
    
    // other getter methods

    Optional<Boolean> getPermanent();
    Optional<String> getDateOfJoining();

    @Nullable String getCurrentProject();

    class Builder extends Employee_Builder {
    }
}
```

在某些情况下，使用Optional是不明智的，这也是构建器类首选@Nullable的另一个原因。

## 8. Collections和Maps

FreeBuilder对集合和Map有特殊的支持：

```java
@FreeBuilder
public interface Employee {

    String getName();
    int getAge();
    
    // other getter methods

    List<Long> getAccessTokens();
    Map<String, Long> getAssetsSerialIdMapping();


    class Builder extends Employee_Builder {
    }
}
```

FreeBuilder添加了方便的方法来将输入元素添加到构建器类中的集合中：

```java
Employee employee = builder.setName("tuyucheng")
    .setAge(10)
    .addAccessTokens(1221819L)
    .addAccessTokens(1223441L, 134567L)
    .build();
```

构建器类中还有一个getAccessTokens()方法，它返回一个不可修改的集合。同样，对于Map：

```java
Employee employee = builder.setName("tuyucheng")
    .setAge(10)
    .addAccessTokens(1221819L)
    .addAccessTokens(1223441L, 134567L)
    .putAssetsSerialIdMapping("Laptop", 12345L)
    .build();
```

Map的getter方法还会向客户端代码返回一个不可修改的Map。

## 9. 嵌套构建器

对于实际应用程序，我们可能不得不为我们的领域实体嵌套很多值对象。由于嵌套对象本身可能需要构建器实现，因此FreeBuilder允许嵌套可构建类型。

例如，假设我们在Employee类中有一个嵌套的复杂类型Address：

```java
@FreeBuilder
public interface Address {
 
    String getCity();

    class Builder extends Address_Builder {
    }
}
```

现在，FreeBuilder生成将Address.Builder作为输入以及Address类型的setter方法：

```java
Address.Builder addressBuilder = new Address.Builder();
addressBuilder.setCity(CITY_NAME);

Employee employee = builder.setName("tuyucheng")
    .setAddress(addressBuilder)
    .build();
```

值得注意的是，FreeBuilder还添加了一个方法来自定义Employee中现有的Address对象：

```java
Employee employee = builder.setName("tuyucheng")
    .setAddress(addressBuilder)
    .mutateAddress(a -> a.setPinCode(112200))
    .build();
```

除了FreeBuilder类型，FreeBuilder还允许嵌套其他构建器，例如[protos]()。

## 10. 构建部分对象

正如我们之前讨论过的，FreeBuilder会为任何违反约束的情况抛出IllegalStateException-例如，必填字段的缺失值。

虽然这对于生产环境来说是理想的，但它通常会使独立于约束的单元测试变得复杂。

为了放宽这些限制，FreeBuilder允许我们构建部分对象：

```java
Employee employee = builder.setName("tuyucheng")
    .setAge(10)
    .setEmail("abc@xyz.com")
    .buildPartial();

assertNotNull(employee.getEmail());
```

因此，即使我们没有为Employee设置所有必填字段，我们仍然可以验证email字段是否具有有效值。

## 11. 自定义toString()方法

对于值对象，我们通常需要添加一个自定义的toString()实现，FreeBuilder通过抽象类允许这样做：

```java
@FreeBuilder
public abstract class Employee {

    abstract String getName();

    abstract int getAge();

    @Override
    public String toString() {
        return getName() + " (" + getAge() + " years old)";
    }

    public static class Builder extends Employee_Builder{
    }
}
```

我们将Employee声明为抽象类而不是接口，并提供了一个自定义的toString()实现。

## 12. 与其他构建器库的比较

我们在本文中讨论的构建器实现与[Lombok]()、[Immutables]()或任何其他[注解处理器]()的构建器实现非常相似。但是，我们已经讨论了一些显著特征：

-   映射器方法
-   嵌套的可构建类型
-   部分对象

## 13. 总结

在本文中，我们使用FreeBuilder库在Java中生成构建器类，在注解的帮助下我们实现了构建器类的各种自定义，从而减少了其实现所需的样板代码。我们还了解了FreeBuilder与其他一些库的不同之处，并在本文中简要讨论了其中的一些特性。