package cn.tuyucheng.taketoday.ejb.tutorial;

import javax.ejb.Remote;

@Remote
public interface HelloStatefulWorld {

	int howManyTimes();

	String getHelloWorld();

}
