package cn.tuyucheng.taketoday.tree;

import cn.tuyucheng.taketoday.queue.TuyuchengQueue;

public class TuyuchengBinaryTreeErgodicTest {
  public static void main(String[] args) {
    TuyuchengBinaryTree<Integer, String> binaryTree = new TuyuchengBinaryTree<>();

    binaryTree.put(20, "smith");
    binaryTree.put(6, "jack");
    binaryTree.put(21, "mike");
    binaryTree.put(5, "tom");
    binaryTree.put(4, "maria");
    binaryTree.put(15, "jackson");
    binaryTree.put(18, "curry");
    binaryTree.put(12, "durant");

    System.out.println("-----------前序遍历---------------");
    TuyuchengQueue<Integer> preQueue = binaryTree.preOrderTraversal();
    for (Integer integer : preQueue) {
      String value = binaryTree.getKey(integer);
      System.out.println(integer + "---" + value);
    }

    System.out.println("-----------中序遍历---------------");
    TuyuchengQueue<Integer> midQueue = binaryTree.middleOrderTraversal();
    for (Integer integer : midQueue) {
      String value = binaryTree.getKey(integer);
      System.out.println(integer + "---" + value);
    }

    System.out.println("-----------后序遍历---------------");
    TuyuchengQueue<Integer> afterQueue = binaryTree.postOrderTraversal();
    for (Integer integer : afterQueue) {
      String value = binaryTree.getKey(integer);
      System.out.println(integer + "---" + value);
    }

    System.out.println("-----------层序遍历---------------");
    TuyuchengQueue<Integer> layerQueue = binaryTree.levelOrderTraversal();
    for (Integer integer : layerQueue) {
      String value = binaryTree.getKey(integer);
      System.out.println(integer + "---" + value);
    }

    System.out.println("---------------------------------");

    System.out.println(binaryTree.maxDepth());
  }
}