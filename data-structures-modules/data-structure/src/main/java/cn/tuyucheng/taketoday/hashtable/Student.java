package cn.tuyucheng.taketoday.hashtable;

/**
 * @Version: 1.0.0
 * @Author: tuyucheng
 * @Email: 2759709304@qq.com
 * @ProjectName: java-study
 * @Description: 代码是我心中的一首诗
 * @CreateTime: 2021-11-16 01:15
 */
@SuppressWarnings("all")
public class Student {
  public int id;
  public String name;
  public Student next;

  public Student(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Student getNext() {
    return next;
  }

  public void setNext(Student next) {
    this.next = next;
  }
}