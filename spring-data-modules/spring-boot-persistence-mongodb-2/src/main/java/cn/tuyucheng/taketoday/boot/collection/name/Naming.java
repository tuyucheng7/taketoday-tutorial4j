package cn.tuyucheng.taketoday.boot.collection.name;

import org.springframework.data.util.ParsingUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Naming {
    public static void main(String[] args) {
        String r = new Naming().fix(args[0]);
        System.out.println(r);
    }

    public String fix(String name) {
        List<String> parts = ParsingUtils.splitCamelCaseToLower(name);
        List<String> result = new ArrayList<>();

        for (String part : parts) {
            if (StringUtils.hasText(part)) {
                result.add(part);
            }
        }

        return StringUtils.collectionToDelimitedString(result, "_");
    }

    public String convert(Class<?> type) {
        return fix(type.getSimpleName());
    }

    public String convert(Object instance) {
        return convert(instance.getClass());
    }
}