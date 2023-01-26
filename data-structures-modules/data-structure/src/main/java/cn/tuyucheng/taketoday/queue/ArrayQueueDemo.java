package cn.tuyucheng.taketoday.queue;

import java.util.Scanner;

public class ArrayQueueDemo {
  public static void main(String[] args) {
    ArrayQueue queue = new ArrayQueue(3);
    char key = ' ';
    Scanner scanner = new Scanner(System.in);
    boolean loop = true;
    while (loop) {
      System.out.println("s(show):显示队列");
      System.out.println("e(exit):退出程序");
      System.out.println("a(add):添加数据");
      System.out.println("g(get):取出数据");
      System.out.println("h(head):查看头数据");
      key = scanner.next().charAt(0);
      switch (key) {
        case 's':
          queue.show();
          break;
        case 'a':
          System.out.println("请输入一个数");
          int num = scanner.nextInt();
          queue.addQueue(num);
          break;
        case 'g':
          try {
            int element = queue.getElement();
            System.out.println("取出的数据为:" + element);
          } catch (Exception e) {
            System.out.println(e.getMessage());
          }
          break;
        case 'h':
          queue.showHead();
          break;
        case 'e':
          scanner.close();
          loop = false;
      }
    }
    System.out.println("退出程序......");
  }

  static class ArrayQueue {
    private int rear;// 指向队列尾的指针
    private int maxsize;// 数组的最大容i昂
    private int front;// 指向队列头的指针
    private int[] arr;// 用于存储数据

    public ArrayQueue(int maxsize) {
      this.maxsize = maxsize;
      this.arr = new int[maxsize];
    }

    public boolean isFull() {
      return (rear + 1) % maxsize == front;
    }

    public boolean isEmpty() {
      return rear == front;
    }

    public void addQueue(int n) {
      if (isFull()) {
        System.out.println("队列已满");
        return;
      }
      arr[rear] = n;
      rear = (rear + 1) % maxsize;
    }

    public int getElement() {
      if (isEmpty()) {
        throw new RuntimeException("队列为空");
      }
      int result = arr[0];
      front = (front + 1) % maxsize;
      return result;
    }

    public void show() {
      if (isEmpty()) {
        System.out.println("队列为空");
        return;
      }
      for (int i : arr) {
        System.out.printf("%d\t", i);
      }
    }

    public void showHead() {
      if (isEmpty()) {
        System.out.println("队列为空");
      } else {
        System.out.println(arr[front + 1]);
      }
    }
  }
}