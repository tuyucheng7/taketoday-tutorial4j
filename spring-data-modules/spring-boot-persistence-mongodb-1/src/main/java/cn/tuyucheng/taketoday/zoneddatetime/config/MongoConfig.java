package cn.tuyucheng.taketoday.zoneddatetime.config;

import cn.tuyucheng.taketoday.zoneddatetime.converter.ZonedDateTimeReadConverter;
import cn.tuyucheng.taketoday.zoneddatetime.converter.ZonedDateTimeWriteConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.List;

@EnableMongoRepositories(basePackages = {"cn.tuyucheng.taketoday"})
public class MongoConfig extends AbstractMongoClientConfiguration {

	private final List<Converter<?, ?>> converters = new ArrayList<>();

	@Override
	protected String getDatabaseName() {
		return "test";
	}

	@Override
	public MongoCustomConversions customConversions() {
		converters.add(new ZonedDateTimeReadConverter());
		converters.add(new ZonedDateTimeWriteConverter());
		return new MongoCustomConversions(converters);
	}
}