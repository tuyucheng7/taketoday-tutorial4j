## 1. 概述

在本文中，我们介绍Spring Data REST中的投影和摘录概念。

我们将学习**如何使用投影创建模型的自定义视图，以及如何使用摘录作为资源集合的默认视图**。

## 2. 域模型

首先定义我们的域模型：Book和Author。

下面是Book实体类：

```java
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;
    
    private String isbn;

    @ManyToMany(mappedBy = "books", fetch = FetchType.EAGER)
    private List<Author> authors;
}
```

以及Author：

```java
@Entity
public class Author {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "book_author",
            joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "author_id", referencedColumnName = "id"))
    private List<Book> books;
}
```

这两个实体具有多对多的关系。

接下来，我们为每个模型定义标准的Spring Data REST Repository：

```java
public interface BookRepository extends CrudRepository<Book, Long> {}
public interface AuthorRepository extends CrudRepository<Author, Long> {}
```

现在，我们可以访问Book端点http://localhost:8080/books/{id}并传递其id获取特定Book的详细信息：

```json
{
    "title": "Animal Farm",
    "isbn": "978-1943138425",
    "_links": {
        "self": {
            "href": "http://localhost:8080/books/1"
        },
        "book": {
            "href": "http://localhost:8080/books/1"
        },
        "authors": {
            "href": "http://localhost:8080/books/1/authors"
        }
    }
}
```

请注意，由于Author模型有其Repository，因此Author详细信息不属于响应的一部分。不过我们可以通过http://localhost:8080/books/1/authors得到Author的详细信息。

## 3. 创建投影

有时，**我们只对实体属性的子集或自定义视图感兴趣**。对于这种情况，我们可以使用投影。让我们使用Spring Data REST投影为我们的Book创建一个自定义视图，首先创建一个名为CustomBook的简单投影：

```java
@Projection(name = "customBook", types = {Book.class }) 
public interface CustomBook { 
    String getTitle();
}
```

请注意，我们的**投影被定义为带有@Projection注解的接口**。我们可以使用name属性来自定义投影的名称，也可以使用types属性定义它所应用的对象。

在我们的示例中，CustomBook投影将仅包含Book的title属性：

```json
{
    "title": "Animal Farm",
    "isbn": "978-1943138425",
    "_links": {
        "self": {
            "href": "http://localhost:8080/books/1"
        },
        "book": {
            "href": "http://localhost:8080/books/1{?projection}",
            "templated": true
        },
        "authors": {
            "href": "http://localhost:8080/books/1/authors"
        }
    }
}
```

我们可以看到访问投影的链接，下面访问我们在http://localhost:8080/books/1?projection=customBook创建的视图：

```json
{
    "title": "Animal Farm",
    "_links": {
        "self": {
            "href": "http://localhost:8080/books/1"
        },
        "book": {
            "href": "http://localhost:8080/books/1{?projection}",
            "templated": true
        },
        "authors": {
            "href": "http://localhost:8080/books/1/authors"
        }
    }
}
```

在这里，返回的JSON体中只包含title属性，而isbn不再出现在自定义视图中。

一般来说，我们可以在http://localhost:8080/books/1?projection={projection name}访问投影结果。

另外，请注意我们需要在与我们的模型相同的包中定义我们的投影。或者，我们可以使用RepositoryRestConfigurerAdapter显式添加它：

```java
@Configuration
public class RestConfig implements RepositoryRestConfigurer {
 
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration repositoryRestConfiguration, CorsRegistry cors) {
        repositoryRestConfiguration.getProjectionConfiguration().addProjection(CustomBook.class);
    }
}
```

## 4. 向投影中添加新数据

现在，让我们看看如何将新数据添加到我们的投影中。正如我们在上一节中讨论的那样，我们可以使用投影来选择要包含在视图中的属性。更重要的是，我们还可以添加原始视图中不包含的数据。

### 4.1 隐藏数据

默认情况下，原始资源视图中不包含id。要在结果中返回id的值，我们可以显式包含id字段：

```java
@Projection(name = "customBook", types = { Book.class }) 
public interface CustomBook {
    @Value("#{target.id}")
    long getId(); 
    
    String getTitle();
}
```

现在访问http://localhost:8080/books/1?projection={projection name}得到的结果将是：

```json
{
    "id": 1,
    "title": "Animal Farm",
    "_links": {
        ...
    }
}
```

请注意，我们还可以包含使用@JsonIgnore从原始视图中隐藏的数据。

### 4.2 计算数据

我们还可以包括根据我们的资源属性计算的新数据。例如，我们可以在我们的投影中包含Author对象的数量：

```java
@Projection(name = "customBook", types = { Book.class }) 
public interface CustomBook {
 
    @Value("#{target.id}")
    long getId(); 
    
    String getTitle();
        
    @Value("#{target.getAuthors().size()}")
    int getAuthorCount();
}
```

同样，下面是访问http://localhost:8080/books/1?projection=customBook返回的结果：

```json
{
    "id": 1,
    "title": "Animal Farm",
    "authorCount": 1,
    "_links": {
        ...
    }
}
```

### 4.3 轻松访问相关资源

最后，如果我们通常需要访问相关资源，比如在我们的例子中是一本书的作者，我们可以通过显式地包含它来避免额外的请求：

```java
@Projection(name = "customBook", types = { Book.class }) 
public interface CustomBook {
 
    @Value("#{target.id}")
    long getId(); 
    
    String getTitle();
    
    List<Author> getAuthors();
    
    @Value("#{target.getAuthors().size()}")
    int getAuthorCount();
}
```

最终的投影输出将是：

```json
{
    "id": 1,
    "title": "Animal Farm",
    "authors": [
        {
            "name": "George Orwell"
        }
    ],
    "authorCount": 1,
    "_links": {
        "self": {
            "href": "http://localhost:8080/books/1"
        },
        "book": {
            "href": "http://localhost:8080/books/1{?projection}",
            "templated": true
        },
        "authors": {
            "href": "http://localhost:8080/books/1/authors"
        }
    }
}
```

## 5. 摘录

**摘录是我们将其作为默认视图应用于资源集合的投影**。

下面我们自定义BookRepository以自动将customBook Projection用于收集响应。为此，我们使用@RepositoryRestResource注解的excerptProjection属性：

```java
@RepositoryRestResource(excerptProjection = CustomBook.class)
public interface BookRepository extends CrudRepository<Book, Long> {}
```

现在我们可以通过调用http://localhost:8080/books来确保customBook是books集合的默认视图：

```json
{
    "_embedded": {
        "books": [
            {
                "id": 1,
                "title": "Animal Farm",
                "authors": [
                    {
                        "name": "George Orwell"
                    }
                ],
                "authorCount": 1,
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/books/1"
                    },
                    "book": {
                        "href": "http://localhost:8080/books/1{?projection}",
                        "templated": true
                    },
                    "authors": {
                        "href": "http://localhost:8080/books/1/authors"
                    }
                }
            }
        ]
    },
    "_links": {
        "self": {
            "href": "http://localhost:8080/books"
        },
        "profile": {
            "href": "http://localhost:8080/profile/books"
        }
    }
}
```

这同样适用于通过http://localhost:8080/authors/1/books访问特定作者的书籍：

```json
{
    "_embedded": {
        "books": [
            {
                "id": 1,
                "authors": [
                    {
                        "name": "George Orwell"
                    }
                ],
                "authorCount": 1,
                "title": "Animal Farm",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/books/1"
                    },
                    "book": {
                        "href": "http://localhost:8080/books/1{?projection}",
                        "templated": true
                    },
                    "authors": {
                        "href": "http://localhost:8080/books/1/authors"
                    }
                }
            }
        ]
    },
    "_links": {
        "self": {
            "href": "http://localhost:8080/authors/1/books"
        }
    }
}
```

如前所述，摘录仅自动应用于集合资源。对于单个资源，我们必须使用前面部分所示的projection参数。这是因为如果我们将投影应用为单个资源的默认视图，那么很难知道如何从局部视图更新资源。

最后一点，**重要的是要记住投影和摘录是只读的**。

## 6. 总结

在本文中，我们介绍了如何使用Spring Data REST投影来创建我们模型的自定义视图，并学习了如何使用摘录作为资源集合的默认视图。