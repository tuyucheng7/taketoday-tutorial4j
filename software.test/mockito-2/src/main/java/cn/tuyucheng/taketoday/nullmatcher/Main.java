package cn.tuyucheng.taketoday.nullmatcher;

class Main {

   Helper helper = new Helper();

   String methodUnderTest() {
      return helper.concat("Tuyucheng", null);
   }
}