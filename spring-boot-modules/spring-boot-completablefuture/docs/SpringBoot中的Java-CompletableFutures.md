## 1. 概述

当Java 8发布时，开发人员获得了许多改变游戏规则的特性。其中有一个CompletableFuture是并行和异步处理的重大改进。

在本教程中，我们将探讨如何利用此功能，使代码在Spring Boot中更快、响应更快。

## 2. 用例

为了更好地理解CompletableFuture的工作原理，我们将使用同步代码，然后重构它以使用CompletableFuture。

我们将重构负责管理客户信息的Spring Boot应用程序。此应用程序连接到数据库。为了本教程的简单性，我们使用H2内存数据库。该数据库包含有关客户的信息，例如ID、姓名、电话和创建日期。该应用程序与其他API通信以获取有关客户的其他信息。

顾名思义，地址外部API返回客户的地址，财务信息外部API返回支付信息，如id、信用卡号和IBAN。忠诚度外部API返回客户获得的奖励积分。最后，交易外部API返回客户的购买交易列表。

通过客户ID检索客户API对数据库执行查询，然后与所有API通信以获取给定客户的所需信息，将其组合成一个响应，并将其返回给调用者。

该应用程序的API也允许调用者更新有关客户的一些信息。查看下图以更好地了解设置。

<img src="../assets/img.png">

该应用程序有一个名为DataLoader的类。当应用程序启动时，此类将使用虚假信息填充数据库和客户端。这样，每次应用程序启动时，我们都会有数据可以使用。该应用程序具有伪造的API客户端类，这些类旨在模拟真实的[REST HTTP客户端](https://javarevisited.blogspot.com/2022/02/spring-boot-restful-web-service-example-tutorial.html)。

为了模拟每个[HTTP客户端](https://www.java67.com/2022/12/10-examples-of-java-httpclient.html)的连接延迟，使用一个简单的代码使其线程休眠几秒钟。我试图让这个项目尽可能接近现实生活场景，同时尽量保持简单。

该应用程序有一个包含两个独立类的控制器层，以及一个包含两个类的服务层。一对用于顺序、同步处理的控制器和服务，另一对用于并行异步处理。

每个服务调用相同的客户端。所以控制器-服务对之间的主要区别在于服务。因此，从这里开始，本教程将重点关注服务层。其余代码很简单，你可以在下面自行检查整个项目：

## 3. 挑战

一切正常，但不幸的是，由于网络延迟，应用程序的响应时间无法接受。我们必须尽可能地减少应用程序的响应时间。

问题是我们有顺序同步代码，因此我们的代码将调用第一个API、第二个，依此类推。每个API调用都需要几秒钟才能完成。当我们查看每个API的响应时间时，时间不会很重要，因为它只有几秒钟，但是当我们将所有调用加起来时，组合响应时间将会很大。

我们可以并行调用所有需要的API，而不是在调用一个接一个地发生时使用顺序同步代码。我们将如何解决这个问题？[是的，我们将使用CompletableFuture](https://javarevisited.blogspot.com/2015/01/how-to-use-future-and-futuretask-in-Java.html)来完成。

## 4. 使用runAsync方法

我们的应用程序有一个PUT API。PUT [API](https://www.java67.com/2016/09/when-to-use-put-or-post-in-restful-web-services.html)用于将实体的当前数据替换为根据请求收到的新数据，因此服务方法的名称为replaceCustomer。

此方法接收客户ID和包含电话号码、财务信息和客户地址的请求正文。首先，它更新数据库中的电话号码，然后调用财务客户端更新财务信息，然后调用地址客户端更新地址。一个方法的完整代码：

```java
public void replaceCustomer(Integer customerId, UpdateCustomerRequest request) {
    log.info("Replacing customer", customerId);
    LocalDateTime startTime = LocalDateTime.now();
    customerRepository.findById(customerId)
        .ifPresent(customerEntity -> {
            customerEntity.setPhoneNumber(request.getPhoneNumber());
            customerRepository.save(customerEntity);
        });
    Set<FinancialInfo> financialInfo = request.getFinancialInfo().stream()
        .map(FinancialInfo::valueOf)
        .collect(Collectors.toSet());
    financialClient.updateFinancialInfo(customerId, financialInfo);

    Address address = Address.valueOf(request.getAddress());
    addressClient.updateAddressByCustomerId(customerId, address);
    log.info("Customer updated successfully!");
    log.info("Operation duration {} sec", Duration.between(startTime, LocalDateTime.now()).toSeconds());
}
```

正如你在此处看到的，该方法更新了客户，然后返回无效。CompelatableFeature类有一个静态方法runAsync用于运行不返回任何内容的代码。我将每个调用包装在这个方法中，现在我有三个可完成的特征对象。

在我将这些对象传递给allOf方法之后。此方法组合所有接收到的对象并返回一个新的CompletableFuture，当所有对象都完成时它就完成了。我使用这种方法是因为所有三个调用都是独立的并且可以并行执行。换句话说，任何给定的可完成特征对象都不必等待另一个可完成特征对象的结果。完整代码如下：

```java
public CompletableFuture<Void> replaceCustomer (Integer customerId, UpdateCustomerRequest request) { 
    log.info("Replacing customer", customerId);
    CompletableFuture<Void> updateCustomerCF = CompletableFuture.runAsync(() -> {
        customerRepository.findById(customerId)
            .ifPresent(customerEntity -> {
                customerEntity.setPhoneNumber(request.getPhoneNumber());
                customerRepository.save(customerEntity);
            });
    });
    CompletableFuture<Void> updateFinancialInfoCF = CompletableFuture.runAsync(() -> {
        Set<FinancialInfo> financialInfo = request.getFinancialInfo().stream()
            .map(FinancialInfo::valueOf)
            .collect(Collectors.toSet());
        financialClient.updateFinancialInfo(customerId, financialInfo);
    });
    CompletableFuture<Void> updateAddressCF = CompletableFuture.runAsync(() -> {
        Address address = Address.valueOf(request.getAddress());
        addressClient.updateAddressByCustomerId(customerId, address);
    });
    return CompletableFuture.allOf(updateCustomerCF, updateAddressCF, updateFinancialInfoCF);
}
```

该应用程序有一个PATCH API，用于以与PUT API方式略有不同的方式更新客户。[PATCH API将接收与PUT](https://javarevisited.blogspot.com/2016/10/difference-between-put-and-post-in-restful-web-service.html)完全相同的信息，但仅当接收到的值不为空时才会执行更新。首先看一下使用顺序代码的方法：

```java
public void updateCustomer(Integer customerId, UpdateCustomerRequest request) {
    log.info("Updating customer", customerId);
    LocalDateTime startTime = LocalDateTime.now();
    if (request.getPhoneNumber() != null) {
        log.info("Received a phone number, updating customer");
        customerRepository.findById(customerId)
            .ifPresent(customerEntity -> {
                customerEntity.setPhoneNumber(request.getPhoneNumber());
                customerRepository.save(customerEntity);
            });
    }
    if (!CollectionUtils.isEmpty(request.getFinancialInfo())) {
        log.info("Received a financial info, updating it");
        Set<FinancialInfo> financialInfo = request.getFinancialInfo().stream()
            .map(FinancialInfo::valueOf)
            .collect(Collectors.toSet());
        financialClient.updateFinancialInfo(customerId, financialInfo);
    }
    if (request.getAddress() != null) {
        log.info("Received a address, updating it");
        Address address = Address.valueOf(request.getAddress());
        addressClient.updateAddressByCustomerId(customerId, address);
    }
    log.info("Customer updated successfully!");
    log.info("Operation duration {} sec", Duration.between(startTime, LocalDateTime.now()).toSeconds());
}
```

在这里，我们可能会遇到接收到的某些信息等于null的情况。很明显，如果该方法接收到的电话号码为null，则它不需要与数据库通信，因此不需要创建可完成的功能。最后，该方法过滤不等于null的对象，然后使用allOf方法将它们合并。

```java
public CompletableFuture<Void> updateCustomer(Integer customerId, UpdateCustomerRequest request) {
    log.info("Updating customer", customerId);
    CompletableFuture<Void> updateCustomerCF = null;
    if (request.getPhoneNumber() != null) {
        log.info("Received a phone number, updating customer");
        updateCustomerCF = CompletableFuture.runAsync(() -> {
            customerRepository.findById(customerId)
                .ifPresent(customerEntity -> {
                    customerEntity.setPhoneNumber(request.getPhoneNumber());
                    customerRepository.save(customerEntity);
                });
        });
    }
    CompletableFuture<Void> updateFinancialInfoCF = null;
    if (!CollectionUtils.isEmpty(request.getFinancialInfo())) {
        log.info("Received a financial info, updating it");
        updateFinancialInfoCF = CompletableFuture.runAsync(() -> {
            Set<FinancialInfo> financialInfo = request.getFinancialInfo().stream()
                .map(FinancialInfo::valueOf)
                .collect(Collectors.toSet());
            financialClient.updateFinancialInfo(customerId, financialInfo);
        });
    }
    CompletableFuture<Void> updateAddressCF = null;
    if (request.getAddress() != null) {
        log.info("Received a address, updating it");
        updateAddressCF = CompletableFuture.runAsync(() -> {
            Address address = Address.valueOf(request.getAddress());
            addressClient.updateAddressByCustomerId(customerId, address);
        });
    }
    List<CompletableFuture<Void>> completableFutures = Stream.of(updateCustomerCF, updateFinancialInfoCF, updateAddressCF)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    log.info("Customer updated successfully!");
    return CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[completableFutures.size()]));
}
```

## 5. 使用supplyAsync方法

正如我之前提到的，该应用程序有一个API，可以通过其ID返回客户。API的消费者发送id，API返回有关客户、地址、财务信息、购买交易列表和忠诚度计划积分计数的信息。为生成响应，一种方法对数据库执行一次调用，对外部API执行另外四次调用。因为我们有顺序和同步代码，所以延迟甚至比更新和替换API的延迟还要长。

```java
public CustomerResponse getCustomerById(Integer customerId) {
    log.info("Getting customer by id {} ", customerId);
    LocalDateTime startTime = LocalDateTime.now();
    CustomerResponse customerResponse = customerRepository.findById(customerId)
        .map(CustomerResponse::valueOf)
        .map(this::fetchCustomerInfo)
        .orElse(null);
    log.info("Operation duration {} sec", Duration.between(startTime, LocalDateTime.now()).toSeconds());
    return customerResponse;
}

private CustomerResponse fetchCustomerInfo(CustomerResponse customerResponse) {
    Integer customerId = customerResponse.getId();
    AddressResponse addressResponse = addressClient.getAddressByCustomerId(customerId)
        .map(AddressResponse::valueOf)
        .orElse(null);

    List<PurchaseTransactionResponse> purchaseTransactionResponses = Stream.ofNullable(purchaseTransactionClient.getPurchaseTransactionsByCustomerId(customerId))
        .flatMap(Collection::stream)
        .map(PurchaseTransactionResponse::valueOf)
        .collect(Collectors.toList());

    List<FinancialResponse> financialResponses = Stream.ofNullable(financialClient.getFinancialInfoByCustomerId(customerId))
        .flatMap(Collection::stream)
        .map(FinancialResponse::valueOf)
        .collect(Collectors.toList());

    LoyaltyResponse loyaltyResponse = loyaltyClient.getLoyaltyPointsByCustomerId(customerId)
        .map(LoyaltyClientResponse::getPoints)
        .map(LoyaltyResponse::new)
        .orElse(null);

    customerResponse.setAddressResponse(addressResponse);
    customerResponse.setPurchaseTransactions(purchaseTransactionResponses);
    customerResponse.setFinancialResponses(financialResponses);
    customerResponse.setLoyaltyResponse(loyaltyResponse);
    return customerResponse;
}
```

现在我们到了需要从可完成的特征对象返回一些东西的部分。与前面的示例一样，我将每个调用包装在Compleatable功能对象中。这里的区别在于使用的方法。我在这里使用supplyAsync方法，因为与返回void的runAsync不同，supplyAsync返回执行结果。

每个调用都不依赖于前一个调用，所有调用都可以并行执行。但是作为一种方法将所有信息合并到返回给API消费者的一个响应中，我们需要组合所有功能，等待所有功能完成，生成响应，然后返回它。

有几种方法可以做到这一点。首先，我们可以使用thenCombine方法来链接所有可完成的特征对象，如下所示。

```java
public CompletableFuture<CustomerResponse> getCustomerById(Integer customerId) {
    log.info("Getting customer by id {} ", customerId);
    CompletableFuture<Optional<CustomerResponse>> customerResponseCF = CompletableFuture.supplyAsync(
            () -> customerRepository.findById(customerId)
                    .map(CustomerResponse::valueOf));
    CompletableFuture<AddressResponse> addressResponseCF = CompletableFuture.supplyAsync(
            () -> addressClient.getAddressByCustomerId(customerId)
                    .map(AddressResponse::valueOf)
                    .orElse(null));
    CompletableFuture<List<PurchaseTransactionResponse>> purchaseTransactionResponsesCF = CompletableFuture.supplyAsync(
            () -> Stream.ofNullable(purchaseTransactionClient.getPurchaseTransactionsByCustomerId(customerId))
                    .flatMap(Collection::stream)
                    .map(PurchaseTransactionResponse::valueOf)
                    .collect(Collectors.toList()));
    CompletableFuture<List<FinancialResponse>> financialResponsesCF = CompletableFuture.supplyAsync(
            () -> Stream.ofNullable(financialClient.getFinancialInfoByCustomerId(customerId))
                    .flatMap(Collection::stream)
                    .map(FinancialResponse::valueOf)
                    .collect(Collectors.toList()));
    CompletableFuture<LoyaltyResponse> loyaltyResponseCF = CompletableFuture.supplyAsync(
            () -> loyaltyClient.getLoyaltyPointsByCustomerId(customerId)
                    .map(LoyaltyClientResponse::getPoints)
                    .map(LoyaltyResponse::new)
                    .orElse(null));

    CompletableFuture<CustomerResponse> response = customerResponseCF
            .thenCombine(addressResponseCF, (customerResponse, addressResponse) -> {
                customerResponse.ifPresent(cr -> cr.setAddressResponse(addressResponse));
                return customerResponse;
            })
            .thenCombine(purchaseTransactionResponsesCF, (customerResponse, purchaseTransactionResponses) -> {
                customerResponse.ifPresent(cr -> cr.setPurchaseTransactions(purchaseTransactionResponses));
                return customerResponse;
            })
            .thenCombine(financialResponsesCF, (customerResponse, financialResponses) -> {
                customerResponse.ifPresent(cr -> cr.setFinancialResponses(financialResponses));
                return customerResponse;
            })
            .thenCombine(loyaltyResponseCF, (customerResponse, loyaltyResponse) -> {
                customerResponse.ifPresent(cr -> cr.setLoyaltyResponse(loyaltyResponse));
                return customerResponse;
            })
            .thenApply(customerResponse -> customerResponse.orElse(null));
    return response;
}
```

另一种方法是使用allOf方法将所有可完成的特征组合成一个，然后调用thenApply方法。此方法在完成后立即处理可完成功能的结果。它可能看起来有点混乱，因为在thenApply方法中我声明了一个不会被使用的变量，这是因为该方法需要一个Function作为参数。

```java
public CompletableFuture<CustomerResponse> getCustomerByIdUsingAllOf(Integer customerId) {
    log.info("Getting customer by id {} using allOf(...)", customerId);
    CompletableFuture<Optional<CustomerResponse>> customerResponseCF = CompletableFuture.supplyAsync(
            () -> customerRepository.findById(customerId)
                    .map(CustomerResponse::valueOf));
    CompletableFuture<AddressResponse> addressResponseCF = CompletableFuture.supplyAsync(
            () -> addressClient.getAddressByCustomerId(customerId)
                    .map(AddressResponse::valueOf)
                    .orElse(null));
    CompletableFuture<List<PurchaseTransactionResponse>> purchaseTransactionResponsesCF = CompletableFuture.supplyAsync(
            () -> Stream.ofNullable(purchaseTransactionClient.getPurchaseTransactionsByCustomerId(customerId))
                    .flatMap(Collection::stream)
                    .map(PurchaseTransactionResponse::valueOf)
                    .collect(Collectors.toList()));
    CompletableFuture<List<FinancialResponse>> financialResponsesCF = CompletableFuture.supplyAsync(
            () -> Stream.ofNullable(financialClient.getFinancialInfoByCustomerId(customerId))
                    .flatMap(Collection::stream)
                    .map(FinancialResponse::valueOf)
                    .collect(Collectors.toList()));
    CompletableFuture<LoyaltyResponse> loyaltyResponseCF = CompletableFuture.supplyAsync(
            () -> loyaltyClient.getLoyaltyPointsByCustomerId(customerId)
                    .map(LoyaltyClientResponse::getPoints)
                    .map(LoyaltyResponse::new)
                    .orElse(null));
    CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(
            customerResponseCF, addressResponseCF,
            purchaseTransactionResponsesCF, financialResponsesCF, loyaltyResponseCF);
    Optional<CustomerResponse> customerResponseOptional = customerResponseCF.join();
    CompletableFuture<CustomerResponse> responseCF = voidCompletableFuture
            .thenApply(unusedVariable -> {
                        customerResponseOptional.ifPresent(cr -> {
                            AddressResponse addressResponse = addressResponseCF.join();
                            cr.setAddressResponse(addressResponse);

                            List<PurchaseTransactionResponse> purchaseTransactionResponses = purchaseTransactionResponsesCF.join();
                            cr.setPurchaseTransactions(purchaseTransactionResponses);

                            List<FinancialResponse> financialResponses = financialResponsesCF.join();
                            cr.setFinancialResponses(financialResponses);

                            LoyaltyResponse loyaltyResponse = loyaltyResponseCF.join();
                            cr.setLoyaltyResponse(loyaltyResponse);
                        });
                        return customerResponseOptional
                                .orElse(null);
                    }
            );
    return responseCF;
}
```

你可能想知道如何创建新线程。CompletableFuture使用来自全局fork/join the common poll的线程执行任务。更多关于Fork/Join框架的信息可以在下面找到：

[使用Fork/JoinFramework在Java中进行并行处理硬件在不断发展，因此软件必须跟上它的步伐。多核处理器允许并行数据……medium.com](https://medium.com/javarevisited/parallel-processing-in-java-using-fork-join-framework-4d2861496733)

你可以从GitHub获取代码，运行它并检查同步代码和异步代码之间的区别。项目的资源文件夹中有一个Postman集合，可以很方便的将它们导入到Postman中。你可以使用此集合来执行对API的调用。

要记住的重要一点是使用CompletableFuture的代码将在何处运行。如果它将在Kubernetes等容器化环境中运行，我建议你按照本教程中的说明在本地测试它：

[带有Docker和Kubernetes的Java多线程如今，我们有幸在配备多核处理器的计算机上运行程序。结果，应用程序运行……medium.com](https://medium.com/javarevisited/java-multithreading-with-docker-and-kubernetes-5f7fae67f8d8)

本教程不涉及CompletableFuture执行期间可能发生的异常处理。请继续关注下一个教程，我也会尝试介绍它。

[在我在LinkedIn上发布本教程后，我得到了AlejandroDeulofeuFleites](https://www.linkedin.com/in/alejandro-deulofeu-fleites-b36470138/)的建议。他问我为什么不让API调用100%异步并执行对join方法的调用。我认为他的观点是正确的，我想在这里做进一步的澄清。所以最好的办法是让服务中的异步方法返回CompletableFuture而根本不执行join调用，将CompletableFuture对象返回给API调用者。这样整个API调用将是异步的。

## 6. 总结

本教程是关于可以使用Java CompletableFuture在Spring Boot中实现的异步代码。这是一个很大的话题，不可能只在一个教程中包含所有内容。但与此同时，本教程提供了足够的材料来开始使用CompletableFuture。