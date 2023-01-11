## 1. 概述

在本教程中，我们将讨论如何使用MongoDB和Spring Boot上传和检索文件。

我们将对小文件使用MongoDB BSON，对大文件使用GridFS。

## 2. Maven配置

首先，我们将spring-boot-starter-data-mongodb依赖添加到我们的pom.xml中：

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

此外，我们需要spring-boot-starter-web和spring-boot-starter-thymeleaf依赖项来显示我们应用程序的用户界面。

在本教程中，我们使用Spring Boot版本2.6.1。

## 3. Spring Boot属性

接下来，我们将配置必要的Spring Boot属性。

让我们从MongoDB属性开始：

```properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=springboot-mongo
```

我们还将设置Servlet Multipart属性以允许上传大文件：

```properties
spring.servlet.multipart.max-file-size=256MB
spring.servlet.multipart.max-request-size=256MB
spring.servlet.multipart.enabled=true
```

## 4. 上传小文件

现在，我们将讨论如何使用MongoDB BSON上传和检索小文件(大小<16MB)。

在这里，我们有一个简单的Document类 — Photo。我们将图像文件存储在BSON二进制文件中：

```java

@Document(collection = "photos")
public class Photo {
    @Id
    private String id;

    private String title;

    private Binary image;
    // constructors getters, setters and toString ..
}
```

我们还需要一个简单的PhotoRepository：

```java
public interface PhotoRepository extends MongoRepository<Photo, String> {

}
```

现在，对于PhotoService，我们只有两个方法：

+ addPhoto() — 将照片上传到MongoDB
+ getPhoto() — 检索具有给定id的照片

```java

@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepo;

    public Photo getPhoto(String id) {
        return photoRepo.findById(id).get();
    }

    public String addPhoto(String title, MultipartFile file) throws IOException {
        Photo photo = new Photo(title);
        photo.setImage(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
        photo = photoRepo.insert(photo);
        return photo.getId();
    }
}
```

## 5. 上传大文件

现在，我们将使用GridFS上传和检索大文件。

首先，我们将定义一个简单的DTO(Video)来表示一个大文件：

```java
public class Video {
    private String title;
    private InputStream stream;
    // constructors getters, setters and toString ..
}
```

与PhotoService类似，我们拥有一个包含两个方法的VideoService — addVideo()和getVideo()：

```java

@Service
public class VideoService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations operations;

    public Video getVideo(String id) throws IllegalStateException, IOException {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
        Video video = new Video();
        video.setTitle(file.getMetadata().get("title").toString());
        video.setStream(operations.getResource(file).getInputStream());
        return video;
    }

    public String addVideo(String title, MultipartFile file) throws IOException {
        DBObject metaData = new BasicDBObject();
        metaData.put("type", "video");
        metaData.put("title", title);
        ObjectId id = gridFsTemplate.store(file.getInputStream(), file.getName(), file.getContentType(), metaData);
        return id.toString();
    }
}
```

## 6. Controllers

现在，让我们来看看控制器 - PhotoController和VideoController。

### 6.1 PhotoController

首先，我们有PhotoController，它将使用我们的PhotoService添加/获取照片。

我们定义addPhoto()方法来上传和创建新照片：

```java

@Controller
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @PostMapping("/photos/add")
    public String addPhoto(@RequestParam("title") String title, @RequestParam("image") MultipartFile image, Model model) throws IOException {
        String id = photoService.addPhoto(title, image);
        return "redirect:/photos/" + id;
    }
}
```

我们也有getPhoto()来检索具有给定id的照片：

```java
public class PhotoController {

    @GetMapping("/photos/{id}")
    public String getPhoto(@PathVariable String id, Model model) {
        Photo photo = photoService.getPhoto(id);
        model.addAttribute("title", photo.getTitle());
        model.addAttribute("image", Base64.getEncoder().encodeToString(photo.getImage().getData()));
        return "photos";
    }
}
```

请注意，由于我们将图像数据作为byte[]返回，因此我们将其转换为Base64字符串以在前端显示。

## 6.2 VideoController

接下来，让我们看看我们的VideoController。

这将有一个类似的方法addVideo()将视频上传到我们的MongoDB：

```java

@Controller
public class VideoController {

    @Autowired
    private VideoService videoService;

    @PostMapping("/videos/add")
    public String addVideo(@RequestParam("title") String title, @RequestParam("file") MultipartFile file, Model model) throws IOException {
        String id = videoService.addVideo(title, file);
        return "redirect:/videos/" + id;
    }
}
```

在这里，我们使用getVideo()来检索具有给定id的视频：

```java

@Controller
public class VideoController {

    @GetMapping("/videos/{id}")
    public String getVideo(@PathVariable String id, Model model) throws IllegalStateException, IOException {
        Video video = videoService.getVideo(id);
        model.addAttribute("title", video.getTitle());
        model.addAttribute("url", "/videos/stream/" + id);
        return "videos";
    }
}
```

我们还可以添加一个streamVideo()方法，该方法将从Video InputStream创建一个流式URL：

```java

@Controller
public class VideoController {

    @GetMapping("/videos/stream/{id}")
    public void streamVideo(@PathVariable String id, HttpServletResponse response) throws IllegalStateException, IOException {
        Video video = videoService.getVideo(id);
        FileCopyUtils.copy(video.getStream(), response.getOutputStream());
    }
}
```

## 7. 前端

最后，让我们看看我们的前端。让我们从uploadPhoto.html开始，它提供了一个简单的表单来上传图片：

```html

<html lang="en">
<body>
<h1>Upload new Photo</h1>
<form action="/photos/add" enctype="multipart/form-data" method="POST">
    Title:<input name="title" type="text"/>
    <br/>
    Image:<input accept="image/" name="image" type="file"/>
    <br/>
    <br/>
    <input type="submit" value="Upload"/>
</form>
</body>
</html>
```

接下来，我们添加photos.html视图以显示我们的照片：

```html

<html xmlns:th="https://www.thymeleaf.org" lang="en">
<body>
<h1>View Photo</h1>
Title: <span th:text="${title}">name</span>
<br/>
<img alt="sample" th:src="{'data:image/png;base64,'+image}" width="200"/>
<br/> <br/>
<a href="/">Back to home page</a>
</body>
</html>
```

同样，我们有uploadVideo.html来上传视频：

```html

<html lang="en">
<body>
<h1>Upload new Video</h1>
<form action="/videos/add" enctype="multipart/form-data" method="POST">
    Title:<input name="title" type="text"/>
    <br/>
    Video:<input accept="video/" name="file" type="file"/>
    <br/>
    <br/>
    <input type="submit" value="Upload"/>
</form>
</body>
</html>
```

和videos.html来显示视频：

```html

<html xmlns:th="https://www.thymeleaf.org" lang="en">
<body>
<h1>View Video</h1>
Title: <span th:text="${title}">title</span>
<br/>
<br/>
<video controls width="400">
    <source th:src="${url}"/>
</video>
<br/> <br/>
<a href="/">Back to home page</a>
</body>
</html>
```

## 8. 总结

在本文中，我们介绍了如何使用MongoDB和Spring Boot上传和检索文件。我们同时使用BSON和GridFS来上传和检索文件。