package cn.tuyucheng.taketoday.collections.sorting.multiple;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

import java.util.Comparator;

public class ComparisonChainExample implements Comparator<Person> {
    @Override
    public int compare(Person o1, Person o2) {
        return ComparisonChain.start()
              .compare(o1.getName(), o2.getName(), Ordering.natural().nullsFirst())
              .compare(o1.getAge(), o2.getAge()).result();
    }
}
