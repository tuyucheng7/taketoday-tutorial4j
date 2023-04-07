## **一、概述**

在本教程中，我们将讨论接口驱动开发 (IDD)，它为编码提供结构。我们将引导您完成 IDD 的使用并解释其优势。

## **2.理念**

接口驱动开发是一种开发方法，侧重于设计系统不同组件之间的[接口。](https://www.baeldung.com/java-interfaces)各自的接口定义了可用的方法。因此，我们提供有关可用功能、预期参数和返回值的信息。

### **2.1. 优点**

由于 IDD 在一开始就定义了接口，因此具有多个员工的项目可以同时开始使用它并开发其实现。这**加快了开发速度，因为他们可以在实现准备就绪之前编写代码**。

此外，**各个模块之间的耦合变得更加松散，从而使系统更加灵活和健壮。**然后，各个接口可以有一个或多个实现。其他模块可以直接实例化这些或使用注释注入它们。如果在使用 Spring Boot 时有多种可能的实现，[*@Qualifier*注解](https://www.baeldung.com/spring-qualifier-annotation)有助于选择正确的实现。

例如，可能有一个接口定义了以下方法：

```java
HelpRequest getHelpRequestById(Long id);复制
```

两个服务可以实现此接口，一个访问缓存，另一个访问数据库以返回所需的对象。使用接口的类与实现无关，可以根据需要灵活交换。这导致更好的可维护性，因为只要维护接口中定义的契约，**使用接口的类就不需要担心实现细节。**

该方法有点类似于[测试驱动开发](https://www.baeldung.com/cs/unit-testing-vs-tdd)，首先定义测试，然后进行匹配实现，直到测试成功运行。IDD 方法还在测试中提供了显着的好处。

**该接口可以轻松地模拟各个方法，**而无需通过模拟框架模拟类。这允许系统的每个组件被隔离测试。从 Java 15 开始，还可以使用[密封接口](https://www.baeldung.com/java-sealed-classes-interfaces)来**指定允许哪些类实现该接口。这提供了额外的保护。**

## **3.例子**

下面，我们将通过一个具体示例来了解如何进行 IDD。例如，让我们以一个名为“Machbarschaft”的应用程序为例，它可以帮助邻居联系并提出帮助请求，例如帮助购物或做家务。

在 IDD 的上下文中，应用程序的开发将按以下方式进行。

### **3.1. 接口识别**

我们首先确定不同的应用程序模块，例如通知、帮助请求或用户管理。在本文中，我们将重点关注帮助请求。

### **3.2. 确定用例**

现在我们寻找所有模块的可能用例。例如，对于帮助请求模块，这可能是创建帮助请求、完成或编辑帮助请求以及检索具有特定状态的所有帮助请求。

### **3.3. 定义接口**

*考虑到这些用例， HelpRequestService*的接口可能如下所示：

```java
public interface HelpRequestService {
    HelpRequestDTO createHelpRequest(CreateHelpRequestDTO createHelpRequestDTO);

    List<HelpRequestDTO> findAllByStatus(HelpRequestStatus status);

    HelpRequestDTO updateHelpRequest(UpdateHelpRequestDTO updateHelpRequestDTO);
}复制
```

createHelpRequest方法采用带有创建帮助请求信息的 CreateHelpRequestDTO 并返回映射到 HelpRequestDTO 的已*创建**帮助**请求*。

findAllByStatus方法仅采用*HelpRequestStatus*，例如， *OPEN*仅返回符合条件的*所有**HelpRequestDTO*的*列表。*这允许开发人员仅选择应该由用户完成的帮助请求或显示当前正在处理的所有帮助请求。

最后一个方法用于更新帮助请求。这里的方法获取更新的信息，然后返回映射到 HelpRequestDTO 的更新的帮助*请求*。

### **3.4. 模块自主开发**

默认情况下，开发者可以独立开发模块。这里的每个模块都将依赖其他模块的接口来执行其功能。这是一个实现的样子：

```java
public class HelpRequestServiceImpl implements HelpRequestService {

    @Override
    public HelpRequestDTO createHelpRequest(CreateHelpRequestDTO createHelpRequestDTO) {
        // here goes the implementation 
        return new HelpRequestDTO();
    }

    @Override
    public List<HelpRequestDTO> findAllByStatus(HelpRequestStatus status) {
        // here goes the implementation
        return List.of(new HelpRequestDTO());
    }

    @Override
    public HelpRequestDTO updateHelpRequest(UpdateHelpRequestDTO updateHelpRequestDTO) {
        // here goes the implementation
        return new HelpRequestDTO();
    }
}复制
```

也可能有 *HelpRequestServiceImpl* 的异步版本，或者如前所述，一个版本使用缓存，另一个用户访问数据库。

### **3.5. 实施测试**

现在接口已成功实现，我们可以继续对其[进行广泛测试](https://www.baeldung.com/java-unit-testing-best-practices)，以确保代码按预期工作。对于 *findAllByStatus*方法，我们可以检查该方法是否仅包含具有正确状态的对象：

```java
@Test
void givenHelpRequestList_whenFindAllByStatus_shouldContainOnlyStatus(){
    HelpRequestService helpRequestService = new HelpRequestServiceImpl();
    List<HelpRequestDTO> allByStatusOpen = helpRequestService.findAllByStatus(HelpRequestStatus.OPEN);
    Assertions.assertThat(allByStatusOpen).extracting(HelpRequestDTO::getStatus)
      .containsOnly(HelpRequestStatus.OPEN);
}复制
```

### **3.6. 模块集成**

在开发和测试每个模块之后，团队通过定义的接口相互通信来集成它们。这将允许不同模块之间的轻松集成和低耦合：

```java
HelpRequestService helpRequestService = new HelpRequestServiceImpl();
helpRequestService.findAllByStatus(HelpRequestStatus.OPEN);复制
```

通过使用 IDD，开发“Machbarschaft”应用程序变得更加容易和强大。接口定义清晰，模块独立开发，便于测试和集成，模块间耦合度低，维护和扩展快速、安全。

总体而言，IDD 有助于开发满足用户需求的高质量且用户友好的应用程序。

## **4。结论**

在本文中，我们讨论了 IDD 的优势，并展示了如何使用 IDD 的具体示例。总体而言，IDD 有助于降低系统的复杂性，提高可维护性和可扩展性，并减少开发时间和成本。