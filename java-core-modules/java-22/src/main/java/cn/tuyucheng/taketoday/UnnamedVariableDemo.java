package cn.tuyucheng.taketoday;

public class UnnamedVariableDemo {

   record Person(String name, int age) {}

   public static void main(String[] args) {
      Person mike = new Person("mike", 22);

      if (mike instanceof Person(_, var age)) {
         System.out.println(age);
      }
   }
}