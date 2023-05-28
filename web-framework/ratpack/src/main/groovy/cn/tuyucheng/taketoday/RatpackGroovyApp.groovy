package cn.tuyucheng.taketoday;

public class RatpackGroovyApp {

   public static void main(String[] args) {
      File file = new File("src/main/groovy/cn/tuyucheng/taketoday/Ratpack.groovy");
      def shell = new GroovyShell()
      shell.evaluate(file)
   }

}

