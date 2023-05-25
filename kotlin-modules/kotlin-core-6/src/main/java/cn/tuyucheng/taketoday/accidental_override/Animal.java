package cn.tuyucheng.taketoday.accidental_override;

public interface Animal {
   default String getSound() {
      return "";
   }
}
