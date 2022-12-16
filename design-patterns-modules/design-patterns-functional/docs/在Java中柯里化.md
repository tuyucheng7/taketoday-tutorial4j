## 1. 简介

从Java 8开始，我们可以在Java中定义单参数和双参数函数，允许我们将它们的行为作为参数传入到其他函数中。但是对于具有更多参数的函数，我们依赖于[Vavr]()这样的外部库。

另一种选择是使用[柯里化](https://en.wikipedia.org/wiki/Currying)，通过结合currying和[函数式接口]()，我们甚至可以定义易于阅读的构建器，强制用户提供所有输入。

**在本教程中，我们将定义柯里化并介绍它的用法**。

## 2. 简单例子

让我们考虑一个具有多个参数的Letter对象的具体示例。

我们简化的第一个版本只需要一个body和一个salutation：

```java
class Letter {
    private String salutation;
    private String body;

    Letter(String salutation, String body) {
        this.salutation = salutation;
        this.body = body;
    }
}
```

### 2.1 按方法创建

可以使用以下方法轻松创建这样的对象：

```java
Letter createLetter(String salutation, String body){
    return new Letter(salutation, body);
}
```

### 2.2 使用BiFunction创建

上面的方法没有任何问题，但我们可能需要将这种行为提供给以函数式风格编写的东西。从Java 8开始，我们可以使用BiFunction来达到这个目的：

```java
BiFunction<String, String, Letter> SIMPLE_LETTER_CREATOR = (salutation, body) -> new Letter(salutation, body);
```

### 2.3 使用函数序列创建

我们还可以将其重述为一系列函数，每个函数都有一个参数：

```java
Function<String, Function<String, Letter>> SIMPLE_CURRIED_LETTER_CREATOR = salutation -> body -> new Letter(salutation, body);
```

我们看到salutation映射到一个函数，生成的函数映射到新的Letter对象。观察返回类型如何从BiFunction更改，我们只使用Function类，**这种对函数序列的转换称为柯里化**。

## 3. 高级示例

为了展示柯里化的优势，让我们用更多参数扩展我们的Letter类构造函数：

```java
class Letter {
    private String returningAddress;
    private String insideAddress;
    private LocalDate dateOfLetter;
    private String salutation;
    private String body;
    private String closing;

    Letter(String returningAddress, String insideAddress, LocalDate dateOfLetter, 
           String salutation, String body, String closing) {
        this.returningAddress = returningAddress;
        this.insideAddress = insideAddress;
        this.dateOfLetter = dateOfLetter;
        this.salutation = salutation;
        this.body = body;
        this.closing = closing;
    }
}
```

### 3.1 按方法创建

像以前一样，我们可以使用方法创建对象：

```java
Letter createLetter(String returnAddress, String insideAddress, LocalDate dateOfLetter,
        String salutation, String body, String closing) {
    return new Letter(returnAddress, insideAddress, dateOfLetter, salutation, body, closing);
}
```

### 3.2 任意数量的Function

Arity(元)是衡量函数接收的参数数量的指标，Java为[nullary-零元]()(Supplier)、[unary-一元]()(Function)和[binary-二元]()(BiFunction)提供了现有的函数式接口，但仅此而已。如果不定义新的函数式接口，我们就无法提供具有六个输入参数的函数。

柯里化是我们的一种方式，**它将任意元数转换为一元函数序列**，因此对于我们的例子，我们得到：

```java
static Function<String, Function<String, Function<LocalDate, Function<String, Function<String, Function<String, Letter>>>>>> LETTER_CREATOR = 
	returnAddress
		-> closing
		-> dateOfLetter
		-> insideAddress
		-> salutation
		-> body
		-> new Letter(returnAddress, insideAddress, dateOfLetter, salutation, body, closing);
```

### 3.3 详细类型

显然，上面的类型不太可读。使用此函数链，我们需要“apply”六次来创建一个Letter对象：

```java
LETTER_CREATOR
    .apply(RETURNING_ADDRESS)
    .apply(CLOSING)
    .apply(DATE_OF_LETTER)
    .apply(INSIDE_ADDRESS)
    .apply(SALUTATION)
    .apply(BODY);
```

### 3.4 预填充值

通过这个函数链，我们可以创建一个工具方法，它预先填写第一个值并返回用于继续完成Letter对象的函数：

```java
Function<String, Function<LocalDate, Function<String, Function<String, Function<String, Letter>>>>> 
  LETTER_CREATOR_PREFILLED = returningAddress -> LETTER_CREATOR.apply(returningAddress).apply(CLOSING);
```

**请注意，为了使其有用，我们必须仔细选择原始函数中参数的顺序，以便不太具体的参数是第一个**。

## 4. 构建器模式

为了克服不友好的类型定义和标准apply方法的重复使用，这意味着你不知道输入的正确顺序，我们可以使用[构建器模式]()：

```java
static AddReturnAddress builder() {
	return returnAddress
		-> closing
		-> dateOfLetter
		-> insideAddress
		-> salutation
		-> body
		-> new Letter(returnAddress, insideAddress, dateOfLetter, salutation, body, closing);
}
```

**我们使用一系列函数式接口，而不是一系列Function**。请注意，上述定义的返回类型是AddReturnAddress，在下文中，我们只需要定义中间接口：

```java
interface AddReturnAddress {
    Letter.AddClosing withReturnAddress(String returnAddress);
}

interface AddClosing {
    Letter.AddDateOfLetter withClosing(String closing);
}

interface AddDateOfLetter {
    Letter.AddInsideAddress withDateOfLetter(LocalDate dateOfLetter);
}

interface AddInsideAddress {
    Letter.AddSalutation withInsideAddress(String insideAddress);
}

interface AddSalutation {
    Letter.AddBody withSalutation(String salutation);
}

interface AddBody {
    Letter withBody(String body);
}
```

因此，使用它来创建一个Letter对象的方法是不言自明的：


```java
Letter.builder()
	.withReturnAddress(RETURNING_ADDRESS)
	.withClosing(CLOSING)
	.withDateOfLetter(DATE_OF_LETTER)
	.withInsideAddress(INSIDE_ADDRESS)
	.withSalutation(SALUTATION)
	.withBody(BODY);
```

像以前一样，我们可以预填充Letter对象：

```java
AddDateOfLetter prefilledLetter = Letter.builder()
		.withReturnAddress(RETURNING_ADDRESS)
		.withClosing(CLOSING);
```

请注意，**接口确保填充顺序**。所以，我们不能只预填closing。

## 5. 总结

在本文中我们了解了如何应用柯里化，因此我们不受标准Java函数式接口支持的有限参数数量的限制。此外，我们可以很容易地预填前几个参数。此外，我们还学习了如何使用它来创建可读的构建器。