package cn.tuyucheng.taketoday.iTextPDFTest;

import cn.tuyucheng.taketoday.iTextPDF.StoryTime;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor.getTextFromPage;
import static org.assertj.core.api.Assertions.assertThat;

class AddWaterMarkToExistingPdfUnitTest {

	@Test
	public void givenAnExistingPDF_whenManipulatedPDFWithIText_thenGeneratePDFwithWatermarks() throws IOException {
		StoryTime storyTime = new StoryTime();
		String outputPdf = "output/aliceupdated.pdf";
		String watermark = "CONFIDENTIAL";

		LocationTextExtractionStrategy extStrategy = new LocationTextExtractionStrategy();
		try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(outputPdf))) {
			for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {
				String textFromPage = getTextFromPage(pdfDocument.getPage(i), extStrategy);
				assertThat(textFromPage).contains(watermark);
			}
		}

	}

}
