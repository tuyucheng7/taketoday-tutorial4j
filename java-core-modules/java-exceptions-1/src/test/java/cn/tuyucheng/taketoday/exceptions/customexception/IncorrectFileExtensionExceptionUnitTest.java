package cn.tuyucheng.taketoday.exceptions.customexception;

import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class IncorrectFileExtensionExceptionUnitTest {

    @Test
    public void testWhenCorrectFileExtensionGiven_ReceivesNoException() throws IncorrectFileNameException {
        assertThat(FileManager.getFirstLine("correctFileNameWithProperExtension.txt")).isEqualTo("Default First Line");
    }

    @Test(expected = IncorrectFileExtensionException.class)
    public void testWhenCorrectFileNameExceptionThrown_ReceivesNoException() throws IncorrectFileNameException {
        StringBuffer sBuffer = new StringBuffer();
        sBuffer.append("src");
        sBuffer.append(File.separator);
        sBuffer.append("test");
        sBuffer.append(File.separator);
        sBuffer.append("resources");
        sBuffer.append(File.separator);
        sBuffer.append("correctFileNameWithoutProperExtension");
        FileManager.getFirstLine(sBuffer.toString());
    }

}
