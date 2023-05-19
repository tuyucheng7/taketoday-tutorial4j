## Spring REST Query Language

本模块包含有关使用Spring的REST查询语言的文章

## 相关文章

+ [使用Spring和JPA Criteria的REST查询语言](http://tu-yucheng.github.io/springweb/2023/05/19/rest-search-language-spring-jpa-criteria.html)
+ [使用Spring Data JPA Criteria的REST查询语言](http://tu-yucheng.github.io/springweb/2023/05/19/rest-api-search-language-spring-data-specifications.html)
+ [使用Spring Data JPA和Querydsl的REST查询语言](http://tu-yucheng.github.io/springweb/2023/05/19/rest-api-search-language-spring-data-querydsl.html)
+ [REST查询语言-高级搜索操作](http://tu-yucheng.github.io/springweb/2023/05/19/rest-api-query-search-language-more-operations.html)
+ [使用RSQL的REST查询语言](http://tu-yucheng.github.io/springweb/2023/05/19/rest-api-search-language-rsql-fiql.html)
+ [REST查询语言-实现OR运算](http://tu-yucheng.github.io/springweb/2023/05/19/rest-api-query-search-or-operation.html)

## 构建项目

```
mvn clean install
```

## 设置MySQL

```
mysql -u root -p 
> CREATE USER 'tutorialuser'@'localhost' IDENTIFIED BY 'tutorialmy5ql';
> GRANT ALL PRIVILEGES ON *.* TO 'tutorialuser'@'localhost';
> FLUSH PRIVILEGES;
```