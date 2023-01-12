## 1. 概述

在本教程中，我们将处理需要在 Spring 应用程序的内部实体与发布回客户端的外部[DTO (数据传输对象)之间发生的转换。](https://www.baeldung.com/java-dto-pattern)

## 延伸阅读：

## [Spring 的 RequestBody 和 ResponseBody 注解](https://www.baeldung.com/spring-request-response-body)

了解 Spring @RequestBody 和 @ResponseBody 注解。

[阅读更多](https://www.baeldung.com/spring-request-response-body)→

## [MapStruct 快速指南](https://www.baeldung.com/mapstruct)

使用 MapStruct 的快速实用指南

[阅读更多](https://www.baeldung.com/mapstruct)→

## 2.模型映射器

让我们首先介绍我们将用于执行此实体-DTO 转换的主要库[ModelMapper](http://modelmapper.org/getting-started/)。

我们将在pom.xml中需要这种依赖：

```xml
<dependency>
    <groupId>org.modelmapper</groupId>
    <artifactId>modelmapper</artifactId>
    <version>3.1.0</version>
</dependency>
```

要检查此库是否有更新版本，[请转到此处](https://search.maven.org/classic/#search|gav|1|g%3A"org.modelmapper" AND a%3A"modelmapper")。

然后我们将在 Spring 配置中定义ModelMapper bean：

```java
@Bean
public ModelMapper modelMapper() {
    return new ModelMapper();
}
```

## 3.DTO

接下来介绍一下这个双面问题的DTO方面，Post DTO：

```java
public class PostDto {
    private static final SimpleDateFormat dateFormat
      = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private Long id;

    private String title;

    private String url;

    private String date;

    private UserDto user;

    public Date getSubmissionDateConverted(String timezone) throws ParseException {
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        return dateFormat.parse(this.date);
    }

    public void setSubmissionDate(Date date, String timezone) {
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        this.date = dateFormat.format(date);
    }

    // standard getters and setters
}

```

请注意，两个自定义日期相关方法处理客户端和服务器之间来回的日期转换：

-   getSubmissionDateConverted()方法将日期字符串转换为服务器时区中的日期，以便在持久化的Post实体中使用它
-   setSubmissionDate()方法是将 DTO 的日期设置为当前用户时区的Post日期

## 4.服务层

现在让我们看一下服务级别操作，它显然适用于实体(而不是 DTO)：

```java
public List<Post> getPostsList(
  int page, int size, String sortDir, String sort) {
 
    PageRequest pageReq
     = PageRequest.of(page, size, Sort.Direction.fromString(sortDir), sort);
 
    Page<Post> posts = postRepository
      .findByUser(userService.getCurrentUser(), pageReq);
    return posts.getContent();
}
```

接下来我们将看一下服务之上的层，即控制器层。这是转换实际发生的地方。

## 5.控制器层

接下来让我们检查一个标准的控制器实现，为Post资源公开简单的 REST API。

我们将在这里展示几个简单的 CRUD 操作：创建、更新、获取一个和获取所有。鉴于操作非常简单，我们对 Entity-DTO 转换方面特别感兴趣：

```java
@Controller
class PostRestController {

    @Autowired
    private IPostService postService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @ResponseBody
    public List<PostDto> getPosts(...) {
        //...
        List<Post> posts = postService.getPostsList(page, size, sortDir, sort);
        return posts.stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PostDto createPost(@RequestBody PostDto postDto) {
        Post post = convertToEntity(postDto);
        Post postCreated = postService.createPost(post));
        return convertToDto(postCreated);
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public PostDto getPost(@PathVariable("id") Long id) {
        return convertToDto(postService.getPostById(id));
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updatePost(@PathVariable("id") Long id, @RequestBody PostDto postDto) {
        if(!Objects.equals(id, postDto.getId())){
            throw new IllegalArgumentException("IDs don't match");
        }
        Post post = convertToEntity(postDto);
        postService.updatePost(post);
    }
}
```

这是我们从Post实体到PostDto的转换：

```java
private PostDto convertToDto(Post post) {
    PostDto postDto = modelMapper.map(post, PostDto.class);
    postDto.setSubmissionDate(post.getSubmissionDate(), 
        userService.getCurrentUser().getPreference().getTimezone());
    return postDto;
}
```

这是从 DTO 到实体的转换：

```java
private Post convertToEntity(PostDto postDto) throws ParseException {
    Post post = modelMapper.map(postDto, Post.class);
    post.setSubmissionDate(postDto.getSubmissionDateConverted(
      userService.getCurrentUser().getPreference().getTimezone()));
 
    if (postDto.getId() != null) {
        Post oldPost = postService.getPostById(postDto.getId());
        post.setRedditID(oldPost.getRedditID());
        post.setSent(oldPost.isSent());
    }
    return post;
}
```

正如我们所见，在模型映射器的帮助下，转换逻辑快速而简单。我们正在使用映射器的地图API，并在不编写一行转换逻辑的情况下转换数据。

## 6. 单元测试

最后，让我们做一个非常简单的测试来确保实体和 DTO 之间的转换工作正常：

```java
public class PostDtoUnitTest {

    private ModelMapper modelMapper = new ModelMapper();

    @Test
    public void whenConvertPostEntityToPostDto_thenCorrect() {
        Post post = new Post();
        post.setId(1L);
        post.setTitle(randomAlphabetic(6));
        post.setUrl("www.test.com");

        PostDto postDto = modelMapper.map(post, PostDto.class);
        assertEquals(post.getId(), postDto.getId());
        assertEquals(post.getTitle(), postDto.getTitle());
        assertEquals(post.getUrl(), postDto.getUrl());
    }

    @Test
    public void whenConvertPostDtoToPostEntity_thenCorrect() {
        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setTitle(randomAlphabetic(6));
        postDto.setUrl("www.test.com");

        Post post = modelMapper.map(postDto, Post.class);
        assertEquals(postDto.getId(), post.getId());
        assertEquals(postDto.getTitle(), post.getTitle());
        assertEquals(postDto.getUrl(), post.getUrl());
    }
}
```

## 七、总结

在本文中，我们通过使用模型映射器库而不是手动编写这些转换，详细介绍了在 Spring REST API 中简化从实体到 DTO 以及从 DTO 到实体的转换。