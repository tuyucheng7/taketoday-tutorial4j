## Spring Boot Property Expansion

此模块包含有关Spring Boot属性扩展的文章

## 相关文章

+ [使用Spring Boot自动扩展属性](docs/使用SpringBoot自动扩展属性.md)

## 子模块

### property-exp-default-config

该模块包含标准的Maven Spring Boot构建和与Maven兼容的Gradle构建。

要执行Maven示例，请运行以下命令：

`mvn spring-boot:run`

要执行Gradle示例，请执行以下操作：

`gradle bootRun`

### property-exp-custom-config

此项目未使用标准的Spring Boot父级，而是手动配置的。运行以下命令：

`mvn exec:java`