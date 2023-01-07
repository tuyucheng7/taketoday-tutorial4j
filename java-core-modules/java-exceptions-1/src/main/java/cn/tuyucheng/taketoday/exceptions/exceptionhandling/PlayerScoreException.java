package cn.tuyucheng.taketoday.exceptions.exceptionhandling;

public class PlayerScoreException extends Exception {

    public PlayerScoreException(Exception e) {
        super(e);
    }
}
