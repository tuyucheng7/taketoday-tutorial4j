一个开源 Java 库，可用于将 Java 对象序列化和反序列化为(和从)JSON。

JSON是Java Script Object Notation，一种开放标准格式，它使用人类可读的文本来传输由属性值对组成的数据对象。

特征：-

1.  紧凑的可读输出
2.  默认处理空对象字段——默认情况下，它们不出现在输出中
3.  页面定位器层次结构的创建非常容易
4.  可以在文件中轻松定义页面定位器继承
5.  为任何定位器地址遍历 JSON OR 定位器文件不那么麻烦
6.  无需花时间向任何人解释 OR 定位器 JSON 文件，它非常易于阅读，任何人都可以预测页面定位器配置或页面上的定位器序列

在本章中，我们将学习如何使用 JSON 为 Selenium 创建对象存储库。让我们创建一个JSON文件作为示例小部分的示例，快照如下：-

![杰森_1](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Json_1.png)

### 第 1 步：为页面的以上部分创建 OR

-   命名约定：“ObjectRepository.json”

![杰森_2](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Json_2.png)

-   强调
    -   {} --> 文件的开始和结束
    -   "HOME" --> Key/Element Name on page which holds elements Locator address
    -   : --> Key 和 Locator Value 之间的分隔符
    -   "XPATH;home" --> 带有定位器标识符和“;”的定位器值 标识符和定位器值之间的分隔符
-   带有子菜单的“培训”部分
    -   "Menu_TRAININGS" --> 表示训练的键也有菜单
    -   “SubMenu” --> 表示“TRININGS”子菜单开始的键
    -   “ SELENIUM” --> 键，它是菜单“TRAININGS”的子菜单
    -   “APPIUM” --> 与“SELENIUM”相同
-   优点
    -   创建页面元素之间的父子关系
    -   很容易遍历巨大的OR
    -   测试人员可以根据页面渲染或页面视图创建继承
    -   使用一些重要的名称轻松划分页面子部分
    -   如果从下到上或从上到下轻松发现任何错误，可以轻松遍历任何定位器

### 第 2 步：如何在 JAVA 代码中获取键/元素定位器地址值

-   导入“ json-path-0.9.0 ”
-   导入“ json-path-0.9.0-dependencies ”
-   下面的快照：-

![杰森_3](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Json_3.png)

-   为 JSON 文件声明文件变量

![Json_4](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Json_4.png)



-   使用为 OR 创建的 JSON 文件初始化“ jsonfile ”变量

![杰森_5](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Json_5.png)



-   只是，开始从 OR 获取数据

![杰森_6](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Json_6.png)

-   读取(jsonfile，“$。”+“HOME”)
-   传递文件变量初始化“ jsonfile ”与“ $ ”结合要获取定位器值的键，例如“ HOME ”

“ Menu_TRAININGS.SubMenu[0].SELENIUM ”，“ SubMenu ”是一个索引为“ 0 ”的数组，它包含子菜单的键，如“ SELENIUM ”和“ APPIUM ”

### 第 3 步：以上代码的输出是

![杰森_7](https://www.toolsqa.com/gallery/selnium%20webdriver/7.Json_7.png)

-   CSS;tutorial --> 反映了对于“ TUTORIALS ”键，我们使用的定位符是“ CSS ”，CSS 的值为“ tutorial ”，这个“ tutorial ”将被页面上Key 的实际CSS 替换。

最后我们可以说，你只需要编写小代码来遍历这个输出字符串，通过它你可以使用分隔符“ ; ”来划分定位器标识符和定位器值。”或你选择的任何要传递给“ driver.findelement(BY.locator identifier(locator value)); ”。

未来学习愉快………………..！安库尔