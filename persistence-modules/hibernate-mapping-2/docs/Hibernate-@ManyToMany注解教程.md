## 1. 简介

在本快速教程中，我们将快速了解如何使用@ManyToMany批注在 Hibernate 中指定此类关系。

## 2. 一个典型的例子

让我们从一个简单的实体关系图开始——它显示了两个实体雇员和项目之间的多对多关联：

[![新 300x59](https://www.baeldung.com/wp-content/uploads/2017/09/New-300x59.png)](https://www.baeldung.com/wp-content/uploads/2017/09/New.png)

在这种情况下，任何给定的员工都可以分配给多个项目，并且一个项目可能有多个员工为它工作，从而导致两者之间存在多对多关联。

我们有一个以employee_id为主键的员工表和一个以project_id为主键的项目表。这里需要一个连接表employee_project来连接双方。

## 3.数据库设置

假设我们已经创建了一个名为spring_hibernate_many_to_many 的数据库。

我们还需要创建employee和project表以及以employee_id和project_id作为外键的employee_project连接表：

```sql
CREATE TABLE `employee` (
  `employee_id` int(11) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`employee_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;

CREATE TABLE `project` (
  `project_id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;

CREATE TABLE `employee_project` (
  `employee_id` int(11) NOT NULL,
  `project_id` int(11) NOT NULL,
  PRIMARY KEY (`employee_id`,`project_id`),
  KEY `project_id` (`project_id`),
  CONSTRAINT `employee_project_ibfk_1` 
   FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`),
  CONSTRAINT `employee_project_ibfk_2` 
   FOREIGN KEY (`project_id`) REFERENCES `project` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

```

数据库设置好后，下一步就是准备 Maven 依赖项和 Hibernate 配置。[有关这方面的信息，请参阅有关Guide to Hibernate4 with Spring 的](https://www.baeldung.com/hibernate-4-spring)文章

## 4.模型类

需要使用 JPA 注解创建模型类Employee和Project ：

```java
@Entity
@Table(name = "Employee")
public class Employee { 
    // ...
 
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
        name = "Employee_Project", 
        joinColumns = { @JoinColumn(name = "employee_id") }, 
        inverseJoinColumns = { @JoinColumn(name = "project_id") }
    )
    Set<Project> projects = new HashSet<>();
   
    // standard constructor/getters/setters
}
@Entity
@Table(name = "Project")
public class Project {    
    // ...  
 
    @ManyToMany(mappedBy = "projects")
    private Set<Employee> employees = new HashSet<>();
    
    // standard constructors/getters/setters   
}
```

正如我们所见，Employee类和Project类都相互引用，这意味着它们之间的关联是双向的。

为了映射多对多关联，我们使用@ManyToMany、@JoinTable和@JoinColumn注解。让我们仔细看看它们。

@ManyToMany注解在两个类中都用于创建实体之间的多对多关系。

该关联有两个方面，即拥有方和相反方。在我们的示例中，拥有方是Employee ，因此通过在Employee类中使用@JoinTable注解在拥有方指定连接表。@JoinTable用于定义连接/链接表。在本例中，它是Employee_Project。

@JoinColumn注解用于指定与主表的连接/链接列。此处，连接列是employee_id，而project_id是反向连接列，因为Project位于关系的反面。

在Project类中，@ManyToMany注解中使用了mappedBy属性，表示employees集合被owner端的projects集合映射。

## 5.执行

为了查看多对多注解的运行情况，我们可以编写以下 JUnit 测试：

```java
public class HibernateManyToManyAnnotationMainIntegrationTest {
	private static SessionFactory sessionFactory;
	private Session session;

	//...

	@Test
        public void givenSession_whenRead_thenReturnsMtoMdata() {
	    prepareData();
       	    @SuppressWarnings("unchecked")
	    List<Employee> employeeList = session.createQuery("FROM Employee").list();
            @SuppressWarnings("unchecked")
	    List<Project> projectList = session.createQuery("FROM Project").list();
            assertNotNull(employeeList);
            assertNotNull(projectList);
            assertEquals(2, employeeList.size());
            assertEquals(2, projectList.size());
        
            for(Employee employee : employeeList) {
               assertNotNull(employee.getProjects());
               assertEquals(2, employee.getProjects().size());
            }
            for(Project project : projectList) {
               assertNotNull(project.getEmployees());
               assertEquals(2, project.getEmployees().size());
            }
        }

	private void prepareData() {
	    String[] employeeData = { "Peter Oven", "Allan Norman" };
	    String[] projectData = { "IT Project", "Networking Project" };
	    Set<Project> projects = new HashSet<Project>();

	    for (String proj : projectData) {
		projects.add(new Project(proj));
	    }

	    for (String emp : employeeData) {
		Employee employee = new Employee(emp.split(" ")[0], emp.split(" ")[1]);
		employee.setProjects(projects);
			
	        for (Project proj : projects) {
		    proj.getEmployees().add(employee);
		}
			
		session.persist(employee);
	    }
	}
	
	//...
}
```

我们可以看到在数据库中创建的两个实体之间的多对多关系：employee、project和employee_project表，示例数据表示该关系。

## 六. 总结

在本教程中，我们看到了如何使用 Hibernate 的多对多注解创建映射，与创建 XML 映射文件相比，这是一种更方便的对应方法。