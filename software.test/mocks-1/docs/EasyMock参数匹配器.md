## 1. 概述

在本教程中，我们介绍EasyMock参数匹配器，**我们将讨论不同类型的预定义匹配器以及如何创建自定义匹配器**。

我们已经在[介绍EasyMock]()的文章中介绍了EasyMock的基础知识，因此你可能需要先阅读它以熟悉EasyMock。

## 2. 简单的mock示例

在本教程中，我们使用非常基本的用户服务作为例子，这是我们简单的UserService接口：

```java
public interface UserService {
    boolean addUser(User user);
    List<User> findByEmail(String email);
    List<User> findByAge(double age);  
}
```

以及相关的用户模型：

```java
public class User {
    private long id;
    private String firstName;
    private String lastName;
    private double age;
    private String email;

    // standard constructor, getters, setters
}
```

因此，我们首先mock我们的UserService以在我们的示例中使用它：

```java
private UserService userService = mock(UserService.class);
```

## 3. 相等匹配器

首先，我们使用eq()匹配器来匹配新添加的User：

```java
@Test
void givenUserService_whenAddNewUser_thenOK() {
	expect(userService.addUser(eq(new User()))).andReturn(true);
	replay(userService);
    
	boolean result = userService.addUser(new User());
    
	verify(userService);
	assertTrue(result);
}
```

此匹配器可用于原始类型和对象，并**对对象使用equals()方法**。

同样，我们可以使用same()匹配器来匹配特定的User：

```java
@Test
void givenUserService_whenAddSpecificUser_thenOK() {
    User user = new User();
    
    expect(userService.addUser(same(user))).andReturn(true);
    replay(userService);

    boolean result = userService.addUser(user);
    verify(userService);
    assertTrue(result);
}
```

same()匹配器使用“==”比较参数，这意味着它在我们的例子中比较User实例。如果我们不使用任何匹配器，则默认情况下使用equals()比较参数。

对于数组，我们还有基于Arrays.equals()方法的aryEq()匹配器。

## 4. any匹配器

有很多any匹配器，例如anyInt()、anyBoolean()、anyDouble()等，**它们指定参数应该具有给定的类型**。

下面是一个使用anyString()将预期电子邮件匹配为任何字符串值的例子：

```java
@Test
void givenUserService_whenSearchForUserByEmail_thenFound() {
	expect(userService.findByEmail(anyString())).andReturn(Collections.emptyList());
	replay(userService);
    
	List<User> result = userService.findByEmail("test@example.com");
	verify(userService);
	
	assertEquals(0, result.size());
}
```

我们还可以使用isA()将参数匹配为特定类的实例：

```java
void givenUserService_whenAddUser_thenOK() {
	expect(userService.addUser(isA(User.class))).andReturn(true);
	replay(userService);
    
	boolean result = userService.addUser(new User());
    
	verify(userService);
	assertTrue(result);
}
```

在这里，我们断言我们期望addUser()方法参数的类型为User。

## 5. 空匹配器

接下来，**我们可以使用isNull()和notNull()匹配器来匹配空值**。

在下面的示例中，如果添加的User值为null，我们将使用isNull()匹配器进行匹配：

```java
@Test
void givenUserService_whenAddNull_thenFail() {
	expect(userService.addUser(isNull())).andReturn(false);
	replay(userService);
    
	boolean result = userService.addUser(null);
	verify(userService);
	assertFalse(result);
}
```

如果添加的User不为空，我们也可以使用notNull()以类似的方式进行匹配：

```java
@Test
void givenUserService_whenAddNotNull_thenOK() {
	expect(userService.addUser(notNull())).andReturn(true);
	replay(userService);
	
	boolean result = userService.addUser(new User());
	verify(userService);
	assertTrue(result);
}
```

## 6. 字符串匹配器

我们可以将多个有用的匹配器与字符串参数一起使用。首先，我们使用startsWith()匹配器来匹配用户的电子邮件前缀：

```java
@Test
void givenUserService_whenSearchForUserByEmailStartsWith_thenFound() {
	expect(userService.findByEmail(startsWith("test")))
			.andReturn(Collections.emptyList());
	replay(userService);
    
	List<User> result = userService.findByEmail("test@example.com");
	verify(userService);
	assertEquals(0, result.size());
}
```

同样，我们可以使用endsWith()匹配器匹配电子邮件后缀：

```java
@Test
void givenUserService_whenSearchForUserByEmailEndsWith_thenFound() {
	expect(userService.findByEmail(endsWith("@example.com")))
			.andReturn(Collections.emptyList());
	replay(userService);
    
	List<User> result = userService.findByEmail("test@example.com");
	verify(userService);
	assertEquals(0, result.size());
}
```

更一般地，我们可以使用contains()将电子邮件与给定的子字符串匹配：

```java
@Test
void givenUserService_whenSearchForUserByEmailContains_thenFound() {
	expect(userService.findByEmail(contains("example")))
			.andReturn(Collections.emptyList());
	replay(userService);
    
	List<User> result = userService.findByEmail("test@example.com");
	verify(userService);
	assertEquals(0, result.size());
}
```

甚至可以使用match()将我们的电子邮件与特定的正则表达式匹配：

```java
@Test
void givenUserService_whenSearchForUserByEmailMatches_thenFound() {
	expect(userService.findByEmail(matches(".+@.+..+")))
			.andReturn(Collections.emptyList());
	replay(userService);
    
	List<User> result = userService.findByEmail("test@exmpale.com");
	verify(userService);
	assertEquals(0, result.size());
}
```

## 7. 数字匹配器

我们也有一些可以使用的数值匹配器，下面是一个使用lt()匹配器将年龄参数匹配为小于100的例子：

```java
@Test
void givenUserService_whenSearchForUserByAgeLess_thenFound() {
	expect(userService.findByAge(lt(100.0)))
			.andReturn(Collections.emptyList());
	replay(userService);
    
	List<User> result = userService.findByAge(20);
	verify(userService);
	assertEquals(0, result.size());
}
```

同样，我们也可以使用geq()将年龄参数匹配为大于或等于10：

```java
@Test
void givenUserService_whenSearchForUserByAgeGreaterThen_thenFound() {
	expect(userService.findByAge(gt(10.0)))
			.andReturn(Collections.emptyList());
	replay(userService);
    
	List<User> result = userService.findByAge(20);
	verify(userService);
	assertEquals(0, result.size());
}
```

还有其他可用的数字匹配器为：

-   lt()：小于给定值
-   leq()：小于或等于
-   gt()：大于
-   geq()：大于或等于

## 8. 组合匹配器

**我们还可以使用and()、or()和not()匹配器组合多个匹配器**。

让我们看看如何结合两个匹配器来验证年龄值既大于10又小于100：

```java
@Test
void givenUserService_whenSearchForUserByAgeRange_thenFound() {
	expect(userService.findByAge(and(gt(10.0), lt(100.0))))
			.andReturn(Collections.emptyList());
	replay(userService);
    
	List<User> result = userService.findByAge(20);
	verify(userService);
	assertEquals(0, result.size());
}
```

另一个例子是将not()与endsWith()组合以匹配不以“.com”结尾的电子邮件：

```java
@Test
void givenUserService_whenSearchForUserByEmailNotEndsWith_thenFound() {
	expect(userService.findByEmail(not(endsWith("com"))))
			.andReturn(Collections.emptyList());
	replay(userService);
    
	List<User> result = userService.findByEmail("test@example.org");
	verify(userService);
	assertEquals(0, result.size());
}
```

## 9. 自定义匹配器

最后，我们介绍如何创建自定义EasyMock匹配器，我们的目标是创建一个简单的minCharCount()匹配器，来匹配长度大于或等于给定值的字符串：

```java
@Test
void givenUserService_whenSearchForUserByEmailCharCount_thenFound() {
	expect(userService.findByEmail(minCharCount(5)))
			.andReturn(Collections.emptyList());
	replay(userService);
    
	List<User> result = userService.findByEmail("test@example.com");
	verify(userService);
	assertEquals(0, result.size());
}
```

要创建自定义参数匹配器，我们需要：

-   创建一个实现IArgumentMatcher接口的新类 
-   使用新的匹配器名称创建一个静态方法，并使用reportMatcher()注册上述类的实例

让我们看看minCharCount()方法中的两个步骤，它在其中声明了一个匿名类：

```java
public static String minCharCount(int count) {
	EasyMock.reportMatcher(new IArgumentMatcher() {
		@Override
		public boolean matches(Object argument) {
			return argument instanceof String && ((String) argument).length() >= count;
		}
        
		@Override
		public void appendTo(StringBuffer buffer) {
			buffer.append("minCharCount(").append(count).append(")");
		}
	});
	return null;
}
```

另外，请注意IArgumentMatcher接口有两个方法：matches()和appendTo()。第一个方法包含匹配器的参数验证逻辑，而第二个方法用于附加匹配器字符串表示以在失败的情况下打印。

## 10. 总结

我们介绍了EasyMock为不同数据类型预定义的参数匹配器，以及如何创建自定义匹配器。