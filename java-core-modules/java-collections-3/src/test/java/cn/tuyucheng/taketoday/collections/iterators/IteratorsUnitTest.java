package cn.tuyucheng.taketoday.collections.iterators;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.ConcurrentModificationException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Source code https://github.com/eugenp/tutorials
 *
 * @author Santosh Thakur
 */

public class IteratorsUnitTest {

    @Test
    public void whenFailFast_ThenThrowsException() {
        assertThatThrownBy(() -> {
            Iterators.failFast1();
        }).isInstanceOf(ConcurrentModificationException.class);
    }

    @Test
    public void whenFailFast_ThenThrowsExceptionInSecondIteration() {
        assertThatThrownBy(() -> {
            Iterators.failFast2();
        }).isInstanceOf(ConcurrentModificationException.class);
    }

    @Test
    public void whenFailSafe_ThenDoesNotThrowException() {
        Assertions.assertThat(Iterators.failSafe1()).isGreaterThanOrEqualTo(0);
    }

}
