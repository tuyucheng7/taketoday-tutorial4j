package cn.tuyucheng.taketoday.hashtable;

public class HashTableTest {
  public static void main(String[] args) {
    Student student1 = new Student(1, "mike");
    Student student2 = new Student(2, "jack");
    Student student3 = new Student(3, "curry");
    Student student4 = new Student(4, "durant");
    Student student12 = new Student(11, "james");
    StudentHashTable studentHashTable = new StudentHashTable(10);
    studentHashTable.add(student1);
    studentHashTable.add(student2);
    studentHashTable.add(student3);
    studentHashTable.add(student4);
    studentHashTable.add(student12);
    studentHashTable.list();
    studentHashTable.findByStudentId(3);
  }
}