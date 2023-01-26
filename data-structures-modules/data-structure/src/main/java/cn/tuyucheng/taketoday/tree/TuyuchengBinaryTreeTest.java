package cn.tuyucheng.taketoday.tree;

@SuppressWarnings("all")
public class TuyuchengBinaryTreeTest {
  public static void main(String[] args) {
    //创建二叉查找树对象
    TuyuchengBinaryTree<Integer, String> tree = new TuyuchengBinaryTree<>();

    //测试插入
    tree.put(1,"张三");
    tree.put(2,"李四");
    tree.put(3,"王五");
    tree.put(4,"jack");
    tree.put(5,"smith");
    System.out.println("插入完毕后元素的个数："+tree.getSize());

    //测试获取
    System.out.println("键2对应的元素是："+tree.getKey(2));

    //测试删除

    tree.delete(3);
    System.out.println("删除后的元素个数："+tree.getSize());
    System.out.println("删除后键2对应的元素:"+tree.getKey(2));

    System.out.println(tree.min());
    System.out.println(tree.max());
  }
}