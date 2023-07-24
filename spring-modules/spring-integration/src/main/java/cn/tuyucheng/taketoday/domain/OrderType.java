package cn.tuyucheng.taketoday.domain;

public enum OrderType {
   BUY('B'),
   SELL('S');
   private final char code;

   OrderType(char code) {
      this.code = code;
   }

   public char getCode() {
      return code;
   }
}