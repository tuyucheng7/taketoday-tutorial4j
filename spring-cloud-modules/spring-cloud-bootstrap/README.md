## Guide to Microservices

本模块包含有关引导Spring Cloud应用程序的文章，这些文章是微服务指南：使用Spring Boot和Spring Cloud电子书的一部分。

## 相关文章

+ [Spring Cloud Bootstrap](docs/SpringCloud-Bootstrap.md)
+ [Spring Cloud-保护服务](docs/SpringCloud-保护服务.md)
+ [Spring Cloud-使用Zipkin跟踪服务](docs/SpringCloud-使用Zipkin跟踪服务.md)
+ [Spring Cloud系列-网关模式](docs/SpringCloud系列-网关模式.md)
+ [Spring Cloud-添加Angular 4](docs/SpringCloud-添加Angular4.md)
+ [如何跨微服务共享DTO](docs/如何跨微服务共享DTO.md)

## 运行项目

- 首先，你需要在默认端口上运行一个redis服务器
- 运行项目：
  - 将application-config文件夹复制到Windows上的c:\Users\\{username}\或*nix上的/home/{username}/。然后在application-config中打开一个git bash终端并运行：
    - git init
    - git add .
    - git commit -m "First commit"
  - 启动config服务器
  - 启动discovery服务器
  - 以任何顺序启动所有其他服务器(gateway，svc-book，svc-rating，zipkin)