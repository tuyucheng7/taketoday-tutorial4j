package cn.tuyucheng.taketoday.serenity.membership;

/**
 * @author tuyucheng
 */
public class Member {

   private int points;

   private Member(int points) {
      if (points < 0)
         throw new IllegalArgumentException("points must not be negative!");
      this.points = points;

   }

   public static Member withInitialPoints(int initialPoints) {
      return new Member(initialPoints);
   }

   public MemberGrade getGrade() {
      if (points < 1000)
         return MemberGrade.Bronze;
      else if (points >= 1000 && points < 5000)
         return MemberGrade.Silver;
      else
         return MemberGrade.Gold;
   }

   public void spend(int moneySpent) {
      points += moneySpent / 10;
   }

}
