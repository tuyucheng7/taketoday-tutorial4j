## 1. 概述

Access Control List(ACL)是附加到对象的权限列表。ACL指定哪些身份被授予对给定对象的哪些操作。

Spring Security ACL是**一个支持域对象安全性的Spring组件**。简而言之，Spring ACL有助于在单个域对象上定义特定用户/角色的权限-而不是在典型的每个操作级别上全面定义权限。

例如，具有Admin角色的用户可以查看(READ)和编辑(WRITE)中央通知框上的所有消息，但普通用户只能查看消息、与其相关联但不能编辑。同时，其他具有WRITE角色的用户可以查看和编辑某些特定消息。

因此，不同的用户/角色对每个特定对象具有不同的权限。在这种情况下，Spring ACL能够实现我们的目的。我们将在本文中探讨如何使用Spring ACL设置基本权限检查。

## 2. 配置

### 2.1 ACL数据库

要使用Spring Security ACL，我们需要在数据库中创建四个必需表。

第一个表是ACL_CLASS，它存储域对象的类名，字段包括：

+ ID
+ CLASS：安全域对象的类名，例如cn.tuyucheng.taketoday.acl.persistence.entity.NoticeMessage

其次，我们需要ACL_SID表，它可以让我们统一识别系统中的任何主体或权限。该表需要：

+ ID
+ SID：这是用户名或角色名，SID代表安全标识(Security Identity)
+ PRINCIPAL：0或1，表示对应的SID是主体(user，如mary、mike、jack...)或权限(role，如ROLE_ADMIN、ROLE_USER、ROLE_EDITOR...)

然后是ACL_OBJECT_IDENTITY表，它存储每个唯一域对象的信息：

+ ID
+ OBJECT_ID_CLASS：定义域对象类，关联到到ACL_CLASS表
+ OBJECT_ID_IDENTITY：域对象可以存储在许多表中，具体取决于类。因此，该字段存储目标对象主键
+ PARENT_OBJECT：在此表中指定此Object Identity的父级
+ OWNER_SID：对象所有者的ID，关联到ACL_SID表
+ ENTRIES_INHERITING：该对象的ACL Entries是否继承自父对象(ACL Entries定义在ACL_ENTRY表中)

最后，ACL_ENTRY存储分配给Object Identity上每个SID的单独权限：

+ ID
+ ACL_OBJECT_IDENTITY：指定Object Identity，关联到ACL_OBJECT_IDENTITY表
+ ACE_ORDER：当前Entry在对应Object Identity的ACL Entry列表中的顺序
+ SID：授予或拒绝权限的目标SID，关联到ACL_SID表
+ MASK：表示授予或拒绝的实际权限的整数位掩码
+ GRANTING：值1表示授予，值0表示拒绝
+ AUDIT_SUCCESS和AUDIT_FAILURE：用于审计目的

### 2.2 依赖

为了能够在我们的项目中使用Spring ACL，让我们首先定义我们的依赖项：

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-acl</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context-support</artifactId>
</dependency>
<dependency>
    <groupId>net.sf.ehcache</groupId>
    <artifactId>ehcache-core</artifactId>
    <version>2.6.11</version>
</dependency>
```

Spring ACL需要一个缓存来存储Object Identity和ACL Entry，所以我们在这里使用Ehcache。而且，为了在Spring中支持Ehcache，我们还需要spring-context-support。

当不使用Spring Boot时，我们需要显式添加版本。这些可以在Maven Central上检查：[spring-security-acl](https://central.sonatype.com/artifact/org.springframework.security/spring-security-acl/6.0.2)、[spring-security-config](https://central.sonatype.com/artifact/org.springframework.security/spring-security-config/6.0.2)、[spring-context-support](https://central.sonatype.com/artifact/org.springframework/spring-context-support/6.0.6)、[ehcache-core](https://central.sonatype.com/artifact/net.sf.ehcache/ehcache-core/2.6.11)。

### 2.3 ACL相关配置

我们需要通过启用全局方法安全性来保护所有返回安全域对象或对对象进行更改的方法：

```java
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class AclMethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

    @Autowired
    MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return defaultMethodSecurityExpressionHandler;
    }
}
```

我们还通过将prePostEnabled设置为true以使用Spring表达式语言(SpEL)来启用基于表达式的访问控制。此外，我们需要一个支持ACL的表达式处理程序：

```java
@Configuration
@EnableAutoConfiguration
public class ACLContext {

    @Bean
    public MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        AclPermissionEvaluator permissionEvaluator = new AclPermissionEvaluator(aclService());
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        return expressionHandler;
    }
}
```

因此，我们将AclPermissionEvaluator分配给DefaultMethodSecurityExpressionHandler。AclPermissionEvaluator需要一个MutableAclService来从数据库加载权限设置和域对象的定义。

为简单起见，我们使用提供的JdbcMutableAclService：

```java
@Bean
public JdbcMutableAclService aclService() {
    return new JdbcMutableAclService(dataSource, lookupStrategy(), aclCache());
}
```

顾名思义，JdbcMutableAclService使用JdbcTemplate来简化数据库访问。它需要一个DataSource(用于JdbcTemplate)、LookupStrategy(在查询数据库时提供优化的查找)和一个AclCache(缓存ACL Entry和Object Identity)。

同样，为了简单起见，我们使用提供的BasicLookupStrategy和EhCacheBasedAclCache。

```java
@Autowired
DataSource dataSource;

@Bean
public EhCacheBasedAclCache aclCache() {
    return new EhCacheBasedAclCache(aclEhCacheFactoryBean().getObject(), permissionGrantingStrategy(), aclAuthorizationStrategy());
}

@Bean
public EhCacheFactoryBean aclEhCacheFactoryBean() {
    EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
    ehCacheFactoryBean.setCacheManager(Objects.requireNonNull(aclCacheManager().getObject()));
    ehCacheFactoryBean.setCacheName("aclCache");
    return ehCacheFactoryBean;
}

@Bean
public EhCacheManagerFactoryBean aclCacheManager() {
    return new EhCacheManagerFactoryBean();
}

@Bean
public PermissionGrantingStrategy permissionGrantingStrategy() {
    return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
}

@Bean
public AclAuthorizationStrategy aclAuthorizationStrategy() {
    return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
}

@Bean
public LookupStrategy lookupStrategy() {
    return new BasicLookupStrategy(dataSource, aclCache(), aclAuthorizationStrategy(), new ConsoleAuditLogger());
}
```

在这里，AclAuthorizationStrategy负责判断当前用户是否拥有对某些对象的所有必需权限。

它需要PermissionGrantingStrategy的支持，它定义了用于确定是否向特定SID授予权限的逻辑。

## 3. Spring ACL的方法安全性

到目前为止，我们已经完成了所有必要的配置。现在我们可以在安全方法上设置所需的检查规则。

默认情况下，Spring ACL为所有可用权限引用BasePermission类。基本上，我们有READ、WRITE、CREATE、DELETE和ADMINISTRATION权限。

让我们尝试定义一些安全规则：

```java
public interface NoticeMessageRepository extends JpaRepository<NoticeMessage, Long> {

    @PostFilter("hasPermission(filterObject, 'READ')")
    List<NoticeMessage> findAll();

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    NoticeMessage findById(Integer id);

    @PreAuthorize("hasPermission(#noticeMessage, 'WRITE')")
    NoticeMessage save(@Param("noticeMessage") NoticeMessage noticeMessage);
}
```

findAll()方法执行后，会触发@PostFilter。指定的规则hasPermission(filterObject, 'READ')意味着只返回当前用户具有READ权限的NoticeMessage。

同样，@PostAuthorize是在findById()方法执行后触发的，确保仅在当前用户具有READ权限时才返回NoticeMessage对象。否则，系统将抛出AccessDeniedException。

另一方面，系统在调用save()方法之前触发@PreAuthorize注解。它将决定是否允许执行相应的方法。否则，将抛出AccessDeniedException。

## 4. 实践

现在让我们使用JUnit测试所有这些配置，我们将使用H2数据库来保持配置尽可能简单。

下面是所需的依赖：

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

### 4.1 场景

在这种情况下，我们有两个用户(manager、hr)和一个USER角色(ROLE_EDITOR)，因此我们的acl_sid表将是：

```h2
INSERT INTO acl_sid (id, principal, sid)
VALUES (1, 1, 'manager'),
       (2, 1, 'hr'),
       (3, 0, 'ROLE_EDITOR');
```

然后，我们需要在acl_class中声明NoticeMessage类。并且会在system_message中插入三个NoticeMessage类的实例。

此外，这3个实例的相应记录必须在acl_object_identity中声明：

```java
@Entity
@Table(name = "system_message")
public class NoticeMessage {

    @Id
    @Column
    private Integer id;
    @Column
    private String content;
}
```

```h2
INSERT INTO acl_class (id, class)
VALUES (1, 'cn.tuyucheng.taketoday.acl.persistence.entity.NoticeMessage');

INSERT INTO system_message(id, content)
VALUES (1, 'First Level Message'),
       (2, 'Second Level Message'),
       (3, 'Third Level Message');

INSERT INTO acl_object_identity
(id, object_id_class, object_id_identity,
 parent_object, owner_sid, entries_inheriting)
VALUES (1, 1, 1, NULL, 3, 0),
       (2, 1, 2, NULL, 3, 0),
       (3, 1, 3, NULL, 3, 0);
```

最初，我们将第一个对象(id=1)的READ和WRITE权限授予用户manager。同时，任何具有ROLE_EDITOR角色的用户都将对所有三个对象具有READ权限，但只拥有对第三个对象(id=3)的WRITE权限。此外，用户hr对第二个对象只有READ权限。

这里，因为我们使用默认的Spring ACL BasePermission类进行权限检查，所以READ权限的掩码值为1，WRITE权限的掩码值为2。我们在acl_entry中的数据为：

```h2
INSERT INTO acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
VALUES (1, 1, 1, 1, 1, 1, 1, 1),
       (2, 1, 2, 1, 2, 1, 1, 1),
       (3, 1, 3, 3, 1, 1, 1, 1),
       (4, 2, 1, 2, 1, 1, 1, 1),
       (5, 2, 2, 3, 1, 1, 1, 1),
       (6, 3, 1, 3, 1, 1, 1, 1),
       (7, 3, 2, 3, 2, 1, 1, 1);
```

### 4.2 测试用例

首先，我们测试调用findAll方法。

根据我们的配置，该方法仅返回用户具有READ权限的那些NoticeMessage。

因此，我们期望结果列表仅包含一条NoticeMessage记录：

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@TestExecutionListeners(listeners = {ServletTestExecutionListener.class,
      DependencyInjectionTestExecutionListener.class,
      DirtiesContextTestExecutionListener.class,
      TransactionalTestExecutionListener.class,
      WithSecurityContextTestExecutionListener.class
})
class SpringACLIntegrationTest extends AbstractJUnit4SpringContextTests {

    private static final Integer FIRST_MESSAGE_ID = 1;
    private static final Integer SECOND_MESSAGE_ID = 2;
    private static final String EDITED_CONTENT = "EDITED";

    @Configuration
    @ComponentScan("cn.tuyucheng.taketoday.acl.*")
    public static class SpringConfig {

    }

    @Autowired
    NoticeMessageRepository noticeMessageRepository;

    @Test
    @WithMockUser(username = "manager")
    void givenUserManager_whenFindAllMessage_thenReturnFirstMessage() {
        List<NoticeMessage> details = noticeMessageRepository.findAll();

        assertNotNull(details);
        assertEquals(1, details.size());
        assertEquals(FIRST_MESSAGE_ID, details.get(0).getId());
    }
}
```

然后我们尝试对具有角色ROLE_EDITOR的任何用户调用相同的方法。请注意，在这种情况下，这些用户对所有三个对象都具有READ权限。

因此，我们期望结果列表将包含所有三条NoticeMessage记录：

```java
@Test
@WithMockUser(roles = {"EDITOR"})
void givenRoleEditor_whenFindAllMessage_thenReturn3Message() {
    List<NoticeMessage> details = noticeMessageRepository.findAll();

    assertNotNull(details);
    assertEquals(3, details.size());
}
```

接下来，使用manager用户，我们尝试通过id获取第一条NoticeMessage记录并更新其内容-这应该一切正常：

```java
@Test
@WithMockUser(username = "manager")
void givenUserManager_whenFind1stMessageByIdAndUpdateItsContent_thenOK() {
    NoticeMessage firstMessage = noticeMessageRepository.findById(FIRST_MESSAGE_ID);

    assertNotNull(firstMessage);
    assertEquals(FIRST_MESSAGE_ID, firstMessage.getId());

    firstMessage.setContent(EDITED_CONTENT);
    noticeMessageRepository.save(firstMessage);
    NoticeMessage editedFirstMessage = noticeMessageRepository.findById(FIRST_MESSAGE_ID);

    assertNotNull(editedFirstMessage);
    assertEquals(FIRST_MESSAGE_ID, editedFirstMessage.getId());
    assertEquals(EDITED_CONTENT, editedFirstMessage.getContent());
}
```

但是，如果任何具有ROLE_EDITOR角色的用户更新了第一条NoticeMessage记录的内容，我们的系统将抛出AccessDeniedException：

```java
@Test
@WithMockUser(roles = {"EDITOR"})
void givenRoleEditor_whenFind1stMessageByIdAndUpdateContent_thenFail() {
    NoticeMessage firstMessage = noticeMessageRepository.findById(FIRST_MESSAGE_ID);

    assertNotNull(firstMessage);
    assertEquals(FIRST_MESSAGE_ID, firstMessage.getId());

    firstMessage.setContent(EDITED_CONTENT);
    assertThrows(AccessDeniedException.class, () -> noticeMessageRepository.save(firstMessage));
}
```

同样，hr用户可以通过id得到第二条NoticeMessage记录，但无法更新它：

```java
@Test
@WithMockUser(username = "hr")
void givenUsernameHr_whenFindMessageById2_thenOK() {
    NoticeMessage secondMessage = noticeMessageRepository.findById(SECOND_MESSAGE_ID);

    assertNotNull(secondMessage);
    assertEquals(SECOND_MESSAGE_ID, secondMessage.getId());
}

@Test
@WithMockUser(username = "hr")
void givenUsernameHr_whenUpdateMessageWithId2_thenFail() {
    NoticeMessage secondMessage = new NoticeMessage();
    secondMessage.setId(SECOND_MESSAGE_ID);
    secondMessage.setContent(EDITED_CONTENT);

    assertThrows(AccessDeniedException.class, () -> noticeMessageRepository.save(secondMessage));
}
```

## 5. 总结

在本文中，我们介绍了Spring ACL的基本配置和使用。

Spring ACL需要特定的表来管理对象、主体/权限和权限设置。与这些表的所有交互(尤其是更新操作)都必须通过AclService。我们将在以后的文章中探讨此服务的基本CRUD操作。

默认情况下，我们仅限于BasePermission类中的预定义权限。