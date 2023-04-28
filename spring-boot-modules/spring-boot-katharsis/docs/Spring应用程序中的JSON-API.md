## 1. 概述

在本文中，我们将开始探索**[JSON-API](http://jsonapi.org/)规范**以及如何将其集成到Spring支持的REST API中。

我们将在Java中使用JSON-API的[Katharsis](https://github.com/katharsis-project/katharsis-framework)实现-我们将设置一个Katharsis支持的Spring应用程序，所以我们只需要一个Spring应用程序。

## 2. Maven

首先，让我们看一下我们的Maven配置-我们需要将以下依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>io.katharsis</groupId>
    <artifactId>katharsis-spring</artifactId>
    <version>3.0.2</version>
</dependency>
```

## 3. 用户资源

接下来，让我们看一下我们的用户资源：

```java
@JsonApiResource(type = "users")
public class User {

    @JsonApiId
    private Long id;

    private String name;

    private String email;
}
```

注意：

-   @JsonApiResource注解用来定义我们的资源User
-   @JsonApiId注解用于定义资源标识

非常简单-这个例子的持久化将是一个Spring Data Repository：

```java
public interface UserRepository extends JpaRepository<User, Long> {
}
```

## 4. 资源Repository

接下来，让我们讨论一下我们的资源Repository-每个资源都应该有一个ResourceRepositoryV2来发布其上可用的API操作：

```java
@Component
public class UserResourceRepository implements ResourceRepositoryV2<User, Long> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findOne(Long id, QuerySpec querySpec) {
        Optional<User> user = userRepository.findById(id);
        return user.isPresent()? user.get() : null;
    }

    @Override
    public ResourceList<User> findAll(QuerySpec querySpec) {
        return querySpec.apply(userRepository.findAll());
    }

    @Override
    public ResourceList<User> findAll(Iterable<Long> ids, QuerySpec querySpec) {
        return querySpec.apply(userRepository.findAllById(ids));
    }

    @Override
    public <S extends User> S save(S entity) {
        return userRepository.save(entity);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Class<User> getResourceClass() {
        return User.class;
    }

    @Override
    public <S extends User> S create(S entity) {
        return save(entity);
    }
}
```

这里有一个简短的说明-这当然**与Spring控制器非常相似**。

## 5. Katharsis配置

当我们使用katharsis-spring时，我们需要做的就是在我们的Spring Boot应用程序中导入KatharsisConfigV3：

```java
@Import(KatharsisConfigV3.class)
```

并在我们的application.properties中配置Katharsis参数：

```properties
katharsis.domainName=http://localhost:8080
katharsis.pathPrefix=/
```

有了它，我们现在可以开始使用API了；例如：

-   GET [http://localhost:8080/users](http://localhost:8080/users)：获取所有用户
-   POST [http://localhost:8080/users](http://localhost:8080/users)：添加新用户等

## 6. 实体关系

接下来，让我们讨论如何在我们的JSON API中处理实体关系。

### 6.1 角色资源

首先，让我们介绍一个新资源-角色：

```java
@JsonApiResource(type = "roles")
public class Role {

    @JsonApiId
    private Long id;

    private String name;

    @JsonApiRelation
    private Set<User> users;
}
```

然后在用户和角色之间建立多对多关系：

```java
@JsonApiRelation(serialize=SerializeType.EAGER)
private Set<Role> roles;
```

### 6.2 RoleResourceRepository

这是我们的角色资源Repository：

```java
@Component
public class RoleResourceRepository implements ResourceRepositoryV2<Role, Long> {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role findOne(Long id, QuerySpec querySpec) {
        Optional<Role> role = roleRepository.findById(id);
        return role.isPresent()? role.get() : null;
    }

    @Override
    public ResourceList<Role> findAll(QuerySpec querySpec) {
        return querySpec.apply(roleRepository.findAll());
    }

    @Override
    public ResourceList<Role> findAll(Iterable<Long> ids, QuerySpec querySpec) {
        return querySpec.apply(roleRepository.findAllById(ids));
    }

    @Override
    public <S extends Role> S save(S entity) {
        return roleRepository.save(entity);
    }

    @Override
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public Class<Role> getResourceClass() {
        return Role.class;
    }

    @Override
    public <S extends Role> S create(S entity) {
        return save(entity);
    }
}
```

重要的是要明白，这个单一的资源Repository不处理关系方面-它需要一个单独的Repository。

### 6.3 关系Repository

为了处理用户-角色之间的多对多关系，我们需要创建一个新样式的Repository：

```java
@Component
public class UserToRoleRelationshipRepository implements RelationshipRepositoryV2<User, Long, Role, Long> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void setRelation(User User, Long roleId, String fieldName) {}

    @Override
    public void setRelations(User user, Iterable<Long> roleIds, String fieldName) {
        Set<Role> roles = new HashSet<Role>();
        roles.addAll(roleRepository.findAllById(roleIds));
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    public void addRelations(User user, Iterable<Long> roleIds, String fieldName) {
        Set<Role> roles = user.getRoles();
        roles.addAll(roleRepository.findAllById(roleIds));
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    public void removeRelations(User user, Iterable<Long> roleIds, String fieldName) {
        Set<Role> roles = user.getRoles();
        roles.removeAll(roleRepository.findAllById(roleIds));
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    public Role findOneTarget(Long sourceId, String fieldName, QuerySpec querySpec) {
        return null;
    }

    @Override
    public ResourceList<Role> findManyTargets(Long sourceId, String fieldName, QuerySpec querySpec) {
        final Optional<User> userOptional = userRepository.findById(sourceId);
        User user = userOptional.isPresent() ? userOptional.get() : new User();
        return  querySpec.apply(user.getRoles());
    }

    @Override
    public Class<User> getSourceResourceClass() {
        return User.class;
    }

    @Override
    public Class<Role> getTargetResourceClass() {
        return Role.class;
    }
}
```

我们在这里忽略关系Repository中的单数方法。

## 7. 测试

最后，让我们分析一些请求，真正理解JSON-API输出是什么样的。

我们将开始检索单个用户资源(id = 2)：

GET [http://localhost:8080/users/2](http://localhost:8080/users/2)

```json
{
    "data": {
        "type": "users",
        "id": "2",
        "attributes": {
            "email": "tom@test.com",
            "username": "tom"
        },
        "relationships": {
            "roles": {
                "links": {
                    "self": "http://localhost:8080/users/2/relationships/roles",
                    "related": "http://localhost:8080/users/2/roles"
                }
            }
        },
        "links": {
            "self": "http://localhost:8080/users/2"
        }
    },
    "included": [
        {
            "type": "roles",
            "id": "1",
            "attributes": {
                "name": "ROLE_USER"
            },
            "relationships": {
                "users": {
                    "links": {
                        "self": "http://localhost:8080/roles/1/relationships/users",
                        "related": "http://localhost:8080/roles/1/users"
                    }
                }
            },
            "links": {
                "self": "http://localhost:8080/roles/1"
            }
        }
    ]
}
```

要点：

-   资源的主要属性可以在**data.attributes**中找到
-   资源的主要关系可以在**data.relationships**中找到
-   当我们使用@JsonApiRelation(serialize=SerializeType.EAGER)作为角色关系时，它包含在JSON中并在**included**节点中找到

接下来-让我们获取包含角色的集合资源：

GET [http://localhost:8080/roles](http://localhost:8080/roles)

```json
{
    "data": [
        {
            "type": "roles",
            "id": "1",
            "attributes": {
                "name": "ROLE_USER"
            },
            "relationships": {
                "users": {
                    "links": {
                        "self": "http://localhost:8080/roles/1/relationships/users",
                        "related": "http://localhost:8080/roles/1/users"
                    }
                }
            },
            "links": {
                "self": "http://localhost:8080/roles/1"
            }
        },
        {
            "type": "roles",
            "id": "2",
            "attributes": {
                "name": "ROLE_ADMIN"
            },
            "relationships": {
                "users": {
                    "links": {
                        "self": "http://localhost:8080/roles/2/relationships/users",
                        "related": "http://localhost:8080/roles/2/users"
                    }
                }
            },
            "links": {
                "self": "http://localhost:8080/roles/2"
            }
        }
    ],
    "included": [
    ]
}
```

这里的快速收获是我们获得了系统中的所有角色-作为**data**节点中的数组。

## 8. 总结

JSON-API是一个很棒的规范-最终以我们在API中使用JSON的方式添加了一些结构，并真正为真正的超媒体API提供支持。

这篇文章探讨了一种在Spring应用程序中设置它的方法。但不管那个实现如何，在我看来，规范本身是非常非常有前途的工作。