package cn.tuyucheng.taketoday.stack;

/**
 * @author: tuyucheng
 * @date 2021/12/319:01
 * @Description: 代码是我心中的一首诗
 */
public class ReversePolishNotationTest {
  public static void main(String[] args) {
    // 3*(17-15)+18/6
    String[] notation = {"3", "17", "15", "-", "*", "18", "6", "/", "+"};
    Stack<Integer> stack = new Stack<>();
    for (String s : notation) {
      if (!("+".equals(s) || "-".equals(s) || "*".equals(s) || "/".equals(s))) {
        stack.push(Integer.valueOf(s));
      } else {
        if ("+".equals(s)) {
          int num1 = stack.pop();
          int num2 = stack.pop();
          stack.push(num1 + num2);
        } else if ("-".equals(s)) {
          int num1 = stack.pop();
          int num2 = stack.pop();
          stack.push(num2 - num1);
        } else if ("*".equals(s)) {
          int num1 = stack.pop();
          int num2 = stack.pop();
          stack.push(num2 * num1);
        } else {
          int num1 = stack.pop();
          int num2 = stack.pop();
          stack.push(num2 / num1);
        }
      }
    }
    System.out.println("表达式的结果为:" + stack.pop());
  }
}