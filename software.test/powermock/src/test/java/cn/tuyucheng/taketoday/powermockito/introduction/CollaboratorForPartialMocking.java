package cn.tuyucheng.taketoday.powermockito.introduction;

class CollaboratorForPartialMocking {

   static String staticMethod() {
      return "Hello Tuyucheng!";
   }

   final String finalMethod() {
      return "Hello Tuyucheng!";
   }

   private String privateMethod() {
      return "Hello Tuyucheng!";
   }

   String privateMethodCaller() {
      return privateMethod() + " Welcome to the Java world.";
   }
}