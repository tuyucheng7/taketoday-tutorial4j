package cn.tuyucheng.taketoday.serenity.membership;

/**
 * @author tuyucheng
 */
public enum Commodity {

   MacBookPro(1499), GoProHero5(400);

   public final int price;

   Commodity(int price) {
      this.price = price;
   }

}
