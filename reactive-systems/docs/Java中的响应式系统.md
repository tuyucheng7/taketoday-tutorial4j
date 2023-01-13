## 1. 简介

在本教程中，我们将了解使用 Spring 和其他工具和框架在Java中创建反应式系统的基础知识。

在此过程中，我们将讨论反应式编程如何成为创建反应式系统的驱动力。这将帮助我们理解创建响应式系统的基本原理以及它在此过程中启发的不同规范、库和标准。

## 2. 什么是反应式系统？

在过去的几十年里，技术领域经历了几次颠覆，导致我们看待技术价值的方式发生了彻底转变。互联网出现之前的计算世界永远无法想象它将改变我们今天的方式方法。

随着 Internet 对大众的影响以及它所承诺的不断发展的体验，应用程序架构师需要保持警惕以满足他们的需求。

从根本上说，这意味着我们永远不能像以前那样设计应用程序。高度响应的应用程序不再是奢侈品，而是必需品。

这也是面对随机故障和不可预测的负载。时间的需要不仅仅是获得正确的结果，而是要快速获得结果！推动我们承诺提供的惊人用户体验非常重要。

这就是需要一种可以为我们提供 Reactive Systems 的架构风格的原因。

### 2.1. 反应宣言

早在 2013 年，由乔纳斯·博纳 (Jonas Boner) 领导的开发团队聚集在一起，在一份名为[反应式宣言 (Reactive Manifesto](https://www.reactivemanifesto.org/) ) 的文档中定义了一组核心原则。这为创建响应式系统的架构风格奠定了基础。从那时起，这个宣言引起了开发者社区的极大兴趣。

基本上，本文档规定了反应式系统灵活、松散耦合和可扩展的秘诀。这使得此类系统易于开发、容错，最重要的是具有高度响应性，是令人难以置信的用户体验的基础。

那么这个秘方是什么？好吧，这几乎不是什么秘密！宣言定义了反应式系统的基本特征或原则：

-   响应式：反应式系统应提供快速且一致的响应时间，从而提供一致的服务质量
-   弹性：反应式系统应该通过和隔离在随机故障的情况下保持响应
-   弹性：这样的系统应该通过具有成本效益的可扩展性在不可预测的工作负载下保持响应
-   消息驱动：它应该依赖于系统组件之间的异步消息传递

这些原则听起来简单而明智，但在复杂的企业架构中并不总是更容易实施。在本教程中，我们将牢记这些原则，用Java开发一个示例系统！

## 3. 什么是响应式编程？

在我们继续之前，了解反应式编程和反应式系统之间的区别很重要。我们经常使用这两个术语，并且很容易将一个术语误解为另一个术语。正如我们之前所见，响应式系统是特定架构风格的结果。

相反，反应式编程是一种编程范式，其重点是开发异步和非阻塞组件。反应式编程的核心是我们可以观察和反应的数据流，甚至可以施加反压。这导致非阻塞执行，从而以更少的执行线程实现更好的可伸缩性。

现在，这并不意味着反应式系统和反应式编程是相互排斥的。事实上，反应式编程是实现反应式系统的重要一步，但它不是全部！

### 3.1. 反应流

[Reactive Streams](http://www.reactive-streams.org/)是一项社区倡议，始于 2013 年，旨在为具有非阻塞背压的异步流处理提供标准。这里的目标是定义一组可以描述必要操作和实体的接口、方法和协议。

从那时起，出现了多种编程语言的几种实现，它们符合反应流规范。其中包括 Akka Streams、Ratpack 和 Vert.x 等等。

### 3.2.Java的反应式库

响应式流背后的最初目标之一是最终作为官方Java标准库包含在内。因此，反应流规范在语义上等同于Java9 中引入的JavaFlow 库。

除此之外，还有一些流行的选择可以在Java中实现响应式编程：

-   [Reactive Extensions](http://reactivex.io/)：通常称为 ReactiveX，它们为使用可观察流的异步编程提供 API。这些可用于多种编程语言和平台，包括称为[RxJava的 Java](https://github.com/ReactiveX/RxJava)
-   [Project Reactor](https://projectreactor.io/)：这是另一个反应式库，基于反应式流规范，旨在在 JVM 上构建非应用程序。它也恰好是[Spring 生态系统中反应堆的基础](https://spring.io/reactive)

## 4. 一个简单的应用

出于本教程的目的，我们将开发一个基于微服务架构且具有最小前端的简单应用程序。应用程序架构应该有足够的元素来创建一个反应式系统。

对于我们的应用程序，我们将采用端到端反应式编程和其他模式和工具来实现反应式系统的基本特征。

### 4.1. 建筑学

我们将从定义一个简单的应用程序架构开始，它不一定表现出反应式系统的特征。从那时起，我们将进行必要的更改以一一实现这些特性。

所以，首先，让我们从定义一个简单的架构开始：

[![阻塞架构](https://www.baeldung.com/wp-content/uploads/2020/07/Blocking-Architecture.jpg)](https://www.baeldung.com/wp-content/uploads/2020/07/Blocking-Architecture.jpg)

这是一个非常简单的架构，它有一堆微服务来促进我们可以下订单的商业用例。它还有一个用于用户体验的前端，所有通信都以 REST over HTTP 的形式进行。此外，每个微服务都在单独的数据库中管理它们的数据，这种做法称为每个服务一个数据库。

我们将在以下小节中继续创建这个简单的应用程序。这将是我们理解这种架构的谬误以及采用原则和实践的方式方法的基础，以便我们可以将其转化为反应式系统。

### 4.3. 库存微服务

库存微服务将负责管理产品列表及其当前库存。它还将允许在处理订单时更改库存。我们将结合使用[Spring Boot](https://www.baeldung.com/spring-boot)和 MongoDB 来开发此服务。

让我们首先定义一个控制器来公开一些端点：

```java
@GetMapping
public List<Product> getAllProducts() {
    return productService.getProducts();
}
 
@PostMapping
public Order processOrder(@RequestBody Order order) {
    return productService.handleOrder(order);
}
 
@DeleteMapping
public Order revertOrder(@RequestBody Order order) {
    return productService.revertOrder(order);
}
```

以及封装我们业务逻辑的服务：

```java
@Transactional
public Order handleOrder(Order order) {       
    order.getLineItems()
      .forEach(l -> {
          Product> p = productRepository.findById(l.getProductId())
            .orElseThrow(() -> new RuntimeException("Could not find the product: " + l.getProductId()));
          if (p.getStock() >= l.getQuantity()) {
              p.setStock(p.getStock() - l.getQuantity());
              productRepository.save(p);
          } else {
              throw new RuntimeException("Product is out of stock: " + l.getProductId());
          }
      });
    return order.setOrderStatus(OrderStatus.SUCCESS);
}

@Transactional
public Order revertOrder(Order order) {
    order.getLineItems()
      .forEach(l -> {
          Product p = productRepository.findById(l.getProductId())
            .orElseThrow(() -> new RuntimeException("Could not find the product: " + l.getProductId()));
          p.setStock(p.getStock() + l.getQuantity());
          productRepository.save(p);
      });
    return order.setOrderStatus(OrderStatus.SUCCESS);
}
```

请注意，我们将实体保存在事务中，这可确保在出现异常时不会出现不一致的状态。

除了这些，我们还必须定义域实体、存储库接口和一堆配置类，这些配置类是一切正常工作所必需的。

但由于这些大多是样板文件，我们将避免仔细研究它们，并且可以在本文最后一节提供的 GitHub 存储库中引用它们。

### 4.4. 航运微服务

航运微服务也不会有太大的不同。这将负责检查是否可以为订单生成发货，并在可能的情况下创建一个。

和以前一样，我们将定义一个控制器来公开我们的端点，实际上只是一个端点：

```java
@PostMapping
public Order process(@RequestBody Order order) {
    return shippingService.handleOrder(order);
}
```

以及封装与订单发货相关的业务逻辑的服务：

```java
public Order handleOrder(Order order) {
    LocalDate shippingDate = null;
    if (LocalTime.now().isAfter(LocalTime.parse("10:00"))
      && LocalTime.now().isBefore(LocalTime.parse("18:00"))) {
        shippingDate = LocalDate.now().plusDays(1);
    } else {
        throw new RuntimeException("The current time is off the limits to place order.");
    }
    shipmentRepository.save(new Shipment()
      .setAddress(order.getShippingAddress())
      .setShippingDate(shippingDate));
    return order.setShippingDate(shippingDate)
      .setOrderStatus(OrderStatus.SUCCESS);
}
```

我们简单的运输服务只是检查有效的时间窗口来下订单。我们将避免像以前那样讨论其余的样板代码。

### 4.5. 订购微服务

最后，我们将定义一个订单微服务，它将负责创建一个新的订单。有趣的是，它还将充当协调器服务，在该服务中它将与订单的库存服务和运输服务进行通信。

让我们用所需的端点定义我们的控制器：

```java
@PostMapping
public Order create(@RequestBody Order order) {
    Order processedOrder = orderService.createOrder(order);
    if (OrderStatus.FAILURE.equals(processedOrder.getOrderStatus())) {
        throw new RuntimeException("Order processing failed, please try again later.");
    }
    return processedOrder;
}
@GetMapping
public List<Order> getAll() {
    return orderService.getOrders();
}
```

并且，封装与订单相关的业务逻辑的服务：

```java
public Order createOrder(Order order) {
    boolean success = true;
    Order savedOrder = orderRepository.save(order);
    Order inventoryResponse = null;
    try {
        inventoryResponse = restTemplate.postForObject(
          inventoryServiceUrl, order, Order.class);
    } catch (Exception ex) {
        success = false;
    }
    Order shippingResponse = null;
    try {
        shippingResponse = restTemplate.postForObject(
          shippingServiceUrl, order, Order.class);
    } catch (Exception ex) {
        success = false;
        HttpEntity<Order> deleteRequest = new HttpEntity<>(order);
        ResponseEntity<Order> deleteResponse = restTemplate.exchange(
          inventoryServiceUrl, HttpMethod.DELETE, deleteRequest, Order.class);
    }
    if (success) {
        savedOrder.setOrderStatus(OrderStatus.SUCCESS);
        savedOrder.setShippingDate(shippingResponse.getShippingDate());
    } else {
        savedOrder.setOrderStatus(OrderStatus.FAILURE);
    }
    return orderRepository.save(savedOrder);
}

public List<Order> getOrders() {
    return orderRepository.findAll();
}
```

我们正在协调调用库存和运输服务的订单处理远非理想。具有多个微服务的分布式事务本身就是一个复杂的主题，超出了本教程的范围。

然而，我们将在本教程后面看到反应式系统如何在一定程度上避免分布式事务的需要。

和以前一样，我们不会查看其余的样板代码。但是，这可以在 GitHub 存储库中引用。

### 4.6. 前端

我们还添加一个用户界面来完成讨论。用户界面将基于 Angular，并且将是一个简单的单页应用程序。

我们需要在 Angular 中创建一个简单的组件来处理创建和获取订单。特别重要的是我们调用 API 来创建订单的部分：

```javascript
createOrder() {
    let headers = new HttpHeaders({'Content-Type': 'application/json'});
    let options = {headers: headers}
    this.http.post('http://localhost:8080/api/orders', this.form.value, options)
      .subscribe(
        (response) => {
          this.response = response
        },
        (error) => {
          this.error = error
        }
      )
}
```

上面的代码片段期望订单数据以表单形式捕获并在组件范围内可用。[Angular 为使用反应式和模板驱动的表单](https://angular.io/guide/forms-overview)创建简单到复杂的表单提供了极好的支持。

同样重要的是我们获得先前创建的订单的部分：

```javascript
getOrders() {
  this.previousOrders = this.http.get(''http://localhost:8080/api/orders'')
}
```

请注意[Angular HTTP 模块](https://angular.io/guide/http)本质上是异步的，因此返回 RxJS Observable s。我们可以通过异步管道传递它们来处理我们视图中的响应：

```html
<div class="container" ngIf="previousOrders !== null">
  <h2>Your orders placed so far:</h2>
  <ul>
    <li ngFor="let order of previousOrders | async">
      <p>Order ID: {{ order.id }}, Order Status: {{order.orderStatus}}, Order Message: {{order.responseMessage}}</p>
    </li>
  </ul>
</div>
```

当然，Angular 需要模板、样式和配置才能工作，但这些可以在 GitHub 存储库中参考。请注意，我们在这里将所有内容捆绑在一个组件中，理想情况下这不是我们应该做的事情。

但是，对于本教程，这些问题不在讨论范围之内。

### 4.7. 部署应用程序

现在我们已经创建了应用程序的所有独立部分，我们应该如何部署它们呢？好吧，我们总是可以手动执行此操作。但我们应该小心，它很快就会变得乏味。

对于本教程，我们将使用[Docker Compose](https://www.baeldung.com/docker-compose)在 Docker Machine上构建和部署我们的应用程序。这将需要我们在每个服务中添加一个标准的 Dockerfile，并为整个应用程序创建一个 Docker Compose 文件。

让我们看看这个docker-compose.yml文件的样子：

```plaintext
version: '3'
services:
  frontend:
    build: ./frontend
    ports:
      - "80:80"
  order-service:
    build: ./order-service
    ports:
      - "8080:8080"
  inventory-service:
    build: ./inventory-service
    ports:
      - "8081:8081"
  shipping-service:
    build: ./shipping-service
    ports:
      - "8082:8082"
```

这是 Docker Compose 中相当标准的服务定义，不需要特别注意。

### 4.8. 这种架构的问题

现在我们有了一个简单的应用程序，其中有多个服务相互交互，我们可以讨论这个架构中的问题。在接下来的部分中，我们将尝试解决一些问题，并最终达到将我们的应用程序转变为反应式系统的状态！

虽然此应用程序远非生产级软件并且存在几个问题，但我们将重点关注与响应式系统的动机相关的问题：

-   库存服务或运输服务的失败会产生连锁反应
-   对外部系统和数据库的调用本质上都是阻塞的
-   部署无法自动处理故障和负载波动

## 5.反应式编程

在任何程序中阻塞调用通常会导致关键资源等待事情发生。这些包括数据库调用、对 Web 服务的调用和文件系统调用。如果我们可以将执行线程从这种等待中解放出来，并提供一种在结果可用时返回的机制，它将产生更好的资源利用率。

这就是采用反应式编程范式为我们所做的。虽然可以为其中许多调用切换到响应式库，但并非所有情况都可行。对我们来说，幸运的是，Spring 使使用 MongoDB 和 REST API 的反应式编程变得更加容易：

[![响应式架构](https://www.baeldung.com/wp-content/uploads/2020/07/Reactive-Architecture.jpg)](https://www.baeldung.com/wp-content/uploads/2020/07/Reactive-Architecture.jpg)

[Spring Data Mongo](https://www.baeldung.com/spring-data-mongodb-tutorial)支持通过 MongoDB Reactive StreamsJavaDriver 进行响应式访问。它提供ReactiveMongoTemplate和ReactiveMongoRepository，两者都具有广泛的映射功能。

[Spring WebFlux](https://www.baeldung.com/spring-webflux)为 Spring提供反应堆栈 Web 框架，支持非阻塞代码和 Reactive Streams 背压。它利用 Reactor 作为其反应库。此外，它还提供WebClient来执行带有 Reactive Streams 背压的 HTTP 请求。它使用 Reactor Netty 作为 HTTP 客户端库。

### 5.1. 库存服务

我们将首先更改我们的端点以发出响应式发布者：

```java
@GetMapping
public Flux<Product> getAllProducts() {
    return productService.getProducts();
}
@PostMapping
public Mono<Order> processOrder(@RequestBody Order order) {
    return productService.handleOrder(order);
}

@DeleteMapping
public Mono<Order> revertOrder(@RequestBody Order order) {
    return productService.revertOrder(order);
}
```

显然，我们还必须对服务进行必要的更改：

```java
@Transactional
public Mono<Order> handleOrder(Order order) {
    return Flux.fromIterable(order.getLineItems())
      .flatMap(l -> productRepository.findById(l.getProductId()))
      .flatMap(p -> {
          int q = order.getLineItems().stream()
            .filter(l -> l.getProductId().equals(p.getId()))
            .findAny().get()
            .getQuantity();
          if (p.getStock() >= q) {
              p.setStock(p.getStock() - q);
              return productRepository.save(p);
          } else {
              return Mono.error(new RuntimeException("Product is out of stock: " + p.getId()));
          }
      })
      .then(Mono.just(order.setOrderStatus("SUCCESS")));
}

@Transactional
public Mono<Order> revertOrder(Order order) {
    return Flux.fromIterable(order.getLineItems())
      .flatMap(l -> productRepository.findById(l.getProductId()))
      .flatMap(p -> {
          int q = order.getLineItems().stream()
            .filter(l -> l.getProductId().equals(p.getId()))
            .findAny().get()
            .getQuantity();
          p.setStock(p.getStock() + q);
          return productRepository.save(p);
      })
      .then(Mono.just(order.setOrderStatus("SUCCESS")));
}
```

### 5.2. 送货服务

同样，我们将更改运输服务的端点：

```java
@PostMapping
public Mono<Order> process(@RequestBody Order order) {
    return shippingService.handleOrder(order);
}
```

并且，服务中的相应更改以利用反应式编程：

```java
public Mono<Order> handleOrder(Order order) {
    return Mono.just(order)
      .flatMap(o -> {
          LocalDate shippingDate = null;
          if (LocalTime.now().isAfter(LocalTime.parse("10:00"))
            && LocalTime.now().isBefore(LocalTime.parse("18:00"))) {
              shippingDate = LocalDate.now().plusDays(1);
          } else {
              return Mono.error(new RuntimeException("The current time is off the limits to place order."));
          }
          return shipmentRepository.save(new Shipment()
            .setAddress(order.getShippingAddress())
            .setShippingDate(shippingDate));
      })
      .map(s -> order.setShippingDate(s.getShippingDate())
        .setOrderStatus(OrderStatus.SUCCESS));
    }
```

### 5.3. 订购服务

我们必须对订单服务的端点进行类似的更改：

```java
@PostMapping
public Mono<Order> create(@RequestBody Order order) {
    return orderService.createOrder(order)
      .flatMap(o -> {
          if (OrderStatus.FAILURE.equals(o.getOrderStatus())) {
              return Mono.error(new RuntimeException("Order processing failed, please try again later. " + o.getResponseMessage()));
          } else {
              return Mono.just(o);
          }
      });
}

@GetMapping
public Flux<Order> getAll() {
    return orderService.getOrders();
}
```

对服务的更改将更加复杂，因为我们必须使用 Spring WebClient来调用库存和运送反应端点：

```java
public Mono<Order> createOrder(Order order) {
    return Mono.just(order)
      .flatMap(orderRepository::save)
      .flatMap(o -> {
          return webClient.method(HttpMethod.POST)
            .uri(inventoryServiceUrl)
            .body(BodyInserters.fromValue(o))
            .exchange();
      })
      .onErrorResume(err -> {
          return Mono.just(order.setOrderStatus(OrderStatus.FAILURE)
            .setResponseMessage(err.getMessage()));
      })
      .flatMap(o -> {
          if (!OrderStatus.FAILURE.equals(o.getOrderStatus())) {
              return webClient.method(HttpMethod.POST)
                .uri(shippingServiceUrl)
                .body(BodyInserters.fromValue(o))
                .exchange();
          } else {
              return Mono.just(o);
          }
      })
      .onErrorResume(err -> {
          return webClient.method(HttpMethod.POST)
            .uri(inventoryServiceUrl)
            .body(BodyInserters.fromValue(order))
            .retrieve()
            .bodyToMono(Order.class)
            .map(o -> o.setOrderStatus(OrderStatus.FAILURE)
              .setResponseMessage(err.getMessage()));
      })
      .map(o -> {
          if (!OrderStatus.FAILURE.equals(o.getOrderStatus())) {
              return order.setShippingDate(o.getShippingDate())
                .setOrderStatus(OrderStatus.SUCCESS);
          } else {
              return order.setOrderStatus(OrderStatus.FAILURE)
                .setResponseMessage(o.getResponseMessage());
          }
      })
      .flatMap(orderRepository::save);
}

public Flux<Order> getOrders() {
    return orderRepository.findAll();
}
```

这种使用反应式 API 的编排并不容易，而且往往容易出错且难以调试。我们将在下一节中看到如何简化它。

### 5.4. 前端

现在，我们的 API 能够在事件发生时对其进行流式传输，很自然地，我们也应该能够在我们的前端利用它。幸运的是，Angular 支持[EventSource](https://developer.mozilla.org/en-US/docs/Web/API/EventSource)，它是 Server-Sent Events 的接口。

让我们看看我们如何将所有以前的订单作为事件流来提取和处理：

```javascript
getOrderStream() {
    return Observable.create((observer) => {
        let eventSource = new EventSource('http://localhost:8080/api/orders')
        eventSource.onmessage = (event) => {
            let json = JSON.parse(event.data)
            this.orders.push(json)
            this._zone.run(() => {
                observer.next(this.orders)
            })
        }
        eventSource.onerror = (error) => {
            if(eventSource.readyState === 0) {
                eventSource.close()
                this._zone.run(() => {
                    observer.complete()
                })
            } else {
                this._zone.run(() => {
                    observer.error('EventSource error: ' + error)
                })
            }
        }
    })
}
```

## 6. 消息驱动架构

我们要解决的第一个问题与服务到服务的通信有关。现在，这些通信是同步的，这带来了几个问题。其中包括级联故障、复杂的编排和分布式事务等等。

解决这个问题的一个明显方法是使这些通信异步。促进所有服务到服务通信的消息代理可以为我们解决问题。我们将使用 Kafka 作为消息代理，并使用[Spring for Kafka](https://www.baeldung.com/spring-kafka)来生成和使用消息：

[![消息驱动架构](https://www.baeldung.com/wp-content/uploads/2020/07/Message-Driven-Architecture.jpg)](https://www.baeldung.com/wp-content/uploads/2020/07/Message-Driven-Architecture.jpg)

我们将使用单个主题来生成和使用具有不同订单状态的订单消息，以便服务做出反应。

让我们看看每个服务需要如何改变。

### 6.1. 库存服务

让我们首先为我们的库存服务定义消息生产者：

```java
@Autowired
private KafkaTemplate<String, Order> kafkaTemplate;

public void sendMessage(Order order) {
    this.kafkaTemplate.send("orders", order);
}
```

接下来，我们必须为库存服务定义一个消息消费者，以对主题上的不同消息做出反应：

```java
@KafkaListener(topics = "orders", groupId = "inventory")
public void consume(Order order) throws IOException {
    if (OrderStatus.RESERVE_INVENTORY.equals(order.getOrderStatus())) {
        productService.handleOrder(order)
          .doOnSuccess(o -> {
              orderProducer.sendMessage(order.setOrderStatus(OrderStatus.INVENTORY_SUCCESS));
          })
          .doOnError(e -> {
              orderProducer.sendMessage(order.setOrderStatus(OrderStatus.INVENTORY_FAILURE)
                .setResponseMessage(e.getMessage()));
          }).subscribe();
    } else if (OrderStatus.REVERT_INVENTORY.equals(order.getOrderStatus())) {
        productService.revertOrder(order)
          .doOnSuccess(o -> {
              orderProducer.sendMessage(order.setOrderStatus(OrderStatus.INVENTORY_REVERT_SUCCESS));
          })
          .doOnError(e -> {
              orderProducer.sendMessage(order.setOrderStatus(OrderStatus.INVENTORY_REVERT_FAILURE)
                .setResponseMessage(e.getMessage()));
          }).subscribe();
    }
}
```

这也意味着我们现在可以安全地从我们的控制器中删除一些冗余端点。这些更改足以在我们的应用程序中实现异步通信。

### 6.2. 送货服务

运输服务的变化与我们之前对库存服务所做的变化相对相似。消息生产者是一样的，消息消费者是特定于发送逻辑的：

```java
@KafkaListener(topics = "orders", groupId = "shipping")
public void consume(Order order) throws IOException {
    if (OrderStatus.PREPARE_SHIPPING.equals(order.getOrderStatus())) {
        shippingService.handleOrder(order)
          .doOnSuccess(o -> {
              orderProducer.sendMessage(order.setOrderStatus(OrderStatus.SHIPPING_SUCCESS)
                .setShippingDate(o.getShippingDate()));
          })
          .doOnError(e -> {
              orderProducer.sendMessage(order.setOrderStatus(OrderStatus.SHIPPING_FAILURE)
                .setResponseMessage(e.getMessage()));
          }).subscribe();
    }
}
```

我们现在可以安全地删除控制器中的所有端点，因为我们不再需要它们。

### 6.3. 订购服务

订单服务的更改将涉及更多，因为这是我们之前进行所有编排的地方。

尽管如此，消息生产者保持不变，消息消费者采用特定于订单服务的逻辑：

```java
@KafkaListener(topics = "orders", groupId = "orders")
public void consume(Order order) throws IOException {
    if (OrderStatus.INITIATION_SUCCESS.equals(order.getOrderStatus())) {
        orderRepository.findById(order.getId())
          .map(o -> {
              orderProducer.sendMessage(o.setOrderStatus(OrderStatus.RESERVE_INVENTORY));
              return o.setOrderStatus(order.getOrderStatus())
                .setResponseMessage(order.getResponseMessage());
          })
          .flatMap(orderRepository::save)
          .subscribe();
    } else if ("INVENTORY-SUCCESS".equals(order.getOrderStatus())) {
        orderRepository.findById(order.getId())
          .map(o -> {
              orderProducer.sendMessage(o.setOrderStatus(OrderStatus.PREPARE_SHIPPING));
              return o.setOrderStatus(order.getOrderStatus())
                .setResponseMessage(order.getResponseMessage());
          })
          .flatMap(orderRepository::save)
          .subscribe();
    } else if ("SHIPPING-FAILURE".equals(order.getOrderStatus())) {
        orderRepository.findById(order.getId())
          .map(o -> {
              orderProducer.sendMessage(o.setOrderStatus(OrderStatus.REVERT_INVENTORY));
              return o.setOrderStatus(order.getOrderStatus())
                .setResponseMessage(order.getResponseMessage());
          })
          .flatMap(orderRepository::save)
          .subscribe();
    } else {
        orderRepository.findById(order.getId())
          .map(o -> {
              return o.setOrderStatus(order.getOrderStatus())
                .setResponseMessage(order.getResponseMessage());
          })
          .flatMap(orderRepository::save)
          .subscribe();
    }
}
```

此处的消费者只是对具有不同订单状态的订单消息做出反应。这就是为我们提供不同服务之间的编排的原因。

最后，我们的订单服务也必须改变以支持这种编排：

```java
public Mono<Order> createOrder(Order order) {
    return Mono.just(order)
      .flatMap(orderRepository::save)
      .map(o -> {
          orderProducer.sendMessage(o.setOrderStatus(OrderStatus.INITIATION_SUCCESS));
          return o;
      })
      .onErrorResume(err -> {
          return Mono.just(order.setOrderStatus(OrderStatus.FAILURE)
            .setResponseMessage(err.getMessage()));
      })
      .flatMap(orderRepository::save);
}
```

请注意，这比我们在上一节中必须使用反应式端点编写的服务要简单得多。异步编排通常会产生更简单的代码，尽管它是以最终一致性和复杂的调试和监控为代价的。正如我们所猜测的那样，我们的前端将不再立即获得订单的最终状态。

## 7. 容器编排服务

我们要解决的最后一个难题与部署有关。

我们在应用程序中想要的是充足的冗余和根据需要自动扩大或缩小的趋势。

我们已经通过 Docker 实现了服务的容器化，并且正在通过 Docker Compose 管理它们之间的依赖关系。虽然这些本身就是很棒的工具，但它们并不能帮助我们实现我们想要的。

因此，我们需要一个容器编排服务来处理我们应用程序中的冗余和可扩展性。虽然有多种选择，但其中一种流行的选择包括 Kubernetes。Kubernetes 为我们提供了一种与云供应商无关的方式来实现容器化工作负载的高度可扩展部署。

[Kubernetes](https://www.baeldung.com/kubernetes)将像 Docker 这样的容器包装成 Pod，这是最小的部署单元。此外，我们可以使用[Deployment](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/)以声明方式描述所需的状态。

Deployment 会创建 ReplicaSets，它在内部负责启动 Pod。我们可以描述在任何时间点应该运行的最小数量的相同 pod。这提供了冗余并因此提供了高可用性。

让我们看看如何为我们的应用程序定义 Kubernetes 部署：

```plaintext
apiVersion: apps/v1
kind: Deployment
metadata: 
  name: inventory-deployment
spec: 
  replicas: 3
  selector:
    matchLabels:
      name: inventory-deployment
  template: 
    metadata: 
      labels: 
        name: inventory-deployment
    spec: 
      containers:
      - name: inventory
        image: inventory-service-async:latest
        ports: 
        - containerPort: 8081
---
apiVersion: apps/v1
kind: Deployment
metadata: 
  name: shipping-deployment
spec: 
  replicas: 3
  selector:
    matchLabels:
      name: shipping-deployment
  template: 
    metadata: 
      labels: 
        name: shipping-deployment
    spec: 
      containers:
      - name: shipping
        image: shipping-service-async:latest
        ports: 
        - containerPort: 8082
---
apiVersion: apps/v1
kind: Deployment
metadata: 
  name: order-deployment
spec: 
  replicas: 3
  selector:
    matchLabels:
      name: order-deployment
  template: 
    metadata: 
      labels: 
        name: order-deployment
    spec: 
      containers:
      - name: order
        image: order-service-async:latest
        ports: 
        - containerPort: 8080
```

在这里，我们声明我们的部署随时维护三个相同的 pod 副本。虽然这是增加冗余的好方法，但对于变化的负载来说可能还不够。Kubernetes 提供了另一种称为 Horizontal [Pod Autoscaler](https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/)的资源， 它可以根据观察到的指标(如 CPU 利用率)扩展部署中的 Pod 数量。

请注意，我们刚刚介绍了托管在 Kubernetes 集群上的应用程序的可扩展性方面。这并不一定意味着底层集群本身是可扩展的。创建高可用性 Kubernetes 集群是一项非常重要的任务，超出了本教程的范围。

## 8. 反应体系

现在我们已经对我们的架构进行了一些改进，也许是时候根据响应式系统的定义对其进行评估了。我们将根据我们在本教程前面讨论的响应式系统的四个特征进行评估：

-   响应式：采用响应式编程范式应该可以帮助我们实现端到端的非阻塞，从而实现响应式应用程序
-   弹性：Kubernetes 部署具有所需数量的 pod 的 ReplicaSet 应该提供针对随机故障的弹性
-   Elastic：Kubernetes 集群和资源应该为我们提供必要的支持，让我们在面对不可预知的负载时保持弹性
-   消息驱动：通过 Kafka 代理异步处理所有服务到服务的通信应该对我们有所帮助

虽然这看起来很有希望，但远未结束。老实说，寻求真正的响应式系统应该是不断改进的过程。在高度复杂的基础设施中，我们永远无法抢占所有可能失败的地方，我们的应用程序只是其中的一小部分。

因此，反应系统将要求构成整体的每个部分都具有可靠性。从物理网络到 DNS 等基础设施服务，它们都应该齐心协力帮助我们实现最终目标。

通常，我们可能无法对所有这些部分进行管理和提供必要的保证。而这正是托管云基础设施有助于减轻我们痛苦的地方。我们可以从 IaaS(Infeastrure-as-a-Service)、BaaS(后端即服务)和 PaaS(平台即服务)等众多服务中进行选择，将责任委托给外部各方。这让我们尽可能地对我们的应用程序负责。

## 9.总结

在本教程中，我们了解了反应式系统的基础知识以及它与反应式编程的比较。我们创建了一个包含多个微服务的简单应用程序，并强调了我们打算使用反应式系统解决的问题。

更进一步，我们更进一步，在架构中引入了反应式编程、基于消息的架构和容器编排服务来实现反应式系统。

最后，我们讨论了由此产生的架构以及它如何保持通向反应式系统的旅程！本教程并未向我们介绍可以帮助我们创建反应式系统的所有工具、框架或模式，但它向我们介绍了旅程。