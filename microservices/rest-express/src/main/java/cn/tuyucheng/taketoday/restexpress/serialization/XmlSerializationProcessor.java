package cn.tuyucheng.taketoday.restexpress.serialization;

import cn.tuyucheng.taketoday.restexpress.uuid.SampleUuidEntity;
import org.restexpress.serialization.xml.XstreamXmlProcessor;

public class XmlSerializationProcessor
        extends XstreamXmlProcessor {
    public XmlSerializationProcessor() {
        super();
        alias("sample", SampleUuidEntity.class);
//		alias("element_name", Element.class);
//		alias("element_name", Element.class);
//		alias("element_name", Element.class);
//		alias("element_name", Element.class);
        registerConverter(new XstreamUuidConverter());
        registerConverter(new XstreamOidConverter());
    }
}
