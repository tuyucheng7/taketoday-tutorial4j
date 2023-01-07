package cn.tuyucheng.taketoday.exceptions;

import org.junit.Test;

import java.io.IOException;
import java.net.UnknownHostException;

public class UnknownHostExceptionHandlingUnitTest {

    @Test(expected = UnknownHostException.class)
    public void givenUnknownHost_whenResolve_thenUnknownHostException() throws IOException {
        UnknownHostExceptionHandling.getResponseCodeUnhandled("http://locaihost");
    }

}
