package cn.tuyucheng.taketoday.enums;

enum class Weekday {
   SUNDAY,
   MONDAY,
   TUESDAY,
   WEDNESDAY,
   THURSDAY,
   FRIDAY,
   SATURDAY,
}


// Demonstration: extending an enum gives compilation failure
/*
enum class MyWeekday: Weekday {
}
*/