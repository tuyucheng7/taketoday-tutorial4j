package cn.tuyucheng.taketoday.exception.indexoutofbounds;

import java.util.List;
import java.util.stream.Collectors;

public class CopyListUsingJava8StreamDemo {
    static List<Integer> copyList(List<Integer> source) {
        return source
              .stream()
              .collect(Collectors.toList());
    }
}
