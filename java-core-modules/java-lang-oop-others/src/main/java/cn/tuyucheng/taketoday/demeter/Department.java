package cn.tuyucheng.taketoday.demeter;

public class Department {

   private Manager manager = new Manager();

   public Manager getManager() {
      return manager;
   }

}
