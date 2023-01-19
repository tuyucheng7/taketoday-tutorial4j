## 一、概述

在本教程中，我们将了解[Model View Controller](https://www.baeldung.com/spring-mvc)和 Model View Presenter 模式。我们还将讨论它们之间的区别。

## 2. 设计模式与架构模式

### 2.1. 架构模式

架构模式是针对软件架构中常见问题的通用的、可重用的解决方案。这些对代码库有广泛的影响。

例如，这些会水平或垂直影响软件。水平地，我们指的是如何在一个层内构建代码。相反，垂直意味着请求如何从外层到内层再返回。

一些更常见的架构模式是MVC、MVP和MVVM。

### 2.2. 设计模式

[设计模式](https://www.baeldung.com/creational-design-patterns)通常与代码级共性相关联。它们提供了各种方案来改进和构建更小的子系统。

此外，设计模式是中等规模的策略，可以充实实体的某些结构和行为及其关系。一些常用的设计模式是[singleton](https://www.baeldung.com/java-singleton)、[factory](https://www.baeldung.com/creational-design-patterns#factory-method)和[builder](https://www.baeldung.com/java-builder-pattern-freebuilder)模式。

设计模式在范围上不同于架构模式。它们更本地化，对代码库的影响更小。相反，它们只影响代码库的特定部分。在下一节中，我们将讨论为什么要使用这些模式。

## 3. 为什么使用 MVC 和 MVP 模式

使用这些模式背后的主要思想是分离业务层和 UI 层之间的关注 点。这些模式为我们提供了易于测试等特性。它们还隐藏数据访问。

我们可以说，通过隔离主要组件，它们更能适应变化。然而，最大的缺点是增加了复杂性和学习曲线。

## 4.MVC模式

在 MVC 模式中，功能基于三个独立的关注点分为三个组件。首先，视图负责渲染 UI 元素。其次，控制器响应 UI 动作。该模型处理业务行为和状态管理。

在大多数实现中，所有三个组件都可以直接相互交互。然而，在某些实现中，控制器负责确定显示哪个视图。

下图显示了 MVC 的控制流程：

[![MVC模式](https://www.baeldung.com/wp-content/uploads/2021/08/MVC_Pattern-273x300-1.png)](https://www.baeldung.com/wp-content/uploads/2021/08/MVC_Pattern-273x300-1.png)

该模型代表整个业务逻辑层。视图表示从模型中获取的数据。此外，它还处理表示逻辑。最后，控制器处理控制流逻辑并更新模型。

MVC没有指定视图和模型的内部结构。通常，视图层在单个类中实现。

但是，在这种情况下，可能会出现一些问题：

-   视图和模型是紧密耦合的。结果，视图的功能需求很容易下降到模型并污染业务逻辑层
-   视图是整体的，通常与 UI 框架紧密耦合。因此，对视图进行单元测试变得困难

## 5. MVP模式

MVP 模式是一种基于 MVC 模式概念的 UI 呈现模式。但是，它没有指定如何构建整个系统。它只规定如何构造视图。

这种模式通常将四个组件的职责分开。首先，视图负责呈现 UI 元素。其次，视图界面用于从其视图松散地耦合演示者。

最后，Presenter 与视图和模型进行交互，模型负责业务行为和状态管理。

在某些实现中，呈现器与服务(控制器)层交互以检索/保存模型。视图接口和服务层通常用于简化为演示者和模型编写单元测试。

下图显示了 MVP 的控制流程：

[![mvp_模式](https://www.baeldung.com/wp-content/uploads/2021/08/mvp-300x227-1.png)](https://www.baeldung.com/wp-content/uploads/2021/08/mvp-300x227-1.png)

该模型与 MVC 中的模型相同，并且包含业务逻辑。视图是显示数据的被动界面。它将用户操作发送给演示者。

演示者位于模型和视图之间。它触发业务逻辑并使视图能够更新。它从模型接收数据并在视图中显示相同的数据。这使得测试演示者更加容易。

尽管如此，MVP 仍然存在一些问题：

-   控制器经常被省略。由于缺少控制器，控制流也必须由演示者处理。这使得演示者负责两个问题：更新模型和展示模型
-   我们不能使用数据绑定。如果可以使用 UI 框架进行绑定，我们应该利用它来简化演示者

## 6.MVC和MVP实现

我们将通过一个简单的例子来理解这些模式。我们有一个产品需要展示和更新。这些操作在 MVC 和 MVP 中的处理方式不同。

### 6.1. 视图类

我们有一个输出产品详细信息的简单视图类。MVP 和 MVC 的视图类相似：

```java
public class ProductView {
    public void printProductDetails(String name, String description, Double price) {
        log.info("Product details:");
        log.info("product Name: " + name);
        log.info("product Description: " + description);
        log.info("product price: " + price);
    }
}

```

### 6.2. MVP 模型和演示者类

现在让我们为 MVP 定义一个Product类，它只负责业务逻辑：

```java
public class Product {
    private String name;
    private String description;
    private Double price;
    
   //getters & setters
}
```

MVP 中的 Presenter 类从模型中获取数据并将其传递给视图：

```java
public class ProductPresenter {
    private final Product product;
    private final ProductView view;
    
     //getters,setters & constructor
    
    public void showProduct() {
        productView.printProductDetails(product.getName(), product.getDescription(), product.getPrice());
    }
}
```

### 6.3. MVC 模型类

对于 MVC，区别在于视图将从模型类而不是 MVP 中的展示器类获取数据。

我们可以为 MVC 定义一个模型类：

```java
public class Product {
    private String name;
    private String description;
    private Double price;
    private ProductView view;
    
    //getters,setters
    
    public void showProduct() {
        view.printProductDetails(name, description, price);
    }
}

```

注意showProduct()方法。此方法处理从模型传递到视图的数据。在 MVP 中，这是在 Presenter 类中完成的，而在 MVC 中，这是在模型类中完成的。

## 七、MVC与MVP的比较

MVC 和 MVP 之间并没有太多区别。这两种模式都专注于分离多个组件的责任，因此促进了 UI(视图)与业务层(模型)的松散耦合。

主要区别在于模式是如何实现的以及在某些高级场景中。让我们看看一些主要区别：

-   耦合：视图和模型在 MVC 中紧耦合，在 MVP 中松耦合
-   通信：在 MVP 中，View-Presenter 和 Presenter-Model 之间的通信通过接口进行。但是，控制器和视图层属于 MVC 中的同一个活动/片段
-   用户输入：在 MVC 中，用户输入由指示模型进行进一步操作的控制器处理。 但在 MVP 中，用户输入由指示演示者调用适当函数的视图处理
-   关系类型：控制器和视图之间存在多对一 关系 。一个 Controller 可以根据 MVC 中所需的操作选择不同的视图。另一方面，在 MVP 中，presenter 和 view 是一对一的关系，一个 presenter 类一次管理一个 view
-   主要组件：在 MVC 中，控制器负责。它根据用户的请求创建适当的视图并与模型交互。相反，在 MVP 中，视图是负责的。Presenter 上的视图调用方法，进一步指导模型
-   单元测试：由于紧密耦合，MVC 对单元测试的支持有限。另一方面，单元测试在 MVP 中得到了很好的支持

## 8. 为什么 MVP 比 MVC 有优势

MVP 比 MVC 略有优势，因为它可以将我们的应用程序分解为模块。因此，我们可以避免必须不断地创建视图。换句话说，MVP 可以帮助我们的视图可重用。

## 9.总结

在本教程中，我们了解了 MVC 和 MVP 架构模式以及它们之间的比较。