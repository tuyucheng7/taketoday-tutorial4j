package cn.tuyucheng.taketoday.springmodulith.events.externalization;

import org.springframework.modulith.events.Externalized;

@Externalized("tuyucheng.article.published::#{slug()}")
public record ArticlePublishedEvent(String slug, String title) {
}