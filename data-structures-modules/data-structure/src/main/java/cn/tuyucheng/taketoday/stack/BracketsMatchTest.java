package cn.tuyucheng.taketoday.stack;

public class BracketsMatchTest {
  public static void main(String[] args) {
    String str = "(上海(武汉))";
    boolean match = isMatch(str);
    System.out.println(str + "中的括号是否匹配:" + match);
  }

  private static boolean isMatch(String str) {
    Stack<String> stack = new Stack<>();
    for (int i = 0; i < str.length(); i++) {
      String s = str.charAt(i) + "";
      if ("(".equals(s)) {
        stack.push(s);
      } else if (")".equals(s)) {
        String result = stack.pop();
        if (result == null) {
          return false;
        }
      }
    }
    return stack.size() == 0;
  }
}