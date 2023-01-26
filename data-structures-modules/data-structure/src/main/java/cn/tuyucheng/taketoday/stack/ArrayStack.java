package cn.tuyucheng.taketoday.stack;

public class ArrayStack {
  public int top = -1;
  private int maxStack;
  private int[] stack;

  public ArrayStack(int maxStack) {
    this.maxStack = maxStack;
    this.stack = new int[maxStack];
  }

  public boolean isFull() {
    return this.top == this.maxStack - 1;
  }

  public boolean isEmpty() {
    return this.top == -1;
  }

  public void push(int value) {
    if (isFull()) {
      throw new RuntimeException("栈满");
    }
    top++;
    this.stack[top] = value;
  }

  public int pop() {
    if (isEmpty()) {
      throw new RuntimeException("空栈");
    }
    int result = stack[top];
    top--;
    return result;
  }

  public void list() {
    if (isEmpty()) {
      throw new RuntimeException("空栈");
    }
    for (int i = 0; i < stack.length; i++) {
      System.out.printf("stack[%d]=%d\n", i, stack[i]);
    }
  }

  public int length() {
    return this.top + 1;
  }

  public boolean isOperator(char operator) {
    return operator == '+' || operator == '-' || operator == '*' || operator == '/';
  }

  public int stackLength() {
    return this.stack.length;
  }

  public int peek() {
    return this.stack[top];
  }

  public int priority(int operator) {
    if (operator == '*' || operator == '/') {
      return 1;
    } else if (operator == '+' || operator == '-') {
      return 0;
    } else {
      return -1;
    }
  }

  public int calculate(int num1, int num2, int operator) {
    int result = 0;
    switch (operator) {
      case '+' -> result = num1 + num2;
      case '-' -> result = num2 - num1;
      case '*' -> result = num1 * num2;
      case '/' -> result = num2 / num1;
      default -> {
      }
    }
    return result;
  }
}