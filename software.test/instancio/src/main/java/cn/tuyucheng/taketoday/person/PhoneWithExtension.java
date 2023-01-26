package cn.tuyucheng.taketoday.person;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PhoneWithExtension extends Phone {
    private final String extension;

    public PhoneWithExtension(final String countryCode, final String number, final String extension) {
        super(countryCode, number);
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}