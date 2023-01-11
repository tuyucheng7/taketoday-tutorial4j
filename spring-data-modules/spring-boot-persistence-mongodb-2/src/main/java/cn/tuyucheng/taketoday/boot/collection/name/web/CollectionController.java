package cn.tuyucheng.taketoday.boot.collection.name.web;

import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/collection")
public class CollectionController {
	@Autowired
	private MongoTemplate mongo;

	@GetMapping("/{name}")
	public List<DBObject> get(@PathVariable String name) {
		return mongo.findAll(DBObject.class, name);
	}
}