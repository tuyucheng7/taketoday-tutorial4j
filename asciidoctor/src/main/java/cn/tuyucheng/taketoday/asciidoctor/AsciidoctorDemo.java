package cn.tuyucheng.taketoday.asciidoctor;

import org.asciidoctor.Asciidoctor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.asciidoctor.Asciidoctor.Factory.create;
import static org.asciidoctor.OptionsBuilder.options;

public class AsciidoctorDemo {

	private final Asciidoctor asciidoctor;

	AsciidoctorDemo() {
		asciidoctor = create();
	}

	public void generatePDFFromString(final String input) {

		final Map<String, Object> options = options().inPlace(true)
			.backend("pdf")
			.asMap();


		final String outfile = asciidoctor.convertFile(new File("sample.adoc"), options);
	}

	String generateHTMLFromString(final String input) {
		return asciidoctor.convert("Hello _Tuyucheng_!", new HashMap<>());
	}
}