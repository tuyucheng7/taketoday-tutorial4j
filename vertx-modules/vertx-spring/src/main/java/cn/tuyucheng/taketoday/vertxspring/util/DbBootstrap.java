package cn.tuyucheng.taketoday.vertxspring.util;

import cn.tuyucheng.taketoday.vertxspring.entity.Article;
import cn.tuyucheng.taketoday.vertxspring.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

@Component
public class DbBootstrap implements CommandLineRunner {

	@Autowired
	private ArticleRepository articleRepository;

	@Override
	public void run(String... arg0) throws Exception {
		IntStream.range(0, 10)
			.forEach(count -> this.articleRepository.save(new Article(new Random().nextLong(), UUID.randomUUID()
				.toString())));
	}
}