package cn.tuyucheng.taketoday.ejbspringcomparison.ejb.singleton;

import javax.ejb.Remote;

@Remote
public interface CounterEJBRemote {
	int count();

	String getName();

	void setName(String name);
}
