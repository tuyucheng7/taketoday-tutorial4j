package cn.tuyucheng.taketoday.billpugh;

public class LazyLoadedSingleton {
   private static LazyLoadedSingleton lazyLoadedSingletonObj;

   private LazyLoadedSingleton() {
   }

   public static LazyLoadedSingleton getInstance() {
      if (null == lazyLoadedSingletonObj) {
         lazyLoadedSingletonObj = new LazyLoadedSingleton();
      }
      return lazyLoadedSingletonObj;
   }
}