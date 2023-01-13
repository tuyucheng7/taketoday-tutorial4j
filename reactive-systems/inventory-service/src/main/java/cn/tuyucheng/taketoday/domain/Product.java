package cn.tuyucheng.taketoday.domain;

import cn.tuyucheng.taketoday.serdeser.ObjectIdSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Product {

	@JsonSerialize(using = ObjectIdSerializer.class)
	private ObjectId id;
	private String name;
	private Long price;
	private Integer stock;

}
