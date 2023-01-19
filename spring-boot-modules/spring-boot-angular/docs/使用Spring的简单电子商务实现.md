## 一、电子商务应用概述

在本教程中，我们将实现一个简单的电子商务应用程序。我们将使用[Spring Boot开发一个 API，并使用](https://spring.io/projects/spring-boot)[Angular](https://angular.io/)开发一个使用 API的客户端应用程序 。

基本上，用户将能够从产品列表中向/从购物车添加/删除产品以及下订单。

## 2.后端部分

为了开发 API，我们将使用最新版本的 Spring Boot。我们还使用 JPA 和 H2 数据库来实现事物的持久性。

要了解有关Spring Boot的更多信息， 你可以查看我们的[Spring Boot 系列文章](https://www.baeldung.com/spring-boot) ，如果你想熟悉构建 REST API，请查看[另一个系列](https://www.baeldung.com/rest-with-spring-series)。

### 2.1. Maven 依赖项

让我们准备我们的项目并将所需的依赖项导入到我们的pom.xml中。

我们需要一些核心 [的Spring Boot依赖项](https://search.maven.org/classic/#search|ga|1|g%3A"org.springframework.boot" AND (a%3A"spring-boot-starter-data-jpa" OR a%3A"spring-boot-starter-web"))：

```html
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>2.2.2.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.2.2.RELEASE</version>
</dependency>

```

然后， [H2 数据库](https://search.maven.org/classic/#search|gav|1|g%3A"com.h2database" AND a%3A"h2")：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>1.4.197</version>
    <scope>runtime</scope>
</dependency>
```

最后——[杰克逊图书馆](https://search.maven.org/classic/#search|gav|1|g%3A"com.fasterxml.jackson.datatype" AND a%3A"jackson-datatype-jsr310")：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
    <version>2.9.6</version>
</dependency>
```

我们使用[Spring Initializr](https://start.spring.io/)来快速设置具有所需依赖项的项目。

### 2.2. 设置数据库

尽管我们可以使用Spring Boot开箱即用的内存 H2 数据库，但在开始开发 API 之前，我们仍会进行一些调整。

我们将在我们的application.properties文件中启用 H2 控制台，这样我们就可以实际检查我们的数据库的状态，看看是否一切都像我们预期的那样进行。

此外，在开发时将 SQL 查询记录到控制台可能很有用：

```plaintext
spring.datasource.name=ecommercedb
spring.jpa.show-sql=true

#H2 settings
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

添加这些设置后，我们将能够 使用jdbc:h2:mem:ecommercedb 作为 JDBC URL 和用户sa 在没有密码的情况下访问位于[http://localhost:8080/h2-console的数据库。](http://localhost:8080/h2-console)

### 2.3. 项目结构

该项目将被组织成几个标准包，Angular 应用程序放在前端文件夹中：

```plaintext
├───pom.xml            
├───src
    ├───main
    │   ├───frontend
    │   ├───java
    │   │   └───com
    │   │       └───baeldung
    │   │           └───ecommerce
    │   │               │   EcommerceApplication.java
    │   │               ├───controller 
    │   │               ├───dto  
    │   │               ├───exception
    │   │               ├───model
    │   │               ├───repository
    │   │               └───service
    │   │                       
    │   └───resources
    │       │   application.properties
    │       ├───static
    │       └───templates
    └───test
        └───java
            └───com
                └───baeldung
                    └───ecommerce
                            EcommerceApplicationIntegrationTest.java
```

需要注意的是，repository 包中的所有接口都很简单，并且扩展了 Spring Data 的[CrudRepository](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html)，因此我们将省略此处显示它们。

### 2.4. 异常处理

我们的 API 需要一个异常处理程序，以便正确处理最终的异常。

[你可以在我们的 REST with Spring](https://www.baeldung.com/exception-handling-for-rest-with-spring)错误处理和[REST API 自定义错误消息处理](https://www.baeldung.com/global-error-handler-in-a-spring-rest-api)文章中找到有关该主题的更多详细信息。

在这里，我们关注 ConstraintViolationException和我们的自定义 ResourceNotFoundException：

```java
@RestControllerAdvice
public class ApiExceptionHandler {

    @SuppressWarnings("rawtypes")
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handle(ConstraintViolationException e) {
        ErrorResponse errors = new ErrorResponse();
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            ErrorItem error = new ErrorItem();
            error.setCode(violation.getMessageTemplate());
            error.setMessage(violation.getMessage());
            errors.addError(error);
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @SuppressWarnings("rawtypes")
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorItem> handle(ResourceNotFoundException e) {
        ErrorItem error = new ErrorItem();
        error.setMessage(e.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
```

### 2.5. 产品

[如果你需要更多有关 Spring 持久性的知识， Spring 持久性系列](https://www.baeldung.com/persistence-with-spring-series)中有很多有用的文章。

我们的应用程序将只支持从数据库中读取产品，所以我们需要先添加一些。

让我们创建一个简单的Product类：

```java
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Product name is required.")
    @Basic(optional = false)
    private String name;

    private Double price;

    private String pictureUrl;

    // all arguments contructor 
    // standard getters and setters
}
```

尽管用户没有机会通过应用程序添加产品，但我们将支持在数据库中保存产品以便预填充产品列表。

一个简单的服务就足以满足我们的需求：

```java
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    // productRepository constructor injection

    @Override
    public Iterable<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProduct(long id) {
        return productRepository
          .findById(id)
          .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }
}
```

一个简单的控制器将处理检索产品列表的请求：

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    // productService constructor injection

    @GetMapping(value = { "", "/" })
    public @NotNull Iterable<Product> getProducts() {
        return productService.getAllProducts();
    }
}
```

为了向用户公开产品列表，我们现在需要做的就是将一些产品实际放入数据库中。因此，我们将使用CommandLineRunner类在我们的主应用程序类中创建一个Bean 。

这样，我们将在应用程序启动期间将产品插入数据库：

```java
@Bean
CommandLineRunner runner(ProductService productService) {
    return args -> {
        productService.save(...);
        // more products
}
```

如果我们现在启动我们的应用程序，我们可以通过 http://localhost:8080/api/products检索产品列表。此外，如果我们转到 http://localhost:8080/h2-console 并登录，我们会看到有一个名为PRODUCT的表，其中包含我们刚刚添加的产品。

### 2.6. 订单

在 API 端，我们需要启用 POST 请求以保存最终用户将要下的订单。

让我们首先创建模型：

```java
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateCreated;

    private String status;

    @JsonManagedReference
    @OneToMany(mappedBy = "pk.order")
    @Valid
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @Transient
    public Double getTotalOrderPrice() {
        double sum = 0D;
        List<OrderProduct> orderProducts = getOrderProducts();
        for (OrderProduct op : orderProducts) {
            sum += op.getTotalPrice();
        }
        return sum;
    }

    @Transient
    public int getNumberOfProducts() {
        return this.orderProducts.size();
    }

    // standard getters and setters
}
```

在这里我们应该注意一些事情。当然，最值得注意的事情之一就是要记住更改表的默认名称。由于我们将类命名为Order，因此默认情况下 应创建名为ORDER的表。但是因为这是一个保留的 SQL 字，我们添加了 @Table(name = “orders”)以避免冲突。

此外，我们有两个@Transient方法，它们将返回该订单的总金额和其中的产品数量。两者都代表计算出来的数据，所以不需要存入数据库。

最后，我们有一个 表示订单详细信息的@OneToMany关系。为此，我们需要另一个实体类：

```java
@Entity
public class OrderProduct {

    @EmbeddedId
    @JsonIgnore
    private OrderProductPK pk;

    @Column(nullable = false)
	private Integer quantity;

    // default constructor

    public OrderProduct(Order order, Product product, Integer quantity) {
        pk = new OrderProductPK();
        pk.setOrder(order);
        pk.setProduct(product);
        this.quantity = quantity;
    }

    @Transient
    public Product getProduct() {
        return this.pk.getProduct();
    }

    @Transient
    public Double getTotalPrice() {
        return getProduct().getPrice()  getQuantity();
    }

    // standard getters and setters

    // hashcode() and equals() methods
}
```

我们这里有一个复合主键：

```java
@Embeddable
public class OrderProductPK implements Serializable {

    @JsonBackReference
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // standard getters and setters

    // hashcode() and equals() methods
}
```

这些类并不太复杂，但我们应该注意，在OrderProduct类中，我们将 @JsonIgnore放在主键上。那是因为我们不想序列化主键的Order部分，因为它是多余的。

我们只需要将Product显示给用户，这就是我们使用瞬态getProduct()方法的原因。

接下来我们需要的是一个简单的服务实现：

```java
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    // orderRepository constructor injection

    @Override
    public Iterable<Order> getAllOrders() {
        return this.orderRepository.findAll();
    }
	
    @Override
    public Order create(Order order) {
        order.setDateCreated(LocalDate.now());
        return this.orderRepository.save(order);
    }

    @Override
    public void update(Order order) {
        this.orderRepository.save(order);
    }
}
```

和一个映射到/api/orders的控制器来处理订单请求。

最重要的是create ()方法：

```java
@PostMapping
public ResponseEntity<Order> create(@RequestBody OrderForm form) {
    List<OrderProductDto> formDtos = form.getProductOrders();
    validateProductsExistence(formDtos);
    // create order logic
    // populate order with products

    order.setOrderProducts(orderProducts);
    this.orderService.update(order);

    String uri = ServletUriComponentsBuilder
      .fromCurrentServletMapping()
      .path("/orders/{id}")
      .buildAndExpand(order.getId())
      .toString();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Location", uri);

    return new ResponseEntity<>(order, headers, HttpStatus.CREATED);
}
```

首先，我们接受具有相应数量的产品清单。之后，我们检查数据库中是否存在所有产品，然后创建并保存一个新订单。我们保留对新创建对象的引用，以便我们可以向其添加订单详细信息。

最后，我们创建一个“位置”标题。

详细的实现在存储库中——本文末尾提到了指向它的链接。

## 3.前端

现在我们已经构建了Spring Boot应用程序，是时候移动项目的Angular部分了。为此，我们首先必须使用 NPM 安装 [Node.js](https://nodejs.org/en/)，然后 安装[Angular CLI](https://cli.angular.io/)，Angular 的命令行界面。

正如我们在官方文档中看到的那样，安装它们真的很容易。

### 3.1. 设置Angular项目

正如我们提到的，我们将使用Angular CLI来创建我们的应用程序。为了简单起见并将所有内容放在一个地方，我们将把我们的Angular应用程序保存在/src/main/frontend文件夹中。

要创建它，我们需要在/src/main文件夹中打开一个终端(或命令提示符)并运行：

```plaintext
ng new frontend
```

这将创建我们Angular应用程序所需的所有文件和文件夹。在文件pakage.json中，我们可以检查安装了哪些版本的依赖项。本教程基于Angularv6.0.3，但旧版本应该可以胜任，至少是 4.3 和更新版本(我们在这里使用的HttpClient是在Angular4.3 中引入的)。

我们应该注意，除非另有说明，否则我们将从/frontend文件夹运行所有命令。

此设置足以通过运行ng serve命令启动Angular应用程序。默认情况下，它在 [http://localhost:4200](http://localhost:4200/)上运行 ，如果我们现在去那里，我们将看到基本的Angular应用程序已加载。

### 3.2. 添加引导程序

在我们继续创建我们自己的组件之前，让我们首先将Bootstrap添加到我们的项目中，这样我们就可以使我们的页面看起来更漂亮。

我们只需要几件事就可以实现这一目标。首先，我们需要 运行一个命令来安装它：

```plaintext
npm install --save bootstrap
```

然后告诉Angular实际使用它。为此，我们需要打开文件 src/main/frontend/angular.json并在“styles”属性下 添加 node_modules/bootstrap/dist/css/bootstrap.min.css 。就是这样。

### 3.3. 组件和模型

在我们开始为我们的应用程序创建组件之前，让我们首先检查一下我们的应用程序的实际外观：

[![电子商务](https://www.baeldung.com/wp-content/uploads/2018/08/ecommerce.png)](https://www.baeldung.com/wp-content/uploads/2018/08/ecommerce.png)

现在，我们将创建一个名为ecommerce的基础组件：

```bash
ng g c ecommerce
```

这将在/frontend/src/app文件夹中创建我们的组件。要在应用程序启动时加载它，我们将把 它包含 到app.component.html 中：

```html
<div class="container">
    <app-ecommerce></app-ecommerce>
</div>
```

接下来，我们将在这个基础组件中创建其他组件：

```plaintext
ng g c /ecommerce/products
ng g c /ecommerce/orders
ng g c /ecommerce/shopping-cart
```

当然，如果愿意，我们可以手动创建所有这些文件夹和文件，但在那种情况下，我们需要记住在我们的AppModule中注册这些组件。

我们还需要一些模型来轻松操作我们的数据：

```javascript
export class Product {
    id: number;
    name: string;
    price: number;
    pictureUrl: string;

    // all arguments constructor
}
export class ProductOrder {
    product: Product;
    quantity: number;

    // all arguments constructor
}
export class ProductOrders {
    productOrders: ProductOrder[] = [];
}
```

提到的最后一个模型与我们后端的OrderForm相匹配。

### 3.4. 基本组件

在我们的电子商务组件的顶部，我们将在右侧放置一个带有主页链接的导航栏：

```html
<nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
    <div class="container">
        <a class="navbar-brand" href="#">Baeldung Ecommerce</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" 
          data-target="#navbarResponsive" aria-controls="navbarResponsive" 
          aria-expanded="false" aria-label="Toggle navigation" 
          (click)="toggleCollapsed()">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div id="navbarResponsive" 
            [ngClass]="{'collapse': collapsed, 'navbar-collapse': true}">
            <ul class="navbar-nav ml-auto">
                <li class="nav-item active">
                    <a class="nav-link" href="#" (click)="reset()">Home
                        <span class="sr-only">(current)</span>
                    </a>
                </li>
            </ul>
        </div>
    </div>
</nav>
```

我们还将从这里加载其他组件：

```html
<div class="row">
    <div class="col-md-9">
        <app-products #productsC [hidden]="orderFinished"></app-products>
    </div>
    <div class="col-md-3">
        <app-shopping-cart (onOrderFinished)=finishOrder($event) #shoppingCartC 
          [hidden]="orderFinished"></app-shopping-cart>
    </div>
    <div class="col-md-6 offset-3">
        <app-orders #ordersC [hidden]="!orderFinished"></app-orders>
    </div>
</div>
```

我们应该记住，为了从我们的组件中看到内容，因为我们正在使用navbar类，我们需要向app.component.css添加一些 CSS ：

```css
.container {
    padding-top: 65px;
}
```

在我们评论最重要的部分之前，让我们检查一下.ts文件：

```javascript
@Component({
    selector: 'app-ecommerce',
    templateUrl: './ecommerce.component.html',
    styleUrls: ['./ecommerce.component.css']
})
export class EcommerceComponent implements OnInit {
    private collapsed = true;
    orderFinished = false;

    @ViewChild('productsC')
    productsC: ProductsComponent;

    @ViewChild('shoppingCartC')
    shoppingCartC: ShoppingCartComponent;

    @ViewChild('ordersC')
    ordersC: OrdersComponent;

    toggleCollapsed(): void {
        this.collapsed = !this.collapsed;
    }

    finishOrder(orderFinished: boolean) {
        this.orderFinished = orderFinished;
    }

    reset() {
        this.orderFinished = false;
        this.productsC.reset();
        this.shoppingCartC.reset();
        this.ordersC.paid = false;
    }
}
```

如我们所见，单击主页链接将重置子组件。我们需要从父组件访问子组件内的方法和字段，所以这就是为什么我们保留对子组件的引用并在reset()方法内使用它们的原因。

### 3.5. 服务

为了让兄弟组件相互通信 并从我们的 API 检索数据/向我们的 API 发送数据，我们需要创建一个服务：

```javascript
@Injectable()
export class EcommerceService {
    private productsUrl = "/api/products";
    private ordersUrl = "/api/orders";

    private productOrder: ProductOrder;
    private orders: ProductOrders = new ProductOrders();

    private productOrderSubject = new Subject();
    private ordersSubject = new Subject();
    private totalSubject = new Subject();

    private total: number;

    ProductOrderChanged = this.productOrderSubject.asObservable();
    OrdersChanged = this.ordersSubject.asObservable();
    TotalChanged = this.totalSubject.asObservable();

    constructor(private http: HttpClient) {
    }

    getAllProducts() {
        return this.http.get(this.productsUrl);
    }

    saveOrder(order: ProductOrders) {
        return this.http.post(this.ordersUrl, order);
    }

    // getters and setters for shared fields
}
```

正如我们所注意到的，这里有相对简单的东西。我们正在发出 GET 和 POST 请求以与 API 通信。此外，我们将需要在组件之间共享的数据设置为可观察的，以便我们稍后可以订阅它。

然而，我们需要指出一件事关于与 API 的通信。如果我们现在运行该应用程序，我们将收到 404 并且不会检索任何数据。这样做的原因是，由于我们使用的是相对 URL，Angular 默认会尝试调用 http://localhost:4200/api/products 并且我们的后端应用程序在localhost:8080上运行。

当然，我们可以将 URL 硬编码为 localhost:8080，但这不是我们想要做的事情。相反，当使用不同的域时，我们应该 在/frontend文件夹中创建一个名为proxy-conf.json的文件：

```plaintext
{
    "/api": {
        "target": "http://localhost:8080",
        "secure": false
    }
}
```

然后我们需要打开package.json 并更改scripts.start 属性以匹配：

```plaintext
"scripts": {
    ...
    "start": "ng serve --proxy-config proxy-conf.json",
    ...
  }
```

现在我们只需要记住使用npm start而不是ng serve来启动应用程序。

### 3.6. 产品

在我们的ProductsComponent中，我们将注入我们之前制作的服务并从 API 加载产品列表并将其转换为ProductOrders列表，因为我们想为每个产品附加一个数量字段：

```javascript
export class ProductsComponent implements OnInit {
    productOrders: ProductOrder[] = [];
    products: Product[] = [];
    selectedProductOrder: ProductOrder;
    private shoppingCartOrders: ProductOrders;
    sub: Subscription;
    productSelected: boolean = false;

    constructor(private ecommerceService: EcommerceService) {}

    ngOnInit() {
        this.productOrders = [];
        this.loadProducts();
        this.loadOrders();
    }

    loadProducts() {
        this.ecommerceService.getAllProducts()
            .subscribe(
                (products: any[]) => {
                    this.products = products;
                    this.products.forEach(product => {
                        this.productOrders.push(new ProductOrder(product, 0));
                    })
                },
                (error) => console.log(error)
            );
    }

    loadOrders() {
        this.sub = this.ecommerceService.OrdersChanged.subscribe(() => {
            this.shoppingCartOrders = this.ecommerceService.ProductOrders;
        });
    }
}
```

我们还需要一个选项来将产品添加到购物车或从中删除一个：

```javascript
addToCart(order: ProductOrder) {
    this.ecommerceService.SelectedProductOrder = order;
    this.selectedProductOrder = this.ecommerceService.SelectedProductOrder;
    this.productSelected = true;
}

removeFromCart(productOrder: ProductOrder) {
    let index = this.getProductIndex(productOrder.product);
    if (index > -1) {
        this.shoppingCartOrders.productOrders.splice(
            this.getProductIndex(productOrder.product), 1);
    }
    this.ecommerceService.ProductOrders = this.shoppingCartOrders;
    this.shoppingCartOrders = this.ecommerceService.ProductOrders;
    this.productSelected = false;
}
```

最后，我们将创建一个我们在 3.4 节中提到的reset () 方法：

```javascript
reset() {
    this.productOrders = [];
    this.loadProducts();
    this.ecommerceService.ProductOrders.productOrders = [];
    this.loadOrders();
    this.productSelected = false;
}
```

我们将遍历 HTML 文件中的产品列表并将其显示给用户：

```html
<div class="row card-deck">
    <div class="col-lg-4 col-md-6 mb-4" ngFor="let order of productOrders">
        <div class="card text-center">
            <div class="card-header">
                <h4>{{order.product.name}}</h4>
            </div>
            <div class="card-body">
                <a href="#"><img class="card-img-top" src={{order.product.pictureUrl}} 
                    alt=""></a>
                <h5 class="card-title">${{order.product.price}}</h5>
                <div class="row">
                    <div class="col-4 padding-0" ngIf="!isProductSelected(order.product)">
                        <input type="number" min="0" class="form-control" 
                            [(ngModel)]=order.quantity>
                    </div>
                    <div class="col-4 padding-0" ngIf="!isProductSelected(order.product)">
                        <button class="btn btn-primary" (click)="addToCart(order)"
                                [disabled]="order.quantity <= 0">Add To Cart
                        </button>
                    </div>
                    <div class="col-12" ngIf="isProductSelected(order.product)">
                        <button class="btn btn-primary btn-block"
                                (click)="removeFromCart(order)">Remove From Cart
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
```

我们还将向相应的 CSS 文件添加一个简单的类，这样一切都可以很好地适应：

```css
.padding-0 {
    padding-right: 0;
    padding-left: 1;
}
```

### 3.7. 购物车

在ShoppingCart组件中，我们还将注入该服务。我们将使用它来订阅ProductsComponent中的更改(以通知何时选择将产品放入购物车)，然后更新购物车的内容并相应地重新计算总成本：

```javascript
export class ShoppingCartComponent implements OnInit, OnDestroy {
    orderFinished: boolean;
    orders: ProductOrders;
    total: number;
    sub: Subscription;

    @Output() onOrderFinished: EventEmitter<boolean>;

    constructor(private ecommerceService: EcommerceService) {
        this.total = 0;
        this.orderFinished = false;
        this.onOrderFinished = new EventEmitter<boolean>();
    }

    ngOnInit() {
        this.orders = new ProductOrders();
        this.loadCart();
        this.loadTotal();
    }

    loadTotal() {
        this.sub = this.ecommerceService.OrdersChanged.subscribe(() => {
            this.total = this.calculateTotal(this.orders.productOrders);
        });
    }

    loadCart() {
        this.sub = this.ecommerceService.ProductOrderChanged.subscribe(() => {
            let productOrder = this.ecommerceService.SelectedProductOrder;
            if (productOrder) {
                this.orders.productOrders.push(new ProductOrder(
                    productOrder.product, productOrder.quantity));
            }
            this.ecommerceService.ProductOrders = this.orders;
            this.orders = this.ecommerceService.ProductOrders;
            this.total = this.calculateTotal(this.orders.productOrders);
        });
    }

    ngOnDestroy() {
        this.sub.unsubscribe();
    }
}
```

当订单完成并且我们需要去结账时，我们从这里向父组件发送一个事件。这里还有 reset ()方法：

```javascript
finishOrder() {
    this.orderFinished = true;
    this.ecommerceService.Total = this.total;
    this.onOrderFinished.emit(this.orderFinished);
}

reset() {
    this.orderFinished = false;
    this.orders = new ProductOrders();
    this.orders.productOrders = []
    this.loadTotal();
    this.total = 0;
}
```

HTML 文件很简单：

```html
<div class="card text-white bg-danger mb-3" style="max-width: 18rem;">
    <div class="card-header text-center">Shopping Cart</div>
    <div class="card-body">
        <h5 class="card-title">Total: ${{total}}</h5>
        <hr>
        <h6 class="card-title">Items bought:</h6>

        <ul>
            <li ngFor="let order of orders.productOrders">
                {{ order.product.name }} - {{ order.quantity}} pcs.
            </li>
        </ul>

        <button class="btn btn-light btn-block" (click)="finishOrder()"
             [disabled]="orders.productOrders.length == 0">Checkout
        </button>
    </div>
</div>
```

### 3.8. 订单

我们将使事情尽可能简单，并在OrdersComponent中通过将属性设置为 true 并将订单保存在数据库中来模拟支付。我们可以通过h2-console或点击 http://localhost:8080/api/orders检查订单是否已保存。

我们在这里也需要EcommerceService，以便从购物车中检索产品列表和我们订单的总金额：

```javascript
export class OrdersComponent implements OnInit {
    orders: ProductOrders;
    total: number;
    paid: boolean;
    sub: Subscription;

    constructor(private ecommerceService: EcommerceService) {
        this.orders = this.ecommerceService.ProductOrders;
    }

    ngOnInit() {
        this.paid = false;
        this.sub = this.ecommerceService.OrdersChanged.subscribe(() => {
            this.orders = this.ecommerceService.ProductOrders;
        });
        this.loadTotal();
    }

    pay() {
        this.paid = true;
        this.ecommerceService.saveOrder(this.orders).subscribe();
    }
}
```

最后我们需要向用户显示信息：

```html
<h2 class="text-center">ORDER</h2>
<ul>
    <li ngFor="let order of orders.productOrders">
        {{ order.product.name }} - ${{ order.product.price }} x {{ order.quantity}} pcs.
    </li>
</ul>
<h3 class="text-right">Total amount: ${{ total }}</h3>

<button class="btn btn-primary btn-block" (click)="pay()" ngIf="!paid">Pay</button>
<div class="alert alert-success" role="alert" ngIf="paid">
    <strong>Congratulation!</strong> You successfully made the order.
</div>
```

## 4. 合并Spring Boot和Angular应用程序

我们完成了两个应用程序的开发，像我们一样单独开发它可能更容易。但是，在生产中，只有一个应用程序会方便得多，所以现在让我们合并这两个应用程序。

我们在这里要做的是构建Angular应用程序，它调用 Webpack 来捆绑所有资产并将它们推送到Spring Boot 应用程序的/resources/static目录中。这样，我们就可以运行Spring Boot应用程序并测试我们的应用程序并将所有这些打包并部署为一个应用程序。

为了使这成为可能，我们需要再次打开“ package.json ”，在 scripts 之后添加一些新脚本。构建：

```plaintext
"postbuild": "npm run deploy",
"predeploy": "rimraf ../resources/static/ && mkdirp ../resources/static",
"deploy": "copyfiles -f dist/ ../resources/static",
```

我们正在使用一些我们没有安装的包，所以让我们安装它们：

```plaintext
npm install --save-dev rimraf
npm install --save-dev mkdirp
npm install --save-dev copyfiles
```

rimraf命令将查看 目录并创建一个新目录(实际上是清理它)，而copyfiles 将文件从分发文件夹(Angular 放置所有内容的地方)到我们的静态文件夹中。

现在我们只需要运行npm run build 命令，这应该会运行所有这些命令，最终输出将是我们在 static 文件夹中打包的应用程序。

然后我们在端口 8080 上运行我们的Spring Boot应用程序，在那里访问它并使用Angular应用程序。

## 5.总结

在本文中，我们创建了一个简单的电子商务应用程序。我们使用Spring Boot在后端创建了一个 API，然后我们在使用Angular制作的前端应用程序中使用它。我们演示了如何制作我们需要的组件，让它们相互通信以及从 API 检索数据/向 API 发送数据。

最后，我们展示了如何将两个应用程序合并为一个打包的 web 应用程序，放在静态文件夹中。