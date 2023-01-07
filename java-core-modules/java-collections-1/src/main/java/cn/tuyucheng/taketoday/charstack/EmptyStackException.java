package cn.tuyucheng.taketoday.charstack;

public class EmptyStackException extends RuntimeException {

    public EmptyStackException() {
        super("Stack is empty");
    }
}