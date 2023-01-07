package cn.tuyucheng.taketoday.array.conversions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ByteArrayToNumericRepresentationUnitTest {
    private static final byte[] INT_BYTE_ARRAY = new byte[]{
            (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE
    };
    private static final int INT_VALUE = 0xCAFEBABE;


    private static final byte[] FLOAT_BYTE_ARRAY = new byte[]{
            (byte) 0x40, (byte) 0x48, (byte) 0xF5, (byte) 0xC3
    };
    private static final float FLOAT_VALUE = 3.14F;


    private static final byte[] LONG_BYTE_ARRAY = new byte[]{
            (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67,
            (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF
    };
    private static final long LONG_VALUE = 0x0123456789ABCDEFL;


    private static final byte[] DOUBLE_BYTE_ARRAY = new byte[]{
            (byte) 0x3F, (byte) 0xE3, (byte) 0xC6, (byte) 0xA7,
            (byte) 0xEF, (byte) 0x9D, (byte) 0xB2, (byte) 0x2D
    };
    private static final double DOUBLE_VALUE = 0.618D;


    @Test
    void givenShiftOperator_whenConvertingByteArrayToInt_thenSuccess() {
        int value = ByteArrayToNumericRepresentation.convertByteArrayToIntUsingShiftOperator(INT_BYTE_ARRAY);

        assertEquals(INT_VALUE, value);
    }

    @Test
    void givenShiftOperator_whenConvertingIntToByteArray_thenSuccess() {
        byte[] bytes = ByteArrayToNumericRepresentation.convertIntToByteArrayUsingShiftOperator(INT_VALUE);

        assertArrayEquals(INT_BYTE_ARRAY, bytes);
    }

    @Test
    void givenShiftOperator_whenConvertingByteArrayToLong_thenSuccess() {
        long value = ByteArrayToNumericRepresentation.convertByteArrayToLongUsingShiftOperator(LONG_BYTE_ARRAY);

        assertEquals(LONG_VALUE, value);
    }

    @Test
    void givenShiftOperator_whenConvertingLongToByteArray_thenSuccess() {
        byte[] bytes = ByteArrayToNumericRepresentation.convertLongToByteArrayUsingShiftOperator(LONG_VALUE);

        assertArrayEquals(LONG_BYTE_ARRAY, bytes);
    }

    @Test
    void givenShiftOperator_whenConvertingByteArrayToFloat_thenSuccess() {
        float value = ByteArrayToNumericRepresentation.convertByteArrayToFloatUsingShiftOperator(FLOAT_BYTE_ARRAY);

        assertEquals(Float.floatToIntBits(FLOAT_VALUE), Float.floatToIntBits(value));
    }

    @Test
    void givenShiftOperator_whenConvertingFloatToByteArray_thenSuccess() {
        byte[] bytes = ByteArrayToNumericRepresentation.convertFloatToByteArrayUsingShiftOperator(FLOAT_VALUE);

        assertArrayEquals(FLOAT_BYTE_ARRAY, bytes);
    }

    @Test
    void givenShiftOperator_whenConvertingByteArrayToDouble_thenSuccess() {
        double value = ByteArrayToNumericRepresentation.convertingByteArrayToDoubleUsingShiftOperator(DOUBLE_BYTE_ARRAY);

        assertEquals(Double.doubleToLongBits(DOUBLE_VALUE), Double.doubleToLongBits(value));
    }

    @Test
    void givenShiftOperator_whenConvertingDoubleToByteArray_thenSuccess() {
        byte[] bytes = ByteArrayToNumericRepresentation.convertDoubleToByteArrayUsingShiftOperator(DOUBLE_VALUE);

        assertArrayEquals(DOUBLE_BYTE_ARRAY, bytes);
    }

    @Test
    void givenByteBuffer_whenConvertingByteArrayToInt_thenSuccess() {
        int value = ByteArrayToNumericRepresentation.convertByteArrayToIntUsingByteBuffer(INT_BYTE_ARRAY);

        assertEquals(INT_VALUE, value);
    }

    @Test
    void givenByteBuffer_whenConvertingIntToByteArray_thenSuccess() {
        byte[] bytes = ByteArrayToNumericRepresentation.convertIntToByteArrayUsingByteBuffer(INT_VALUE);

        assertArrayEquals(INT_BYTE_ARRAY, bytes);
    }

    @Test
    void givenByteBuffer_whenConvertingByteArrayToLong_thenSuccess() {
        long value = ByteArrayToNumericRepresentation.convertByteArrayToLongUsingByteBuffer(LONG_BYTE_ARRAY);

        assertEquals(LONG_VALUE, value);
    }

    @Test
    void givenByteBuffer_whenConvertingLongToByteArray_thenSuccess() {
        byte[] bytes = ByteArrayToNumericRepresentation.convertLongToByteArrayUsingByteBuffer(LONG_VALUE);

        assertArrayEquals(LONG_BYTE_ARRAY, bytes);
    }

    @Test
    void givenByteBuffer_whenConvertingByteArrayToFloat_thenSuccess() {
        float value = ByteArrayToNumericRepresentation.convertByteArrayToFloatUsingByteBuffer(FLOAT_BYTE_ARRAY);

        assertEquals(Float.floatToIntBits(FLOAT_VALUE), Float.floatToIntBits(value));
    }

    @Test
    void givenByteBuffer_whenConvertingFloatToByteArray_thenSuccess() {
        byte[] bytes = ByteArrayToNumericRepresentation.convertFloatToByteArrayUsingByteBuffer(FLOAT_VALUE);

        assertArrayEquals(FLOAT_BYTE_ARRAY, bytes);
    }

    @Test
    void givenByteBuffer_whenConvertingByteArrayToDouble_thenSuccess() {
        double value = ByteArrayToNumericRepresentation.convertByteArrayToDoubleUsingByteBuffer(DOUBLE_BYTE_ARRAY);

        assertEquals(Double.doubleToLongBits(DOUBLE_VALUE), Double.doubleToLongBits(value));
    }

    @Test
    void givenByteBuffer_whenConvertingDoubleToByteArray_thenSuccess() {
        byte[] bytes = ByteArrayToNumericRepresentation.convertDoubleToByteArrayUsingByteBuffer(DOUBLE_VALUE);

        assertArrayEquals(DOUBLE_BYTE_ARRAY, bytes);
    }

    @Test
    void givenBigInteger_whenConvertingByteArrayToInt_thenSuccess() {
        int value = ByteArrayToNumericRepresentation.convertByteArrayToIntUsingBigInteger(INT_BYTE_ARRAY);

        assertEquals(INT_VALUE, value);
    }

    @Test
    void givenBigInteger_whenConvertingIntToByteArray_thenSuccess() {
        byte[] bytes = ByteArrayToNumericRepresentation.convertIntToByteArrayUsingBigInteger(INT_VALUE);

        assertArrayEquals(INT_BYTE_ARRAY, bytes);
    }

    @Test
    void givenBigInteger_whenConvertingByteArrayToLong_thenSuccess() {
        long value = ByteArrayToNumericRepresentation.convertByteArrayToLongUsingBigInteger(LONG_BYTE_ARRAY);

        assertEquals(LONG_VALUE, value);
    }

    @Test
    void givenBigInteger_whenConvertingLongToByteArray_thenSuccess() {
        byte[] bytes = ByteArrayToNumericRepresentation.convertLongToByteArrayUsingBigInteger(LONG_VALUE);

        assertArrayEquals(LONG_BYTE_ARRAY, bytes);
    }

    @Test
    void givenBigInteger_whenConvertingByteArrayToFloat_thenSuccess() {
        float value = ByteArrayToNumericRepresentation.convertByteArrayToFloatUsingBigInteger(FLOAT_BYTE_ARRAY);

        assertEquals(Float.floatToIntBits(FLOAT_VALUE), Float.floatToIntBits(value));
    }

    @Test
    void givenBigInteger_whenConvertingFloatToByteArray_thenSuccess() {
        byte[] bytes = ByteArrayToNumericRepresentation.convertFloatToByteArrayUsingBigInteger(FLOAT_VALUE);

        assertArrayEquals(FLOAT_BYTE_ARRAY, bytes);
    }

    @Test
    void givenBigInteger_whenConvertingByteArrayToDouble_thenSuccess() {
        double value = ByteArrayToNumericRepresentation.convertByteArrayToDoubleUsingBigInteger(DOUBLE_BYTE_ARRAY);

        assertEquals(Double.doubleToLongBits(DOUBLE_VALUE), Double.doubleToLongBits(value));
    }

    @Test
    void givenBigInteger_whenConvertingDoubleToByteArray_thenSuccess() {
        byte[] bytes = ByteArrayToNumericRepresentation.convertDoubleToByteArrayUsingBigInteger(DOUBLE_VALUE);

        assertArrayEquals(DOUBLE_BYTE_ARRAY, bytes);
    }

    @Test
    void givenGuava_whenConvertingByteArrayToInt_thenSuccess() {
        int value = ByteArrayToNumericRepresentation.convertingByteArrayToIntUsingGuava(INT_BYTE_ARRAY);

        assertEquals(INT_VALUE, value);
    }

    @Test
    void givenGuava_whenConvertingIntToByteArray_thenSuccess() {
        byte[] bytes = ByteArrayToNumericRepresentation.convertIntToByteArrayUsingGuava(INT_VALUE);

        assertArrayEquals(INT_BYTE_ARRAY, bytes);
    }

    @Test
    void givenGuava_whenConvertingByteArrayToLong_thenSuccess() {
        long value = ByteArrayToNumericRepresentation.convertByteArrayToLongUsingGuava(LONG_BYTE_ARRAY);

        assertEquals(LONG_VALUE, value);
    }

    @Test
    void givenGuava_whenConvertingLongToByteArray_thenSuccess() {
        byte[] bytes = ByteArrayToNumericRepresentation.convertLongToByteArrayUsingGuava(LONG_VALUE);

        assertArrayEquals(LONG_BYTE_ARRAY, bytes);
    }

    @Test
    void givenGuava_whenConvertingByteArrayToFloat_thenSuccess() {
        float value = ByteArrayToNumericRepresentation.convertByteArrayToFloatUsingGuava(FLOAT_BYTE_ARRAY);

        assertEquals(Float.floatToIntBits(FLOAT_VALUE), Float.floatToIntBits(value));
    }

    @Test
    void givenGuava_whenConvertingFloatToByteArray_thenSuccess() {
        byte[] bytes = ByteArrayToNumericRepresentation.convertFloatToByteArrayUsingGuava(FLOAT_VALUE);

        assertArrayEquals(FLOAT_BYTE_ARRAY, bytes);
    }

    @Test
    void givenGuava_whenConvertingByteArrayToDouble_thenSuccess() {
        double value = ByteArrayToNumericRepresentation.convertByteArrayToDoubleUsingGuava(DOUBLE_BYTE_ARRAY);

        assertEquals(Double.doubleToLongBits(DOUBLE_VALUE), Double.doubleToLongBits(value));
    }

    @Test
    void givenGuava_whenConvertingDoubleToByteArray_thenSuccess() {
        byte[] bytes = ByteArrayToNumericRepresentation.convertDoubleToByteArrayUsingGuava(DOUBLE_VALUE);

        assertArrayEquals(DOUBLE_BYTE_ARRAY, bytes);
    }

    @Test
    void givenCommonsLang_whenConvertingByteArrayToInt_thenSuccess() {
        int value = ByteArrayToNumericRepresentation.convertByteArrayToIntUsingCommonsLang(INT_BYTE_ARRAY);

        assertEquals(INT_VALUE, value);
    }

    @Test
    void givenCommonsLang_whenConvertingIntToByteArray_thenSuccess() {
        byte[] bytes = ByteArrayToNumericRepresentation.convertIntToByteArrayUsingCommonsLang(INT_VALUE);

        assertArrayEquals(INT_BYTE_ARRAY, bytes);
    }

    @Test
    void givenCommonsLang_whenConvertingByteArrayToLong_thenSuccess() {
        long value = ByteArrayToNumericRepresentation.convertByteArrayToLongUsingCommonsLang(LONG_BYTE_ARRAY);

        assertEquals(LONG_VALUE, value);
    }

    @Test
    void givenCommonsLang_whenConvertingLongToByteArray_thenSuccess() {
        byte[] bytes = ByteArrayToNumericRepresentation.convertLongToByteArrayUsingCommonsLang(LONG_VALUE);

        assertArrayEquals(LONG_BYTE_ARRAY, bytes);
    }

    @Test
    void givenCommonsLang_whenConvertingByteArrayToFloat_thenSuccess() {
        float value = ByteArrayToNumericRepresentation.convertByteArrayToFloatUsingCommonsLang(FLOAT_BYTE_ARRAY);

        assertEquals(Float.floatToIntBits(FLOAT_VALUE), Float.floatToIntBits(value));
    }

    @Test
    void givenCommonsLang_whenConvertingFloatToByteArray_thenSuccess() {
        byte[] bytes = ByteArrayToNumericRepresentation.convertFloatToByteArrayUsingCommonsLang(FLOAT_VALUE);

        assertArrayEquals(FLOAT_BYTE_ARRAY, bytes);
    }

    @Test
    void givenCommonsLang_whenConvertingByteArrayToDouble_thenSuccess() {
        double value = ByteArrayToNumericRepresentation.convertByteArrayToDoubleUsingCommonsLang(DOUBLE_BYTE_ARRAY);

        assertEquals(Double.doubleToLongBits(DOUBLE_VALUE), Double.doubleToLongBits(value));
    }

    @Test
    void givenCommonsLang_whenConvertingDoubleToByteArray_thenSuccess() {
        byte[] bytes = ByteArrayToNumericRepresentation.convertDoubleToByteArrayUsingCommonsLang(DOUBLE_VALUE);

        assertArrayEquals(DOUBLE_BYTE_ARRAY, bytes);
    }
}