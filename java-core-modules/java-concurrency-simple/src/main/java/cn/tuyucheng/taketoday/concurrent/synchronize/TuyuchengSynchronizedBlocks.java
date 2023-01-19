package cn.tuyucheng.taketoday.concurrent.synchronize;

public class TuyuchengSynchronizedBlocks {
	private int count = 0;
	private static int staticCount = 0;

	void performSynchronisedTask() {
		synchronized (this) {
			setCount(getCount() + 1);
		}
	}

	static void performStaticSyncTask() {
		synchronized (TuyuchengSynchronizedBlocks.class) {
			setStaticCount(getStaticCount() + 1);
		}
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	static int getStaticCount() {
		return staticCount;
	}

	private static void setStaticCount(int staticCount) {
		TuyuchengSynchronizedBlocks.staticCount = staticCount;
	}
}