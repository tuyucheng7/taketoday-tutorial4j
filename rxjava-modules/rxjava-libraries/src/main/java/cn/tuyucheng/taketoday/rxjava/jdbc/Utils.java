package cn.tuyucheng.taketoday.rxjava.jdbc;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

class Utils {

	static String getStringFromInputStream(InputStream input) throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(input, writer, "UTF-8");
		return writer.toString();
	}
}