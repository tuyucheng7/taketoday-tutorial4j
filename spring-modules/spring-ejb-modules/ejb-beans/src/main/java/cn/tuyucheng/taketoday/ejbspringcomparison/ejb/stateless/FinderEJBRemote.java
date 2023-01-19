package cn.tuyucheng.taketoday.ejbspringcomparison.ejb.stateless;

import javax.ejb.Remote;

@Remote
public interface FinderEJBRemote {

	String search(String keyword);
}
