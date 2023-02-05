package cn.tuyucheng.taketoday.spliteratorAPI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

class ExecutorUnitTest {
	Article article;
	Stream<Author> stream;
	Spliterator<Author> spliterator;
	Spliterator<Article> split1;
	Spliterator<Article> split2;

	@BeforeEach
	void init() {
		article = new Article(Arrays.asList(new Author("Ahmad", 0), new Author("Eugen", 0), new Author("Alice", 1),
			new Author("Alice", 1), new Author("Mike", 0), new Author("Alice", 1), new Author("Mike", 0),
			new Author("Alice", 1), new Author("Mike", 0), new Author("Alice", 1), new Author("Mike", 0),
			new Author("Mike", 0), new Author("Alice", 1), new Author("Mike", 0), new Author("Alice", 1),
			new Author("Mike", 0), new Author("Alice", 1), new Author("Mike", 0), new Author("Alice", 1),
			new Author("Mike", 0), new Author("Michał", 0), new Author("Loredana", 1)), 0);
		stream = article.getListOfAuthors().stream();
		split1 = Executor.generateElements().spliterator();
		split2 = split1.trySplit();
		spliterator = new RelatedAuthorSpliterator(article.getListOfAuthors());
	}

	@Test
	void givenAStreamOfAuthors_whenProcessedInParallelWithCustomSpliterator_coubtProducesRightOutput() {
		Stream<Author> stream2 = StreamSupport.stream(spliterator, true);
		assertThat(Executor.countAuthors(stream2.parallel())).isEqualTo(9);
	}

	@Test
	void givenSpliterator_whenAppliedToAListOfArticle_thenSplitInHalf() {
		assertThat(new Task(split1).call()).containsSequence(Executor.generateElements().size() / 2 + "");
		assertThat(new Task(split2).call()).containsSequence(Executor.generateElements().size() / 2 + "");
	}
}