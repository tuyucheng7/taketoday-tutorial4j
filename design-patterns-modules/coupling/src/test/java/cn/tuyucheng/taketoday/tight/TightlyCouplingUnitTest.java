package cn.tuyucheng.taketoday.tight;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

class TightlyCouplingUnitTest {

	@Test
	void givenMetadataCollector_thenCollectMetadata() {
		MetadataCollector collector = mock(MetadataCollector.class);
		doNothing().when(collector).collectMetadata();
	}

	@Test
	void givenMetadataCollectorWithDifferentInput_thenCollectMetadata() {
		MetadataCollector collector = new MetadataCollector();
		collector.collectMetadata(1, 1);
	}
}