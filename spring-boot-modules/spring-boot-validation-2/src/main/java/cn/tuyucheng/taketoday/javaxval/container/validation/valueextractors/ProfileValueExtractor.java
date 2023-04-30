package cn.tuyucheng.taketoday.javaxval.container.validation.valueextractors;

import cn.tuyucheng.taketoday.javaxval.container.validation.Profile;
import jakarta.validation.valueextraction.ExtractedValue;
import jakarta.validation.valueextraction.UnwrapByDefault;
import jakarta.validation.valueextraction.ValueExtractor;

@UnwrapByDefault
public class ProfileValueExtractor implements ValueExtractor<@ExtractedValue(type = String.class) Profile> {

	@Override
	public void extractValues(Profile originalValue, ValueExtractor.ValueReceiver receiver) {
		receiver.value(null, originalValue.getCompanyName());
	}

}
