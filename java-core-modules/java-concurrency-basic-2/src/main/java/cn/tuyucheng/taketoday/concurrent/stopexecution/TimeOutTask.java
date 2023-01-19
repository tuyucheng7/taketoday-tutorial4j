package cn.tuyucheng.taketoday.concurrent.stopexecution;

import java.util.Timer;
import java.util.TimerTask;

public class TimeOutTask extends TimerTask {
	private final Thread thread;
	private final Timer timer;

	public TimeOutTask(Thread thread, Timer timer) {
		this.thread = thread;
		this.timer = timer;
	}

	@Override
	public void run() {
		if (thread != null && thread.isAlive()) {
			thread.interrupt();
			timer.cancel();
		}
	}
}