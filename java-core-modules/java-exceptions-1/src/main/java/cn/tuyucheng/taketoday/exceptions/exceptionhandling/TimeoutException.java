package cn.tuyucheng.taketoday.exceptions.exceptionhandling;

public class TimeoutException extends Exception {

    public TimeoutException(String message) {
        super(message);
    }
}
