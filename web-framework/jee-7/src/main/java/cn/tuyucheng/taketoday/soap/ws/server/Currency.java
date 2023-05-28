package cn.tuyucheng.taketoday.soap.ws.server;

public enum Currency {

   EUR, INR, USD;

   public static Currency fromValue(String v) {
      return valueOf(v);
   }

   public String value() {
      return name();
   }

}
