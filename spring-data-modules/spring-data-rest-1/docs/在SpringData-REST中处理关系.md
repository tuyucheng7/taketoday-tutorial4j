##  1. 概述

在本教程中，我们学习**如何在Spring Data REST中处理实体之间的关系**，重点关注Spring Data REST为Repository公开的关联资源，考虑我们可以定义的每种关系类型。

为了避免任何额外的设置，我们使用H2嵌入式数据库作为演示。

## 2. 一对一关系

### 2.1 数据模型

让我们使用@OneToOne注解定义两个实体类Library和Address，它们具有一对一的关系。关联由关联的Library端拥有：

```java
@Entity
public class Library {

    @Id
    @GeneratedValue
    private long id;

    @Column
    private String name;

    @OneToOne
    @JoinColumn(name = "address_id")
    @RestResource(path = "libraryAddress", rel="address")
    private Address address;
    
    // standard constructor, getters, setters
}
```

```java
@Entity
public class Address {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String location;

    @OneToOne(mappedBy = "address")
    private Library library;

    // standard constructor, getters, setters
}
```

@RestResource注解是可选的，我们可以用它来自定义端点。**我们还必须注意为每个关联资源使用不同的名称**。否则，我们会得到JsonMappingException异常，异常消息提示为“检测到具有相同关系类型的多个关联链接！消除关联的歧义。”

关联名默认为属性名，我们可以使用@RestResource注解的rel属性自定义：

```java
@OneToOne
@JoinColumn(name = "secondary_address_id")
@RestResource(path = "libraryAddress", rel="address")
private Address secondaryAddress;
```

如果我们要将上面的secondaryAddress属性添加到Library类，我们将有两个名为address的资源，因此会遇到冲突。我们可以通过为rel属性指定一个不同的值，或者通过省略RestResource注解来解决这个问题，这样资源名称默认为secondaryAddress。

### 2.2 Repository

为了将这些实体公开为资源，我们将通过扩展CrudRepository接口为它们创建Repository接口：

```java
public interface LibraryRepository extends CrudRepository<Library, Long> {}
```

```java
public interface AddressRepository extends CrudRepository<Address, Long> {}
```

### 2.3 创建资源

首先，我们添加一个稍后使用的Library实例：

```shell
curl -i -X POST -H "Content-Type:application/json" -d '{"name":"My Library"}' http://localhost:8080/libraries
```

然后API返回JSON对象：

```json
{
    "name": "My Library",
    "_links": {
        "self": {
            "href": "http://localhost:8080/libraries/1"
        },
        "library": {
            "href": "http://localhost:8080/libraries/1"
        },
        "address": {
            "href": "http://localhost:8080/libraries/1/libraryAddress"
        }
    }
}
```

请注意，如果我们在Windows上使用curl，则必须转义表示JSON主体的字符串中的双引号字符：

```shell
-d "{\"name\": \"My Library\"}"
```

我们可以在响应正文中看到，关联资源已在libraries/{libraryId}/address端点公开。

**在我们创建关联之前，向该端点发送GET请求将返回一个空对象**。但是，如果我们想添加一个关联，我们必须首先创建一个Address实例：

```bash
curl -i -X POST -H "Content-Type:application/json" -d '{"location":"Main Street nr 5"}' http://localhost:8080/addresses
```

POST请求的结果是一个包含地址记录的JSON对象：

```json
{
    "location": "Main Street nr 5",
    "_links": {
        "self": {
            "href": "http://localhost:8080/addresses/1"
        },
        "address": {
            "href": "http://localhost:8080/addresses/1"
        },
        "library": {
            "href": "http://localhost:8080/addresses/1/library"
        }
    }
}
```

### 2.4 创建关联

**在持久化两个实例之后，我们可以通过使用一个关联资源来建立关系**。

这是使用HTTP方法PUT完成的，它支持text/uri-list的媒体类型，以及包含要绑定到关联的资源URI的主体。

由于Library实体是关联的所有者，因此我们将向Library添加一个Address：

```shell
curl -i -X PUT -d "http://localhost:8080/addresses/1" -H "Content-Type:text/uri-list" http://localhost:8080/libraries/1/libraryAddress
```

如果成功，它将返回204状态码。为了验证这一点，我们可以检查Address的Library关联资源：

```shell
curl -i -X GET http://localhost:8080/addresses/1/library
```

它应该返回名称为“My Library”的Library JSON对象。

要删除关联，我们可以使用DELETE方法调用端点，确保使用关系所有者的关联资源：

```shell
curl -i -X DELETE http://localhost:8080/libraries/1/libraryAddress
```

## 3. 一对多关系

我们使用@OneToMany和@ManyToOne注解定义一对多关系，并可以添加可选的@RestResource注解来自定义关联资源。

### 3.1 数据模型

为了举例说明一对多关系，我们添加一个新的Book实体，它表示与Library实体关系的“多”端：

```java
@Entity
public class Book {

    @Id
    @GeneratedValue
    private long id;
    
    @Column(nullable=false)
    private String title;
    
    @ManyToOne
    @JoinColumn(name="library_id")
    private Library library;
    
    // standard constructor, getter, setter
}
```

然后我们也将关系添加到Library类：

```java
public class Library {
 
    //...
 
    @OneToMany(mappedBy = "library")
    private List<Book> books;
 
    //...
}
```

### 3.2 Repository

我们还需要创建一个BookRepository：

```java
public interface BookRepository extends CrudRepository<Book, Long> { }
```

### 3.3 关联资源

为了将Book对象添加到Library中的books集合，我们需要先使用/books集合资源创建一个Book实例：

```shell
curl -i -X POST -d "{"title": "Book1"}" -H "Content-Type:application/json" http://localhost:8080/books
```

这是POST请求的响应：

```json
{
    "title": "Book1",
    "_links": {
        "self": {
            "href": "http://localhost:8080/books/1"
        },
        "book": {
            "href": "http://localhost:8080/books/1"
        },
        "bookLibrary": {
            "href": "http://localhost:8080/books/1/library"
        }
    }
}
```

在响应正文中，我们可以看到关联端点/books/{bookId}/library已创建。

现在我们通过向包含图书馆资源URI的关联资源发送PUT请求，将Book与我们在上一节中创建的Library相关联：

```shell
curl -i -X PUT -H "Content-Type:text/uri-list" -d "http://localhost:8080/libraries/1" http://localhost:8080/books/1/library
```

我们可以通过在图书馆/书籍关联资源上使用GET方法来验证图书馆中的书籍：

```shell
curl -i -X GET http://localhost:8080/libraries/1/books
```

返回的JSON对象将包含一个书籍数组：

```json
{
    "_embedded": {
        "books": [
            {
                "title": "Book1",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/books/1"
                    },
                    "book": {
                        "href": "http://localhost:8080/books/1"
                    },
                    "bookLibrary": {
                        "href": "http://localhost:8080/books/1/library"
                    }
                }
            }
        ]
    },
    "_links": {
        "self": {
            "href": "http://localhost:8080/libraries/1/books"
        }
    }
}
```

要删除关联，我们可以在关联资源上使用DELETE方法：

```shell
curl -i -X DELETE http://localhost:8080/books/1/library
```

## 4. 多对多关系

我们使用@ManyToMany注解定义多对多关系，我们还可以向其中添加@RestResource。

### 4.1 数据模型

要创建多对多关系的示例，我们将添加一个新的模型类Author，它与Book实体具有多对多关系：

```java
@Entity
public class Author {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "book_author", 
            joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id"), 
            inverseJoinColumns = @JoinColumn(name = "author_id", referencedColumnName = "id"))
    private List<Book> books;

    // standard constructors, getters, setters
}
```

然后我们在Book类中添加关联：

```java
public class Book {
 
    //...
 
    @ManyToMany(mappedBy = "books")
    private List<Author> authors;
 
    //...
}
```

### 4.2 Repository

接下来，我们创建一个Repository接口来管理Author实体：

```java
public interface AuthorRepository extends CrudRepository<Author, Long> { }
```

### 4.3 关联资源

与前面几节一样，我们必须先创建资源，然后才能建立关联。我们通过向/authors集合资源发送POST请求来创建一个Author实例：

```shell
curl -i -X POST -H "Content-Type:application/json" -d "{"name":"author1"}" http://localhost:8080/authors
```

接下来，我们向数据库中添加第二条Book记录：

```shell
curl -i -X POST -H "Content-Type:application/json" -d "{"title":"Book 2"}" http://localhost:8080/books
```

然后我们对Author记录执行GET请求以查看关联URL：

```json
{
    "name": "author1",
    "_links": {
        "self": {
            "href": "http://localhost:8080/authors/1"
        },
        "author": {
            "href": "http://localhost:8080/authors/1"
        },
        "books": {
            "href": "http://localhost:8080/authors/1/books"
        }
    }
}
```

现在，我们可以使用PUT方法通过端点“authors/1/books”在两个Book记录和Author记录之间创建关联，该方法支持媒体类型text/uri-list并且可以接收多个URI。

要发送多个URI，我们必须用换行符分隔它们：

```shell
curl -i -X PUT -H "Content-Type:text/uri-list" --data-binary @uris.txt http://localhost:8080/authors/1/books
```

uris.txt文件包含书籍的URI，每一个单独一行：

```shell
http://localhost:8080/books/1
http://localhost:8080/books/2
```

为了验证这两个Book对象都与Author相关联，我们可以向关联端点发送GET请求：

```shell
curl -i -X GET http://localhost:8080/authors/1/books
```

下面是我们得到的结果：

```json
{
    "_embedded": {
        "books": [
            {
                "title": "Book 1",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/books/1"
                    }
                    //...
                }
            },
            {
                "title": "Book 2",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/books/2"
                    }
                    //...
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

要删除关联，我们可以使用DELETE方法向关联资源的URL发送请求，后跟{bookId}：

```shell
curl -i -X DELETE http://localhost:8080/authors/1/books/1
```

## 5. 使用TestRestTemplate测试端点

让我们创建一个注入TestRestTemplate实例的测试类，并定义我们将使用的常量：

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringDataRestApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
class SpringDataRelationshipsTest {

    @Autowired
    private TestRestTemplate template;

    private static String BOOK_ENDPOINT = "http://localhost:8080/books/";
    private static String AUTHOR_ENDPOINT = "http://localhost:8080/authors/";
    private static String ADDRESS_ENDPOINT = "http://localhost:8080/addresses/";
    private static String LIBRARY_ENDPOINT = "http://localhost:8080/libraries/";

    private static String LIBRARY_NAME = "My Library";
    private static String AUTHOR_NAME = "George Orwell";
}
```

### 5.1 测试一对一关系

我们编写一个测试方法，通过向集合资源发出POST请求来保存Library和Address对象。然后它通过PUT请求将关系保存到关联资源，并验证是否已通过对同一资源的GET请求建立了关系：

```java
@Test
void whenSaveOneToOneRelationship_thenCorrect() {
    Library library = new Library(LIBRARY_NAME);
    template.postForEntity(LIBRARY_ENDPOINT, library, Library.class);
   
    Address address = new Address("Main street, nr 1");
    template.postForEntity(ADDRESS_ENDPOINT, address, Address.class);
    
    HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.add("Content-type", "text/uri-list");
    HttpEntity<String> httpEntity = new HttpEntity<>(ADDRESS_ENDPOINT + "/1", requestHeaders);
    template.exchange(LIBRARY_ENDPOINT + "/1/libraryAddress", HttpMethod.PUT, httpEntity, String.class);

    ResponseEntity<Library> libraryGetResponse= template.getForEntity(ADDRESS_ENDPOINT + "/1/library", Library.class);
    
    assertEquals("library is incorrect",libraryGetResponse.getBody().getName(), LIBRARY_NAME);
}
```

### 5.2 测试一对多关系

现在我们编写一个测试方法来保存一个Library实例和两个Book实例，向每个Book对象的/library关联资源发送一个PUT请求，并验证关系是否已保存：

```java
@Test
void whenSaveOneToManyRelationship_thenCorrect() {
    Library library = new Library(LIBRARY_NAME);
    template.postForEntity(LIBRARY_ENDPOINT, library, Library.class);

    Book book1 = new Book("Dune");
    template.postForEntity(BOOK_ENDPOINT, book1, Book.class);

    Book book2 = new Book("1984");
    template.postForEntity(BOOK_ENDPOINT, book2, Book.class);

    HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.add("Content-Type", "text/uri-list");    
    HttpEntity<String> bookHttpEntity = new HttpEntity<>(LIBRARY_ENDPOINT + "/1", requestHeaders);
    template.exchange(BOOK_ENDPOINT + "/1/library", HttpMethod.PUT, bookHttpEntity, String.class);
    template.exchange(BOOK_ENDPOINT + "/2/library", HttpMethod.PUT, bookHttpEntity, String.class);

    ResponseEntity<Library> libraryGetResponse = template.getForEntity(BOOK_ENDPOINT + "/1/library", Library.class);
    assertEquals("library is incorrect", libraryGetResponse.getBody().getName(), LIBRARY_NAME);
}
```

### 5.3 测试多对多关系

为了测试Book和Author实体之间的多对多关系，我们创建一个测试方法来保存一个Author记录和两个Book记录。

然后，它向/books关联资源发送一个PUT请求，其中包含两个books的URI，并验证关系是否已建立：

```java
@Test
void whenSaveManyToManyRelationship_thenCorrect() {
    Author author1 = new Author(AUTHOR_NAME);
    template.postForEntity(AUTHOR_ENDPOINT, author1, Author.class);

    Book book1 = new Book("Animal Farm");
    template.postForEntity(BOOK_ENDPOINT, book1, Book.class);

    Book book2 = new Book("1984");
    template.postForEntity(BOOK_ENDPOINT, book2, Book.class);

    HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.add("Content-type", "text/uri-list");
    HttpEntity<String> httpEntity = new HttpEntity<>(BOOK_ENDPOINT + "/1n" + BOOK_ENDPOINT + "/2", requestHeaders);
    template.exchange(AUTHOR_ENDPOINT + "/1/books", HttpMethod.PUT, httpEntity, String.class);

    String jsonResponse = template.getForObject(BOOK_ENDPOINT + "/1/authors", String.class);
    JSONObject jsonObj = new JSONObject(jsonResponse).getJSONObject("_embedded");
    JSONArray jsonArray = jsonObj.getJSONArray("authors");
    assertEquals("author is incorrect", jsonArray.getJSONObject(0).getString("name"), AUTHOR_NAME);
}
```

## 6. 总结

在本文中，我们演示了如何使用Spring Data REST来处理不同类型的实体关系。