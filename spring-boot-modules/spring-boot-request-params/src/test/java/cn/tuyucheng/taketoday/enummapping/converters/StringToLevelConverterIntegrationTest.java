package cn.tuyucheng.taketoday.enummapping.converters;

import cn.tuyucheng.taketoday.enummapping.EnumMappingMainApplication;
import cn.tuyucheng.taketoday.enummapping.enums.Level;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EnumMappingMainApplication.class)
class StringToLevelConverterIntegrationTest {

	@Autowired
	ConversionService conversionService;

	@Test
	void whenConvertStringToLevelEnumUsingCustomConverter_thenSuccess() {
		Assertions.assertThat(conversionService.convert("low", Level.class)).isEqualTo(Level.LOW);
	}

	@Test
	void whenStringIsEmpty_thenReturnNull() {
		assertThat(conversionService.convert("", Level.class)).isNull();
	}

	@Test
	void whenStringIsNull_thenReturnNull() {
		assertThat(conversionService.convert(null, Level.class)).isNull();
	}
}