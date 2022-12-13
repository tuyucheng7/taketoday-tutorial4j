package cn.tuyucheng.taketoday.loose;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LooselyCouplingUnitTest {

	@Test
	void givenMetadataCollector_thenCollectMetadataXMLAndExportCSV() {
		FetchMetadata metadata = new XMLFetch();
		ExportMetadata exportMetadata = new CSVExport();
		MetadataCollector collector = new MetadataCollector(metadata, exportMetadata);
		collector.collectMetadata();
		assertTrue(collector.getExportMetadata() instanceof CSVExport);
		assertTrue(collector.getFetchMetadata() instanceof XMLFetch);
	}

	@Test
	void givenMetadataCollector_thenCollectMetadataUsingJSONAndExportPDF() {
		FetchMetadata metadata = new JSONFetch();
		ExportMetadata exportMetadata = new PDFExport();
		MetadataCollector collector = new MetadataCollector(metadata, exportMetadata);
		collector.collectMetadata();
		assertTrue(collector.getExportMetadata() instanceof PDFExport);
		assertTrue(collector.getFetchMetadata() instanceof JSONFetch);
	}

	@Test
	void givenMetadataCollector_thenCollectMetadataUsingXMLAndExportPDF() {
		FetchMetadata metadata = new XMLFetch();
		ExportMetadata exportMetadata = new PDFExport();
		MetadataCollector collector = new MetadataCollector(metadata, exportMetadata);
		collector.collectMetadata();
		assertTrue(collector.getExportMetadata() instanceof PDFExport);
		assertTrue(collector.getFetchMetadata() instanceof XMLFetch);
	}
}