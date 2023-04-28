## 相关文章

+ [使用SendGrid和Spring Boot发送电子邮件](docs/使用SendGrid和SpringBoot发送电子邮件.md)

## 运行项目

1. 打开`/src/main/resources`中的`application.properties`文件，并将值替换为你的`API key`和你的模板ID
2. 打开`MailController`类并替换电子邮件地址
3. 使用`mvn clean package`构建项目
4. 使用`mvn spring-boot:run`运行此项目
5. 访问[http://localhost:8080/sendgrid?msg=HelloWorld](http://localhost:8080/sendgrid?msg=HelloWorld)发送电子邮件