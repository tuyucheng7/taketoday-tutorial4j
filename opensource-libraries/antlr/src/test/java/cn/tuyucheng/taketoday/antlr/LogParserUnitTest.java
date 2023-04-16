package cn.tuyucheng.taketoday.antlr;

import cn.tuyucheng.taketoday.antlr.log.LogListener;
import cn.tuyucheng.taketoday.antlr.log.model.LogEntry;
import cn.tuyucheng.taketoday.antlr.log.model.LogLevel;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class LogParserUnitTest {

	@Test
	void whenLogContainsOneErrorLogEntry_thenOneErrorIsReturned() throws Exception {
		String logLines = "2018-May-05 14:20:21 DEBUG entering awesome method\r\n" +
			"2018-May-05 14:20:24 ERROR Bad thing happened\r\n";
		LogLexer serverLogLexer = new LogLexer(CharStreams.fromString(logLines));
		CommonTokenStream tokens = new CommonTokenStream(serverLogLexer);
		LogParser logParser = new LogParser(tokens);
		ParseTreeWalker walker = new ParseTreeWalker();
		LogListener logWalker = new LogListener();
		walker.walk(logWalker, logParser.log());

		assertThat(logWalker.getEntries().size(), is(2));
		LogEntry error = logWalker.getEntries().get(1);
		assertThat(error.getLevel(), is(LogLevel.ERROR));
		assertThat(error.getMessage(), is("Bad thing happened"));
		assertThat(error.getTimestamp(), is(LocalDateTime.of(2018, 5, 5, 14, 20, 24)));
	}
}