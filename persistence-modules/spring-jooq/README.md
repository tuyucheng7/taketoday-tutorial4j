## Spring jOOQ

本模块包含有关使用jOOQ的Spring的文章

## 相关文章

+ [Jooq与Spring简介](docs/Jooq与Spring简介.md)
+ [jOOQ中的计数查询](docs/jOOQ中的计数查询.md)
+ [jOOQ的Spring Boot支持](docs/jOOQ的SpringBoot支持.md)

In order to fix the error "Plugin execution not covered by lifecycle configuration: org.jooq:jooq-codegen-maven:3.7.3:
generate (execution: default, phase: generate-sources)", right-click on the error message and choose "Mark goal
generated as ignore in pom.xml". Until version 1.4.x, the maven-plugin-plugin was covered by the default lifecycle
mapping that ships with m2e.

Since version 1.5.x, the m2e default lifecycle mapping no longer covers the maven-plugin-plugin.