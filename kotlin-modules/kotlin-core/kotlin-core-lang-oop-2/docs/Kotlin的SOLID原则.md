## 1. 简介

Robert C.Martin制定了SOLID原则，以便为企业软件的混乱带来一些秩序。多年来，它们被以各种方式解释，OOP对许多程序员的承诺变得相当糟糕，有许多笑话嘲笑Java的设计和编码的方式。那么**SOLID原则是否仍然存在且相关**，或者我们应该放弃它们以支持更实用主义？

在本教程中，我们将看到这些规则仍然适用，尽管它们的适用范围需要严格审查。

## 2. 单一职责

单一责任原则也许是争议最少的原则，没有人会争辩说他们的类或方法应该同时做十件事。很明显，**如果更改一个代码块仅更改程序行为的一个方面，那么源代码的支持就会容易得多**。

然而，通常很难定义什么是“单一方面”，考虑以下内容：

```kotlin
data class Employee(
    val id: UUID,
    val firstName: String,
    val secondName: String,
    val title: String,
    val salary: BigDecimal
) {
    fun save() {
        TODO("Save Employee to the database")
    }

    companion object {
        fun loadFromDb(id: UUID): Employee {
            TODO("Go to DB and create an Employee object")
        }
    }
}
```

此代码仅处理Employee，但是，我们的数据库操作是否属于一个域？通常如何处理非常常见的OOP实践，即在同一类中拥有对此类数据进行操作的数据和逻辑？

事实上，**行为比数据更灵活、更容易出错**，因此会更频繁地改变。因此，将行为分离到一个服务类中并让数据类哑是有意义的：

```kotlin
data class PureEmployee(
    val id: UUID,
    val firstName: String,
    val secondName: String,
    val title: String,
    val salary: BigDecimal
)

class EmployeeService(dataSource: DataSource) {
    fun upsert(employee: PureEmployee) {
        TODO("Save Employee to the database")
    }

    fun findById(id: UUID): PureEmployee {
        TODO("Go to DB and create an Employee object")
    }
}
```

同样的思维过程也适用于有副作用的函数，**一个好的规则是要么保持函数纯净**(即，根本没有副作用，引用透明)，**要么使副作用成为该函数唯一做的事情**，以便其用户敏锐地意识到后果：

```kotlin
class EmployeeService(dataSource: DataSource) {
    // ...
    fun raiseSalary(employee: PureEmployee, raise: BigDecimal): PureEmployee =
        employee.copy(salary = employee.salary + raise)
}

// Somewhere in the controller code

service.raiseSalary(employee, raise)
    .let { service.upsert(it) }
```

总的来说，设计改进的范围应该要窄得多。OOP过于关注大模块及其关系，而在这些模块内部，事情变得非常不可读。如果我们关注方法级别的概念，我们可能会获得更好的可读性。

## 3. 对扩展开放，对修改关闭

考虑到较低层次的细节，让我们看一下SOLID的第二个字母。事实上，我们可以说**所有的高阶函数都非常坚定地体现了开闭原则**，我们可以根据需要以任何方式扩展map{}函数的行为，但原始库代码将保持不变：

```kotlin
// Here we return a list of Strings
departments.map { it.toString() }

// And here - a list of Ints
departments.map { it.employeeCount }
```

在企业软件开发的背景下，类可重用性的案例很少。首先，领域类被建模以反映它们的领域，因此，它们的功能非常专业。其次，有些类足够抽象，可以在其他地方使用，但将它们提取到可分发的库中需要额外的努力。总的来说，一个普通的类代表了太多的逻辑块，无法容纳在其他地方。然而，**在方法层面上，模式经常出现**。因此，工程师的任务是发现这些模式并将它们概括为高阶函数，从而使代码更短，并且通过引入更高层次的概念，更易于理解：

```kotlin
class TraditionalEmployeeService(private val employeeRepository: EmployeeRepository) {
    fun handleRaise(id: UUID, raise: BigDecimal) =
        employeeRepository.findById(id)
            .copy(salary = salary + raise)
            .let { employeeRepository.upsert(it) }

    fun handleTitleChange(id: UUID, newTitle: String) =
        employeeRepository.findById(id)
            .copy(title = newTitle)
            .let { employeeRepository.upsert(it) }
}
```

但如果有很多这样的字段，查找和保存就会变得重复。相反，我们可能会**梳理出公共部分并自定义其余部分**：

```kotlin
class FunctionalEmployeeService(private val employeeRepository: EmployeeRepository) {
    private fun handleChange(id: UUID, change: PureEmployee.() -> PureEmployee) {
        employeeRepository.findById(id)
            .change()
            .let { employeeRepository.upsert(it) }
    }

    fun handleRaise(id: UUID, raise: BigDecimal) =
        handleChange(id) { copy(salary = salary + raise) }

    fun handleTitleChange(id: UUID, newTitle: String) =
        handleChange(id) { copy(title = newTitle) }
}
```

## 4. 里氏替换

里氏替换原则基本上是说**接口的所有后代都应该是可互换的**，这允许工程师更改程序的行为，而无需更改其转换类型的方式。如果我们有一个函数序列：

```kotlin
fun f(a: A): B = TODO()
fun g(b: B): C = TODO()
fun h(a: A): C = g(f(a))
```

然后，如果我们将B更改为它的后代B'，我们仍然会得到C作为结果类型。然而，这主要意味着类A、B和C具有一些重要的行为，正如我们在[第2节](https://www.baeldung.com/kotlin/solid-principles#single-responsibility)中讨论的那样，混合数据和行为不是我们应该轻易进行的事情。

另一方面，如果在这个序列中我们改变函数g()而不是保持它的签名不变，我们仍然保持类型转换序列。在这种情况下，g'()**函数可能被认为是同一函数接口的后代**。而且，事实上，诸如map{}之类的库函数将采用所有接收一个参数并产生一个结果的函数-这实际上是一个函数接口的定义：

```kotlin
fun interface G {
    fun invoke(b: B): C
}

fun H(a: A, f: (A) -> B, g: G) = g(f(a))
```

## 5. 接口隔离

凭借Kotlin中为函数提供的支持级别，这个特定原则非常容易遵循。事实上，**我们可以根据需要创建任意数量的独立函数**：

```kotlin
internal fun createDataSource(): DataSource = TODO("Provide a datasource")
```

此类函数也可以实现函数接口。

## 6. 依赖倒置

Kotlin非常容易地适应依赖倒置原则，与Java不同，**我们可以在一行中同时定义多个构造函数版本**：

```kotlin
class VeryComplexService(
    private val properties: Properties = Properties(),
    private val employeeRepository: EmployeeRepository = EmployeeRepository(createDataSource()),
    private val transformer: G = G { C() }
)
```

由于我们可以将属性声明和构造函数参数整理在一起，因此在构造函数中指定所有依赖项真的很容易。

更重要的是，**Kotlin默认**[禁止扩展类](https://kotlinlang.org/docs/inheritance.html#overriding-methods)，只有接口对扩展开放。这也符合依赖倒置原则，因为它使工程师定义适当的抽象接口或显式破坏原则。

## 7. 总结

**SOLID原则可能很有用，但有必要根据行业经验调整它们的解释**。函数式编程和OOP并非不兼容或相互排斥，它们是不同层次的概念。

单一职责原则就是“分而治之”，我们在从教育到心理学的各个领域都可以遇到。开闭规则让我们从大规模生产的角度思考，而不是一次性的结构。在我们从类和模块构造我们的软件之前，我们必须从函数构造它，在这里函数式编程确实非常有用。里氏替换原则和接口隔离告诉我们要使我们的软件片段小而耐用，而函数是最小的软件片段，依赖倒置谈到这些部分很容易彼此分离。

在本教程中，我们针对新的语言特性测试了SOLID原则，并确保即使我们编写Kotlin，它们仍然可以指导我们。