package cn.tuyucheng.taketoday.synchronizedcollections;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SynchronizedMapUnitTest {

    @Test
    public void givenSynchronizedMap_whenTwoThreadsAddElements_thenCorrectMapSize() throws InterruptedException {
        Map<Integer, String> syncMap = Collections.synchronizedMap(new HashMap<>());

        Runnable mapOperations = () -> {
            syncMap.put(1, "one");
            syncMap.put(2, "two");
            syncMap.put(3, "three");

        };
        Thread thread1 = new Thread(mapOperations);
        Thread thread2 = new Thread(mapOperations);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        assertThat(syncMap).hasSize(3);
    }
}