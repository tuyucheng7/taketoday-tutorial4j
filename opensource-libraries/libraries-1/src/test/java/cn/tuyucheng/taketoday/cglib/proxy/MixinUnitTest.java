package cn.tuyucheng.taketoday.cglib.proxy;

import cn.tuyucheng.taketoday.cglib.mixin.Class1;
import cn.tuyucheng.taketoday.cglib.mixin.Class2;
import cn.tuyucheng.taketoday.cglib.mixin.Interface1;
import cn.tuyucheng.taketoday.cglib.mixin.Interface2;
import cn.tuyucheng.taketoday.cglib.mixin.MixinInterface;
import net.sf.cglib.proxy.Mixin;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class MixinUnitTest {

	@Test
	public void givenTwoClasses_whenMixedIntoOne_thenMixinShouldHaveMethodsFromBothClasses() throws Exception {
		// when
		Mixin mixin = Mixin.create(new Class[]{Interface1.class, Interface2.class, MixinInterface.class}, new Object[]{new Class1(), new Class2()});
		MixinInterface mixinDelegate = (MixinInterface) mixin;

		// then
		assertEquals("first behaviour", mixinDelegate.first());
		assertEquals("second behaviour", mixinDelegate.second());
	}
}
