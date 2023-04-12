package cn.tuyucheng.taketoday.joinpoint;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnableAspectJAutoProxy
public class JoinPointAroundCacheAspectIntegrationTest {

	@Autowired
	private ArticleService articleService;

	@Test
	// @Ignore("fails test")
	public void shouldPopulateCache() {
		assertTrue(JoinPointAroundCacheAspect.CACHE.isEmpty());

		List<String> articles = articleService.getArticleList();

		assertFalse(JoinPointAroundCacheAspect.CACHE.isEmpty());
		assertEquals(1, JoinPointAroundCacheAspect.CACHE.size());
		assertEquals(JoinPointAroundCacheAspect.CACHE.values().iterator().next(), articles);
	}
}