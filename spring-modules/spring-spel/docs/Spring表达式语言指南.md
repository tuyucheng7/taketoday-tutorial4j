## 1. 概述

Spring表达式语言(SpEL)是一种功能强大的表达式语言，支持在运行时查询和操作对象图。我们可以将它与XML或基于注解的Spring配置一起使用。

该语言有几个可用的运算符：

| 类型 | 运算符                                          |
| ----- |----------------------------------------------|
| 算术  | +, -, *, /, %, ^, div, mod                   |
| 关系型 | <, >, ==, !=, <=, >=, lt, gt, eq, ne, le, ge |
| 逻辑 | 和，或，不是，&&，                                   ||，！                   |
| 条件| ?:                                           |
| 正则表达式 | matches                                      |

## 2. 运算符

对于这些示例，我们将使用基于注解的配置。有关XML配置的更多详细信息，请参阅本文后面的部分。

SpEL表达式以#符号开头并用大括号括起来：#{expression}。

可以以类似的方式引用属性，以$符号开头并用大括号括起来：${property.name}。

属性占位符不能包含SpEL表达式，但表达式可以包含属性引用：

```java
#{${someProperty} + 2}
```

在上面的示例中，假设someProperty的值为2，因此生成的表达式将为2 + 2，其计算结果为4。

### 2.1 算术运算符

SpEL支持所有基本算术运算符：

```java
@Value("#{19 + 1}") // 20
private double add; 

@Value("#{'String1 ' + 'string2'}") // "String1 string2"
private String addString; 

@Value("#{20 - 1}") // 19
private double subtract;

@Value("#{10 * 2}") // 20
private double multiply;

@Value("#{36 / 2}") // 19
private double divide;

@Value("#{36 div 2}") // 18, the same as for / operator
private double divideAlphabetic; 

@Value("#{37 % 10}") // 7
private double modulo;

@Value("#{37 mod 10}") // 7, the same as for % operator
private double moduloAlphabetic; 

@Value("#{2 ^ 9}") // 512
private double powerOf;

@Value("#{(2 + 2) * 2 + 9}") // 17
private double brackets;
```

除法和模运算具有字母别名，div代表/和mod代表%。+运算符也可用于拼接字符串。

### 2.2 关系和逻辑运算符

SpEL还支持所有基本的关系和逻辑操作：

```java
@Value("#{1 == 1}") // true
private boolean equal;

@Value("#{1 eq 1}") // true
private boolean equalAlphabetic;

@Value("#{1 != 1}") // false
private boolean notEqual;

@Value("#{1 ne 1}") // false
private boolean notEqualAlphabetic;

@Value("#{1 < 1}") // false
private boolean lessThan;

@Value("#{1 lt 1}") // false
private boolean lessThanAlphabetic;

@Value("#{1 <= 1}") // true
private boolean lessThanOrEqual;

@Value("#{1 le 1}") // true
private boolean lessThanOrEqualAlphabetic;

@Value("#{1 > 1}") // false
private boolean greaterThan;

@Value("#{1 gt 1}") // false
private boolean greaterThanAlphabetic;

@Value("#{1 >= 1}") // true
private boolean greaterThanOrEqual;

@Value("#{1 ge 1}") // true
private boolean greaterThanOrEqualAlphabetic;
```

所有关系运算符也有字母别名。例如，在基于XML的配置中，我们不能使用包含尖括号(<、<=、>、>=)的运算符。相反，我们可以使用lt(小于)、le(小于或等于)、gt(大于)或ge(大于或等于)。

### 2.3 逻辑运算符

SpEL还支持所有基本的逻辑操作：

```java
@Value("#{250 > 200 && 200 < 4000}") // true
private boolean and; 

@Value("#{250 > 200 and 200 < 4000}") // true
private boolean andAlphabetic;

@Value("#{400 > 300 || 150 < 100}") // true
private boolean or;

@Value("#{400 > 300 or 150 < 100}") // true
private boolean orAlphabetic;

@Value("#{!true}") // false
private boolean not;

@Value("#{not true}") // false
private boolean notAlphabetic;
```

与算术和关系运算符一样，所有逻辑运算符也有字母别名。

### 2.4 条件运算符

我们使用条件运算符根据某些条件注入不同的值：

```java
@Value("#{2 > 1 ? 'a' : 'b'}") // "a"
private String ternary;
```

我们使用三元运算符在表达式中执行紧凑的if-then-else条件逻辑。在这个例子中，我们试图检查是否存在。

三元运算符的另一个常见用途是检查某个变量是否为空，然后返回变量值或默认值：

```java
@Value("#{someBean.someProperty != null ? someBean.someProperty : 'default'}")
private String ternary;
```

Elvis运算符是一种缩短Groovy语言中上述情况的三元运算符语法的方法。它也可以在SpEL中使用。

这段代码等价于上面的代码：

```java
@Value("#{someBean.someProperty ?: 'default'}") // Will inject provided string if someProperty is null
private String elvis;
```

### 2.5 在SpEL中使用正则表达式

我们可以使用matches运算符来检查字符串是否与给定的正则表达式匹配：

```java
@Value("#{'100' matches '\\d+' }") // true
private boolean validNumericStringResult;

@Value("#{'100fghdjf' matches '\\d+' }") // false
private boolean invalidNumericStringResult;

@Value("#{'valid alphabetic string' matches '[a-zA-Z\\s]+' }") // true
private boolean validAlphabeticStringResult;

@Value("#{'invalid alphabetic string #$1' matches '[a-zA-Z\\s]+' }") // false
private boolean invalidAlphabeticStringResult;

@Value("#{someBean.someValue matches '\d+'}") // true if someValue contains only digits
private boolean validNumericValue;
```

### 2.6 访问List和Map对象

借助SpEL，我们可以访问上下文中任何Map或List的内容。

我们将创建新的bean workersHolder，它将一些工人及其薪水的信息存储在List和Map中：

```java
@Component("workersHolder")
public class WorkersHolder {
    private List<String> workers = new LinkedList<>();
    private Map<String, Integer> salaryByWorkers = new HashMap<>();

    public WorkersHolder() {
        workers.add("John");
        workers.add("Susie");
        workers.add("Alex");
        workers.add("George");

        salaryByWorkers.put("John", 35000);
        salaryByWorkers.put("Susie", 47000);
        salaryByWorkers.put("Alex", 12000);
        salaryByWorkers.put("George", 14000);
    }

    // Getters and setters
}
```

现在我们可以使用SpEL访问集合的值：

```java
@Value("#{workersHolder.salaryByWorkers['John']}") // 35000
private Integer johnSalary;

@Value("#{workersHolder.salaryByWorkers['George']}") // 14000
private Integer georgeSalary;

@Value("#{workersHolder.salaryByWorkers['Susie']}") // 47000
private Integer susieSalary;

@Value("#{workersHolder.workers[0]}") // John
private String firstWorker;

@Value("#{workersHolder.workers[3]}") // George
private String lastWorker;

@Value("#{workersHolder.workers.size()}") // 4
private Integer numberOfWorkers;
```

## 3. 在Spring配置中使用

### 3.1 引用Bean

在此示例中，我们将了解如何在基于XML的配置中使用SpEL。我们可以使用表达式来引用bean或bean字段/方法。

例如，假设我们有以下类：

```java
public class Engine {
    private int capacity;
    private int horsePower;
    private int numberOfCylinders;
    // Getters and setters
}

public class Car {
    private String make;
    private int model;
    private Engine engine;
    private int horsePower;
    // Getters and setters
}
```

现在我们创建一个应用程序上下文，其中表达式用于注入值：

```xml
<bean id="engine" class="cn.tuyucheng.taketoday.spring.spel.Engine">
   <property name="capacity" value="3200"/>
   <property name="horsePower" value="250"/>
   <property name="numberOfCylinders" value="6"/>
</bean>
<bean id="someCar" class="cn.tuyucheng.taketoday.spring.spel.Car">
   <property name="make" value="Some make"/>
   <property name="model" value="Some model"/>
   <property name="engine" value="#{engine}"/>
   <property name="horsePower" value="#{engine.horsePower}"/>
</bean>
```

看看someCar bean。someCar的engine和horsePower字段使用的表达式分别是对engine bean和horsePower字段的bean引用。

要对基于注解的配置执行相同操作，请使用@Value("#{expression}")注解。

### 3.2 在配置中使用运算符

本文第一部分中的每个运算符都可以在XML和基于注解的配置中使用。

但是，请记住，在基于XML的配置中，我们不能使用尖括号运算符“<”。相反，我们应该使用字母别名，例如lt(小于)或le(小于或等于)。

对于基于注解的配置，没有这样的限制：

```java
public class SpelOperators {
    private boolean equal;
    private boolean notEqual;
    private boolean greaterThanOrEqual;
    private boolean and;
    private boolean or;
    private String addString;

    // Getters and setters
    
    @Override
    public String toString() {
        // toString which include all fields
    }
}
```

现在我们将向应用程序上下文添加一个spelOperators bean：

```xml
<bean id="spelOperators" class="cn.tuyucheng.taketoday.spring.spel.SpelOperators">
    <property name="equal" value="#{1 == 1}"/>
    <property name="notEqual" value="#{1 lt 1}"/>
    <property name="greaterThanOrEqual" value="#{someCar.engine.numberOfCylinders >= 6}"/>
    <property name="and" value="#{someCar.horsePower == 250 and someCar.engine.capacity lt 4000}"/>
    <property name="or" value="#{someCar.horsePower > 300 or someCar.engine.capacity > 3000}"/>
    <property name="addString" value="#{someCar.model + ' manufactured by ' + someCar.make}"/>
</bean>
```

从上下文中检索该bean，然后我们可以验证是否正确注入了值：

```java
ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
SpelOperators spelOperators = (SpelOperators) context.getBean("spelOperators");
```

在这里我们可以看到spelOperators bean的toString方法的输出：

```shell
[equal=true, notEqual=false, greaterThanOrEqual=true, and=true, 
or=true, addString=Some model manufactured by Some make]
```

## 4. 以编程方式解析表达式

有时，我们可能希望在配置上下文之外解析表达式。幸运的是，这可以使用SpelExpressionParser实现。

我们可以使用我们在前面的示例中看到的所有运算符，但应该在没有大括号和哈希符号的情况下使用它们。也就是说，如果我们想在Spring配置中使用带有+运算符的表达式，则语法为#{1 + 1}；在配置之外使用时，语法很简单(1 + 1)。

在下面的示例中，我们将使用上一节中定义的Car和Engine bean。

### 4.1 使用ExpressionParser

让我们看一个简单的例子：

```java
ExpressionParser expressionParser = new SpelExpressionParser();
Expression expression = expressionParser.parseExpression("'Any string'");
String result = (String) expression.getValue();
```

ExpressionParser负责解析表达式字符串。在此示例中，SpEL解析器将简单地将字符串“Any String”作为表达式进行评估。毫不奇怪，结果将是'Any String'。

与在配置中使用SpEL一样，我们可以使用它来调用方法、访问属性或调用构造函数：

```java
Expression expression = expressionParser.parseExpression("'Any string'.length()");
Integer result = (Integer) expression.getValue();
```

此外，我们可以调用构造函数，而不是直接对文本进行操作：

```java
Expression expression = expressionParser.parseExpression("new String('Any string').length()");
```

我们还可以以相同的方式访问String类的bytes属性，从而得到字符串的byte[]表示：

```java
Expression expression = expressionParser.parseExpression("'Any string'.bytes");
byte[] result = (byte[]) expression.getValue();
```

我们可以链接方法调用，就像在普通Java代码中一样：

```java
Expression expression = expressionParser.parseExpression("'Any string'.replace(\" \", \"\").length()");
Integer result = (Integer) expression.getValue();
```

在本例中，结果将为9，因为我们已将空格替换为空字符串。

如果我们不想强制转换表达式结果，我们可以使用泛型方法T getValue(Class<T\> desiredResultType)，我们可以在其中提供我们想要返回的所需类类型。

请注意，如果返回值无法转换为desiredResultType，则将抛出EvaluationException：

```java
Integer result = expression.getValue(Integer.class);
```

最常见的用法是提供一个针对特定对象实例进行评估的表达式字符串：

```java
Car car = new Car();
car.setMake("Good manufacturer");
car.setModel("Model 3");
car.setYearOfProduction(2014);

ExpressionParser expressionParser = new SpelExpressionParser();
Expression expression = expressionParser.parseExpression("model");

EvaluationContext context = new StandardEvaluationContext(car);
String result = (String) expression.getValue(context);
```

在这种情况下，结果将等于car对象“Model 3”的model字段的值。StandardEvaluationContext类指定表达式将针对哪个对象进行评估。

创建上下文对象后不能更改它。StandardEvaluationContext的构建成本很高，并且在重复使用期间，它会建立缓存状态，使后续表达式评估能够更快地执行。由于缓存的原因，如果根对象没有更改，最好在可能的情况下重用StandardEvaluationContext。

但是，如果根对象被反复更改，我们可以使用下面示例中所示的机制：

```java
Expression expression = expressionParser.parseExpression("model");
String result = (String) expression.getValue(car);
```

在这里，我们使用一个参数调用getValue方法，该参数表示我们要对其应用SpEL表达式的对象。

我们也可以像以前一样使用泛型的getValue方法：

```java
Expression expression = expressionParser.parseExpression("yearOfProduction > 2005");
boolean result = expression.getValue(car, Boolean.class);
```

### 4.2 使用ExpressionParser设置值

在解析表达式返回的表达式对象上使用setValue方法，我们可以在对象上设置值。SpEL将负责类型转换。默认情况下，SpEL使用org.springframework.core.convert.ConversionService。我们可以在类型之间创建自己的自定义转换器。ConversionService是泛型感知的，因此我们可以将它与泛型一起使用。

让我们来看看我们在实践中是如何做到的：

```java
Car car = new Car();
car.setMake("Good manufacturer");
car.setModel("Model 3");
car.setYearOfProduction(2014);

CarPark carPark = new CarPark();
carPark.getCars().add(car);

StandardEvaluationContext context = new StandardEvaluationContext(carPark);

ExpressionParser expressionParser = new SpelExpressionParser();
expressionParser.parseExpression("cars[0].model").setValue(context, "Other model");
```

生成的car对象将具有模型“Other model”，它是从“Model 3”更改而来的。

### 4.3 解析器配置

在下面的例子中，我们将使用这个类：

```java
public class CarPark {
    private List<Car> cars = new ArrayList<>();

    // Getter and setter
}
```

可以通过使用SpelParserConfiguration对象调用构造函数来配置ExpressionParser。

例如，如果我们尝试在不配置解析器的情况下将car对象添加到CarPark类的cars数组中，我们将得到如下错误：

```shell
EL1025E:(pos 4): The collection has '0' elements, index '0' is invalid
```

我们可以改变解析器的行为，允许它在指定索引为null时自动创建元素(autoGrowNullReferences，构造函数的第一个参数)，或者自动增长数组或列表以容纳超出其初始大小的元素(autoGrowCollections，第二个参数):

```java
SpelParserConfiguration config = new SpelParserConfiguration(true, true);
StandardEvaluationContext context = new StandardEvaluationContext(carPark);

ExpressionParser expressionParser = new SpelExpressionParser(config);
expressionParser.parseExpression("cars[0]").setValue(context, car);

Car result = carPark.getCars().get(0);
```

生成的car对象将等于前面示例中设置为carPark对象的cars数组的第一个元素的car对象。

## 5. 总结

SpEL是一种功能强大、得到良好支持的表达式语言，我们可以在Spring产品组合中的所有产品中使用它。我们可以使用它来配置Spring应用程序或编写解析器以在任何应用程序中执行更通用的任务。