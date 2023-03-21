## Spring Security OpenID

本模块包含有关OpenID与Spring Security的文章

## 相关文章

+ [Spring Security和OIDC](docs/SpringSecurity和OIDC.md)
+ [Spring Security来自JWT的映射权限](docs/SpringSecurity来自JWT的映射权限.md)

## 运行项目
```
mvn spring-boot:run
```

## 获取Google App Client ID, Secret

- 我们需要通过在[谷歌开发者控制台](https://console.developers.google.com/project/_/apiui/credential?pli=1)创建一个新项目来获取客户端ID和客户端密钥。

- 我们可以按照这些说明在他们的平台上注册我们的客户应用程序。

- 一旦我们有了客户端ID和密钥，我们必须确保将它们添加到application.properties文件中。