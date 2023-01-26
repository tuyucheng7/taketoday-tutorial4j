package cn.tuyucheng.taketoday.stack;

/**
 * @author: tuyucheng
 * @date 2021/12/319:01
 * @Description: 代码是我心中的一首诗
 */
public class CalculatorDemo {
  public static void main(String[] args) {
    ArrayStack numberStack = new ArrayStack(10);
    ArrayStack symbolStack = new ArrayStack(10);
    String expression = "4+3*5";
    int length = expression.length();
    int temp1;
    int temp2;
    int ch;
    int result;
    StringBuilder values = new StringBuilder();
    for (int i = 0; i < length; i++) {
      char chs = expression.charAt(i);
      if (symbolStack.isOperator(chs)) {
        if (!symbolStack.isEmpty()) {
          if (symbolStack.priority(chs) <= symbolStack.priority(symbolStack.peek())) {
            temp1 = numberStack.pop();
            temp2 = numberStack.pop();
            ch = symbolStack.pop();
            result = numberStack.calculate(temp1, temp2, ch);
            numberStack.push(result);
            symbolStack.push(chs);
          } else {
            symbolStack.push(chs);
          }
        } else {
          symbolStack.push(chs);
        }
      } else {
        values.append(chs);
        if (i == length - 1) {
          numberStack.push(Integer.parseInt(values.toString()));
        } else {
          char data = expression.substring(i + 1, i + 2).charAt(0);
          if (symbolStack.isOperator(data)) {
            numberStack.push(Integer.parseInt(values.toString()));
            values = new StringBuilder();
          }
        }
      }
    }
    while (!symbolStack.isEmpty()) {
      temp1 = numberStack.pop();
      temp2 = numberStack.pop();
      ch = symbolStack.pop();
      result = numberStack.calculate(temp1, temp2, ch);
      numberStack.push(result);
    }
    int res = numberStack.pop();
    System.out.println("表达式的结果为:" + res);
  }
}