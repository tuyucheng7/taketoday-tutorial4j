package cn.tuyucheng.taketoday.linkedlist;

public class TuyuchengLinkedListTest {
  public static void main(String[] args) {
    TuyuchengLinkedList<Integer> list = new TuyuchengLinkedList<>();
    list.insert(2);
    list.insert(3);
    list.insert(4);
    list.insert(2);
    list.insert(1);
    list.remove(2);
    list.reverse();
    for (Integer integer : list) {
      System.out.println(integer);
    }
  }
}