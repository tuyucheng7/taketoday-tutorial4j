package cn.tuyucheng.taketoday.collections.filtering;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.util.Collection;

public class CollectionUtilsCollectionFilter {

    public static Collection<Integer> findEvenNumbers(Collection<Integer> baseCollection) {
        Predicate<Integer> apacheEventNumberPredicate = item -> item % 2 == 0;

        CollectionUtils.filter(baseCollection, apacheEventNumberPredicate);
        return baseCollection;
    }
}