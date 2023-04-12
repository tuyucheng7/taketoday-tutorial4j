## 1. 概述

在本文中，我们将**使用bean后处理器实现自定义Spring注解**。

那么这有什么帮助呢？简单的说，我们可以重用同一个bean，而不必创建多个相同类型的相似bean。

我们将在一个简单的项目中为DAO实现做到这一点，用一个灵活的GenericDao替换所有DAO。

## 2. Gradle

我们需要spring-core、spring-aop和spring-context-support依赖。我们可以在build.gradle中声明spring-context-support。

```groovy
dependencies {
    implementation 'org.springframework:spring-context-support:5.3.13'
}
```

## 3. Generic DAO

大多数Spring/JPA/Hibernate实现使用标准DAO，通常每个实体对应一个DAO。

我们将用GenericDao替换该解决方案；改为编写一个自定义注解处理器并使用该GenericDao实现：

### 3.1 GenericDAO

```java
public class GenericDao<E> {
    private Class<E> entityClass;

    public GenericDao(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    public List<E> findAll() {
        // ...
    }

    public Optional<E> persist(E toPersist) {
        // ...
    }
}
```

在真实场景中，你当然需要注入PersistenceContext，并实际提供这些方法的实现。现在，我们将尽可能地简化这一过程。

现在，让我们为自定义注入创建注解。

### 3.2 Data Access

```java

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Documented
public @interface DataAccess {
    Class<?> entity();
}
```

我们将使用上面的注解来注入一个GenericDao，如下所示：

```text
@DataAccess(entity=Person.class)
private GenericDao<Person> personDao;
```

也许有些人会问，“Spring如何识别我们的@DataAccess注解？”。但事实并非如此，默认情况Spring并不会识别。

但是我们可以告诉Spring通过自定义BeanPostProcessor来识别注解。

### 3.3 DataAccessAnnotationProcessor

```java

@Component
public class DataAccessAnnotationProcessor implements BeanPostProcessor {
    private ConfigurableListableBeanFactory configurableBeanFactory;

    @Autowired
    public DataAccessAnnotationProcessor(ConfigurableListableBeanFactory beanFactory) {
        this.configurableBeanFactory = beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        this.scanDataAccessAnnotation(bean, beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    protected void scanDataAccessAnnotation(Object bean, String beanName) {
        this.configureFieldInjection(bean);
    }

    private void configureFieldInjection(Object bean) {
        Class<?> managedBeanClass = bean.getClass();
        FieldCallback fieldCallback = new DataAccessFieldCallback(configurableBeanFactory, bean);
        ReflectionUtils.doWithFields(managedBeanClass, fieldCallback);
    }
}
```

下面是我们使用到的DataAccessFieldCallback的实现：

### 3.4 DataAccessFieldCallback

```java
public class DataAccessFieldCallback implements FieldCallback {
    private static Logger logger = LoggerFactory.getLogger(DataAccessFieldCallback.class);

    private static int AUTOWIRE_MODE = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

    private static String ERROR_ENTITY_VALUE_NOT_SAME = "@DataAccess(entity) " + "value should have same type with injected generic type.";
    private static String WARN_NON_GENERIC_VALUE = "@DataAccess annotation assigned " + "to raw (non-generic) declaration. This will make your code less type-safe.";
    private static String ERROR_CREATE_INSTANCE = "Cannot create instance of " + "type '{}' or instance creation is failed because: {}";

    private ConfigurableListableBeanFactory configurableBeanFactory;
    private Object bean;

    public DataAccessFieldCallback(ConfigurableListableBeanFactory bf, Object bean) {
        configurableBeanFactory = bf;
        this.bean = bean;
    }

    @Override
    public void doWith(Field field)
            throws IllegalArgumentException, IllegalAccessException {
        if (!field.isAnnotationPresent(DataAccess.class)) {
            return;
        }
        ReflectionUtils.makeAccessible(field);
        Type fieldGenericType = field.getGenericType();
        // In this example, get actual "GenericDAO' type.
        Class<?> generic = field.getType();
        Class<?> classValue = field.getDeclaredAnnotation(DataAccess.class).entity();

        if (genericTypeIsValid(classValue, fieldGenericType)) {
            String beanName = classValue.getSimpleName() + generic.getSimpleName();
            Object beanInstance = getBeanInstance(beanName, generic, classValue);
            field.set(bean, beanInstance);
        } else {
            throw new IllegalArgumentException(ERROR_ENTITY_VALUE_NOT_SAME);
        }
    }

    public boolean genericTypeIsValid(Class<?> clazz, Type field) {
        if (field instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) field;
            Type type = parameterizedType.getActualTypeArguments()[0];
            return type.equals(clazz);
        } else {
            logger.warn(WARN_NON_GENERIC_VALUE);
            return true;
        }
    }

    public Object getBeanInstance(String beanName, Class<?> genericClass, Class<?> paramClass) {
        Object daoInstance = null;
        if (!configurableBeanFactory.containsBean(beanName)) {
            logger.info("Creating new DataAccess bean named '{}'.", beanName);

            Object toRegister = null;
            try {
                Constructor<?> ctr = genericClass.getConstructor(Class.class);
                toRegister = ctr.newInstance(paramClass);
            } catch (Exception e) {
                logger.error(ERROR_CREATE_INSTANCE, genericClass.getTypeName(), e);
                throw new RuntimeException(e);
            }

            daoInstance = configurableBeanFactory.initializeBean(toRegister, beanName);
            configurableBeanFactory.autowireBeanProperties(daoInstance, AUTOWIRE_MODE, true);
            configurableBeanFactory.registerSingleton(beanName, daoInstance);
            logger.info("Bean named '{}' created successfully.", beanName);
        } else {
            daoInstance = configurableBeanFactory.getBean(beanName);
            logger.info("Bean named '{}' already exists used as current bean reference.", beanName);
        }
        return daoInstance;
    }
}
```

这是一个相当不错的实现，但其中最重要的部分是doWith()方法：

```
genericDaoInstance = configurableBeanFactory.initializeBean(beanToRegister, beanName);
configurableBeanFactory.autowireBeanProperties(genericDaoInstance, autowireMode, true);
configurableBeanFactory.registerSingleton(beanName, genericDaoInstance);
```

这将告诉Spring根据运行时通过@DataAccess注解注入的对象初始化bean。

beanName确保我们将获得bean的唯一实例，因为在这种情况下，我们确实希望根据通过@DataAccess注解注入的实体创建GenericDao的单个对象。

最后，让我们在接下来的Spring配置中使用这个新的bean处理器。

### 3.5 CustomAnnotationConfiguration

```java

@Configuration
@ComponentScan("cn.tuyucheng.taketoday.customannotation")
public class CustomAnnotationConfiguration {

}
```

这里重要的一点是，@ComponentScan注解的value需要指向我们的自定义bean后处理器所在的包，并确保它在运行时由Spring扫描和自动装配。

## 4. 测试DAO

让我们从一个支持Spring的测试和两个简单的实体类示例开始 - Person和Account。

```java

@Data
public class Account implements Serializable {
    @Serial
    private static final long serialVersionUID = 7857541629844398625L;

    private Long id;
    private String email;
    private Person person;
}

@Data
public class Person implements Serializable {
    @Serial
    private static final long serialVersionUID = 9005331414216374586L;

    private Long id;
    private String name;
}
```

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CustomAnnotationConfiguration.class})
class DataAccessAnnotationIntegrationTest {

    @DataAccess(entity = Person.class)
    private GenericDAO<Person> personGenericDAO;
    @DataAccess(entity = Account.class)
    private GenericDAO<Account> accountGenericDAO;
    @DataAccess(entity = Person.class)
    private GenericDAO<Person> anotherPersonGenericDAO;
}
```

我们在@DataAccess注解的帮助下注入了一些GenericDao实例。为了测试新bean是否被正确注入，我们需要涵盖：

1. 是否注入成功
2. 是否具有相同实体的bean实例相同
3. 是否GenericDao中的方法确实按预期工作

第1点实际上已被Spring本身所覆盖。因为如果无法注入bean，框架会很早就抛出异常。

为了测试第2点，我们需要查看两个都使用Person类的GenericDao实例：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CustomAnnotationConfiguration.class})
class DataAccessAnnotationIntegrationTest {

    @Test
    void whenGenericDAOInjected_thenItIsSingleton() {
        assertThat(personGenericDAO, not(sameInstance(accountGenericDAO)));
        assertThat(personGenericDAO, not(equalTo(accountGenericDAO)));
        assertThat(personGenericDAO, sameInstance(anotherPersonGenericDAO));
    }
}
```

我们不希望personGenericDao等于accountGenericDao。

但我们确实希望personGenericDao和anotherPersonGenericDao是完全相同的实例。

为了测试第3点，我们在这里只测试一些简单的持久层相关逻辑：

```java

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CustomAnnotationConfiguration.class})
class DataAccessAnnotationIntegrationTest {

    @Test
    void whenFindAll_thenMessagesIsCorrect() {
        personGenericDAO.findAll();
        assertThat(personGenericDAO.getMessage(), is("Would create findAll query from Person"));
        accountGenericDAO.findAll();
        assertThat(accountGenericDAO.getMessage(), is("Would create findAll query from Account"));
    }

    @Test
    void whenPersist_thenMakeSureThatMessagesIsCorrect() {
        personGenericDAO.persist(new Person());
        assertThat(personGenericDAO.getMessage(), is("Would create persist query from Person"));
        accountGenericDAO.persist(new Account());
        assertThat(accountGenericDAO.getMessage(), is("Would create persist query from Account"));
    }
}
```

## 5. 总结

在本文中，我们在Spring中实现了一个非常好用的自定义注解，并使用了BeanPostProcessor。
总体目标是摆脱我们通常在持久层中使用的多个DAO实现，并使用一个清爽的、简单的通用实现，而不会在过程中丢失任何功能。