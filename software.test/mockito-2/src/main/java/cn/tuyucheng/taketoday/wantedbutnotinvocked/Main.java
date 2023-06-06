package cn.tuyucheng.taketoday.wantedbutnotinvocked;

class Main {

   Helper helper = new Helper();

   String methodUnderTest(int i) {
      if (i > 5) {
         return helper.getTuyuchengString();
      }
      return "Hello";
   }
}