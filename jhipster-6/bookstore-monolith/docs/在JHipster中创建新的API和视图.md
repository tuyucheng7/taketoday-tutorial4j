## 1. 概述

[在本教程中，我们将了解如何在JHipster](https://www.baeldung.com/jhipster)应用程序中创建新的 API 。然后我们将该 API 集成到前端显示中。

## 2. 示例应用

对于本教程，我们将使用一个简单的书店应用程序。

这家书店是作为一个整体建造的。它使用[Angular](https://www.baeldung.com/spring-boot-angular-web)作为前端，并有一个名为book的实体，其中包含以下字段：

-   标题
-   作者
-   公布数据
-   价格
-   数量

JHipster 自动生成 API 和前端视图，为一本书提供简单的操作：查看、创建、编辑和删除。

对于本教程，我们将添加一个允许用户购买图书的 API，以及前端调用新 API 的按钮。

我们将只关注购买的 API 和前端方面。我们不会执行任何付款处理，只会执行最少的验证。

## 3. Spring Boot 的变化

JHipster 提供了一个用于创建新[控制器的](https://www.baeldung.com/spring-controllers)[生成器](https://www.jhipster.tech/creating-a-spring-controller/)。但是，对于本教程，我们将手动创建 API 和相关代码。

### 3.1. 资源类

第一步是更新生成的BookResource类。我们添加前端代码将调用的新端点：

```java
@GetMapping("/books/purchase/{id}")
public ResponseEntity<BookDTO> purchase(@PathVariable Long id) {
    Optional<BookDTO> bookDTO = bookService.purchase(id);
    return ResponseUtil.wrapOrNotFound(bookDTO);
}
```

这会创建一个位于/books/purchase/{id}的新 API 端点。唯一的输入是书的id，我们返回一个BookDTO来反映购买后的新库存水平。

### 3.2. 服务接口和类

现在，我们需要更新BookService接口以包含一个新的购买方法：

```java
Optional<BookDTO> purchase(Long id);
```

然后，我们需要在BookServiceImpl类中实现新方法：

```java
@Override
public Optional<BookDTO> purchase(Long id) {
    Optional<BookDTO> bookDTO = findOne(id);
    if (bookDTO.isPresent()) {
        int quantity = bookDTO.get().getQuantity();
        if (quantity > 0) {
            bookDTO.get().setQuantity(quantity - 1);
            Book book = bookMapper.toEntity(bookDTO.get());
            book = bookRepository.save(book);
            return bookDTO;
        }
        else {
            throw new BadRequestAlertException("Book is not in stock", "book", "notinstock");
        }
    }
    return Optional.empty();
}
```

让我们看看这段代码中发生了什么。首先，我们通过id查找这本书 以确认它存在。如果没有，我们返回一个空的[Optional](https://www.baeldung.com/java-optional)。

如果它确实存在，那么我们确保它的库存水平大于零。否则，我们将抛出BadRequestAlertException。虽然此异常通常仅在 JHipster 的 REST 层中使用，但我们在这里使用它来演示如何向前端返回有用的错误消息。

否则，如果库存大于零，那么我们将其减一，将其保存到存储库中，并返回更新后的 DTO。

### 3.3. 安全配置

所需的最后更改是在SecurityConfiguration类中：

```java
.antMatchers("/api/books/purchase/**").authenticated()
```

这确保只有经过身份验证的用户才允许调用我们的新 API。

## 4.前端变化

现在让我们关注前端的变化。JHipster 创建了一个用于显示单本书的视图，我们将在其中添加新的购买按钮。

### 4.1. 服务等级

首先，我们需要在现有的book.service.ts文件中添加一个新方法。这个文件已经包含了操作书籍对象的方法，所以它是为我们的新 API 添加逻辑的好地方：

```javascript
purchase(id: number): Observable<EntityResponseType> {
    return this.http
        .get<IBook>(`${this.resourceUrl}/purchase/${id}`, { observe: 'response' })
        .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
}
```

### 4.2. 成分

然后，我们需要更新book.component.ts中的组件代码。我们将创建一个函数来调用 Angular 图书服务中的新方法，然后监听服务器返回的响应：

```javascript
purchase(id: number) {
    this.bookService.purchase(id).subscribe(
        (res: HttpResponse<IBook>) => {
            this.book = res.body;
        },
        (res: HttpErrorResponse) => console.log(res.message)
    );
}复制
```

### 4.3. 看法

最后，我们可以在 book view 中添加一个按钮来调用组件中的新 purchase 方法：

```html
<button type="button"
             class="btn btn-primary"
             (click)="purchase(book.id)">
    <span>Purchase</span>
</button>复制
```

下图显示了前端的更新视图：

[![jhipster 自定义 api 前端](https://www.baeldung.com/wp-content/uploads/2019/03/jhipster-custom-api-frontend-257x300-1.jpg)](https://www.baeldung.com/wp-content/uploads/2019/03/jhipster-custom-api-frontend-257x300-1.jpg)

单击新的购买按钮将调用我们的新 API，前端将自动更新新的库存状态(如果出现问题则显示错误)。

## 5.总结

在本教程中，我们了解了如何在 JHipster 中创建自定义 API，并将它们集成到前端中。

我们首先将 API 和业务逻辑添加到 Spring Boot 中。然后，我们修改了前端代码以利用新的 API 并显示结果。只需一点努力，我们就能够在 JHipster 自动生成的现有CRUD操作之上添加新功能。
