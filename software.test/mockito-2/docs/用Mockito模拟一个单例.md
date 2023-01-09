## 1. 概述

在本教程中，我们介绍如何使用[Mockito]() mock单例。

## 2. 项目设置

我们创建一个使用[单例]()的小项目，然后看看如何为使用该单例的类编写测试。

### 2.1 依赖项-JUnit和Mockito

首先，我们添加[JUnit](https://search.maven.org/search?q=g:org.junit.jupiter AND a:junit-jupiter-api)和[Mockito](https://search.maven.org/search?q=g:org.mockito AND a:mockito-core)依赖项：

```xml
<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-api</artifactId>
        <version>5.9.1</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>4.8.1</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 2.2 代码示例

我们创建一个单例的CacheManager类来管理内存缓存：

```java
public class CacheManager {
    private final HashMap<String, Object> map;
    private static CacheManager instance;

    private CacheManager() {
        map = new HashMap<>();
    }

    public static CacheManager getInstance() {
        if (instance == null) {
            instance = new CacheManager();
        }
        return instance;
    }

    public <T> T getValue(String key, Class<T> clazz) {
        return clazz.cast(map.get(key));
    }

    public Object setValue(String key, Object value) {
        return map.put(key, value);
    }
}
```

**为了简单起见，我们使用了更简单的单例实现，而不考虑多线程情况**。

接下来，我们将创建一个ProduceService：

```java
public class ProductService {

    private final ProductDAO productDAO;
    private final CacheManager cacheManager;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
        this.cacheManager = CacheManager.getInstance();
    }

    public Product getProduct(String productName) {
        Product product = cacheManager.getValue(productName, Product.class);
        if (product == null) {
            product = productDAO.getProduct(productName);
        }
        return product;
    }
}
```

getProduct()方法首先检查该值是否存在于缓存中。如果没有，它会调用DAO来获取产品。

我们将为getProduct()方法编写一个测试。如果产品存在于缓存中，该测试将检查是否没有对DAO的调用。为此，我们希望使cacheManager.getValue()方法在被调用时返回一个产品。

由于单例实例是由静态getInstance()方法提供的，因此需要以不同方式对其进行mock和注入。让我们看一下执行此操作的几种方法。

## 3. 使用另一个构造函数的解决方法

一种解决方法是向ProductService添加另一个构造函数，以便轻松注入单例CacheManager的mock实例：

```java
public ProductService(ProductDAO productDAO, CacheManager cacheManager) {
    this.productDAO = productDAO;
    this.cacheManager = cacheManager;
}
```

下面我们编写一个使用此构造函数并使用Mockito mock CacheManager的测试：

```java
@Test
void givenValueExistsInCache_whenGetProduct_thenDAOIsNotCalled() {
    ProductDAO productDAO = mock(ProductDAO.class);
    CacheManager cacheManager = mock(CacheManager.class);
    Product product = new Product("product1", "description");
    
    when(cacheManager.getValue(any(), any())).thenReturn(product);

    ProductService productService = new ProductService(productDAO, cacheManager);
    productService.getProduct("product1");

    verify(productDAO, times(0)).getProduct(any());
}
```

这里需要注意几个要点：

-   我们mock了CacheManager并使用新的构造函数将其注入到ProductService中。
-   我们stub了cacheManager.getValue()方法，以便在调用时返回产品。
-   最后，我们验证了在调用productService.getProduct()方法时没有调用productDao.getProduct()方法。

**这个测试可以正常通过，但这不是推荐的方法。编写测试不应该要求我们在类中创建额外的方法或构造函数**。

接下来，让我们看一下另一种不需要更改被测代码的方法。

## 4. Mockito-inline

mock单例CacheManager的另一种方法是mock静态方法CacheManager.getInstance()。**默认情况下，Mockito-core不支持mock静态方法。但是，我们可以通过启用Mockito-inline扩展来mock静态方法**。

### 4.1 启用Mockito-inline

使用Mockito启用[mock静态方法]()的一种方法是添加[Mockito-inline](https://search.maven.org/search?q=g:org.mockito AND a:mockito-inline)依赖项而不是Mockito-core：

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-inline</artifactId>
    <version>4.8.1</version>
    <scope>test</scope>
</dependency>
```

我们可以使用此依赖项来替换mockito-core。

另一种方法是[激活内联Mock maker](https://www.baeldung.com/mockito-mock-static-methods#configuring-mockito-for-static-methods)。

### 4.2 修改测试

然后，对我们的测试做一些简单的更改：

```java
@Test
void givenValueExistsInCache_whenGetProduct_thenDAOIsNotCalled_mockingStatic() {
    ProductDAO productDAO = mock(ProductDAO.class);
    CacheManager cacheManager = mock(CacheManager.class);
    Product product = new Product("product1", "description");

    try (MockedStatic<CacheManager> cacheManagerMock = mockStatic(CacheManager.class)) {
        cacheManagerMock.when(CacheManager::getInstance).thenReturn(cacheManager);
        when(cacheManager.getValue(any(), any())).thenReturn(product);
        
        ProductService productService = new ProductService(productDAO);
        productService.getProduct("product1");
        
        verify(productDAO, times(0)).getProduct(any());
    }
}
```

在上面的代码中需要注意的几个要点：

-   我们使用方法mockStatic()来创建类CacheManager的mock版本。
-   接下来，我们mock getInstance()方法以返回我们mock的CacheManager实例。
-   我们在mock getInstance()方法后创建了ProductService，当ProductService的构造函数调用getInstance()时，将返回mock的CacheManager实例。

测试应该按预期执行，因为mock的CacheManager返回了产品。

## 5. 总结

在本文中，我们介绍了几种使用Mockito为单例编写单元测试的方法。我们演示了一种基于构造函数的解决方法来传递mock实例；然后我们演示了使用Mockito-inline mock静态getInstance()方法。