package cn.tuyucheng.taketoday.collections.filtering;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.util.Collection;

public class GuavaCollectionFilter {

    public static Collection<Integer> findEvenNumbers(Collection<Integer> baseCollection) {
        Predicate<Integer> guavaPredicate = item -> item % 2 == 0;

        return Collections2.filter(baseCollection, guavaPredicate);
    }
}