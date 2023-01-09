package cn.tuyucheng.taketoday;


import com.nhl.link.rest.runtime.LinkRestBuilder;
import com.nhl.link.rest.runtime.LinkRestRuntime;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/linkrest")
public class LinkRestApplication extends ResourceConfig {

	public LinkRestApplication() {
		ServerRuntime cayenneRuntime = ServerRuntime.builder()
			.addConfig("cayenne-linkrest-project.xml")
			.build();
		LinkRestRuntime lrRuntime = LinkRestBuilder.build(cayenneRuntime);
		super.register(lrRuntime);
		packages("cn.tuyucheng.taketoday.linkrest.apis");
	}

}
