package cn.tuyucheng.taketoday.mybatis;

import cn.tuyucheng.taketoday.mybatis.mapper.ToDoMapper;
import cn.tuyucheng.taketoday.mybatis.model.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MybatisApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(MybatisApplication.class, args);
	}

	@Autowired
	private ToDoMapper todoMapper;

	@Override
	public void run(String... args) throws Exception {
		TODO newItem = new TODO(2L, "title_2", "body_2");
		int createdCount = todoMapper.createNew(newItem);
		System.out.println("Created items count : " + createdCount);

		TODO item = todoMapper.findById(2L);
		System.out.println(item);

		int deletedCount = todoMapper.deleteById(2L);
		System.out.println("Deleted items count : " + deletedCount);

		TODO deletedItem = todoMapper.findById(2L);
		System.out.println("Deleted item should be null : " + deletedItem);
	}
}