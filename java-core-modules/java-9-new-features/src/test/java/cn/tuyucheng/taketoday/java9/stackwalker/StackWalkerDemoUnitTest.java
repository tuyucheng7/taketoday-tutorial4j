package cn.tuyucheng.taketoday.java9.stackwalker;

import org.junit.jupiter.api.Test;

class StackWalkerDemoUnitTest {

	@Test
	void giveStalkWalker_whenWalkingTheStack_thenShowStackFrames() {
		new StackWalkerDemo().methodOne();
	}

	@Test
	void giveStalkWalker_whenInvokingFindCaller_thenFindCallingClass() {
		new StackWalkerDemo().findCaller();
	}
}