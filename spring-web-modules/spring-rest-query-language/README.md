## Spring REST Query Language

本模块包含有关使用Spring的REST查询语言的文章

## 相关文章

+ [使用Spring和JPA Criteria的REST查询语言](docs/使用Spring和JPA-Criteria的REST查询语言.md)
+ [使用Spring Data JPA Criteria的REST查询语言](docs/使用SpringDataJPA-Criteria的REST查询语言.md)
+ [使用Spring Data JPA和Querydsl的REST查询语言](docs/使用SpringDataJPA和Querydsl的REST查询语言.md)
+ [REST查询语言-高级搜索操作](docs/REST查询语言-高级搜索操作.md)
+ [使用RSQL的REST查询语言](docs/使用RSQL的REST查询语言.md)
+ [REST查询语言-实现OR运算](docs/REST查询语言-实现或运算.md)

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