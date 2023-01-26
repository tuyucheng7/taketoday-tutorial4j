package cn.tuyucheng.taketoday.tree;

public class TuyuchengRedBlackTreeTest {
  public static void main(String[] args) {
    TuyuchengRedBlackTree<String, String> redBlackTree = new TuyuchengRedBlackTree<>();
    redBlackTree.put("1", "张三");
    redBlackTree.put("2", "李四");
    redBlackTree.put("3", "王五");
    redBlackTree.put("4", "赵六");
    redBlackTree.put("5", "田七");
    System.out.println(redBlackTree.get("1"));
    System.out.println(redBlackTree.get("2"));
    System.out.println(redBlackTree.get("3"));
    System.out.println(redBlackTree.get("4"));
    System.out.println(redBlackTree.get("5"));
  }
}