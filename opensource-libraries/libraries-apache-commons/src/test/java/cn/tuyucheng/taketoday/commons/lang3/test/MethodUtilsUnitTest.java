package cn.tuyucheng.taketoday.commons.lang3.test;

import cn.tuyucheng.taketoday.commons.lang3.beans.User;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

public class MethodUtilsUnitTest {

	@Test
	public void givenMethodUtilsClass_whenCalledgetAccessibleMethod_thenCorrect() {
		assertThat(MethodUtils.getAccessibleMethod(User.class, "getName")).isInstanceOf(Method.class);
	}
}
