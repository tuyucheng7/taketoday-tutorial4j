## 1. 概述

**MyBatis是一个SQL映射器框架，支持将自定义SQL语句和存储过程映射到Java对象**，从而有助于在面向对象的应用程序中使用关系型数据库。

在本教程中，我们将通过示例学习使用mybatis-spring-boot-autoconfigure依赖项和嵌入式数据库在[Spring Boot 3](https://howtodoinjava.com/java/whats-new-spring-6-spring-boot-3/)中配置MyBatis。

## 2. Maven依赖

使用Spring Boot时，最简单的方法是在项目中包含[mybatis-spring-boot-starter](https://central.sonatype.com/artifact/org.mybatis.spring.boot/mybatis-spring-boot-starter/3.0.1)依赖项。

```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```

上面的启动器依赖传递地包括mybatis-spring-boot-autoconfigure、spring-boot-starter、spring-boot-starter-jdbc和mybatis-spring依赖，否则必须单独包含。

此外，我们应该包括用于数据库连接的关系型数据库驱动程序依赖项。我们使用[H2数据库](https://howtodoinjava.com/spring-boot2/h2-database-example/)，因此在POM中包含以下依赖：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

## 3. 模型

MyBatis将POJO对象映射到SQL语句，因此让我们创建一个非常简单的TODO对象用于演示目的。

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TODO {
    private Long id;
    private String title;
    private String body;
}
```

等效的数据库模式脚本文件schema.sql是：

```sql
create TABLE IF NOT EXISTS `TBL_TODO`
(
    `id`    INTEGER PRIMARY KEY,
    `title` VARCHAR(100)  NOT NULL,
    `body`  VARCHAR(2000) NOT NULL
);
```

如果需要，我们可以通过data.sql使用一些默认数据初始化表：

```sql
TRUNCATE TABLE TBL_TODO;
INSERT INTO TBL_TODO
VALUES (1, 'TITLE', 'BODY');
```

## 4. @Mapper配置

默认情况下，mybatis-spring-boot-starter将在组件扫描期间搜索带有[@Mapper](https://mybatis.org/mybatis-3/apidocs/org/apache/ibatis/annotations/Mapper.html)注解的映射器。

```java
import cn.tuyucheng.taketoday.mybatis.model.TODO;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ToDoMapper {

    @Select("select * from TBL_TODO")
    List<TODO> findAll();

    @Select("SELECT * FROM TBL_TODO WHERE id = #{id}")
    TODO findById(@Param("id") Long id);

    @Delete("DELETE FROM TBL_TODO WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Insert("INSERT INTO TBL_TODO(id, title, body) " + " VALUES (#{id}, #{title}, #{body})")
    int createNew(TODO item);

    @Update("Update TBL_TODO set title=#{title}, " + " body=#{body} where id=#{id}")
    int update(TODO item);
}
```

我们还可以使用**@MapperScan**注解在Spring @Configuration类中指定包。它将扫描指定包中的所有接口以查找以下映射器注解，并将这些接口注册为映射器。

-   @Select
-   @Insert
-   @Update
-   @Delete

```java
import org.mybatis.spring.annotation.MapperScan;

@Configuration
@MapperScan("cn.tuyucheng.taketoday.mybatis.mapper")
public class PersistenceConfig {
    // ...
}
```

并定义映射器接口如下：

```java
public interface ToDoMapper {

    @Select("select * from TBL_TODO")
    List<TODO> findAll();
    
    // ...
}
```

## 5. 数据源配置

**[MybatisAutoConfiguration](https://github.com/mybatis/spring-boot-starter/blob/master/mybatis-spring-boot-autoconfigure/src/main/java/org/mybatis/spring/boot/autoconfigure/MybatisAutoConfiguration.java)类自动扫描并配置SqlSessionFactory和DataSource bean**，如application.properties文件中指定的那样。

```properties
spring.datasource.url=jdbc:h2:file:/data/articles
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.sql.init.mode=always
```

**要以编程方式配置数据源，我们可以在@Configuration类中定义bean**：

```java
@Configuration
public class PersistenceConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
              .setType(EmbeddedDatabaseType.H2)
              .addScript("schema.sql")
              .addScript("data.sql")
              .build();
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource());
        return factoryBean.getObject();
    }
}
```

## 6. 演示

最后，经过上面的Mapper和DataSource的配置，我们可以测试Mapper对数据库的各种增删改查操作了。让我们使用CommandLineRunner来执行演示代码：

```java
import cn.tuyucheng.taketoday.mybatis.mapper.ToDoMapper;
import cn.tuyucheng.taketoday.mybatis.model.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App implements CommandLineRunner {
    
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Autowired
    private ToDoMapper todoMapper;

    @Override
    public void run(String... args) throws Exception {
        TODO newItem = new TODO(2L, "title_2", "body_2");
        int createdCount = todoMapper.createNew(newItem);
        System.out.println("Created items count : " + createdCount);

        TODO item = todoMapper.findById(2L);
        System.out.println(item);

        int deletedCount = todoMapper.deleteById(2L);
        System.out.println("Deleted items count : " + deletedCount);

        TODO deletedItem = todoMapper.findById(2L);
        System.out.println("Deleted item should be null : " + deletedItem);
    }
}
```

程序输出：

```shell
Created items count : 1
TODO(id=2, title=title_2, body=body_2)
Deleted items count : 1
Deleted item should be null : null
```

## 7. 总结

在这个简短的教程中，我们学习了使用Spring Boot配置MyBatis。我们还了解了映射器扫描选项和数据源配置。