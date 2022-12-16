## 1. 概述

依赖倒置原则(DIP)构成了面向对象编程原则集合的一部分，这些原则通常被称为[SOLID](../../design-patterns-solid/docs/SOLID原则的可靠指南.md)。

从本质上讲，DIP是一种简单但功能强大的编程范例，我们可以使用它来**实现结构良好、高度解耦和可重用的软件组件**。

在本教程中，我们将探索实现DIP的不同方法-一种在Java 8中，另一种在Java 11中使用[JPMS]()(Java平台模块系统)。

## 2. 依赖注入和控制反转不是DIP实现

首先也是最重要的，让我们做一个基本的区分以正确理解基础知识：**DIP[既不是依赖注入(DI)也不是控制反转(IoC)]()**。即便如此，他们都能很好地协同工作。

简单来说，DI就是让软件组件通过它们的API明确声明它们的依赖关系或协作者，而不是自己去获取它们。如果没有DI，软件组件彼此紧密耦合。因此，它们很难重用、替换、Mock和测试，从而导致严格的设计。**使用DI，提供组件依赖关系和连接对象图的责任从组件转移到底层注入框架**。从这个角度来看，DI只是实现IoC的一种方式。

另一方面，**IoC是一种应用程序的流程控制被逆转的模式**。使用传统的编程方法，我们的自定义代码可以控制应用程序的流程。相反，**使用IoC，控制权将转移到外部框架或容器**。

**该框架是一个可扩展的代码库，它定义了用于插入我们自己的代码的挂钩点**。

反过来，框架通过一个或多个专门的子类、使用接口的实现以及通过注解来回调我们的代码。[Spring框架]()是最后一种方法的一个很好的例子。

## 3. DIP基础知识

为了理解DIP背后的动机，让我们从它的正式定义开始，Robert C.Martin在他的[《敏捷软件开发：原则、模式和实践》](https://www.pearson.com/us/higher-education/program/Martin-Agile-Software-Development-Principles-Patterns-and-Practices/PGM272869.html)书中给出了这个定义：

1.  高层模块不应该依赖于低层模块，两者都应该依赖于抽象。
2.  抽象不应依赖于细节，细节应该依赖于抽象。

因此，很明显，**DIP的核心是通过抽象出高层和低层组件之间的交互来反转它们之间的经典依赖关系**。

在传统的软件开发中，高层组件依赖于低层组件。因此，很难重用高级组件。

### 3.1 设计选择和DIP

让我们考虑一个简单的StringProcessor类，该类使用StringReader组件获取String值，并使用StringWriter组件将其写入其他地方：

```java
public class StringProcessor {

    private final StringReader stringReader;
    private final StringWriter stringWriter;

    public StringProcessor(StringReader stringReader, StringWriter stringWriter) {
        this.stringReader = stringReader;
        this.stringWriter = stringWriter;
    }

    public void printString() {
        stringWriter.write(stringReader.getValue());
    }
}
```

尽管StringProcessor类的实现是基本的，但我们可以在这里做出多种设计选择。

让我们将每个设计选择分解为单独的项目，以清楚地了解每个项目如何影响整体设计：

1.  **低级组件StringReader和StringWriter是放在同一个包中的具体类**，高级组件StringProcessor放在不同的包中。StringProcessor依赖于StringReader和StringWriter，没有依赖关系的反转，因此StringProcessor不能在不同的上下文中重用。
2.  **StringReader和StringWriter是与实现一起放在同一个包中的接口**。StringProcessor现在依赖于抽象，但低级组件则不依赖于抽象。我们还没有实现依赖关系的倒置。
3.  **StringReader和StringWriter是与StringProcessor放在同一个包中的接口**。现在，StringProcessor拥有抽象的明确所有权。StringProcessor、StringReader和StringWriter都依赖于抽象，我们通过抽象组件之间的交互实现了自上而下的依赖倒置，StringProcessor现在可以在不同的上下文中重用。
4.  **StringReader和StringWriter是放置在与StringProcessor分开的包中的接口**。我们实现了依赖倒置，也更容易替换StringReader和StringWriter实现，StringProcessor也可以在不同的上下文中重用。

在上述所有方案中，只有第3项和第4项是DIP的有效实现。

### 3.2 定义抽象的所有权

第3项是直接DIP实现，其中**高级组件和抽象被放置在同一个包中**。因此，**高级组件拥有抽象**。在这个实现中，高级组件负责定义抽象协议，通过该协议与低级组件进行交互。

同样，第4项是更加解耦的DIP实现。在这个模式的变体中，**高层组件和低层组件都没有抽象的所有权**。

抽象被放置在一个单独的层中，这有助于切换低级组件。同时，所有组件彼此隔离，从而产生更强的封装。

### 3.3 选择正确的抽象级别

在大多数情况下，选择高级组件将使用的抽象应该相当简单，但有一个注意事项值得注意：抽象级别。

在上面的示例中，我们使用DI将StringReader类型注入到StringProcessor类中，**只要StringReader的抽象级别接近StringProcessor的域，这就会有效**。

相比之下，如果StringReader是一个从文件中读取字符串值的[File]()对象，我们就会失去DIP的内在优势。在那种情况下，StringReader的抽象级别将远低于StringProcessor的域级别。

简而言之，**高层组件用来与低层组件交互操作的抽象级别应该始终接近前者的域**。

## 4. Java 8实现

我们已经深入研究了DIP的关键概念，所以现在我们将探索Java 8中该模式的一些实际实现。

### 4.1 直接DIP实现

让我们创建一个Demo应用程序，该应用程序从持久层获取一些Customer并以某种额外的方式处理它们。

该层的底层存储通常是一个数据库，但为了保持代码简单，这里我们将使用一个普通的Map。

让我们从**定义高级组件**开始：

```java
public class CustomerService {

    private final CustomerDao customerDao;

    // standard constructor / getter

    public Optional<Customer> findById(int id) {
        return customerDao.findById(id);
    }

    public List<Customer> findAll() {
        return customerDao.findAll();
    }
}
```

正如我们所见，CustomerService类实现了findById()和findAll()方法，它们使用简单的[DAO]()实现从持久层获取Customer。当然，我们可以在类中封装更多的功能，但为了简单起见，让我们保持这样。

在这种情况下，CustomerDao类型是CustomerService用于使用低级组件的抽象。

由于这是一个直接的DIP实现，让我们在CustomerService的同一个包中将抽象定义为一个接口：

```java
public interface CustomerDao {

    Optional<Customer> findById(int id);

    List<Customer> findAll();
}
```

通过将抽象放在高级组件的同一个包中，我们让组件负责拥有抽象。这个实现细节**真正反转了高层组件和低层组件之间的依赖关系**。

此外，**CustomerDao的抽象级别接近于CustomerService的抽象级别**，这也是良好的DIP实现所必需的。

现在，让我们在不同的包中创建低级组件。在这种情况下，它只是一个基本的CustomerDao实现：

```java
public class SimpleCustomerDao implements CustomerDao {

    // standard constructor / getter

    @Override
    public Optional<Customer> findById(int id) {
        return Optional.ofNullable(customers.get(id));
    }

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }
}
```

最后，让我们创建一个单元测试来检查CustomerService类的功能：

```java
@BeforeEach
void setUpCustomerServiceInstance() {
    var customers = new HashMap<Integer, Customer>();
    customers.put(1, new Customer("John"));
    customers.put(2, new Customer("Susan"));
    customerService = new CustomerService(new SimpleCustomerDao(customers));
}

@Test
void givenCustomerServiceInstance_whenCalledFindById_thenCorrect() {
    assertThat(customerService.findById(1)).isInstanceOf(Optional.class);
}

@Test
void givenCustomerServiceInstance_whenCalledFindAll_thenCorrect() {
    assertThat(customerService.findAll()).isInstanceOf(List.class);
}

@Test
void givenCustomerServiceInstance_whenCalledFindByIdWithNullCustomer_thenCorrect() {
    var customers = new HashMap<>();
    customers.put(1, null);
    customerService = new CustomerService(new SimpleCustomerDao(customers));
    Customer customer = customerService.findById(1).orElseGet(() -> new Customer("Non-existing customer"));
    assertThat(customer.getName()).isEqualTo("Non-existing customer");
}
```

单元测试使用CustomerService API，并且它还演示了如何手动将抽象注入到高级组件中。在大多数情况下，我们会使用某种DI容器或框架来完成此操作。

此外，下图从高级到低级包的角度显示了我们的Demo应用程序的结构：

<img src="../assets/img.png">

### 4.2 替代DIP实现

正如我们之前所讨论的，可以使用替代的DIP实现，其中我们将高级组件、抽象和低级组件放在不同的包中。

出于显而易见的原因，这种变体更灵活，可以更好地封装组件，并且更容易替换低级组件。

当然，实现该模式的这种变体归结为将CustomerService、MapCustomerDao和CustomerDao放在单独的包中。

因此，下图足以显示每个组件如何使用此实现进行布局：

<img src="../assets/img_1.png">

## 5. Java 11模块化实现

将我们的Demo应用程序重构为模块化应用程序相当容易。

这是演示JPMS如何实施最佳编程实践的一种非常好的方式，包括通过DIP进行的强封装、抽象和组件重用。

我们不需要从头开始重新实现示例组件。因此，**模块化我们的示例应用程序只是将每个组件文件连同相应的模块描述符放在一个单独的模块中**。

以下是模块化项目结构的外观：

```shell
project base directory (could be anything, like dipmodular)
|- cn.tuyucheng.taketoday.dip.services
   module-info.java
       |- cn
           |- tuyucheng
               |- takeToday
                   |- dip
                       |- services
                           |- CustomerService.java
|- cn.tuyucheng.taketoday.dip.daos
   module-info.java
       |- cn
           |- tuyucheng
               |- takeToday
                   |- dip
                       |- daos
                           |- CustomerDao.java
|- cn.tuyucheng.taketoday.dip.daoimplementations 
   module-info.java 
       |- cn
           |- tuyucheng
               |- takeToday
                   |- dip
                       |- daoimplementations
                           |- SimpleCustomerDao.java 
|- cn.tuyucheng.taketoday.dip.entities
   module-info.java
       |- cn
           |- tuyucheng
               |- takeToday
                   |- dip
                       |- entities
                           |- Customer.java
|- cn.tuyucheng.taketoday.dip.mainapp 
   module-info.java 
       |- cn
           |- tuyucheng
               |- takeToday
                   |- dip
                       |- mainapp
                           |- MainApplication.java
```

### 5.1 高级组件模块

首先我们将CustomerService类放在它自己的模块中。

我们将在根目录cn.tuyucheng.taketoday.dip.services中创建此模块，并添加模块描述符module-info.java：

```java
module cn.tuyucheng.taketoday.dip.services {
    requires cn.tuyucheng.taketoday.dip.entities;
    requires cn.tuyucheng.taketoday.dip.daos;
    uses cn.tuyucheng.taketoday.dip.daos.CustomerDao;
    exports cn.tuyucheng.taketoday.dip.services;
}
```

出于显而易见的原因，我们不会详细介绍JPMS的工作原理。即便如此，仅通过查看requires指令就可以清楚地看到模块依赖关系。

这里值得注意的最相关的细节是uses指令。**它声明该模块是一个客户端模块**，它使用CustomerDao接口的实现。

当然，我们仍然需要在这个模块中放置高级组件，即CustomerService类。因此，在根目录cn.tuyucheng.taketoday.dip.services中，让我们创建以下类似包的目录结构：cn/tuyucheng/taketoday/dip/services。

最后，我们将CustomerService.java文件放在该目录中。

### 5.2 抽象模块

同样，我们需要将CustomerDao接口放在它自己的模块中。因此，让我们在根目录cn.tuyucheng.taketoday.dip.daos中创建模块，并添加模块描述符：

```java
module cn.tuyucheng.taketoday.dip.daos {
    requires cn.tuyucheng.taketoday.dip.entities;
    exports cn.tuyucheng.taketoday.dip.daos;
}
```

现在，我们进入到cn.tuyucheng.taketoday.dip.daos目录并创建以下目录结构：cn/tuyucheng/taketoday/dip/daos，让我们将CustomerDao.java文件放在该目录中。

### 5.3 底层组件模块

从逻辑上讲，我们也需要将低级组件SimpleCustomerDao放在一个单独的模块中。正如预期的那样，该过程看起来与我们刚刚对其他模块所做的非常相似。

让我们在根目录cn.tuyucheng.taketoday.dip.daoimplementations中创建新模块，并包含模块描述符：

```java
module cn.tuyucheng.taketoday.dip.daoimplementations {
    requires cn.tuyucheng.taketoday.dip.entities;
    requires cn.tuyucheng.taketoday.dip.daos;
    provides cn.tuyucheng.taketoday.dip.daos.CustomerDao with cn.tuyucheng.taketoday.dip.daoimplementations.SimpleCustomerDao;
    exports cn.tuyucheng.taketoday.dip.daoimplementations;
}
```

在JPMS上下文中，**这是一个服务提供者模块**，因为它声明了provides和with指令。

在这种情况下，该模块通过SimpleCustomerDao实现使CustomerDao服务可供一个或多个消费者模块使用。

请记住，我们的消费者模块cn.tuyucheng.taketoday.dip.services通过uses指令使用此服务。

**这清楚地表明使用JPMS直接实现DIP是多么简单，只需在不同的模块中定义消费者、服务提供者和抽象**。

同样，我们需要将SimpleCustomerDao.java文件放在这个新模块中。让我们进入到cn.tuyucheng.taketoday.dip.daoimplementations目录，并使用以下名称创建一个类似包的新目录结构：cn/tuyucheng/taketoday/dip/daoimplementations。最后，我们将SimpleCustomerDao.java文件放在该目录中。

### 5.4 实体模块

此外，我们必须创建另一个模块，我们可以在其中放置Customer.java类。和之前一样，让我们创建根目录cn.tuyucheng.taketoday.dip.entities并包含模块描述符：

```java
module cn.tuyucheng.taketoday.dip.entities {
    exports cn.tuyucheng.taketoday.dip.entities;
}
```

在包的根目录中，让我们创建目录cn/tuyucheng/taketoday/dip/entities并添加以下Customer.java文件：

```java
public class Customer {

    private final String name;

    // standard constructor / getter / toString
}
```

### 5.5 主应用模块

接下来，我们需要创建一个附加模块，允许我们定义Demo应用程序的入口点。因此，让我们创建另一个根目录cn.tuyucheng.taketoday.dip.mainapp并将模块描述符放入其中：

```java
module cn.tuyucheng.taketoday.dip.mainapp {
    requires cn.tuyucheng.taketoday.dip.entities;
    requires cn.tuyucheng.taketoday.dip.daos;
    requires cn.tuyucheng.taketoday.dip.daoimplementations;
    requires cn.tuyucheng.taketoday.dip.services;
    exports cn.tuyucheng.taketoday.dip.mainapp;
}
```

现在，我们进入到模块的根目录，并创建以下目录结构：cn/tuyucheng/taketoday/dip/mainapp。在该目录中，让我们添加一个MainApplication.java文件，它只实现了一个main()方法：

```java
public class MainApplication {

    public static void main(String[] args) {
        var customers = new HashMap<Integer, Customer>();
        customers.put(1, new Customer("John"));
        customers.put(2, new Customer("Susan"));
        CustomerService customerService = new CustomerService(new SimpleCustomerDao(customers));
        customerService.findAll().forEach(System.out::println);
    }
}
```

最后，从我们的IDE或命令控制台编译并运行Demo应用程序。

正如预期的那样，当应用程序启动时，我们应该会看到打印到控制台的Customer对象集合：

```shell
Customer{name=John}
Customer{name=Susan}
```

此外，下图显示了应用程序各个模块的依赖关系：

<img src="../assets/img_2.png">

## 6. 总结

在本教程中，我们深入探讨了DIP的关键概念，并演示了该模式在Java 8和Java 11中的不同实现，后者使用JPMS。