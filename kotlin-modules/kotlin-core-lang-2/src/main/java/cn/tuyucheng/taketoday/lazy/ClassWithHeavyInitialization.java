package cn.tuyucheng.taketoday.lazy;

public class ClassWithHeavyInitialization {
   private ClassWithHeavyInitialization() {
   }

   public static ClassWithHeavyInitialization getInstance() {
      return LazyHolder.INSTANCE;
   }

   private static class LazyHolder {
      public static final ClassWithHeavyInitialization INSTANCE = new ClassWithHeavyInitialization();
   }
}