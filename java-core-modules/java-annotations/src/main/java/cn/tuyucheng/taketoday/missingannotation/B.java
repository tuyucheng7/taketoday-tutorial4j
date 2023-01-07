package cn.tuyucheng.taketoday.missingannotation;

@A
@C(D.class)
public class B {

    public static void main(String[] args) {
        System.out.println("It worked");
    }
}