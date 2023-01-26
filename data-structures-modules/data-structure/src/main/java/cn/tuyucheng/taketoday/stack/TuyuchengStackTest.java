package cn.tuyucheng.taketoday.stack;

public class TuyuchengStackTest {
  public static void main(String[] args) {
    TuyuchengStack<Integer> stack = new TuyuchengStack<>();
    stack.push(1);
    stack.push(2);
    stack.push(3);
    System.out.println("栈中元素的个数:" + stack.getSize());
    for (Integer integer : stack) {
      System.out.println(integer);
    }

    System.out.println("---------------------");
    Integer result = stack.pop();
    System.out.println("弹出的元素是:" + result);
    System.out.println("栈中元素的个数:" + stack.getSize());
  }
}