package cn.tuyucheng.taketoday.enums

enum class PrimaryColor : IPrimaryColor {
   RED {
      override fun paint(): String {
         return "red"
      }
   };
}