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
public class SymbolTableTest {
  public static void main(String[] args) {
    SymbolTable<Integer, String> symbolTable = new SymbolTable<>();
    symbolTable.put(1, "smith");
    symbolTable.put(2, "mike");
    symbolTable.put(3, "jack");
    symbolTable.put(2, "tom");
    System.out.println(symbolTable.get(2));
    System.out.println(symbolTable.size());
    symbolTable.delete(3);
    System.out.println(symbolTable.size());
  }
}