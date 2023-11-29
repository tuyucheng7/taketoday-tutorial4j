package cn.tuyucheng.taketoday.serenity.spring;

import org.apache.commons.lang3.RandomUtils;

/**
 * @author tuyucheng
 */
public class RandomNumberUtil {

   public static int randomInt() {
      return RandomUtils.nextInt(1, 10);
   }
}
