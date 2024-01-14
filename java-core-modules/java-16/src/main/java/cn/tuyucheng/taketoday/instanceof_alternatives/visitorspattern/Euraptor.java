package cn.tuyucheng.taketoday.instanceof_alternatives.visitorspattern;

public class Euraptor implements Dino {

   String flies() {
      return "flying";
   }

   @Override
   public String move(Visitor dinobehave) {
      return dinobehave.visit(this);
   }
}