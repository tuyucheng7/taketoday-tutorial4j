package cn.tuyucheng.taketoday.interfacesingleimpl;

public class MockAnimal implements Animal {
   @Override
   public String makeSound() {
      return "Mock animal sound!";
   }
}
