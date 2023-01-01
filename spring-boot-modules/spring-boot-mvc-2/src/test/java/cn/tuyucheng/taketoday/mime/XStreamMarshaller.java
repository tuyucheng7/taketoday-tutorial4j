package cn.tuyucheng.taketoday.mime;

import com.thoughtworks.xstream.XStream;
import org.springframework.http.MediaType;

import java.util.List;

public final class XStreamMarshaller implements IMarshaller {

	private XStream xstream;

	public XStreamMarshaller() {
		super();

		xstream = new XStream();
		xstream.autodetectAnnotations(true);
		xstream.processAnnotations(Foo.class);
	}

	@Override
	public final <T> String encode(final T resource) {
		return xstream.toXML(resource);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final <T> T decode(final String resourceAsString, final Class<T> clazz) {
		return (T) xstream.fromXML(resourceAsString);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> decodeList(final String resourcesAsString, final Class<T> clazz) {
		return this.decode(resourcesAsString, List.class);
	}

	@Override
	public final String getMime() {
		return MediaType.APPLICATION_XML.toString();
	}
}