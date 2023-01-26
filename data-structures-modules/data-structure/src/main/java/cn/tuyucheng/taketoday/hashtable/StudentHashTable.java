package cn.tuyucheng.taketoday.hashtable;

public class StudentHashTable {
  private StudentLinkedList[] studentLinkedLists;
  private int size;

  public StudentHashTable(int size) {
    this.size = size;
    studentLinkedLists = new StudentLinkedList[size];
    for (int i = 0; i < size; i++) {
      studentLinkedLists[i] = new StudentLinkedList();
    }
  }

  /**
   * 散列函数
   *
   * @param sid 学生id
   * @return 返回哈希表索引
   */
  public int getHash(int sid) {
    return sid % size;
  }

  public void add(Student student) {
    int hash = getHash(student.id);
    StudentLinkedList studentLinkedList = studentLinkedLists[hash];
    studentLinkedList.add(student);
  }

  public void list() {
    for (int i = 0; i < size; i++) {
      studentLinkedLists[i].list(i);
    }
  }

  public void findByStudentId(int sid) {
    int hash = getHash(sid);
    Student student = studentLinkedLists[hash].queryStudent(sid);
    if (student != null) {
      System.out.printf("在第%d条链表中找到了学生,编号是:%d\n", (hash + 1), sid);
    } else {
      System.out.println("没有找到该学生");
    }
  }
}