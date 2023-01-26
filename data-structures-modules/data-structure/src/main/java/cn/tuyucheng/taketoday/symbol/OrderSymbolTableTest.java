package cn.tuyucheng.taketoday.symbol;

/**
 * @Version: 1.0.0
 * @Author: tuyucheng
 * @Email: 2759709304@qq.com
 * @ProjectName: java-study
 * @Description: 代码是我心中的一首诗
 * @CreateTime: 2021-11-16 01:15
 */
@SuppressWarnings("all")
public class OrderSymbolTableTest {
  public static void main(String[] args) {
    OrderSymbolTable<Integer, String> orderSymbolTable = new OrderSymbolTable<>();
    orderSymbolTable.put(1, "smith");
    orderSymbolTable.put(2, "tom");
    orderSymbolTable.put(4, "mike");
    orderSymbolTable.put(7, "jack");
    orderSymbolTable.put(3, "curry");
    orderSymbolTable.put(6, "james");
  }
}