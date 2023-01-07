package cn.tuyucheng.taketoday.primitivetype;

import com.google.common.primitives.Primitives;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PrimitiveTypeUnitTest {

	@Test
	public void givenAClass_whenCheckWithPrimitiveTypeUtil_thenShouldValidate() {
		assertTrue(PrimitiveTypeUtil.isPrimitiveType(false));
		assertTrue(PrimitiveTypeUtil.isPrimitiveType(1L));
		assertFalse(PrimitiveTypeUtil.isPrimitiveType(StringUtils.EMPTY));
	}

	@Test
	public void givenAClass_whenCheckWithCommonsLang_thenShouldValidate() {
		assertTrue(ClassUtils.isPrimitiveOrWrapper(Boolean.FALSE.getClass()));
		assertTrue(ClassUtils.isPrimitiveOrWrapper(boolean.class));
		assertFalse(ClassUtils.isPrimitiveOrWrapper(StringUtils.EMPTY.getClass()));
	}

	@Test
	public void givenAClass_whenCheckWithGuava_thenShouldValidate() {
		assertTrue(Primitives.isWrapperType(Boolean.FALSE.getClass()));
		assertFalse(Primitives.isWrapperType(StringUtils.EMPTY.getClass()));
		assertFalse(Primitives.isWrapperType(Boolean.TYPE.getClass()));
	}
}
