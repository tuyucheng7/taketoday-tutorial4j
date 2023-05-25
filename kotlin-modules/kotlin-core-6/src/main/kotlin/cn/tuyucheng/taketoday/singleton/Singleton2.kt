package cn.tuyucheng.taketoday.singleton

class Singleton2 private constructor() {
   companion object {

      @Volatile
      private var instance: Singleton2? = null

      fun getInstance() =
            instance ?: synchronized(this) {
               instance ?: Singleton2().also { instance = it }
            }
   }

   fun doSomething() = "Doing something"
}