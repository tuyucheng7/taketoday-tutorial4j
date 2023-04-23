## 1. 概述

在本教程中，我们将了解使用单例作用域创建的Spring bean如何在幕后工作以提供多个并发请求。
此外，我们将了解Java如何将bean实例存储在内存中以及它如何处理对它们的并发访问。

## 2. Spring Beans和Java堆内存

正如我们所知，Java堆是一个全局共享内存，可供应用程序中所有正在运行的线程访问。
**当Spring容器创建具有单例作用域的bean时，该bean存储在堆中**。这样，所有并发线程都能够指向同一个bean实例。

接下来，让我们了解线程的堆栈内存是什么以及它如何帮助服务并发请求。

## 3. 如何处理并发请求？

举个例子，让我们看一个Spring应用程序，它有一个名为ProductService的单例bean：

```java

@Service
public class ProductService {

    private final static List<Product> productRepository = asList(
            new Product(1, "Product 1", new Stock(100)),
            new Product(2, "Product 2", new Stock(50))
    );

    public Optional<Product> getProductById(int id) {
        Optional<Product> product = productRepository.stream()
                .filter(p -> p.id() == id)
                .findFirst();
        String productName = product.map(Product::name).orElse(null);
        System.out.printf("Thread: %s; bean instance: %s; product id: %s has the name: %s%n", currentThread().getName(), this, id, productName);
        return product;
    }
}

public record Product(int id, String name, Stock stock) {

}

@RestController()
@RequestMapping("product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public Product getProductDetails(@PathVariable("id") int productId) {
        return productService.getProductById(productId).orElse(null);
    }
}
```

这个bean有一个方法getProductById()，它将产品数据返回给调用方。
此外，此bean返回的数据在端点/product/{id}上公开给客户端。

接下来，让我们看看当两个调用同时到达端点/product/{id}时，运行时会发生什么。
具体来说，第一个线程将调用/product/1，第二个线程将调用/product/2。

Spring为每个请求创建一个不同的线程。正如我们在下面的控制台输出中看到的，两个线程都使用相同的ProductService实例来返回产品数据：

```text
Thread: http-nio-8080-exec-1; bean instance: cn.tuyucheng.taketoday.concurrentrequest.ProductService@43c71e4d; product id: 2 has the name: Product 2
Thread: http-nio-8080-exec-2; bean instance: cn.tuyucheng.taketoday.concurrentrequest.ProductService@43c71e4d; product id: 1 has the name: Product 1
```

Spring可以在多个线程中使用同一个bean实例，首先是因为Java会为每个线程创建一个私有堆栈内存。

**栈内存负责存储线程执行期间方法内部使用的局部变量的状态**。这样，Java确保并行执行的线程不会覆盖彼此的变量。

其次，由于ProductService bean在堆级别没有设置任何限制或锁定，
**因此每个线程的程序计数器都能够指向堆内存中bean实例的相同引用**。
因此，两个线程可以同时执行getProductById()方法。

接下来，我们将理解为什么单例bean必须是无状态的。

## 4. 无状态单例bean与有状态单例bean

为了理解无状态单例bean的重要性，让我们看看使用有状态单例bean的副作用是什么。

假设我们将productName变量改为类级别：

```java

@Service
public class ProductService {
    private String productName = null;

    // ...

    public Optional getProductById(int id) {
        // ...
        productName = product.map(Product::getName).orElse(null);
        // ...
    }
}
```

现在，让我们再次调用Service并查看输出：

```text
Thread: http-nio-8080-exec-1; bean instance: cn.tuyucheng.taketoday.concurrentrequest.ProductService@2b8bcfb5 product id: 2 has the name: Product 2
Thread: http-nio-8080-exec-2; bean instance: cn.tuyucheng.taketoday.concurrentrequest.ProductService@2b8bcfb5 product id: 1 has the name: Product 2
```

正如我们所见，对productId 1的调用显示的productName是“Product 2”而不是“Product 1”。
发生这种情况是因为ProductService是有状态的，并且与所有正在运行的线程共享相同的productName变量。

在代码中，使用线程池执行两次mock调用，可以模拟出上面的运行效果。

```java

@SpringBootTest
@AutoConfigureMockMvc
public class ConcurrentRequestUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductController controller;

    @Test
    void givenContextLoads_thenProductControllerIsAvailable() {
        assertThat(controller).isNotNull();
    }

    @Test
    void givenMultipleCallsRunInParallel_thenAllCallsReturn200() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(() -> performCall("/product/1", status().isOk()));
        executor.submit(() -> performCall("/product/2/stock", status().isOk()));
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
    }

    private void performCall(String url, ResultMatcher expect) {
        try {
            this.mockMvc.perform(get(url)).andExpect(expect);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

**为了避免这样的副作用，保持我们的单例bean无状态至关重要**。

## 5. 总结

在本文中，我们了解了Spring中对单例bean的并发访问是如何工作的。
首先，我们知道Java将单例bean存储在堆内存中，不同线程可以从堆访问同一个单例实例。
最后，我们阐明了使用无状态bean重要性，并通过一个例子演示了使用有状态bean可能带来的问题。