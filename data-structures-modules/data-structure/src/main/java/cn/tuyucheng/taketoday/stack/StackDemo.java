package cn.tuyucheng.taketoday.stack;

public class StackDemo {
  public static void main(String[] args) {
    System.out.println("aba是否是一个回文数据:" + detecation("aba"));
    System.out.println("hello是否是一个回文数据:" + detecation("hello"));
  }

  private static boolean detecation(String str) {
    ArrayStack arrayStack = new ArrayStack(10);
    int length = str.length();
    for (int i = 0; i < length; i++) {
      arrayStack.push(str.charAt(i));
    }
    StringBuilder result = new StringBuilder();
    int arrayLength = arrayStack.length();
    for (int i = 0; i < arrayLength; i++) {
      if (!arrayStack.isEmpty()) {
        char chs = (char) arrayStack.pop();
        result.append(chs);
      }
    }
    return str.equals(result.toString());
  }
}