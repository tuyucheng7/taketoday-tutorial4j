package cn.tuyucheng.taketoday.java9.streams.reactive.flowvsrx;

class VideoFrame {
	private final long number;

	public VideoFrame(long number) {
		this.number = number;
	}

	public long getNumber() {
		return number;
	}
}