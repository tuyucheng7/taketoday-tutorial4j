package cn.tuyucheng.taketoday.java.lists;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListToStringUnitTest {

    @Test
    public void whenListToString_thenPrintDefault() {
        List<Integer> intLIst = Arrays.asList(1, 2, 3);
        System.out.println(intLIst);
    }

    @Test
    public void whenCollectorsJoining_thenPrintCustom() {
        List<Integer> intList = Arrays.asList(1, 2, 3);
        System.out.println(intList.stream()
              .map(n -> String.valueOf(n))
              .collect(Collectors.joining("-", "{", "}")));
    }

    @Test
    public void whenStringUtilsJoin_thenPrintCustom() {
        List<Integer> intList = Arrays.asList(1, 2, 3);
        System.out.println(StringUtils.join(intList, "|"));
    }
}
