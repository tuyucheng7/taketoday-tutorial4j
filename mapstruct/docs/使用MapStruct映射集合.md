## 1. 概述

在本教程中，我们将学习如何使用 MapStruct 映射对象集合。

由于本文已经假设对 MapStruct 有基本的了解，因此初学者应该首先查看我们的[MapStruct 快速指南](https://www.baeldung.com/mapstruct)。

## 2.映射集合

通常，使用MapStruct映射集合的工作方式与简单类型相同。

基本上，我们必须创建一个简单的接口或抽象类，并声明映射方法。根据我们的声明，MapStruct 会自动生成映射代码。通常，生成的代码将遍历源集合，将每个元素转换为目标类型，并将它们中的每一个都包含在目标集合中。

让我们看一个简单的例子。

### 2.1. 映射列表

首先，我们将考虑一个简单的 POJO 作为映射器的映射源：

```java
public class Employee {
    private String firstName;
    private String lastName;

    // constructor, getters and setters
}

```

目标将是一个简单的 DTO：

```java
public class EmployeeDTO {

    private String firstName;
    private String lastName;

    // getters and setters
}
```

接下来，我们将定义我们的映射器：

```java
@Mapper
public interface EmployeeMapper {
    List<EmployeeDTO> map(List<Employee> employees);
}

```

最后，让我们看看从我们的EmployeeMapper接口生成的 MapStruct 代码：

```java
public class EmployeeMapperImpl implements EmployeeMapper {

    @Override
    public List<EmployeeDTO> map(List<Employee> employees) {
        if (employees == null) {
            return null;
        }

        List<EmployeeDTO> list = new ArrayList<EmployeeDTO>(employees.size());
        for (Employee employee : employees) {
            list.add(employeeToEmployeeDTO(employee));
        }

        return list;
    }

    protected EmployeeDTO employeeToEmployeeDTO(Employee employee) {
        if (employee == null) {
            return null;
        }

        EmployeeDTO employeeDTO = new EmployeeDTO();

        employeeDTO.setFirstName(employee.getFirstName());
        employeeDTO.setLastName(employee.getLastName());

        return employeeDTO;
    }
}

```

需要注意的一件重要事情是MapStruct 自动为我们生成了从Employee到EmployeeDTO的映射。

有些情况下这是不可能的。例如，假设我们要将Employee模型映射到以下模型：

```java
public class EmployeeFullNameDTO {

    private String fullName;

    // getter and setter
}
```

在这种情况下，如果我们只是声明从 Employee 列表到 EmployeeFullNameDTO 列表的映射方法，我们将收到编译时错误或警告：

```plaintext
Warning:(11, 31) java: Unmapped target property: "fullName". 
  Mapping from Collection element "com.baeldung.mapstruct.mappingCollections.model.Employee employee" to 
  "com.baeldung.mapstruct.mappingCollections.dto.EmployeeFullNameDTO employeeFullNameDTO".
```

基本上，这意味着，在这种情况下，MapStruct 无法为我们自动生成映射。因此，我们需要手动定义Employee和EmployeeFullNameDTO之间的映射。

鉴于这些要点，让我们手动定义它：

```java
@Mapper
public interface EmployeeFullNameMapper {

    List<EmployeeFullNameDTO> map(List<Employee> employees);

    default EmployeeFullNameDTO map(Employee employee) {
        EmployeeFullNameDTO employeeInfoDTO = new EmployeeFullNameDTO();
        employeeInfoDTO.setFullName(employee.getFirstName() + " " + employee.getLastName());

        return employeeInfoDTO;
    }
}
```

生成的代码将使用我们定义的方法将源List的元素映射到目标List。

这也适用于一般情况。如果我们定义了一个将源元素类型映射到目标元素类型的方法，MapStruct 将使用它。

### 2.2. 映射集和地图

使用 MapStruct 映射集的工作方式与列表相同。例如，假设我们要将一组Employee实例映射到一组EmployeeDTO实例。

和以前一样，我们需要一个映射器：

```java
@Mapper
public interface EmployeeMapper {

    Set<EmployeeDTO> map(Set<Employee> employees);
}
```

然后 MapStruct 将生成相应的代码：

```java
public class EmployeeMapperImpl implements EmployeeMapper {

    @Override
    public Set<EmployeeDTO> map(Set<Employee> employees) {
        if (employees == null) {
            return null;
        }

        Set<EmployeeDTO> set = 
          new HashSet<EmployeeDTO>(Math.max((int)(employees.size() / .75f ) + 1, 16));
        for (Employee employee : employees) {
            set.add(employeeToEmployeeDTO(employee));
        }

        return set;
    }

    protected EmployeeDTO employeeToEmployeeDTO(Employee employee) {
        if (employee == null) {
            return null;
        }

        EmployeeDTO employeeDTO = new EmployeeDTO();

        employeeDTO.setFirstName(employee.getFirstName());
        employeeDTO.setLastName(employee.getLastName());

        return employeeDTO;
    }
}
```

这同样适用于地图。假设我们要将Map<String, Employee>映射到Map<String, EmployeeDTO>。

我们可以按照与之前相同的步骤进行操作：

```java
@Mapper
public interface EmployeeMapper {

    Map<String, EmployeeDTO> map(Map<String, Employee> idEmployeeMap);
}
```

MapStruct 完成了它的工作：

```java
public class EmployeeMapperImpl implements EmployeeMapper {

    @Override
    public Map<String, EmployeeDTO> map(Map<String, Employee> idEmployeeMap) {
        if (idEmployeeMap == null) {
            return null;
        }

        Map<String, EmployeeDTO> map = new HashMap<String, EmployeeDTO>(Math.max((int)(idEmployeeMap.size() / .75f) + 1, 16));

        for (java.util.Map.Entry<String, Employee> entry : idEmployeeMap.entrySet()) {
            String key = entry.getKey();
            EmployeeDTO value = employeeToEmployeeDTO(entry.getValue());
            map.put(key, value);
        }

        return map;
    }

    protected EmployeeDTO employeeToEmployeeDTO(Employee employee) {
        if (employee == null) {
            return null;
        }

        EmployeeDTO employeeDTO = new EmployeeDTO();

        employeeDTO.setFirstName(employee.getFirstName());
        employeeDTO.setLastName(employee.getLastName());

        return employeeDTO;
    }
}
```

## 3. 集合映射策略

通常，我们需要映射具有父子关系的数据类型。通常，我们有一个数据类型(父)，它的字段是另一个数据类型(子)的集合。

对于这种情况，MapStruct 提供了一种方法来选择如何将子项设置或添加到父类型。特别是，@Mapper注解有一个collectionMappingStrategy属性，可以是ACCESSOR_ONLY、SETTER_PREFERRED、ADDER_PREFERRED或TARGET_IMMUTABLE。

所有这些值都指的是应将子项设置或添加到父类型的方式。默认值为ACCESSOR_ONLY，表示只能使用accessors来设置children的Collection。

当Collection字段的设置器不可用但我们有加法器时，此选项会派上用场。另一种有用的情况是Collection在父类型上是不可变的。通常，我们在生成的目标类型中会遇到这些情况。

### 3.1. ACCESSOR_ONLY集合映射策略

让我们看一个例子来更好地理解它是如何工作的。

我们将创建一个Company类作为我们的映射源：

```java
public class Company {

    private List<Employee> employees;

   // getter and setter
}
```

我们映射的目标将是一个简单的 DTO：

```java
public class CompanyDTO {

    private List<EmployeeDTO> employees;

    public List<EmployeeDTO> getEmployees() {
        return employees;
    }

    public void setEmployees(List<EmployeeDTO> employees) {
        this.employees = employees;
    }

    public void addEmployee(EmployeeDTO employeeDTO) {
        if (employees == null) {
            employees = new ArrayList<>();
        }

        employees.add(employeeDTO);
    }
}
```

请注意，我们同时提供了设置器setEmployees和加法器addEmployee。此外，对于加法器，我们负责集合初始化。

现在，假设我们要将Company映射到CompanyDTO。然后，和以前一样，我们需要一个映射器：

```java
@Mapper(uses = EmployeeMapper.class)
public interface CompanyMapper {
    CompanyDTO map(Company company);
}
```

请注意，我们重用了EmployeeMapper和默认的collectionMappingStrategy。

现在让我们看一下 MapStruct 生成的代码：

```java
public class CompanyMapperImpl implements CompanyMapper {

    private final EmployeeMapper employeeMapper = Mappers.getMapper(EmployeeMapper.class);

    @Override
    public CompanyDTO map(Company company) {
        if (company == null) {
            return null;
        }

        CompanyDTO companyDTO = new CompanyDTO();

        companyDTO.setEmployees(employeeMapper.map(company.getEmployees()));

        return companyDTO;
    }
}
```

如我们所见，MapStruct使用设置器setEmployees来设置EmployeeDTO实例列表。发生这种情况是因为我们使用了默认的collectionMappingStrategy， ACCESSOR_ONLY。

MapStruct 还在 EmployeeMapper 中找到了一个将List<Employee> 映射到 List<EmployeeDTO>的方法，并重用了它。

### 3.2. ADDER_PREFERRED集合映射策略

相反，假设我们使用ADDER_PREFERRED作为collectionMappingStrategy：

```java
@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        uses = EmployeeMapper.class)
public interface CompanyMapperAdderPreferred {
    CompanyDTO map(Company company);
}
```

同样，我们想重用 EmployeeMapper。但是，我们需要先显式添加一个可以将单个 Employee转换为 EmployeeDTO的方法：

```java
@Mapper
public interface EmployeeMapper {
    EmployeeDTO map(Employee employee);
    List map(List employees);
    Set map(Set employees);
    Map<String, EmployeeDTO> map(Map<String, Employee> idEmployeeMap);
}
```

这是因为 MapStruct 会使用加法器将EmployeeDTO实例一一添加到目标CompanyDTO实例中：

```java
public class CompanyMapperAdderPreferredImpl implements CompanyMapperAdderPreferred {

    private final EmployeeMapper employeeMapper = Mappers.getMapper( EmployeeMapper.class );

    @Override
    public CompanyDTO map(Company company) {
        if ( company == null ) {
            return null;
        }

        CompanyDTO companyDTO = new CompanyDTO();

        if ( company.getEmployees() != null ) {
            for ( Employee employee : company.getEmployees() ) {
                companyDTO.addEmployee( employeeMapper.map( employee ) );
            }
        }

        return companyDTO;
    }
}
```

如果加法器不可用，将使用设置器。

我们可以在 MapStruct 的[参考文档](https://mapstruct.org/documentation/stable/reference/html/#collection-mapping-strategies)中找到所有集合映射策略的完整描述。

## 4. 目标收集的实现类型

MapStruct 支持集合接口作为映射方法的目标类型。

在这种情况下，生成的代码中使用了一些默认实现。例如，List的默认实现是ArrayList，从上面的示例中可以看出。

[我们可以在参考文档](https://mapstruct.org/documentation/stable/reference/html/#implementation-types-for-collection-mappings)中找到 MapStruct 支持的接口的完整列表，以及它为每个接口使用的默认实现。

## 5.总结

在本文中，我们探讨了如何使用 MapStruct 映射集合。

首先，我们研究了如何映射不同类型的集合。然后，我们学习了如何使用集合映射策略自定义父子关系映射器。

在此过程中，我们强调了使用 MapStruct 映射集合时要记住的关键点和注意事项。