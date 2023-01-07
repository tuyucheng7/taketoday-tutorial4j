package cn.tuyucheng.taketoday.array.conversions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FloatToByteArrayUnitTest {

    @Test
    void givenAFloat_thenConvertToByteArray() {
        assertArrayEquals(new byte[]{63, -116, -52, -51}, FloatToByteArray.floatToByteArray(1.1f));
    }

    @Test
    void givenAByteArray_thenConvertToFloat() {
        assertEquals(1.1f, FloatToByteArray.byteArrayToFloat(new byte[]{63, -116, -52, -51}), 0);
    }

    @Test
    void givenAFloat_thenConvertToByteArrayUsingByteBuffer() {
        assertArrayEquals(new byte[]{63, -116, -52, -51}, FloatToByteArray.floatToByteArrayWithByteBuffer(1.1f));
    }

    @Test
    void givenAByteArray_thenConvertToFloatUsingByteBuffer() {
        assertEquals(1.1f, FloatToByteArray.byteArrayToFloatWithByteBuffer(new byte[]{63, -116, -52, -51}), 0);
    }

    @Test
    void givenAFloat_thenConvertToByteArray_thenConvertToFloat() {
        float floatToConvert = 200.12f;
        byte[] byteArray = FloatToByteArray.floatToByteArray(floatToConvert);
        assertEquals(200.12f, FloatToByteArray.byteArrayToFloat(byteArray), 0);
    }

    @Test
    void givenAFloat_thenConvertToByteArrayWithByteBuffer_thenConvertToFloatWithByteBuffer() {
        float floatToConvert = 30100.42f;
        byte[] byteArray = FloatToByteArray.floatToByteArrayWithByteBuffer(floatToConvert);
        assertEquals(30100.42f, FloatToByteArray.byteArrayToFloatWithByteBuffer(byteArray), 0);
    }
}