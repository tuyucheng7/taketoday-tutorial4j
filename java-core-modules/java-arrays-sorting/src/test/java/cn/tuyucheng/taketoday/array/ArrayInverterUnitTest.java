package cn.tuyucheng.taketoday.array;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ArrayInverterUnitTest {

    private String[] fruits = {"apples", "tomatoes", "bananas", "guavas", "pineapples", "oranges"};

    @Test
    public void invertArrayWithForLoop() {
        ArrayInverter inverter = new ArrayInverter();
        inverter.invertUsingFor(fruits);

        assertThat(fruits).isEqualTo(new String[]{"oranges", "pineapples", "guavas", "bananas", "tomatoes", "apples"});
    }

    @Test
    public void invertArrayWithCollectionsReverse() {
        ArrayInverter inverter = new ArrayInverter();
        inverter.invertUsingCollectionsReverse(fruits);

        assertThat(fruits).isEqualTo(new String[]{"oranges", "pineapples", "guavas", "bananas", "tomatoes", "apples"});
    }

    @Test
    public void invertArrayWithStreams() {
        ArrayInverter inverter = new ArrayInverter();

        assertThat(inverter.invertUsingStreams(fruits)).isEqualTo(new String[]{"oranges", "pineapples", "guavas", "bananas", "tomatoes", "apples"});
    }

    @Test
    public void invertArrayWithCommonsLang() {
        ArrayInverter inverter = new ArrayInverter();
        inverter.invertUsingCommonsLang(fruits);

        assertThat(fruits).isEqualTo(new String[]{"oranges", "pineapples", "guavas", "bananas", "tomatoes", "apples"});
    }

    @Test
    public void invertArrayWithGuava() {
        ArrayInverter inverter = new ArrayInverter();

        assertThat(inverter.invertUsingGuava(fruits)).isEqualTo(new String[]{"oranges", "pineapples", "guavas", "bananas", "tomatoes", "apples"});
    }
}