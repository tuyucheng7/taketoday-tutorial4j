package cn.tuyucheng.taketoday.boot.converter;

import cn.tuyucheng.taketoday.boot.domain.Modes;
import org.springframework.core.convert.converter.Converter;

public class StringToEnumConverter implements Converter<String, Modes> {

    @Override
    public Modes convert(String from) {
        return Modes.valueOf(from);
    }
}