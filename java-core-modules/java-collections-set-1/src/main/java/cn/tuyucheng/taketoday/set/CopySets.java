package cn.tuyucheng.taketoday.set;

import com.google.gson.Gson;
import org.apache.commons.lang.SerializationUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CopySets {

    // Copy Constructor
    public static <T> Set<T> copyByConstructor(Set<T> original) {
        Set<T> copy = new HashSet<>(original);
        return copy;
    }

    // Set.addAll
    public static <T> Set<T> copyBySetAddAll(Set<T> original) {
        Set<T> copy = new HashSet<>();
        copy.addAll(original);
        return copy;
    }

    // Set.clone
    public static <T> Set<T> copyBySetClone(HashSet<T> original) {
        Set<T> copy = (Set<T>) original.clone();
        return copy;
    }

    // JSON
    public static <T> Set<T> copyByJson(Set<T> original) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(original);
        Set<T> copy = gson.fromJson(jsonStr, Set.class);

        return copy;
    }

    // Apache Commons Lang
    public static <T extends Serializable> Set<T> copyByApacheCommonsLang(Set<T> original) {
        Set<T> copy = new HashSet<>();
        for (T item : original) {
            copy.add((T) SerializationUtils.clone(item));
        }
        return copy;
    }

    // Collectors.toSet
    public static <T extends Serializable> Set<T> copyByCollectorsToSet(Set<T> original) {
        Set<T> copy = original.stream()
              .collect(Collectors.toSet());

        return copy;
    }

}
