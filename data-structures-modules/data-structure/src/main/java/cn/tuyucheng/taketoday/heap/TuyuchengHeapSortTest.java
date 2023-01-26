package cn.tuyucheng.taketoday.heap;

import java.util.Arrays;

public class TuyuchengHeapSortTest {
  public static void main(String[] args) {
    String[] array = {"S", "O", "R", "T", "E", "X", "A", "M", "P", "L", "E"};
    TuyuchengHeapSort.sort(array);
    System.out.println(Arrays.toString(array));
  }
}