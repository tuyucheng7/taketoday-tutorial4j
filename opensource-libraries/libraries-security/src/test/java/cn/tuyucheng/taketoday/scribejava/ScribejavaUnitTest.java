package cn.tuyucheng.taketoday.scribejava;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore("fails on CI")
public class ScribejavaUnitTest {

	@Test
	public void contextLoad() {

	}

}
