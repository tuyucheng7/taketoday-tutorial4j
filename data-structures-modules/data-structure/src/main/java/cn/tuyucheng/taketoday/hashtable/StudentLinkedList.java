package cn.tuyucheng.taketoday.hashtable;

public class StudentLinkedList {
  private Student head;

  public void add(Student newStudent) {
    if (head == null) {
      this.head = newStudent;
      return;
    }
    Student temp = head;
    while (true) {
      if (temp.next == null) {
        break;
      }
      temp = temp.next;
    }
    temp.next = newStudent;
  }

  public void list(int no) {
    if (head == null) {
      System.out.println("第" + no + "个链表为空");
      return;
    }
    Student temp = head;
    while (true) {
      System.out.printf("id=%d name=%s\t", temp.id, temp.name);
      if (temp.next == null) {
        break;
      }
      temp = temp.next;
    }
    System.out.println();
  }

  public Student queryStudent(int id) {
    if (head == null) {
      System.out.println("链表为空");
      return null;
    }

    Student temp = head;
    while (true) {
      if (temp.id == id) {
        break;
      }
      if (temp.next == null) {
        temp = null;
        break;
      }
      temp = temp.next;
    }
    return temp;
  }
}