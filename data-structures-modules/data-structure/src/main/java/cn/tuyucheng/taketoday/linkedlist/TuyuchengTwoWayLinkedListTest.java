package cn.tuyucheng.taketoday.linkedlist;

public class TuyuchengTwoWayLinkedListTest {
  public static void main(String[] args) {
    TuyuchengTwoWayLinkedList<Integer> list = new TuyuchengTwoWayLinkedList<>();
    list.insert(1);
    list.insert(2);
    list.insert(3);
    list.insert(4);
    list.insert(5);
    list.insert(6);
    list.insert(7);
    list.insert(3, 10);
    System.out.println(list.indexOf(7));
    for (Integer integer : list) {
      System.out.println(integer);
    }
  }
}