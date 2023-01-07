package cn.tuyucheng.taketoday.synthetic;

import java.util.Comparator;

/**
 * Class which contains a synthetic bridge method.
 */
public class BridgeMethodDemo implements Comparator<Integer> {

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Integer o1, Integer o2) {
		return 0;
	}

}
