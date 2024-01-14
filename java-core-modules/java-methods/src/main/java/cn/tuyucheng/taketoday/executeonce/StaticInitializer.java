package cn.tuyucheng.taketoday.executeonce;

final class StaticInitializer {

   static int CALL_COUNT = 0;

   static {
      initializationLogic();
   }

   private static void initializationLogic() {
      CALL_COUNT++;
   }
}