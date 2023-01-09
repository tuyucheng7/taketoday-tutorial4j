## 1. 概述

在本教程中，我们将展示如何使用 Google Guava 的Table接口及其多个实现。

Guava 的Table是一个集合，表示包含行、列和相关单元格值的类似表的结构。行和列充当有序的键对。

## 2. Google Guava 的表格

让我们看看如何使用Table类。

### 2.1. Maven 依赖

让我们首先在pom.xml中添加 Google 的 Guava 库依赖项：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")检查最新版本的依赖项。

### 2.2. 关于

如果我们要使用核心Java中存在的集合来表示 Guava 的表，那么该结构将是一个行映射，其中每行包含一个列映射以及相关的单元格值。

表表示一个特殊的映射，其中可以以组合方式指定两个键以引用单个值。

它类似于创建地图的地图，例如Map<UniversityName, Map<CoursesOffered, SeatAvailable>>。表格也是代表战舰游戏板的完美方式。

## 3.创建

你可以通过多种方式创建Table的实例：

-   使用内部使用LinkedHashMap的类HashBasedTable的

    创建

    方法：

    

    

    

    

    ```java
    Table<String, String, Integer> universityCourseSeatTable 
      = HashBasedTable.create();
    ```

-   如果我们需要一个

    Table

    ，其行键和列键需要按其自然顺序或通过提供比较器进行排序，你可以使用名为TreeBasedTable的类的create方法创建

    Table

    的实例，该类在内部使用TreeMap：

    

    

    

    

    

    

    ```java
    Table<String, String, Integer> universityCourseSeatTable
      = TreeBasedTable.create();
    
    ```

-   如果我们事先知道行键和列键并且表大小是固定的，请使用类ArrayTable中的

    create

    方法：

    

    

    ```java
    List<String> universityRowTable 
      = Lists.newArrayList("Mumbai", "Harvard");
    List<String> courseColumnTables 
      = Lists.newArrayList("Chemical", "IT", "Electrical");
    Table<String, String, Integer> universityCourseSeatTable
      = ArrayTable.create(universityRowTable, courseColumnTables);
    
    ```

-   如果我们打算创建一个

    Table

    的不可变实例，其内部数据永远不会改变，请使用

    ImmutableTable

    类(创建遵循构建器模式)：

    ```java
    Table<String, String, Integer> universityCourseSeatTable
      = ImmutableTable.<String, String, Integer> builder()
      .put("Mumbai", "Chemical", 120).build();
    
    ```

## 4.使用

让我们从一个显示Table用法的简单示例开始。

### 4.1. 恢复

如果我们知道行键和列键，那么我们可以获得与行键和列键关联的值：

```java
@Test
public void givenTable_whenGet_returnsSuccessfully() {
    Table<String, String, Integer> universityCourseSeatTable 
      = HashBasedTable.create();
    universityCourseSeatTable.put("Mumbai", "Chemical", 120);
    universityCourseSeatTable.put("Mumbai", "IT", 60);
    universityCourseSeatTable.put("Harvard", "Electrical", 60);
    universityCourseSeatTable.put("Harvard", "IT", 120);

    int seatCount 
      = universityCourseSeatTable.get("Mumbai", "IT");
    Integer seatCountForNoEntry 
      = universityCourseSeatTable.get("Oxford", "IT");

    assertThat(seatCount).isEqualTo(60);
    assertThat(seatCountForNoEntry).isEqualTo(null);
}
```

### 4.2. 检查条目

我们可以根据以下内容检查表中是否存在条目：

-   行键
-   列键
-   行键和列键
-   价值

让我们看看如何检查条目的存在：

```java
@Test
public void givenTable_whenContains_returnsSuccessfully() {
    Table<String, String, Integer> universityCourseSeatTable 
      = HashBasedTable.create();
    universityCourseSeatTable.put("Mumbai", "Chemical", 120);
    universityCourseSeatTable.put("Mumbai", "IT", 60);
    universityCourseSeatTable.put("Harvard", "Electrical", 60);
    universityCourseSeatTable.put("Harvard", "IT", 120);

    boolean entryIsPresent
      = universityCourseSeatTable.contains("Mumbai", "IT");
    boolean courseIsPresent 
      = universityCourseSeatTable.containsColumn("IT");
    boolean universityIsPresent 
      = universityCourseSeatTable.containsRow("Mumbai");
    boolean seatCountIsPresent 
      = universityCourseSeatTable.containsValue(60);

    assertThat(entryIsPresent).isEqualTo(true);
    assertThat(courseIsPresent).isEqualTo(true);
    assertThat(universityIsPresent).isEqualTo(true);
    assertThat(seatCountIsPresent).isEqualTo(true);
}
```

### 4.3. 移动

我们可以通过提供行键和列键从表中删除一个条目：

```java
@Test
public void givenTable_whenRemove_returnsSuccessfully() {
    Table<String, String, Integer> universityCourseSeatTable
      = HashBasedTable.create();
    universityCourseSeatTable.put("Mumbai", "Chemical", 120);
    universityCourseSeatTable.put("Mumbai", "IT", 60);

    int seatCount 
      = universityCourseSeatTable.remove("Mumbai", "IT");

    assertThat(seatCount).isEqualTo(60);
    assertThat(universityCourseSeatTable.remove("Mumbai", "IT")).
      isEqualTo(null);
}

```

### 4.4. 单元格值映射的行键

通过提供列键，我们可以获得一个Map表示，其中键作为行，值作为CellValue ：

```java
@Test
public void givenTable_whenColumn_returnsSuccessfully() {
    Table<String, String, Integer> universityCourseSeatTable 
      = HashBasedTable.create();
    universityCourseSeatTable.put("Mumbai", "Chemical", 120);
    universityCourseSeatTable.put("Mumbai", "IT", 60);
    universityCourseSeatTable.put("Harvard", "Electrical", 60);
    universityCourseSeatTable.put("Harvard", "IT", 120);

    Map<String, Integer> universitySeatMap 
      = universityCourseSeatTable.column("IT");

    assertThat(universitySeatMap).hasSize(2);
    assertThat(universitySeatMap.get("Mumbai")).isEqualTo(60);
    assertThat(universitySeatMap.get("Harvard")).isEqualTo(120);
}

```

### 4.5. 表的地图表示

我们可以使用columnMap方法获得Map<UniversityName, Map<CoursesOffered, SeatAvailable>>表示：

```java
@Test
public void givenTable_whenColumnMap_returnsSuccessfully() {
    Table<String, String, Integer> universityCourseSeatTable 
      = HashBasedTable.create();
    universityCourseSeatTable.put("Mumbai", "Chemical", 120);
    universityCourseSeatTable.put("Mumbai", "IT", 60);
    universityCourseSeatTable.put("Harvard", "Electrical", 60);
    universityCourseSeatTable.put("Harvard", "IT", 120);

    Map<String, Map<String, Integer>> courseKeyUniversitySeatMap 
      = universityCourseSeatTable.columnMap();

    assertThat(courseKeyUniversitySeatMap).hasSize(3);
    assertThat(courseKeyUniversitySeatMap.get("IT")).hasSize(2);
    assertThat(courseKeyUniversitySeatMap.get("Electrical")).hasSize(1);
    assertThat(courseKeyUniversitySeatMap.get("Chemical")).hasSize(1);
}

```

### 4.6. 单元格值映射的列键

通过提供行键，我们可以获得一个Map表示，其中键作为列，值作为CellValue ：

```java
@Test
public void givenTable_whenRow_returnsSuccessfully() {
    Table<String, String, Integer> universityCourseSeatTable 
      = HashBasedTable.create();
    universityCourseSeatTable.put("Mumbai", "Chemical", 120);
    universityCourseSeatTable.put("Mumbai", "IT", 60);
    universityCourseSeatTable.put("Harvard", "Electrical", 60);
    universityCourseSeatTable.put("Harvard", "IT", 120);

    Map<String, Integer> courseSeatMap 
      = universityCourseSeatTable.row("Mumbai");

    assertThat(courseSeatMap).hasSize(2);
    assertThat(courseSeatMap.get("IT")).isEqualTo(60);
    assertThat(courseSeatMap.get("Chemical")).isEqualTo(120);
}

```

### 4.7. 获取不同的行键

我们可以使用rowKeySet方法从表中获取所有行键：

```java
@Test
public void givenTable_whenRowKeySet_returnsSuccessfully() {
    Table<String, String, Integer> universityCourseSeatTable
      = HashBasedTable.create();
    universityCourseSeatTable.put("Mumbai", "Chemical", 120);
    universityCourseSeatTable.put("Mumbai", "IT", 60);
    universityCourseSeatTable.put("Harvard", "Electrical", 60);
    universityCourseSeatTable.put("Harvard", "IT", 120);

    Set<String> universitySet = universityCourseSeatTable.rowKeySet();

    assertThat(universitySet).hasSize(2);
}

```

### 4.8. 获取不同的列键

我们可以使用columnKeySet方法从表中获取所有列键：

```java
@Test
public void givenTable_whenColKeySet_returnsSuccessfully() {
    Table<String, String, Integer> universityCourseSeatTable
      = HashBasedTable.create();
    universityCourseSeatTable.put("Mumbai", "Chemical", 120);
    universityCourseSeatTable.put("Mumbai", "IT", 60);
    universityCourseSeatTable.put("Harvard", "Electrical", 60);
    universityCourseSeatTable.put("Harvard", "IT", 120);

    Set<String> courseSet = universityCourseSeatTable.columnKeySet();

    assertThat(courseSet).hasSize(3);
}

```

## 5.总结

在本教程中，我们演示了Guava 库中Table类的方法。Table类提供了一个集合，表示包含行、列和关联单元格值的类似表的结构。