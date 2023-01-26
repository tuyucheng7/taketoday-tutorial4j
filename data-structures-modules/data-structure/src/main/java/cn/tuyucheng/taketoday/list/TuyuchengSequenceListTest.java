package cn.tuyucheng.taketoday.list;

public class TuyuchengSequenceListTest {
  public static void main(String[] args) {
    TuyuchengSequenceList<Integer> list = new TuyuchengSequenceList<>(4);
    list.insert(1);
    list.insert(2);
    list.insert(3);
    list.insert(4);
    System.out.println(list.get(2));
    System.out.println(list.remove(2));
    System.out.println(list.indexOf(2));
    for (Integer integer : list) {
      System.out.println(integer);
    }
  }
}