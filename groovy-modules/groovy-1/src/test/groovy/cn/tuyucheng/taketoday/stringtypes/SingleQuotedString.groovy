package cn.tuyucheng.taketoday.stringtypes

import org.junit.Assert
import org.junit.Test

class SingleQuotedString {

	@Test
	void 'single quoted string'() {
		def example = 'Hello world'

		Assert.assertEquals('Hello world!', 'Hello' + ' world!')
	}

}
