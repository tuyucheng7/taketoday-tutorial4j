## 1. 概述

默认情况下，[MongoDBJava驱动程序生成](https://www.baeldung.com/java-mongodb-last-inserted-id)ObjectId类型的ID 。有时，我们可能希望使用另一种类型的数据作为对象的唯一标识符，例如[UUID](https://www.baeldung.com/java-uuid)。但是，MongoDBJava驱动程序无法自动生成 UUID。

[在本教程中，我们将了解使用 MongoDBJava驱动程序和Spring Data MongoDB](https://www.baeldung.com/spring-data-mongodb-guide)生成 UUID 的三种方法。

## 2.共同点

应用程序只管理一种类型的数据是非常罕见的。为了简化 MongoDB 数据库中 ID 的管理，实现一个定义所有[文档](https://www.mongodb.com/docs/manual/core/document/)类 ID 的抽象类会更容易。

```java
public abstract class UuidIdentifiedEntity {

    @Id   
    protected UUID id;    

    public void setId(UUID id) {

        if (this.id != null) {
            throw new UnsupportedOperationException("ID is already defined");
        }

        this.id = id;
    }

    // Getter
}
```

对于本教程中的示例，我们将假设 MongoDB 数据库中持久保存的所有类都继承自此类。

## 3.配置UUID支持

要允许在 MongoDB 中存储 UUID，我们必须配置驱动程序。这个配置非常简单，只告诉驱动程序如何将 UUID 存储在数据库中。如果多个应用程序使用同一个数据库，我们必须小心处理。

我们所要做的就是在启动时在我们的 MongoDB 客户端中指定uuidRepresentation参数：

```java
@Bean
public MongoClient mongo() throws Exception {
    ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017/test");
    MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
      .uuidRepresentation(UuidRepresentation.STANDARD)
      .applyConnectionString(connectionString).build();
    return MongoClients.create(mongoClientSettings);
}

```

如果我们使用 Spring Boot，我们可以在我们的 application.properties 文件中指定这个参数：

```java
spring.data.mongodb.uuid-representation=standard
```

## 4. 使用生命周期事件

处理 UUID 生成的第一种方法是使用 Spring 的生命周期事件。对于 MongoDB 实体，我们不能使用 JPA 注解@PrePersist等。因此，我们必须实现在[ApplicationContext](https://www.baeldung.com/spring-application-context)中注册的事件侦听器类。为此，我们的类必须扩展 Spring 的[AbstractMongoEventListener ](https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/core/mapping/event/AbstractMongoEventListener.html)类：

```java
public class UuidIdentifiedEntityEventListener extends AbstractMongoEventListener<UuidIdentifiedEntity> {
    
    @Override
    public void onBeforeConvert(BeforeConvertEvent<UuidIdentifiedEntity> event) {
        
        super.onBeforeConvert(event);
        UuidIdentifiedEntity entity = event.getSource();
        
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        } 
    }    
}
```

在这种情况下，我们使用OnBeforeConvert事件，它在 Spring 将我们的Java对象转换为Document对象并将其发送到 MongoDB 驱动程序之前触发。

键入我们的事件以捕获UuidIdentifiedEntity类允许处理此抽象超类型的所有子类。一旦使用 UUID 作为 ID 的对象被转换，Spring 将调用我们的代码。

我们必须知道，Spring 将事件处理委托给可能是异步的[TaskExecutor 。](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/task/TaskExecutor.html)Spring 不保证在对象被有效转换之前处理完事件。如果的TaskExecutor是异步的，则不鼓励使用此方法，因为 ID 可能会在对象转换后生成，从而导致异常：

InvalidDataAccessApiUsageException：无法为实体自动生成 java.util.UUID 类型的 ID

[我们可以通过使用@Component](https://www.baeldung.com/spring-component-annotation)注解或在@Configuration类中生成它来在ApplicationContext中注册事件侦听器：

```java
@Bean
public UuidIdentifiedEntityEventListener uuidIdentifiedEntityEventListener() {
    return new UuidIdentifiedEntityEventListener();
}
```

## 5.使用实体回调

Spring 基础设施提供了钩子来在实体生命周期的某些点执行自定义代码。这些称为EntityCallbacks，我们可以在我们的案例中使用它们在对象保存在数据库中之前生成 UUID。

与前面看到的事件侦听器方法不同，回调保证它们的执行是同步的，并且代码将在对象生命周期的预期时间点运行。

Spring Data MongoDB 提供了一组我们可以在应用程序中使用的回调。在我们的例子中，我们将使用与之前相同的事件。可以直接在@Configuration类中提供回调：

```java
@Bean
public BeforeConvertCallback<UuidIdentifiedEntity> beforeSaveCallback() {
        
    return (entity, collection) -> {
          
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        return entity;
    };
}

```

我们还可以使用实现[BeforeConvertCallback](https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/core/mapping/event/BeforeConvertCallback.html)接口的组件。

## 6. 使用自定义存储库

Spring Data MongoDB 提供了第三种方法来实现我们的目标：使用自定义存储库实现。通常，我们只需声明一个继承自MongoRepository 的接口，然后由 Spring 处理存储库相关的代码。

如果我们想改变 Spring Data 处理对象的方式，我们可以定义 Spring 将在存储库级别执行的自定义代码。为此，我们必须首先定义一个扩展MongoRepository的接口：

```java
@NoRepositoryBean
public interface CustomMongoRepository<T extends UuidIdentifiedEntity> extends MongoRepository<T, UUID> { }

```

@NoRepositoryBean注解阻止 Spring 生成与 MongoRepository 关联的常规[代码](https://www.baeldung.com/spring-data-annotations)片段。此接口强制使用 UUID 作为对象中 ID 的类型。

然后，我们必须创建一个存储库类，它将定义处理我们的 UUID 所需的行为：

```java
public class CustomMongoRepositoryImpl<T extends UuidIdentifiedEntity> 
  extends SimpleMongoRepository<T, UUID> implements CustomMongoRepository<T>
```

在此存储库中，我们必须通过覆盖SimpleMongoRepository的相关方法来捕获我们需要生成 ID 的所有方法调用。在我们的例子中，这些方法是save()和insert()：

```java
@Override
public <S extends T> S save(S entity) {
    generateId(entity);
    return super.save(entity);
}

```

最后，我们需要告诉 Spring 使用我们的自定义类作为存储库的实现而不是默认实现。我们在@Configuration类中这样做：

```java
@EnableMongoRepositories(basePackages = "com.baeldung.repository", repositoryBaseClass = CustomMongoRepositoryImpl.class)

```

然后，我们可以像往常一样声明我们的存储库而无需更改：

```java
public interface BookRepository extends MongoRepository<Book, UUID> { }
```

## 七. 总结

在本文中，我们看到了三种使用 Spring Data MongoDB 将 UUID 实现为 MongoDB 对象 ID 的方法。