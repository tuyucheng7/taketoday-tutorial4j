package cn.tuyucheng.taketoday.jmxterm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerUnitTest {

   @Test
   void givenNewPlayer_thenScoreIsZero() {
      Player player = new Player("John");
      assertEquals(0, player.getScore());
   }

   @Test
   void givenNewPlayer_whenIncrementScore_thenScoreIsOne() {
      Player player = new Player("John");
      player.incrementScore();
      assertEquals(1, player.getScore());
   }
}