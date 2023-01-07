package cn.tuyucheng.taketoday.consoleout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ConsoleAndOutUnitTest {

    @Test
    void whenRetreivingConsole_thenPrintConsoleObject() {
        assertThrows(NullPointerException.class, () -> {
            ConsoleAndOut.printConsoleObject();
        });
    }

    @Test
    void whenReadingPassword_thenReadPassword() {
        assertThrows(NullPointerException.class, () -> {
            ConsoleAndOut.readPasswordFromConsole();
        });
    }

    @Test
    void whenRetrievingSysOut_thenPrintSysOutObject() {
        ConsoleAndOut.printSysOut();
    }
}
