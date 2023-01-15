package cn.tuyucheng.taketoday.easymock;

import org.easymock.EasyMockSupport;
import org.junit.Test;

import static org.easymock.EasyMock.expect;

public class BaeldungReaderMockDelegationUnitTest {

	EasyMockSupport easyMockSupport = new EasyMockSupport();

	@Test
	public void givenBaeldungReader_whenReadAndWriteSequencially_thenWorks() {
		ArticleReader mockArticleReader = easyMockSupport.createMock(ArticleReader.class);
		IArticleWriter mockArticleWriter = easyMockSupport.createMock(IArticleWriter.class);
		BaeldungReader baeldungReader = new BaeldungReader(mockArticleReader, mockArticleWriter);

		expect(mockArticleReader.next()).andReturn(null);
		expect(mockArticleWriter.write("title", "content")).andReturn("");
		easyMockSupport.replayAll();

		baeldungReader.readNext();
		baeldungReader.write("title", "content");
		easyMockSupport.verifyAll();
	}

}