package cn.tuyucheng.taketoday.springevents.synchronous;

class GenericStringSpringAppEvent extends GenericSpringAppEvent<String> {

	GenericStringSpringAppEvent(final Object source, final String what) {
		super(source, what);
	}

}
