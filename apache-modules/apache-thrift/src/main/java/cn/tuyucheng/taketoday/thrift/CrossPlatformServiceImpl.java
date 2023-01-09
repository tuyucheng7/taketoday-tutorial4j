package cn.tuyucheng.taketoday.thrift;

import cn.tuyucheng.taketoday.thrift.impl.CrossPlatformResource;
import cn.tuyucheng.taketoday.thrift.impl.CrossPlatformService;
import cn.tuyucheng.taketoday.thrift.impl.InvalidOperationException;
import org.apache.thrift.TException;

import java.util.Collections;
import java.util.List;

public class CrossPlatformServiceImpl implements CrossPlatformService.Iface {

	@Override
	public CrossPlatformResource get(final int id) throws InvalidOperationException, TException {
		// add some action
		return new CrossPlatformResource();
	}

	@Override
	public void save(final CrossPlatformResource resource) throws InvalidOperationException, TException {
		// add some action
	}

	@Override
	public List<CrossPlatformResource> getList() throws InvalidOperationException, TException {
		// add some action
		return Collections.emptyList();
	}

	@Override
	public boolean ping() throws InvalidOperationException, TException {
		return true;
	}
}
