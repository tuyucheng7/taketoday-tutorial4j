## Editing

|           快捷键           |                             操作                             |
|:-----------------------:|:----------------------------------------------------------:|
|      Ctrl + Space       |                    基本代码完成(任何类的名称，方法或变量)                    |
|  Ctrl + Shift + Space   |                 智能代码完成（过滤列表按预期类型划分的方法和变量)                  |
|  Ctrl + Shift + Enter   |                            完成语句                            |
|        Ctrl + P         |                          方法的参数信息                           |
|        Ctrl + Q         |                           快速文档查找                           |
|        Ctrl + F1        |                      在插入符号处显示错误或警告的描述                      |
|      Alt + Insert       | 生成代码(Getters，Setters，Constructors，hashcode/equals，toString |
|        Ctrl + O         |                            重写方法                            |
|        Ctrl + I         |                            实现方法                            |
|     Ctrl + Alt + T      |        包围代码块(if..else，try..catch，for，synchronized等)        |
|        Ctrl + /         |                          注释/取消注释行                          |
|    Ctrl + Shift + /     |                          注释/取消注释块                          |
|        Ctrl + W         |                         选择连续增加的代码块                         |
|    Ctrl + Shift + W     |                         选择连续减少的代码块                         |
|         Alt + Q         |                           上下文信息                            |
|       Alt + Enter       |                        显示意图操作和快速操作                         |
|     Ctrl + Alt + L      |                           格式化代码                            |
|     Ctrl + Alt + O      |                           优化包导入                            |
|     Ctrl + Alt + I      |                           自动缩进行                            |
|    Tab / Shift + Tab    |                        缩进/取消缩进选定的行                         |
|        Ctrl + X         |                       剪切当前选择的行或块到剪切板                       |
|        Ctrl + C         |                       复制当前行或选择的块到剪切板                       |
|    Ctrl + Shift + V     |                          从历史剪切板复制                          |
|        Ctrl + D         |                         复制当前行或选择的块                         |
|        Ctrl + Y         |                          删除光标所在行                           |
|      Shift + Enter      |                           开始新的一行                           |
|    Ctrl + Shift + U     |                     在插入符号或选定块中切换单词的大小写                     |
|   Ctrl + Shift + ]/[    |                        选择直到代码块结束/开始                        |
| Ctrl + Delete/Backspace |                         删除到单词结尾/开头                         |
|    Ctrl + NumPad+/-     |                          展开/折叠代码块                          |
| Ctrl + Shift + NumPad+  |                            展开所有                            |
| Ctrl + Shift + NumPad-  |                            折叠所有                            |
|        Ctrl + F4        |                           关闭活动编辑器选项卡                            |


## Usage Search

|        快捷键         |      操作       |
|:------------------:|:-------------:|
| Alt + F7/Ctrl + F7 | 查找用法/在文件中查找用法 |
| Ctrl + Shift + F7  |  在文件中高亮显示用法   |
|  Ctrl + Alt + F7   |     显示用法      |


## Navigation

|           快捷键            |       操作        |
|:------------------------:|:---------------:|
|         Ctrl + N         |    打开Class搜索    |
|     Ctrl + Shift + N     |     打开文件搜索      |
|  Ctrl + Alt + Shift + N  |     打开符号搜索      |
|     Alt + Right/Left     | 打开下一个/上一个编辑器选项卡 |
|           F12            |    返回上一个工具窗口    |
|           Esc            |     返回到编辑区      |
|       Shift + Esc        |  隐藏活动或最后一个活动窗口  |
|         Ctrl + E         |     最近打开的文件     |
| Ctrl + Alt + Left/Right  |      返回/前进      |
| Ctrl + Shift + Backspace |   导航到上一次编辑的位置   |
|         Alt + F1         | 在任何视图中选择当前文件或符号 |
|  Ctrl + B(或Ctrl + 鼠标单击)  |      转到声明       |
|      Ctrl + Alt + B      |      转到实现       |
|     Ctrl + Shift + I     |     打开快速定义      |
|     Ctrl + Shift + B     |     转到类型声明      |
|         Ctrl + U         |    转到父类方法/父类    |
|      Alt + Up/Down       |   转到上一个/下一个方法   |
|        Ctrl + ]/[        |  移动到代码块的结尾/开始   |
|        Ctrl + F12        |    弹出文件结构窗口     |
|         Ctrl + H         |     类的层次结构      |
|     Ctrl + Shift + H     |     方法的层次结构     |
|      Ctrl + Alt + H      |    方法调用层次结构     |
|      F2/Shift + F2       |   下一个/上一个高亮错误   |
|     F4/Ctrl + Enter      |     编辑源/显示源     |
|        Alt + Home        |      显示导航栏      |
|           F11            |      添加书签       |
|        Ctrl + F11        |    使用助记符添加书签    |
|        Ctrl + 0-9        |    转到指定的数字书签    |
|       Shift + F11        |     显示所有书签      |


## Search/Replace

|       快捷键        |    操作     |
|:----------------:|:---------:|
|     双击Shift      |   全局搜索    |
|     Ctrl + F     | 在当前文件中查找  |
|  F3/Shift + F3   | 转到下一个/上一个 |
|     Ctrl + R     |    替换     |
| Ctrl + Shift + F |  在路径中查找   |
| Ctrl + Shift + R |  在路径中替换   |


## Live Templates

|      快捷键       |            操作             |
|:--------------:|:-------------------------:|
| Ctrl + Alt + J |           包围代码            |
|      iter      |          增强For循环          |
|      inst      |           类型检查            |
|      itco      | 遍历java.util.Collection的元素 |
|      itit      |  遍历java.util.Iterator的元素  |
|      itli      |    遍历java.util.List的元素    |
|      psf       |    public static final    |
|      thr       |         throw new         |


## Refactoring

|      快捷键       |      操作       |
|:--------------:|:-------------:|
|       F5       |      复制       |
|       F6       |      移动       |
|  Alt + Delete  |     安全删除      |
|   Shift + F6   |      重命名      |
|   Ctrl + F6    |     改变签名      |
| Ctrl + Alt + N | 将变量的定义与使用作为一行 |
| Ctrl + Alt + M |     提取为方法     |
| Ctrl + Alt + V |     提取为变量     |
| Ctrl + Alt + F |     提取为字段     |
| Ctrl + Alt + C |     提取为常量     |
| Ctrl + Alt + P |     提取为参数     |


## Debugging

|          快捷键          |   操作    |
|:---------------------:|:-------:|
|         F8/F7         |  跨过/进入  |
| Shift + F7/Shift + F8 | 智能进入/退出 |
|       Alt + F9        |  运行到光标  |
|       Alt + F8        |  评估表达式  |
|          F9           |  恢复程序   |
|       Ctrl + F8       |  切换断点   |
|   Ctrl + Shift + F8   |  查看短点   |


## Compile and Run

|         快捷键          |                操作                 |
|:--------------------:|:---------------------------------:|
|      Ctrl + F9       |           编译项目(编译修改和依赖)           |
|  Ctrl + Shift + F9   |           编译选择的文件，包或模块            |
| Alt + Shift + F10/F9 | 选择Configuration and run/and debug |
|    Shift + F10/F9    |             运行/Debug              |
|  Ctrl + Shift + F10  |        从编辑器运行上下文配置                           |


## VCS/Local History

|        快捷键        |    操作     |
|:-----------------:|:---------:|
| Ctrl + K/Ctrl + T |  提交/更新项目  |
|  Alt + Shift + C  |  显示最近的更改  |
|      Alt + `      | 弹出VCS操作窗口 |


## General

|          快捷键           |        操作         |
|:----------------------:|:-----------------:|
|       Alt + 0-9        |     打开对应的工具窗口     |
|        Ctrl + S        |       保存所有        |
|     Ctrl + Alt + Y     |        同步         |
|   Ctrl + Shift + F12   |      最大化编辑器       |
|    Alt + Shift + I     | 使用当前profile检查当前文件 |
|        Ctrl + `        |     快速切换当前主题      |
|     Ctrl + Alt + S     |      打开设置窗口       |
| Ctrl + Alt + Shift + S |     打开项目结构窗口      |
|    Ctrl + Shift + A    |     查找Action      |
|       Ctrl + Tab       |    在选项卡和工具窗口之间切换               |