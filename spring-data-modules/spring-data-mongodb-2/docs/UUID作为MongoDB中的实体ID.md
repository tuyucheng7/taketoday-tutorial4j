## 1. 概述

默认情况下，[MongoDB Java驱动程序](https://www.baeldung.com/java-mongodb-last-inserted-id)生成ObjectId类型的ID。有时，我们可能希望使用另一种类型的数据作为对象的唯一标识符，例如[UUID](https://www.baeldung.com/java-uuid)。但是，**MongoDB Java驱动程序无法自动生成UUID**。

在本教程中，我们将了解使用MongoDB Java驱动程序和[Spring Data MongoDB](https://www.baeldung.com/spring-data-mongodb-guide)生成UUID的三种方法。

## 2. 共同点

应用程序只管理一种类型的数据是非常罕见的。为了简化MongoDB数据库中ID的管理，实现一个定义所有[Document](https://www.mongodb.com/docs/manual/core/document/)类ID的抽象类会更容易。

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

对于本教程中的示例，我们将假设MongoDB数据库中持久保存的所有类都继承自此类。

## 3. 配置UUID支持

要允许在MongoDB中存储UUID，我们必须配置驱动程序。这个配置非常简单，只告诉驱动程序如何将UUID存储在数据库中。如果多个应用程序使用同一个数据库，我们必须小心处理。

**我们所要做的就是在启动时在我们的MongoDB客户端中指定uuidRepresentation参数**：

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

如果我们使用Spring Boot，我们可以在我们的application.properties文件中指定这个参数：

```properties
spring.data.mongodb.uuid-representation=standard
```

## 4. 使用生命周期事件

处理UUID生成的第一种方法是使用Spring的生命周期事件。对于MongoDB实体，我们不能使用JPA注解@PrePersist等。因此，我们必须实现在[ApplicationContext](https://www.baeldung.com/spring-application-context)中注册的事件监听器类。**为此，我们的类必须扩展Spring的[AbstractMongoEventListener](https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/core/mapping/event/AbstractMongoEventListener.html)类**：

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

在这种情况下，我们使用OnBeforeConvert事件，该事件在Spring将我们的Java对象转换为Document对象并将其发送到MongoDB驱动程序之前触发。

键入我们的事件以捕获UuidIdentifiedEntity类允许处理此抽象超类型的所有子类。一旦使用UUID作为ID的对象被转换，Spring将调用我们的代码。

我们必须意识到，Spring将事件处理委托给可能是异步的[TaskExecutor](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/task/TaskExecutor.html)。**Spring不保证在对象被有效转换之前处理完事件**。如果你的TaskExecutor是异步的，则不鼓励使用此方法，因为ID可能会在对象转换后生成，从而导致异常：

> InvalidDataAccessApiUsageException: Cannot autogenerate id of type java.util.UUID for entity

我们可以通过使用[@Component](https://www.baeldung.com/spring-component-annotation)注解或在@Configuration类中生成它来在ApplicationContext中注册事件监听器：

```java
@Bean
public UuidIdentifiedEntityEventListener uuidIdentifiedEntityEventListener() {
    return new UuidIdentifiedEntityEventListener();
}
```

## 5. 使用实体回调

Spring基础设施提供了在实体生命周期中的某些点执行自定义代码的钩子。这些称为EntityCallbacks，我们可以在我们的案例中使用它们在对象保存在数据库中之前生成UUID。

与前面看到的事件监听器方法不同，**回调保证它们的执行是同步的**，并且代码将在对象生命周期的预期时间点运行。

Spring Data MongoDB提供了一组我们可以在应用程序中使用的回调。在我们的例子中，我们将使用与之前相同的事件。**回调可以直接在@Configuration类中提供**：

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

## 6. 使用自定义Repository

Spring Data MongoDB提供了第三种方法来实现我们的目标：使用自定义Repository实现。通常，我们只需声明一个继承自MongoRepository的接口，然后由Spring处理Repository相关的代码。

如果我们想改变Spring Data处理对象的方式，我们可以定义Spring将在Repository级别执行的自定义代码。为此，**我们必须首先定义一个扩展MongoRepository的接口**：

```java
@NoRepositoryBean
public interface CustomMongoRepository<T extends UuidIdentifiedEntity> extends MongoRepository<T, UUID> {
}
```

[@NoRepositoryBean](https://www.baeldung.com/spring-data-annotations)注解阻止了Spring生成与MongoRepository关联的常规代码片段。此接口强制使用UUID作为对象中ID的类型。

然后，我们必须创建一个Repository类，它将定义处理我们的UUID所需的行为：

```java
public class CustomMongoRepositoryImpl<T extends UuidIdentifiedEntity> extends SimpleMongoRepository<T, UUID> implements CustomMongoRepository<T>
```

在此Repository中，**我们必须通过覆盖SimpleMongoRepository的相关方法来捕获我们需要生成ID的所有方法调用**。在我们的例子中，这些方法是save()和insert()：

```java
@Override
public <S extends T> S save(S entity) {
    generateId(entity);
    return super.save(entity);
}
```

最后，我们需要告诉Spring使用我们的自定义类作为Repository的实现而不是使用默认实现。我们在@Configuration类中这样做：

```java
@EnableMongoRepositories(basePackages = "cn.tuyucheng.taketoday.repository", repositoryBaseClass = CustomMongoRepositoryImpl.class)
```

然后，我们可以像往常一样声明我们的Repository，无需其它更改：

```java
public interface BookRepository extends MongoRepository<Book, UUID> { }
```

## 7. 总结

在本文中，我们看到了三种使用Spring Data MongoDB将UUID实现为MongoDB对象ID的方法。