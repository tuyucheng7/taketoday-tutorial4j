package cn.tuyucheng.taketoday.examples.guice.modules;

import cn.tuyucheng.taketoday.examples.guice.Communication;
import cn.tuyucheng.taketoday.examples.guice.CommunicationMode;
import cn.tuyucheng.taketoday.examples.guice.DefaultCommunicator;
import cn.tuyucheng.taketoday.examples.guice.EmailCommunicationMode;
import cn.tuyucheng.taketoday.examples.guice.IMCommunicationMode;
import cn.tuyucheng.taketoday.examples.guice.SMSCommunicationMode;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BasicModule extends AbstractModule {

	@Override
	protected void configure() {
		try {
			bind(Communication.class).toConstructor(Communication.class.getConstructor(Boolean.class));
			bind(Boolean.class).toInstance(true);
		} catch (NoSuchMethodException ex) {
			Logger.getLogger(cn.tuyucheng.taketoday.examples.guice.binding.BasicModule.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SecurityException ex) {
			Logger.getLogger(cn.tuyucheng.taketoday.examples.guice.binding.BasicModule.class.getName()).log(Level.SEVERE, null, ex);
		}
		bind(DefaultCommunicator.class).annotatedWith(Names.named("AnotherCommunicator")).to(DefaultCommunicator.class).asEagerSingleton();

		bind(CommunicationMode.class).annotatedWith(Names.named("IMComms")).to(IMCommunicationMode.class);
		bind(CommunicationMode.class).annotatedWith(Names.named("EmailComms")).to(EmailCommunicationMode.class);
		bind(CommunicationMode.class).annotatedWith(Names.named("SMSComms")).to(SMSCommunicationMode.class);
	}

}
