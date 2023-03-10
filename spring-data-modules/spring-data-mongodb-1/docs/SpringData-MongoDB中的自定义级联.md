## 1. 概述

本教程将继续探索Spring Data MongoDB的一些核心功能-@DBRef注解和生命周期事件。

## 2. @DBRef

映射框架不支持在其他文档中**存储父子关系**和嵌入文档。我们可以做的是-我们可以单独存储它们并使用DBRef来引用文档。

当从MongoDB加载对象时，这些引用将被急切地解析，我们将取回一个映射对象，该对象看起来与嵌入主文档中的映射对象相同。

让我们看一些代码：

```java
@DBRef
private EmailAddress emailAddress;
```

EmailAddress如下所示：

```java
@Document
public class EmailAddress {
    @Id
    private String id;

    private String value;

    // standard getters and setters
}
```

请注意，**映射框架不处理级联操作**。因此-如果我们触发对父级的保存，则不会自动保存子项。如果我们也想保存子项，则需要明确触发对子项的保存。

这正是**生命周期事件派上用场**的地方。

## 3. 生命周期事件

Spring Data MongoDB发布了一些非常有用的生命周期事件-例如onBeforeConvert、onBeforeSave、onAfterSave、onAfterLoad和onAfterConvert。

为了拦截其中一个事件，我们需要注册一个AbstractMappingEventListener的子类并重写这里的其中一个方法。调度事件时，将调用我们的监听器并传入域对象。

### 3.1 基本级联保存

让我们看一下我们之前的例子-使用emailAddress保存用户。现在，我们可以监听将在域对象进入转换器之前调用的onBeforeConvert事件：

```java
public class UserCascadeSaveMongoEventListener extends AbstractMongoEventListener<Object> {
    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        Object source = event.getSource();
        if ((source instanceof User) && (((User) source).getEmailAddress() != null)) {
            mongoOperations.save(((User) source).getEmailAddress());
        }
    }
}
```

现在我们只需要将监听器注册到MongoConfig中：

```java
@Bean
public UserCascadeSaveMongoEventListener userCascadingMongoEventListener() {
    return new UserCascadeSaveMongoEventListener();
}
```

或者作为XML：

```xml
<bean class="cn.tuyucheng.taketoday.event.UserCascadeSaveMongoEventListener" />
```

我们已经完成了级联语义-尽管仅适用于用户。

### 3.2 通用级联实现

现在让我们通过**使级联功能更通用**来改进以前的解决方案。让我们从定义自定义注解开始：

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CascadeSave {
    //
}
```

现在让我们**处理自定义监听器**以通用地处理这些字段，而不必强制转换为任何特定实体：

```java
public class CascadeSaveMongoEventListener extends AbstractMongoEventListener<Object> {

    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        Object source = event.getSource();
        ReflectionUtils.doWithFields(source.getClass(), new CascadeCallback(source, mongoOperations));
    }
}
```

所以我们使用了Spring的反射工具类，并在所有符合我们条件的字段上运行回调：

```java
@Override
public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
    ReflectionUtils.makeAccessible(field);

    if (field.isAnnotationPresent(DBRef.class) && field.isAnnotationPresent(CascadeSave.class)) {
        Object fieldValue = field.get(getSource());
        if (fieldValue != null) {
            FieldCallback callback = new FieldCallback();
            ReflectionUtils.doWithFields(fieldValue.getClass(), callback);

            getMongoOperations().save(fieldValue);
        }
    }
}
```

如你所见，我们正在寻找同时具有DBRef注解和CascadeSave的字段。一旦我们找到这些字段，我们就保存子实体。

让我们看一下FieldCallback类，我们用它来检查子项是否有@Id注解：

```java
public class FieldCallback implements ReflectionUtils.FieldCallback {
    private boolean idFound;

    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
        ReflectionUtils.makeAccessible(field);

        if (field.isAnnotationPresent(Id.class)) {
            idFound = true;
        }
    }

    public boolean isIdFound() {
        return idFound;
    }
}
```

最后，为了使它们协同工作，我们当然需要对emailAddress字段进行正确标注：

```java
@DBRef
@CascadeSave
private EmailAddress emailAddress;
```

### 3.3 级联测试

现在让我们看一个场景-我们使用emailAddress保存一个用户，保存操作自动级联到这个嵌入的实体：

```java
User user = new User();
user.setName("Brendan");
EmailAddress emailAddress = new EmailAddress();
emailAddress.setValue("b@gmail.com");
user.setEmailAddress(emailAddress);
mongoTemplate.insert(user);
```

让我们检查一下我们的数据库：

```json
{
    "_id" : ObjectId("55cee9cc0badb9271768c8b9"),
    "name" : "Brendan",
    "age" : null,
    "email" : {
        "value" : "b@gmail.com"
    }
}
```

## 4. 总结

在本文中，我们展示了Spring Data MongoDB的一些很酷的特性-@DBRef注解、生命周期事件以及我们如何智能地处理级联。